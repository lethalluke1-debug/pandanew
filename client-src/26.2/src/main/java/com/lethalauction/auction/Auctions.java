/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.lethalauction.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lethalauction.LethalAuctionClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
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
import net.minecraft.world.level.ItemLike;

public final class Auctions {
    public static final long a = 6000L;
    private static final List<C66> b = new ArrayList<C66>();
    private static final int c = 50;
    private static final Path d = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-history.json");
    private static final Gson e = new GsonBuilder().setPrettyPrinting().create();
    private static final String f = "\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b";
    private static final String g = "(?<name>\\.?[A-Za-z0-9_]{2,16})";
    private static final List<Pattern> h = List.of(Pattern.compile("^(?<name>\\.?[A-Za-z0-9_]{2,16}) (?:has )?(?:just )?(?:paid|sent|gave|given) you \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b", 2), Pattern.compile("^you (?:have )?(?:just )?(?:received|got|been paid|were paid|was paid) \\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b(?: dollars| coins| money)? (?:from|by) (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^\\+\\s*\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b (?:from|by) (?<name>\\.?[A-Za-z0-9_]{2,16})", 2), Pattern.compile("^\\$?\\s*(?<amount>[0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbt]?)\\b (?:has been |was )?(?:received|paid to you|sent to you) (?:from|by) (?<name>\\.?[A-Za-z0-9_]{2,16})", 2));
    private static final Pattern i = Pattern.compile("^(?:\\[[^\\]]{0,24}\\]|\\([^)]{0,24}\\)|[^A-Za-z0-9$+.]+)\\s*");
    private static final String j = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21x\u028f\u1d22";

    private Auctions() {
    }

    public static void a(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds) {
        Auctions.a(item, quantity, minimumBid, worthEach, timerSeconds, "Pay over = I keep");
    }

    public static void a(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds, String note) {
        Auctions.a(item, quantity, worthEach, ItemStack.EMPTY, 0, 0L, minimumBid, timerSeconds, note);
    }

    public static void a(ItemStack item, int quantity, long worthEach, ItemStack item2, int quantity2, long worthEach2, long minimumBid, int timerSeconds, String note) {
        C66 a = new C66(item.copy(), quantity, minimumBid, worthEach, timerSeconds, System.currentTimeMillis(), note);
        if (!item2.isEmpty()) {
            a.item2 = item2.copy();
            a.item2Id = BuiltInRegistries.ITEM.getKey(item2.getItem()).toString();
            a.quantity2 = quantity2;
            a.worthEach2 = worthEach2;
        }
        b.add(0, a);
        while (b.size() > 50) {
            b.remove(b.size() - 1);
        }
        Auctions.b();
    }

    public static void a(C66 a) {
        if (a.restart()) {
            Auctions.a(a.item(), a.quantity(), a.minimumBid(), a.timerSeconds(), a.note());
        } else {
            Auctions.a(a.item(), a.quantity(), a.worthEach(), a.item2(), a.quantity2(), a.worthEach2(), a.minimumBid(), a.timerSeconds(), a.note());
        }
    }

    public static void a(ItemStack item, int quantity, long exactPrice, int timerSeconds, String note) {
        C66 a = new C66(item.copy(), quantity, exactPrice, 0L, timerSeconds, System.currentTimeMillis(), note);
        a.restart = true;
        b.add(0, a);
        while (b.size() > 50) {
            b.remove(b.size() - 1);
        }
        Auctions.b();
    }

    public static void a() {
        if (Files.exists(d, new LinkOption[0])) {
            try (BufferedReader r = Files.newBufferedReader(d);){
                List<C66> saved = (List<C66>)e.fromJson((Reader)r, new TypeToken<List<C66>>(){}.getType());
                b.clear();
                if (saved != null) {
                    for (C66 a : saved) {
                        if (a == null || a.itemId == null) continue;
                        if (!a.ended()) {
                            a.cancelled = true;
                        }
                        a.finished = true;
                        b.add(a);
                    }
                }
            }
            catch (IOException | RuntimeException var6) {
                LethalAuctionClient.LOGGER.warn("Could not read {}", (Object)d, (Object)var6);
            }
        }
    }

