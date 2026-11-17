package com.lethalauction.m;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lethalauction.LethalAuctionClient;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_12249;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

/**
 * Room decorations. Client-side holograms the player places in the world (only
 * they see them). Each decoration is a small set of flat-coloured boxes drawn
 * through the same white texture / buffer the car uses. Placements are saved to
 * a config file so they survive restarts.
 */
public final class K62 {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lethalauction-room.json");
    private static final Gson GSON = new GsonBuilder().create();
    private static final K51 buf = new K51();
    private static final List<C101> placed = new ArrayList<C101>();
    private static boolean loaded;

    // Only draw decorations within this many blocks of the camera.
    private static final double RENDER_DIST = 96.0;

    // Shown in the Room tab.
    public static final String[] NAMES = new String[]{"Christmas Tree", "Snow", "Gift"};
    public static int sel = 0;
    public static int rot = 0;
    public static float scl = 1.0f;
    public static boolean editing;

    public static void scaleUp() {
        scl = Math.min(3.0f, Math.round((scl + 0.25f) * 100.0f) / 100.0f);
    }

    public static void scaleDown() {
        scl = Math.max(0.25f, Math.round((scl - 0.25f) * 100.0f) / 100.0f);
    }

    private K62() {
    }

    public static int count() {
        K62.load();
        return placed.size();
    }

    public static void select(int i) {
        sel = i < 0 ? 0 : (i >= NAMES.length ? NAMES.length - 1 : i);
    }

    public static void rotate() {
        rot = rot + 1 & 3;
    }

    public static void toggleEditing() {
        editing = !editing;
    }

    public static void clearAll() {
        K62.load();
        placed.clear();
        K62.save();
    }

    public static void removeLast() {
        K62.load();
        if (!placed.isEmpty()) {
            placed.remove(placed.size() - 1);
            K62.save();
        }
    }

    // Move every placed decoration so the whole arrangement is centred on the
    // player's current position. Useful on servers (like DonutSMP) that spawn
    // you far from where you placed them after a relog.
    public static synchronized void bringHere(class_310 mc) {
        if (mc.field_1724 == null) {
            return;
        }
        K62.load();
        if (placed.isEmpty()) {
            return;
        }
        class_2338 target = mc.field_1724.method_24515();
        C101 anchor = placed.get(0);
        int ddx = target.method_10263() - anchor.x;
        int ddy = target.method_10264() - anchor.y;
        int ddz = target.method_10260() - anchor.z;
        for (C101 p : placed) {
            p.x += ddx;
            p.y += ddy;
            p.z += ddz;
        }
        K62.save();
    }

    private static class_2338 target(class_310 mc) {
        class_239 hr = mc.field_1765;
        if (hr instanceof class_3965) {
            class_3965 bhr = (class_3965)hr;
            if (bhr.method_17783() != class_239.class_240.field_1333) {
                return bhr.method_17777().method_10093(bhr.method_17780());
            }
        }
        return null;
    }

    // Place the selected decoration. Never deletes: if a decoration is already
    // in that cell this is a no-op. Removal is done with the Remove last / Clear
    // all buttons.
    public static void place(class_310 mc) {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        K62.load();
        class_2338 bp = K62.target(mc);
        if (bp == null) {
            return;
        }
        for (int i = 0; i < placed.size(); ++i) {
            C101 p = placed.get(i);
            if (p.x == bp.method_10263() && p.y == bp.method_10264() && p.z == bp.method_10260()) {
                return;
            }
        }
        C101 p = new C101();
        p.t = sel;
        p.x = bp.method_10263();
        p.y = bp.method_10264();
        p.z = bp.method_10260();
        p.r = rot;
        p.s = scl;
        // Snow gets a random shallow depth each time it is placed.
        p.h = sel == 1 ? 0.10f + 0.05f * (float)(int)(Math.random() * 5.0) : 0.0f;
        placed.add(p);
        K62.save();
    }

