/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.Font$GlyphVisitor
 *  net.minecraft.client.gui.Font$PreparedText
 *  net.minecraft.client.gui.font.TextRenderable
 *  net.minecraft.client.gui.font.TextRenderable$Styled
 *  net.minecraft.network.chat.Component
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.lethalauction.m;

import com.lethalauction.m.K52;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class K54 {
    private K54() {
    }

    public static void a(Font font, Component text, float x, float y, int color, boolean shadow, final Matrix4f matrix, final K52 buffers, final Font.DisplayMode mode, final int light) {
        Font.PreparedText prepared = font.prepareText(text.getVisualOrderText(), x, y, color, shadow, false, 0);
        prepared.visit(new Font.GlyphVisitor(){

            public void acceptGlyph(TextRenderable.Styled styled) {
                this.a((TextRenderable)styled);
            }

            public void acceptEffect(TextRenderable r) {
                this.a(r);
            }

            public void acceptRenderable(TextRenderable r) {
                this.a(r);
            }

            private void a(TextRenderable r) {
                r.render((Matrix4fc)matrix, buffers.a(r.renderType(mode)), light, false);
            }
        });
    }
}

