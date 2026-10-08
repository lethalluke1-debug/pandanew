package donutauction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

/** Everything saved to config/donutauction.json: settings, theme, presets and history. */
public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config instance = new Config();

    /** DonutSMP payment message, e.g. "Steve paid you $2.5M." Group 1 = player, group 2 = amount. */
    public static final String DEFAULT_PAYMENT_PATTERN =
            "^([A-Za-z0-9_.]{2,16}) paid you \\$([0-9][0-9.,]*\\s*[KkMmBbTt]?)\\.?$";

    // General
    public String apiKey = "";
    public boolean announce = true;
    public boolean announceBids = true;
    public boolean timeWarnings = true;
    public boolean antiSnipe = true;
    public boolean autoRefund = false;
    public String ruleText = "Pay over = I keep";
    public String paymentPattern = DEFAULT_PAYMENT_PATTERN;
    public String msgStart = "[Auction] {qty}x {item} | Worth {worth} | Min {min} | {time}s | /pay {seller} <amount> to bid";
    public String msgBid = "[Auction] Top bid: {bid} by {bidder}";
    public String msgWarn = "[Auction] {time}s left! Top bid {bid} by {bidder}";
    public String msgSold = "[Auction] SOLD {qty}x {item} to {bidder} for {bid}!";
    public String msgNone = "[Auction] {qty}x {item} ended with no bids.";
    public String msgCancel = "[Auction] Auction for {item} was cancelled.";

    // Theme
    public int accent = 0xFF8B5CF6;
    public boolean smallText = true;
    public boolean showHud = true;
    public boolean hudTop = true;

    public List<Preset> presets = new ArrayList<>();
    public List<HistoryEntry> history = new ArrayList<>();

    public static Config get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("donutauction.json");
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) return;
        try (Reader r = Files.newBufferedReader(file)) {
            Config c = GSON.fromJson(r, Config.class);
            if (c != null) {
                if (c.presets == null) c.presets = new ArrayList<>();
                if (c.history == null) c.history = new ArrayList<>();
                if (c.paymentPattern == null || c.paymentPattern.isBlank()) c.paymentPattern = DEFAULT_PAYMENT_PATTERN;
                instance = c;
            }
        } catch (Exception e) {
            DonutAuctionClient.LOGGER.warn("Could not read Donut Auction config", e);
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(instance, w);
        } catch (Exception e) {
            DonutAuctionClient.LOGGER.warn("Could not save Donut Auction config", e);
        }
    }
}
