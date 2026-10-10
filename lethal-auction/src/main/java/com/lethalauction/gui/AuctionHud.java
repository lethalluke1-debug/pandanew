package com.lethalauction.gui;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Auctions.Auction;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

/** The live auction card shown at the top of the screen while one of your auctions is running. */
public class AuctionHud implements HudElement {
    private static final int W = 300, H = 106;
    private static final float SCALE = 0.75f;

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        Auction a = Auctions.current();
        if (a == null) {
            return;
        }
        int accent = Theme.accent();

        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate((g.guiWidth() - W * SCALE) / 2f, 6);
        p.scale(SCALE);

        Draw.round(g, -2, -2, W + 4, H + 4, 14, Draw.alpha(accent, 0.18f));
        Draw.roundBordered(g, 0, 0, W, H, 12, 0xF00F0E15, Draw.lerpColor(0xFF2A2633, accent, 0.45f));

        // item
        Draw.roundBordered(g, 10, 10, 52, 52, 10, 0xFF0C0B11, 0xFF2A2633);
        p.pushMatrix();
        p.translate(18, 18);
        p.scale(2.25f);
        g.item(a.item(), 0, 0);
        p.popMatrix();
        if (a.quantity() > 1) {
            Draw.textRight(g, "x" + a.quantity(), 58, 54, 10.5f, 0xFFFFFFFF, Draw.SEMIBOLD);
        }

        boolean ended = a.ended();
        Draw.text(g, ended ? "Auction ended" : "No bids", 72, 22, 15, 0xFFF3F3F5, Draw.SEMIBOLD);
        String min = a.minimumBid() > 0 ? "Min " + compact(a.minimumBid()) : "No minimum";
        int mw = Math.round(Draw.width(min, 12, Draw.MEDIUM) + 22);
        Draw.round(g, 72, 35, mw, 22, 11, 0xFF1F1D27);
        Draw.textCentered(g, min, 72 + mw / 2f, 46.5f, 12, 0xFFCFCDD8, Draw.MEDIUM);

        long secs = a.secondsLeft();
        String time = String.format("%02d:%02d", secs / 60, secs % 60);
        Draw.roundBordered(g, 220, 22, 70, 30, 15, 0xFF15141B, Draw.lerpColor(0xFF34323C, accent, 0.25f));
        Draw.textCentered(g, time, 255, 37.5f, 15, 0xFFFFFFFF, Draw.SEMIBOLD);

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
            Draw.round(g, 10, 88, Math.max(4, fw), 5, 2, accent);
        }
        Draw.textCentered(g, "Lethal Auction", 150, 100, 9, 0xFF6E6A78, Draw.MEDIUM);
        p.popMatrix();
    }

    /** 377000000 -> "377M", 2500 -> "2.5K". */
    static String compact(long amount) {
        return Auctions.formatMoney(amount).substring(1).toUpperCase(java.util.Locale.ROOT);
    }
}
