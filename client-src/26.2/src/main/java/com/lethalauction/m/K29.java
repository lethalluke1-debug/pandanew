/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K32;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Matrix3x2fStack;

public final class K29 {
    public static final String[] a = new String[]{"Default", "Theme", "Custom", "Clear"};
    private static final int b = 3;
    private static final int c = 10;
    private static int d;
    private static int e;
    private static int f;
    private static int g;
    private static int h;
    private static boolean i;
    private static boolean j;

    private K29() {
    }

    public static boolean a() {
        return j;
    }

    public static void a(GuiGraphicsExtractor g) {
        boolean bl = j = K37.b() && !K9.aV;
        if (!j) {
            return;
        }
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        float s = K9.aS;
        if (s != 1.0f) {
            float ax = g.guiWidth();
            float ay = (float)g.guiHeight() / 2.0f;
            p.translate(ax, ay);
            p.scale(s);
            p.translate(-ax, -ay);
        }
        i = false;
    }

    public static void b(GuiGraphicsExtractor g) {
        if (!j) {
            return;
        }
        g.pose().popMatrix();
    }

    public static void a(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        d = x0 -= K29.b();
        e = y0;
        f = x1;
        K29.g = y1;
        h = color;
        i = true;
    }

    public static void b(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        x0 -= K29.b();
        int body = K29.a(color);
        int title = K29.b(i ? h : color);
        if (K9.aX != 3) {
            int top;
            int n = top = i ? Math.min(e, y0) : y0;
            if (!K9.aT) {
                if (i) {
                    g.fill(d, e, f, K29.g, title);
                }
                g.fill(x0, y0, x1, y1, body);
            } else if (!i) {
                K18.a(g, x0, top, x1 - x0, y1 - top, 3, body);
            } else {
                K18.b(g, d, e, f - d, K29.g - e, 3, title);
                K18.c(g, x0, y0, x1 - x0, y1 - y0, 3, body);
            }
        }
    }

    private static int a(int vanilla) {
        return switch (K9.aX) {
            case 1 -> K18.a(K18.a(-16053744, K32.a(), 0.35f), 0.55f);
            case 2 -> K29.a(K9.aZ);
            default -> vanilla;
        };
    }

    private static int b(int vanilla) {
        return switch (K9.aX) {
            case 1 -> K18.a(K32.c(), 0.75f);
            case 2 -> K29.a(Math.min(1.0f, K9.aZ + 0.15f));
            default -> vanilla;
        };
    }

    public static void a(GuiGraphicsExtractor g, Font font, Component text, int x, int y, int color, boolean shadow) {
        g.text(font, text, x - K29.b(), y, color, shadow);
    }

    public static int b() {
        return K9.aU && Minecraft.getInstance().player != null ? 10 : 0;
    }

    public static void b(GuiGraphicsExtractor g, Font font, Component text, int x, int y, int color, boolean shadow) {
        if (K9.aU) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                g.pose().pushMatrix();
                g.pose().translate(0.0f, -0.5f);
                PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)g, (PlayerSkin)mc.player.getSkin(), (int)(x - 10), (int)y, (int)8);
                g.pose().popMatrix();
            }
        }
        g.text(font, text, x, y, color, shadow);
    }

    public static int a(float opacity) {
        int rgb = Color.HSBtoRGB(K9.aY, 0.75f, 0.55f) & 0xFFFFFF;
        return Math.round(Math.max(0.0f, Math.min(1.0f, opacity)) * 255.0f) << 24 | rgb;
    }
}

