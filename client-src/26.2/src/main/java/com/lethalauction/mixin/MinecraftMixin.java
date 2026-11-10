/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K12;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public class MinecraftMixin {
    @Inject(method={"startAttack"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$hitFigure(CallbackInfoReturnable<Boolean> cir) {
        if (K12.b((Minecraft)(Object)this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"continueAttack"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$noMineThroughFigure(boolean leftClick, CallbackInfo ci) {
        if (leftClick && K12.c((Minecraft)(Object)this)) {
            ci.cancel();
        }
    }
}

