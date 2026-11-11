/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K20;
import com.lethalauction.m.K22;
import com.lethalauction.m.K23;
import com.lethalauction.m.K37;
import com.lethalauction.m.K39;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.joml.Matrix3x2fStack;

public class K34
extends class_437 {
    private final long a = System.currentTimeMillis();

    public K34() {
        super((class_2561)class_2561.method_43470((String)"Lethal Client updating"));
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25393() {
        if (!K37.g()) {
            if (K37.b()) {
                this.field_22787.method_1507((class_437)new K20());
            } else if (K37.l() != K37.C91.CHECKING && K37.l() != K37.C91.OFFLINE) {
                this.field_22787.method_1507((class_437)new K22(null, K20::new));
            }
        }
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float partialTick) {
        long now = System.currentTimeMillis();
        float menuScale = Math.min((float)this.field_22789 * 0.564f / 1036.0f, (float)this.field_22790 * 0.94f / 518.0f);
        float pw = 1036.0f * menuScale;
        float ph = 518.0f * menuScale;
        float scale = Math.min(pw / 620.0f, ph / 330.0f);
        boolean ready = K39.a() == K39.C92.READY;
        int accent = ready ? -13315969 : -14715649;
        float open = 1.0f - (float)Math.pow(1.0f - Math.min(1.0f, (float)(now - this.a) / 550.0f), 3.0);
        K18.h = Math.min(1.0f, 0.35f + open);
        int x = Math.round(((float)this.field_22789 - pw) / 2.0f);
        int y = Math.round(((float)this.field_22790 - ph) / 2.0f);
        int w = Math.round(pw);
        int h = Math.round(ph);
        int r = Math.max(4, Math.round(14.0f * pw / 1036.0f));
        g.method_44379(x - 6, y - 6, x + w + 6, y - 6 + Math.round((float)(h + 12) * open));
        float pulse = 0.5f + 0.5f * (float)Math.sin((double)now / 420.0);
        for (int i = 4; i >= 1; --i) {
            K18.a(g, x - i, y - i, w + 2 * i, h + 2 * i, r + i, 0, K18.a(accent, (0.18f + 0.2f * pulse) / (float)i));
        }
        K18.a(g, x, y, w, h, r, -200997109, K18.a(accent, 0.75f));
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate((float)this.field_22789 / 2.0f, (float)this.field_22790 / 2.0f);
        p.scale(scale);
        float t = (float)(now - this.a) / 1000.0f;
        float spin = ready ? 0.0f : t;
        K23.a(g, -14.0f, -34.0f, 26.0f, 10, spin * 1.1f, accent);
        K23.a(g, 26.0f, -6.0f, 15.0f, 8, -spin * 1.9f + 0.2f, K18.a(accent, -1, 0.25f));
        if (!ready) {
            for (int i = 0; i < 3; ++i) {
                double a = (double)t * 2.2 + (double)i * Math.PI * 2.0 / 3.0;
                float sx = (float)Math.cos(a) * 48.0f;
                float sy = -26.0f + (float)Math.sin(a) * 26.0f;
                K18.a(g, sx, sy, 1.8f, K18.a(accent, 0.35f + 0.5f * (float)(0.5 + 0.5 * Math.sin(a * 3.0))));
            }
        }
        float rise = (1.0f - open) * 8.0f;
        K18.b(g, "Lethal Client", 0.0f, 34.0f + rise, 16.0f, -789258, K18.c);
        String dots = ".".repeat((int)(now / 450L % 4L));
        if (ready) {
            K18.b(g, "Update ready", 0.0f, 54.0f + rise, 11.0f, -6577748, K18.a);
            K18.b(g, "Restart Minecraft to finish.", 0.0f, 70.0f + rise, 10.0f, -10656654, K18.a);
        } else {
            K18.b(g, "Updating" + dots, 0.0f, 54.0f + rise, 11.0f, -6577748, K18.a);
            K18.b(g, "Be back soon.", 0.0f, 70.0f + rise, 10.0f, -10656654, K18.a);
        }
        K18.b(g, "Press Esc to go back", 0.0f, ph / 2.0f / scale - 20.0f, 10.0f, -10656654, K18.b);
        p.popMatrix();
        g.method_44380();
        K18.h = 1.0f;
    }
}

