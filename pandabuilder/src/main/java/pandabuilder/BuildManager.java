package pandabuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Holds the selected schematic and build state, and warns when materials run out. */
public final class BuildManager {
    private static Schematic selected;
    private static boolean building;
    private static boolean paused;
    private static final Set<Item> warnedOut = new HashSet<>();
    private static int tickCounter;

    private BuildManager() {
    }

    public static Path schematicsDir() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("schematics");
    }

    public static List<Path> listSchematics() {
        Path dir = schematicsDir();
        List<Path> files = new ArrayList<>();
        try {
            Files.createDirectories(dir);
            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(Files::isRegularFile)
                        .filter(p -> {
                            String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                            return n.endsWith(".schem") || n.endsWith(".litematic") || n.endsWith(".schematic");
                        })
                        .sorted()
                        .forEach(files::add);
            }
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not list schematics", e);
        }
        return files;
    }

    public static Schematic selected() {
        return selected;
    }

    public static boolean isBuilding() {
        return building;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void select(Path path) throws IOException {
        selected = Schematic.load(path);
    }

    public static String itemName(Item item) {
        return new ItemStack(item).getHoverName().getString();
    }

    /** Counts every item in the player's inventory, including offhand and armor slots. */
    public static Map<Item, Integer> inventoryCounts() {
        Map<Item, Integer> counts = new HashMap<>();
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return counts;
        Inventory inv = client.player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    /** Items the schematic needs that you don't have enough of: item -> how many more. */
    public static Map<Item, Integer> missing() {
        Map<Item, Integer> result = new LinkedHashMap<>();
        if (selected == null) return result;
        Map<Item, Integer> have = inventoryCounts();
        selected.materials.forEach((item, need) -> {
            int shortBy = need - have.getOrDefault(item, 0);
            if (shortBy > 0) result.put(item, shortBy);
        });
        return result;
    }

    public static void startBuild() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || selected == null) return;
        if (!BaritoneBridge.isInstalled()) {
            message(Component.literal("Baritone isn't installed. Put the Baritone api-fabric jar in your mods folder.").withStyle(ChatFormatting.RED));
            return;
        }
        if (selected.fileName.contains(" ")) {
            message(Component.literal("Rename the schematic so it has no spaces, Baritone can't read names with spaces.").withStyle(ChatFormatting.RED));
            return;
        }
        BlockPos pos = client.player.blockPosition();
        BaritoneBridge.execute("set allowInventory true");
        BaritoneBridge.execute("set buildIgnoreExisting true");
        // Slower, more careful building: one layer at a time, walking, no risky jumps.
        BaritoneBridge.execute("set buildInLayers true");
        BaritoneBridge.execute("set allowSprint false");
        BaritoneBridge.execute("set allowParkour false");
        BaritoneBridge.execute("set allowDiagonalAscend false");
        boolean ok = BaritoneBridge.execute("build " + selected.fileName + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        if (ok) {
            building = true;
            paused = false;
            warnedOut.clear();
            message(Component.literal("Building " + selected.fileName + " at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).withStyle(ChatFormatting.GREEN));
        } else {
            message(Component.literal("Baritone refused the build command, check chat for its error.").withStyle(ChatFormatting.RED));
        }
    }

    public static void pause() {
        if (BaritoneBridge.execute("pause")) paused = true;
    }

    public static void resume() {
        if (BaritoneBridge.execute("resume")) {
            paused = false;
            warnedOut.clear();
        }
    }

    public static void cancel() {
        BaritoneBridge.execute("cancel");
        building = false;
        paused = false;
        warnedOut.clear();
    }

    /**
     * Once a second while building: in creative mode, refills any needed block you've run out of so the
     * build never stops for materials; in survival, warns about blocks you have none of.
     */
    public static void tick(Minecraft client) {
        if (!building || selected == null || client.player == null) return;
        if (++tickCounter % 20 != 0) return;

        if (client.player.isCreative()) {
            if (refillCreative(client) && !paused) {
                BaritoneBridge.execute("resume");
            }
            return;
        }

        Map<Item, Integer> have = inventoryCounts();
        for (Item item : selected.materials.keySet()) {
            boolean out = have.getOrDefault(item, 0) == 0;
            if (out && warnedOut.add(item)) {
                message(Component.literal("Out of " + itemName(item)
                        + ". Get more, then press Resume (or type #resume).").withStyle(ChatFormatting.GOLD));
            } else if (!out) {
                warnedOut.remove(item);
            }
        }
    }

    /** Puts a full stack of every needed-but-missing block into empty inventory slots. Creative mode only. */
    private static boolean refillCreative(Minecraft client) {
        if (client.gameMode == null) return false;
        Inventory inv = client.player.getInventory();
        Map<Item, Integer> have = inventoryCounts();
        boolean refilled = false;
        for (Item item : selected.materials.keySet()) {
            if (have.getOrDefault(item, 0) > 0) continue;
            int slot = emptySlot(inv);
            if (slot < 0) {
                if (warnedOut.add(item)) {
                    message(Component.literal("Inventory full, clear some slots so I can add "
                            + itemName(item) + ".").withStyle(ChatFormatting.GOLD));
                }
                continue;
            }
            ItemStack stack = new ItemStack(item, item.getDefaultMaxStackSize());
            inv.setItem(slot, stack.copy());
            // Inventory slots 0-8 are the hotbar (menu slots 36-44); 9-35 are the main inventory (same numbers).
            int menuSlot = slot < 9 ? 36 + slot : slot;
            client.gameMode.handleCreativeModeItemAdd(stack, menuSlot);
            warnedOut.remove(item);
            refilled = true;
        }
        return refilled;
    }

    /** First empty main-inventory slot, then hotbar; -1 if full. */
    private static int emptySlot(Inventory inv) {
        for (int i = 9; i < 36; i++) {
            if (inv.getItem(i).isEmpty()) return i;
        }
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).isEmpty()) return i;
        }
        return -1;
    }

    private static void message(Component text) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.sendSystemMessage(Component.literal("[Panda Builder] ").withStyle(ChatFormatting.AQUA).append(text));
        }
    }
}
