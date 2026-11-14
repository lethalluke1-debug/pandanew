package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K42;
import com.lethalauction.m.K47;
import com.lethalauction.m.K9;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Now-playing source. On Windows the mod reads the current media from the system
 * media API (the same feed the volume overlay uses) via a small bundled
 * PowerShell script. The script is extracted to &lt;config&gt;/lethal-music and
 * launched with "powershell -File"; it prints one compact JSON line ~8x a second
 * on stdout, which this class parses. It also still reads an optional
 * &lt;config&gt;/lethalauction/nowplaying.json written by a separate helper.
 */
public final class K48 {
    private static final boolean WIN = System.getProperty("os.name", "").toLowerCase().contains("win");
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
        .resolve("lethalauction").resolve("nowplaying.json");
    private static final Path MDIR = FabricLoader.getInstance().getConfigDir().resolve("lethal-music");
    private static volatile boolean c;   // running
    private static Process d;            // reader process
    private static Thread e;             // stdout reader
    private static Thread g;             // file poller
    private static volatile long lastValid = 0L;   // last reading that had a real song

    private K48() {
    }

    public static boolean a() {
        return true;
    }

    public static boolean b() {
        return c;
    }

    public static synchronized void c() {
        if (c) {
            return;
        }
        c = true;
        if (WIN) {
            Thread sp = new Thread(K48::h, "Lethal now-playing");
            sp.setDaemon(true);
            e = sp;
            sp.start();
        }
        Thread poll = new Thread(K48::i, "Lethal now-playing file");
        poll.setDaemon(true);
        g = poll;
        poll.start();
    }

    public static synchronized void d() {
        c = false;
        K47.m();
        if (d != null) {
            try {
                d.destroy();
            } catch (Exception exception) {
                // ignore
            }
            d = null;
        }
        e = null;
        g = null;
    }

    // Windows now-playing without PowerShell: Spotify writes the current song
    // into its window title ("Artist - Song"), which the built-in `tasklist`
    // command reports. No bundled script, no powershell -> no antivirus flag.
    private static void h() {
        while (c) {
            try {
                String wt = K48.j();
                if (wt != null) {
                    int dash = wt.indexOf(" - ");
                    String artist = dash > 0 ? wt.substring(0, dash).trim() : "";
                    String song = dash > 0 ? wt.substring(dash + 3).trim() : wt.trim();
                    if (!song.isEmpty()) {
                        String artKey = "spotify|" + wt;
                        K47.a(new K47.C96(K47.C95.SPOTIFY, song, artist, true, 0L, 0L, System.currentTimeMillis(), artKey));
                        K42.a(artKey, song, artist);
                        lastValid = System.currentTimeMillis();
                    }
                } else if (System.currentTimeMillis() - lastValid > 1500L) {
                    K47.m();
                }
            } catch (Exception ex) {
                // ignore
            }
            try {
                Thread.sleep(1500L);
            } catch (InterruptedException ie) {
                return;
            }
        }
    }

    // Reads Spotify's window title via `tasklist`; null when not playing.
    private static String j() {
        try {
            Process p = new ProcessBuilder("tasklist", "/fi", "imagename eq Spotify.exe", "/v", "/fo", "csv", "/nh").start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));) {
                String line;
                while ((line = r.readLine()) != null) {
                    int i = line.lastIndexOf("\",\"");
                    if (i < 0) continue;
                    String wt = line.substring(i + 3);
                    if (wt.endsWith("\"")) {
                        wt = wt.substring(0, wt.length() - 1);
                    }
                    wt = wt.trim();
                    if (wt.isEmpty() || wt.equals("N/A")) continue;
                    String low = wt.toLowerCase();
                    if (low.equals("spotify") || low.startsWith("spotify premium") || low.startsWith("spotify free")) continue;
                    if (wt.contains(" - ")) {
                        return wt;
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    // Optional extra source: an external helper writing the JSON file.
    private static void i() {
        while (c) {
            try {
                if (Files.exists(FILE)) {
                    long age = System.currentTimeMillis() - Files.getLastModifiedTime(FILE).toMillis();
                    if (age <= 8000L) {
                        String content = Files.readString(FILE, StandardCharsets.UTF_8);
                        if (!content.isEmpty() && content.charAt(0) == '﻿') {
                            content = content.substring(1);
                        }
                        content = content.trim();
                        if (!content.isEmpty()) {
                            K48.a(content);
                        }
                    }
                }
            } catch (Exception exception) {
                // ignore
            }
            try {
                Thread.sleep(700L);
            } catch (InterruptedException interruptedException) {
                return;
            }
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
            if ((which = K48.b(app)) == K47.C95.NONE || title.isEmpty()) {
                // Don't clear on a single blank reading (the reader fires ~8x a
                // second and WinRT can briefly hiccup); only clear once the song
                // has really been gone for a moment, so the HUD doesn't flicker.
                if (System.currentTimeMillis() - lastValid > 1500L) {
                    K47.m();
                }
                return;
            }
            lastValid = System.currentTimeMillis();
            boolean playing = status.equals("playing");
            String artKey = app + "|" + title + "|" + artist;
            K47.a(new K47.C96(which, title, artist, playing, pos, dur, sampledAt, artKey));
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
