/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K24;
import com.lethalauction.m.K37;
import com.lethalauction.m.K47;
import com.lethalauction.m.K9;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public class K25
implements HudElement {
    public static final int a = 272;
    static final int b = 108;
    public static final float c = 0.6f;
    static final int d = 72;
    private static final int f = -435482856;
    private static final int g = 0x30FFFFFF;
    private static final int h = -7695450;
    public static final K24 e = new K24(){

        @Override
        public String a() {
            return "the Now Playing card";
        }

        @Override
        public float b() {
            return 272.0f * K25.b();
        }

        @Override
        public float c() {
            return (float)K25.c() * K25.b();
        }

        @Override
        public float d() {
            return K9.W;
        }

        @Override
        public float e() {
            return K9.X;
        }

        @Override
        public void a(float fx, float fy) {
            K9.W = fx;
            K9.X = fy;
        }

        @Override
        public float f() {
            return K9.Y;
        }

        @Override
        public void a(float size) {
            K9.Y = size;
        }

        @Override
        public void g() {
            K9.W = 0.5f;
            K9.X = 0.02f;
            K9.Y = 1.0f;
        }

        @Override
        public void a(GuiGraphicsExtractor g, float x, float y) {
            K25.a(g, x, y);
        }
    };

    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().gui.screen() == null) {
            K25.a(g);
        }
    }

    public static void a(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (!K9.U || !K9.V || !K37.c() || mc.gui.hud.isHidden()) {
            return;
        }
        if (!K47.c().a()) {
            return;
        }
        float a = K25.a();
        if (a <= 0.01f) {
            return;
        }
        float prev = K18.h;
        K18.h = prev * a;
        K25.a(g, e.a(g.guiWidth()), e.b(g.guiHeight()));
        K18.h = prev;
    }

    static float a() {
        long base;
        long since;
        long now = System.currentTimeMillis();
        if (!K47.d() && K47.h() < K47.g()) {
            return 0.0f;
        }
        float inK = Math.max(0.0f, Math.min(1.0f, (float)(now - K47.g()) / 500.0f));
        float fadeIn = 1.0f - (float)Math.pow(1.0f - inK, 3.0);
        float vis = K47.d() ? 1.0f : ((since = now - (base = Math.max(K47.h(), K47.g()))) <= 3500L ? 1.0f : (since >= 5000L ? 0.0f : 1.0f - (float)(since - 3500L) / 1500.0f));
        return Math.min(fadeIn, vis);
    }

    public static float b() {
        return 0.6f * K9.Y;
    }

    static int c() {
        return 108;
    }

    public static void a(GuiGraphicsExtractor g, float x, float y) {
        K25.a(g, x, y, K25.b());
    }

    public static void a(GuiGraphicsExtractor g, float x, float y, float s) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K25.b(g);
        p.popMatrix();
    }

    static void b(GuiGraphicsExtractor g) {
        K47.C97 t = K47.c();
        int accent = t.c().color == 0 ? -11695105 : t.c().color;
        K18.a(g, 0, 0, 272, 108, 14, -435482856, 0x30FFFFFF);
        int ax = 12;
        int ay = 12;
        Identifier art = K47.n();
        K18.a(g, ax, ay, 72, 72, 10, -16447732);
        if (art != null) {
            int iw = Math.max(1, K47.o());
            int ih = Math.max(1, K47.p());
            float since = (float)(System.currentTimeMillis() - K47.t()) / 420.0f;
            float ease = since >= 1.0f ? 1.0f : 1.0f - (float)Math.pow(1.0f - since, 3.0);
            float ga = K18.h;
            Identifier prev = K47.q();
            if (prev != null && ease < 1.0f) {
                K18.a(g, prev, ax, ay, 72, 72, Math.max(1, K47.r()), Math.max(1, K47.s()), K18.a(-1, ga));
            }
            K18.a(g, art, ax, ay, 72, 72, iw, ih, K18.a(-1, ease * ga));
            K18.a(g, ax, ay, 72, 72, 10, 0, 0x22FFFFFF);
        } else {
            K18.a(g, ax, ay, 72, 72, 10, K18.a(accent, -16447732, 0.55f));
            K18.a(g, 60156, (float)ax + 36.0f, (float)ay + 36.0f, 30.0f, -855638017);
        }
        int tx = ax + 72 + 14;
        int maxW = 272 - tx - 14;
        K18.a(g, "NOW PLAYING", (float)tx, 22.0f, 8.0f, K18.a(accent, 0.85f), K18.c);
        K18.a(g, K25.a(t.d(), 15.0f, K18.c, (float)maxW), (float)tx, 42.0f, 15.0f, -1, K18.c);
        K18.a(g, K25.a(t.e().isEmpty() ? "Unknown artist" : t.e(), 11.0f, K18.b, (float)maxW), (float)tx, 60.0f, 11.0f, -7695450, K18.b);
        if (K9.ay) {
            String lt = null;
            int lc = -7695450;
            switch (K47.a()) {
                case 1: {
                    lt = "Finding lyrics\u2026";
                    break;
                }
                case 2: {
                    lt = "Lyrics ready";
                    lc = -11938144;
                    break;
                }
                case 3: {
                    lt = "No lyrics for this song";
                    lc = -2055606;
                    break;
                }
                case 4: {
                    lt = "Lyrics service unreachable";
                    lc = -2074022;
                    break;
                }
            }
            if (lt != null) {
                K18.a(g, K25.a(lt, 9.0f, K18.b, (float)maxW), (float)tx, 76.0f, 9.0f, lc, K18.b);
            }
        }
        K18.a(g, (float)ax + 4.0f, 92.0f, 3.5f, accent);
        K18.a(g, t.c().label.isEmpty() ? "Music" : t.c().label, (float)ax + 11.0f, 92.0f, 9.0f, -7695450, K18.b);
        long pos = t.b();
        long dur = t.h();
        float frac = dur > 0L ? Math.max(0.0f, Math.min(1.0f, (float)pos / (float)dur)) : 0.0f;
        int rowY = 92;
        int barL = tx + 22;
        int barR = 258 - (dur > 0L ? 28 : 0);
        int bw = Math.max(8, barR - barL);
        K18.a(g, K25.a(pos), (float)tx, (float)rowY, 7.5f, -7695450, K18.b);
        K18.a(g, barL, rowY - 1, bw, 3, 1, 0x33FFFFFF);
        if (frac > 0.0f) {
            K18.a(g, barL, rowY - 1, Math.max(2, Math.round((float)bw * frac)), 3, 1, accent);
            K18.a(g, (float)barL + (float)bw * frac, (float)rowY + 0.5f, 2.6f, -1);
        }
        if (dur > 0L) {
            K18.c(g, "-" + K25.a(dur - pos), 258.0f, (float)rowY, 7.5f, -7695450, K18.b);
        }
    }

    private static String a(String s, float size, Identifier font, float maxW) {
        if (K18.a(s, size, font) <= maxW) {
            return s;
        }
        String ell = "...";
        while (s.length() > 1 && K18.a(s + ell, size, font) > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s.trim() + ell;
    }

    private static String a(long ms) {
        if (ms < 0L) {
            ms = 0L;
        }
        long sec = ms / 1000L;
        return sec / 60L + ":" + String.format("%02d", sec % 60L);
    }
}

