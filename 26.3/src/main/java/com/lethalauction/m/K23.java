/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K20;
import com.lethalauction.m.K37;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public class K23
extends Screen {
    private static final int a = -678620;
    private final long b = System.currentTimeMillis();
    private long c;

    public K23() {
        super((Component)Component.literal((String)"Lethal Client maintenance"));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick() {
        if (!K37.k() && K37.b()) {
            if (this.c == 0L) {
                this.c = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - this.c > 1200L) {
                this.minecraft.gui.setScreen((Screen)new K20());
            }
        }
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        float open;
        long now = System.currentTimeMillis();
        float menuScale = Math.min((float)this.width * 0.564f / 1036.0f, (float)this.height * 0.94f / 518.0f);
        float pw = 1036.0f * menuScale;
        float ph = 518.0f * menuScale;
        float px = ((float)this.width - pw) / 2.0f;
        float py = ((float)this.height - ph) / 2.0f;
        float scale = Math.min(pw / 620.0f, ph / 330.0f);
        boolean back = this.c != 0L;
        int accent = back ? -13315969 : -678620;
        K18.h = open = K23.a(Math.min(1.0f, (float)(now - this.b) / 400.0f));
        int x = Math.round(px);
        int y = Math.round(py);
        int w = Math.round(pw);
        int h = Math.round(ph);
        int r = Math.max(4, Math.round(14.0f * pw / 1036.0f));
        float pulse = 0.5f + 0.5f * (float)Math.sin((double)now / 420.0);
        for (int i = 4; i >= 1; --i) {
            K18.a(g, x - i, y - i, w + 2 * i, h + 2 * i, r + i, 0, K18.a(accent, (0.18f + 0.2f * pulse) / (float)i));
        }
        K18.a(g, x, y, w, h, r, -200997109, K18.a(accent, 0.75f));
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate((float)this.width / 2.0f, (float)this.height / 2.0f);
        p.scale(scale);
        float t = (float)(now - this.b) / 1000.0f;
        float spin = back ? 0.0f : t;
        K23.a(g, -14.0f, -34.0f, 26.0f, 10, spin * 1.1f, accent);
        K23.a(g, 26.0f, -6.0f, 15.0f, 8, -spin * 1.9f + 0.2f, K18.a(accent, -1, 0.25f));
        for (int i = 0; i < 3; ++i) {
            double a = (double)t * 2.2 + (double)i * Math.PI * 2.0 / 3.0;
            float sx = (float)Math.cos(a) * 48.0f;
            float sy = -26.0f + (float)Math.sin(a) * 26.0f;
            K18.a(g, sx, sy, 1.8f, K18.a(accent, 0.35f + 0.5f * (float)(0.5 + 0.5 * Math.sin(a * 3.0))));
        }
        float rise = (1.0f - open) * 8.0f;
        if (back) {
            K18.b(g, "Lethal Client", 0.0f, 34.0f + rise, 16.0f, -789258, K18.c);
            K18.b(g, "Back online, opening...", 0.0f, 54.0f + rise, 11.0f, -6577748, K18.a);
        } else {
            K18.b(g, "Lethal Client", 0.0f, 34.0f + rise, 16.0f, -789258, K18.c);
            String dots = ".".repeat((int)(now / 450L % 4L));
            K18.b(g, "Under maintenance" + dots, 0.0f, 54.0f + rise, 11.0f, -6577748, K18.a);
            K18.b(g, "It opens again by itself when it's back.", 0.0f, 70.0f + rise, 10.0f, -10656654, K18.a);
            int bw = 160;
            int by = 88;
            K18.a(g, -bw / 2, by, bw, 4, 2, -15064269);
            float k = (float)(0.5 + 0.5 * Math.sin((double)t * 2.4));
            int seg = 44;
            int sx = -bw / 2 + Math.round((float)(bw - seg) * k);
            K18.a(g, sx, by, seg, 4, 2, accent);
        }
        float bottom = ph / 2.0f / scale - 20.0f;
        K18.b(g, "Press Esc to go back", 0.0f, bottom, 10.0f, -10656654, K18.b);
        p.popMatrix();
        K18.h = 1.0f;
    }

    static void a(GuiGraphicsExtractor g, float cx, float cy, float radius, int teeth, float angle, int color) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(cx, cy);
        p.rotate(angle);
        int tw = Math.max(3, Math.round(radius * 0.42f));
        int th = Math.max(3, Math.round(radius * 0.36f));
        for (int i = 0; i < teeth; ++i) {
            p.pushMatrix();
            p.rotate((float)(Math.PI * 2 * (double)i / (double)teeth));
            K18.a(g, -tw / 2, Math.round(-radius - (float)th * 0.55f), tw, th + 2, 1, color);
            p.popMatrix();
        }
        p.popMatrix();
        K18.a(g, cx, cy, radius * 0.86f, color);
        K18.a(g, cx, cy, radius * 0.38f, -16447733);
    }

    private static float a(float k) {
        return 1.0f - (float)Math.pow(1.0f - k, 3.0);
    }
}

