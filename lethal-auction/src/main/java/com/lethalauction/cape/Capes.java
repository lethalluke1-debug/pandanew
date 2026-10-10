package com.lethalauction.cape;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.LethalConfig;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * Custom capes stored in config/lethalauction/capes. The selected one replaces the local player's cape
 * on this client only.
 */
public final class Capes {
    public static final Path FOLDER = FabricLoader.getInstance().getConfigDir().resolve("lethalauction").resolve("capes");

    /** A loaded cape image. */
    public record Cape(String name, Identifier texture, int width, int height) {
    }

    private static final Map<String, Cape> LOADED = new LinkedHashMap<>();
    private static boolean scanned;
    private static String status = "";
    private static long statusUntil;
    private static volatile boolean dialogOpen;

    private Capes() {
    }

    /** All capes in the folder, loading any new ones. Must be called on the render thread. */
    public static List<Cape> all() {
        if (!scanned) {
            rescan();
        }
        return new ArrayList<>(LOADED.values());
    }

    /** The cape to draw on the local player, or null to keep their real cape. */
    public static Cape selected() {
        String name = LethalConfig.cape;
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (!scanned) {
            rescan();
        }
        return LOADED.get(name);
    }

    public static void select(String name) {
        LethalConfig.cape = name == null ? "" : name;
        LethalConfig.save();
    }

    public static void rescan() {
        scanned = true;
        try {
            Files.createDirectories(FOLDER);
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not create {}", FOLDER, e);
            return;
        }
        List<Path> files = new ArrayList<>();
        try (Stream<Path> s = Files.list(FOLDER)) {
            s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")).sorted().forEach(files::add);
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not list {}", FOLDER, e);
        }
        List<String> present = new ArrayList<>();
        for (Path file : files) {
            String name = file.getFileName().toString();
            present.add(name);
            if (!LOADED.containsKey(name)) {
                Cape cape = load(file);
                if (cape != null) {
                    LOADED.put(name, cape);
                }
            }
        }
        for (String name : new ArrayList<>(LOADED.keySet())) {
            if (!present.contains(name)) {
                Minecraft.getInstance().getTextureManager().release(LOADED.remove(name).texture());
            }
        }
    }

    private static Cape load(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            NativeImage image = fixLegacySize(NativeImage.read(in));
            String key = file.getFileName().toString().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
            Identifier id = Identifier.fromNamespaceAndPath("lethalauction", "capes/" + key);
            int w = image.getWidth(), h = image.getHeight();
            Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "Lethal Auction cape " + key, image));
            return new Cape(file.getFileName().toString(), id, w, h);
        } catch (IOException | RuntimeException e) {
            LethalAuctionClient.LOGGER.warn("Could not load cape {}", file, e);
            return null;
        }
    }

    /** Old-style 22x17 cape images are padded onto a 64x32 canvas like vanilla does. */
    private static NativeImage fixLegacySize(NativeImage image) {
        if (image.getWidth() == image.getHeight() * 2) {
            return image;
        }
        int scale = Math.max(1, image.getHeight() / 17);
        NativeImage fixed = new NativeImage(64 * scale, 32 * scale, true);
        for (int y = 0; y < Math.min(image.getHeight(), fixed.getHeight()); y++) {
            for (int x = 0; x < Math.min(image.getWidth(), fixed.getWidth()); x++) {
                fixed.setPixel(x, y, image.getPixel(x, y));
            }
        }
        image.close();
        return fixed;
    }

    /** Opens a file picker on a background thread and copies the chosen PNG into the capes folder. */
    public static void upload() {
        if (dialogOpen) {
            return;
        }
        dialogOpen = true;
        Thread t = new Thread(() -> {
            try {
                String chosen;
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    PointerBuffer filters = stack.mallocPointer(1);
                    filters.put(stack.UTF8("*.png"));
                    filters.flip();
                    chosen = TinyFileDialogs.tinyfd_openFileDialog("Choose a cape image (PNG)", "", filters, "PNG images", false);
                }
                if (chosen == null) {
                    return;
                }
                Path source = Path.of(chosen);
                Files.createDirectories(FOLDER);
                Path target = FOLDER.resolve(source.getFileName().toString());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                String name = target.getFileName().toString();
                Minecraft.getInstance().execute(() -> {
                    LOADED.remove(name);
                    rescan();
                    if (LOADED.containsKey(name)) {
                        select(name);
                        setStatus("Cape added and equipped");
                    } else {
                        setStatus("That file is not a valid PNG image");
                    }
                });
            } catch (IOException | RuntimeException e) {
                LethalAuctionClient.LOGGER.warn("Cape upload failed", e);
                Minecraft.getInstance().execute(() -> setStatus("Could not add that cape"));
            } finally {
                dialogOpen = false;
            }
        }, "Lethal Auction cape picker");
        t.setDaemon(true);
        t.start();
    }

    public static void delete(Cape cape) {
        try {
            Files.deleteIfExists(FOLDER.resolve(cape.name()));
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not delete cape {}", cape.name(), e);
        }
        if (cape.name().equals(LethalConfig.cape)) {
            select("");
        }
        rescan();
    }

    private static void setStatus(String text) {
        status = text;
        statusUntil = System.currentTimeMillis() + 3500;
    }

    public static String status() {
        return System.currentTimeMillis() < statusUntil ? status : "";
    }
}
