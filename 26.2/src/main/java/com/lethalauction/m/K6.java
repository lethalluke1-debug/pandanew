/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.Entity
 */
package com.lethalauction.m;

import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K47;
import com.lethalauction.m.K8;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.platform.NativeImage;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public final class K6 {
    public static final boolean a = false;
    private static final int c = 0;
    private static final int d = 1;
    private static final int e = 2;
    private static final int f = 128;
    public static final List<C72> b = List.of(new C72("tophat", "Top Hat", C74.HAT, List.of(new C73("Classic", 0x1A1A1E, 13111342, 13938487)), List.of(new C71(C70.HEAD, -5.5f, -8.6f, -5.5f, 11.0f, 1.0f, 11.0f, 0), new C71(C70.HEAD, -3.5f, -15.5f, -3.5f, 7.0f, 7.0f, 7.0f, 0), new C71(C70.HEAD, -3.6f, -10.3f, -3.6f, 7.2f, 1.6f, 7.2f, 1))), new C72("beanie", "Beanie", C74.HAT, List.of(new C73("Cherry", 14100029, 0xF2F2F2, 0xF2F2F2)), List.of(new C71(C70.HEAD, -4.5f, -9.2f, -4.5f, 9.0f, 4.5f, 9.0f, 0), new C71(C70.HEAD, -4.7f, -5.4f, -4.7f, 9.4f, 1.8f, 9.4f, 1), new C71(C70.HEAD, -1.5f, -11.0f, -1.5f, 3.0f, 2.0f, 3.0f, 2))), new C72("cap", "Cap", C74.HAT, List.of(new C73("Lethal", 2061567, 728644, 0xFFFFFF)), List.of(new C71(C70.HEAD, -4.4f, -8.8f, -4.4f, 8.8f, 3.3f, 8.8f, 0), new C71(C70.HEAD, -4.2f, -5.9f, -8.2f, 8.4f, 0.6f, 4.0f, 1), new C71(C70.HEAD, -1.3f, -8.1f, -4.7f, 2.6f, 1.8f, 0.4f, 2), new C71(C70.HEAD, -0.6f, -9.3f, -0.6f, 1.2f, 0.5f, 1.2f, 1))), new C72("backwardscap", "Backwards Cap", C74.HAT, List.of(new C73("Lethal", 2061567, 728644, 0xFFFFFF), new C73("Crimson", 13770542, 0x141414, 0xF4F4F4), new C73("Royal", 3038392, 1981048, 0xFFFFFF), new C73("Snow", 15921906, 13619151, 3815994)), List.of(new C71(C70.HEAD, -4.4f, -8.8f, -4.4f, 8.8f, 3.3f, 8.8f, 0), new C71(C70.HEAD, -4.2f, -5.9f, 4.2f, 8.4f, 0.6f, 4.0f, 1), new C71(C70.HEAD, -2.0f, -7.4f, -4.8f, 4.0f, 1.8f, 0.4f, 2), new C71(C70.HEAD, -0.6f, -9.3f, -0.6f, 1.2f, 0.5f, 1.2f, 1))), new C72("jordanlows", "Jordan Lows", C74.SHOES, List.of(new C73("Chicago", 0xF4F4F4, 13770542, 0x141414)), K6.a(new C71(null, -2.4f, 9.9f, -2.4f, 4.8f, 1.8f, 4.8f, 0), new C71(null, -2.4f, 10.5f, -3.5f, 4.8f, 1.2f, 1.2f, 0), new C71(null, -2.45f, 10.45f, -3.6f, 4.9f, 1.3f, 1.0f, 1), new C71(null, -2.45f, 9.9f, 1.9f, 4.9f, 1.8f, 0.6f, 1), new C71(null, -2.45f, 9.6f, -2.45f, 4.9f, 0.4f, 4.9f, 1), new C71(null, 2.35f, 10.3f, -2.6f, 0.25f, 0.6f, 4.0f, 2), new C71(null, -2.6f, 10.3f, -2.6f, 0.25f, 0.6f, 4.0f, 2), new C71(null, -2.5f, 11.5f, -3.6f, 5.0f, 0.6f, 6.2f, 0))), new C72("basketball", "Basketball Shoes", C74.SHOES, List.of(new C73("Black Gold", 1447965, 0xF4F4F4, 16106818)), K6.a(new C71(null, -2.5f, 8.2f, -2.5f, 5.0f, 3.5f, 5.0f, 0), new C71(null, -2.5f, 10.3f, -3.6f, 5.0f, 1.4f, 1.2f, 0), new C71(null, -2.6f, 11.3f, -3.8f, 5.2f, 0.9f, 6.5f, 1), new C71(null, -2.55f, 8.9f, -2.55f, 5.1f, 0.8f, 5.1f, 2), new C71(null, 2.4f, 9.4f, -2.2f, 0.3f, 1.6f, 3.5f, 2), new C71(null, -2.7f, 9.4f, -2.2f, 0.3f, 1.6f, 3.5f, 2))), new C72("running", "Running Shoes", C74.SHOES, List.of(new C73("Volt", 6185837, 0xF4F4F4, 8191823)), K6.a(new C71(null, -2.35f, 10.0f, -2.35f, 4.7f, 1.6f, 4.7f, 0), new C71(null, -2.35f, 10.6f, -3.5f, 4.7f, 1.0f, 1.2f, 0), new C71(null, -2.45f, 11.3f, -3.6f, 4.9f, 0.8f, 6.1f, 1), new C71(null, 2.3f, 10.3f, -3.0f, 0.25f, 0.5f, 4.8f, 2), new C71(null, -2.55f, 10.3f, -3.0f, 0.25f, 0.5f, 4.8f, 2), new C71(null, -0.8f, 9.6f, 2.3f, 1.6f, 1.0f, 0.3f, 2))), new C72("headphones", "Headphones", C74.AUDIO, List.of(new C73("Midnight", 1447965, 2764344, 5082111), new C73("Snow", 15922424, 13225688, 10134196), new C73("Lethal", 2061567, 728644, 0xFFFFFF), new C73("Crimson", 13770542, 0x141414, 0xF4F4F4), new C73("Bubblegum", 16736162, 0xFFE3EF, 16747200), new C73("Gold", 1316379, 13938487, 16106818)), List.of(new C71(C70.HEAD, -5.0f, -9.1f, -1.4f, 10.0f, 1.4f, 2.8f, 1), new C71(C70.HEAD, -5.1f, -8.0f, -1.2f, 1.3f, 3.6f, 2.4f, 1), new C71(C70.HEAD, 3.8f, -8.0f, -1.2f, 1.3f, 3.6f, 2.4f, 1), new C71(C70.HEAD, -5.8f, -5.6f, -2.0f, 1.8f, 3.8f, 4.0f, 0), new C71(C70.HEAD, 4.0f, -5.6f, -2.0f, 1.8f, 3.8f, 4.0f, 0), new C71(C70.HEAD, -6.0f, -5.1f, -1.4f, 0.6f, 2.8f, 2.8f, 2), new C71(C70.HEAD, 5.4f, -5.1f, -1.4f, 0.6f, 2.8f, 2.8f, 2))));
    private static final Map<String, DynamicTexture> g = new HashMap<String, DynamicTexture>();
    private static boolean h;

    private static List<C71> a(C71 ... perFoot) {
        ArrayList<C71> boxes = new ArrayList<C71>();
        for (C70 leg : new C70[]{C70.RIGHT_LEG, C70.LEFT_LEG}) {
            for (C71 b : perFoot) {
                boxes.add(new C71(leg, b.b(), b.c(), b.d(), b.e(), b.f(), b.g(), b.h()));
            }
        }
        return boxes;
    }

    private K6() {
    }

    public static C72 a(String id) {
        for (C72 i : b) {
            if (!i.a.equals(id)) continue;
            return i;
        }
        return null;
    }

    public static Map<C74, C75> b(String text) {
        LinkedHashMap<C74, C75> out = new LinkedHashMap<C74, C75>();
        if (text == null || text.isEmpty()) {
            return out;
        }
        for (String part : text.split(",")) {
            int colon = part.indexOf(58);
            C72 item = K6.a(colon < 0 ? part : part.substring(0, colon));
            if (item == null) continue;
            int variant = 0;
            try {
                variant = colon < 0 ? 0 : Integer.parseInt(part.substring(colon + 1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            out.put(item.c, new C75(item, Math.max(0, Math.min(item.d.size() - 1, variant))));
        }
        return out;
    }

    public static String a(Map<C74, C75> worn) {
        ArrayList<String> parts = new ArrayList<String>();
        for (C75 w : worn.values()) {
            parts.add((String)((Object)(w.a().a + ":" + w.b())));
        }
        return String.join((CharSequence)",", parts);
    }

    public static void a(C72 item, int variant) {
        Map<C74, C75> worn = K6.b(K9.aG);
        C75 now = worn.get((Object)item.c);
        if (now != null && now.a() == item && now.b() == variant) {
            worn.remove((Object)item.c);
        } else {
            worn.put(item.c, new C75(item, variant));
        }
        K9.aG = K6.a(worn);
        K9.b();
    }

    public static void a(C74 slot) {
        Map<C74, C75> worn = K6.b(K9.aG);
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
        Minecraft mc = Minecraft.getInstance();
        if (!K37.e() || mc.level == null) {
            return "";
        }
        Entity e = mc.level.getEntity(entityId);
        if (e == null) {
            return "";
        }
        if (mc.player != null && e.getUUID().equals(mc.player.getUUID())) {
            return K6.a();
        }
        UUID id = e.getUUID();
        return K38.e(id) ? K38.a(id) : "";
    }

    static void b() {
        if (h) {
            return;
        }
        h = true;
        for (C72 item : b) {
            int u = 0;
            int v = 0;
            int rowHeight = 0;
            EnumMap<C70, MeshDefinition> meshes = new EnumMap<C70, MeshDefinition>(C70.class);
            for (int i = 0; i < item.e.size(); ++i) {
                C71 b = item.e.get(i);
                int rw = (int)Math.ceil(2.0f * (b.e() + b.g())) + 1;
                int rh = (int)Math.ceil(b.g() + b.f()) + 1;
                if (u + rw > 128) {
                    u = 0;
                    v += rowHeight;
                    rowHeight = 0;
                }
                item.g[i] = new int[]{u, v, rw, rh};
                MeshDefinition mesh = meshes.computeIfAbsent(b.a(), k -> new MeshDefinition());
                PartDefinition root = mesh.getRoot();
                root.addOrReplaceChild("box" + i, CubeListBuilder.create().texOffs(u, v).addBox(b.b(), b.c(), b.d(), b.e(), b.f(), b.g()), PartPose.ZERO);
                u += rw;
                rowHeight = Math.max(rowHeight, rh);
            }
            for (Map.Entry e : meshes.entrySet()) {
                item.f.put((C70)((Object)e.getKey()), LayerDefinition.create((MeshDefinition)((MeshDefinition)e.getValue()), (int)128, (int)128).bakeRoot());
            }
        }
    }

    static Identifier b(C72 item, int variant) {
        String key = item.a + "_" + variant;
        Identifier location = Identifier.fromNamespaceAndPath((String)"lethalauction", (String)("cosmetics/" + key));
        if (!g.containsKey(key)) {
            NativeImage img = new NativeImage(128, 128, true);
            C73 p = item.d.get(variant);
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
                        img.setPixel(x, y, 0xFF000000 | K6.a(base, grain));
                    }
                }
            }
            DynamicTexture texture = new DynamicTexture(() -> "Lethal Client cosmetic " + key, img);
            Minecraft.getInstance().getTextureManager().register(location, (AbstractTexture)texture);
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

    static enum C70 {
        HEAD,
        RIGHT_LEG,
        LEFT_LEG;

    }

    static final class C71 {
        private final C70 a;
        private final float b;
        private final float c;
        private final float d;
        private final float e;
        private final float f;
        private final float g;
        private final int h;
        C71(C70 a, float b, float c, float d, float e, float f, float g, int h) { this.a = a; this.b = b; this.c = c; this.d = d; this.e = e; this.f = f; this.g = g; this.h = h; }
        public C70 a() { return this.a; }
        public float b() { return this.b; }
        public float c() { return this.c; }
        public float d() { return this.d; }
        public float e() { return this.e; }
        public float f() { return this.f; }
        public float g() { return this.g; }
        public int h() { return this.h; }


    }

    public static final class C72 {
        public final String a;
        public final String b;
        public final C74 c;
        public final List<C73> d;
        final List<C71> e;
        final Map<C70, ModelPart> f = new EnumMap<C70, ModelPart>(C70.class);
        final int[][] g;

        C72(String id, String name, C74 slot, List<C73> palettes, List<C71> boxes) {
            this.a = id;
            this.b = name;
            this.c = slot;
            this.d = palettes;
            this.e = boxes;
            this.g = new int[boxes.size()][4];
        }
    }

    public static enum C74 {
        HAT("Hats"),
        SHOES("Shoes"),
        AUDIO("Audio");

        public final String title;

        private C74(String title) {
            this.title = title;
        }
    }

    public static final class C75 {
        private final C72 a;
        private final int b;
        C75(C72 a, int b) { this.a = a; this.b = b; }
        public C72 a() { return this.a; }
        public int b() { return this.b; }


    }

    public static final class C73 {
        private final String a;
        private final int b;
        private final int c;
        private final int d;
        C73(String a, int b, int c, int d) { this.a = a; this.b = b; this.c = c; this.d = d; }
        public String a() { return this.a; }
        public int b() { return this.b; }
        public int c() { return this.c; }
        public int d() { return this.d; }


    }
}

