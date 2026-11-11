/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.player.PlayerModel
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.entity.state.AvatarRenderState
 *  net.minecraft.client.renderer.entity.state.EntityRenderState
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.Identifier
 */
package com.lethalauction.m;

import com.lethalauction.m.K6;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class K7
extends RenderLayer<AvatarRenderState, PlayerModel> {
    public K7(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        if (state.isInvisible) {
            return;
        }
        String text = K6.a(state.id);
        if (text.isEmpty()) {
            return;
        }
        K6.b();
        PlayerModel model = (PlayerModel)this.getParentModel();
        boolean helmet = !state.headEquipment.isEmpty();
        boolean boots = !state.feetEquipment.isEmpty();
        for (K6.C75 worn : K6.b(text).values()) {
            K6.C72 item = worn.a();
            RenderType type = RenderTypes.entityCutout((Identifier)K6.b(item, worn.b()));
            for (Map.Entry<K6.C70, ModelPart> e : item.f.entrySet()) {
                ModelPart bone = switch (e.getKey()) {
                    default -> throw new MatchException(null, null);
                    case K6.C70.HEAD -> model.head;
                    case K6.C70.RIGHT_LEG -> model.rightLeg;
                    case K6.C70.LEFT_LEG -> model.leftLeg;
                };
                poseStack.pushPose();
                bone.translateAndRotate(poseStack);
                if (e.getKey() == K6.C70.HEAD && helmet) {
                    poseStack.scale(1.16f, 1.16f, 1.16f);
                } else if (e.getKey() != K6.C70.HEAD && boots) {
                    poseStack.translate(0.0f, 0.0625f, 0.0f);
                    poseStack.scale(1.4f, 1.0f, 1.4f);
                }
                collector.submitModelPart(e.getValue(), poseStack, type, lightCoords, OverlayTexture.NO_OVERLAY, null);
                poseStack.popPose();
            }
        }
    }

}

