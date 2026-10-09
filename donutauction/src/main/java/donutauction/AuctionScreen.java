package donutauction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AuctionScreen extends Screen {
   private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");
   private static final int SIDEBAR_W = 100;
   private static final int CELL = 18;
   private static final int RIGHT_W = 128;
   private static donutauction.AuctionScreen.Page page = donutauction.AuctionScreen.Page.NEW;
   private static String search = "";
   private static String selectedItem;
   private static String fMin = "";
   private static String fQty = "1";
   private static String fTimer = "60";
   private static String fWorth = "";
   private static boolean worthEdited;
   private static int gridScroll;
   private static int listScroll;
   private int x0;
   private int y0;
   private int w;
   private int h;
   private EditBox searchBox;
   private EditBox minBox;
   private EditBox qtyBox;
   private EditBox timerBox;
   private EditBox worthBox;
   private EditBox apiBox;
   private EditBox ruleBox;
   private boolean settingWorth;
   private List<Item> gridItems = List.of();
   private String gridQuery;
   private String status = "";
   private long statusAt;
   private static final int CARD_H = 50;

   public AuctionScreen() {
      super(Component.literal("Donut Auction"));
   }

   private int mainX() {
      return this.x0 + 100 + 6;
   }

   private int mainW() {
      return this.x0 + this.w - 6 - this.mainX();
   }

   private int top() {
      return this.y0 + 32;
   }

   private int bottom() {
      return this.y0 + this.h - 6;
   }

   private int gridW() {
      return this.mainW() - 128 - 6;
   }

   private int rightX() {
      return this.mainX() + this.mainW() - 128;
   }

   private int gridTop() {
      return this.top() + 20;
   }

   private int cols() {
      return Math.max(1, (this.gridW() - 6) / 18);
   }

   private int visibleRows() {
      return Math.max(1, (this.bottom() - this.gridTop() - 4) / 18);
   }

   private int navY(int var1) {
      return this.y0 + 62 + var1 * 16 + (var1 >= 3 ? 14 : 0);
   }

   protected void init() {
      this.w = Math.clamp((long)(this.width - 20), 360, 480);
      this.h = Math.clamp((long)(this.height - 20), 220, 280);
      this.x0 = (this.width - this.w) / 2;
      this.y0 = (this.height - this.h) / 2;
      this.searchBox = this.minBox = this.qtyBox = this.timerBox = this.worthBox = this.apiBox = this.ruleBox = null;
      switch (page) {
         case NEW:
            this.searchBox = this.field(this.mainX() + 14, this.top() + 3, this.gridW() - 20, search, "Search items...", 40, var0 -> {
               search = var0;
               gridScroll = 0;
            });
            int var3 = this.rightX();
            int var4 = this.top() + 50;
            this.minBox = this.field(var3 + 4, var4 + 2, 120, fMin, "none", 16, var0 -> fMin = var0);
            this.qtyBox = this.field(var3 + 4, var4 + 28, 54, fQty, "1", 4, var0 -> fQty = var0);
            this.timerBox = this.field(var3 + 64 + 4, var4 + 28, 56, fTimer, "60", 4, var0 -> fTimer = var0);
            this.worthBox = this.field(var3 + 4, var4 + 54, 120, fWorth, "auto", 16, var1x -> {
               fWorth = var1x;
               if (!this.settingWorth) {
                  worthEdited = true;
               }
            });
            break;
         case SETTINGS:
            donutauction.Config var1 = donutauction.Config.get();
            int var2 = this.mainX() + 6;
            this.apiBox = this.field(var2 + 4, this.settingsFieldY(0) + 2, this.mainW() - 20, var1.apiKey, "Run /api in game, paste the key", 200, var1x -> {
               var1.apiKey = var1x.trim();
               donutauction.WorthService.clear();
               donutauction.Config.save();
            });
            this.ruleBox = this.field(var2 + 4, this.settingsFieldY(1) + 2, this.mainW() - 20, var1.ruleText, "Shown on the HUD", 60, var1x -> {
               var1.ruleText = var1x;
               donutauction.Config.save();
            });
      }
   }

   private EditBox field(int var1, int var2, int var3, String var4, String var5, int var6, Consumer<String> var7) {
      EditBox var8 = new EditBox(this.font, var1, var2, var3, 10, Component.empty());
      var8.setBordered(false);
      var8.setMaxLength(var6);
      var8.setTextColor(-723720);
      var8.setValue(var4);
      var8.setHint(Component.literal(var5).withColor(-10526344));
      var8.setResponder(var7);
      this.addRenderableWidget(var8);
      return var8;
   }

   private void setPage(donutauction.AuctionScreen.Page var1) {
      page = var1;
      listScroll = 0;
      this.rebuildWidgets();
   }

   private void flash(String var1) {
      this.status = var1;
      this.statusAt = System.currentTimeMillis();
   }

   public void extractBackground(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
      donutauction.Config var5 = donutauction.Config.get();
      if (this.minecraft.level == null) {
         this.extractPanorama(var1, var4);
      }

      if (var5.frostedBlur) {
         this.extractBlurredBackground(var1);
      }

      if (var5.seeThroughGui) {
         var1.fill(0, 0, this.width, this.height, 0x40000000);
      } else {
         var1.fill(0, 0, this.width, this.height, -1072689136);
      }

      if (var5.ambientBackground) {
         int var6 = donutauction.Ui.accent();
         var1.fillGradient(0, 0, this.width, this.height / 2, donutauction.Ui.alpha(var6, 72), donutauction.Ui.alpha(var6, 0));
         var1.fillGradient(0, this.height / 2, this.width, this.height, 0, donutauction.Ui.alpha(darker(var6), 96));
      }

      this.minecraft.gui.hud.extractDeferredSubtitles();
   }

   private static int darker(int var0) {
      int var1 = (var0 >> 16 & 0xFF) / 2;
      int var2 = (var0 >> 8 & 0xFF) / 2;
      int var3 = (var0 & 0xFF) / 2;
      return 0xFF000000 | var1 << 16 | var2 << 8 | var3;
   }

   public void extractRenderState(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
      donutauction.Ui.round(var1, this.x0 - 1, this.y0 - 1, this.w + 2, this.h + 2, 8, donutauction.Ui.alpha(donutauction.Ui.accent(), 85));
      donutauction.Ui.round(var1, this.x0, this.y0, this.w, this.h, 7, donutauction.Ui.windowBg());
      this.drawSidebar(var1, var2, var3);
      this.drawHeader(var1);
      switch (page) {
         case NEW:
            this.drawNew(var1, var2, var3);
            break;
         case PRESETS:
            this.drawPresets(var1, var2, var3);
            break;
         case HISTORY:
            this.drawHistory(var1, var2, var3);
            break;
         case SETTINGS:
            this.drawSettings(var1, var2, var3);
            break;
         case THEME:
            this.drawTheme(var1, var2, var3);
      }

      super.extractRenderState(var1, var2, var3, var4);
      if (!this.status.isEmpty() && System.currentTimeMillis() - this.statusAt < 3000L) {
         int var5 = donutauction.Ui.width(this.font, this.status) + 12;
         donutauction.Ui.box(
            var1, this.x0 + (this.w - var5) / 2, this.y0 + this.h - 18, var5, 12, 5, -15132122, donutauction.Ui.alpha(donutauction.Ui.accent(), 160)
         );
         donutauction.Ui.text(var1, this.font, this.status, this.x0 + (this.w - var5) / 2.0F + 6.0F, this.y0 + this.h - 15, -723720);
      }
   }

   private void drawSidebar(GuiGraphicsExtractor var1, int var2, int var3) {
      int var4 = this.x0 + 5;
      int var5 = this.y0 + 5;
      byte var6 = 96;
      int var7 = this.h - 10;
      donutauction.Ui.round(var1, var4, var5, var6, var7, 6, donutauction.Ui.panelBg());
      donutauction.Ui.text(var1, this.font, Component.literal("DONUT").withStyle(ChatFormatting.BOLD), var4 + 9, var5 + 8, -723720, 0.85F);
      donutauction.Ui.text(var1, this.font, Component.literal("AUCTION").withStyle(ChatFormatting.BOLD), var4 + 9, var5 + 17, donutauction.Ui.accent(), 0.85F);
      donutauction.Ui.text(var1, this.font, Component.literal("by Lethal"), var4 + 9, var5 + 26, -10526344, 0.6F);
      var1.fill(var4 + 7, var5 + 38, var4 + var6 - 7, var5 + 39, -14013637);
      donutauction.Ui.text(var1, this.font, Component.literal("AUCTION"), var4 + 7, var5 + 44, -10526344, 0.6F);
      donutauction.Ui.text(var1, this.font, Component.literal("GENERAL"), var4 + 7, this.navY(3) - 10, -10526344, 0.6F);
      donutauction.AuctionScreen.Page[] var8 = donutauction.AuctionScreen.Page.values();

      for (int var9 = 0; var9 < var8.length; var9++) {
         int var10 = this.navY(var9);
         boolean var11 = var8[var9] == page;
         boolean var12 = donutauction.Ui.inside(var2, var3, var4 + 3, var10, var6 - 6, 14);
         if (var11) {
            donutauction.Ui.round(var1, var4 + 3, var10, var6 - 6, 14, 4, donutauction.Ui.alpha(donutauction.Ui.accent(), 56));
            var1.fill(var4, var10 + 3, var4 + 2, var10 + 11, donutauction.Ui.accent());
         } else if (var12) {
            donutauction.Ui.round(var1, var4 + 3, var10, var6 - 6, 14, 4, -14671569);
         }

         int var13 = var11 ? donutauction.Ui.accent() : (var12 ? -723720 : -6513229);
         donutauction.Ui.text(var1, this.font, var8[var9].icon, var4 + 9, var10 + 3.5F, var13);
         donutauction.Ui.text(var1, this.font, var8[var9].title, var4 + 21, var10 + 3.5F, var11 ? -723720 : var13);
      }

      int var14 = var5 + var7 - 28;
      donutauction.Ui.round(var1, var4 + 3, var14, var6 - 6, 24, 5, -15132122);
      if (this.minecraft.player != null) {
         PlayerFaceExtractor.extractRenderState(var1, this.minecraft.player.getSkin(), var4 + 8, var14 + 4, 16);
      }

      donutauction.Ui.text(var1, this.font, donutauction.Ui.ellipsize(this.font, this.minecraft.getUser().getName(), var6 - 40), var4 + 28, var14 + 5, -723720);
      donutauction.Ui.text(
         var1,
         this.font,
         donutauction.Auction.running() ? "Auction live" : "Seller",
         var4 + 28,
         var14 + 14,
         donutauction.Auction.running() ? donutauction.Ui.accent() : -10526344
      );
      donutauction.Ui.round(var1, var4 + var6 - 13, var14 + 9, 5, 5, 2, donutauction.Auction.running() ? donutauction.Ui.accent() : -11870592);
   }

   private void drawHeader(GuiGraphicsExtractor var1) {
      int var2 = this.mainX();
      int var3 = this.y0 + 6;
      int var4 = this.mainW();
      donutauction.Ui.round(var1, var2, var3, var4, 20, 5, donutauction.Ui.panelBg());
      String var5 = this.minecraft.getUser().getName();
      donutauction.Ui.text(var1, this.font, "Hello, ", var2 + 8, var3 + 7, -6513229);
      donutauction.Ui.bold(var1, this.font, var5, var2 + 8 + donutauction.Ui.width(this.font, "Hello, "), var3 + 7, -723720);
      String var6 = LocalTime.now().format(CLOCK);
      donutauction.Ui.bold(var1, this.font, var6, var2 + var4 / 2.0F - donutauction.Ui.boldWidth(this.font, var6) / 2.0F, var3 + 7, donutauction.Ui.accent());
      donutauction.Auction var7 = donutauction.Auction.current();
      String var8 = var7 != null ? "LIVE " + donutauction.Ui.clock(var7.remainingMs()) : page.title;
      int var9 = donutauction.Ui.width(this.font, var8) + 12;
      donutauction.Ui.box(
         var1,
         var2 + var4 - var9 - 6,
         var3 + 4,
         var9,
         12,
         6,
         var7 != null ? donutauction.Ui.alpha(donutauction.Ui.accent(), 64) : -15855849,
         var7 != null ? donutauction.Ui.accent() : -14013637
      );
      donutauction.Ui.text(var1, this.font, var8, var2 + var4 - var9, var3 + 7, var7 != null ? -723720 : -6513229);
      var1.fill(var2 + 4, var3 + 21, var2 + var4 - 4, var3 + 22, donutauction.Ui.alpha(donutauction.Ui.accent(), 192));
   }

   private List<Item> items() {
      String var1 = search.trim().toLowerCase(Locale.ROOT);
      if (var1.equals(this.gridQuery)) {
         return this.gridItems;
      } else {
         LinkedHashSet var2 = new LinkedHashSet();
         if (this.minecraft.player != null) {
            for (int var3 = 0; var3 < 36; var3++) {
               ItemStack var4 = this.minecraft.player.getInventory().getItem(var3);
               if (!var4.isEmpty() && matches(var4.getItem(), var1)) {
                  var2.add(var4.getItem());
               }
            }
         }

         for (Item var6 : BuiltInRegistries.ITEM) {
            if (var6 != Items.AIR && matches(var6, var1)) {
               var2.add(var6);
            }
         }

         this.gridQuery = var1;
         this.gridItems = new ArrayList<>(var2);
         return this.gridItems;
      }
   }

   private static boolean matches(Item var0, String var1) {
      return var1.isEmpty()
         ? true
         : new ItemStack(var0).getHoverName().getString().toLowerCase(Locale.ROOT).contains(var1)
            || BuiltInRegistries.ITEM.getKey(var0).getPath().contains(var1.replace(' ', '_'));
   }

   private void drawNew(GuiGraphicsExtractor var1, int var2, int var3) {
      int var4 = this.mainX();
      int var5 = this.gridW();
      donutauction.Ui.round(var1, var4, this.top(), var5, this.bottom() - this.top(), 6, -15526882);
      donutauction.Ui.box(
         var1,
         var4 + 4,
         this.top() + 1,
         var5 - 8,
         13,
         4,
         -15855849,
         this.searchBox != null && this.searchBox.isFocused() ? donutauction.Ui.accent() : -14013637
      );
      donutauction.Ui.text(var1, this.font, "⌕", var4 + 7, this.top() + 4, -6513229);
      List var6 = this.items();
      String var7 = var6.size() + " items";
      donutauction.Ui.text(var1, this.font, var7, var4 + var5 - 8 - donutauction.Ui.width(this.font, var7), this.top() + 4, -10526344);
      int var8 = this.cols();
      int var9 = this.visibleRows();
      int var10 = Math.max(0, (var6.size() + var8 - 1) / var8 - var9);
      gridScroll = Math.clamp((long)gridScroll, 0, var10);
      int var11 = var4 + (var5 - var8 * 18) / 2;
      Item var12 = null;

      for (int var13 = 0; var13 < var9; var13++) {
         for (int var14 = 0; var14 < var8; var14++) {
            int var15 = (gridScroll + var13) * var8 + var14;
            if (var15 >= var6.size()) {
               break;
            }

            Item var16 = (Item)var6.get(var15);
            int var17 = var11 + var14 * 18;
            int var18 = this.gridTop() + var13 * 18;
            String var19 = BuiltInRegistries.ITEM.getKey(var16).toString();
            boolean var20 = var19.equals(selectedItem);
            boolean var21 = donutauction.Ui.inside(var2, var3, var17, var18, 17, 17);
            donutauction.Ui.round(var1, var17, var18, 17, 17, 3, var20 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 96) : (var21 ? -14671569 : -15132122));
            var1.item(new ItemStack(var16), var17 + 1, var18 + 1);
            if (var21) {
               var12 = var16;
            }
         }
      }

      if (var10 > 0) {
         int var22 = var9 * 18;
         int var23 = Math.max(10, var22 * var9 / (var9 + var10));
         int var24 = this.gridTop() + (var22 - var23) * gridScroll / var10;
         var1.fill(var4 + var5 - 4, this.gridTop(), var4 + var5 - 2, this.gridTop() + var22, -13882051);
         var1.fill(var4 + var5 - 4, var24, var4 + var5 - 2, var24 + var23, donutauction.Ui.accent());
      }

      if (var12 != null) {
         var1.setTooltipForNextFrame(this.font, new ItemStack(var12).getHoverName(), var2, var3);
      }

      this.drawForm(var1, var2, var3);
   }

   private void drawForm(GuiGraphicsExtractor var1, int var2, int var3) {
      int var4 = this.rightX();
      short var5 = 128;
      int var6 = this.top();
      donutauction.Ui.box(var1, var4, var6, var5, 40, 6, -15526882, -14013637);
      donutauction.Ui.box(var1, var4 + 6, var6 + 8, 24, 24, 5, -15855849, -14013637);
      if (selectedItem == null) {
         donutauction.Ui.text(var1, this.font, "No item yet", var4 + 36, var6 + 11, -723720);
         donutauction.Ui.text(var1, this.font, "Pick one on the left", var4 + 36, var6 + 21, -10526344);
      } else {
         ItemStack var7 = donutauction.Auction.stackOf(selectedItem);
         var1.item(var7, var4 + 10, var6 + 12);
         donutauction.Ui.text(var1, this.font, donutauction.Ui.ellipsize(this.font, var7.getHoverName().getString(), var5 - 42), var4 + 36, var6 + 8, -723720);
         donutauction.WorthService.Result var8 = donutauction.WorthService.get(selectedItem);
         if (var8.status() == donutauction.WorthService.Status.OK) {
            donutauction.Ui.text(var1, this.font, "Worth " + donutauction.Money.format(var8.each()) + " each", var4 + 36, var6 + 17, -670639);
            if (!worthEdited) {
               this.autoFillWorth(var8.each());
            }
         } else {
            donutauction.Ui.text(
               var1,
               this.font,
               donutauction.Ui.ellipsize(this.font, var8.detail(), var5 - 42),
               var4 + 36,
               var6 + 17,
               var8.status() == donutauction.WorthService.Status.LOADING ? -6513229 : -10526344
            );
         }

         boolean var9 = this.minecraft.player != null && this.minecraft.player.getInventory().countItem(var7.getItem()) > 0;
         donutauction.Ui.text(var1, this.font, var9 ? "In your inventory" : "Not in your inventory", var4 + 36, var6 + 26, var9 ? -11870592 : -1035969);
      }

      int var14 = var6 + 50;
      this.label(var1, "Minimum bid", var4, var14 - 8);
      this.frame(var1, var4, var14, var5, this.minBox);
      this.label(var1, "Quantity", var4, var14 + 18);
      this.frame(var1, var4, var14 + 26, var5 / 2 - 4, this.qtyBox);
      this.label(var1, "Timer (sec)", var4 + var5 / 2, var14 + 18);
      this.frame(var1, var4 + var5 / 2, var14 + 26, var5 / 2, this.timerBox);
      this.label(var1, "Worth of one item", var4, var14 + 44);
      this.frame(var1, var4, var14 + 52, var5, this.worthBox);
      double var15 = donutauction.Money.parse(fWorth);
      int var10 = parseInt(fQty, 1);
      String var11 = var15 > 0.0 ? donutauction.Money.format(var15 * Math.max(1, var10)) : "-";
      donutauction.Ui.text(var1, this.font, "Total worth", var4 + 2, var14 + 72, -6513229);
      donutauction.Ui.bold(var1, this.font, var11, var4 + var5 - 2 - donutauction.Ui.boldWidth(this.font, var11), var14 + 72, -670639);
      if (var10 > 1 && var15 > 0.0) {
         String var12 = donutauction.Money.format(var15) + " x " + var10;
         donutauction.Ui.text(var1, this.font, var12, var4 + var5 - 2 - donutauction.Ui.width(this.font, var12), var14 + 81, -10526344);
      }

      int var16 = this.bottom() - 32;
      if (donutauction.Auction.running()) {
         donutauction.Ui.button(var1, this.font, var4, var16, var5, 14, "End now (sell)", donutauction.Ui.inside(var2, var3, var4, var16, var5, 14), true, true);
         donutauction.Ui.button(
            var1, this.font, var4, var16 + 17, var5, 14, "Cancel auction", donutauction.Ui.inside(var2, var3, var4, var16 + 17, var5, 14), false, true
         );
      } else {
         boolean var13 = selectedItem != null;
         donutauction.Ui.button(
            var1, this.font, var4, var16, var5, 14, "Start Auction", var13 && donutauction.Ui.inside(var2, var3, var4, var16, var5, 14), true, var13
         );
         donutauction.Ui.button(
            var1,
            this.font,
            var4,
            var16 + 17,
            var5,
            14,
            "Save as preset",
            var13 && donutauction.Ui.inside(var2, var3, var4, var16 + 17, var5, 14),
            false,
            var13
         );
      }
   }

   private void autoFillWorth(double var1) {
      String var3 = donutauction.Money.format(var1);
      if (this.worthBox != null && !var3.equals(this.worthBox.getValue())) {
         this.settingWorth = true;
         this.worthBox.setValue(var3);
         this.settingWorth = false;
      }

      fWorth = var3;
   }

   private void label(GuiGraphicsExtractor var1, String var2, int var3, int var4) {
      donutauction.Ui.text(var1, this.font, var2, var3 + 2, var4, -6513229);
   }

   private void frame(GuiGraphicsExtractor var1, int var2, int var3, int var4, EditBox var5) {
      boolean var6 = var5 != null && var5.isFocused();
      donutauction.Ui.box(var1, var2, var3, var4, 14, 4, -15855849, var6 ? donutauction.Ui.accent() : -14013637);
   }

   private static int parseInt(String var0, int var1) {
      try {
         return Integer.parseInt(var0.trim());
      } catch (NumberFormatException var3) {
         return var1;
      }
   }

   private void startFromForm() {
      if (selectedItem != null && !donutauction.Auction.running()) {
         double var1 = donutauction.Money.parse(fMin);
         double var3 = donutauction.Money.parse(fWorth);
         int var5 = parseInt(fQty, -1);
         int var6 = parseInt(fTimer, -1);
         if (var1 < 0.0) {
            this.flash("Minimum bid isn't a number");
         } else if (var5 < 1 || var5 > 2304) {
            this.flash("Quantity must be 1-2304");
         } else if (var6 >= 5 && var6 <= 3600) {
            donutauction.Auction.start(selectedItem, var5, var1, Math.max(0.0, var3), var6);
            this.onClose();
         } else {
            this.flash("Timer must be 5-3600 seconds");
         }
      }
   }

   private void saveFromForm() {
      if (selectedItem != null) {
         String var1 = donutauction.Auction.stackOf(selectedItem).getHoverName().getString();
         donutauction.Config.get()
            .presets
            .add(
               new donutauction.Preset(
                  var1,
                  selectedItem,
                  Math.max(1, parseInt(fQty, 1)),
                  Math.max(0.0, donutauction.Money.parse(fMin)),
                  Math.clamp((long)parseInt(fTimer, 60), 5, 3600),
                  Math.max(0.0, donutauction.Money.parse(fWorth))
               )
            );
         donutauction.Config.save();
         this.flash("Saved preset \"" + var1 + "\"");
      }
   }

   private void loadPreset(donutauction.Preset var1) {
      selectedItem = var1.itemId;
      fMin = var1.minBid > 0.0 ? donutauction.Money.format(var1.minBid) : "";
      fQty = Integer.toString(var1.quantity);
      fTimer = Integer.toString(var1.timerSec);
      fWorth = var1.worthEach > 0.0 ? donutauction.Money.format(var1.worthEach) : "";
      worthEdited = var1.worthEach > 0.0;
      search = "";
      this.setPage(donutauction.AuctionScreen.Page.NEW);
   }

   private int presetX(int var1) {
      return this.mainX() + var1 % 2 * ((this.mainW() - 6) / 2 + 6);
   }

   private int presetY(int var1) {
      return this.top() + 16 + var1 / 2 * 56 - listScroll;
   }

   private int presetW() {
      return (this.mainW() - 6) / 2;
   }

   private void drawPresets(GuiGraphicsExtractor var1, int var2, int var3) {
      List var4 = donutauction.Config.get().presets;
      donutauction.Ui.bold(var1, this.font, "Presets", this.mainX() + 2, this.top() + 2, -723720);
      donutauction.Ui.text(
         var1, this.font, var4.size() + " saved", this.mainX() + 6 + donutauction.Ui.boldWidth(this.font, "Presets"), this.top() + 2, -10526344
      );
      if (var4.isEmpty()) {
         donutauction.Ui.text(var1, this.font, "Set up an auction on New Auction and press \"Save as preset\".", this.mainX() + 2, this.top() + 20, -6513229);
      } else {
         var1.enableScissor(this.mainX() - 2, this.top() + 14, this.mainX() + this.mainW() + 2, this.bottom());

         for (int var5 = 0; var5 < var4.size(); var5++) {
            donutauction.Preset var6 = (donutauction.Preset)var4.get(var5);
            int var7 = this.presetX(var5);
            int var8 = this.presetY(var5);
            int var9 = this.presetW();
            if (var8 + 50 >= this.top() && var8 <= this.bottom()) {
               boolean var10 = donutauction.Ui.inside(var2, var3, var7, var8, var9, 50);
               donutauction.Ui.box(
                  var1, var7, var8, var9, 50, 6, var10 ? -14671569 : -15132122, var10 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 160) : -14013637
               );
               donutauction.Ui.round(var1, var7 + 6, var8 + 6, 4, 4, 2, donutauction.Ui.accent());
               donutauction.Ui.bold(var1, this.font, donutauction.Ui.ellipsize(this.font, var6.name, var9 - 24), var7 + 13, var8 + 5, -723720);
               var1.item(donutauction.Auction.stackOf(var6.itemId), var7 + 6, var8 + 15);
               String var11 = (var6.minBid > 0.0 ? "Min " + donutauction.Money.format(var6.minBid) : "No min")
                  + (var6.quantity > 1 ? "  x" + var6.quantity : "")
                  + "  "
                  + var6.timerSec
                  + "s";
               donutauction.Ui.text(var1, this.font, donutauction.Ui.ellipsize(this.font, var11, var9 - 30), var7 + 26, var8 + 16, -6513229);
               if (var6.worthEach > 0.0) {
                  donutauction.Ui.text(var1, this.font, "Worth " + donutauction.Money.format(var6.worthEach * var6.quantity), var7 + 26, var8 + 24, -670639);
               }

               int var12 = (var9 - 16 - 14) / 2;
               donutauction.Ui.button(
                  var1, this.font, var7 + 4, var8 + 35, var12, 11, "Load", donutauction.Ui.inside(var2, var3, var7 + 4, var8 + 35, var12, 11), false, true
               );
               boolean var13 = !donutauction.Auction.running();
               donutauction.Ui.button(
                  var1,
                  this.font,
                  var7 + 8 + var12,
                  var8 + 35,
                  var12,
                  11,
                  "Start",
                  var13 && donutauction.Ui.inside(var2, var3, var7 + 8 + var12, var8 + 35, var12, 11),
                  true,
                  var13
               );
               donutauction.Ui.button(
                  var1,
                  this.font,
                  var7 + var9 - 18,
                  var8 + 35,
                  14,
                  11,
                  "×",
                  donutauction.Ui.inside(var2, var3, var7 + var9 - 18, var8 + 35, 14, 11),
                  false,
                  true
               );
            }
         }

         var1.disableScissor();
      }
   }

   private boolean clickPresets(double var1, double var3) {
      List var5 = donutauction.Config.get().presets;
      if (!donutauction.Ui.inside(var1, var3, this.mainX(), this.top() + 14, this.mainW(), this.bottom() - this.top() - 14)) {
         return false;
      } else {
         for (int var6 = 0; var6 < var5.size(); var6++) {
            donutauction.Preset var7 = (donutauction.Preset)var5.get(var6);
            int var8 = this.presetX(var6);
            int var9 = this.presetY(var6);
            int var10 = this.presetW();
            int var11 = (var10 - 16 - 14) / 2;
            if (donutauction.Ui.inside(var1, var3, var8 + 4, var9 + 35, var11, 11)) {
               this.loadPreset(var7);
               return true;
            }

            if (donutauction.Ui.inside(var1, var3, var8 + 8 + var11, var9 + 35, var11, 11)) {
               if (donutauction.Auction.running()) {
                  return true;
               }

               double var12 = var7.worthEach;
               if (var12 <= 0.0) {
                  donutauction.WorthService.Result var14 = donutauction.WorthService.get(var7.itemId);
                  if (var14.status() == donutauction.WorthService.Status.OK) {
                     var12 = var14.each();
                  }
               }

               donutauction.Auction.start(var7.itemId, var7.quantity, var7.minBid, var12, var7.timerSec);
               this.onClose();
               return true;
            }

            if (donutauction.Ui.inside(var1, var3, var8 + var10 - 18, var9 + 35, 14, 11)) {
               var5.remove(var6);
               donutauction.Config.save();
               this.flash("Deleted preset \"" + var7.name + "\"");
               return true;
            }
         }

         return false;
      }
   }

   private void drawHistory(GuiGraphicsExtractor var1, int var2, int var3) {
      List var4 = donutauction.Config.get().history;
      donutauction.Ui.bold(var1, this.font, "History", this.mainX() + 2, this.top() + 2, -723720);
      int var5 = this.mainX() + this.mainW() - 40;
      if (!var4.isEmpty()) {
         donutauction.Ui.button(var1, this.font, var5, this.top(), 40, 11, "Clear", donutauction.Ui.inside(var2, var3, var5, this.top(), 40, 11), false, true);
      }

      if (var4.isEmpty()) {
         donutauction.Ui.text(var1, this.font, "Finished auctions show up here.", this.mainX() + 2, this.top() + 20, -6513229);
      } else {
         var1.enableScissor(this.mainX() - 2, this.top() + 14, this.mainX() + this.mainW() + 2, this.bottom());

         for (int var6 = 0; var6 < var4.size(); var6++) {
            donutauction.HistoryEntry var7 = (donutauction.HistoryEntry)var4.get(var6);
            int var8 = this.top() + 16 + var6 * 24 - listScroll;
            if (var8 + 22 >= this.top() && var8 <= this.bottom()) {
               donutauction.Ui.box(var1, this.mainX(), var8, this.mainW(), 21, 5, -15132122, -14013637);
               ItemStack var9 = donutauction.Auction.stackOf(var7.itemId);
               var1.item(var9, this.mainX() + 4, var8 + 2);
               donutauction.Ui.text(
                  var1, this.font, var9.getHoverName().getString() + (var7.quantity > 1 ? " x" + var7.quantity : ""), this.mainX() + 24, var8 + 4, -723720
               );
               String var10 = var7.cancelled
                  ? "Cancelled"
                  : (var7.winner == null ? "No bids" : "Sold to " + var7.winner + " for $" + donutauction.Money.format(var7.price));
               donutauction.Ui.text(var1, this.font, var10, this.mainX() + 24, var8 + 12, var7.winner != null ? -6513229 : -10526344);
               if (var7.worthTotal > 0.0) {
                  String var11 = "Worth " + donutauction.Money.format(var7.worthTotal);
                  donutauction.Ui.text(var1, this.font, var11, this.mainX() + this.mainW() - 6 - donutauction.Ui.width(this.font, var11), var8 + 4, -670639);
                  if (var7.winner != null) {
                     double var12 = var7.price - var7.worthTotal;
                     String var14 = (var12 >= 0.0 ? "+" : "-") + donutauction.Money.format(Math.abs(var12));
                     donutauction.Ui.text(
                        var1,
                        this.font,
                        var14,
                        this.mainX() + this.mainW() - 6 - donutauction.Ui.width(this.font, var14),
                        var8 + 12,
                        var12 >= 0.0 ? -11870592 : -1035969
                     );
                  }
               }
            }
         }

         var1.disableScissor();
      }
   }

   private List<donutauction.AuctionScreen.Toggle> toggles() {
      donutauction.Config var1 = donutauction.Config.get();
      return List.of(
         new donutauction.AuctionScreen.Toggle(
            "Announce in chat", "Start, warnings and result in chat", () -> var1.announce, () -> var1.announce = !var1.announce
         ),
         new donutauction.AuctionScreen.Toggle(
            "Announce new top bids", "At most every 2.5s", () -> var1.announceBids, () -> var1.announceBids = !var1.announceBids
         ),
         new donutauction.AuctionScreen.Toggle(
            "10 second warning", "Chat message near the end", () -> var1.timeWarnings, () -> var1.timeWarnings = !var1.timeWarnings
         ),
         new donutauction.AuctionScreen.Toggle(
            "Anti-snipe", "A late top bid resets the timer to 10s", () -> var1.antiSnipe, () -> var1.antiSnipe = !var1.antiSnipe
         ),
         new donutauction.AuctionScreen.Toggle(
            "Auto refund", "/pay back outbid and losing bids", () -> var1.autoRefund, () -> var1.autoRefund = !var1.autoRefund
         )
      );
   }

   private int toggleY(int var1) {
      return this.top() + 14 + var1 / 2 * 26;
   }

   private int toggleX(int var1) {
      return this.mainX() + var1 % 2 * ((this.mainW() - 6) / 2 + 6);
   }

   private int toggleW() {
      return (this.mainW() - 6) / 2;
   }

   private int settingsFieldY(int var1) {
      return this.top() + 14 + 78 + 12 + var1 * 26;
   }

   private void drawSettings(GuiGraphicsExtractor var1, int var2, int var3) {
      donutauction.Ui.text(var1, this.font, "AUCTIONS", this.mainX() + 2, this.top() + 4, -10526344);
      List var4 = this.toggles();

      for (int var5 = 0; var5 < var4.size(); var5++) {
         donutauction.AuctionScreen.Toggle var6 = (donutauction.AuctionScreen.Toggle)var4.get(var5);
         int var7 = this.toggleX(var5);
         int var8 = this.toggleY(var5);
         int var9 = this.toggleW();
         boolean var10 = var6.get().getAsBoolean();
         boolean var11 = donutauction.Ui.inside(var2, var3, var7, var8, var9, 22);
         donutauction.Ui.box(
            var1, var7, var8, var9, 22, 5, var11 ? -14671569 : -15132122, var10 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 160) : -14013637
         );
         donutauction.Ui.text(var1, this.font, var6.label(), var7 + 6, var8 + 4, -723720);
         donutauction.Ui.text(var1, this.font, donutauction.Ui.ellipsize(this.font, var6.hint(), var9 - 34), var7 + 6, var8 + 13, -10526344);
         donutauction.Ui.toggle(var1, var7 + var9 - 24, var8 + 7, var10);
      }

      int var12 = this.settingsFieldY(0);
      int var13 = this.settingsFieldY(1);
      this.label(var1, "DonutSMP API key (for worth)", this.mainX() + 6, var12 - 9);
      this.frame(var1, this.mainX() + 6, var12, this.mainW() - 12, this.apiBox);
      this.label(var1, "Rule text", this.mainX() + 6, var13 - 9);
      this.frame(var1, this.mainX() + 6, var13, this.mainW() - 12, this.ruleBox);
      donutauction.Ui.text(var1, this.font, "Bids are read from \"Name paid you $X\" messages.", this.mainX() + 8, var13 + 20, -10526344);
   }

   private int cardW() {
      return (this.mainW() - 12) / 4;
   }

   private int cardX(int var1) {
      return this.mainX() + var1 % 4 * (this.cardW() + 4);
   }

   private int cardY(int var1) {
      return this.top() + 13 + var1 / 4 * 34;
   }

   private int panelY() {
      return this.cardY(4) + 36;
   }

   private int panelW() {
      return Math.min(this.mainW(), 300);
   }

   private int panelX() {
      return this.mainX() + (this.mainW() - this.panelW()) / 2;
   }

   private int rowY(int var1) {
      return this.panelY() + 19 + var1 * 26;
   }

   private int resetX() {
      return this.panelX() + this.panelW() - 16;
   }

   private void drawTheme(GuiGraphicsExtractor var1, int var2, int var3) {
      donutauction.Config var4 = donutauction.Config.get();
      int var5 = donutauction.Ui.paletteIndex();
      int var6 = this.top() + 2;
      donutauction.Ui.text(var1, this.font, "PALETTES", this.mainX() + 2, var6, -10526344);
      String var7 = var5 >= 0 ? donutauction.Ui.PALETTE_NAMES[var5] : "Custom";
      String var8 = "Active: ";
      int var9 = this.mainX() + this.mainW() - 2 - donutauction.Ui.width(this.font, var8 + var7);
      donutauction.Ui.text(var1, this.font, var8, var9, var6, -10526344);
      donutauction.Ui.text(var1, this.font, var7, var9 + donutauction.Ui.width(this.font, var8), var6, donutauction.Ui.accent());
      int var10 = this.cardW();

      for (int var11 = 0; var11 < donutauction.Ui.PALETTES.length; var11++) {
         int[] var12 = donutauction.Ui.PALETTES[var11];
         int var13 = this.cardX(var11);
         int var14 = this.cardY(var11);
         boolean var15 = var11 == var5;
         boolean var16 = donutauction.Ui.inside(var2, var3, var13, var14, var10, 30);
         int var17 = var15 ? donutauction.Ui.alpha(var12[0], 46) : (var16 ? -14671569 : -15132122);
         donutauction.Ui.box(var1, var13, var14, var10, 30, 5, var17, var15 ? var12[0] : (var16 ? donutauction.Ui.alpha(var12[0], 120) : -14013637));
         if (var15) {
            donutauction.Ui.round(var1, var13 + 2, var14 + 2, var10 - 4, 30 - 4, 4, donutauction.Ui.alpha(var12[3], 140));
         }

         donutauction.Ui.text(
            var1, this.font, donutauction.Ui.ellipsize(this.font, donutauction.Ui.PALETTE_NAMES[var11], var10 - 18), var13 + 6, var14 + 5, var15 ? -723720 : -6513229
         );
         if (var15) {
            donutauction.Ui.round(var1, var13 + var10 - 10, var14 + 5, 5, 5, 2, var12[0]);
         } else {
            donutauction.Ui.round(var1, var13 + var10 - 10, var14 + 5, 5, 5, 2, -13882051);
         }

         int var18 = var13 + 5;
         int var19 = var14 + 17;
         int var20 = var10 - 10;
         donutauction.Ui.round(var1, var18, var19, var20, 8, 3, var12[3]);
         int var21 = var20 * 5 / 10;
         int var22 = var20 * 3 / 10;
         donutauction.Ui.round(var1, var18, var19, var21 + 3, 8, 3, var12[0]);
         var1.fill(var18 + var21, var19, var18 + var21 + var22, var19 + 8, var12[1]);
         var1.fill(var18 + var21 + var22, var19, var18 + var20 - 4, var19 + 8, var12[2]);
         donutauction.Ui.round(var1, var18 + var20 - 7, var19, 7, 8, 3, var12[2]);
         var1.fill(var18 + var21, var19, var18 + var21 + 1, var19 + 8, donutauction.Ui.alpha(var12[3], 120));
         var1.fill(var18 + var21 + var22, var19, var18 + var21 + var22 + 1, var19 + 8, donutauction.Ui.alpha(var12[3], 120));
      }

      int var23 = this.panelX();
      int var24 = this.panelY();
      int var25 = this.panelW();
      int var26 = Math.min(this.bottom() - var24, 99);
      donutauction.Ui.box(var1, var23, var24, var25, var26, 7, donutauction.Ui.alpha(-15132122, var4.seeThroughGui ? 210 : 255), -14013637);
      donutauction.Ui.round(var1, var23 + 9, var24 + 6, 6, 6, 2, donutauction.Ui.accent());
      donutauction.Ui.round(var1, var23 + 12, var24 + 9, 4, 4, 1, donutauction.Ui.alpha(darker(donutauction.Ui.accent()), 255));
      donutauction.Ui.text(var1, this.font, Component.literal("EFFECTS").withStyle(ChatFormatting.BOLD), var23 + 21, var24 + 6, -6513229, 0.85F);
      String[][] var27 = new String[][]{
         {"See-Through GUI", "Lets the game show through the menu."},
         {"Frosted Blur", "Blurs the world behind the menu."},
         {"Ambient Background", "Soft glow in your palette color."}
      };
      boolean[] var28 = new boolean[]{var4.seeThroughGui, var4.frostedBlur, var4.ambientBackground};

      for (int var29 = 0; var29 < var27.length; var29++) {
         int var30 = this.rowY(var29);
         if (donutauction.Ui.inside(var2, var3, var23 + 3, var30, var25 - 22, 24)) {
            donutauction.Ui.round(var1, var23 + 4, var30, var25 - 8, 24, 5, donutauction.Ui.alpha(donutauction.Ui.accent(), 30));
         }

         donutauction.Ui.text(var1, this.font, Component.literal(var27[var29][0]).withStyle(ChatFormatting.BOLD), var23 + 12, var30 + 4, -1, 0.9F);
         donutauction.Ui.text(
            var1, this.font, Component.literal(donutauction.Ui.ellipsize(this.font, var27[var29][1], var25 - 60)), var23 + 12, var30 + 14, -6513229, 0.7F
         );
         donutauction.Ui.bigToggle(var1, var23 + var25 - 46, var30 + 6, var28[var29]);
         boolean var31 = donutauction.Ui.inside(var2, var3, this.resetX() - 2, var30 + 5, 12, 14);
         donutauction.Ui.text(var1, this.font, Component.literal("↻"), this.resetX(), var30 + 8, var31 ? -723720 : -10526344, 0.8F);
         if (var31) {
            var1.setTooltipForNextFrame(this.font, Component.literal("Reset to default"), var2, var3);
         }
      }
   }

   private boolean clickTheme(double var1, double var3) {
      donutauction.Config var5 = donutauction.Config.get();

      for (int var6 = 0; var6 < donutauction.Ui.PALETTES.length; var6++) {
         if (donutauction.Ui.inside(var1, var3, this.cardX(var6), this.cardY(var6), this.cardW(), 30)) {
            var5.accent = donutauction.Ui.PALETTES[var6][0];
            donutauction.Config.save();
            return true;
         }
      }

      for (int var7 = 0; var7 < 3; var7++) {
         int var8 = this.rowY(var7);
         boolean var9 = donutauction.Ui.inside(var1, var3, this.resetX() - 2, var8 + 5, 12, 14);
         if (var9 || donutauction.Ui.inside(var1, var3, this.panelX() + 3, var8, this.panelW() - 22, 24)) {
            switch (var7) {
               case 0 -> var5.seeThroughGui = var9 || !var5.seeThroughGui;
               case 1 -> var5.frostedBlur = var9 || !var5.frostedBlur;
               default -> var5.ambientBackground = var9 || !var5.ambientBackground;
            }

            donutauction.Config.save();
            return true;
         }
      }

      return false;
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (super.mouseClicked(var1, var2)) {
         return true;
      } else if (var1.button() != 0) {
         return false;
      } else {
         double var3 = var1.x();
         double var5 = var1.y();
         donutauction.AuctionScreen.Page[] var7 = donutauction.AuctionScreen.Page.values();

         for (int var8 = 0; var8 < var7.length; var8++) {
            if (donutauction.Ui.inside(var3, var5, this.x0 + 8, this.navY(var8), 90, 14)) {
               this.setPage(var7[var8]);
               return true;
            }
         }
         boolean var9 = switch (page) {
            case NEW -> this.clickNew(var3, var5);
            case PRESETS -> this.clickPresets(var3, var5);
            case HISTORY -> this.clickHistory(var3, var5);
            case SETTINGS -> this.clickSettings(var3, var5);
            case THEME -> this.clickTheme(var3, var5);
         };
         if (!var9) {
            this.setFocused(null);
         }

         return var9;
      }
   }

   private boolean clickNew(double var1, double var3) {
      List var5 = this.items();
      int var6 = this.cols();
      int var7 = this.visibleRows();
      int var8 = this.mainX() + (this.gridW() - var6 * 18) / 2;

      for (int var9 = 0; var9 < var7; var9++) {
         for (int var10 = 0; var10 < var6; var10++) {
            int var11 = (gridScroll + var9) * var6 + var10;
            if (var11 >= var5.size()) {
               break;
            }

            if (donutauction.Ui.inside(var1, var3, var8 + var10 * 18, this.gridTop() + var9 * 18, 17, 17)) {
               String var12 = BuiltInRegistries.ITEM.getKey((Item)var5.get(var11)).toString();
               if (!var12.equals(selectedItem)) {
                  selectedItem = var12;
                  worthEdited = false;
                  fWorth = "";
                  if (this.worthBox != null) {
                     this.settingWorth = true;
                     this.worthBox.setValue("");
                     this.settingWorth = false;
                  }
               }

               return true;
            }
         }
      }

      int var13 = this.rightX();
      int var14 = this.bottom() - 32;
      if (donutauction.Ui.inside(var1, var3, var13, var14, 128, 14)) {
         if (donutauction.Auction.running()) {
            donutauction.Auction.endNow();
            this.flash("Auction ended");
         } else {
            this.startFromForm();
         }

         return true;
      } else if (donutauction.Ui.inside(var1, var3, var13, var14 + 17, 128, 14)) {
         if (donutauction.Auction.running()) {
            donutauction.Auction.cancel();
            this.flash("Auction cancelled");
         } else {
            this.saveFromForm();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean clickHistory(double var1, double var3) {
      int var5 = this.mainX() + this.mainW() - 40;
      if (!donutauction.Config.get().history.isEmpty() && donutauction.Ui.inside(var1, var3, var5, this.top(), 40, 11)) {
         donutauction.Config.get().history.clear();
         donutauction.Config.save();
         return true;
      } else {
         return false;
      }
   }

   private boolean clickSettings(double var1, double var3) {
      List var5 = this.toggles();

      for (int var6 = 0; var6 < var5.size(); var6++) {
         if (donutauction.Ui.inside(var1, var3, this.toggleX(var6), this.toggleY(var6), this.toggleW(), 22)) {
            ((donutauction.AuctionScreen.Toggle)var5.get(var6)).flip().run();
            donutauction.Config.save();
            return true;
         }
      }

      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (page == donutauction.AuctionScreen.Page.NEW
         && donutauction.Ui.inside(var1, var3, this.mainX(), this.gridTop(), this.gridW(), this.bottom() - this.gridTop())) {
         gridScroll = gridScroll - (int)Math.signum(var7);
         return true;
      } else if (page != donutauction.AuctionScreen.Page.PRESETS && page != donutauction.AuctionScreen.Page.HISTORY) {
         return super.mouseScrolled(var1, var3, var5, var7);
      } else {
         int var9 = page == donutauction.AuctionScreen.Page.PRESETS
            ? (donutauction.Config.get().presets.size() + 1) / 2
            : donutauction.Config.get().history.size();
         int var10 = page == donutauction.AuctionScreen.Page.PRESETS ? var9 * 56 : var9 * 24;
         int var11 = Math.max(0, var10 - (this.bottom() - this.top() - 16));
         listScroll = Math.clamp(listScroll - Math.round(var7 * 14.0), 0, var11);
         return true;
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private static enum Page {
      NEW("New Auction", "+"),
      PRESETS("Presets", "☰"),
      HISTORY("History", "⌛"),
      SETTINGS("Settings", "⚙"),
      THEME("Theme", "◐");

      final String title;
      final String icon;

      private Page(String nullxx, String nullxxx) {
         this.title = nullxx;
         this.icon = nullxxx;
      }
   }

   private record Toggle(String label, String hint, BooleanSupplier get, Runnable flip) {
   }
}
