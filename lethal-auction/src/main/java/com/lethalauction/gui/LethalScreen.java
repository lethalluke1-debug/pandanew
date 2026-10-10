package com.lethalauction.gui;

import com.lethalauction.gui.Modules.Columns;
import com.lethalauction.gui.Modules.Module;
import com.lethalauction.gui.Modules.Setting;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

/**
 * The Lethal Auction menu. Everything is laid out in a fixed 1036x518 "design space" that is scaled to
 * the window, so it keeps the same proportions at every GUI scale. Only the sidebar tabs and scrolling
 * respond to input; every other button and toggle is display-only.
 */
public class LethalScreen extends Screen {
    static final int DW = 1036;
    static final int DH = 518;

    // Palette
    static final int ACCENT = 0xFFE000FD;
    static final int WHITE = 0xFFF3F3F5;
    static final int TEXT = 0xFFE3E3E8;
    static final int GRAY = 0xFF9B9FA4;
    static final int MUTED = 0xFF8F8597;
    static final int CARD = 0xFF101118;
    static final int CARD_BORDER = 0xFF1B1622;
    static final int CARD_ON = 0xFF1A1224;
    static final int CARD_ON_BORDER = 0xFFA21FC2;
    static final int SEPARATOR = 0xFF2B2135;
    static final int BUTTON = 0xFF14131A;
    static final int BUTTON_BORDER = 0xFF34323C;
    static final int RED = 0xFFC4141C;

    // Icon code points (Tabler Icons)
    static final int I_SWORDS = 0xf132, I_RUN = 0xec82, I_DONUT = 0xeadd, I_EYE = 0xea9a, I_TOOL = 0xeb40,
            I_SETTINGS = 0xeb20, I_FILE = 0xeaa2, I_PALETTE = 0xeb01, I_USERS = 0xebf2, I_SEARCH = 0xeb1c,
            I_LEFT = 0xea60, I_RIGHT = 0xea61, I_DOWN = 0xea5f, I_DESKTOP = 0xea89, I_MUSIC = 0xeafc,
            I_X = 0xeb55, I_RESET = 0xeb15, I_BRUSH = 0xebb8, I_SPARKLES = 0xf6d7, I_COMMAND = 0xea78;
    static final int IF_INFO = 0xf6d8, IF_USER = 0xfd19;

    static final Identifier TEX_FRAME = Draw.id("textures/gui/frame.png");
    static final Identifier TEX_SIDEBAR = Draw.id("textures/gui/sidebar.png");
    static final Identifier TEX_CONTENT = Draw.id("textures/gui/content.png");

    enum Page {
        COMBAT("Combat", I_SWORDS, 132), MOVEMENT("Movement", I_RUN, 162), DONUT("DonutSMP", I_DONUT, 193),
        VISUALS("Visuals", I_EYE, 223), MISC("Misc", I_TOOL, 253), SETTINGS("Settings", I_SETTINGS, 320),
        CONFIGS("Configs", I_FILE, 350), THEME("Theme", I_PALETTE, 380), SOCIALS("Socials", I_USERS, 410);

        final String label;
        final int icon;
        final int y;

        Page(String label, int icon, int y) {
            this.label = label;
            this.icon = icon;
            this.y = y;
        }
    }

    // Content viewport
    static final int VIEW_X = 232, VIEW_Y = 54, VIEW_R = 1027, VIEW_B = 508;

    private static Page lastPage = Page.COMBAT;

    private Page page = lastPage;
    private final float[] scrollTarget = new float[Page.values().length];
    private final float[] scroll = new float[Page.values().length];
    private final float[] maxScroll = new float[Page.values().length];
    private final Columns combat = Modules.combat(), movement = Modules.movement(), donut = Modules.donut(),
            visuals = Modules.visuals(), misc = Modules.misc();

    private float scale = 1, originX, originY;

