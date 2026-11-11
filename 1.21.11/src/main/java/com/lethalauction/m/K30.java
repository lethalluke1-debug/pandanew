/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.class_156
 *  net.minecraft.class_2596
 *  net.minecraft.class_2935
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K19;
import com.lethalauction.m.K20;
import com.lethalauction.m.K24;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_156;
import net.minecraft.class_2596;
import net.minecraft.class_2935;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_9779;
import org.joml.Matrix3x2fStack;

public class K30
implements HudElement {
    public static final int a = 92;
    public static final int b = 50;
    public static final float c = 0.62f;
    private static final int e = -12200829;
    private static final int f = -15038;
    private static final int g = -42401;
    private static final long[] h = new long[4];
    private static int i;
    private static int j;
    private static volatile int k;
    private static volatile long l;
    private static long m;
    private static Object n;
    public static final K24 d;

    public void render(class_332 g, class_9779 deltaTracker) {
        class_310 mc = class_310.method_1551();
        if (!K9.y || K30.c() == 0 || !K37.c() || mc.field_1690.field_1842 || mc.field_1755 instanceof K19) {
            return;
        }
        K30.a(g, d.a(g.method_51421()), d.b(g.method_51443()));
    }

    private static int c() {
        return (K9.z ? 1 : 0) + (K9.A ? 1 : 0);
    }

    public static float a() {
        return 0.62f * K9.F;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static void a(class_310 mc) {
        class_634 conn = mc.method_1562();
        if (conn != n) {
            n = conn;
            long[] lArray = h;
            // MONITORENTER : h
            i = 0;
            j = 0;
            // MONITOREXIT : lArray
            k = -1;
            l = 0L;
        }
        if (conn == null) return;
        if (!K9.A) return;
        if (!K9.y && !(mc.field_1755 instanceof K20)) {
            return;
        }
        long now = class_156.method_658();
        if (now - m < 1000L) return;
        m = now;
        conn.method_52787((class_2596)new class_2935(now));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void a(long sentAt) {
        long rtt = class_156.method_658() - sentAt;
        if (rtt < 0L || rtt > 30000L) {
            return;
        }
        long[] lArray = h;
        synchronized (h) {
            K30.h[K30.j] = rtt;
            j = (j + 1) % h.length;
            i = Math.min(i + 1, h.length);
            long sum = 0L;
            for (int i = 0; i < K30.i; ++i) {
                sum += h[i];
            }
            k = (int)Math.round((double)sum / (double)i);
            // ** MonitorExit[var4_2] (shouldn't be in output)
            l = class_156.method_658();
            return;
        }
    }

    public static int b() {
        class_310 mc = class_310.method_1551();
        if (mc.method_1562() == null || mc.field_1724 == null) {
            return -1;
        }
        if (k >= 0 && class_156.method_658() - l < 10000L) {
            return k;
        }
        class_640 info = mc.method_1562().method_2871(mc.field_1724.method_5667());
        return info == null ? -1 : info.method_2959();
    }

    public static void a(class_332 g, float x, float y) {
        K30.a(g, x, y, K30.a());
    }

    public static void a(class_332 g, float x, float y, float s) {
        class_310 mc = class_310.method_1551();
        int n = Math.max(1, K30.c());
        int w = n * 92;
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K18.a(g, 0, 0, w, 50, 12, -435482856, 0x40FFFFFF);
        if (K30.c() == 0) {
            K18.b(g, "Nothing on", (float)w / 2.0f, 25.0f, 10.5f, -7695450, K18.b);
        }
        int sx = 0;
        if (K9.z) {
            int fps = mc.method_47599();
            K30.a(g, sx, "FPS", Integer.toString(fps), "", fps >= 60 ? -12200829 : (fps >= 30 ? -15038 : -42401));
            sx += 92;
        }
        if (K9.A) {
            int ping;
            if (sx > 0) {
                K18.a(g, sx, 11, 1, 28, 0x26FFFFFF);
            }
            K30.a(g, sx, "PING", (ping = K30.b()) < 0 ? "--" : Integer.toString(ping), ping < 0 ? "" : "ms", ping < 0 ? -7695450 : (ping < 80 ? -12200829 : (ping < 150 ? -15038 : -42401)));
        }
        p.popMatrix();
    }

    private static void a(class_332 g, int x, String label, String value, String unit, int color) {
        K18.a(g, (float)x + 18.0f, 16.0f, 3.0f, color);
        K18.a(g, label, (float)x + 26.0f, 16.0f, 9.5f, -7695450, K18.b);
        K18.a(g, value, (float)x + 14.0f, 34.0f, 17.0f, -1, K18.c);
        if (!unit.isEmpty()) {
            K18.a(g, unit, (float)x + 16.0f + K18.a(value, 17.0f, K18.c), 36.0f, 10.0f, -7695450, K18.b);
        }
    }

    static {
        k = -1;
        d = new K24(){

            @Override
            public String a() {
                return "the FPS and ping display";
            }

            @Override
            public float b() {
                return (float)(Math.max(1, K30.c()) * 92) * K30.a();
            }

            @Override
            public float c() {
                return 50.0f * K30.a();
            }

            @Override
            public float d() {
                return K9.D;
            }

            @Override
            public float e() {
                return K9.E;
            }

            @Override
            public void a(float fx, float fy) {
                K9.D = fx;
                K9.E = fy;
            }

            @Override
            public float f() {
                return K9.F;
            }

            @Override
            public void a(float size) {
                K9.F = size;
            }

            @Override
            public void g() {
                K9.D = 0.01f;
                K9.E = 0.02f;
                K9.F = 1.0f;
            }

            @Override
            public void a(class_332 g, float x, float y) {
                K30.a(g, x, y);
            }
        };
    }
}

