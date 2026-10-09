package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * The live auction strip at the top (or bottom) of the screen. It slides in when an auction starts, the item bobs
 * and sways, a new top bid flashes and pops, the timer pulses in the last 10 seconds, and after the result it
 * slides back out.
 */
public final class AuctionHud {
    private static final int W = 214;
    private static final int H = 32;

    private AuctionHud() {}

    public static void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Config c = Config.get();
        if (!c.showHud || mc.player == null) return;
        Auction live = Auction.current();
        Auction done = live == null ? Auction.recent() : null;
        if (live == null && done == null) return;
        Auction a = live != null ? live : done;
        long now = System.currentTimeMillis();
        boolean anim = c.animations;

        Font font = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        int x = (sw - W) / 2;
        int restY = c.hudTop ? 5 : sh - H - 50;

        // Slide in from off-screen; slide out over the last 600ms of the result.
        float in = Ui.easeOutBack(Ui.anim(a.startedAt(), 450));
        float out = 0;
        if (live == null) {
            long left = Auction.lastEndedAt() + Auction.resultShowMs() - now;
            out = anim ? Math.clamp(1 - left / 600f, 0f, 1f) : 0f;
        }
        int hidden = c.hudTop ? -H - 12 : sh + 12;
        float y = hidden + (restY - hidden) * in;
        y += (hidden - restY) * Ui.easeOut(out);

