package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
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

    /** Every living thing except you: players, mobs and animals. Armor stands are skipped. */
    public static boolean shouldGlow(Entity entity) {
        return on && entity instanceof LivingEntity && !(entity instanceof ArmorStand)
                && entity != Minecraft.getInstance().player && entity.isAlive();
    }

    /** Box colour: players in the accent red, hostile mobs orange, everything else green. */
    public static int color(Entity entity) {
        if (entity instanceof Player) return Brand.ACCENT;
        if (entity instanceof Enemy) return 0xFFFB923C;
        return 0xFF4ADE80;
    }
}
