/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.lethalauction.m;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K43;
import com.lethalauction.m.K47;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class K45 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
    private static volatile int b;
    private static volatile int c;
    private static volatile int d;
    private static volatile long e;
    private static volatile boolean f;
    private static final Pattern g;
    private static volatile String h;

    private K45() {
    }

    static void a(String key, String title, String artist, long durationMs) {
        if (key == null || key.isEmpty() || title == null || title.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (key.equals(h) && (!f || now - e <= 20000L)) {
            return;
        }
        h = key;
        e = now;
        Thread t = new Thread(() -> K45.b(key, title, artist, durationMs), "lethal-lyrics");
        t.setDaemon(true);
        t.start();
    }

    private static void b(String key, String title, String artist, long durationMs) {
        List<C94> lines;
        K47.b();
        b = 0;
        c = 0;
        d = 0;
        f = false;
        String t = K45.e(title);
        String a = K45.d(artist);
        String synced = K45.a(() -> K45.a(a, t, a, durationMs, false), () -> K45.a("", t, a, durationMs, true), () -> K45.b(a + " " + t, t, a, durationMs, true), () -> K45.a(title, artist, durationMs));
        if (synced != null && !synced.isBlank() && !(lines = K45.a(synced)).isEmpty()) {
            K43.a("synced lyrics loaded (" + lines.size() + " lines) for \"" + title + "\"");
            K47.a(key, lines);
            return;
        }
        String plain = K45.a(() -> K45.c(a, t, a, durationMs, false), () -> K45.c("", t, a, durationMs, true), () -> K45.d(a + " " + t, t, a, durationMs, true), () -> K45.b(title, artist, durationMs));
        List<C94> est = K45.a(plain, durationMs);
        if (!est.isEmpty()) {
            K43.a("plain lyrics (estimated timing, " + est.size() + " lines) for \"" + title + "\"");
            K47.a(key, est);
            return;
        }
        if (c == 0) {
            f = true;
            K43.a("lyrics: LRCLIB gave no 200 for \"" + title + "\" (replies=" + b + ", rejected=" + d + ")");
            K47.a(key, 4);
        } else {
            f = false;
            K43.a("no lyrics at all for \"" + title + "\"");
            K47.a(key, List.of());
        }
    }

    private static String a(C93 ... attempts) {
        for (C93 at : attempts) {
            try {
                String s = at.get();
                if (s == null || s.isBlank()) continue;
                return s;
            }
            catch (Exception e) {
                K43.a("lyrics attempt error: " + String.valueOf(e));
            }
        }
        return null;
    }

    private static String a(String title, String artist, long durationMs) throws Exception {
        JsonObject o;
        JsonElement body;
        StringBuilder u = new StringBuilder("https://lrclib.net/api/get?track_name=").append(K45.f(title)).append("&artist_name=").append(K45.f(artist));
        if (durationMs > 0L) {
            u.append("&duration=").append(durationMs / 1000L);
        }
        if ((body = K45.c(u.toString())) != null && body.isJsonObject() && (o = body.getAsJsonObject()).has("syncedLyrics") && !o.get("syncedLyrics").isJsonNull()) {
            return o.get("syncedLyrics").getAsString();
        }
        return null;
    }

    private static String a(String queryArtist, String t, String a, long durationMs, boolean strict) throws Exception {
        String url = "https://lrclib.net/api/search?track_name=" + K45.f(t);
        if (!queryArtist.isEmpty()) {
            url = url + "&artist_name=" + K45.f(queryArtist);
        }
        return K45.b(K45.c(url), t, a, durationMs, strict);
    }

    private static String b(String q, String t, String a, long durationMs, boolean strict) throws Exception {
        return K45.b(K45.c("https://lrclib.net/api/search?q=" + K45.f(q.trim())), t, a, durationMs, strict);
    }

    private static String b(String title, String artist, long durationMs) throws Exception {
        JsonObject o;
        JsonElement body;
        StringBuilder u = new StringBuilder("https://lrclib.net/api/get?track_name=").append(K45.f(title)).append("&artist_name=").append(K45.f(artist));
        if (durationMs > 0L) {
            u.append("&duration=").append(durationMs / 1000L);
        }
        if ((body = K45.c(u.toString())) != null && body.isJsonObject() && (o = body.getAsJsonObject()).has("plainLyrics") && !o.get("plainLyrics").isJsonNull()) {
            return o.get("plainLyrics").getAsString();
        }
        return null;
    }

    private static String c(String queryArtist, String t, String a, long durationMs, boolean strict) throws Exception {
        String url = "https://lrclib.net/api/search?track_name=" + K45.f(t);
        if (!queryArtist.isEmpty()) {
            url = url + "&artist_name=" + K45.f(queryArtist);
        }
        return K45.a(K45.c(url), t, a, durationMs, strict);
    }

    private static String d(String q, String t, String a, long durationMs, boolean strict) throws Exception {
        return K45.a(K45.c("https://lrclib.net/api/search?q=" + K45.f(q.trim())), t, a, durationMs, strict);
    }

    private static String a(JsonElement body, String t, String a, long durationMs, boolean strict) {
        if (body == null || !body.isJsonArray()) {
            return null;
        }
        String best = null;
        double bestDiff = Double.MAX_VALUE;
        for (JsonElement e : body.getAsJsonArray()) {
            String s;
            JsonObject o = e.getAsJsonObject();
            if (!o.has("plainLyrics") || o.get("plainLyrics").isJsonNull() || (s = o.get("plainLyrics").getAsString()).isBlank()) continue;
            if (strict) {
                if (!K45.a(o, t, a, durationMs)) continue;
                return s;
            }
            double diff = K45.a(o, durationMs);
            if (!(diff <= 30.0) || !(diff < bestDiff)) continue;
            bestDiff = diff;
            best = s;
        }
        return strict ? null : best;
    }

    private static double a(JsonObject o, long durationMs) {
        if (durationMs <= 0L) {
            return 0.0;
        }
        if (!o.has("duration") || o.get("duration").isJsonNull()) {
            return 25.0;
        }
        double d = o.get("duration").getAsDouble();
        return d <= 0.0 ? 25.0 : Math.abs(d - (double)durationMs / 1000.0);
    }

    private static boolean a(JsonObject o, String title, String artist, long durationMs) {
        String ca;
        double dur;
        if (durationMs > 0L && o.has("duration") && !o.get("duration").isJsonNull() && (dur = o.get("duration").getAsDouble()) > 0.0 && Math.abs(dur - (double)durationMs / 1000.0) > 7.0) {
            return false;
        }
        String ct = o.has("trackName") && !o.get("trackName").isJsonNull() ? o.get("trackName").getAsString() : "";
        String string = ca = o.has("artistName") && !o.get("artistName").isJsonNull() ? o.get("artistName").getAsString() : "";
        if (!artist.isEmpty() && !K45.a(ca, artist)) {
            return false;
        }
        return title.isEmpty() || K45.a(ct, title);
    }

    private static String b(String s) {
        if (s == null) {
            return "";
        }
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        n = n.toLowerCase(Locale.ROOT);
        n = n.replaceAll("\\(.*?\\)|\\[.*?\\]", " ");
        n = n.replaceAll("feat\\.?|ft\\.?|remaster(ed)?|\\d{4}", " ");
        n = n.replaceAll("[^a-z0-9]+", " ").trim();
        return n;
    }

    private static boolean a(String a, String b) {
        String na = K45.b(a);
        String nb = K45.b(b);
        return !na.isEmpty() && !nb.isEmpty() && (na.contains(nb) || nb.contains(na));
    }

    static List<C94> a(String plain, long durationMs) {
        ArrayList<C94> out = new ArrayList<C94>();
        if (plain == null || plain.isBlank()) {
            return out;
        }
        ArrayList<String> text = new ArrayList<String>();
        for (String raw : plain.split("\\r?\\n")) {
            String s = raw.trim();
            if (s.isEmpty()) continue;
            text.add(s);
        }
        if (text.isEmpty()) {
            return out;
        }
        long span = durationMs > 20000L ? durationMs : (long)text.size() * 3000L;
        long lead = Math.min(6000L, span / 12L);
        long usable = Math.max(1L, span - lead - Math.min(8000L, span / 8L));
        for (int i = 0; i < text.size(); ++i) {
            long ms = lead + usable * (long)i / (long)Math.max(1, text.size());
            out.add(new C94(ms, (String)text.get(i)));
        }
        return out;
    }

    private static String b(JsonElement body, String t, String a, long durationMs, boolean strict) {
        if (body == null || !body.isJsonArray()) {
            return null;
        }
        String best = null;
        double bestDiff = Double.MAX_VALUE;
        for (JsonElement e : body.getAsJsonArray()) {
            String s;
            JsonObject o = e.getAsJsonObject();
            if (!o.has("syncedLyrics") || o.get("syncedLyrics").isJsonNull() || (s = o.get("syncedLyrics").getAsString()).isBlank()) continue;
            if (strict) {
                if (!K45.a(o, t, a, durationMs)) continue;
                return s;
            }
            double diff = K45.a(o, durationMs);
            if (!(diff <= 30.0) || !(diff < bestDiff)) continue;
            bestDiff = diff;
            best = s;
        }
        return strict ? null : best;
    }

    private static JsonElement c(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10L)).header("User-Agent", "LethalClient Minecraft mod").GET().build();
        HttpResponse<String> res = a.send(req, HttpResponse.BodyHandlers.ofString());
        ++b;
        int code = res.statusCode();
        if (code == 200) {
            ++c;
        } else {
            ++d;
        }
        K43.a("  GET " + url + " -> HTTP " + code);
        if (code != 200) {
            return null;
        }
        return JsonParser.parseString((String)res.body());
    }

    static List<C94> a(String lrc) {
        ArrayList<C94> out = new ArrayList<C94>();
        for (String raw : lrc.split("\\r?\\n")) {
            Matcher m = g.matcher(raw);
            ArrayList<Long> stamps = new ArrayList<Long>();
            int end = 0;
            while (m.find()) {
                long min = Long.parseLong(m.group(1));
                long sec = Long.parseLong(m.group(2));
                long frac = 0L;
                if (m.group(3) != null) {
                    frac = Long.parseLong((m.group(3) + "000").substring(0, 3));
                }
                stamps.add(min * 60000L + sec * 1000L + frac);
                end = m.end();
            }
            if (stamps.isEmpty()) continue;
            String text = raw.substring(end).trim();
            Iterator iterator = stamps.iterator();
            while (iterator.hasNext()) {
                long ms = (Long)iterator.next();
                out.add(new C94(ms, text));
            }
        }
        out.sort((x, y) -> Long.compare(x.a(), y.a()));
        ArrayList<C94> dedup = new ArrayList<C94>(out.size());
        for (C94 l : out) {
            C94 prev;
            if (!dedup.isEmpty() && (prev = (C94)dedup.get(dedup.size() - 1)).b().equalsIgnoreCase(l.b()) && l.a() - prev.a() < 1200L) continue;
            dedup.add(l);
        }
        return dedup;
    }

    private static String d(String artist) {
        if (artist == null) {
            return "";
        }
        String s = artist.replaceAll("(?i)\\b(feat|ft|with)\\.?\\b.*$", " ");
        s = s.split("[,&]|\\bx\\b|/| vs ")[0];
        return s.replaceAll("\\s+", " ").trim();
    }

    private static String e(String title) {
        if (title == null) {
            return "";
        }
        String s = title.replaceAll("(?i)\\((feat|ft|with)\\.?[^)]*\\)", " ");
        s = s.replaceAll("(?i)\\[[^]]*]", " ");
        s = s.replaceAll("(?i)\\s*-\\s*(single|ep|remaster.*|deluxe.*)$", " ");
        return s.replaceAll("\\s+", " ").trim();
    }

    private static String f(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    static {
        g = Pattern.compile("\\[(\\d+):(\\d{2})(?:\\.(\\d{1,3}))?]");
        h = "";
    }

    private static interface C93 {
        public String get() throws Exception;
    }

    public static final class C94 {
        private final long a;
        private final String b;
        C94(long a, String b) { this.a = a; this.b = b; }
        public long a() { return this.a; }
        public String b() { return this.b; }


    }
}

