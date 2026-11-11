/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_11890
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K14;
import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K9;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11890;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_1007.class})
public abstract class AvatarRendererMixin {
    @Inject(method={"method_74935(Lnet/minecraft/class_11890;D)Z"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethalauction$showOwnName(class_11890 entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        class_310 mc = class_310.method_1551();
        if (!((Boolean)cir.getReturnValue()).booleanValue() && entity == mc.field_1724 && mc.method_1560() == mc.field_1724 && (K14.a() || K9.m && K37.e()) && !mc.field_1690.method_31044().method_31034() && !mc.field_1690.field_1842 && !entity.method_5767()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$nametagIcon(class_11890 entity, class_10055 state, float partialTicks, CallbackInfo ci) {
        boolean self;
        if (state.field_53337 == null) {
            return;
        }
        boolean bl = self = entity == class_310.method_1551().field_1724;
        if (self && K9.m && K37.e()) {
            state.field_53337 = class_2561.method_43470((String)"Player");
            return;
        }
        if (K14.a() && (self || K38.e(entity.method_5667()))) {
            state.field_53337 = K14.a(state.field_53337, entity.method_5667());
        }
    }
}

