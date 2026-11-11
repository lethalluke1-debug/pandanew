/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.Display$TextDisplay
 *  net.minecraft.world.entity.Display$TextDisplay$CachedInfo
 *  net.minecraft.world.entity.Display$TextDisplay$CachedLine
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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"net.minecraft.client.renderer.entity.DisplayRenderer$TextDisplayRenderer"})
public abstract class TextDisplayRendererMixin {
    @Inject(method={"extractRenderState(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/client/renderer/entity/state/TextDisplayEntityRenderState;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$nametagIcon(Display.TextDisplay entity, TextDisplayEntityRenderState state, float partialTicks, CallbackInfo ci) {
        boolean selfFake;
        boolean fakeSelf;
        boolean bl = fakeSelf = K9.m && K37.e();
        if (!K14.b() && !fakeSelf) {
            return;
        }
        Display.TextDisplay.CachedInfo info = state.cachedInfo;
        if (info == null || info.lines().isEmpty()) {
            return;
        }
        Player owner = K14.a((Entity)entity);
        if (owner == null) {
            return;
        }
        boolean bl2 = selfFake = fakeSelf && owner == Minecraft.getInstance().player;
        if (!selfFake && !K14.a(owner)) {
            return;
        }
        String name = owner.getGameProfile().name();
        List lines = info.lines();
        int target = lines.size() - 1;
        boolean found = false;
        for (int i = 0; i < lines.size(); ++i) {
            if (!K14.a(((Display.TextDisplay.CachedLine)lines.get(i)).contents()).contains(name)) continue;
            target = i;
            found = true;
            break;
        }
        if (selfFake) {
            if (!found) {
                return;
            }
            Display.TextDisplay.CachedLine fl = (Display.TextDisplay.CachedLine)lines.get(target);
            String repl = K14.a(fl.contents()).replace(name, "Player");
            FormattedCharSequence fseq = Component.literal((String)repl).getVisualOrderText();
            int fw = Minecraft.getInstance().font.width(fseq);
            ArrayList<Display.TextDisplay.CachedLine> fout = new ArrayList<Display.TextDisplay.CachedLine>(lines);
            fout.set(target, new Display.TextDisplay.CachedLine(fseq, fw));
            state.cachedInfo = new Display.TextDisplay.CachedInfo(fout, Math.max(info.width(), fw));
            return;
        }
        FormattedCharSequence badge = K14.e(owner.getUUID());
        FormattedCharSequence sticker = K14.f(owner.getUUID());
        int extra = Minecraft.getInstance().font.width(badge) + Minecraft.getInstance().font.width(sticker);
        if (extra <= 0) {
            return;
        }
        ArrayList<Display.TextDisplay.CachedLine> out = new ArrayList<Display.TextDisplay.CachedLine>(lines);
        Display.TextDisplay.CachedLine line = (Display.TextDisplay.CachedLine)lines.get(target);
        FormattedCharSequence combined = FormattedCharSequence.composite((FormattedCharSequence[])new FormattedCharSequence[]{badge, line.contents(), sticker});
        out.set(target, new Display.TextDisplay.CachedLine(combined, line.width() + extra));
        state.cachedInfo = new Display.TextDisplay.CachedInfo(out, Math.max(info.width(), line.width() + extra));
    }
}

