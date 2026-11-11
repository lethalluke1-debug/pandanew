/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1921
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 */
package com.lethalauction.m;

import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;

public final class K51
implements class_4597 {
    public static final K51 a = new K51();
    private class_4597 b;

    public void a(class_4597 src) {
        this.b = src;
    }

    public class_4588 method_73477(class_1921 type) {
        return this.b.method_73477(type);
    }

    public void b(class_4597 src) {
        if (src instanceof class_4597.class_4598) {
            class_4597.class_4598 bs = (class_4597.class_4598)src;
            bs.method_22993();
        }
    }
}

