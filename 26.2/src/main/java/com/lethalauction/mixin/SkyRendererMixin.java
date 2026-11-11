/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  net.minecraft.client.renderer.SkyRenderer
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K50;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SkyRenderer.class})
public abstract class SkyRendererMixin {
    @Shadow
    @Final
    private RenderTarget renderTarget;

    @Inject(method={"renderSkyDisc", "renderEndSky"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$customSky(CallbackInfo ci) {
        if (K50.a()) {
            K50.a(this.renderTarget);
            ci.cancel();
        }
    }

    @Inject(method={"renderSunriseAndSunset", "renderSunMoonAndStars", "renderDarkDisc", "renderEndFlash"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$hideVanillaSky(CallbackInfo ci) {
        if (K50.a()) {
            ci.cancel();
        }
    }
}

