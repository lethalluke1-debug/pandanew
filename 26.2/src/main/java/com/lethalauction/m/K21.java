/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.client.gui.components.toasts.Toast$Visibility
 *  net.minecraft.client.gui.components.toasts.ToastManager
 *  net.minecraft.client.renderer.RenderPipelines
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class K21
implements Toast {
    private static final Identifier a = Identifier.withDefaultNamespace((String)"toast/advancement");
    private static final Identifier b = K18.a("textures/font/badge_member.png");
    private final Component c;
    private final Component d;
    private final long e;
    private Toast.Visibility f = Toast.Visibility.HIDE;

    private K21(Component title, Component description, long displayMs) {
        this.c = title;
        this.d = description;
        this.e = displayMs;
    }

    public static void a(String title, String description) {
        K21.a(title, description, 5000L);
    }

    public static void a(String title, String description, long displayMs) {
        Minecraft.getInstance().gui.toastManager().addToast((Toast)new K21((Component)Component.literal((String)title), (Component)Component.literal((String)description), displayMs));
    }

    public Toast.Visibility getWantedVisibility() {
        return this.f;
    }

    public void update(ToastManager manager, long fullyVisibleForMs) {
        this.f = (double)fullyVisibleForMs >= (double)this.e * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public void extractRenderState(GuiGraphicsExtractor g, Font font, long fullyVisibleForMs) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, a, 0, 0, this.width(), this.height());
        g.text(font, this.c, 30, 7, -8530948, false);
        g.text(font, this.d, 30, 18, -1, false);
        K18.a(g, b, 8, 8, 16, 16, 64, 64);
    }
}

