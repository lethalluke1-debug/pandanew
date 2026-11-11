/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_11890
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K14;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import net.minecraft.class_10017;
import net.minecraft.class_11890;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_897.class})
public abstract class EntityRendererMixin {
    @Inject(method={"method_62354(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$carrierIcon(class_1297 entity, class_10017 state, float partialTicks, CallbackInfo ci) {
        boolean fakeSelf;
        if (state.field_53337 == null || entity instanceof class_11890 || !entity.method_5767()) {
            return;
        }
        boolean bl = fakeSelf = K9.m && K37.e();
        if (!K14.b() && !fakeSelf) {
            return;
        }
        class_1657 owner = K14.a(entity);
        if (owner == null) {
            return;
        }
        if (fakeSelf && owner == class_310.method_1551().field_1724) {
            state.field_53337 = class_2561.method_43470((String)"Player");
            return;
        }
        if (K14.a(owner)) {
            state.field_53337 = K14.a(state.field_53337, owner.method_5667());
        }
    }
}

