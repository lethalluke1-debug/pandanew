package pandabuilder.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pandabuilder.AutoMine;
import pandabuilder.Esp;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$esp(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Esp.shouldGlow(entity)) cir.setReturnValue(true);
    }

    // Vanilla calls stopDestroyBlock() every tick the attack key isn't held, which reset Auto Mine's progress
    // so only blocks that break in one tick (stone with fast tools) ever broke. Deepslate never did.
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$keepMining(boolean attackDown, CallbackInfo ci) {
        if (AutoMine.isOn()) ci.cancel();
    }
}
