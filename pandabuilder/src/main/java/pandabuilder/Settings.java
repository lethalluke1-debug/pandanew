package pandabuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Settings {
    public static boolean autoTotem = false;
    public static boolean anchorAura = false;
    /** How far (in blocks) Anchor Aura looks for a target. */
    public static double anchorRange = 6.0;
    /** Ticks to wait between anchor detonations. */
    public static int anchorDelay = 2;

    private Settings() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pandabuilder.properties");
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            props.load(reader);
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read settings", e);
            return;
        }
        autoTotem = Boolean.parseBoolean(props.getProperty("autoTotem", "false"));
        anchorAura = Boolean.parseBoolean(props.getProperty("anchorAura", "false"));
        try {
            anchorRange = Double.parseDouble(props.getProperty("anchorRange", "6.0"));
            anchorDelay = Integer.parseInt(props.getProperty("anchorDelay", "2"));
        } catch (NumberFormatException e) {
            PandaBuilderClient.LOGGER.warn("Invalid Anchor Aura setting, using defaults", e);
            anchorRange = 6.0;
            anchorDelay = 2;
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("autoTotem", Boolean.toString(autoTotem));
        props.setProperty("anchorAura", Boolean.toString(anchorAura));
        props.setProperty("anchorRange", Double.toString(anchorRange));
        props.setProperty("anchorDelay", Integer.toString(anchorDelay));
        Path path = file();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                props.store(writer, "Panda Builder settings");
            }
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not save settings", e);
        }
    }
}
