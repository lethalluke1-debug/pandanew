package pandabuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Has Baritone wander into unexplored chunks so the Base Finder can scan them. */
public final class AutoExplore {
   private static boolean running;
   private static int ticks;
   private static int startTick;

   private AutoExplore() {
   }

   public static boolean isRunning() {
      return running;
   }

   public static void setEnabled(boolean on) {
      if (on) {
         if (!BaritoneBridge.isInstalled()) {
            BaseFinder.message(Component.literal("Auto Explore needs Baritone. Put the Baritone fabric jar for 26.2 in your mods folder.").withStyle(ChatFormatting.RED));
            return;
         }

         if (BuildManager.isBuilding()) {
            BaseFinder.message(Component.literal("Cancel the current build before exploring.").withStyle(ChatFormatting.RED));
            return;
         }

         if (!Settings.baseFinder) {
            BaseFinder.toggle();
         }

         StorageRun.stop(false);
         if (BaritoneBridge.execute("explore")) {
            running = true;
            startTick = ticks;
            BaseFinder.message(Component.literal("Auto Explore started. Bases will be logged as chunks load.").withStyle(ChatFormatting.GREEN));
         } else {
            BaseFinder.message(Component.literal("Baritone refused the explore command, check chat for its error.").withStyle(ChatFormatting.RED));
         }
      } else {
         if (running) {
            BaritoneBridge.execute("cancel");
         }

         running = false;
      }
   }

   public static void stopForBuild() {
      running = false;
   }

   public static void tick() {
      ticks++;
      // Notice when exploring was stopped from Baritone itself (#stop, death, disconnect...).
      if (running && ticks % 40 == 0 && ticks - startTick > 100 && Boolean.FALSE.equals(BaritoneBridge.isProcessActive("getExploreProcess"))) {
         running = false;
         BaseFinder.message(Component.literal("Auto Explore stopped.").withStyle(ChatFormatting.GOLD));
      }
   }

   static void onBaseFound() {
      if (running && Settings.stopOnFind) {
         setEnabled(false);
         BaseFinder.message(Component.literal("Stopped exploring because a base was found. Open the Base Finder to go there.").withStyle(ChatFormatting.GOLD));
      }
   }
}
