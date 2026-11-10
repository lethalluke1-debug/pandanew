/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.player.LocalPlayer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K40;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MouseHandler.class})
public class MouseHandlerMixin {
    @Redirect(method={"turnPlayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")})
    private void lethal$turn(LocalPlayer player, double dx, double dy) {
        if (K40.a()) {
            K40.a(dx, dy);
        } else {
            double s = K40.f();
            player.turn(dx * s, dy * s);
        }
    }

    @Inject(method={"onScroll"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethal$scroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.screen() == null && K40.a(yOffset)) {
            ci.cancel();
        }
    }
}

