package pandabuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * Finds base blocks (chests, barrels, shulkers, ...) in loaded chunks; EspHud draws them. Which blocks are shown
 * and whether boxes, tracers and distance markers are drawn are set in the menu and saved to
 * config/pandabuilder-storage-esp.properties.
 */
public final class StorageEsp {
    private static final int RESCAN_TICKS = 10;
    private static final int MAX_TARGETS = 1024;

    public enum Type {
        CHEST("Chest", 0xFFF59E0B),
        TRAPPED_CHEST("Trapped Chest", 0xFFEF4444),
        ENDER_CHEST("Ender Chest", 0xFFA855F7),
        SHULKER_BOX("Shulker Box", 0xFFEC4899),
        BARREL("Barrel", 0xFFC2803A),
        DISPENSER("Dispenser", 0xFFCBD5E1),
        DROPPER("Dropper", 0xFF94A3B8),
        HOPPER("Hopper", 0xFF64748B),
        FURNACE("Furnace", 0xFF78716C),
        BREWING_STAND("Brewing Stand", 0xFFFDE047),
        CRAFTER("Crafter", 0xFFFDBA74),
        SPAWNER("Spawner", 0xFF22D3EE);

        public final String label;
        public final int color;

        Type(String label, int color) {
            this.label = label;
            this.color = color;
        }
    }

    public record Target(BlockPos pos, String label, int color) {}

    /** Custom blocks are searched for in chunks closer than this, since every block has to be checked. */
    private static final int CUSTOM_RADIUS = 8;

    private static boolean on;
    private static boolean boxes = true;
    private static boolean tracers = false;
    private static boolean markers = true;
    private static final Set<Type> enabled = EnumSet.allOf(Type.class);
    /** Extra blocks the player added through the search, in the order added. */
    private static final Set<Block> custom = new LinkedHashSet<>();

    private static int cooldown;
    private static List<Target> targets = List.of();

    private StorageEsp() {}

    // ---- module ----

    public static boolean isOn() {
        return on;
    }

    public static void toggle() {
        on = !on;
        cooldown = 0;
        if (!on) targets = List.of();
        AutoTotem.message(Component.literal("Storage ESP: " + (on ? "ON" : "OFF")));
    }

    public static List<Target> targets() {
        return targets;
    }

    // ---- options ----

    public static boolean boxes() { return boxes; }
    public static boolean tracers() { return tracers; }
    public static boolean markers() { return markers; }
    public static boolean isEnabled(Type type) { return enabled.contains(type); }

    public static void toggleBoxes() { boxes = !boxes; save(); }
    public static void toggleTracers() { tracers = !tracers; save(); }
    public static void toggleMarkers() { markers = !markers; save(); }

    public static List<Block> customBlocks() {
        return List.copyOf(custom);
    }

    public static boolean isCustom(Block block) {
        return custom.contains(block);
    }

    public static void toggleCustom(Block block) {
        if (!custom.remove(block)) custom.add(block);
        cooldown = 0;
        save();
    }

    /** Box colour for a custom block: its map colour, or white if it has none. */
    public static int colorOf(Block block) {
        int col = block.defaultMapColor().col;
        return col == 0 ? 0xFFFFFFFF : 0xFF000000 | col;
    }

    public static void toggleType(Type type) {
        if (!enabled.remove(type)) enabled.add(type);
        cooldown = 0;
        save();
    }

    // ---- scanning ----

    public static void tick(Minecraft mc) {
        if (!on) return;
        if (mc.level == null || mc.player == null) {
            targets = List.of();
            return;
        }
        if (cooldown-- > 0) return;
        cooldown = RESCAN_TICKS;
        targets = scan(mc, mc.level);
    }

