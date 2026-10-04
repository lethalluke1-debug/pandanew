package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

/**
 * Detaches the camera from the player. Your movement keys and mouse fly the camera
 * (sprint = faster, jump/sneak = up/down) while your player stands still.
 * The camera position is applied in {@link pandabuilder.mixin.CameraMixin}, mouse look in {@link pandabuilder.mixin.EntityMixin}.
 */
public final class Freecam {
   private static final double SPEED = 0.5;
   private static final double SPRINT_SPEED = 1.5;
   private static boolean active;
   private static double x;
   private static double y;
   private static double z;
   private static double prevX;
   private static double prevY;
   private static double prevZ;
   private static float yaw;
   private static float pitch;

   private Freecam() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void toggle() {
      Minecraft client = Minecraft.getInstance();
      LocalPlayer player = client.player;
      if (!active && player == null) {
         return;
      }

      active = !active;
      if (active) {
         x = prevX = player.getX();
         y = prevY = player.getEyeY();
         z = prevZ = player.getZ();
         yaw = player.getYRot();
         pitch = player.getXRot();
         releaseMovementKeys(client.options);
      }

      BaseFinder.message(Component.literal("Freecam " + (active ? "ON" : "OFF")).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED));
   }

   /** Runs before the player ticks, so the player never sees the movement keys as pressed. */
   public static void startTick(Minecraft client) {
      if (!active) {
         return;
      }

      if (client.player == null || client.level == null || client.player.getHealth() <= 0.0F) {
         active = false;
         return;
      }

      prevX = x;
      prevY = y;
      prevZ = z;
      Options o = client.options;
      if (client.gui.screen() == null) {
         double forward = (held(client, o.keyUp) ? 1 : 0) - (held(client, o.keyDown) ? 1 : 0);
         double strafe = (held(client, o.keyLeft) ? 1 : 0) - (held(client, o.keyRight) ? 1 : 0);
         double up = (held(client, o.keyJump) ? 1 : 0) - (held(client, o.keyShift) ? 1 : 0);
         double speed = held(client, o.keySprint) ? SPRINT_SPEED : SPEED;
         if (forward != 0 && strafe != 0) {
            forward *= 0.7071;
            strafe *= 0.7071;
         }

         double rad = Math.toRadians(yaw);
         double sin = Math.sin(rad);
         double cos = Math.cos(rad);
         // Minecraft yaw: 0 = south (+Z), 90 = west (-X).
         x += (-sin * forward + cos * strafe) * speed;
         z += (cos * forward + sin * strafe) * speed;
         y += up * speed;
      }

      releaseMovementKeys(o);
   }

   private static void releaseMovementKeys(Options o) {
      o.keyUp.setDown(false);
      o.keyDown.setDown(false);
      o.keyLeft.setDown(false);
      o.keyRight.setDown(false);
      o.keyJump.setDown(false);
      o.keyShift.setDown(false);
      o.keySprint.setDown(false);
   }

   // The key mappings are forced up every tick, so read the physical keyboard instead.
   private static boolean held(Minecraft client, KeyMapping mapping) {
      String name = mapping.saveString();
      if (!name.startsWith("key.keyboard.")) {
         return false;
      }

      try {
         return InputConstants.isKeyDown(client.getWindow(), InputConstants.getKey(name).getValue());
      } catch (IllegalArgumentException e) {
         return false;
      }
   }

   public static void turn(double xo, double yo) {
      yaw += (float)xo * 0.15F;
      pitch = Math.max(-90.0F, Math.min(90.0F, pitch + (float)yo * 0.15F));
   }

   public static double x(float partialTicks) {
      return prevX + (x - prevX) * partialTicks;
   }

   public static double y(float partialTicks) {
      return prevY + (y - prevY) * partialTicks;
   }

   public static double z(float partialTicks) {
      return prevZ + (z - prevZ) * partialTicks;
   }

   public static float yaw() {
      return yaw;
   }

   public static float pitch() {
      return pitch;
   }
}
