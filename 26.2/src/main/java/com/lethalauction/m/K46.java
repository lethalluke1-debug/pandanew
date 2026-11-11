/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.entity.player.Player
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K48;
import com.lethalauction.m.K6;
import com.lethalauction.m.K9;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;

public final class K46 {
    private static boolean a;
    private static long b;
    private static int c;

    private K46() {
    }

    public static void a(Minecraft mc) {
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

    private static void b(Minecraft mc) {
        if (mc.level == null) {
            return;
        }
        ++c;
        for (Player p : mc.level.players()) {
            String cos;
            if (p == mc.player || (cos = K6.a(p.getId())).isEmpty() || !cos.contains("headphones") || (c + p.getId()) % 9 != 0) continue;
            float yaw = p.yBodyRot * ((float)Math.PI / 180);
            double side = (Math.random() < 0.5 ? -1.0 : 1.0) * (0.3 + Math.random() * 0.06);
            double fwd = (Math.random() - 0.5) * 0.18;
            double x = p.getX() - Math.cos(yaw) * side + Math.sin(yaw) * fwd;
            double z = p.getZ() - Math.sin(yaw) * side - Math.cos(yaw) * fwd;
            double y = p.getEyeY() + 0.18 + Math.random() * 0.14;
            mc.level.addParticle((ParticleOptions)ParticleTypes.NOTE, x, y, z, Math.random(), 0.0, 0.0);
        }
    }
}

