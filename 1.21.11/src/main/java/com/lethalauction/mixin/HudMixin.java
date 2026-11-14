/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_266
 *  net.minecraft.class_327
 *  net.minecraft.class_329
 *  net.minecraft.class_332
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
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_327;
import net.minecraft.class_329;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_329.class})
public abstract class HudMixin {
    @Inject(method = {"method_1758"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void lethalauction$hideSmallOverlay(class_2561 component, boolean animate, CallbackInfo ci) {
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

    @Inject(method={"method_1757"}, at={@At(value="HEAD")}, cancellable=true)
    private void lethalauction$custom(class_332 g, class_266 objective, CallbackInfo ci) {
        if (K17.a() && K17.a(g, objective)) {
            ci.cancel();
            return;
        }
        K29.a(g);
    }

    @Inject(method={"method_1757"}, at={@At(value="RETURN")})
    private void lethalauction$end(class_332 g, class_266 objective, CallbackInfo ci) {
        K29.b(g);
    }

    @Redirect(method={"method_1757"}, at=@At(value="INVOKE", ordinal=0, target="Lnet/minecraft/class_332;method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V"))
    private void lethalauction$title(class_332 g, class_327 font, class_2561 text, int x, int y, int color, boolean shadow) {
        if (K29.a()) {
            K29.b(g, font, text, x, y, color, shadow);
        } else {
            g.method_51439(font, text, x, y, color, shadow);
        }
    }

    @Redirect(method={"method_1757"}, at=@At(value="INVOKE", ordinal=1, target="Lnet/minecraft/class_332;method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V"))
    private void lethalauction$lineName(class_332 g, class_327 font, class_2561 text, int x, int y, int color, boolean shadow) {
        if (K29.a()) {
            K29.a(g, font, text, x, y, color, shadow);
        } else {
            g.method_51439(font, text, x, y, color, shadow);
        }
    }

    @Redirect(method={"method_1757"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_25294(IIIII)V", ordinal=0))
    private void lethalauction$titleFill(class_332 g, int x0, int y0, int x1, int y1, int color) {
        if (K29.a()) {
            K29.a(g, x0, y0, x1, y1, color);
        } else {
            g.method_25294(x0, y0, x1, y1, color);
        }
    }

    @Redirect(method={"method_1757"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_25294(IIIII)V", ordinal=1))
    private void lethalauction$bodyFill(class_332 g, int x0, int y0, int x1, int y1, int color) {
        if (K29.a()) {
            K29.b(g, x0, y0, x1, y1, color);
        } else {
            g.method_25294(x0, y0, x1, y1, color);
        }
    }
}

