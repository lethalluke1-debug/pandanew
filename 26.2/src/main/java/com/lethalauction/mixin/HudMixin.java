/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.Hud
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.scores.Objective
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Transactions;
import com.lethalauction.m.K17;
import com.lethalauction.m.K29;
import com.lethalauction.m.K9;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Hud.class})
public abstract class HudMixin {
    @Inject(method = {"setOverlayMessage"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void lethalauction$hideSmallOverlay(Component component, boolean animate, CallbackInfo ci) {
        if (!K9.hideSmall || K9.hideSmallStr.isBlank() || component == null) {
            return;
        }
        try {
            long amt = Transactions.matchAmount(component.getString());
            if (amt >= 0L) {
                long thr = Auctions.e(K9.hideSmallStr);
                if (thr > 0L && amt <= thr) {
                    ci.cancel();
                }
            }
        } catch (Throwable t) {
            // never break the hud
        }
    }

    @Inject(method={"displayScoreboardSidebar"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$custom(GuiGraphicsExtractor g, Objective objective, CallbackInfo ci) {
        if (K17.a() && K17.a(g, objective)) {
            ci.cancel();
            return;
        }
        K29.a(g);
    }

    @Inject(method={"displayScoreboardSidebar"}, at={@At(value="RETURN")})
    private void lethalauction$end(GuiGraphicsExtractor g, Objective objective, CallbackInfo ci) {
        K29.b(g);
    }

    @Redirect(method={"displayScoreboardSidebar"}, at={@At(value="INVOKE", ordinal=0, target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V")})
    private void lethalauction$title(GuiGraphicsExtractor g, Font font, Component text, int x, int y, int color, boolean shadow) {
        if (K29.a()) {
            K29.b(g, font, text, x, y, color, shadow);
        } else {
            g.text(font, text, x, y, color, shadow);
        }
    }

    @Redirect(method={"displayScoreboardSidebar"}, at={@At(value="INVOKE", ordinal=1, target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V")})
    private void lethalauction$lineName(GuiGraphicsExtractor g, Font font, Component text, int x, int y, int color, boolean shadow) {
        if (K29.a()) {
            K29.a(g, font, text, x, y, color, shadow);
        } else {
            g.text(font, text, x, y, color, shadow);
        }
    }

    @Redirect(method={"displayScoreboardSidebar"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal=0)})
    private void lethalauction$titleFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        if (K29.a()) {
            K29.a(g, x0, y0, x1, y1, color);
        } else {
            g.fill(x0, y0, x1, y1, color);
        }
    }

    @Redirect(method={"displayScoreboardSidebar"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal=1)})
    private void lethalauction$bodyFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        if (K29.a()) {
            K29.b(g, x0, y0, x1, y1, color);
        } else {
            g.fill(x0, y0, x1, y1, color);
        }
    }
}

