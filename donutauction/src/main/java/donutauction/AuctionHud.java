package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * The live auction strip at the top (or bottom) of the screen: item tile, top bid, bidder, a countdown pill and a
 * thin progress line. After it ends, the result shows for a few seconds.
 */
public final class AuctionHud {
    private static final int W = 230;
    private static final int H = 34;

    private AuctionHud() {}

    public static void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Config c = Config.get();
        if (!c.showHud || mc.player == null) return;
        Auction live = Auction.current();
        Auction done = live == null ? Auction.recent() : null;
        if (live == null && done == null) return;
        Auction a = live != null ? live : done;

        Font font = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        int x = (sw - W) / 2;
        int y = c.hudTop ? 6 : sh - H - 52;
        int accent = Ui.accent();
        boolean urgent = live != null && a.remainingMs() <= 10_000;
        int edge = urgent ? Ui.RED : accent;

        // Card with a soft glow and a coloured left edge.
        Ui.round(g, x - 2, y - 2, W + 4, H + 4, 8, Ui.alpha(edge, 0x30));
        Ui.box(g, x, y, W, H, 6, 0xEC0D0E16, Ui.alpha(edge, 0x90));
        g.fill(x + 1, y + 6, x + 3, y + H - 6, edge);

        // Item tile.
        Ui.box(g, x + 8, y + 5, 24, 24, 5, Ui.FIELD, Ui.BORDER);
        g.item(a.stack(), x + 12, y + 9);
        if (a.quantity > 1) {
            String q = "x" + a.quantity;
            Ui.text(g, font, Component.literal(q).withStyle(ChatFormatting.BOLD), x + 31 - font.width(q) * 0.6f, y + 23, Ui.TEXT, 0.6f);
        }

        int tx = x + 38;
        String title = live != null ? "LIVE AUCTION" : a.wasCancelled() ? "CANCELLED" : a.topBidder() != null ? "SOLD" : "ENDED";
        Ui.text(g, font, Component.literal(title).withStyle(ChatFormatting.BOLD), tx, y + 5, live != null ? edge : Ui.MUTED, 0.6f);
        float titleW = font.width(Component.literal(title).withStyle(ChatFormatting.BOLD)) * 0.6f;
        Ui.text(g, font, Component.literal(Ui.ellipsize(font, a.itemName(), 90)), tx + titleW + 4, y + 5, Ui.MUTED, 0.6f);

        // Top bid, big.
        String bid = a.topBidder() != null ? "$" + Money.format(a.topBid()) : live != null ? "No bids yet" : "No bids";
        Ui.text(g, font, Component.literal(bid).withStyle(ChatFormatting.BOLD), tx, y + 12, a.topBidder() != null ? Ui.TEXT : Ui.MUTED, 1.0f);
        float bidW = font.width(Component.literal(bid).withStyle(ChatFormatting.BOLD));
        if (a.topBidder() != null) {
            Ui.text(g, font, Component.literal("by " + a.topBidder()), tx + bidW + 4, y + 13.5f, accent, 0.7f);
        }

        // Details line.
        String details = (a.minBid > 0 ? "Min " + Money.format(a.minBid) : "No minimum")
                + (a.worthEach > 0 ? "  •  Worth " + Money.format(a.worthTotal()) : "")
                + (c.ruleText.isBlank() ? "" : "  •  " + c.ruleText);
        Ui.text(g, font, Component.literal(details), tx, y + 24, Ui.FAINT, 0.6f);

        // Countdown pill.
        String time = live != null ? Ui.clock(a.remainingMs()) : "DONE";
        int pw = Math.round(font.width(time) * 0.75f) + 10;
        Ui.box(g, x + W - pw - 8, y + 6, pw, 12, 6, urgent ? Ui.alpha(Ui.RED, 0x40) : Ui.alpha(accent, 0x30), Ui.alpha(edge, 0xA0));
        Ui.text(g, font, Component.literal(time).withStyle(ChatFormatting.BOLD), x + W - pw - 3, y + 8.5f, Ui.TEXT, 0.75f);
        if (live != null && a.bidders() > 0) {
            String n = a.bidders() + (a.bidders() == 1 ? " bidder" : " bidders");
            Ui.text(g, font, Component.literal(n), x + W - 8 - font.width(n) * 0.6f, y + 21, Ui.FAINT, 0.6f);
        }

        // Progress line along the bottom.
        int bx = x + 8, bw = W - 16, by = y + H - 3;
        g.fill(bx, by, bx + bw, by + 1, Ui.alpha(Ui.BORDER, 0xFF));
        if (live != null) g.fill(bx, by, bx + Math.round(bw * a.progress()), by + 1, edge);

        String credit = "Donut Auction • by Lethal";
        Ui.text(g, font, Component.literal(credit), x + (W - font.width(credit) * 0.5f) / 2f + 0, y + H + 3, Ui.alpha(Ui.MUTED, 0x90), 0.5f);
    }
}
