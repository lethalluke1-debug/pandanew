/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 */
package com.lethalauction.m;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lethalauction.LethalAuctionClient;
import com.lethalauction.m.K1;
import com.lethalauction.m.K14;
import com.lethalauction.m.K36;
import com.lethalauction.m.K37;
import com.lethalauction.m.K4;
import com.lethalauction.m.K6;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class K38 {
    private static final HttpClient a = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
    private static final int b = 300;
    private static final Map<UUID, String> c = new ConcurrentHashMap<UUID, String>();
    private static final Map<UUID, Integer> d = new ConcurrentHashMap<UUID, Integer>();
    private static final Map<UUID, Integer> e = new ConcurrentHashMap<UUID, Integer>();
    private static final Map<UUID, String> f = new ConcurrentHashMap<UUID, String>();
    private static final Map<UUID, String> g = new ConcurrentHashMap<UUID, String>();
    private static String h;
    private static String i;
    private static String j;
    private static final Path k;
    private static final int l = 3000;
    private static boolean m;
    private static volatile List<String> n;
    private static volatile List<String> o;
    private static volatile boolean p;
    private static volatile String q;
    private static volatile Path r;
    private static long s;
    private static int t;
    private static boolean u;
    private static long v;
    private static long w;
    private static final Deque<Long> x;
    private static final Map<String, Identifier> y;
    private static final Set<String> z;
    private static final Set<String> A;
    private static final Set<String> B;
    private static volatile long C;

    private K38() {
    }

    public static void a(Minecraft mc) {
        if (!m) {
            m = true;
            K38.d();
        }
        if (++t % 20 != 1) {
            return;
        }
        boolean capeChanged = false;
        ArrayList<String> list = new ArrayList<String>();
        ClientPacketListener connection = mc.getConnection();
        if (connection != null) {
            UUID self = mc.getUser().getProfileId();
            for (PlayerInfo info : connection.getOnlinePlayers()) {
                UUID id = info.getProfile().id();
                if (id == null || id.equals(self) || list.size() >= 300) continue;
                list.add(id.toString());
            }
        }
        if (!Set.copyOf(n).containsAll(list)) {
            w = System.currentTimeMillis();
        }
        n = List.copyOf(list);
        boolean any = K37.h() > 0;
        for (int i = 0; !any && i < list.size(); ++i) {
            any = c.containsKey(UUID.fromString((String)list.get(i)));
        }
        p = any;
        String animated = K4.e();
        K4.C63 cape = animated == null ? K4.g() : null;
        Path file = cape == null ? null : K4.a.resolve(cape.a());
        try {
            if (animated != null) {
                if (!animated.equals(q)) {
                    capeChanged = u;
                }
                q = animated;
                r = null;
            } else if (file == null) {
                if (!q.isEmpty()) {
                    capeChanged = true;
                }
                q = "";
                r = null;
            } else {
                long modified = Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
                if (!file.equals(r) || modified != s) {
                    boolean firstLook = r == null && !u;
                    q = K38.a(Files.readAllBytes(file));
                    r = file;
                    s = modified;
                    if (!firstLook && K37.a() && K37.f() != null) {
                        K38.b(K37.f(), K37.n());
                    }
                }
            }
        }
        catch (Exception e) {
            q = "";
            r = null;
        }
        u = true;
        if (h != null && !h.equals(K9.aG)) {
            capeChanged = true;
        }
        h = K9.aG;
        if (i != null && !i.equals(K9.aH)) {
            capeChanged = true;
        }
        i = K9.aH;
        String figNow = K9.aI;
        if (j != null && !j.equals(figNow)) {
            capeChanged = true;
        }
        j = figNow;
        if (capeChanged) {
            K37.a(1500L);
        } else if (w > v && System.currentTimeMillis() - v > 4000L && K37.a(1000L)) {
            long now;
            v = now = System.currentTimeMillis();
            x.clear();
            x.add(now + 3000L);
            x.add(now + 7000L);
            x.add(now + 15000L);
        } else if (!x.isEmpty() && System.currentTimeMillis() >= x.peekFirst() && K37.a(3000L)) {
            x.pollFirst();
        }
    }

    static boolean a() {
        for (String id : n) {
            if (!c.containsKey(UUID.fromString(id))) continue;
            return true;
        }
        return false;
    }

    static void a(JsonObject body) {
        body.addProperty("uuid", Minecraft.getInstance().getUser().getProfileId().toString());
        body.addProperty("cape", q);
        body.addProperty("cos", K6.a());
        body.addProperty("fig", K9.aI);
        JsonArray arr = new JsonArray();
        List<String> list = n;
        for (String id : list) {
            arr.add(id);
        }
        body.add("nearby", (JsonElement)arr);
        o = list;
    }

    static void a(JsonObject outer, String server, String key) {
        C = System.currentTimeMillis();
        if (outer.has("needCape") && !outer.get("needCape").getAsBoolean() && !q.isEmpty()) {
            B.add(q);
        }
        if (outer.has("players") && outer.get("players").isJsonObject()) {
            HashMap<UUID, String> next = new HashMap<UUID, String>();
            for (java.util.Map.Entry e : outer.getAsJsonObject("players").entrySet()) {
                try {
                    next.put(UUID.fromString((String)e.getKey()), ((JsonElement)e.getValue()).isJsonNull() ? "" : ((JsonElement)e.getValue()).getAsString());
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            }
            HashMap<UUID, String> cosmetics = new HashMap<UUID, String>();
            if (outer.has("cosmetics") && outer.get("cosmetics").isJsonObject()) {
                for (java.util.Map.Entry e : outer.getAsJsonObject("cosmetics").entrySet()) {
                    try {
                        cosmetics.put(UUID.fromString((String)e.getKey()), ((JsonElement)e.getValue()).getAsString());
                    }
                    catch (RuntimeException runtimeException) {}
                }
            }
            HashMap<UUID, String> figures = new HashMap<UUID, String>();
            if (outer.has("figures") && outer.get("figures").isJsonObject()) {
                for (java.util.Map.Entry e : outer.getAsJsonObject("figures").entrySet()) {
                    try {
                        figures.put(UUID.fromString((String)e.getKey()), ((JsonElement)e.getValue()).getAsString());
                    }
                    catch (RuntimeException runtimeException) {}
                }
            }
            HashMap<UUID, Integer> ranks = new HashMap<UUID, Integer>();
            if (outer.has("ranks") && outer.get("ranks").isJsonObject()) {
                for (java.util.Map.Entry e : outer.getAsJsonObject("ranks").entrySet()) {
                    try {
                        ranks.put(UUID.fromString((String)e.getKey()), K14.a(((JsonElement)e.getValue()).getAsString()));
                    }
                    catch (RuntimeException runtimeException) {}
                }
            }
            HashMap<UUID, Integer> ranks2 = new HashMap<UUID, Integer>();
            if (outer.has("ranks2") && outer.get("ranks2").isJsonObject()) {
                for (Map.Entry e : outer.getAsJsonObject("ranks2").entrySet()) {
                    try {
                        ranks2.put(UUID.fromString((String)e.getKey()), K14.a(((JsonElement)e.getValue()).getAsString()));
                    }
                    catch (RuntimeException runtimeException) {}
                }
            }
            boolean changed = false;
            for (String id : o) {
                Integer r2;
                String before;
                UUID uuid = UUID.fromString(id);
                String cape = (String)next.get(uuid);
                String string = before = cape == null ? c.remove(uuid) : c.put(uuid, cape);
                changed |= cape == null ? before != null : !cape.equals(before);
                String cos = cape == null ? null : (String)cosmetics.get(uuid);
                String cosBefore = cos == null || cos.isEmpty() ? f.remove(uuid) : f.put(uuid, cos);
                changed |= !Objects.equals(cosBefore, cos == null || cos.isEmpty() ? null : cos);
                String figv = cape == null ? null : (String)figures.get(uuid);
                String figBefore = figv == null || figv.isEmpty() ? g.remove(uuid) : g.put(uuid, figv);
                changed |= !Objects.equals(figBefore, figv == null || figv.isEmpty() ? null : figv);
                Integer r = cape == null ? null : (Integer)ranks.get(uuid);
                Integer rankBefore = r == null || r == 0 ? d.remove(uuid) : d.put(uuid, r);
                changed |= !Objects.equals(rankBefore, r == null || r == 0 ? null : r);
                Integer n = r2 = cape == null ? null : (Integer)ranks2.get(uuid);
                if (r2 == null || r2 == 0) {
                    e.remove(uuid);
                    continue;
                }
                e.put(uuid, r2);
            }
            if (changed) {
                K38.e();
            }
        }
        if (outer.has("needCape") && outer.get("needCape").getAsBoolean()) {
            K38.b(server, key);
        }
    }

    public static String b() {
        long ago;
        long l = ago = C == 0L ? -1L : (System.currentTimeMillis() - C) / 1000L;
        String cape = q.isEmpty() ? "no cape picked" : (B.contains(q) ? "your cape is shared" : "sharing your cape...");
        long online = n.stream().filter(id -> c.containsKey(UUID.fromString(id))).count();
        return online + " Lethal Client " + (online == 1L ? "user" : "users") + " online with you (of " + n.size() + " players) \u00b7 " + cape + (String)(ago < 0L ? " \u00b7 not checked yet" : " \u00b7 checked " + ago + "s ago");
    }

    public static String a(UUID id) {
        return f.getOrDefault(id, "");
    }

    public static String b(UUID id) {
        return g.getOrDefault(id, "");
    }

    public static int c(UUID id) {
        return d.getOrDefault(id, 0);
    }

    public static int d(UUID id) {
        return e.getOrDefault(id, 0);
    }

    public static boolean c() {
        return p;
    }

    public static boolean e(UUID id) {
        return c.containsKey(id);
    }

    public static Identifier a(UUID id, String server) {
        String sha = c.get(id);
        if (sha == null || sha.isEmpty()) {
            return null;
        }
        if (K1.a(sha)) {
            return K1.b(sha);
        }
        Identifier tex = y.get(sha);
        if (tex == null && server != null && z.add(sha)) {
            K38.a(sha, server);
        }
        return tex;
    }

    private static void d() {
        if (!Files.exists(k, new LinkOption[0])) {
            return;
        }
        Properties p = new Properties();
        try (BufferedReader r = Files.newBufferedReader(k);){
            p.load(r);
            for (String name : p.stringPropertyNames()) {
                try {
                    int savedRank;
                    String[] fields = p.getProperty(name, "").split("\\|", -1);
                    UUID uuid = UUID.fromString(name);
                    c.put(uuid, fields[0]);
                    if (fields.length > 1 && !fields[1].isEmpty() && (savedRank = Integer.parseInt(fields[1])) > 0) {
                        d.put(uuid, savedRank);
                    }
                    if (fields.length <= 2 || fields[2].isEmpty()) continue;
                    f.put(uuid, fields[2]);
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            }
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not read {}", (Object)k, (Object)e);
        }
    }

    private static synchronized void e() {
        Properties p = new Properties();
        c.entrySet().stream().limit(3000L).forEach(e -> {
            int r = d.getOrDefault(e.getKey(), 0);
            String cos = f.getOrDefault(e.getKey(), "");
            p.setProperty(((UUID)e.getKey()).toString(), (String)e.getValue() + "|" + r + "|" + cos);
        });
        try (BufferedWriter w = Files.newBufferedWriter(k, new OpenOption[0]);){
            p.store(w, "Lethal Client users seen with you, so their badge shows straight away");
        }
        catch (Exception e2) {
            LethalAuctionClient.LOGGER.warn("Could not save {}", (Object)k, (Object)e2);
        }
    }

    private static void a(String sha, String server) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(server + "/api/cape/" + sha)).timeout(Duration.ofSeconds(30L)).GET().build();
        ((CompletableFuture)a.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(response -> {
            byte[] png = (byte[])response.body();
            if (response.statusCode() != 200 || !K38.a(png).equals(sha)) {
                z.remove(sha);
                return;
            }
            Minecraft.getInstance().execute(() -> {
                try {
                    NativeImage image = NativeImage.read((byte[])png);
                    Identifier id = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("shared_capes/" + sha));
                    Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "Lethal Client shared cape " + sha, image));
                    y.put(sha, id);
                }
                catch (Exception e) {
                    LethalAuctionClient.LOGGER.warn("Could not load a shared cape", (Throwable)e);
                }
            });
        })).exceptionally(e -> {
            z.remove(sha);
            return null;
        });
    }

    private static void b(String server, String key) {
        Path file = K38.r;
        String sha = q;
        if (file == null || sha.isEmpty() || !A.add(sha)) {
            return;
        }
        try {
            byte[] png = Files.readAllBytes(file);
            HttpRequest req = HttpRequest.newBuilder(URI.create(server + "/api/cape")).timeout(Duration.ofSeconds(60L)).header("Content-Type", "image/png").header("x-key", key).header("x-hwid", K36.a()).POST(HttpRequest.BodyPublishers.ofByteArray(png)).build();
            a.sendAsync(req, HttpResponse.BodyHandlers.discarding()).whenComplete((r, e) -> {
                if (e != null || r.statusCode() != 200) {
                    A.remove(sha);
                } else {
                    B.add(sha);
                    K37.a(0L);
                }
            });
        }
        catch (Exception e2) {
            A.remove(sha);
        }
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
        k = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-users.properties");
        n = List.of();
        o = List.of();
        q = "";
        x = new ArrayDeque<Long>();
        y = new ConcurrentHashMap<String, Identifier>();
        z = ConcurrentHashMap.newKeySet();
        A = ConcurrentHashMap.newKeySet();
        B = ConcurrentHashMap.newKeySet();
    }
}

