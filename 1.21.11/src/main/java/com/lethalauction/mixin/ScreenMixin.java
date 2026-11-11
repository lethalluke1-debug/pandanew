/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K33;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_437.class})
public abstract class ScreenMixin {
    @Inject(method={"method_57735(Lnet/minecraft/class_332;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$menuBackground(class_332 g, CallbackInfo ci) {
        class_437 self = (class_437)(Object)this;
        if (!K33.a() || class_310.method_1551().field_1687 != null || self instanceof class_442) {
            return;
        }
        K33.a(g, self.field_22789, self.field_22790);
        ci.cancel();
    }
}

