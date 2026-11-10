/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K32;
import com.lethalauction.m.K35;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public class K26
extends Screen {
    private static final float a = 1036.0f;
    private static final float b = 518.0f;
    private final Screen c;
    private final String d;
    private final String e;
    private final long f;
    private final long g = System.currentTimeMillis();
    private float h = 1.0f;
    private float i;
    private float j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private int r;

    public K26(Screen parent, String command, String recipient, long amount) {
        super((Component)Component.literal((String)"Confirm payment"));
        this.c = parent;
        this.d = command;
        this.e = recipient;
        this.f = amount;
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void a() {
        K35.b(this.d);
        this.minecraft.gui.setScreen(this.c);
    }

    private void b() {
        this.minecraft.gui.setScreen(this.c);
    }

    private float a(double mx) {
        return (float)((mx - (double)this.i) / (double)this.h);
    }

    private float b(double my) {
        return (float)((my - (double)this.j) / (double)this.h);
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        long now = System.currentTimeMillis();
        float open = K26.a(Math.min(1.0f, (float)(now - this.g) / 300.0f));
        int accent = K32.a();
        K18.a(g, 0, 0, this.width, this.height, K18.a(-16777216, 0.6f * open));
        this.h = Math.min((float)this.width * 0.564f / 1036.0f, (float)this.height * 0.94f / 518.0f);
        this.i = ((float)this.width - 1036.0f * this.h) / 2.0f;
        this.j = ((float)this.height - 518.0f * this.h) / 2.0f;
        float mdx = this.a(mouseX);
        float mdy = this.b(mouseY);
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(this.i, this.j);
        p.scale(this.h);
        K18.h = open;
        float pulse = 0.5f + 0.5f * (float)Math.sin((double)now / 420.0);
        for (int i = 5; i >= 1; --i) {
            K18.a(g, -i * 2, -i * 2, 1036 + 4 * i, 518 + 4 * i, 22 + i, 0, K18.a(accent, (0.14f + 0.16f * pulse) / (float)i));
        }
        K18.a(g, 0, 0, 1036, 518, 22, -200997109, K18.a(accent, 0.85f));
        float cx = 518.0f;
        K18.a(g, cx, 150.0f, 46.0f, K18.a(accent, 0.16f));
        K18.a(g, cx, 150.0f, 34.0f, accent);
        K18.b(g, "!", cx, 150.0f, 46.0f, -15657441, K18.c);
        K18.b(g, "Confirm payment", cx, 234.0f, 30.0f, -789258, K18.c);
        K18.b(g, "You're about to pay " + this.e, cx, 274.0f, 16.0f, -6577748, K18.a);
        K18.b(g, "$" + K35.a(this.f), cx, 332.0f, 52.0f, accent, K18.c);
        this.m = 300;
        this.n = 54;
        this.k = (int)(cx - (float)this.m - 12.0f);
        this.l = 408;
        this.q = 300;
        this.r = 54;
        this.o = (int)(cx + 12.0f);
        this.p = 408;
        boolean hc = K26.a(mdx, mdy, this.k, this.l, this.m, this.n);
        boolean hx = K26.a(mdx, mdy, this.o, this.p, this.q, this.r);
        K18.a(g, this.k, this.l, this.m, this.n, 12, K18.a(accent, -16777216, hc ? 0.08f : 0.22f), accent);
        K18.b(g, "Confirm & Pay", (float)this.k + (float)this.m / 2.0f, (float)this.l + (float)this.n / 2.0f, 18.0f, -15657441, K18.c);
        K18.a(g, this.o, this.p, this.q, this.r, 12, hx ? -14012614 : -14670804, -12959408);
        K18.b(g, "Cancel (Esc)", (float)this.o + (float)this.q / 2.0f, (float)this.p + (float)this.r / 2.0f, 18.0f, -789258, K18.c);
        K18.h = 1.0f;
        p.popMatrix();
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            float my;
            float mx = this.a(event.x());
            if (K26.a(mx, my = this.b(event.y()), this.k, this.l, this.m, this.n)) {
                this.a();
                return true;
            }
            if (K26.a(mx, my, this.o, this.p, this.q, this.r)) {
                this.b();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean keyPressed(KeyEvent event) {
        int key = event.key();
        if (key == 256) {
            this.b();
            return true;
        }
        if (key == 257 || key == 335) {
            this.a();
            return true;
        }
        return super.keyPressed(event);
    }

    private static boolean a(float mx, float my, int x, int y, int w, int h) {
        return mx >= (float)x && mx < (float)(x + w) && my >= (float)y && my < (float)(y + h);
    }

    private static float a(float t) {
        return t * t * (3.0f - 2.0f * t);
    }
}

