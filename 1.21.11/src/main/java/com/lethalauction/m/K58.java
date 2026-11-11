/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K24;
import com.lethalauction.m.K56;
import com.lethalauction.m.K57;
import com.lethalauction.m.K59;
import com.lethalauction.m.K9;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.joml.Matrix3x2fStack;

public class K58
implements HudElement {
    public static final int a = 262;
    public static final float b = 0.62f;
    static final int c = 44;
    private static final int z = -435548650;
    private static final int A = 0x30FFFFFF;
    private static final int B = -7695450;
    private static final int C = -10722186;
    static final int d = 60058;
    static final int e = 60677;
    static final int f = 60560;
    static final int g = 60531;
    public static final int h = -6460161;
    public static final int i = -45734;
    public static final int j = -11015968;
    public static final int k = 0;
    public static final int l = 1;
    public static final int m = 2;
    public static final int n = 3;
    public static final String[] o = new String[]{"Clean", "Chunky", "Twitch", "Bubble"};
    public static final int p = 0;
    public static final int q = 1;
    public static final int r = 2;
    public static final int s = 3;
    public static final int t = 4;
    public static final int u = 5;
    public static final String[] v = new String[]{"None", "Fade", "Slide", "Pop", "Drop", "Typewriter"};
    static final float w = 300.0f;
    static final float x = 55.0f;
    private static final class_2960 D = K18.a("poppins_cmed");
    private static final class_2960 E = K18.a("poppins_csemi");
    private static boolean F;
    private static final String[][] G;
    private static final String H = "https://static-cdn.jtvnw.net/emoticons/v2/";
    public static final K24 y;

    static String a(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length());
        s.codePoints().forEach(c -> {
            if (K58.b(c)) {
                sb.appendCodePoint(c);
            }
        });
        return sb.toString();
    }

    private static boolean b(int c) {
        if (c == 32) {
            return true;
        }
        if (c >= 33 && c <= 126) {
            return true;
        }
        if (c >= 160 && c <= 591) {
            return true;
        }
        if (c >= 8208 && c <= 8231) {
            return true;
        }
        if (c >= 8240 && c <= 8286) {
            return true;
        }
        if (c >= 8352 && c <= 8383) {
            return true;
        }
        return c == 8482 || c == 8230 || c == 9733 || c == 10084 || c == 8226;
    }

    public void render(class_332 g, class_9779 deltaTracker) {
        if (class_310.method_1551().field_1755 == null) {
            K58.a(g);
        }
    }

    public static void a(class_332 g) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
    }

    public static float a() {
        return 0.62f * K9.aj;
    }

    static void a(class_332 g, int codepoint, float cx, float cy, float size, int color) {
        K18.b(g, new String(Character.toChars(codepoint)), cx, cy, size, color, K18.a("icons_extra"));
    }

    public static int b() {
        return Math.max(3, Math.min(8, K9.ak));
    }

    static int c() {
        int t = K9.al;
        return t < 0 || t >= o.length ? 0 : t;
    }

    static int d() {
        int a = K9.am;
        return a < 0 || a >= v.length ? 0 : a;
    }

    static float e() {
        return switch (K58.c()) {
            case 1 -> 13.0f;
            case 3 -> 12.5f;
            case 2 -> 13.0f;
            default -> 12.5f;
        };
    }

    static int f() {
        return Math.round((float)K58.b() * K58.e()) + 6;
    }

    public static int g() {
        return 44 + K58.f() + 8;
    }

    public static void a(class_332 g, float x, float y) {
        K58.b(g, x, y, K58.a());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void a(class_332 g, float x, float y, float s) {
        F = true;
        try {
            K58.b(g, x, y, s);
        }
        finally {
            F = false;
        }
    }

    private static K57.C104[] l() {
        long now = System.currentTimeMillis();
        long cycle = 2200L;
        long newest = now - now % cycle;
        int n = G.length;
        K57.C104[] out = new K57.C104[n];
        for (int i = 0; i < n; ++i) {
            String[] s = G[i];
            K57.C104 l = new K57.C104(s[0], s[1], s[2], K58.d(s[3]));
            long l2 = l.e = i == n - 1 ? newest : now - 60000L - (long)(n - i) * 1000L;
            if (!s[4].isEmpty()) {
                l.f = new String[]{s[4]};
            }
            if ("twitch".equals(s[0]) && s[2].contains("Kappa")) {
                int k = s[2].indexOf("Kappa");
                l.i = new K57.C103[]{new K57.C103(k, k + 4, "25")};
            }
            out[i] = l;
        }
        return out;
    }

    private static int d(String s) {
        try {
            return 0xFF000000 | Integer.parseInt(s.replace("#", ""), 16);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static void b(class_332 g, float x, float y, float s) {
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K58.b(g);
        p.popMatrix();
    }

    static void b(class_332 g) {
        int h = K58.g();
        K18.a(g, 0, 0, 262, h, 13, -435548650, 0x30FFFFFF);
        K58.c(g);
        K18.a(g, 12, 43, 238, 1, 0x14FFFFFF);
        int top = 48;
        int area = K58.f();
        if (K58.a(g, top, area)) {
            return;
        }
        K58.b(g, top, area);
    }

    private static boolean a(class_332 g, int top, int area) {
        if (F || K57.a() == 2 || K57.j() > 0) {
            return false;
        }
        String msg = switch (K57.a()) {
            case 1 -> "Connecting to your chat\u2026";
            case 3 -> K57.b();
            default -> K58.m() ? "Starting\u2026" : "Add your handles in the Stream tab";
        };
        K18.b(g, msg, 131.0f, (float)top + (float)area / 2.0f, 9.5f, -10722186, K18.b);
        return true;
    }

    private static void c(class_332 g) {
        float iconY = 14.0f;
        float countY = 28.0f;
        float gx = 12.0f;
        gx = K58.a(g, gx, iconY, countY, 60677, -6460161, 0, F ? "demo" : K9.ae);
        gx = K58.a(g, gx, iconY, countY, 60560, -45734, 1, F ? "demo" : K9.af);
        gx = K58.a(g, gx, iconY, countY, 60531, -11015968, 2, F ? "demo" : K9.ag);
        if (!F && !K58.m()) {
            K18.a(g, "LIVE CHAT", 12.0f, 20.0f, 9.5f, -7695450, K18.c);
        }
        boolean live = F || K58.i();
        float pcy = 20.0f;
        long total = F ? 2847L : K58.j();
        String vtxt = K58.a(Math.max(0L, total));
        float vw = K18.a(vtxt, 10.0f, K18.c);
        float pillW = vw + 24.0f;
        float pillX = 250.0f - pillW;
        K18.a(g, Math.round(pillX), Math.round(pcy - 8.0f), Math.round(pillW), 16, 8, 0x22FFFFFF);
        K58.a(g, 60058, pillX + 11.0f, pcy, 10.0f, live ? -1578256 : -7695450);
        K18.a(g, vtxt, pillX + 19.0f, pcy, 10.0f, live ? -1 : -7695450, K18.c);
        if (live) {
            float pulse = 0.55f + 0.45f * (float)((Math.sin((double)System.currentTimeMillis() / 420.0) + 1.0) / 2.0);
            K18.a(g, pillX - 9.0f, pcy, 3.0f, K18.a(-50340, pulse));
        }
    }

    private static float a(class_332 g, float x, float iconY, float countY, int icon, int color, int plat, String handle) {
        boolean live;
        if (handle == null || handle.trim().isEmpty()) {
            return x;
        }
        boolean bl = live = F || K58.a(plat);
        long v = F ? (long)(plat == 0 ? 1623 : (plat == 1 ? 842 : 382)) : K58.c(plat);
        int ic = live ? color : K18.a(color, -5261628, 0.45f);
        String cnt = v >= 0L ? K58.a(v) : "";
        float cw = cnt.isEmpty() ? 0.0f : K18.a(cnt, 8.5f, K18.c);
        float colW = Math.max(18.0f, cw) + 12.0f;
        float cx = x + colW / 2.0f;
        K58.a(g, icon, cx, iconY, 12.0f, ic);
        if (live) {
            K18.a(g, cx + 7.5f, iconY - 5.5f, 2.0f, -11804534);
        }
        if (v >= 0L) {
            K18.b(g, cnt, cx, countY, 8.5f, -2893598, K18.c);
        } else {
            K18.a(g, Math.round(cx - 3.0f), Math.round(countY - 0.5f), 6, 1, 0, -7695450);
        }
        return x + colW + 6.0f;
    }

    private static long c(int plat) {
        return switch (plat) {
            case 0 -> K57.c();
            case 1 -> K57.d();
            case 2 -> K57.e();
            default -> -1L;
        };
    }

    private static void b(class_332 g, int top, int area) {
        int th = K58.c();
        long now = System.currentTimeMillis();
        float wrapW = K58.d(th);
        K57.C104[] lines = F ? K58.l() : K57.a(K58.b() + 6);
        ArrayList<C108> blocks = new ArrayList<C108>();
        for (K57.C104 l : lines) {
            C108 b = new C108();
            b.a = l;
            String nm = K58.a(l.b == null ? "" : l.b);
            b.b = nm.isEmpty() ? "?" : nm;
            b.c = l.d != 0 ? K58.i(l.d) : K58.c(l.a);
            b.d = K58.b(l.a);
            b.e = K58.c(l.a);
            b.f = K58.a(l);
            b.g = K58.b(l);
            K58.a(b, th, wrapW);
            blocks.add(b);
        }
        float y = top + area;
        for (int i = blocks.size() - 1; i >= 0; --i) {
            C108 b = (C108)blocks.get(i);
            float by = y - b.k;
            if (by < (float)top) break;
            K58.a(g, th, b, by, now);
            y = by - K58.e(th);
        }
    }

    private static void a(class_332 g, int th, C108 b, float by, long now) {
        float age = now - b.a.e;
        int anim = K58.d();
        float p = Math.max(0.0f, Math.min(1.0f, age / 300.0f));
        float ease = 1.0f - (float)Math.pow(1.0f - p, 3.0);
        float reveal = 1.0f;
        float prevA = K18.h;
        Matrix3x2fStack ps = g.method_51448();
        ps.pushMatrix();
        switch (anim) {
            case 1: {
                K18.h = prevA * ease;
                break;
            }
            case 2: {
                K18.h = prevA * ease;
                ps.translate(-(1.0f - ease) * 20.0f, 0.0f);
                break;
            }
            case 4: {
                K18.h = prevA * ease;
                ps.translate(0.0f, -(1.0f - ease) * 14.0f);
                break;
            }
            case 3: {
                K18.h = prevA * ease;
                float s = K58.a(p);
                float ax = 14.0f;
                float ay = by + b.k / 2.0f;
                ps.translate(ax, ay);
                ps.scale(s);
                ps.translate(-ax, -ay);
                break;
            }
            case 5: {
                int chars = b.j;
                reveal = chars == 0 ? 1.0f : Math.min(1.0f, age / ((float)chars * 55.0f));
                break;
            }
        }
        K58.a(g, th, b, by, reveal);
        ps.popMatrix();
        K18.h = prevA;
    }

    private static void a(C108 b, int th, float wrapW) {
        float lh = K58.e();
        float ns = K58.f(th);
        float ms = K58.g(th);
        float es = K58.h(th);
        float badgeSz = ms + 2.0f;
        float spaceW = K18.a(" ", ms, D);
        boolean showIcon = th != 2;
        String sep = th == 2 ? ":" : "";
        ArrayList<C109> placed = new ArrayList<C109>();
        int line = 0;
        float x = 0.0f;
        for (C107 bd : b.f) {
            if (x + badgeSz > wrapW && x > 0.0f) {
                x = 0.0f;
            }
            C109 pl = new C109();
            pl.a = 2;
            pl.b = ++line;
            pl.c = x;
            pl.d = badgeSz;
            pl.f = bd.a;
            pl.g = bd.b;
            pl.h = bd.c;
            placed.add(pl);
            x += badgeSz + 2.0f;
        }
        String nm = b.b + sep;
        float nameW = K18.a(nm, ns, E);
        C109 pn = new C109();
        pn.a = 3;
        pn.b = line;
        pn.c = x;
        pn.d = nameW;
        pn.e = nm;
        pn.g = b.c;
        placed.add(pn);
        x += nameW + 5.0f;
        int chars = 0;
        for (C110 u : b.g) {
            float uw;
            float f = uw = u.a ? es : K18.a(u.b, ms, D);
            if (x + spaceW + uw > wrapW && x > 0.0f) {
                ++line;
                x = 0.0f;
            } else if (x > 0.0f) {
                x += spaceW;
            }
            C109 pl = new C109();
            pl.a = u.a ? 1 : 0;
            pl.b = line;
            pl.c = x;
            pl.d = uw;
            pl.e = u.b;
            pl.f = u.c;
            pl.g = -1578256;
            placed.add(pl);
            x += uw;
            chars += u.a ? 2 : u.b.length();
        }
        b.h = placed;
        b.i = line + 1;
        b.j = chars;
        float padY = th == 1 || th == 3 ? 8.0f : 0.0f;
        b.k = (float)b.i * lh + padY + 2.0f;
    }

    private static void a(class_332 g, int th, C108 b, float by, float reveal) {
        float lh = K58.e();
        float ms = K58.g(th);
        float es = K58.h(th);
        boolean boxed = th == 1 || th == 3;
        float padX = boxed ? 8.0f : 0.0f;
        float padY = boxed ? 4.0f : 0.0f;
        boolean showIcon = th != 2;
        float indent = showIcon ? 15.0f : 0.0f;
        float x0 = 12.0f + padX + indent;
        if (th == 1) {
            K18.a(g, 12, Math.round(by), 238, Math.round(b.k - 2.0f), 7, -871097818);
        } else if (th == 3) {
            K18.a(g, 12, Math.round(by), 238, Math.round(b.k - 2.0f), 9, K18.a(b.e, 0.16f));
            K18.a(g, 12, Math.round(by), 238, Math.round(b.k - 2.0f), 9, 0, K18.a(b.e, 0.28f));
        }
        float textY = by + padY + lh / 2.0f + 1.0f;
        if (showIcon) {
            K58.a(g, b.d, 12.0f + padX + 6.0f, textY, 10.0f, b.e);
        }
        int revealChars = (int)Math.ceil(reveal * (float)b.j);
        int shown = 0;
        block5: for (C109 pl : b.h) {
            float px = x0 + pl.c;
            float ry = textY + (float)pl.b * lh;
            switch (pl.a) {
                case 3: {
                    K18.a(g, pl.e, px, ry, K58.f(th), pl.g, E);
                    continue block5;
                }
                case 2: {
                    K58.a(g, pl.f, pl.g, pl.h, px, ry, ms + 2.0f);
                    continue block5;
                }
                case 1: {
                    if (reveal < 1.0f && shown >= revealChars) continue block5;
                    shown += 2;
                    K58.a(g, pl.f, px, ry, es);
                    continue block5;
                }
            }
            String t = pl.e;
            if (reveal < 1.0f) {
                int room = revealChars - shown;
                if (room <= 0) continue;
                if (t.length() > room) {
                    t = t.substring(0, room);
                }
                shown += t.length();
            }
            K18.a(g, t, px, ry, ms, -1578256, D);
        }
    }

    private static void a(class_332 g, String url, int chipColor, int glyph, float x, float cy, float s) {
        class_2960 tex;
        int bx = Math.round(x);
        int by = Math.round(cy - s / 2.0f);
        int bs = Math.round(s);
        float ccx = (float)bx + (float)bs / 2.0f;
        float ccy = (float)by + (float)bs / 2.0f;
        class_2960 class_29602 = tex = url == null ? null : K56.a(url);
        if (tex != null) {
            K18.a(g, tex, bx, by, bs, bs, K56.c(url), K56.d(url));
            return;
        }
        K18.a(g, bx, by, bs, bs, 2, chipColor);
        if (glyph != 0) {
            K58.a(g, glyph, ccx, ccy, (float)bs - 2.5f, -1);
        }
    }

    private static void a(class_332 g, String url, float x, float cy, float s) {
        class_2960 tex;
        class_2960 class_29602 = tex = url == null ? null : K56.a(url);
        if (tex != null) {
            K18.a(g, tex, Math.round(x), Math.round(cy - s / 2.0f), Math.round(s), Math.round(s), K56.c(url), K56.d(url));
        } else if (url != null && !K56.b(url)) {
            K18.a(g, Math.round(x), Math.round(cy - s / 2.0f), Math.round(s), Math.round(s), 2, 0x22FFFFFF);
        }
    }

    private static List<C107> a(K57.C104 l) {
        ArrayList<C107> out = new ArrayList<C107>();
        String[] keys = l.g;
        String[] roles = l.f;
        int n = Math.max(keys.length, roles.length);
        for (int i = 0; i < n && out.size() < 3; ++i) {
            String role = i < roles.length ? roles[i] : "";
            C107 bd = new C107();
            bd.b = K58.f(role);
            bd.c = K58.e(role);
            if ("twitch".equals(l.a) && i < keys.length) {
                bd.a = K59.a(l.h, keys[i]);
            }
            out.add(bd);
        }
        return out;
    }

    private static List<C110> b(K57.C104 l) {
        ArrayList<C110> out = new ArrayList<C110>();
        String text = l.c == null ? "" : l.c;
        K57.C103[] emotes = l.i;
        if (emotes.length == 0) {
            for (String w : text.split(" ")) {
                String cw = K58.a(w);
                if (cw.isEmpty()) continue;
                out.add(C110.a(cw));
            }
            return out;
        }
        int[] cps = text.codePoints().toArray();
        int pos = 0;
        for (K57.C103 e : emotes) {
            if (e.a < 0 || e.a > cps.length) continue;
            if (e.a > pos) {
                K58.a(out, cps, pos, Math.min(e.a, cps.length));
            }
            int end = Math.min(e.b, cps.length - 1);
            out.add(C110.b(H + e.c + "/static/dark/1.0"));
            pos = end + 1;
        }
        if (pos < cps.length) {
            K58.a(out, cps, pos, cps.length);
        }
        return out;
    }

    private static void a(List<C110> out, int[] cps, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; ++i) {
            if (cps[i] == 32) {
                if (sb.length() <= 0) continue;
                out.add(C110.a(sb.toString()));
                sb.setLength(0);
                continue;
            }
            if (!K58.b(cps[i])) continue;
            sb.appendCodePoint(cps[i]);
        }
        if (sb.length() > 0) {
            out.add(C110.a(sb.toString()));
        }
    }

    private static int e(String role) {
        if (role == null) {
            return 60206;
        }
        return switch (role.toLowerCase()) {
            case "broadcaster" -> 60690;
            case "moderator" -> 61488;
            case "vip" -> 60261;
            case "subscriber", "founder", "member" -> 60206;
            case "verified", "partner" -> 61944;
            case "premium", "turbo", "prime" -> 59960;
            case "sub-gifter", "sub_gifter", "bits-leader" -> 60264;
            default -> 60206;
        };
    }

    private static int f(String role) {
        if (role == null) {
            return -8353124;
        }
        return switch (role.toLowerCase()) {
            case "broadcaster" -> -1500906;
            case "moderator" -> -13324725;
            case "vip" -> -2095687;
            case "subscriber", "founder", "member" -> -7256065;
            case "verified", "partner" -> -7256065;
            case "staff", "admin", "global_mod" -> -10206811;
            case "premium", "turbo", "prime" -> -16734977;
            case "artist", "artist-badge" -> -1292135;
            default -> -8353124;
        };
    }

    private static float d(int th) {
        float pad = th == 1 || th == 3 ? 16.0f : 0.0f;
        float indent = th == 2 ? 0.0f : 15.0f;
        return 238.0f - pad - indent;
    }

    private static float e(int th) {
        return switch (th) {
            case 1 -> 4.0f;
            case 3 -> 4.0f;
            case 2 -> 1.0f;
            default -> 2.0f;
        };
    }

    private static float f(int th) {
        return th == 1 ? 10.0f : 9.5f;
    }

    private static float g(int th) {
        return th == 1 ? 10.0f : 9.5f;
    }

    private static float h(int th) {
        return K58.e();
    }

    private static float a(float p) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float k = p - 1.0f;
        float v = 1.0f + c3 * k * k * k + c1 * k * k;
        return 0.6f + 0.4f * Math.max(0.0f, Math.min(1.25f, v));
    }

    static boolean h() {
        return K57.f() == 2;
    }

    static boolean a(int p) {
        return switch (p) {
            case 1 -> {
                if (K57.g() == 2) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (K57.h() == 2 || K57.e() >= 0L) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    static boolean i() {
        return K58.a(1) || K58.a(2);
    }

    static long j() {
        long t = 0L;
        boolean any = false;
        if (K57.c() >= 0L) {
            t += K57.c();
            any = true;
        }
        if (K57.d() >= 0L) {
            t += K57.d();
            any = true;
        }
        if (K57.e() >= 0L) {
            t += K57.e();
            any = true;
        }
        return any ? t : 0L;
    }

    static int b(String platform) {
        return switch (platform == null ? "" : platform) {
            case "twitch" -> 60677;
            case "youtube" -> 60560;
            case "tiktok" -> 60531;
            default -> 60677;
        };
    }

    static int c(String platform) {
        return switch (platform == null ? "" : platform) {
            case "twitch" -> -6460161;
            case "youtube" -> -45734;
            case "tiktok" -> -11015968;
            default -> -4735284;
        };
    }

    private static int i(int c) {
        return 0xFF000000 | c & 0xFFFFFF;
    }

    private static boolean m() {
        return !K9.ae.trim().isEmpty() || !K9.af.trim().isEmpty() || !K9.ag.trim().isEmpty();
    }

    static String a(long n) {
        if (n >= 1000000L) {
            return String.format("%.1fM", (double)n / 1000000.0);
        }
        if (n >= 10000L) {
            return n / 1000L + "K";
        }
        if (n >= 1000L) {
            return String.format("%.1fK", (double)n / 1000.0);
        }
        return Long.toString(n);
    }

    public static class_2960 k() {
        return K18.a("icons_extra");
    }

    static {
        G = new String[][]{{"twitch", "Ninja", "lets gooo Kappa", "#9D6CFF", "broadcaster"}, {"youtube", "MrBeast", "first", "#FF4D5A", "member"}, {"tiktok", "charli", "omg hi", "#57E8E0", ""}, {"twitch", "pokimane", "that was so clean", "#FF69B4", "moderator"}, {"youtube", "lethalluke", "ty for the follows!", "#FFCB3A", "moderator"}, {"twitch", "xqc", "KEKW no way", "#00E0C6", "vip"}};
        y = new K24(){

            @Override
            public String a() {
                return "your live chat overlay";
            }

            @Override
            public float b() {
                return 262.0f * K58.a();
            }

            @Override
            public float c() {
                return (float)K58.g() * K58.a();
            }

            @Override
            public float d() {
                return K9.ah;
            }

            @Override
            public float e() {
                return K9.ai;
            }

            @Override
            public void a(float fx, float fy) {
                K9.ah = fx;
                K9.ai = fy;
            }

            @Override
            public float f() {
                return K9.aj;
            }

            @Override
            public void a(float size) {
                K9.aj = size;
            }

            @Override
            public void g() {
                K9.ah = 0.02f;
                K9.ai = 0.25f;
                K9.aj = 1.0f;
            }

            @Override
            public void a(class_332 g, float x, float y) {
                K58.a(g, x, y);
            }
        };
    }

    private static final class C108 {
        K57.C104 a;
        String b;
        int c;
        int d;
        int e;
        List<C107> f;
        List<C110> g;
        List<C109> h;
        int i;
        int j;
        float k;

        private C108() {
        }
    }

    private static final class C107 {
        String a;
        int b;
        int c;

        private C107() {
        }
    }

    private static final class C109 {
        int a;
        int b;
        float c;
        float d;
        String e;
        String f;
        int g;
        int h;

        private C109() {
        }
    }

    private static final class C110 {
        boolean a;
        String b;
        String c;

        private C110() {
        }

        static C110 a(String t) {
            C110 u = new C110();
            u.b = t;
            return u;
        }

        static C110 b(String url) {
            C110 u = new C110();
            u.a = true;
            u.c = url;
            return u;
        }
    }
}

