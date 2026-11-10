/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1011
 */
package com.lethalauction.m;

import net.minecraft.class_1011;

public final class K3 {
    public static final float a = 0.625f;
    private static final String b = "0123456789:.$ABCDEFGHIKLMNOSTUWXJPQRVYZ_-!";
    private static final String[] c = new String[]{"01110100011001110101110011000101110", "00100011000010000100001000010001110", "01110100010000100010001000100011111", "11111000100010000010000011000101110", "00010001100101010010111110001000010", "11111100001111000001000011000101110", "00110010001000011110100011000101110", "11111000010001000100010000100001000", "01110100011000101110100011000101110", "01110100011000101111000010001001100", "00000011000110000000011000110000000", "00000000000000000000000000110001100", "00100011111010001110001011111000100", "01110100011000111111100011000110001", "11110100011000111110100011000111110", "01110100011000010000100001000101110", "11100100101000110001100011001011100", "11111100001000011110100001000011111", "11111100001000011110100001000010000", "01110100011000010111100011000101111", "10001100011000111111100011000110001", "01110001000010000100001000010001110", "10001100101010011000101001001010001", "10000100001000010000100001000011111", "10001110111010110101100011000110001", "10001110011010110011100011000110001", "01110100011000110001100011000101110", "01111100001000001110000010000111110", "11111001000010000100001000010000100", "10001100011000110001100011000101110", "10001100011000110101101011010101010", "10001100010101000100010101000110001", "00111000100001000010000101001001100", "11110100011000111110100001000010000", "01110100011000110001101011001001101", "11110100011000111110101001001010001", "10001100011000110001100010101000100", "10001100010101000100001000010000100", "11111000010001000100010001000011111", "00000000000000000000000000000011111", "00000000000000011111000000000000000", "00100001000010000100001000000000100"};

    private K3() {
    }

    public static class_1011 a(class_1011 src, int cropX, int cropY, int cropW, int cropH) {
        int s = Math.max(1, Math.min(16, Math.round((float)cropW / 10.0f)));
        class_1011 out = new class_1011(64 * s, 32 * s, true);
        K3.a(src, cropX, cropY, cropW, cropH, out, s, s, 10 * s, 16 * s, false, 1.0f);
        K3.a(src, cropX, cropY, cropW, cropH, out, 12 * s, s, 10 * s, 16 * s, true, 0.6f);
        for (int x = 0; x < 10 * s; ++x) {
            int top = K3.a(src, cropX + x * cropW / (10 * s), cropY);
            int bottom = K3.a(src, cropX + x * cropW / (10 * s), cropY + cropH - 1);
            for (int y = 0; y < s; ++y) {
                out.method_61941(s + x, y, top);
                out.method_61941(11 * s + x, y, bottom);
            }
        }
        for (int y = 0; y < 16 * s; ++y) {
            int left = K3.a(src, cropX, cropY + y * cropH / (16 * s));
            int right = K3.a(src, cropX + cropW - 1, cropY + y * cropH / (16 * s));
            for (int x = 0; x < s; ++x) {
                out.method_61941(x, s + y, left);
                out.method_61941(11 * s + x, s + y, right);
            }
        }
        K3.a(src, cropX, cropY, cropW, cropH, out, 22 * s, 0, 24 * s, 22 * s, false, 0.9f);
        return out;
    }

    private static void a(class_1011 src, int sx, int sy, int sw, int sh, class_1011 dst, int dx, int dy, int dw, int dh, boolean mirror, float shade) {
        for (int y = 0; y < dh; ++y) {
            for (int x = 0; x < dw; ++x) {
                int px = sx + (mirror ? dw - 1 - x : x) * sw / dw;
                int py = sy + y * sh / dh;
                dst.method_61941(dx + x, dy + y, K3.a(K3.a(src, px, py), shade));
            }
        }
    }

    private static int a(class_1011 img, int x, int y) {
        x = Math.max(0, Math.min(img.method_4307() - 1, x));
        y = Math.max(0, Math.min(img.method_4323() - 1, y));
        return img.method_61940(x, y) | 0xFF000000;
    }

    static int a(int argb, float f) {
        if (f >= 1.0f) {
            return argb;
        }
        int r = (int)((float)(argb >> 16 & 0xFF) * f);
        int g = (int)((float)(argb >> 8 & 0xFF) * f);
        int b = (int)((float)(argb & 0xFF) * f);
        return argb & 0xFF000000 | r << 16 | g << 8 | b;
    }

    static int a(int a, int b, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return 0xFF000000 | (int)((float)ar + (float)(br - ar) * t) << 16 | (int)((float)ag + (float)(bg - ag) * t) << 8 | (int)((float)ab + (float)(bb - ab) * t);
    }

    public static int a(String text, int scale) {
        return text.isEmpty() ? 0 : (text.length() * 6 - 1) * scale;
    }

    public static void a(class_1011 img, String text, int x, int y, int scale, int color) {
        for (int i = 0; i < text.length(); ++i) {
            int g = b.indexOf(Character.toUpperCase(text.charAt(i)));
            if (g >= 0) {
                String bits = c[g];
                for (int row = 0; row < 7; ++row) {
                    for (int col = 0; col < 5; ++col) {
                        if (bits.charAt(row * 5 + col) != '1') continue;
                        K3.a(img, x + col * scale, y + row * scale, scale, scale, color);
                    }
                }
            }
            x += 6 * scale;
        }
    }

    public static void b(class_1011 img, String text, int cx, int y, int scale, int color) {
        K3.a(img, text, cx - K3.a(text, scale) / 2, y, scale, color);
    }

    public static void a(class_1011 img, int x, int y, int w, int h, int color) {
        for (int yy = Math.max(0, y); yy < Math.min(img.method_4323(), y + h); ++yy) {
            for (int xx = Math.max(0, x); xx < Math.min(img.method_4307(), x + w); ++xx) {
                img.method_61941(xx, yy, color);
            }
        }
    }
}

