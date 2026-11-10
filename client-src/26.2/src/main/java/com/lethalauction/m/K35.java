/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package com.lethalauction.m;

import com.lethalauction.auction.Auctions;
import com.lethalauction.m.K26;
import com.lethalauction.m.K9;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class K35 {
    private static boolean a;

    private K35() {
    }

    public static boolean a(String command) {
        if (a) {
            a = false;
            return true;
        }
        if (!K9.B || command == null) {
            return true;
        }
        String c = command.trim();
        String low = c.toLowerCase(Locale.ROOT);
        if (low.startsWith("/")) {
            c = c.substring(1);
            low = low.substring(1);
        }
        if (!low.startsWith("pay ")) {
            return true;
        }
        String[] parts = c.split("\\s+");
        if (parts.length < 3) {
            return true;
        }
        long amount = Auctions.e(parts[parts.length - 1]);
        long threshold = Math.max(1L, Auctions.e(K9.C));
        if (amount <= 0L || amount < threshold) {
            return true;
        }
        String recipient = parts[1];
        Minecraft mc = Minecraft.getInstance();
        String cmd = c;
        mc.execute(() -> mc.gui.setScreen((Screen)new K26(mc.gui.screen(), cmd, recipient, amount)));
        return false;
    }

    public static void b(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) {
            return;
        }
        a = true;
        try {
            mc.getConnection().sendCommand(command);
        }
        catch (Throwable t) {
            a = false;
        }
    }

    public static String a(long n) {
        if (n >= 1000000000L) {
            return K35.a((double)n / 1.0E9) + "b";
        }
        if (n >= 1000000L) {
            return K35.a((double)n / 1000000.0) + "m";
        }
        if (n >= 1000L) {
            return K35.a((double)n / 1000.0) + "k";
        }
        return Long.toString(n);
    }

    private static String a(double v) {
        String s = String.format(Locale.ROOT, "%.2f", v);
        while (s.contains(".") && (s.endsWith("0") || s.endsWith("."))) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}

