package donutauction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A running auction. Bids are payments: each "Name paid you $X" adds to that player's total, and the highest
 * total at or above the minimum wins when the timer runs out.
 */
public final class Auction {
    /** Bids in the last this-many seconds push the end back to this many seconds left. */
    private static final int ANTI_SNIPE_SEC = 10;
    private static final long RESULT_SHOW_MS = 6000;

    private static Auction current;
    private static Auction last;
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
    private long lastBidAt;
    private int lastTickSecond = -1;

    private Auction(String itemId, int quantity, double minBid, double worthEach, int durationSec, String seller) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.minBid = minBid;
        this.worthEach = worthEach;
        this.durationSec = durationSec;
        this.seller = seller;
        this.startedAt = System.currentTimeMillis();
        this.endsAt = startedAt + durationSec * 1000L;
    }

    // ---- state ----

    public static Auction current() {
        return current;
    }

    /** The auction that just ended, while its result is still shown. */
    public static Auction recent() {
        return last != null && System.currentTimeMillis() - lastEndedAt < RESULT_SHOW_MS ? last : null;
    }

    public static boolean running() {
        return current != null;
    }

    public static void start(String itemId, int quantity, double minBid, double worthEach, int durationSec) {
        if (current != null) return;
        Minecraft mc = Minecraft.getInstance();
        current = new Auction(itemId, Math.max(1, quantity), Math.max(0, minBid), Math.max(0, worthEach),
                Math.max(5, durationSec), mc.getUser().getName());
        current.say(Config.get().msgStart);
        Sounds.start();
    }

    // ---- getters for the HUD and menu ----

    public String itemName() {
        return stack().getHoverName().getString();
    }

    public ItemStack stack() {
        return stackOf(itemId);
    }

    public double worthTotal() {
        return worthEach * quantity;
    }

    public String topBidder() {
        return topBidder;
    }

    public double topBid() {
        return topBid;
    }

    public int bidders() {
        return totals.size();
    }

    public long remainingMs() {
        return Math.max(0, endsAt - System.currentTimeMillis());
    }

    public float progress() {
        long total = Math.max(1, endsAt - startedAt);
        return Math.clamp((float) remainingMs() / total, 0f, 1f);
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    public long startedAt() {
        return startedAt;
    }

    /** When the top bid last changed (0 = never), for the HUD's flash. */
    public long lastBidAt() {
        return lastBidAt;
    }

    public static long lastEndedAt() {
        return lastEndedAt;
    }

    public static long resultShowMs() {
        return RESULT_SHOW_MS;
    }

    public static ItemStack stackOf(String itemId) {
        Identifier id = Identifier.tryParse(itemId);
        Item item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    // ---- bids ----

    /** Called for every system chat line; picks out payments. */
    public static void onGameMessage(String text) {
        if (current == null) return;
        Matcher m = pattern().matcher(text.strip());
        if (!m.matches()) return;
        double amount = Money.parse(m.group(2));
        if (amount <= 0) return;
        current.onPayment(m.group(1), amount);
    }

    private static Pattern pattern() {
        String src = Config.get().paymentPattern;
        if (pattern == null || !src.equals(patternSource)) {
            try {
                pattern = Pattern.compile(src);
            } catch (Exception e) {
                pattern = Pattern.compile(Config.DEFAULT_PAYMENT_PATTERN);
            }
            patternSource = src;
        }
        return pattern;
    }

    private void onPayment(String player, double amount) {
        if (player.equalsIgnoreCase(seller)) return;
        totals.merge(player, amount, Double::sum);
        String previous = topBidder;
        recomputeTop();
        if (topBidder == null || topBidder.equals(previous) && totals.get(player) < topBid) return;

        Config c = Config.get();
        // Late bid: give everyone a few more seconds.
        if (c.antiSnipe && remainingMs() < ANTI_SNIPE_SEC * 1000L) {
            endsAt = System.currentTimeMillis() + ANTI_SNIPE_SEC * 1000L;
            warned = true;
        }
        lastBidAt = System.currentTimeMillis();
        Sounds.bid();
        if (previous != null && !previous.equals(topBidder) && c.autoRefund) refund(previous);
        if (c.announceBids && System.currentTimeMillis() - lastBidAnnounce > 2500) {
            lastBidAnnounce = System.currentTimeMillis();
            say(c.msgBid);
        }
    }

    private void recomputeTop() {
        topBidder = null;
        topBid = 0;
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getValue() >= minBid && e.getValue() > topBid) {
                topBid = e.getValue();
                topBidder = e.getKey();
            }
        }
    }

    private void refund(String player) {
        Double amount = totals.remove(player);
        if (amount != null && amount > 0) {
            DonutAuctionClient.queueCommand("pay " + player + " " + Money.plain(amount));
        }
        recomputeTop();
    }

    // ---- lifecycle ----

    public static void tick() {
        Auction a = current;
        if (a == null) return;
        Config c = Config.get();
        if (c.timeWarnings && !a.warned && a.remainingMs() <= 10_000 && a.durationSec > 15) {
            a.warned = true;
            a.say(c.msgWarn);
        }
        int secondsLeft = (int) ((a.remainingMs() + 999) / 1000);
        if (secondsLeft <= 5 && secondsLeft >= 1 && secondsLeft != a.lastTickSecond) Sounds.tick(secondsLeft);
        a.lastTickSecond = secondsLeft;
        if (a.remainingMs() <= 0) a.finish(false);
    }

    /** Ends now and sells to the top bidder. */
    public static void endNow() {
        if (current != null) current.finish(false);
    }

    /** Ends with no sale. */
    public static void cancel() {
        if (current != null) current.finish(true);
    }

    private void finish(boolean cancel) {
        Config c = Config.get();
        cancelled = cancel;
        if (cancel) {
            topBidder = null;
            say(c.msgCancel);
            Sounds.noSale();
        } else {
            say(topBidder != null ? c.msgSold : c.msgNone);
            if (topBidder != null) Sounds.sold(); else Sounds.noSale();
        }
        if (c.autoRefund) {
            for (String p : new ArrayList<>(totals.keySet())) {
                if (!p.equals(topBidder)) refund(p);
            }
        }

        HistoryEntry h = new HistoryEntry();
        h.itemId = itemId;
        h.quantity = quantity;
        h.winner = cancel ? null : topBidder;
        h.price = cancel ? 0 : topBid;
        h.worthTotal = worthTotal();
        h.endedAt = System.currentTimeMillis();
        h.cancelled = cancel;
        List<HistoryEntry> hist = c.history;
        hist.addFirst(h);
        while (hist.size() > 50) hist.removeLast();
        Config.save();

        last = this;
        lastEndedAt = System.currentTimeMillis();
        current = null;
    }

    // ---- chat ----

    private void say(String template) {
        if (!Config.get().announce || template == null || template.isBlank()) return;
        DonutAuctionClient.queueChat(fill(template));
    }

    private String fill(String t) {
        return t.replace("{qty}", Integer.toString(quantity))
                .replace("{item}", itemName())
                .replace("{worth}", worthEach > 0 ? Money.format(worthTotal()) : "?")
                .replace("{min}", minBid > 0 ? Money.format(minBid) : "none")
                .replace("{time}", Long.toString((remainingMs() + 999) / 1000))
                .replace("{bid}", topBidder != null ? Money.format(topBid) : "-")
                .replace("{bidder}", topBidder != null ? topBidder : "nobody")
                .replace("{seller}", seller)
                .replace("{rule}", Config.get().ruleText);
    }
}
