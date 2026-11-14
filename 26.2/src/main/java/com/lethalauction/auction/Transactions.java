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
    private static final String d = "\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b";
    private static final String e = "(?<name>\\.?[A-Za-z0-9_]{2,16})";
    private static final String f = "(?<name>.{1,32}?)";
    private static final List<Pattern> g = List.of(Pattern.compile("^you (?:have )?(?:just )?(?:paid|sent|gave) (?<name>\\.?[A-Za-z0-9_]{2,16}) \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b", 2), Pattern.compile("^you (?:have )?(?:just )?(?:paid|sent|gave) \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b(?: dollars| coins| money)? to (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^-\\s*\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b (?:to|for) (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b (?:has been |was )?(?:sent|paid|given) to (?<name>\\.?[A-Za-z0-9_]{2,16})", 2));
    private static final List<Pattern> h = List.of(Pattern.compile("^you (?:have )?(?:just )?(?:bought|purchased) (?:[0-9,]+\\s*x?\\s*)?(?<name>.{1,32}?) for \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b", 2), Pattern.compile("^you (?:have )?(?:just )?spent \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b(?: on (?<name>.{1,40}))?", 2), Pattern.compile("^you (?:have )?(?:just )?paid \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b(?: dollars| coins| money)?(?: for (?<name>.{1,40}))?$", 2), Pattern.compile("^\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b (?:was |has been )?(?:withdrawn|taken|removed|deducted|charged)(?: from your (?<name>[A-Za-z ]{1,20}))?", 2));
    private static final List<C69> i = new ArrayList<C69>();
    private static boolean j;

    private Transactions() {
    }

    public static synchronized List<C69> a() {
        Transactions.f();
        return Collections.unmodifiableList(new ArrayList<C69>(i));
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
        for (C69 t : i) {
            if (t.at() < start) break;
            if (t.received()) {
                received += t.amount();
                continue;
            }
            sent += t.amount();
        }
        return new long[]{received, sent};
    }

    public static synchronized void e() {
        i.clear();
        Transactions.g();
    }

    public static void a(String plain) {
        Matcher m;
        String text = Auctions.c(plain);
        Matcher in = Auctions.d(text);
        if (in != null) {
            Transactions.a(in.group("name"), Auctions.e(in.group("amount").replace(" ", "")), true);
            return;
        }
        for (Pattern p : g) {
            m = p.matcher(text);
            if (!m.find()) continue;
            Transactions.a(m.group("name"), Auctions.e(m.group("amount").replace(" ", "")), false);
            return;
        }
        for (Pattern p : h) {
            m = p.matcher(text);
            if (!m.find()) continue;
            String item = m.group("name");
            String label = item == null || item.isBlank() ? "Purchase" : item.trim();
            Transactions.a(label, Auctions.e(m.group("amount").replace(" ", "")), false);
            return;
        }
    }

    public static synchronized void a(String player, long amount, boolean received) {
        if (amount <= 0L) {
            return;
        }
        if (K9.hideSmall && K9.hideSmallAmt > 0L && amount < K9.hideSmallAmt) {
            return;
        }
        Transactions.f();
        i.add(0, new C69(System.currentTimeMillis(), player, amount, received));
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
            List<C69> saved = (List<C69>)b.fromJson((Reader)r, new TypeToken<List<C69>>(){}.getType());
            if (saved != null) {
                for (C69 t : saved) {
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

    public static final class C69 {
        private final long at;
        private final String player;
        private final long amount;
        private final boolean received;
        C69(long at, String player, long amount, boolean received) { this.at = at; this.player = player; this.amount = amount; this.received = received; }
        public long at() { return this.at; }
        public String player() { return this.player; }
        public long amount() { return this.amount; }
        public boolean received() { return this.received; }
    }
}

