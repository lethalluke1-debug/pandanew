/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 */
package com.lethalauction.m;

import com.lethalauction.m.K43;
import com.lethalauction.m.K45;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class K47 {
    private static final AtomicReference<C97> f = new AtomicReference<C97>(C97.a);
    private static volatile long g;
    private static volatile long h;
    private static volatile long i;
    private static volatile long j;
    private static volatile List<K45.C95> k;
    private static volatile String l;
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = 4;
    private static volatile int m;
    private static final AtomicReference<String> n;
    private static final Identifier o;
    private static final Identifier p;
    private static volatile Identifier q;
    private static volatile int r;
    private static volatile int s;
    private static volatile Identifier t;
    private static volatile int u;
    private static volatile int v;
    private static volatile long w;

    public static int a() {
        return m;
    }

    static void b() {
        m = 1;
    }

    static void a(String key, int status) {
        l = key;
        m = status;
    }

    private K47() {
    }

    public static C97 c() {
        return f.get();
    }

    public static boolean d() {
        return f.get().f();
    }

    public static long e() {
        return g;
    }

    public static long f() {
        return h;
    }

    public static long g() {
        return i;
    }

    public static long h() {
        return j;
    }

    public static boolean i() {
        C97 t = f.get();
        if (t.f()) {
            return true;
        }
        return t.a() && System.currentTimeMillis() - g < 4000L;
    }

    static void a(C97 t) {
        C97 was = f.getAndSet(t);
        long now = System.currentTimeMillis();
        if (!(!t.a() || was.a() && t.j().equals(was.j()))) {
            i = now;
        }
        if (t.f()) {
            j = now;
        }
        if (was.f() && !t.f()) {
            g = now;
        }
        if (!was.f() && t.f()) {
            h = now;
        }
        if (!t.j().equals(l)) {
            k = List.of();
            if (m == 2 || m == 3) {
                m = 0;
            }
        }
    }

    static void a(String key, List<K45.C95> lines) {
        l = key;
        k = lines == null ? List.of() : lines;
        m = k.isEmpty() ? 3 : 2;
    }

    public static boolean j() {
        return !k.isEmpty();
    }

    public static List<K45.C95> k() {
        return k;
    }

    public static int l() {
        return k.size();
    }

    public static int a(long posMs) {
        List<K45.C95> ls = k;
        int idx = -1;
        int i = 0;
        while (i < ls.size() && ls.get(i).a() <= posMs) {
            idx = i++;
        }
        return idx;
    }

    public static String a(int i) {
        List<K45.C95> ls = k;
        return i >= 0 && i < ls.size() ? ls.get(i).b() : "";
    }

    public static long b(int i) {
        List<K45.C95> ls = k;
        return i >= 0 && i < ls.size() ? ls.get(i).a() : -1L;
    }

    static void m() {
        C97 was = f.getAndSet(C97.a);
        if (was.f()) {
            g = System.currentTimeMillis();
        }
    }

    public static Identifier n() {
        return q;
    }

    public static int o() {
        return r;
    }

    public static int p() {
        return s;
    }

    public static Identifier q() {
        return t;
    }

    public static int r() {
        return u;
    }

    public static int s() {
        return v;
    }

    public static long t() {
        return w;
    }

    static void a(String key, Path png) {
        if (key == null || key.isEmpty() || key.equals(n.get())) {
            return;
        }
        try {
            K47.a(key, K47.a(Files.readAllBytes(png)));
        }
        catch (Exception e) {
            K43.a("load local cover failed: " + String.valueOf(e));
        }
    }

    static void a(String key) {
        if (key == null || key.equals(n.get())) {
            return;
        }
        q = null;
        t = null;
        r = 0;
        s = 0;
        n.set(key);
    }

    static void a(String key, byte[] bytes) {
        if (key == null || key.isEmpty() || bytes == null || bytes.length == 0) {
            return;
        }
        try {
            K47.a(key, K47.a(bytes));
        }
        catch (Exception e) {
            K43.a("decode downloaded cover failed: " + String.valueOf(e));
        }
    }

    private static void a(String key, NativeImage img) {
        Minecraft.getInstance().execute(() -> K47.b(key, img));
    }

    private static NativeImage a(byte[] bytes) throws Exception {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
        if (bi == null) {
            throw new IOException("unsupported image");
        }
        int w = bi.getWidth();
        int h = bi.getHeight();
        NativeImage img = new NativeImage(w, h, true);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                img.setPixel(x, y, 0xFF000000 | bi.getRGB(x, y));
            }
        }
        return img;
    }

    private static void b(String key, NativeImage img) {
        Identifier loc = q == o ? p : o;
        DynamicTexture tex = new DynamicTexture(() -> "Lethal Client album art", img);
        Minecraft.getInstance().getTextureManager().register(loc, (AbstractTexture)tex);
        t = q;
        u = r;
        v = s;
        q = loc;
        r = img.getWidth();
        s = img.getHeight();
        w = System.currentTimeMillis();
        n.set(key);
    }

    static {
        k = List.of();
        l = "";
        m = 0;
        n = new AtomicReference<String>("");
        o = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"album_art_a");
        p = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"album_art_b");
    }

    public static final class C97 {
        private final C96 b;
        private final String c;
        private final String d;
        private final boolean e;
        private final long f;
        private final long g;
        private final long h;
        private final String i;
        C97(C96 b, String c, String d, boolean e, long f, long g, long h, String i) { this.b = b; this.c = c; this.d = d; this.e = e; this.f = f; this.g = g; this.h = h; this.i = i; }
        static final C97 a = new C97(C96.NONE, "", "", false, 0L, 0L, 0L, "");

        public boolean a() {
            return this.b != C96.NONE && !this.c.isEmpty();
        }

        public long b() {
            long p = this.f;
            if (this.e) {
                p += System.currentTimeMillis() - this.h;
            }
            return this.g > 0L ? Math.max(0L, Math.min(this.g, p)) : Math.max(0L, p);
        }




        public C96 c() {
            return this.b;
        }

        public String d() {
            return this.c;
        }

        public String e() {
            return this.d;
        }

        public boolean f() {
            return this.e;
        }

        public long g() {
            return this.f;
        }

        public long h() {
            return this.g;
        }

        public long i() {
            return this.h;
        }

        public String j() {
            return this.i;
        }
    }

    public static enum C96 {
        NONE("", 0),
        SPOTIFY("Spotify", -14829228),
        APPLE("Apple Music", -246716),
        OTHER("Media", -7695450);

        public final String label;
        public final int color;

        private C96(String label, int color) {
            this.label = label;
            this.color = color;
        }
    }
}

