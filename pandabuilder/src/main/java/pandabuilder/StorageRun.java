package pandabuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

/**
 * Walks Baritone into a found base and visits every chest, barrel, shulker box, dispenser/dropper and sign there,
 * nearest first. Baritone digs its own way in with whatever pickaxe is in the inventory (allowInventory moves it to the hotbar).
 * At a container it waits for you to open and close it (or press Next); at a sign it waits a few seconds so you can read it.
 */
public final class StorageRun {
   private static final int SEARCH_RADIUS = 64;
   private static final int APPROACH_TIMEOUT_TICKS = 20 * 60;
   private static final int VISIT_TIMEOUT_TICKS = 20 * 90;
   private static final int SIGN_READ_TICKS = 20 * 4;
   private static final int OPEN_WAIT_TICKS = 20 * 8;
   private static final double REACH = 4.0;
   private static final double CLOSE = 2.6;

   private enum Phase {
      IDLE,
      APPROACH,
      TRAVEL,
      AT_STORAGE
   }

   private static Phase phase = Phase.IDLE;
   private static BaseFinder.Base base;
   private static final Set<Long> visited = new HashSet<>();
   private static BaseFinder.Target target;
   private static int total;
   private static int phaseTicks;
   private static int idleChecks;
   private static boolean openedHere;

   private StorageRun() {
   }

   public static boolean isRunning() {
      return phase != Phase.IDLE;
   }

   public static String status() {
      return switch (phase) {
         case IDLE -> "";
         case APPROACH -> "Storage run: walking to the base area to scan it (no target yet)";
         case TRAVEL -> "Storage run: " + visited.size() + "/" + total + " -> " + target.kind() + " at " + target.x() + ", " + target.y() + ", " + target.z();
         case AT_STORAGE -> "Storage run: " + (visited.size() + 1) + "/" + total + (target.isSign() ? " reading sign..." : " open it, then close it (or press Next)");
      };
   }

   public static void start(BaseFinder.Base b) {
      if (!BaritoneBridge.isInstalled()) {
         BaseFinder.message(Component.literal("Storage Run needs Baritone. Put the Baritone fabric jar for 26.2 in your mods folder.").withStyle(ChatFormatting.RED));
         return;
      }

      if (!b.dimension.equals(BaseFinder.currentDimension())) {
         BaseFinder.message(Component.literal("That base is in " + b.dimension + ", go there first.").withStyle(ChatFormatting.GOLD));
         return;
      }

      if (BuildManager.isBuilding()) {
         BaseFinder.message(Component.literal("Cancel the current build first.").withStyle(ChatFormatting.RED));
         return;
      }

      if (Settings.runTargets.isEmpty()) {
         BaseFinder.message(Component.literal("No Run Targets picked. Pick some blocks in Base Finder > Run Targets.").withStyle(ChatFormatting.RED));
         return;
      }

      AutoExplore.setEnabled(false);
      if (!Settings.baseFinder) {
         BaseFinder.toggle();
      }

      BaritoneBridge.execute("set allowBreak true");
      BaritoneBridge.execute("set allowInventory true");
      BaritoneBridge.execute("set autoTool true");
      BaritoneBridge.execute("set blocksToAvoidBreaking " + avoidList());
      base = b;
      BaseFinder.rescanNear(b.x, b.z, SEARCH_RADIUS);
      visited.clear();
      target = null;
      total = 0;
      BaseFinder.message(Component.literal("Storage run started at " + b.x + ", " + b.y + ", " + b.z + ". Baritone will dig in with your pickaxe.").withStyle(ChatFormatting.GREEN));
      if (!nextTarget()) {
         // Chunks around the base aren't scanned yet (too far away): walk there first.
         phase = Phase.APPROACH;
         phaseTicks = 0;
         BaseFinder.message(Component.literal("None of your picked blocks are scanned near that base yet, walking to the base area first.").withStyle(ChatFormatting.GOLD));
         BaritoneBridge.execute("goto " + b.x + " " + b.y + " " + b.z);
      }
   }

   public static void stop(boolean tellBaritone) {
      if (phase == Phase.IDLE) {
         return;
      }

      phase = Phase.IDLE;
      target = null;
      if (tellBaritone) {
         BaritoneBridge.execute("cancel");
      }

      BaseFinder.message(Component.literal("Storage run stopped (" + visited.size() + " visited).").withStyle(ChatFormatting.GOLD));
   }

   public static void next() {
      if (phase == Phase.AT_STORAGE || phase == Phase.TRAVEL) {
         markVisited();
         if (!nextTarget()) {
            finish();
         }
      }
   }

   private static String avoidList() {
      // Baritone's defaults plus everything you picked, so it doesn't dig through what it's visiting.
      Set<String> names = new java.util.LinkedHashSet<>(List.of("minecraft:crafting_table", "minecraft:furnace", "minecraft:chest", "minecraft:trapped_chest"));
      names.addAll(Settings.runTargets);
      return String.join(",", names);
   }

   private static long key(BaseFinder.Target p) {
      return ((long)p.x() & 67108863L) << 38 | ((long)p.y() & 4095L) << 26 | (long)p.z() & 67108863L;
   }

   private static void markVisited() {
      if (target != null) {
         visited.add(key(target));
      }
   }

