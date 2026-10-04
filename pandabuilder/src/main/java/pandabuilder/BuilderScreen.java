package pandabuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BuilderScreen extends Screen {
   private static final int FILE_ROW = 12;
   private static final int MAT_ROW = 18;
   private static final int LIST_TOP = 56;
   private List<Path> files = new ArrayList<>();
   private int fileScroll;
   private int matScroll;
   private String error;

   public BuilderScreen() {
      super(Component.literal("Panda Builder"));
   }

   protected void init() {
      this.files = BuildManager.listSchematics();
      int tx = 10;
      int tw = Math.min(130, (this.width - 20 - 12) / 4);
      this.addRenderableWidget(Button.builder(toggleLabel("Schematic Builder", Settings.builder), b -> {
         Settings.builder = !Settings.builder;
         if (!Settings.builder && BuildManager.isBuilding()) {
            BuildManager.cancel();
         }

         Settings.save();
         b.setMessage(toggleLabel("Schematic Builder", Settings.builder));
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("Creative Refill", Settings.creativeRefill), b -> {
         Settings.creativeRefill = !Settings.creativeRefill;
         Settings.save();
         b.setMessage(toggleLabel("Creative Refill", Settings.creativeRefill));
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("Checklist HUD", Settings.checklistHud), b -> {
         Settings.checklistHud = !Settings.checklistHud;
         Settings.save();
         b.setMessage(toggleLabel("Checklist HUD", Settings.checklistHud));
      }).bounds(tx, 20, tw, 20).build());
      tx += tw + 4;
      this.addRenderableWidget(Button.builder(toggleLabel("Auto Totem", Settings.autoTotem), b -> {
         AutoTotem.toggle();
         b.setMessage(toggleLabel("Auto Totem", Settings.autoTotem));
      }).bounds(tx, 20, tw, 20).build());
      int y = this.height - 26;
      int w = 70;
      int x = 10;
      this.addRenderableWidget(Button.builder(Component.literal("Build Here"), b -> {
         BuildManager.startBuild();
         this.onClose();
      }).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Pause"), b -> BuildManager.pause()).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Resume"), b -> BuildManager.resume()).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> BuildManager.cancel()).bounds(x, y, w, 20).build());
      x += w + 4;
      this.addRenderableWidget(
         Button.builder(Component.literal("Refresh"), b -> this.files = BuildManager.listSchematics()).bounds(x, y, w, 20).build()
      );
      x += w + 4;
      this.addRenderableWidget(
         Button.builder(Component.literal("Open Folder"), b -> Util.getPlatform().openPath(BuildManager.schematicsDir()))
            .bounds(x, y, w + 10, 20)
            .build()
      );
   }

   private static Component toggleLabel(String name, boolean on) {
      return Component.literal(name + ": " + (on ? "§aON" : "§cOFF"));
   }

   private int fileListRight() {
      return Math.min(180, this.width / 3);
   }

   private int listBottom() {
      return this.height - 34;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(graphics, mouseX, mouseY, delta);
      String title = "Panda Builder";
      graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, 6, -1, true);
      String baritone = BaritoneBridge.isInstalled() ? "Baritone: found" : "Baritone: NOT installed";
      graphics.text(this.font, baritone, this.width - this.font.width(baritone) - 8, 6, BaritoneBridge.isInstalled() ? -11141291 : -43691, true);
      this.extractFileList(graphics, mouseX, mouseY);
      this.extractMaterials(graphics);
   }

   private void extractFileList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
      int left = 10;
      int right = this.fileListRight();
      graphics.fill(left, 56, right, this.listBottom(), -2013265920);
      graphics.text(this.font, "Schematics", left + 4, 45, -171, true);
      if (this.files.isEmpty()) {
         graphics.text(this.font, "No files in", left + 4, 60, -5592406, true);
         graphics.text(this.font, ".minecraft/schematics", left + 4, 72, -5592406, true);
      } else {
         Schematic selected = BuildManager.selected();
         int visible = (this.listBottom() - 56 - 4) / 12;

         for (int i = 0; i < visible && i + this.fileScroll < this.files.size(); i++) {
            Path file = this.files.get(i + this.fileScroll);
            int y = 59 + i * 12;
            boolean isSelected = selected != null && selected.path.equals(file);
            boolean hovered = mouseX >= left && mouseX < right && mouseY >= y - 1 && mouseY < y + 12 - 1;
            if (isSelected) {
               graphics.fill(left + 1, y - 2, right - 1, y + 12 - 2, -14522710);
            } else if (hovered) {
               graphics.fill(left + 1, y - 2, right - 1, y + 12 - 2, 1157627903);
            }

            String name = this.font.plainSubstrByWidth(file.getFileName().toString(), right - left - 8);
            graphics.text(this.font, name, left + 4, y, -1, true);
         }
      }
   }

   private void extractMaterials(GuiGraphicsExtractor graphics) {
      int left = this.fileListRight() + 10;
      int right = this.width - 10;
      graphics.fill(left, 56, right, this.listBottom(), -2013265920);
      Schematic selected = BuildManager.selected();
      if (this.error != null) {
         graphics.text(this.font, this.error, left + 4, 60, -43691, true);
      } else if (selected == null) {
         graphics.text(this.font, "Pick a schematic on the left", left + 4, 60, -5592406, true);
      } else if (!selected.materialsKnown) {
         graphics.text(this.font, "Material list not available for old .schematic files.", left + 4, 60, -5592406, true);
         graphics.text(this.font, "You can still press Build Here.", left + 4, 72, -5592406, true);
      } else {
         Map<Item, Integer> have = BuildManager.inventoryCounts();
         int missingTypes = 0;
         int total = 0;

         for (Entry<Item, Integer> e : selected.materials.entrySet()) {
            total += e.getValue();
            if (have.getOrDefault(e.getKey(), 0) < e.getValue()) {
               missingTypes++;
            }
         }

         String header = "Materials: "
            + total
            + " blocks, "
            + selected.materials.size()
            + " types, "
            + (missingTypes == 0 ? "you have everything" : missingTypes + " types short");
         graphics.text(this.font, header, left, 45, missingTypes == 0 ? -11141291 : -22016, true);
         List<Entry<Item, Integer>> entries = new ArrayList<>(selected.materials.entrySet());
         int visible = (this.listBottom() - 56 - 4) / 18;
         this.matScroll = Math.max(0, Math.min(this.matScroll, entries.size() - visible));

         for (int i = 0; i < visible && i + this.matScroll < entries.size(); i++) {
            Entry<Item, Integer> ex = entries.get(i + this.matScroll);
            int y = 59 + i * 18;
            int need = ex.getValue();
            int got = have.getOrDefault(ex.getKey(), 0);
            graphics.item(new ItemStack((ItemLike)ex.getKey()), left + 4, y);
            graphics.text(this.font, BuildManager.itemName(ex.getKey()), left + 24, y + 4, -1, true);
            String count = got + " / " + need;
            graphics.text(this.font, count, right - this.font.width(count) - 6, y + 4, got >= need ? -11141291 : -43691, true);
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      } else {
         double mouseX = event.x();
         double mouseY = event.y();
         if (mouseX >= 10.0 && mouseX < this.fileListRight() && mouseY >= 56.0 && mouseY < this.listBottom()) {
            int index = (int)((mouseY - 56.0 - 1.0) / 12.0) + this.fileScroll;
            if (index >= 0 && index < this.files.size()) {
               try {
                  BuildManager.select(this.files.get(index));
                  this.error = null;
                  this.matScroll = 0;
               } catch (Exception var9) {
                  this.error = "Couldn't read that file: " + var9.getMessage();
                  PandaBuilderClient.LOGGER.warn("Failed to load schematic", var9);
               }

               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      int step = verticalAmount > 0.0 ? -1 : 1;
      if (mouseX < this.fileListRight()) {
         int visible = (this.listBottom() - 56 - 4) / 12;
         this.fileScroll = Math.max(0, Math.min(this.fileScroll + step, Math.max(0, this.files.size() - visible)));
      } else {
         this.matScroll = Math.max(0, this.matScroll + step);
      }

      return true;
   }

   public boolean isPauseScreen() {
      return false;
   }
}
