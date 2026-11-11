package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K42;
import com.lethalauction.m.K45;
import com.lethalauction.m.K47;
import com.lethalauction.m.K9;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Now-playing source. The mod never launches a process or ships a script (that
 * is what antivirus flagged before). Instead it polls a small local file that
 * an optional, separately-distributed helper keeps up to date:
 *   &lt;config&gt;/lethalauction/nowplaying.json  — one JSON object:
 *   {"app":"Spotify","title":"...","artist":"...","status":"playing",
 *    "pos":0,"dur":0,"art":"C:/path.jpg","t":&lt;epoch-ms&gt;}
 * If the file is missing or its last write is stale, the now-playing card hides.
 */
public final class K48 {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
        .resolve("lethalauction").resolve("nowplaying.json");
    private static volatile boolean c;   // running
    private static Thread e;             // poll thread

    private K48() {
    }

    // supported: reading a local file works anywhere, no native helper in the jar
    public static boolean a() {
        return true;
    }

    // running
    public static boolean b() {
        return c;
    }

    // start
    public static synchronized void c() {
        if (c) {
            return;
        }
        c = true;
        Thread t = new Thread(K48::f, "Lethal now-playing");
        t.setDaemon(true);
        e = t;
        t.start();
    }

    // stop
    public static synchronized void d() {
        c = false;
        K47.m();
        e = null;
    }

    // poll loop: read the file and feed it through the same parser as before
    private static void f() {
        while (c) {
            try {
                if (Files.exists(FILE)) {
                    long age = System.currentTimeMillis() - Files.getLastModifiedTime(FILE).toMillis();
                    if (age <= 8000L) {
                        String content = Files.readString(FILE, StandardCharsets.UTF_8).trim();
                        if (!content.isEmpty()) {
                            K48.a(content);
                        } else {
                            K47.m();
                        }
                    } else {
                        K47.m();
                    }
                } else {
                    K47.m();
                }
            } catch (Exception exception) {
                // ignore — just try again next tick
            }
            try {
                Thread.sleep(700L);
            } catch (InterruptedException interruptedException) {
                return;
            }
        }
        K47.m();
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
