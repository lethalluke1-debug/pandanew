/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.client.resources.DefaultPlayerSkin
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K22;
import com.lethalauction.m.K32;
import com.lethalauction.m.K9;
import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Matrix3x2fStack;

public final class K33 {
    private static final Identifier b = K22.a;
    private static final int c = 0;
    private static final int d = 1;
    public static final C89[] a = new C89[]{new C89("Cherry Grove", "cherry", 0, true), new C89("Dark Forest", "forest", 1, false)};
    private static boolean e;
    private static long f;
    private static final int g = 150;
    private static final int h = 11;
    private static final int i = 13;
    private static final int j = 33;
    private static final int k = 54;
    private static final int l = 62;
    private static final int m = 18;
    private static final int n = 90;
    private static final int o = 24;
    private static final int p = 12;

    private static Identifier a(String name) {
        return Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("textures/title/" + name + ".png"));
    }

    private K33() {
    }

    public static boolean a() {
        return K9.G;
    }

    private static C89 b() {
        return a[Math.floorMod(K9.J, a.length)];
    }

    private static float a(int seed) {
        seed = seed * 374761393 + 668265263;
        seed = (seed ^ seed >>> 13) * 1274126177;
        return (float)((seed ^ seed >>> 16) & Integer.MAX_VALUE) / 2.1474836E9f;
    }

    public static void a(GuiGraphicsExtractor g, int w, int h) {
        long now = System.currentTimeMillis();
        float t = (float)(now % 3600000L) / 1000.0f;
        C89 p = K33.b();
        K18.a(g, K33.a(p.b()), 0, 0, w, h, 1920, 1080);
        if (p.d()) {
            float sweep = (float)Math.sin((double)t * 0.12) * 0.5f + 0.5f;
            int rayX = Math.round(sweep * (float)w) - w / 4;
            K18.d(g, rayX, 0, w / 2, h, 16774360, 352318680);
            K18.d(g, rayX + w / 2, 0, w / 2, h, 352318680, 16774360);
        }
        switch (p.c()) {
            case 0: {
                K33.a(g, w, h, now);
                break;
            }
            case 1: {
                K33.a(g, w, h, t);
                break;
            }
        }
        boolean dark = p.c() == 1;
        int moteCol = dark ? -4200240 : -2344;
        for (int i = 0; i < (dark ? 22 : 46); ++i) {
            float speed = 6.0f + K33.a(i * 5) * 10.0f;
            float my = (K33.a(i * 7) * (float)h - t * speed) % (float)(h + 12);
            if (my < 0.0f) {
                my += (float)(h + 12);
            }
            float mx = K33.a(i) * (float)w + (float)Math.sin((double)t * 0.4 + (double)i) * 18.0f;
            mx = Math.floorMod((int)mx, w);
            float tw = 0.25f + 0.55f * Math.abs((float)Math.sin((double)t * 1.3 + (double)i * 0.7));
            float sz = 0.8f + K33.a(i * 3) * 1.6f;
            K18.a(g, mx, my, sz, K18.a(moteCol, tw * (dark ? 0.35f : 0.6f)));
        }
        K18.e(g, 0, 0, w, h / 5, 0x33000000, 0);
        K18.e(g, 0, h - h / 4, w, h / 4, 0, Integer.MIN_VALUE);
    }

    private static void a(GuiGraphicsExtractor g, int w, int h, long now) {
        int[] cols = new int[]{-18731, -28736, -11802, -15650};
        float ts = (float)(now % 3600000L) / 1000.0f;
        for (int i = 0; i < 90; ++i) {
            float speed = 16.0f + K33.a(i * 5) * 22.0f;
            float y = (K33.a(i * 7) * (float)h + ts * speed) % (float)(h + 16) - 8.0f;
            float sway = (float)Math.sin((double)now / 900.0 + (double)i * 1.7) * 26.0f;
            float x = K33.a(i) * (float)w + sway;
            float sz = 2.0f + K33.a(i * 3) * 2.8f;
            float wob = 0.5f + 0.5f * Math.abs((float)Math.sin((double)now / 500.0 + (double)i));
            K18.b(g, x, y, sz * (0.6f + wob), K18.a(cols[i % cols.length], 0.85f));
        }
    }

    private static void a(GuiGraphicsExtractor g, int w, int h, float t) {
        for (int i = 0; i < 48; ++i) {
            float baseX = K33.a(i) * (float)w;
            float baseY = (float)h * 0.3f + K33.a(i * 7) * ((float)h * 0.62f);
            float sp = 0.5f + K33.a(i * 13) * 0.9f;
            float fx = baseX + (float)Math.sin((double)(t * sp) + (double)i * 1.7) * 62.0f + (float)Math.sin((double)(t * sp) * 0.37 + (double)i) * 26.0f;
            float fy = baseY + (float)Math.cos((double)(t * sp) * 0.8 + (double)i * 2.3) * 36.0f + (float)Math.sin((double)t * 0.6 + (double)i * 3.0) * 13.0f;
            float blink = (float)Math.sin((double)(t * (1.6f + K33.a(i * 5) * 1.6f)) + (double)i * 2.1);
            blink = Math.max(0.0f, blink);
            if ((blink *= blink) < 0.03f) continue;
            K18.a(g, fx, fy, 3.8f, K18.a(-7340064, blink * 0.18f));
            K18.a(g, fx, fy, 1.5f, K18.a(-1310730, blink));
        }
    }

    public static void b(GuiGraphicsExtractor g, int w, int h) {
        if (K33.a()) {
            Minecraft mc = Minecraft.getInstance();
            String name = mc.getUser().getName();
            float nameSize = 14.0f;
            float nameW = K18.a(name, nameSize, K18.c);
            int head = 15;
            float gap = 7.0f;
            float total = (float)head + gap + nameW;
            float startX = (float)w / 2.0f - total / 2.0f;
            int ny = h / 4 + 36;
            PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)g, (PlayerSkin)K33.a(mc), (int)Math.round(startX), (int)(ny - head / 2), (int)head);
            K18.a(g, name, startX + (float)head + gap, (float)ny, nameSize, -1, K18.c);
        }
        int bs = 22;
        int bx = w - bs - 6;
        int by = h - bs - 6;
        int accent = K32.a();
        K18.a(g, bx, by, bs, bs, 6, e ? accent : -552331745, e ? accent : 0x40FFFFFF);
        K18.b(g, K22.a(60161), (float)bx + (float)bs / 2.0f, (float)by + (float)bs / 2.0f, 12.0f, -1, b);
        if (e) {
            float prog = Math.min(1.0f, (float)(System.currentTimeMillis() - f) / 170.0f);
            float e = 1.0f - (1.0f - prog) * (1.0f - prog) * (1.0f - prog);
            float scale = 0.86f + 0.14f * e;
            float ax = (float)K33.b(w) + 75.0f;
            float ay = (float)K33.c(h) + 45.0f;
            Matrix3x2fStack pose = g.pose();
            pose.pushMatrix();
            pose.translate(ax, ay);
            pose.scale(scale);
            pose.translate(-ax, -ay);
            float prev = K18.h;
            K18.h = prev * e;
            K33.c(g, w, h);
            K18.h = prev;
            pose.popMatrix();
        }
    }

    private static PlayerSkin a(Minecraft mc) {
        try {
            return (PlayerSkin)mc.getSkinManager().createLookup(mc.getGameProfile(), false).get();
        }
        catch (Exception e) {
            return DefaultPlayerSkin.get((GameProfile)mc.getGameProfile());
        }
    }

    private static int b(int w) {
        return w - 150 - 6;
    }

    private static int c(int h) {
        return h - 90 - 34;
    }

    private static int d(int x) {
        return x + 150 - 11 - 24;
    }

    private static void c(GuiGraphicsExtractor g, int w, int h) {
        int x = K33.b(w);
        int y = K33.c(h);
        boolean on = K33.a();
        K18.a(g, x, y, 150, 90, 9, -435417578, 0x33FFFFFF);
        K18.a(g, "LETHAL TITLE", (float)(x + 11), (float)(y + 13), 7.0f, -7695450, K18.b);
        K18.a(g, "Enabled", (float)(x + 11), (float)(y + 33), 9.0f, -1, K18.c);
        K33.a(g, K33.d(x), y + 33, on, true);
        K18.a(g, "SCENE", (float)(x + 11), (float)(y + 54), 7.0f, on ? -7695450 : -11116946, K18.b);
        int lax = x + 11;
        int rax = x + 150 - 11 - 18;
        int arrowCol = on ? -552199644 : -14012612;
        int arrowBorder = on ? 0x40FFFFFF : 0x26FFFFFF;
        int glyphCol = on ? -1 : -11116946;
        boolean many = a.length > 1;
        K18.a(g, lax, y + 62, 18, 18, 6, arrowCol, arrowBorder);
        K18.b(g, "<", (float)lax + 9.0f, (float)(y + 62) + 9.0f, 10.0f, many ? glyphCol : -11116946, K18.c);
        K18.a(g, rax, y + 62, 18, 18, 6, arrowCol, arrowBorder);
        K18.b(g, ">", (float)rax + 9.0f, (float)(y + 62) + 9.0f, 10.0f, many ? glyphCol : -11116946, K18.c);
        K18.b(g, K33.b().a(), (float)(lax + 18 + rax) / 2.0f, (float)(y + 62) + 9.0f, 8.5f, on ? -1 : -11116946, K18.c);
    }

    private static void a(GuiGraphicsExtractor g, int x, int cy, boolean value, boolean active) {
        int track;
        int top = cy - 6;
        int accent = K32.a();
        track = value ? (active ? accent : -12959408) : (-13880766);
        if (!active) {
            track = K18.a(track, 0.5f);
        }
        K18.a(g, x, top, 24, 12, 6, track);
        int knob = 8;
        int kx = value ? x + 24 - knob - 2 : x + 2;
        K18.a(g, (float)kx + (float)knob / 2.0f, cy, (float)knob / 2.0f, active ? -1 : -1711276033);
    }

    public static boolean a(double mx, double my, int w, int h) {
        int bs = 22;
        int bx = w - bs - 6;
        int by = h - bs - 6;
        if (mx >= (double)bx && mx < (double)(bx + bs) && my >= (double)by && my < (double)(by + bs)) {
            boolean bl = e = !e;
            if (e) {
                f = System.currentTimeMillis();
            }
            return true;
        }
        if (!e) {
            return false;
        }
        int x = K33.b(w);
        int y = K33.c(h);
        if (K33.b(mx, my, K33.d(x), y + 33)) {
            K9.G = !K9.G;
            K9.b();
            return true;
        }
        if (K33.a() && a.length > 1 && my >= (double)(y + 62) && my < (double)(y + 62 + 18)) {
            int lax = x + 11;
            int rax = x + 150 - 11 - 18;
            if (mx >= (double)lax && mx < (double)(lax + 18)) {
                K9.J = Math.floorMod(K9.J - 1, a.length);
                K9.b();
                return true;
            }
            if (mx >= (double)rax && mx < (double)(rax + 18)) {
                K9.J = Math.floorMod(K9.J + 1, a.length);
                K9.b();
                return true;
            }
        }
        if (mx >= (double)x && mx < (double)(x + 150) && my >= (double)y && my < (double)(y + 90)) {
            return true;
        }
        e = false;
        return true;
    }

    private static boolean b(double mx, double my, int x, int cy) {
        int top = cy - 6;
        return mx >= (double)x && mx < (double)(x + 24) && my >= (double)top && my < (double)(top + 12);
    }

    public static final class C89 {
        private final String a;
        private final String b;
        private final int c;
        private final boolean d;
        C89(String a, String b, int c, boolean d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public String b() { return this.b; }
        public int c() { return this.c; }
        public boolean d() { return this.d; }


    }
}

