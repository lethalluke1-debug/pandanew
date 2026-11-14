package com.lethalauction.mixin;

import com.lethalauction.m.K9;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * When "See-through menus" is on, draw menu/container background textures from
 * the bundled transparent set (lethalauction namespace) instead of the vanilla
 * opaque ones, so the panels become see-through with their slot/border outlines
 * kept. Turning the toggle off restores the vanilla textures.
 */
@Mixin(class_332.class)
public class GuiGraphicsMixin {
    @ModifyVariable(method = "method_25290(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIFFIIII)V", at = @At("HEAD"), argsOnly = true, index = 2)
    private class_2960 lethal$seeThroughTex(class_2960 tex) {
        try {
            if (tex != null && K9.seeThroughGui && class_310.method_1551().field_1687 != null
                    && "minecraft".equals(tex.method_12836()) && tex.method_12832().startsWith("textures/gui/container/")) {
                return class_2960.method_60655("lethalauction", tex.method_12832());
            }
        } catch (Throwable t) {
            // never break rendering
        }
        return tex;
    }
}
