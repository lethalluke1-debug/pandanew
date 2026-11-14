package com.lethalauction.mixin;

import com.lethalauction.m.K12;
import net.minecraft.class_309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pressing Escape while a figure waypoint is set unbinds the waypoint (so the
 * figure follows you again) and swallows that Escape press, instead of opening
 * the pause menu. With no waypoint set, Escape behaves normally.
 */
@Mixin(value = {class_309.class})
public class KeyboardHandlerMixin {
    @Inject(method = {"method_22676"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void lethal$escClearWaypoint(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key == 256 && action == 1 && class_310.method_1551().field_1755 == null && K12.clearWaypoint()) {
            ci.cancel();
        }
    }
}
