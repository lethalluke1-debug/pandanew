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
    public static boolean editing;  // placing mode: show a ghost at the looked-at block

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
                K62.draw(vc, base, cam, p.x, p.y, p.z, p.r, p.t);
            }
            if (editing) {
                BlockPos bp = K62.target(mc);
                if (bp != null) {
                    K62.draw(vc, base, cam, bp.getX(), bp.getY(), bp.getZ(), rot, sel);
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

    private static void draw(VertexConsumer vc, Matrix4fc base, Vec3 cam, int bx, int by, int bz, int r, int type) {
        Matrix4f m = new Matrix4f(base);
        m.translate((float)((double)bx + 0.5 - cam.x), (float)((double)by - cam.y), (float)((double)bz + 0.5 - cam.z));
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

    // ===== decoration geometry (origin at the centre of the block's bottom; up is +y) =====

    private static final int GREEN = 0x1E7A2E;
    private static final int DGREEN = 0x155C22;
    private static final int BROWN = 0x5A3A1A;
    private static final int WHITE = 0xF2F2F2;
    private static final int RED = 0xC8323A;
    private static final int GOLD = 0xFFD24A;
    private static final int BLUE = 0x3A7BD5;

    // Decoration parts either overlap deeply (so no thin near-coincident faces
    // z-fight) or stand clearly proud of the surface. Nothing is buried flush.
    private static void tree(VertexConsumer vc, Matrix4f m) {
        K62.box(vc, m, -0.09f, 0.0f, -0.09f, 0.09f, 0.26f, 0.09f, BROWN);
        K62.box(vc, m, -0.38f, 0.18f, -0.38f, 0.38f, 0.54f, 0.38f, GREEN);
        K62.box(vc, m, -0.27f, 0.46f, -0.27f, 0.27f, 0.80f, 0.27f, GREEN);
        K62.box(vc, m, -0.16f, 0.72f, -0.16f, 0.16f, 1.02f, 0.16f, GREEN);
        K62.box(vc, m, -0.09f, 0.96f, -0.09f, 0.09f, 1.14f, 0.09f, GOLD);
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
    private static final int[][] FACE = new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {1, 5, 7, 3}, {0, 2, 6, 4}};
    private static final float[][] NORM = new float[][]{{-1.0f, 0.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};

    private static void box(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
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
            int[][] tris = new int[][]{{q[0], q[2], q[1]}, {q[0], q[3], q[2]}};
            for (int[] tri : tris) {
                for (int k : tri) {
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

        public C101() {
        }
    }
}
