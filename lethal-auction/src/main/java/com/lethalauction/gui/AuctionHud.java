package com.lethalauction.gui;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Auctions.Auction;
import java.util.Locale;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

/** The live auction card shown at the top of the screen, plus the winner celebration. */
public class AuctionHud implements HudElement {
    private static final int W = 300;
    private static final float SCALE = 0.62f;
    private static final int LOW_TIME = 10;
    private static final int RED = 0xFFFF4D5E;
    private static final int GOLD = 0xFFFFD54A;
    private static final int GREEN = 0xFF45E08A;

    private static final int CONFETTI = 140;
    private static final long CONFETTI_MILLIS = 5000;
    private static final int[] CONFETTI_COLORS = {0xFFFF4D8D, 0xFFFFD54A, 0xFF45E08A, 0xFF3FA9FF, 0xFFB46CFF, 0xFFFFFFFF, 0xFFFF8A3D};

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        Auction a = Auctions.current();
        if (a == null) {
            return;
        }
        long now = System.currentTimeMillis();
        // Seconds as a small number: a float of the full epoch time only changes every ~2 minutes.
        float t = (now % 3_600_000L) / 1000f;
        boolean won = a.ended() && a.hasBid();
        if (won) {
            drawConfetti(g, now - a.endsAt(), a.endsAt());
        }

        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        float x = (g.guiWidth() - W * SCALE) / 2f;
        long secs = a.secondsLeft();
        long msLeft = a.ended() ? 0 : a.endsAt() - now;
        boolean low = !a.ended() && secs <= LOW_TIME;
        // One short shake when 10 seconds are left, then constant shaking for the last 5 seconds.
        float shake = 0;
        if (!a.ended()) {
            if (msLeft <= 10_000 && msLeft > 9_000) {
                shake = 3.5f * (msLeft - 9_000) / 1000f;
            } else if (msLeft <= 5_000) {
                shake = 0.5f + 0.7f * (1 - msLeft / 5000f);
            }
        }
        x += (float) (Math.sin(t * 55) * shake) * SCALE * 2.2f;
        float y = 5 + (float) (Math.cos(t * 47) * shake * 0.8f) * SCALE;
        float rot = (float) (Math.sin(t * 38) * shake * 0.006f);
        float alpha = 1;
        if (a.ended()) {
            long since = now - a.endsAt();
            float k = Math.min(1, since / 250f);
            y += (1 - easeOutBack(k)) * -6;
            // fade and slide away once the celebration is over
            alpha = Math.max(0, Math.min(1, (Auctions.RESULT_MILLIS - since) / 900f));
            y -= (1 - alpha) * 16;
        }
        p.translate(x + W * SCALE / 2f, y);
        p.rotate(rot);
        p.translate(-W * SCALE / 2f, 0);
        p.scale(SCALE);

