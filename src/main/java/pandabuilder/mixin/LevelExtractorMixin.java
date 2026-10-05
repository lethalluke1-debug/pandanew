package pandabuilder.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pandabuilder.Esp;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {
    // Vanilla skips entities in chunk sections hidden behind terrain, so their outline never drew. ESP targets
    // are always extracted so the glow shows through walls.
    @Inject(method = "isEntityVisible", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$espVisible(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (Esp.shouldGlow(entity)) cir.setReturnValue(true);
    }
}
