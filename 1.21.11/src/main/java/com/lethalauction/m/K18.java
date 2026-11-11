/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_11719
 *  net.minecraft.class_11719$class_11721
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import net.minecraft.class_10799;
import net.minecraft.class_11719;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import org.joml.Matrix3x2fStack;

public final class K18 {
    public static final class_2960 a = K18.a("regular");
    public static final class_2960 b = K18.a("medium");
    public static final class_2960 c = K18.a("semibold");
    public static final class_2960 d = K18.a("logo");
    public static final class_2960 e = K18.a("caveat");
    public static final class_2960 f = K18.a("icons");
    public static final class_2960 g = K18.a("icons_filled");
    public static float h = 1.0f;
    static float i = 4.0f;
    private static final class_2960 j = K18.a("textures/gui/corners.png");
    private static final int k = 400;
    private static final int l = 2500;
    private static final int m = 4;
    private static final int n = 24;

    private K18() {
    }

    public static class_2960 a(String path) {
        return class_2960.method_60655((String)"lethalauction", (String)path);
    }

    private static class_2561 a(String s, class_2960 font) {
        return class_2561.method_43470((String)s).method_27694(st -> st.method_27704((class_11719)new class_11719.class_11721(font)));
    }

    public static float a(String s, float size, class_2960 font) {
        return (float)class_310.method_1551().field_1772.method_27525((class_5348)K18.a(s, font)) * size / 10.0f;
    }

    public static void a(class_332 g, String s, float x, float cy, float size, int color, class_2960 font) {
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(x, cy);
        p.scale(size / 10.0f);
        p.translate(0.0f, -i);
        g.method_51439(class_310.method_1551().field_1772, K18.a(s, font), 0, 0, K18.a(color), false);
        p.popMatrix();
    }

    public static void b(class_332 g, String s, float cx, float cy, float size, int color, class_2960 font) {
        K18.a(g, s, cx - K18.a(s, size, font) / 2.0f, cy, size, color, font);
    }

    public static void c(class_332 g, String s, float rx, float cy, float size, int color, class_2960 font) {
        K18.a(g, s, rx - K18.a(s, size, font), cy, size, color, font);
    }

    public static void a(class_332 g, int codepoint, float cx, float cy, float size, int color) {
        K18.a(g, f, codepoint, cx, cy, size, color);
    }

    public static void b(class_332 g, int codepoint, float cx, float cy, float size, int color) {
        K18.a(g, K18.g, codepoint, cx, cy, size, color);
    }

    private static void a(class_332 g, class_2960 font, int codepoint, float cx, float cy, float size, int color) {
        String s = new String(Character.toChars(codepoint));
        K18.b(g, s, cx, cy, size, color, font);
    }

    public static void a(class_332 g, int x, int y, int w, int h, int color) {
        if (w > 0 && h > 0) {
            g.method_25294(x, y, x + w, y + h, K18.a(color));
        }
    }

    public static void a(class_332 g, int x, int y, int w, int h, int r, int color) {
        if ((r = Math.max(0, Math.min(Math.min(r, 24), Math.min(w, h) / 2))) == 0) {
            K18.a(g, x, y, w, h, color);
        } else {
            K18.c(g, x, y, w, h, r, 2, color);
            K18.a(g, x + r, y, w - 2 * r, h, color);
            K18.a(g, x, y + r, r, h - 2 * r, color);
            K18.a(g, x + w - r, y + r, r, h - 2 * r, color);
        }
    }

    public static void a(class_332 g, int x, int y, int w, int h, int r, int fill, int border) {
        r = Math.max(1, Math.min(Math.min(r, 24), Math.min(w, h) / 2));
        if (fill >>> 24 != 0) {
            K18.a(g, x + 1, y + 1, w - 2, h - 2, r - 1, fill);
        }
        K18.c(g, x, y, w, h, r, 8 * r + 6, border);
        K18.a(g, x + r, y, w - 2 * r, 1, border);
        K18.a(g, x + r, y + h - 1, w - 2 * r, 1, border);
        K18.a(g, x, y + r, 1, h - 2 * r, border);
        K18.a(g, x + w - 1, y + r, 1, h - 2 * r, border);
    }

