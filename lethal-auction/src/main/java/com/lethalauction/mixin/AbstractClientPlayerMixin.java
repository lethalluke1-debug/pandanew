package com.lethalauction.mixin;

import com.lethalauction.cape.Capes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Swaps in the selected custom cape for the local player only. Other players are untouched. */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void lethalauction$customCape(CallbackInfoReturnable<PlayerSkin> cir) {
        if ((Object) this != Minecraft.getInstance().player) {
            return;
        }
        Capes.Cape cape = Capes.selected();
        if (cape == null) {
            return;
        }
        PlayerSkin skin = cir.getReturnValue();
        ClientAsset.Texture texture = new ClientAsset.ResourceTexture(cape.texture(), cape.texture());
        cir.setReturnValue(new PlayerSkin(skin.body(), texture, texture, skin.model(), skin.secure()));
    }
}
