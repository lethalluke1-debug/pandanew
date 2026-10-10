package com.lethalauction.gui;

import com.lethalauction.LethalConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

/** Restyles the vanilla scoreboard sidebar: size, background colour and rounded corners. */
public final class ScoreboardStyle {
    public static final String[] BACKGROUNDS = {"Default", "Theme", "Custom", "Clear"};
    private static final int RADIUS = 3;

    private static int titleX0, titleY0, titleX1, titleY1, titleColor;
    private static boolean haveTitle;

    private ScoreboardStyle() {
    }

    /** Scales the sidebar around the middle of the right screen edge, where vanilla anchors it. */
    public static void begin(GuiGraphicsExtractor g) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        float s = LethalConfig.sbScale;
        if (s != 1f) {
            float ax = g.guiWidth(), ay = g.guiHeight() / 2f;
            p.translate(ax, ay);
            p.scale(s);
            p.translate(-ax, -ay);
        }
        haveTitle = false;
    }

    public static void end(GuiGraphicsExtractor g) {
        g.pose().popMatrix();
    }

    /** Vanilla's title-bar fill. Remembered and drawn together with the body so corners line up. */
    public static void titleFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        titleX0 = x0;
        titleY0 = y0;
        titleX1 = x1;
        titleY1 = y1;
        titleColor = color;
        haveTitle = true;
    }

    /** Vanilla's body fill: draws the whole background in the chosen style. */
    public static void bodyFill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        int body = bodyColor(color), title = titleColor(haveTitle ? titleColor : color);
        if (LethalConfig.sbBackground == 3) {
            return;
        }
        int top = haveTitle ? Math.min(titleY0, y0) : y0;
        if (!LethalConfig.sbRounded) {
            if (haveTitle) {
                g.fill(titleX0, titleY0, titleX1, titleY1, title);
            }
            g.fill(x0, y0, x1, y1, body);
            return;
        }
        if (!haveTitle) {
            Draw.round(g, x0, top, x1 - x0, y1 - top, RADIUS, body);
            return;
        }
        // Title (top corners) and body (bottom corners) are separate pieces that never overlap, so
        // see-through colours do not double up into a line where they meet.
        Draw.roundTop(g, titleX0, titleY0, titleX1 - titleX0, titleY1 - titleY0, RADIUS, title);
        Draw.roundBottom(g, x0, y0, x1 - x0, y1 - y0, RADIUS, body);
    }

    private static int bodyColor(int vanilla) {
        return switch (LethalConfig.sbBackground) {
            case 1 -> Draw.alpha(Draw.lerpColor(0xFF0B0A10, Theme.accent(), 0.35f), 0.55f);
            case 2 -> custom(LethalConfig.sbOpacity);
            default -> vanilla;
        };
    }

    private static int titleColor(int vanilla) {
        return switch (LethalConfig.sbBackground) {
            case 1 -> Draw.alpha(Theme.accentDark(), 0.75f);
            case 2 -> custom(Math.min(1f, LethalConfig.sbOpacity + 0.15f));
            default -> vanilla;
        };
    }

    /** The custom colour from the hue slider at the given opacity. */
    public static int custom(float opacity) {
        int rgb = java.awt.Color.HSBtoRGB(LethalConfig.sbHue, 0.75f, 0.55f) & 0xFFFFFF;
        return (Math.round(Math.max(0, Math.min(1, opacity)) * 255) << 24) | rgb;
    }
}