    public static void b(class_332 g, int x, int y, int w, int h, int r, int color) {
        if ((r = Math.max(0, Math.min(Math.min(r, 24), Math.min(w / 2, h)))) == 0) {
            K18.a(g, x, y, w, h, color);
        } else {
            int cellY = 4 * r * r - 2;
            int q = r * 4;
            K18.f(g, x, y, r, 2, cellY, color);
            K18.f(g, x + w - r, y, r, 2 + q, cellY, color);
            K18.a(g, x + r, y, w - 2 * r, r, color);
            K18.a(g, x, y + r, w, h - r, color);
        }
    }

    public static void c(class_332 g, int x, int y, int w, int h, int r, int color) {
        if ((r = Math.max(0, Math.min(Math.min(r, 24), Math.min(w / 2, h)))) == 0) {
            K18.a(g, x, y, w, h, color);
        } else {
            int cellY = 4 * r * r - 2;
            int q = r * 4;
            K18.a(g, x, y, w, h - r, color);
            K18.f(g, x, y + h - r, r, 2, cellY + q, color);
            K18.f(g, x + w - r, y + h - r, r, 2 + q, cellY + q, color);
            K18.a(g, x + r, y + h - r, w - 2 * r, r, color);
        }
    }

    private static void c(class_332 g, int x, int y, int w, int h, int r, int cellX, int color) {
        int cellY = 4 * r * r - 2;
        int q = r * 4;
        K18.f(g, x, y, r, cellX, cellY, color);
        K18.f(g, x + w - r, y, r, cellX + q, cellY, color);
        K18.f(g, x, y + h - r, r, cellX, cellY + q, color);
        K18.f(g, x + w - r, y + h - r, r, cellX + q, cellY + q, color);
    }

    private static void f(class_332 g, int x, int y, int r, int u, int v, int color) {
        int q = r * 4;
        g.method_25293(class_10799.field_56883, j, x, y, (float)u, (float)v, r, r, q, q, 400, 2500, K18.a(color));
    }

    private static int a(int color) {
        return h >= 1.0f ? color : K18.a(color, h);
    }

    public static int a(int color, float f) {
        int a = Math.round((float)(color >>> 24) * f);
        return Math.max(0, Math.min(255, a)) << 24 | color & 0xFFFFFF;
    }

    public static void a(class_332 g, float cx, float cy, float radius, int color) {
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(cx, cy);
        p.scale(radius / 16.0f);
        K18.a(g, -16, -16, 32, 32, 16, color);
        p.popMatrix();
    }

    public static void b(class_332 g, float cx, float cy, float size, int color) {
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(cx, cy);
        p.rotate(0.7853982f);
        p.scale(size / 20.0f);
        K18.a(g, -7, -7, 14, 14, color);
        p.popMatrix();
    }

    public static void a(class_332 g, class_2960 tex, int x, int y, int w, int h, int texW, int texH) {
        K18.a(g, tex, x, y, w, h, texW, texH, -1);
    }

    public static void a(class_332 g, class_2960 tex, int x, int y, int w, int h, int texW, int texH, int color) {
        g.method_25293(class_10799.field_56883, tex, x, y, 0.0f, 0.0f, w, h, texW, texH, texW, texH, color);
    }

    public static void d(class_332 g, int x, int y, int w, int h, int from, int to) {
        for (int i = 0; i < w; ++i) {
            K18.a(g, x + i, y, 1, h, K18.a(from, to, w <= 1 ? 0.0f : (float)i / (float)(w - 1)));
        }
    }

    public static void e(class_332 g, int x, int y, int w, int h, int from, int to) {
        int band = Math.max(1, h / 96);
        for (int i = 0; i < h; i += band) {
            K18.a(g, x, y + i, w, Math.min(band, h - i), K18.a(from, to, h <= 1 ? 0.0f : (float)i / (float)(h - 1)));
        }
    }

    public static void b(class_332 g, int x, int y, int w, int h, int top, int mid, int bottom) {
        int half = h / 2;
        K18.e(g, x, y, w, half, top, mid);
        K18.e(g, x, y + half, w, h - half, mid, bottom);
    }

    public static int a(int a, int b, float t) {
        int aa = a >>> 24;
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int ba = b >>> 24;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return (int)((float)aa + (float)(ba - aa) * t) << 24 | (int)((float)ar + (float)(br - ar) * t) << 16 | (int)((float)ag + (float)(bg - ag) * t) << 8 | (int)((float)ab + (float)(bb - ab) * t);
    }
}

