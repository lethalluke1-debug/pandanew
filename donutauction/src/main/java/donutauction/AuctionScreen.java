package donutauction;

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
      donutauction.Ui.sound("open", 1.0F);
   }

   private int mainX() {
      return this.x0 + 116;
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
      return this.y0 + 62 + var1 * 18 + (var1 >= 3 ? 18 : 0);
   }

   private int searchW() {
      return Math.min(160, this.mainW() * 2 / 5);
   }

   private int searchX() {
      return this.mainX() + this.mainW() - this.searchW() - 5;
   }

   protected void init() {
      this.w = Math.clamp((long)(this.width - 30), 380, 600);
      this.h = Math.clamp((long)(this.height - 30), 230, 340);
      this.x0 = (this.width - this.w) / 2;
      this.y0 = (this.height - this.h) / 2;
      this.searchBox = this.minBox = this.qtyBox = this.timerBox = this.worthBox = this.apiBox = this.ruleBox = null;
      switch (page) {
         case NEW:
            this.searchBox = this.field(this.searchX() + 18, this.y0 + 12, this.searchW() - 24, search, "Search items...", 40, var0 -> {
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
      var8.setHint(donutauction.Ui.styled(Component.literal(var5).withColor(-10526344)));
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
      donutauction.Ui.round(var1, this.x0 - 1, this.y0 - 1, this.w + 2, this.h + 2, 14, 0x30FFFFFF);
      donutauction.Ui.round(var1, this.x0, this.y0, this.w, this.h, 13, donutauction.Ui.windowBg());
      if (donutauction.Config.get().ambientBackground) {
         var1.enableScissor(this.x0 + 2, this.y0 + 2, this.x0 + this.w - 2, this.y0 + this.h - 2);
         donutauction.Ui.glow(
            var1, this.mainX() - 30, this.top() - 30, this.mainW() + 60, this.bottom() - this.top() + 60, donutauction.Ui.alpha(donutauction.Ui.accent(), 150)
         );
         var1.disableScissor();
      }

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
            var1, this.x0 + (this.w - var5) / 2, this.y0 + this.h - 18, var5, 12, 5, donutauction.Ui.card(), donutauction.Ui.alpha(donutauction.Ui.accent(), 160)
         );
         donutauction.Ui.text(var1, this.font, this.status, this.x0 + (this.w - var5) / 2.0F + 6.0F, this.y0 + this.h - 15, -723720);
      }
   }

   private void drawSidebar(GuiGraphicsExtractor var1, int var2, int var3) {
      int var4 = this.x0 + 6;
      int var5 = this.y0 + 6;
      int var6 = 104;
      int var7 = this.h - 12;
      int var8 = donutauction.Ui.accent();
      donutauction.Ui.round(var1, var4 - 1, var5 - 1, var6 + 2, var7 + 2, 11, 0x18FFFFFF);
      donutauction.Ui.roundGradient(var1, var4, var5, var6, var7, 10, donutauction.Ui.sidebarTop(), donutauction.Ui.sidebarBottom());
      donutauction.Ui.glow(var1, var4 - 4, var5 - 2, 44, 44, donutauction.Ui.alpha(var8, 150));
      donutauction.Ui.tex(var1, donutauction.Ui.id("textures/gui/logo.png"), var4 + 10, var5 + 10, 20, 20, var8);
      this.spaced(var1, "DONUT", var4 + 37, var5 + 11, -1, 1.05F, 1.2F);
      donutauction.Ui.text(var1, this.font, Component.literal("v1.0.0"), var4 + 38, var5 + 22, 0xFF6E6E7A, 0.65F);
      var1.fill(var4 + 10, var5 + 38, var4 + var6 - 10, var5 + 39, 0x18FFFFFF);
      donutauction.Ui.text(var1, this.font, Component.literal("AUCTION"), var4 + 10, this.navY(0) - 11, 0xFF8A8A96, 0.75F);
      donutauction.Ui.text(var1, this.font, Component.literal("GENERAL"), var4 + 10, this.navY(3) - 11, 0xFF8A8A96, 0.75F);
      donutauction.AuctionScreen.Page[] var9 = donutauction.AuctionScreen.Page.values();

      for (int var10 = 0; var10 < var9.length; var10++) {
         int var11 = this.navY(var10);
         boolean var12 = var9[var10] == page;
         boolean var13 = donutauction.Ui.inside(var2, var3, var4 + 4, var11, var6 - 8, 16);
         if (var12) {
            donutauction.Ui.round(var1, var4 + 4, var11, var6 - 8, 16, 5, donutauction.Ui.alpha(var8, 46));
            donutauction.Ui.round(var1, var4 + 1, var11 + 3, 2, 10, 1, var8);
         } else if (var13) {
            donutauction.Ui.round(var1, var4 + 4, var11, var6 - 8, 16, 5, 0x14FFFFFF);
         }

         int var14 = var12 ? var8 : (var13 ? -1 : 0xFFB4B4BE);
         donutauction.Ui.icon(var1, var9[var10].icon, var4 + 12, var11 + 3, 10, var12 ? var8 : 0xFF8A8A96);
         donutauction.Ui.text(var1, this.font, Component.literal(var9[var10].title), var4 + 28, var11 + 4, var14, 0.85F);
      }

      int var15 = var5 + var7 - 32;
      donutauction.Ui.round(var1, var4 + 5, var15, var6 - 10, 27, 7, donutauction.Ui.card());
      if (this.minecraft.player != null) {
         PlayerFaceExtractor.extractRenderState(var1, this.minecraft.player.getSkin(), var4 + 10, var15 + 5, 17);
      }

      donutauction.Ui.text(
         var1,
         this.font,
         Component.literal(donutauction.Ui.ellipsize(this.font, this.minecraft.getUser().getName(), var6 - 50)).withStyle(ChatFormatting.BOLD),
         var4 + 32,
         var15 + 6,
         -1,
         0.8F
      );
      donutauction.Ui.text(
         var1,
         this.font,
         Component.literal(donutauction.Auction.running() ? "Auction live" : "Seller"),
         var4 + 32,
         var15 + 16,
         donutauction.Auction.running() ? var8 : 0xFF8A8A96,
         0.65F
      );
      int var16 = donutauction.Auction.running() ? var8 : -11870592;
      donutauction.Ui.glow(var1, var4 + var6 - 21, var15 + 7, 13, 13, donutauction.Ui.alpha(var16, 190));
      donutauction.Ui.round(var1, var4 + var6 - 17, var15 + 11, 5, 5, 3, var16);
   }

   private void spaced(GuiGraphicsExtractor var1, String var2, int var3, int var4, int var5, float var6, float var7) {
      float var8 = var3;

      for (int var9 = 0; var9 < var2.length(); var9++) {
         Component var10 = Component.literal(String.valueOf(var2.charAt(var9))).withStyle(ChatFormatting.BOLD);
         donutauction.Ui.text(var1, this.font, var10, var8, var4, var5, var6);
         var8 += donutauction.Ui.fw(this.font, var10) * var6 + var7;
      }
   }

   private void drawHeader(GuiGraphicsExtractor var1) {
      int var2 = this.mainX();
      int var3 = this.y0 + 6;
      int var4 = this.mainW();
      donutauction.Ui.round(var1, var2 - 1, var3 - 1, var4 + 2, 24, 9, 0x18FFFFFF);
      donutauction.Ui.round(var1, var2, var3, var4, 22, 8, donutauction.Ui.panelBg());
      String var5 = this.minecraft.getUser().getName();
      donutauction.Ui.text(var1, this.font, Component.literal("Hello, "), var2 + 10, var3 + 7, 0xFFB4B4BE, 0.85F);
      donutauction.Ui.text(
         var1,
         this.font,
         Component.literal(var5).withStyle(ChatFormatting.BOLD),
         var2 + 10 + donutauction.Ui.fw(this.font, "Hello, ") * 0.85F,
         var3 + 7,
         -1,
         0.85F
      );
      int var6 = this.searchX();
      int var7 = this.searchW();
      boolean var8 = this.searchBox != null && this.searchBox.isFocused();
      donutauction.Ui.round(var1, var6 - 1, var3 + 3, var7 + 2, 16, 8, var8 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 200) : 0x22FFFFFF);
      donutauction.Ui.round(var1, var6, var3 + 4, var7, 14, 7, donutauction.Ui.field());
      donutauction.Ui.icon(var1, "search", var6 + 6, var3 + 7, 8, 0xFF8A8A96);
      if (this.searchBox == null) {
         donutauction.Ui.text(var1, this.font, Component.literal("Search items..."), var6 + 18, var3 + 7, 0xFF6E6E7A, 0.75F);
      }

      donutauction.Auction var9 = donutauction.Auction.current();
      if (var9 != null) {
         String var10 = "LIVE " + donutauction.Ui.clock(var9.remainingMs());
         int var11 = Math.round(donutauction.Ui.fw(this.font, var10) * 0.75F) + 16;
         int var12 = var6 - var11 - 6;
         donutauction.Ui.round(var1, var12, var3 + 4, var11, 14, 7, donutauction.Ui.alpha(donutauction.Ui.accent(), 60));
         donutauction.Ui.round(var1, var12 + 5, var3 + 9, 4, 4, 2, donutauction.Ui.accent());
         donutauction.Ui.text(var1, this.font, Component.literal(var10), var12 + 12, var3 + 7, -1, 0.75F);
      }

      donutauction.Ui.glow(var1, var2 - 20, var3 + 15, var4 + 40, 16, donutauction.Ui.alpha(donutauction.Ui.accent(), 210));
      donutauction.Ui.round(var1, var2 + 4, var3 + 22, var4 - 8, 1, 0, donutauction.Ui.accent());
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
      donutauction.Ui.round(var1, var4, this.top(), var5, this.bottom() - this.top(), 9, donutauction.Ui.card());
      donutauction.Ui.icon(var1, "grid", var4 + 8, this.top() + 6, 8, donutauction.Ui.accent());
      donutauction.Ui.text(var1, this.font, Component.literal("ITEMS").withStyle(ChatFormatting.BOLD), var4 + 20, this.top() + 6, 0xFFB4B4BE, 0.75F);
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
            donutauction.Ui.round(var1, var17, var18, 17, 17, 3, var20 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 96) : (var21 ? donutauction.Ui.cardHover() : donutauction.Ui.card()));
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
         var1.fill(var4 + var5 - 4, this.gridTop(), var4 + var5 - 2, this.gridTop() + var22, donutauction.Ui.border());
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
      donutauction.Ui.round(var1, var4, var6, var5, 40, 8, donutauction.Ui.card());
      donutauction.Ui.box(var1, var4 + 6, var6 + 8, 24, 24, 5, donutauction.Ui.field(), donutauction.Ui.border());
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
      donutauction.Ui.box(var1, var2, var3, var4, 14, 4, donutauction.Ui.field(), var6 ? donutauction.Ui.accent() : donutauction.Ui.border());
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
                  var1, var7, var8, var9, 50, 6, var10 ? donutauction.Ui.cardHover() : donutauction.Ui.card(), var10 ? donutauction.Ui.alpha(donutauction.Ui.accent(), 160) : donutauction.Ui.border()
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
               donutauction.Ui.box(var1, this.mainX(), var8, this.mainW(), 21, 5, donutauction.Ui.card(), donutauction.Ui.border());
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
      return this.top() + 18 + var1 / 2 * 34;
   }

   private int toggleX(int var1) {
      return this.mainX() + var1 % 2 * ((this.mainW() - 6) / 2 + 6);
   }

   private int toggleW() {
      return (this.mainW() - 6) / 2;
   }

   private int settingsFieldY(int var1) {
      return this.top() + 18 + 102 + 14 + var1 * 26;
   }

   private void drawSettings(GuiGraphicsExtractor var1, int var2, int var3) {
      this.sectionTitle(var1, "gear", "AUCTIONS", this.mainX() + 2, this.top() + 4);
      List var4 = this.toggles();

      for (int var5 = 0; var5 < var4.size(); var5++) {
         donutauction.AuctionScreen.Toggle var6 = (donutauction.AuctionScreen.Toggle)var4.get(var5);
         int var7 = this.toggleX(var5);
         int var8 = this.toggleY(var5);
         int var9 = this.toggleW();
         boolean var10 = var6.get().getAsBoolean();
         boolean var11 = donutauction.Ui.inside(var2, var3, var7, var8, var9, 28);
         donutauction.Ui.round(var1, var7, var8, var9, 28, 8, var11 ? donutauction.Ui.cardHover() : donutauction.Ui.card());
         Component var12x = Component.literal(var6.label()).withStyle(ChatFormatting.BOLD);
         float var13x = Math.min(0.85F, (var9 - 46) / (float)Math.max(1, donutauction.Ui.fw(this.font, var12x)));
         donutauction.Ui.text(var1, this.font, var12x, var7 + 9, var8 + 5 + (0.85F - var13x) * 4.0F, -1, var13x);
         donutauction.Ui.text(
            var1, this.font, Component.literal(donutauction.Ui.ellipsize(this.font, var6.hint(), var9 - 50)), var7 + 9, var8 + 16, 0xFF8A8A96, 0.65F
         );
         donutauction.Ui.bigToggle(var1, var7 + var9 - 31, var8 + 9, var10);
      }

      int var12 = this.settingsFieldY(0);
      int var13 = this.settingsFieldY(1);
      this.label(var1, "DonutSMP API key (for worth)", this.mainX() + 6, var12 - 11);
      this.frame(var1, this.mainX() + 6, var12, this.mainW() - 12, this.apiBox);
      this.label(var1, "Rule text", this.mainX() + 6, var13 - 11);
      this.frame(var1, this.mainX() + 6, var13, this.mainW() - 12, this.ruleBox);
      donutauction.Ui.text(var1, this.font, "Bids are read from \"Name paid you $X\" messages.", this.mainX() + 8, var13 + 20, -10526344);
   }

   private int themeY() {
      return this.top() + 2 - listScroll;
   }

   private int presetsY() {
      return this.themeY() + 28;
   }

   private int gridX() {
      return this.mainX() + 8;
   }

   private int cardW() {
      return (this.mainW() - 16 - 12) / 4;
   }

   private int cardX(int var1) {
      return this.gridX() + var1 % 4 * (this.cardW() + 4);
   }

   private int cardY(int var1) {
      return this.presetsY() + 34 + var1 / 4 * 36;
   }

   private int presetsH() {
      int var1 = (donutauction.Ui.PALETTES.length + 3) / 4;
      return 34 + var1 * 36 + 4;
   }

   private int panelY() {
      return this.presetsY() + this.presetsH() + 8;
   }

   private int panelW() {
      return this.mainW();
   }

   private int panelX() {
      return this.mainX();
   }

   private int rowY(int var1) {
      return this.panelY() + 24 + var1 * 26;
   }

   private int resetX() {
      return this.panelX() + this.panelW() - 18;
   }

   private int themeContentH() {
      return 28 + this.presetsH() + 8 + 24 + 78 + 8;
   }

   private boolean inView(double var1) {
      return var1 >= this.top() && var1 < this.bottom();
   }

   private void sectionTitle(GuiGraphicsExtractor var1, String var2, String var3, int var4, int var5) {
      donutauction.Ui.icon(var1, var2, var4, var5, 9, donutauction.Ui.accent());
      donutauction.Ui.text(var1, this.font, Component.literal(var3).withStyle(ChatFormatting.BOLD), var4 + 14, var5 + 1, -6513229, 0.75F);
   }

   private void drawTheme(GuiGraphicsExtractor var1, int var2, int var3) {
      donutauction.Config var4 = donutauction.Config.get();
      int var5 = donutauction.Ui.paletteIndex();
      int var6 = donutauction.Ui.accent();
      boolean var7 = this.inView(var3);
      var1.enableScissor(this.mainX() - 2, this.top(), this.mainX() + this.mainW() + 2, this.bottom());
      int var8 = this.themeY();
      donutauction.Ui.glow(var1, this.mainX() - 30, var8 - 30, this.mainW() + 60, 84, donutauction.Ui.alpha(var6, 110));
      donutauction.Ui.round(var1, this.mainX() + 4, var8 + 3, 18, 18, 9, var6);
      donutauction.Ui.round(var1, this.mainX() + 5, var8 + 4, 16, 16, 8, 0xFF141419);
      donutauction.Ui.icon(var1, "palette", this.mainX() + 8, var8 + 7, 10, var6);
      donutauction.Ui.text(var1, this.font, Component.literal("Theme").withStyle(ChatFormatting.BOLD), this.mainX() + 27, var8 + 4, -1, 0.85F);
      donutauction.Ui.text(var1, this.font, Component.literal("Customize the look and feel of your menu."), this.mainX() + 27, var8 + 14, -6513229, 0.65F);
      int var9 = this.presetsY();
      donutauction.Ui.round(var1, this.mainX(), var9, this.mainW(), this.presetsH(), 9, donutauction.Ui.card());
      this.sectionTitle(var1, "grid", "PRESETS", this.mainX() + 9, var9 + 8);
      donutauction.Ui.text(var1, this.font, Component.literal("Click a palette to theme the whole menu"), this.mainX() + 9, var9 + 22, -6513229, 0.7F);
      int var10 = this.cardW();

      for (int var11 = 0; var11 < donutauction.Ui.PALETTES.length; var11++) {
         int[] var12 = donutauction.Ui.PALETTES[var11];
         int var13 = this.cardX(var11);
         int var14 = this.cardY(var11);
         boolean var15 = var11 == var5;
         boolean var16 = var7 && donutauction.Ui.inside(var2, var3, var13, var14, var10, 32);
         if (var15) {
            donutauction.Ui.glow(var1, var13 - 8, var14 - 8, var10 + 16, 48, donutauction.Ui.alpha(var12[0], 70));
            donutauction.Ui.round(var1, var13 - 1, var14 - 1, var10 + 2, 34, 7, var12[0]);
         } else if (var16) {
            donutauction.Ui.round(var1, var13 - 1, var14 - 1, var10 + 2, 34, 7, donutauction.Ui.alpha(var12[0], 140));
         }

         donutauction.Ui.round(var1, var13, var14, var10, 32, 6, var16 && !var15 ? 0xFF24242E : donutauction.Ui.cardHover());
         donutauction.Ui.text(
            var1,
            this.font,
            Component.literal(donutauction.Ui.ellipsize(this.font, donutauction.Ui.PALETTE_NAMES[var11], var10 - 14)),
            var13 + 5,
            var14 + 4,
            var15 || var16 ? -1 : -2039584,
            0.75F
         );
         if (var15) {
            donutauction.Ui.round(var1, var13 + var10 - 8, var14 + 4, 4, 4, 2, var12[0]);
         }

         int var17 = var13 + 4;
         int var18 = var14 + 14;
         int var19 = var10 - 8;
         int var20 = var19 / 4;
         donutauction.Ui.round(var1, var17, var18, var19, 14, 3, var12[3]);
         donutauction.Ui.round(var1, var17, var18, var20 + 4, 14, 3, var12[0]);
         var1.fill(var17 + var20, var18, var17 + var20 * 2, var18 + 14, var12[1]);
         var1.fill(var17 + var20 * 2, var18, var17 + var20 * 3, var18 + 14, var12[2]);
      }

      int var21 = this.panelX();
      int var22 = this.panelY();
      int var23 = this.panelW();
      donutauction.Ui.round(var1, var21, var22, var23, 24 + 78 + 4, 9, donutauction.Ui.card());
      this.sectionTitle(var1, "spark", "EFFECTS", var21 + 9, var22 + 8);
      String[][] var24 = new String[][]{
         {"See-Through GUI", "Allow the game to show through the menu."},
         {"Frosted Blur", "Apply frosted glass blur behind the menu."},
         {"Ambient Background", "Enable the soft glow in your palette color."}
      };
      boolean[] var25 = new boolean[]{var4.seeThroughGui, var4.frostedBlur, var4.ambientBackground};

      for (int var26 = 0; var26 < var24.length; var26++) {
         int var27 = this.rowY(var26);
         boolean var28 = var7 && donutauction.Ui.inside(var2, var3, var21 + 4, var27, var23 - 26, 24);
         if (var28) {
            donutauction.Ui.round(var1, var21 + 4, var27, var23 - 8, 24, 6, donutauction.Ui.cardHover());
         }

         donutauction.Ui.text(var1, this.font, Component.literal(var24[var26][0]).withStyle(ChatFormatting.BOLD), var21 + 12, var27 + 4, -1, 0.85F);
         donutauction.Ui.text(
            var1, this.font, Component.literal(donutauction.Ui.ellipsize(this.font, var24[var26][1], var23 - 70)), var21 + 12, var27 + 14, -6513229, 0.65F
         );
         donutauction.Ui.bigToggle(var1, var21 + var23 - 50, var27 + 6, var25[var26]);
         boolean var29 = var7 && donutauction.Ui.inside(var2, var3, this.resetX() - 2, var27 + 5, 13, 14);
         donutauction.Ui.icon(var1, "reset", this.resetX(), var27 + 8, 8, var29 ? -1 : -10526344);
      }

      int var30 = this.themeContentH();
      int var31 = this.bottom() - this.top();
      if (var30 > var31) {
         int var32 = Math.max(16, var31 * var31 / var30);
         int var33 = this.top() + (var31 - var32) * listScroll / Math.max(1, var30 - var31);
         donutauction.Ui.round(var1, this.mainX() + this.mainW() + 1, var33, 2, var32, 1, donutauction.Ui.alpha(var6, 150));
      }

      var1.disableScissor();
   }

   private boolean clickTheme(double var1, double var3) {
      if (!this.inView(var3)) {
         return false;
      } else {
         donutauction.Config var5 = donutauction.Config.get();

         for (int var6 = 0; var6 < donutauction.Ui.PALETTES.length; var6++) {
            if (donutauction.Ui.inside(var1, var3, this.cardX(var6), this.cardY(var6), this.cardW(), 32)) {
               var5.accent = donutauction.Ui.PALETTES[var6][0];
               donutauction.Config.save();
               donutauction.Ui.sound("select", 1.0F);
               return true;
            }
         }

         for (int var7 = 0; var7 < 3; var7++) {
            int var8 = this.rowY(var7);
            boolean var9 = donutauction.Ui.inside(var1, var3, this.resetX() - 2, var8 + 5, 13, 14);
            if (var9 || donutauction.Ui.inside(var1, var3, this.panelX() + 4, var8, this.panelW() - 26, 24)) {
               boolean var10 = switch (var7) {
                  case 0 -> var5.seeThroughGui = var9 || !var5.seeThroughGui;
                  case 1 -> var5.frostedBlur = var9 || !var5.frostedBlur;
                  default -> var5.ambientBackground = var9 || !var5.ambientBackground;
               };
               donutauction.Config.save();
               donutauction.Ui.sound(var10 ? "toggle_on" : "toggle_off", 1.0F);
               return true;
            }
         }

         return false;
      }
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
            if (donutauction.Ui.inside(var3, var5, this.x0 + 10, this.navY(var8), 96, 16)) {
               if (var7[var8] != page) {
                  donutauction.Ui.sound("click", 1.0F);
               }

               this.setPage(var7[var8]);
               return true;
            }
         }
         if (page != donutauction.AuctionScreen.Page.NEW && donutauction.Ui.inside(var3, var5, this.searchX(), this.y0 + 10, this.searchW(), 14)) {
            donutauction.Ui.sound("click", 1.0F);
            this.setPage(donutauction.AuctionScreen.Page.NEW);
            if (this.searchBox != null) {
               this.setFocused(this.searchBox);
            }

            return true;
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
         } else if (page != donutauction.AuctionScreen.Page.THEME && page != donutauction.AuctionScreen.Page.SETTINGS) {
            donutauction.Ui.sound("click", 1.0F);
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
         if (donutauction.Ui.inside(var1, var3, this.toggleX(var6), this.toggleY(var6), this.toggleW(), 28)) {
            ((donutauction.AuctionScreen.Toggle)var5.get(var6)).flip().run();
            donutauction.Config.save();
            donutauction.Ui.sound(((donutauction.AuctionScreen.Toggle)var5.get(var6)).get().getAsBoolean() ? "toggle_on" : "toggle_off", 1.0F);
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
      } else if (page == donutauction.AuctionScreen.Page.THEME) {
         int var12 = Math.max(0, this.themeContentH() - (this.bottom() - this.top()));
         listScroll = Math.clamp(listScroll - Math.round(var7 * 16.0), 0, var12);
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
      NEW("New Auction", "plus"),
      PRESETS("Presets", "stack"),
      HISTORY("History", "clock"),
      SETTINGS("Settings", "gear"),
      THEME("Theme", "palette");

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
