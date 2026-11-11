/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.state.EntityRenderState
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Avatar
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K14;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class EntityRendererMixin {
    @Inject(method={"extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$carrierIcon(Entity entity, EntityRenderState state, float partialTicks, CallbackInfo ci) {
        boolean fakeSelf;
        if (state.nameTag == null || entity instanceof Avatar || !entity.isInvisible()) {
            return;
        }
        boolean bl = fakeSelf = K9.m && K37.e();
        if (!K14.b() && !fakeSelf) {
            return;
        }
        Player owner = K14.a(entity);
        if (owner == null) {
            return;
        }
        if (fakeSelf && owner == Minecraft.getInstance().player) {
            state.nameTag = Component.literal((String)"Player");
            return;
        }
        if (K14.a(owner)) {
            state.nameTag = K14.a(state.nameTag, owner.getUUID());
        }
    }
}

