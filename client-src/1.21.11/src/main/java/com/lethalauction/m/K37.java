/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.metadata.ModOrigin
 *  net.minecraft.class_310
 */
package com.lethalauction.m;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.LethalAuctionClient;
import com.lethalauction.m.K14;
import com.lethalauction.m.K36;
import com.lethalauction.m.K38;
import com.lethalauction.m.K39;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.class_310;

public final class K37 {
    private static volatile boolean c;
    public static final String a;
    public static final String b;
    private static final String d = "MCowBQYDK2VwAyEA/heURKbfdnk9D0TdZtnFV3Kz8zTXtH8rGeJPzhMpyXQ=";
    private static final Path e;
    private static final boolean f = true;
    private static final boolean g = true;
    private static final long h = 60L;
    private static final long i = 15L;
    private static final long j = 2L;
    private static volatile long k;
    private static final HttpClient l;
    private static final ScheduledExecutorService m;
    private static volatile C91 n;
    private static volatile String o;
    private static volatile long p;
    private static String q;
    private static String r;
    private static boolean s;
    private static String t;
    private static boolean u;
    private static volatile boolean v;
    private static volatile boolean w;
    private static volatile String x;
    private static volatile int y;
    private static volatile int z;
    private static volatile Set<String> A;
    private static final SecureRandom B;
    private static volatile String C;

    private K37() {
    }

    public static boolean a() {
        return n == C91.ACTIVE;
    }

    public static boolean b() {
        return K37.a() && !w;
    }

    public static boolean c() {
        return K37.b() && c;
    }

    public static boolean d() {
        return true;
    }

    public static boolean e() {
        return switch (n.ordinal()) {
            case 2, 9 -> true;
            case 1, 7 -> true;
            default -> false;
        };
    }

    public static String f() {
        return t;
    }

    public static boolean g() {
        return n == C91.OUTDATED;
    }

    public static int h() {
        return y;
    }

    public static int i() {
        return z;
    }

    public static String j() {
        return x;
    }

    public static boolean k() {
        return w;
    }

    public static C91 l() {
        return n;
    }

    public static String m() {
        return o;
    }

    public static String n() {
        return q;
    }

    public static long o() {
        return p;
    }

    public static boolean p() {
        return s;
    }

    public static void q() {
        s = true;
        K37.v();
    }

    public static boolean r() {
        boolean lost = v;
        v = false;
        return lost;
    }

    public static void s() {
        t = K37.B();
        K39.e();
        if (Files.exists(e, new LinkOption[0])) {
            Properties p = new Properties();
            try (BufferedReader r = Files.newBufferedReader(e);){
                p.load(r);
            }
            catch (IOException e) {
                LethalAuctionClient.LOGGER.warn("Could not read {}", (Object)K37.e, (Object)e);
            }
            q = p.getProperty("key", "");
            r = p.getProperty("token", "");
            s = Boolean.parseBoolean(p.getProperty("introSeen", "false"));
        }
        if (!q.isEmpty() && K37.A()) {
            K37.a(C91.ACTIVE, "License active.");
        }
    }

    private static void v() {
        Properties p = new Properties();
        p.setProperty("key", q);
        p.setProperty("token", r);
        p.setProperty("introSeen", Boolean.toString(s));
        try {
            Files.createDirectories(e.getParent(), new FileAttribute[0]);
            try (BufferedWriter w = Files.newBufferedWriter(e, new OpenOption[0]);){
                p.store(w, "Lethal Client license");
            }
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", (Object)K37.e, (Object)e);
        }
    }

    public static CompletableFuture<C90> t() {
        return K37.a(K37.w());
    }

