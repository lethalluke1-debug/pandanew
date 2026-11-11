/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_7172
 *  net.minecraft.class_765
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K0;
import net.minecraft.class_310;
import net.minecraft.class_7172;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={class_765.class})
public abstract class LightmapMixin {
    @Redirect(method={"method_3313"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_7172;method_41753()Ljava/lang/Object;"))
    private Object lethalauction$fullbright(class_7172<?> option) {
        if (K0.a() && option == class_310.method_1551().field_1690.method_42473()) {
            return 16.0;
        }
        return option.method_41753();
    }
}

