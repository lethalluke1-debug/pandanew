package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Detaches the camera from the player. WASD/Space/Shift fly the camera, Sprint flies faster and the mouse
 * aims it, while the real player stays still. CameraMixin applies the position, KeyboardInputMixin freezes
 * the player and EntityMixin redirects mouse turning here.
 */
public final class Freecam {
    private static final double SPEED = 0.6;
    private static final double SPRINT_SPEED = 1.8;

    private static boolean on;
    private static double x, y, z, prevX, prevY, prevZ;
    private static float yaw, pitch;

    private Freecam() {}

    public static boolean isOn() {
        return on;
    }

    public static void toggle() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!on && player == null) return;
        on = !on;
        if (on) {
            x = prevX = player.getX();
            y = prevY = player.getEyeY();
            z = prevZ = player.getZ();
            yaw = player.getYRot();
            pitch = player.getXRot();
        }
        AutoTotem.message(Component.literal("Freecam: " + (on ? "ON" : "OFF")));
    }

    public static void tick(Minecraft mc) {
        if (!on) return;
        if (mc.player == null) {
            on = false;
            return;
        }
        prevX = x;
        prevY = y;
        prevZ = z;
        if (mc.gui.screen() != null) return;

        Options o = mc.options;
        double forward = axis(o.keyUp.isDown(), o.keyDown.isDown());
        double strafe = axis(o.keyLeft.isDown(), o.keyRight.isDown());
        double vertical = axis(o.keyJump.isDown(), o.keyShift.isDown());
        double speed = o.keySprint.isDown() ? SPRINT_SPEED : SPEED;

        double rad = Math.toRadians(yaw);
        double sin = Math.sin(rad), cos = Math.cos(rad);
        x += (-sin * forward + cos * strafe) * speed;
        z += (cos * forward + sin * strafe) * speed;
        y += vertical * speed;
    }

    private static double axis(boolean positive, boolean negative) {
        return (positive ? 1 : 0) - (negative ? 1 : 0);
    }

    /** Same scaling vanilla uses in Entity.turn. */
    public static void turn(double dx, double dy) {
        yaw += (float) dx * 0.15f;
        pitch = Mth.clamp(pitch + (float) dy * 0.15f, -90.0f, 90.0f);
    }

    public static Vec3 position(float partialTick) {
        return new Vec3(Mth.lerp(partialTick, prevX, x), Mth.lerp(partialTick, prevY, y), Mth.lerp(partialTick, prevZ, z));
    }

    public static float yaw() {
        return yaw;
    }

    public static float pitch() {
        return pitch;
    }
}