    public static void render(WorldRenderContext ctx) {
        try {
            K62.load();
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 == null) {
                return;
            }
            if (placed.isEmpty() && !editing) {
                return;
            }
            class_243 cam = mc.field_1773.method_19418().method_71156();
            double maxSq = RENDER_DIST * RENDER_DIST;
            class_4588 vc = buf.method_73477(class_12249.method_75990((net.minecraft.class_2960)K10.a));
            Matrix4f base = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
            for (C101 p : placed) {
                double dx = (double)p.x + 0.5 - cam.field_1352;
                double dy = (double)p.y + 0.5 - cam.field_1351;
                double dz = (double)p.z + 0.5 - cam.field_1350;
                if (dx * dx + dy * dy + dz * dz > maxSq) {
                    continue;
                }
                float sc = p.t == 1 ? 1.0f : (p.s <= 0.0f ? 1.0f : p.s);
                K62.draw(vc, base, cam, p.x, p.y, p.z, p.r, sc, p.t, p.h);
            }
            if (editing) {
                class_2338 bp = K62.target(mc);
                if (bp != null) {
                    K62.draw(vc, base, cam, bp.method_10263(), bp.method_10264(), bp.method_10260(), rot, sel == 1 ? 1.0f : scl, sel, sel == 1 ? 0.2f : 0.0f);
                }
            }
        }
        catch (Throwable t) {
            // never break world rendering
        }
        finally {
            try {
                buf.a(ctx.consumers());
            }
            catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void draw(class_4588 vc, Matrix4fc base, class_243 cam, int bx, int by, int bz, int r, float s, int type, float h) {
        Matrix4f m = new Matrix4f(base);
        m.translate((float)((double)bx + 0.5 - cam.field_1352), (float)((double)by - cam.field_1351), (float)((double)bz + 0.5 - cam.field_1350));
        if (r != 0) {
            m.rotateY((float)Math.toRadians(r * 90));
        }
        if (s != 1.0f) {
            m.scale(s);
        }
        K62.drawGeom(vc, m, type, h);
    }

    private static void drawGeom(class_4588 vc, Matrix4f m, int type, float h) {
        switch (type) {
            case 0: K62.tree(vc, m); break;
            case 1: K62.snow(vc, m, h); break;
            case 2: K62.gift(vc, m); break;
            default: K62.tree(vc, m);
        }
    }

    // The raw boxes of a decoration, captured for the GUI preview.
    private static java.util.List<float[]> capture;

    public static synchronized java.util.List<float[]> model(int type) {
        java.util.ArrayList<float[]> list = new java.util.ArrayList<float[]>();
        capture = list;
        try {
            K62.drawGeom(null, null, type, 0.5f);
        }
        catch (Throwable t) {
            // ignore
        }
        finally {
            capture = null;
        }
        return list;
    }

    private static final int GREEN = 0x1E7A2E;
    private static final int DGREEN = 0x155C22;
    private static final int BROWN = 0x5A3A1A;
    private static final int WHITE = 0xF2F2F2;
    private static final int RED = 0xC8323A;
    private static final int GOLD = 0xFFD24A;
    private static final int BLUE = 0x3A7BD5;
    private static final int POT = 0xC8A06E;
    private static final int POT2 = 0x9C7A4E;
    private static final int DIRT = 0x4A3524;

    private static void tree(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.19f, 0.0f, -0.19f, 0.19f, 0.06f, 0.19f, POT2);
        K62.box(vc, m, -0.22f, 0.05f, -0.22f, 0.22f, 0.20f, 0.22f, POT);
        K62.box(vc, m, -0.225f, 0.10f, -0.03f, 0.225f, 0.14f, 0.03f, POT2);
        K62.box(vc, m, -0.20f, 0.19f, -0.20f, 0.20f, 0.25f, 0.20f, POT2);
        K62.box(vc, m, -0.17f, 0.23f, -0.17f, 0.17f, 0.27f, 0.17f, DIRT);
        K62.box(vc, m, -0.06f, 0.25f, -0.06f, 0.06f, 0.42f, 0.06f, BROWN);
        K62.box(vc, m, -0.44f, 0.36f, -0.44f, 0.44f, 0.60f, 0.44f, GREEN);
        K62.box(vc, m, -0.37f, 0.56f, -0.37f, 0.37f, 0.78f, 0.37f, GREEN);
        K62.box(vc, m, -0.29f, 0.74f, -0.29f, 0.29f, 0.96f, 0.29f, GREEN);
        K62.box(vc, m, -0.21f, 0.92f, -0.21f, 0.21f, 1.14f, 0.21f, GREEN);
        K62.box(vc, m, -0.12f, 1.10f, -0.12f, 0.12f, 1.32f, 0.12f, GREEN);
        K62.box(vc, m, -0.03f, 1.28f, -0.13f, 0.03f, 1.50f, 0.13f, GOLD);
        K62.box(vc, m, -0.13f, 1.28f, -0.03f, 0.13f, 1.50f, 0.03f, GOLD);
        K62.box(vc, m, -0.07f, 1.31f, -0.07f, 0.07f, 1.47f, 0.07f, GOLD);
        K62.box(vc, m, 0.46f, 0.42f, -0.06f, 0.56f, 0.54f, 0.06f, RED);
        K62.box(vc, m, -0.06f, 0.44f, -0.56f, 0.06f, 0.56f, -0.46f, BLUE);
        K62.box(vc, m, -0.06f, 0.60f, 0.39f, 0.06f, 0.72f, 0.49f, GOLD);
        K62.box(vc, m, -0.49f, 0.64f, 0.10f, -0.39f, 0.76f, 0.22f, RED);
        K62.box(vc, m, 0.31f, 0.80f, -0.06f, 0.41f, 0.92f, 0.06f, BLUE);
        K62.box(vc, m, 0.10f, 0.84f, -0.41f, 0.22f, 0.96f, -0.31f, GOLD);
        K62.glow(vc, m, 0.14f, 0.50f, 0.46f, 0.22f, 0.56f, 0.54f, K62.twinkle(0));
        K62.glow(vc, m, -0.54f, 0.46f, -0.20f, -0.46f, 0.52f, -0.14f, K62.twinkle(1));
        K62.glow(vc, m, 0.46f, 0.54f, 0.14f, 0.54f, 0.60f, 0.20f, K62.twinkle(2));
        K62.glow(vc, m, 0.39f, 0.62f, -0.20f, 0.47f, 0.68f, -0.14f, K62.twinkle(3));
        K62.glow(vc, m, -0.18f, 0.68f, -0.47f, -0.12f, 0.74f, -0.39f, K62.twinkle(4));
        K62.glow(vc, m, 0.14f, 0.72f, 0.39f, 0.20f, 0.78f, 0.47f, K62.twinkle(5));
        K62.glow(vc, m, -0.41f, 0.80f, 0.06f, -0.31f, 0.86f, 0.12f, K62.twinkle(6));
        K62.glow(vc, m, 0.31f, 0.86f, -0.14f, 0.39f, 0.92f, -0.08f, K62.twinkle(7));
        K62.glow(vc, m, -0.04f, 0.98f, 0.23f, 0.04f, 1.04f, 0.31f, K62.twinkle(8));
    }

