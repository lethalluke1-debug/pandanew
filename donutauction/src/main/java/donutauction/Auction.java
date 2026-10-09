package donutauction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class Auction {
   private static final int ANTI_SNIPE_SEC = 10;
   private static final long RESULT_SHOW_MS = 6000L;
   private static donutauction.Auction current;
   private static donutauction.Auction last;
   private static long lastEndedAt;
   private static Pattern pattern;
   private static String patternSource;
   public final String itemId;
   public final int quantity;
   public final double minBid;
   public final double worthEach;
   public final int durationSec;
   public final String seller;
   private final long startedAt;
   private long endsAt;
   private final Map<String, Double> totals = new LinkedHashMap<>();
   private String topBidder;
   private double topBid;
   private boolean warned;
   private long lastBidAnnounce;
   private boolean cancelled;

   private Auction(String var1, int var2, double var3, double var5, int var7, String var8) {
      this.itemId = var1;
      this.quantity = var2;
      this.minBid = var3;
      this.worthEach = var5;
      this.durationSec = var7;
      this.seller = var8;
      this.startedAt = System.currentTimeMillis();
      this.endsAt = this.startedAt + var7 * 1000L;
   }

   public static donutauction.Auction current() {
      return current;
   }

   public static donutauction.Auction recent() {
      return last != null && System.currentTimeMillis() - lastEndedAt < 6000L ? last : null;
   }

   public static boolean running() {
      return current != null;
   }

   public static void start(String var0, int var1, double var2, double var4, int var6) {
      if (current == null) {
         Minecraft var7 = Minecraft.getInstance();
         current = new donutauction.Auction(var0, Math.max(1, var1), Math.max(0.0, var2), Math.max(0.0, var4), Math.max(5, var6), var7.getUser().getName());
         current.say(donutauction.Config.get().msgStart);
      }
   }

   public String itemName() {
      return this.stack().getHoverName().getString();
   }

   public ItemStack stack() {
      return stackOf(this.itemId);
   }

   public double worthTotal() {
      return this.worthEach * this.quantity;
   }

   public String topBidder() {
      return this.topBidder;
   }

   public double topBid() {
      return this.topBid;
   }

   public int bidders() {
      return this.totals.size();
   }

   public long remainingMs() {
      return Math.max(0L, this.endsAt - System.currentTimeMillis());
   }

   public float progress() {
      long var1 = Math.max(1L, this.endsAt - this.startedAt);
      return Math.clamp((float)this.remainingMs() / (float)var1, 0.0F, 1.0F);
   }

   public boolean wasCancelled() {
      return this.cancelled;
   }

   public static ItemStack stackOf(String var0) {
      Identifier var1 = Identifier.tryParse(var0);
      Item var2 = var1 == null ? null : (Item)BuiltInRegistries.ITEM.getOptional(var1).orElse(null);
      return var2 == null ? ItemStack.EMPTY : new ItemStack(var2);
   }

   public static void onGameMessage(String var0) {
      if (current != null) {
         Matcher var1 = pattern().matcher(var0.strip());
         if (var1.matches()) {
            double var2 = donutauction.Money.parse(var1.group(2));
            if (!(var2 <= 0.0)) {
               current.onPayment(var1.group(1), var2);
            }
         }
      }
   }

   private static Pattern pattern() {
      String var0 = donutauction.Config.get().paymentPattern;
      if (pattern == null || !var0.equals(patternSource)) {
         try {
            pattern = Pattern.compile(var0);
         } catch (Exception var2) {
            pattern = Pattern.compile("^([A-Za-z0-9_.]{2,16}) paid you \\$([0-9][0-9.,]*\\s*[KkMmBbTt]?)\\.?$");
         }

         patternSource = var0;
      }

      return pattern;
   }

   private void onPayment(String var1, double var2) {
      if (!var1.equalsIgnoreCase(this.seller)) {
         this.totals.merge(var1, var2, Double::sum);
         String var4 = this.topBidder;
         this.recomputeTop();
         if (this.topBidder != null && (!this.topBidder.equals(var4) || !(this.totals.get(var1) < this.topBid))) {
            donutauction.Config var5 = donutauction.Config.get();
            if (var5.antiSnipe && this.remainingMs() < 10000L) {
               this.endsAt = System.currentTimeMillis() + 10000L;
               this.warned = true;
            }

            if (var4 != null && !var4.equals(this.topBidder) && var5.autoRefund) {
               this.refund(var4);
            }

            if (var5.announceBids && System.currentTimeMillis() - this.lastBidAnnounce > 2500L) {
               this.lastBidAnnounce = System.currentTimeMillis();
               this.say(var5.msgBid);
            }
         }
      }
   }

   private void recomputeTop() {
      this.topBidder = null;
      this.topBid = 0.0;

      for (Entry var2 : this.totals.entrySet()) {
         if ((Double)var2.getValue() >= this.minBid && (Double)var2.getValue() > this.topBid) {
            this.topBid = (Double)var2.getValue();
            this.topBidder = (String)var2.getKey();
         }
      }
   }

   private void refund(String var1) {
      Double var2 = this.totals.remove(var1);
      if (var2 != null && var2 > 0.0) {
         donutauction.DonutAuctionClient.queueCommand("pay " + var1 + " " + donutauction.Money.plain(var2));
      }

      this.recomputeTop();
   }

   public static void tick() {
      donutauction.Auction var0 = current;
      if (var0 != null) {
         donutauction.Config var1 = donutauction.Config.get();
         if (var1.timeWarnings && !var0.warned && var0.remainingMs() <= 10000L && var0.durationSec > 15) {
            var0.warned = true;
            var0.say(var1.msgWarn);
         }

         if (var0.remainingMs() <= 0L) {
            var0.finish(false);
         }
      }
   }

   public static void endNow() {
      if (current != null) {
         current.finish(false);
      }
   }

   public static void cancel() {
      if (current != null) {
         current.finish(true);
      }
   }

   private void finish(boolean var1) {
      donutauction.Config var2 = donutauction.Config.get();
      this.cancelled = var1;
      if (var1) {
         this.topBidder = null;
         this.say(var2.msgCancel);
      } else {
         this.say(this.topBidder != null ? var2.msgSold : var2.msgNone);
      }

      if (var2.autoRefund) {
         for (String var4 : new ArrayList<>(this.totals.keySet())) {
            if (!var4.equals(this.topBidder)) {
               this.refund(var4);
            }
         }
      }

      donutauction.HistoryEntry var5 = new donutauction.HistoryEntry();
      var5.itemId = this.itemId;
      var5.quantity = this.quantity;
      var5.winner = var1 ? null : this.topBidder;
      var5.price = var1 ? 0.0 : this.topBid;
      var5.worthTotal = this.worthTotal();
      var5.endedAt = System.currentTimeMillis();
      var5.cancelled = var1;
      List var6 = var2.history;
      var6.addFirst(var5);

      while (var6.size() > 50) {
         var6.removeLast();
      }

      donutauction.Config.save();
      last = this;
      lastEndedAt = System.currentTimeMillis();
      current = null;
   }

   private void say(String var1) {
      if (donutauction.Config.get().announce && var1 != null && !var1.isBlank()) {
         donutauction.DonutAuctionClient.queueChat(this.fill(var1));
      }
   }

   private String fill(String var1) {
      return var1.replace("{qty}", Integer.toString(this.quantity))
         .replace("{item}", this.itemName())
         .replace("{worth}", this.worthEach > 0.0 ? donutauction.Money.format(this.worthTotal()) : "?")
         .replace("{min}", this.minBid > 0.0 ? donutauction.Money.format(this.minBid) : "none")
         .replace("{time}", Long.toString((this.remainingMs() + 999L) / 1000L))
         .replace("{bid}", this.topBidder != null ? donutauction.Money.format(this.topBid) : "-")
         .replace("{bidder}", this.topBidder != null ? this.topBidder : "nobody")
         .replace("{seller}", this.seller)
         .replace("{rule}", donutauction.Config.get().ruleText);
   }
}
