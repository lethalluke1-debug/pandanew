/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3532
 *  net.minecraft.class_5498
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_5498;

public final class K40 {
    public static class_304 a;
    public static class_304 b;
    private static boolean c;
    private static boolean d;
    private static float e;
    private static float f;
    private static class_5498 g;
    private static boolean h;
    private static boolean i;
    private static int j;
    private static double k;
    private static long l;
    private static final double m = 1.0;
    private static final double n = 0.5;
    private static final double o = 1.5;
    private static double p;
    private static double q;

    private K40() {
    }

    public static void a(class_310 mc) {
        boolean wantZoom;
        boolean wantLook;
        boolean allowed;
        boolean bl = allowed = K37.b() && mc.field_1724 != null;
        if (K9.L) {
            while (a.method_1436()) {
                d = !d;
            }
            wantLook = d;
        } else {
            while (a.method_1436()) {
            }
            d = false;
            wantLook = a.method_1434();
        }
        if ((wantLook &= allowed && K9.K) && !c) {
            c = true;
            e = mc.field_1724.method_36454();
            f = mc.field_1724.method_36455();
            g = mc.field_1690.method_31044();
            if (K9.M && g.method_31034()) {
                mc.field_1690.method_31043(class_5498.field_26665);
            }
        } else if (!wantLook && c) {
            c = false;
            d = false;
            if (g != null) {
                mc.field_1690.method_31043(g);
            }
        }
        if (K9.O) {
            while (b.method_1436()) {
                i = !i;
            }
            wantZoom = i;
        } else {
            while (b.method_1436()) {
            }
            i = false;
            wantZoom = b.method_1434();
        }
        if (!(wantZoom &= allowed && K9.N) && h) {
            j = 0;
            i = false;
        }
        if (wantZoom != h && K9.S) {
            mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)(wantZoom ? class_3417.field_26972 : class_3417.field_26973), (float)1.0f, (float)1.0f));
        }
        h = wantZoom;
    }

    public static boolean a() {
        return c;
    }

    public static float b() {
        return e;
    }

    public static float c() {
        return f;
    }

    public static void a(double dx, double dy) {
        e += (float)dx * 0.15f;
        f = class_3532.method_15363((float)(f + (float)dy * 0.15f), (float)-90.0f, (float)90.0f);
    }

    public static boolean d() {
        return h;
    }

    public static boolean a(double amount) {
        if (!h || !K9.Q || amount == 0.0) {
            return false;
        }
        j = class_3532.method_15340((int)(j + (amount > 0.0 ? 1 : -1)), (int)-3, (int)10);
        return true;
    }

    private static double b(double t) {
        return t >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * t);
    }

    public static double e() {
        long now = System.nanoTime();
        double dt = l == 0L ? 0.0 : Math.min(0.1, (double)(now - l) / 1.0E9);
        l = now;
        boolean smooth = K9.R;
        p = h ? (smooth ? Math.min(1.0, p + dt / 1.0) : 1.0) : (smooth ? Math.max(0.0, p - dt / 0.5) : 0.0);
        double eased = h ? K40.b(p) : 1.0 - K40.b(1.0 - p);
        double scrollTarget = Math.pow(1.5, j);
        q += (scrollTarget - q) * (smooth ? 1.0 - Math.exp(-dt * 10.0) : 1.0);
        double full = Math.max(1.0, (double)K9.P * q);
        k = 1.0 + (full - 1.0) * eased;
        if (!h && p <= 0.0) {
            q = 1.0;
        }
        return k;
    }

    public static double f() {
        return k > 1.0 ? 1.0 / k : 1.0;
    }

    static {
        k = 1.0;
        q = 1.0;
    }
}

