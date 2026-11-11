/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K3;
import com.lethalauction.m.K32;
import java.util.Locale;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public final class K2 {
    public static final class_2960 a = class_2960.method_60655((String)"lethalauction", (String)"auction_cape");
    public static final int b = 8;
    public static final int c = 512;
    public static final int d = 256;
    private static class_1043 e;
    private static long f;

    private K2() {
    }

    public static void a(Auctions.C65 auction) {
        long now = System.currentTimeMillis();
        if (now - f >= 90L) {
            if (e == null) {
                e = new class_1043(() -> "Lethal Client auction cape", 512, 256, true);
                class_310.method_1551().method_1531().method_4616(a, (class_1044)e);
            }
            f = now;
            K2.a(e.method_4525(), auction, now);
            e.method_4524();
        }
    }

    private static void a(class_1011 img, Auctions.C65 a, long now) {
        Object sub;
        float progress;
        long secs;
        int accent = K32.a() | 0xFF000000;
        int dark = K3.a(accent, -16382966, 0.82f);
        int deep = K3.a(accent, -16777216, 0.93f);
        int light = K3.a(accent, -1, 0.55f);
        float t = (float)(now % 100000L) / 1000.0f;
        K3.a(img, 0, 0, 512, 256, deep);
        int fx = 8;
        int fy = 8;
        int fw = 80;
        int fh = 128;
        K2.a(img, fx, fy, fw, fh, accent, dark, deep, t, true);
        K2.a(img, 96, 8, fw, fh, accent, dark, deep, t, false);
        K2.a(img, 176, 0, 192, 176, accent, dark, deep, t, false);
        K3.a(img, fx, fy, fw, 2, light);
        K3.a(img, fx, fy + fh - 2, fw, 2, light);
        K3.a(img, fx, fy, 2, fh, light);
        K3.a(img, fx + fw - 2, fy, 2, fh, light);
        int cx = fx + fw / 2;
        boolean won = a != null && a.ended() && a.hasBid();
        K3.b(img, won ? "WINNER" : "AUCTION", cx, fy + 9, 1, light);
        if (a == null) {
            secs = 75L - now / 1000L % 75L;
            progress = (float)secs / 75.0f;
            sub = "LETHAL";
        } else {
            secs = a.secondsLeft();
            progress = a.progressLeft();
            sub = a.ended() ? (a.hasBid() ? "SOLD " + Auctions.a(a.topBid()).toUpperCase(Locale.ROOT) : "ENDED") : (a.hasBid() ? Auctions.a(a.topBid()).toUpperCase(Locale.ROOT) : "NO BIDS");
        }
        int midY = fy + fh / 2;
        if (won) {
            K2.a(img, a.topBidder(), cx, midY, fw, light, t, now - a.endsAt());
            K3.b(img, (String)sub, cx, midY + 16, 1, light);
        } else {
            String time = Auctions.b(secs);
            int scale = K3.a(time, 4) <= fw - 8 ? 4 : (K3.a(time, 3) <= fw - 8 ? 3 : 2);
            boolean low = a != null && !a.ended() && secs <= 10L;
            boolean blink = low && now / 250L % 2L == 0L;
            K3.b(img, time, cx, midY - 7 * scale / 2 - 4, scale, blink ? -45730 : -1);
            K3.b(img, (String)sub, cx, midY + 7 * scale / 2 + 6, 1, light);
        }
        boolean low = a != null && !a.ended() && secs <= 10L;
        int bx = fx + 8;
        int by = fy + fh - 16;
        int bw = fw - 16;
        K3.a(img, bx, by, bw, 4, dark);
        K3.a(img, bx, by, Math.round((float)bw * Math.max(0.0f, Math.min(1.0f, progress))), 4, low ? -45730 : light);
    }

    private static void a(class_1011 img, String name, int cx, int midY, int faceW, int light, float t, long since) {
        boolean canGrow;
        String text = name.toUpperCase(Locale.ROOT);
        int room = faceW - 8;
        int scale = K3.a(text, 2) <= room ? 2 : 1;
        boolean bl = canGrow = K3.a(text, scale + 1) <= room;
        if (canGrow && since > 600L && Math.sin((double)t * 3.2) > 0.2) {
            ++scale;
        }
        int width = K3.a(text, scale);
        int x = cx - width / 2;
        if (width > faceW - 4) {
            int span = width + faceW;
            x = cx + faceW / 2 - (int)(since / 25L % (long)span);
        }
        int top = midY - 7 * scale / 2 - 6;
        for (int i = 0; i < text.length(); ++i) {
            float appear = Math.max(0.0f, Math.min(1.0f, (float)(since - (long)(i * 60)) / 220.0f));
            if (appear <= 0.0f) continue;
            int bob = (int)Math.round(Math.sin((double)(t * 7.0f) - (double)i * 0.7) * 2.0);
            int pop = (int)((1.0f - appear) * 10.0f);
            float shimmer = 0.5f + 0.5f * (float)Math.sin((double)(t * 5.0f) - (double)i * 0.6);
            K3.a(img, String.valueOf(text.charAt(i)), x + i * 6 * scale, top + bob + pop, scale, K3.a(-10934, light, shimmer));
        }
        for (int k = 0; k < 10; ++k) {
            double seed = (double)k * 12.9898;
            int sx = cx - faceW / 2 + 4 + (int)(Math.abs(Math.sin(seed) * 43758.5453) % (double)(faceW - 8));
            int sy = midY - 40 + (int)(Math.abs(Math.sin(seed * 1.7) * 23421.631) % 22.0);
            if (k % 2 == 1) {
                sy += 44;
            }
            if (!(Math.sin(t * 4.0f + (float)k) > 0.55)) continue;
            K3.a(img, sx, sy, 2, 2, -1);
        }
    }

    private static void a(class_1011 img, int x0, int y0, int w, int h, int accent, int dark, int deep, float t, boolean stripes) {
        float glowY = (float)(0.35 + 0.15 * Math.sin((double)t * 1.3));
        for (int y = 0; y < h; ++y) {
            float v = (float)y / (float)h;
            int base = K3.a(K3.a(accent, dark, 0.35f), deep, v);
            for (int x = 0; x < w; ++x) {
                float u = (float)x / (float)w;
                float dx = u - 0.5f;
                float dy = v - glowY;
                float glow = (float)Math.max(0.0, 1.0 - Math.sqrt(dx * dx * 2.2f + dy * dy * 3.5f) * (double)1.6f);
                int c = K3.a(base, accent, glow * 0.55f);
                if (stripes && Math.floorMod((int)((float)(x + y) - t * 14.0f), 16) < 3) {
                    c = K3.a(c, -1, 0.08f);
                }
                img.method_61941(x0 + x, y0 + y, c);
            }
        }
    }
}

