/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4184
 *  net.minecraft.class_757
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K40;
import net.minecraft.class_4184;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_757.class})
public abstract class GameRendererMixin {
    @Inject(method={"method_3196"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethal$zoom(class_4184 camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Float> cir) {
        double factor = K40.e();
        if (factor > 1.0) {
            cir.setReturnValue(Float.valueOf((float)((double)((Float)cir.getReturnValue()).floatValue() / factor)));
        }
    }
}