    public LethalScreen() {
        super(Component.literal("Lethal Auction"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void layout() {
        scale = Math.min(width * 0.564f / DW, height * 0.94f / DH);
        originX = (width - DW * scale) / 2f;
        originY = (height - DH * scale) / 2f;
    }

    private float toDesignX(double mx) {
        return (float) ((mx - originX) / scale);
    }

    private float toDesignY(double my) {
        return (float) ((my - originY) / scale);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        layout();
        int pi = page.ordinal();
        scroll[pi] += (scrollTarget[pi] - scroll[pi]) * 0.35f;
        if (Math.abs(scrollTarget[pi] - scroll[pi]) < 0.3f) {
            scroll[pi] = scrollTarget[pi];
        }

        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(originX, originY);
        p.scale(scale);

        Draw.texture(g, TEX_FRAME, 0, 0, DW, DH, DW * 2, DH * 2);
        drawSidebar(g, toDesignX(mouseX), toDesignY(mouseY));
        Draw.texture(g, TEX_CONTENT, 232, 7, 795, 502, 795 * 2, 502 * 2);
        drawHeader(g);

        g.enableScissor(VIEW_X, VIEW_Y, VIEW_R, VIEW_B);
        int top = Math.round(-scroll[pi]);
        int contentBottom = switch (page) {
            case COMBAT -> drawColumns(g, combat, top);
            case MOVEMENT -> drawColumns(g, movement, top);
            case DONUT -> drawColumns(g, donut, top);
            case VISUALS -> drawColumns(g, visuals, top);
            case MISC -> drawColumns(g, misc, top);
            case SETTINGS -> drawSettings(g, top);
            case CONFIGS -> drawConfigs(g, top);
            case THEME -> drawTheme(g, top);
            case SOCIALS -> drawSocials(g, top);
        };
        g.disableScissor();

        float contentHeight = contentBottom - top;
        maxScroll[pi] = Math.max(0, contentHeight - (VIEW_B - VIEW_Y) + 6);
        drawScrollbar(g, contentHeight, scroll[pi]);

        p.popMatrix();
    }

    // ---------------------------------------------------------------- sidebar

    private void drawSidebar(GuiGraphicsExtractor g, float mx, float my) {
        Draw.texture(g, TEX_SIDEBAR, 8, 7, 215, 500, 430, 1000);

        // logo
        Draw.round(g, 27, 23, 38, 44, 8, 0x30E000FD);
        Draw.round(g, 32, 27, 11, 35, 3, ACCENT);
        Draw.round(g, 32, 51, 29, 11, 3, ACCENT);
        Draw.text(g, "LETHAL", 75, 37, 15, 0xFFEDE6F2, Draw.LOGO);
        Draw.text(g, "A U C T I O N", 76, 53, 9.5f, 0xFFD9CCE2, Draw.SEMIBOLD);
        Draw.text(g, "v1.0.0", 75, 67, 10.5f, MUTED, Draw.REGULAR);

        Draw.rect(g, 26, 83, 178, 1, 0x664A2A55);
        Draw.text(g, "MODULES", 26, 105, 12.5f, 0xFF8C7C95, Draw.REGULAR);
        Draw.text(g, "GENERAL", 26, 292, 12.5f, 0xFF8C7C95, Draw.REGULAR);

        for (Page pg : Page.values()) {
            boolean active = pg == page;
            boolean hover = mx >= 24 && mx < 212 && my >= pg.y - 13 && my < pg.y + 14;
            if (active) {
                Draw.round(g, 24, pg.y - 13, 188, 27, 7, 0x8A3A2546);
                Draw.round(g, 20, pg.y - 7, 3, 14, 1, ACCENT);
            } else if (hover) {
                Draw.round(g, 24, pg.y - 13, 188, 27, 7, 0x40362240);
            }
            Draw.icon(g, pg.icon, 47, pg.y, 21, active ? 0xFFC10FDC : 0xFFA597AD);
            Draw.text(g, pg.label, 75, pg.y, 13, active ? 0xFFCC12E8 : 0xFFBBADC4, active ? Draw.SEMIBOLD : Draw.MEDIUM);
        }

        Draw.rect(g, 26, 439, 178, 1, 0x664A2A55);

        // user card
        Draw.roundBordered(g, 20, 449, 192, 50, 10, 0xFF0F1319, 0xFF241A2A);
        PlayerFaceExtractor.extractRenderState(g, skin(), 31, 459, 28);
        Draw.text(g, "You", 75, 465, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Lifetime", 75, 483, 11.5f, MUTED, Draw.REGULAR);
        Draw.circle(g, 191, 473, 7, 0x3343CC7F);
        Draw.circle(g, 191, 473, 4.5f, 0x7743CC7F);
        Draw.circle(g, 191, 473, 3.5f, 0xFF43CC7F);
    }

    private PlayerSkin skin() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            return mc.player.getSkin();
        }
        return DefaultPlayerSkin.get(mc.getGameProfile());
    }

    // ---------------------------------------------------------------- header

    private void drawHeader(GuiGraphicsExtractor g) {
        float hx = 253;
        Draw.text(g, "Hello, ", hx, 29, 13, 0xFFC3C3CC, Draw.REGULAR);
        Draw.text(g, "You", hx + Draw.width("Hello, ", 13, Draw.REGULAR), 29, 13, WHITE, Draw.MEDIUM);

        String clock = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Draw.textCentered(g, clock, 519, 29, 13, 0xFFC419E0, Draw.SEMIBOLD);

        Draw.roundBordered(g, 753, 17, 251, 24, 9, 0xFF0B0D12, 0xFF1F2128);
        Draw.icon(g, I_SEARCH, 771, 29, 13, 0xFFA4A6AD);
        Draw.text(g, "Search modules...", 787, 29, 12.5f, 0xFFA4A6AD, Draw.REGULAR);
        Draw.roundBordered(g, 960, 20, 38, 18, 6, 0xFF15171D, 0xFF2E3038);
        Draw.icon(g, I_COMMAND, 973, 29, 10, 0xFFC8C9CE);
        Draw.text(g, "K", 982, 29, 11.5f, 0xFFC8C9CE, Draw.MEDIUM);
    }

    private void drawScrollbar(GuiGraphicsExtractor g, float contentHeight, float offset) {
        float view = VIEW_B - VIEW_Y;
        if (contentHeight <= view + 6) {
            return;
        }
        float trackTop = 58, trackH = 446;
        float thumbH = Math.max(30, trackH * view / contentHeight);
        float maxS = contentHeight - view + 6;
        float t = maxS <= 0 ? 0 : offset / maxS;
        int y = Math.round(trackTop + (trackH - thumbH) * t);
        Draw.round(g, 1021, y, 3, Math.round(thumbH), 1, 0xFF4E4A56);
    }

    // ---------------------------------------------------------------- module pages

    private int drawColumns(GuiGraphicsExtractor g, Columns cols, int top) {
        int bottom = top;
        bottom = Math.max(bottom, drawColumn(g, cols.left(), 233, top));
        bottom = Math.max(bottom, drawColumn(g, cols.right(), 632, top));
        return bottom;
    }

    private int drawColumn(GuiGraphicsExtractor g, List<Module> mods, int x, int top) {
        int y = top + 62;
        for (Module m : mods) {
            y += drawModule(g, m, x, y, 381) + 9;
        }
        return y;
    }

    private int drawModule(GuiGraphicsExtractor g, Module m, int x, int y, int w) {
        int h = moduleHeight(m);
        int fill = m.on ? CARD_ON : CARD;
        if (m.on) {
            Draw.round(g, x - 2, y - 2, w + 4, h + 4, 12, 0x40B21FD6);
            Draw.roundBordered(g, x, y, w, h, 10, fill, CARD_ON_BORDER);
        } else {
            Draw.roundBordered(g, x, y, w, h, 10, fill, CARD_BORDER);
        }

        Draw.text(g, m.name, x + 13, y + 18, 12.5f, WHITE, Draw.SEMIBOLD);
        float infoX = x + 13 + Draw.width(m.name, 12.5f, Draw.SEMIBOLD) + 11;
        Draw.iconFilled(g, IF_INFO, infoX, y + 17, 13.5f, 0xFFE6E6EA);
        if (m.diamond) {
            Draw.diamond(g, infoX + 17, y + 16, 10, RED);
        }

        Draw.text(g, "KeyBind:", x + 13, y + 38, 12, GRAY, Draw.MEDIUM);
        float kx = x + 13 + Draw.width("KeyBind:", 12, Draw.MEDIUM) + 5;
        if (m.key != null) {
            Draw.text(g, m.key, kx, y + 38, 12, WHITE, Draw.SEMIBOLD);
        } else {
            keyIcon(g, Math.round(kx) + 3, y + 38, fill);
        }

        toggle(g, x + w - 47, y + 27, m.on);

        if (m.expanded()) {
            Draw.rect(g, x + 12, y + 50, w - 24, 1, SEPARATOR);
            int cy = y + 72;
            for (Setting s : m.settings) {
                switch (s.kind()) {
                    case TOGGLE -> {
                        Draw.text(g, s.label(), x + 19, cy, 12.5f, TEXT, Draw.MEDIUM);
                        toggle(g, x + w - 47, cy, s.on());
                        cy += 29;
                    }
                    case MODE -> {
                        Draw.text(g, s.label(), x + 19, cy, 12.5f, TEXT, Draw.MEDIUM);
                        modeBox(g, x + w - 150, cy, 131, s.value());
                        cy += 32;
                    }
                    case SLIDER -> {
                        Draw.text(g, s.label(), x + 18, cy, 12.5f, TEXT, Draw.MEDIUM);
                        int ty = cy + 17;
                        slider(g, x + 16, ty, w - 34, s.frac());
                        cy = ty + 29;
                    }
                    case SUBHEADER -> {
                        Draw.rect(g, x + 12, cy - 9, w - 24, 1, SEPARATOR);
                        Draw.text(g, s.label(), x + 16, cy - 1, 12, 0xFF8F8F98, Draw.REGULAR);
                        cy += 26;
                    }
                    case RESET -> Draw.textRight(g, "Reset", x + w - 17, cy - 5, 12, 0xFFA8AAB0, Draw.REGULAR);
                }
            }
        }
        return h;
    }

    private static int moduleHeight(Module m) {
        if (!m.expanded()) {
            return 54;
        }
        int cy = 72;
        boolean endsWithReset = false;
        for (Setting s : m.settings) {
            switch (s.kind()) {
                case TOGGLE -> cy += 29;
                case MODE -> cy += 32;
                case SLIDER -> cy += 46;
                case SUBHEADER -> cy += 26;
                case RESET -> endsWithReset = true;
            }
        }
        return endsWithReset ? cy - 5 + 24 : cy - 12;
    }

    static void toggle(GuiGraphicsExtractor g, int x, int cy, boolean on) {
        if (on) {
            Draw.round(g, x, cy - 8, 32, 16, 8, ACCENT);
            Draw.circle(g, x + 24, cy, 6, 0xFFFFFFFF);
        } else {
            Draw.round(g, x, cy - 8, 32, 16, 8, 0xFF2E3139);
            Draw.circle(g, x + 8, cy, 6, 0xFF9EA2AA);
        }
    }

    static void slider(GuiGraphicsExtractor g, int x, int cy, int w, float frac) {
        Draw.round(g, x, cy - 2, w, 4, 2, 0xFF30273C);
        int fw = Math.round(w * frac);
        if (fw > 0) {
            Draw.round(g, x, cy - 2, Math.max(4, fw), 4, 2, 0xFFB40FD4);
        }
        Draw.circle(g, x + fw, cy, 5, 0xFFFFFFFF);
    }

    static void modeBox(GuiGraphicsExtractor g, int x, int cy, int w, String value) {
        Draw.roundBordered(g, x, cy - 12, w, 25, 6, 0xFF050407, 0xFF33123D);
        Draw.icon(g, I_LEFT, x + 11, cy, 12, 0xFF9C9CA6);
        Draw.icon(g, I_RIGHT, x + w - 11, cy, 12, 0xFF9C9CA6);
        Draw.textCentered(g, value, x + w / 2f, cy, 12.5f, WHITE, Draw.MEDIUM);
    }

    static void keyIcon(GuiGraphicsExtractor g, int cx, int cy, int bg) {
        Draw.roundBordered(g, cx - 4, cy - 5, 9, 10, 2, bg, GRAY);
        Draw.rect(g, cx - 1, cy - 2, 3, 4, GRAY);
    }

    static void button(GuiGraphicsExtractor g, int x, int y, int w, int h, String label, int fill, int border, int color) {
        Draw.roundBordered(g, x, y, w, h, 6, fill, border);
        Draw.textCentered(g, label, x + w / 2f, y + h / 2f + 0.5f, 12.5f, color, Draw.SEMIBOLD);
    }

    // ---------------------------------------------------------------- settings

    private int drawSettings(GuiGraphicsExtractor g, int t) {
        Draw.icon(g, I_SETTINGS, 253, t + 80, 22, 0xFFB00FD0);
        Draw.text(g, "Settings", 272, t + 76, 13.5f, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Configure interface, security, and system behavior.", 272, t + 92, 10.5f, 0xFFB3B0BA, Draw.REGULAR);

        // Interface
        Draw.roundBordered(g, 235, t + 120, 778, 197, 12, 0xFF1A1124, 0xFF2A1A36);
        Draw.icon(g, I_DESKTOP, 261, t + 150, 16, 0xFFD10FF0);
        Draw.text(g, "Interface", 280, t + 150, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "GUI Settings", 250, t + 176, 10.5f, 0xFFB3B0BA, Draw.REGULAR);
        Draw.roundBordered(g, 250, t + 192, 746, 108, 10, 0xFF130E1A, 0xFF2A2433);
        Draw.text(g, "Menu Bind", 268, t + 223, 12.5f, WHITE, Draw.MEDIUM);
        Draw.roundBordered(g, 906, t + 206, 74, 29, 6, 0xFF131219, 0xFF34323C);
        Draw.textCentered(g, "RSHIFT", 943, t + 221, 12, WHITE, Draw.SEMIBOLD);
        Draw.rect(g, 258, t + 247, 730, 1, 0xFF221A29);
        Draw.text(g, "Quick Friend", 268, t + 278, 12.5f, WHITE, Draw.MEDIUM);
        Draw.round(g, 940, t + 270, 32, 16, 8, 0xFF0A0C0E);
        Draw.circle(g, 948, t + 278, 7, 0xFFF2F2F4);

        // Sounds
        Draw.roundBordered(g, 235, t + 330, 778, 232, 12, 0xFF1A1124, 0xFF2A1A36);
        Draw.icon(g, I_MUSIC, 260, t + 359, 16, 0xFFD10FF0);
        Draw.text(g, "Sounds", 281, t + 360, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Client Sounds", 250, t + 386, 10.5f, 0xFFB3B0BA, Draw.REGULAR);
        Draw.roundBordered(g, 250, t + 402, 746, 144, 10, 0xFF130E1A, 0xFF2A2433);
        Draw.text(g, "Module Sound", 268, t + 426, 12.5f, WHITE, Draw.MEDIUM);
        toggle(g, 945, t + 426, true);
        Draw.text(g, "Sound Pack", 268, t + 466, 12.5f, WHITE, Draw.MEDIUM);
        Draw.roundBordered(g, 864, t + 452, 116, 25, 6, 0xFF131219, 0xFF34323C);
        Draw.text(g, "Default", 878, t + 465, 12.5f, WHITE, Draw.MEDIUM);
        Draw.icon(g, I_DOWN, 966, t + 465, 9, 0xFF9C9CA6);
        Draw.text(g, "Volume", 268, t + 506, 12.5f, WHITE, Draw.MEDIUM);
        Draw.textRight(g, "70%", 857, t + 505, 12, 0xFFC3C3CC, Draw.REGULAR);
        slider(g, 868, t + 504, 112, 0.31f);
        return t + 575;
    }

    // ---------------------------------------------------------------- configs

    private int drawConfigs(GuiGraphicsExtractor g, int t) {
        Draw.round(g, 246, t + 66, 754, 26, 7, 0xFF0D0F13);
        Draw.round(g, 248, t + 67, 249, 24, 6, 0xFF7A0B90);
        Draw.round(g, 248, t + 67, 249, 12, 6, 0xFF8A0FA2);
        Draw.textCentered(g, "My Configs", 372.5f, t + 79.5f, 12.5f, 0xFFF0C8FF, Draw.MEDIUM);
        Draw.textCentered(g, "Community", 624, t + 79.5f, 12.5f, 0xFFC9CAD0, Draw.REGULAR);
        Draw.textCentered(g, "Downloads", 875, t + 79.5f, 12.5f, 0xFFC9CAD0, Draw.REGULAR);

        button(g, 247, t + 103, 88, 23, "Autosave", BUTTON, 0xFF3A3344, WHITE);
        Draw.text(g, "Active", 348, t + 115, 12.5f, 0xFFB3B0BA, Draw.REGULAR);
        Draw.text(g, "test", 348 + Draw.width("Active ", 12.5f, Draw.REGULAR), t + 115, 12.5f, WHITE, Draw.MEDIUM);
        button(g, 770, t + 103, 72, 23, "Create", 0xFF6A0A7A, 0xFF9A1AB4, 0xFFF0B8FF);
        button(g, 849, t + 103, 72, 23, "Import", BUTTON, BUTTON_BORDER, WHITE);
        button(g, 928, t + 103, 72, 23, "Redeem", BUTTON, BUTTON_BORDER, WHITE);

        Draw.roundBordered(g, 242, t + 135, 763, 47, 8, 0xFF32243C, 0xFF4A2E58);
        Draw.round(g, 243, t + 142, 3, 32, 1, 0xFFCD0BE6);
        Draw.text(g, "test", 258, t + 154, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "114 modules", 258, t + 171, 12, 0xFFB3B0BA, Draw.REGULAR);
        button(g, 736, t + 148, 73, 23, "Publish", BUTTON, 0xFF44424E, WHITE);
        button(g, 816, t + 148, 68, 23, "Share", 0xFF2E2B35, 0xFF4A4654, 0xFFB98FC8);
        button(g, 891, t + 148, 74, 23, "Apply", 0xFF6A0A7A, 0xFF9A1AB4, 0xFFFF9BFF);
        Draw.roundBordered(g, 972, t + 148, 26, 23, 6, BUTTON, 0xFF44424E);
        Draw.icon(g, I_X, 985, t + 159.5f, 11, WHITE);
        return t + 190;
    }

    // ---------------------------------------------------------------- theme

    private static final String[] PRESET_NAMES = {"Limelight", "Sunset", "Electric", "Nebula", "Goldrush", "Emerald", "Plasma", "Crimson"};
    private static final int[][] PRESET_COLORS = {
            {0xB8F906, 0xE8F960, 0x04F97F, 0x181C04}, {0xFE0072, 0xFD6904, 0xB02DFF, 0x1E0228},
            {0x2E6AFE, 0x04BEFE, 0x5F7AFF, 0x0A1230}, {0x9E4EFF, 0xC77BFF, 0x5B18C9, 0x160032},
            {0xFEBD08, 0xFFD869, 0xFD9808, 0x2B180A}, {0x07DD8E, 0x33F7B3, 0x07A271, 0x0C2320},
            {0xFE2CCE, 0x6E5AFE, 0x08D8FF, 0x1E0142}, {0xFE3664, 0xFD775F, 0xD20007, 0x1C0009}};

    private int drawTheme(GuiGraphicsExtractor g, int t) {
        int[] cx = {249, 436, 623, 810};
        for (int i = 0; i < 8; i++) {
            int x = cx[i % 4];
            int y = t + 60 + (i / 4) * 69;
            Draw.round(g, x, y, 184, 60, 6, 0xFF24222C);
            Draw.text(g, PRESET_NAMES[i], x + 6, y + 13, 12, 0xFFE0DFE6, Draw.REGULAR);
            int sx = x + 3, sw = 178, sy = y + 26, sh = 32;
            int[] seg = {Math.round(sw * 0.25f), Math.round(sw * 0.25f), Math.round(sw * 0.25f)};
            int ox = sx;
            for (int k = 0; k < 4; k++) {
                int w = k < 3 ? seg[k] : sx + sw - ox;
                Draw.rect(g, ox, sy, w, sh, 0xFF000000 | PRESET_COLORS[i][k]);
                ox += w;
            }
        }

        // Appearance
        int a = t + 222;
        Draw.roundBordered(g, 233, a, 383, 330, 10, 0xFF1F1328, 0xFF2E1D3A);
        Draw.icon(g, I_BRUSH, 258, a + 25, 14, 0xFFD10FF0);
        Draw.text(g, "APPEARANCE", 278, a + 25, 12.5f, 0xFFCDBBD6, Draw.REGULAR);
        Draw.text(g, "Main Color", 256, a + 66, 12.5f, WHITE, Draw.MEDIUM);
        Draw.roundBordered(g, 549, a + 54, 24, 24, 4, 0xFFE100FE, 0xFFF0B8FF);
        Draw.icon(g, I_RESET, 591, a + 66, 10, 0xFF8F8597);

        int px = 254, py = a + 88, pw = 231, ph = 88;
        for (int i = 0; i < pw; i++) {
            int topCol = Draw.lerpColor(0xFFFFFFFF, 0xFFE100FE, i / (float) (pw - 1));
            g.fillGradient(px + i, py, px + i + 1, py + ph, topCol, 0xFF000000);
        }
        Draw.circle(g, px + pw, py + 1, 5, 0xFFFFFFFF);
        Draw.circle(g, px + pw, py + 1, 3.5f, 0xFFE100FE);

        int hy = a + 181;
        for (int i = 0; i < pw; i++) {
            Draw.rect(g, px + i, hy, 1, 10, 0xFF000000 | hsv(i / (float) pw));
        }
        Draw.circle(g, px + 188, hy + 5, 5, 0xFFFFFFFF);
        Draw.circle(g, px + 188, hy + 5, 3.5f, 0xFF3F2BFF);

        int ay = a + 199;
        for (int i = 0; i < pw; i += 5) {
            for (int j = 0; j < 10; j += 5) {
                boolean light = ((i / 5) + (j / 5)) % 2 == 0;
                Draw.rect(g, px + i, ay + j, Math.min(5, pw - i), 5, light ? 0xFFBDBDBD : 0xFF7A7A7A);
            }
        }
        for (int i = 0; i < pw; i++) {
            int alpha = Math.round(255f * i / (pw - 1));
            Draw.rect(g, px + i, ay, 1, 10, (alpha << 24) | 0xE100FE);
        }
        Draw.circle(g, px + pw, ay + 5, 5, 0xFFFFFFFF);
        Draw.circle(g, px + pw, ay + 5, 3.5f, 0xFFE100FE);

        Draw.text(g, "Toggle Style", 251, a + 236, 12.5f, WHITE, Draw.MEDIUM);
        Draw.text(g, "Choose the style of toggles.", 251, a + 251, 10.5f, 0xFFB3B0BA, Draw.REGULAR);
        Draw.round(g, 442, a + 230, 134, 30, 8, 0xFF211429);
        Draw.round(g, 443, a + 231, 66, 28, 7, 0xFF7A0B90);
        Draw.textCentered(g, "Modern", 476, a + 245, 12.5f, 0xFFF0B8FF, Draw.MEDIUM);
        Draw.textCentered(g, "Classic", 546, a + 245, 12.5f, WHITE, Draw.MEDIUM);
        Draw.icon(g, I_RESET, 591, a + 244, 10, 0xFF8F8597);
        Draw.text(g, "Ambient Color Mode", 251, a + 294, 12.5f, WHITE, Draw.MEDIUM);
        modeBox(g, 442, a + 294, 134, "Theme");

        // Effects
        Draw.roundBordered(g, 631, a, 382, 239, 10, 0xFF1F1328, 0xFF2E1D3A);
        Draw.icon(g, I_SPARKLES, 654, a + 25, 14, 0xFFD10FF0);
        Draw.text(g, "EFFECTS", 674, a + 25, 12.5f, 0xFFCDBBD6, Draw.REGULAR);
        String[][] fx = {{"See-Through GUI", "Allow background blur and transparency."},
                {"Frosted Blur", "Apply frosted glass blur to surfaces."},
                {"Ambient Background", "Enable the ambient gradient background."}};
        for (int i = 0; i < 3; i++) {
            int ry = a + 69 + i * 57;
            Draw.text(g, fx[i][0], 648, ry, 12.5f, WHITE, Draw.MEDIUM);
            Draw.text(g, fx[i][1], 648, ry + 16, 10, 0xFFB3B0BA, Draw.REGULAR);
            toggle(g, 942, ry + 8, true);
            Draw.icon(g, I_RESET, 989, ry + 8, 10, 0xFF8F8597);
        }
        return a + 345;
    }

    private static int hsv(float h) {
        float r = Math.abs(h * 6 - 3) - 1, gr = 2 - Math.abs(h * 6 - 2), b = 2 - Math.abs(h * 6 - 4);
        r = Math.max(0, Math.min(1, r));
        gr = Math.max(0, Math.min(1, gr));
        b = Math.max(0, Math.min(1, b));
        return ((int) (r * 255) << 16) | ((int) (gr * 255) << 8) | (int) (b * 255);
    }

    // ---------------------------------------------------------------- socials

    private int drawSocials(GuiGraphicsExtractor g, int t) {
        Draw.icon(g, I_USERS, 253, t + 79, 17, 0xFFD10FF0);
        Draw.text(g, "Saved Players", 272, t + 77, 13, WHITE, Draw.SEMIBOLD);
        int bx = Math.round(272 + Draw.width("Saved Players", 13, Draw.SEMIBOLD) + 10);
        Draw.round(g, bx, t + 66, 26, 18, 9, 0xFF9602B2);
        Draw.textCentered(g, "0", bx + 13, t + 75, 11.5f, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Players ignored by selected modules", 272, t + 94, 10.5f, 0xFFB3B0BA, Draw.REGULAR);
        button(g, 887, t + 69, 113, 26, "Add / Manage", BUTTON, BUTTON_BORDER, WHITE);

        Draw.roundBordered(g, 241, t + 105, 765, 135, 8, 0xFF0F1319, 0xFF201A2C);
        Draw.iconFilled(g, IF_USER, 623, t + 130, 22, 0xFF3A3E46);
        Draw.textCentered(g, "No saved players", 625, t + 162, 12.5f, WHITE, Draw.SEMIBOLD);
        Draw.textCentered(g, "Add a player and your modules will never target them.", 625, t + 179, 12.5f, 0xFFC3C5CB, Draw.REGULAR);
        button(g, 568, t + 199, 111, 27, "Add Player", 0xFF14151B, 0xFF2C2E35, WHITE);
        return t + 250;
    }

    // ---------------------------------------------------------------- input

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        layout();
        float x = toDesignX(event.x()), y = toDesignY(event.y());
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && x >= 24 && x < 212) {
            for (Page pg : Page.values()) {
                if (y >= pg.y - 13 && y < pg.y + 14) {
                    page = pg;
                    lastPage = pg;
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int pi = page.ordinal();
        scrollTarget[pi] = (float) Math.max(0, Math.min(maxScroll[pi], scrollTarget[pi] - scrollY * 32));
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    /** Dev helper: lets a test run jump straight to a page. */
    public LethalScreen showPage(String name) {
        for (Page pg : Page.values()) {
            if (pg.name().equalsIgnoreCase(name)) {
                page = pg;
            }
        }
        return this;
    }
}
