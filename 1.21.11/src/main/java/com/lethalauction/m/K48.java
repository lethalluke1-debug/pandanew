package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.m.K42;
import com.lethalauction.m.K45;
import com.lethalauction.m.K47;
import com.lethalauction.m.K9;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Now-playing source. On Windows the mod reads the current media from the
 * system media API. The reader script is piped to PowerShell over stdin — it is
 * never written to disk as a .ps1 file, which is the dropped-script pattern that
 * antivirus flagged before. It also still reads an optional
 * &lt;config&gt;/lethalauction/nowplaying.json written by a separate helper, for
 * anyone who would rather the mod launch nothing at all.
 */
public final class K48 {
    private static final boolean WIN = System.getProperty("os.name", "").toLowerCase().contains("win");
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
        .resolve("lethalauction").resolve("nowplaying.json");
    private static volatile boolean c;   // running
    private static Process d;            // reader process
    private static Thread e;             // stdout reader
    private static Thread g;             // file poller
    // Pause detection by position movement (Spotify's SMTC can report
    // "Playing" even while paused; if the track position stops advancing for a
    // couple of seconds we treat it as paused).
    private static volatile long lastPos = -1L;
    private static volatile long lastAdvanceWall = 0L;
    private static volatile String lastKey = "";

    private static final String SCRIPT = """
$ErrorActionPreference='SilentlyContinue'
$out='__OUTFILE__'
Add-Type -AssemblyName System.Runtime.WindowsRuntime
$asTask=([System.WindowsRuntimeSystemExtensions].GetMethods()|Where-Object{$_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'})[0]
function Await($op,$t){$x=$asTask.MakeGenericMethod($t).Invoke($null,@($op));$x.Wait(-1)|Out-Null;$x.Result}
[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]|Out-Null
$M=[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]
$P=[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
$mgr=$null
while($true){
 try{
  if($mgr -eq $null){$mgr=Await ($M::RequestAsync()) $M}
  $s=$mgr.GetCurrentSession()
  if($s){
   $p=Await ($s.TryGetMediaPropertiesAsync()) $P
   $pb=$s.GetPlaybackInfo();$tl=$s.GetTimelineProperties()
   $st=if([int]$pb.PlaybackStatus -eq 4){'playing'}else{'paused'}
   $o=[ordered]@{app=[string]$s.SourceAppUserModelId;title=[string]$p.Title;artist=[string]$p.Artist;status=$st;pos=[long]$tl.Position.TotalMilliseconds;dur=[long]$tl.EndTime.TotalMilliseconds;art='';t=[long]([DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds())}
   $j=($o|ConvertTo-Json -Compress)
  }else{$j='{}'}
  try{[IO.File]::WriteAllText($out,$j,[Text.Encoding]::UTF8)}catch{}
  [Console]::Out.WriteLine($j);[Console]::Out.Flush()
 }catch{$mgr=$null}
 Start-Sleep -Milliseconds 1000
}
""";

    private K48() {
    }

    public static boolean a() {
        return true;
    }

    public static boolean b() {
        // On Windows, report "running" only while the reader process is alive, so
        // the controller relaunches it automatically if the first start flakes
        // (no need to toggle off/on by hand).
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

    // Launch the system-media reader. The script is passed via -EncodedCommand
    // (never written to disk), and writes its readings to nowplaying.json each
    // second; the file poller reads that, which avoids PowerShell's buffering of
    // redirected stdout (that buffering made the HUD stop updating until toggled).
    private static void h() {
        try {
            try {
                Files.createDirectories(FILE.getParent());
            } catch (Exception ignore) {
                // ignore
            }
            String outPath = FILE.toString().replace("'", "''");
            String script = SCRIPT.replace("__OUTFILE__", outPath);
            String enc = java.util.Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));
            Process p = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-EncodedCommand", enc).start();
            d = p;
            Thread r = new Thread(() -> K48.e(p), "Lethal now-playing");
            r.setDaemon(true);
            e = r;
            r.start();
        } catch (Exception exception) {
            d = null;
        }
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
            boolean statusPlaying = status.equals("playing");
            String artKey = app + "|" + title + "|" + artist;
            long nowWall = System.currentTimeMillis();
            if (!artKey.equals(lastKey)) {
                lastKey = artKey;
                lastPos = pos;
                lastAdvanceWall = nowWall;
            } else if (pos > lastPos + 250L) {
                lastPos = pos;
                lastAdvanceWall = nowWall;
            }
            // Playing only if the status says so AND the position has actually
            // advanced within the last few seconds.
            boolean playing = statusPlaying && nowWall - lastAdvanceWall < 2500L;
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
