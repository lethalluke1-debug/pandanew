package com.lethalauction.mixin;

import com.lethalauction.m.K9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * When "See-through menus" is on, draw menu/container background textures from
 * the bundled transparent set (lethalauction namespace) instead of the vanilla
 * opaque ones, so the panels become see-through with their slot/border outlines
 * kept. Turning the toggle off restores the vanilla textures.
 */
@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsMixin {
    @ModifyVariable(method = "blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V", at = @At("HEAD"), argsOnly = true, require = 0)
    private Identifier lethal$seeThroughTex(Identifier tex) {
        try {
            if (tex != null && K9.seeThroughGui && Minecraft.getInstance().level != null
                    && "minecraft".equals(tex.getNamespace()) && tex.getPath().startsWith("textures/gui/container/")) {
                return Identifier.fromNamespaceAndPath("lethalauction", tex.getPath());
            }
        } catch (Throwable t) {
            // never break rendering
        }
        return tex;
    }
}