        Draw.globalAlpha = alpha;
        try {
            if (a.ended()) {
                drawResult(g, a, t, now, won, alpha);
            } else {
                drawLive(g, a, t, secs, low);
            }
        } finally {
            Draw.globalAlpha = 1f;
            p.popMatrix();
        }
    }

    // ---------------------------------------------------------------- running auction

    private void drawLive(GuiGraphicsExtractor g, Auction a, float t, long secs, boolean low) {
        int accent = Theme.accent();
        int h = 106;
        int border = low ? Draw.lerpColor(RED, 0xFF000000, 0.15f) : Draw.lerpColor(0xFF2A2633, accent, 0.45f);
        Draw.round(g, -2, -2, W + 4, h + 4, 14, Draw.alpha(low ? RED : accent, low ? 0.22f + 0.12f * pulse(t, 4) : 0.18f));
        Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, border);

        floatingItem(g, a, t, 1f);

        String title = a.hasBid() ? a.topBidder() : "No bids";
        Draw.text(g, title, 72, 22, 15, 0xFFF3F3F5, Draw.SEMIBOLD);
        float px = 72;
        if (a.hasBid()) {
            px += pill(g, px, "Bid " + compact(a.topBid()), Theme.accentDark(), 0xFFFFFFFF) + 6;
        }
        String min = a.minimumBid() > 0 ? "Min " + compact(a.minimumBid()) : "No minimum";
        pill(g, px, min, 0xFF1F1D27, 0xFFCFCDD8);

        String time = String.format(Locale.ROOT, "%02d:%02d", secs / 60, secs % 60);
        int timerBorder = low ? RED : Draw.lerpColor(0xFF34323C, accent, 0.25f);
        Draw.roundBordered(g, 220, 22, 70, 30, 15, low ? 0xFF2A0F14 : 0xFF15141B, timerBorder);
        Draw.textCentered(g, time, 255, 37.5f, 15, low ? RED : 0xFFFFFFFF, Draw.SEMIBOLD);

        int light = Theme.accentLight();
        Draw.circle(g, 15, 75, 2.5f, accent);
        Draw.text(g, "Pay over = I keep", 22, 75, 11, light, Draw.SEMIBOLD);
        if (a.worthEach() > 0) {
            String worth = "Worth " + compact(a.worthEach() * a.quantity());
            float wx = 22 + Draw.width("Pay over = I keep", 11, Draw.SEMIBOLD) + 12;
            int ww = Math.round(Draw.width(worth, 11, Draw.SEMIBOLD) + 18);
            Draw.round(g, Math.round(wx), 66, ww, 18, 9, Theme.accentDark());
            Draw.textCentered(g, worth, wx + ww / 2f, 75, 11, 0xFFFFFFFF, Draw.SEMIBOLD);
        }

        float frac = a.progressLeft();
        Draw.round(g, 10, 88, 280, 5, 2, 0xFF24222C);
        int fw = Math.round(280 * frac);
        if (fw > 0) {
            Draw.round(g, 10, 88, Math.max(4, fw), 5, 2, low ? RED : accent);
        }
        Draw.textCentered(g, "Lethal Auction", 150, 100, 9, 0xFF6E6A78, Draw.MEDIUM);
    }

    // ---------------------------------------------------------------- result

    private void drawResult(GuiGraphicsExtractor g, Auction a, float t, long now, boolean won, float alpha) {
        int accent = Theme.accent();
        int h = 72;
        if (won) {
            int glow = Draw.lerpColor(accent, GOLD, 0.5f + 0.5f * pulse(t, 3));
            Draw.round(g, -3, -3, W + 6, h + 6, 15, Draw.alpha(glow, 0.28f));
            Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, glow);
        } else {
            Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, Draw.lerpColor(0xFF2A2633, accent, 0.3f));
        }
        floatingItem(g, a, t, alpha);

        if (won) {
            long since = now - a.endsAt();
            animatedName(g, a.topBidder(), 74, 26, t, since);
            float subIn = Math.max(0, Math.min(1, (since - 250) / 300f));
            float bounce = (float) Math.sin(t * 5) * 1.2f;
            Draw.text(g, "won for " + compact(a.topBid()), 74 + (1 - subIn) * 12, 51 + bounce, 13,
                    Draw.alpha(GREEN, subIn), Draw.SEMIBOLD);
            String copied = "Name copied";
            int cw = Math.round(Draw.width(copied, 10.5f, Draw.SEMIBOLD) + 18);
            Draw.round(g, W - 12 - cw, 12, cw, 20, 10, Draw.alpha(Theme.accentDark(), subIn));
            Draw.textCentered(g, copied, W - 12 - cw / 2f, 22, 10.5f, Draw.alpha(0xFFFFFFFF, subIn), Draw.SEMIBOLD);
        } else {
            Draw.text(g, "Auction ended", 74, 26, 15, 0xFFF3F3F5, Draw.SEMIBOLD);
            Draw.text(g, "No winner", 74, 50, 13, 0xFF9C9CA6, Draw.MEDIUM);
        }
    }

    /** Winner name: letters pop in one by one, then keep waving, pulsing and shimmering gold / theme colour. */
    private void animatedName(GuiGraphicsExtractor g, String name, float x, float cy, float t, long sinceEnd) {
        float size = 18;
        int light = Theme.accentLight();
        Matrix3x2fStack p = g.pose();
        float cx = x;
        for (int i = 0; i < name.length(); i++) {
            String ch = String.valueOf(name.charAt(i));
            float charW = Draw.width(ch, size, Draw.SEMIBOLD);
            float appear = Math.max(0, Math.min(1, (sinceEnd - i * 55) / 260f));
            if (appear > 0) {
                float wave = (float) Math.sin(t * 7 - i * 0.7) * 4.5f;
                float pop = (1 - easeOutBack(appear)) * 14;
                float scale = 1 + 0.2f * (float) Math.sin(t * 6 - i * 0.7);
                float shimmer = 0.5f + 0.5f * (float) Math.sin(t * 5 - i * 0.6);
                int color = Draw.alpha(Draw.lerpColor(GOLD, light, shimmer), appear);
                p.pushMatrix();
                p.translate(cx + charW / 2f, cy + wave + pop);
                p.scale(scale);
                Draw.text(g, ch, -charW / 2f, 0, size, color, Draw.SEMIBOLD);
                p.popMatrix();
            }
            cx += charW;
        }
    }

    // ---------------------------------------------------------------- shared pieces

    /** The item floats up and down (with a little tilt) inside its box. */
    private void floatingItem(GuiGraphicsExtractor g, Auction a, float t, float alpha) {
        Draw.roundBordered(g, 10, 10, 52, 52, 10, 0xFF0C0B11, 0xFF2A2633);
        float bob = (float) Math.sin(t * 2.4) * 5.5f;
        float tilt = (float) Math.sin(t * 1.7) * 0.12f;
        float size = 2.1f * (0.55f + 0.45f * alpha);
        Draw.round(g, 24 + Math.round(bob * 0.4f), 55, 24 - Math.round(bob * 0.8f), 3, 1, Draw.alpha(0xFF000000, 0.4f - bob * 0.03f));
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(36, 34 + bob);
        p.rotate(tilt);
        p.scale(size);
        if (alpha > 0.05f) {
            g.item(a.item(), -8, -8);
        }
        p.popMatrix();
        if (a.quantity() > 1) {
            Draw.textRight(g, "x" + a.quantity(), 58, 55, 10.5f, 0xFFFFFFFF, Draw.SEMIBOLD);
        }
    }

    private static float pill(GuiGraphicsExtractor g, float x, String text, int fill, int color) {
        int w = Math.round(Draw.width(text, 12, Draw.MEDIUM) + 22);
        Draw.round(g, Math.round(x), 35, w, 22, 11, fill);
        Draw.textCentered(g, text, x + w / 2f, 46.5f, 12, color, Draw.MEDIUM);
        return w;
    }

    private void drawConfetti(GuiGraphicsExtractor g, long sinceEnd, long seed) {
        if (sinceEnd > CONFETTI_MILLIS) {
            return;
        }
        float time = sinceEnd / 1000f;
        float fade = Math.min(1, (CONFETTI_MILLIS - sinceEnd) / 1000f);
        int gw = g.guiWidth(), gh = g.guiHeight();
        Random r = new Random(seed);
        Matrix3x2fStack p = g.pose();
        for (int i = 0; i < CONFETTI; i++) {
            boolean left = i % 2 == 0;
            float x0 = left ? -10 : gw + 10;
            float y0 = gh * (0.15f + r.nextFloat() * 0.35f);
            float vx = (left ? 1 : -1) * (gw * (0.25f + r.nextFloat() * 0.45f));
            float vy = -gh * (0.35f + r.nextFloat() * 0.5f);
            float drag = 1.6f + r.nextFloat();
            float delay = r.nextFloat() * 0.35f;
            float spin = (r.nextFloat() - 0.5f) * 14;
            int color = CONFETTI_COLORS[r.nextInt(CONFETTI_COLORS.length)];
            float w = 2 + r.nextFloat() * 2, h = 3 + r.nextFloat() * 3;
            float tt = time - delay;
            if (tt <= 0) {
                continue;
            }
            float damp = (1 - (float) Math.exp(-drag * tt)) / drag;
            float px = x0 + vx * damp + (float) Math.sin(tt * 5 + i) * 6;
            float py = y0 + vy * damp + 0.5f * gh * 0.9f * tt * tt;
            if (py > gh + 10) {
                continue;
            }
            p.pushMatrix();
            p.translate(px, py);
            p.rotate(spin * tt);
            p.scale(1, (float) Math.abs(Math.cos(tt * 7 + i)) * 0.8f + 0.2f);
            Draw.rect(g, Math.round(-w / 2), Math.round(-h / 2), Math.max(1, Math.round(w)), Math.max(1, Math.round(h)),
                    Draw.alpha(color, fade));
            p.popMatrix();
        }
    }

    private static float pulse(float t, float speed) {
        return 0.5f + 0.5f * (float) Math.sin(t * speed);
    }

    private static float easeOutBack(float k) {
        float c1 = 1.70158f, c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(k - 1, 3) + c1 * (float) Math.pow(k - 1, 2);
    }

    /** 377000000 -> "377M", 2500 -> "2.5K". */
    static String compact(long amount) {
        return Auctions.formatMoney(amount).substring(1).toUpperCase(Locale.ROOT);
    }
}
