/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K12;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_310.class})
public class MinecraftMixin {
    @Inject(method={"method_1536"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$hitFigure(CallbackInfoReturnable<Boolean> cir) {
        if (K12.b((class_310)(Object)this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"method_1590"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$noMineThroughFigure(boolean leftClick, CallbackInfo ci) {
        if (leftClick && K12.c((class_310)(Object)this)) {
            ci.cancel();
        }
    }
}

