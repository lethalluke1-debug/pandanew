/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K40;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float var1, float var2);

    @Redirect(method={"alignWithEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal=0)})
    private void lethal$freelookRiding(Camera camera, float yRot, float xRot) {
        this.lethal$rotate(yRot, xRot);
    }

    @Redirect(method={"alignWithEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal=1)})
    private void lethal$freelook(Camera camera, float yRot, float xRot) {
        this.lethal$rotate(yRot, xRot);
    }

    private void lethal$rotate(float yRot, float xRot) {
        if (K40.a()) {
            this.setRotation(K40.b(), K40.c());
        } else {
            this.setRotation(yRot, xRot);
        }
    }

    @Inject(method={"calculateFov"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethal$zoom(float partialTick, CallbackInfoReturnable<Float> cir) {
        double factor = K40.e();
        if (factor > 1.0) {
            cir.setReturnValue(Float.valueOf((float)((double)((Float)cir.getReturnValue()).floatValue() / factor)));
        }
    }
}

