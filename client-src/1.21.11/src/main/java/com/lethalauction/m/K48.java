/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K42;
import com.lethalauction.m.K45;
import com.lethalauction.m.K47;
import com.lethalauction.m.K9;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class K48 {
    private static final boolean a = System.getProperty("os.name", "").toLowerCase().contains("win");
    private static final boolean b = true;
    private static volatile boolean c;
    private static Process d;
    private static Thread e;
    private static Path f;
    private static Path g;

    private K48() {
    }

    public static boolean a() {
        if (a) {
            // empty if block
        }
        return false;
    }

    public static boolean b() {
        Process p = d;
        return c && p != null && p.isAlive();
    }

    public static synchronized void c() {
    }

    public static synchronized void d() {
        c = false;
        K47.m();
        if (d != null) {
            d.destroy();
            d = null;
        }
        e = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void e() {
        Process p = d;
        if (p == null) {
            return;
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));){
            String line;
            while (c && (line = r.readLine()) != null) {
                K48.a(line);
            }
        }
        catch (Exception exception) {
        }
        finally {
            c = false;
            K47.m();
        }
    }

    private static void a(String line) {
        try {
            K47.C95 which;
            JsonObject o = JsonParser.parseString((String)line).getAsJsonObject();
            String app = K48.a(o, "app");
            String title = K48.a(o, "title");
            String artist = K48.a(o, "artist");
            String status = K48.a(o, "status");
            long pos = o.has("pos") && !o.get("pos").isJsonNull() ? o.get("pos").getAsLong() : 0L;
            long dur = o.has("dur") && !o.get("dur").isJsonNull() ? o.get("dur").getAsLong() : 0L;
            String artPath = K48.a(o, "art");
            long sampledAt = o.has("t") && !o.get("t").isJsonNull() ? o.get("t").getAsLong() : System.currentTimeMillis();
            long nowClock = System.currentTimeMillis();
            if (sampledAt <= 0L || sampledAt > nowClock || nowClock - sampledAt > 10000L) {
                sampledAt = nowClock;
            }
            if ((which = K48.b(app)) != K47.C95.SPOTIFY || title.isEmpty()) {
                K47.m();
                return;
            }
            boolean playing = status.equals("playing");
            String artKey = app + "|" + title + "|" + artist;
            K47.a(new K47.C96(which, title, artist, playing, pos, dur, sampledAt, artKey));
            if (K9.ay) {
                K45.a(artKey, title, artist, dur);
            }
            if (!artPath.isEmpty()) {
                K47.a(artKey, Path.of(artPath, new String[0]));
            } else {
                K42.a(artKey, title, artist);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static K47.C95 b(String id) {
        String s = id.toLowerCase();
        if (s.contains("spotify")) {
            return K47.C95.SPOTIFY;
        }
        if (s.contains("apple") || s.contains("itunes") || s.contains("music.ui")) {
            return K47.C95.APPLE;
        }
        return id.isEmpty() ? K47.C95.NONE : K47.C95.OTHER;
    }

    private static String a(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }
}

