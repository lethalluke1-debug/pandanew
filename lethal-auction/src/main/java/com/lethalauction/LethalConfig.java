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
    /** Volume for the mod's sounds, 0 to 1. */
    public static float volume = 0.4f;
    /** File name of the selected custom cape in config/lethalauction/capes, or empty for none. */
    public static String cape = "";
    /** Wear the animated auction cape (with the timer) while an auction runs. Off by default. */
    public static boolean auctionCape = false;

    // Scoreboard sidebar
    public static float sbScale = 1f;
    public static boolean sbRounded = false;
    /** 0 = Default (vanilla), 1 = Theme, 2 = Custom colour, 3 = Clear. */
    public static int sbBackground = 0;
    public static float sbHue = 0.8f;
    public static float sbOpacity = 0.45f;

    // Last values used in the menu
    public static String lastPage = "AUCTION";
    public static String formMinBid = "";
    public static String formQuantity = "1";
    public static String formTimer = "60";
    public static String formWorth = "";
    public static String formNote = "Pay over = I keep";

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
        lastPage = p.getProperty("lastPage", lastPage);
        formMinBid = p.getProperty("formMinBid", formMinBid);
        formQuantity = p.getProperty("formQuantity", formQuantity);
        formTimer = p.getProperty("formTimer", formTimer);
        formWorth = p.getProperty("formWorth", formWorth);
        formNote = p.getProperty("formNote", formNote);
        cape = p.getProperty("cape", cape);
        auctionCape = Boolean.parseBoolean(p.getProperty("auctionCape", "false"));
        sbScale = clamp(parseFloat(p.getProperty("sbScale"), 1f), 0.5f, 1.5f);
        sbRounded = Boolean.parseBoolean(p.getProperty("sbRounded", "false"));
        sbBackground = Math.floorMod((int) parseFloat(p.getProperty("sbBackground"), 0), 4);
        sbHue = clamp(parseFloat(p.getProperty("sbHue"), 0.8f), 0f, 1f);
        sbOpacity = clamp(parseFloat(p.getProperty("sbOpacity"), 0.45f), 0f, 1f);
        try {
            volume = Math.max(0f, Math.min(1f, Float.parseFloat(p.getProperty("volume", "0.4"))));
        } catch (NumberFormatException e) {
            volume = 0.4f;
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("preset", Integer.toString(preset));
        p.setProperty("seeThrough", Boolean.toString(seeThrough));
        p.setProperty("frostedBlur", Boolean.toString(frostedBlur));
        p.setProperty("ambientBackground", Boolean.toString(ambientBackground));
        p.setProperty("moduleSound", Boolean.toString(moduleSound));
        p.setProperty("lastPage", lastPage);
        p.setProperty("formMinBid", formMinBid);
        p.setProperty("formQuantity", formQuantity);
        p.setProperty("formTimer", formTimer);
        p.setProperty("formWorth", formWorth);
        p.setProperty("formNote", formNote);
        p.setProperty("cape", cape);
        p.setProperty("auctionCape", Boolean.toString(auctionCape));
        p.setProperty("sbScale", Float.toString(sbScale));
        p.setProperty("sbRounded", Boolean.toString(sbRounded));
        p.setProperty("sbBackground", Integer.toString(sbBackground));
        p.setProperty("sbHue", Float.toString(sbHue));
        p.setProperty("sbOpacity", Float.toString(sbOpacity));
        p.setProperty("volume", Float.toString(volume));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                p.store(w, "Lethal Auction settings");
            }
        } catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", FILE, e);
        }
    }

    private static float parseFloat(String s, float def) {
        try {
            return s == null ? def : Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static void resetTheme() {
        preset = -1;
        seeThrough = false;
        frostedBlur = true;
        ambientBackground = true;
    }
}
