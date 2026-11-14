/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K33;
import com.lethalauction.m.K9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Screen.class})
public abstract class ScreenMixin {
    @Inject(method={"extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$menuBackground(GuiGraphicsExtractor g, CallbackInfo ci) {
        Screen self = (Screen)(Object)this;
        if (!K33.a() || Minecraft.getInstance().level != null || self instanceof TitleScreen) {
            return;
        }
        K33.a(g, self.width, self.height);
        ci.cancel();
    }

    @Inject(method = {"extractBackground"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void lethal$seeThroughGui(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // Removes only the dark dim/blur behind menus (the transparent panel
        // texture still draws on top). For a container screen this fires via its
        // super.extractBackground call, so the panel's own blit is kept.
        if (K9.seeThroughGui && Minecraft.getInstance().level != null) {
            ci.cancel();
        }
    }
}

