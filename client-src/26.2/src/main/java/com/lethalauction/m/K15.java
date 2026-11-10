/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K18;
import com.lethalauction.m.K32;
import com.lethalauction.m.K37;
import java.util.Locale;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

public class K15
implements HudElement {
    private static final int a = 300;
    private static final float b = 0.62f;
    private static final long c = 550L;
    private static final int d = 10;
    private static final int e = -45730;
    private static final int f = -10934;
    private static final int g = -12197750;
    private static final int h = 140;
    private static final long i = 5000L;
    private static final int[] j = new int[]{-45683, -10934, -12197750, -12604929, -4952833, -1, -30147};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        if (!K37.b()) {
            return;
        }
        Auctions.C66 a = Auctions.c();
        if (a != null) {
            float k;
            long since;
            boolean won;
            long now = System.currentTimeMillis();
            float t = (float)(now % 3600000L) / 1000.0f;
            boolean bl = won = a.ended() && a.hasBid();
            if (won) {
                this.a(g, now - a.endsAt(), a.endsAt());
            }
            Matrix3x2fStack p = g.pose();
            p.pushMatrix();
            float x = ((float)g.guiWidth() - 186.0f) / 2.0f;
            long secs = a.secondsLeft();
            long msLeft = a.ended() ? 0L : a.endsAt() - now;
            boolean low = !a.ended() && secs <= 10L;
            float shake = 0.0f;
            if (!a.ended()) {
                if (msLeft <= 10000L && msLeft > 9000L) {
                    shake = 3.5f * (float)(msLeft - 9000L) / 1000.0f;
                } else if (msLeft <= 5000L) {
                    shake = 0.5f + 0.7f * (1.0f - (float)msLeft / 5000.0f);
                }
            }
            x += (float)(Math.sin(t * 55.0f) * (double)shake) * 0.62f * 2.2f;
            float y = 5.0f + (float)(Math.cos(t * 47.0f) * (double)shake * (double)0.8f) * 0.62f;
            float rot = (float)(Math.sin(t * 38.0f) * (double)shake * (double)0.006f);
            float alpha = 1.0f;
            float pop = 1.0f;
            if (!a.ended() && (since = now - a.startedAt()) < 550L) {
                k = Math.max(0.0f, (float)since / 550.0f);
                y -= (1.0f - K15.a(k)) * 70.0f * 0.62f;
                alpha = Math.min(1.0f, k * 2.2f);
                pop = 0.85f + 0.15f * K15.a(k);
            }
            if (a.ended()) {
                since = now - a.endsAt();
                k = Math.min(1.0f, (float)since / 250.0f);
                y += (1.0f - K15.a(k)) * -6.0f;
                alpha = Math.max(0.0f, Math.min(1.0f, (float)(6000L - since) / 900.0f));
                y -= (1.0f - alpha) * 16.0f;
            }
            p.translate(x + 93.0f, y);
            p.rotate(rot);
            p.scale(pop);
            p.translate(-93.0f, 0.0f);
            p.scale(0.62f);
            K18.h = alpha;
            try {
                if (a.ended()) {
                    this.a(g, a, t, now, won, alpha);
                } else {
                    this.a(g, a, t, secs, low);
                }
            }
            finally {
                K18.h = 1.0f;
                p.popMatrix();
            }
        }
    }

    private void a(GuiGraphicsExtractor g, Auctions.C66 a, float t, long secs, boolean low) {
        int accent = K32.a();
        int h = 106;
        int border = low ? K18.a(-45730, -16777216, 0.15f) : K18.a(-14014925, accent, 0.45f);
        K18.a(g, -2, -2, 304, h + 4, 14, K18.a(low ? -45730 : accent, low ? 0.22f + 0.12f * K15.a(t, 4.0f) : 0.18f));
        K18.a(g, 0, 0, 300, h, 12, -267448811, border);
        this.a(g, a, t, 1.0f);
        String title = a.hasBid() ? a.topBidder() : "No bids";
        K18.a(g, title, 72.0f, 22.0f, 15.0f, -789515, K18.c);
        float px = 72.0f;
        if (a.hasBid() && !a.restart()) {
            px += K15.a(g, px, "Bid " + K15.a(a.topBid()), K32.c(), -1) + 6.0f;
        }
        String min = a.restart() ? "Pay " + K15.a(a.minimumBid()) + " \u00b7 last wins" : (a.minimumBid() > 0L ? "Min " + K15.a(a.minimumBid()) : "No minimum");
        K15.a(g, px, min, -14738137, -3158568);
        String time = Auctions.b(secs);
        int timerBorder = low ? -45730 : K18.a(-13356484, accent, 0.25f);
        K18.a(g, 220, 22, 70, 30, 15, low ? -14020844 : -15395813, timerBorder);
        K18.b(g, time, 255.0f, 37.5f, 15.0f, low ? -45730 : -1, K18.c);
        int light = K32.d();
        String note = a.note();
        float noteEnd = 10.0f;
        if (!note.isBlank()) {
            K18.a(g, 15.0f, 75.0f, 2.5f, accent);
            K18.a(g, note, 22.0f, 75.0f, 11.0f, light, K18.c);
            noteEnd = 22.0f + K18.a(note, 11.0f, K18.c);
        }
        if (a.totalWorth() > 0L) {
            String worth = "Worth " + K15.a(a.totalWorth());
            float wx = noteEnd + 12.0f;
            int ww = Math.round(K18.a(worth, 11.0f, K18.c) + 18.0f);
            K18.a(g, Math.round(wx), 66, ww, 18, 9, K32.c());
            K18.b(g, worth, wx + (float)ww / 2.0f, 75.0f, 11.0f, -1, K18.c);
        }
        float frac = a.progressLeft();
        K18.a(g, 10, 88, 280, 5, 2, -14409172);
        int fw = Math.round(280.0f * frac);
        if (fw > 0) {
            K18.a(g, 10, 88, Math.max(4, fw), 5, 2, low ? -45730 : accent);
        }
        K18.b(g, "Lethal Client", 150.0f, 100.0f, 9.0f, -9541000, K18.b);
    }

    private void a(GuiGraphicsExtractor g, Auctions.C66 a, float t, long now, boolean won, float alpha) {
        int accent = K32.a();
        int h = 72;
        if (won) {
            int glow = K18.a(accent, -10934, 0.5f + 0.5f * K15.a(t, 3.0f));
            K18.a(g, -3, -3, 306, h + 6, 15, K18.a(glow, 0.28f));
            K18.a(g, 0, 0, 300, h, 12, -267448811, glow);
        } else {
            K18.a(g, 0, 0, 300, h, 12, -267448811, K18.a(-14014925, accent, 0.3f));
        }
        this.a(g, a, t, alpha);
        if (won) {
            long since = now - a.endsAt();
            this.a(g, a.topBidder(), 74.0f, 26.0f, t, since);
            float subIn = Math.max(0.0f, Math.min(1.0f, (float)(since - 250L) / 300.0f));
            float bounce = (float)Math.sin(t * 5.0f) * 1.2f;
            K18.a(g, "won for " + K15.a(a.topBid()), 74.0f + (1.0f - subIn) * 12.0f, 51.0f + bounce, 13.0f, K18.a(-12197750, subIn), K18.c);
        } else {
            K18.a(g, "Auction ended", 74.0f, 26.0f, 15.0f, -789515, K18.c);
            K18.a(g, "No winner", 74.0f, 50.0f, 13.0f, -6513498, K18.b);
        }
    }

    private void a(GuiGraphicsExtractor g, String name, float x, float cy, float t, long sinceEnd) {
        float size = 18.0f;
        int light = K32.d();
        Matrix3x2fStack p = g.pose();
        float cx = x;
        for (int i = 0; i < name.length(); ++i) {
            String ch = String.valueOf(name.charAt(i));
            float charW = K18.a(ch, size, K18.c);
            float appear = Math.max(0.0f, Math.min(1.0f, (float)(sinceEnd - (long)(i * 55)) / 260.0f));
            if (appear > 0.0f) {
                float wave = (float)Math.sin((double)(t * 7.0f) - (double)i * 0.7) * 4.5f;
                float pop = (1.0f - K15.a(appear)) * 14.0f;
                float scale = 1.0f + 0.2f * (float)Math.sin((double)(t * 6.0f) - (double)i * 0.7);
                float shimmer = 0.5f + 0.5f * (float)Math.sin((double)(t * 5.0f) - (double)i * 0.6);
                int color = K18.a(K18.a(-10934, light, shimmer), appear);
                p.pushMatrix();
                p.translate(cx + charW / 2.0f, cy + wave + pop);
                p.scale(scale);
                K18.a(g, ch, -charW / 2.0f, 0.0f, size, color, K18.c);
                p.popMatrix();
            }
            cx += charW;
        }
    }

    private void a(GuiGraphicsExtractor g, Auctions.C66 a, float t, float alpha) {
        K18.a(g, 10, 10, 52, 52, 10, -15987951, -14014925);
        float bob = (float)Math.sin((double)t * 2.4) * 5.5f;
        float tilt = (float)Math.sin((double)t * 1.7) * 0.12f;
        float size = 2.1f * (0.55f + 0.45f * alpha);
        K18.a(g, 24 + Math.round(bob * 0.4f), 55, 24 - Math.round(bob * 0.8f), 3, 1, K18.a(-16777216, 0.4f - bob * 0.03f));
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(36.0f, 34.0f + bob);
        p.rotate(tilt);
        p.scale(size);
        if (alpha > 0.05f) {
            if (a.hasSecondItem()) {
                p.scale(0.62f);
                g.item(a.item(), -16, -16);
                g.item(a.item2(), 0, 0);
            } else {
                g.item(a.item(), -8, -8);
            }
        }
        p.popMatrix();
        if (a.hasSecondItem()) {
            if (a.quantity() > 1) {
                K18.a(g, "x" + a.quantity(), 13.0f, 20.0f, 9.0f, -1, K18.c);
            }
            if (a.quantity2() > 1) {
                K18.c(g, "x" + a.quantity2(), 59.0f, 55.0f, 9.0f, -1, K18.c);
            }
        } else if (a.quantity() > 1) {
            K18.c(g, "x" + a.quantity(), 58.0f, 55.0f, 10.5f, -1, K18.c);
        }
    }

    private static float a(GuiGraphicsExtractor g, float x, String text, int fill, int color) {
        int w = Math.round(K18.a(text, 12.0f, K18.b) + 22.0f);
        K18.a(g, Math.round(x), 35, w, 22, 11, fill);
        K18.b(g, text, x + (float)w / 2.0f, 46.5f, 12.0f, color, K18.b);
        return w;
    }

    private void a(GuiGraphicsExtractor g, long sinceEnd, long seed) {
        if (sinceEnd <= 5000L) {
            float time = (float)sinceEnd / 1000.0f;
            float fade = Math.min(1.0f, (float)(5000L - sinceEnd) / 1000.0f);
            int gw = g.guiWidth();
            int gh = g.guiHeight();
            Random r = new Random(seed);
            Matrix3x2fStack p = g.pose();
            for (int i = 0; i < 140; ++i) {
                boolean left = i % 2 == 0;
                float x0 = left ? -10.0f : (float)(gw + 10);
                float y0 = (float)gh * (0.15f + r.nextFloat() * 0.35f);
                float vx = (float)(left ? 1 : -1) * ((float)gw * (0.25f + r.nextFloat() * 0.45f));
                float vy = (float)(-gh) * (0.35f + r.nextFloat() * 0.5f);
                float drag = 1.6f + r.nextFloat();
                float delay = r.nextFloat() * 0.35f;
                float spin = (r.nextFloat() - 0.5f) * 14.0f;
                int color = j[r.nextInt(j.length)];
                float w = 2.0f + r.nextFloat() * 2.0f;
                float h = 3.0f + r.nextFloat() * 3.0f;
                float tt = time - delay;
                if (tt <= 0.0f) continue;
                float damp = (1.0f - (float)Math.exp(-drag * tt)) / drag;
                float px = x0 + vx * damp + (float)Math.sin(tt * 5.0f + (float)i) * 6.0f;
                float py = y0 + vy * damp + 0.5f * (float)gh * 0.9f * tt * tt;
                if (py > (float)(gh + 10)) continue;
                p.pushMatrix();
                p.translate(px, py);
                p.rotate(spin * tt);
                p.scale(1.0f, (float)Math.abs(Math.cos(tt * 7.0f + (float)i)) * 0.8f + 0.2f);
                K18.a(g, Math.round(-w / 2.0f), Math.round(-h / 2.0f), Math.max(1, Math.round(w)), Math.max(1, Math.round(h)), K18.a(color, fade));
                p.popMatrix();
            }
        }
    }

    private static float a(float t, float speed) {
        return 0.5f + 0.5f * (float)Math.sin(t * speed);
    }

    private static float a(float k) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float)Math.pow(k - 1.0f, 3.0) + c1 * (float)Math.pow(k - 1.0f, 2.0);
    }

    static String a(long amount) {
        return Auctions.a(amount).substring(1).toUpperCase(Locale.ROOT);
    }
}

