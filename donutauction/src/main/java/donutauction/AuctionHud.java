package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class AuctionHud {
   private static final int W = 230;
   private static final int H = 34;

   private AuctionHud() {
   }

   public static void extractRenderState(GuiGraphicsExtractor var0, DeltaTracker var1) {
      Minecraft var2 = Minecraft.getInstance();
      donutauction.Config var3 = donutauction.Config.get();
      if (var3.showHud && var2.player != null) {
         donutauction.Auction var4 = donutauction.Auction.current();
         donutauction.Auction var5 = var4 == null ? donutauction.Auction.recent() : null;
         if (var4 != null || var5 != null) {
            donutauction.Auction var6 = var4 != null ? var4 : var5;
            Font var7 = var2.font;
            int var8 = var2.getWindow().getGuiScaledWidth();
            int var9 = var2.getWindow().getGuiScaledHeight();
            int var10 = (var8 - 230) / 2;
            int var11 = var3.hudTop ? 6 : var9 - 34 - 52;
            int var12 = donutauction.Ui.accent();
            boolean var13 = var4 != null && var6.remainingMs() <= 10000L;
            int var14 = var13 ? -1035969 : var12;
            donutauction.Ui.round(var0, var10 - 2, var11 - 2, 234, 38, 8, donutauction.Ui.alpha(var14, 48));
            donutauction.Ui.box(var0, var10, var11, 230, 34, 6, -334688746, donutauction.Ui.alpha(var14, 144));
            var0.fill(var10 + 1, var11 + 6, var10 + 3, var11 + 34 - 6, var14);
            donutauction.Ui.box(var0, var10 + 8, var11 + 5, 24, 24, 5, -15855849, -14013637);
            var0.item(var6.stack(), var10 + 12, var11 + 9);
            if (var6.quantity > 1) {
               String var15 = "x" + var6.quantity;
               donutauction.Ui.text(
                  var0, var7, Component.literal(var15).withStyle(ChatFormatting.BOLD), var10 + 31 - donutauction.Ui.fw(var7, var15) * 0.6F, var11 + 23, -723720, 0.6F
               );
            }

            int var27 = var10 + 38;
            String var16 = var4 != null ? "LIVE AUCTION" : (var6.wasCancelled() ? "CANCELLED" : (var6.topBidder() != null ? "SOLD" : "ENDED"));
            donutauction.Ui.text(var0, var7, Component.literal(var16).withStyle(ChatFormatting.BOLD), var27, var11 + 5, var4 != null ? var14 : -6513229, 0.6F);
            float var17 = donutauction.Ui.fw(var7, Component.literal(var16).withStyle(ChatFormatting.BOLD)) * 0.6F;
            donutauction.Ui.text(
               var0, var7, Component.literal(donutauction.Ui.ellipsize(var7, var6.itemName(), 90)), var27 + var17 + 4.0F, var11 + 5, -6513229, 0.6F
            );
            String var18 = var6.topBidder() != null ? "$" + donutauction.Money.format(var6.topBid()) : (var4 != null ? "No bids yet" : "No bids");
            donutauction.Ui.text(
               var0, var7, Component.literal(var18).withStyle(ChatFormatting.BOLD), var27, var11 + 12, var6.topBidder() != null ? -723720 : -6513229, 1.0F
            );
            float var19 = donutauction.Ui.fw(var7, Component.literal(var18).withStyle(ChatFormatting.BOLD));
            if (var6.topBidder() != null) {
               donutauction.Ui.text(var0, var7, Component.literal("by " + var6.topBidder()), var27 + var19 + 4.0F, var11 + 13.5F, var12, 0.7F);
            }

            String var20 = (var6.minBid > 0.0 ? "Min " + donutauction.Money.format(var6.minBid) : "No minimum")
               + (var6.worthEach > 0.0 ? "  •  Worth " + donutauction.Money.format(var6.worthTotal()) : "")
               + (var3.ruleText.isBlank() ? "" : "  •  " + var3.ruleText);
            donutauction.Ui.text(var0, var7, Component.literal(var20), var27, var11 + 24, -10526344, 0.6F);
            String var21 = var4 != null ? donutauction.Ui.clock(var6.remainingMs()) : "DONE";
            int var22 = Math.round(donutauction.Ui.fw(var7, var21) * 0.75F) + 10;
            donutauction.Ui.box(
               var0,
               var10 + 230 - var22 - 8,
               var11 + 6,
               var22,
               12,
               6,
               var13 ? donutauction.Ui.alpha(-1035969, 64) : donutauction.Ui.alpha(var12, 48),
               donutauction.Ui.alpha(var14, 160)
            );
            donutauction.Ui.text(var0, var7, Component.literal(var21).withStyle(ChatFormatting.BOLD), var10 + 230 - var22 - 3, var11 + 8.5F, -723720, 0.75F);
            if (var4 != null && var6.bidders() > 0) {
               String var23 = var6.bidders() + (var6.bidders() == 1 ? " bidder" : " bidders");
               donutauction.Ui.text(var0, var7, Component.literal(var23), var10 + 230 - 8 - donutauction.Ui.fw(var7, var23) * 0.6F, var11 + 21, -10526344, 0.6F);
            }

            int var28 = var10 + 8;
            short var24 = 214;
            int var25 = var11 + 34 - 3;
            var0.fill(var28, var25, var28 + var24, var25 + 1, donutauction.Ui.alpha(-14013637, 255));
            if (var4 != null) {
               var0.fill(var28, var25, var28 + Math.round(var24 * var6.progress()), var25 + 1, var14);
            }

            String var26 = "Donut Auction • by Lethal";
            donutauction.Ui.text(
               var0,
               var7,
               Component.literal(var26),
               var10 + (230.0F - donutauction.Ui.fw(var7, var26) * 0.5F) / 2.0F + 0.0F,
               var11 + 34 + 3,
               donutauction.Ui.alpha(-6513229, 144),
               0.5F
            );
         }
      }
   }
}
