package com.lethalauction.auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.item.ItemStack;

/** Auctions started from the menu during this game session. */
public final class Auctions {
    /** How long the winner card stays on screen after an auction ends. */
    public static final long RESULT_MILLIS = 6000;

    public static final class Auction {
        private final ItemStack item;
        private final int quantity;
        private final long minimumBid;
        private final long worthEach;
        private final int timerSeconds;
        private final long startedAt;
        private String topBidder;
        private long topBid;
        private boolean finished;
        private long finishedAt;

        Auction(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds, long startedAt) {
            this.item = item;
            this.quantity = quantity;
            this.minimumBid = minimumBid;
            this.worthEach = worthEach;
            this.timerSeconds = timerSeconds;
            this.startedAt = startedAt;
        }

        public ItemStack item() {
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
            long ms = endsAt() - System.currentTimeMillis();
            return ms <= 0 ? 0 : (ms + 999) / 1000;
        }

        /** True once the timer has run out. No more bids are accepted after this. */
        public boolean ended() {
            return System.currentTimeMillis() >= endsAt();
        }

        /** Fraction of the timer still remaining, from 1 down to 0. */
        public float progressLeft() {
            float total = timerSeconds * 1000f;
            return total <= 0 ? 0 : Math.max(0, (endsAt() - System.currentTimeMillis()) / total);
        }

        /** Set once the end has been processed (winner announced). */
        public boolean finished() {
            return finished;
        }

        public long finishedAt() {
            return finishedAt;
        }

        boolean offer(String player, long amount) {
            if (ended() || amount < Math.max(1, minimumBid) || amount <= topBid) {
                return false;
            }
            topBidder = player;
            topBid = amount;
            return true;
        }
    }

    private static final List<Auction> RECENT = new ArrayList<>();

    /** DonutSMP-style payment messages. Group "name" is the payer, group "amount" the money. */
    private static final String AMOUNT = "\\$?(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)";
    private static final String NAME = "(?<name>[A-Za-z0-9_.]{2,16})";
    private static final List<Pattern> PAYMENT_PATTERNS = List.of(
            Pattern.compile("^" + NAME + " (?:has )?(?:paid|sent) you " + AMOUNT, Pattern.CASE_INSENSITIVE),
            Pattern.compile("^you (?:have )?received " + AMOUNT + " from " + NAME, Pattern.CASE_INSENSITIVE),
            Pattern.compile("^\\+\\s*" + AMOUNT + " (?:from|by) " + NAME, Pattern.CASE_INSENSITIVE));

    private Auctions() {
    }

    public static void start(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds) {
        RECENT.add(0, new Auction(item.copy(), quantity, minimumBid, worthEach, timerSeconds, System.currentTimeMillis()));
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
            if (System.currentTimeMillis() - last.endsAt() < RESULT_MILLIS) {
                return last;
            }
        }
        return null;
    }

    /**
     * Handles a server (system) chat message. If it is a payment to us while an auction is running,
     * it becomes a bid. Returns the auction that took the bid, or null.
     */
    public static Auction onServerMessage(String plain) {
        String text = plain.trim();
        for (Pattern p : PAYMENT_PATTERNS) {
            Matcher m = p.matcher(text);
            if (m.find()) {
                long amount = parseAmount(m.group("amount").replace(" ", ""));
                if (amount > 0) {
                    return bid(m.group("name"), amount);
                }
            }
        }
        return null;
    }

    /** Applies a payment as a bid on the running auction. Returns that auction if the bid was taken. */
    public static Auction bid(String player, long amount) {
        for (Auction a : RECENT) {
            if (!a.ended()) {
                return a.offer(player, amount) ? a : null;
            }
        }
        return null;
    }

    /** Marks auctions whose timer ran out as finished. Returns the ones that finished just now. */
    public static List<Auction> tick() {
        List<Auction> justFinished = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Auction a : RECENT) {
            if (!a.finished && a.ended()) {
                a.finished = true;
                a.finishedAt = now;
                justFinished.add(a);
            }
        }
        return justFinished;
    }

    public static List<Auction> recent() {
        return Collections.unmodifiableList(RECENT);
    }

    public static void remove(Auction auction) {
        RECENT.remove(auction);
    }

    public static void clear() {
        RECENT.clear();
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
