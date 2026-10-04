package pandabuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class Settings {
   public static boolean builder = true;
   public static boolean creativeRefill = true;
   public static boolean checklistHud = true;
   public static boolean autoTotem = false;
   public static boolean baseFinder = false;
   public static boolean stopOnFind = false;
   public static int baseSensitivity = 1;
   public static boolean clickStorageRun = true;
   public static boolean esp = false;
   // Block ids ("minecraft:chest") the Storage Run walks to.
   public static final Set<String> runTargets = new LinkedHashSet<>(defaultRunTargets());

   private Settings() {
   }

   public static Set<String> defaultRunTargets() {
      Set<String> ids = new LinkedHashSet<>();

      for (String id : new String[]{"chest", "trapped_chest", "barrel", "shulker_box", "dispenser", "dropper"}) {
         ids.add("minecraft:" + id);
      }

      for (String color : new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
      }) {
         ids.add("minecraft:" + color + "_shulker_box");
      }

      for (String wood : new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "pale_oak", "bamboo", "crimson", "warped"}) {
         ids.add("minecraft:" + wood + "_sign");
         ids.add("minecraft:" + wood + "_wall_sign");
         ids.add("minecraft:" + wood + "_hanging_sign");
         ids.add("minecraft:" + wood + "_wall_hanging_sign");
      }

      return ids;
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
         } catch (IOException var7) {
            PandaBuilderClient.LOGGER.warn("Could not read settings", var7);
            return;
         }

         builder = Boolean.parseBoolean(props.getProperty("builder", "true"));
         creativeRefill = Boolean.parseBoolean(props.getProperty("creativeRefill", "true"));
         checklistHud = Boolean.parseBoolean(props.getProperty("checklistHud", "true"));
         autoTotem = Boolean.parseBoolean(props.getProperty("autoTotem", "false"));
         baseFinder = Boolean.parseBoolean(props.getProperty("baseFinder", "false"));
         stopOnFind = Boolean.parseBoolean(props.getProperty("stopOnFind", "false"));
         clickStorageRun = Boolean.parseBoolean(props.getProperty("clickStorageRun", "true"));
         esp = Boolean.parseBoolean(props.getProperty("esp", "false"));
         String targets = props.getProperty("runTargets");
         if (targets != null) {
            runTargets.clear();

            for (String id : targets.split(",")) {
               if (!id.isBlank()) {
                  runTargets.add(id.trim());
               }
            }
         }

         try {
            baseSensitivity = Math.max(0, Math.min(2, Integer.parseInt(props.getProperty("baseSensitivity", "1"))));
         } catch (NumberFormatException e) {
            baseSensitivity = 1;
         }
      }
   }

   public static void save() {
      Properties props = new Properties();
      props.setProperty("builder", Boolean.toString(builder));
      props.setProperty("creativeRefill", Boolean.toString(creativeRefill));
      props.setProperty("checklistHud", Boolean.toString(checklistHud));
      props.setProperty("autoTotem", Boolean.toString(autoTotem));
      props.setProperty("baseFinder", Boolean.toString(baseFinder));
      props.setProperty("stopOnFind", Boolean.toString(stopOnFind));
      props.setProperty("clickStorageRun", Boolean.toString(clickStorageRun));
      props.setProperty("esp", Boolean.toString(esp));
      props.setProperty("runTargets", String.join(",", runTargets));
      props.setProperty("baseSensitivity", Integer.toString(baseSensitivity));
      Path file = file();

      try {
         Files.createDirectories(file.getParent());

         try (Writer writer = Files.newBufferedWriter(file)) {
            props.store(writer, "Panda Builder settings");
         }
      } catch (IOException var7) {
         PandaBuilderClient.LOGGER.warn("Could not save settings", var7);
      }
   }
}
