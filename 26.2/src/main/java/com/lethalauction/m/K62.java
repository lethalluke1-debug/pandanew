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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;
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

    // Only draw decorations within this many blocks of the camera.
    private static final double RENDER_DIST = 30.0;
    // World lightmap packed into (u,v) for the decoration body being drawn, so
    // decorations are shaded by the real world light instead of always full-bright.
    // The actual bulbs/lights are drawn emissive via glow() so they stand out.
    private static int lu = 240;
    private static int lv = 240;

    // Shown in the Room tab.
    public static final String[] NAMES = new String[]{"Christmas Tree", "Snow"};
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

    // Place the selected decoration. Never deletes: if a decoration is already
    // in that cell this is a no-op, so pressing the key near existing ones can't
    // wipe them. Removal is done with the Remove last / Clear all buttons.
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
        // Snow gets a random shallow depth each time it is placed (a low layer,
        // never a full block): 0.10, 0.15, 0.20, 0.25 or 0.30 of a block.
        p.h = sel == 1 ? 0.10f + 0.05f * (float)(int)(Math.random() * 5.0) : 0.0f;
        placed.add(p);
        K62.save();
    }

    public static void render(LevelRenderContext ctx) {
        try {
            K62.load();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            if (placed.isEmpty() && !editing) {
                return;
            }
            Vec3 cam = mc.gameRenderer.mainCamera().position();
            double maxSq = RENDER_DIST * RENDER_DIST;
            VertexConsumer vc = buf.a(RenderTypes.entityCutoutCull(K10.a));
            Matrix4f base = ctx.poseStack().last().pose();
            for (C101 p : placed) {
                double dx = (double)p.x + 0.5 - cam.x;
                double dy = (double)p.y + 0.5 - cam.y;
                double dz = (double)p.z + 0.5 - cam.z;
                if (dx * dx + dy * dy + dz * dz > maxSq) {
                    continue;
                }
                // Snow ignores the size slider so it always stays a one-block layer.
                float sc = p.t == 1 ? 1.0f : (p.s <= 0.0f ? 1.0f : p.s);
                K62.lightAt(mc, p.x, p.y, p.z);
                K62.draw(vc, base, cam, p.x, p.y, p.z, p.r, sc, p.t, p.h);
            }
            if (editing) {
                BlockPos bp = K62.target(mc);
                if (bp != null) {
                    K62.lightAt(mc, bp.getX(), bp.getY(), bp.getZ());
                    // Ghost shows a representative snow depth (real depth is random on place).
                    K62.draw(vc, base, cam, bp.getX(), bp.getY(), bp.getZ(), rot, sel == 1 ? 1.0f : scl, sel, sel == 1 ? 0.2f : 0.0f);
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

    // Sample the real world light at the decoration's cell so its body is shaded
    // like the surroundings (dark at night / in caves). The bulbs stay emissive.
    private static void lightAt(Minecraft mc, int bx, int by, int bz) {
        lu = 240;
        lv = 240;
        try {
            if (mc.level != null) {
                BlockPos pos = new BlockPos(bx, by, bz);
                LevelLightEngine le = mc.level.getLightEngine();
                int block = le.getLayerListener(LightLayer.BLOCK).getLightValue(pos);
                int sky = le.getLayerListener(LightLayer.SKY).getLightValue(pos);
                lu = (block & 0xF) << 4;
                lv = (sky & 0xF) << 4;
            }
        }
        catch (Throwable t) {
            lu = 240;
            lv = 240;
        }
    }

    private static void draw(VertexConsumer vc, Matrix4fc base, Vec3 cam, int bx, int by, int bz, int r, float s, int type, float h) {
        Matrix4f m = new Matrix4f(base);
        m.translate((float)((double)bx + 0.5 - cam.x), (float)((double)by - cam.y), (float)((double)bz + 0.5 - cam.z));
        if (r != 0) {
            m.rotateY((float)Math.toRadians(r * 90));
        }
        if (s != 1.0f) {
            m.scale(s);
        }
        K62.drawGeom(vc, m, type, h);
    }

    private static void drawGeom(VertexConsumer vc, Matrix4f m, int type, float h) {
        switch (type) {
            case 0: K62.tree(vc, m); break;
            case 1: K62.snow(vc, m, h); break;
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
        // ornaments: float ~0.02 OUTSIDE the branch face (no intersection, so no
        // z-fighting line). Culling hides each one's inward face, so they still
        // read as sitting on the tree.
        K62.box(vc, m, 0.46f, 0.42f, -0.06f, 0.56f, 0.54f, 0.06f, RED);
        K62.box(vc, m, -0.06f, 0.44f, -0.56f, 0.06f, 0.56f, -0.46f, BLUE);
        K62.box(vc, m, -0.06f, 0.60f, 0.39f, 0.06f, 0.72f, 0.49f, GOLD);
        K62.box(vc, m, -0.49f, 0.64f, 0.10f, -0.39f, 0.76f, 0.22f, RED);
        K62.box(vc, m, 0.31f, 0.80f, -0.06f, 0.41f, 0.92f, 0.06f, BLUE);
        K62.box(vc, m, 0.10f, 0.84f, -0.41f, 0.22f, 0.96f, -0.31f, GOLD);
        // flashing string lights (twinkle over time), also floating just outside
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

    // Twinkling Christmas-light colour: cycles bright hues with a per-bulb phase.
    private static int twinkle(int i) {
        int[] cols = new int[]{0xFF4D4D, 0xFFE24A, 0x58E058, 0x5AA0FF, 0xFFFFFF, 0xFF7ACF, 0xFFA030};
        long t = System.currentTimeMillis() / 350L + (long)(i * 3);
        return cols[(int)(t % (long)cols.length)];
    }

    private static final int SNOW = 0xF0F4FA;
    private static final int SNOW2 = 0xDDE6F2;

    // A snow layer covering the block footprint, of the given height. Holograms
    // have no collision, so you can always walk through it. Height is randomised
    // when the decoration is placed, so no two look the same.
    private static void snow(VertexConsumer vc, Matrix4f m, float h) {
        if (h <= 0.0f) {
            h = 0.2f;
        }
        // slightly inset so neighbouring snow blocks don't z-fight along the seam
        K62.box(vc, m, -0.49f, 0.0f, -0.49f, 0.49f, h, 0.49f, SNOW);
        // faint shaded rim near the base for a bit of depth
        K62.box(vc, m, -0.50f, 0.0f, -0.50f, 0.50f, Math.min(0.03f, h), 0.50f, SNOW2);
    }

    // double-sided flat-colour box
    // Corners: 0..3 bottom (y0), 4..7 top (y1). Each face is wound CCW as seen
    // from outside, so with back-face culling only the outer shell draws (which
    // stops overlapping boxes z-fighting at their junctions).
    private static final int[][] FACE = new int[][]{{1, 5, 6, 2}, {0, 3, 7, 4}, {4, 7, 6, 5}, {0, 1, 2, 3}, {3, 2, 6, 7}, {0, 4, 5, 1}};
    private static final float[][] NORM = new float[][]{{1.0f, 0.0f, 0.0f}, {-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};

    // Emissive box: forces full-bright lightmap so bulbs/lights glow in the dark
    // (they only glow visually — client holograms can't cast light on the world).
    private static void glow(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        int su = lu;
        int sv = lv;
        lu = 240;
        lv = 240;
        K62.box(vc, m, x0, y0, z0, x1, y1, z1, rgb);
        lu = su;
        lv = sv;
    }

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
                    vc.addVertex(p.x, p.y, p.z).setColor(r, g, b, 255).setUv(0.5f, 0.5f).setUv1(0, 10).setUv2(lu, lv).setNormal(n.x, n.y, n.z);
                }
            }
        }
    }

    // ===== persistence =====

    // Force a fresh read from the save file (used on every server join so
    // placements are always restored, even after a relog). Clears first to
    // avoid duplicating what is already in memory.
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
            LethalAuctionClient.LOGGER.info("[Room] loaded {} decoration(s) from {} (exists={})", new Object[]{placed.size(), FILE, Files.exists(FILE)});
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
            LethalAuctionClient.LOGGER.info("[Room] saved {} decoration(s) to {}", (Object)placed.size(), (Object)FILE);
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
