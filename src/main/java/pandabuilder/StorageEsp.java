package pandabuilder;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
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
import net.minecraft.world.level.chunk.status.ChunkStatus;

/** Finds base blocks (chests, barrels, shulkers, ...) in loaded chunks. EspHud draws them. */
public final class StorageEsp {
    private static final int RESCAN_TICKS = 10;
    private static final int MAX_TARGETS = 1024;

    public record Target(BlockPos pos, int color) {}

    private static boolean on;
    private static int cooldown;
    private static List<Target> targets = List.of();

    private StorageEsp() {}

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
        ChunkPos center = mc.player.chunkPosition();
        int radius = mc.options.getEffectiveRenderDistance();
        for (int cx = center.x() - radius; cx <= center.x() + radius; cx++) {
            for (int cz = center.z() - radius; cz <= center.z() + radius; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    int color = color(be);
                    if (color != 0 && !be.isRemoved()) found.add(new Target(be.getBlockPos(), color));
                    if (found.size() >= MAX_TARGETS) return found;
                }
            }
        }
        return found;
    }

    /** 0 means "not a base block". Subclasses are checked before the classes they extend. */
    private static int color(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) return 0xFFEF4444;
        if (be instanceof ChestBlockEntity) return 0xFFF59E0B;
        if (be instanceof EnderChestBlockEntity) return 0xFFA855F7;
        if (be instanceof ShulkerBoxBlockEntity) return 0xFFEC4899;
        if (be instanceof BarrelBlockEntity) return 0xFFC2803A;
        if (be instanceof DropperBlockEntity) return 0xFF94A3B8;
        if (be instanceof DispenserBlockEntity) return 0xFFCBD5E1;
        if (be instanceof HopperBlockEntity) return 0xFF64748B;
        if (be instanceof CrafterBlockEntity) return 0xFFFDBA74;
        if (be instanceof AbstractFurnaceBlockEntity) return 0xFF78716C;
        if (be instanceof BrewingStandBlockEntity) return 0xFFFDE047;
        if (be instanceof SpawnerBlockEntity) return 0xFF22D3EE;
        return 0;
    }
}
