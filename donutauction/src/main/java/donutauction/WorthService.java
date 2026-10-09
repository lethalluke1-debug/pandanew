package donutauction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Worth of one item from donut.auction, the public DonutSMP price tracker. It needs no API key:
 * GET https://api.donut.auction/v2/items/search?q=netherite_block returns matching items with a market value.
 * Results are cached for 10 minutes.
 */
public final class WorthService {
    private static final String SEARCH = "https://api.donut.auction/v2/items/search?q=";
    private static final long CACHE_MS = 10 * 60 * 1000;
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public enum Status { LOADING, OK, NOT_FOUND, ERROR }

    public record Result(Status status, double each, String detail, long at) {}

    private static final Map<String, Result> CACHE = new ConcurrentHashMap<>();

    private WorthService() {}

    /** Current result for an item id like "minecraft:elytra", starting a lookup if needed. */
    public static Result get(String itemId) {
        Result r = CACHE.get(itemId);
        long now = System.currentTimeMillis();
        if (r != null && (r.status() == Status.LOADING || now - r.at() < CACHE_MS)) return r;
        // Failed lookups are retried after 30 seconds rather than 10 minutes.
        if (r != null && r.status() == Status.ERROR && now - r.at() < 30_000) return r;
        Result loading = new Result(Status.LOADING, 0, "Checking prices...", now);
        CACHE.put(itemId, loading);
        Thread.ofVirtual().start(() -> CACHE.put(itemId, fetch(itemId)));
        return loading;
    }

    private static Result fetch(String itemId) {
        long now = System.currentTimeMillis();
        String name = itemId.contains(":") ? itemId.substring(itemId.indexOf(':') + 1) : itemId;
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(SEARCH + URLEncoder.encode(name, StandardCharsets.UTF_8)))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", "DonutAuction/1.0 (Fabric mod by Lethal)")
                    .GET()
                    .build();
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 429) return new Result(Status.ERROR, 0, "Price site busy, retrying", now);
            if (resp.statusCode() / 100 != 2) return new Result(Status.ERROR, 0, "Price site error " + resp.statusCode(), now);

            return parse(name, resp.body(), now);
        } catch (Exception e) {
            return new Result(Status.ERROR, 0, "Couldn't reach price site", now);
        }
    }

    /** Picks the market value of {@code name} (e.g. "elytra") out of a /v2/items/search response. */
    static Result parse(String name, String json, long now) {
        JsonElement root = JsonParser.parseString(json);
        JsonArray items = root.isJsonObject() && root.getAsJsonObject().has("items")
                && root.getAsJsonObject().get("items").isJsonArray() ? root.getAsJsonObject().getAsJsonArray("items") : null;
        if (items == null) return new Result(Status.NOT_FOUND, 0, "No price data", now);

        // Exact item, preferably the plain (unenchanted) one.
        double plain = -1, any = -1;
        for (JsonElement el : items) {
            if (!el.isJsonObject()) continue;
            JsonObject entry = el.getAsJsonObject();
            JsonObject item = obj(entry, "item");
            if (item == null || !item.has("itemName")) continue;
            if (!item.get("itemName").getAsString().toLowerCase(Locale.ROOT).equals(name)) continue;
            JsonObject price = obj(entry, "price");
            if (price == null || !price.has("value") || price.get("value").isJsonNull()) continue;
            double value = price.get("value").getAsDouble();
            if (value <= 0) continue;
            boolean enchanted = item.has("enchantments") && item.get("enchantments").isJsonArray()
                    && !item.getAsJsonArray("enchantments").isEmpty();
            if (!enchanted && plain < 0) plain = value;
            if (any < 0) any = value;
        }
        double value = plain > 0 ? plain : any;
        if (value <= 0) return new Result(Status.NOT_FOUND, 0, "No recent sales tracked", now);
        return new Result(Status.OK, value, "Market value", now);
    }

    private static JsonObject obj(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonObject() ? o.getAsJsonObject(key) : null;
    }
}
