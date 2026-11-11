/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.renderer.LightmapRenderStateExtractor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LightmapRenderStateExtractor.class})
public abstract class LightmapMixin {
    @Redirect(method={"extract"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;")})
    private Object lethalauction$fullbright(OptionInstance<?> option) {
        if (K0.a() && option == Minecraft.getInstance().options.gamma()) {
            return 16.0;
        }
        return option.get();
    }
}

