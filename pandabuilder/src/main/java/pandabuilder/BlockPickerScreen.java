package pandabuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Pick which blocks (out of every block in the game) the Storage Run walks to. */
public class BlockPickerScreen extends Screen {
   private static final int ROW = 18;
   private static final int LIST_TOP = 56;
   private final Screen parent;
   private final List<Entry> all = new ArrayList<>();
   private List<Entry> shown = new ArrayList<>();
   private EditBox search;
   private boolean onlyPicked;
   private boolean changed;
   private String notice;
   private int scroll;

   private record Entry(Block block, String id, String name, String searchText) {
   }

   public BlockPickerScreen(Screen parent) {
      super(Component.literal("Run Targets"));
      this.parent = parent;

      for (Block block : BuiltInRegistries.BLOCK) {
         Identifier key = BuiltInRegistries.BLOCK.getKey(block);
         if (key == null || key.getPath().equals("air") || key.getPath().equals("cave_air") || key.getPath().equals("void_air")) {
            continue;
         }

         String id = key.toString();
         String name = block.getName().getString();
         this.all.add(new Entry(block, id, name, (name + " " + id).toLowerCase(Locale.ROOT)));
      }

      this.all.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
   }

   protected void init() {
      String query = this.search != null ? this.search.getValue() : "";
      this.search = new EditBox(this.font, 10, 22, Math.max(80, this.width - 20 - 3 * 94), 18, Component.literal("Search"));
      this.search.setMaxLength(64);
      this.search.setHint(Component.literal("Search blocks..."));
      this.search.setValue(query);
      this.search.setResponder(text -> this.refilter());
      this.addRenderableWidget(this.search);
      int x = this.width - 10 - 3 * 94 + 4;
      this.addRenderableWidget(Button.builder(this.filterLabel(), b -> {
         this.onlyPicked = !this.onlyPicked;
         b.setMessage(this.filterLabel());
         this.refilter();
      }).bounds(x, 21, 90, 20).build());
      x += 94;
      this.addRenderableWidget(Button.builder(Component.literal("Pick Shown"), b -> {
         // Without a search this would pick every block in the game (stone, dirt...).
         if (this.search.getValue().isBlank() && !this.onlyPicked) {
            this.notice = "Type a search first (e.g. \"ore\"), Pick Shown picks everything in the list.";
            return;
         }

         this.notice = null;
         for (Entry e : this.shown) {
            Settings.runTargets.add(e.id);
         }

         this.changed = true;
      }).bounds(x, 21, 90, 20).build());
      x += 94;
      this.addRenderableWidget(Button.builder(Component.literal("Unpick Shown"), b -> {
         for (Entry e : this.shown) {
            Settings.runTargets.remove(e.id);
         }

         this.changed = true;
         this.refilter();
      }).bounds(x, 21, 90, 20).build());
      int y = this.height - 26;
      this.addRenderableWidget(Button.builder(Component.literal("Reset to Default"), b -> {
         Settings.runTargets.clear();
         Settings.runTargets.addAll(Settings.defaultRunTargets());
         this.changed = true;
         this.refilter();
      }).bounds(10, y, 110, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose()).bounds(this.width - 90, y, 80, 20).build());
      this.setInitialFocus(this.search);
      this.refilter();
   }

   private Component filterLabel() {
      return Component.literal(this.onlyPicked ? "Show: Picked" : "Show: All");
   }

   private void refilter() {
      String q = this.search == null ? "" : this.search.getValue().trim().toLowerCase(Locale.ROOT);
      List<Entry> list = new ArrayList<>();

      for (Entry e : this.all) {
         if ((q.isEmpty() || e.searchText.contains(q)) && (!this.onlyPicked || Settings.runTargets.contains(e.id))) {
            list.add(e);
         }
      }

      this.shown = list;
      this.scroll = 0;
   }

   private int listBottom() {
      return this.height - 34;
   }

   private int visibleRows() {
      return (this.listBottom() - LIST_TOP - 4) / ROW;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(graphics, mouseX, mouseY, delta);
      String title = "Run Targets: blocks the Storage Run goes to";
      graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, 6, -1, true);
      int left = 10;
      int right = this.width - 10;
      String header = Settings.runTargets.size() + " picked | " + this.shown.size() + " shown | click a block to pick or unpick it";
      graphics.text(this.font, this.font.plainSubstrByWidth(this.notice != null ? this.notice : header, right - left), left, 45, this.notice != null ? -43691 : -171, true);
      graphics.fill(left, LIST_TOP, right, this.listBottom(), -2013265920);
      int visible = this.visibleRows();
      this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, this.shown.size() - visible)));

      for (int i = 0; i < visible && i + this.scroll < this.shown.size(); i++) {
         Entry e = this.shown.get(i + this.scroll);
         int y = LIST_TOP + 2 + i * ROW;
         boolean picked = Settings.runTargets.contains(e.id);
         boolean hovered = mouseX >= left && mouseX < right && mouseY >= y && mouseY < y + ROW;
         if (picked) {
            graphics.fill(left + 1, y, right - 1, y + ROW, 1429514804);
         } else if (hovered) {
            graphics.fill(left + 1, y, right - 1, y + ROW, 1157627903);
         }

         graphics.text(this.font, picked ? "§a[x]" : "§7[ ]", left + 4, y + 5, -1, true);
         graphics.item(new ItemStack(e.block.asItem()), left + 24, y + 1);
         graphics.text(this.font, e.name, left + 44, y + 5, -1, true);
         graphics.text(this.font, e.id, right - this.font.width(e.id) - 6, y + 5, -8355712, true);
      }
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      }

      double mouseX = event.x();
      double mouseY = event.y();
      if (mouseX >= 10.0 && mouseX < this.width - 10 && mouseY >= LIST_TOP + 2 && mouseY < this.listBottom()) {
         int index = (int)((mouseY - LIST_TOP - 2.0) / ROW) + this.scroll;
         if (index >= 0 && index < this.shown.size() && index - this.scroll < this.visibleRows()) {
            String id = this.shown.get(index).id;
            if (!Settings.runTargets.remove(id)) {
               Settings.runTargets.add(id);
            }

            this.changed = true;
            return true;
         }
      }

      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll = Math.max(0, this.scroll + (verticalAmount > 0.0 ? -3 : 3));
      return true;
   }

   public void onClose() {
      if (this.changed) {
         Settings.save();
         BaseFinder.targetsChanged();
      }

      this.minecraft.gui.setScreen(this.parent);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
