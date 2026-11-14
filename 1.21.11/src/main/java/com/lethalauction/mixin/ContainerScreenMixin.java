package com.lethalauction.mixin;

import com.lethalauction.m.K9;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * When "See-through menus" is on, skip drawing the container's own background
 * panel (the grey window) in-world, so only the slots/items are left over the
 * world. The dark blur is removed separately in ScreenMixin.
 */
@Mixin(value = {class_465.class})
public abstract class ContainerScreenMixin {
    @Shadow
    protected abstract void method_2389(class_332 var1, float var2, int var3, int var4);

    @Redirect(method = "method_71085", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_465;method_2389(Lnet/minecraft/class_332;FII)V"), require = 0)
    private void lethal$seeThroughGui(class_465 self, class_332 g, float partialTick, int mouseX, int mouseY) {
        if (!(K9.seeThroughGui && class_310.method_1551().field_1687 != null)) {
            this.method_2389(g, partialTick, mouseX, mouseY);
        }
    }
}
