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
        float t = now / 1000f;
        boolean won = a.ended() && a.hasBid();
        if (won) {
            drawConfetti(g, now - a.endsAt(), a.endsAt());
        }

        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        float x = (g.guiWidth() - W * SCALE) / 2f;
        long secs = a.secondsLeft();
        boolean low = !a.ended() && secs <= LOW_TIME;
        if (low) {
            float strength = 0.6f + 2.2f * (1 - (secs - 1) / (float) LOW_TIME);
            x += (float) (Math.sin(t * 55) * strength * SCALE);
        }
        float y = 5;
        if (a.ended()) {
            // pop the result card in
            float k = Math.min(1, (now - a.endsAt()) / 250f);
            y += (1 - easeOutBack(k)) * -6;
        }
        p.translate(x, y);
        p.scale(SCALE);

        if (a.ended()) {
            drawResult(g, a, t, now, won);
        } else {
            drawLive(g, a, t, secs, low);
        }
        p.popMatrix();
    }

    // ---------------------------------------------------------------- running auction

    private void drawLive(GuiGraphicsExtractor g, Auction a, float t, long secs, boolean low) {
        int accent = Theme.accent();
        int h = 106;
        int border = low ? Draw.lerpColor(RED, 0xFF000000, 0.15f) : Draw.lerpColor(0xFF2A2633, accent, 0.45f);
        Draw.round(g, -2, -2, W + 4, h + 4, 14, Draw.alpha(low ? RED : accent, low ? 0.22f + 0.12f * pulse(t, 4) : 0.18f));
        Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, border);

        floatingItem(g, a, t);

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

    private void drawResult(GuiGraphicsExtractor g, Auction a, float t, long now, boolean won) {
        int accent = Theme.accent();
        int h = 72;
        if (won) {
            int glow = Draw.lerpColor(accent, GOLD, 0.5f + 0.5f * pulse(t, 3));
            Draw.round(g, -3, -3, W + 6, h + 6, 15, Draw.alpha(glow, 0.28f));
            Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, glow);
        } else {
            Draw.roundBordered(g, 0, 0, W, h, 12, 0xF00F0E15, Draw.lerpColor(0xFF2A2633, accent, 0.3f));
        }
        floatingItem(g, a, t);

        if (won) {
            animatedName(g, a.topBidder(), 74, 26, t, now - a.endsAt());
            Draw.text(g, "won for " + compact(a.topBid()), 74, 50, 13, GREEN, Draw.SEMIBOLD);
        } else {
            Draw.text(g, "Auction ended", 74, 26, 15, 0xFFF3F3F5, Draw.SEMIBOLD);
            Draw.text(g, "No winner", 74, 50, 13, 0xFF9C9CA6, Draw.MEDIUM);
        }
    }

    /** Winner name: letters pop in one by one, then wave and shimmer between gold and the theme colour. */
    private void animatedName(GuiGraphicsExtractor g, String name, float x, float cy, float t, long sinceEnd) {
        float size = 17;
        int light = Theme.accentLight();
        float cx = x;
        for (int i = 0; i < name.length(); i++) {
            String ch = String.valueOf(name.charAt(i));
            float appear = Math.max(0, Math.min(1, (sinceEnd - i * 45) / 220f));
            float charW = Draw.width(ch, size, Draw.SEMIBOLD);
            if (appear > 0) {
                float wave = (float) Math.sin(t * 6 - i * 0.55) * 2.2f;
                float pop = (1 - easeOutBack(appear)) * 10;
                float shimmer = 0.5f + 0.5f * (float) Math.sin(t * 4 - i * 0.45);
                int color = Draw.alpha(Draw.lerpColor(GOLD, light, shimmer), appear);
                Draw.text(g, ch, cx, cy + wave + pop, size, color, Draw.SEMIBOLD);
            }
            cx += charW;
        }
    }

    // ---------------------------------------------------------------- shared pieces

    /** The item gently floats up and down inside its box. */
    private void floatingItem(GuiGraphicsExtractor g, Auction a, float t) {
        Draw.roundBordered(g, 10, 10, 52, 52, 10, 0xFF0C0B11, 0xFF2A2633);
        float bob = (float) Math.sin(t * 2.6) * 3f;
        Draw.round(g, 22, 52, 28, 3, 1, Draw.alpha(0xFF000000, 0.35f - bob * 0.03f));
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(18, 16 + bob);
        p.scale(2.25f);
        g.item(a.item(), 0, 0);
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
