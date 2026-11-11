/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_368
 *  net.minecraft.class_368$class_369
 *  net.minecraft.class_374
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;

public class K21
implements class_368 {
    private static final class_2960 a = class_2960.method_60656((String)"toast/advancement");
    private static final class_2960 b = K18.a("textures/font/badge_member.png");
    private final class_2561 c;
    private final class_2561 d;
    private final long e;
    private class_368.class_369 f = class_368.class_369.field_2209;

    private K21(class_2561 title, class_2561 description, long displayMs) {
        this.c = title;
        this.d = description;
        this.e = displayMs;
    }

    public static void a(String title, String description) {
        K21.a(title, description, 5000L);
    }

    public static void a(String title, String description, long displayMs) {
        class_310.method_1551().method_1566().method_1999((class_368)new K21((class_2561)class_2561.method_43470((String)title), (class_2561)class_2561.method_43470((String)description), displayMs));
    }

    public class_368.class_369 method_61988() {
        return this.f;
    }

    public void method_61989(class_374 manager, long fullyVisibleForMs) {
        this.f = (double)fullyVisibleForMs >= (double)this.e * manager.method_48221() ? class_368.class_369.field_2209 : class_368.class_369.field_2210;
    }

    public void method_1986(class_332 g, class_327 font, long fullyVisibleForMs) {
        g.method_52706(class_10799.field_56883, a, 0, 0, this.method_29049(), this.method_29050());
        g.method_51439(font, this.c, 30, 7, -8530948, false);
        g.method_51439(font, this.d, 30, 18, -1, false);
        K18.a(g, b, 8, 8, 16, 16, 64, 64);
    }
}

