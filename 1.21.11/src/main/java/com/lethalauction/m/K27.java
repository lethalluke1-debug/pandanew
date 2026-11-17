/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.minecraft.class_1068
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_490
 *  net.minecraft.class_7532
 *  net.minecraft.class_8685
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2fStack
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.mixin.TabOverlayAccessor;
import net.minecraft.class_640;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lethalauction.auction.Transactions;
import com.lethalauction.m.K18;
import com.lethalauction.m.K19;
import com.lethalauction.m.K24;
import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.class_1068;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_490;
import net.minecraft.class_7532;
import net.minecraft.class_8685;
import net.minecraft.class_9779;
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
        public void a(class_332 g, float x, float y) {
            K27.a(g, x, y);
        }
    };

    public void render(class_332 g, class_9779 deltaTracker) {
        class_310 mc = class_310.method_1551();
        if (!K9.t || !K37.c() || mc.field_1690.field_1842 || mc.field_1755 instanceof K19) {
            return;
        }
        K27.a(g, K27.a(g.method_51421()), K27.b(g.method_51443()));
    }

    // Brief win/loss popup state (+1 on a win, "- All" on a loss).
    public static long flashAt = 0L;
    public static boolean flashWin = true;

    public static void flash(boolean win) {
        flashWin = win;
        flashAt = System.currentTimeMillis();
    }

    // DonutSMP balance, read from the tab list (player entry / footer / header).
    private static final Pattern BAL = Pattern.compile("\\$\\s*([0-9][0-9,]*(?:\\.[0-9]+)?\\s*[kmbtKMBT]?)");
    private static long balAt = 0L;
    private static String balStr = "";

    private static String balIn(String s) {
        if (s == null) return null;
        Matcher m = BAL.matcher(s);
        if (m.find()) return "$" + m.group(1).replace(" ", "").toUpperCase(java.util.Locale.ROOT);
        return null;
    }

    private static String balance(class_310 mc) {
        long now = System.currentTimeMillis();
        if (now - balAt < 500L) return balStr;
        balAt = now;
        try {
            if (mc.method_1562() != null && mc.field_1724 != null) {
                String b;
                class_640 info = mc.method_1562().method_2871(mc.field_1724.method_5667());
                if (info != null && info.method_2971() != null && (b = balIn(info.method_2971().getString())) != null) { balStr = b; return b; }
                TabOverlayAccessor tab = (TabOverlayAccessor)mc.field_1705.method_1750();
                if (tab.lethalauction$footer() != null && (b = balIn(tab.lethalauction$footer().getString())) != null) { balStr = b; return b; }
                if (tab.lethalauction$header() != null && (b = balIn(tab.lethalauction$header().getString())) != null) { balStr = b; return b; }
            }
        } catch (Throwable t) {
            // ignore
        }
        return balStr;
    }

    public static float a() {
        return 0.54f * K9.x;
    }

    public static float a(int guiWidth) {
        return K9.v * Math.max(1.0f, (float)guiWidth - 250.0f * K27.a());
    }

    public static float b(int guiHeight) {
        return K9.w * Math.max(1.0f, (float)guiHeight - 84.0f * K27.a());
    }

    public static void a(class_332 g, float x, float y) {
        class_310 mc = class_310.method_1551();
        long[] session = Transactions.d();
        long profit = session[0] - session[1];
        boolean up = profit >= 0L;
        int color = up ? -12200829 : -42401;
        float s = K27.a();
        float t = (float)(System.currentTimeMillis() % 3600000L) / 1000.0f;
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K18.a(g, -2, -2, 254, 88, 14, K18.a(color, 0.12f + 0.05f * (float)Math.sin(t * 2.0f)));
        K18.a(g, 0, 0, 250, 84, 12, -435482856, K18.a(color, 0.6f));
        K18.a(g, 0, 14, 3, 56, 1, color);
        K18.a(g, 12, 12, 60, 60, 10, -15722458);
        // Always render the player's head (the skin-model toggle was removed).
        class_7532.method_52722((class_332)g, (class_8685)(mc.field_1724 != null ? mc.field_1724.method_52814() : class_1068.method_52854((GameProfile)mc.method_53462())), (int)18, (int)18, (int)48);
        K18.a(g, "SESSION PROFIT", 86.0f, 22.0f, 10.5f, -7695450, K18.b);
        String amount = (up ? "+" : "-") + Auctions.a(Math.abs(profit));
        K18.a(g, amount, 86.0f, 46.0f, 22.0f, color, K18.c);
        // Optional win-streak badge, top-right - centred, static, with a brief
        // +1 / -All popup. Earned/Spent/Taxes details live in the Transactions tab.
        if (K9.winStreakOn) {
            int orange = 0xFFFF8A3D;
            float wsCx = 214.0f;
            K18.b(g, "STREAK", wsCx, 22.0f, 10.5f, K18.a(orange, 0.9f), K18.b);
            K18.b(g, Integer.toString(K9.winStreak), wsCx, 46.0f, 22.0f, orange, K18.c);
            long since = System.currentTimeMillis() - flashAt;
            if (since >= 0L && since < 1100L) {
                float a = 1.0f - (float)since / 1100.0f;
                int col = flashWin ? 0xFF4DE07A : 0xFFFF5555;
                String txt = flashWin ? "+1" : "- All";
                K18.b(g, txt, wsCx, 63.0f - (1.0f - a) * 6.0f, 11.0f, K18.a(col, a), K18.c);
            }
        }
        // Bottom-right: your current DonutSMP money, read from the tab list.
        String bal = K27.balance(mc);
        if (bal != null && !bal.isEmpty()) {
            int money = -12200829;
            K18.c(g, "balance", 243.0f, 62.0f, 7.5f, -9735291, K18.c);
            K18.c(g, bal, 243.0f, 76.0f, 13.0f, money, K18.c);
        }
        p.popMatrix();
    }
}

