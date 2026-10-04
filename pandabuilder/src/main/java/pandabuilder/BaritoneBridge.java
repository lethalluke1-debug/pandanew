package pandabuilder;

import java.lang.reflect.Method;

/**
 * Talks to Baritone through reflection so this mod builds and runs without Baritone on the classpath.
 * Requires a Baritone jar that includes the API (the "api-fabric" release, or Meteor Client's bundled Baritone).
 */
public final class BaritoneBridge {
    private BaritoneBridge() {
    }

    public static boolean isInstalled() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /** Runs a Baritone command without the "#" prefix, e.g. "build house.schem 0 64 0". */
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
            Object result = execute.invoke(commandManager, command);
            return !(result instanceof Boolean b) || b;
        } catch (ReflectiveOperationException | LinkageError e) {
            PandaBuilderClient.LOGGER.warn("Baritone command failed: {}", command, e);
            return false;
        }
    }
}
