/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K9;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

public final class K54 {
    public static final int a = 0;
    public static final int b = 1;
    private static final int q = 300;
    public static final int c = 0;
    public static final int d = 1;
    public static final int e = 2;
    public static final int f = 3;
    public static final int g = 4;
    public static final int h = 5;
    public static final int i = 6;
    public static final int j = 7;
    public static final int k = 8;
    public static final int l = 9;
    public static final int m = 10;
    public static final String[] n = new String[]{"Wheel", "Plinko", "Lottery", "Horse Race", "Drag Strip", "Ice Boat", "Sumo", "Slot", "Claw", "Pinata", "Random"};
    private static final long[] r = new long[]{5500L, 3600L, 4600L, 6000L, 4600L, 5000L, 5400L, 4600L, 4600L, 3600L};
    public static final int[] o = new int[]{0, 1, 6, 7};
    private static volatile int s = 0;
    public static final int[] p = new int[]{-41862, -11548161, -15797, -10888821, -5215233, -26051, -11677471, -2073973, -7617718, -8485121, -36797, -14235942};
    private static final List<C101> t = new ArrayList<C101>();
    private static volatile boolean u;
    private static volatile boolean v;
    private static long w;
    private static long x;
    private static double y;
    private static double z;
    private static double A;
    private static volatile int B;
    private static volatile long C;
    private static volatile String D;
    private static long E;
    private static final double F = 2.2E-4;
    private static double G;
    private static long H;
    private static int I;
    private static boolean J;
    private static volatile long K;

    private K54() {
    }

    public static int a() {
        return s;
    }

    public static synchronized List<C101> b() {
        return new ArrayList<C101>(t);
    }

    public static synchronized int c() {
        return t.size();
    }

    public static boolean d() {
        return u;
    }

    public static boolean e() {
        return v;
    }

    public static long f() {
        return C;
    }

    public static double g() {
        long now = System.currentTimeMillis();
        if (v) {
            float p = x <= 0L ? 1.0f : Math.max(0.0f, Math.min(1.0f, (float)(now - w) / (float)x));
            double ease = 1.0 - Math.pow(1.0f - p, 3.0);
            return y + (z - y) * ease;
        }
        if (u && B < 0) {
            return G + (double)(now - H) * 2.2E-4;
        }
        return A;
    }

    public static boolean h() {
        return v && System.currentTimeMillis() >= w + x;
    }

    public static boolean i() {
        return !v && B >= 0;
    }

    public static int j() {
        return B;
    }

    public static long k() {
        return K;
    }

    public static synchronized String l() {
        return B >= 0 && B < t.size() ? K54.t.get((int)K54.B).a : "";
    }

    public static synchronized void a(boolean collecting) {
        u = collecting;
        if (collecting) {
            B = -1;
            E = System.currentTimeMillis();
            G = A;
            H = E;
        }
    }

    public static synchronized void m() {
        t.clear();
        B = -1;
        v = false;
        A = 0.0;
        G = 0.0;
        H = System.currentTimeMillis();
    }

    public static int n() {
        if (!u || K9.av <= 0 || v) {
            return -1;
        }
        long elapsed = System.currentTimeMillis() - E;
        int left = (int)Math.ceil((double)((long)K9.av * 1000L - elapsed) / 1000.0);
        return Math.max(0, left);
    }

    public static long o() {
        if (!u || K9.av <= 0 || v) {
            return -1L;
        }
        return Math.max(0L, (long)K9.av * 1000L - (System.currentTimeMillis() - E));
    }

    public static void p() {
        K54.q();
    }

    public static synchronized void q() {
        int w;
        int n = t.size();
        if (n < 2 || v) {
            return;
        }
        u = false;
        int type = K9.ax;
        boolean enabled = false;
        for (int gg : o) {
            if (gg != type) continue;
            enabled = true;
            break;
        }
        s = type == 10 || !enabled ? o[(int)(Math.random() * (double)o.length)] : K54.b(type);
        B = w = (int)(Math.random() * (double)n);
        if (s == 0) {
            double seg = Math.PI * 2 / (double)n;
            double center = (double)w * seg + seg / 2.0;
            double base = -1.5707963267948966 - center;
            int turns = 4 + (int)(Math.random() * 3.0);
            y = K54.g();
            for (z = base - Math.PI * 2 * (double)turns; z > y; z -= Math.PI * 2) {
            }
        }
        x = r[K54.b(s)] + (long)(Math.random() * 500.0);
        K54.w = System.currentTimeMillis();
        v = true;
        J = false;
        I = -1;
    }

    private static int b(int t) {
        return t < 0 || t >= r.length ? 0 : t;
    }

    public static float r() {
        if (!v) {
            return B >= 0 ? 1.0f : 0.0f;
        }
        return x <= 0L ? 1.0f : Math.max(0.0f, Math.min(1.0f, (float)(System.currentTimeMillis() - w) / (float)x));
    }

    public static synchronized void s() {
        if (u && !v && K9.av > 0 && t.size() >= 2 && System.currentTimeMillis() - E >= (long)K9.av * 1000L) {
            K54.p();
        }
        if (v) {
            float p = K54.r();
            float ease = 1.0f - (float)Math.pow(1.0f - p, 3.0);
            int ratchet = (int)(ease * 34.0f);
            if (ratchet != I) {
                I = ratchet;
                K54.u();
            }
            if (System.currentTimeMillis() >= w + x) {
                v = false;
                A = z;
                G = z;
                H = System.currentTimeMillis();
                K = System.currentTimeMillis();
                if (!J) {
                    J = true;
                    K54.v();
                }
            }
        }
    }

    private static void u() {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        float pitch = 1.3f + (float)(Math.random() * (double)0.7f);
        try {
            mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)((class_3414)class_3417.field_15204.comp_349()), (float)pitch, (float)0.5f));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void v() {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        try {
            mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3417.field_15195, (float)1.0f, (float)0.9f));
            mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3417.field_14709, (float)1.2f, (float)0.7f));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void a(String platform, String name, String text) {
    }

    public static void a(String plain) {
        long need;
        if (!u || v) {
            return;
        }
        Matcher in = Auctions.d(Auctions.c(plain));
        if (in == null) {
            return;
        }
        long amount = Auctions.e(in.group("amount").replace(" ", ""));
        if (amount != (need = Math.max(1L, Auctions.e(K9.at.trim())))) {
            return;
        }
        K54.a(in.group("name"), "game", false);
    }

    private static synchronized void a(String name, String platform, boolean allowDupe) {
        if (v || t.size() >= 300) {
            return;
        }
        if ((name = name.trim()).isEmpty()) {
            return;
        }
        if (!allowDupe) {
            for (C101 e : t) {
                if (!e.a.equalsIgnoreCase(name)) continue;
                return;
            }
        }
        t.add(new C101(name, platform));
        C = System.currentTimeMillis();
        D = name;
    }

    public static String t() {
        return D;
    }

    public static int a(int i) {
        return p[Math.floorMod(i, p.length)];
    }

    static {
        B = -1;
        D = "";
        I = -1;
    }

    public static final class C101 {
        public final String a;
        public final String b;
        public final long c;

        C101(String name, String platform) {
            this.a = name;
            this.b = platform;
            this.c = System.currentTimeMillis();
        }
    }
}

