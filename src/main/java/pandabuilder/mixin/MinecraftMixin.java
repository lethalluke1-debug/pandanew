package pandabuilder.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pandabuilder.AutoMine;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    // Vanilla calls stopDestroyBlock() every tick the attack key isn't held, which reset Auto Mine's progress
    // so only blocks that break in one tick (stone with fast tools) ever broke. Deepslate never did.
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$keepMining(boolean attackDown, CallbackInfo ci) {
        if (AutoMine.isOn() || AutoMine.blockClicks()) ci.cancel();
    }

    // "Freeze in Freecam": the player's own clicks don't break, place or pick blocks while flying the camera.
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$noAttack(CallbackInfoReturnable<Boolean> cir) {
        if (AutoMine.blockClicks()) cir.setReturnValue(false);
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$noUse(CallbackInfo ci) {
        if (AutoMine.blockClicks()) ci.cancel();
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void pandabuilder$noPick(CallbackInfo ci) {
        if (AutoMine.blockClicks()) ci.cancel();
    }
}
