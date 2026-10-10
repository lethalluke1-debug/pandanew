package com.lethalauction.cape;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Auctions.Auction;
import com.lethalauction.gui.Theme;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/**
 * The live "Auction Cape": a theme-coloured animated cape with the auction timer on its back. It is
 * redrawn a few times a second while it is visible (worn during an auction, or previewed in the menu).
 */
public final class AuctionCape {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("lethalauction", "auction_cape");
    /** Texture pixels per cape pixel. */
    public static final int S = 8;
    public static final int WIDTH = 64 * S, HEIGHT = 32 * S;

    private static DynamicTexture texture;
    private static long lastDraw;

    private AuctionCape() {
    }

    /** Redraws the cape if it is due. Pass null for a preview with a sample timer. Render thread only. */
    public static void update(Auction auction) {
        long now = System.currentTimeMillis();
        if (now - lastDraw < 90) {
            return;
        }
        if (texture == null) {
            texture = new DynamicTexture(() -> "Lethal Auction auction cape", WIDTH, HEIGHT, true);
            Minecraft.getInstance().getTextureManager().register(TEXTURE, texture);
        }
        lastDraw = now;
        draw(texture.getPixels(), auction, now);
        texture.upload();
    }

    private static void draw(NativeImage img, Auction a, long now) {
        int accent = Theme.accent() | 0xFF000000;
        int dark = CapeImages.mix(accent, 0xFF06040A, 0.82f);
        int deep = CapeImages.mix(accent, 0xFF000000, 0.93f);
        int light = CapeImages.mix(accent, 0xFFFFFFFF, 0.55f);
        float t = (now % 100_000L) / 1000f;

        // Whole texture: dark base so edges and elytra match.
        CapeImages.fill(img, 0, 0, WIDTH, HEIGHT, deep);

        int fx = S, fy = S, fw = 10 * S, fh = 16 * S;
        drawFace(img, fx, fy, fw, fh, accent, dark, deep, t, true);
        drawFace(img, 12 * S, S, fw, fh, accent, dark, deep, t, false);
        // elytra area gets the same animated background
        drawFace(img, 22 * S, 0, 24 * S, 22 * S, accent, dark, deep, t, false);

        // border on the outside face
        CapeImages.fill(img, fx, fy, fw, 2, light);
        CapeImages.fill(img, fx, fy + fh - 2, fw, 2, light);
        CapeImages.fill(img, fx, fy, 2, fh, light);
        CapeImages.fill(img, fx + fw - 2, fy, 2, fh, light);

        int cx = fx + fw / 2;
        boolean won = a != null && a.ended() && a.hasBid();
        CapeImages.textCentered(img, won ? "WINNER" : "AUCTION", cx, fy + 9, 1, light);

        long secs;
        float progress;
        String sub;
        if (a == null) {
            secs = 75 - (now / 1000) % 75;
            progress = secs / 75f;
            sub = "LETHAL";
        } else {
            secs = a.secondsLeft();
            progress = a.progressLeft();
            if (a.ended()) {
                sub = a.hasBid() ? "SOLD " + Auctions.formatMoney(a.topBid()).toUpperCase(Locale.ROOT) : "ENDED";
            } else {
                sub = a.hasBid() ? Auctions.formatMoney(a.topBid()).toUpperCase(Locale.ROOT) : "NO BIDS";
            }
        }
        int midY = fy + fh / 2;
        if (won) {
            drawWinner(img, a.topBidder(), cx, midY, fw, light, t, now - a.endsAt());
            CapeImages.textCentered(img, sub, cx, midY + 16, 1, light);
        } else {
            String time = Auctions.formatClock(secs);
            int scale = CapeImages.textWidth(time, 4) <= fw - 8 ? 4 : CapeImages.textWidth(time, 3) <= fw - 8 ? 3 : 2;
            boolean low = a != null && !a.ended() && secs <= 10;
            boolean blink = low && (now / 250) % 2 == 0;
            CapeImages.textCentered(img, time, cx, midY - 7 * scale / 2 - 4, scale, blink ? 0xFFFF4D5E : 0xFFFFFFFF);
            CapeImages.textCentered(img, sub, cx, midY + 7 * scale / 2 + 6, 1, light);
        }
        boolean low = a != null && !a.ended() && secs <= 10;

        // progress bar
        int bx = fx + 8, by = fy + fh - 16, bw = fw - 16;
        CapeImages.fill(img, bx, by, bw, 4, dark);
        CapeImages.fill(img, bx, by, Math.round(bw * Math.max(0, Math.min(1, progress))), 4, low ? 0xFFFF4D5E : light);
    }

