/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K18;
import com.lethalauction.m.K32;
import com.lethalauction.m.K33;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractButton.class})
public abstract class AbstractButtonMixin {
    @Inject(method={"extractDefaultSprite"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$rounded(GuiGraphicsExtractor g, CallbackInfo ci) {
        if (!K33.a() || Minecraft.getInstance().level != null) {
            return;
        }
        AbstractWidget w = (AbstractWidget)(Object)this;
        int x = w.getX();
        int y = w.getY();
        int ww = w.getWidth();
        int hh = w.getHeight();
        int accent = K32.a();
        boolean hover = w.isHoveredOrFocused();
        int fill = !w.isActive() ? 1712592672 : (hover ? K18.a(accent, 0.85f) : -652599767);
        int border = !w.isActive() ? 0x33FFFFFF : (hover ? accent : 0x40FFFFFF);
        int r = Math.min(hh / 2, 10);
        K18.a(g, x, y, ww, hh, r, fill, border);
        ci.cancel();
    }
}

