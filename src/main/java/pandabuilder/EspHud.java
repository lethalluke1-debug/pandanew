package pandabuilder;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Box ESP drawn on the HUD, for entities (Esp) and base blocks (StorageEsp). Each box is projected to the
 * screen with the camera's matrices, so it shows through walls no matter how the world renderer culls things
 * (vanilla occlusion, Sodium, EntityCulling) or whether shaders hide the glowing outline.
 */
public final class EspHud {
    private static final Matrix4f MATRIX = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();
    /** Output of {@link #project}: x0, y0, x1, y1 in GUI pixels. */
    private static final int[] RECT = new int[4];

    private EspHud() {}

    public static void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if ((!Esp.isOn() && !StorageEsp.isOn()) || mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 cam = camera.position();
        camera.getViewRotationProjectionMatrix(MATRIX);
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();

        StringBuilder status = new StringBuilder();
        if (StorageEsp.isOn()) {
            int shown = 0;
            for (StorageEsp.Target t : StorageEsp.targets()) {
                AABB box = new AABB(t.pos()).deflate(0.06).move(cam.reverse());
                if (!project(box, sw, sh)) continue;
                drawBox(g, t.color());
                shown++;
            }
            status.append("Storage • ").append(StorageEsp.targets().size()).append(" found • ")
                    .append(shown).append(" on screen");
        }
        if (Esp.isOn()) {
            int[] counts = drawEntities(g, mc, cam, delta.getGameTimeDeltaPartialTick(true), sw, sh);
            if (!status.isEmpty()) status.append("   ");
            // "known" is what the server has sent this client. If a player behind a wall isn't counted, the
            // server is hiding them (anti-ESP) and no client-side ESP can show them.
            status.append("ESP • ").append(counts[0]).append(" known • ").append(counts[1]).append(" on screen");
        }

        String text = status.toString();
        g.fill(4, 4, 10 + mc.font.width(text), 16, 0x99000000);
        g.text(mc.font, text, 7, 6, Brand.ACCENT, false);
    }

    /** Returns {known, on screen}. */
    private static int[] drawEntities(GuiGraphicsExtractor g, Minecraft mc, Vec3 cam, float partial, int sw, int sh) {
        int tracked = 0, shown = 0;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (!Esp.shouldGlow(e)) continue;
            tracked++;
            LivingEntity p = (LivingEntity) e;

            // Interpolated bounding box, relative to the camera.
            Vec3 pos = p.getPosition(partial);
            AABB box = p.getBoundingBox().move(pos.subtract(p.position())).move(cam.reverse());
            if (!project(box, sw, sh)) continue;
            int x0 = RECT[0], y0 = RECT[1], x1 = RECT[2], y1 = RECT[3];
            drawBox(g, Esp.color(p));
            shown++;

            // Health bar on the left.
            float health = Math.clamp(p.getHealth() / Math.max(1.0f, p.getMaxHealth()), 0.0f, 1.0f);
            int barTop = y1 - Math.round((y1 - y0) * health);
            g.fill(x0 - 4, y0 - 1, x0 - 2, y1 + 1, 0xFF000000);
            g.fill(x0 - 4, barTop, x0 - 2, y1, health > 0.5f ? 0xFF34D399 : health > 0.25f ? 0xFFFBBF24 : 0xFFF0313F);

            String label = p.getName().getString() + " " + Math.round(mc.player.distanceTo(p)) + "m";
            int lw = mc.font.width(label);
            int lx = (x0 + x1) / 2 - lw / 2, ly = y0 - 11;
            g.fill(lx - 2, ly - 1, lx + lw + 2, ly + 9, 0x99000000);
            g.text(mc.font, label, lx, ly, Brand.TEXT, false);
        }
        return new int[] {tracked, shown};
    }

    private static void drawBox(GuiGraphicsExtractor g, int color) {
        int x0 = RECT[0], y0 = RECT[1], x1 = RECT[2], y1 = RECT[3];
        g.outline(x0 - 1, y0 - 1, x1 - x0 + 2, y1 - y0 + 2, 0xFF000000);
        g.outline(x0, y0, x1 - x0, y1 - y0, color);
    }

    /**
     * Projects a camera-relative box to its screen rectangle in {@link #RECT}. Returns false if any corner is
     * behind the camera or the box is entirely off screen.
     */
    private static boolean project(AABB box, int sw, int sh) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            CORNER.set((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY,
                    (i & 4) == 0 ? box.minZ : box.maxZ, 1.0f);
            MATRIX.transform(CORNER);
            if (CORNER.w <= 0.05f) return false;
            float sx = (CORNER.x / CORNER.w + 1.0f) * 0.5f * sw;
            float sy = (1.0f - CORNER.y / CORNER.w) * 0.5f * sh;
            minX = Math.min(minX, sx);
            minY = Math.min(minY, sy);
            maxX = Math.max(maxX, sx);
            maxY = Math.max(maxY, sy);
        }
        if (maxX < 0 || maxY < 0 || minX > sw || minY > sh) return false;

        RECT[0] = Math.round(minX);
        RECT[1] = Math.round(minY);
        RECT[2] = Math.max(Math.round(maxX), RECT[0] + 2);
        RECT[3] = Math.max(Math.round(maxY), RECT[1] + 2);
        return true;
    }
}
