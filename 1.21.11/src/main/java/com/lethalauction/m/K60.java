/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_7532
 *  net.minecraft.class_8685
 *  net.minecraft.class_9779
 *  org.joml.Matrix3x2fStack
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.lethalauction.m;

import com.lethalauction.m.K18;
import com.lethalauction.m.K24;
import com.lethalauction.m.K37;
import com.lethalauction.m.K54;
import com.lethalauction.m.K55;
import com.lethalauction.m.K56;
import com.lethalauction.m.K58;
import com.lethalauction.m.K9;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_7532;
import net.minecraft.class_8685;
import net.minecraft.class_9779;
import org.joml.Matrix3x2fStack;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class K60
implements HudElement {
    public static final int a = 182;
    public static final float b = 0.72f;
    static final int c = 256;
    static final int d = 26;
    private static final int f = -435548650;
    private static final int g = 0x30FFFFFF;
    private static final int h = -7695450;
    private static int i = -1;
    private static class_2960 j;
    private static int k;
    private static final long l = 10000L;
    private static final long m = 1000L;
    private static final List<K54.C101> n;
    private static String o;
    private static class_2960 p;
    private static int q;
    private static int r;
    private static int s;
    private static volatile boolean t;
    public static final K24 e;

    public void render(class_332 g, class_9779 deltaTracker) {
        if (class_310.method_1551().field_1755 == null) {
            K60.a(g);
        }
    }

    public static void a(class_332 g) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (!K9.an || !K37.c() || mc.field_1690.field_1842) {
            return;
        }
        if (!K54.d() && !K54.e() && K54.j() < 0) {
            return;
        }
        float alpha = 1.0f;
        if (K54.i()) {
            long age = System.currentTimeMillis() - K54.k();
            if (age >= 11000L) {
                return;
            }
            if (age > 10000L) {
                alpha = 1.0f - (float)(age - 10000L) / 1000.0f;
            }
        }
        float prev = K18.h;
        K18.h = prev * Math.max(0.0f, Math.min(1.0f, alpha));
        K60.a(g, e.a(g.method_51421()), e.b(g.method_51443()));
        K18.h = prev;
    }

    public static float a() {
        return 0.72f * K9.aq;
    }

    public static int b() {
        return 208;
    }

    public static void a(class_332 g, float x, float y) {
        K60.a(g, x, y, K60.a());
    }

    public static void a(class_332 g, float x, float y, float s) {
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(x, y);
        p.scale(s);
        K60.b(g);
        p.popMatrix();
    }

    static void b(class_332 g) {
        int game;
        List<K54.C101> entries = K54.b();
        int n = entries.size();
        K18.a(g, 0, 0, 182, 208, 14, -435548650, 0x30FFFFFF);
        game = K54.e() || K54.i() ? K54.a() : (K9.ax == 10 ? -1 : K9.ax);
        if (n < 2) {
            K60.a(g, game);
            K60.b(g, n);
            K60.b(g, entries, n);
            K60.e(g);
            K60.d(g);
            return;
        }
        if (game == 0) {
            K60.a(g, entries, n);
        } else if (game == -1) {
            K60.c(g);
        } else {
            K55.a(g, game, entries, n, K54.j(), K54.r(), K54.e(), 6, 6, 170, 170);
        }
        K60.d(g);
        K60.b(g, n);
        K60.b(g, entries, n);
        K60.e(g);
        if (K54.i()) {
            K60.b(g, 91.0f, 91.0f, 81.0f);
        }
    }

    private static void a(class_332 g, List<K54.C101> entries, int n) {
        boolean showNames;
        float cx = 91.0f;
        float cy = 91.0f;
        float R = 81.0f;
        K60.a(n);
        double rot = K54.g();
        double seg = Math.PI * 2 / (double)n;
        K18.a(g, cx, cy, R + 3.0f, 0x22000000);
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(cx, cy);
        p.rotate((float)rot);
        if (j != null) {
            K18.a(g, j, Math.round(-R), Math.round(-R), Math.round(2.0f * R), Math.round(2.0f * R), 256, 256);
        }
        float nameStart = R * 0.3f;
        boolean bl = showNames = (float)(Math.PI * 2 * (double)(R * 0.55f) / (double)n) >= 8.0f;
        if (showNames) {
            for (int i = 0; i < n; ++i) {
                double a = (double)i * seg + seg / 2.0;
                p.pushMatrix();
                p.rotate((float)a);
                String nm = K60.b(entries.get((int)i).a, R - nameStart - 6.0f);
                K18.a(g, nm, nameStart, 0.3f, 8.0f, -1072688617, K18.c);
                K18.a(g, nm, nameStart, 0.0f, 8.0f, -1, K18.c);
                p.popMatrix();
            }
        }
        p.popMatrix();
        K60.b(g, cx, cy);
        K60.a(g, cx, cy - R - 4.0f, 11.0f, 13.0f, -1);
    }

    private static List<K54.C101> d() {
        ArrayList<K54.C101> l = new ArrayList<K54.C101>();
        for (int i = 0; i < 6; ++i) {
            l.add(new K54.C101("", "game"));
        }
        return l;
    }

    private static void a(class_332 g, int game) {
        if (game == -1) {
            K60.c(g);
        } else if (game == 0) {
            K60.a(g, n, n.size());
        } else {
            K55.a(g, game, n, n.size(), -1, 0.0f, false, 6, 6, 170, 170);
        }
        String msg = K54.c() == 0 ? "Waiting for entries" : "Need 2+ to start";
        float w = K18.a(msg, 9.0f, K18.c) + 18.0f;
        float x = 91.0f - w / 2.0f;
        K18.a(g, Math.round(x), 152, Math.round(w), 17, 8, -871756266);
        K18.b(g, msg, 91.0f, 160.5f, 9.0f, -4208940, K18.c);
    }

    private static void c(class_332 g) {
        float t = (float)System.currentTimeMillis() / 1000.0f;
        int[] cols = new int[]{-45702, -11548161, -15797, -10888821, -5215233, -26051};
        for (int i = 0; i < 9; ++i) {
            float bx = 24 + i * 53 % 134;
            float by = 30 + i * 37 % 122;
            float fx = bx + (float)Math.sin((double)t * 0.7 + (double)i) * 7.0f;
            float fy = by + (float)Math.cos((double)t * 0.6 + (double)i * 1.3) * 7.0f;
            float a = 0.18f + 0.12f * (float)Math.sin(t + (float)i);
            K18.b(g, "?", fx, fy, 16.0f + (float)(i % 3 * 6), K18.a(cols[i % cols.length], Math.max(0.08f, a)), K18.c);
        }
        K18.b(g, "?", 91.0f, 91.0f, 40.0f, 0x22FFFFFF, K18.c);
        K18.b(g, "RANDOM", 91.0f, 125.0f, 10.0f, -4208940, K18.c);
    }

    private static void d(class_332 g) {
        long ms = K54.o();
        if (ms < 0L) {
            return;
        }
        int secs = (int)Math.ceil((double)ms / 1000.0);
        boolean low = secs <= 10;
        float shake = 0.0f;
        if (ms <= 10000L && ms > 9000L) {
            shake = 2.0f * (float)(ms - 9000L) / 1000.0f;
        } else if (ms <= 5000L) {
            shake = 0.4f + 0.7f * (1.0f - (float)ms / 5000.0f);
        }
        float t = (float)System.currentTimeMillis() / 1000.0f;
        float dx = (float)(Math.sin(t * 55.0f) * (double)shake);
        String txt = secs >= 60 ? secs / 60 + ":" + String.format("%02d", secs % 60) : secs + "s";
        float w = K18.a(txt, 10.0f, K18.c) + 20.0f;
        float x = 91.0f - w / 2.0f + dx;
        K18.a(g, Math.round(x), 6, Math.round(w), 17, 8, low ? -231074794 : -435022046);
        K18.b(g, txt, x + w / 2.0f, 14.5f, 10.0f, low ? -41907 : -1, K18.c);
    }

    private static float a(float p) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float k = p - 1.0f;
        return 1.0f + c3 * k * k * k + c1 * k * k;
    }

    private static void b(class_332 g, float cx, float cy, float R) {
        float age = System.currentTimeMillis() - K54.k();
        if (age < 0.0f) {
            return;
        }
        int pieces = 70;
        float dur = 2600.0f;
        for (int i = 0; i < pieces; ++i) {
            float seed = (float)(i * 1297 % 1000) / 1000.0f;
            float t = age / dur - seed * 0.25f;
            if (t < 0.0f || t > 1.0f) continue;
            float sx = (float)(i * 73 % 100) / 100.0f * 172.0f + 5.0f;
            float driftX = (float)Math.sin(age / 240.0f + (float)i) * 6.0f;
            float py = -6.0f + t * 206.0f;
            float a2 = 1.0f - t;
            int col = K54.a(i);
            float w = 3 + i % 3;
            float h = 2 + i % 2;
            float prev = K18.h;
            K18.h = prev * Math.max(0.0f, Math.min(1.0f, a2));
            K18.a(g, Math.round(sx + driftX), Math.round(py), Math.round(w), Math.round(h), 1, col);
            K18.h = prev;
        }
        float in = Math.min(1.0f, age / 320.0f);
        float s = 0.6f + 0.4f * K60.a(in);
        Matrix3x2fStack p = g.method_51448();
        p.pushMatrix();
        p.translate(cx, cy - R * 0.42f);
        p.scale(s);
        K18.b(g, "CONGRATULATIONS", 0.6f, 0.6f, 15.0f, -872415232, K18.c);
        K18.b(g, "CONGRATULATIONS", 0.0f, 0.0f, 15.0f, -7605, K18.c);
        p.popMatrix();
    }

    private static void b(class_332 g, List<K54.C101> entries, int n) {
        float cx = 91.0f;
        float sy = 195.0f;
        if (K54.i()) {
            String lbl = "WINNER ";
            float wl = K18.a(lbl, 8.5f, K18.b);
            String w = K60.a(K54.l(), 162.0f - wl);
            float wn = K18.a(w, 12.0f, K18.c);
            float x = cx - (wl + wn) / 2.0f;
            K18.a(g, lbl, x, sy, 8.5f, -10888821, K18.b);
            K18.a(g, w, x + wl, sy, 12.0f, -1, K18.c);
        } else if (K54.e()) {
            K18.b(g, "Spinning\u2026", cx, sy, 10.0f, -2893598, K18.c);
        } else if (K54.d()) {
            K18.b(g, K60.f(), cx, sy, 9.5f, -1578256, K18.c);
        } else {
            K18.b(g, n + " entries", cx, sy, 9.5f, -7695450, K18.b);
        }
    }

    private static void e(class_332 g) {
        long age = System.currentTimeMillis() - K54.f();
        String nm = K54.t();
        if (nm == null || nm.isEmpty() || age > 2200L) {
            return;
        }
        float in = Math.min(1.0f, (float)age / 220.0f);
        float out = age > 1700L ? 1.0f - (float)(age - 1700L) / 500.0f : 1.0f;
        float ease = 1.0f - (float)Math.pow(1.0f - in, 3.0);
        float rise = (1.0f - ease) * 8.0f;
        String txt = "+ " + nm;
        float tw = K18.a(txt, 9.0f, K18.c);
        float w = tw + 18.0f;
        float x = 91.0f - w / 2.0f;
        float y = 28.0f + rise;
        float prev = K18.h;
        K18.h = prev * Math.max(0.0f, Math.min(1.0f, out));
        K18.a(g, Math.round(x), Math.round(y), Math.round(w), 16, 8, -435153118);
        K18.a(g, "+", x + 8.0f, y + 8.0f, 9.5f, -10888821, K18.c);
        K18.a(g, nm, x + 8.0f + K18.a("+ ", 9.0f, K18.c), y + 8.0f, 9.0f, -1, K18.c);
        K18.h = prev;
    }

    private static void b(class_332 g, float cx, float cy) {
        K18.a(g, cx, cy, 19.0f, -15657697);
        K18.a(g, cx, cy, 17.0f, -14999248);
        class_2960 img = K60.e();
        if (img != null) {
            float s = 32.0f;
            K18.a(g, img, Math.round(cx - s / 2.0f), Math.round(cy - s / 2.0f), Math.round(s), Math.round(s), q, r);
        } else {
            K18.a(g, cx, cy, 13.0f, -15797);
            K18.b(g, "$", cx, cy - 0.5f, 15.0f, -12965114, K18.c);
        }
    }

    public static void c() {
        if (t) {
            return;
        }
        t = true;
        Thread thread = new Thread(() -> {
            try {
                String chosen;
                try (MemoryStack stack = MemoryStack.stackPush();){
                    PointerBuffer filters = stack.mallocPointer(1);
                    filters.put(stack.UTF8((CharSequence)"*.png"));
                    filters.flip();
                    chosen = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Choose a PNG for the wheel center", (CharSequence)"", (PointerBuffer)filters, (CharSequence)"PNG images", (boolean)false);
                }
                if (chosen == null) {
                    return;
                }
                byte[] bytes = Files.readAllBytes(Path.of(chosen, new String[0]));
                try (class_1011 check = class_1011.method_49277((byte[])bytes);){
                    if (check.method_4307() < 1) {
                        return;
                    }
                }
                Path dir = FabricLoader.getInstance().getConfigDir().resolve("lethalauction");
                Files.createDirectories(dir, new FileAttribute[0]);
                Path dest = dir.resolve("wheel-center.png");
                Files.write(dest, bytes, new OpenOption[0]);
                class_310.method_1551().execute(() -> {
                    K9.aw = dest.toString();
                    K9.b();
                    o = null;
                });
            }
            catch (Exception exception) {
            }
            finally {
                t = false;
            }
        }, "Lethal wheel image picker");
        thread.setDaemon(true);
        thread.start();
    }

    private static class_2960 e() {
        String path;
        String string = path = K9.aw == null ? "" : K9.aw.trim();
        if (path.isEmpty()) {
            p = null;
            o = "";
            return null;
        }
        if (path.equals(o)) {
            return p;
        }
        o = path;
        try {
            byte[] bytes = Files.readAllBytes(Path.of(path, new String[0]));
            class_1011 img = class_1011.method_49277((byte[])bytes);
            q = img.method_4307();
            r = img.method_4323();
            K60.a(img);
            class_2960 loc = class_2960.method_60655((String)"lethalauction", (String)("wheel/center_" + ++s));
            class_310.method_1551().method_1531().method_4616(loc, (class_1044)new class_1043(() -> "Lethal wheel center", img));
            p = loc;
        }
        catch (Exception e) {
            p = null;
        }
        return p;
    }

    private static void a(class_1011 img) {
        int w = img.method_4307();
        int h = img.method_4323();
        float cx = (float)w / 2.0f;
        float cy = (float)h / 2.0f;
        float r = (float)Math.min(w, h) / 2.0f;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float dx = (float)x + 0.5f - cx;
                float dy = (float)y + 0.5f - cy;
                float d = (float)Math.sqrt(dx * dx + dy * dy);
                float keep = Math.max(0.0f, Math.min(1.0f, r - d + 0.5f));
                if (keep >= 1.0f) continue;
                int p = img.method_61940(x, y);
                int a = Math.round((float)(p >>> 24 & 0xFF) * keep);
                img.method_61941(x, y, p & 0xFFFFFF | a << 24);
            }
        }
    }

    private static void b(class_332 g, int n) {
        String label = n + (n == 1 ? " entry" : " entries");
        float tw = K18.a(label, 8.5f, K18.c);
        float w = tw + 22.0f;
        K18.a(g, 8, 6, Math.round(w), 17, 8, -435022046);
        K58.a(g, 60402, 19.0f, 14.5f, 10.0f, -4208940);
        K18.a(g, label, 27.0f, 14.5f, 8.5f, -1, K18.c);
    }

    private static void a(class_332 g, K54.C101 e, float cx, float cy, float s) {
        class_2960 tex;
        if (s < 1.0f) {
            return;
        }
        int size = Math.round(s);
        int x = Math.round(cx - (float)size / 2.0f);
        int y = Math.round(cy - (float)size / 2.0f);
        K18.a(g, x - 1, y - 1, size + 2, size + 2, 2, 0x66000000);
        class_8685 skin = K60.b(e.a);
        if (skin != null) {
            try {
                class_7532.method_52722((class_332)g, (class_8685)skin, (int)x, (int)y, (int)size);
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (K60.a(e.b)) {
            int col = K58.c(e.b);
            K18.a(g, x, y, size, size, 2, K18.a(col, -15986922, 0.55f));
            K58.a(g, K58.b(e.b), cx, cy, (float)size * 0.8f, col);
            return;
        }
        String url = K60.c(e.a);
        class_2960 class_29602 = tex = url == null ? null : K56.a(url);
        if (tex != null) {
            K18.a(g, tex, x, y, size, size, K56.c(url), K56.d(url));
        } else {
            K18.a(g, x, y, size, size, 2, -13946042);
        }
    }

    private static boolean a(String p) {
        return "twitch".equals(p) || "youtube".equals(p) || "tiktok".equals(p);
    }

    private static class_8685 b(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        class_310 mc = class_310.method_1551();
        class_634 conn = mc.method_1562();
        if (conn == null) {
            return null;
        }
        try {
            for (class_640 info : conn.method_2880()) {
                if (!info.method_2966().name().equalsIgnoreCase(name)) continue;
                return info.method_52810();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private static String c(String name) {
        if (name == null) {
            return null;
        }
        String n = name.trim();
        if (!n.matches("[A-Za-z0-9_]{1,16}")) {
            return null;
        }
        return "https://mc-heads.net/avatar/" + n + "/16.png";
    }

    private static String f() {
        String custom;
        String string = custom = K9.au == null ? "" : K9.au.trim();
        if (!custom.isEmpty()) {
            return custom;
        }
        String amt = K9.at == null || K9.at.isBlank() ? "1" : K9.at.trim();
        return "pay $" + amt + " to enter";
    }

    private static void a(class_332 g, float cx, float topY, float w, float h, int color) {
        int rows = Math.round(h);
        for (int r = 0; r < rows; ++r) {
            float t = (float)r / (float)rows;
            float hw = w / 2.0f * (1.0f - t);
            K18.a(g, Math.round(cx - hw), Math.round(topY + (float)r), Math.max(1, Math.round(hw * 2.0f)), 1, color);
        }
    }

    private static String a(String s, float maxW) {
        if (s == null) {
            return "";
        }
        if (K18.a(s, 12.0f, K18.c) <= maxW) {
            return s;
        }
        while (s.length() > 1 && K18.a(s + "\u2026", 12.0f, K18.c) > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "\u2026";
    }

    private static String b(String s, float maxW) {
        if (s == null) {
            return "";
        }
        if (K18.a(s, 8.0f, K18.c) <= maxW) {
            return s;
        }
        while (s.length() > 1 && K18.a(s + "\u2026", 8.0f, K18.c) > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "\u2026";
    }

    private static void a(int n) {
        if (n == i && j != null) {
            return;
        }
        i = n;
        try {
            class_1011 img = K60.b(n);
            class_2960 loc = class_2960.method_60655((String)"lethalauction", (String)("wheel/w_" + ++k));
            class_1043 tex = new class_1043(() -> "Lethal wheel", img);
            class_310.method_1551().method_1531().method_4616(loc, (class_1044)tex);
            j = loc;
        }
        catch (Exception e) {
            j = null;
        }
    }

    private static class_1011 b(int n) {
        class_1011 img = new class_1011(256, 256, true);
        double c = 128.0;
        double r = c - 4.0;
        double seg = Math.PI * 2 / (double)n;
        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 256; ++x) {
                double dx = (double)x + 0.5 - c;
                double dy = (double)y + 0.5 - c;
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d > r || d < 17.0) {
                    img.method_61941(x, y, 0);
                    continue;
                }
                double ang = Math.atan2(dy, dx);
                if (ang < 0.0) {
                    ang += Math.PI * 2;
                }
                int si = (int)(ang / seg) % n;
                int col = K54.a(si);
                double within = ang - (double)si * seg;
                double edge = Math.min(within, seg - within) * d;
                if (edge < 1.1) {
                    col = -15986664;
                } else if (d > r - 5.0) {
                    col = K60.a(col, 0.72f);
                } else if (d > r - 9.0) {
                    col = K60.a(col, 0.88f);
                }
                img.method_61941(x, y, col);
            }
        }
        return img;
    }

    private static int a(int argb, float f) {
        int a = argb >>> 24;
        int rr = Math.round((float)(argb >> 16 & 0xFF) * f);
        int gg = Math.round((float)(argb >> 8 & 0xFF) * f);
        int bb = Math.round((float)(argb & 0xFF) * f);
        return a << 24 | rr << 16 | gg << 8 | bb;
    }

    static {
        n = K60.d();
        q = 1;
        r = 1;
        e = new K24(){

            @Override
            public String a() {
                return "the Chat Games wheel";
            }

            @Override
            public float b() {
                return 182.0f * K60.a();
            }

            @Override
            public float c() {
                return (float)K60.b() * K60.a();
            }

            @Override
            public float d() {
                return K9.ao;
            }

            @Override
            public float e() {
                return K9.ap;
            }

            @Override
            public void a(float fx, float fy) {
                K9.ao = fx;
                K9.ap = fy;
            }

            @Override
            public float f() {
                return K9.aq;
            }

            @Override
            public void a(float size) {
                K9.aq = size;
            }

            @Override
            public void g() {
                K9.ao = 0.5f;
                K9.ap = 0.2f;
                K9.aq = 1.0f;
            }

            @Override
            public void a(class_332 g, float x, float y) {
                K60.a(g, x, y);
            }
        };
    }
}

