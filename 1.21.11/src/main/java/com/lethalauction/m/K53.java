/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4597
 *  org.joml.Matrix4f
 */
package com.lethalauction.m;

import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_4597;
import org.joml.Matrix4f;

public final class K53 {
    private K53() {
    }

    public static void a(class_327 font, class_2561 text, float x, float y, int color, boolean shadow, Matrix4f matrix, class_4597 buffers, class_327.class_6415 mode, int light) {
        font.method_27522(text, x, y, color, shadow, matrix, buffers, mode, 0, light);
    }
}

