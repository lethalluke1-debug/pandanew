package com.lethalauction.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

/** Drawing helpers. All coordinates are in the GUI's design space (see LethalScreen). */
public final class Draw {
    public static final Identifier REGULAR = id("regular");
    public static final Identifier MEDIUM = id("medium");
    public static final Identifier SEMIBOLD = id("semibold");
    public static final Identifier LOGO = id("logo");
    public static final Identifier ICONS = id("icons");
    public static final Identifier ICONS_FILLED = id("icons_filled");

    /** Distance from the top of a line of text to its visual middle, in font units (font size 10). */
    static float textMid = 4.0f;

    private Draw() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("lethalauction", path);
    }

    private static Component comp(String s, Identifier font) {
        return Component.literal(s).withStyle(st -> st.withFont(new FontDescription.Resource(font)));
    }

    public static float width(String s, float size, Identifier font) {
        return Minecraft.getInstance().font.width(comp(s, font)) * size / 10f;
    }

    /** Draws text with its left edge at x and its vertical middle at cy. */
    public static void text(GuiGraphicsExtractor g, String s, float x, float cy, float size, int color, Identifier font) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(x, cy);
        p.scale(size / 10f);
        p.translate(0, -textMid);
        g.text(Minecraft.getInstance().font, comp(s, font), 0, 0, color, false);
        p.popMatrix();
    }

    public static void textCentered(GuiGraphicsExtractor g, String s, float cx, float cy, float size, int color, Identifier font) {
        text(g, s, cx - width(s, size, font) / 2f, cy, size, color, font);
    }

    public static void textRight(GuiGraphicsExtractor g, String s, float rx, float cy, float size, int color, Identifier font) {
        text(g, s, rx - width(s, size, font), cy, size, color, font);
    }

    /** Draws a single icon glyph centred on (cx, cy). */
    public static void icon(GuiGraphicsExtractor g, int codepoint, float cx, float cy, float size, int color) {
        iconFrom(g, ICONS, codepoint, cx, cy, size, color);
    }

    public static void iconFilled(GuiGraphicsExtractor g, int codepoint, float cx, float cy, float size, int color) {
        iconFrom(g, ICONS_FILLED, codepoint, cx, cy, size, color);
    }

    private static void iconFrom(GuiGraphicsExtractor g, Identifier font, int codepoint, float cx, float cy, float size, int color) {
        String s = new String(Character.toChars(codepoint));
        textCentered(g, s, cx, cy, size, color, font);
    }

    public static void rect(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        if (w > 0 && h > 0) {
            g.fill(x, y, x + w, y + h, color);
        }
    }

    private static final Identifier CORNERS = id("textures/gui/corners.png");
    private static final int ATLAS_W = 400, ATLAS_H = 2500, ATLAS_SCALE = 4, MAX_RADIUS = 24;

    /** Filled rounded rectangle: four anti-aliased corner sprites plus three rectangles. */
    public static void round(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        r = Math.max(0, Math.min(Math.min(r, MAX_RADIUS), Math.min(w, h) / 2));
        if (r == 0) {
            rect(g, x, y, w, h, color);
            return;
        }
        corners(g, x, y, w, h, r, 2, color);
        rect(g, x + r, y, w - 2 * r, h, color);
        rect(g, x, y + r, r, h - 2 * r, color);
        rect(g, x + w - r, y + r, r, h - 2 * r, color);
    }

    /** Rounded rectangle with a 1-unit border ring. Works with translucent colours. */
    public static void roundBordered(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int fill, int border) {
        r = Math.max(1, Math.min(Math.min(r, MAX_RADIUS), Math.min(w, h) / 2));
        if ((fill >>> 24) != 0) {
            round(g, x + 1, y + 1, w - 2, h - 2, r - 1, fill);
        }
        corners(g, x, y, w, h, r, 8 * r + 6, border);
        rect(g, x + r, y, w - 2 * r, 1, border);
        rect(g, x + r, y + h - 1, w - 2 * r, 1, border);
        rect(g, x, y + r, 1, h - 2 * r, border);
        rect(g, x + w - 1, y + r, 1, h - 2 * r, border);
    }

    private static void corners(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int cellX, int color) {
        int cellY = 4 * r * r - 2;
        int q = r * ATLAS_SCALE;
        corner(g, x, y, r, cellX, cellY, color);
        corner(g, x + w - r, y, r, cellX + q, cellY, color);
        corner(g, x, y + h - r, r, cellX, cellY + q, color);
        corner(g, x + w - r, y + h - r, r, cellX + q, cellY + q, color);
    }

    private static void corner(GuiGraphicsExtractor g, int x, int y, int r, int u, int v, int color) {
        int q = r * ATLAS_SCALE;
        g.blit(RenderPipelines.GUI_TEXTURED, CORNERS, x, y, u, v, r, r, q, q, ATLAS_W, ATLAS_H, color);
    }

    /** Multiplies a colour's alpha by f. */
    public static int alpha(int color, float f) {
        int a = Math.round((color >>> 24) * f);
        return (Math.max(0, Math.min(255, a)) << 24) | (color & 0xFFFFFF);
    }

    public static void circle(GuiGraphicsExtractor g, float cx, float cy, float radius, int color) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(cx, cy);
        p.scale(radius / 16f);
        round(g, -16, -16, 32, 32, 16, color);
        p.popMatrix();
    }

    public static void diamond(GuiGraphicsExtractor g, float cx, float cy, float size, int color) {
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(cx, cy);
        p.rotate((float) (Math.PI / 4));
        p.scale(size / 20f);
        rect(g, -7, -7, 14, 14, color);
        p.popMatrix();
    }

    public static void texture(GuiGraphicsExtractor g, Identifier tex, int x, int y, int w, int h, int texW, int texH) {
        texture(g, tex, x, y, w, h, texW, texH, 0xFFFFFFFF);
    }

    /** Draws a texture stretched to w x h, multiplied by the given ARGB colour. */
    public static void texture(GuiGraphicsExtractor g, Identifier tex, int x, int y, int w, int h, int texW, int texH, int color) {
        g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, w, h, texW, texH, texW, texH, color);
    }

    public static void hGradient(GuiGraphicsExtractor g, int x, int y, int w, int h, int from, int to) {
        for (int i = 0; i < w; i++) {
            rect(g, x + i, y, 1, h, lerpColor(from, to, w <= 1 ? 0 : i / (float) (w - 1)));
        }
    }

    public static int lerpColor(int a, int b, float t) {
        int aa = a >>> 24, ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int ba = b >>> 24, br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
    }
}
