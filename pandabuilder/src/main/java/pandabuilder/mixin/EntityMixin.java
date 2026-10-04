package pandabuilder.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.Freecam;

@Mixin(Entity.class)
public abstract class EntityMixin {
   // Mouse movement turns the free camera instead of the player while freecam is on.
   @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
   private void pandabuilder$freecamTurn(double xo, double yo, CallbackInfo ci) {
      if (Freecam.isActive() && (Object)this == Minecraft.getInstance().player) {
         Freecam.turn(xo, yo);
         ci.cancel();
      }
   }
}
