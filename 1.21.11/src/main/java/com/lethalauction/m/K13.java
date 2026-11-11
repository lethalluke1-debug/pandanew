/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_3532
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 *  net.minecraft.class_640
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package com.lethalauction.m;

import com.lethalauction.m.K51;
import com.lethalauction.m.K53;
import java.util.Optional;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_3532;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_640;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

public final class K13 {
    private static final K51 a = new K51();

    private K13() {
    }

    public static void a(WorldRenderContext ctx) {
        try {
            K13.b(ctx);
        }
        catch (Throwable throwable) {
        }
        finally {
            try {
                a.b(ctx.consumers());
            }
            catch (Throwable throwable) {}
        }
    }

    private static void b(WorldRenderContext ctx) {
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
        float pt = mc.method_61966().method_60637(false);
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double px = class_3532.method_16436((double)pt, (double)mc.field_1724.field_6014, (double)mc.field_1724.method_23317());
        double py = class_3532.method_16436((double)pt, (double)mc.field_1724.field_6036, (double)mc.field_1724.method_23318());
        double pz = class_3532.method_16436((double)pt, (double)mc.field_1724.field_5969, (double)mc.field_1724.method_23321());
        double topY = py + (double)mc.field_1724.method_17682() + 0.6;
        Matrix4f m = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
        m.translate((float)(px - cam.field_1352), (float)(topY - cam.field_1351), (float)(pz - cam.field_1350));
        m.rotate((Quaternionfc)mc.field_1773.method_19418().method_23767());
        m.scale(-0.025f, -0.025f, 0.025f);
        class_327 font = mc.field_1772;
        float w = (float)font.method_27525((class_5348)rank) / 2.0f;
        K53.a(font, rank, -w + 0.6f, 0.6f, 0x50000000, false, m, a, class_327.class_6415.field_33993, 0xF000F0);
        K53.a(font, rank, -w, 0.0f, -1, false, m, a, class_327.class_6415.field_33993, 0xF000F0);
    }

    public static class_2561 a(class_310 mc) {
        class_640 info;
        class_5250 tag;
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return null;
        }
        String name = mc.field_1724.method_5820();
        class_269 sb = mc.field_1687.method_8428();
        class_268 team = sb.method_1164(name);
        if (team != null && !(tag = class_268.method_1142((class_270)team, (class_2561)class_2561.method_43473())).getString().trim().isEmpty()) {
            return tag;
        }
        class_640 class_6402 = info = mc.method_1562() == null ? null : mc.method_1562().method_2871(mc.field_1724.method_5667());
        if (info != null && info.method_2971() != null) {
            class_2561 tab = info.method_2971();
            class_5250 pre = class_2561.method_43473();
            boolean[] done = new boolean[]{false};
            tab.method_27658((style, text) -> {
                int idx;
                if (done[0]) {
                    return Optional.empty();
                }
                int n = idx = name.isEmpty() ? -1 : text.indexOf(name);
                if (idx >= 0) {
                    if (idx > 0) {
                        pre.method_10852((class_2561)class_2561.method_43470((String)text.substring(0, idx)).method_10862(style));
                    }
                    done[0] = true;
                } else {
                    pre.method_10852((class_2561)class_2561.method_43470((String)text).method_10862(style));
                }
                return Optional.empty();
            }, class_2583.field_24360);
            if (!pre.getString().trim().isEmpty()) {
                return pre;
            }
            if (!tab.getString().trim().equals(name)) {
                return tab;
            }
        }
        return null;
    }
}

