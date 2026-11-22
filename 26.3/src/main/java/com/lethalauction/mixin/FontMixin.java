package com.lethalauction.mixin;

import com.lethalauction.m.K9;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Global font override. When a custom font is chosen in the Font tab, text that
 * would render with Minecraft's default font is redirected to the chosen font
 * (bundled as lethalauction:font_*). The mod's own icon/badge fonts use their
 * own ids, so they are untouched.
 */
@Mixin(Font.class)
public class FontMixin {
    @ModifyVariable(method = "getGlyphSource", at = @At("HEAD"), argsOnly = true)
    private FontDescription lethal$font(FontDescription desc) {
        try {
            String f = K9.font;
            if (f != null && !f.isEmpty() && desc instanceof FontDescription.Resource r
                    && "minecraft:default".equals(r.id().toString())) {
                return new FontDescription.Resource(Identifier.fromNamespaceAndPath("lethalauction", f));
            }
        } catch (Throwable t) {
            // never break text rendering
        }
        return desc;
    }
}
