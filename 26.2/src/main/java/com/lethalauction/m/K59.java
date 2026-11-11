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
import com.lethalauction.m.K57;
import com.lethalauction.m.K58;
import com.lethalauction.m.K60;
import com.lethalauction.m.K9;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public class K59
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
    private static final Identifier D = K18.a("poppins_cmed");
    private static final Identifier E = K18.a("poppins_csemi");
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
            if (K59.b(c)) {
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

    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().gui.screen() == null) {
            K59.a(g);
        }
    }

    public static void a(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
    }

    public static float a() {
        return 0.62f * K9.aj;
    }

    static void a(GuiGraphicsExtractor g, int codepoint, float cx, float cy, float size, int color) {
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
        return switch (K59.c()) {
            case 1 -> 13.0f;
            case 3 -> 12.5f;
            case 2 -> 13.0f;
            default -> 12.5f;
        };
    }

    static int f() {
        return Math.round((float)K59.b() * K59.e()) + 6;
    }

    public static int g() {
        return 44 + K59.f() + 8;
    }

    public static void a(GuiGraphicsExtractor g, float x, float y) {
        K59.b(g, x, y, K59.a());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void a(GuiGraphicsExtractor g, float x, float y, float s) {
        F = true;
        try {
            K59.b(g, x, y, s);
        }
        finally {
            F = false;
        }
    }

    private static K58.C109[] l() {
        long now = System.currentTimeMillis();
        long cycle = 2200L;
        long newest = now - now % cycle;
        int n = G.length;
        K58.C109[] out = new K58.C109[n];
        for (int i = 0; i < n; ++i) {
            String[] s = G[i];
            K58.C109 l = new K58.C109(s[0], s[1], s[2], K59.d(s[3]));
            long l2 = l.e = i == n - 1 ? newest : now - 60000L - (long)(n - i) * 1000L;
            if (!s[4].isEmpty()) {
                l.f = new String[]{s[4]};
            }
            if ("twitch".equals(s[0]) && s[2].contains("Kappa")) {
                int k = s[2].indexOf("Kappa");
                l.i = new K58.C108[]{new K58.C108(k, k + 4, "25")};
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

    public static void b(GuiGraphicsExtractor g, float x, float y, float s) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K59.b(g);
        p.popMatrix();
    }

    static void b(GuiGraphicsExtractor g) {
        int h = K59.g();
        K18.a(g, 0, 0, 262, h, 13, -435548650, 0x30FFFFFF);
        K59.c(g);
        K18.a(g, 12, 43, 238, 1, 0x14FFFFFF);
        int top = 48;
        int area = K59.f();
        if (K59.a(g, top, area)) {
            return;
        }
        K59.b(g, top, area);
    }

    private static boolean a(GuiGraphicsExtractor g, int top, int area) {
        if (F || K58.a() == 2 || K58.j() > 0) {
            return false;
        }
        String msg = switch (K58.a()) {
            case 1 -> "Connecting to your chat\u2026";
            case 3 -> K58.b();
            default -> K59.m() ? "Starting\u2026" : "Add your handles in the Stream tab";
        };
        K18.b(g, msg, 131.0f, (float)top + (float)area / 2.0f, 9.5f, -10722186, K18.b);
        return true;
    }

    private static void c(GuiGraphicsExtractor g) {
        float iconY = 14.0f;
        float countY = 28.0f;
        float gx = 12.0f;
        gx = K59.a(g, gx, iconY, countY, 60677, -6460161, 0, F ? "demo" : K9.ae);
        gx = K59.a(g, gx, iconY, countY, 60560, -45734, 1, F ? "demo" : K9.af);
        gx = K59.a(g, gx, iconY, countY, 60531, -11015968, 2, F ? "demo" : K9.ag);
        if (!F && !K59.m()) {
            K18.a(g, "LIVE CHAT", 12.0f, 20.0f, 9.5f, -7695450, K18.c);
        }
        boolean live = F || K59.i();
        float pcy = 20.0f;
        long total = F ? 2847L : K59.j();
        String vtxt = K59.a(Math.max(0L, total));
        float vw = K18.a(vtxt, 10.0f, K18.c);
        float pillW = vw + 24.0f;
        float pillX = 250.0f - pillW;
        K18.a(g, Math.round(pillX), Math.round(pcy - 8.0f), Math.round(pillW), 16, 8, 0x22FFFFFF);
        K59.a(g, 60058, pillX + 11.0f, pcy, 10.0f, live ? -1578256 : -7695450);
        K18.a(g, vtxt, pillX + 19.0f, pcy, 10.0f, live ? -1 : -7695450, K18.c);
        if (live) {
            float pulse = 0.55f + 0.45f * (float)((Math.sin((double)System.currentTimeMillis() / 420.0) + 1.0) / 2.0);
            K18.a(g, pillX - 9.0f, pcy, 3.0f, K18.a(-50340, pulse));
        }
    }

    private static float a(GuiGraphicsExtractor g, float x, float iconY, float countY, int icon, int color, int plat, String handle) {
        boolean live;
        if (handle == null || handle.trim().isEmpty()) {
            return x;
        }
        boolean bl = live = F || K59.a(plat);
        long v = F ? (long)(plat == 0 ? 1623 : (plat == 1 ? 842 : 382)) : K59.c(plat);
        int ic = live ? color : K18.a(color, -5261628, 0.45f);
        String cnt = v >= 0L ? K59.a(v) : "";
        float cw = cnt.isEmpty() ? 0.0f : K18.a(cnt, 8.5f, K18.c);
        float colW = Math.max(18.0f, cw) + 12.0f;
        float cx = x + colW / 2.0f;
        K59.a(g, icon, cx, iconY, 12.0f, ic);
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
            case 0 -> K58.c();
            case 1 -> K58.d();
            case 2 -> K58.e();
            default -> -1L;
        };
    }

    private static void b(GuiGraphicsExtractor g, int top, int area) {
        int th = K59.c();
        long now = System.currentTimeMillis();
        float wrapW = K59.d(th);
        K58.C109[] lines = F ? K59.l() : K58.a(K59.b() + 6);
        ArrayList<C113> blocks = new ArrayList<C113>();
        for (K58.C109 l : lines) {
            C113 b = new C113();
            b.a = l;
            String nm = K59.a(l.b == null ? "" : l.b);
            b.b = nm.isEmpty() ? "?" : nm;
            b.c = l.d != 0 ? K59.i(l.d) : K59.c(l.a);
            b.d = K59.b(l.a);
            b.e = K59.c(l.a);
            b.f = K59.a(l);
            b.g = K59.b(l);
            K59.a(b, th, wrapW);
            blocks.add(b);
        }
        float y = top + area;
        for (int i = blocks.size() - 1; i >= 0; --i) {
            C113 b = (C113)blocks.get(i);
            float by = y - b.k;
            if (by < (float)top) break;
            K59.a(g, th, b, by, now);
            y = by - K59.e(th);
        }
    }

    private static void a(GuiGraphicsExtractor g, int th, C113 b, float by, long now) {
        float age = now - b.a.e;
        int anim = K59.d();
        float p = Math.max(0.0f, Math.min(1.0f, age / 300.0f));
        float ease = 1.0f - (float)Math.pow(1.0f - p, 3.0);
        float reveal = 1.0f;
        float prevA = K18.h;
        Matrix3x2fStack ps = g.pose();
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
                float s = K59.a(p);
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
        K59.a(g, th, b, by, reveal);
        ps.popMatrix();
        K18.h = prevA;
    }

    private static void a(C113 b, int th, float wrapW) {
        float lh = K59.e();
        float ns = K59.f(th);
        float ms = K59.g(th);
        float es = K59.h(th);
        float badgeSz = ms + 2.0f;
        float spaceW = K18.a(" ", ms, D);
        boolean showIcon = th != 2;
        String sep = th == 2 ? ":" : "";
        ArrayList<C114> placed = new ArrayList<C114>();
        int line = 0;
        float x = 0.0f;
        for (C112 bd : b.f) {
            if (x + badgeSz > wrapW && x > 0.0f) {
                x = 0.0f;
            }
            C114 pl = new C114();
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
        C114 pn = new C114();
        pn.a = 3;
        pn.b = line;
        pn.c = x;
        pn.d = nameW;
        pn.e = nm;
        pn.g = b.c;
        placed.add(pn);
        x += nameW + 5.0f;
        int chars = 0;
        for (C115 u : b.g) {
            float uw;
            float f = uw = u.a ? es : K18.a(u.b, ms, D);
            if (x + spaceW + uw > wrapW && x > 0.0f) {
                ++line;
                x = 0.0f;
            } else if (x > 0.0f) {
                x += spaceW;
            }
            C114 pl = new C114();
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

    private static void a(GuiGraphicsExtractor g, int th, C113 b, float by, float reveal) {
        float lh = K59.e();
        float ms = K59.g(th);
        float es = K59.h(th);
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
            K59.a(g, b.d, 12.0f + padX + 6.0f, textY, 10.0f, b.e);
        }
        int revealChars = (int)Math.ceil(reveal * (float)b.j);
        int shown = 0;
        block5: for (C114 pl : b.h) {
            float px = x0 + pl.c;
            float ry = textY + (float)pl.b * lh;
            switch (pl.a) {
                case 3: {
                    K18.a(g, pl.e, px, ry, K59.f(th), pl.g, E);
                    continue block5;
                }
                case 2: {
                    K59.a(g, pl.f, pl.g, pl.h, px, ry, ms + 2.0f);
                    continue block5;
                }
                case 1: {
                    if (reveal < 1.0f && shown >= revealChars) continue block5;
                    shown += 2;
                    K59.a(g, pl.f, px, ry, es);
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

    private static void a(GuiGraphicsExtractor g, String url, int chipColor, int glyph, float x, float cy, float s) {
        Identifier tex;
        int bx = Math.round(x);
        int by = Math.round(cy - s / 2.0f);
        int bs = Math.round(s);
        float ccx = (float)bx + (float)bs / 2.0f;
        float ccy = (float)by + (float)bs / 2.0f;
        Identifier identifier = tex = url == null ? null : K57.a(url);
        if (tex != null) {
            K18.a(g, tex, bx, by, bs, bs, K57.c(url), K57.d(url));
            return;
        }
        K18.a(g, bx, by, bs, bs, 2, chipColor);
        if (glyph != 0) {
            K59.a(g, glyph, ccx, ccy, (float)bs - 2.5f, -1);
        }
    }

    private static void a(GuiGraphicsExtractor g, String url, float x, float cy, float s) {
        Identifier tex;
        Identifier identifier = tex = url == null ? null : K57.a(url);
        if (tex != null) {
            K18.a(g, tex, Math.round(x), Math.round(cy - s / 2.0f), Math.round(s), Math.round(s), K57.c(url), K57.d(url));
        } else if (url != null && !K57.b(url)) {
            K18.a(g, Math.round(x), Math.round(cy - s / 2.0f), Math.round(s), Math.round(s), 2, 0x22FFFFFF);
        }
    }

    private static List<C112> a(K58.C109 l) {
        ArrayList<C112> out = new ArrayList<C112>();
        String[] keys = l.g;
        String[] roles = l.f;
        int n = Math.max(keys.length, roles.length);
        for (int i = 0; i < n && out.size() < 3; ++i) {
            String role = i < roles.length ? roles[i] : "";
            C112 bd = new C112();
            bd.b = K59.f(role);
            bd.c = K59.e(role);
            if ("twitch".equals(l.a) && i < keys.length) {
                bd.a = K60.a(l.h, keys[i]);
            }
            out.add(bd);
        }
        return out;
    }

    private static List<C115> b(K58.C109 l) {
        ArrayList<C115> out = new ArrayList<C115>();
        String text = l.c == null ? "" : l.c;
        K58.C108[] emotes = l.i;
        if (emotes.length == 0) {
            for (String w : text.split(" ")) {
                String cw = K59.a(w);
                if (cw.isEmpty()) continue;
                out.add(C115.a(cw));
            }
            return out;
        }
        int[] cps = text.codePoints().toArray();
        int pos = 0;
        for (K58.C108 e : emotes) {
            if (e.a < 0 || e.a > cps.length) continue;
            if (e.a > pos) {
                K59.a(out, cps, pos, Math.min(e.a, cps.length));
            }
            int end = Math.min(e.b, cps.length - 1);
            out.add(C115.b(H + e.c + "/static/dark/1.0"));
            pos = end + 1;
        }
        if (pos < cps.length) {
            K59.a(out, cps, pos, cps.length);
        }
        return out;
    }

    private static void a(List<C115> out, int[] cps, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; ++i) {
            if (cps[i] == 32) {
                if (sb.length() <= 0) continue;
                out.add(C115.a(sb.toString()));
                sb.setLength(0);
                continue;
            }
            if (!K59.b(cps[i])) continue;
            sb.appendCodePoint(cps[i]);
        }
        if (sb.length() > 0) {
            out.add(C115.a(sb.toString()));
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
        return K59.e();
    }

    private static float a(float p) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float k = p - 1.0f;
        float v = 1.0f + c3 * k * k * k + c1 * k * k;
        return 0.6f + 0.4f * Math.max(0.0f, Math.min(1.25f, v));
    }

    static boolean h() {
        return K58.f() == 2;
    }

    static boolean a(int p) {
        return switch (p) {
            case 1 -> {
                if (K58.g() == 2) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (K58.h() == 2 || K58.e() >= 0L) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    static boolean i() {
        return K59.a(1) || K59.a(2);
    }

    static long j() {
        long t = 0L;
        boolean any = false;
        if (K58.c() >= 0L) {
            t += K58.c();
            any = true;
        }
        if (K58.d() >= 0L) {
            t += K58.d();
            any = true;
        }
        if (K58.e() >= 0L) {
            t += K58.e();
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

    public static Identifier k() {
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
                return 262.0f * K59.a();
            }

            @Override
            public float c() {
                return (float)K59.g() * K59.a();
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
            public void a(GuiGraphicsExtractor g, float x, float y) {
                K59.a(g, x, y);
            }
        };
    }

    private static final class C113 {
        K58.C109 a;
        String b;
        int c;
        int d;
        int e;
        List<C112> f;
        List<C115> g;
        List<C114> h;
        int i;
        int j;
        float k;

        private C113() {
        }
    }

    private static final class C112 {
        String a;
        int b;
        int c;

        private C112() {
        }
    }

    private static final class C114 {
        int a;
        int b;
        float c;
        float d;
        String e;
        String f;
        int g;
        int h;

        private C114() {
        }
    }

    private static final class C115 {
        boolean a;
        String b;
        String c;

        private C115() {
        }

        static C115 a(String t) {
            C115 u = new C115();
            u.b = t;
            return u;
        }

        static C115 b(String url) {
            C115 u = new C115();
            u.a = true;
            u.c = url;
            return u;
        }
    }
}

