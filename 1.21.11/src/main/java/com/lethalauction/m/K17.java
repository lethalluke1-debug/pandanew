/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 *  net.minecraft.class_5481
 *  net.minecraft.class_640
 *  net.minecraft.class_9011
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K9;
import com.lethalauction.mixin.TabOverlayAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_640;
import net.minecraft.class_9011;

public final class K17 {
    private static final int a = 31996;
    private static final int b = 50677;
    private static final int[] c = new int[]{65280, 10814460, 0xFF0000, 16545539, 42751, 16770560};
    private static final String[] d = new String[]{"Money", "Shards", "Kills", "Deaths", "", "Playtime"};

    private K17() {
    }

    public static boolean a() {
        return K37.b() && K9.aV;
    }

    public static boolean a(class_332 g, class_266 obj) {
        try {
            int titleW;
            class_310 mc = class_310.method_1551();
            class_327 font = mc.field_1772;
            class_269 sb = obj.method_1117();
            List<class_9011> entries = new ArrayList<class_9011>(sb.method_1184(obj));
            entries.removeIf(class_9011::method_55385);
            entries.sort(Comparator.comparingInt(class_9011::comp_2128).reversed());
            if (entries.size() > 15) {
                entries = entries.subList(0, 15);
            }
            ArrayList<class_2561> body = new ArrayList<class_2561>();
            body.add(class_2561.method_43470((String)" "));
            for (class_9011 e : entries) {
                class_268 team = sb.method_1164(e.comp_2127());
                class_2561 name = team != null ? class_268.method_1142((class_270)team, (class_2561)e.method_55387()) : e.method_55387();
                String string = name.getString();
                if (K17.a(string.toLowerCase()) || string.trim().isEmpty()) continue;
                body.add(K17.a((class_2561)name));
            }
            body.add(class_2561.method_43470((String)" "));
            body.add(K17.b());
            Objects.requireNonNull(font);
            int lineH = 9;
            int maxW = titleW = K17.a(font);
            for (class_2561 class_25612 : body) {
                maxW = Math.max(maxW, font.method_27525((class_5348)class_25612));
            }
            int pad = 4;
            int n = maxW + pad * 2;
            int right = g.method_51421();
            int left = right - n;
            int totalH = (body.size() + 1) * lineH + 4;
            int top = (g.method_51443() - totalH) / 2;
            g.method_25294(left, top, right, top + lineH + 2, 0x70000000);
            g.method_25294(left, top + lineH + 2, right, top + totalH, 0x50000000);
            K17.a(g, font, (left + right) / 2, top + 2);
            int y = top + lineH + 4;
            for (class_2561 class_25613 : body) {
                g.method_51430(font, class_25613.method_30937(), left + pad, y, -1, false);
                y += lineH;
            }
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static class_2561 a(class_2561 line) {
        ArrayList texts = new ArrayList();
        ArrayList styles = new ArrayList();
        line.method_27658((style, text) -> {
            texts.add(text);
            styles.add(style);
            return Optional.empty();
        }, class_2583.field_24360);
        int iconRun = -1;
        int statColor = 0xFFFFFF;
        String label = null;
        long bestD = 9000L;
        for (int i = 0; i < styles.size(); ++i) {
            if (((String)texts.get(i)).trim().isEmpty() || ((class_2583)styles.get(i)).method_10973() == null) continue;
            int c = ((class_2583)styles.get(i)).method_10973().method_27716() & 0xFFFFFF;
            for (int k = 0; k < K17.c.length; ++k) {
                long d = K17.a(c, K17.c[k]);
                if (d >= bestD) continue;
                bestD = d;
                iconRun = i;
                statColor = K17.c[k];
                label = K17.d[k].isEmpty() ? (line.getString().contains("(") ? "Team" : "Keyall") : K17.d[k];
            }
        }
        if (label == null) {
            return line;
        }
        boolean hasLabel = line.getString().toLowerCase().contains(label.toLowerCase());
        class_5250 out = class_2561.method_43473();
        for (int i = 0; i < texts.size(); ++i) {
            if (i > iconRun) {
                out.method_10852((class_2561)class_2561.method_43470((String)((String)texts.get(i))).method_27696(class_2583.field_24360.method_36139(statColor)));
            } else {
                out.method_10852((class_2561)class_2561.method_43470((String)((String)texts.get(i))).method_10862((class_2583)styles.get(i)));
            }
            if (i != iconRun || hasLabel) continue;
            String sep = ((String)texts.get(i)).endsWith(" ") ? "" : " ";
            out.method_10852((class_2561)class_2561.method_43470((String)(sep + label + " ")).method_27696(class_2583.field_24360.method_36139(0xFFFFFF)));
        }
        return out;
    }

    private static boolean a(String low) {
        if (low.matches(".*\\d\\s*ms.*")) {
            return true;
        }
        return low.contains("na east") || low.contains("na west") || low.contains("eu ") || low.contains("asia") || low.contains("ocean");
    }

    private static class_2561 b() {
        int ping = K17.c();
        class_2583 gray = class_2583.field_24360.method_36139(10133678);
        class_2583 blue = class_2583.field_24360.method_36139(42751);
        return class_2561.method_43470((String)"Ping ").method_27696(gray).method_10852((class_2561)class_2561.method_43470((String)"(").method_27696(gray)).method_10852((class_2561)class_2561.method_43470((String)(ping + "ms")).method_27696(blue)).method_10852((class_2561)class_2561.method_43470((String)")").method_27696(gray));
    }

    private static int c() {
        try {
            Integer p;
            class_310 mc = class_310.method_1551();
            class_640 info = mc.method_1562().method_2871(mc.field_1724.method_5667());
            if (info != null && info.method_2971() != null && (p = K17.b(info.method_2971().getString())) != null) {
                return p;
            }
            TabOverlayAccessor tab = (TabOverlayAccessor)mc.field_1705.method_1750();
            if (tab.lethalauction$footer() != null && (p = K17.b(tab.lethalauction$footer().getString())) != null) {
                return p;
            }
            if (tab.lethalauction$header() != null && (p = K17.b(tab.lethalauction$header().getString())) != null) {
                return p;
            }
            if (info != null && info.method_2959() > 1) {
                return info.method_2959();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    private static Integer b(String s) {
        if (s == null) {
            return null;
        }
        Matcher m = Pattern.compile("(\\d+)\\s*ms").matcher(s);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }

    private static long a(int a, int b) {
        long dr = (a >> 16 & 0xFF) - (b >> 16 & 0xFF);
        long dg = (a >> 8 & 0xFF) - (b >> 8 & 0xFF);
        long db = (a & 0xFF) - (b & 0xFF);
        return dr * dr + dg * dg + db * db;
    }

    private static int a(class_327 font) {
        String s = "Donut SMP";
        int w = 0;
        for (int i = 0; i < s.length(); ++i) {
            w += font.method_30880(K17.a(s.charAt(i), 0xFFFFFF));
        }
        return w;
    }

    private static void a(class_332 g, class_327 font, int cx, int y) {
        String s = "Donut SMP";
        int x = cx - K17.a(font) / 2;
        int n = s.length();
        for (int i = 0; i < n; ++i) {
            float f = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            int col = 0xFF000000 | K17.a(31996, 50677, f);
            class_5481 seq = K17.a(s.charAt(i), col);
            g.method_51430(font, seq, x, y, col, false);
            x += font.method_30880(seq);
        }
    }

    private static class_5481 a(char c, int color) {
        return class_2561.method_43470((String)String.valueOf(c)).method_27696(class_2583.field_24360.method_10982(Boolean.valueOf(true)).method_36139(color & 0xFFFFFF)).method_30937();
    }

    private static int a(int a, int b, float t) {
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return Math.round((float)ar + (float)(br - ar) * t) << 16 | Math.round((float)ag + (float)(bg - ag) * t) << 8 | Math.round((float)ab + (float)(bb - ab) * t);
    }
}

