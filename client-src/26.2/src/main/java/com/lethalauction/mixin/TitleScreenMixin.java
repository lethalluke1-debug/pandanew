/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.LogoRenderer
 *  net.minecraft.client.gui.components.SplashRenderer
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K33;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TitleScreen.class})
public abstract class TitleScreenMixin
extends Screen {
    protected TitleScreenMixin() {
        super(null);
    }

    @Inject(method={"extractRenderState"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/screens/TitleScreen;extractPanorama(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V", shift=At.Shift.AFTER)})
    private void lethal$background(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (K33.a()) {
            K33.a(g, this.width, this.height);
        }
    }

    @Redirect(method={"extractRenderState"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/LogoRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IF)V")})
    private void lethal$logo(LogoRenderer renderer, GuiGraphicsExtractor g, int height, float alpha) {
        if (!K33.a()) {
            renderer.extractRenderState(g, height, alpha);
        }
    }

    @Inject(method={"extractRenderState"}, at={@At(value="TAIL")})
    private void lethal$overlay(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        K33.b(g, this.width, this.height);
    }

    @Redirect(method={"extractRenderState"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/SplashRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;ILnet/minecraft/client/gui/Font;F)V")})
    private void lethal$splash(SplashRenderer splash, GuiGraphicsExtractor g, int x, Font font, float alpha) {
        if (!K33.a()) {
            splash.extractRenderState(g, x, font, alpha);
        }
    }

    @Redirect(method={"extractRenderState"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V")})
    private void lethal$version(GuiGraphicsExtractor g, Font font, String text, int x, int y, int color) {
        if (!K33.a()) {
            g.text(font, text, x, y, color);
        }
    }
}

