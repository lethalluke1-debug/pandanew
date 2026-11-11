/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K24;
import com.lethalauction.m.K9;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class K19
extends class_437 {
    private final class_437 a;
    private final K24 b;
    private boolean c;
    private double d;
    private double e;

    public K19(class_437 parent, K24 hud) {
        super((class_2561)class_2561.method_43470((String)("Move " + hud.a())));
        this.a = parent;
        this.b = hud;
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float partialTick) {
        K18.a(g, 0, 0, this.field_22789, this.field_22790, 0x66000000);
        for (int x = 0; x < this.field_22789; x += 20) {
            K18.a(g, x, 0, 1, this.field_22790, 0x10FFFFFF);
        }
        for (int y = 0; y < this.field_22790; y += 20) {
            K18.a(g, 0, y, this.field_22789, 1, 0x10FFFFFF);
        }
        float cx = (float)this.field_22789 / 2.0f;
        K18.a(g, Math.round(cx - 150.0f), 10, 300, 38, 10, -435482856);
        K18.b(g, "Drag " + this.b.a() + " to move it", cx, 23.0f, 10.0f, -1, K18.c);
        K18.b(g, "Use - and + (or scroll) to resize  \u00b7  Esc or Done to save", cx, 37.0f, 8.5f, -7695450, K18.b);
        float x = this.b.a(this.field_22789);
        float y = this.b.b(this.field_22790);
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
        float by = this.field_22790 - 34;
        this.a(g, cx - 176.0f, by, 26, 22, "-", mouseX, mouseY);
        K18.a(g, Math.round(cx - 146.0f), Math.round(by), 64, 22, 7, -435087321);
        K18.b(g, "Size " + Math.round(this.b.f() * 100.0f) + "%", cx - 114.0f, by + 11.0f, 9.0f, -1, K18.b);
        this.a(g, cx - 78.0f, by, 26, 22, "+", mouseX, mouseY);
        this.a(g, cx - 44.0f, by, 100, 22, "Reset", mouseX, mouseY);
        this.a(g, cx + 62.0f, by, 100, 22, "Done", mouseX, mouseY);
    }

    private void a(class_332 g, float x, float y, int w, int h, String label, int mouseX, int mouseY) {
        boolean hover = (float)mouseX >= x && (float)mouseX < x + (float)w && (float)mouseY >= y && (float)mouseY < y + (float)h;
        K18.a(g, Math.round(x), Math.round(y), w, h, 7, hover ? -14715649 : -435087321, hover ? -11559937 : -14011056);
        K18.b(g, label, x + (float)w / 2.0f, y + (float)h / 2.0f, 9.5f, -1, K18.c);
    }

    private boolean a(double mx, double my) {
        float x = this.b.a(this.field_22789);
        float y = this.b.b(this.field_22790);
        return mx >= (double)x && mx < (double)(x + this.b.b()) && my >= (double)y && my < (double)(y + this.b.c());
    }

    public boolean method_25402(class_11909 event, boolean doubleClick) {
        double mx = event.comp_4798();
        double my = event.comp_4799();
        float cx = (float)this.field_22789 / 2.0f;
        float by = this.field_22790 - 34;
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
                this.method_25419();
                return true;
            }
        }
        if (event.method_74245() == 0 && this.a(mx, my)) {
            this.c = true;
            this.d = mx - (double)this.b.a(this.field_22789);
            this.e = my - (double)this.b.b(this.field_22790);
            return true;
        }
        return super.method_25402(event, doubleClick);
    }

    public boolean method_25403(class_11909 event, double dx, double dy) {
        if (this.c) {
            float maxX = Math.max(1.0f, (float)this.field_22789 - this.b.b());
            float maxY = Math.max(1.0f, (float)this.field_22790 - this.b.c());
            this.b.a((float)Math.max(0.0, Math.min(1.0, (event.comp_4798() - this.d) / (double)maxX)), (float)Math.max(0.0, Math.min(1.0, (event.comp_4799() - this.e) / (double)maxY)));
            return true;
        }
        return super.method_25403(event, dx, dy);
    }

    public boolean method_25406(class_11909 event) {
        this.c = false;
        return super.method_25406(event);
    }

    public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.a(scrollY * 0.1);
        return true;
    }

    private void a(double by) {
        this.b.a((float)Math.max(0.5, Math.min(2.5, (double)Math.round(((double)this.b.f() + by) * 10.0) / 10.0)));
    }

    public void method_25419() {
        K9.b();
        this.field_22787.method_1507(this.a);
    }
}

