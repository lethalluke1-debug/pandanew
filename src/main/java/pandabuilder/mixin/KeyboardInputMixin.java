package pandabuilder.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.Freecam;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    // While Freecam is on the movement keys fly the camera, so the player itself gets no input.
    @Inject(method = "tick", at = @At("TAIL"))
    private void pandabuilder$freezePlayer(CallbackInfo ci) {
        if (!Freecam.isOn()) return;
        keyPresses = Input.EMPTY;
        moveVector = Vec2.ZERO;
    }
}
