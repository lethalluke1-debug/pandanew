package com.lethalauction.cape;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.LethalConfig;
import com.lethalauction.auction.Auctions;
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

    public static final int MAX_SLOTS = 5;
    private static final Identifier UPLOAD_TEXTURE = Identifier.fromNamespaceAndPath("lethalauction", "cape_upload");

    /** A photo waiting to be cropped into a cape. Crop is in image pixels; only its position can change. */
    public static final class Pending {
        public final NativeImage image;
        public String suggestedName;
        public final int cropW, cropH;
        public int cropX, cropY;

        Pending(NativeImage image, String suggestedName) {
            this.image = image;
            this.suggestedName = suggestedName;
            int w = image.getWidth(), h = image.getHeight();
            if (w / (float) h > CapeImages.FACE_ASPECT) {
                cropH = h;
                cropW = Math.max(1, Math.round(h * CapeImages.FACE_ASPECT));
            } else {
                cropW = w;
                cropH = Math.max(1, Math.round(w / CapeImages.FACE_ASPECT));
            }
            cropX = (w - cropW) / 2;
            cropY = (h - cropH) / 2;
        }

        public Identifier texture() {
            return UPLOAD_TEXTURE;
        }

        public void moveTo(int x, int y) {
            cropX = Math.max(0, Math.min(image.getWidth() - cropW, x));
            cropY = Math.max(0, Math.min(image.getHeight() - cropH, y));
        }
    }

    private static Pending pending;
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

    public static Pending pending() {
        return pending;
    }

    public static boolean full() {
        return all().size() >= MAX_SLOTS;
    }

    /** Texture to draw on the local player right now, or null to keep their real cape. */
    public static Identifier activeTexture() {
        if (LethalConfig.auctionCape) {
            Auctions.Auction a = Auctions.current();
            if (a != null && !a.cancelled()) {
                AuctionCape.update(a);
                return AuctionCape.TEXTURE;
            }
        }
        Cape cape = selected();
        return cape == null ? null : cape.texture();
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

    /** Opens a file picker on a background thread; the chosen image then opens in the cropper. */
    public static void upload() {
        if (dialogOpen || pending != null) {
            return;
        }
        if (full()) {
            setStatus("All " + MAX_SLOTS + " slots are full. Delete a cape first.");
            return;
        }
        dialogOpen = true;
        Thread t = new Thread(() -> {
            try {
                String chosen;
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    PointerBuffer filters = stack.mallocPointer(3);
                    filters.put(stack.UTF8("*.png"));
                    filters.put(stack.UTF8("*.jpg"));
                    filters.put(stack.UTF8("*.jpeg"));
                    filters.flip();
                    chosen = TinyFileDialogs.tinyfd_openFileDialog("Choose a picture for your cape", "", filters, "Images", false);
                }
                if (chosen == null) {
                    return;
                }
                byte[] bytes = Files.readAllBytes(Path.of(chosen));
                String base = Path.of(chosen).getFileName().toString().replaceAll("\\.[A-Za-z0-9]+$", "");
                Minecraft.getInstance().execute(() -> openCropper(bytes, base));
            } catch (IOException | RuntimeException e) {
                LethalAuctionClient.LOGGER.warn("Cape upload failed", e);
                Minecraft.getInstance().execute(() -> setStatus("Could not open that picture"));
            } finally {
                dialogOpen = false;
            }
        }, "Lethal Auction cape picker");
        t.setDaemon(true);
        t.start();
    }

    /** Dev helper: open the cropper on a picture file without the file dialog. */
    public static void devOpenCropper(Path file) {
        try {
            openCropper(Files.readAllBytes(file), "Sunset");
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("dev crop failed", e);
        }
    }

    private static void openCropper(byte[] bytes, String name) {
        try {
            NativeImage image = NativeImage.read(bytes);
            NativeImage preview = new NativeImage(image.getWidth(), image.getHeight(), false);
            preview.copyFrom(image);
            Minecraft.getInstance().getTextureManager().register(UPLOAD_TEXTURE, new DynamicTexture(() -> "Lethal Auction cape upload", preview));
            pending = new Pending(image, name.isBlank() ? "Cape" : name);
        } catch (IOException | RuntimeException e) {
            LethalAuctionClient.LOGGER.warn("Could not read picture", e);
            setStatus("That picture could not be read (use PNG)");
        }
    }

    /** Saves the cropped part of the pending picture as a new cape and wears it. */
    public static void savePending() {
        Pending p = pending;
        if (p == null) {
            return;
        }
        String name = uniqueName(safeName(p.suggestedName));
        try (NativeImage cape = CapeImages.fromCrop(p.image, p.cropX, p.cropY, p.cropW, p.cropH)) {
            Files.createDirectories(FOLDER);
            cape.writeToFile(FOLDER.resolve(name + ".png"));
        } catch (IOException | RuntimeException e) {
            LethalAuctionClient.LOGGER.warn("Could not save cape", e);
            setStatus("Could not save the cape");
        }
        cancelPending();
        rescan();
        select(name + ".png");
        setStatus("Cape saved and equipped");
    }

    public static void cancelPending() {
        if (pending != null) {
            pending.image.close();
            pending = null;
            Minecraft.getInstance().getTextureManager().release(UPLOAD_TEXTURE);
        }
    }

    /** Renames a cape file. Returns false if the name is empty or taken. */
    public static boolean rename(Cape cape, String newName) {
        String clean = safeName(newName);
        if (clean.isEmpty()) {
            return false;
        }
        String file = clean + ".png";
        if (file.equals(cape.name())) {
            return true;
        }
        if (Files.exists(FOLDER.resolve(file))) {
            setStatus("A cape with that name already exists");
            return false;
        }
        try {
            Files.move(FOLDER.resolve(cape.name()), FOLDER.resolve(file));
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not rename cape", e);
            return false;
        }
        boolean wasSelected = cape.name().equals(LethalConfig.cape);
        rescan();
        if (wasSelected) {
            select(file);
        }
        return true;
    }

    private static String safeName(String name) {
        return name.trim().replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", " ");
    }

    private static String uniqueName(String base) {
        String name = base.isEmpty() ? "Cape" : base;
        int i = 2;
        while (Files.exists(FOLDER.resolve(name + ".png"))) {
            name = base + " " + i++;
        }
        return name;
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
