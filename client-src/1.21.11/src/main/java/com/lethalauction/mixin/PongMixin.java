/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2923
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K30;
import net.minecraft.class_2923;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_634.class})
public class PongMixin {
    @Inject(method={"method_12666"}, at={@At(value="HEAD")})
    private void lethal$pong(class_2923 packet, CallbackInfo ci) {
        K30.a(packet.comp_2201());
    }
}

