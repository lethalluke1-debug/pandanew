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
 * Box ESP drawn on the HUD. Each target's bounding box is projected to the screen with the camera's matrices,
 * so it shows through walls no matter how the world renderer culls entities (vanilla occlusion, Sodium,
 * EntityCulling) or whether shaders hide the glowing outline.
 */
public final class EspHud {
    private static final Matrix4f MATRIX = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();

    private EspHud() {}

    public static void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (!Esp.isOn() || mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 cam = camera.position();
        camera.getViewRotationProjectionMatrix(MATRIX);
        float partial = delta.getGameTimeDeltaPartialTick(true);
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();

        int tracked = 0, shown = 0;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (!Esp.shouldGlow(e)) continue;
            tracked++;
            LivingEntity p = (LivingEntity) e;

            // Interpolated bounding box, relative to the camera.
            Vec3 pos = p.getPosition(partial);
            AABB box = p.getBoundingBox().move(pos.subtract(p.position())).move(cam.reverse());

            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
            boolean visible = true;
            for (int i = 0; i < 8 && visible; i++) {
                CORNER.set((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY,
                        (i & 4) == 0 ? box.minZ : box.maxZ, 1.0f);
                MATRIX.transform(CORNER);
                if (CORNER.w <= 0.05f) {
                    visible = false; // behind the camera
                    break;
                }
                float sx = (CORNER.x / CORNER.w + 1.0f) * 0.5f * sw;
                float sy = (1.0f - CORNER.y / CORNER.w) * 0.5f * sh;
                minX = Math.min(minX, sx);
                minY = Math.min(minY, sy);
                maxX = Math.max(maxX, sx);
                maxY = Math.max(maxY, sy);
            }
            if (!visible || maxX < 0 || maxY < 0 || minX > sw || minY > sh) continue;

            int x0 = Math.round(minX), y0 = Math.round(minY), x1 = Math.round(maxX), y1 = Math.round(maxY);
            if (x1 - x0 < 2) x1 = x0 + 2;
            if (y1 - y0 < 4) y1 = y0 + 4;

            g.outline(x0 - 1, y0 - 1, x1 - x0 + 2, y1 - y0 + 2, 0xFF000000);
            g.outline(x0, y0, x1 - x0, y1 - y0, Esp.color(p));
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

        // Status line: "known" is what the server has sent this client. If a player behind a wall isn't counted,
        // the server is hiding them (anti-ESP) and no client-side ESP can show them.
        String status = "ESP \u2022 " + tracked + " known \u2022 " + shown + " on screen";
        g.fill(4, 4, 10 + mc.font.width(status), 16, 0x99000000);
        g.text(mc.font, status, 7, 6, Brand.ACCENT, false);
    }
}
