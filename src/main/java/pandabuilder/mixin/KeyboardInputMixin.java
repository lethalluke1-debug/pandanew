package pandabuilder.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pandabuilder.AutoMine;
import pandabuilder.Freecam;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    private static final Input FORWARD = new Input(true, false, false, false, false, false, false);
    private static final Input FORWARD_JUMP = new Input(true, false, false, false, true, false, false);

    @Inject(method = "tick", at = @At("TAIL"))
    private void pandabuilder$overrideMovement(CallbackInfo ci) {
        // While Freecam is on the movement keys fly the camera, so the player itself gets no input...
        if (Freecam.isOn()) {
            keyPresses = Input.EMPTY;
            moveVector = Vec2.ZERO;
        }
        // ...except Auto Mine walking forward, which keeps going in Freecam.
        if (AutoMine.isWalking()) {
            keyPresses = AutoMine.isJumping() ? FORWARD_JUMP : FORWARD;
            moveVector = new Vec2(0.0f, 1.0f);
        }
    }
}
