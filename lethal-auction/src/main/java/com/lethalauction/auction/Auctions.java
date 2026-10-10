package com.lethalauction.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Auctions started from the menu during this game session. */
public final class Auctions {
    /** How long the winner card stays on screen after an auction ends. */
    public static final long RESULT_MILLIS = 6000;

    public static final class Auction {
        private final String itemId;
        private transient ItemStack item;
        private final int quantity;
        private final long minimumBid;
        private final long worthEach;
        private final int timerSeconds;
        private final long startedAt;
        private String topBidder;
        private long topBid;
        private boolean finished;
        private long finishedAt;
        private boolean cancelled;

        Auction(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds, long startedAt) {
            this.item = item;
            this.itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
            this.quantity = quantity;
            this.minimumBid = minimumBid;
            this.worthEach = worthEach;
            this.timerSeconds = timerSeconds;
            this.startedAt = startedAt;
        }

        /** The auctioned item. Saved auctions only store the item id, so the stack is rebuilt on first use. */
        public ItemStack item() {
            if (item == null) {
                try {
                    item = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId)));
                } catch (RuntimeException e) {
                    return ItemStack.EMPTY;
                }
            }
            return item;
        }

        public int quantity() {
            return quantity;
        }

        public long minimumBid() {
            return minimumBid;
        }

        public long worthEach() {
            return worthEach;
        }

        public int timerSeconds() {
            return timerSeconds;
        }

        public long startedAt() {
            return startedAt;
        }

        public long endsAt() {
            return startedAt + timerSeconds * 1000L;
        }

        public String topBidder() {
            return topBidder;
        }

        public long topBid() {
            return topBid;
        }

        public boolean hasBid() {
            return topBidder != null;
        }

        public long secondsLeft() {
            if (cancelled) {
                return 0;
            }
            long ms = endsAt() - System.currentTimeMillis();
            return ms <= 0 ? 0 : (ms + 999) / 1000;
        }

        /** True once the timer has run out or the auction was cancelled. No more bids are accepted after this. */
        public boolean ended() {
            return cancelled || System.currentTimeMillis() >= endsAt();
        }

        public boolean cancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
            save();
        }

        /** Fraction of the timer still remaining, from 1 down to 0. */
        public float progressLeft() {
            float total = timerSeconds * 1000f;
            return total <= 0 || cancelled ? 0 : Math.max(0, (endsAt() - System.currentTimeMillis()) / total);
        }

        /** Set once the end has been processed (winner announced). */
        public boolean finished() {
            return finished;
        }

        public long finishedAt() {
            return finishedAt;
        }

        BidResult.Kind offer(String player, long amount) {
            if (ended()) {
                return BidResult.Kind.CLOSED;
            }
            if (amount < Math.max(1, minimumBid)) {
                return BidResult.Kind.BELOW_MINIMUM;
            }
            if (amount <= topBid) {
                return BidResult.Kind.NOT_HIGHER;
            }
            topBidder = player;
            topBid = amount;
            save();
            return BidResult.Kind.ACCEPTED;
        }
    }

    /** What happened to a payment that arrived while an auction was running. */
    public record BidResult(Kind kind, Auction auction, String player, long amount) {
        public enum Kind { ACCEPTED, BELOW_MINIMUM, NOT_HIGHER, CLOSED, UNREADABLE }
    }

    private static final List<Auction> RECENT = new ArrayList<>();
    private static final int MAX_HISTORY = 50;
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-history.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Payment messages. Group "name" is the payer, group "amount" the money. */
    private static final String AMOUNT = "\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b";
    private static final String NAME = "(?<name>[A-Za-z0-9_]{2,16})";
    private static final List<Pattern> PAYMENT_PATTERNS = List.of(
            Pattern.compile("^" + NAME + " (?:has )?(?:just )?(?:paid|sent|gave|given) you " + AMOUNT, Pattern.CASE_INSENSITIVE),
            Pattern.compile("^you (?:have )?(?:just )?(?:received|got|been paid|were paid|was paid) " + AMOUNT
                    + "(?: dollars| coins| money)? (?:from|by) " + NAME, Pattern.CASE_INSENSITIVE),
            Pattern.compile("^\\+\\s*" + AMOUNT + " (?:from|by) " + NAME, Pattern.CASE_INSENSITIVE),
            Pattern.compile("^" + AMOUNT + " (?:has been |was )?(?:received|paid to you|sent to you) (?:from|by) " + NAME, Pattern.CASE_INSENSITIVE));
    /** Leading decorations servers put before messages: [tags], (tags) and symbol runs like "»" or "$ |". */
    private static final Pattern PREFIX = Pattern.compile("^(?:\\[[^\\]]{0,24}\\]|\\([^)]{0,24}\\)|[^A-Za-z0-9$+]+)\\s*");
    private static final String SMALL_CAPS = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ";

    private Auctions() {
    }

    public static void start(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds) {
        RECENT.add(0, new Auction(item.copy(), quantity, minimumBid, worthEach, timerSeconds, System.currentTimeMillis()));
        while (RECENT.size() > MAX_HISTORY) {
            RECENT.remove(RECENT.size() - 1);
        }
        save();
    }

    /** Starts the same auction again with a fresh timer and no bids. */
    public static void redo(Auction a) {
        start(a.item(), a.quantity(), a.minimumBid(), a.worthEach(), a.timerSeconds());
    }

    /** Loads the saved auction history. Auctions that were still running when the game closed are cancelled. */
    public static void load() {
        if (!Files.exists(FILE)) {
            return;
        }
        try (Reader r = Files.newBufferedReader(FILE)) {
            List<Auction> saved = GSON.fromJson(r, new TypeToken<List<Auction>>() { }.getType());
            RECENT.clear();
            if (saved != null) {
                for (Auction a : saved) {
                    if (a != null && a.itemId != null) {
                        if (!a.ended()) {
                            a.cancelled = true;
                        }
                        a.finished = true;
                        RECENT.add(a);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            com.lethalauction.LethalAuctionClient.LOGGER.warn("Could not read {}", FILE, e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(RECENT, w);
            }
        } catch (IOException e) {
            com.lethalauction.LethalAuctionClient.LOGGER.warn("Could not save {}", FILE, e);
        }
    }

    /** The newest running auction, otherwise the newest one whose result is still being shown. */
    public static Auction current() {
        for (Auction a : RECENT) {
            if (!a.ended()) {
                return a;
            }
        }
        if (!RECENT.isEmpty()) {
            Auction last = RECENT.get(0);
            if (!last.cancelled() && System.currentTimeMillis() - last.endsAt() < RESULT_MILLIS) {
                return last;
            }
        }
        return null;
    }

    /** Converts small-caps letters (used by many servers) to normal letters and tidies spaces. */
    static String normalize(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        text.codePoints().forEach(c -> {
            int idx = SMALL_CAPS.indexOf(c);
            if (idx >= 0 && c != 'x') {
                sb.append((char) ('a' + idx));
            } else if (Character.isSpaceChar(c)) {
                sb.append(' ');
            } else {
                sb.appendCodePoint(c);
            }
        });
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    /**
     * Handles a server (system) message. If it is a payment to us while an auction is running, it is
     * offered as a bid. Returns what happened, or null if the message is not about a payment.
     */
    public static BidResult onServerMessage(String plain) {
        Auction live = running();
        if (live == null) {
            return null;
        }
        String text = normalize(plain);
        for (int i = 0; i < 4; i++) {
            Matcher pm = PREFIX.matcher(text);
            if (!pm.find() || pm.end() == 0) {
                break;
            }
            text = text.substring(pm.end());
        }
        for (Pattern p : PAYMENT_PATTERNS) {
            Matcher m = p.matcher(text);
            if (m.find()) {
                long amount = parseAmount(m.group("amount").replace(" ", ""));
                if (amount > 0) {
                    String name = m.group("name");
                    return new BidResult(live.offer(name, amount), live, name, amount);
                }
            }
        }
        String lower = text.toLowerCase(Locale.ROOT);
        String head = lower.substring(0, Math.min(24, lower.length()));
        boolean looksLikePayment = (lower.contains("paid you") || lower.contains("received") || lower.contains("sent you")
                || lower.contains("gave you")) && lower.contains("$") && lower.matches(".*[0-9].*")
                && !head.contains(":") && !head.contains(">");
        return looksLikePayment ? new BidResult(BidResult.Kind.UNREADABLE, live, null, 0) : null;
    }

    /** Offers a payment as a bid on the running auction. */
    public static BidResult bid(String player, long amount) {
        Auction live = running();
        return live == null ? null : new BidResult(live.offer(player, amount), live, player, amount);
    }

    /** The auction currently taking bids, or null. */
    public static Auction running() {
        for (Auction a : RECENT) {
            if (!a.ended()) {
                return a;
            }
        }
        return null;
    }

    /** Marks auctions whose timer ran out as finished. Returns the ones that finished just now. */
    public static List<Auction> tick() {
        List<Auction> justFinished = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Auction a : RECENT) {
            if (!a.finished && a.ended() && !a.cancelled) {
                a.finished = true;
                a.finishedAt = now;
                justFinished.add(a);
            }
        }
        if (!justFinished.isEmpty()) {
            save();
        }
        return justFinished;
    }

    public static List<Auction> recent() {
        return Collections.unmodifiableList(RECENT);
    }

    public static void remove(Auction auction) {
        RECENT.remove(auction);
        save();
    }

    public static void clear() {
        RECENT.clear();
        save();
    }

    /** Parses amounts like "1500", "2.5k", "3m" or "1b". Returns -1 if the text is not a valid amount. */
    public static long parseAmount(String text) {
        String s = text.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("$", "");
        if (s.isEmpty()) {
            return -1;
        }
        double mult = 1;
        char last = s.charAt(s.length() - 1);
        if (last == 'k' || last == 'm' || last == 'b' || last == 't') {
            mult = last == 'k' ? 1e3 : last == 'm' ? 1e6 : last == 'b' ? 1e9 : 1e12;
            s = s.substring(0, s.length() - 1);
        }
        try {
            double v = Double.parseDouble(s) * mult;
            if (v < 0 || v > 1e15 || Double.isNaN(v)) {
                return -1;
            }
            return Math.round(v);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Formats an amount as "$950", "$12.5k", "$3m". */
    public static String formatMoney(long amount) {
        if (amount < 1000) {
            return "$" + amount;
        }
        String[] units = {"k", "m", "b", "t"};
        double v = amount;
        int u = -1;
        while (v >= 1000 && u < units.length - 1) {
            v /= 1000;
            u++;
        }
        String num = v >= 100 ? String.format(Locale.ROOT, "%.0f", v) : String.format(Locale.ROOT, "%.1f", v);
        if (num.endsWith(".0")) {
            num = num.substring(0, num.length() - 2);
        }
        return "$" + num + units[u];
    }

    public static String formatTime(long seconds) {
        if (seconds >= 60) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        }
        return seconds + "s";
    }
}