   /** Picks the nearest unvisited target and sends Baritone right next to it. */
   private static boolean nextTarget() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || base == null) {
         return false;
      }

      List<BaseFinder.Target> todo = new ArrayList<>();

      for (BaseFinder.Target p : BaseFinder.targetsNear(base.x, base.z, SEARCH_RADIUS)) {
         if (visited.contains(key(p)) || !Settings.runTargets.contains(p.id()) || Boolean.FALSE.equals(BaseFinder.stillTarget(p))) {
            continue;
         }

         // A double chest is two blocks: only visit one half.
         boolean twin = false;

         for (BaseFinder.Target q : todo) {
            if (p.isChest() && q.kind().equals(p.kind()) && q.y() == p.y() && Math.abs(q.x() - p.x()) + Math.abs(q.z() - p.z()) == 1) {
               twin = true;
               break;
            }
         }

         if (!twin) {
            todo.add(p);
         }
      }

      total = Math.max(total, visited.size() + todo.size());
      if (todo.isEmpty()) {
         return false;
      }

      BaseFinder.Target best = null;
      double bestD = Double.MAX_VALUE;

      for (BaseFinder.Target p : todo) {
         double d = distSq(player, p, 4.0);
         if (d < bestD) {
            bestD = d;
            best = p;
         }
      }

      target = best;
      BaseFinder.message(
         Component.literal("Going to " + best.id() + " at " + best.x() + ", " + best.y() + ", " + best.z() + " (" + (visited.size() + 1) + "/" + total + ")")
            .withStyle(ChatFormatting.AQUA)
      );
      phase = Phase.TRAVEL;
      phaseTicks = 0;
      idleChecks = 0;
      openedHere = false;
      // Next to the block, never inside it. Without the API jar fall back to goto: on top of solid
      // containers, or the sign's own spot (signs have no collision).
      if (!BaritoneBridge.gotoNextTo(best.x(), best.y(), best.z())) {
         int y = best.isSign() ? best.y() : best.y() + 1;
         BaritoneBridge.execute("goto " + best.x() + " " + y + " " + best.z());
      }

      return true;
   }

   private static double distSq(LocalPlayer player, BaseFinder.Target p, double yWeight) {
      double dx = player.getX() - (p.x() + 0.5);
      double dy = player.getEyeY() - (p.y() + 0.5);
      double dz = player.getZ() - (p.z() + 0.5);
      return dx * dx + dy * dy * yWeight + dz * dz;
   }

   private static void finish() {
      phase = Phase.IDLE;
      target = null;
      BaseFinder.message(Component.literal("Storage run done: visited " + visited.size() + " spots.").withStyle(ChatFormatting.GREEN));
   }

   public static void tick(Minecraft client) {
      LocalPlayer player = client.player;
      if (phase == Phase.IDLE) {
         return;
      }

      if (player == null || client.level == null || player.getHealth() <= 0.0F || base == null || !base.dimension.equals(BaseFinder.currentDimension())) {
         stop(false);
         return;
      }

      phaseTicks++;
      switch (phase) {
         case APPROACH:
            if (phaseTicks % 20 == 0) {
               if (phaseTicks % 100 == 0) {
                  BaseFinder.rescanNear(base.x, base.z, SEARCH_RADIUS);
               }

               if (nextTarget()) {
                  return;
               }

               if (phaseTicks > APPROACH_TIMEOUT_TICKS && Boolean.FALSE.equals(BaritoneBridge.isProcessActive("getCustomGoalProcess"))) {
                  BaseFinder.message(Component.literal("Reached the base area but found none of your picked blocks (Base Finder > Run Targets).").withStyle(ChatFormatting.GOLD));
                  phase = Phase.IDLE;
               }
            }
            break;
         case TRAVEL:
            if (phaseTicks % 10 == 0 && Boolean.FALSE.equals(BaseFinder.stillTarget(target))) {
               BaseFinder.message(Component.literal("The " + target.kind() + " at " + target.x() + ", " + target.y() + ", " + target.z() + " isn't there anymore (or isn't picked), skipping it.").withStyle(ChatFormatting.GOLD));
               next();
               return;
            }

            double d = distSq(player, target, 1.0);
            boolean baritoneDone = phaseTicks % 10 == 0 && Boolean.FALSE.equals(BaritoneBridge.isProcessActive("getCustomGoalProcess"));
            if (d <= CLOSE * CLOSE || d <= REACH * REACH && baritoneDone) {
               phase = Phase.AT_STORAGE;
               phaseTicks = 0;
               BaritoneBridge.execute("cancel");
               String what = target.kind() + " " + (visited.size() + 1) + "/" + total + " (" + target.x() + ", " + target.y() + ", " + target.z() + ")";
               BaseFinder.message(
                  Component.literal(target.isSign() ? "At " + what + ". Read it, moving on in a few seconds." : "At " + what + ". Open it (within 8s) and close it, and I'll go to the next one.")
                     .withStyle(ChatFormatting.GREEN)
               );
            } else if (phaseTicks % 20 == 0) {
               // Baritone gave up (no path) or someone removed the container: skip it.
               if (Boolean.FALSE.equals(BaritoneBridge.isProcessActive("getCustomGoalProcess"))) {
                  idleChecks++;
               } else {
                  idleChecks = 0;
               }

               if (idleChecks >= 3 || phaseTicks > VISIT_TIMEOUT_TICKS) {
                  BaseFinder.message(Component.literal("Couldn't reach the " + target.kind() + " at " + target.x() + ", " + target.y() + ", " + target.z() + ", skipping it.").withStyle(ChatFormatting.GOLD));
                  next();
               }
            }
            break;
         case AT_STORAGE:
            if (target.isSign()) {
               if (phaseTicks >= SIGN_READ_TICKS) {
                  next();
               }

               break;
            }

            boolean open = player.containerMenu != player.inventoryMenu;
            if (open) {
               openedHere = true;
            } else if (openedHere || phaseTicks >= OPEN_WAIT_TICKS) {
               // Closed it, or didn't open anything (not every picked block has an inventory).
               next();
            }
            break;
         default:
            break;
      }
   }
}
