/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.lethalauction.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lethalauction.LethalAuctionClient;
import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K9;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public final class Transactions {
    private static final Path a = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-transactions.json");
    private static final Gson b = new GsonBuilder().create();
    private static final int c = 5000;
    private static final String d = "\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b";
    private static final String e = "(?<name>\\.?[A-Za-z0-9_]{2,16})";
    private static final String f = "(?<name>.{1,32}?)";
    private static final List<Pattern> g = List.of(Pattern.compile("^you (?:have )?(?:just )?(?:paid|sent|gave) (?<name>\\.?[A-Za-z0-9_]{2,16}) \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b", 2), Pattern.compile("^you (?:have )?(?:just )?(?:paid|sent|gave) \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b(?: dollars| coins| money)? to (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^-\\s*\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b (?:to|for) (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b (?:has been |was )?(?:sent|paid|given) to (?<name>\\.?[A-Za-z0-9_]{2,16})", 2));
    private static final List<Pattern> h = List.of(Pattern.compile("^you (?:have )?(?:just )?(?:bought|purchased) (?:[0-9,]+\\s*x?\\s*)?(?<name>.{1,32}?) for \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b", 2), Pattern.compile("^you (?:have )?(?:just )?spent \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b(?: on (?<name>.{1,40}))?", 2), Pattern.compile("^you (?:have )?(?:just )?paid \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b(?: dollars| coins| money)?(?: for (?<name>.{1,40}))?$", 2), Pattern.compile("^\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\b (?:was |has been )?(?:withdrawn|taken|removed|deducted|charged)(?: from your (?<name>[A-Za-z ]{1,20}))?", 2));
    private static final List<C68> i = new ArrayList<C68>();
    private static boolean j;
    // A bare amount with nothing else: DonutSMP shows sells/earnings as a plain
    // "$35K" action-bar pop (its money icon normalises to $), with no player or
    // "sold" text. "+$" / "$" = money in (a sale); "-$" = money out.
    private static final Pattern k = Pattern.compile("^\\+?\\s*\\$\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\s*$", 2);
    private static final Pattern l = Pattern.compile("^-\\s*\\$\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\s*$", 2);
    // DonutSMP now charges a tax and prints it inline, e.g.
    //   "You paid its_skots $50M (+$ 1M tax)"
    // Tax that ADDS to money out (a buy/pay) increases what you spent; tax on
    // money in (a sale) reduces what you received. We parse the "(...tax)" chunk
    // and fold it into the recorded amount so profit reflects the real coins.
    private static final Pattern n = Pattern.compile("\\(\\s*[+-]?\\s*\\$?\\s*(?<tax>[0-9][0-9,]*(?:\\.[0-9]+)?[kmbt]?)\\s*tax\\s*\\)", 2);
    private static long lastAmt;
    private static long lastAt;

    private static long taxOf(String text) {
        try {
            Matcher m = n.matcher(text);
            if (m.find()) {
                return Auctions.e(m.group("tax").replace(" ", ""));
            }
        } catch (Exception e) {
            // ignore
        }
        return 0L;
    }

    // Fold tax into the amount: received -> subtract (you got less), sent -> add
    // (it cost you more).
    private static long adj(long amount, boolean received, long tax) {
        if (tax <= 0L) {
            return amount;
        }
        return received ? Math.max(0L, amount - tax) : amount + tax;
    }

    private Transactions() {
    }

    public static synchronized List<C68> a() {
        Transactions.f();
        return Collections.unmodifiableList(new ArrayList<C68>(i));
    }

    public static long b() {
        if (K9.aF <= 0L) {
            Transactions.c();
        }
        return K9.aF;
    }

    public static void c() {
        K9.aF = System.currentTimeMillis();
        K9.b();
    }

    public static synchronized long[] d() {
        Transactions.f();
        long start = Transactions.b();
        long received = 0L;
        long sent = 0L;
        long tax = 0L;
        for (C68 t : i) {
            if (t.at() < start) break;
            tax += t.tax();
            if (t.received()) {
                received += t.amount();
                continue;
            }
            sent += t.amount();
        }
        return new long[]{received, sent, tax};
    }

    public static synchronized void e() {
        i.clear();
        Transactions.g();
    }

    public static void a(String plain) {
        Matcher m;
        String text = Auctions.c(plain);
        long tax = Transactions.taxOf(text);
        Matcher in = Auctions.d(text);
        if (in != null) {
            long amt = Auctions.e(in.group("amount").replace(" ", ""));
            Transactions.dbg(plain, in.group("amount"), amt, true, "IN");
            Transactions.a(in.group("name"), Transactions.adj(amt, true, tax), true, tax);
            return;
        }
        for (Pattern p : g) {
            m = p.matcher(text);
            if (!m.find()) continue;
            long amt = Auctions.e(m.group("amount").replace(" ", ""));
            Transactions.dbg(plain, m.group("amount"), amt, false, "OUT-pay");
            Transactions.a(m.group("name"), Transactions.adj(amt, false, tax), false, tax);
            return;
        }
        for (Pattern p : h) {
            m = p.matcher(text);
            if (!m.find()) continue;
            String item = m.group("name");
            String label = item == null || item.isBlank() ? "Purchase" : item.trim();
            long amt = Auctions.e(m.group("amount").replace(" ", ""));
            Transactions.dbg(plain, m.group("amount"), amt, false, "OUT-buy");
            Transactions.a(label, Transactions.adj(amt, false, tax), false, tax);
            return;
        }
        Matcher sale = k.matcher(text);
        if (sale.find()) {
            long amt = Auctions.e(sale.group("amount").replace(" ", ""));
            Transactions.dbg(plain, sale.group("amount"), amt, true, "IN-sale");
            Transactions.b(Transactions.adj(amt, true, tax), "Sale", true, tax);
            return;
        }
        Matcher spend = l.matcher(text);
        if (spend.find()) {
            long amt = Auctions.e(spend.group("amount").replace(" ", ""));
            Transactions.dbg(plain, spend.group("amount"), amt, false, "OUT-spend");
            Transactions.b(Transactions.adj(amt, false, tax), "Purchase", false, tax);
        }
    }

    // Append each recognised transaction to a debug file so a mis-parse (e.g. a
    // $18 pay read as $1B) can be traced to the exact chat line that caused it.
    private static final Path dbgFile = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-transactions-debug.txt");

    private static void dbg(String rawLine, String amountGroup, long parsed, boolean received, String kind) {
        try {
            if (Files.exists(dbgFile, new LinkOption[0]) && Files.size(dbgFile) > 262144L) {
                Files.deleteIfExists(dbgFile);
            }
            String line = java.time.LocalTime.now().withNano(0) + "  " + kind + "  " + (received ? "+" : "-")
                + Auctions.a(parsed) + " (raw amount=\"" + amountGroup + "\" -> " + parsed + ")  | line: "
                + (rawLine == null ? "" : rawLine.trim()) + System.lineSeparator();
            Files.writeString(dbgFile, line, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {
            // never let logging break transaction tracking
        }
    }

    // A bare "$amount" pop fires again on re-render, so drop an identical amount
    // seen within 2s to avoid counting one sale twice.
    private static synchronized void b(long amount, String label, boolean received) {
        Transactions.b(amount, label, received, 0L);
    }

    private static synchronized void b(long amount, String label, boolean received, long tax) {
        long now = System.currentTimeMillis();
        if (amount <= 0L || amount == lastAmt && now - lastAt < 2000L) {
            return;
        }
        lastAmt = amount;
        lastAt = now;
        Transactions.a(label, amount, received, tax);
    }

    // Returns the pay amount if this chat line is a payment (incoming or
    // outgoing /pay), or -1 if it is not a payment, for the hide-small filter.
    public static long matchAmount(String plain) {
        try {
            String text = Auctions.c(plain);
            Matcher in = Auctions.d(text);
            if (in != null) {
                return Auctions.e(in.group("amount").replace(" ", ""));
            }
            for (Pattern p : g) {
                Matcher m = p.matcher(text);
                if (m.find()) {
                    return Auctions.e(m.group("amount").replace(" ", ""));
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return -1L;
    }

    public static synchronized void a(String player, long amount, boolean received) {
        Transactions.a(player, amount, received, 0L);
    }

    public static synchronized void a(String player, long amount, boolean received, long tax) {
        if (amount <= 0L) {
            return;
        }
        if (K9.hideSmall && !K9.hideSmallStr.isBlank()) {
            long thr = 0L;
            try {
                thr = Auctions.e(K9.hideSmallStr);
            } catch (Exception ex) {
                // ignore
            }
            if (thr > 0L && amount <= thr) {
                return;
            }
        }
        Transactions.f();
        i.add(0, new C68(System.currentTimeMillis(), player, amount, received, Math.max(0L, tax)));
        while (i.size() > 5000) {
            i.remove(i.size() - 1);
        }
        Transactions.g();
    }

    private static void f() {
        if (j) {
            return;
        }
        j = true;
        if (!Files.exists(a, new LinkOption[0])) {
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(a);){
            List<C68> saved = (List<C68>)b.fromJson((Reader)r, new TypeToken<List<C68>>(){}.getType());
            if (saved != null) {
                for (C68 t : saved) {
                    if (t == null || t.player() == null) continue;
                    i.add(t);
                }
            }
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not read {}", (Object)a, (Object)e);
        }
    }

    private static void g() {
        try {
            Files.createDirectories(a.getParent(), new FileAttribute[0]);
            try (BufferedWriter w = Files.newBufferedWriter(a, new OpenOption[0]);){
                b.toJson(i, (Appendable)w);
            }
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", (Object)a, (Object)e);
        }
    }

    public static final class C68 {
        private final long at;
        private final String player;
        private final long amount;
        private final boolean received;
        private final long tax;
        C68(long at, String player, long amount, boolean received) { this(at, player, amount, received, 0L); }
        C68(long at, String player, long amount, boolean received, long tax) { this.at = at; this.player = player; this.amount = amount; this.received = received; this.tax = tax; }
        public long at() { return this.at; }
        public String player() { return this.player; }
        public long amount() { return this.amount; }
        public boolean received() { return this.received; }
        public long tax() { return this.tax; }
    }
}

