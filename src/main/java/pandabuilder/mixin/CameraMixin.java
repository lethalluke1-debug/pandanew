package pandabuilder.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.Freecam;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean detached;

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Shadow protected abstract void setPosition(Vec3 position);

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void pandabuilder$freecam(float partialTick, CallbackInfo ci) {
        if (!Freecam.isOn()) return;
        setRotation(Freecam.yaw(), Freecam.pitch());
        setPosition(Freecam.position(partialTick));
        detached = true;
    }
}
