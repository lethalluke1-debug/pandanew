/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.class_1068
 *  net.minecraft.class_10799
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_3675
 *  net.minecraft.class_437
 *  net.minecraft.class_490
 *  net.minecraft.class_7532
 *  net.minecraft.class_7923
 *  net.minecraft.class_8685
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Transactions;
import com.lethalauction.m.K1;
import com.lethalauction.m.K12;
import com.lethalauction.m.K14;
import com.lethalauction.m.K16;
import com.lethalauction.m.K18;
import com.lethalauction.m.K19;
import com.lethalauction.m.K2;
import com.lethalauction.m.K22;
import com.lethalauction.m.K25;
import com.lethalauction.m.K27;
import com.lethalauction.m.K29;
import com.lethalauction.m.K30;
import com.lethalauction.m.K31;
import com.lethalauction.m.K32;
import com.lethalauction.m.K37;
import com.lethalauction.m.K39;
import com.lethalauction.m.K4;
import com.lethalauction.m.K40;
import com.lethalauction.m.K47;
import com.lethalauction.m.K48;
import com.lethalauction.m.K49;
import com.lethalauction.m.K50;
import com.lethalauction.m.K52;
import com.lethalauction.m.K54;
import com.lethalauction.m.K57;
import com.lethalauction.m.K58;
import com.lethalauction.m.K6;
import com.lethalauction.m.K60;
import com.lethalauction.m.K8;
import com.lethalauction.m.K9;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import net.minecraft.class_1068;
import net.minecraft.class_10799;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_490;
import net.minecraft.class_7532;
import net.minecraft.class_7923;
import net.minecraft.class_8685;
import org.joml.Matrix3x2fStack;

