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
        // The now-playing card lives only in the menu; in-game shows just the top
        // lyrics ticker. Keep it visible while the chat is open (pressing T).
        net.minecraft.client.gui.screens.Screen sc = Minecraft.getInstance().gui.screen();
        if (sc == null || sc instanceof net.minecraft.client.gui.screens.ChatScreen) {
            K25.ticker(g);
        }
    }

    // Top-centre karaoke lyrics ticker (like the reference video): time, mini
    // album art, the current line with sung words lit up, and an equalizer.
    private static int tLine = -1;
    private static long tAt = 0L;

    private static String mmss(long ms) {
        if (ms < 0L) ms = 0L;
        long s = ms / 1000L;
        return s / 60L + ":" + String.format("%02d", s % 60L);
    }

    // The font used to draw the karaoke lyric words, from the Music-tab picker.
    public static Identifier lf() {
        return K9.lyricsFont == null || K9.lyricsFont.isEmpty() ? K18.b : K18.a(K9.lyricsFont);
    }

    public static void ticker(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !K9.U || mc.gui.hud.isHidden() || !K37.c()) {
            return;
        }
        if (!K47.d() || !K47.c().a()) {
            return;
        }
        long pos = K47.pos() + (long)K9.aB;
        // Karaoke when the song has synced lyrics and there is a current line;
        // otherwise (no lyrics, or between lines) we show the title + artist.
        boolean hasLyrics = K47.j();
        int line = hasLyrics ? K47.a(pos) : -1;
        String text = line >= 0 ? K47.a(line) : null;
        String[] ws = text != null && !text.isBlank() ? K44.a(text) : null;
        boolean karaoke = ws != null && ws.length > 0;
        long now = System.currentTimeMillis();
        K44 timing = karaoke ? K44.a(K47.k()) : null;
        long lstart = karaoke ? K47.b(line) : 0L;
        long lend = karaoke ? K47.b(line + 1) : 0L;
        if (karaoke && lend <= lstart) {
            lend = lstart + 3000L;
        }
        int accent = K47.c().c().color == 0 ? -11695105 : K47.c().c().color;
        float fs = 8.0f;
        Identifier lfont = K25.lf();
        float space = 0.0f;
        float[] ww = null;
        float lineW = 0.0f;
        if (karaoke) {
            space = K18.a(" ", fs, lfont);
            ww = new float[ws.length];
            for (int i = 0; i < ws.length; ++i) {
                ww[i] = K18.a(ws[i], fs, lfont);
                lineW += ww[i] + (i < ws.length - 1 ? space : 0.0f);
            }
        }
        String time = K25.mmss(pos);
        float timeW = K18.a(time, 7.0f, K18.b);
        int art = 13;
        int eq = 14;
        int pad = 8;
        int gap = 6;
        // FIXED, compact pill - it never grows/shrinks or slides; long lines
        // scale down to fit so the bar stays one constant size.
        float pw = 300.0f;
        float ph = 19.0f;
        float cx = (float)g.guiWidth() / 2.0f;
        float x0 = cx - pw / 2.0f;
        float y0 = 6.0f;
        float cyl = y0 + ph / 2.0f;
        float prevA = K18.h;
        // pill
        K18.a(g, Math.round(x0), Math.round(y0), Math.round(pw), Math.round(ph), 11, -872415232, K18.a(accent, 0.35f));
        K18.a(g, time, x0 + (float)pad, cyl, 7.0f, -5197648, K18.b);
        // mini album art with rounded corners
        int ay = Math.round(y0 + (ph - (float)art) / 2.0f);
        float artX = x0 + (float)pad + timeW + (float)gap;
        Identifier a = K47.n();
        if (a != null) {
            K18.a(g, a, Math.round(artX), ay, art, art, Math.max(1, K47.o()), Math.max(1, K47.p()));
        } else {
            K18.a(g, Math.round(artX), ay, art, art, 4, accent);
        }
        // round the picture's corners by masking them with the pill colour
        // (no visible ring, just rounded corners)
        K18.a(g, Math.round(artX) - 1, ay - 1, art + 2, art + 2, 4, 0, -872415232);
        K18.a(g, Math.round(artX) - 1, ay - 1, art + 2, art + 2, 4, 0, -872415232);
        // karaoke words, centred in the fixed lyric area, scaled to fit
        float lyricLeft = artX + (float)art + (float)gap;
        float lyricRight = x0 + pw - (float)pad - (float)eq - (float)gap;
        float lyricArea = Math.max(40.0f, lyricRight - lyricLeft);
        if (karaoke) {
            float lscale = lineW > lyricArea && lineW > 0.0f ? lyricArea / lineW : 1.0f;
            float drawW = lineW * lscale;
            float x = lyricLeft + (lyricArea - drawW) / 2.0f;
            for (int i = 0; i < ws.length; ++i) {
                long wt = timing != null && line < timing.a.length && i < timing.a[line].length ? timing.a[line][i] : lstart + (long)((float)i / (float)ws.length * (float)(lend - lstart));
                long wnext = timing != null && line < timing.a.length && i + 1 < timing.a[line].length ? timing.a[line][i + 1] : wt + 400L;
                boolean sung = pos >= wt;
                boolean active = pos >= wt && pos < wnext;
                int col = active ? accent : (sung ? -1 : -8092540);
                // active word bounces up and pops slightly
                float prog = active && wnext > wt ? (float)(pos - wt) / (float)(wnext - wt) : 0.0f;
                float bounce = active ? -2.5f * (float)Math.sin((double)prog * 3.1416) : 0.0f;
                float wfs = fs * lscale * (active ? 1.0f + 0.14f * (float)Math.sin((double)prog * 3.1416) : 1.0f);
                K18.a(g, ws[i], x, cyl + bounce, wfs, col, lfont);
                x += (ww[i] + space) * lscale;
            }
        } else {
            // no synced lyrics: show "Title · Artist", scaled to fit, gently pulsing
            String title = K47.c().d() == null ? "" : K47.c().d();
            String artist = K47.c().e() == null ? "" : K47.c().e();
            float tfs = 8.5f;
            String sep = artist.isEmpty() ? "" : "  ·  ";
            float tW = K18.a(title, tfs, K18.c);
            float sW = K18.a(sep + artist, tfs, K18.b);
            float totW = Math.max(1.0f, tW + sW);
            float tscale = totW > lyricArea ? lyricArea / totW : 1.0f;
            float drawW = totW * tscale;
            float tx = lyricLeft + (lyricArea - drawW) / 2.0f;
            float pulse = 0.82f + 0.18f * (float)Math.sin((double)(now % 2200L) / 2200.0 * 6.2832);
            K18.a(g, title, tx, cyl, tfs * tscale, K18.a(-1, pulse), K18.c);
            if (!artist.isEmpty()) {
                K18.a(g, sep + artist, tx + tW * tscale, cyl, tfs * tscale, -7695450, K18.b);
            }
        }
        // equalizer
        float ex = x0 + pw - (float)pad - (float)eq;
        for (int i = 0; i < 4; ++i) {
            float phase = (float)(now % 1000L) / 1000.0f * 6.2832f + (float)i * 1.3f;
            int bh = Math.round(3.0f + 7.0f * (0.5f + 0.5f * (float)Math.sin(phase)));
            K18.a(g, Math.round(ex + (float)(i * 4)), Math.round(cyl + 5.0f - (float)bh), 2, bh, accent);
        }
        K18.h = prevA;
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
        K18.a(g, (float)ax + 4.0f, 92.0f, 3.5f, accent);
        K18.a(g, t.c().label.isEmpty() ? "Music" : t.c().label, (float)ax + 11.0f, 92.0f, 9.0f, -7695450, K18.b);
        long dur = t.h();
        int rowY = 92;
        int barL = tx + 22;
        int barR = 258 - (dur > 0L ? 28 : 0);
        int bw = Math.max(8, barR - barL);
        K18.a(g, barL, rowY - 1, bw, 3, 1, 0x33FFFFFF);
        if (dur > 0L) {
            long pos = K47.pos();
            float frac = Math.max(0.0f, Math.min(1.0f, (float)pos / (float)dur));
            K18.a(g, K25.a(pos), (float)tx, (float)rowY, 7.5f, -7695450, K18.b);
            if (frac > 0.0f) {
                K18.a(g, barL, rowY - 1, Math.max(2, Math.round((float)bw * frac)), 3, 1, accent);
                K18.a(g, (float)barL + (float)bw * frac, (float)rowY + 0.5f, 2.6f, -1);
            }
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