    private static String w() {
        try {
            UUID id = class_310.method_1551().method_1548().method_44717();
            if (id != null) {
                return id.toString();
            }
        }
        catch (Exception id) {
            // empty catch block
        }
        String name = "";
        try {
            name = class_310.method_1551().method_1548().method_1676();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return name.isEmpty() ? "FREE" : name;
    }

    public static synchronized CompletableFuture<C90> a(String newKey) {
        String k = newKey.trim().toUpperCase();
        if (k.isEmpty()) {
            return CompletableFuture.completedFuture(new C90(C91.INVALID, "Enter your license key."));
        }
        if (t == null) {
            C90 r2 = new C90(C91.NOT_SET_UP, "The license server has not been set up yet.");
            if (!K37.a()) {
                K37.a(r2.b(), r2.c());
            }
            return CompletableFuture.completedFuture(r2);
        }
        boolean sameKey = k.equals(q);
        if (!K37.a()) {
            K37.a(C91.CHECKING, "Checking your key...");
        }
        return CompletableFuture.supplyAsync(() -> K37.b(k), m).thenApply(r -> {
            Class<K37> clazz = K37.class;
            synchronized (K37.class) {
                boolean wasActive = K37.a();
                boolean wasOutdated = K37.g();
                if (r.a()) {
                    if (K37.k == 0L) {
                        K37.k = System.currentTimeMillis();
                    }
                    q = k;
                    K37.r = r.c;
                    p = r.d;
                    K37.v();
                    K37.a(C91.ACTIVE, r.b);
                    c = true;
                    w = r.e;
                    K37.x();
                } else if (sameKey || !wasActive) {
                    if (r.a == C91.OFFLINE) {
                        if (sameKey && K37.A()) {
                            K37.a(C91.ACTIVE, "License active (offline).");
                            c = true;
                            // ** MonitorExit[var3_3] (shouldn't be in output)
                            return new C90(C91.ACTIVE, "License active (offline).");
                        }
                        K37.a(C91.OFFLINE, r.b);
                    } else {
                        if (sameKey && r.a != C91.OUTDATED) {
                            K37.r = "";
                            K37.v();
                        }
                        K37.a(r.a, r.b);
                        c = false;
                        if (r.a == C91.OUTDATED) {
                            q = k;
                            K37.v();
                            K37.x();
                        }
                        if (wasActive || wasOutdated && r.a != C91.OUTDATED) {
                            v = true;
                        }
                    }
                }
                // ** MonitorExit[var3_3] (shouldn't be in output)
                return new C90(r.a, r.b);
            }
        });
    }

    public static synchronized void u() {
        c = false;
        q = "";
        r = "";
        K37.v();
        K37.a(C91.NONE, "");
    }

    private static void x() {
        if (!u) {
            u = true;
            K37.y();
        }
    }

    private static void y() {
        m.scheduleWithFixedDelay(() -> {
            long seconds = K38.a() ? 2L : (n == C91.OUTDATED ? 15L : 60L);
            long interval = seconds * 1000L;
            if (!(q.isEmpty() || n != C91.ACTIVE && n != C91.OFFLINE && n != C91.OUTDATED || System.currentTimeMillis() - k < interval)) {
                k = System.currentTimeMillis();
                K37.a(q);
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    public static boolean a(long minGapMs) {
        long now = System.currentTimeMillis();
        if (!q.isEmpty() && K37.e() && now - k > minGapMs) {
            k = now;
            K37.a(q);
            return true;
        }
        return false;
    }

    private static String z() {
        String h;
        block4: {
            h = C;
            if (h != null) {
                return h;
            }
            h = "";
            try {
                Optional<ModOrigin> origin = FabricLoader.getInstance().getModContainer("lethalauction").map(c -> c.getOrigin());
                if (!origin.isPresent()) break block4;
                for (Path p : origin.get().getPaths()) {
                    if (!Files.isRegularFile(p, new LinkOption[0])) continue;
                    byte[] dig = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(p));
                    h = HexFormat.of().formatHex(dig);
                    break;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        C = h;
        return h;
    }

    private static C89 b(String k) {
        String updateSha;
        HttpResponse<String> response;
        byte[] nonceBytes = new byte[16];
        B.nextBytes(nonceBytes);
        String nonce = HexFormat.of().formatHex(nonceBytes);
        JsonObject body = new JsonObject();
        body.addProperty("key", k);
        body.addProperty("keyless", Boolean.valueOf(true));
        body.addProperty("hwid", K36.a());
        body.addProperty("user", class_310.method_1551().method_1548().method_1676());
        body.addProperty("nonce", nonce);
        body.addProperty("version", a);
        body.addProperty("mc", b);
        body.addProperty("build", K37.z());
        K38.a(body);
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(t + "/api/verify")).timeout(Duration.ofSeconds(12L)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body.toString())).build();
            response = l.send(req, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException e) {
            return new C89(C91.OFFLINE, "Can't reach the license server. Check your internet.", "", 0L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new C89(C91.OFFLINE, "Can't reach the license server.", "", 0L);
        }
        catch (IllegalArgumentException e) {
            return new C89(C91.NOT_SET_UP, "The license server address is wrong.", "", 0L);
        }
        String raw = response.body();
        JsonObject payload = K37.c(raw);
        if (!(payload != null && k.equals(K37.a(payload, "key")) && K36.a().equals(K37.a(payload, "hwid")) && nonce.equals(K37.a(payload, "nonce")))) {
            LethalAuctionClient.LOGGER.warn("License server sent an unsigned or mismatched answer (HTTP {})", (Object)response.statusCode());
            return new C89(C91.OFFLINE, "The license server gave a bad answer. Try again later.", "", 0L);
        }
        String answerStatus = K37.a(payload, "status");
        if ("active".equals(answerStatus) || "outdated".equals(answerStatus)) {
            y = K14.a(K37.a(payload, "rank"));
            z = K14.a(K37.a(payload, "rank2"));
            ConcurrentHashMap.KeySetView<String, Boolean> set = ConcurrentHashMap.newKeySet();
            if (payload.has("owned") && payload.get("owned").isJsonArray()) {
                for (JsonElement e : payload.getAsJsonArray("owned")) {
                    try {
                        set.add(e.getAsString());
                    }
                    catch (RuntimeException runtimeException) {}
                }
            }
            A = set;
        }
        if ("active".equals(answerStatus) || "outdated".equals(answerStatus)) {
            try {
                K38.a(JsonParser.parseString((String)raw).getAsJsonObject(), t, k);
            }
            catch (Exception e) {
                LethalAuctionClient.LOGGER.warn("Could not read the player list", (Throwable)e);
            }
        }
        K39.a(!(updateSha = K37.a(payload, "updateSha")).isEmpty());
        if (!updateSha.isEmpty()) {
            String updateVersion = K37.a(payload, "updateVersion");
            m.execute(() -> K39.a(t, k, updateSha, updateVersion));
        }
        String msg = K37.a(payload, "message");
        return switch (K37.a(payload, "status")) {
            case "active" -> new C89(C91.ACTIVE, msg, raw, payload.has("expires") ? payload.get("expires").getAsLong() : 0L, payload.has("maintenance") && payload.get("maintenance").getAsBoolean());
            case "revoked" -> new C89(C91.REVOKED, msg, "", 0L);
            case "expired" -> new C89(C91.EXPIRED, msg, "", 0L);
            case "hwid_mismatch" -> new C89(C91.IN_USE, msg, "", 0L);
            case "outdated" -> {
                x = K37.a(payload, "required");
                yield new C89(C91.OUTDATED, msg, "", 0L);
            }
            default -> new C89(C91.INVALID, msg.isEmpty() ? "Invalid license key." : msg, "", 0L);
        };
    }

    private static boolean A() {
        JsonObject payload = K37.c(r);
        if (payload == null || !"active".equals(K37.a(payload, "status"))) {
            return false;
        }
        if (!q.equals(K37.a(payload, "key")) || !K36.a().equals(K37.a(payload, "hwid"))) {
            return false;
        }
        long now = System.currentTimeMillis();
        long issued = payload.has("issued") ? payload.get("issued").getAsLong() : 0L;
        long validUntil = payload.has("validUntil") ? payload.get("validUntil").getAsLong() : 0L;
        p = payload.has("expires") ? payload.get("expires").getAsLong() : 0L;
        y = K14.a(K37.a(payload, "rank"));
        z = K14.a(K37.a(payload, "rank2"));
        return now < validUntil && now > issued - 300000L;
    }

    private static JsonObject c(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        try {
            JsonObject outer = JsonParser.parseString((String)raw).getAsJsonObject();
            String payload = outer.get("payload").getAsString();
            byte[] sig = Base64.getDecoder().decode(outer.get("sig").getAsString());
            PublicKey pub = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(d)));
            Signature verifier = Signature.getInstance("Ed25519");
            verifier.initVerify(pub);
            verifier.update(payload.getBytes(StandardCharsets.US_ASCII));
            if (!verifier.verify(sig)) {
                return null;
            }
            return JsonParser.parseString((String)new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8)).getAsJsonObject();
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String a(JsonObject o, String name) {
        return o.has(name) && !o.get(name).isJsonNull() ? o.get(name).getAsString() : "";
    }

    private static C91 a(C91 s, String msg) {
        n = s;
        o = msg;
        return s;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String B() {
        String override = System.getProperty("lethalauction.licenseServer");
        if (override != null && !override.isBlank()) {
            return override.replaceAll("/+$", "");
        }
        try (InputStream in = K37.class.getResourceAsStream("/assets/lethalauction/license.json");){
            if (in == null) {
                String string2 = null;
                return string2;
            }
            JsonObject o = JsonParser.parseString((String)new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            String url = K37.a(o, "server").trim().replaceAll("/+$", "");
            String string = url.startsWith("http") && !url.contains("CHANGE-ME") ? url : null;
            return string;
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not read license.json", (Throwable)e);
            return null;
        }
    }

    static {
        a = FabricLoader.getInstance().getModContainer("lethalauction").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0");
        b = FabricLoader.getInstance().getModContainer("minecraft").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
        e = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-license.properties");
        l = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
        m = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Lethal Client license");
            t.setDaemon(true);
            return t;
        });
        n = C91.NONE;
        o = "";
        q = "";
        K37.r = "";
        x = "";
        A = Set.of();
        B = new SecureRandom();
    }

    public static enum C91 {
        NONE,
        CHECKING,
        ACTIVE,
        INVALID,
        REVOKED,
        EXPIRED,
        IN_USE,
        OFFLINE,
        NOT_SET_UP,
        OUTDATED;

    }

    public static final class C90 {
        private final C91 a;
        private final String b;
        C90(C91 a, String b) { this.a = a; this.b = b; }

        public boolean a() {
            return this.a == C91.ACTIVE;
        }




        public C91 b() {
            return this.a;
        }

        public String c() {
            return this.b;
        }
    }

    private static final class C89 {
        private final C91 a;
        private final String b;
        private final String c;
        private final long d;
        private final boolean e;

        C89(C91 status, String message, String token, long expires) {
            this(status, message, token, expires, false);
        }

        private C89(C91 status, String message, String token, long expires, boolean maintenance) {
            this.a = status;
            this.b = message;
            this.c = token;
            this.d = expires;
            this.e = maintenance;
        }

        boolean a() {
            return this.a == C91.ACTIVE;
        }




        public C91 b() {
            return this.a;
        }

        public String c() {
            return this.b;
        }

        public String d() {
            return this.c;
        }

        public long e() {
            return this.d;
        }

        public boolean f() {
            return this.e;
        }
    }
}

