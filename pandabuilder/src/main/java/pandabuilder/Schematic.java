package pandabuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** A loaded schematic file and the items needed to build it. */
public final class Schematic {
    public final Path path;
    public final String fileName;
    /** Items needed, largest count first. Empty if the format has no material info (legacy .schematic). */
    public final Map<Item, Integer> materials;
    public final boolean materialsKnown;

    private Schematic(Path path, Map<Item, Integer> materials, boolean materialsKnown) {
        this.path = path;
        this.fileName = path.getFileName().toString();
        this.materials = materials;
        this.materialsKnown = materialsKnown;
    }

    public static Schematic load(Path path) throws IOException {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".schematic")) {
            // Legacy MCEdit format uses numeric block ids; Baritone can still build it.
            return new Schematic(path, Map.of(), false);
        }

        CompoundTag root = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        Map<String, Long> stateCounts = new HashMap<>();
        if (name.endsWith(".litematic")) {
            readLitematic(root, stateCounts);
        } else {
            readSponge(root, stateCounts);
        }

        Map<Item, Integer> items = new HashMap<>();
        stateCounts.forEach((state, count) -> addState(state, count, items));

        List<Map.Entry<Item, Integer>> sorted = new ArrayList<>(items.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        Map<Item, Integer> ordered = new LinkedHashMap<>();
        for (Map.Entry<Item, Integer> e : sorted) {
            ordered.put(e.getKey(), e.getValue());
        }
        return new Schematic(path, ordered, true);
    }

    /** Sponge .schem, versions 2 and 3. */
    private static void readSponge(CompoundTag root, Map<String, Long> out) {
        if (root.contains("Schematic")) {
            root = root.getCompoundOrEmpty("Schematic");
        }
        CompoundTag palette;
        byte[] data;
        if (root.contains("Blocks")) {
            CompoundTag blocks = root.getCompoundOrEmpty("Blocks");
            palette = blocks.getCompoundOrEmpty("Palette");
            data = blocks.getByteArray("Data").orElse(new byte[0]);
        } else {
            palette = root.getCompoundOrEmpty("Palette");
            data = root.getByteArray("BlockData").orElse(new byte[0]);
        }

        Map<Integer, String> byId = new HashMap<>();
        int maxId = 0;
        for (String state : palette.keySet()) {
            int id = palette.getIntOr(state, -1);
            if (id < 0) continue;
            byId.put(id, state);
            maxId = Math.max(maxId, id);
        }

        long[] counts = new long[maxId + 1];
        int i = 0;
        while (i < data.length) {
            int value = 0;
            int shift = 0;
            byte b;
            do {
                b = data[i++];
                value |= (b & 0x7F) << shift;
                shift += 7;
            } while ((b & 0x80) != 0 && i < data.length);
            if (value >= 0 && value < counts.length) {
                counts[value]++;
            }
        }
        for (int id = 0; id < counts.length; id++) {
            String state = byId.get(id);
            if (state != null && counts[id] > 0) {
                out.merge(state, counts[id], Long::sum);
            }
        }
    }

    /** Litematica .litematic, all regions. */
    private static void readLitematic(CompoundTag root, Map<String, Long> out) {
        CompoundTag regions = root.getCompoundOrEmpty("Regions");
        for (String regionName : regions.keySet()) {
            CompoundTag region = regions.getCompoundOrEmpty(regionName);
            ListTag paletteNbt = region.getListOrEmpty("BlockStatePalette");
            long[] states = region.getLongArray("BlockStates").orElse(new long[0]);
            CompoundTag size = region.getCompoundOrEmpty("Size");
            long volume = Math.abs((long) size.getIntOr("x", 0) * size.getIntOr("y", 0) * size.getIntOr("z", 0));

            String[] palette = new String[paletteNbt.size()];
            for (int p = 0; p < palette.length; p++) {
                CompoundTag entry = paletteNbt.getCompoundOrEmpty(p);
                StringBuilder sb = new StringBuilder(entry.getStringOr("Name", "minecraft:air"));
                if (entry.contains("Properties")) {
                    CompoundTag props = entry.getCompoundOrEmpty("Properties");
                    sb.append('[');
                    boolean first = true;
                    for (String key : props.keySet()) {
                        if (!first) sb.append(',');
                        sb.append(key).append('=').append(props.getStringOr(key, ""));
                        first = false;
                    }
                    sb.append(']');
                }
                palette[p] = sb.toString();
            }
            if (palette.length == 0) continue;

            int bits = Math.max(2, 32 - Integer.numberOfLeadingZeros(palette.length - 1));
            long mask = (1L << bits) - 1L;
            long[] counts = new long[palette.length];
            for (long index = 0; index < volume; index++) {
                long startBit = index * bits;
                int startLong = (int) (startBit >> 6);
                int endLong = (int) ((startBit + bits - 1) >> 6);
                int offset = (int) (startBit & 63);
                if (endLong >= states.length) break;
                long value;
                if (startLong == endLong) {
                    value = (states[startLong] >>> offset) & mask;
                } else {
                    value = ((states[startLong] >>> offset) | (states[endLong] << (64 - offset))) & mask;
                }
                if (value < counts.length) {
                    counts[(int) value]++;
                }
            }
            for (int p = 0; p < palette.length; p++) {
                if (counts[p] > 0) {
                    out.merge(palette[p], counts[p], Long::sum);
                }
            }
        }
    }

    /** Converts a block state like "minecraft:oak_slab[type=double]" into the item(s) needed to place it. */
    private static void addState(String state, long count, Map<Item, Integer> out) {
        String id = state;
        String props = "";
        int bracket = state.indexOf('[');
        if (bracket >= 0) {
            id = state.substring(0, bracket);
            props = state.substring(bracket);
        }
        // The second half of doors, tall plants and beds is placed automatically.
        if (props.contains("half=upper") || props.contains("part=head")) return;

        Identifier identifier = Identifier.tryParse(id);
        if (identifier == null) return;
        Block block = BuiltInRegistries.BLOCK.getValue(identifier);
        Item item = block.asItem();
        if (item == Items.AIR) return;

        long perBlock = props.contains("type=double") ? 2 : 1;
        long total = count * perBlock;
        out.merge(item, (int) Math.min(Integer.MAX_VALUE, total), (a, b) -> (int) Math.min(Integer.MAX_VALUE, (long) a + b));
    }
}
