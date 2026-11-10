/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 */
package com.lethalauction.m;

import com.lethalauction.m.K6;
import java.util.Map;
import net.minecraft.class_10017;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_591;
import net.minecraft.class_630;

public class K7
extends class_3887<class_10055, class_591> {
    public K7(class_3883<class_10055, class_591> renderer) {
        super(renderer);
    }

    public void method_4199(class_4587 poseStack, class_11659 collector, int lightCoords, class_10055 state, float yRot, float xRot) {
        if (state.field_53333) {
            return;
        }
        String text = K6.a(state.field_53528);
        if (text.isEmpty()) {
            return;
        }
        K6.b();
        class_591 model = (class_591)this.method_17165();
        boolean helmet = !state.field_55309.method_7960();
        boolean boots = !state.field_53420.method_7960();
        for (K6.C74 worn : K6.b(text).values()) {
            K6.C71 item = worn.a();
            class_1921 type = class_12249.method_75990((class_2960)K6.b(item, worn.b()));
            for (Map.Entry<K6.C69, class_630> e : item.f.entrySet()) {
                class_630 bone = switch (e.getKey()) {
                    default -> throw new MatchException(null, null);
                    case K6.C69.HEAD -> model.field_3398;
                    case K6.C69.RIGHT_LEG -> model.field_3392;
                    case K6.C69.LEFT_LEG -> model.field_3397;
                };
                poseStack.method_22903();
                bone.method_22703(poseStack);
                if (e.getKey() == K6.C69.HEAD && helmet) {
                    poseStack.method_22905(1.16f, 1.16f, 1.16f);
                } else if (e.getKey() != K6.C69.HEAD && boots) {
                    poseStack.method_46416(0.0f, 0.0625f, 0.0f);
                    poseStack.method_22905(1.4f, 1.0f, 1.4f);
                }
                collector.method_73491(e.getValue(), poseStack, type, lightCoords, class_4608.field_21444, null);
                poseStack.method_22909();
            }
        }
    }

}

