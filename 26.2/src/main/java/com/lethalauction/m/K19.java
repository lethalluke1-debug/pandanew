/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K24;
import com.lethalauction.m.K9;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class K19
extends Screen {
    private final Screen a;
    private final K24 b;
    private boolean c;
    private double d;
    private double e;

    public K19(Screen parent, K24 hud) {
        super((Component)Component.literal((String)("Move " + hud.a())));
        this.a = parent;
        this.b = hud;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        K18.a(g, 0, 0, this.width, this.height, 0x66000000);
        for (int x = 0; x < this.width; x += 20) {
            K18.a(g, x, 0, 1, this.height, 0x10FFFFFF);
        }
        for (int y = 0; y < this.height; y += 20) {
            K18.a(g, 0, y, this.width, 1, 0x10FFFFFF);
        }
        float cx = (float)this.width / 2.0f;
        K18.a(g, Math.round(cx - 150.0f), 10, 300, 38, 10, -435482856);
        K18.b(g, "Drag " + this.b.a() + " to move it", cx, 23.0f, 10.0f, -1, K18.c);
        K18.b(g, "Use - and + (or scroll) to resize  \u00b7  Esc or Done to save", cx, 37.0f, 8.5f, -7695450, K18.b);
        float x = this.b.a(this.width);
        float y = this.b.b(this.height);
        float w = this.b.b();
        float h = this.b.c();
        this.b.a(g, x, y);
        boolean hover = this.c || this.a(mouseX, mouseY);
        int edge = hover ? -11559937 : -1711276033;
        int i = 0;
        while ((float)i < w) {
            K18.a(g, Math.round(x + (float)i), Math.round(y - 3.0f), 3, 1, edge);
            K18.a(g, Math.round(x + (float)i), Math.round(y + h + 2.0f), 3, 1, edge);
            i += 6;
        }
        i = 0;
        while ((float)i < h) {
            K18.a(g, Math.round(x - 3.0f), Math.round(y + (float)i), 1, 3, edge);
            K18.a(g, Math.round(x + w + 2.0f), Math.round(y + (float)i), 1, 3, edge);
            i += 6;
        }
        float by = this.height - 34;
        this.a(g, cx - 176.0f, by, 26, 22, "-", mouseX, mouseY);
        K18.a(g, Math.round(cx - 146.0f), Math.round(by), 64, 22, 7, -435087321);
        K18.b(g, "Size " + Math.round(this.b.f() * 100.0f) + "%", cx - 114.0f, by + 11.0f, 9.0f, -1, K18.b);
        this.a(g, cx - 78.0f, by, 26, 22, "+", mouseX, mouseY);
        this.a(g, cx - 44.0f, by, 100, 22, "Reset", mouseX, mouseY);
        this.a(g, cx + 62.0f, by, 100, 22, "Done", mouseX, mouseY);
    }

    private void a(GuiGraphicsExtractor g, float x, float y, int w, int h, String label, int mouseX, int mouseY) {
        boolean hover = (float)mouseX >= x && (float)mouseX < x + (float)w && (float)mouseY >= y && (float)mouseY < y + (float)h;
        K18.a(g, Math.round(x), Math.round(y), w, h, 7, hover ? -14715649 : -435087321, hover ? -11559937 : -14011056);
        K18.b(g, label, x + (float)w / 2.0f, y + (float)h / 2.0f, 9.5f, -1, K18.c);
    }

    private boolean a(double mx, double my) {
        float x = this.b.a(this.width);
        float y = this.b.b(this.height);
        return mx >= (double)x && mx < (double)(x + this.b.b()) && my >= (double)y && my < (double)(y + this.b.c());
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();
        float cx = (float)this.width / 2.0f;
        float by = this.height - 34;
        if (my >= (double)by && my < (double)(by + 22.0f)) {
            if (mx >= (double)(cx - 176.0f) && mx < (double)(cx - 150.0f)) {
                this.a(-0.1);
                return true;
            }
            if (mx >= (double)(cx - 78.0f) && mx < (double)(cx - 52.0f)) {
                this.a(0.1);
                return true;
            }
            if (mx >= (double)(cx - 44.0f) && mx < (double)(cx + 56.0f)) {
                this.b.g();
                return true;
            }
            if (mx >= (double)(cx + 62.0f) && mx < (double)(cx + 162.0f)) {
                this.onClose();
                return true;
            }
        }
        if (event.button() == 0 && this.a(mx, my)) {
            this.c = true;
            this.d = mx - (double)this.b.a(this.width);
            this.e = my - (double)this.b.b(this.height);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.c) {
            float maxX = Math.max(1.0f, (float)this.width - this.b.b());
            float maxY = Math.max(1.0f, (float)this.height - this.b.c());
            this.b.a((float)Math.max(0.0, Math.min(1.0, (event.x() - this.d) / (double)maxX)), (float)Math.max(0.0, Math.min(1.0, (event.y() - this.e) / (double)maxY)));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        this.c = false;
        return super.mouseReleased(event);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.a(scrollY * 0.1);
        return true;
    }

    private void a(double by) {
        this.b.a((float)Math.max(0.5, Math.min(2.5, (double)Math.round(((double)this.b.f() + by) * 10.0) / 10.0)));
    }

    public void onClose() {
        K9.b();
        this.minecraft.gui.setScreen(this.a);
    }
}

