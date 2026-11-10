/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_4264
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K18;
import com.lethalauction.m.K32;
import com.lethalauction.m.K33;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4264;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_4264.class})
public abstract class AbstractButtonMixin {
    @Inject(method={"method_75794"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$rounded(class_332 g, CallbackInfo ci) {
        if (!K33.a() || class_310.method_1551().field_1687 != null) {
            return;
        }
        class_339 w = (class_339)(Object)this;
        int x = w.method_46426();
        int y = w.method_46427();
        int ww = w.method_25368();
        int hh = w.method_25364();
        int accent = K32.a();
        boolean hover = w.method_25367();
        int fill = !w.method_37303() ? 1712592672 : (hover ? K18.a(accent, 0.85f) : -652599767);
        int border = !w.method_37303() ? 0x33FFFFFF : (hover ? accent : 0x40FFFFFF);
        int r = Math.min(hh / 2, 10);
        K18.a(g, x, y, ww, hh, r, fill, border);
        ci.cancel();
    }
}

