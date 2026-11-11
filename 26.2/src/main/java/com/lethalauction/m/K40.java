/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public final class K40 {
    public static KeyMapping a;
    public static KeyMapping b;
    private static boolean c;
    private static boolean d;
    private static float e;
    private static float f;
    private static CameraType g;
    private static boolean h;
    private static boolean i;
    private static int j;
    private static double k;
    private static long l;
    private static final double m = 1.0;
    private static final double n = 0.5;
    private static final double o = 1.5;
    private static double p;
    private static double q;

    private K40() {
    }

    public static void a(Minecraft mc) {
        boolean wantZoom;
        boolean wantLook;
        boolean allowed;
        boolean bl = allowed = K37.b() && mc.player != null;
        if (K9.L) {
            while (a.consumeClick()) {
                d = !d;
            }
            wantLook = d;
        } else {
            while (a.consumeClick()) {
            }
            d = false;
            wantLook = a.isDown();
        }
        if ((wantLook &= allowed && K9.K) && !c) {
            c = true;
            e = mc.player.getYRot();
            f = mc.player.getXRot();
            g = mc.options.getCameraType();
            if (K9.M && g.isFirstPerson()) {
                mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
        } else if (!wantLook && c) {
            c = false;
            d = false;
            if (g != null) {
                mc.options.setCameraType(g);
            }
        }
        if (K9.O) {
            while (b.consumeClick()) {
                i = !i;
            }
            wantZoom = i;
        } else {
            while (b.consumeClick()) {
            }
            i = false;
            wantZoom = b.isDown();
        }
        if (!(wantZoom &= allowed && K9.N) && h) {
            j = 0;
            i = false;
        }
        if (wantZoom != h && K9.S) {
            mc.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)(wantZoom ? SoundEvents.SPYGLASS_USE : SoundEvents.SPYGLASS_STOP_USING), (float)1.0f, (float)1.0f));
        }
        h = wantZoom;
    }

    public static boolean a() {
        return c;
    }

    public static float b() {
        return e;
    }

    public static float c() {
        return f;
    }

    public static void a(double dx, double dy) {
        e += (float)dx * 0.15f;
        f = Mth.clamp((float)(f + (float)dy * 0.15f), (float)-90.0f, (float)90.0f);
    }

    public static boolean d() {
        return h;
    }

    public static boolean a(double amount) {
        if (!h || !K9.Q || amount == 0.0) {
            return false;
        }
        j = Mth.clamp((int)(j + (amount > 0.0 ? 1 : -1)), (int)-3, (int)10);
        return true;
    }

    private static double b(double t) {
        return t >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * t);
    }

    public static double e() {
        long now = System.nanoTime();
        double dt = l == 0L ? 0.0 : Math.min(0.1, (double)(now - l) / 1.0E9);
        l = now;
        boolean smooth = K9.R;
        p = h ? (smooth ? Math.min(1.0, p + dt / 1.0) : 1.0) : (smooth ? Math.max(0.0, p - dt / 0.5) : 0.0);
        double eased = h ? K40.b(p) : 1.0 - K40.b(1.0 - p);
        double scrollTarget = Math.pow(1.5, j);
        q += (scrollTarget - q) * (smooth ? 1.0 - Math.exp(-dt * 10.0) : 1.0);
        double full = Math.max(1.0, (double)K9.P * q);
        k = 1.0 + (full - 1.0) * eased;
        if (!h && p <= 0.0) {
            q = 1.0;
        }
        return k;
    }

    public static double f() {
        return k > 1.0 ? 1.0 / k : 1.0;
    }

    static {
        k = 1.0;
        q = 1.0;
    }
}

