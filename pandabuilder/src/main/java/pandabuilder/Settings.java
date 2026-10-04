package pandabuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** On/off switches for each feature, saved to config/pandabuilder.properties. */
public final class Settings {
    /** Lets Build Here start Baritone. */
    public static boolean builder = true;
    /** In creative mode, adds missing schematic blocks to your inventory during a build. */
    public static boolean creativeRefill = true;
    /** Shows the blocks you still need on screen whenever a schematic is selected. */
    public static boolean checklistHud = true;

    private Settings() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pandabuilder.properties");
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) return;
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            props.load(reader);
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read settings", e);
            return;
        }
        builder = Boolean.parseBoolean(props.getProperty("builder", "true"));
        creativeRefill = Boolean.parseBoolean(props.getProperty("creativeRefill", "true"));
        checklistHud = Boolean.parseBoolean(props.getProperty("checklistHud", "true"));
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("builder", Boolean.toString(builder));
        props.setProperty("creativeRefill", Boolean.toString(creativeRefill));
        props.setProperty("checklistHud", Boolean.toString(checklistHud));
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                props.store(writer, "Panda Builder settings");
            }
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not save settings", e);
        }
    }
}
