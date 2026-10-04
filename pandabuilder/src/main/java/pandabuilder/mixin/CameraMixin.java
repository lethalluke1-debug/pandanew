package pandabuilder.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.Freecam;

@Mixin(Camera.class)
public abstract class CameraMixin {
   @Shadow
   private boolean detached;

   @Shadow
   protected abstract void setPosition(double x, double y, double z);

   @Shadow
   protected abstract void setRotation(float yRot, float xRot);

   @Inject(method = "alignWithEntity", at = @At("TAIL"))
   private void pandabuilder$freecam(float partialTicks, CallbackInfo ci) {
      if (Freecam.isActive()) {
         this.setRotation(Freecam.yaw(), Freecam.pitch());
         this.setPosition(Freecam.x(partialTicks), Freecam.y(partialTicks), Freecam.z(partialTicks));
         // Detached so your own player is drawn and the hand isn't.
         this.detached = true;
      }
   }
}