        g.pose().pushMatrix();
        g.pose().translate(0, y);
        draw(g, font, a, live != null, x, now, anim);
        g.pose().popMatrix();
    }

    private static void draw(GuiGraphicsExtractor g, Font font, Auction a, boolean live, int x, long now, boolean anim) {
        Config c = Config.get();
        int accent = Ui.accent();
        boolean urgent = live && a.remainingMs() <= 10_000;
        int edge = urgent ? Ui.RED : live ? accent : a.topBidder() != null && !a.wasCancelled() ? Ui.GREEN : Ui.MUTED;
        float flash = a.lastBidAt() > 0 && anim ? Math.clamp(1 - (now - a.lastBidAt()) / 700f, 0f, 1f) : 0f;
        float pulse = urgent && anim ? (float) (0.5 + 0.5 * Math.sin(now / 120.0)) : 0f;

        // Card: glow (brighter on a new bid or in the last seconds), border, coloured left edge.
        int glow = 0x28 + Math.round(flash * 0x60) + Math.round(pulse * 0x30);
        Ui.round(g, x - 3, -3, W + 6, H + 6, 9, Ui.alpha(edge, Math.min(0xC0, glow)));
        Ui.box(g, x, 0, W, H, 6, 0xEE0C0D15, Ui.alpha(edge, 0x90 + Math.round(flash * 0x6F)));
        g.fill(x + 1, 6, x + 3, H - 6, edge);

        // Item tile with the item bobbing and gently swaying.
        Ui.box(g, x + 7, 4, 24, 24, 5, Ui.FIELD, Ui.alpha(edge, 0x70 + Math.round(flash * 0x8F)));
        float t = now / 1000f;
        float bob = anim ? (float) Math.sin(t * 2.6) * 1.6f : 0f;
        float sway = anim ? (float) Math.sin(t * 1.7) * 0.12f : 0f;
        float pop = 1 + flash * 0.25f;
        g.pose().pushMatrix();
        g.pose().translate(x + 19, 16 + bob);
        g.pose().rotate(sway);
        g.pose().scale(pop, pop);
        g.item(a.stack(), -8, -8);
        g.pose().popMatrix();
        if (a.quantity > 1) {
            String q = "x" + a.quantity;
            Ui.text(g, font, Component.literal(q).withStyle(ChatFormatting.BOLD), x + 30 - font.width(q) * 0.55f, 22.5f, Ui.TEXT, 0.55f);
        }

        int tx = x + 37;
        String title = live ? "LIVE" : a.wasCancelled() ? "CANCELLED" : a.topBidder() != null ? "SOLD" : "ENDED";
        Component titleC = Component.literal(title).withStyle(ChatFormatting.BOLD);
        float titleW = font.width(titleC) * 0.55f;
        Ui.round(g, tx - 1, 4, Math.round(titleW) + 6, 8, 3, Ui.alpha(edge, 0x40));
        if (live && anim) Ui.round(g, tx + 1, 6, 4, 4, 2, Ui.alpha(edge, 0x80 + (int) (0x7F * (0.5 + 0.5 * Math.sin(t * 5)))));
        Ui.text(g, font, titleC, tx + (live ? 7 : 2), 5.5f, Ui.TEXT, 0.55f);
        float afterTitle = tx + titleW + (live ? 12 : 8);
        Ui.text(g, font, Component.literal(Ui.ellipsize(font, a.itemName(), 95)), afterTitle, 5.5f, Ui.MUTED, 0.6f);

        // Top bid: pops when it changes.
        String bid = a.topBidder() != null ? "$" + Money.format(a.topBid()) : live ? "No bids yet" : "No bids";
        Component bidC = Component.literal(bid).withStyle(ChatFormatting.BOLD);
        float bidScale = 0.85f * (1 + flash * 0.3f);
        Ui.text(g, font, bidC, tx, 13.5f - flash * 1.2f, a.topBidder() != null ? (flash > 0.3f ? edge : Ui.TEXT) : Ui.MUTED, bidScale);
        float bidW = font.width(bidC) * bidScale;
        if (a.topBidder() != null) {
            Ui.text(g, font, Component.literal("by " + a.topBidder()), tx + bidW + 4, 15, accent, 0.6f);
        }

        String details = (a.minBid > 0 ? "Min " + Money.format(a.minBid) : "No minimum")
                + (a.worthEach > 0 ? "  •  Worth " + Money.format(a.worthTotal()) : "")
                + (c.ruleText.isBlank() ? "" : "  •  " + c.ruleText);
        Ui.text(g, font, Component.literal(details), tx, 23.5f, Ui.FAINT, 0.55f);

        // Countdown pill; pulses red in the last 10 seconds.
        String time = live ? Ui.clock(a.remainingMs()) : "DONE";
        Component timeC = Component.literal(time).withStyle(ChatFormatting.BOLD);
        int pw = Math.round(font.width(timeC) * 0.7f) + 10;
        int px = x + W - pw - 7;
        int pillFill = urgent ? Ui.alpha(Ui.RED, 0x30 + Math.round(pulse * 0x50)) : Ui.alpha(edge, 0x30);
        Ui.box(g, px, 5, pw, 11, 5, pillFill, Ui.alpha(edge, 0xA0));
        Ui.text(g, font, timeC, px + 5, 7.5f, Ui.TEXT, 0.7f);
        if (live && a.bidders() > 0) {
            String n = a.bidders() + (a.bidders() == 1 ? " bidder" : " bidders");
            Ui.text(g, font, Component.literal(n), x + W - 7 - font.width(n) * 0.55f, 20, Ui.FAINT, 0.55f);
        }

        // Progress line with a moving shimmer.
        int bx = x + 7, bw = W - 14, by = H - 3;
        g.fill(bx, by, bx + bw, by + 1, Ui.BORDER);
        if (live) {
            int fillW = Math.round(bw * a.progress());
            g.fill(bx, by, bx + fillW, by + 1, edge);
            if (anim && fillW > 10) {
                int sx = bx + (int) ((now / 6) % (fillW + 20)) - 10;
                g.fill(Math.max(bx, sx), by, Math.min(bx + fillW, sx + 10), by + 1, Ui.alpha(0xFFFFFF, 0xB0));
            }
        }

        String credit = "Donut Auction • by Lethal";
        Ui.text(g, font, Component.literal(credit), x + (W - font.width(credit) * 0.5f) / 2f, H + 2.5f, Ui.alpha(Ui.MUTED, 0x80), 0.5f);
    }
}
