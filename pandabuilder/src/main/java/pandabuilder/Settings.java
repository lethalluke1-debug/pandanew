package pandabuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class Settings {
   public static boolean autoTotem = false;

   private Settings() {
   }

   private static Path file() {
      return FabricLoader.getInstance().getConfigDir().resolve("pandabuilder.properties");
   }

   public static void load() {
      Path file = file();
      if (Files.exists(file)) {
         Properties props = new Properties();

         try (Reader reader = Files.newBufferedReader(file)) {
            props.load(reader);
         } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read settings", e);
            return;
         }

         autoTotem = Boolean.parseBoolean(props.getProperty("autoTotem", "false"));
      }
   }

   public static void save() {
      Properties props = new Properties();
      props.setProperty("autoTotem", Boolean.toString(autoTotem));
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
