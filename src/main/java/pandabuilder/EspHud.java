package pandabuilder;

import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Storage ESP drawn on the HUD: boxes, tracers and distance markers. Positions are projected to the screen with
 * the camera's matrices, so they show through walls no matter how the world renderer culls things or whether
 * shaders are on.
 */
public final class EspHud {
    private static final Matrix4f MATRIX = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();
    /** Output of {@link #projectBox}: x0, y0, x1, y1 in GUI pixels. */
    private static final int[] RECT = new int[4];
    /** Output of {@link #projectPoint}: x, y in GUI pixels. */
    private static final float[] POINT = new float[2];

    private EspHud() {}

    public static void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (!StorageEsp.isOn() || mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 cam = camera.position();
        camera.getViewRotationProjectionMatrix(MATRIX);
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();

        int shown = 0;
        for (StorageEsp.Target t : StorageEsp.targets()) {
            int color = t.type().color;
            Vec3 center = Vec3.atCenterOf(t.pos());

            if (StorageEsp.tracers() && projectPoint(center.subtract(cam), sw, sh)) {
                line(g, sw / 2.0f, sh / 2.0f, POINT[0], POINT[1], (color & 0x00FFFFFF) | 0xCC000000);
            }

            AABB box = new AABB(t.pos()).deflate(0.06).move(cam.reverse());
            if (!projectBox(box, sw, sh)) continue;
            shown++;

            if (StorageEsp.boxes()) {
                g.outline(RECT[0] - 1, RECT[1] - 1, RECT[2] - RECT[0] + 2, RECT[3] - RECT[1] + 2, 0xFF000000);
                g.outline(RECT[0], RECT[1], RECT[2] - RECT[0], RECT[3] - RECT[1], color);
            }
            if (StorageEsp.markers()) {
                String label = t.type().label + " " + Math.round(Math.sqrt(mc.player.distanceToSqr(center))) + "m";
                int lw = mc.font.width(label);
                int lx = (RECT[0] + RECT[2]) / 2 - lw / 2, ly = RECT[1] - 11;
                g.fill(lx - 2, ly - 1, lx + lw + 2, ly + 9, 0x99000000);
                g.text(mc.font, label, lx, ly, color, false);
            }
        }

        String status = String.format(Locale.ROOT, "Storage ESP • %d found • %d on screen",
                StorageEsp.targets().size(), shown);
        g.fill(4, 4, 10 + mc.font.width(status), 16, 0x99000000);
        g.text(mc.font, status, 7, 6, Brand.ACCENT, false);
    }

    /** 1px line, drawn as a thin rectangle rotated around its start point. */
    private static void line(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, int color) {
        float dx = x1 - x0, dy = y1 - y0;
        int length = Math.round((float) Math.sqrt(dx * dx + dy * dy));
        if (length < 1) return;
        g.pose().pushMatrix();
        g.pose().translate(x0, y0);
        g.pose().rotate((float) Math.atan2(dy, dx));
        g.fill(0, 0, length, 1, color);
        g.pose().popMatrix();
    }

    /**
     * Projects a camera-relative point. Points behind the camera are pushed off the screen edge in the direction
     * you'd turn to face them, so tracers still point the right way.
     */
    private static boolean projectPoint(Vec3 rel, int sw, int sh) {
        CORNER.set((float) rel.x, (float) rel.y, (float) rel.z, 1.0f);
        MATRIX.transform(CORNER);
        float w = Math.abs(CORNER.w) < 1.0e-4f ? 1.0e-4f : Math.abs(CORNER.w);
        float nx = CORNER.x / w, ny = CORNER.y / w;
        if (CORNER.w <= 0.0f) {
            float len = (float) Math.sqrt(nx * nx + ny * ny);
            if (len < 1.0e-4f) return false;
            nx = nx / len * 4.0f;
            ny = ny / len * 4.0f;
        }
        POINT[0] = (nx + 1.0f) * 0.5f * sw;
        POINT[1] = (1.0f - ny) * 0.5f * sh;
        return true;
    }

    /**
     * Projects a camera-relative box to its screen rectangle in {@link #RECT}. Returns false if any corner is
     * behind the camera or the box is entirely off screen.
     */
    private static boolean projectBox(AABB box, int sw, int sh) {
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
