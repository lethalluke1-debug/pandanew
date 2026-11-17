package com.lethalauction.m;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lethalauction.LethalAuctionClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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

    // Shown in the Room tab. Keep the first three as the ones the user asked for.
    public static final String[] NAMES = new String[]{"Christmas Tree", "String Lights", "Bed", "Gift", "Candy Cane", "Snowman"};
    public static int sel = 0;      // which decoration the picker has selected
    public static int rot = 0;      // placement rotation, 0..3 (x90 degrees)
    public static float scl = 1.0f; // placement scale
    public static boolean editing;  // placing mode: show a ghost at the looked-at block

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

    // The block cell the crosshair would place into (the air next to the face
    // you are looking at), or null if you are not looking at a block.
    private static BlockPos target(Minecraft mc) {
        HitResult hr = mc.hitResult;
        if (hr instanceof BlockHitResult bhr && bhr.getType() != HitResult.Type.MISS) {
            return bhr.getBlockPos().relative(bhr.getDirection());
        }
        return null;
    }

    // Place the selected decoration, or remove one already in that cell (toggle).
    public static void place(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            return;
        }
        K62.load();
        BlockPos bp = K62.target(mc);
        if (bp == null) {
            return;
        }
        for (int i = 0; i < placed.size(); ++i) {
            C101 p = placed.get(i);
            if (p.x == bp.getX() && p.y == bp.getY() && p.z == bp.getZ()) {
                placed.remove(i);
                K62.save();
                return;
            }
        }
        C101 p = new C101();
        p.t = sel;
        p.x = bp.getX();
        p.y = bp.getY();
        p.z = bp.getZ();
        p.r = rot;
        p.s = scl;
        placed.add(p);
        K62.save();
    }

    public static void render(LevelRenderContext ctx) {
        try {
            K62.load();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || !K37.e()) {
                return;
            }
            if (placed.isEmpty() && !editing) {
                return;
            }
            Vec3 cam = mc.gameRenderer.mainCamera().position();
            VertexConsumer vc = buf.a(RenderTypes.entityCutoutCull(K10.a));
            Matrix4f base = ctx.poseStack().last().pose();
            for (C101 p : placed) {
                K62.draw(vc, base, cam, p.x, p.y, p.z, p.r, p.s <= 0.0f ? 1.0f : p.s, p.t);
            }
            if (editing) {
                BlockPos bp = K62.target(mc);
                if (bp != null) {
                    K62.draw(vc, base, cam, bp.getX(), bp.getY(), bp.getZ(), rot, scl, sel);
                }
            }
        }
        catch (Throwable t) {
            // never break world rendering
        }
        finally {
            try {
                buf.a(ctx.submitNodeCollector());
            }
            catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void draw(VertexConsumer vc, Matrix4fc base, Vec3 cam, int bx, int by, int bz, int r, float s, int type) {
        Matrix4f m = new Matrix4f(base);
        m.translate((float)((double)bx + 0.5 - cam.x), (float)((double)by - cam.y), (float)((double)bz + 0.5 - cam.z));
        if (r != 0) {
            m.rotateY((float)Math.toRadians(r * 90));
        }
        if (s != 1.0f) {
            m.scale(s);
        }
        K62.drawGeom(vc, m, type);
    }

    private static void drawGeom(VertexConsumer vc, Matrix4f m, int type) {
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

    // The raw boxes of a decoration as {x0,y0,z0,x1,y1,z1, rgb-as-int-bits} rows,
    // captured by running its geometry with the renderer swapped out. Used by the
    // GUI preview (which projects these boxes itself).
    private static java.util.List<float[]> capture;

    public static synchronized java.util.List<float[]> model(int type) {
        java.util.ArrayList<float[]> list = new java.util.ArrayList<float[]>();
        capture = list;
        try {
            K62.drawGeom(null, null, type);
        }
        catch (Throwable t) {
            // ignore
        }
        finally {
            capture = null;
        }
        return list;
    }

    // ===== decoration geometry (origin at the centre of the block's bottom; up is +y) =====

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

    // Decoration parts either overlap deeply (so no thin near-coincident faces
    // z-fight) or stand clearly proud of the surface. Nothing is buried flush.
    private static void tree(VertexConsumer vc, Matrix4f m) {
        // potted barrel base
        K62.box(vc, m, -0.19f, 0.0f, -0.19f, 0.19f, 0.06f, 0.19f, POT2);
        K62.box(vc, m, -0.22f, 0.05f, -0.22f, 0.22f, 0.20f, 0.22f, POT);
        K62.box(vc, m, -0.225f, 0.10f, -0.03f, 0.225f, 0.14f, 0.03f, POT2);
        K62.box(vc, m, -0.20f, 0.19f, -0.20f, 0.20f, 0.25f, 0.20f, POT2);
        K62.box(vc, m, -0.17f, 0.23f, -0.17f, 0.17f, 0.27f, 0.17f, DIRT);
        // trunk
        K62.box(vc, m, -0.06f, 0.25f, -0.06f, 0.06f, 0.42f, 0.06f, BROWN);
        // five foliage tiers, gentle taper, deep overlaps
        K62.box(vc, m, -0.44f, 0.36f, -0.44f, 0.44f, 0.60f, 0.44f, GREEN);
        K62.box(vc, m, -0.37f, 0.56f, -0.37f, 0.37f, 0.78f, 0.37f, GREEN);
        K62.box(vc, m, -0.29f, 0.74f, -0.29f, 0.29f, 0.96f, 0.29f, GREEN);
        K62.box(vc, m, -0.21f, 0.92f, -0.21f, 0.21f, 1.14f, 0.21f, GREEN);
        K62.box(vc, m, -0.12f, 1.10f, -0.12f, 0.12f, 1.32f, 0.12f, GREEN);
        // star topper: crossed slabs + core
        K62.box(vc, m, -0.03f, 1.28f, -0.13f, 0.03f, 1.50f, 0.13f, GOLD);
        K62.box(vc, m, -0.13f, 1.28f, -0.03f, 0.13f, 1.50f, 0.03f, GOLD);
        K62.box(vc, m, -0.07f, 1.31f, -0.07f, 0.07f, 1.47f, 0.07f, GOLD);
        // ornaments: each straddles one tier face by ~0.06 (pokes out, anchored
        // inside) so no face is near-coplanar with the branch -> no z-fighting.
        K62.box(vc, m, 0.38f, 0.42f, -0.06f, 0.50f, 0.54f, 0.06f, RED);
        K62.box(vc, m, -0.06f, 0.44f, -0.50f, 0.06f, 0.56f, -0.38f, BLUE);
        K62.box(vc, m, -0.06f, 0.60f, 0.31f, 0.06f, 0.72f, 0.43f, GOLD);
        K62.box(vc, m, -0.43f, 0.64f, 0.10f, -0.31f, 0.76f, 0.22f, RED);
        K62.box(vc, m, 0.23f, 0.80f, -0.06f, 0.35f, 0.92f, 0.06f, BLUE);
        K62.box(vc, m, 0.10f, 0.84f, -0.35f, 0.22f, 0.96f, -0.23f, GOLD);
        // flashing string lights (twinkle over time), same straddle rule
        K62.box(vc, m, 0.14f, 0.50f, 0.38f, 0.22f, 0.56f, 0.50f, K62.twinkle(0));
        K62.box(vc, m, -0.50f, 0.46f, -0.20f, -0.38f, 0.52f, -0.14f, K62.twinkle(1));
        K62.box(vc, m, 0.38f, 0.54f, 0.14f, 0.50f, 0.60f, 0.20f, K62.twinkle(2));
        K62.box(vc, m, 0.31f, 0.62f, -0.20f, 0.43f, 0.68f, -0.14f, K62.twinkle(3));
        K62.box(vc, m, -0.18f, 0.68f, -0.43f, -0.12f, 0.74f, -0.31f, K62.twinkle(4));
        K62.box(vc, m, 0.14f, 0.72f, 0.31f, 0.20f, 0.78f, 0.43f, K62.twinkle(5));
        K62.box(vc, m, -0.35f, 0.80f, 0.06f, -0.23f, 0.86f, 0.12f, K62.twinkle(6));
        K62.box(vc, m, 0.23f, 0.86f, -0.14f, 0.35f, 0.92f, -0.08f, K62.twinkle(7));
        K62.box(vc, m, -0.04f, 0.98f, 0.15f, 0.04f, 1.04f, 0.27f, K62.twinkle(8));
    }

    // Twinkling Christmas-light colour: cycles bright hues with a per-bulb phase.
    private static int twinkle(int i) {
        int[] cols = new int[]{0xFF4D4D, 0xFFE24A, 0x58E058, 0x5AA0FF, 0xFFFFFF, 0xFF7ACF, 0xFFA030};
        long t = System.currentTimeMillis() / 350L + (long)(i * 3);
        return cols[(int)(t % (long)cols.length)];
    }

    private static void lights(VertexConsumer vc, Matrix4f m) {
        // a string of bulbs on a bar, bulbs standing clearly proud of the wire
        K62.box(vc, m, -0.44f, 0.44f, -0.03f, 0.44f, 0.50f, 0.03f, 0x4A4A4A);
        K62.box(vc, m, -0.44f, 0.0f, -0.02f, -0.40f, 0.50f, 0.02f, WHITE);
        K62.box(vc, m, 0.40f, 0.0f, -0.02f, 0.44f, 0.50f, 0.02f, WHITE);
        int[] cols = new int[]{RED, GOLD, GREEN, BLUE, RED, GOLD, BLUE};
        for (int i = 0; i < 7; ++i) {
            float x = -0.36f + (float)i / 6.0f * 0.72f;
            K62.box(vc, m, x - 0.04f, 0.30f, 0.03f, x + 0.04f, 0.42f, 0.11f, cols[i]);
        }
    }

    private static void bed(VertexConsumer vc, Matrix4f m) {
        // spans two blocks in +z. Mattress sits on a frame; pillow stands proud.
        K62.box(vc, m, -0.42f, 0.0f, -0.46f, 0.42f, 0.14f, 1.42f, BROWN);
        K62.box(vc, m, -0.40f, 0.14f, -0.44f, 0.40f, 0.34f, 1.40f, RED);
        K62.box(vc, m, -0.34f, 0.34f, -0.40f, 0.34f, 0.50f, -0.06f, WHITE);
    }

    private static void gift(VertexConsumer vc, Matrix4f m) {
        K62.box(vc, m, -0.30f, 0.0f, -0.30f, 0.30f, 0.42f, 0.30f, RED);
        // ribbons stand 0.04 proud of each face so they never z-fight the box
        K62.box(vc, m, -0.06f, -0.01f, -0.34f, 0.06f, 0.43f, 0.34f, GOLD);
        K62.box(vc, m, -0.34f, -0.01f, -0.06f, 0.34f, 0.43f, 0.06f, GOLD);
        // bow overlaps down into the ribbon so no faces coincide
        K62.box(vc, m, -0.12f, 0.40f, -0.12f, 0.12f, 0.58f, 0.12f, GOLD);
    }

    private static void candyCane(VertexConsumer vc, Matrix4f m) {
        for (int i = 0; i < 7; ++i) {
            float y = i * 0.13f;
            int col = i % 2 == 0 ? RED : WHITE;
            K62.box(vc, m, -0.07f, y, -0.07f, 0.07f, y + 0.13f, 0.07f, col);
        }
        // hook: two segments deeply overlapping the top
        K62.box(vc, m, -0.07f, 0.84f, -0.07f, 0.24f, 0.98f, 0.07f, WHITE);
        K62.box(vc, m, 0.10f, 0.66f, -0.07f, 0.24f, 0.92f, 0.07f, RED);
    }

    private static void snowman(VertexConsumer vc, Matrix4f m) {
        K62.box(vc, m, -0.26f, 0.0f, -0.26f, 0.26f, 0.46f, 0.26f, WHITE);
        K62.box(vc, m, -0.19f, 0.40f, -0.19f, 0.19f, 0.80f, 0.19f, WHITE);
        K62.box(vc, m, -0.12f, 0.74f, -0.12f, 0.12f, 1.02f, 0.12f, WHITE);
        // hat sits proud on the head
        K62.box(vc, m, -0.16f, 1.00f, -0.16f, 0.16f, 1.05f, 0.16f, 0x202020);
        K62.box(vc, m, -0.10f, 1.05f, -0.10f, 0.10f, 1.22f, 0.10f, 0x202020);
        // nose + eyes stand proud of the face (-Z)
        K62.box(vc, m, -0.03f, 0.86f, -0.20f, 0.03f, 0.92f, -0.12f, GOLD);
        K62.box(vc, m, -0.08f, 0.90f, -0.16f, -0.04f, 0.94f, -0.12f, 0x202020);
        K62.box(vc, m, 0.04f, 0.90f, -0.16f, 0.08f, 0.94f, -0.12f, 0x202020);
    }

    // double-sided flat-colour box
    // Corners: 0..3 bottom (y0), 4..7 top (y1). Each face is wound CCW as seen
    // from outside, so with back-face culling only the outer shell draws (which
    // stops overlapping boxes z-fighting at their junctions).
    private static final int[][] FACE = new int[][]{{1, 5, 6, 2}, {0, 3, 7, 4}, {4, 7, 6, 5}, {0, 1, 2, 3}, {3, 2, 6, 7}, {0, 4, 5, 1}};
    private static final float[][] NORM = new float[][]{{1.0f, 0.0f, 0.0f}, {-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};

    private static void box(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
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
                    vc.addVertex(p.x, p.y, p.z).setColor(r, g, b, 255).setUv(0.5f, 0.5f).setUv1(0, 10).setUv2(240, 240).setNormal(n.x, n.y, n.z);
                }
            }
        }
    }

    // ===== persistence =====

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
        public float s = 1.0f;

        public C101() {
        }
    }
}
