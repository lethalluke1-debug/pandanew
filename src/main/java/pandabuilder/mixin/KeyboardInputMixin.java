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
    @Inject(method = "tick", at = @At("TAIL"))
    private void pandabuilder$overrideMovement(CallbackInfo ci) {
        // While Freecam is on the movement keys fly the camera, so the player itself gets no input...
        if (Freecam.isOn()) {
            keyPresses = Input.EMPTY;
            moveVector = Vec2.ZERO;
        }
        // ...except Auto Mine's own movement (forward, sidesteps, step-ups), which keeps going in Freecam.
        if (AutoMine.isMoving() || AutoMine.jump()) {
            boolean left = AutoMine.left(), right = AutoMine.right();
            keyPresses = new Input(AutoMine.forward(), AutoMine.back(), left, right, AutoMine.jump(), false, false);
            float side = left ? AutoMine.strafeAmount() : right ? -AutoMine.strafeAmount() : 0.0f;
            moveVector = new Vec2(side, AutoMine.forwardAmount());
            if (moveVector.length() > 1.0f) moveVector = moveVector.normalized();
        }
    }
}
