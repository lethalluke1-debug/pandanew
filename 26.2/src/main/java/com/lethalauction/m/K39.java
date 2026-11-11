/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.metadata.ModOrigin
 *  net.fabricmc.loader.api.metadata.ModOrigin$Kind
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.m.K36;
import com.lethalauction.m.K37;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;

public final class K39 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(HttpClient.Redirect.NORMAL).build();
    private static Path b;
    private static String c;
    private static volatile String d;
    private static volatile String e;
    private static volatile C93 f;
    private static volatile float g;
    private static volatile boolean h;
    private static boolean i;

    private K39() {
    }

    public static C93 a() {
        return f;
    }

    public static String b() {
        return e;
    }

    public static float c() {
        return f == C93.READY ? 1.0f : g;
    }

    public static boolean d() {
        return h || f != C93.NONE;
    }

    static void a(boolean value) {
        h = value;
    }

    static void e() {
        try {
            String testJar = System.getProperty("lethalauction.testJar");
            if (testJar != null && !testJar.isBlank()) {
                b = Path.of(testJar, new String[0]);
                c = K39.a(Files.readAllBytes(b));
                K39.f();
                return;
            }
            ModOrigin origin = ((ModContainer)FabricLoader.getInstance().getModContainer("lethalauction").orElseThrow()).getOrigin();
            if (origin.getKind() != ModOrigin.Kind.PATH) {
                return;
            }
            List paths = origin.getPaths();
            if (paths.size() != 1 || !((Path)paths.get(0)).toString().toLowerCase(Locale.ROOT).endsWith(".jar") || !Files.isRegularFile((Path)paths.get(0), new LinkOption[0])) {
                return;
            }
            b = (Path)paths.get(0);
            c = K39.a(Files.readAllBytes(b));
            K39.f();
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Lethal Client updater could not start", (Throwable)e);
            b = null;
        }
    }

    private static void f() throws IOException {
        try (Stream<Path> files = Files.list(b.getParent());){
            files.filter(f -> f.getFileName().toString().endsWith(".jar.old")).forEach(f -> {
                try {
                    Files.deleteIfExists(f);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            });
        }
        Path update = K39.g();
        if (!Files.exists(update, new LinkOption[0])) {
            return;
        }
        String sha = K39.a(Files.readAllBytes(update));
        if (sha.equals(c)) {
            Files.deleteIfExists(update);
            Files.deleteIfExists(K39.h());
        } else {
            d = sha;
            e = Files.exists(K39.h(), new LinkOption[0]) ? Files.readString(K39.h()).trim() : "";
            K39.f = C93.READY;
            if (!K39.j()) {
                K39.k();
            }
        }
    }

    static void a(String server, String key, String sha, String version) {
        if (b == null || sha.isEmpty() || sha.equals(c) || sha.equals(d) || K39.a(version, K37.a) <= 0) {
            return;
        }
        g = 0.0f;
        f = C93.DOWNLOADING;
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(server + "/api/update")).timeout(Duration.ofSeconds(90L)).header("x-key", key).header("x-hwid", K36.a()).header("x-mc", K37.b).GET().build();
            HttpResponse<InputStream> response = a.send(req, HttpResponse.BodyHandlers.ofInputStream());
            long total = response.headers().firstValueAsLong("content-length").orElse(-1L);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = response.body();){
                int n;
                byte[] buf = new byte[16384];
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                    if (total <= 0L) continue;
                    g = Math.min(0.99f, (float)out.size() / (float)total);
                }
            }
            byte[] jar = out.toByteArray();
            if (response.statusCode() != 200 || !K39.a(jar).equals(sha)) {
                LethalAuctionClient.LOGGER.info("Lethal Client update not ready yet (HTTP {})", (Object)response.statusCode());
                f = d.isEmpty() ? C93.NONE : C93.READY;
                return;
            }
            Path update = K39.g();
            Path part = update.resolveSibling(String.valueOf(update.getFileName()) + ".part");
            Files.write(part, jar, new OpenOption[0]);
            Files.move(part, update, StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(K39.h(), (CharSequence)version, new OpenOption[0]);
            d = sha;
            e = version;
            f = C93.READY;
            LethalAuctionClient.LOGGER.info("Lethal Client {} downloaded; it is used from the next start", (Object)version);
            if (!K39.j()) {
                K39.k();
            }
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Lethal Client update download failed", (Throwable)e);
            f = d.isEmpty() ? C93.NONE : C93.READY;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Path g() {
        return b.resolveSibling("lethal-client.update");
    }

    private static Path h() {
        return b.resolveSibling("lethal-client.update.version");
    }

    private static Path i() {
        return b.resolveSibling("Lethal Client.jar");
    }

    private static synchronized boolean j() {
        Path update = K39.g();
        if (!Files.exists(update, new LinkOption[0]) || !Files.exists(b, new LinkOption[0])) {
            return false;
        }
        Path target = K39.i();
        try {
            Path old = b.resolveSibling(String.valueOf(b.getFileName()) + ".old");
            Files.move(b, old, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(update, target, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                Files.move(old, b, StandardCopyOption.REPLACE_EXISTING);
                throw e;
            }
            try {
                Files.deleteIfExists(old);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            Files.deleteIfExists(K39.h());
            b = target;
            LethalAuctionClient.LOGGER.info("Lethal Client update put in place as {}", (Object)target.getFileName());
            return true;
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.info("Lethal Client update will be put in place when the game closes ({})", (Object)e.toString());
            return false;
        }
    }

    private static synchronized void k() {
        if (i) {
            return;
        }
        i = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Path update = K39.g();
            if (!Files.exists(update, new LinkOption[0])) {
                return;
            }
            Path target = K39.i();
            try {
                if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
                    K39.a(b, update, K39.h(), target);
                } else {
                    Files.move(update, target, StandardCopyOption.REPLACE_EXISTING);
                    if (!target.equals(b)) {
                        Files.deleteIfExists(b);
                    }
                    Files.deleteIfExists(K39.h());
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }, "Lethal Client update"));
    }

    private static void a(Path jar, Path update, Path version, Path target) throws IOException {
        Path script = Files.createTempFile("lethal-client-update", ".vbs", new FileAttribute[0]);
        Files.writeString(script, (CharSequence)("\ufeff" + String.join((CharSequence)"\r\n", "On Error Resume Next", "Set fso = CreateObject(\"Scripting.FileSystemObject\")", "If WScript.Arguments.Count = 0 Then", "  Set wmi = GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\.\\root\\cimv2\")", "  Set startup = wmi.Get(\"Win32_ProcessStartup\").SpawnInstance_", "  startup.ShowWindow = 0", "  result = wmi.Get(\"Win32_Process\").Create(\"wscript.exe //B //Nologo \"\"\" & WScript.ScriptFullName & \"\"\" go\", Null, startup, pid)", "  If Err.Number <> 0 Or result <> 0 Then", "    Err.Clear", "    CreateObject(\"WScript.Shell\").Run \"wscript.exe //B //Nologo \"\"\" & WScript.ScriptFullName & \"\"\" go\", 0, False", "  End If", "  WScript.Quit", "End If", "jar = " + K39.a(jar), "upd = " + K39.a(update), "ver = " + K39.a(version), "dst = " + K39.a(target), "For i = 1 To 120", "  If fso.FileExists(jar) Then fso.DeleteFile jar, True", "  Err.Clear", "  If Not fso.FileExists(jar) Then", "    If fso.FileExists(dst) Then fso.DeleteFile dst, True", "    fso.MoveFile upd, dst", "    If fso.FileExists(ver) Then fso.DeleteFile ver, True", "    Exit For", "  End If", "  WScript.Sleep 500", "Next", "fso.DeleteFile WScript.ScriptFullName, True", "")), StandardCharsets.UTF_16LE, new OpenOption[0]);
        Process p = new ProcessBuilder("wscript.exe", "//B", "//Nologo", script.toAbsolutePath().toString()).start();
        try {
            p.waitFor(10L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String a(Path path) {
        return "\"" + path.toAbsolutePath().toString().replace("\"", "\"\"") + "\"";
    }

    static int a(String a, String b) {
        String[] pa = a.split("[^0-9]+");
        String[] pb = b.split("[^0-9]+");
        int n = Math.max(pa.length, pb.length);
        for (int i = 0; i < n; ++i) {
            long y;
            long x = i < pa.length && !pa[i].isEmpty() ? Long.parseLong(pa[i]) : 0L;
            long l = y = i < pb.length && !pb[i].isEmpty() ? Long.parseLong(pb[i]) : 0L;
            if (x == y) continue;
            return Long.compare(x, y);
        }
        return 0;
    }

    private static String a(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static {
        c = "";
        d = "";
        e = "";
        f = C93.NONE;
    }

    public static enum C93 {
        NONE,
        DOWNLOADING,
        READY;

    }
}

