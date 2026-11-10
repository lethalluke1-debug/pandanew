/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package com.lethalauction.m;

import com.lethalauction.m.K13;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class K28
implements HudElement {
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gui.hud.isHidden()) {
            return;
        }
        if (mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        Component rank = K13.a(mc);
        if (rank == null) {
            return;
        }
        int w = mc.font.width((FormattedText)rank);
        int x = g.guiWidth() / 2 - w / 2;
        int y = Math.round((float)g.guiHeight() * 0.16f);
        Objects.requireNonNull(mc.font);
        g.fill(x - 8, y - 4, x + w + 8, y + 9 + 4, -1879048192);
        g.text(mc.font, rank, x, y, -1, true);
    }
}

