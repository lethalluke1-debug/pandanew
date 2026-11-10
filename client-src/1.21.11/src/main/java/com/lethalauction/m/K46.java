/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_310
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K48;
import com.lethalauction.m.K6;
import com.lethalauction.m.K9;
import net.minecraft.class_1657;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_310;

public final class K46 {
    private static boolean a;
    private static long b;
    private static int c;

    private K46() {
    }

    public static void a(class_310 mc) {
        long now;
        boolean on;
        boolean bl = on = K9.U && K37.b() && K48.a();
        if (on && !a) {
            K48.c();
            a = true;
        } else if (!on && a) {
            K48.d();
            a = false;
        } else if (on && a && !K48.b() && (now = System.currentTimeMillis()) - b > 3000L) {
            b = now;
            K48.d();
            K48.c();
        }
        if (!on) {
            return;
        }
        K46.b(mc);
    }

    private static void b(class_310 mc) {
        if (mc.field_1687 == null) {
            return;
        }
        ++c;
        for (class_1657 p : mc.field_1687.method_18456()) {
            String cos;
            if (p == mc.field_1724 || (cos = K6.a(p.method_5628())).isEmpty() || !cos.contains("headphones") || (c + p.method_5628()) % 9 != 0) continue;
            float yaw = p.field_6283 * ((float)Math.PI / 180);
            double side = (Math.random() < 0.5 ? -1.0 : 1.0) * (0.3 + Math.random() * 0.06);
            double fwd = (Math.random() - 0.5) * 0.18;
            double x = p.method_23317() - Math.cos(yaw) * side + Math.sin(yaw) * fwd;
            double z = p.method_23321() - Math.sin(yaw) * side - Math.cos(yaw) * fwd;
            double y = p.method_23320() + 0.18 + Math.random() * 0.14;
            mc.field_1687.method_8406((class_2394)class_2398.field_11224, x, y, z, Math.random(), 0.0, 0.0);
        }
    }
}

