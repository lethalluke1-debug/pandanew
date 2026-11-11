/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package com.lethalauction.m;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public final class K1 {
    public static final String a = "anim:";
    public static final List<C61> b = List.of(new C61("aurora", "Aurora"), new C61("inferno", "Inferno"), new C61("galaxy", "Galaxy"), new C61("rainbow", "Rainbow"), new C61("matrix", "Matrix"), new C61("storm", "Storm"));
    public static final int c = 128;
    public static final int d = 64;
    private static final Map<String, class_1043> e = new HashMap<String, class_1043>();
    private static final Map<String, Long> f = new HashMap<String, Long>();

    private K1() {
    }

    public static boolean a(String cape) {
        return cape != null && cape.startsWith(a);
    }

    public static class_2960 b(String cape) {
        long now;
        String id;
        String string = id = K1.a(cape) ? cape.substring(a.length()) : cape;
        if (b.stream().noneMatch(d -> d.b().equals(id))) {
            return null;
        }
        class_2960 location = class_2960.method_60655((String)"lethalauction", (String)("animated_capes/" + id));
        class_1043 texture = e.get(id);
        if (texture == null) {
            texture = new class_1043(() -> "Lethal Client cape " + id, 128, 64, true);
            class_310.method_1551().method_1531().method_4616(location, (class_1044)texture);
            e.put(id, texture);
        }
        if ((now = System.currentTimeMillis()) - f.getOrDefault(id, 0L) >= 50L) {
            f.put(id, now);
            K1.a(texture.method_4525(), id, (float)(now % 1000000L) / 1000.0f);
            texture.method_4524();
        }
        return location;
    }

    private static void a(class_1011 img, String id, float t) {
        for (int y = 0; y < 64; ++y) {
            for (int x = 0; x < 128; ++x) {
                float u = x < 23 ? (float)(x - 2) / 20.0f : (float)(x - 24) / 20.0f;
                float v = (float)(y - 2) / 32.0f;
                int rgb = switch (id) {
                    case "aurora" -> K1.a(u, v, t);
                    case "inferno" -> K1.b(u, v, t);
                    case "galaxy" -> K1.a(x, y, u, v, t);
                    case "rainbow" -> K1.c(u, v, t);
                    case "matrix" -> K1.d(u, v, t);
                    default -> K1.e(u, v, t);
                };
                img.method_61941(x, y, 0xFF000000 | rgb);
            }
        }
    }

    private static int a(float u, float v, float t) {
        float wave = (float)Math.sin((double)(v * 7.0f - t * 1.8f) + Math.sin(u * 5.0f + t * 0.9f) * (double)1.6f);
        float glow = 0.5f + 0.5f * wave;
        glow *= glow;
        int base = K1.a(330526, 728890, v);
        int band = K1.a(4063136, 9395199, K1.b(v * 1.2f + 0.15f * (float)Math.sin(u * 4.0f + t)));
        int c = K1.a(base, band, glow * 0.9f);
        return K1.a(c, 0.85f + 0.15f * (float)Math.sin(u * 20.0f + t * 3.0f));
    }

    private static int b(float u, float v, float t) {
        float flicker;
        float heat = K1.b(v * 1.15f - 0.1f);
        float fire = K1.b(heat * (0.75f + 0.5f * (flicker = 0.55f + 0.45f * (float)Math.sin(u * 13.0f + t * 7.0f + v * 9.0f) * (float)Math.sin(u * 7.0f - t * 5.0f))));
        int c = fire < 0.35f ? K1.a(1180418, 9049094, fire / 0.35f) : (fire < 0.7f ? K1.a(9049094, 16738832, (fire - 0.35f) / 0.35f) : K1.a(16738832, 16769658, (fire - 0.7f) / 0.3f));
        float ex = (float)Math.floor(u * 8.0f);
        float ey = K1.a(v - t * (0.4f + 0.2f * K1.a(ex, 3.0f)) + K1.a(ex, 7.0f));
        if (Math.abs(K1.a(u * 8.0f) - 0.5f) < 0.18f && ey < 0.05f) {
            c = K1.a(c, 16765562, 0.8f);
        }
        return c;
    }

    private static int a(int x, int y, float u, float v, float t) {
        float swirl = (float)Math.sin(u * 6.0f + v * 4.0f + t * 0.8f) + (float)Math.sin(u * 3.0f - v * 7.0f - t * 0.5f);
        float neb = K1.b(0.5f + 0.25f * swirl);
        int c = K1.a(459546, K1.a(5974686, 3107839, K1.b(v + 0.2f * swirl)), neb * neb);
        float r = K1.a((float)x, (float)y);
        if (r > 0.94f) {
            float tw = 0.5f + 0.5f * (float)Math.sin(t * (2.0f + r * 5.0f) + r * 50.0f);
            c = K1.a(c, 0xFFFFFF, tw * (r - 0.94f) / 0.06f);
        }
        return c;
    }

    private static int c(float u, float v, float t) {
        float hue = K1.a(u * 0.45f + v * 0.9f - t * 0.35f);
        int c = K1.f(hue, 0.75f, 1.0f);
        float shine = 0.85f + 0.15f * (float)Math.sin((u + v) * 12.0f - t * 4.0f);
        return K1.a(c, shine);
    }

    private static int d(float u, float v, float t) {
        float col = (float)Math.floor(u * 10.0f);
        float speed = 0.5f + K1.a(col, 1.0f) * 0.9f;
        float head = K1.a(t * speed * 0.6f + K1.a(col, 2.0f));
        float behind = head - v;
        if (behind < 0.0f) {
            behind += 1.0f;
        }
        float trail = (float)Math.exp(-behind * 5.0f);
        float cell = K1.a(col, (float)Math.floor(v * 16.0f) + (float)Math.floor(t * 6.0f));
        float glyph = cell > 0.35f ? 1.0f : 0.4f;
        int c = K1.a(67586, 1638242, trail * glyph);
        if (behind < 0.04f) {
            c = K1.a(c, 14090212, 0.8f);
        }
        return c;
    }

    private static int e(float u, float v, float t) {
        int c = K1.a(330268, 925514, v);
        float path = 0.5f + 0.28f * (float)Math.sin(v * 9.0f + t * 2.2f) + 0.08f * (float)Math.sin(v * 31.0f - t * 9.0f);
        float d = Math.abs(u - path);
        float bolt = (float)Math.exp(-d * d * 900.0f);
        float glow = (float)Math.exp(-d * d * 60.0f) * 0.5f;
        float flash = Math.max(0.0f, 1.0f - K1.a(t / 2.3f) * 6.0f);
        c = K1.a(c, 4172287, K1.b(glow + flash * 0.35f));
        return K1.a(c, 15267583, K1.b(bolt));
    }

    private static float a(float a, float b) {
        double h = Math.sin((double)a * 127.1 + (double)b * 311.7) * 43758.5453;
        return (float)(h - Math.floor(h));
    }

    private static float a(float f) {
        return f - (float)Math.floor(f);
    }

    private static float b(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    private static int a(int a, int b, float k) {
        k = K1.b(k);
        int r = Math.round((float)(a >> 16 & 0xFF) + (float)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
        int g = Math.round((float)(a >> 8 & 0xFF) + (float)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
        int bl = Math.round((float)(a & 0xFF) + (float)((b & 0xFF) - (a & 0xFF)) * k);
        return r << 16 | g << 8 | bl;
    }

    private static int a(int c, float k) {
        return K1.a(0, c, k);
    }

    private static int f(float h, float s, float v) {
        float b;
        float g;
        float r;
        float c = v * s;
        float hp = h * 6.0f;
        float x = c * (1.0f - Math.abs(hp % 2.0f - 1.0f));
        if (hp < 1.0f) {
            r = c;
            g = x;
            b = 0.0f;
        } else if (hp < 2.0f) {
            r = x;
            g = c;
            b = 0.0f;
        } else if (hp < 3.0f) {
            r = 0.0f;
            g = c;
            b = x;
        } else if (hp < 4.0f) {
            r = 0.0f;
            g = x;
            b = c;
        } else if (hp < 5.0f) {
            r = x;
            g = 0.0f;
            b = c;
        } else {
            r = c;
            g = 0.0f;
            b = x;
        }
        float m = v - c;
        return Math.round((r + m) * 255.0f) << 16 | Math.round((g + m) * 255.0f) << 8 | Math.round((b + m) * 255.0f);
    }

    public static final class C61 {
        private final String a;
        private final String b;
        C61(String a, String b) { this.a = a; this.b = b; }

        public String a() {
            return K1.a + this.a;
        }




        public String b() {
            return this.a;
        }

        public String c() {
            return this.b;
        }
    }
}

