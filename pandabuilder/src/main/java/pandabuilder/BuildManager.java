package pandabuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        return MinecraftClient.getInstance().runDirectory.toPath().resolve("schematics");
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

    /** Counts every item in the player's inventory, including offhand and armor slots. */
    public static Map<Item, Integer> inventoryCounts() {
        Map<Item, Integer> counts = new HashMap<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return counts;
        PlayerInventory inv = client.player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    /** Items the schematic needs that you don't have enough of: item -> how many more. */
    public static Map<Item, Integer> missing() {
        Map<Item, Integer> result = new java.util.LinkedHashMap<>();
        if (selected == null) return result;
        Map<Item, Integer> have = inventoryCounts();
        selected.materials.forEach((item, need) -> {
            int short_ = need - have.getOrDefault(item, 0);
            if (short_ > 0) result.put(item, short_);
        });
        return result;
    }

    public static void startBuild() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || selected == null) return;
        if (!BaritoneBridge.isInstalled()) {
            message(Text.literal("Baritone isn't installed. Put the Baritone api-fabric jar in your mods folder.").formatted(Formatting.RED));
            return;
        }
        if (selected.fileName.contains(" ")) {
            message(Text.literal("Rename the schematic so it has no spaces, Baritone can't read names with spaces.").formatted(Formatting.RED));
            return;
        }
        BlockPos pos = client.player.getBlockPos();
        BaritoneBridge.execute("set allowInventory true");
        BaritoneBridge.execute("set buildIgnoreExisting true");
        boolean ok = BaritoneBridge.execute("build " + selected.fileName + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        if (ok) {
            building = true;
            paused = false;
            warnedOut.clear();
            message(Text.literal("Building " + selected.fileName + " at " + pos.toShortString()).formatted(Formatting.GREEN));
        } else {
            message(Text.literal("Baritone refused the build command, check chat for its error.").formatted(Formatting.RED));
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

    /** Once a second while building, warns about any needed block you have none of. */
    public static void tick(MinecraftClient client) {
        if (!building || selected == null || client.player == null) return;
        if (++tickCounter % 20 != 0) return;

        Map<Item, Integer> have = inventoryCounts();
        for (Item item : selected.materials.keySet()) {
            boolean out = have.getOrDefault(item, 0) == 0;
            if (out && warnedOut.add(item)) {
                message(Text.literal("Out of " + item.getName().getString()
                        + ". Get more, then press Resume (or type #resume).").formatted(Formatting.GOLD));
            } else if (!out) {
                warnedOut.remove(item);
            }
        }
    }

    private static void message(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[Panda Builder] ").formatted(Formatting.AQUA).append(text), false);
        }
    }
}
