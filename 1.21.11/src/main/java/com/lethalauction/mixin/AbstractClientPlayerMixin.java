/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12079$class_10726
 *  net.minecraft.class_12079$class_12081
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_742
 *  net.minecraft.class_7920
 *  net.minecraft.class_8685
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K4;
import com.lethalauction.m.K49;
import net.minecraft.class_12079;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_742;
import net.minecraft.class_7920;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_742.class})
public abstract class AbstractClientPlayerMixin {
    @Inject(method={"method_52814"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethalauction$customCape(CallbackInfoReturnable<class_8685> cir) {
        class_2960 id;
        class_12079.class_12081 body;
        if (!K37.e()) {
            return;
        }
        class_742 self = (class_742)(Object)this;
        boolean you = self == class_310.method_1551().field_1724;
        class_8685 skin = (class_8685)cir.getReturnValue();
        K49.C98 picked = you ? K49.c() : null;
        Object object = body = picked != null ? new class_12079.class_10726(picked.b(), picked.b()) : skin.comp_1626();
        class_7920 model = picked != null ? (picked.c() ? class_7920.field_41122 : class_7920.field_41123) : skin.comp_1629();
        class_2960 class_29602 = id = you ? K4.d() : K38.a(self.method_5667(), K37.f());
        if (id != null) {
            class_12079.class_10726 texture = new class_12079.class_10726(id, id);
            cir.setReturnValue(new class_8685((class_12079.class_12081)body, (class_12079.class_12081)texture, (class_12079.class_12081)texture, model, skin.comp_1630()));
        } else if (picked != null) {
            cir.setReturnValue(new class_8685((class_12079.class_12081)body, skin.comp_1627(), skin.comp_1628(), model, skin.comp_1630()));
        }
    }
}