    private static int twinkle(int i) {
        int[] cols = new int[]{0xFF4D4D, 0xFFE24A, 0x58E058, 0x5AA0FF, 0xFFFFFF, 0xFF7ACF, 0xFFA030};
        long t = System.currentTimeMillis() / 350L + (long)(i * 3);
        return cols[(int)(t % (long)cols.length)];
    }

    // Wrapped present: red box with gold ribbons proud of each face + a bow.
    private static void gift(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.30f, 0.0f, -0.30f, 0.30f, 0.42f, 0.30f, RED);
        K62.box(vc, m, -0.06f, -0.01f, -0.34f, 0.06f, 0.43f, 0.34f, GOLD);
        K62.box(vc, m, -0.34f, -0.01f, -0.06f, 0.34f, 0.43f, 0.06f, GOLD);
        K62.box(vc, m, -0.12f, 0.40f, -0.12f, 0.12f, 0.58f, 0.12f, GOLD);
    }

    private static final int SNOW = 0xF0F4FA;
    private static final int SNOW2 = 0xDDE6F2;

    // A snow layer covering the block footprint, of the given height. Holograms
    // have no collision, so you can always walk through it.
    private static void snow(class_4588 vc, Matrix4f m, float h) {
        if (h <= 0.0f) {
            h = 0.2f;
        }
        K62.box(vc, m, -0.49f, 0.0f, -0.49f, 0.49f, h, 0.49f, SNOW);
        K62.box(vc, m, -0.50f, 0.0f, -0.50f, 0.50f, Math.min(0.03f, h), 0.50f, SNOW2);
    }

    // double-sided flat-colour box: quads wound both ways, culling keeps the
    // camera-facing one (stops overlapping boxes z-fighting + avoids tri seams).
    private static final int[][] FACE = new int[][]{{1, 5, 6, 2}, {0, 3, 7, 4}, {4, 7, 6, 5}, {0, 1, 2, 3}, {3, 2, 6, 7}, {0, 4, 5, 1}};
    private static final float[][] NORM = new float[][]{{1.0f, 0.0f, 0.0f}, {-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};

    // Decorations are drawn full-bright on 1.21.11 (no world-light sampling).
    private static void glow(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        K62.box(vc, m, x0, y0, z0, x1, y1, z1, rgb);
    }

    private static void box(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        if (capture != null) {
            capture.add(new float[]{x0, y0, z0, x1, y1, z1, Float.intBitsToFloat(rgb)});
            return;
        }
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        float[][] c = new float[][]{{x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}, {x0, y0, z1}, {x0, y1, z0}, {x1, y1, z0}, {x1, y1, z1}, {x0, y1, z1}};
        Vector3f p = new Vector3f();
        Vector3f n = new Vector3f();
        for (int f = 0; f < 6; ++f) {
            int[] q = FACE[f];
            m.transformDirection(NORM[f][0], NORM[f][1], NORM[f][2], n);
            n.normalize();
            int[][] quads = new int[][]{{q[0], q[1], q[2], q[3]}, {q[3], q[2], q[1], q[0]}};
            for (int[] quad : quads) {
                for (int k : quad) {
                    m.transformPosition(c[k][0], c[k][1], c[k][2], p);
                    vc.method_22912(p.x, p.y, p.z).method_1336(r, g, b, 255).method_22913(0.5f, 0.5f).method_60796(0, 10).method_22921(240, 240).method_22914(n.x, n.y, n.z);
                }
            }
        }
    }

    // ===== persistence =====

    public static synchronized void reload() {
        loaded = false;
        placed.clear();
        K62.load();
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            if (Files.exists(FILE)) {
                try (Reader rd = Files.newBufferedReader(FILE);){
                    List<C101> saved = GSON.fromJson(rd, new TypeToken<List<C101>>(){}.getType());
                    if (saved != null) {
                        for (C101 p : saved) {
                            if (p != null) {
                                placed.add(p);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not read room decorations", (Throwable)e);
        }
    }

    private static synchronized void save() {
        try {
            if (FILE.getParent() != null) {
                Files.createDirectories(FILE.getParent());
            }
            try (Writer w = Files.newBufferedWriter(FILE);){
                GSON.toJson(placed, w);
            }
        }
        catch (Exception e) {
            LethalAuctionClient.LOGGER.warn("Could not save room decorations", (Throwable)e);
        }
    }

    public static final class C101 {
        public int t;
        public int x;
        public int y;
        public int z;
        public int r;
        public float s = 1.0f;
        public float h = 0.0f;

        public C101() {
        }
    }
}
