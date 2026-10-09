package donutauction;

import java.util.Locale;

public final class Money {
   private Money() {
   }

   public static double parse(String var0) {
      if (var0 == null) {
         return -1.0;
      } else {
         String var1 = var0.trim().replace(",", "").replace("$", "").replace(" ", "").toLowerCase(Locale.ROOT);
         if (var1.isEmpty()) {
            return 0.0;
         } else {
            double var2 = 1.0;
            char var4 = var1.charAt(var1.length() - 1);
            switch (var4) {
               case 'b':
                  var2 = 1.0E9;
                  break;
               case 'k':
                  var2 = 1000.0;
                  break;
               case 'm':
                  var2 = 1000000.0;
                  break;
               case 't':
                  var2 = 1.0E12;
            }

            if (var2 != 1.0) {
               var1 = var1.substring(0, var1.length() - 1);
            }

            try {
               double var5 = Double.parseDouble(var1) * var2;
               return !(var5 < 0.0) && !Double.isNaN(var5) && !Double.isInfinite(var5) ? var5 : -1.0;
            } catch (NumberFormatException var7) {
               return -1.0;
            }
         }
      }
   }

   public static String format(double var0) {
      if (var0 < 1000.0) {
         return trim(var0);
      } else {
         String[] var2 = new String[]{"K", "M", "B", "T"};

         int var3;
         for (var3 = -1; var0 >= 1000.0 && var3 < var2.length - 1; var3++) {
            var0 /= 1000.0;
         }

         return trim(var0) + var2[var3];
      }
   }

   public static String plain(double var0) {
      return String.format(Locale.ROOT, "%.0f", Math.floor(var0));
   }

   private static String trim(double var0) {
      String var2 = var0 >= 100.0
         ? String.format(Locale.ROOT, "%.0f", var0)
         : (var0 >= 10.0 ? String.format(Locale.ROOT, "%.1f", var0) : String.format(Locale.ROOT, "%.2f", var0));
      if (var2.contains(".")) {
         var2 = var2.replaceAll("0+$", "").replaceAll("\\.$", "");
      }

      return var2;
   }
}
