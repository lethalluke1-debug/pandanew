package pandabuilder;

import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public final class BaritoneBridge {
   public enum Mode {
      // The api-fabric jar: commands go straight through Baritone's API.
      API,
      // The standalone jar: Baritone is loaded but its API is obfuscated, so commands go through chat with the # prefix.
      CHAT,
      MISSING
   }

   private static final String CHAT_PREFIX = "#";
   private static Mode mode;

   private BaritoneBridge() {
   }

   public static Mode mode() {
      if (mode == null) {
         if (hasClass("baritone.api.BaritoneAPI")) {
            mode = Mode.API;
         } else if (FabricLoader.getInstance().isModLoaded("baritone")) {
            mode = Mode.CHAT;
         } else {
            mode = Mode.MISSING;
         }

         PandaBuilderClient.LOGGER.info("Baritone mode: {}", mode);
      }

      return mode;
   }

   public static boolean isInstalled() {
      return mode() != Mode.MISSING;
   }

   public static String statusText() {
      return switch (mode()) {
         case API -> "Baritone: found";
         case CHAT -> "Baritone: found (standalone, using # commands)";
         case MISSING -> "Baritone: NOT installed";
      };
   }

   private static boolean hasClass(String name) {
      try {
         // Don't initialize: a broken Baritone static init must not crash our menu.
         Class.forName(name, false, BaritoneBridge.class.getClassLoader());
         return true;
      } catch (ClassNotFoundException | LinkageError e) {
         return false;
      }
   }

   private static Object primaryBaritone() throws ReflectiveOperationException {
      Class<?> api = Class.forName("baritone.api.BaritoneAPI");
      Object provider = api.getMethod("getProvider").invoke(null);
      Class<?> providerType = Class.forName("baritone.api.IBaritoneProvider");
      return providerType.getMethod("getPrimaryBaritone").invoke(provider);
   }

   public static boolean execute(String command) {
      switch (mode()) {
         case API:
            try {
               Object baritone = primaryBaritone();
               Class<?> baritoneType = Class.forName("baritone.api.IBaritone");
               Object commandManager = baritoneType.getMethod("getCommandManager").invoke(baritone);
               Class<?> managerType = Class.forName("baritone.api.command.manager.ICommandManager");
               Method execute = managerType.getMethod("execute", String.class);
               return !(execute.invoke(commandManager, command) instanceof Boolean b && !b);
            } catch (LinkageError | ReflectiveOperationException e) {
               PandaBuilderClient.LOGGER.warn("Baritone command failed: {}", command, e);
               return false;
            }
         case CHAT:
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) {
               return false;
            }

            // Baritone cancels chat messages that start with its prefix, so nothing reaches the server.
            client.player.connection.sendChat(CHAT_PREFIX + command);
            return true;
         default:
            return false;
      }
   }

   // TRUE/FALSE when Baritone can tell us, null when it can't (standalone jar or an error).
   public static Boolean isProcessActive(String getter) {
      if (mode() != Mode.API) {
         return null;
      }

      try {
         Object baritone = primaryBaritone();
         Object process = Class.forName("baritone.api.IBaritone").getMethod(getter).invoke(baritone);
         Object active = Class.forName("baritone.api.process.IBaritoneProcess").getMethod("isActive").invoke(process);
         return active instanceof Boolean b ? b : null;
      } catch (LinkageError | ReflectiveOperationException e) {
         return null;
      }
   }
}
