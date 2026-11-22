/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.state.level.SkyRenderState
 *  net.minecraft.world.level.dimension.DimensionType$Skybox
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K50;
import com.lethalauction.m.K9;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public abstract class LevelRendererMixin {
    @Inject(method={"addCloudsPass"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$hideClouds(CallbackInfo ci) {
        if (K9.r && K50.a()) {
            ci.cancel();
        }
    }

    @Redirect(method={"addSkyPass"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/state/level/SkyRenderState;skybox:Lnet/minecraft/world/level/dimension/DimensionType$Skybox;", opcode=180)}, require=0)
    private DimensionType.Skybox lethalauction$forceSkyInNether(SkyRenderState state) {
        return K50.a() ? DimensionType.Skybox.OVERWORLD : state.skybox;
    }
}

