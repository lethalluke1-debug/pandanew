/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12076
 *  net.minecraft.class_2874$class_12326
 *  net.minecraft.class_761
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K50;
import com.lethalauction.m.K9;
import net.minecraft.class_12076;
import net.minecraft.class_2874;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_761.class})
public abstract class LevelRendererMixin {
    @Inject(method={"method_62204"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$hideClouds(CallbackInfo ci) {
        if (K9.r && K50.a()) {
            ci.cancel();
        }
    }

    @Redirect(method={"method_62200"}, at=@At(value="FIELD", target="Lnet/minecraft/class_12076;field_64464:Lnet/minecraft/class_2874$class_12326;", opcode=180), require=0)
    private class_2874.class_12326 lethalauction$forceSkyInNether(class_12076 state) {
        return K50.a() ? class_2874.class_12326.field_64386 : state.field_64464;
    }
}

