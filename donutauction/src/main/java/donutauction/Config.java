package donutauction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public final class Config {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static donutauction.Config instance = new donutauction.Config();
   public static final String DEFAULT_PAYMENT_PATTERN = "^([A-Za-z0-9_.]{2,16}) paid you \\$([0-9][0-9.,]*\\s*[KkMmBbTt]?)\\.?$";
   public String apiKey = "";
   public boolean announce = true;
   public boolean announceBids = true;
   public boolean timeWarnings = true;
   public boolean antiSnipe = true;
   public boolean autoRefund = false;
   public String ruleText = "Pay over = I keep";
   public String paymentPattern = "^([A-Za-z0-9_.]{2,16}) paid you \\$([0-9][0-9.,]*\\s*[KkMmBbTt]?)\\.?$";
   public String msgStart = "[Auction] {qty}x {item} | Worth {worth} | Min {min} | {time}s | /pay {seller} <amount> to bid";
   public String msgBid = "[Auction] Top bid: {bid} by {bidder}";
   public String msgWarn = "[Auction] {time}s left! Top bid {bid} by {bidder}";
   public String msgSold = "[Auction] SOLD {qty}x {item} to {bidder} for {bid}!";
   public String msgNone = "[Auction] {qty}x {item} ended with no bids.";
   public String msgCancel = "[Auction] Auction for {item} was cancelled.";
   public int accent = -7643914;
   public boolean smallText = true;
   public boolean showHud = true;
   public boolean hudTop = true;
   public List<donutauction.Preset> presets = new ArrayList<>();
   public List<donutauction.HistoryEntry> history = new ArrayList<>();

   public static donutauction.Config get() {
      return instance;
   }

   private static Path file() {
      return FabricLoader.getInstance().getConfigDir().resolve("donutauction.json");
   }

   public static void load() {
      Path var0 = file();
      if (Files.exists(var0)) {
         try (BufferedReader var1 = Files.newBufferedReader(var0)) {
            donutauction.Config var2 = (donutauction.Config)GSON.fromJson(var1, donutauction.Config.class);
            if (var2 != null) {
               if (var2.presets == null) {
                  var2.presets = new ArrayList<>();
               }

               if (var2.history == null) {
                  var2.history = new ArrayList<>();
               }

               if (var2.paymentPattern == null || var2.paymentPattern.isBlank()) {
                  var2.paymentPattern = "^([A-Za-z0-9_.]{2,16}) paid you \\$([0-9][0-9.,]*\\s*[KkMmBbTt]?)\\.?$";
               }

               instance = var2;
            }
         } catch (Exception var6) {
            donutauction.DonutAuctionClient.LOGGER.warn("Could not read Donut Auction config", var6);
         }
      }
   }

   public static void save() {
      try (BufferedWriter var0 = Files.newBufferedWriter(file())) {
         GSON.toJson(instance, var0);
      } catch (Exception var5) {
         donutauction.DonutAuctionClient.LOGGER.warn("Could not save Donut Auction config", var5);
      }
   }
}
