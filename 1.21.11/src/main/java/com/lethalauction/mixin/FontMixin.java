package com.lethalauction.mixin;

import com.lethalauction.m.K9;
import net.minecraft.class_11719;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Global font override. When a custom font is chosen in the Font tab, text that
 * would render with Minecraft's default font is redirected to the chosen font
 * (bundled as lethalauction:font_*). The mod's own icon/badge fonts use their
 * own ids, so they are untouched.
 */
@Mixin(class_327.class)
public class FontMixin {
    @ModifyVariable(method = "method_72732", at = @At("HEAD"), argsOnly = true)
    private class_11719 lethal$font(class_11719 desc) {
        try {
            String f = K9.font;
            if (f != null && !f.isEmpty() && desc.equals(class_11719.field_61972)) {
                return new class_11719.class_11721(class_2960.method_60655("lethalauction", f));
            }
        } catch (Throwable t) {
            // never break text rendering
        }
        return desc;
    }
}
