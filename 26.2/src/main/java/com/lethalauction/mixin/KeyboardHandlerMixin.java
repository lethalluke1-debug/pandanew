package com.lethalauction.mixin;

import com.lethalauction.m.K12;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pressing Escape while a figure waypoint is set unbinds the waypoint (so the
 * figure follows you again) and swallows that Escape press, instead of opening
 * the pause menu. With no waypoint set, Escape behaves normally.
 */
@Mixin(value = {KeyboardHandler.class})
public class KeyboardHandlerMixin {
    @Inject(method = {"keyPress"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void lethal$escClearWaypoint(long window, int action, KeyEvent event, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (event.key() == 256 && action == 1 && mc.gui.screen() == null && K12.clearWaypoint()) {
            ci.cancel();
        }
    }
}
