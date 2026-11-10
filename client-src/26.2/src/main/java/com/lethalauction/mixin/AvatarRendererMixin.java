/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.player.AvatarRenderer
 *  net.minecraft.client.renderer.entity.state.AvatarRenderState
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Avatar
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K14;
import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AvatarRenderer.class})
public abstract class AvatarRendererMixin {
    @Inject(method={"shouldShowName(Lnet/minecraft/world/entity/Avatar;D)Z"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethalauction$showOwnName(Avatar entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (!((Boolean)cir.getReturnValue()).booleanValue() && entity == mc.player && mc.getCameraEntity() == mc.player && (K14.a() || K9.m && K37.e()) && !mc.options.getCameraType().isFirstPerson() && !mc.gui.hud.isHidden() && !entity.isInvisible()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$nametagIcon(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        boolean self;
        if (state.nameTag == null) {
            return;
        }
        boolean bl = self = entity == Minecraft.getInstance().player;
        if (self && K9.m && K37.e()) {
            state.nameTag = Component.literal((String)"Player");
            return;
        }
        if (K14.a() && (self || K38.e(entity.getUUID()))) {
            state.nameTag = K14.a(state.nameTag, entity.getUUID());
        }
    }
}

