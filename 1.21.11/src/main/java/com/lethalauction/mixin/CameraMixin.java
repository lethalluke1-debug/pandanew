/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4184
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K40;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={class_4184.class})
public abstract class CameraMixin {
    @Shadow
    protected abstract void method_19325(float var1, float var2);

    @Redirect(method={"method_19321"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_4184;method_19325(FF)V"))
    private void lethal$freelook(class_4184 camera, float yRot, float xRot) {
        if (K40.a()) {
            this.method_19325(K40.b(), K40.c());
        } else {
            this.method_19325(yRot, xRot);
        }
    }
}

