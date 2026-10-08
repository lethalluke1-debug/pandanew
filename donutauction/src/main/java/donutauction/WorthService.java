package donutauction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Worth of one item = the cheapest current listing on the DonutSMP auction house, per item, from the public API
 * (GET https://api.donutsmp.net/v1/auction/list/{page}, key from /api in game). Results are cached for 10 minutes.
 */
public final class WorthService {
    private static final String BASE = "https://api.donutsmp.net/v1/auction/list/";
    private static final long CACHE_MS = 10 * 60 * 1000;
    private static final int PAGES = 3;
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

    public enum Status { LOADING, OK, NOT_FOUND, NO_KEY, ERROR }

    public record Result(Status status, double each, String detail, long at) {}

    private static final Map<String, Result> CACHE = new ConcurrentHashMap<>();

    private WorthService() {}

    /** Current result for an item id like "minecraft:elytra", starting a lookup if needed. */
    public static Result get(String itemId) {
        String key = Config.get().apiKey.trim();
        if (key.isEmpty()) return new Result(Status.NO_KEY, 0, "Add your /api key in Settings", 0);
        Result r = CACHE.get(itemId);
        long now = System.currentTimeMillis();
        if (r != null && (r.status() == Status.LOADING || now - r.at() < CACHE_MS)) return r;
        Result loading = new Result(Status.LOADING, 0, "Checking auction house...", now);
        CACHE.put(itemId, loading);
        Thread.ofVirtual().start(() -> CACHE.put(itemId, fetch(itemId, key)));
        return loading;
    }

    public static void clear() {
        CACHE.clear();
    }

    private static Result fetch(String itemId, String key) {
        long now = System.currentTimeMillis();
        String path = itemId.contains(":") ? itemId.substring(itemId.indexOf(':') + 1) : itemId;
        String search = path.replace('_', ' ');
        double best = Double.MAX_VALUE;
        try {
            for (int page = 1; page <= PAGES; page++) {
                String body = "{\"search\":\"" + search.replace("\"", "") + "\",\"sort\":\"lowest_price\"}";
                HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + page))
                        .timeout(Duration.ofSeconds(10))
                        .header("Authorization", "Bearer " + key)
                        .header("Content-Type", "application/json")
                        .method("GET", HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 401 || resp.statusCode() == 403) {
                    return new Result(Status.ERROR, 0, "API key rejected (" + resp.statusCode() + ")", now);
                }
                if (resp.statusCode() == 429) return new Result(Status.ERROR, 0, "Rate limited, try again soon", now);
                if (resp.statusCode() / 100 != 2) {
                    if (page == 1) return new Result(Status.ERROR, 0, "Auction API error " + resp.statusCode(), now);
                    break;
                }
                JsonArray list = results(JsonParser.parseString(resp.body()));
                if (list == null || list.isEmpty()) break;
                int real = 0;
                for (JsonElement el : list) {
                    if (el == null || !el.isJsonObject()) continue; // pages are padded with nulls
                    real++;
                    JsonObject entry = el.getAsJsonObject();
                    JsonObject item = entry.has("item") && entry.get("item").isJsonObject() ? entry.getAsJsonObject("item") : null;
                    if (item == null || !item.has("id")) continue;
                    String id = item.get("id").getAsString().toLowerCase(Locale.ROOT);
                    if (!id.equals(itemId) && !id.equals(path)) continue;
                    if (!entry.has("price")) continue;
                    double price = entry.get("price").getAsDouble();
                    int count = item.has("count") ? Math.max(1, item.get("count").getAsInt()) : 1;
                    best = Math.min(best, price / count);
                }
                if (best != Double.MAX_VALUE || real == 0) break;
            }
        } catch (Exception e) {
            return new Result(Status.ERROR, 0, "Couldn't reach the auction API", now);
        }
        if (best == Double.MAX_VALUE) return new Result(Status.NOT_FOUND, 0, "Not listed on /ah right now", now);
        return new Result(Status.OK, best, "Lowest on /ah", now);
    }

    private static JsonArray results(JsonElement root) {
        if (root.isJsonArray()) return root.getAsJsonArray();
        if (!root.isJsonObject()) return null;
        JsonObject o = root.getAsJsonObject();
        for (String k : new String[] {"result", "auctions", "data"}) {
            if (o.has(k) && o.get(k).isJsonArray()) return o.getAsJsonArray(k);
        }
        return null;
    }
}
