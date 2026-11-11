/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K37;
import com.lethalauction.m.K41;
import com.lethalauction.m.K44;
import com.lethalauction.m.K47;
import com.lethalauction.m.K51;
import com.lethalauction.m.K54;
import com.lethalauction.m.K9;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class K53 {
    private static final float[][] a = new float[][]{{-22.0f, 7.0f}, {20.0f, -4.0f}, {-6.0f, -12.0f}, {16.0f, 12.0f}, {-26.0f, -8.0f}, {8.0f, 4.0f}, {-14.0f, 14.0f}, {26.0f, 6.0f}, {2.0f, -16.0f}, {-18.0f, 0.0f}, {12.0f, -10.0f}, {-8.0f, 9.0f}};
    private static final float b = 0.062f;
    private static final long c = -200L;
    private static final int[] d = new int[]{13220607, 0xFFFFFF, 16751512, 9098239, 0x8CFFC8, 16767162, 0xFF7A7A, 11640319};
    private static final List<C104> e = new ArrayList<C104>();
    private static final Set<Integer> f = new HashSet<Integer>();
    private static final Random g = new Random();
    private static String h = "";
    private static long i = Long.MIN_VALUE;
    private static int j = -1;
    private static int k = -1;
    private static int l = -1;
    private static int m = -1;
    private static final Map<String, Component> n = new HashMap<String, Component>();
    private static final Matrix4f o = new Matrix4f();
    private static final Matrix4f p = new Matrix4f();
    private static final Matrix4f q = new Matrix4f();
    private static final Matrix4f r = new Matrix4f();

    public static int a() {
        return d.length;
    }

    public static int a(int i) {
        return d[Math.floorMod(i, d.length)];
    }

    static int b() {
        int i = K9.aC;
        return i >= 0 && i < d.length ? d[i] : 0xFFFFFF;
    }

    private K53() {
    }

    public static void a(LevelRenderContext ctx) {
        try {
            K53.b(ctx);
        }
        catch (Throwable ignored) {
            e.clear();
        }
        finally {
            K51.a.a(ctx.submitNodeCollector());
        }
    }

    public static void c() {
        e.clear();
        f.clear();
        j = -1;
        k = -1;
    }

    private static void b(LevelRenderContext ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (!K9.ay || !K9.U || !K37.c() || mc.gui.hud.isHidden() || mc.player == null || mc.level == null) {
            K53.c();
            return;
        }
        Matrix4f base = new Matrix4f((Matrix4fc)ctx.poseStack().last().pose());
        Vec3 cam = mc.gameRenderer.mainCamera().position();
        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        boolean see = !mc.level.canSeeSky(mc.player.blockPosition().above());
        long now = System.currentTimeMillis();
        boolean active = false;
        if (K47.d() && K47.j() && K47.c().a()) {
            long pos;
            String id = K47.c().j();
            if (!id.equals(h)) {
                h = id;
                e.clear();
                f.clear();
                j = -1;
                k = -1;
            }
            if ((pos = K47.c().b() + (long)K9.aB + -200L) < i - 3000L) {
                f.clear();
            }
            i = pos;
            int line = K47.a(pos);
            if (line >= 0 && line < K47.l() && pos <= K53.c(line) + 450L) {
                active = K53.a(mc, line, pos, cam, pt, now);
            }
        }
        if (!active) {
            for (C104 w2 : e) {
                if (w2.n != 0L) continue;
                w2.n = now;
            }
        }
        e.removeIf(w -> w.n != 0L && (float)(now - w.n) > 2200.0f);
        K53.a(mc, base, cam, pt, now, 2200.0f, see);
    }

    private static long c(int i) {
        long t = K47.b(i + 1);
        return t >= 0L ? t : K47.b(i) + 3000L;
    }

    private static boolean a(Minecraft mc, int line, long pos, Vec3 cam, float pt, long now) {
        String text = K47.a(line);
        if (text == null || text.isBlank()) {
            return false;
        }
        String[] ws = K44.a(text);
        if (ws.length == 0) {
            return false;
        }
        j = line;
        int[] sizes = K53.b(ws.length);
        int nc = sizes.length;
        int[] startIdx = new int[nc];
        int c = 0;
        for (int k = 0; k < nc; ++k) {
            startIdx[k] = c;
            c += sizes[k];
        }
        K44 timing = K44.a(K47.k());
        long lstart = K47.b(line);
        long lend = Math.max(lstart + 1L, K53.c(line));
        for (int k = 0; k < nc; ++k) {
            int key;
            long ct = timing != null && line < timing.a.length && startIdx[k] < timing.a[line].length ? timing.a[line][startIdx[k]] : lstart + (long)((float)startIdx[k] / (float)ws.length * (float)(lend - lstart));
            if (ct > pos || ct < pos - 1400L || !f.add(key = line * 64 + k)) continue;
            K53.a(K53.a(ws, startIdx[k], startIdx[k] + sizes[k]), mc, cam, pt, now);
        }
        return true;
    }

    static int[] b(int n) {
        int take;
        if (n <= 0) {
            return new int[0];
        }
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < n; i += take) {
            int rem = n - i;
            take = rem <= 4 ? rem : 3;
            out.add(take);
        }
        int[] a = new int[out.size()];
        for (int k = 0; k < a.length; ++k) {
            a[k] = (Integer)out.get(k);
        }
        return a;
    }

    private static String a(String[] ws, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to && i < ws.length; ++i) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(K41.a(ws[i]));
        }
        return sb.toString();
    }

    private static float d() {
        return K9.aD * 0.94f;
    }

    private static void a(String str, Minecraft mc, Vec3 cam, float pt, long now) {
        double nz;
        double nx;
        if (str == null || str.isEmpty()) {
            return;
        }
        float yaw = mc.player.getViewYRot(pt);
        float pitch = -mc.player.getViewXRot(pt) + 12.0f;
        int pxW = K53.b(str);
        double best = Double.MAX_VALUE;
        double bYaw = 0.0;
        double bPitch = 0.0;
        double bDist = 8.0;
        double bScale = 1.0;
        double bHalfW = 1.0;
        double bHalfH = 1.0;
        int bSlot = 0;
        for (int it = 0; it < 28; ++it) {
            double halfH;
            int slot;
            int guard = 0;
            while (((slot = g.nextInt(a.length)) == l || slot == m) && ++guard < 12) {
            }
            double sy = Math.toRadians(yaw + (a[slot][0] + (g.nextFloat() - 0.5f) * 7.0f));
            double sp = Math.toRadians(pitch + (a[slot][1] + (g.nextFloat() - 0.5f) * 6.0f));
            double dist = 6.5 + g.nextDouble() * 8.5;
            float scale = 0.82f + g.nextFloat() * 0.5f;
            double halfW = (double)((float)pxW * 0.062f * K53.d() * scale) * 0.5 * 0.95;
            double score = K53.a(sy, sp, dist, halfW, halfH = (double)(0.682f * K53.d() * scale) * 0.5, cam);
            if (score < best) {
                best = score;
                bYaw = sy;
                bPitch = sp;
                bDist = dist;
                bScale = scale;
                bHalfW = halfW;
                bHalfH = halfH;
                bSlot = slot;
            }
            if (score <= 0.0) break;
        }
        m = l;
        l = bSlot;
        double dx = -Math.sin(bYaw) * Math.cos(bPitch);
        double dy = Math.sin(bPitch);
        double dz = Math.cos(bYaw) * Math.cos(bPitch);
        double x = cam.x + dx * bDist;
        double y = cam.y + dy * bDist;
        double z = cam.z + dz * bDist;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        if (horiz < 1.0E-4) {
            double a = Math.toRadians(yaw);
            nx = Math.sin(a);
            nz = -Math.cos(a);
        } else {
            nx = -dx / horiz;
            nz = -dz / horiz;
        }
        float tilt = (float)Math.toRadians((float)(g.nextBoolean() ? 1 : -1) * (10.0f + g.nextFloat() * 22.0f));
        float pitchR = (float)Math.toRadians((g.nextFloat() - 0.5f) * 14.0f);
        float roll = (float)Math.toRadians((g.nextFloat() - 0.5f) * 8.0f);
        for (C104 wrd : e) {
            if (wrd.n != 0L) continue;
            wrd.n = now;
        }
        e.add(new C104(str, x, y, z, nx, nz, tilt, pitchR, roll, (float)bScale, (float)bHalfW, (float)bHalfH, now));
        while (e.size() > 7) {
            e.remove(0);
        }
    }

    private static double a(double yaw, double pitch, double dist, double halfW, double halfH, Vec3 cam) {
        double sum = 0.0;
        double aw = halfW / dist;
        double ah = halfH / dist;
        for (C104 wrd : e) {
            double dx = wrd.b - cam.x;
            double dy = wrd.c - cam.y;
            double dz = wrd.d - cam.z;
            double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (d < 0.5) continue;
            double wy = Math.atan2(-dx, dz);
            double wp = Math.asin(Math.max(-1.0, Math.min(1.0, dy / d)));
            double ddy = Math.abs(((yaw - wy) % (Math.PI * 2) + Math.PI * 3) % (Math.PI * 2) - Math.PI) * Math.cos(pitch);
            double ddp = Math.abs(pitch - wp);
            double limW = aw + (double)wrd.k / d + 0.035;
            double limH = ah + (double)wrd.l / d + 0.03;
            if (!(ddy < limW) || !(ddp < limH)) continue;
            sum += 1.0 - ddy / limW + (1.0 - ddp / limH) + 0.5;
        }
        return sum;
    }

    private static float a(float f) {
        return f < 0.0f ? 0.0f : Math.min(f, 1.0f);
    }

    private static float b(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g;
    }

    private static void a(Minecraft mc, Matrix4f base, Vec3 cam, float pt, long now, float gone, boolean see) {
        if (e.isEmpty()) {
            return;
        }
        Font font = mc.font;
        int color = K53.b();
        int quality = 3;
        Font.DisplayMode mode = see ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
        float beat = 0.0f;
        double viewYaw = Math.toRadians(mc.player.getViewYRot(pt));
        double viewPitch = Math.toRadians(mc.player.getViewXRot(pt));
        double fwdVX = -Math.sin(viewYaw) * Math.cos(viewPitch);
        double fwdVY = -Math.sin(viewPitch);
        double fwdVZ = Math.cos(viewYaw) * Math.cos(viewPitch);
        for (C104 wrd : e) {
            double d6;
            boolean alive;
            long age = now - wrd.m;
            float a = K53.a((float)age / 70.0f);
            float shrink = 1.0f;
            boolean bl = alive = wrd.n == 0L;
            if (!alive) {
                float g = (float)(now - wrd.n) / gone;
                if (g >= 1.0f) continue;
                a *= 1.0f - g * g;
                shrink = 1.0f - 0.5f * K53.b(K53.a((float)(now - wrd.n) / 240.0f));
            }
            double rise = Math.min((double)age / 1000.0 * 0.25, 1.0);
            double dx = wrd.b - cam.x;
            double dy = wrd.c + rise - cam.y;
            double dz = wrd.d - cam.z;
            if (a < 0.03f || dx * fwdVX + dy * fwdVY + dz * fwdVZ < 0.3) continue;
            double ct = Math.cos(wrd.g);
            double st = Math.sin(wrd.g);
            double d13 = wrd.e * ct - wrd.f * st;
            double aX = d6 = wrd.e * st + wrd.f * ct;
            double aY = 0.0;
            double aZ = -d13;
            double bX = 0.0;
            double bY = 1.0;
            double bZ = 0.0;
            double cX = d13;
            double cY = 0.0;
            double cZ = d6;
            double cp = Math.cos(wrd.h);
            double sp = Math.sin(wrd.h);
            double bpX = bX * cp + cX * sp;
            double bpY = bY * cp + cY * sp;
            double bpZ = bZ * cp + cZ * sp;
            double cpX = cX * cp - bX * sp;
            double cpY = cY * cp - bY * sp;
            double cpZ = cZ * cp - bZ * sp;
            double cr = Math.cos(wrd.i);
            double sr = Math.sin(wrd.i);
            double rX = aX * cr + bpX * sr;
            double rY = aY * cr + bpY * sr;
            double rZ = aZ * cr + bpZ * sr;
            double uX = bpX * cr - aX * sr;
            double uY = bpY * cr - aY * sr;
            double uZ = bpZ * cr - aZ * sr;
            if (dx * cpX + dy * cpY + dz * cpZ > 0.0) {
                rX = -rX;
                rY = -rY;
                rZ = -rZ;
                cpX = -cpX;
                cpY = -cpY;
                cpZ = -cpZ;
            }
            float pop = 1.0f + 0.22f * (1.0f - K53.b(K53.a((float)age / 200.0f)));
            if (alive) {
                pop += 0.06f * beat;
            }
            float s = 0.062f * K53.d() * wrd.j * pop;
            Matrix4f m = o.set((Matrix4fc)base).mul((Matrix4fc)p.set((float)rX * s, (float)rY * s, (float)rZ * s, 0.0f, (float)(-uX) * s, (float)(-uY) * s, (float)(-uZ) * s, 0.0f, (float)cpX * s, (float)cpY * s, (float)cpZ * s, 0.0f, (float)dx, (float)dy, (float)dz, 1.0f));
            String str = wrd.a;
            int fullW = K53.b(str);
            int reveal = Math.max(1, (int)Math.ceil((float)str.length() * K53.a((float)age / 85.0f)));
            Component comp = K53.c(str.substring(0, Math.min(str.length(), reveal)));
            Matrix4f wm = q.set((Matrix4fc)m);
            wm.translate((float)(-fullW) / 2.0f, -4.5f, 0.0f);
            int main = K18.a(9211040, color, shrink);
            int glowCol = K18.a(9211040, color, shrink);
            K53.a(font, wm, comp, 0.7f, 0.9f, -0.5f, 328712, a * 0.55f, mode);
            float gstr = a * shrink * 0.2f * (alive ? 1.3f : 0.8f);
            for (int i = 0; i < quality; ++i) {
                double ang = (double)i * Math.PI * 2.0 / (double)quality + 0.3927;
                K53.a(font, wm, comp, (float)Math.cos(ang) * 1.1f, (float)Math.sin(ang) * 1.1f, 0.1f, glowCol, gstr * 6.0f / (float)quality, mode);
            }
            K53.a(font, wm, comp, 0.0f, 0.0f, 0.3f, main, a, mode);
        }
    }

    private static void a(Font font, Matrix4f m, Component comp, float x, float y, float z, int rgb, float alpha, Font.DisplayMode mode) {
        if (alpha <= 0.03f) {
            return;
        }
        Matrix4f mm = r.set((Matrix4fc)m).translate(x, y, z);
        int argb = Math.round(Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f) << 24 | rgb & 0xFFFFFF;
        K54.a(font, comp, 0.0f, 0.0f, argb, false, mm, K51.a, mode, 0xF000F0);
    }

    private static int b(String s) {
        return Minecraft.getInstance().font.width((FormattedText)K53.c(s));
    }

    private static Component c(String s) {
        if (n.size() > 600) {
            n.clear();
        }
        return n.computeIfAbsent(s, k -> Component.literal((String)K53.a(k)).withStyle(st -> st.withFont((FontDescription)new FontDescription.Resource(K18.e))));
    }

    static String a(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length());
        block7: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '`': 
                case '\u00b4': 
                case '\u02bc': 
                case '\u2018': 
                case '\u2019': 
                case '\u2032': {
                    b.append('\'');
                    continue block7;
                }
                case '\u201c': 
                case '\u201d': 
                case '\u2033': {
                    b.append('\"');
                    continue block7;
                }
                case '\u2010': 
                case '\u2011': 
                case '\u2012': 
                case '\u2013': 
                case '\u2014': 
                case '\u2015': {
                    b.append('-');
                    continue block7;
                }
                case '\u2026': {
                    b.append("...");
                    continue block7;
                }
                case '\u00a0': 
                case '\u2007': 
                case '\u202f': {
                    b.append(' ');
                    continue block7;
                }
                default: {
                    b.append(c);
                }
            }
        }
        return b.toString();
    }

    static float a(long pos, long lineStart) {
        long period = 480L;
        float ph = (float)Math.floorMod(pos - lineStart, period) / (float)period;
        return (float)Math.pow(1.0 - (double)ph, 2.4);
    }

    private static final class C104 {
        final String a;
        final double b;
        final double c;
        final double d;
        final double e;
        final double f;
        final float g;
        final float h;
        final float i;
        final float j;
        final float k;
        final float l;
        final long m;
        long n = 0L;

        C104(String text, double x, double y, double z, double nx, double nz, float tilt, float pitch, float roll, float size, float halfW, float halfH, long born) {
            this.a = text;
            this.b = x;
            this.c = y;
            this.d = z;
            this.e = nx;
            this.f = nz;
            this.g = tilt;
            this.h = pitch;
            this.i = roll;
            this.j = size;
            this.k = halfW;
            this.l = halfH;
            this.m = born;
        }
    }
}

