/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 *  net.minecraft.class_9779
 */
package com.lethalauction.m;

import com.lethalauction.m.K13;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import net.minecraft.class_9779;

public class K28
implements HudElement {
    public void render(class_332 g, class_9779 deltaTracker) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1690.field_1842) {
            return;
        }
        if (mc.field_1690.method_31044().method_31034()) {
            return;
        }
        class_2561 rank = K13.a(mc);
        if (rank == null) {
            return;
        }
        int w = mc.field_1772.method_27525((class_5348)rank);
        int x = g.method_51421() / 2 - w / 2;
        int y = Math.round((float)g.method_51443() * 0.16f);
        Objects.requireNonNull(mc.field_1772);
        g.method_25294(x - 8, y - 4, x + w + 8, y + 9 + 4, -1879048192);
        g.method_51439(mc.field_1772, rank, x, y, -1, true);
    }
}