public class K20
extends class_437 {
    static final int a = 1036;
    static final int b = 518;
    static final int c = -789515;
    static final int d = -1842200;
    static final int e = -6578268;
    static final int f = -5001030;
    static final int g = -7371369;
    static final int h = -15724264;
    static final int i = -15002078;
    static final int j = -15461606;
    static final int k = -13356484;
    static final int l = -3927012;
    static final int m = 61328;
    static final int n = 60394;
    static final int o = 60192;
    static final int p = 60161;
    static final int q = 60188;
    static final int r = 60000;
    static final int s = 60001;
    static final int t = 59999;
    static final int u = 60041;
    static final int v = 60156;
    static final int w = 60245;
    static final int x = 60181;
    static final int y = 63191;
    static final int z = 60024;
    static final int A = 61082;
    static final int B = 60231;
    static final int C = 60077;
    static final int D = 60225;
    static final class_2960 E = K18.a("textures/gui/frame.png");
    static final class_2960 F = K18.a("textures/gui/sidebar.png");
    static final class_2960 G = K18.a("textures/gui/content.png");
    static final String[] H = new String[]{"Limelight", "Sunset", "Electric", "Nebula", "Goldrush", "Emerald", "Plasma", "Crimson"};
    static final int[][] I = new int[][]{{12122374, 15268192, 326015, 1580036}, {16646258, 16607492, 11546111, 1966632}, {3042046, 311038, 6257407, 660016}, {10374911, 13073407, 5970121, 1441842}, {16694536, 16767081, 16619528, 2824202}, {515470, 3405747, 500337, 795424}, {16657614, 7232254, 579839, 1966402}, {16660068, 16611167, 13762567, 1835017}};
    static final int J = 232;
    static final int K = 54;
    static final int L = 1027;
    static final int M = 508;
    private static C81 N = K20.b();
    private C81 O = N;
    private final float[] P = new float[C81.values().length];
    private final float[] Q = new float[C81.values().length];
    private final float[] R = new float[C81.values().length];
    private final List<C80> S = new ArrayList<C80>();
    private class_304 T;
    private boolean U;
    private float V = 1.0f;
    private float W;
    private float X;
    private long Y = System.currentTimeMillis();
    private static final float Z = 320.0f;
    private int aa;
    private int ab = 0;
    private int ac;
    private int ad;
    private int ae = -1;
    private long af = System.currentTimeMillis();
    private long ag;
    private static final String[] ah = new String[]{"DonutSMP"};
    private long ai;
    private String aj = "";
    private int ak;
    private int al;
    private int am;
    private int an;
    private float ao;
    private static final int ap = 247;
    private static final int aq = 128;
    private static final int ar = 381;
    private static final int as = 369;
    private static final int at = 38;
    private static final int au = 41;
    private static final int av = 9;
    private final K31 aw = K31.a(40);
    private final K31 ax = K31.b(12);
    private final K31 ay = K31.a(K9.bc, 5);
    private final K31 az = K31.a(K9.bd, 5);
    private final K31 aA = K31.b(12);
    private final K31 aB = K31.a(32);
    private final K31 aC = K31.a("1", 5);
    private final K31 aD = K31.b(12);
    private final K31 aE = K31.a(16);
    private static int aF;
    private final K31 aG = K31.b(12);
    private final K31 aH = K31.a("60", 5);
    private boolean aI;
    private boolean aJ;
    private int aK;
    private class_1799 aL = class_1799.field_8037;
    private K31 aM;
    private List<class_1799> aN = List.of();
    private List<class_1799> aO = List.of();
    private String aP;
    private class_1799 aQ = class_1799.field_8037;
    private float aR;
    private float aS;
    private float aT;
    private float aU;
    private float aV;
    private int aW = -1;
    private int aX;
    private int aY;
    private long aZ;
    private long ba;
    private static final int bb = 836;
    private static final int bc = 144;
    private int bd;
    private boolean be;
    private static final int bf = 60164;
    private static final int bg = 241;
    private static final int bh = 500;
    private static final int bi = 360;
    private K4.C62 bj;
    private final K31 bk = K31.a(24);
    private final K31 bl = K31.a(24);
    private static final IntPredicate bm;
    private final K31 bn = new K31(K9.ae, 40, bm);
    private final K31 bo = new K31(K9.af, 40, bm);
    private final K31 bp = new K31(K9.ag, 40, bm);
    private final K31 bq = new K31(K9.au, 40, c -> c >= 32 && c != 127);
    private final K31 br = new K31(K9.at, 12, c -> c >= 48 && c <= 57 || c == 46 || c == 107 || c == 109 || c == 98 || c == 116 || c == 75 || c == 77 || c == 66 || c == 84);
    private final K31 bs = new K31(K9.C, 12, c -> c >= 48 && c <= 57 || c == 46 || c == 107 || c == 109 || c == 98 || c == 116 || c == 75 || c == 77 || c == 66 || c == 84);
    private final K31 bt = new K31("", 16, c -> c >= 97 && c <= 122 || c >= 65 && c <= 90 || c >= 48 && c <= 57 || c == 95);
    private boolean bu;
    private K4.C63 bv;
    private boolean bw;
    private float bx;
    private float by;
    private int bz;
    private int bA;
    private float bB = 1.0f;
    private int bC;
    private C82 bD;
    private class_332 bE;
    private int bF = -1;
    private int bG = 0;
    private final K31 bH = new K31(K37.n(), 40, c -> c >= 97 && c <= 122 || c >= 65 && c <= 90 || c >= 48 && c <= 57 || c == 45);
    private boolean bI;
    private volatile boolean bJ;
    private volatile String bK = "";
    private volatile boolean bL;
    private static final String[] bM;
    private static final int[] bN;
    private static final String[] bO;
    private static final int[] bP;
    private float[] bQ;
    private boolean[] bR;

    static int a(int p, int k) {
        if (p >= 0 && p < I.length) {
            return I[p][k];
        }
        int c = p - I.length;
        if (c >= 0 && c < K9.c.length) {
            return K9.c[c][k];
        }
        return I[2][k];
    }

    public K20() {
        super((class_2561)class_2561.method_43470((String)"Lethal Client"));
        this.ax.b = K9.bb;
        this.aA.b = K9.be;
        this.aB.b = K9.bf;
    }

    private static C81 b() {
        for (C81 p : C81.values()) {
            if (p == C81.CROSSHAIR || !p.name().equals(K9.ba)) continue;
            return p;
        }
        return C81.AUCTION;
    }

    private void c() {
        K9.ba = this.O.name();
        K9.bb = this.ax.b;
        K9.bc = this.ay.b.isEmpty() ? "1" : this.ay.b;
        K9.bd = this.az.b.isEmpty() ? "60" : this.az.b;
        K9.be = this.aA.b;
        K9.bf = this.aB.b;
        this.d();
        K9.b();
    }

    private void d() {
        K9.ae = this.bn.b.trim();
        K9.af = this.bo.b.trim();
        K9.ag = this.bp.b.trim();
        K9.au = this.bq.b.trim();
        String amt = this.br.b.trim();
        K9.at = amt.isEmpty() ? "1" : amt;
    }

    public void method_25432() {
        this.c();
        super.method_25432();
    }

    public boolean method_25421() {
        return false;
    }

    private void e() {
        this.V = Math.min((float)this.field_22789 * 0.564f / 1036.0f, (float)this.field_22790 * 0.94f / 518.0f);
        this.W = ((float)this.field_22789 - 1036.0f * this.V) / 2.0f;
        this.X = ((float)this.field_22790 - 518.0f * this.V) / 2.0f;
    }

    private float a(double mx) {
        return (float)((mx - (double)this.W) / (double)this.V);
    }

    private float b(double my) {
        return (float)((my - (double)this.X) / (double)this.V);
    }

    private void f() {
        this.ak = K32.a();
        this.an = K32.b();
        this.al = K32.c();
        this.am = K32.d();
        this.ao = K9.d ? 0.72f : 1.0f;
    }

    private int a(int color) {
        return K18.a(color, this.ao);
    }

    private int a(int base, float amount) {
        return K18.a(base, this.ak, amount);
    }

    private void a(int x, int y, int w, int h, Runnable action) {
        if (this.U) {
            int top = Math.max(y, 54);
            int bottom = Math.min(y + h, 508);
            if (bottom <= top) {
                return;
            }
            this.S.add(new C80(x, top, w, bottom - top, action));
        } else {
            this.S.add(new C80(x, y, w, h, action));
        }
    }

    public void method_25420(class_332 g, int mouseX, int mouseY, float partialTick) {
        if (this.field_22787.field_1687 != null && !K9.e) {
            this.method_57735(g);
        } else {
            super.method_25420(g, mouseX, mouseY, partialTick);
        }
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float partialTick) {
        this.e();
        this.aX = mouseX;
        this.aY = mouseY;
        this.aU = this.a((double)mouseX);
        this.aV = this.b((double)mouseY);
        this.f();
        this.S.clear();
        int pi = this.O.ordinal();
        this.Q[pi] = this.Q[pi] + (this.P[pi] - this.Q[pi]) * 0.35f;
        if (Math.abs(this.P[pi] - this.Q[pi]) < 0.3f) {
            this.Q[pi] = this.P[pi];
        }
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(this.W, this.X);
        p.scale(this.V);
        K18.a(g, E, 0, 0, 1036, 518, 2072, 1036, this.a(-1));
        this.a(g, this.a((double)mouseX), this.b((double)mouseY));
        if (K9.f) {
            K18.a(g, G, 232, 7, 795, 502, 1590, 1004, this.a(this.an));
        } else {
            K18.a(g, 232, 7, 795, 502, 14, this.a(-15856108));
            K18.a(g, 241, 48, 776, 1, this.ak);
        }
        this.a(g);
        float k = Math.min(1.0f, (float)(System.currentTimeMillis() - this.Y) / 320.0f);
        float ease = 1.0f - (float)Math.pow(1.0f - k, 3.0);
        int revealBottom = 54 + Math.round(454.0f * ease);
        g.method_44379(232, 54, 1027, Math.max(55, revealBottom));
        K18.h = 0.35f + 0.65f * ease;
        this.U = true;
        int top = Math.round(-this.Q[pi] - (1.0f - ease) * 12.0f);
        int contentBottom = switch (this.O.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.a(g, top);
            case 12 -> this.c(g, top);
            case 1 -> this.d(g, top);
            case 3 -> this.t(g, top);
            case 4 -> this.e(g, top);
            case 5 -> this.o(g, top);
            case 8 -> this.r(g, top);
            case 9 -> this.q(g, top);
            case 10 -> this.h(g, top);
            case 2 -> this.i(g, top);
            case 11 -> this.s(g, top);
            case 6 -> this.j(g, top);
            case 7 -> this.k(g, top);
        };
        this.U = false;
        K18.h = 1.0f;
        g.method_44380();
        float contentHeight = contentBottom - top;
        this.R[pi] = Math.max(0.0f, contentHeight - 454.0f + 6.0f);
        this.b(g, contentHeight, this.Q[pi]);
        p.popMatrix();
    }

    private void a(class_332 g, float mx, float my) {
        if (K9.f) {
            K18.a(g, F, 8, 7, 215, 500, 430, 1000, this.a(this.an));
        } else {
            K18.a(g, 8, 7, 215, 500, 14, this.a(-15593192), -14278100);
        }
        K18.a(g, 24, 25, 40, 40, 12, K18.a(this.ak, 0.22f));
        K18.a(g, 26, 27, 36, 36, 10, this.al);
        K18.a(g, 26, 27, 36, 27, 10, K18.a(this.ak, this.al, 0.25f));
        K18.a(g, 26, 27, 36, 18, 10, this.ak);
        K18.a(g, 61328, 44.0f, 45.0f, 20.0f, -1);
        K18.a(g, "LETHAL", 74.0f, 45.0f, 12.0f, -1186062, K18.d);
        K18.a(g, 26, 83, 178, 1, 0x66FFFFFF & this.a(-14013904, 0.3f));
        K18.a(g, "MODULES", 26.0f, 105.0f, 12.5f, -7568235, K18.a);
        int ty = 130;
        for (C81 pg : C81.values()) {
            if (pg == C81.CROSSHAIR) {
                continue;
            }
            boolean hover;
            boolean active = pg == this.O;
            boolean bl = hover = mx >= 24.0f && mx < 212.0f && my >= (float)(ty - 12) && my < (float)(ty + 12);
            if (active) {
                K18.a(g, 24, ty - 12, 188, 24, 7, K18.a(this.a(-14013902, 0.22f), 0.6f));
                K18.a(g, 20, ty - 7, 3, 14, 1, this.ak);
            } else if (hover) {
                K18.a(g, 24, ty - 12, 188, 24, 7, 1077289536);
            }
            int activeColor = K18.a(this.ak, -1, 0.08f);
            K18.b(g, K22.a(pg.icon), 47.0f, (float)ty, 21.0f, active ? activeColor : -5924947, pg.iconFont);
            K18.a(g, pg.label, 75.0f, (float)ty, 13.0f, active ? activeColor : -4477500, active ? K18.c : K18.b);
            this.a(24, ty - 12, 188, 24, () -> {
                if (this.O != pg) {
                    this.Y = System.currentTimeMillis();
                }
                this.O = pg;
                N = pg;
                K9.ba = pg.name();
            });
            ty += 24;
        }
        K18.a(g, 26, 439, 178, 1, 0x66FFFFFF & this.a(-14013904, 0.3f));
        K18.a(g, 20, 449, 192, 50, 10, this.a(-15789287), -14411222);
        class_7532.method_52722((class_332)g, (class_8685)this.h(), (int)31, (int)459, (int)28);
        K18.a(g, K20.a(K20.g(), 100.0f, 13.0f, K18.c), 75.0f, 465.0f, 13.0f, -789515, K18.c);
        K18.a(g, K14.a(K37.h()), 75.0f, 483.0f, 11.5f, -7371369, K18.a);
        K18.a(g, 191.0f, 473.0f, 7.0f, 860081279);
        K18.a(g, 191.0f, 473.0f, 4.5f, 2000931967);
        K18.a(g, 191.0f, 473.0f, 3.5f, -12333953);
    }

    private static String g() {
        if (K9.m) {
            return "Player";
        }
        return class_310.method_1551().method_1548().method_1676();
    }

    private class_8685 h() {
        class_310 mc = class_310.method_1551();
        if (K9.m) {
            return class_1068.method_62620();
        }
        return mc.field_1724 != null ? mc.field_1724.method_52814() : class_1068.method_52854((GameProfile)mc.method_53462());
    }

    private void a(class_332 g) {
        K18.a(g, 235, 11, 779, 36, 10, this.a(-15658471));
        float hx = 253.0f;
        K18.a(g, "Hello, ", hx, 29.0f, 13.0f, -3947572, K18.a);
        K18.a(g, K20.g(), hx + K18.a("Hello, ", 13.0f, K18.a), 29.0f, 13.0f, -789515, K18.c);
    }

    private void b(class_332 g, float contentHeight, float offset) {
        float view = 454.0f;
        if (!(contentHeight <= view + 6.0f)) {
            float trackTop = 58.0f;
            float trackH = 446.0f;
            float thumbH = Math.max(30.0f, trackH * view / contentHeight);
            float maxS = contentHeight - view + 6.0f;
            float t = maxS <= 0.0f ? 0.0f : offset / maxS;
            int y = Math.round(trackTop + (trackH - thumbH) * t);
            K18.a(g, 1021, y, 3, Math.round(thumbH), 1, -11646378);
        }
    }

    private List<class_1799> i() {
        String q;
        if (this.aN.isEmpty()) {
            ArrayList<class_1799> list = new ArrayList<class_1799>();
            try {
                for (class_1792 item : class_7923.field_41178) {
                    if (item == class_1802.field_8162) continue;
                    list.add(new class_1799((class_1935)item));
                }
            }
            catch (RuntimeException var6) {
                return List.of();
            }
            this.aN = list;
            this.aP = null;
        }
        if (!(q = this.aw.b.trim().toLowerCase(Locale.ROOT)).equals(this.aP)) {
            this.aP = q;
            if (q.isEmpty()) {
                this.aO = this.aN;
            } else {
                String idQuery = q.replace(' ', '_');
                ArrayList<class_1799> list = new ArrayList<class_1799>();
                for (class_1799 stack : this.aN) {
                    if (!stack.method_7964().getString().toLowerCase(Locale.ROOT).contains(q) && !class_7923.field_41178.method_10221(stack.method_7909()).method_12832().contains(idQuery)) continue;
                    list.add(stack);
                }
                this.aO = list;
            }
            this.aS = 0.0f;
            this.aR = 0.0f;
        }
        return this.aO;
    }

    private boolean a(float x, float y) {
        return x >= 247.0f && x < 628.0f && y >= 128.0f && y < 497.0f;
    }

    private int a(class_332 g, int t) {
        long each2;
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        List<class_1799> list = this.i();
        K18.a(g, 235, 58, 405, 447, 12, cardFill, cardBorder);
        K18.a(g, 254.0f, 76.0f, 3.5f, this.ak);
        K18.a(g, "New Auction", 263.0f, 76.0f, 13.5f, -789515, K18.c);
        K18.c(g, list.size() + (list.size() == 1 ? " item" : " items"), 627.0f, 76.0f, 12.0f, -5001030, K18.a);
        this.a(g, this.aw, 247, 92, 381, 28, "Search items...");
        K18.a(g, 247, 128, 381, 369, 10, this.a(-15987951), -14541270);
        int x0 = 255;
        int y0 = 136;
        int viewH = 353;
        int rows = (list.size() + 9 - 1) / 9;
        int contentH = Math.max(0, rows * 41 - 3);
        this.aT = Math.max(0, contentH - viewH);
        this.aS = Math.max(0.0f, Math.min(this.aT, this.aS));
        this.aR += (this.aS - this.aR) * 0.35f;
        if (Math.abs(this.aS - this.aR) < 0.3f) {
            this.aR = this.aS;
        }
        int scrollPx = Math.round(this.aR);
        int gridTop = 130;
        int gridBottom = 495;
        g.method_44379(248, gridTop, 627, gridBottom);
        int firstRow = Math.max(0, (scrollPx - 8) / 41);
        int lastRow = Math.min(rows - 1, (scrollPx + viewH + 8) / 41 + 1);
        class_1799 hovered = class_1799.field_8037;
        for (int row = firstRow; row <= lastRow; ++row) {
            int i;
            for (int col = 0; col < 9 && (i = row * 9 + col) < list.size(); ++col) {
                boolean hover;
                class_1799 stack = list.get(i);
                int cx = x0 + col * 41;
                int cy = y0 + row * 41 - scrollPx;
                boolean isSelected = !this.aQ.method_7960() && this.aQ.method_7909() == stack.method_7909() || this.aJ && !this.aL.method_7960() && this.aL.method_7909() == stack.method_7909();
                boolean bl = hover = this.a(this.aU, this.aV) && this.aU >= (float)cx && this.aU < (float)(cx + 38) && this.aV >= (float)cy && this.aV < (float)(cy + 38) && this.aV >= (float)gridTop && this.aV < (float)gridBottom;
                if (isSelected) {
                    K18.a(g, cx, cy, 38, 38, 7, this.a(-15330020, 0.25f), this.ak);
                } else {
                    K18.a(g, cx, cy, 38, 38, 7, hover ? -14409172 : -15330020);
                }
                if (hover) {
                    hovered = stack;
                }
                Matrix3x2fStack pose = g.method_51448();
                pose.pushMatrix();
                pose.translate((float)(cx + 7), (float)(cy + 7));
                pose.scale(1.5f);
                g.method_51427(stack, 0, 0);
                pose.popMatrix();
                int top = Math.max(cy, gridTop);
                int bottom = Math.min(cy + 38, gridBottom);
                if (bottom <= top) continue;
                this.a(cx, top, 38, bottom - top, () -> this.a(stack));
            }
        }
        g.method_44380();
        if (list.isEmpty()) {
            K18.b(g, "No items match your search", 437.5f, 168.0f, 12.5f, -5001030, K18.a);
        }
        if (this.aT > 0.0f) {
            float thumbH = Math.max(24.0f, (float)viewH * (float)viewH / (float)contentH);
            int ty = Math.round(136.0f + ((float)viewH - thumbH) * (this.aR / this.aT));
            K18.a(g, 622, ty, 3, Math.round(thumbH), 1, K18.a(this.ak, 0.75f));
        }
        if (!hovered.method_7960()) {
            g.method_51438(this.field_22793, hovered.method_7964(), this.aX, this.aY);
        }
        K18.a(g, 652, 58, 361, 82, 12, cardFill, cardBorder);
        if (!this.aJ) {
            K18.a(g, 664, 69, 60, 60, 10, this.a(-15987951));
            if (this.aQ.method_7960()) {
                K18.a(g, "No item yet", 738.0f, 88.0f, 13.0f, -789515, K18.c);
                K18.a(g, "Pick one on the left", 738.0f, 108.0f, 12.0f, -5001030, K18.a);
            } else {
                Matrix3x2fStack pose = g.method_51448();
                pose.pushMatrix();
                pose.translate(674.0f, 79.0f);
                pose.scale(2.5f);
                g.method_51427(this.aQ, 0, 0);
                pose.popMatrix();
                K18.a(g, K20.a(this.aQ.method_7964().getString(), 170.0f, 14.0f, K18.c), 738.0f, 99.0f, 14.0f, -789515, K18.c);
            }
            if (!this.aI) {
                this.a(g, 917, 86, 84, 26, "+ 2nd item", this.a(-15395813), -13356484, -789515, () -> {
                    this.aJ = true;
                    this.aK = this.aQ.method_7960() ? 0 : 1;
                });
            }
        } else {
            this.a(g, 664, 69, 60, this.aQ, this.aK == 0, 0);
            this.a(g, 732, 69, 60, this.aL, this.aK == 1, 1);
            String n1 = this.aQ.method_7960() ? "Pick item 1" : this.aQ.method_7964().getString();
            String n2 = this.aL.method_7960() ? "Pick item 2" : this.aL.method_7964().getString();
            K18.a(g, K20.a(n1, 110.0f, 12.0f, K18.c), 804.0f, 86.0f, 12.0f, this.aQ.method_7960() ? -5001030 : -789515, K18.c);
            K18.a(g, K20.a(n2, 110.0f, 12.0f, K18.c), 804.0f, 108.0f, 12.0f, this.aL.method_7960() ? -5001030 : -789515, K18.c);
            this.a(g, 921, 86, 80, 26, "Remove 2nd", this.a(-15395813), -13356484, -789515, () -> {
                this.aJ = false;
                this.aL = class_1799.field_8037;
                this.aK = 0;
            });
        }
        this.b(g);
        if (this.aI) {
            return this.b(g, t);
        }
        long bid = Auctions.e(this.ax.b);
        int qty = K20.b(this.ay.b);
        int secs = K20.b(this.az.b);
        long each = this.aA.b.isBlank() ? 0L : Auctions.e(this.aA.b);
        int qty2 = K20.b(this.aC.b);
        long l = each2 = this.aG.b.isBlank() ? 0L : Auctions.e(this.aG.b);
        if (!this.aJ) {
            K18.a(g, "Minimum bid", 664.0f, 178.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.ax, 652, 188, 361, 32, "e.g. 10k");
            K18.a(g, "Quantity", 664.0f, 238.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.ay, 652, 248, 174, 32, "1");
            K18.a(g, "Timer (sec)", 851.0f, 238.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.az, 839, 248, 174, 32, "60");
            K18.a(g, "Worth of one item", 664.0f, 298.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.aA, 652, 308, 361, 32, "Optional");
        } else {
            K18.a(g, "Minimum bid (for both)", 664.0f, 178.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.ax, 652, 188, 361, 32, "e.g. 10k");
            K18.a(g, "Qty item 1", 664.0f, 238.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.ay, 652, 248, 113, 32, "1");
            K18.a(g, "Qty item 2", 788.0f, 238.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.aC, 776, 248, 113, 32, "1");
            K18.a(g, "Timer (sec)", 912.0f, 238.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.az, 900, 248, 113, 32, "60");
            K18.a(g, "Worth of one (item 1)", 664.0f, 298.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.aA, 652, 308, 174, 32, "Optional");
            K18.a(g, "Worth of one (item 2)", 851.0f, 298.0f, 12.5f, -1842200, K18.b);
            this.a(g, this.aG, 839, 308, 174, 32, "Optional");
        }
        K18.a(g, "Note on the auction card", 664.0f, 358.0f, 12.5f, -1842200, K18.b);
        this.a(g, this.aB, 652, 368, 361, 32, "e.g. Pay over = I keep");
        boolean secondOk = !this.aJ || !this.aL.method_7960() && qty2 > 0 && each2 >= 0L;
        boolean ready = !this.aQ.method_7960() && bid > 0L && qty > 0 && secs > 0 && each >= 0L && secondOk;
        long total = each * (long)qty + (this.aJ ? each2 * (long)qty2 : 0L);
        Object hint = null;
        int hintColor = -5001030;
        if (System.currentTimeMillis() < this.aZ) {
            hint = "Auction started! See Recent Auctions.";
            hintColor = this.am;
        } else if (System.currentTimeMillis() < this.ba) {
            hint = "Auction cancelled.";
            hintColor = -30059;
        } else if (!this.aQ.method_7960() && !this.ax.b.isEmpty() && bid <= 0L) {
            hint = "Enter a valid minimum bid (e.g. 2500 or 2.5k).";
        } else if (this.aJ && this.aL.method_7960()) {
            hint = this.aQ.method_7960() ? "Pick the two items on the left." : "Now pick item 2 on the left.";
        } else if (ready && total > 0L) {
            hint = "Total worth: " + Auctions.a(total);
        }
        if (hint != null) {
            K18.b(g, (String)hint, 832.5f, 409.0f, 12.0f, hintColor, K18.b);
        }
        if (ready) {
            this.a(g, 652, 420, 361, 32, "Start Auction", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
                if (this.aJ) {
                    Auctions.a(this.aQ, qty, each, this.aL, qty2, each2, bid, secs, this.aB.b.trim());
                } else {
                    Auctions.a(this.aQ, qty, bid, each, secs, this.aB.b.trim());
                }
                this.c();
                this.aZ = System.currentTimeMillis() + 3000L;
                this.k();
            });
        } else {
            K18.a(g, 652, 420, 361, 32, 6, this.a(-15395813), -14474966);
            K18.b(g, "Start Auction", 832.5f, 436.5f, 12.5f, -10593178, K18.c);
        }
        Auctions.C65 running = Auctions.d();
        if (running != null) {
            this.a(g, 652, 462, 361, 32, "Cancel Auction", this.a(-14020844), -8773072, -30059, () -> {
                running.cancel();
                this.ba = System.currentTimeMillis() + 3000L;
                this.aZ = 0L;
            });
        } else {
            this.a(g, 652, 462, 361, 32, "Cancel", this.a(-15395813), -13356484, -789515, this::k);
        }
        return t + 452;
    }

    private List<K31> j() {
        if (this.aI) {
            return List.of(this.aw, this.ax, this.ay, this.aH, this.aB);
        }
        return this.aJ ? List.of(this.aw, this.ax, this.ay, this.aC, this.az, this.aA, this.aG, this.aB) : List.of(this.aw, this.ax, this.ay, this.az, this.aA, this.aB);
    }

    private void b(class_332 g) {
        String[] modes = new String[]{"Standard", "Reset"};
        int y = 146;
        int h = 18;
        int gap = 8;
        int x0 = 652;
        int tabW = (361 - gap) / 2;
        K18.a(g, x0 - 2, y - 2, 365, h + 4, 11, this.a(-15987951));
        int i = 0;
        while (i < 2) {
            int tx = x0 + i * (tabW + gap);
            boolean on = i == 1 == this.aI;
            K18.a(g, tx, y, tabW, h, 8, on ? this.al : this.a(-14935012), on ? this.ak : this.a(-14804444, 0.18f));
            K18.b(g, modes[i], (float)tx + (float)tabW / 2.0f, (float)y + (float)h / 2.0f, 11.5f, on ? this.am : -4539718, K18.c);
            int fi = i++;
            this.a(tx, y, tabW, h, () -> {
                boolean bl = this.aI = fi == 1;
                if (this.aI) {
                    this.aJ = false;
                    this.aL = class_1799.field_8037;
                    this.aK = 0;
                }
                this.aM = null;
            });
        }
    }

    private int b(class_332 g, int t) {
        long price = Auctions.e(this.ax.b);
        int qty = K20.b(this.ay.b);
        int secs = K20.b(this.aH.b);
        K18.a(g, "Exact price to pay", 664.0f, 178.0f, 12.5f, -1842200, K18.b);
        this.a(g, this.ax, 652, 188, 361, 32, "e.g. 25m");
        K18.a(g, "Quantity", 664.0f, 238.0f, 12.5f, -1842200, K18.b);
        this.a(g, this.ay, 652, 248, 174, 32, "1");
        K18.a(g, "Reset timer (sec)", 851.0f, 238.0f, 12.5f, -1842200, K18.b);
        this.a(g, this.aH, 839, 248, 174, 32, "60");
        K18.a(g, "Note on the auction card", 664.0f, 298.0f, 12.5f, -1842200, K18.b);
        this.a(g, this.aB, 652, 308, 361, 32, "e.g. Last to pay wins");
        K18.a(g, "Only pays of exactly this price count. Each one restarts the", 652.0f, 352.0f, 10.5f, -6513498, K18.a);
        K18.a(g, "timer; when it runs out, the last person to pay wins.", 652.0f, 367.0f, 10.5f, -6513498, K18.a);
        boolean ready = !this.aQ.method_7960() && price > 0L && qty > 0 && secs > 0;
        Object hint = null;
        int hintColor = -5001030;
        if (System.currentTimeMillis() < this.aZ) {
            hint = "Auction started! See Recent Auctions.";
            hintColor = this.am;
        } else if (System.currentTimeMillis() < this.ba) {
            hint = "Auction cancelled.";
            hintColor = -30059;
        } else if (!this.aQ.method_7960() && !this.ax.b.isEmpty() && price <= 0L) {
            hint = "Enter a valid price (e.g. 25m).";
        } else if (ready) {
            hint = "Pay " + Auctions.a(price) + " \u2192 " + secs + "s timer, last payer wins.";
        }
        if (hint != null) {
            K18.b(g, (String)hint, 832.5f, 404.0f, 12.0f, hintColor, K18.b);
        }
        if (ready) {
            this.a(g, 652, 420, 361, 32, "Start Reset Auction", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
                Auctions.a(this.aQ, qty, price, secs, this.aB.b.trim());
                this.c();
                this.aZ = System.currentTimeMillis() + 3000L;
                this.k();
            });
        } else {
            K18.a(g, 652, 420, 361, 32, 6, this.a(-15395813), -14474966);
            K18.b(g, "Start Reset Auction", 832.5f, 436.5f, 12.5f, -10593178, K18.c);
        }
        Auctions.C65 running = Auctions.d();
        if (running != null) {
            this.a(g, 652, 462, 361, 32, "Cancel Auction", this.a(-14020844), -8773072, -30059, () -> {
                running.cancel();
                this.ba = System.currentTimeMillis() + 3000L;
                this.aZ = 0L;
            });
        } else {
            this.a(g, 652, 462, 361, 32, "Cancel", this.a(-15395813), -13356484, -789515, this::k);
        }
        return t + 452;
    }

    private void a(class_1799 stack) {
        if (this.aJ && this.aK == 1) {
            this.aL = stack;
        } else {
            this.aQ = stack;
            if (this.aJ && this.aL.method_7960()) {
                this.aK = 1;
            }
        }
    }

    private void a(class_332 g, int x, int y, int size, class_1799 stack, boolean active, int slot) {
        if (active) {
            K18.a(g, x, y, size, size, 10, this.a(-15987951), this.ak);
        } else {
            K18.a(g, x, y, size, size, 10, this.a(-15987951));
        }
        if (stack.method_7960()) {
            K18.b(g, "+", (float)x + (float)size / 2.0f, (float)y + (float)size / 2.0f, 20.0f, active ? this.am : -9541000, K18.b);
        } else {
            Matrix3x2fStack pose = g.method_51448();
            pose.pushMatrix();
            pose.translate((float)x + ((float)size - 35.2f) / 2.0f, (float)y + ((float)size - 35.2f) / 2.0f);
            pose.scale(2.2f);
            g.method_51427(stack, 0, 0);
            pose.popMatrix();
        }
        K18.a(g, x + 3, y + 3, 14, 14, 7, active ? this.ak : -14409172);
        K18.b(g, Integer.toString(slot + 1), (float)x + 10.0f, (float)y + 10.5f, 9.0f, -1, K18.c);
        this.a(x, y, size, size, () -> {
            this.aK = slot;
        });
    }

    private void k() {
        this.aQ = class_1799.field_8037;
        this.aL = class_1799.field_8037;
        this.aJ = false;
        this.aK = 0;
        this.aC.b = "1";
        this.aG.b = "";
        this.ax.b = "";
        this.ay.b = "1";
        this.az.b = "60";
        this.aA.b = "";
        this.aM = null;
    }

    private static int b(String s) {
        try {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        }
        catch (NumberFormatException var2) {
            return 0;
        }
    }

    private static String a(String s, float maxWidth, float size, class_2960 font) {
        if (K18.a(s, size, font) <= maxWidth) {
            return s;
        }
        while (s.length() > 1 && K18.a(s + "...", size, font) > maxWidth) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "...";
    }

    private void a(class_332 g, K31 box, int x, int y, int w, int h, String placeholder) {
        boolean isFocused = this.aM == box;
        K18.a(g, x, y, w, h, 8, this.a(-15987951), isFocused ? this.ak : -14014925);
        float cy = (float)y + (float)h / 2.0f;
        float maxW = w - 26;
        if (box.b.isEmpty() && !isFocused) {
            K18.a(g, placeholder, (float)(x + 12), cy, 13.0f, -10593178, K18.b);
        } else {
            String shown = box.b;
            while (!shown.isEmpty() && K18.a(shown, 13.0f, K18.b) > maxW) {
                shown = shown.substring(1);
            }
            if (isFocused && box.c && !shown.isEmpty()) {
                K18.a(g, x + 10, Math.round(cy - 9.0f), Math.round(K18.a(shown, 13.0f, K18.b) + 4.0f), 18, 3, K18.a(this.ak, 0.45f));
            }
            K18.a(g, shown, (float)(x + 12), cy, 13.0f, -789515, K18.b);
            if (isFocused && !box.c && System.currentTimeMillis() / 500L % 2L == 0L) {
                int cx = Math.round((float)(x + 13) + K18.a(shown, 13.0f, K18.b));
                K18.a(g, cx, Math.round(cy - 7.0f), 1, 14, -789515);
            }
        }
        this.a(x, y, w, h, () -> {
            this.aM = box;
            box.c = false;
        });
    }

    private void a(class_332 g, int x, int cy, boolean on, Runnable action) {
        if (on) {
            K18.a(g, x, cy - 8, 32, 16, 8, this.ak);
            K18.a(g, x + 24, cy, 6.0f, -1);
        } else {
            K18.a(g, x, cy - 8, 32, 16, 8, -13749959);
            K18.a(g, x + 8, cy, 6.0f, -6380886);
        }
        this.a(x - 2, cy - 10, 36, 20, action);
    }

    private void a(class_332 g, int x, int cy, int w, float frac) {
        K18.a(g, x, cy - 2, w, 4, 2, -13621444);
        int fw = Math.round((float)w * frac);
        if (fw > 0) {
            K18.a(g, x, cy - 2, Math.max(4, fw), 4, 2, K18.a(this.ak, -16777216, 0.2f));
        }
        K18.a(g, x + fw, cy, 5.0f, -1);
    }

    private void a(class_332 g, int x, int cy, int w, String value) {
        K18.a(g, x, cy - 12, w, 25, 6, -16448505, this.a(-15462376, 0.15f));
        K18.a(g, 60000, (float)(x + 11), (float)cy, 12.0f, -6513498);
        K18.a(g, 60001, (float)(x + w - 11), (float)cy, 12.0f, -6513498);
        K18.b(g, value, (float)x + (float)w / 2.0f, (float)cy, 12.5f, -789515, K18.b);
        this.a(x, cy - 12, w, 25, (Runnable)null);
    }

    private void a(class_332 g, int x, int y, int w, int h, String label, int fill, int border, int color, Runnable action) {
        K18.a(g, x, y, w, h, 6, fill, border);
        K18.b(g, label, (float)x + (float)w / 2.0f, (float)y + (float)h / 2.0f + 0.5f, 12.5f, color, K18.c);
        this.a(x, y, w, h, action);
    }

    private void a(class_332 g, int x, int y, int w, int h, String label) {
        this.a(g, x, y, w, h, label, this.al, K18.a(this.ak, -16777216, 0.3f), this.am, null);
    }

    private int c(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        int boxFill = this.a(this.a(-15790828, 0.04f));
        int boxBorder = -14015437;
        K18.a(g, 60192, 253.0f, (float)(t + 80), 22.0f, head);
        K18.a(g, "Settings", 272.0f, (float)(t + 76), 13.5f, -789515, K18.c);
        K18.a(g, "Configure interface, security, and system behavior.", 272.0f, (float)(t + 92), 10.5f, -5001030, K18.a);
        K18.a(g, 235, t + 120, 778, 266, 12, cardFill, cardBorder);
        K18.a(g, 60041, 261.0f, (float)(t + 150), 16.0f, head);
        K18.a(g, "Interface", 280.0f, (float)(t + 150), 13.0f, -789515, K18.c);
        K18.a(g, "GUI Settings", 250.0f, (float)(t + 176), 10.5f, -5001030, K18.a);
        K18.a(g, 250, t + 192, 746, 56, 10, boxFill, boxBorder);
        K18.a(g, "Menu Bind", 268.0f, (float)(t + 220), 12.5f, -789515, K18.b);
        K18.a(g, "Click, then press a key to open the menu with.", 268.0f, (float)(t + 236), 9.5f, -7568235, K18.a);
        this.a(g, 876, t + 208, LethalAuctionClient.openMenu());
        K18.a(g, 250, t + 254, 746, 56, 10, boxFill, boxBorder);
        K18.a(g, "Figure Waypoint", 268.0f, (float)(t + 282), 12.5f, -789515, K18.b);
        K18.a(g, "Look at a block and press this to send your mini figure to it; press again to recall. Press Delete while binding to unbind.", 268.0f, (float)(t + 298), 9.5f, -7568235, K18.a);
        this.a(g, 876, t + 270, LethalAuctionClient.figureWaypoint());
        K18.a(g, 250, t + 316, 746, 56, 10, boxFill, boxBorder);
        K18.a(g, "Lethal title screen", 268.0f, (float)(t + 338), 12.5f, -789515, K18.b);
        K18.a(g, "Custom menu background and branding.", 268.0f, (float)(t + 354), 9.5f, -7568235, K18.a);
        this.a(g, 945, t + 344, K9.G, () -> {
            K9.G = !K9.G;
            K9.b();
        });
        int s0 = t + 400;
        K18.a(g, 235, s0, 778, 184, 12, cardFill, cardBorder);
        K18.a(g, 60156, 260.0f, (float)(s0 + 29), 16.0f, head);
        K18.a(g, "Sounds", 281.0f, (float)(s0 + 30), 13.0f, -789515, K18.c);
        K18.a(g, "Client Sounds", 250.0f, (float)(s0 + 56), 10.5f, -5001030, K18.a);
        K18.a(g, 250, s0 + 72, 746, 96, 10, boxFill, boxBorder);
        K18.a(g, "Module Sound", 268.0f, (float)(s0 + 96), 12.5f, -789515, K18.b);
        this.a(g, 945, s0 + 96, K9.g, () -> {
            K9.g = !K9.g;
            K9.b();
        });
        K18.a(g, 258, s0 + 119, 730, 1, -14542295);
        K18.a(g, "Volume", 268.0f, (float)(s0 + 143), 12.5f, -789515, K18.b);
        K18.c(g, Math.round(K9.h * 100.0f) + "%", 822.0f, (float)(s0 + 142), 12.0f, -3947572, K18.b);
        this.bd = s0 + 142;
        this.a(g, 836, this.bd, 144, K9.h);
        this.a(830, this.bd - 9, 156, 18, () -> {
            this.be = true;
            this.a(this.aU);
        });
        return s0 + 184 + 10;
    }

    private int a(class_332 g, int s1, int head, int cardFill, int cardBorder, int boxFill, int boxBorder) {
        int statusColor;
        String status;
        K18.a(g, 235, s1, 778, 160, 12, cardFill, cardBorder);
        K18.b(g, K22.a(60130), 260.0f, (float)(s1 + 29), 16.0f, head, K22.a);
        K18.a(g, "License", 281.0f, (float)(s1 + 30), 13.0f, -789515, K18.c);
        K18.a(g, "Security", 250.0f, (float)(s1 + 56), 10.5f, -5001030, K18.a);
        String ver = "Current version " + K37.a;
        if (K39.a() == K39.C92.READY) {
            ver = ver + "  \u00b7  " + K39.b() + " downloaded, restart to update";
        }
        K18.c(g, ver, 996.0f, (float)(s1 + 30), 11.5f, -5001030, K18.b);
        K18.a(g, 250, s1 + 72, 746, 72, 10, boxFill, boxBorder);
        if (K37.d()) {
            K18.a(g, "Free", 268.0f, (float)(s1 + 96), 12.5f, -789515, K18.b);
            String freeStatus = K37.a() ? "Active \u00b7 Lethal Client is free, no key needed" : (K37.m().isEmpty() ? "Connecting..." : K37.m());
            int freeColor = K37.a() ? -13315969 : -5001030;
            K18.a(g, "\u2022 " + K20.a(freeStatus, 700.0f, 10.5f, K18.a), 268.0f, (float)(s1 + 118), 10.5f, freeColor, K18.a);
            return s1 + 173;
        }
        K18.a(g, "License Key", 268.0f, (float)(s1 + 96), 12.5f, -789515, K18.b);
        if (!this.bK.isEmpty()) {
            status = this.bK;
            statusColor = this.bL ? -42401 : -13315969;
        } else if (K37.a()) {
            long exp = K37.o();
            status = exp > 0L ? "Active on this PC \u00b7 expires " + String.valueOf(LocalDate.ofInstant(Instant.ofEpochMilli(exp), ZoneId.systemDefault())) : "Active on this PC";
            statusColor = -13315969;
        } else {
            status = K37.m().isEmpty() ? "Not activated" : K37.m();
            statusColor = -42401;
        }
        K18.a(g, "\u2022 " + K20.a(status, 260.0f, 10.5f, K18.a), 268.0f, (float)(s1 + 118), 10.5f, statusColor, K18.a);
        ArrayList<K22.C84> spots = new ArrayList<K22.C84>();
        K22.a(g, this.bH, 560, s1 + 92, 300, 32, this.bI, this.aM == this.bH, this.ak, "Enter license key", () -> {
            this.bI = !this.bI;
        }, spots);
        for (K22.C84 sp : spots) {
            this.a(sp.a(), sp.b(), sp.c(), sp.d(), sp.e());
        }
        this.a(560, s1 + 92, 270, 32, () -> {
            this.aM = this.bH;
            this.bH.c = false;
        });
        this.a(g, 870, s1 + 93, 110, 30, this.bJ ? "Checking..." : "Save", this.al, K18.a(this.ak, -16777216, 0.3f), this.am, this::l);
        return s1 + 173;
    }

    private void l() {
        if (this.bJ) {
            return;
        }
        this.aM = null;
        this.bJ = true;
        this.bK = "";
        K37.a(this.bH.b).thenAccept(r -> {
            this.bJ = false;
            this.bL = !r.a();
            this.bK = r.a() ? "Key saved \u00b7 active on this PC" : r.c();
        });
    }

    private void a(float designX) {
        K9.h = (float)Math.round(Math.max(0.0f, Math.min(1.0f, (designX - 836.0f) / 144.0f)) * 100.0f) / 100.0f;
    }

    private int d(class_332 g, int t) {
        List<Auctions.C65> recent = Auctions.f();
        int head = K18.a(this.ak, -1, 0.05f);
        K18.a(g, 60394, 253.0f, (float)(t + 80), 17.0f, head);
        K18.a(g, "Recent Auctions", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        int bx = Math.round(272.0f + K18.a("Recent Auctions", 13.0f, K18.c) + 10.0f);
        String count = Integer.toString(recent.size());
        int bw = Math.max(26, Math.round(K18.a(count, 11.5f, K18.c) + 16.0f));
        K18.a(g, bx, t + 66, bw, 18, 9, this.al);
        K18.b(g, count, (float)bx + (float)bw / 2.0f, (float)(t + 75), 11.5f, -789515, K18.c);
        K18.a(g, "Your auction history. Press Redo to run one again.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        this.a(g, 892, t + 69, 108, 26, "Clear History", -15461606, -13356484, recent.isEmpty() ? -9541000 : -789515, recent.isEmpty() ? null : Auctions::g);
        if (recent.isEmpty()) {
            K18.a(g, 241, t + 105, 765, 135, 8, this.a(-15789287), -14673364);
            K18.a(g, 61328, 623.0f, (float)(t + 132), 22.0f, -12960186);
            K18.b(g, "No auctions yet", 623.0f, (float)(t + 162), 12.5f, -789515, K18.c);
            K18.b(g, "Pick an item on the Auction tab and press Start Auction.", 623.0f, (float)(t + 179), 12.5f, -3947061, K18.a);
            this.a(g, 568, t + 199, 111, 27, "New Auction", -15461093, -13881803, -789515, () -> {
                this.Y = System.currentTimeMillis();
                this.O = C81.AUCTION;
                N = C81.AUCTION;
            });
            return t + 250;
        }
        int y = t + 108;
        for (Auctions.C65 a : recent) {
            String qtyText;
            boolean live = !a.ended();
            K18.a(g, 241, y, 765, 54, 10, this.a(-15724264), -15002078);
            if (live) {
                K18.a(g, 242, y + 11, 3, 32, 1, this.ak);
            }
            K18.a(g, 253, y + 9, 36, 36, 8, this.a(-15987951));
            Matrix3x2fStack pose = g.method_51448();
            pose.pushMatrix();
            pose.translate(259.0f, (float)(y + 15));
            pose.scale(1.5f);
            g.method_51427(a.item(), 0, 0);
            pose.popMatrix();
            if (a.hasSecondItem()) {
                pose.pushMatrix();
                pose.translate(273.0f, (float)(y + 29));
                pose.scale(1.0f);
                g.method_51427(a.item2(), 0, 0);
                pose.popMatrix();
            }
            String name = K20.a(a.title(), 200.0f, 13.0f, K18.c);
            K18.a(g, name, 301.0f, (float)(y + 20), 13.0f, -789515, K18.c);
            String string = qtyText = a.hasSecondItem() ? "x" + a.quantity() + " + x" + a.quantity2() : "x" + a.quantity();
            String sub = qtyText + "  \u00b7  " + (a.hasBid() ? (live ? "top bid by " : "won by ") + a.topBidder() : a.timerSeconds() + "s timer");
            K18.a(g, K20.a(sub, 205.0f, 11.5f, K18.a), 301.0f, (float)(y + 37), 11.5f, -5001030, K18.a);
            this.a(g, 515, y, "MIN BID", a.minimumBid() > 0L ? Auctions.a(a.minimumBid()) : "None");
            this.a(g, 612, y, "TOP BID", a.hasBid() ? Auctions.a(a.topBid()) : "-");
            if (a.hasSecondItem()) {
                this.a(g, 709, y, "WORTH", a.totalWorth() > 0L ? Auctions.a(a.totalWorth()) : "-");
            } else {
                this.a(g, 709, y, "WORTH EACH", a.worthEach() > 0L ? Auctions.a(a.worthEach()) : "-");
            }
            if (live) {
                K18.a(g, 806, y + 15, 74, 24, 6, this.a(-15395813), -14474966);
                K18.b(g, "Redo", 843.0f, (float)y + 27.5f, 12.0f, -10593178, K18.c);
            } else {
                boolean busy;
                boolean bl = busy = Auctions.d() != null;
                if (busy) {
                    K18.a(g, 806, y + 15, 74, 24, 6, this.a(-15395813), -14474966);
                    K18.b(g, "Redo", 843.0f, (float)y + 27.5f, 12.0f, -10593178, K18.c);
                } else {
                    this.a(g, 806, y + 15, 74, 24, "Redo", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> Auctions.a(a));
                }
            }
            if (live) {
                K18.a(g, 888, y + 15, 76, 24, 12, this.al);
                K18.b(g, Auctions.c(a.secondsLeft()), 926.0f, (float)y + 27.5f, 12.0f, this.am, K18.c);
            } else if (a.cancelled()) {
                K18.a(g, 888, y + 15, 76, 24, 12, -14020844);
                K18.b(g, "Cancelled", 926.0f, (float)y + 27.5f, 11.5f, -30059, K18.c);
            } else if (a.hasBid()) {
                K18.a(g, 888, y + 15, 76, 24, 12, -15583964);
                K18.b(g, "Sold", 926.0f, (float)y + 27.5f, 12.0f, -12197750, K18.c);
            } else {
                K18.a(g, 888, y + 15, 76, 24, 12, -14409172);
                K18.b(g, "Ended", 926.0f, (float)y + 27.5f, 12.0f, -6513498, K18.c);
            }
            K18.a(g, 972, y + 15, 24, 24, 6, -15461606, -12303794);
            K18.a(g, 60245, 984.0f, (float)(y + 27), 11.0f, -789515);
            this.a(972, y + 15, 24, 24, () -> Auctions.b(a));
            y += 62;
        }
        return y;
    }

    private void a(class_332 g, int x, int y, String label, String value) {
        K18.a(g, label, (float)x, (float)(y + 19), 9.5f, -7568235, K18.b);
        K18.a(g, value, (float)x, (float)(y + 36), 13.0f, -789515, K18.c);
    }

    private int e(class_332 g, int t) {
        int x;
        boolean full;
        K4.C63 pending = K4.b();
        if (pending != null) {
            return this.a(g, t, pending);
        }
        int head = K18.a(this.ak, -1, 0.05f);
        K18.a(g, 61082, 253.0f, (float)(t + 80), 18.0f, head);
        K18.a(g, "Cape", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Only you can see this cape. It replaces the cape you are wearing.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        List<K4.C62> capes = K4.a();
        boolean bl = full = capes.size() >= 5;
        if (full) {
            K18.a(g, 778, t + 69, 120, 26, 6, this.a(-15395813), -14474966);
            K18.b(g, "Slots full", 838.0f, (float)t + 82.5f, 12.5f, -10593178, K18.c);
        } else {
            this.a(g, 778, t + 69, 120, 26, "Upload Cape", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, K4::i);
        }
        this.a(g, 904, t + 69, 100, 26, "Open Folder", -15461606, -13356484, -789515, () -> {
            K4.h();
            class_156.method_668().method_60932(K4.a);
        });
        String status = K4.l();
        if (!status.isEmpty()) {
            K18.c(g, status, 1004.0f, (float)(t + 108), 11.0f, this.am, K18.b);
        }
        K4.C62 current = K4.f();
        int y = t + 120;
        for (int i = 0; i <= 5; ++i) {
            x = 235 + i * 130;
            if (i == 0) {
                this.a(g, x, y, (K4.C62)null, current == null && !K1.a(K9.i));
                continue;
            }
            if (i - 1 < capes.size()) {
                K4.C62 cape = capes.get(i - 1);
                this.a(g, x, y, cape, current != null && current.a().equals(cape.a()));
                continue;
            }
            this.a(g, x, y);
        }
        int dy = y + 194;
        for (x = 241; x < 1005; x += 10) {
            K18.a(g, x, dy, 5, 1, 0x40FFFFFF);
        }
        int ay = this.f(g, dy + 22);
        for (int x2 = 241; x2 < 1005; x2 += 10) {
            K18.a(g, x2, ay, 5, 1, 0x40FFFFFF);
        }
        return this.g(g, ay + 22);
    }

    private int f(class_332 g, int y) {
        K18.b(g, "Animated Capes", 623.0f, (float)(y + 4), 15.0f, -789515, K18.c);
        K18.b(g, "Made for Lethal Client. Other players with the client see them moving too.", 623.0f, (float)(y + 22), 10.5f, -5001030, K18.a);
        int top = y + 40;
        int w = 122;
        int h = 150;
        for (int i = 0; i < K1.b.size(); ++i) {
            K1.C61 d = K1.b.get(i);
            int x = 233 + i * 131;
            boolean selected = d.a().equals(K9.i);
            if (selected) {
                K18.a(g, x - 3, top - 3, w + 6, h + 6, 10, K18.a(this.ak, 0.18f));
                K18.a(g, x - 1, top - 1, w + 2, h + 2, 9, 0, this.ak);
                K18.a(g, x, top, w, h, 8, this.a(-15001054), K18.a(this.ak, 0.6f));
            } else {
                K18.a(g, x, top, w, h, 8, this.a(-15001054));
            }
            K18.a(g, x + 6, top + 6, w - 12, 110, 6, this.a(-15987951));
            class_2960 tex = K1.b(d.b());
            if (tex != null) {
                K20.a(g, tex, 128, 64, x + w / 2, top + 61, 36, 58);
            }
            if (selected) {
                K18.a(g, x + w - 15, top + 15, 6.0f, this.ak);
                K18.a(g, x + w - 15, top + 15, 4.8f, -16052716);
                K18.a(g, x + w - 15, top + 15, 3.6f, this.ak);
            }
            int nameColor = selected ? K18.a(this.ak, -1, 0.15f) : -2039834;
            K18.b(g, d.c(), (float)x + (float)w / 2.0f, (float)(top + 134), 12.0f, nameColor, K18.c);
            this.a(x, top, w, h, () -> K4.a(d.a()));
        }
        return top + h + 16;
    }

    private void a(class_332 g, int x, int y, K4.C62 cape, boolean selected) {
        int nameColor;
        int w = 122;
        int h = 178;
        if (selected) {
            K18.a(g, x - 3, y - 3, w + 6, h + 6, 10, K18.a(this.ak, 0.18f));
            K18.a(g, x - 1, y - 1, w + 2, h + 2, 9, 0, this.ak);
            K18.a(g, x, y, w, h, 8, this.a(-15001054), K18.a(this.ak, 0.6f));
        } else {
            K18.a(g, x, y, w, h, 8, this.a(-15001054));
        }
        K18.a(g, x + 6, y + 6, w - 12, 110, 6, this.a(-15987951));
        int pcx = x + w / 2;
        int pcy = y + 61;
        if (cape == null) {
            K18.a(g, 60245, (float)pcx, (float)pcy, 30.0f, -11909548);
        } else {
            K20.a(g, cape.b(), cape.c(), cape.d(), pcx, pcy, 36, 58);
        }
        if (selected) {
            K18.a(g, x + w - 15, y + 15, 6.0f, this.ak);
            K18.a(g, x + w - 15, y + 15, 4.8f, -16052716);
            K18.a(g, x + w - 15, y + 15, 3.6f, this.ak);
        }
        int n = nameColor = selected ? K18.a(this.ak, -1, 0.15f) : -2039834;
        if (cape == null) {
            K18.b(g, "Normal cape", (float)pcx, (float)(y + 134), 12.0f, nameColor, K18.c);
            K18.b(g, "Your real cape", (float)pcx, (float)(y + 154), 10.5f, -5001030, K18.a);
            this.a(x, y, w, h, () -> K4.a(""));
        } else if (this.bj == cape) {
            this.a(g, this.bk, x + 6, y + 122, w - 12, 24, "Name");
            this.a(g, x + 6, y + 150, w - 12, 22, "Save", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, this::m);
        } else {
            String label = K20.a(cape.a().replaceAll("(?i)\\.png$", ""), w - 16, 12.0f, K18.c);
            K18.b(g, label, (float)pcx, (float)(y + 134), 12.0f, nameColor, K18.c);
            int bw = (w - 18) / 2;
            K18.a(g, x + 6, y + 148, bw, 24, 6, -15461606, -13356484);
            K18.a(g, 60164, (float)(x + 6) + (float)bw / 2.0f, (float)(y + 160), 13.0f, this.am);
            this.a(x + 6, y + 148, bw, 24, () -> {
                this.bj = cape;
                this.bk.b = cape.a().replaceAll("(?i)\\.png$", "");
                this.bk.c = true;
                this.aM = this.bk;
            });
            K18.a(g, x + 12 + bw, y + 148, bw, 24, 6, -14020844, -10871256);
            K18.a(g, 60225, (float)(x + 12 + bw) + (float)bw / 2.0f, (float)(y + 160), 13.0f, -30059);
            this.a(x + 12 + bw, y + 148, bw, 24, () -> K4.a(cape));
            this.a(x, y, w, 140, () -> K4.a(cape.a()));
        }
    }

    private void a(class_332 g, int x, int y) {
        int i;
        int w = 122;
        int h = 178;
        int dash = K18.a(-1, 0.18f);
        for (i = 6; i < w - 6; i += 8) {
            K18.a(g, x + i, y, 4, 1, dash);
            K18.a(g, x + i, y + h - 1, 4, 1, dash);
        }
        for (i = 6; i < h - 6; i += 8) {
            K18.a(g, x, y + i, 1, 4, dash);
            K18.a(g, x + w - 1, y + i, 1, 4, dash);
        }
        K18.a(g, 60231, (float)x + (float)w / 2.0f, (float)(y + 72), 22.0f, -9541000);
        K18.b(g, "Empty slot", (float)x + (float)w / 2.0f, (float)(y + 104), 11.5f, -7568235, K18.b);
        K18.b(g, "Click to upload", (float)x + (float)w / 2.0f, (float)(y + 120), 10.0f, -9541000, K18.a);
        this.a(x, y, w, h, K4::i);
    }

    private void m() {
        if (this.bj == null || K4.a(this.bj, this.bk.b)) {
            this.bj = null;
            this.aM = null;
        }
    }

    private int g(class_332 g, int y) {
        int i;
        K18.b(g, "Auction Cape", 623.0f, (float)(y + 4), 15.0f, -789515, K18.c);
        K18.b(g, "Wear a live timer on your back during auctions. Your chosen cape comes back afterwards.", 623.0f, (float)(y + 22), 10.5f, -5001030, K18.a);
        int cx = 333;
        int cy = y + 38;
        int cw = 580;
        int ch = 150;
        float tm = (float)(System.currentTimeMillis() % 100000L) / 1000.0f;
        K18.a(g, cx - 3, cy - 3, cw + 6, ch + 6, 16, K18.a(this.ak, 0.12f + 0.06f * (float)Math.sin(tm * 2.0f)));
        K18.a(g, cx, cy, cw, ch, 14, this.a(K18.a(-16053744, this.ak, 0.1f)), K18.a(this.ak, 0.55f));
        g.method_44379(cx + 1, Math.max(cy + 1, 54), cx + cw - 1, Math.min(cy + ch - 1, 508));
        for (i = 0; i < 6; ++i) {
            float bx = (float)cx + (float)cw * (0.15f + 0.7f * (0.5f + 0.5f * (float)Math.sin(tm * (0.35f + (float)i * 0.09f) + (float)i * 1.7f)));
            float by = (float)cy + (float)ch * (0.5f + 0.42f * (float)Math.sin(tm * (0.5f + (float)i * 0.07f) + (float)i * 2.3f));
            float r = 38 + i % 3 * 12;
            int col = i % 2 == 0 ? this.ak : K18.a(this.ak, -1, 0.35f);
            K18.a(g, bx, by, r, K18.a(col, 0.1f));
            K18.a(g, bx, by, r * 0.55f, K18.a(col, 0.1f));
        }
        for (i = -ch; i < cw; i += 26) {
            int sx = cx + i + (int)(tm * 18.0f % 26.0f);
            for (int k = 0; k < ch; k += 2) {
                K18.a(g, sx + k, cy + k, 2, 2, 0xDFFFFFF);
            }
        }
        g.method_44380();
        Auctions.C65 live = Auctions.c();
        K2.a(live != null && !live.cancelled() ? live : null);
        K18.a(g, cx + 22, cy + 16, 84, 118, 10, -1442446070);
        K20.a(g, K2.a, 512, 256, cx + 64, cy + 75, 64, 102, true);
        K18.a(g, "Live timer cape", (float)(cx + 128), (float)(cy + 30), 14.0f, -789515, K18.c);
        K18.a(g, "Switches on by itself when an auction starts and", (float)(cx + 128), (float)(cy + 52), 11.0f, -2698530, K18.a);
        K18.a(g, "shows the time left, the top bid and a progress bar.", (float)(cx + 128), (float)(cy + 68), 11.0f, -2698530, K18.a);
        K18.a(g, "When nothing is being auctioned you keep your cape.", (float)(cx + 128), (float)(cy + 84), 11.0f, -2698530, K18.a);
        K18.a(g, K9.k ? "Enabled" : "Disabled", (float)(cx + 128), (float)(cy + 118), 12.5f, K9.k ? this.am : -6513498, K18.c);
        this.a(g, cx + cw - 58, cy + 118, K9.k, () -> {
            K9.k = !K9.k;
            K9.b();
        });
        return cy + ch + 16;
    }

    private static void a(class_332 g, class_2960 tex, int texW, int texH, int cx, int cy, int w, int h) {
        K20.a(g, tex, texW, texH, cx, cy, w, h, false);
    }

    private static void a(class_332 g, class_2960 tex, int texW, int texH, int cx, int cy, int w, int h, boolean single) {
        float sx = (float)texW / 64.0f;
        float sy = (float)texH / 32.0f;
        int rw = Math.round(10.0f * sx);
        int rh = Math.round(16.0f * sy);
        if (single) {
            g.method_25302(class_10799.field_56883, tex, cx - w / 2, cy - h / 2, sx, sy, w, h, rw, rh, texW, texH);
        } else {
            g.method_25302(class_10799.field_56883, tex, cx - w - 3, cy - h / 2, sx, sy, w, h, rw, rh, texW, texH);
            g.method_25302(class_10799.field_56883, tex, cx + 3, cy - h / 2, 12.0f * sx, sy, w, h, rw, rh, texW, texH);
        }
    }

    private int a(class_332 g, int t, K4.C63 p) {
        if (this.bv != p) {
            this.bv = p;
            this.bl.b = p.b;
            this.bl.c = false;
        }
        int head = K18.a(this.ak, -1, 0.05f);
        K18.a(g, 61082, 253.0f, (float)(t + 80), 18.0f, head);
        K18.a(g, "Pick your cape area", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Drag the frame to choose which part of the picture goes on your cape.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int bx = 241;
        int by = t + 112;
        int bw = 500;
        int bh = 360;
        this.bC = by;
        K18.a(g, bx, by, bw, bh, 10, this.a(-15987951), -14014925);
        int iw = p.a.method_4307();
        int ih = p.a.method_4323();
        this.bB = Math.min((float)(bw - 24) / (float)iw, (float)(bh - 24) / (float)ih);
        int dw = Math.round((float)iw * this.bB);
        int dh = Math.round((float)ih * this.bB);
        int ix = bx + (bw - dw) / 2;
        int iy = by + (bh - dh) / 2;
        g.method_25302(class_10799.field_56883, p.a(), ix, iy, 0.0f, 0.0f, dw, dh, iw, ih, iw, ih);
        int fx = ix + Math.round((float)p.e * this.bB);
        int fy = iy + Math.round((float)p.f * this.bB);
        int fw = Math.round((float)p.c * this.bB);
        int fh = Math.round((float)p.d * this.bB);
        int shade = -1342177280;
        K18.a(g, ix, iy, dw, fy - iy, shade);
        K18.a(g, ix, fy + fh, dw, iy + dh - fy - fh, shade);
        K18.a(g, ix, fy, fx - ix, fh, shade);
        K18.a(g, fx + fw, fy, ix + dw - fx - fw, fh, shade);
        K18.a(g, fx - 1, fy - 1, fw + 2, fh + 2, 3, 0, this.ak);
        K18.a(g, fx, fy, fw, fh, 2, 0, K18.a(-1, 0.5f));
        if (p.c < iw && fw > 40) {
            K18.a(g, 60000, (float)(fx + 10), (float)fy + (float)fh / 2.0f, 14.0f, -1);
            K18.a(g, 60001, (float)(fx + fw - 10), (float)fy + (float)fh / 2.0f, 14.0f, -1);
        }
        this.a(ix, iy, dw, dh, () -> {
            this.bw = true;
            this.bx = this.aU;
            this.by = this.aV;
            this.bz = p.e;
            this.bA = p.f;
        });
        int px = 757;
        K18.a(g, "Preview", (float)px, (float)(t + 122), 12.5f, -1842200, K18.b);
        K18.a(g, px, t + 134, 248, 186, 10, this.a(-15987951));
        int pw = 96;
        int ph = Math.round((float)pw / 0.625f);
        g.method_25302(class_10799.field_56883, p.a(), px + 124 - pw / 2, t + 227 - ph / 2, (float)p.e, (float)p.f, pw, ph, p.c, p.d, iw, ih);
        K18.a(g, "Name", (float)px, (float)(t + 332), 12.5f, -1842200, K18.b);
        this.a(g, this.bl, px, t + 342, 248, 28, "Cape name");
        K18.a(g, "Zoom", (float)px, (float)(t + 386), 12.5f, -1842200, K18.b);
        K18.c(g, String.format(Locale.ROOT, "%.1fx", Float.valueOf(p.g)), (float)(px + 248), (float)(t + 386), 12.0f, -3947572, K18.b);
        this.a(g, px + 52, t + 386, 150, (p.g - 1.0f) / 4.0f, (Float f) -> p.a(1.0f + f.floatValue() * 4.0f));
        this.a(g, px, t + 404, 248, 28, "Save Cape", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
            K4.b().b = this.bl.b.isBlank() ? K4.b().b : this.bl.b;
            K4.j();
            this.aM = null;
        });
        this.a(g, px, t + 438, 248, 28, "Cancel", this.a(-15395813), -13356484, -789515, () -> {
            K4.k();
            this.aM = null;
        });
        return t + 480;
    }

    private void a(int x, int cy, int w, float frac, Consumer<Float> set) {
        this.a(this.n(), x, cy, w, frac, set);
    }

    private void a(class_332 g, int x, int cy, int w, float frac, Consumer<Float> set) {
        this.a(g, x, cy, w, frac);
        this.a(x - 6, cy - 9, w + 12, 18, () -> {
            this.bD = new C82(x, w, set);
            set.accept(Float.valueOf(Math.max(0.0f, Math.min(1.0f, (this.aU - (float)x) / (float)w))));
        });
    }

    private class_332 n() {
        return this.bE;
    }

    private int h(class_332 g, int t) {
        this.bE = g;
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.a(g, 61201, 253.0f, (float)(t + 80), 17.0f, head);
        K18.a(g, "Scoreboard", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Change the size, corners and background of the scoreboard on the right of your screen.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        this.a(g, 912, t + 69, 88, 26, "Reset", -15461606, -13356484, -789515, () -> {
            K9.aS = 1.0f;
            K9.aT = false;
            K9.aX = 0;
            K9.aY = 0.8f;
            K9.aZ = 0.45f;
            K9.b();
        });
        int x0 = 235;
        int w0 = 500;
        int y = t + 116;
        K18.a(g, x0, y, w0, 64, 12, cardFill, cardBorder);
        K18.a(g, "DonutSMP scoreboard", (float)(x0 + 18), (float)(y + 22), 13.0f, -789515, K18.c);
        K18.a(g, "Clean DonutSMP-style board: gradient title, no name, real region + ping", (float)(x0 + 18), (float)(y + 42), 10.5f, -5001030, K18.a);
        this.a(g, x0 + w0 - 52, y + 32, K9.aV, () -> {
            K9.aV = !K9.aV;
            K9.b();
        });
        K18.a(g, x0, y += 76, w0, 64, 12, cardFill, cardBorder);
        K18.a(g, "Size", (float)(x0 + 18), (float)(y + 22), 13.0f, -789515, K18.c);
        K18.a(g, "Make it bigger or smaller", (float)(x0 + 18), (float)(y + 42), 10.5f, -5001030, K18.a);
        K18.c(g, Math.round(K9.aS * 100.0f) + "%", (float)(x0 + 260), (float)(y + 32), 12.0f, -3947572, K18.b);
        this.a(x0 + 276, y + 32, 200, K9.aS - 0.5f, (Float f) -> {
            K9.aS = (float)Math.round((0.5f + f.floatValue()) * 20.0f) / 20.0f;
        });
        K18.a(g, x0, y += 76, w0, 64, 12, cardFill, cardBorder);
        K18.a(g, "Rounded corners", (float)(x0 + 18), (float)(y + 22), 13.0f, -789515, K18.c);
        K18.a(g, "Smooth corners instead of a square box", (float)(x0 + 18), (float)(y + 42), 10.5f, -5001030, K18.a);
        this.a(g, x0 + w0 - 52, y + 32, K9.aT, () -> {
            K9.aT = !K9.aT;
            K9.b();
        });
        K18.a(g, x0, y += 76, w0, 64, 12, cardFill, cardBorder);
        K18.a(g, "Player head", (float)(x0 + 18), (float)(y + 22), 13.0f, -789515, K18.c);
        K18.a(g, "Show your head next to the name at the top", (float)(x0 + 18), (float)(y + 42), 10.5f, -5001030, K18.a);
        this.a(g, x0 + w0 - 52, y + 32, K9.aU, () -> {
            K9.aU = !K9.aU;
            K9.b();
        });
        boolean custom = K9.aX == 2;
        int bh = custom ? 168 : 96;
        K18.a(g, x0, y += 76, w0, bh, 12, cardFill, cardBorder);
        K18.a(g, "Background", (float)(x0 + 18), (float)(y + 22), 13.0f, -789515, K18.c);
        int segX = x0 + 18;
        int segY = y + 40;
        int segW = w0 - 36;
        int opt = (segW - 6) / 4;
        K18.a(g, segX, segY, segW, 32, 8, -15987951);
        int i = 0;
        while (i < 4) {
            boolean on;
            int ox = segX + 3 + i * opt;
            boolean bl = on = K9.aX == i;
            if (on) {
                K18.a(g, ox, segY + 3, opt, 26, 6, this.al);
            }
            K18.b(g, K29.a[i], (float)ox + (float)opt / 2.0f, (float)segY + 16.5f, 12.0f, on ? this.am : -3552560, K18.c);
            int which = i++;
            this.a(ox, segY + 3, opt, 26, () -> {
                K9.aX = which;
                K9.b();
            });
        }
        if (custom) {
            K18.a(g, "Colour", (float)(x0 + 18), (float)(y + 96), 12.0f, -1842200, K18.b);
            int hx = x0 + 120;
            int hw = w0 - 138;
            for (int i2 = 0; i2 < hw; ++i2) {
                K18.a(g, hx + i2, y + 91, 1, 10, 0xFF000000 | Color.HSBtoRGB((float)i2 / (float)hw, 0.75f, 0.85f));
            }
            int kx = hx + Math.round(K9.aY * (float)hw);
            K18.a(g, kx, y + 96, 7.0f, -1);
            K18.a(g, kx, y + 96, 5.0f, 0xFF000000 | Color.HSBtoRGB(K9.aY, 0.75f, 0.85f));
            this.a(hx - 6, y + 87, hw + 12, 18, () -> {
                this.bD = new C82(hx, hw, f -> {
                    K9.aY = f.floatValue();
                });
                K9.aY = Math.max(0.0f, Math.min(1.0f, (this.aU - (float)hx) / (float)hw));
            });
            K18.a(g, "Opacity", (float)(x0 + 18), (float)(y + 132), 12.0f, -1842200, K18.b);
            K18.c(g, Math.round(K9.aZ * 100.0f) + "%", (float)(x0 + w0 - 18), (float)(y + 132), 12.0f, -3947572, K18.b);
            this.a(x0 + 120, y + 132, w0 - 190, K9.aZ, (Float f) -> {
                K9.aZ = (float)Math.round(f.floatValue() * 100.0f) / 100.0f;
            });
        }
        int px = 751;
        int pw = 254;
        int ph = 300;
        int py = t + 116;
        K18.a(g, px, py, pw, ph, 12, this.a(-15987951), cardBorder);
        K18.a(g, "Preview", (float)(px + 14), (float)(py + 18), 12.0f, -1842200, K18.b);
        this.d(g, px + pw / 2, py + ph / 2 + 12);
        return Math.max(y + bh, py + ph) + 12;
    }

    private int i(class_332 g, int t) {
        boolean on;
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        int green = -12200829;
        int red = -30107;
        if (this.aD.b.isEmpty() && !K9.s.isEmpty() && this.aM != this.aD) {
            this.aD.b = K9.s;
        }
        if (!this.aD.b.equals(K9.s)) {
            K9.s = this.aD.b;
            K9.b();
        }
        List<Transactions.C68> all = Transactions.a();
        long min = this.aD.b.isBlank() ? 0L : Math.max(0L, Auctions.e(this.aD.b));
        String q = this.aE.b.trim().toLowerCase(Locale.ROOT);
        ArrayList<Transactions.C68> shown = new ArrayList<Transactions.C68>();
        long received = 0L;
        long sent = 0L;
        for (Transactions.C68 tx : all) {
            if (tx.amount() < min || aF == 1 && !tx.received() || aF == 2 && tx.received() || !q.isEmpty() && !tx.player().toLowerCase(Locale.ROOT).contains(q)) continue;
            shown.add(tx);
            if (tx.received()) {
                received += tx.amount();
                continue;
            }
            sent += tx.amount();
        }
        K18.b(g, K22.a(61940), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Transactions", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        int bx = Math.round(272.0f + K18.a("Transactions", 13.0f, K18.c) + 10.0f);
        String count = Integer.toString(shown.size());
        int bw = Math.max(26, Math.round(K18.a(count, 11.5f, K18.c) + 16.0f));
        K18.a(g, bx, t + 66, bw, 18, 9, this.al);
        K18.b(g, count, (float)bx + (float)bw / 2.0f, (float)(t + 75), 11.5f, -789515, K18.c);
        K18.a(g, "Money you got and sent in chat. Set a minimum to only see the big ones.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        this.a(g, 892, t + 69, 108, 26, "Clear History", -15461606, -13356484, all.isEmpty() ? -9541000 : -789515, all.isEmpty() ? null : Transactions::e);
        int y = t + 110;
        int cw = 247;
        String[] labels = new String[]{"RECEIVED", "SENT"};
        String[] values = new String[]{"+" + Auctions.a(received), "-" + Auctions.a(sent)};
        int[] colors = new int[]{-12200829, -30107};
        for (int i = 0; i < 2; ++i) {
            int x = 241 + i * (cw + 12);
            K18.a(g, x, y, cw, 54, 10, cardFill, cardBorder);
            K18.a(g, labels[i], (float)(x + 16), (float)(y + 19), 9.5f, -7568235, K18.b);
            K18.a(g, values[i], (float)(x + 16), (float)(y + 38), 15.0f, colors[i], K18.c);
        }
        long[] session = Transactions.d();
        long profit = session[0] - session[1];
        int pc = profit >= 0L ? green : -42401;
        int sx = 241 + 2 * (cw + 12);
        K18.a(g, sx, y, cw, 54, 10, cardFill, K18.a(pc, 0.45f));
        K18.a(g, "SESSION  \u00b7  " + K20.a(System.currentTimeMillis() - Transactions.b()).replace("just now", "started now"), (float)(sx + 16), (float)(y + 19), 9.5f, -7568235, K18.b);
        K18.a(g, (profit >= 0L ? "+" : "-") + Auctions.a(Math.abs(profit)), (float)(sx + 16), (float)(y + 38), 15.0f, pc, K18.c);
        this.a(g, sx + cw - 70, y + 15, 58, 24, "Reset", this.a(-15395813), -13356484, -789515, Transactions::c);
        K18.a(g, 241, y += 66, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Profit display", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Shows this session's profit on screen with your skin. Red when you're down.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        K18.a(g, "Skin", 712.0f, (float)(y + 25), 11.0f, -3947581, K18.b);
        this.a(g, 760, y + 25, K9.u, () -> {
            K9.u = !K9.u;
            K9.b();
        });
        this.a(g, 806, y + 13, 110, 24, "Edit position", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
            K9.t = true;
            K9.b();
            this.field_22787.method_1507((class_437)new K19(this, K27.d));
        });
        this.a(g, 960, y + 25, K9.t, () -> {
            K9.t = !K9.t;
            K9.b();
        });
        K18.a(g, "Minimum", 241.0f, (float)((y += 66) + 15), 11.5f, -1842200, K18.b);
        this.a(g, this.aD, 300, y, 118, 30, "Any, e.g. 5m");
        String[][] quick = new String[][]{{"Any", ""}, {"100k+", "100k"}, {"1m+", "1m"}, {"5m+", "5m"}, {"10m+", "10m"}};
        int qx = 426;
        for (String[] option : quick) {
            int w = Math.round(K18.a(option[0], 11.5f, K18.b) + 20.0f);
            on = this.aD.b.equalsIgnoreCase(option[1]);
            K18.a(g, qx, y + 3, w, 24, 12, on ? this.al : this.a(-15395813), on ? this.ak : -13356484);
            K18.b(g, option[0], (float)qx + (float)w / 2.0f, (float)(y + 15), 11.5f, on ? -1 : -3947581, K18.b);
            this.a(qx, y + 3, w, 24, () -> {
                this.aD.b = option[1];
                this.aM = null;
            });
            qx += w + 6;
        }
        String[] types = new String[]{"All", "Received", "Sent"};
        int tx0 = 718;
        int i = 0;
        while (i < 3) {
            int w = 62;
            int x = tx0 + i * w;
            on = aF == i;
            K18.a(g, x, y + 3, w - 2, 24, 7, on ? this.al : this.a(-15395813));
            K18.b(g, types[i], (float)x + (float)(w - 2) / 2.0f, (float)(y + 15), 11.5f, on ? -1 : -3947581, K18.b);
            int pick = i++;
            this.a(x, y + 3, w - 2, 24, () -> {
                aF = pick;
            });
        }
        this.a(g, this.aE, 906, y, 100, 30, "Player");
        y += 42;
        if (shown.isEmpty()) {
            K18.a(g, 241, y, 765, 110, 10, this.a(-15789287), -14673364);
            String msg = all.isEmpty() ? "No transactions yet" : "Nothing matches these filters";
            String sub = all.isEmpty() ? "When someone pays you, or you pay someone, it shows up here." : "Lower the minimum or pick All.";
            K18.b(g, msg, 623.5f, (float)(y + 45), 12.5f, -789515, K18.c);
            K18.b(g, sub, 623.5f, (float)(y + 65), 11.5f, -5001030, K18.a);
            return y + 122;
        }
        long now = System.currentTimeMillis();
        for (Transactions.C68 tx : shown) {
            if (y + 44 >= 40 && y <= 520) {
                int c = tx.received() ? green : red;
                K18.a(g, 241, y, 765, 44, 9, this.a(-15724264), -15002078);
                K18.a(g, 265.0f, (float)y + 22.0f, 12.0f, K18.a(c, 0.16f));
                K18.b(g, K22.a(tx.received() ? 59923 : 59940), 265.0f, (float)y + 22.5f, 14.0f, c, K22.a);
                K18.a(g, tx.player(), 287.0f, (float)(y + 16), 12.5f, -789515, K18.c);
                K18.a(g, (tx.received() ? "Paid you" : "You paid") + "  \u00b7  " + K20.a(now - tx.at()), 287.0f, (float)(y + 31), 10.5f, -5001030, K18.a);
                String amount = (tx.received() ? "+" : "-") + Auctions.a(tx.amount());
                K18.c(g, amount, 992.0f, (float)(y + 17), 14.0f, c, K18.c);
                K18.c(g, String.format(Locale.ROOT, "%,d", tx.amount()), 992.0f, (float)(y + 32), 10.0f, -7568235, K18.a);
            }
            y += 50;
        }
        return y;
    }

    private static String a(long ms) {
        long s = ms / 1000L;
        if (s < 60L) {
            return "just now";
        }
        if (s < 3600L) {
            return s / 60L + " min ago";
        }
        if (s < 86400L) {
            return s / 3600L + " h ago";
        }
        return s / 86400L + (s < 172800L ? " day ago" : " days ago");
    }

    private int j(class_332 g, int t) {
        int v;
        int sx;
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.a(g, 60156, 253.0f, (float)(t + 80), 17.0f, head);
        K18.a(g, "Music", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Shows the song playing in Spotify, with a card you can move. Pause and skip too.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        boolean supported = K48.a();
        int y = t + 116;
        K18.a(g, 241, y, 765, 58, 10, cardFill, cardBorder);
        K18.a(g, "Now Playing", 257.0f, (float)(y + 22), 12.5f, -789515, K18.c);
        K18.a(g, supported ? "Reads Spotify right off your PC \u2014 nothing to install." : "Only works on Windows; this computer can't read the media.", 257.0f, (float)(y + 40), 10.5f, -5001030, K18.a);
        this.a(g, 960, y + 29, K9.U, () -> {
            K9.U = !K9.U;
            K9.b();
        });
        int cardH = 184;
        K18.a(g, 241, y += 70, 765, cardH, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", 257.0f, (float)(y + 18), 10.0f, -7568235, K18.b);
        if (K47.c().a()) {
            float s = 1.0f;
            float ox = 623.5f - 272.0f * s / 2.0f;
            float oy = (float)(y + 30) + ((float)(cardH - 30) - (float)K25.c() * s) / 2.0f;
            K25.a(g, ox, oy, s);
        } else {
            K18.a(g, 60156, 623.5f, (float)(y + 74), 24.0f, 0x40FFFFFF);
            K18.b(g, K9.U ? "Nothing playing" : "Turn it on above", 623.5f, (float)(y + 102), 12.0f, -5001030, K18.c);
            K18.b(g, supported ? "Open Spotify and press play" : "Available on Windows", 623.5f, (float)(y + 118), 10.0f, -7568235, K18.b);
        }
        K18.a(g, 241, y += cardH + 12, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Show on screen", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Drag it anywhere and make it bigger or smaller.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 806, y + 13, 110, 24, "Edit position", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
            K9.V = true;
            K9.b();
            this.field_22787.method_1507((class_437)new K19(this, K25.e));
        });
        this.a(g, 960, y + 25, K9.V, () -> {
            K9.V = !K9.V;
            K9.b();
        });
        y += 62;
        K18.a(g, "Headphones", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Wear headphones while music plays", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "They appear on your head whenever a song is playing. Other Lethal players see them too.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.Z, () -> {
            K9.Z = !K9.Z;
            K9.b();
        });
        y += 62;
        if (K9.Z) {
            K6.C71 hp = K6.a("headphones");
            int rowH = 96;
            K18.a(g, 241, y, 765, rowH, 10, cardFill, cardBorder);
            K18.a(g, "Colour", 257.0f, (float)(y + 20), 11.5f, -3947581, K18.b);
            int cv = Math.max(0, Math.min(hp.d.size() - 1, K9.aa));
            K18.a(g, 300.0f, (float)y + (float)rowH / 2.0f + 6.0f, 30.0f, 0x14FFFFFF);
            this.a(g, hp, hp.d.get(cv), 300.0f, (float)y + (float)rowH / 2.0f + 6.0f);
            sx = 430;
            v = 0;
            while (v < hp.d.size()) {
                boolean on;
                K6.C72 pal = hp.d.get(v);
                float dx = sx + v * 44;
                float dy = (float)y + (float)rowH / 2.0f;
                boolean bl = on = v == cv;
                if (on) {
                    K18.a(g, dx, dy, 15.0f, -1);
                }
                K18.a(g, dx, dy, 13.0f, 0xFF000000 | pal.b());
                K18.a(g, dx + 4.5f, dy + 4.5f, 5.0f, 0xFF000000 | pal.c());
                K18.b(g, pal.a(), dx, dy + 26.0f, 8.0f, on ? -789515 : -7568235, K18.b);
                int variant = v++;
                this.a(Math.round(dx - 15.0f), Math.round(dy - 15.0f), 30, 34, () -> {
                    K9.aa = variant;
                    K9.b();
                });
            }
            y += rowH + 12;
        }
        return y + 6;
    }

    private int k(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        this.d();
        K18.b(g, K22.a(60206), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Chat Games", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "A spin-the-wheel raffle. Viewers pay you in game to enter and the wheel picks a winner.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int y = t + 116;
        y = this.b(g, y, cardFill, cardBorder);
        return y + 6;
    }

    private int a(class_332 g, int y, int cardFill, int cardBorder) {
        K18.a(g, 241, y, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Show chat on screen", 257.0f, (float)(y + 20), 12.5f, -789515, K18.c);
        K18.a(g, "Merges your Twitch, YouTube and TikTok chat into one on-screen overlay.", 257.0f, (float)(y + 37), 10.0f, -5001030, K18.a);
        this.b(g, 918, y + 25);
        this.a(g, 962, y + 25, K9.ab, () -> {
            K9.ab = !K9.ab;
            K9.ac = true;
            K9.b();
        });
        int ch = 128;
        K18.a(g, 241, y += 62, 765, ch, 10, cardFill, cardBorder);
        K18.a(g, "Your channels", 257.0f, (float)(y + 22), 12.5f, -789515, K18.c);
        K18.a(g, "Enter your handle for each platform you stream on \u2014 leave one blank to skip it.", 368.0f, (float)(y + 22), 10.0f, -5001030, K18.b);
        int ry = y + 38;
        K20.b(g, 270.0f, (float)(ry + 14), 15.0f, 1.0f);
        K18.a(g, "Twitch", 288.0f, (float)(ry + 14), 11.0f, -789515, K18.b);
        this.a(g, this.bn, 366, ry + 2, 634, 26, "your_twitch_username");
        K20.a(g, 270.0f, (float)((ry += 30) + 14), 15.0f, 1.0f);
        K18.a(g, "YouTube", 288.0f, (float)(ry + 14), 11.0f, -789515, K18.b);
        this.a(g, this.bo, 366, ry + 2, 634, 26, "@handle or channel ID");
        K20.c(g, 270.0f, (ry += 30) + 14, 15.0f, 1.0f);
        K18.a(g, "TikTok", 288.0f, (float)(ry + 14), 11.0f, -789515, K18.b);
        this.a(g, this.bp, 366, ry + 2, 634, 26, "your_tiktok_username");
        K18.a(g, 241, y += ch + 12, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Overlay", 257.0f, (float)(y + 20), 12.5f, -789515, K18.c);
        K18.a(g, "Chat lines shown", 257.0f, (float)(y + 37), 10.0f, -5001030, K18.a);
        int[] lines = new int[]{4, 6, 8};
        for (int i = 0; i < lines.length; ++i) {
            int fl = lines[i];
            this.a(g, 470 + i * 50, y + 12, 44, K9.ak == fl, Integer.toString(fl), () -> {
                K9.ak = fl;
                K9.b();
            });
        }
        this.a(g, 884, y + 12, 118, 26, "Move overlay", this.a(-15395813), -13356484, -789515, () -> {
            K9.ac = true;
            K9.ab = true;
            K9.b();
            this.field_22787.method_1507((class_437)new K19(this, K58.y));
        });
        return y += 62;
    }

    private int b(class_332 g, int y, int cardFill, int cardBorder) {
        K9.au = this.bq.b.trim();
        String amt = this.br.b.trim();
        K9.at = amt.isEmpty() ? "1" : amt;
        K18.a(g, 241, y, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Pay exactly", 257.0f, (float)(y + 28), 12.5f, -789515, K18.c);
        K18.a(g, "Only pays of this exact amount enter. One entry per person.", 420.0f, (float)(y + 28), 10.5f, -5001030, K18.b);
        K18.a(g, "$", 772.0f, (float)(y + 25), 13.0f, -5001030, K18.c);
        this.a(g, this.br, 784, y + 11, 218, 28, "e.g. 1m");
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Entry message", 257.0f, (float)(y + 28), 12.5f, -789515, K18.c);
        K18.a(g, "Shown under the wheel (blank = auto).", 500.0f, (float)(y + 28), 10.0f, -5001030, K18.b);
        this.a(g, this.bq, 702, y + 11, 300, 28, "pay $1m to enter");
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Center image", 257.0f, (float)(y + 20), 12.5f, -789515, K18.c);
        boolean hasImg = K9.aw != null && !K9.aw.isBlank();
        K18.a(g, hasImg ? "Custom PNG in the middle of the wheel." : "Import a PNG for the middle of the wheel.", 257.0f, (float)(y + 37), 10.0f, -5001030, K18.a);
        this.a(g, 806, y + 13, 110, 24, hasImg ? "Change PNG" : "Choose PNG", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> K60.c());
        if (hasImg) {
            this.a(g, 922, y + 13, 80, 24, "Remove", this.a(-15395813), -13356484, -789515, () -> {
                K9.aw = "";
                K9.b();
            });
        }
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Auto-spin timer", 257.0f, (float)(y + 20), 12.5f, -789515, K18.c);
        K18.a(g, "Spin automatically after a countdown (or leave Off to spin yourself).", 257.0f, (float)(y + 36), 10.0f, -5001030, K18.a);
        int[] secs = new int[]{0, 30, 60, 120, 300};
        String[] slab = new String[]{"Off", "30s", "1m", "2m", "5m"};
        for (int i = 0; i < secs.length; ++i) {
            int fs = secs[i];
            this.a(g, 760 + i * 50, y + 13, 46, K9.av == fs, slab[i], () -> {
                K9.av = fs;
                K9.b();
            });
        }
        K18.a(g, 241, y += 62, 765, 62, 10, cardFill, cardBorder);
        boolean open = K54.d();
        boolean spinning = K54.e();
        int count = K54.c();
        K18.a(g, open ? "Collecting entries\u2026" : (spinning ? "Running\u2026" : "Ready"), 257.0f, (float)(y + 24), 12.5f, open ? -10888821 : -789515, K18.c);
        K18.a(g, count + " entered", 257.0f, (float)(y + 42), 10.5f, -5001030, K18.b);
        this.a(g, 560, y + 18, 120, 26, open ? "Stop entries" : "Open entries", open ? -8769997 : this.al, open ? -4694948 : K18.a(this.ak, -16777216, 0.15f), this.am, () -> K54.a(!K54.d()));
        boolean canStart = count >= 2 && !spinning;
        this.a(g, 688, y + 18, 90, 26, "START", canStart ? -13735110 : this.a(-15395813), canStart ? -11938144 : -13356484, canStart ? -1 : -8947849, () -> {
            if (count >= 2) {
                K54.q();
                this.method_25419();
            }
        });
        this.a(g, 786, y + 18, 90, 26, "Clear", this.a(-15395813), -13356484, -789515, () -> K54.m());
        this.a(g, 884, y + 18, 118, 26, "Move overlay", this.a(-15395813), -13356484, -789515, () -> {
            K9.an = true;
            K9.b();
            K54.a(true);
            this.field_22787.method_1507((class_437)new K19(this, K60.e));
        });
        K18.a(g, "Games", 257.0f, (float)((y += 74) + 10), 12.5f, -789515, K18.c);
        K18.a(g, "Pick the event \u2014 hover to preview. Random plays a different one each round.", 310.0f, (float)(y + 10), 10.0f, -5001030, K18.b);
        y += 24;
        int[] opts = new int[K54.o.length + 1];
        System.arraycopy(K54.o, 0, opts, 0, K54.o.length);
        opts[opts.length - 1] = 10;
        int tw = 112;
        int th = 100;
        int tgap = 9;
        boolean anyHover = false;
        for (int i = 0; i < opts.length; ++i) {
            boolean hover;
            int gid = opts[i];
            int tx = 257 + i * (tw + tgap);
            int ty = y;
            boolean sel = K9.ax == gid;
            boolean bl = hover = this.aU >= (float)tx && this.aU < (float)(tx + tw) && this.aV >= (float)ty && this.aV < (float)(ty + th);
            if (hover) {
                anyHover = true;
                if (this.aW != i) {
                    this.aW = i;
                    LethalAuctionClient.playClick();
                }
            }
            K18.a(g, tx, ty, tw, th, 10, sel ? this.a(this.a(-15395813, 0.3f)) : this.a(-15724011), sel ? this.ak : (hover ? -12105140 : -14014925));
            this.a(g, gid, tx + 10, ty + 8, tw - 20, th - 34);
            K18.b(g, K54.n[gid], (float)tx + (float)tw / 2.0f, (float)(ty + th - 14), 11.0f, sel ? this.am : -789515, K18.c);
            this.a(tx, ty, tw, th, () -> {
                K9.ax = gid;
                K9.b();
            });
        }
        if (!anyHover) {
            this.aW = -1;
        }
        return y += th + 10;
    }

    private void a(class_332 g, int game, int x, int y, int w, int h) {
        int[] C = new int[]{-41862, -11548161, -15797, -10888821, -5215233, -26051};
        float cx = (float)x + (float)w / 2.0f;
        float cy = (float)y + (float)h / 2.0f;
        switch (game) {
            case 0: {
                int i;
                float r = (float)Math.min(w, h) / 2.0f - 2.0f;
                for (i = 0; i < 6; ++i) {
                    double a0 = (double)i / 6.0 * 6.283;
                    double a1 = (double)(i + 1) / 6.0 * 6.283;
                    K18.a(g, cx, cy, r, -15986400);
                }
                for (i = 0; i < 6; ++i) {
                    double a = (double)i / 6.0 * 6.283 + 0.5;
                    K18.a(g, cx + (float)Math.cos(a) * r * 0.55f, cy + (float)Math.sin(a) * r * 0.55f, r * 0.26f, C[i]);
                }
                K18.a(g, cx, cy, r * 0.22f, -15797);
                K18.b(g, "$", cx, cy, r * 0.5f, -12965114, K18.c);
                break;
            }
            case 1: {
                for (int r = 0; r < 4; ++r) {
                    for (int c = 0; c < 4; ++c) {
                        K18.a(g, (float)(x + 10) + (float)(c * (w - 20)) / 3.0f + (float)(r % 2 * 5), (float)(y + 6) + (float)(r * (h - 24)) / 3.0f, 1.6f, -12957850);
                    }
                }
                K18.a(g, cx + 6.0f, y + 10, 4.0f, -15797);
                for (int i = 0; i < 5; ++i) {
                    K18.a(g, Math.round((float)(x + 4) + (float)(i * (w - 8)) / 5.0f), Math.round(y + h - 8), Math.round((float)(w - 8) / 5.0f - 2.0f), 8, 2, K18.a(C[i], 0.5f));
                }
                break;
            }
            case 6: {
                float r = (float)Math.min(w, h) / 2.0f - 2.0f;
                K18.a(g, cx, cy, r, -15797);
                K18.a(g, cx, cy, r - 2.0f, -15986400);
                K18.a(g, cx - 7.0f, cy, 6.0f, C[0]);
                K18.a(g, cx + 7.0f, cy, 6.0f, C[1]);
                break;
            }
            case 7: {
                for (int r = 0; r < 3; ++r) {
                    float rx = (float)(x + 4) + (float)(r * (w - 8)) / 3.0f;
                    K18.a(g, Math.round(rx), y + 4, Math.round((float)(w - 8) / 3.0f - 4.0f), h - 8, 3, -15986400);
                    K18.a(g, Math.round(rx + 3.0f), Math.round(cy - 5.0f), Math.round((float)(w - 8) / 3.0f - 10.0f), 10, 2, C[r]);
                }
                break;
            }
            case 8: {
                K18.a(g, Math.round(cx), y + 4, 2, Math.round((float)h * 0.4f), -7695450);
                K18.a(g, Math.round(cx - 6.0f), Math.round((float)(y + 4) + (float)h * 0.4f), 12, 5, 2, -4735284);
                for (int i = 0; i < 4; ++i) {
                    K18.a(g, Math.round((float)(x + 6) + (float)(i * (w - 12)) / 4.0f), Math.round(y + h - 16), Math.round((float)(w - 12) / 4.0f - 4.0f), 14, 4, C[i]);
                }
                break;
            }
            default: {
                float t = (float)System.currentTimeMillis() / 1000.0f;
                for (int i = 0; i < 5; ++i) {
                    float fx = (float)(x + 10 + i * 29 % Math.max(1, w - 20)) + (float)Math.sin(t + (float)i) * 4.0f;
                    float fy = y + 8 + i * 17 % Math.max(1, h - 16);
                    K18.b(g, "?", fx, fy, 12.0f + (float)(i % 3 * 5), K18.a(C[i], 0.5f), K18.c);
                }
                K18.b(g, "?", cx, cy, (float)Math.min(w, h) * 0.5f, 0x33FFFFFF, K18.c);
            }
        }
    }

    private void a(class_332 g, float cx, float cy, float size, String id) {
        try {
            class_2960 rid = id.contains(":") ? class_2960.method_60654((String)id) : class_2960.method_60655((String)"minecraft", (String)id);
            class_1792 item = (class_1792)class_7923.field_41178.method_63535(rid);
            class_1799 stack = new class_1799((class_1935)item);
            if (stack.method_7960()) {
                return;
            }
            Matrix3x2fStack p = g.method_51448();
            p.pushMatrix();
            p.translate(cx - size / 2.0f, cy - size / 2.0f);
            p.scale(size / 16.0f);
            g.method_51427(stack, 0, 0);
            p.popMatrix();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void a(class_332 g, int x, int y, int w, boolean active, String label, Runnable set) {
        if (active) {
            this.a(g, x, y, w, 24, label, this.al, this.ak, this.am, () -> {});
        } else {
            this.a(g, x, y, w, 24, label, this.a(-15395813), -13356484, -789515, set);
        }
    }

    private int a(class_332 g, int y, int icon, int color, String label, K31 box, String placeholder) {
        float cy = y + 15;
        K18.b(g, K22.a(icon), 266.0f, cy, 14.0f, color, K22.a);
        K18.a(g, label, 282.0f, cy, 11.5f, -789515, K18.b);
        this.a(g, box, 370, y, 630, 30, placeholder);
        return y + 38;
    }

    private void b(class_332 g, int rx, int cy) {
        String txt;
        int st = K57.a();
        int dot = switch (st) {
            case 2 -> {
                txt = "Connected";
                yield -11938144;
            }
            case 1 -> {
                txt = "Connecting";
                yield -2055606;
            }
            case 3 -> {
                txt = K57.b().isEmpty() ? "Offline" : K57.b();
                yield -2074022;
            }
            default -> {
                txt = K9.ab ? "Idle" : "Off";
                yield -9735032;
            }
        };
        float tw = K18.a(txt, 9.5f, K18.b);
        float w = tw + 26.0f;
        float x = (float)rx - w;
        K18.a(g, Math.round(x), cy - 9, Math.round(w), 18, 9, 0x22FFFFFF);
        K18.a(g, x + 12.0f, cy, 3.0f, dot);
        K18.a(g, txt, x + 20.0f, (float)cy, 9.5f, -3947581, K18.b);
    }

    private void a(class_332 g, int x, int y, boolean active, String label, Runnable set) {
        if (active) {
            this.a(g, x, y, 100, 24, label, this.al, this.ak, this.am, () -> {});
        } else {
            this.a(g, x, y, 100, 24, label, this.a(-15395813), -13356484, -789515, set);
        }
    }

    private void a(class_332 g, int boxX, int boxY, int boxW, int boxH) {
        float cx = (float)boxX + (float)boxW / 2.0f;
        float cy = (float)(boxY + 30) + (float)(boxH - 30) / 2.0f;
        int color = K52.a(K9.aC) & 0xFFFFFF;
        float sizeF = Math.max(0.75f, Math.min(1.35f, K9.aD));
        long now = System.currentTimeMillis();
        String[] words = new String[]{"as", "your", "shadow", "crosses", "mine"};
        float[][] p = new float[][]{{-150.0f, -28.0f, 1.0f, 0.0f}, {-30.0f, -42.0f, 1.15f, 1.1f}, {118.0f, -22.0f, 1.35f, 2.0f}, {-96.0f, 28.0f, 1.1f, 3.0f}, {72.0f, 36.0f, 1.25f, 4.2f}};
        for (int i = 0; i < words.length; ++i) {
            float alpha;
            float ph = p[i][3];
            float bob = (float)Math.sin((double)now / 900.0 + (double)ph) * 3.0f;
            float cycle = (float)(((double)now / 1000.0 + (double)ph) % 4.0);
            alpha = cycle < 0.3f ? cycle / 0.3f : (cycle > 3.4f ? Math.max(0.0f, (4.0f - cycle) / 0.6f) : 1.0f);
            if (alpha <= 0.02f) continue;
            float size = 15.0f * p[i][2] * sizeF;
            float wx = cx + p[i][0];
            float wy = cy + p[i][1] + bob;
            for (int gq = 0; gq < 3; ++gq) {
                double ang = (double)gq * Math.PI * 2.0 / 3.0 + 0.4;
                int gcol = Math.round(alpha * 60.0f) << 24 | color;
                K18.b(g, words[i], wx + (float)Math.cos(ang) * 1.6f, wy + (float)Math.sin(ang) * 1.6f, size, gcol, K18.e);
            }
            int main = Math.round(alpha * 255.0f) << 24 | color;
            K18.b(g, words[i], wx, wy, size, main, K18.e);
        }
    }

    private int c(class_332 g, int y, int cardFill, int cardBorder) {
        K18.a(g, "Nametag", 243.0f, (float)(y + 8), 12.5f, -789515, K18.c);
        boolean canToggle = K37.h() >= 4;
        int cardH = 92;
        K18.a(g, 241, y += 22, 765, cardH, 10, cardFill, cardBorder);
        K18.a(g, "Show nametag icons", 257.0f, (float)(y + 22), 12.5f, -789515, K18.c);
        K18.a(g, canToggle ? "Your role icon and sticker on your nametag, shown to other Lethal Client players. Pick stickers in the Locker." : "Your role icon and sticker on your nametag. Pick stickers in the Locker. Creator rank and above can hide this.", 257.0f, (float)(y + 40), 10.0f, -5001030, K18.a);
        int rank = K37.h();
        this.a(g, 257, y + 54, "On", true, rank);
        this.a(g, 420, y + 54, "Off", false, rank);
        if (canToggle) {
            this.a(g, 960, y + 29, K9.l, () -> {
                K9.l = !K9.l;
                K9.b();
            });
        } else {
            K18.a(g, 960, y + 21, 32, 16, 8, K18.a(this.ak, 0.4f));
            K18.a(g, 984.0f, y + 29, 6.0f, -1275068417);
            K18.b(g, K22.a(60130), 944.0f, (float)(y + 29), 11.0f, -7568235, K22.a);
        }
        int cardH2 = 72;
        K18.a(g, 241, y += cardH + 10, 765, cardH2, 10, cardFill, cardBorder);
        K18.a(g, "Fake player name", 257.0f, (float)(y + 22), 12.5f, -789515, K18.c);
        K18.a(g, "Shows \"Player\" above your head instead of your username. Only you see it; others still see your real name.", 257.0f, (float)(y + 42), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 29, K9.m, () -> {
            K9.m = !K9.m;
            K9.b();
        });
        return y + cardH2 + 4;
    }

    private void a(class_332 g, int x, int y, String label, boolean withBadge, int rank) {
        String name = "You";
        float pad = 8.0f;
        float badgeW = withBadge ? 11.0f : 0.0f;
        float nameW = K18.a(name, 10.0f, K18.b);
        int w = Math.round(pad * 2.0f + badgeW + nameW);
        K18.a(g, x, y, Math.max(44, w), 20, 6, -1291845632);
        float tx = (float)x + pad;
        if (withBadge) {
            K18.a(g, K14.c(rank), tx, (float)(y + 10), 11.0f, -1, K14.c());
            tx += badgeW + 2.0f;
        }
        K18.a(g, name, tx, (float)(y + 10), 10.0f, -1, K18.b);
        boolean shown = K9.l || rank < 4;
        boolean active = withBadge == shown;
        K18.a(g, label, (float)x, (float)(y + 30), 9.0f, active ? this.am : -7568235, K18.b);
    }

    private int l(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.b(g, K22.a(60237), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Mini Figures", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Pick a pack on the left, choose a figure, and it follows you around.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        long now = System.currentTimeMillis();
        List<K12.C78> figs = K12.b;
        int railX = 235;
        int railW = 190;
        int y = t + 120;
        String[] packs = new String[]{"DonutSMP"};
        for (int pk = 0; pk < packs.length; ++pk) {
            boolean open = this.ab == pk;
            K18.a(g, railX, y, railW, 32, 9, open ? this.al : this.a(-15395813), open ? this.ak : cardBorder);
            K18.a(g, railX + 17, y + 16, 3.8f, open ? this.am : -7568235);
            K18.a(g, packs[pk], (float)(railX + 30), (float)(y + 20), 11.5f, open ? this.am : -789515, K18.c);
            K18.c(g, open ? "\u2013" : "+", (float)(railX + railW - 14), (float)(y + 21), 13.0f, open ? this.am : -7568235, K18.c);
            int fpk = pk;
            this.a(railX, y, railW, 32, () -> {
                this.ab = this.ab == fpk ? -1 : fpk;
            });
            y += 36;
            if (!open) continue;
            for (K12.C78 fig : figs) {
                boolean eq = fig.a().equals(K9.aI);
                K18.a(g, railX + 12, y, railW - 12, 26, 7, eq ? K18.a(this.ak, 0.18f) : cardFill, eq ? this.ak : cardBorder);
                K18.a(g, fig.b(), (float)(railX + 26), (float)(y + 17), 10.5f, eq ? this.am : -2039834, eq ? K18.c : K18.a);
                if (eq) {
                    K18.a(g, railX + railW - 16, y + 13, 3.4f, this.ak);
                }
                this.a(railX + 12, y, railW - 12, 26, () -> this.a(fig));
                y += 29;
            }
            y += 6;
        }
        int railBottom = y;
        int pvX = railX + railW + 18;
        int pvW = 1013 - pvX;
        int pvY = t + 120;
        int pvH = 352;
        K18.a(g, pvX, pvY, pvW, pvH, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", (float)pvX + 16.0f, (float)(pvY + 20), 10.0f, -7568235, K18.b);
        K12.C78 worn = K12.a();
        if (worn == null) {
            K18.b(g, "Pick a figure from the pack", (float)pvX + (float)pvW / 2.0f, (float)pvY + (float)pvH / 2.0f - 6.0f, 12.0f, -5001030, K18.b);
            K18.b(g, "on the left to preview and summon it", (float)pvX + (float)pvW / 2.0f, (float)pvY + (float)pvH / 2.0f + 12.0f, 11.0f, -7568235, K18.a);
        } else {
            K18.a(g, (float)pvX + (float)pvW / 2.0f, pvY + 150, 70.0f, 0x12FFFFFF);
            K20.a(g, worn.c(), false, pvX + pvW / 2, pvY + 70, 6);
            K18.b(g, worn.b(), (float)pvX + (float)pvW / 2.0f, (float)(pvY + pvH - 86), 15.0f, K18.a(this.ak, -1, 0.15f), K18.c);
            K18.b(g, "DonutSMP", (float)pvX + (float)pvW / 2.0f, (float)(pvY + pvH - 66), 10.0f, this.am, K18.a);
            int bw = 150;
            int bx = Math.round((float)pvX + (float)pvW / 2.0f - (float)bw / 2.0f);
            this.a(g, bx, pvY + pvH - 48, bw, 30, "Put away", this.a(-15395813), -13356484, -789515, () -> this.a(worn));
        }
        int iy = Math.max(railBottom, pvY + pvH) + 12;
        K18.a(g, 235, iy, 778, 58, 12, cardFill, cardBorder);
        K18.a(g, "See others' figures", 253.0f, (float)(iy + 23), 12.5f, -789515, K18.c);
        K18.a(g, "Show other Lethal Client players' figures following them. Turn off to hide them from your view.", 253.0f, (float)(iy + 41), 10.5f, -5001030, K18.a);
        this.a(g, 961, iy + 29, K9.aK, () -> {
            K9.aK = !K9.aK;
            K9.b();
        });
        return iy + 70;
    }

    private void a(class_332 g, int x, int y, int w, int h, K12.C78 fig, long now) {
        boolean on = fig.a().equals(K9.aI);
        int pcx = x + w / 2;
        if (on) {
            K18.a(g, x - 3, y - 3, w + 6, h + 6, 12, K18.a(this.ak, 0.16f));
            K18.a(g, x, y, w, h, 10, this.a(-15001054), this.ak);
        } else {
            K18.a(g, x, y, w, h, 10, this.a(-15462119), this.a(-14804444, 0.12f));
        }
        K18.a(g, x + 8, y + 8, w - 16, 120, 7, this.a(-15987951));
        float bob = (float)Math.sin((double)now / 520.0 + (double)(fig.a().hashCode() & 0xFF)) * 2.2f;
        float pivotY = y + 70;
        float pop = 1.0f;
        if (on && fig.a().equals(this.aj)) {
            float k = Math.min(1.0f, (float)(now - this.ai) / 440.0f);
            float e = K20.b(k);
            pop = 1.0f + (1.0f - e) * 0.42f;
            if (now - this.ai < 380L) {
                float rk = (float)(now - this.ai) / 380.0f;
                K18.a(g, pcx, pivotY, 22.0f + rk * 40.0f, K18.a(this.ak, (1.0f - rk) * 0.28f));
            }
        }
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate((float)pcx, pivotY);
        p.scale(pop, pop);
        p.translate((float)(-pcx), -pivotY);
        K20.a(g, fig.c(), false, pcx, Math.round((float)(y + 20) - bob), 3);
        p.popMatrix();
        if (on) {
            K18.a(g, x + w - 16, y + 16, 7.0f, this.ak);
            K18.a(g, x + w - 16, y + 16, 5.4f, -16052716);
            K18.a(g, x + w - 16, y + 16, 3.4f, this.ak);
        }
        int nameColor = on ? K18.a(this.ak, -1, 0.15f) : -2039834;
        K18.b(g, K20.a(fig.b(), w - 20, 12.5f, K18.c), (float)pcx, (float)(y + 146), 12.5f, nameColor, K18.c);
        K18.b(g, on ? "Equipped" : "DonutSMP", (float)pcx, (float)(y + 163), 9.5f, on ? this.am : -7568235, K18.a);
        String label = on ? "Unequip" : "Equip";
        this.a(g, x + 14, y + h - 40, w - 28, 28, label, on ? this.a(-15395813) : this.al, on ? -13356484 : K18.a(this.ak, -16777216, 0.3f), on ? -789515 : this.am, () -> this.a(fig));
    }

    private void a(K12.C78 fig) {
        boolean wasOn = fig.a().equals(K9.aI);
        K12.c(fig.a());
        if (!wasOn) {
            this.ai = System.currentTimeMillis();
            this.aj = fig.a();
        } else {
            this.aj = "";
        }
    }

    private static float b(float k) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float km = k - 1.0f;
        return 1.0f + c3 * km * km * km + c1 * km * km;
    }

    private int m(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        K18.b(g, K22.a(60237), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Skins", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Changes your real Minecraft skin, so everyone sees it, even without the client.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        List<K49.C98> skins = K49.a();
        boolean hist = this.bu;
        this.a(g, 696, t + 69, 96, 26, "History", hist ? this.al : -15461606, hist ? K18.a(this.ak, -16777216, 0.15f) : -13356484, hist ? this.am : -789515, () -> {
            this.bu = !this.bu;
        });
        if (skins.size() >= 5) {
            K18.a(g, 798, t + 69, 108, 26, 6, this.a(-15395813), -14474966);
            K18.b(g, "Slots full", 852.0f, (float)t + 82.5f, 12.0f, -10593178, K18.c);
        } else {
            this.a(g, 798, t + 69, 108, 26, "Upload Skin", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, K49::g);
        }
        this.a(g, 912, t + 69, 92, 26, "Open Folder", -15461606, -13356484, -789515, () -> {
            K49.f();
            class_156.method_668().method_60932(K49.a);
        });
        String status = K49.d();
        if (!status.isEmpty()) {
            K18.c(g, status, 1004.0f, (float)(t + 108), 11.0f, this.am, K18.b);
        }
        if (this.bu) {
            return this.n(g, t);
        }
        this.a(g, this.bt, 233, t + 118, 250, 30, "Type a player's username...");
        if (!K49.b() && !K49.e()) {
            this.a(g, 491, t + 118, 120, 30, "Get Skin", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> K49.a(this.bt.b));
        } else {
            K18.a(g, 491, t + 118, 120, 30, 8, this.a(-15395813), -14474966);
            K18.b(g, K49.e() ? "Grabbing..." : "Slots full", 551.0f, (float)t + 133.5f, 12.0f, -10593178, K18.c);
        }
        K18.a(g, "Looks a player up and drops their skin into a free slot.", 623.0f, (float)t + 133.5f, 10.5f, -7568235, K18.a);
        K49.C98 current = K49.c();
        int y = t + 162;
        for (int i = 0; i <= 5; ++i) {
            int x = 233 + i * 131;
            if (i == 0) {
                this.a(g, x, y, (K49.C98)null, current == null);
                continue;
            }
            if (i - 1 < skins.size()) {
                K49.C98 skin = skins.get(i - 1);
                this.a(g, x, y, skin, current != null && current.a().equals(skin.a()));
                continue;
            }
            this.c(g, x, y);
        }
        int iy = y + 196;
        K18.a(g, 235, iy, 778, 66, 12, this.a(this.a(-15462119, 0.07f)), this.a(-14804444, 0.12f));
        K18.a(g, "How it works", 253.0f, (float)(iy + 20), 12.5f, -789515, K18.c);
        K18.a(g, "Click a skin to wear it. You see it straight away; other players see it after you rejoin the server.", 253.0f, (float)(iy + 38), 10.5f, -5001030, K18.a);
        K18.a(g, "Use 64x64 skin PNGs. Slim (Alex) arms are spotted by themselves. Normal skin puts your old one back.", 253.0f, (float)(iy + 53), 10.5f, -5001030, K18.a);
        return iy + 78;
    }

    private int n(class_332 g, int t) {
        List<K49.C97> recent = K49.h();
        int y = t + 120;
        if (recent.isEmpty()) {
            K18.a(g, 235, y, 778, 130, 12, this.a(this.a(-15462119, 0.07f)), this.a(-14804444, 0.12f));
            K18.a(g, 60225, 624.0f, (float)(y + 46), 22.0f, -9541000);
            K18.b(g, "No history yet", 624.0f, (float)(y + 76), 13.0f, -789515, K18.c);
            K18.b(g, "Wear a skin and it shows up here, so you can switch back in one click.", 624.0f, (float)(y + 96), 10.5f, -7568235, K18.a);
            return y + 150;
        }
        int shown = Math.min(recent.size(), 10);
        for (int i = 0; i < shown; ++i) {
            int x = 233 + i % 5 * 131;
            int cy = y + i / 5 * 190;
            this.a(g, x, cy, recent.get(i));
        }
        int rows = (shown + 4) / 5;
        int iy = y + rows * 190 + 4;
        K18.a(g, 235, iy, 778, 56, 12, this.a(this.a(-15462119, 0.07f)), this.a(-14804444, 0.12f));
        K18.a(g, "Skin history", 253.0f, (float)(iy + 22), 12.5f, -789515, K18.c);
        K18.a(g, "The last skins you wore. Click one to bring it back into a slot and wear it again.", 253.0f, (float)(iy + 39), 10.5f, -5001030, K18.a);
        return iy + 68;
    }

    private void a(class_332 g, int x, int y, K49.C97 recent) {
        int w = 122;
        int h = 178;
        K18.a(g, x, y, w, h, 8, this.a(-15001054));
        K18.a(g, x + 6, y + 6, w - 12, 110, 6, this.a(-15987951));
        int pcx = x + w / 2;
        K20.a(g, recent.c(), recent.d(), pcx, y + 14, 3);
        K18.b(g, K20.a(recent.b(), w - 16, 12.0f, K18.c), (float)pcx, (float)(y + 134), 12.0f, -2039834, K18.c);
        K18.a(g, x + 6, y + 148, w - 12, 24, 6, this.al, K18.a(this.ak, 0.6f));
        K18.b(g, "Wear again", (float)pcx, (float)(y + 160), 11.0f, this.am, K18.c);
        this.a(x, y, w, h, () -> K49.a(recent));
    }

    private void a(class_332 g, int x, int y, K49.C98 skin, boolean selected) {
        int nameColor;
        int w = 122;
        int h = 178;
        if (selected) {
            K18.a(g, x - 3, y - 3, w + 6, h + 6, 10, K18.a(this.ak, 0.18f));
            K18.a(g, x - 1, y - 1, w + 2, h + 2, 9, 0, this.ak);
            K18.a(g, x, y, w, h, 8, this.a(-15001054), K18.a(this.ak, 0.6f));
        } else {
            K18.a(g, x, y, w, h, 8, this.a(-15001054));
        }
        K18.a(g, x + 6, y + 6, w - 12, 110, 6, this.a(-15987951));
        int pcx = x + w / 2;
        if (skin == null) {
            class_7532.method_52722((class_332)g, (class_8685)this.h(), (int)(pcx - 24), (int)(y + 37), (int)48);
        } else {
            K20.a(g, skin.b(), skin.c(), pcx, y + 14, 3);
        }
        if (selected) {
            K18.a(g, x + w - 15, y + 15, 6.0f, this.ak);
            K18.a(g, x + w - 15, y + 15, 4.8f, -16052716);
            K18.a(g, x + w - 15, y + 15, 3.6f, this.ak);
        }
        int n = nameColor = selected ? K18.a(this.ak, -1, 0.15f) : -2039834;
        if (skin == null) {
            K18.b(g, "Normal skin", (float)pcx, (float)(y + 134), 12.0f, nameColor, K18.c);
            K18.b(g, "Your own skin", (float)pcx, (float)(y + 154), 10.5f, -5001030, K18.a);
            this.a(x, y, w, h, () -> K49.b(""));
        } else {
            K18.b(g, K20.a(skin.a().replaceAll("(?i)\\.png$", ""), w - 16, 12.0f, K18.c), (float)pcx, (float)(y + 134), 12.0f, nameColor, K18.c);
            K18.a(g, x + 6, y + 148, w - 12, 24, 6, -14020844, -10871256);
            K18.a(g, 60225, (float)pcx, (float)(y + 160), 13.0f, -30059);
            this.a(x + 6, y + 148, w - 12, 24, () -> K49.a(skin));
            this.a(x, y, w, 140, () -> K49.b(skin.a()));
        }
    }

    private static void a(class_332 g, class_2960 tex, boolean slim, int cx, int top, int s) {
        int arm = slim ? 3 : 4;
        int bx = cx - 4 * s;
        K20.a(g, tex, bx, top, 8, 8, 8, 8, s);
        K20.a(g, tex, bx, top, 40, 8, 8, 8, s);
        K20.a(g, tex, bx, top + 8 * s, 20, 20, 8, 12, s);
        K20.a(g, tex, bx, top + 8 * s, 20, 36, 8, 12, s);
        K20.a(g, tex, bx - arm * s, top + 8 * s, 44, 20, arm, 12, s);
        K20.a(g, tex, bx - arm * s, top + 8 * s, 44, 36, arm, 12, s);
        K20.a(g, tex, bx + 8 * s, top + 8 * s, 36, 52, arm, 12, s);
        K20.a(g, tex, bx + 8 * s, top + 8 * s, 52, 52, arm, 12, s);
        K20.a(g, tex, bx, top + 20 * s, 4, 20, 4, 12, s);
        K20.a(g, tex, bx, top + 20 * s, 4, 36, 4, 12, s);
        K20.a(g, tex, bx + 4 * s, top + 20 * s, 20, 52, 4, 12, s);
        K20.a(g, tex, bx + 4 * s, top + 20 * s, 4, 52, 4, 12, s);
    }

    private static void a(class_332 g, class_2960 tex, int cx, int top, int s) {
        int hs = Math.round((float)s * 1.5f);
        int hx = cx - 4 * hs;
        K20.a(g, tex, hx, top, 8, 8, 8, 8, hs);
        K20.a(g, tex, hx, top, 40, 8, 8, 8, hs);
        int by = top + 8 * hs;
        int bx = cx - 4 * s;
        int arm = 4;
        K20.a(g, tex, bx, by, 20, 20, 8, 12, s);
        K20.a(g, tex, bx, by, 20, 36, 8, 12, s);
        K20.a(g, tex, bx - arm * s, by, 44, 20, arm, 12, s);
        K20.a(g, tex, bx - arm * s, by, 44, 36, arm, 12, s);
        K20.a(g, tex, bx + 8 * s, by, 36, 52, arm, 12, s);
        K20.a(g, tex, bx + 8 * s, by, 52, 52, arm, 12, s);
        K20.a(g, tex, bx, by + 12 * s, 4, 20, 4, 12, s);
        K20.a(g, tex, bx, by + 12 * s, 4, 36, 4, 12, s);
        K20.a(g, tex, bx + 4 * s, by + 12 * s, 20, 52, 4, 12, s);
        K20.a(g, tex, bx + 4 * s, by + 12 * s, 4, 52, 4, 12, s);
    }

    private static void a(class_332 g, class_2960 tex, int x, int y, int u, int v, int w, int h, int s) {
        g.method_25302(class_10799.field_56883, tex, x, y, (float)u, (float)v, w * s, h * s, w, h, 64, 64);
    }

    private void c(class_332 g, int x, int y) {
        int i;
        int w = 122;
        int h = 178;
        int dash = K18.a(-1, 0.18f);
        for (i = 6; i < w - 6; i += 8) {
            K18.a(g, x + i, y, 4, 1, dash);
            K18.a(g, x + i, y + h - 1, 4, 1, dash);
        }
        for (i = 6; i < h - 6; i += 8) {
            K18.a(g, x, y + i, 1, 4, dash);
            K18.a(g, x + w - 1, y + i, 1, 4, dash);
        }
        K18.a(g, 60231, (float)x + (float)w / 2.0f, (float)(y + 72), 22.0f, -9541000);
        K18.b(g, "Empty slot", (float)x + (float)w / 2.0f, (float)(y + 104), 11.5f, -7568235, K18.b);
        K18.b(g, "Click to upload", (float)x + (float)w / 2.0f, (float)(y + 120), 10.0f, -9541000, K18.a);
        this.a(x, y, w, h, K49::g);
    }

    private int o(class_332 g, int t) {
        int bottom;
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        int head = K18.a(this.ak, -1, 0.05f);
        K18.b(g, K22.a(60237), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Mini Locker", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Your mini figure and its cosmetics. Other Lethal Client players see what you equip.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int cx = 235;
        int cw = 1013 - cx;
        String[] tabs = new String[]{"Figures", "Hats", "Accessories", "Shoes", "Emotes"};
        int ty = t + 118;
        int tgap = 8;
        int tabW = (cw - (tabs.length - 1) * tgap) / tabs.length;
        int i = 0;
        while (i < tabs.length) {
            int tx = cx + i * (tabW + tgap);
            boolean on = this.aa == i;
            K18.a(g, tx, ty, tabW, 32, 9, on ? this.al : this.a(-15395813), on ? this.ak : cardBorder);
            K18.b(g, tabs[i], (float)tx + (float)tabW / 2.0f, (float)ty + 16.5f, 11.5f, on ? this.am : -789515, K18.c);
            int fi = i++;
            this.a(tx, ty, tabW, 32, () -> {
                this.aa = fi;
            });
        }
        int pvY = ty + 44;
        int pvH = 230;
        K18.a(g, cx, pvY, cw, pvH, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", (float)cx + 16.0f, (float)(pvY + 20), 10.0f, -7568235, K18.b);
        K12.C78 worn = K12.a();
        if (worn == null) {
            K18.b(g, "Pick a figure below to preview and summon it", (float)cx + (float)cw / 2.0f, (float)pvY + (float)pvH / 2.0f, 12.0f, -5001030, K18.b);
        } else {
            K20.a(g, worn.c(), cx + cw / 2, pvY + 22, 5);
            K18.b(g, worn.b(), (float)cx + (float)cw / 2.0f, (float)(pvY + pvH - 16), 14.0f, K18.a(this.ak, -1, 0.15f), K18.c);
        }
        int sy = pvY + pvH + 30;
        if (this.aa == 0) {
            if (this.Y != this.ag) {
                this.ag = this.Y;
                this.af = System.currentTimeMillis();
            }
            bottom = this.ae < 0 ? this.b(g, cx, cw, sy, cardBorder) : this.c(g, cx, cw, sy, cardBorder);
        } else {
            K18.a(g, cx, sy, cw, 130, 12, cardFill, cardBorder);
            K18.b(g, K22.a(60130), (float)cx + (float)cw / 2.0f, (float)(sy + 50), 20.0f, head, K22.a);
            K18.b(g, "Coming soon", (float)cx + (float)cw / 2.0f, (float)(sy + 82), 12.5f, -789515, K18.c);
            K18.b(g, tabs[this.aa] + " for your mini figure drop here soon.", (float)cx + (float)cw / 2.0f, (float)(sy + 100), 10.0f, -5001030, K18.a);
            bottom = sy + 130;
        }
        return bottom + 10;
    }

    private int b(int i) {
        return bP[(i % bP.length + bP.length) % bP.length];
    }

    private static void a(class_332 g, float x, float y, int size, int color, boolean left) {
        for (int k = 0; k <= size; ++k) {
            int px = Math.round(left ? x + (float)(size - k) : x + (float)k);
            K18.a(g, px, Math.round(y + (float)k), 2, 2, color);
            K18.a(g, px, Math.round(y + (float)(2 * size) - (float)k), 2, 2, color);
        }
    }

    private static void a(class_332 g, float x, float y, float tw, float th, int color) {
        int n = Math.max(1, Math.round(tw));
        for (int i = 0; i < n; ++i) {
            int barH = Math.max(1, Math.round(th * (1.0f - (float)i / (float)n)));
            K18.a(g, Math.round(x + (float)i), Math.round(y - (float)barH / 2.0f), 1, barH, color);
        }
    }

    private static void a(class_332 g, float cx, float cy, float s, float a) {
        int red = K18.a(-65536, a);
        int white = K18.a(-1, Math.min(1.0f, a + 0.25f));
        int w = Math.round(s * 1.06f);
        int h = Math.round(s * 0.62f);
        K18.a(g, Math.round(cx - (float)w / 2.0f), Math.round(cy - (float)h / 2.0f), w, h, Math.max(4, Math.round((float)h * 0.34f)), red);
        K20.a(g, cx - s * 0.1f, cy, s * 0.26f, s * 0.34f, white);
    }

    private static void b(class_332 g, float cx, float cy, float s, float a) {
        int pur = K18.a(-7256321, a);
        int white = K18.a(-1, Math.min(1.0f, a + 0.25f));
        int w = Math.round(s * 0.74f);
        int h = Math.round(s * 0.8f);
        int x = Math.round(cx - (float)w / 2.0f);
        int y = Math.round(cy - (float)h / 2.0f);
        K18.a(g, x, y, w, h, 3, pur);
        K18.a(g, x + Math.round((float)w * 0.62f), y + h, Math.round((float)w * 0.22f), Math.round(s * 0.14f), pur);
        int barW = Math.max(2, Math.round(s * 0.11f));
        int barH = Math.round((float)h * 0.42f);
        K18.a(g, Math.round(cx - s * 0.17f), Math.round(cy - (float)h * 0.16f), barW, barH, white);
        K18.a(g, Math.round(cx + s * 0.06f), Math.round(cy - (float)h * 0.16f), barW, barH, white);
    }

    private static void c(class_332 g, float cx, float cy, float s, float a) {
        K20.a(g, cx - s * 0.08f, cy - s * 0.06f, s, K18.a(-14289682, a));
        K20.a(g, cx + s * 0.08f, cy + s * 0.06f, s, K18.a(-119723, a));
        K20.a(g, cx, cy, s, K18.a(-1, Math.min(1.0f, a + 0.2f)));
    }

    private static void a(class_332 g, float cx, float cy, float s, int col) {
        int stemW = Math.max(3, Math.round(s * 0.13f));
        int stemH = Math.round(s * 0.56f);
        K18.a(g, Math.round(cx + s * 0.04f), Math.round(cy - s * 0.3f), stemW, stemH, col);
        K18.a(g, Math.round(cx + s * 0.04f), Math.round(cy - s * 0.3f), Math.round(s * 0.3f), stemW, col);
        K18.a(g, Math.round(cx + s * 0.3f), Math.round(cy - s * 0.3f), stemW, Math.round(s * 0.16f), col);
        K18.a(g, cx - s * 0.04f, cy + s * 0.24f, s * 0.16f, col);
    }

    private static void b(class_332 g, class_2960 tex, int x, int y, int s) {
        K20.a(g, tex, x, y, 8, 8, 8, 8, s);
        K20.a(g, tex, x, y, 40, 8, 8, 8, s);
    }

    private int b(class_332 g, int cx, int cw, int sy, int cardBorder) {
        boolean chov;
        List<K12.C79> packs = K12.a;
        long now = System.currentTimeMillis();
        float ga0 = K18.h;
        int total = packs.size();
        if (this.bQ == null || this.bQ.length != total) {
            this.bQ = new float[total];
            this.bR = new boolean[total];
        }
        int cols = 3;
        int gap = 16;
        int bw = 240;
        int bh = 88;
        int headRoom = 26;
        int rowGap = headRoom + 18;
        int topPad = headRoom + 6;
        K12.C79 creator = packs.get(0);
        int pink = -36941;
        int cbw = 272;
        int cbx = cx + (cw - cbw) / 2;
        int cby = sy + topPad;
        float craw = (float)(now - this.af) / 360.0f;
        boolean bl = chov = craw > 0.0f && this.aU >= (float)cbx && this.aU < (float)(cbx + cbw) && this.aV >= (float)(cby - headRoom) && this.aV < (float)(cby + bh);
        if (chov && !this.bR[0]) {
            LethalAuctionClient.playClick();
        }
        this.bR[0] = chov;
        this.bQ[0] = this.bQ[0] + ((chov ? 1.0f : 0.0f) - this.bQ[0]) * 0.22f;
        float chp = this.bQ[0];
        if (craw > 0.0f) {
            float cease = K20.b(Math.min(1.0f, craw));
            float cgrow = 1.0f - (float)Math.pow(1.0f - Math.min(1.0f, craw), 3.0);
            int cdy = Math.round((1.0f - cease) * 18.0f);
            K18.h = ga0 * Math.min(1.0f, craw * 1.5f);
            int gpx = Math.round(chp * 5.0f);
            int ex = cbx - gpx;
            int ey = cby + cdy - gpx;
            int ew = cbw + gpx * 2;
            int eh = bh + gpx * 2;
            int acc = K18.a(pink, this.am, chp);
            float cpulse = 0.1f + 0.03f * (float)Math.sin((double)now / 750.0) + chp * 0.13f;
            K18.a(g, ex - 4, ey - 4, ew + 8, eh + 8, 14, K18.a(pink, cpulse));
            K18.a(g, ex, ey, ew, eh, 11, this.a(-15724011), pink);
            int hs = 3;
            int faceW = 8 * hs;
            int tileW = faceW + 8;
            int tileH = faceW + 8;
            int tgap = 6;
            int hy = ey - 18;
            int cshown = Math.min(4, creator.c().size());
            int tW = cshown * tileW + (cshown - 1) * tgap;
            int hxC = ex + (ew - tW) / 2;
            for (int h = 0; h < cshown; ++h) {
                int tx = hxC + h * (tileW + tgap);
                K18.a(g, tx, hy, tileW, tileH, 7, this.a(-15987951), K18.a(pink, 0.6f));
                K20.b(g, creator.c().get(h).c(), tx + (tileW - faceW) / 2, hy + (tileH - faceW) / 2, hs);
            }
            K18.a(g, creator.b(), (float)ex + 18.0f, (float)(ey + 34), 15.5f, pink, K18.c);
            K18.a(g, "Exclusive figures", (float)ex + 18.0f, (float)(ey + 52), 11.0f, -7568235, K18.a);
            K18.a(g, "Open box", (float)ex + 18.0f, (float)(ey + eh - 16), 12.0f, acc, K18.c);
            K20.a(g, (float)(ex + ew - 26) + chp * 3.0f, (float)(ey + eh - 22), 6, acc, false);
            int fUW = ew - 32;
            int cuw = Math.max(4, Math.round((float)fUW * cgrow));
            K18.a(g, ex + 16 + (fUW - cuw) / 2, ey + eh - 6, cuw, 3, 2, K18.a(pink, 0.82f + chp * 0.15f));
            K18.h = ga0;
            this.a(cbx, cby - headRoom, cbw, bh + headRoom, () -> {
                this.ae = 0;
                this.af = System.currentTimeMillis();
            });
        }
        int gridW = cols * bw + (cols - 1) * gap;
        int startX = cx + Math.max(0, (cw - gridW) / 2);
        int rows = (packs.size() - 1 + cols - 1) / cols;
        int baseY = cby + bh + rowGap;
        for (int i = 1; i < packs.size(); ++i) {
            boolean hov;
            int gi = i - 1;
            int hi = i;
            int bx = startX + gi % cols * (bw + gap);
            int by = baseY + gi / cols * (bh + rowGap);
            int pc = this.b(i);
            float raw = (float)(now - this.af - (long)hi * 80L) / 360.0f;
            boolean bl2 = hov = raw > 0.0f && this.aU >= (float)bx && this.aU < (float)(bx + bw) && this.aV >= (float)(by - headRoom) && this.aV < (float)(by + bh);
            if (hov && !this.bR[hi]) {
                LethalAuctionClient.playClick();
            }
            this.bR[hi] = hov;
            int n = hi;
            this.bQ[n] = this.bQ[n] + ((hov ? 1.0f : 0.0f) - this.bQ[hi]) * 0.22f;
            float hp = this.bQ[hi];
            if (raw <= 0.0f) continue;
            float ease = K20.b(Math.min(1.0f, raw));
            float grow = 1.0f - (float)Math.pow(1.0f - Math.min(1.0f, raw), 3.0);
            int dy = Math.round((1.0f - ease) * 18.0f);
            K18.h = ga0 * Math.min(1.0f, raw * 1.5f);
            int gpx = Math.round(hp * 5.0f);
            int ex = bx - gpx;
            int ey = by + dy - gpx;
            int ew = bw + gpx * 2;
            int eh = bh + gpx * 2;
            int accent = K18.a(pc, this.am, hp);
            float pulse = 0.09f + 0.025f * (float)Math.sin((double)now / 950.0) + hp * 0.13f;
            K18.a(g, ex - 4, ey - 4, ew + 8, eh + 8, 14, K18.a(pc, pulse));
            K18.a(g, ex, ey, ew, eh, 11, this.a(-15724011), pc);
            int hs = 3;
            int faceW = 8 * hs;
            int tileW = faceW + 8;
            int tileH = faceW + 8;
            int tgap = 6;
            int hy = ey - 18;
            K12.C79 pack = packs.get(i);
            int shown = Math.min(4, pack.c().size());
            int extra = pack.c().size() - shown;
            int chipW = extra > 0 ? 28 : 0;
            int headW = shown * tileW + (shown - 1 + (extra > 0 ? 1 : 0)) * tgap + chipW;
            int hxStart = ex + (ew - headW) / 2;
            for (int h = 0; h < shown; ++h) {
                int tx = hxStart + h * (tileW + tgap);
                K18.a(g, tx, hy, tileW, tileH, 7, this.a(-15987951), K18.a(pc, 0.55f));
                K20.b(g, pack.c().get(h).c(), tx + (tileW - faceW) / 2, hy + (tileH - faceW) / 2, hs);
            }
            if (extra > 0) {
                int tx = hxStart + shown * (tileW + tgap);
                K18.a(g, tx, hy, chipW, tileH, 7, this.a(-15724011), K18.a(pc, 0.55f));
                K18.b(g, "+" + extra, (float)tx + (float)chipW / 2.0f, (float)hy + (float)tileH / 2.0f, 11.0f, accent, K18.c);
            }
            K18.a(g, pack.b(), (float)ex + 18.0f, (float)(ey + 34), 15.5f, -789515, K18.c);
            K18.a(g, pack.c().size() + " figures", (float)ex + 18.0f, (float)(ey + 52), 11.0f, -7568235, K18.a);
            if (i >= packs.size() - 3) {
                int nbw = 46;
                int nbh = 18;
                int nbx = ex + ew - nbw - 9;
                int nby = ey + 8;
                float gl = 0.35f + 0.2f * (float)((Math.sin((double)now / 420.0) + 1.0) / 2.0);
                K18.a(g, nbx - 3, nby - 3, nbw + 6, nbh + 6, 11, K18.a(-11938144, gl));
                K18.a(g, nbx, nby, nbw, nbh, 9, -11938144);
                K18.b(g, "NEW", (float)nbx + (float)nbw / 2.0f, (float)nby + (float)nbh / 2.0f, 10.5f, -16247534, K18.c);
            }
            K18.a(g, "Open box", (float)ex + 18.0f, (float)(ey + eh - 16), 12.0f, accent, K18.c);
            K20.a(g, (float)(ex + ew - 26) + hp * 3.0f, (float)(ey + eh - 22), 6, accent, false);
            int fullUW = ew - 32;
            int uw = Math.max(4, Math.round((float)fullUW * grow));
            K18.a(g, ex + 16 + (fullUW - uw) / 2, ey + eh - 6, uw, 3, 2, K18.a(pc, 0.82f + hp * 0.15f));
            K18.h = ga0;
            int fi = i;
            this.a(bx, by + dy - headRoom, bw, bh + headRoom, () -> {
                this.ae = fi;
                this.af = System.currentTimeMillis();
            });
        }
        K18.h = ga0;
        return baseY + rows * (bh + rowGap);
    }

    private static int a(float h, float s, float v) {
        float g;
        float r;
        h = (h % 1.0f + 1.0f) % 1.0f;
        int i = (int)(h * 6.0f) % 6;
        float f = h * 6.0f - (float)((int)(h * 6.0f));
        float p = v * (1.0f - s);
        float q = v * (1.0f - f * s);
        float t = v * (1.0f - (1.0f - f) * s);
        float bch = switch (i) {
            case 0 -> {
                r = v;
                g = t;
                yield p;
            }
            case 1 -> {
                r = q;
                g = v;
                yield p;
            }
            case 2 -> {
                r = p;
                g = v;
                yield t;
            }
            case 3 -> {
                r = p;
                g = q;
                yield v;
            }
            case 4 -> {
                r = t;
                g = p;
                yield v;
            }
            default -> {
                r = v;
                g = p;
                yield q;
            }
        };
        return 0xFF000000 | (int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(bch * 255.0f);
    }

    private int c(class_332 g, int cx, int cw, int sy, int cardBorder) {
        List<K12.C79> packs = K12.a;
        int idx = Math.max(0, Math.min(this.ae, packs.size() - 1));
        K12.C79 pack = packs.get(idx);
        int pc = this.b(idx);
        boolean bHov = this.aU >= (float)cx && this.aU < (float)(cx + 122) && this.aV >= (float)sy && this.aV < (float)(sy + 30);
        K18.a(g, cx, sy, 122, 30, 9, this.a(-15462119), bHov ? pc : cardBorder);
        K20.a(g, (float)(cx + 16), (float)(sy + 10), 5, bHov ? -789515 : -7568235, true);
        K18.a(g, "All packs", (float)cx + 32.0f, (float)(sy + 15), 11.5f, bHov ? -789515 : -7568235, K18.c);
        this.a(cx, sy, 122, 30, () -> {
            this.ae = -1;
            this.af = System.currentTimeMillis();
        });
        K18.a(g, cx + 140, sy + 7, 4, 16, 2, pc);
        K18.a(g, pack.b(), (float)cx + 152.0f, (float)(sy + 15), 15.0f, -789515, K18.c);
        return this.a(g, cx, cw, sy + 46, pack, cardBorder, pc);
    }

    private int a(class_332 g, int cx, int cw, int y, K12.C79 pack, int cardBorder, int pc) {
        int cols = 4;
        int gap = 12;
        int cardW = (cw - (cols - 1) * gap) / cols;
        int cardH = 166;
        int rowGap = 12;
        long now = System.currentTimeMillis();
        float ga0 = K18.h;
        int col = 0;
        int rowY = y;
        List<K12.C78> figs = pack.c();
        for (int i = 0; i < figs.size(); ++i) {
            K12.C78 fig = figs.get(i);
            int fx = cx + col * (cardW + gap);
            float raw = (float)(now - this.af - (long)i * 55L) / 320.0f;
            if (raw > 0.0f) {
                boolean hov;
                float ease = 1.0f - (float)Math.pow(1.0f - Math.min(1.0f, raw), 3.0);
                int ry = rowY + Math.round((1.0f - ease) * 14.0f);
                K18.h = ga0 * Math.min(1.0f, raw * 1.6f);
                boolean on = fig.a().equals(K9.aI);
                boolean bl = hov = this.aU >= (float)fx && this.aU < (float)(fx + cardW) && this.aV >= (float)ry && this.aV < (float)(ry + cardH);
                if (on) {
                    K18.a(g, fx - 3, ry - 3, cardW + 6, cardH + 6, 12, K18.a(this.ak, 0.16f));
                    K18.a(g, fx, ry, cardW, cardH, 10, this.a(-15001054), this.ak);
                } else {
                    K18.a(g, fx, ry, cardW, cardH, 10, this.a(-15462119), hov ? pc : cardBorder);
                }
                K18.a(g, fx + 8, ry + 8, cardW - 16, 116, 7, this.a(-15987951));
                K20.a(g, fig.c(), fx + cardW / 2, ry + 16, 3);
                if (on) {
                    K18.a(g, fx + cardW - 15, ry + 15, 6.5f, this.ak);
                    K18.a(g, fx + cardW - 15, ry + 15, 3.0f, -16052716);
                }
                int nameColor = on ? K18.a(this.ak, -1, 0.15f) : -2039834;
                K18.b(g, K20.a(fig.b(), cardW - 16, 12.0f, K18.c), (float)fx + (float)cardW / 2.0f, (float)(ry + 140), 12.0f, nameColor, K18.c);
                K18.b(g, on ? "Equipped" : pack.b(), (float)fx + (float)cardW / 2.0f, (float)(ry + 156), 9.0f, on ? this.am : -7568235, K18.a);
                K18.h = ga0;
                this.a(fx, ry, cardW, cardH, () -> this.a(fig));
            }
            if (++col < cols) continue;
            col = 0;
            rowY += cardH + rowGap;
        }
        K18.h = ga0;
        return (col > 0 ? rowY + cardH : rowY) + 4;
    }

    private int a(class_332 g, int cx, int cw, int y, K12.C79 pack, int cardBorder) {
        int cols = 4;
        int gap = 12;
        int cardW = (cw - (cols - 1) * gap) / cols;
        int cardH = 166;
        int rowGap = 12;
        int col = 0;
        int rowY = y;
        for (K12.C78 fig : pack.c()) {
            int fx = cx + col * (cardW + gap);
            boolean on = fig.a().equals(K9.aI);
            if (on) {
                K18.a(g, fx - 3, rowY - 3, cardW + 6, cardH + 6, 12, K18.a(this.ak, 0.16f));
                K18.a(g, fx, rowY, cardW, cardH, 10, this.a(-15001054), this.ak);
            } else {
                K18.a(g, fx, rowY, cardW, cardH, 10, this.a(-15462119), cardBorder);
            }
            K18.a(g, fx + 8, rowY + 8, cardW - 16, 116, 7, this.a(-15987951));
            K20.a(g, fig.c(), fx + cardW / 2, rowY + 16, 3);
            if (on) {
                K18.a(g, fx + cardW - 15, rowY + 15, 6.5f, this.ak);
                K18.a(g, fx + cardW - 15, rowY + 15, 3.0f, -16052716);
            }
            int nameColor = on ? K18.a(this.ak, -1, 0.15f) : -2039834;
            K18.b(g, K20.a(fig.b(), cardW - 16, 12.0f, K18.c), (float)fx + (float)cardW / 2.0f, (float)(rowY + 140), 12.0f, nameColor, K18.c);
            K18.b(g, on ? "Equipped" : pack.b(), (float)fx + (float)cardW / 2.0f, (float)(rowY + 156), 9.0f, on ? this.am : -7568235, K18.a);
            this.a(fx, rowY, cardW, cardH, () -> this.a(fig));
            if (++col < cols) continue;
            col = 0;
            rowY += cardH + rowGap;
        }
        return (col > 0 ? rowY + cardH : rowY) + 4;
    }

    private int b(class_332 g, int cx, int cw, int y, int cardBorder, int head) {
        String[][] emotes = new String[][]{{"popcorn", "Popcorn"}};
        boolean has = K12.a() != null;
        K18.a(g, "Emotes", (float)cx + 2.0f, (float)(y + 10), 12.5f, -789515, K18.c);
        int rowY = y + 20;
        int cols = 4;
        int gap = 12;
        int cardW = (cw - (cols - 1) * gap) / cols;
        int cardH = 150;
        int col = 0;
        for (String[] em : emotes) {
            int fx = cx + col * (cardW + gap);
            K18.a(g, fx, rowY, cardW, cardH, 10, this.a(-15462119), cardBorder);
            K18.a(g, fx + 8, rowY + 8, cardW - 16, 100, 7, this.a(-15987951));
            K18.b(g, K22.a(60156), (float)fx + (float)cardW / 2.0f, (float)(rowY + 64), 26.0f, head, K22.a);
            K18.b(g, em[1], (float)fx + (float)cardW / 2.0f, (float)(rowY + 124), 12.0f, -2039834, K18.c);
            K18.b(g, has ? "Click to play" : "Equip a figure first", (float)fx + (float)cardW / 2.0f, (float)(rowY + 140), 9.0f, has ? this.am : -7568235, K18.a);
            String id = em[0];
            if (has) {
                this.a(fx, rowY, cardW, cardH, () -> {
                    K12.d(id);
                    this.method_25419();
                });
            }
            if (++col < cols) continue;
            col = 0;
            rowY += cardH + 12;
        }
        return (col > 0 ? rowY + cardH : rowY) + 4;
    }

    private int c(class_332 g, int rx, int rw, int y, int cardFill, int cardBorder) {
        int idx;
        int h = 40;
        int cardW = 150;
        int gap = 8;
        int innerX = rx + 24;
        int innerW = rw - 48;
        int perPage = Math.max(1, (innerW + gap) / (cardW + gap));
        int pages = Math.max(1, (ah.length + perPage - 1) / perPage);
        if (this.ac >= pages) {
            this.ac = pages - 1;
        }
        if (pages > 1) {
            this.a(g, rx, y + 8, 20, h - 16, "\u2039", this.a(-15395813), -13356484, -789515, () -> {
                this.ac = Math.max(0, this.ac - 1);
            });
            this.a(g, rx + rw - 20, y + 8, 20, h - 16, "\u203a", this.a(-15395813), -13356484, -789515, () -> {
                this.ac = Math.min(pages - 1, this.ac + 1);
            });
        }
        int start = this.ac * perPage;
        for (int i = 0; i < perPage && (idx = start + i) < ah.length; ++i) {
            int cxp = innerX + i * (cardW + gap);
            boolean on = this.ad == idx;
            K18.a(g, cxp, y, cardW, h, 10, on ? this.al : cardFill, on ? this.ak : cardBorder);
            K18.a(g, cxp + 18, (float)y + (float)h / 2.0f, 4.0f, on ? this.am : -7568235);
            K18.a(g, ah[idx], (float)(cxp + 32), (float)y + (float)h / 2.0f + 4.0f, 11.0f, on ? this.am : -789515, K18.c);
            int fi = idx;
            this.a(cxp, y, cardW, h, () -> {
                this.ad = fi;
            });
        }
        return y + h + 10;
    }

    private void d(class_332 g, int px, int py, int pw, int ph, int cardBorder) {
        K18.a(g, px, py, pw, ph, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", (float)px + 16.0f, (float)(py + 20), 10.0f, -7568235, K18.b);
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            K18.b(g, "Join a world to see", (float)px + (float)pw / 2.0f, (float)py + (float)ph / 2.0f - 8.0f, 11.0f, -5001030, K18.b);
            K18.b(g, "yourself wearing them", (float)px + (float)pw / 2.0f, (float)py + (float)ph / 2.0f + 8.0f, 11.0f, -5001030, K18.b);
            return;
        }
        int x0 = Math.round(this.W + (float)(px + 10) * this.V);
        int y0 = Math.round(this.X + (float)(py + 34) * this.V);
        int x1 = Math.round(this.W + (float)(px + pw - 10) * this.V);
        int y1 = Math.round(this.X + (float)(py + ph - 10) * this.V);
        int size = Math.round((float)(y1 - y0) * 0.34f);
        class_490.method_2486((class_332)g, (int)x0, (int)y0, (int)x1, (int)y1, (int)size, (float)0.0625f, (float)this.aX, (float)this.aY, (class_1309)mc.field_1724);
    }

    private int a(class_332 g, int rx, int rw, int y, K6.C73 slot, int cardFill, int cardBorder) {
        Map<K6.C73, K6.C74> worn = K6.b(K9.aG);
        K6.C74 current = worn.get((Object)slot);
        if (current != null) {
            this.a(g, 927, y - 30, 86, 22, "Take off", this.a(-15395813), -13356484, -789515, () -> K6.a(slot));
        }
        int cols = 3;
        int gap = 10;
        int cw = (rw - (cols - 1) * gap) / cols;
        int col = 0;
        int cardY = y;
        for (K6.C71 item : K6.b) {
            if (item.c != slot) continue;
            int cx = rx + col * (cw + gap);
            this.a(g, cx, cardY, cw, item, current, cardFill, cardBorder);
            if (++col < cols) continue;
            col = 0;
            cardY += 138;
        }
        return (col > 0 ? cardY + 138 : cardY) + 6;
    }

    private void a(class_332 g, int cx, int y, int cw, K6.C71 item, K6.C74 current, int cardFill, int cardBorder) {
        int shownVariant;
        boolean wearing = current != null && current.a() == item;
        int n = shownVariant = wearing ? current.b() : 0;
        if (wearing) {
            K18.a(g, cx - 3, y - 3, cw + 6, 126, 12, K18.a(this.ak, 0.16f));
            K18.a(g, cx, y, cw, 120, 10, cardFill, this.ak);
        } else {
            K18.a(g, cx, y, cw, 120, 10, cardFill, cardBorder);
        }
        K18.a(g, (float)cx + (float)cw / 2.0f, y + 38, 34.0f, 0x14FFFFFF);
        K18.a(g, (float)cx + (float)cw / 2.0f, y + 38, 26.0f, 0x10FFFFFF);
        this.a(g, item, item.d.get(shownVariant), (float)cx + (float)cw / 2.0f, (float)(y + 38));
        K18.b(g, item.b, (float)cx + (float)cw / 2.0f, (float)(y + 78), 12.0f, wearing ? K18.a(this.ak, -1, 0.2f) : -789515, K18.c);
        if (item.d.size() == 1) {
            int bw = 90;
            int bx = Math.round((float)cx + (float)cw / 2.0f - (float)bw / 2.0f);
            if (wearing) {
                this.a(g, bx, y + 90, bw, 22, "Wearing", this.al, this.ak, -1, () -> K6.a(item, 0));
            } else {
                this.a(g, bx, y + 90, bw, 22, "Wear", this.a(-15395813), -13356484, -789515, () -> K6.a(item, 0));
            }
            return;
        }
        int v = 0;
        while (v < item.d.size()) {
            boolean on;
            K6.C72 pal = item.d.get(v);
            float dx = (float)cx + (float)cw / 2.0f + (float)(v - 1) * 30.0f;
            float dy = y + 101;
            boolean bl = on = wearing && current.b() == v;
            if (on) {
                K18.a(g, dx, dy, 11.5f, -1);
            }
            K18.a(g, dx, dy, 10.0f, 0xFF000000 | pal.b());
            K18.a(g, dx + 3.5f, dy + 3.5f, 4.0f, 0xFF000000 | pal.c());
            int variant = v++;
            this.a(Math.round(dx - 12.0f), Math.round(dy - 12.0f), 24, 24, () -> K6.a(item, variant));
        }
    }

    private int p(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.b(g, K22.a(60426), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Cosmetics", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Hats and shoes. They sit over your helmet and boots too. Other Lethal Client users see them.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int px = 235;
        int py = Math.max(t + 110, 62);
        int pw = 220;
        int ph = 336;
        K18.a(g, px, py, pw, ph, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", (float)(px + 16), (float)(py + 20), 10.0f, -7568235, K18.b);
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            K18.b(g, "Join a world to see", (float)px + (float)pw / 2.0f, (float)py + (float)ph / 2.0f - 8.0f, 11.0f, -5001030, K18.b);
            K18.b(g, "yourself wearing them", (float)px + (float)pw / 2.0f, (float)py + (float)ph / 2.0f + 8.0f, 11.0f, -5001030, K18.b);
        } else {
            int x0 = Math.round(this.W + (float)(px + 10) * this.V);
            int y0 = Math.round(this.X + (float)(py + 30) * this.V);
            int x1 = Math.round(this.W + (float)(px + pw - 10) * this.V);
            int y1 = Math.round(this.X + (float)(py + ph - 10) * this.V);
            int size = Math.round((float)(y1 - y0) * 0.36f);
            class_490.method_2486((class_332)g, (int)x0, (int)y0, (int)x1, (int)y1, (int)size, (float)0.0625f, (float)this.aX, (float)this.aY, (class_1309)mc.field_1724);
        }
        Map<K6.C73, K6.C74> worn = K6.b(K9.aG);
        int x = 467;
        int y = t + 110;
        for (K6.C73 slot : K6.C73.values()) {
            if (slot == K6.C73.AUDIO) continue;
            K18.a(g, slot.title, (float)(x + 2), (float)(y + 10), 12.5f, -789515, K18.c);
            K6.C74 current = worn.get((Object)slot);
            if (current != null) {
                this.a(g, 927, y - 2, 86, 22, "Take off", this.a(-15395813), -13356484, -789515, () -> K6.a(slot));
            }
            y += 26;
            int cw = (1013 - x - 24) / 3;
            int cx = x;
            for (K6.C71 item : K6.b) {
                int shownVariant;
                if (item.c != slot) continue;
                boolean wearing = current != null && current.a() == item;
                int n = shownVariant = wearing ? current.b() : 0;
                if (wearing) {
                    K18.a(g, cx - 3, y - 3, cw + 6, 126, 12, K18.a(this.ak, 0.16f));
                    K18.a(g, cx, y, cw, 120, 10, cardFill, this.ak);
                } else {
                    K18.a(g, cx, y, cw, 120, 10, cardFill, cardBorder);
                }
                K18.a(g, (float)cx + (float)cw / 2.0f, y + 38, 34.0f, 0x14FFFFFF);
                K18.a(g, (float)cx + (float)cw / 2.0f, y + 38, 26.0f, 0x10FFFFFF);
                this.a(g, item, item.d.get(shownVariant), (float)cx + (float)cw / 2.0f, (float)(y + 38));
                K18.b(g, item.b, (float)cx + (float)cw / 2.0f, (float)(y + 78), 12.0f, wearing ? K18.a(this.ak, -1, 0.2f) : -789515, K18.c);
                if (item.d.size() == 1) {
                    int bw = 90;
                    int bx = Math.round((float)cx + (float)cw / 2.0f - (float)bw / 2.0f);
                    if (wearing) {
                        this.a(g, bx, y + 90, bw, 22, "Wearing", this.al, this.ak, -1, () -> K6.a(item, 0));
                    } else {
                        this.a(g, bx, y + 90, bw, 22, "Wear", this.a(-15395813), -13356484, -789515, () -> K6.a(item, 0));
                    }
                    cx += cw + 12;
                    continue;
                }
                int v = 0;
                while (v < item.d.size()) {
                    boolean on;
                    K6.C72 pal = item.d.get(v);
                    float dx = (float)cx + (float)cw / 2.0f + (float)(v - 1) * 30.0f;
                    float dy = y + 101;
                    boolean bl = on = wearing && current.b() == v;
                    if (on) {
                        K18.a(g, dx, dy, 11.5f, -1);
                    }
                    K18.a(g, dx, dy, 10.0f, 0xFF000000 | pal.b());
                    K18.a(g, dx + 3.5f, dy + 3.5f, 4.0f, 0xFF000000 | pal.c());
                    int variant = v++;
                    this.a(Math.round(dx - 12.0f), Math.round(dy - 12.0f), 24, 24, () -> K6.a(item, variant));
                }
                cx += cw + 12;
            }
            y += 138;
        }
        return Math.max(t + 110 + ph, y) + 10;
    }

    private int d(class_332 g, int t, int cardFill, int cardBorder, int head) {
        K18.b(g, "\ue025", 253.0f, (float)(t + 82), 15.0f, -1, K14.c());
        K18.a(g, "Stickers", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Decorate your nametag \u2014 everyone running Lethal Client sees it. Click to equip; click again to remove.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        List<K8.C76> all = K8.a;
        String worn = K9.aH;
        int left = 241;
        int right = 1013;
        int cols = 6;
        int gap = 12;
        int cw = (right - left - gap * (cols - 1)) / cols;
        int ch = 94;
        int y0 = t + 122;
        for (int i = 0; i < all.size(); ++i) {
            int border;
            K8.C76 s = all.get(i);
            int col = i % cols;
            int row = i / cols;
            int cx = left + col * (cw + gap);
            int cy = y0 + row * (ch + gap);
            boolean on = s.a().equals(worn);
            int n = border = on ? this.ak : cardBorder;
            if (on) {
                K18.a(g, cx - 3, cy - 3, cw + 6, ch + 6, 12, K18.a(this.ak, 0.16f));
            }
            K18.a(g, cx, cy, cw, ch, 10, cardFill, border);
            K18.a(g, (float)cx + (float)cw / 2.0f, cy + 40, 27.0f, 0x14FFFFFF);
            K18.b(g, s.c(), (float)cx + (float)cw / 2.0f, (float)(cy + 46), 30.0f, -1, K14.c());
            K18.b(g, s.b(), (float)cx + (float)cw / 2.0f, (float)(cy + ch - 14), 11.0f, on ? K18.a(this.ak, -1, 0.2f) : -789515, K18.c);
            if (on) {
                K18.b(g, "\u2713 Equipped", (float)cx + (float)cw / 2.0f, (float)(cy + 15), 8.0f, K18.a(this.ak, -1, 0.3f), K18.b);
            }
            String id = s.a();
            this.a(cx, cy, cw, ch, () -> K8.d(id));
        }
        int rows = (all.size() + cols - 1) / cols;
        return y0 + rows * (ch + gap) + 20;
    }

    private void a(class_332 g, K6.C71 item, K6.C72 pal, float cx, float cy) {
        int p = 0xFF000000 | pal.b();
        int s2 = 0xFF000000 | pal.c();
        int a = 0xFF000000 | pal.d();
        int x = Math.round(cx);
        int y = Math.round(cy);
        switch (item.a) {
            case "tophat": {
                K18.a(g, x - 34, y + 16, 68, 8, 3, p);
                K18.a(g, x - 20, y - 26, 40, 44, 4, p);
                K18.a(g, x - 20, y + 6, 40, 7, s2);
                break;
            }
            case "beanie": {
                K18.a(g, x - 26, y - 18, 52, 40, 20, p);
                K18.a(g, x - 28, y + 12, 56, 12, 4, s2);
                K18.a(g, x, y - 22, 8.0f, a);
                break;
            }
            case "cap": {
                K18.a(g, x - 24, y - 16, 48, 34, 18, p);
                K18.a(g, x - 10, y + 12, 46, 8, 4, s2);
                K18.a(g, x - 6, y - 6, 12, 9, 2, a);
                break;
            }
            case "jordanlows": 
            case "basketball": 
            case "running": {
                boolean high = item.a.equals("basketball");
                int top = high ? y - 22 : y - 8;
                K18.a(g, x - 26, top, 30, y + 14 - top, 6, p);
                K18.a(g, x - 26, y - 6, 56, 20, 8, p);
                K18.a(g, x - 30, y + 12, 64, 8, 4, s2);
                if (high) {
                    K18.a(g, x - 26, top + 8, 30, 6, 3, a);
                    K18.a(g, x - 14, y - 2, 30, 6, 3, a);
                    break;
                }
                if (item.a.equals("running")) {
                    K18.a(g, x - 16, y + 2, 38, 4, 2, a);
                    K18.a(g, x - 8, y - 4, 22, 4, 2, a);
                    K18.a(g, x - 30, y + 10, 64, 10, 5, s2);
                    break;
                }
                K18.a(g, x + 12, y - 6, 18, 18, 8, s2);
                K18.a(g, x - 26, top, 10, y + 14 - top, 4, s2);
                K18.a(g, x - 26, top, 30, 4, 2, s2);
                K18.a(g, x - 18, y + 2, 30, 4, 2, a);
                break;
            }
            case "headphones": {
                K18.a(g, x - 26, y - 20, 52, 10, 5, s2);
                K18.a(g, x - 26, y - 16, 6, 24, 3, s2);
                K18.a(g, x + 20, y - 16, 6, 24, 3, s2);
                K18.a(g, x - 30, y - 8, 12, 20, 5, p);
                K18.a(g, x + 18, y - 8, 12, 20, 5, p);
                K18.a(g, x - 27, y - 4, 5, 12, 2, a);
                K18.a(g, x + 22, y - 4, 5, 12, 2, a);
                break;
            }
        }
    }

    private int q(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.b(g, K22.a(60081), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Utilities", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "FPS & ping display, freelook and zoom. Keys can also be changed in Minecraft's Controls.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int y = t + 116;
        K18.a(g, "FPS & Ping", 243.0f, (float)(y + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 120, 12, this.a(this.a(-16053235, 0.05f)), cardBorder);
        K18.a(g, "PREVIEW", 257.0f, (float)(y + 18), 10.0f, -7568235, K18.b);
        float ps = 1.5f;
        float pw = K30.d.b() / K30.a() * ps;
        K30.a(g, 623.5f - pw / 2.0f, (float)(y + 60) - 50.0f * ps / 2.0f, ps);
        K18.a(g, 241, y += 132, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Show on screen", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Drag it anywhere and make it bigger or smaller.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 806, y + 13, 110, 24, "Edit position", this.al, K18.a(this.ak, -16777216, 0.15f), this.am, () -> {
            K9.y = true;
            K9.b();
            this.field_22787.method_1507((class_437)new K19(this, K30.d));
        });
        this.a(g, 960, y + 25, K9.y, () -> {
            K9.y = !K9.y;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "FPS", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Frames per second. Green at 60 or more, yellow at 30 or more, red below that.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.z, () -> {
            K9.z = !K9.z;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Ping", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Your connection to the server in ms. Green under 80, yellow under 150, red above.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.A, () -> {
            K9.A = !K9.A;
            K9.b();
        });
        y += 62;
        K18.a(g, "Freelook", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Freelook", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Look around without turning your player. Let go and you look where you were before.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 806, y + 13, K40.a);
        this.a(g, 960, y + 25, K9.K, () -> {
            K9.K = !K9.K;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Key works by", 257.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 345, y + 13, K9.L, (Boolean on) -> {
            K9.L = on;
        });
        K18.a(g, "Third person while looking", 690.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 960, y + 25, K9.M, () -> {
            K9.M = !K9.M;
            K9.b();
        });
        y += 62;
        K18.a(g, "Zoom", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Zoom", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Zooms in smoothly. Your hand stays normal and the mouse slows down to aim easier.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 806, y + 13, K40.b);
        this.a(g, 960, y + 25, K9.N, () -> {
            K9.N = !K9.N;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Key works by", 257.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 345, y + 13, K9.O, (Boolean on) -> {
            K9.O = on;
        });
        K18.a(g, "Zoom amount", 690.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 850, y + 13, 26, 24, "-", this.a(-15395813), -13356484, -789515, () -> {
            K9.P = Math.max(1.5f, K9.P - 0.5f);
            K9.b();
        });
        K18.b(g, String.format(Locale.ROOT, "%.1fx", Float.valueOf(K9.P)), 913.0f, (float)(y + 25), 12.0f, -789515, K18.c);
        this.a(g, 950, y + 13, 26, 24, "+", this.a(-15395813), -13356484, -789515, () -> {
            K9.P = Math.min(10.0f, K9.P + 0.5f);
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Scroll to zoom more", 257.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 450, y + 25, K9.Q, () -> {
            K9.Q = !K9.Q;
            K9.b();
        });
        K18.a(g, "Smooth zoom", 690.0f, (float)(y + 25), 11.5f, -3947581, K18.b);
        this.a(g, 960, y + 25, K9.R, () -> {
            K9.R = !K9.R;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Zoom sound", 257.0f, (float)(y + 18), 11.5f, -3947581, K18.b);
        K18.a(g, "Plays the spyglass sound when you zoom in and out.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.S, () -> {
            K9.S = !K9.S;
            K9.b();
        });
        y += 62;
        K18.a(g, "Interface", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "See-through menus", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Hides the dark blur behind inventories and menus, so you can watch the world while they're open.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.seeThroughGui, () -> {
            K9.seeThroughGui = !K9.seeThroughGui;
            K9.b();
        });
        y += 62;
        K9.C = this.bs.b.trim().isEmpty() ? "1m" : this.bs.b.trim();
        K18.a(g, "Safety", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Confirm big payments", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "A /pay of the amount below or more pops up a confirm box, so you can't fat-finger it.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.B, () -> {
            K9.B = !K9.B;
            K9.b();
        });
        K18.a(g, 241, y += 62, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Confirm at", 257.0f, (float)(y + 25), 12.5f, -789515, K18.c);
        K18.a(g, "Payments this big or bigger ask first. Supports 1k / 1m / 1b.", 430.0f, (float)(y + 25), 10.0f, -5001030, K18.b);
        K18.a(g, "$", 772.0f, (float)(y + 22), 13.0f, -5001030, K18.c);
        this.a(g, this.bs, 784, y + 11, 218, 28, "e.g. 1m");
        y += 62;
        K18.a(g, "World", 243.0f, (float)((y += 14) + 8), 12.5f, -789515, K18.c);
        K18.a(g, 241, y += 22, 765, 50, 10, cardFill, cardBorder);
        K18.a(g, "Fullbright", 257.0f, (float)(y + 18), 12.5f, -789515, K18.c);
        K18.a(g, "Lights up the whole world so you can see in caves and at night. No FPS cost.", 257.0f, (float)(y + 35), 10.0f, -5001030, K18.a);
        this.a(g, 960, y + 25, K9.T, () -> {
            K9.T = !K9.T;
            K9.b();
        });
        y += 62;
        y += 14;
        y = this.c(g, y, cardFill, cardBorder);
        return y + 6;
    }

    private void a(class_332 g, int x, int y, class_304 key) {
        String label;
        boolean waiting;
        boolean bl = waiting = this.T == key;
        label = waiting ? "Press a key..." : (key.method_1415() ? "Not set" : key.method_16007().getString());
        if (waiting) {
            this.a(g, x, y, 110, 24, label, this.al, this.ak, this.am, () -> {
                this.T = key;
            });
        } else {
            this.a(g, x, y, 110, 24, label, this.a(-15395813), -13356484, -789515, () -> {
                this.T = key;
            });
        }
    }

    private void a(class_332 g, int x, int y, boolean toggle, Consumer<Boolean> set) {
        String[] labels = new String[]{"Holding", "Pressing once"};
        for (int i = 0; i < 2; ++i) {
            boolean on = i == 1 == toggle;
            boolean value = i == 1;
            int w = i == 0 ? 80 : 110;
            int bx = x + (i == 0 ? 0 : 86);
            Runnable action = () -> {
                set.accept(value);
                K9.b();
            };
            if (on) {
                this.a(g, bx, y, w, 24, labels[i], this.al, this.ak, this.am, action);
                continue;
            }
            this.a(g, bx, y, w, 24, labels[i], this.a(-15395813), -13356484, -3947581, action);
        }
    }

    private int r(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.b(g, K22.a(60478), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Font", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "Changes the font of all text in the game, like a resource pack. Each preview below is drawn in that font.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int y = t + 116;
        String[][] opts = new String[][]{{"", "Default"}, {"font_jersey10", "Jersey 10"}, {"font_silkscreen", "Silkscreen"}, {"font_vt323", "VT323"}, {"font_minecraft", "Minecraft"}};
        for (String[] o : opts) {
            boolean sel = K9.font.equals(o[0]);
            float cy = (float)y + 31.0f;
            K18.a(g, 241, y, 765, 62, 10, cardFill, sel ? this.ak : cardBorder);
            K18.a(g, o[1], 257.0f, cy, 12.5f, sel ? -1 : -789515, K18.c);
            class_2960 pf = o[0].isEmpty() ? class_2960.method_60655("minecraft", "default") : K18.a(o[0]);
            K18.a(g, "Aa Bb Gg  Hello 123", 430.0f, cy, 17.0f, sel ? -1 : -2039584, pf);
            String fv = o[0];
            this.a(241, y, 765, 62, () -> {
                K9.font = fv;
                K9.b();
            });
            y += 72;
        }
        return y + 6;
    }

    private int s(class_332 g, int t) {
        int head = K18.a(this.ak, -1, 0.05f);
        int cardFill = this.a(this.a(-15462119, 0.07f));
        int cardBorder = this.a(-14804444, 0.12f);
        K18.b(g, K22.a(60022), 253.0f, (float)(t + 80), 17.0f, head, K22.a);
        K18.a(g, "Sky", 272.0f, (float)(t + 77), 13.0f, -789515, K18.c);
        K18.a(g, "A night sky with northern lights and shooting stars, only on your screen.", 272.0f, (float)(t + 94), 10.5f, -5001030, K18.a);
        int px = 235;
        int pw = 778;
        int y = t + 112;
        K18.a(g, px, y, pw, 58, 12, cardFill, cardBorder);
        K18.a(g, "Custom sky", (float)(px + 18), (float)(y + 21), 13.0f, -789515, K18.c);
        K18.a(g, "Replaces the normal sky in the overworld. Pick a look below.", (float)(px + 18), (float)(y + 39), 10.5f, -5001030, K18.a);
        this.a(g, px + pw - 52, y + 29, K9.o, () -> {
            K9.o = !K9.o;
            K9.b();
        });
        y += 70;
        int cols = 4;
        int gap = 12;
        int cw = (pw - gap * (cols - 1)) / cols;
        int ch = 112;
        float time = (float)(System.currentTimeMillis() % 3600000L) / 1000.0f;
        for (int i = 0; i < K50.a.size(); ++i) {
            K50.C99 ps = K50.a.get(i);
            int x = px + i % cols * (cw + gap);
            int cy = y + i / cols * (ch + 30);
            if (cy > 516 || cy + ch + 30 < 50) continue;
            boolean on = K9.p == i;
            this.a(g, ps, x, cy, cw, ch, time, i);
            K18.a(g, x - 1, cy - 1, cw + 2, ch + 2, 10, 0, on ? this.ak : -14014925);
            if (on) {
                K18.a(g, x - 3, cy - 3, cw + 6, ch + 6, 12, 0, K18.a(this.ak, 0.35f));
            }
            K18.a(g, ps.a(), (float)(x + 4), (float)(cy + ch + 15), 12.0f, on ? K18.a(this.ak, -1, 0.3f) : -2039584, K18.c);
            int pick = i;
            this.a(x, cy, cw, ch + 24, () -> {
                K9.p = pick;
                K9.o = true;
                K9.b();
            });
        }
        int rows = (K50.a.size() + cols - 1) / cols;
        K18.a(g, px, y += rows * (ch + 30) + 4, pw, 58, 12, cardFill, cardBorder);
        K18.a(g, "Shooting stars", (float)(px + 18), (float)(y + 21), 13.0f, -789515, K18.c);
        K18.a(g, "Streaks flying across the sky every few seconds.", (float)(px + 18), (float)(y + 39), 10.5f, -5001030, K18.a);
        this.a(g, px + pw - 52, y + 29, K9.q, () -> {
            K9.q = !K9.q;
            K9.b();
        });
        K18.a(g, px, y += 68, pw, 58, 12, cardFill, cardBorder);
        K18.a(g, "Hide clouds", (float)(px + 18), (float)(y + 21), 13.0f, -789515, K18.c);
        K18.a(g, "White clouds don't suit a night sky. Turn off to keep them.", (float)(px + 18), (float)(y + 39), 10.5f, -5001030, K18.a);
        this.a(g, px + pw - 52, y + 29, K9.r, () -> {
            K9.r = !K9.r;
            K9.b();
        });
        return y + 70;
    }

    private void a(class_332 g, K50.C99 ps, int x, int y, int w, int h, float time, int seed) {
        float period;
        float phase;
        float k;
        int i;
        int top = 0xFF000000 | ps.b();
        int bottom = 0xFF000000 | ps.c();
        for (int row = 0; row < h; row += 2) {
            float k2 = (float)Math.pow((float)row / (float)h, 1.6f);
            K18.a(g, x, y + row, w, Math.min(2, h - row), K18.a(top, bottom, k2));
        }
        Random rnd = new Random((long)seed * 7919L + 11L);
        int stars = Math.round(40.0f * ps.g());
        for (i = 0; i < stars; ++i) {
            float sx = (float)(x + 3) + rnd.nextFloat() * (float)(w - 6);
            float sy = (float)(y + 3) + rnd.nextFloat() * ((float)h * 0.8f);
            float tw = 0.45f + 0.55f * (float)(0.5 + 0.5 * Math.sin((double)time * (1.5 + (double)rnd.nextFloat() * 3.0) + (double)i));
            K18.a(g, Math.round(sx), Math.round(sy), 1, 1, K18.a(-1, tw));
        }
        if (ps.h() > 0.0f) {
            for (i = 0; i < 36; ++i) {
                float k3 = (float)i / 36.0f;
                float gx = (float)x + k3 * (float)w;
                float gy = (float)y + (float)h * (0.85f - k3 * 0.7f) + (float)Math.sin((double)k3 * 9.0 + (double)seed) * 4.0f;
                int c = K18.a(0xFF000000 | ps.d(), 0xFF000000 | ps.e(), k3);
                K18.a(g, gx, gy, 5.0f, K18.a(c, 0.12f * ps.h()));
            }
        }
        if (ps.f() > 0.0f) {
            for (int band = 0; band < 2; ++band) {
                for (int i2 = 0; i2 < w; i2 += 4) {
                    k = (float)i2 / (float)w;
                    float wave = (float)Math.sin((double)k * 6.0 + (double)time * 0.6 + (double)band * 1.8) * 7.0f + (float)Math.sin((double)k * 13.0 - (double)time * 0.4) * 3.0f;
                    float base = (float)y + (float)h * (0.36f + (float)band * 0.16f) + wave;
                    int c = K18.a(0xFF000000 | ps.d(), 0xFF000000 | ps.e(), (float)(0.5 + 0.5 * Math.sin((double)k * 4.0 + (double)time * 0.3 + (double)band)));
                    float a = Math.min(1.0f, ps.f()) * (0.35f + 0.25f * (float)Math.sin((double)k * 25.0 + (double)time * 1.3 + (double)(band * 3)));
                    K18.a(g, x + i2, Math.round(base - 10.0f), 4, 12, K18.a(c, a * 0.45f));
                    K18.a(g, x + i2, Math.round(base - 3.0f), 4, 4, K18.a(c, a));
                }
            }
        }
        if (ps.i() > 0.0f && (phase = (time + (float)seed * 0.7f) % (period = 3.0f / ps.i())) < 0.7f) {
            k = phase / 0.7f;
            Random sr = new Random((long)((time + (float)seed * 0.7f) / period) * 31L + (long)seed);
            float sx = (float)(x + 10) + sr.nextFloat() * ((float)w * 0.6f);
            float sy = (float)(y + 6) + sr.nextFloat() * ((float)h * 0.3f);
            for (int i3 = 0; i3 < 14; ++i3) {
                float tk = k - (float)i3 * 0.02f;
                if (!(tk >= 0.0f)) continue;
                K18.a(g, Math.round(sx + tk * 60.0f), Math.round(sy + tk * 26.0f), 2, 1, K18.a(-1, (1.0f - (float)i3 / 14.0f) * (float)Math.sin((double)k * Math.PI)));
            }
        }
    }

    private void d(class_332 g, int cx, int cy) {
        class_327 font = this.field_22787.field_1772;
        int lineH = 10;
        String title = "Player";
        int width = font.method_1727(title);
        for (String v : bO) {
            width = Math.max(width, 11 + font.method_1727(v));
        }
        width += 6;
        int h = bO.length * lineH + 2;
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate((float)cx, (float)cy);
        p.scale(1.8f * K9.aS);
        int x0 = -width / 2;
        int x1 = x0 + width;
        int bottom = h / 2;
        int bodyTop = bottom - h;
        K29.a(g, x0 - 2, bodyTop - lineH - 1, x1, bodyTop - 1, 0x66000000);
        K29.b(g, x0 - 2, bodyTop - 1, x1, bottom, 0x4C000000);
        int titleX = x0 + (width - font.method_1727(title)) / 2 - 1;
        K29.b(g, font, (class_2561)class_2561.method_43470((String)title), titleX, bodyTop - lineH, -1, false);
        int rowX = x0 - (K9.aU ? 10 : 0);
        for (int i = 0; i < bO.length; ++i) {
            int ly = bodyTop + 1 + i * lineH;
            String bits = bM[i];
            for (int r = 0; r < 7; ++r) {
                for (int c = 0; c < 7; ++c) {
                    if (bits.charAt(r * 7 + c) != '1') continue;
                    K18.a(g, rowX + c, ly + r, 1, 1, bN[i]);
                }
            }
            g.method_51433(font, bO[i], rowX + 11, ly, -1, false);
        }
        p.popMatrix();
    }

    private int t(class_332 g, int t) {
        int[] cx = new int[]{249, 436, 623, 810};
        int total = H.length + K9.c.length;
        int i = 0;
        while (i < total) {
            boolean selected;
            int x = cx[i % 4];
            int y = t + 60 + i / 4 * 69;
            boolean custom = i >= H.length;
            String name = custom ? "Custom " + (i - H.length + 1) : H[i];
            boolean bl = selected = K9.b == i;
            if (selected) {
                K18.a(g, x - 3, y - 3, 190, 66, 9, K18.a(this.ak, 0.18f));
                K18.a(g, x - 1, y - 1, 186, 62, 7, 0, this.ak);
                K18.a(g, x, y, 184, 60, 6, this.a(-15001054), K18.a(this.ak, 0.6f));
            } else {
                K18.a(g, x, y, 184, 60, 6, this.a(-14409172));
            }
            K18.a(g, name, (float)(x + 6), (float)(y + 13), 12.0f, selected ? K18.a(this.ak, -1, 0.15f) : -2039834, selected ? K18.c : K18.a);
            if (custom) {
                int bw = 32;
                int bx = x + 184 - bw - 5;
                K18.a(g, bx, y + 6, bw, 14, 4, this.a(-14409172), K18.a(this.ak, 0.45f));
                K18.b(g, "Edit", (float)bx + (float)bw / 2.0f, (float)(y + 13), 7.5f, -2039834, K18.b);
                int ct = i - H.length;
                this.a(bx - 2, y + 3, bw + 6, 20, () -> {
                    this.bF = ct;
                    this.bG = 0;
                });
            } else if (selected) {
                K18.a(g, x + 174, y + 12, 6.0f, this.ak);
                K18.a(g, x + 174, y + 12, 4.8f, -16052716);
                K18.a(g, x + 174, y + 12, 3.6f, this.ak);
            }
            int sx = x + 3;
            int sw = 178;
            int sy = y + 26;
            int sh = 32;
            int seg = Math.round((float)sw * 0.25f);
            int ox = sx;
            for (int k = 0; k < 4; ++k) {
                int w = k < 3 ? seg : sx + sw - ox;
                K18.a(g, ox, sy, w, sh, 0xFF000000 | K20.a(i, k));
                ox += w;
            }
            int index = i++;
            this.a(x, y, 184, 60, () -> {
                K9.b = index;
                K9.b();
            });
        }
        int rows = (total + 3) / 4;
        int a = t + 60 + rows * 69 + 13;
        int head = K18.a(this.ak, -1, 0.05f);
        K18.a(g, 233, a, 780, 239, 10, this.a(this.a(-15396325, 0.08f)), this.a(-14541270, 0.14f));
        K18.a(g, 63191, 256.0f, (float)(a + 25), 14.0f, head);
        K18.a(g, "EFFECTS", 276.0f, (float)(a + 25), 12.5f, -3292202, K18.b);
        this.a(g, 915, a + 13, 82, 24, "Reset", -15461606, -13356484, -789515, () -> {
            K9.c();
            K9.b();
        });
        String[][] fx = new String[][]{{"See-Through GUI", "Allow background blur and transparency."}, {"Frosted Blur", "Apply frosted glass blur to surfaces."}, {"Ambient Background", "Enable the ambient gradient background."}};
        boolean[] state = new boolean[]{K9.d, K9.e, K9.f};
        for (int i2 = 0; i2 < 3; ++i2) {
            int ry = a + 69 + i2 * 57;
            K18.a(g, fx[i2][0], 252.0f, (float)ry, 12.5f, -789515, K18.c);
            K18.a(g, fx[i2][1], 252.0f, (float)(ry + 16), 10.0f, -5001030, K18.a);
            int which = i2;
            this.a(g, 942, ry + 8, state[i2], () -> {
                switch (which) {
                    case 0: {
                        K9.d = !K9.d;
                        break;
                    }
                    case 1: {
                        K9.e = !K9.e;
                        break;
                    }
                    default: {
                        K9.f = !K9.f;
                    }
                }
                K9.b();
            });
            K18.a(g, 60181, 989.0f, (float)(ry + 8), 10.0f, -7371369);
            boolean def = which != 0;
            this.a(982, ry + 1, 14, 14, () -> {
                switch (which) {
                    case 0: {
                        K9.d = def;
                        break;
                    }
                    case 1: {
                        K9.e = def;
                        break;
                    }
                    default: {
                        K9.f = def;
                    }
                }
                K9.b();
            });
        }
        if (this.bF >= 0) {
            this.c(g);
        }
        return a + 255;
    }

    private void c(class_332 g) {
        this.S.clear();
        int e = this.bF;
        if (e < 0 || e >= K9.c.length) {
            this.bF = -1;
            return;
        }
        int[] pal = K9.c[e];
        int pw = 430;
        int ph = 258;
        int px = 629 - pw / 2;
        int py = 281 - ph / 2;
        K18.a(g, 232, 54, 795, 454, -1275068416);
        K18.a(g, px, py, pw, ph, 12, this.a(-15461356), K18.a(this.ak, 0.5f));
        K18.a(g, "Custom " + (e + 1), (float)(px + 20), (float)(py + 24), 14.0f, -1, K18.c);
        K18.a(g, "Pick a slot, then set its colour", (float)(px + 20), (float)(py + 40), 9.5f, -7108970, K18.a);
        int slotW = 48;
        int slotH = 30;
        int gap = 12;
        int slotsX = px + 20;
        int slotsY = py + 54;
        int k = 0;
        while (k < 4) {
            boolean sel;
            int sxk = slotsX + k * (slotW + gap);
            boolean bl = sel = this.bG == k;
            if (sel) {
                K18.a(g, sxk - 2, slotsY - 2, slotW + 4, slotH + 4, 7, 0, this.ak);
            }
            K18.a(g, sxk, slotsY, slotW, slotH, 6, 0xFF000000 | pal[k] & 0xFFFFFF, K18.a(-1, 0.3f));
            int kk = k++;
            this.a(sxk, slotsY, slotW, slotH, () -> {
                this.bG = kk;
            });
        }
        int cur = pal[this.bG] & 0xFFFFFF;
        int r = cur >> 16 & 0xFF;
        int gg = cur >> 8 & 0xFF;
        int b = cur & 0xFF;
        K18.a(g, px + pw - 86, py + 54, 66, 30, 6, 0xFF000000 | cur, K18.a(-1, 0.3f));
        K18.a(g, String.format("#%06X", cur), (float)(px + 20), (float)(py + 104), 11.0f, -2039834, K18.b);
        int slX = px + 60;
        int slW = pw - 60 - 56;
        int slY = py + 128;
        this.a(g, "R", px + 20, slX, slY, slW, r, f -> this.a(e, 16, f.floatValue()));
        this.a(g, "G", px + 20, slX, slY + 30, slW, gg, f -> this.a(e, 8, f.floatValue()));
        this.a(g, "B", px + 20, slX, slY + 60, slW, b, f -> this.a(e, 0, f.floatValue()));
        this.a(g, px + 20, py + ph - 38, 130, 27, "Use this theme", -15461606, -13356484, -789515, () -> {
            K9.b = H.length + e;
            K9.b();
        });
        this.a(g, px + pw - 92, py + ph - 38, 72, 27, "Done", this.ak, this.ak, -16777216, () -> {
            K9.b();
            this.bF = -1;
        });
        this.a(px, py, pw, ph, () -> {});
        this.a(232, 54, 795, 454, () -> {
            K9.b();
            this.bF = -1;
        });
    }

    private void a(class_332 g, String label, int labelX, int x, int cy, int w, int val, Consumer<Float> set) {
        K18.a(g, label, (float)labelX, (float)(cy + 4), 10.5f, -2039834, K18.c);
        this.a(g, x, cy, w, (float)val / 255.0f, set);
        K18.c(g, Integer.toString(val), (float)(x + w + 44), (float)(cy + 4), 10.0f, -3947572, K18.b);
    }

    private void a(int e, int shift, float frac) {
        int v = Math.max(0, Math.min(255, Math.round(frac * 255.0f)));
        int c = K9.c[e][this.bG] & 0xFFFFFF;
        K9.c[e][this.bG] = c = c & ~(255 << shift) | v << shift;
    }

    public boolean method_25402(class_11909 event, boolean doubleClick) {
        if (event.method_74245() != 0) {
            return true;
        }
        this.e();
        float x = this.a(event.comp_4798());
        float y = this.b(event.comp_4799());
        this.aM = null;
        this.T = null;
        for (C80 h : List.copyOf(this.S)) {
            if (!h.a(x, y)) continue;
            if (h.e() != null) {
                h.e().run();
            }
            LethalAuctionClient.playClick();
            return true;
        }
        return true;
    }

    public boolean method_25403(class_11909 event, double dragX, double dragY) {
        if (this.bD != null) {
            this.e();
            this.bD.c().accept(Float.valueOf(Math.max(0.0f, Math.min(1.0f, (this.a(event.comp_4798()) - (float)this.bD.a()) / (float)this.bD.b()))));
            return true;
        }
        K4.C63 crop = K4.b();
        if (this.bw && crop != null) {
            this.e();
            float mx = this.a(event.comp_4798());
            float my = this.b(event.comp_4799());
            crop.a(this.bz + Math.round((mx - this.bx) / this.bB), this.bA + Math.round((my - this.by) / this.bB));
            return true;
        }
        if (this.be) {
            this.e();
            this.a(this.a(event.comp_4798()));
            return true;
        }
        return super.method_25403(event, dragX, dragY);
    }

    public boolean method_25406(class_11909 event) {
        if (this.bD != null) {
            this.bD = null;
            K9.b();
            return true;
        }
        if (this.bw) {
            this.bw = false;
            return true;
        }
        if (this.be) {
            this.be = false;
            K9.b();
            LethalAuctionClient.playClick();
            return true;
        }
        return super.method_25406(event);
    }

    public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.e();
        if (this.O == C81.AUCTION && this.a(this.a(mouseX), this.b(mouseY))) {
            this.aS = (float)Math.max(0.0, Math.min((double)this.aT, (double)this.aS - scrollY * 41.0));
            return true;
        }
        int pi = this.O.ordinal();
        this.P[pi] = (float)Math.max(0.0, Math.min((double)this.R[pi], (double)this.P[pi] - scrollY * 32.0));
        return true;
    }

    public boolean method_25400(class_11905 event) {
        if (this.aM != null) {
            if (this.aM.a(event.method_74226()) && this.aM == this.bH) {
                this.bK = "";
            }
            return true;
        }
        return super.method_25400(event);
    }

    public boolean method_25404(class_11908 event) {
        if (this.T != null) {
            int kc = event.comp_4795();
            if (kc == 261 || kc == 259) {
                this.T.method_1422(class_3675.field_16237);
                class_304.method_1426();
                this.field_22787.field_1690.method_1640();
            } else if (kc != 256) {
                this.T.method_1422(class_3675.method_15985((class_11908)event));
                class_304.method_1426();
                this.field_22787.field_1690.method_1640();
            }
            this.T = null;
            return true;
        }
        if (this.aM == null) {
            if (LethalAuctionClient.openMenu().method_1417(event)) {
                this.method_25419();
                return true;
            }
            return super.method_25404(event);
        }
        int key = event.comp_4795();
        if (this.aM == this.bH && event.method_74230()) {
            this.l();
            return true;
        }
        if (this.aM == this.bt && event.method_74230()) {
            K49.a(this.bt.b);
            return true;
        }
        if (this.aM == this.bH && (event.method_74242() || event.method_74244())) {
            return true;
        }
        if (this.aM != this.bk || !event.method_74230() && !event.method_74231()) {
            if (key == 259) {
                this.aM.a(event.method_74240());
            } else if (key == 261) {
                if (this.aM.c) {
                    this.aM.a(true);
                }
            } else if (event.method_74241()) {
                this.aM.c = !this.aM.b.isEmpty();
            } else if (event.method_74231() || event.method_74230()) {
                this.aM = null;
            } else if (event.method_74236() && this.j().contains(this.aM)) {
                List<K31> order = this.j();
                int i = order.indexOf(this.aM);
                int next = event.method_74239() ? i - 1 + order.size() : i + 1;
                this.aM = order.get(next % order.size());
            } else if (event.method_74243()) {
                this.aM.a(this.field_22787.field_1774.method_1460());
            } else if (event.method_74244()) {
                this.field_22787.field_1774.method_1455(this.aM.b);
                this.aM.b = "";
                this.aM.c = false;
            } else if (event.method_74242()) {
                this.field_22787.field_1774.method_1455(this.aM.b);
            } else if (key == 263 || key == 262 || key == 268 || key == 269) {
                this.aM.c = false;
            }
            return true;
        }
        if (event.method_74230()) {
            this.m();
        } else {
            this.bj = null;
            this.aM = null;
        }
        return true;
    }

    public void a() {
        this.aQ = new class_1799((class_1935)class_1802.field_8833);
        this.ax.b = "45k";
        this.aA.b = "50k";
        this.aw.b = "ely";
        this.aB.b = "Pay over = I keep";
        this.aM = this.aw;
        this.aw.c = true;
    }

    public K20 a(String name) {
        for (C81 pg : C81.values()) {
            if (!pg.name().equalsIgnoreCase(name)) continue;
            this.O = pg;
        }
        return this;
    }

    static {
        bm = c -> c >= 97 && c <= 122 || c >= 65 && c <= 90 || c >= 48 && c <= 57 || c == 95 || c == 45 || c == 46 || c == 64;
        bM = new String[]{"0001000011111001010000111110000101001111100001000", "0001000001110011111110111110001110001101100100010", "0000011000011100011101011100011100000100001010000", "0111110111111110010011111111011111001010100000000"};
        bN = new int[]{-12594881, -5750785, -2084806, -30177};
        bO = new String[]{"1T", "1000", "1", "1"};
        bP = new int[]{-11548161, -13510, -26051, -41892, -10888821, -5215233, -36955, -13730305, -4618434};
    }

    static enum C81 {
        AUCTION("Auction", 61328, 130),
        RECENT("Recent Auctions", 60394, 154),
        TRANSACTIONS("Transactions", 61940, 178, K22.a),
        THEME("Theme", 60161, 202),
        CAPE("Cape", 61082, 226),
        COSMETICS("Mini Locker", 60237, 250, K22.a),
        MUSIC("Music", 60156, 274),
        STREAM("Stream", 61929, 298, K22.a),
        CROSSHAIR("Font", 60478, 322, K22.a),
        STATS("Utilities", 60081, 346, K22.a),
        SCOREBOARD("Scoreboard", 61201, 370),
        SKY("Sky", 60022, 394, K22.a),
        SETTINGS("Settings", 60192, 418);

        final String label;
        final int icon;
        final int y;
        final class_2960 iconFont;

        private C81(String label, int icon, int y) {
            this(label, icon, y, K18.f);
        }

        private C81(String label, int icon, int y, class_2960 iconFont) {
            this.label = label;
            this.icon = icon;
            this.y = y;
            this.iconFont = iconFont;
        }
    }

    private static final class C80 {
        private final int a;
        private final int b;
        private final int c;
        private final int d;
        private final Runnable e;
        C80(int a, int b, int c, int d, Runnable e) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; }
        public int a() { return this.a; }
        public int b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }
        public Runnable e() { return this.e; }
        boolean a(float px, float py) {
            return px >= (float)this.a && px < (float)(this.a + this.c) && py >= (float)this.b && py < (float)(this.b + this.d);
        }



    }

    private static final class C82 {
        private final int a;
        private final int b;
        private final Consumer<Float> c;
        C82(int a, int b, Consumer<Float> c) { this.a = a; this.b = b; this.c = c; }
        public int a() { return this.a; }
        public int b() { return this.b; }
        public Consumer<Float> c() { return this.c; }


    }
}

