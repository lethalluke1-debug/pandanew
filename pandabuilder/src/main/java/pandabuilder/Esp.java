package pandabuilder;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * Draws boxes through walls around your picked Run Target blocks, and a box, label and tracer line
 * for every found base, so you can see underground bases and stashes without walking anywhere.
 * Uses Minecraft's own gizmo renderer (always-on-top), added once per client tick.
 */
public final class Esp {
   private static final int TARGET_RANGE = 64;
   private static final int MAX_BOXES = 600;
   private static final int BASE_RANGE = 1024;

   private Esp() {
   }

   public static void toggle() {
      Settings.esp = !Settings.esp;
      if (Settings.esp && !Settings.baseFinder) {
         // The ESP shows what the scanner finds, so it needs the scanner running.
         BaseFinder.toggle();
      }

      Settings.save();
      BaseFinder.message(Component.literal("ESP " + (Settings.esp ? "ON" : "OFF")).withStyle(Settings.esp ? ChatFormatting.GREEN : ChatFormatting.RED));
   }

   private static int colorFor(BaseFinder.Target t) {
      String id = t.id();
      if (id.endsWith("shulker_box")) {
         return 0xFFD040FF;
      } else if (id.endsWith("chest") || id.endsWith("barrel")) {
         return 0xFFFFAA00;
      } else if (id.endsWith("sign")) {
         return 0xFFFFFFFF;
      } else if (id.endsWith("dispenser") || id.endsWith("dropper") || id.endsWith("hopper")) {
         return 0xFFAAAAAA;
      } else {
         return 0xFF55FFFF;
      }
   }

   /** Called at the end of every client tick, which Minecraft runs inside its per-tick gizmo collection. */
   public static void tick(Minecraft client) {
      LocalPlayer player = client.player;
      if (!Settings.esp || player == null || client.level == null) {
         return;
      }

      try {
         drawTargets(player);
         drawBases(player);
      } catch (IllegalStateException e) {
         // No gizmo collector active (shouldn't happen during a client tick); skip this tick.
      }
   }

   private static void drawTargets(LocalPlayer player) {
      List<BaseFinder.Target> targets = BaseFinder.targetsNear((int)player.getX(), (int)player.getZ(), TARGET_RANGE);
      int drawn = 0;

      for (BaseFinder.Target t : targets) {
         if (drawn >= MAX_BOXES) {
            break;
         }

         if (!Settings.runTargets.contains(t.id())) {
            continue;
         }

         Gizmos.cuboid(new BlockPos(t.x(), t.y(), t.z()), 0.02F, GizmoStyle.stroke(colorFor(t), 2.0F)).setAlwaysOnTop();
         drawn++;
      }
   }

   private static void drawBases(LocalPlayer player) {
      String dim = BaseFinder.currentDimension();
      Vec3 eye = new Vec3(player.getX(), player.getEyeY(), player.getZ());

      for (BaseFinder.Base b : BaseFinder.basesInThisWorld()) {
         if (!b.dimension.equals(dim)) {
            continue;
         }

         double dist = Math.sqrt(BaseFinder.distanceSq(player, b.x, b.z));
         if (dist > BASE_RANGE) {
            continue;
         }

         BlockPos pos = new BlockPos(b.x, b.y, b.z);
         Gizmos.cuboid(pos, 1.5F, GizmoStyle.strokeAndFill(0xFF55FF55, 3.0F, 0x3055FF55)).setAlwaysOnTop();
         // Tracer from your eyes to the base, handy for underground bases.
         Gizmos.line(eye, new Vec3(b.x + 0.5, b.y + 0.5, b.z + 0.5), 0xFF55FF55, 2.0F).setAlwaysOnTop();
         Gizmos.billboardTextOverBlock("Base (score " + b.score + ") " + (int)dist + "m  y=" + b.y, pos, 0, 0xFF55FF55, 1.0F).setAlwaysOnTop();
      }
   }
}
