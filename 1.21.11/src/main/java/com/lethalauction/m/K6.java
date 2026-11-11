/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_1297
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5603
 *  net.minecraft.class_5606
 *  net.minecraft.class_5607
 *  net.minecraft.class_5609
 *  net.minecraft.class_5610
 *  net.minecraft.class_630
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K47;
import com.lethalauction.m.K8;
import com.lethalauction.m.K9;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.class_630;

public final class K6 {
    public static final boolean a = false;
    private static final int c = 0;
    private static final int d = 1;
    private static final int e = 2;
    private static final int f = 128;
    public static final List<C71> b = List.of(new C71("tophat", "Top Hat", C73.HAT, List.of(new C72("Classic", 0x1A1A1E, 13111342, 13938487)), List.of(new C70(C69.HEAD, -5.5f, -8.6f, -5.5f, 11.0f, 1.0f, 11.0f, 0), new C70(C69.HEAD, -3.5f, -15.5f, -3.5f, 7.0f, 7.0f, 7.0f, 0), new C70(C69.HEAD, -3.6f, -10.3f, -3.6f, 7.2f, 1.6f, 7.2f, 1))), new C71("beanie", "Beanie", C73.HAT, List.of(new C72("Cherry", 14100029, 0xF2F2F2, 0xF2F2F2)), List.of(new C70(C69.HEAD, -4.5f, -9.2f, -4.5f, 9.0f, 4.5f, 9.0f, 0), new C70(C69.HEAD, -4.7f, -5.4f, -4.7f, 9.4f, 1.8f, 9.4f, 1), new C70(C69.HEAD, -1.5f, -11.0f, -1.5f, 3.0f, 2.0f, 3.0f, 2))), new C71("cap", "Cap", C73.HAT, List.of(new C72("Lethal", 2061567, 728644, 0xFFFFFF)), List.of(new C70(C69.HEAD, -4.4f, -8.8f, -4.4f, 8.8f, 3.3f, 8.8f, 0), new C70(C69.HEAD, -4.2f, -5.9f, -8.2f, 8.4f, 0.6f, 4.0f, 1), new C70(C69.HEAD, -1.3f, -8.1f, -4.7f, 2.6f, 1.8f, 0.4f, 2), new C70(C69.HEAD, -0.6f, -9.3f, -0.6f, 1.2f, 0.5f, 1.2f, 1))), new C71("jordanlows", "Jordan Lows", C73.SHOES, List.of(new C72("Chicago", 0xF4F4F4, 13770542, 0x141414)), K6.a(new C70(null, -2.4f, 9.9f, -2.4f, 4.8f, 1.8f, 4.8f, 0), new C70(null, -2.4f, 10.5f, -3.5f, 4.8f, 1.2f, 1.2f, 0), new C70(null, -2.45f, 10.45f, -3.6f, 4.9f, 1.3f, 1.0f, 1), new C70(null, -2.45f, 9.9f, 1.9f, 4.9f, 1.8f, 0.6f, 1), new C70(null, -2.45f, 9.6f, -2.45f, 4.9f, 0.4f, 4.9f, 1), new C70(null, 2.35f, 10.3f, -2.6f, 0.25f, 0.6f, 4.0f, 2), new C70(null, -2.6f, 10.3f, -2.6f, 0.25f, 0.6f, 4.0f, 2), new C70(null, -2.5f, 11.5f, -3.6f, 5.0f, 0.6f, 6.2f, 0))), new C71("basketball", "Basketball Shoes", C73.SHOES, List.of(new C72("Black Gold", 1447965, 0xF4F4F4, 16106818)), K6.a(new C70(null, -2.5f, 8.2f, -2.5f, 5.0f, 3.5f, 5.0f, 0), new C70(null, -2.5f, 10.3f, -3.6f, 5.0f, 1.4f, 1.2f, 0), new C70(null, -2.6f, 11.3f, -3.8f, 5.2f, 0.9f, 6.5f, 1), new C70(null, -2.55f, 8.9f, -2.55f, 5.1f, 0.8f, 5.1f, 2), new C70(null, 2.4f, 9.4f, -2.2f, 0.3f, 1.6f, 3.5f, 2), new C70(null, -2.7f, 9.4f, -2.2f, 0.3f, 1.6f, 3.5f, 2))), new C71("running", "Running Shoes", C73.SHOES, List.of(new C72("Volt", 6185837, 0xF4F4F4, 8191823)), K6.a(new C70(null, -2.35f, 10.0f, -2.35f, 4.7f, 1.6f, 4.7f, 0), new C70(null, -2.35f, 10.6f, -3.5f, 4.7f, 1.0f, 1.2f, 0), new C70(null, -2.45f, 11.3f, -3.6f, 4.9f, 0.8f, 6.1f, 1), new C70(null, 2.3f, 10.3f, -3.0f, 0.25f, 0.5f, 4.8f, 2), new C70(null, -2.55f, 10.3f, -3.0f, 0.25f, 0.5f, 4.8f, 2), new C70(null, -0.8f, 9.6f, 2.3f, 1.6f, 1.0f, 0.3f, 2))), new C71("headphones", "Headphones", C73.AUDIO, List.of(new C72("Midnight", 1447965, 2764344, 5082111), new C72("Snow", 15922424, 13225688, 10134196), new C72("Lethal", 2061567, 728644, 0xFFFFFF), new C72("Crimson", 13770542, 0x141414, 0xF4F4F4), new C72("Bubblegum", 16736162, 0xFFE3EF, 16747200), new C72("Gold", 1316379, 13938487, 16106818)), List.of(new C70(C69.HEAD, -5.0f, -9.1f, -1.4f, 10.0f, 1.4f, 2.8f, 1), new C70(C69.HEAD, -5.1f, -8.0f, -1.2f, 1.3f, 3.6f, 2.4f, 1), new C70(C69.HEAD, 3.8f, -8.0f, -1.2f, 1.3f, 3.6f, 2.4f, 1), new C70(C69.HEAD, -5.8f, -5.6f, -2.0f, 1.8f, 3.8f, 4.0f, 0), new C70(C69.HEAD, 4.0f, -5.6f, -2.0f, 1.8f, 3.8f, 4.0f, 0), new C70(C69.HEAD, -6.0f, -5.1f, -1.4f, 0.6f, 2.8f, 2.8f, 2), new C70(C69.HEAD, 5.4f, -5.1f, -1.4f, 0.6f, 2.8f, 2.8f, 2))));
    private static final Map<String, class_1043> g = new HashMap<String, class_1043>();
    private static boolean h;

