package donutauction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WorthService {
   private static final String BASE = "https://api.donutsmp.net/v1/auction/list/";
   private static final long CACHE_MS = 600000L;
   private static final int PAGES = 3;
   private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
   private static final Map<String, donutauction.WorthService.Result> CACHE = new ConcurrentHashMap<>();

   private WorthService() {
   }

   public static donutauction.WorthService.Result get(String var0) {
      String var1 = donutauction.Config.get().apiKey.trim();
      if (var1.isEmpty()) {
         return new donutauction.WorthService.Result(donutauction.WorthService.Status.NO_KEY, 0.0, "Add your /api key in Settings", 0L);
      } else {
         donutauction.WorthService.Result var2 = CACHE.get(var0);
         long var3 = System.currentTimeMillis();
         if (var2 == null || var2.status() != donutauction.WorthService.Status.LOADING && var3 - var2.at() >= 600000L) {
            donutauction.WorthService.Result var5 = new donutauction.WorthService.Result(
               donutauction.WorthService.Status.LOADING, 0.0, "Checking auction house...", var3
            );
            CACHE.put(var0, var5);
            Thread.ofVirtual().start(() -> CACHE.put(var0, fetch(var0, var1)));
            return var5;
         } else {
            return var2;
         }
      }
   }

   public static void clear() {
      CACHE.clear();
   }

   private static donutauction.WorthService.Result fetch(String var0, String var1) {
      long var2 = System.currentTimeMillis();
      String var4 = var0.contains(":") ? var0.substring(var0.indexOf(58) + 1) : var0;
      String var5 = var4.replace('_', ' ');
      double var6 = Double.MAX_VALUE;

      try {
         for (int var8 = 1; var8 <= 3; var8++) {
            String var9 = "{\"search\":\"" + var5.replace("\"", "") + "\",\"sort\":\"lowest_price\"}";
            HttpRequest var10 = HttpRequest.newBuilder(URI.create("https://api.donutsmp.net/v1/auction/list/" + var8))
               .timeout(Duration.ofSeconds(10L))
               .header("Authorization", "Bearer " + var1)
               .header("Content-Type", "application/json")
               .method("GET", BodyPublishers.ofString(var9))
               .build();
            HttpResponse var11 = HTTP.send(var10, BodyHandlers.ofString());
            if (var11.statusCode() == 401 || var11.statusCode() == 403) {
               return new donutauction.WorthService.Result(donutauction.WorthService.Status.ERROR, 0.0, "API key rejected (" + var11.statusCode() + ")", var2);
            }

            if (var11.statusCode() == 429) {
               return new donutauction.WorthService.Result(donutauction.WorthService.Status.ERROR, 0.0, "Rate limited, try again soon", var2);
            }

            if (var11.statusCode() / 100 != 2) {
               if (var8 == 1) {
                  return new donutauction.WorthService.Result(donutauction.WorthService.Status.ERROR, 0.0, "Auction API error " + var11.statusCode(), var2);
               }
               break;
            }

            JsonArray var12 = results(JsonParser.parseString((String)var11.body()));
            if (var12 == null || var12.isEmpty()) {
               break;
            }

            int var13 = 0;

            for (JsonElement var15 : var12) {
               if (var15 != null && var15.isJsonObject()) {
                  var13++;
                  JsonObject var16 = var15.getAsJsonObject();
                  JsonObject var17 = var16.has("item") && var16.get("item").isJsonObject() ? var16.getAsJsonObject("item") : null;
                  if (var17 != null && var17.has("id")) {
                     String var18 = var17.get("id").getAsString().toLowerCase(Locale.ROOT);
                     if ((var18.equals(var0) || var18.equals(var4)) && var16.has("price")) {
                        double var19 = var16.get("price").getAsDouble();
                        int var21 = var17.has("count") ? Math.max(1, var17.get("count").getAsInt()) : 1;
                        var6 = Math.min(var6, var19 / var21);
                     }
                  }
               }
            }

            if (var6 != Double.MAX_VALUE || var13 == 0) {
               break;
            }
         }
      } catch (Exception var22) {
         return new donutauction.WorthService.Result(donutauction.WorthService.Status.ERROR, 0.0, "Couldn't reach the auction API", var2);
      }

      return var6 == Double.MAX_VALUE
         ? new donutauction.WorthService.Result(donutauction.WorthService.Status.NOT_FOUND, 0.0, "Not listed on /ah right now", var2)
         : new donutauction.WorthService.Result(donutauction.WorthService.Status.OK, var6, "Lowest on /ah", var2);
   }

   private static JsonArray results(JsonElement var0) {
      if (var0.isJsonArray()) {
         return var0.getAsJsonArray();
      } else if (!var0.isJsonObject()) {
         return null;
      } else {
         JsonObject var1 = var0.getAsJsonObject();

         for (String var5 : new String[]{"result", "auctions", "data"}) {
            if (var1.has(var5) && var1.get(var5).isJsonArray()) {
               return var1.getAsJsonArray(var5);
            }
         }

         return null;
      }
   }

   public record Result(donutauction.WorthService.Status status, double each, String detail, long at) {
   }

   public static enum Status {
      LOADING,
      OK,
      NOT_FOUND,
      NO_KEY,
      ERROR;
   }
}
