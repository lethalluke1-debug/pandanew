/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.joml.Matrix3x2fStack;

public class K16
implements HudElement {
    public static final List<String> a = List.of("Default", "Dot", "Cross", "Circle", "Corners", "X");
    public static final List<String> b = List.of("White", "Lethal Blue", "Green", "Red", "Yellow", "Pink");
    public static final int[] c = new int[]{-1, -11559937, -12197766, -45730, -10163, -36904};
    public static final List<String> d = List.of("Small", "Medium", "Large");
    private static final float[] e = new float[]{0.75f, 1.0f, 1.4f};
    private static final int f = -1728053248;
    private final HudElement g;

    public K16(HudElement vanilla) {
        this.g = vanilla;
    }

    public static boolean a() {
        return K9.aP > 0 && K37.b();
    }

    public void render(class_332 g, class_9779 deltaTracker) {
        class_310 mc = class_310.method_1551();
        if (!K16.a()) {
            this.g.render(g, deltaTracker);
            return;
        }
        if (!mc.field_1690.method_31044().method_31034() || mc.field_1724 == null || mc.field_1724.method_7325()) {
            return;
        }
        K16.a(g, (float)g.method_51421() / 2.0f, (float)g.method_51443() / 2.0f, K9.aP, K16.b(), 1.0f);
    }

    public static int b() {
        int i = K9.aQ;
        return c[i >= 0 && i < c.length ? i : 0];
    }

    public static void a(class_332 g, float cx, float cy, int style, int color, float zoom) {
        int si = K9.aR;
        float size = e[si >= 0 && si < e.length ? si : 1] * zoom;
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(cx, cy);
        p.scale(size * 0.5f);
        switch (style) {
            case 1: {
                K18.a(g, 0.0f, 0.0f, 3.4f, -1728053248);
                K18.a(g, 0.0f, 0.0f, 2.4f, color);
                break;
            }
            case 2: {
                K16.a(g, -9, -1, 6, 2, color);
                K16.a(g, 3, -1, 6, 2, color);
                K16.a(g, -1, -9, 2, 6, color);
                K16.a(g, -1, 3, 2, 6, color);
                break;
            }
            case 3: {
                K16.a(g, 7.0f, color);
                K18.a(g, 0.0f, 0.0f, 1.6f, -1728053248);
                K18.a(g, 0.0f, 0.0f, 1.0f, color);
                break;
            }
            case 4: {
                int[][] bars;
                for (int[] b : bars = new int[][]{{-8, -8, 4, 2}, {-8, -8, 2, 4}, {4, -8, 4, 2}, {6, -8, 2, 4}, {-8, 6, 4, 2}, {-8, 4, 2, 4}, {4, 6, 4, 2}, {6, 4, 2, 4}, {-1, -1, 2, 2}}) {
                    K18.a(g, b[0] - 1, b[1] - 1, b[2] + 2, b[3] + 2, -1728053248);
                }
                for (int[] b : bars) {
                    K18.a(g, b[0], b[1], b[2], b[3], color);
                }
                break;
            }
            case 5: {
                for (int i = 2; i <= 7; ++i) {
                    K16.a(g, i, i, color);
                    K16.a(g, -i - 1, i, color);
                    K16.a(g, i, -i - 1, color);
                    K16.a(g, -i - 1, -i - 1, color);
                }
                break;
            }
        }
        p.popMatrix();
    }

    private static void a(class_332 g, int x, int y, int w, int h, int color) {
        K18.a(g, x - 1, y - 1, w + 2, h + 2, -1728053248);
        K18.a(g, x, y, w, h, color);
    }

    private static void a(class_332 g, int x, int y, int color) {
        K18.a(g, x - 1, y - 1, 3, 3, -1728053248);
        K18.a(g, x, y, 2, 2, color);
    }

    private static void a(class_332 g, float r, int color) {
        double a;
        int i;
        int steps = 40;
        for (i = 0; i < steps; ++i) {
            a = Math.PI * 2 * (double)i / (double)steps;
            K18.a(g, Math.round((float)Math.cos(a) * r) - 1, Math.round((float)Math.sin(a) * r) - 1, 3, 3, -1728053248);
        }
        for (i = 0; i < steps; ++i) {
            a = Math.PI * 2 * (double)i / (double)steps;
            K18.a(g, Math.round((float)Math.cos(a) * r) - 1, Math.round((float)Math.sin(a) * r) - 1, 2, 2, color);
        }
    }
}

