package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

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
   public static final String[] PALETTE_NAMES = new String[]{"Orchid", "Ember", "Glacier", "Lagoon", "Citrus", "Sakura", "Midnight", "Ruby"};
   public static final int[][] PALETTES = new int[][]{
      {0xFF8B5CF6, 0xFFC4A1FF, 0xFF6D28D9, 0xFF140E24},
      {0xFFFF5A36, 0xFFFF9A3C, 0xFFD6264A, 0xFF24100C},
      {0xFF4FC3F7, 0xFFA5E8FF, 0xFF2F7BEA, 0xFF0D1824},
      {0xFF14D3B4, 0xFF5EF0D0, 0xFF0E9C8C, 0xFF0A1F1C},
      {0xFFC6F432, 0xFFF2E85C, 0xFF7BDB3A, 0xFF171F0A},
      {0xFFFF7AB6, 0xFFFFC1DC, 0xFFE0479A, 0xFF241019},
      {0xFF6C7BFF, 0xFF9AA6FF, 0xFF3D3FD1, 0xFF10122A},
      {0xFFE8304A, 0xFFFF6B6B, 0xFFA3162E, 0xFF220A0E}
   };

   public static int paletteIndex() {
      int var0 = accent();

      for (int var1 = 0; var1 < PALETTES.length; var1++) {
         if (PALETTES[var1][0] == var0) {
            return var1;
         }
      }

      return -1;
   }

   private Ui() {
   }

   public static int accent() {
      return donutauction.Config.get().accent | 0xFF000000;
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
      var0.text(var1, var2, 0, 0, var5, false);
      var0.pose().popMatrix();
   }

   public static int width(Font var0, String var1) {
      return Math.round(var0.width(var1) * scale());
   }

   public static int boldWidth(Font var0, String var1) {
      return Math.round(var0.width(Component.literal(var1).withStyle(ChatFormatting.BOLD)) * scale());
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
         var0.fill(var1, var2 + var5, var1 + var3, var2 + var4 - var5, var6);

         for (int var7 = 0; var7 < var5; var7++) {
            double var8 = var5 - var7 - 0.5;
            int var10 = (int)Math.round(var5 - Math.sqrt(var5 * var5 - var8 * var8));
            var0.fill(var1 + var10, var2 + var7, var1 + var3 - var10, var2 + var7 + 1, var6);
            var0.fill(var1 + var10, var2 + var4 - 1 - var7, var1 + var3 - var10, var2 + var4 - var7, var6);
         }
      }
   }

   public static void box(GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      round(var0, var1 - 1, var2 - 1, var3 + 2, var4 + 2, var5 + 1, var7);
      round(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void toggle(GuiGraphicsExtractor var0, int var1, int var2, boolean var3) {
      round(var0, var1, var2, 18, 9, 4, var3 ? accent() : -13882051);
      round(var0, var3 ? var1 + 10 : var1 + 1, var2 + 1, 7, 7, 3, var3 ? -723720 : -9210484);
   }

   public static void button(
      GuiGraphicsExtractor var0, Font var1, int var2, int var3, int var4, int var5, String var6, boolean var7, boolean var8, boolean var9
   ) {
      int var10 = !var9 ? -15132122 : (var8 ? (var7 ? alpha(accent(), 255) : alpha(accent(), 208)) : (var7 ? -14671569 : -15132122));
      box(var0, var2, var3, var4, var5, 4, var10, var8 && var9 ? alpha(accent(), 255) : -14013637);
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
