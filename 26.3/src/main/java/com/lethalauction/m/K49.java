/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.lethalauction.m;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lethalauction.LethalAuctionClient;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.platform.NativeImage;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class K49 {
    public static final Path a = FabricLoader.getInstance().getConfigDir().resolve("lethalauction").resolve("skins");
    private static final Path e = FabricLoader.getInstance().getConfigDir().resolve("lethalauction").resolve("original-skin.png");
    public static final Path b = FabricLoader.getInstance().getConfigDir().resolve("lethalauction").resolve("skin-history");
    public static final int c = 5;
    public static final int d = 12;
    private static final HttpClient f = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(HttpClient.Redirect.NORMAL).build();
    private static final Map<String, C99> g = new LinkedHashMap<String, C99>();
    private static final Map<String, C98> h = new LinkedHashMap<String, C98>();
    private static boolean i;
    private static boolean j;
    private static volatile boolean k;
    private static volatile boolean l;
    private static volatile String m;
    private static volatile long n;
    private static C99 o;

    private K49() {
    }

    public static List<C99> a() {
        if (!i) {
            K49.f();
        }
        return new ArrayList<C99>(g.values());
    }

    public static boolean b() {
        return K49.a().size() >= 5;
    }

    public static C99 c() {
        String name = K9.aN;
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (!i) {
            K49.f();
        }
        return g.get(name);
    }

    public static String d() {
        return System.currentTimeMillis() < n || l ? m : "";
    }

    public static boolean e() {
        return l;
    }

    private static void a(String text, long ms) {
        m = text;
        n = System.currentTimeMillis() + ms;
    }

    public static void f() {
        i = true;
        try {
            Files.createDirectories(a, new FileAttribute[0]);
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not create {}", (Object)a, (Object)e);
            return;
        }
        ArrayList<Path> files = new ArrayList<Path>();
        try (Stream<Path> s = Files.list(a);){
            s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")).sorted().forEach(files::add);
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not list {}", (Object)a, (Object)e);
        }
        ArrayList<String> present = new ArrayList<String>();
        for (Path file : files) {
            C99 skin;
            String name = file.getFileName().toString();
            present.add(name);
            if (g.containsKey(name) || (skin = K49.a(file, "skins/" + K49.c(name))) == null) continue;
            g.put(name, skin);
        }
        for (String name : new ArrayList<String>(g.keySet())) {
            if (present.contains(name)) continue;
            Minecraft.getInstance().getTextureManager().release(g.remove(name).b());
        }
    }

    private static String c(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static C99 a(Path file, String path) {
        try (InputStream in = Files.newInputStream(file, new OpenOption[0]);){
            NativeImage image = NativeImage.read((InputStream)in);
            if (image.getWidth() != 64 || image.getHeight() != 64) {
                image.close();
                C99 c992 = null;
                return c992;
            }
            Identifier id = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)path);
            boolean slim = K49.a(image);
            Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "Lethal Client skin " + path, image));
            C99 c99 = new C99(file.getFileName().toString(), id, slim);
            return c99;
        }
        catch (IOException | RuntimeException e) {
            LethalAuctionClient.LOGGER.warn("Could not load skin {}", (Object)file, (Object)e);
            return null;
        }
    }

    private static boolean a(NativeImage image) {
        return image.getPixel(54, 20) >>> 24 == 0 && image.getPixel(55, 31) >>> 24 == 0;
    }

    public static void a(String username) {
        String query;
        if (l || k) {
            return;
        }
        String string = query = username == null ? "" : username.trim();
        if (query.isEmpty()) {
            K49.a("Type a player's name first", 3000L);
            return;
        }
        if (!query.matches("[A-Za-z0-9_]{1,16}")) {
            K49.a("That isn't a valid Minecraft name", 3500L);
            return;
        }
        if (K49.b()) {
            K49.a("All 5 slots are full. Delete a skin first.", 3500L);
            return;
        }
        l = true;
        K49.a("Looking up " + query + "...", 60000L);
        Thread t = new Thread(() -> {
            byte[] png = null;
            Object error = null;
            String resolvedName = query;
            try {
                HttpResponse<String> idRes = f.send(HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + query)).timeout(Duration.ofSeconds(15L)).GET().build(), HttpResponse.BodyHandlers.ofString());
                if (idRes.statusCode() == 404 || idRes.statusCode() == 204 || idRes.body() == null || idRes.body().isBlank()) {
                    error = "No player called " + query;
                } else if (idRes.statusCode() != 200) {
                    error = "Minecraft's servers are busy (" + idRes.statusCode() + "). Try again.";
                } else {
                    HttpResponse<String> profRes;
                    JsonObject idJson = JsonParser.parseString((String)idRes.body()).getAsJsonObject();
                    String uuid = idJson.get("id").getAsString();
                    if (idJson.has("name")) {
                        resolvedName = idJson.get("name").getAsString();
                    }
                    if ((profRes = f.send(HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).timeout(Duration.ofSeconds(15L)).GET().build(), HttpResponse.BodyHandlers.ofString())).statusCode() != 200) {
                        error = "Couldn't read that player's profile (" + profRes.statusCode() + ")";
                    } else {
                        JsonObject prof = JsonParser.parseString((String)profRes.body()).getAsJsonObject();
                        String encoded = prof.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                        JsonObject textures = JsonParser.parseString((String)new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("textures");
                        if (textures == null || !textures.has("SKIN")) {
                            error = resolvedName + " is using a default skin (Steve/Alex), nothing to grab.";
                        } else {
                            String url = textures.getAsJsonObject("SKIN").get("url").getAsString();
                            png = f.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(20L)).GET().build(), HttpResponse.BodyHandlers.ofByteArray()).body();
                        }
                    }
                }
            }
            catch (Exception e) {
                LethalAuctionClient.LOGGER.warn("Skin lookup for {} failed", (Object)query, (Object)e);
                error = "Couldn't reach Minecraft's servers. Try again.";
            }
            byte[] finalPng = png;
            String finalError = (String)error;
            String finalName = resolvedName;
            Minecraft.getInstance().execute(() -> {
                l = false;
                if (finalError != null) {
                    K49.a(finalError, 5000L);
                } else {
                    K49.a(finalPng, finalName);
                }
            });
        }, "Lethal Client skin lookup");
        t.setDaemon(true);
        t.start();
    }

    public static void g() {
        if (k) {
            return;
        }
        if (K49.b()) {
            K49.a("All 5 slots are full. Delete a skin first.", 3500L);
            return;
        }
        k = true;
        Thread t = new Thread(() -> {
            try {
                String chosen;
                try (MemoryStack stack = MemoryStack.stackPush();){
                    PointerBuffer filters = stack.mallocPointer(1);
                    filters.put(stack.UTF8((CharSequence)"*.png"));
                    filters.flip();
                    chosen = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Choose a Minecraft skin (64x64 PNG)", (CharSequence)"", (PointerBuffer)filters, (CharSequence)"Skins", (boolean)false);
                }
                if (chosen != null) {
                    byte[] bytes = Files.readAllBytes(Path.of(chosen, new String[0]));
                    String base = Path.of(chosen, new String[0]).getFileName().toString().replaceAll("(?i)\\.png$", "").replaceAll("[\\\\/:*?\"<>|]", "").trim();
                    Minecraft.getInstance().execute(() -> K49.a(bytes, base.isEmpty() ? "Skin" : base));
                }
            }
            catch (IOException | RuntimeException e) {
                LethalAuctionClient.LOGGER.warn("Skin upload failed", (Throwable)e);
                Minecraft.getInstance().execute(() -> K49.a("Could not open that file", 3500L));
            }
            finally {
                k = false;
            }
        }, "Lethal Client skin picker");
        t.setDaemon(true);
        t.start();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String a(byte[] bytes, String base) {
        try (NativeImage check = NativeImage.read((byte[])bytes);){
            if (check.getWidth() != 64 || check.getHeight() != 64) {
                K49.a("That isn't a 64x64 Minecraft skin", 3500L);
                String string = null;
                return string;
            }
        }
        catch (IOException e) {
            K49.a("That isn't a PNG picture", 3500L);
            return null;
        }
        Object name = base;
        int i = 2;
        while (Files.exists(a.resolve((String)name + ".png"), new LinkOption[0])) {
            name = base + " " + i;
            ++i;
        }
        try {
            Files.createDirectories(a, new FileAttribute[0]);
            Files.write(a.resolve((String)name + ".png"), bytes, new OpenOption[0]);
        }
        catch (IOException e) {
            K49.a("Could not save the skin", 3500L);
            return null;
        }
        K49.f();
        K49.a("Added " + (String)name + ". Click it to wear it.", 3500L);
        return (String)name + ".png";
    }

    public static void a(C99 skin) {
        try {
            Files.deleteIfExists(a.resolve(skin.a()));
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not delete skin {}", (Object)skin.a(), (Object)e);
        }
        if (skin.a().equals(K9.aN)) {
            K9.aN = "";
            K9.b();
        }
        K49.f();
    }

    public static void b(String name) {
        String display;
        byte[] bytes;
        Path file;
        if (l) {
            return;
        }
        if (name.isEmpty()) {
            if (!Files.exists(e, new LinkOption[0])) {
                K9.aN = "";
                K9.b();
                K49.a("Back to your normal skin", 3000L);
                return;
            }
            file = e;
            boolean slim = o != null ? o.c() : K9.aO;
        } else {
            C99 skin = g.get(name);
            if (skin == null) {
                return;
            }
            file = a.resolve(name);
            boolean slim = skin.c();
        }
        try {
            bytes = Files.readAllBytes(file);
        }
        catch (IOException e) {
            K49.a("Couldn't read that skin", 3500L);
            return;
        }
        K9.aN = name;
        K9.b();
        boolean real = !name.isEmpty();
        String string = display = real ? name.replaceAll("(?i)\\.png$", "") : "";
        if (real) {
            K49.b(bytes, display);
        }
        K49.a(real ? "Skin applied" : "Back to your normal skin", 4000L);
    }

    private static void b(byte[] png, String display) {
        try {
            Files.createDirectories(b, new FileAttribute[0]);
            try (Stream<Path> s = Files.list(b);){
                for (Path p2 : s.filter(p -> p.getFileName().toString().endsWith(".png")).toList()) {
                    try {
                        if (!Arrays.equals(Files.readAllBytes(p2), png)) continue;
                        Files.deleteIfExists(p2);
                    }
                    catch (IOException iOException) {}
                }
            }
            String safe = display.replaceAll("[^A-Za-z0-9_ .-]", "_").trim();
            if (safe.isEmpty()) {
                safe = "Skin";
            }
            Files.write(b.resolve(System.currentTimeMillis() + "__" + safe + ".png"), png, new OpenOption[0]);
            try (Stream<Path> s = Files.list(b);){
                List<Path> files = s.filter(p -> p.getFileName().toString().endsWith(".png")).sorted().toList();
                for (int i = 0; i < files.size() - 12; ++i) {
                    Files.deleteIfExists(files.get(i));
                }
            }
            j = false;
        }
        catch (IOException e) {
            LethalAuctionClient.LOGGER.warn("Could not save skin to history", (Throwable)e);
        }
    }

    private static String d(String fileName) {
        int i = fileName.indexOf("__");
        String d = i >= 0 ? fileName.substring(i + 2) : fileName;
        return d.replaceAll("(?i)\\.png$", "");
    }

    public static List<C98> h() {
        if (!j) {
            K49.i();
        }
        ArrayList<C98> list = new ArrayList<C98>(h.values());
        Collections.reverse(list);
        return list;
    }

    private static void i() {
        j = true;
        try {
            Files.createDirectories(b, new FileAttribute[0]);
        }
        catch (IOException e) {
            return;
        }
        ArrayList<Path> files = new ArrayList<Path>();
        try (Stream<Path> s = Files.list(b);){
            s.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")).sorted().forEach(files::add);
        }
        catch (IOException e) {
            return;
        }
        ArrayList<String> present = new ArrayList<String>();
        for (Path file : files) {
            C98 r;
            String fn = file.getFileName().toString();
            present.add(fn);
            if (h.containsKey(fn) || (r = K49.a(file)) == null) continue;
            h.put(fn, r);
        }
        for (String fn : new ArrayList<String>(h.keySet())) {
            if (present.contains(fn)) continue;
            Minecraft.getInstance().getTextureManager().release(h.remove(fn).c());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static C98 a(Path file) {
        try (InputStream in = Files.newInputStream(file, new OpenOption[0]);){
            NativeImage image = NativeImage.read((InputStream)in);
            if (image.getWidth() != 64 || image.getHeight() != 64) {
                image.close();
                C98 c982 = null;
                return c982;
            }
            String fn = file.getFileName().toString();
            Identifier id = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("history/" + K49.c(fn)));
            boolean slim = K49.a(image);
            Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "Lethal Client history " + fn, image));
            C98 c98 = new C98(fn, K49.d(fn), id, slim);
            return c98;
        }
        catch (IOException | RuntimeException e) {
            LethalAuctionClient.LOGGER.warn("Could not load history skin {}", (Object)file, (Object)e);
            return null;
        }
    }

    public static void a(C98 recent) {
        byte[] bytes;
        if (l) {
            return;
        }
        if (K49.b()) {
            K49.a("Slots are full. Delete one to bring a skin back.", 3500L);
            return;
        }
        try {
            bytes = Files.readAllBytes(b.resolve(recent.a()));
        }
        catch (IOException e) {
            K49.a("Couldn't read that skin", 3500L);
            return;
        }
        String slot = K49.a(bytes, recent.b());
        if (slot != null) {
            K49.b(slot);
        }
    }

    static {
        m = "";
    }

    public static final class C99 {
        private final String a;
        private final Identifier b;
        private final boolean c;
        C99(String a, Identifier b, boolean c) { this.a = a; this.b = b; this.c = c; }
        public String a() { return this.a; }
        public Identifier b() { return this.b; }
        public boolean c() { return this.c; }


    }

    public static final class C98 {
        private final String a;
        private final String b;
        private final Identifier c;
        private final boolean d;
        C98(String a, String b, Identifier c, boolean d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public Identifier c() { return this.c; }
        public boolean d() { return this.d; }


    }
}

