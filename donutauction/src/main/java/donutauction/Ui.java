package donutauction;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Colours and drawing helpers shared by the menu and the HUD. Text is drawn small by default. */
public final class Ui {
    private Ui() {}

    // Base palette (dark navy); the accent comes from the theme.
    public static final int WINDOW = 0xF20E0F17;
    public static final int SIDEBAR = 0xFF13141E;
    public static final int CARD = 0xFF191A26;
    public static final int CARD_HOVER = 0xFF20212F;
    public static final int FIELD = 0xFF0E0F17;
    public static final int BORDER = 0xFF2A2B3B;
    public static final int TEXT = 0xFFF4F4F8;
    public static final int MUTED = 0xFF9C9DB3;
    public static final int FAINT = 0xFF5F6178;
    public static final int GOLD = 0xFFF5C451;
    public static final int GREEN = 0xFF4ADE80;
    public static final int RED = 0xFFF0313F;
    public static final int TRACK_OFF = 0xFF2C2D3D;
    public static final int KNOB_OFF = 0xFF73758C;

    /** Accent presets for the Theme page. */
    public static final int[] ACCENTS = {0xFF8B5CF6, 0xFFD946EF, 0xFFF0313F, 0xFFF59E0B, 0xFF34D399, 0xFF22D3EE, 0xFF3B82F6, 0xFFFB7185};
    public static final String[] ACCENT_NAMES = {"Violet", "Magenta", "Crimson", "Amber", "Emerald", "Cyan", "Blue", "Rose"};

    public static int accent() {
        return Config.get().accent | 0xFF000000;
    }

    public static int alpha(int color, int a) {
        return (color & 0x00FFFFFF) | (a << 24);
    }

    /** Every piece of text in the mod is drawn at this size. */
    public static float scale() {
        return 0.75f;
    }

    /** 0..1 progress of an animation that started at {@code startMs} and lasts {@code durationMs}. */
    public static float anim(long startMs, long durationMs) {
        if (!Config.get().animations) return 1f;
        return Math.clamp((System.currentTimeMillis() - startMs) / (float) durationMs, 0f, 1f);
    }

    public static float easeOut(float t) {
        float u = 1 - t;
        return 1 - u * u * u;
    }

    /** Ease-out with a small overshoot, for things that pop in. */
    public static float easeOutBack(float t) {
        float c1 = 1.70158f, c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    // ---- text ----

    public static void text(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color) {
        text(g, font, Component.literal(s), x, y, color, scale());
    }

    public static void bold(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color) {
        text(g, font, Component.literal(s).withStyle(ChatFormatting.BOLD), x, y, color, scale());
    }

    public static void text(GuiGraphicsExtractor g, Font font, Component c, float x, float y, int color, float scale) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);
        g.text(font, c, 0, 0, color, false);
        g.pose().popMatrix();
    }

    public static int width(Font font, String s) {
        return Math.round(font.width(s) * scale());
    }

    public static int boldWidth(Font font, String s) {
        return Math.round(font.width(Component.literal(s).withStyle(ChatFormatting.BOLD)) * scale());
    }

    /** Height of one line of small text. */
    public static int lineH(Font font) {
        return Math.round(font.lineHeight * scale());
    }

    public static String ellipsize(Font font, String s, int maxW) {
        if (width(font, s) <= maxW) return s;
        int end = s.length();
        while (end > 0 && width(font, s.substring(0, end) + "...") > maxW) end--;
        return s.substring(0, end) + "...";
    }

    // ---- shapes ----

    /** Filled rectangle with rounded corners, built from horizontal strips. */
    public static void round(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        g.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    /** Rounded box with a 1px border. */
    public static void box(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int fill, int border) {
        round(g, x - 1, y - 1, w + 2, h + 2, r + 1, border);
        round(g, x, y, w, h, r, fill);
    }

    public static void toggle(GuiGraphicsExtractor g, int x, int y, boolean on) {
        round(g, x, y, 18, 9, 4, on ? accent() : TRACK_OFF);
        round(g, on ? x + 10 : x + 1, y + 1, 7, 7, 3, on ? TEXT : KNOB_OFF);
    }

    /** A button; primary ones are filled with the accent. */
    public static void button(GuiGraphicsExtractor g, Font font, int x, int y, int w, int h, String label, boolean hover,
                              boolean primary, boolean enabled) {
        int fill = !enabled ? CARD : primary ? (hover ? alpha(accent(), 0xFF) : alpha(accent(), 0xD0)) : hover ? CARD_HOVER : CARD;
        box(g, x, y, w, h, 4, fill, primary && enabled ? alpha(accent(), 0xFF) : BORDER);
        int color = !enabled ? FAINT : primary ? TEXT : hover ? TEXT : MUTED;
        text(g, font, label, x + (w - width(font, label)) / 2f, y + (h - lineH(font)) / 2f + 0.5f, color);
    }

    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static String clock(long ms) {
        long s = (ms + 999) / 1000;
        return String.format("%02d:%02d", s / 60, s % 60);
    }
}
