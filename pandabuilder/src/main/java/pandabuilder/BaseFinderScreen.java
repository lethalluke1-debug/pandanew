package pandabuilder;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class BaseFinderScreen extends Screen {
   private static final int ROW = 22;
   private static final int LIST_TOP = 56;
   private final Screen parent;
   private int scroll;

   public BaseFinderScreen(Screen parent) {
      super(Component.literal("Base Finder"));
      this.parent = parent;
   }

   protected void init() {
      int tx = 10;
      int tw = Math.min(130, (this.width - 20 - 20) / 6);
      this.addRenderableWidget(Button.builder(toggleLabel("Base Finder", Settings.baseFinder), b -> {
         BaseFinder.toggle();
         b.setMessage(toggleLabel("Base Finder", Settings.baseFinder));
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("Auto Explore", AutoExplore.isRunning()), b -> {
         AutoExplore.setEnabled(!AutoExplore.isRunning());
         this.rebuildWidgets();
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("Stop On Find", Settings.stopOnFind), b -> {
         Settings.stopOnFind = !Settings.stopOnFind;
         Settings.save();
         b.setMessage(toggleLabel("Stop On Find", Settings.stopOnFind));
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(sensitivityLabel(), b -> {
         Settings.baseSensitivity = (Settings.baseSensitivity + 1) % 3;
         Settings.save();
         BaseFinder.rescan();
         b.setMessage(sensitivityLabel());
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(clickLabel(), b -> {
         Settings.clickStorageRun = !Settings.clickStorageRun;
         Settings.save();
         b.setMessage(clickLabel());
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("ESP", Settings.esp), b -> {
         Esp.toggle();
         this.rebuildWidgets();
      }).bounds(tx, 20, tw, 20).build());
      int y = this.height - 26;
      int w = 80;
      int x = 10;
      this.addRenderableWidget(Button.builder(Component.literal("Rescan"), b -> BaseFinder.rescan()).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Clear List"), b -> {
         BaseFinder.clearBases();
         this.scroll = 0;
      }).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Stop Baritone"), b -> {
         AutoExplore.setEnabled(false);
         StorageRun.stop(false);
         BaritoneBridge.execute("cancel");
      }).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Run Targets..."), b -> this.minecraft.gui.setScreen(new BlockPickerScreen(this))).bounds(x, y, w + 10, 20).build());
      x += w + 14;
      this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.minecraft.gui.setScreen(this.parent)).bounds(x, y, w, 20).build());
   }

   private static Component toggleLabel(String name, boolean on) {
      return Component.literal(name + ": " + (on ? "§aON" : "§cOFF"));
   }

   private static Component clickLabel() {
      return Component.literal("Click: " + (Settings.clickStorageRun ? "Storage Run" : "Walk There"));
   }

   private static Component sensitivityLabel() {
      return Component.literal("Sensitivity: " + BaseFinder.SENSITIVITY_NAMES[Math.max(0, Math.min(2, Settings.baseSensitivity))]);
   }

   private int listBottom() {
      return this.height - 34;
   }

   private List<BaseFinder.Base> sortedBases() {
      List<BaseFinder.Base> list = BaseFinder.basesInThisWorld();
      LocalPlayer player = Minecraft.getInstance().player;
      String dim = BaseFinder.currentDimension();
      // Bases in this dimension first, nearest first; other dimensions after, best score first.
      list.sort((a, b) -> {
         boolean ha = a.dimension.equals(dim);
         boolean hb = b.dimension.equals(dim);
         if (ha != hb) {
            return ha ? -1 : 1;
         } else if (ha && player != null) {
            return Double.compare(BaseFinder.distanceSq(player, a.x, a.z), BaseFinder.distanceSq(player, b.x, b.z));
         } else {
            return Integer.compare(b.score, a.score);
         }
      });
      return list;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(graphics, mouseX, mouseY, delta);
      String title = "Base Finder";
      graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, 6, -1, true);
      String status = BaritoneBridge.statusText();
      graphics.text(this.font, status, this.width - this.font.width(status) - 8, 6, BaritoneBridge.isInstalled() ? -11141291 : -43691, true);
      int left = 10;
      int right = this.width - 10;
      List<BaseFinder.Base> list = this.sortedBases();
      String header = Settings.baseFinder
         ? "Found " + list.size() + " | scanned " + BaseFinder.scannedChunks() + " chunks, " + BaseFinder.queuedChunks() + " queued | click a base: " + (Settings.clickStorageRun ? "storage run" : "walk there")
         : "Base Finder is OFF. Turn it on to scan chunks as they load.";
      graphics.text(this.font, this.font.plainSubstrByWidth(header, right - left), left, 45, Settings.baseFinder ? -171 : -22016, true);
      graphics.fill(left, LIST_TOP, right, this.listBottom(), -2013265920);
      if (list.isEmpty()) {
         graphics.text(this.font, "No bases found yet. Explore (or turn on Auto Explore) to load new chunks.", left + 4, LIST_TOP + 4, -5592406, true);
         graphics.text(this.font, "High sensitivity finds small stashes, Low only big bases.", left + 4, LIST_TOP + 16, -5592406, true);
         return;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      String dim = BaseFinder.currentDimension();
      int visible = (this.listBottom() - LIST_TOP - 4) / ROW;
      this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, list.size() - visible)));

      for (int i = 0; i < visible && i + this.scroll < list.size(); i++) {
         BaseFinder.Base b = list.get(i + this.scroll);
         int y = LIST_TOP + 3 + i * ROW;
         boolean hovered = mouseX >= left && mouseX < right && mouseY >= y - 1 && mouseY < y + ROW - 1;
         if (hovered) {
            graphics.fill(left + 1, y - 2, right - 1, y + ROW - 3, 1157627903);
         }

         boolean here = b.dimension.equals(dim);
         String where = here && player != null ? (int)Math.sqrt(BaseFinder.distanceSq(player, b.x, b.z)) + "m" : b.dimension;
         String line1 = "Score " + b.score + "   " + b.x + ", " + b.y + ", " + b.z + "   (" + where + ")";
         graphics.text(this.font, line1, left + 4, y, here ? -11141291 : -5592406, true);
         graphics.text(this.font, this.font.plainSubstrByWidth(b.summary, right - left - 12), left + 10, y + 10, -1, true);
      }
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      }

      double mouseX = event.x();
      double mouseY = event.y();
      if (mouseX >= 10.0 && mouseX < this.width - 10 && mouseY >= LIST_TOP && mouseY < this.listBottom()) {
         int index = (int)((mouseY - LIST_TOP - 2.0) / ROW) + this.scroll;
         List<BaseFinder.Base> list = this.sortedBases();
         if (index >= 0 && index < list.size()) {
            goTo(list.get(index));
            return true;
         }
      }

      return false;
   }

   private void goTo(BaseFinder.Base b) {
      if (!b.dimension.equals(BaseFinder.currentDimension())) {
         BaseFinder.message(Component.literal("That base is in " + b.dimension + ", go there first.").withStyle(ChatFormatting.GOLD));
         return;
      }

      if (!BaritoneBridge.isInstalled()) {
         BaseFinder.message(Component.literal("Base is at " + b.x + ", " + b.y + ", " + b.z + ". Install Baritone to walk there automatically.").withStyle(ChatFormatting.GOLD));
         return;
      }

      if (Settings.clickStorageRun) {
         StorageRun.start(b);
         this.onClose();
         return;
      }

      AutoExplore.setEnabled(false);
      StorageRun.stop(false);
      if (BaritoneBridge.execute("goto " + b.x + " " + b.y + " " + b.z)) {
         BaseFinder.message(Component.literal("Walking to base at " + b.x + ", " + b.y + ", " + b.z).withStyle(ChatFormatting.GREEN));
         this.onClose();
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll = Math.max(0, this.scroll + (verticalAmount > 0.0 ? -1 : 1));
      return true;
   }

   public boolean isPauseScreen() {
      return false;
   }
}