    /**
     * The winner's name: letters pop in one by one, then each bobs and shimmers between gold and the
     * theme colour. The whole name breathes bigger and smaller when there is room for the larger size.
     */
    private static void drawWinner(NativeImage img, String name, int cx, int midY, int faceW, int light, float t, long since) {
        String text = name.toUpperCase(Locale.ROOT);
        int room = faceW - 8;
        int scale = CapeImages.textWidth(text, 2) <= room ? 2 : 1;
        boolean canGrow = CapeImages.textWidth(text, scale + 1) <= room;
        if (canGrow && since > 600 && Math.sin(t * 3.2) > 0.2) {
            scale++;
        }
        int width = CapeImages.textWidth(text, scale);
        int x = cx - width / 2;
        if (width > faceW - 4) {
            // too long even at the smallest size: scroll it across
            int span = width + faceW;
            x = cx + faceW / 2 - (int) ((since / 25) % span);
        }
        int top = midY - 7 * scale / 2 - 6;
        for (int i = 0; i < text.length(); i++) {
            float appear = Math.max(0, Math.min(1, (since - i * 60) / 220f));
            if (appear <= 0) {
                continue;
            }
            int bob = (int) Math.round(Math.sin(t * 7 - i * 0.7) * 2);
            int pop = (int) ((1 - appear) * 10);
            float shimmer = 0.5f + 0.5f * (float) Math.sin(t * 5 - i * 0.6);
            CapeImages.text(img, String.valueOf(text.charAt(i)), x + i * 6 * scale, top + bob + pop, scale,
                    CapeImages.mix(0xFFFFD54A, light, shimmer));
        }
        // twinkling sparkles around the name
        for (int k = 0; k < 10; k++) {
            double seed = k * 12.9898;
            int sx = cx - faceW / 2 + 4 + (int) (Math.abs(Math.sin(seed) * 43758.5453) % (faceW - 8));
            int sy = midY - 40 + (int) (Math.abs(Math.sin(seed * 1.7) * 23421.631) % 22);
            if (k % 2 == 1) {
                sy += 44;
            }
            if (Math.sin(t * 4 + k) > 0.55) {
                CapeImages.fill(img, sx, sy, 2, 2, 0xFFFFFFFF);
            }
        }
    }

    /** Vertical gradient, a soft moving glow and drifting diagonal stripes. */
    private static void drawFace(NativeImage img, int x0, int y0, int w, int h, int accent, int dark, int deep, float t, boolean stripes) {
        float glowY = (float) (0.35 + 0.15 * Math.sin(t * 1.3));
        for (int y = 0; y < h; y++) {
            float v = y / (float) h;
            int base = CapeImages.mix(CapeImages.mix(accent, dark, 0.35f), deep, v);
            for (int x = 0; x < w; x++) {
                float u = x / (float) w;
                float dx = u - 0.5f, dy = v - glowY;
                float glow = (float) Math.max(0, 1 - Math.sqrt(dx * dx * 2.2f + dy * dy * 3.5f) * 1.6f);
                int c = CapeImages.mix(base, accent, glow * 0.55f);
                if (stripes && Math.floorMod((int) (x + y - t * 14), 16) < 3) {
                    c = CapeImages.mix(c, 0xFFFFFFFF, 0.08f);
                }
                img.setPixel(x0 + x, y0 + y, c);
            }
        }
    }
}
