/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_8020
 *  net.minecraft.class_8519
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K33;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_8020;
import net.minecraft.class_8519;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_442.class})
public abstract class TitleScreenMixin
extends class_437 {
    protected TitleScreenMixin() {
        super(null);
    }

    @Inject(method={"method_25394"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_442;method_57728(Lnet/minecraft/class_332;F)V", shift=At.Shift.AFTER)})
    private void lethal$background(class_332 g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (K33.a()) {
            K33.a(g, this.field_22789, this.field_22790);
        }
    }

    @Redirect(method={"method_25394"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_8020;method_48209(Lnet/minecraft/class_332;IF)V"))
    private void lethal$logo(class_8020 renderer, class_332 g, int height, float alpha) {
        if (!K33.a()) {
            renderer.method_48209(g, height, alpha);
        }
    }

    @Inject(method={"method_25394"}, at={@At(value="TAIL")})
    private void lethal$overlay(class_332 g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        K33.b(g, this.field_22789, this.field_22790);
    }

    @Redirect(method={"method_25394"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_8519;method_51453(Lnet/minecraft/class_332;ILnet/minecraft/class_327;F)V"))
    private void lethal$splash(class_8519 splash, class_332 g, int x, class_327 font, float alpha) {
        if (!K33.a()) {
            splash.method_51453(g, x, font, alpha);
        }
    }

    @Redirect(method={"method_25394"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_25303(Lnet/minecraft/class_327;Ljava/lang/String;III)V"))
    private void lethal$version(class_332 g, class_327 font, String text, int x, int y, int color) {
        if (!K33.a()) {
            g.method_25303(font, text, x, y, color);
        }
    }
}

