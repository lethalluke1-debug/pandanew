/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 */
package com.lethalauction.m;

import net.minecraft.class_332;

public interface K24 {
    public String a();

    public float b();

    public float c();

    public float d();

    public float e();

    public void a(float var1, float var2);

    public float f();

    public void a(float var1);

    public void g();

    public void a(class_332 var1, float var2, float var3);

    default public float a(int guiWidth) {
        return this.d() * Math.max(1.0f, (float)guiWidth - this.b());
    }

    default public float b(int guiHeight) {
        return this.e() * Math.max(1.0f, (float)guiHeight - this.c());
    }
}

