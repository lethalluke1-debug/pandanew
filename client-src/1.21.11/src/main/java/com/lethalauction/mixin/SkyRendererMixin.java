/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_9975
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K50;
import net.minecraft.class_310;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_9975.class})
public abstract class SkyRendererMixin {
    @Inject(method={"method_62302", "method_62312"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$customSky(CallbackInfo ci) {
        if (K50.a()) {
            K50.a(class_310.method_1551().method_1522());
            ci.cancel();
        }
    }

    @Inject(method={"method_62306", "method_62307", "method_62305", "method_73244"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$hideVanillaSky(CallbackInfo ci) {
        if (K50.a()) {
            ci.cancel();
        }
    }
}

