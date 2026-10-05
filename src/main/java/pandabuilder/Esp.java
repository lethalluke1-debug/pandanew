package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class Esp {
    private static boolean on;

    private Esp() {}

    public static void toggle() {
        on = !on;
        AutoTotem.message(Component.literal("ESP: " + (on ? "ON" : "OFF")));
    }

    public static boolean isOn() {
        return on;
    }

    /** Used by MinecraftMixin: other players get the glowing outline, which renders through walls. */
    public static boolean shouldGlow(Entity entity) {
        return on && entity instanceof Player && entity != Minecraft.getInstance().player;
    }
}
