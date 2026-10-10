package com.lethalauction.auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.item.ItemStack;

/** Auctions started from the menu during this game session. */
public final class Auctions {
    public record Auction(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds, long startedAt) {
        public long secondsLeft() {
            long elapsed = (System.currentTimeMillis() - startedAt) / 1000;
            return Math.max(0, timerSeconds - elapsed);
        }

        public boolean ended() {
            return secondsLeft() == 0;
        }
    }

    private static final List<Auction> RECENT = new ArrayList<>();

    private Auctions() {
    }

    public static void start(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds) {
        RECENT.add(0, new Auction(item.copy(), quantity, minimumBid, worthEach, timerSeconds, System.currentTimeMillis()));
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
        if (last == 'k' || last == 'm' || last == 'b') {
            mult = last == 'k' ? 1e3 : last == 'm' ? 1e6 : 1e9;
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
