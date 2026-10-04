package pandabuilder;

import java.lang.reflect.Method;

public final class BaritoneBridge {
   private BaritoneBridge() {
   }

   public static boolean isInstalled() {
      try {
         Class.forName("baritone.api.BaritoneAPI");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   public static boolean execute(String command) {
      try {
         Class<?> api = Class.forName("baritone.api.BaritoneAPI");
         Object provider = api.getMethod("getProvider").invoke(null);
         Class<?> providerType = Class.forName("baritone.api.IBaritoneProvider");
         Object baritone = providerType.getMethod("getPrimaryBaritone").invoke(provider);
         Class<?> baritoneType = Class.forName("baritone.api.IBaritone");
         Object commandManager = baritoneType.getMethod("getCommandManager").invoke(baritone);
         Class<?> managerType = Class.forName("baritone.api.command.manager.ICommandManager");
         Method execute = managerType.getMethod("execute", String.class);
         return !(execute.invoke(commandManager, command) instanceof Boolean b && !b);
      } catch (LinkageError | ReflectiveOperationException var11) {
         PandaBuilderClient.LOGGER.warn("Baritone command failed: {}", command, var11);
         return false;
      }
   }
}