    public static void b() {
        try {
            Files.createDirectories(d.getParent(), new FileAttribute[0]);
            try (BufferedWriter w = Files.newBufferedWriter(d, new OpenOption[0]);){
                e.toJson(b, (Appendable)w);
            }
        }
        catch (IOException var5) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", (Object)d, (Object)var5);
        }
    }

    public static C66 c() {
        C66 last;
        for (C66 a : b) {
            if (a.ended()) continue;
            return a;
        }
        if (!b.isEmpty() && !(last = b.get(0)).cancelled() && System.currentTimeMillis() - last.endsAt() < 6000L) {
            return last;
        }
        return null;
    }

    static String a(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        text.codePoints().forEach(c -> {
            int idx = j.indexOf(c);
            if (idx >= 0 && c != 120) {
                sb.append((char)(97 + idx));
            } else if (Character.isSpaceChar(c)) {
                sb.append(' ');
            } else {
                sb.appendCodePoint(c);
            }
        });
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    public static C67 b(String plain) {
        Matcher pm;
        C66 live = Auctions.d();
        if (live == null) {
            return null;
        }
        String text = Auctions.a(plain);
        for (int i = 0; i < 4 && (pm = Auctions.i.matcher(text)).find() && pm.end() != 0; ++i) {
            text = text.substring(pm.end());
        }
        for (Pattern p : h) {
            long amount;
            Matcher m = p.matcher(text);
            if (!m.find() || (amount = Auctions.e(m.group("amount").replace(" ", ""))) <= 0L) continue;
            String name = m.group("name");
            return new C67(live.offer(name, amount), live, name, amount);
        }
        String lower = text.toLowerCase(Locale.ROOT);
        String head = lower.substring(0, Math.min(24, lower.length()));
        boolean looksLikePayment = (lower.contains("paid you") || lower.contains("received") || lower.contains("sent you") || lower.contains("gave you")) && lower.contains("$") && lower.matches(".*[0-9].*") && !head.contains(":") && !head.contains(">");
        return looksLikePayment ? new C67(C67.C118.UNREADABLE, live, null, 0L) : null;
    }

    public static String c(String plain) {
        Matcher pm;
        String text = Auctions.a(plain);
        for (int i = 0; i < 4 && (pm = Auctions.i.matcher(text)).find() && pm.end() != 0; ++i) {
            text = text.substring(pm.end());
        }
        return text;
    }

    public static Matcher d(String text) {
        for (Pattern p : h) {
            Matcher m = p.matcher(text);
            if (!m.find()) continue;
            return m;
        }
        return null;
    }

    public static C67 a(String player, long amount) {
        C66 live = Auctions.d();
        return live == null ? null : new C67(live.offer(player, amount), live, player, amount);
    }

    public static C66 d() {
        for (C66 a : b) {
            if (a.ended()) continue;
            return a;
        }
        return null;
    }

    public static List<C66> e() {
        ArrayList<C66> justFinished = new ArrayList<C66>();
        long now = System.currentTimeMillis();
        for (C66 a : b) {
            if (a.finished || !a.ended() || a.cancelled) continue;
            a.finished = true;
            a.finishedAt = now;
            justFinished.add(a);
        }
        if (!justFinished.isEmpty()) {
            Auctions.b();
        }
        return justFinished;
    }

    public static List<C66> f() {
        return Collections.unmodifiableList(b);
    }

    public static void b(C66 auction) {
        b.remove(auction);
        Auctions.b();
    }

    public static void g() {
        b.clear();
        Auctions.b();
    }

    public static long e(String text) {
        String s = text.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("$", "");
        if (s.isEmpty()) {
            return -1L;
        }
        double mult = 1.0;
        char last = s.charAt(s.length() - 1);
        if (last == 'k' || last == 'm' || last == 'b' || last == 't') {
            mult = last == 'k' ? 1000.0 : (last == 'm' ? 1000000.0 : (last == 'b' ? 1.0E9 : 1.0E12));
            s = s.substring(0, s.length() - 1);
        }
        try {
            double v = Double.parseDouble(s) * mult;
            return !(v < 0.0) && !(v > 1.0E15) && !Double.isNaN(v) ? Math.round(v) : -1L;
        }
        catch (NumberFormatException var7) {
            return -1L;
        }
    }

    public static String a(long amount) {
        String num;
        int u;
        if (amount < 1000L) {
            return "$" + amount;
        }
        String[] units = new String[]{"k", "m", "b", "t"};
        double v = amount;
        for (u = -1; v >= 1000.0 && u < units.length - 1; v /= 1000.0, ++u) {
        }
        String string = num = v >= 100.0 ? String.format(Locale.ROOT, "%.0f", v) : String.format(Locale.ROOT, "%.1f", v);
        if (num.endsWith(".0")) {
            num = num.substring(0, num.length() - 2);
        }
        return "$" + num + units[u];
    }

    public static String b(long seconds) {
        return seconds <= 10L ? Long.toString(Math.max(0L, seconds)) : seconds / 60L + ":" + String.format(Locale.ROOT, "%02d", seconds % 60L);
    }

    public static String c(long seconds) {
        return seconds >= 60L ? seconds / 60L + "m " + seconds % 60L + "s" : seconds + "s";
    }

    public static final class C66 {
        private final String itemId;
        private transient ItemStack item;
        private final int quantity;
        private final long minimumBid;
        private final long worthEach;
        private final int timerSeconds;
        private long startedAt;
        private final String note;
        private boolean restart;
        private String item2Id;
        private transient ItemStack item2;
        private int quantity2;
        private long worthEach2;
        private String topBidder;
        private long topBid;
        private boolean finished;
        private long finishedAt;
        private boolean cancelled;

        C66(ItemStack item, int quantity, long minimumBid, long worthEach, int timerSeconds, long startedAt, String note) {
            this.note = note;
            this.item = item;
            this.itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
            this.quantity = quantity;
            this.minimumBid = minimumBid;
            this.worthEach = worthEach;
            this.timerSeconds = timerSeconds;
            this.startedAt = startedAt;
        }

        public ItemStack item() {
            if (this.item == null) {
                try {
                    this.item = new ItemStack((ItemLike)BuiltInRegistries.ITEM.getValue(Identifier.parse((String)this.itemId)));
                }
                catch (RuntimeException var2) {
                    return ItemStack.EMPTY;
                }
            }
            return this.item;
        }

        public int quantity() {
            return this.quantity;
        }

        public boolean hasSecondItem() {
            return this.item2Id != null;
        }

        public ItemStack item2() {
            if (this.item2Id == null) {
                return ItemStack.EMPTY;
            }
            if (this.item2 == null) {
                try {
                    this.item2 = new ItemStack((ItemLike)BuiltInRegistries.ITEM.getValue(Identifier.parse((String)this.item2Id)));
                }
                catch (RuntimeException e) {
                    return ItemStack.EMPTY;
                }
            }
            return this.item2;
        }

        public int quantity2() {
            return this.quantity2;
        }

        public long worthEach2() {
            return this.worthEach2;
        }

        public long totalWorth() {
            return this.worthEach * (long)this.quantity + this.worthEach2 * (long)this.quantity2;
        }

        public String title() {
            String name = this.item().getHoverName().getString();
            return this.hasSecondItem() ? name + " + " + this.item2().getHoverName().getString() : name;
        }

        public long minimumBid() {
            return this.minimumBid;
        }

        public long worthEach() {
            return this.worthEach;
        }

        public String note() {
            return this.note == null ? "Pay over = I keep" : this.note;
        }

        public int timerSeconds() {
            return this.timerSeconds;
        }

        public boolean restart() {
            return this.restart;
        }

        public long startedAt() {
            return this.startedAt;
        }

        public long endsAt() {
            return this.startedAt + (long)this.timerSeconds * 1000L;
        }

        public String topBidder() {
            return this.topBidder;
        }

        public long topBid() {
            return this.topBid;
        }

        public boolean hasBid() {
            return this.topBidder != null;
        }

        public long secondsLeft() {
            if (this.cancelled) {
                return 0L;
            }
            long ms = this.endsAt() - System.currentTimeMillis();
            return ms <= 0L ? 0L : (ms + 999L) / 1000L;
        }

        public boolean ended() {
            return this.cancelled || System.currentTimeMillis() >= this.endsAt();
        }

        public boolean cancelled() {
            return this.cancelled;
        }

        public void cancel() {
            this.cancelled = true;
            Auctions.b();
        }

        public float progressLeft() {
            float total = (float)this.timerSeconds * 1000.0f;
            return !(total <= 0.0f) && !this.cancelled ? Math.max(0.0f, (float)(this.endsAt() - System.currentTimeMillis()) / total) : 0.0f;
        }

        public boolean finished() {
            return this.finished;
        }

        public long finishedAt() {
            return this.finishedAt;
        }

        C67.C118 offer(String player, long amount) {
            if (this.ended()) {
                return C67.C118.CLOSED;
            }
            if (this.restart) {
                if (amount != this.minimumBid) {
                    return C67.C118.NOT_EXACT;
                }
                this.topBidder = player;
                this.topBid = amount;
                this.startedAt = System.currentTimeMillis();
                Auctions.b();
                return C67.C118.ACCEPTED;
            }
            if (amount < Math.max(1L, this.minimumBid)) {
                return C67.C118.BELOW_MINIMUM;
            }
            if (amount <= this.topBid) {
                return C67.C118.NOT_HIGHER;
            }
            this.topBidder = player;
            this.topBid = amount;
            Auctions.b();
            return C67.C118.ACCEPTED;
        }
    }

    public static final class C67 {
        private final C118 a;
        private final C66 b;
        private final String c;
        private final long d;
        C67(C118 a, C66 b, String c, long d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public C118 a() { return this.a; }
        public C66 b() { return this.b; }
        public String c() { return this.c; }
        public long d() { return this.d; }



        public static enum C118 {
            ACCEPTED,
            BELOW_MINIMUM,
            NOT_HIGHER,
            NOT_EXACT,
            CLOSED,
            UNREADABLE;

        }
    }
}

