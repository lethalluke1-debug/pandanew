/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.core.ClientAsset$ResourceTexture
 *  net.minecraft.core.ClientAsset$Texture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.player.PlayerModelType
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K4;
import com.lethalauction.m.K49;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayer.class})
public abstract class AbstractClientPlayerMixin {
    @Inject(method={"getSkin"}, at={@At(value="RETURN")}, cancellable=true)
    private void lethalauction$customCape(CallbackInfoReturnable<PlayerSkin> cir) {
        Identifier id;
        ClientAsset.Texture body;
        if (!K37.e()) {
            return;
        }
        AbstractClientPlayer self = (AbstractClientPlayer)(Object)this;
        boolean you = self == Minecraft.getInstance().player;
        PlayerSkin skin = (PlayerSkin)cir.getReturnValue();
        K49.C99 picked = you ? K49.c() : null;
        Object object = body = picked != null ? new ClientAsset.ResourceTexture(picked.b(), picked.b()) : skin.body();
        PlayerModelType model = picked != null ? (picked.c() ? PlayerModelType.SLIM : PlayerModelType.WIDE) : skin.model();
        Identifier identifier = id = you ? K4.d() : K38.a(self.getUUID(), K37.f());
        if (id != null) {
            ClientAsset.ResourceTexture texture = new ClientAsset.ResourceTexture(id, id);
            cir.setReturnValue(new PlayerSkin((ClientAsset.Texture)body, (ClientAsset.Texture)texture, (ClientAsset.Texture)texture, model, skin.secure()));
        } else if (picked != null) {
            cir.setReturnValue(new PlayerSkin((ClientAsset.Texture)body, skin.cape(), skin.elytra(), model, skin.secure()));
        }
    }
}

