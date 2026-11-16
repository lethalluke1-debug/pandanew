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

    public static final String[] NAMES = new String[]{"Christmas Tree", "String Lights", "Bed", "Gift", "Candy Cane", "Snowman"};
    public static int sel = 0;
    public static int rot = 0;
    public static boolean editing;

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
                placed.remove(i);
                K62.save();
                return;
            }
        }
        C101 p = new C101();
        p.t = sel;
        p.x = bp.method_10263();
        p.y = bp.method_10264();
        p.z = bp.method_10260();
        p.r = rot;
        placed.add(p);
        K62.save();
    }

    public static void render(WorldRenderContext ctx) {
        try {
            K62.load();
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 == null || !K37.e()) {
                return;
            }
            if (placed.isEmpty() && !editing) {
                return;
            }
            class_243 cam = mc.field_1773.method_19418().method_71156();
            class_4588 vc = buf.method_73477(class_12249.method_75990((net.minecraft.class_2960)K10.a));
            Matrix4f base = new Matrix4f((Matrix4fc)ctx.matrices().method_23760().method_23761());
            for (C101 p : placed) {
                K62.draw(vc, base, cam, p.x, p.y, p.z, p.r, p.t);
            }
            if (editing) {
                class_2338 bp = K62.target(mc);
                if (bp != null) {
                    K62.draw(vc, base, cam, bp.method_10263(), bp.method_10264(), bp.method_10260(), rot, sel);
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

    private static void draw(class_4588 vc, Matrix4fc base, class_243 cam, int bx, int by, int bz, int r, int type) {
        Matrix4f m = new Matrix4f(base);
        m.translate((float)((double)bx + 0.5 - cam.field_1352), (float)((double)by - cam.field_1351), (float)((double)bz + 0.5 - cam.field_1350));
        if (r != 0) {
            m.rotateY((float)Math.toRadians(r * 90));
        }
        switch (type) {
            case 0: K62.tree(vc, m); break;
            case 1: K62.lights(vc, m); break;
            case 2: K62.bed(vc, m); break;
            case 3: K62.gift(vc, m); break;
            case 4: K62.candyCane(vc, m); break;
            case 5: K62.snowman(vc, m); break;
            default: K62.tree(vc, m);
        }
    }

    private static final int GREEN = 0x1E7A2E;
    private static final int BROWN = 0x5A3A1A;
    private static final int WHITE = 0xF2F2F2;
    private static final int RED = 0xC8323A;
    private static final int GOLD = 0xFFD24A;
    private static final int BLUE = 0x3A7BD5;

    private static void tree(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.08f, 0.0f, -0.08f, 0.08f, 0.22f, 0.08f, BROWN);
        K62.box(vc, m, -0.36f, 0.20f, -0.36f, 0.36f, 0.50f, 0.36f, GREEN);
        K62.box(vc, m, -0.28f, 0.48f, -0.28f, 0.28f, 0.74f, 0.28f, GREEN);
        K62.box(vc, m, -0.18f, 0.72f, -0.18f, 0.18f, 0.96f, 0.18f, GREEN);
        K62.box(vc, m, -0.07f, 0.95f, -0.07f, 0.07f, 1.09f, 0.07f, GOLD);
        K62.box(vc, m, 0.24f, 0.34f, 0.0f, 0.32f, 0.42f, 0.08f, RED);
        K62.box(vc, m, -0.30f, 0.30f, 0.14f, -0.22f, 0.38f, 0.22f, BLUE);
        K62.box(vc, m, 0.0f, 0.58f, 0.20f, 0.08f, 0.66f, 0.28f, GOLD);
        K62.box(vc, m, -0.14f, 0.80f, -0.10f, -0.06f, 0.88f, -0.02f, RED);
    }

    private static void lights(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.42f, 0.0f, 0.0f, -0.38f, 0.5f, 0.04f, WHITE);
        K62.box(vc, m, 0.38f, 0.0f, 0.0f, 0.42f, 0.5f, 0.04f, WHITE);
        int[] cols = new int[]{RED, GOLD, GREEN, BLUE, RED, GOLD};
        for (int i = 0; i < 6; ++i) {
            float t = (float)i / 5.0f;
            float x = -0.40f + t * 0.80f;
            float droop = (float)Math.sin(t * Math.PI) * 0.10f;
            float y = 0.46f - droop;
            int c = cols[i];
            K62.box(vc, m, x - 0.03f, y - 0.05f, -0.01f, x + 0.03f, y + 0.01f, 0.05f, c);
        }
    }

    private static void bed(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.40f, 0.0f, -0.46f, 0.40f, 0.12f, 1.40f, BROWN);
        K62.box(vc, m, -0.38f, 0.12f, -0.44f, 0.38f, 0.30f, 1.38f, RED);
        K62.box(vc, m, -0.34f, 0.30f, -0.40f, 0.34f, 0.42f, 0.00f, WHITE);
    }

    private static void gift(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.30f, 0.0f, -0.30f, 0.30f, 0.42f, 0.30f, RED);
        K62.box(vc, m, -0.05f, 0.0f, -0.31f, 0.05f, 0.44f, 0.31f, GOLD);
        K62.box(vc, m, -0.31f, 0.0f, -0.05f, 0.31f, 0.44f, 0.05f, GOLD);
        K62.box(vc, m, -0.10f, 0.42f, -0.10f, 0.10f, 0.56f, 0.10f, GOLD);
    }

    private static void candyCane(class_4588 vc, Matrix4f m) {
        for (int i = 0; i < 6; ++i) {
            float y = i * 0.14f;
            int c = i % 2 == 0 ? RED : WHITE;
            K62.box(vc, m, -0.06f, y, -0.06f, 0.06f, y + 0.14f, 0.06f, c);
        }
        K62.box(vc, m, -0.06f, 0.84f, -0.06f, 0.18f, 0.96f, 0.06f, RED);
        K62.box(vc, m, 0.12f, 0.72f, -0.06f, 0.24f, 0.90f, 0.06f, WHITE);
    }

    private static void snowman(class_4588 vc, Matrix4f m) {
        K62.box(vc, m, -0.26f, 0.0f, -0.26f, 0.26f, 0.44f, 0.26f, WHITE);
        K62.box(vc, m, -0.19f, 0.42f, -0.19f, 0.19f, 0.78f, 0.19f, WHITE);
        K62.box(vc, m, -0.12f, 0.76f, -0.12f, 0.12f, 1.00f, 0.12f, WHITE);
        K62.box(vc, m, -0.15f, 0.98f, -0.15f, 0.15f, 1.02f, 0.15f, 0x202020);
        K62.box(vc, m, -0.10f, 1.02f, -0.10f, 0.10f, 1.18f, 0.10f, 0x202020);
        K62.box(vc, m, -0.05f, 0.86f, -0.14f, 0.05f, 0.92f, -0.11f, GOLD);
        K62.box(vc, m, -0.06f, 0.88f, -0.13f, -0.03f, 0.91f, -0.11f, 0x202020);
        K62.box(vc, m, 0.03f, 0.88f, -0.13f, 0.06f, 0.91f, -0.11f, 0x202020);
    }

    private static final int[][] FACE = new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {1, 5, 7, 3}, {0, 2, 6, 4}};
    private static final float[][] NORM = new float[][]{{-1.0f, 0.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};

    private static void box(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        float[][] c = new float[][]{{x0, y0, z0}, {x0, y0, z1}, {x0, y1, z0}, {x0, y1, z1}, {x1, y0, z0}, {x1, y0, z1}, {x1, y1, z0}, {x1, y1, z1}};
        Vector3f p = new Vector3f();
        Vector3f n = new Vector3f();
        for (int f = 0; f < 6; ++f) {
            int[] q = FACE[f];
            m.transformDirection(NORM[f][0], NORM[f][1], NORM[f][2], n);
            n.normalize();
            int[][] tris = new int[][]{{q[0], q[1], q[2]}, {q[0], q[2], q[3]}, {q[0], q[2], q[1]}, {q[0], q[3], q[2]}};
            for (int[] tri : tris) {
                for (int k : tri) {
                    m.transformPosition(c[k][0], c[k][1], c[k][2], p);
                    vc.method_22912(p.x, p.y, p.z).method_1336(r, g, b, 255).method_22913(0.5f, 0.5f).method_60796(0, 10).method_22921(240, 240).method_22914(n.x, n.y, n.z);
                }
            }
        }
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

        public C101() {
        }
    }
}
