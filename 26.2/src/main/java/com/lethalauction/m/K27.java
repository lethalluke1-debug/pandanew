/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.resources.DefaultPlayerSkin
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Transactions;
import com.lethalauction.m.K18;
import com.lethalauction.m.K19;
import com.lethalauction.m.K24;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Matrix3x2fStack;

public class K27
implements HudElement {
    public static final int a = 250;
    public static final int b = 84;
    public static final float c = 0.62f;
    private static final int e = -12200829;
    private static final int f = -42401;
    public static final K24 d = new K24(){

        @Override
        public String a() {
            return "the profit display";
        }

        @Override
        public float b() {
            return 250.0f * K27.a();
        }

        @Override
        public float c() {
            return 84.0f * K27.a();
        }

        @Override
        public float d() {
            return K9.v;
        }

        @Override
        public float e() {
            return K9.w;
        }

        @Override
        public void a(float fx, float fy) {
            K9.v = fx;
            K9.w = fy;
        }

        @Override
        public float f() {
            return K9.x;
        }

        @Override
        public void a(float size) {
            K9.x = size;
        }

        @Override
        public void g() {
            K9.v = 0.01f;
            K9.w = 0.32f;
            K9.x = 1.0f;
        }

        @Override
        public void a(GuiGraphicsExtractor g, float x, float y) {
            K27.a(g, x, y);
        }
    };

    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (!K9.t || !K37.c() || mc.gui.hud.isHidden() || mc.gui.screen() instanceof K19) {
            return;
        }
        K27.a(g, K27.a(g.guiWidth()), K27.b(g.guiHeight()));
    }

    public static float a() {
        return 0.62f * K9.x;
    }

    public static float a(int guiWidth) {
        return K9.v * Math.max(1.0f, (float)guiWidth - 250.0f * K27.a());
    }

    public static float b(int guiHeight) {
        return K9.w * Math.max(1.0f, (float)guiHeight - 84.0f * K27.a());
    }

    public static void a(GuiGraphicsExtractor g, float x, float y) {
        Minecraft mc = Minecraft.getInstance();
        long[] session = Transactions.d();
        long profit = session[0] - session[1];
        boolean up = profit >= 0L;
        int color = up ? -12200829 : -42401;
        float s = K27.a();
        float t = (float)(System.currentTimeMillis() % 3600000L) / 1000.0f;
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K18.a(g, -2, -2, 254, 88, 14, K18.a(color, 0.12f + 0.05f * (float)Math.sin(t * 2.0f)));
        K18.a(g, 0, 0, 250, 84, 12, -435482856, K18.a(color, 0.6f));
        K18.a(g, 0, 14, 3, 56, 1, color);
        K18.a(g, 12, 12, 60, 60, 10, -15722458);
        if (!K9.u || mc.player == null) {
            PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)g, (PlayerSkin)(mc.player != null ? mc.player.getSkin() : DefaultPlayerSkin.get((GameProfile)mc.getGameProfile())), (int)18, (int)18, (int)48);
        }
        K18.a(g, "SESSION PROFIT", 86.0f, 22.0f, 10.5f, -7695450, K18.b);
        String amount = (up ? "+" : "-") + Auctions.a(Math.abs(profit));
        K18.a(g, amount, 86.0f, 46.0f, 22.0f, color, K18.c);
        String detail = "+" + Auctions.a(session[0]) + "  \u00b7  -" + Auctions.a(session[1]);
        K18.a(g, detail, 86.0f, 68.0f, 10.0f, -9735291, K18.b);
        p.popMatrix();
        if (K9.u && mc.player != null) {
            int x0 = Math.round(x + 12.0f * s);
            int y0 = Math.round(y + 12.0f * s);
            int x1 = Math.round(x + 72.0f * s);
            int y1 = Math.round(y + 72.0f * s);
            int size = Math.max(8, Math.round((float)(y1 - y0) * 0.42f));
            float lookX = (float)(x0 + x1) / 2.0f + 6.0f * (float)Math.sin(t * 0.8f);
            float lookY = (float)y0 + (float)(y1 - y0) * 0.3f;
            InventoryScreen.extractEntityInInventoryFollowsMouse((GuiGraphicsExtractor)g, (int)x0, (int)y0, (int)x1, (int)y1, (int)size, (float)0.0625f, (float)lookX, (float)lookY, (LivingEntity)mc.player);
        }
    }
}

