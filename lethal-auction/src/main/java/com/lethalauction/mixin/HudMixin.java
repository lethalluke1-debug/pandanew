package com.lethalauction.mixin;

import com.lethalauction.gui.ScoreboardStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Applies the Scoreboard tab settings to the vanilla sidebar. */
@Mixin(Hud.class)
public abstract class HudMixin {
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"))
    private void lethalauction$begin(GuiGraphicsExtractor g, Objective objective, CallbackInfo ci) {
        ScoreboardStyle.begin(g);
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
    private void lethalauction$end(GuiGraphicsExtractor g, Objective objective, CallbackInfo ci) {
        ScoreboardStyle.end(g);
    }

    @Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"))
    private void lethalauction$title(GuiGraphicsExtractor g, net.minecraft.client.gui.Font font,
                                     net.minecraft.network.chat.Component text, int x, int y, int color, boolean shadow) {
        ScoreboardStyle.title(g, font, text, x, y, color, shadow);
    }

    @Redirect(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"))
    private void lethalauction$lineName(GuiGraphicsExtractor g, net.minecraft.client.gui.Font font,
                                        net.minecraft.network.chat.Component text, int x, int y, int color, boolean shadow) {
        ScoreboardStyle.lineName(g, font, text, x, y, color, shadow);
    }

    @Redirect(method = "displayScoreboardSidebar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 0))
    private void lethalauction$titleFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        ScoreboardStyle.titleFill(g, x0, y0, x1, y1, color);
    }

    @Redirect(method = "displayScoreboardSidebar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 1))
    private void lethalauction$bodyFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        ScoreboardStyle.bodyFill(g, x0, y0, x1, y1, color);
    }
}
