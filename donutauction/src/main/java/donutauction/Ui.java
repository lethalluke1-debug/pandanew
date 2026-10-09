package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class Ui {
   public static final int WINDOW = -233959657;
   public static final int SIDEBAR = -15526882;
   public static final int CARD = -15132122;
   public static final int CARD_HOVER = -14671569;
   public static final int FIELD = -15855849;
   public static final int BORDER = -14013637;
   public static final int TEXT = -723720;
   public static final int MUTED = -6513229;
   public static final int FAINT = -10526344;
   public static final int GOLD = -670639;
   public static final int GREEN = -11870592;
   public static final int RED = -1035969;
   public static final int TRACK_OFF = -13882051;
   public static final int KNOB_OFF = -9210484;
   public static final String[] PALETTE_NAMES = new String[]{
      "Orchid", "Ember", "Glacier", "Lagoon", "Citrus", "Sakura", "Midnight", "Ruby", "Neon", "Honey", "Mint", "Frost"
   };
   public static final int[][] PALETTES = new int[][]{
      {0xFF8B5CF6, 0xFFC4A1FF, 0xFF6D28D9, 0xFF140E24},
      {0xFFFF5A36, 0xFFFF9A3C, 0xFFD6264A, 0xFF24100C},
      {0xFF4FC3F7, 0xFFA5E8FF, 0xFF2F7BEA, 0xFF0D1824},
      {0xFF14D3B4, 0xFF5EF0D0, 0xFF0E9C8C, 0xFF0A1F1C},
      {0xFFC6F432, 0xFFF2E85C, 0xFF7BDB3A, 0xFF171F0A},
      {0xFFFF7AB6, 0xFFFFC1DC, 0xFFE0479A, 0xFF241019},
      {0xFF6C7BFF, 0xFF9AA6FF, 0xFF3D3FD1, 0xFF10122A},
      {0xFFE8304A, 0xFFFF6B6B, 0xFFA3162E, 0xFF220A0E},
      {0xFFFF2BD6, 0xFF7B2BFF, 0xFF2BE0FF, 0xFF140A22},
      {0xFFFFB020, 0xFFFFD66B, 0xFFFF8A00, 0xFF221706},
      {0xFF3DFFA8, 0xFFB6FFD9, 0xFF18C47A, 0xFF0A1E14},
      {0xFFE6ECFF, 0xFF9FB4FF, 0xFF6F86E8, 0xFF12141F}
   };
   private static final Identifier CIRCLE = id("textures/gui/circle.png");
   private static final Identifier GLOW = id("textures/gui/glow.png");

   public static Identifier id(String var0) {
      return Identifier.fromNamespaceAndPath("donutauction", var0);
   }

   public static void tex(GuiGraphicsExtractor var0, Identifier var1, int var2, int var3, int var4, int var5, int var6) {
      var0.blit(RenderPipelines.GUI_TEXTURED, var1, var2, var3, 0.0F, 0.0F, var4, var5, 1, 1, 1, 1, var6);
   }

   public static void icon(GuiGraphicsExtractor var0, String var1, int var2, int var3, int var4, int var5) {
      tex(var0, id("textures/gui/icon_" + var1 + ".png"), var2, var3, var4, var4, var5);
   }

   public static void glow(GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5) {
      tex(var0, GLOW, var1, var2, var3, var4, var5);
   }

   public static void sound(String var0, float var1) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(id(var0)), var1, 0.45F));
   }

   public static int paletteIndex() {
      int var0 = accent();

      for (int var1 = 0; var1 < PALETTES.length; var1++) {
         if (PALETTES[var1][0] == var0) {
            return var1;
         }
      }

      return -1;
   }

   public static final FontDescription UI_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath("donutauction", "ui"));
   public static final FontDescription UI_FONT_BOLD = new FontDescription.Resource(Identifier.fromNamespaceAndPath("donutauction", "ui_bold"));

   private Ui() {
   }

   public static Component styled(Component var0) {
      MutableComponent var1 = var0.copy();
      boolean var2 = var1.getStyle().isBold();
      return var1.withStyle(var1x -> var1x.withBold(false).withFont(var2 ? UI_FONT_BOLD : UI_FONT));
   }

   public static int fw(Font var0, String var1) {
      return var0.width(styled(Component.literal(var1)));
   }

   public static int fw(Font var0, Component var1) {
      return var0.width(styled(var1));
   }

   private static int mix(int var0, int var1, float var2) {
      int var3 = (int)((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = (int)((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = (int)((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var0 & 0xFF000000 | var3 << 16 | var4 << 8 | var5;
   }

   private static int glass(int var0, float var1, int var2) {
      int var3 = mix(var0, accent(), var1);
      return donutauction.Config.get().seeThroughGui ? alpha(var3, var2) : var3 | 0xFF000000;
   }

   public static int card() {
      return glass(-15132122, 0.1F, 205);
   }

   public static int cardHover() {
      return glass(-14671569, 0.16F, 225);
   }

   public static int field() {
      return glass(-15855849, 0.06F, 215);
   }

   public static int border() {
      return glass(-14013637, 0.22F, 170);
   }

   public static int accent() {
      return donutauction.Config.get().accent | 0xFF000000;
   }

   public static int windowBg() {
      return glass(-233959657, 0.14F, 185);
   }

   public static int panelBg() {
      return glass(-15526882, 0.1F, 190);
   }

   public static void bigToggle(GuiGraphicsExtractor var0, int var1, int var2, boolean var3) {
      round(var0, var1, var2, 24, 12, 6, var3 ? accent() : border());
      round(var0, var3 ? var1 + 14 : var1 + 2, var2 + 2, 8, 8, 4, var3 ? -1 : -9210484);
   }

   public static int alpha(int var0, int var1) {
      return var0 & 16777215 | var1 << 24;
   }

   public static float scale() {
      return donutauction.Config.get().smallText ? 0.75F : 1.0F;
   }

   public static void text(GuiGraphicsExtractor var0, Font var1, String var2, float var3, float var4, int var5) {
      text(var0, var1, Component.literal(var2), var3, var4, var5, scale());
   }

   public static void bold(GuiGraphicsExtractor var0, Font var1, String var2, float var3, float var4, int var5) {
      text(var0, var1, Component.literal(var2).withStyle(ChatFormatting.BOLD), var3, var4, var5, scale());
   }

   public static void text(GuiGraphicsExtractor var0, Font var1, Component var2, float var3, float var4, int var5, float var6) {
      var0.pose().pushMatrix();
      var0.pose().translate(var3, var4);
      var0.pose().scale(var6, var6);
      var0.text(var1, styled(var2), 0, 0, var5, false);
      var0.pose().popMatrix();
   }

   public static int width(Font var0, String var1) {
      return Math.round(fw(var0, var1) * scale());
   }

   public static int boldWidth(Font var0, String var1) {
      return Math.round(fw(var0, Component.literal(var1).withStyle(ChatFormatting.BOLD)) * scale());
   }

   public static int lineH(Font var0) {
      return Math.round(9.0F * scale());
   }

   public static String ellipsize(Font var0, String var1, int var2) {
      if (width(var0, var1) <= var2) {
         return var1;
      } else {
         int var3 = var1.length();

         while (var3 > 0 && width(var0, var1.substring(0, var3) + "...") > var2) {
            var3--;
         }

         return var1.substring(0, var3) + "...";
      }
   }

   public static void round(GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         var5 = Math.min(var5, Math.min(var3, var4) / 2);
         if (var5 <= 0) {
            var0.fill(var1, var2, var1 + var3, var2 + var4, var6);
         } else {
            var0.fill(var1 + var5, var2, var1 + var3 - var5, var2 + var4, var6);
            var0.fill(var1, var2 + var5, var1 + var5, var2 + var4 - var5, var6);
            var0.fill(var1 + var3 - var5, var2 + var5, var1 + var3, var2 + var4 - var5, var6);
            var0.blit(RenderPipelines.GUI_TEXTURED, CIRCLE, var1, var2, 0.0F, 0.0F, var5, var5, 128, 128, 256, 256, var6);
            var0.blit(RenderPipelines.GUI_TEXTURED, CIRCLE, var1 + var3 - var5, var2, 128.0F, 0.0F, var5, var5, 128, 128, 256, 256, var6);
            var0.blit(RenderPipelines.GUI_TEXTURED, CIRCLE, var1, var2 + var4 - var5, 0.0F, 128.0F, var5, var5, 128, 128, 256, 256, var6);
            var0.blit(RenderPipelines.GUI_TEXTURED, CIRCLE, var1 + var3 - var5, var2 + var4 - var5, 128.0F, 128.0F, var5, var5, 128, 128, 256, 256, var6);
         }
      }
   }

   public static void box(GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      round(var0, var1 - 1, var2 - 1, var3 + 2, var4 + 2, var5 + 1, var7);
      round(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void toggle(GuiGraphicsExtractor var0, int var1, int var2, boolean var3) {
      round(var0, var1, var2, 18, 9, 4, var3 ? accent() : border());
      round(var0, var3 ? var1 + 10 : var1 + 1, var2 + 1, 7, 7, 4, var3 ? -1 : -9210484);
   }

   public static void button(
      GuiGraphicsExtractor var0, Font var1, int var2, int var3, int var4, int var5, String var6, boolean var7, boolean var8, boolean var9
   ) {
      int var10 = !var9 ? card() : (var8 ? (var7 ? alpha(accent(), 255) : alpha(accent(), 208)) : (var7 ? cardHover() : card()));
      box(var0, var2, var3, var4, var5, 4, var10, var8 && var9 ? alpha(accent(), 255) : border());
      int var11 = !var9 ? -10526344 : (var8 ? -723720 : (var7 ? -723720 : -6513229));
      text(var0, var1, var6, var2 + (var4 - width(var1, var6)) / 2.0F, var3 + (var5 - lineH(var1)) / 2.0F + 0.5F, var11);
   }

   public static boolean inside(double var0, double var2, int var4, int var5, int var6, int var7) {
      return var0 >= var4 && var0 < var4 + var6 && var2 >= var5 && var2 < var5 + var7;
   }

   public static String clock(long var0) {
      long var2 = (var0 + 999L) / 1000L;
      return String.format("%02d:%02d", var2 / 60L, var2 % 60L);
   }
}
