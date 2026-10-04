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
 * Walks Baritone into a found base and visits every chest, barrel and shulker box there, nearest first.
 * Baritone digs its own way in with whatever pickaxe is in the inventory (allowInventory moves it to the hotbar).
 * At each container it waits for you to open and close it (or press Next) before moving on.
 */
public final class StorageRun {
   private static final int SEARCH_RADIUS = 64;
   private static final int APPROACH_TIMEOUT_TICKS = 20 * 60;
   private static final int VISIT_TIMEOUT_TICKS = 20 * 90;

   private enum Phase {
      IDLE,
      APPROACH,
      TRAVEL,
      AT_STORAGE
   }

   private static Phase phase = Phase.IDLE;
   private static BaseFinder.Base base;
   private static final Set<Long> visited = new HashSet<>();
   private static int[] target;
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
         case APPROACH -> "Storage run: heading to base";
         case TRAVEL -> "Storage run: " + visited.size() + "/" + total + " -> " + target[0] + ", " + target[1] + ", " + target[2];
         case AT_STORAGE -> "Storage run: " + (visited.size() + 1) + "/" + total + " open it, then close it (or press Next)";
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

      AutoExplore.setEnabled(false);
      if (!Settings.baseFinder) {
         BaseFinder.toggle();
      }

      BaritoneBridge.execute("set allowBreak true");
      BaritoneBridge.execute("set allowInventory true");
      BaritoneBridge.execute("set autoTool true");
      BaritoneBridge.execute("set blocksToAvoidBreaking " + avoidList());
      base = b;
      visited.clear();
      target = null;
      total = 0;
      BaseFinder.message(Component.literal("Storage run started at " + b.x + ", " + b.y + ", " + b.z + ". Baritone will dig in with your pickaxe.").withStyle(ChatFormatting.GREEN));
      if (!nextTarget()) {
         // Chunks around the base aren't scanned yet (too far away): walk there first.
         phase = Phase.APPROACH;
         phaseTicks = 0;
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
      List<String> names = new ArrayList<>(List.of("crafting_table", "furnace", "chest", "trapped_chest", "barrel", "ender_chest", "shulker_box"));

      for (String color : new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
      }) {
         names.add(color + "_shulker_box");
      }

      return String.join(",", names);
   }

   private static long key(int[] p) {
      return ((long)p[0] & 67108863L) << 38 | ((long)p[1] & 4095L) << 26 | (long)p[2] & 67108863L;
   }

   private static void markVisited() {
      if (target != null) {
         visited.add(key(target));
      }
   }

   /** Picks the nearest unvisited container and sends Baritone to stand on top of it. */
   private static boolean nextTarget() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || base == null) {
         return false;
      }

      List<int[]> storage = BaseFinder.storageNear(base.x, base.z, SEARCH_RADIUS);
      // A double chest is two blocks: only visit one half.
      List<int[]> todo = new ArrayList<>();

      for (int[] p : storage) {
         if (visited.contains(key(p))) {
            continue;
         }

         boolean twin = false;

         for (int[] q : todo) {
            if (q[1] == p[1] && Math.abs(q[0] - p[0]) + Math.abs(q[2] - p[2]) == 1) {
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

      int[] best = null;
      double bestD = Double.MAX_VALUE;

      for (int[] p : todo) {
         double dx = player.getX() - (p[0] + 0.5);
         double dy = player.getY() - p[1];
         double dz = player.getZ() - (p[2] + 0.5);
         double d = dx * dx + dy * dy * 4.0 + dz * dz;
         if (d < bestD) {
            bestD = d;
            best = p;
         }
      }

      target = best;
      phase = Phase.TRAVEL;
      phaseTicks = 0;
      idleChecks = 0;
      openedHere = false;
      // Standing on top of the container: Baritone breaks the blocks in the way but never the target itself.
      BaritoneBridge.execute("goto " + best[0] + " " + (best[1] + 1) + " " + best[2]);
      return true;
   }

   private static void finish() {
      phase = Phase.IDLE;
      target = null;
      BaseFinder.message(Component.literal("Storage run done: visited " + visited.size() + " containers.").withStyle(ChatFormatting.GREEN));
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
               if (nextTarget()) {
                  return;
               }

               if (phaseTicks > APPROACH_TIMEOUT_TICKS && Boolean.FALSE.equals(BaritoneBridge.isProcessActive("getCustomGoalProcess"))) {
                  BaseFinder.message(Component.literal("Reached the base area but found no chests, barrels or shulker boxes.").withStyle(ChatFormatting.GOLD));
                  phase = Phase.IDLE;
               }
            }
            break;
         case TRAVEL:
            double dx = player.getX() - (target[0] + 0.5);
            double dz = player.getZ() - (target[2] + 0.5);
            double dy = player.getY() - (target[1] + 1);
            if (dx * dx + dz * dz <= 2.25 && Math.abs(dy) <= 1.5) {
               phase = Phase.AT_STORAGE;
               phaseTicks = 0;
               BaseFinder.message(
                  Component.literal("At container " + (visited.size() + 1) + "/" + total + " (" + target[0] + ", " + target[1] + ", " + target[2] + "). Open it, close it and I'll go to the next one.")
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
                  BaseFinder.message(Component.literal("Couldn't reach " + target[0] + ", " + target[1] + ", " + target[2] + ", skipping it.").withStyle(ChatFormatting.GOLD));
                  next();
               }
            }
            break;
         case AT_STORAGE:
            boolean open = player.containerMenu != player.inventoryMenu;
            if (open) {
               openedHere = true;
            } else if (openedHere) {
               next();
            }
            break;
         default:
            break;
      }
   }
}