    private static List<C70> a(C70 ... perFoot) {
        ArrayList<C70> boxes = new ArrayList<C70>();
        for (C69 leg : new C69[]{C69.RIGHT_LEG, C69.LEFT_LEG}) {
            for (C70 b : perFoot) {
                boxes.add(new C70(leg, b.b(), b.c(), b.d(), b.e(), b.f(), b.g(), b.h()));
            }
        }
        return boxes;
    }

    private K6() {
    }

    public static C71 a(String id) {
        for (C71 i : b) {
            if (!i.a.equals(id)) continue;
            return i;
        }
        return null;
    }

    public static Map<C73, C74> b(String text) {
        LinkedHashMap<C73, C74> out = new LinkedHashMap<C73, C74>();
        if (text == null || text.isEmpty()) {
            return out;
        }
        for (String part : text.split(",")) {
            int colon = part.indexOf(58);
            C71 item = K6.a(colon < 0 ? part : part.substring(0, colon));
            if (item == null) continue;
            int variant = 0;
            try {
                variant = colon < 0 ? 0 : Integer.parseInt(part.substring(colon + 1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            out.put(item.c, new C74(item, Math.max(0, Math.min(item.d.size() - 1, variant))));
        }
        return out;
    }

    public static String a(Map<C73, C74> worn) {
        ArrayList<String> parts = new ArrayList<String>();
        for (C74 w : worn.values()) {
            parts.add((String)((Object)(w.a().a + ":" + w.b())));
        }
        return String.join((CharSequence)",", parts);
    }

    public static void a(C71 item, int variant) {
        Map<C73, C74> worn = K6.b(K9.aG);
        C74 now = worn.get((Object)item.c);
        if (now != null && now.a() == item && now.b() == variant) {
            worn.remove((Object)item.c);
        } else {
            worn.put(item.c, new C74(item, variant));
        }
        K9.aG = K6.a(worn);
        K9.b();
    }

    public static void a(C73 slot) {
        Map<C73, C74> worn = K6.b(K9.aG);
        worn.remove((Object)slot);
        K9.aG = K6.a(worn);
        K9.b();
    }

    public static String a() {
        String st;
        Object base = K9.aG;
        if (K9.Z && K47.i()) {
            int v = Math.max(0, Math.min(5, K9.aa));
            Object object = base = ((String)base).isEmpty() ? "headphones:" + v : (String)base + ",headphones:" + v;
        }
        if (!(st = K8.b()).isEmpty()) {
            base = ((String)base).isEmpty() ? st : (String)base + "," + st;
        }
        return (String)base;
    }

    public static String a(int entityId) {
        class_310 mc = class_310.method_1551();
        if (!K37.e() || mc.field_1687 == null) {
            return "";
        }
        class_1297 e = mc.field_1687.method_8469(entityId);
        if (e == null) {
            return "";
        }
        if (mc.field_1724 != null && e.method_5667().equals(mc.field_1724.method_5667())) {
            return K6.a();
        }
        UUID id = e.method_5667();
        return K38.e(id) ? K38.a(id) : "";
    }

    static void b() {
        if (h) {
            return;
        }
        h = true;
        for (C71 item : b) {
            int u = 0;
            int v = 0;
            int rowHeight = 0;
            EnumMap<C69, class_5609> meshes = new EnumMap<C69, class_5609>(C69.class);
            for (int i = 0; i < item.e.size(); ++i) {
                C70 b = item.e.get(i);
                int rw = (int)Math.ceil(2.0f * (b.e() + b.g())) + 1;
                int rh = (int)Math.ceil(b.g() + b.f()) + 1;
                if (u + rw > 128) {
                    u = 0;
                    v += rowHeight;
                    rowHeight = 0;
                }
                item.g[i] = new int[]{u, v, rw, rh};
                class_5609 mesh = meshes.computeIfAbsent(b.a(), k -> new class_5609());
                class_5610 root = mesh.method_32111();
                root.method_32117("box" + i, class_5606.method_32108().method_32101(u, v).method_32097(b.b(), b.c(), b.d(), b.e(), b.f(), b.g()), class_5603.field_27701);
                u += rw;
                rowHeight = Math.max(rowHeight, rh);
            }
            for (Map.Entry e : meshes.entrySet()) {
                item.f.put((C69)((Object)e.getKey()), class_5607.method_32110((class_5609)((class_5609)e.getValue()), (int)128, (int)128).method_32109());
            }
        }
    }

    static class_2960 b(C71 item, int variant) {
        String key = item.a + "_" + variant;
        class_2960 location = class_2960.method_60655((String)"lethalauction", (String)("cosmetics/" + key));
        if (!g.containsKey(key)) {
            class_1011 img = new class_1011(128, 128, true);
            C72 p = item.d.get(variant);
            for (int i = 0; i < item.e.size(); ++i) {
                int[] r = item.g[i];
                int role = item.e.get(i).h();
                for (int y = r[1]; y < Math.min(128, r[1] + r[3]); ++y) {
                    for (int x = r[0]; x < Math.min(128, r[0] + r[2]); ++x) {
                        int base = switch (role) {
                            case 1 -> p.c();
                            case 2 -> p.d();
                            default -> p.b();
                        };
                        float grain = 0.93f + 0.1f * K6.a(x, y, i);
                        img.method_61941(x, y, 0xFF000000 | K6.a(base, grain));
                    }
                }
            }
            class_1043 texture = new class_1043(() -> "Lethal Client cosmetic " + key, img);
            class_310.method_1551().method_1531().method_4616(location, (class_1044)texture);
            g.put(key, texture);
        }
        return location;
    }

    private static float a(int x, int y, int salt) {
        double h = Math.sin((double)x * 127.1 + (double)y * 311.7 + (double)salt * 74.7) * 43758.5453;
        return (float)(h - Math.floor(h));
    }

    private static int a(int c, float k) {
        int r = Math.min(255, Math.round((float)(c >> 16 & 0xFF) * k));
        int g = Math.min(255, Math.round((float)(c >> 8 & 0xFF) * k));
        int b = Math.min(255, Math.round((float)(c & 0xFF) * k));
        return r << 16 | g << 8 | b;
    }

    static enum C69 {
        HEAD,
        RIGHT_LEG,
        LEFT_LEG;

    }

    static final class C70 {
        private final C69 a;
        private final float b;
        private final float c;
        private final float d;
        private final float e;
        private final float f;
        private final float g;
        private final int h;
        C70(C69 a, float b, float c, float d, float e, float f, float g, int h) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f; this.g = g; this.h = h; }
        public C69 a() { return this.a; }
        public float b() { return this.b; }
        public float c() { return this.c; }
        public float d() { return this.d; }
        public float e() { return this.e; }
        public float f() { return this.f; }
        public float g() { return this.g; }
        public int h() { return this.h; }


    }

    public static final class C71 {
        public final String a;
        public final String b;
        public final C73 c;
        public final List<C72> d;
        final List<C70> e;
        final Map<C69, class_630> f = new EnumMap<C69, class_630>(C69.class);
        final int[][] g;

        C71(String id, String name, C73 slot, List<C72> palettes, List<C70> boxes) {
            this.a = id;
            this.b = name;
            this.c = slot;
            this.d = palettes;
            this.e = boxes;
            this.g = new int[boxes.size()][4];
        }
    }

    public static enum C73 {
        HAT("Hats"),
        SHOES("Shoes"),
        AUDIO("Audio");

        public final String title;

        private C73(String title) {
            this.title = title;
        }
    }

    public static final class C74 {
        private final C71 a;
        private final int b;
        C74(C71 a, int b) { this.a = a; this.b = b; }
        public C71 a() { return this.a; }
        public int b() { return this.b; }


    }

    public static final class C72 {
        private final String a;
        private final int b;
        private final int c;
        private final int d;
        C72(String a, int b, int c, int d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public int b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }


    }
}

