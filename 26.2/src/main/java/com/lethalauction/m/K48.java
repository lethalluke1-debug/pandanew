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
    private static long songStart = 0L;            // when the current song was first seen
    private static String lastKey = "";            // current song key, to detect changes

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

    // The now-playing reader. We feed this script to `powershell -Command -`
    // over stdin instead of shipping a .ps1 file, so the jar carries no script
    // file and no "-ExecutionPolicy Bypass" (what antivirus flagged), while
    // still reading the real Windows media session: title, artist, position,
    // duration and play/paused. It prints one compact JSON line ~1x/second.
    private static final String PS = "Add-Type -AssemblyName System.Runtime.WindowsRuntime\n" +
        "$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\n" +
        "function Await($op, $t) { $task = $asTask.MakeGenericMethod($t).Invoke($null, @($op)); $task.Wait(-1) | Out-Null; $task.Result }\n" +
        "[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime] | Out-Null\n" +
        "$tm = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]\n" +
        "$tp = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]\n" +
        "$mgr = Await ($tm::RequestAsync()) $tm\n" +
        "while ($true) {\n" +
        "  try {\n" +
        "    $s = $mgr.GetCurrentSession()\n" +
        "    if ($s) {\n" +
        "      $p = Await ($s.TryGetMediaPropertiesAsync()) $tp\n" +
        "      $pb = $s.GetPlaybackInfo()\n" +
        "      $tl = $s.GetTimelineProperties()\n" +
        "      $st = if ([int]$pb.PlaybackStatus -eq 4) { 'playing' } else { 'paused' }\n" +
        "      $o = [ordered]@{ app=[string]$s.SourceAppUserModelId; title=[string]$p.Title; artist=[string]$p.Artist; status=$st; pos=[long]$tl.Position.TotalMilliseconds; dur=[long]$tl.EndTime.TotalMilliseconds; art=''; t=[long]([DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()) }\n" +
        "      [Console]::Out.WriteLine(($o | ConvertTo-Json -Compress)); [Console]::Out.Flush()\n" +
        "    }\n" +
        "  } catch { }\n" +
        "  Start-Sleep -Milliseconds 1000\n" +
        "}\n";

    private static void h() {
        try {
            Process p = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command", "-").redirectErrorStream(true).start();
            d = p;
            java.io.OutputStream os = p.getOutputStream();
            os.write(PS.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));) {
                String line;
                while (c && (line = r.readLine()) != null) {
                    if (!line.isEmpty()) {
                        K48.a(line);
                    }
                }
            }
        } catch (Exception ex) {
            d = null;
        }
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
