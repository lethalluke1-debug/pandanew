package com.lethalauction;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/** User settings, saved to config/lethalauction.properties. */
public final class LethalConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lethalauction.properties");

    /** Index into the theme presets, or -1 for the default Lethal theme. */
    public static int preset = -1;
    public static boolean seeThrough = false;
    public static boolean frostedBlur = true;
    public static boolean ambientBackground = true;
    public static boolean moduleSound = true;

    private LethalConfig() {
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            return;
        }
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(FILE)) {
            p.load(r);
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not read {}", FILE, e);
            return;
        }
        try {
            preset = Integer.parseInt(p.getProperty("preset", "-1"));
        } catch (NumberFormatException e) {
            preset = -1;
        }
        seeThrough = Boolean.parseBoolean(p.getProperty("seeThrough", "false"));
        frostedBlur = Boolean.parseBoolean(p.getProperty("frostedBlur", "true"));
        ambientBackground = Boolean.parseBoolean(p.getProperty("ambientBackground", "true"));
        moduleSound = Boolean.parseBoolean(p.getProperty("moduleSound", "true"));
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("preset", Integer.toString(preset));
        p.setProperty("seeThrough", Boolean.toString(seeThrough));
        p.setProperty("frostedBlur", Boolean.toString(frostedBlur));
        p.setProperty("ambientBackground", Boolean.toString(ambientBackground));
        p.setProperty("moduleSound", Boolean.toString(moduleSound));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                p.store(w, "Lethal Auction settings");
            }
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", FILE, e);
        }
    }

    public static void resetTheme() {
        preset = -1;
        seeThrough = false;
        frostedBlur = true;
        ambientBackground = true;
    }
}