    private static List<Target> scan(Minecraft mc, ClientLevel level) {
        List<Target> found = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        ChunkPos center = mc.player.chunkPosition();
        int radius = mc.options.getEffectiveRenderDistance();
        int customRadius = Math.min(radius, CUSTOM_RADIUS);
        for (int cx = center.x() - radius; cx <= center.x() + radius; cx++) {
            for (int cz = center.z() - radius; cz <= center.z() + radius; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    Type type = classify(be);
                    if (type != null && enabled.contains(type) && !be.isRemoved()) {
                        found.add(new Target(be.getBlockPos(), type.label, type.color));
                        seen.add(be.getBlockPos());
                        if (found.size() >= MAX_TARGETS) return found;
                    }
                }
                boolean near = Math.abs(cx - center.x()) <= customRadius && Math.abs(cz - center.z()) <= customRadius;
                if (near && !custom.isEmpty() && scanCustom(level, chunk, found, seen)) return found;
            }
        }
        return found;
    }

    /** Adds custom blocks in one chunk. Returns true once the target cap is reached. */
    private static boolean scanCustom(ClientLevel level, LevelChunk chunk, List<Target> found, Set<BlockPos> seen) {
        LevelChunkSection[] sections = chunk.getSections();
        int baseX = chunk.getPos().getMinBlockX(), baseZ = chunk.getPos().getMinBlockZ();
        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            // The palette check skips sections that can't contain any custom block without reading every block.
            if (section.hasOnlyAir() || !section.getStates().maybeHas(st -> custom.contains(st.getBlock()))) continue;
            int baseY = SectionPos.sectionToBlockCoord(level.getSectionYFromSectionIndex(i));
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        Block block = section.getBlockState(x, y, z).getBlock();
                        if (!custom.contains(block)) continue;
                        BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                        if (!seen.add(pos)) continue;
                        found.add(new Target(pos, block.getName().getString(), colorOf(block)));
                        if (found.size() >= MAX_TARGETS) return true;
                    }
                }
            }
        }
        return false;
    }

    /** Subclasses are checked before the classes they extend (trapped chest, dropper). */
    private static Type classify(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) return Type.TRAPPED_CHEST;
        if (be instanceof ChestBlockEntity) return Type.CHEST;
        if (be instanceof EnderChestBlockEntity) return Type.ENDER_CHEST;
        if (be instanceof ShulkerBoxBlockEntity) return Type.SHULKER_BOX;
        if (be instanceof BarrelBlockEntity) return Type.BARREL;
        if (be instanceof DropperBlockEntity) return Type.DROPPER;
        if (be instanceof DispenserBlockEntity) return Type.DISPENSER;
        if (be instanceof HopperBlockEntity) return Type.HOPPER;
        if (be instanceof CrafterBlockEntity) return Type.CRAFTER;
        if (be instanceof AbstractFurnaceBlockEntity) return Type.FURNACE;
        if (be instanceof BrewingStandBlockEntity) return Type.BREWING_STAND;
        if (be instanceof SpawnerBlockEntity) return Type.SPAWNER;
        return null;
    }

    // ---- config ----

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pandabuilder-storage-esp.properties");
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) return;
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(file)) {
            p.load(r);
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read Storage ESP settings", e);
            return;
        }
        boxes = Boolean.parseBoolean(p.getProperty("boxes", "true"));
        tracers = Boolean.parseBoolean(p.getProperty("tracers", "false"));
        markers = Boolean.parseBoolean(p.getProperty("markers", "true"));
        enabled.clear();
        for (Type t : Type.values()) {
            if (Boolean.parseBoolean(p.getProperty("block." + t.name().toLowerCase(), "true"))) enabled.add(t);
        }
        custom.clear();
        for (String id : p.getProperty("custom", "").split(",")) {
            Identifier key = Identifier.tryParse(id.trim());
            if (key != null && !id.isBlank()) BuiltInRegistries.BLOCK.getOptional(key).ifPresent(custom::add);
        }
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("boxes", Boolean.toString(boxes));
        p.setProperty("tracers", Boolean.toString(tracers));
        p.setProperty("markers", Boolean.toString(markers));
        for (Type t : Type.values()) p.setProperty("block." + t.name().toLowerCase(), Boolean.toString(enabled.contains(t)));
        StringBuilder ids = new StringBuilder();
        for (Block b : custom) {
            if (!ids.isEmpty()) ids.append(',');
            ids.append(BuiltInRegistries.BLOCK.getKey(b));
        }
        p.setProperty("custom", ids.toString());
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, Brand.NAME + " Storage ESP settings");
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not save Storage ESP settings", e);
        }
    }
}
