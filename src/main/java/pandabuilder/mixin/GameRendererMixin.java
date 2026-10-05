package pandabuilder.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.Freecam;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    // In Freecam the first-person hands (pickaxe, offhand totem) belong to the body, not the camera. They swayed,
    // bobbed and dipped as Auto Mine moved and mined, so they're hidden.
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$hideHands(CameraRenderState camera, float partialTick, Matrix4fc projection, CallbackInfo ci) {
        if (Freecam.isOn()) ci.cancel();
    }

    // View bobbing follows the body's walking; the free camera shouldn't bob while the body walks.
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$noBob(CameraRenderState camera, PoseStack poseStack, CallbackInfo ci) {
        if (Freecam.isOn()) ci.cancel();
    }
}
