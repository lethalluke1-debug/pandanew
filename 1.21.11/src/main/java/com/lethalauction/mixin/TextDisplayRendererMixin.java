/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10071
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5481
 *  net.minecraft.class_8113$class_8123
 *  net.minecraft.class_8113$class_8123$class_8125
 *  net.minecraft.class_8113$class_8123$class_8126
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.lethalauction.mixin;

import com.lethalauction.m.K14;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_10071;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5481;
import net.minecraft.class_8113;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"net/minecraft/class_8138$class_8141"})
public abstract class TextDisplayRendererMixin {
    @Inject(method={"method_62409(Lnet/minecraft/class_8113$class_8123;Lnet/minecraft/class_10071;F)V"}, at={@At(value="TAIL")})
    private void lethalauction$nametagIcon(class_8113.class_8123 entity, class_10071 state, float partialTicks, CallbackInfo ci) {
        boolean selfFake;
        boolean fakeSelf;
        boolean bl = fakeSelf = K9.m && K37.e();
        if (!K14.b() && !fakeSelf) {
            return;
        }
        class_8113.class_8123.class_8125 info = state.field_53589;
        if (info == null || info.comp_1247().isEmpty()) {
            return;
        }
        class_1657 owner = K14.a((class_1297)entity);
        if (owner == null) {
            return;
        }
        boolean bl2 = selfFake = fakeSelf && owner == class_310.method_1551().field_1724;
        if (!selfFake && !K14.a(owner)) {
            return;
        }
        String name = owner.method_7334().name();
        List lines = info.comp_1247();
        int target = lines.size() - 1;
        boolean found = false;
        for (int i = 0; i < lines.size(); ++i) {
            if (!K14.a(((class_8113.class_8123.class_8126)lines.get(i)).comp_1249()).contains(name)) continue;
            target = i;
            found = true;
            break;
        }
        if (selfFake) {
            if (!found) {
                return;
            }
            class_8113.class_8123.class_8126 fl = (class_8113.class_8123.class_8126)lines.get(target);
            String repl = K14.a(fl.comp_1249()).replace(name, "Player");
            class_5481 fseq = class_2561.method_43470((String)repl).method_30937();
            int fw = class_310.method_1551().field_1772.method_30880(fseq);
            ArrayList<class_8113.class_8123.class_8126> fout = new ArrayList<class_8113.class_8123.class_8126>(lines);
            fout.set(target, new class_8113.class_8123.class_8126(fseq, fw));
            state.field_53589 = new class_8113.class_8123.class_8125(fout, Math.max(info.comp_1248(), fw));
            return;
        }
        class_5481 badge = K14.e(owner.method_5667());
        class_5481 sticker = K14.f(owner.method_5667());
        int extra = class_310.method_1551().field_1772.method_30880(badge) + class_310.method_1551().field_1772.method_30880(sticker);
        if (extra <= 0) {
            return;
        }
        ArrayList<class_8113.class_8123.class_8126> out = new ArrayList<class_8113.class_8123.class_8126>(lines);
        class_8113.class_8123.class_8126 line = (class_8113.class_8123.class_8126)lines.get(target);
        class_5481 combined = class_5481.method_34909((class_5481[])new class_5481[]{badge, line.comp_1249(), sticker});
        out.set(target, new class_8113.class_8123.class_8126(combined, line.comp_1250() + extra));
        state.field_53589 = new class_8113.class_8123.class_8125(out, Math.max(info.comp_1248(), line.comp_1250() + extra));
    }
}

