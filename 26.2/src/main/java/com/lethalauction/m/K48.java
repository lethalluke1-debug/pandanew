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

    private K48() {
    }

    public static boolean a() {
        return true;
    }

    public static boolean b() {
        return c && (!WIN || (d != null && d.isAlive()));
    }

    public static synchronized void c() {
        if (c) {
            return;
        }
        c = true;
        if (WIN) {
            h();
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

    // Extract the bundled reader script and launch it with "powershell -File".
    private static void h() {
        try {
            Files.createDirectories(MDIR);
            Path ps1 = MDIR.resolve("nowplaying.ps1");
            try (InputStream in = K48.class.getResourceAsStream("/assets/lethalauction/music/nowplaying.ps1")) {
                if (in != null) {
                    Files.copy(in, ps1, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            String[] args = new String[]{"-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", ps1.toString(), "read", MDIR.toString()};
            Process p;
            try {
                p = K48.a("powershell", args);
            } catch (Exception psErr) {
                p = K48.a("pwsh", args);
            }
            d = p;
            final Process fp = p;
            Thread r = new Thread(() -> K48.e(fp), "Lethal now-playing");
            r.setDaemon(true);
            e = r;
            r.start();
        } catch (Exception exception) {
            d = null;
        }
    }

    private static Process a(String exe, String[] args) throws Exception {
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(exe);
        for (String a : args) {
            cmd.add(a);
        }
        return new ProcessBuilder(cmd).start();
    }

    private static void e(Process p) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));) {
            String line;
            while (c && (line = r.readLine()) != null) {
                if (!line.isEmpty()) {
                    K48.a(line);
                }
            }
        } catch (Exception exception) {
            // ignore
        }
    }

    // Optional fallback: an external helper writing the JSON file.
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
            K47.C96 which;
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
            if ((which = K48.b(app)) == K47.C96.NONE || title.isEmpty()) {
                K47.m();
                return;
            }
            boolean playing = status.equals("playing");
            String artKey = app + "|" + title + "|" + artist;
            K47.a(new K47.C97(which, title, artist, playing, pos, dur, sampledAt, artKey));
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

    private static K47.C96 b(String id) {
        String s = id.toLowerCase();
        if (s.contains("spotify")) {
            return K47.C96.SPOTIFY;
        }
        if (s.contains("apple") || s.contains("itunes") || s.contains("music.ui")) {
            return K47.C96.APPLE;
        }
        return id.isEmpty() ? K47.C96.NONE : K47.C96.OTHER;
    }

    private static String a(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }
}
