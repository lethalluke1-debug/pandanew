package com.lethalauction.gui;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.LethalConfig;
import com.lethalauction.gui.Modules.Module;
import com.lethalauction.gui.Modules.Setting;
import java.util.ArrayList;
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
 * the window, so it keeps the same proportions at every GUI scale. The sidebar tabs, theme presets, theme
 * effects and the module sound toggle work; the other buttons only play the click sound.
 */
public class LethalScreen extends Screen {
    static final int DW = 1036;
    static final int DH = 518;

    // Fixed palette
    static final int WHITE = 0xFFF3F3F5;
    static final int TEXT = 0xFFE3E3E8;
    static final int GRAY = 0xFF9B9FA4;
    static final int SUBTLE = 0xFFB3B0BA;
    static final int MUTED = 0xFF8F8597;
    static final int CARD = 0xFF101118;
    static final int CARD_BORDER = 0xFF1B1622;
    static final int BUTTON = 0xFF14131A;
    static final int BUTTON_BORDER = 0xFF34323C;
    static final int RED = 0xFFC4141C;

    static final int DEFAULT_ACCENT = 0xFFE000FD;
    static final int DEFAULT_TINT = 0xFFA611FB;

    // Icon code points (Tabler Icons)
    static final int I_GAVEL = 0xef90, I_HISTORY = 0xebea, I_SETTINGS = 0xeb20, I_PALETTE = 0xeb01,
            I_SEARCH = 0xeb1c, I_LEFT = 0xea60, I_RIGHT = 0xea61, I_DOWN = 0xea5f, I_DESKTOP = 0xea89,
            I_MUSIC = 0xeafc, I_X = 0xeb55, I_RESET = 0xeb15, I_SPARKLES = 0xf6d7, I_COMMAND = 0xea78;
    static final int IF_INFO = 0xf6d8;

    static final Identifier TEX_FRAME = Draw.id("textures/gui/frame.png");
    static final Identifier TEX_SIDEBAR = Draw.id("textures/gui/sidebar.png");
    static final Identifier TEX_CONTENT = Draw.id("textures/gui/content.png");

    static final String[] PRESET_NAMES = {"Limelight", "Sunset", "Electric", "Nebula", "Goldrush", "Emerald", "Plasma", "Crimson"};
    static final int[][] PRESET_COLORS = {
            {0xB8F906, 0xE8F960, 0x04F97F, 0x181C04}, {0xFE0072, 0xFD6904, 0xB02DFF, 0x1E0228},
            {0x2E6AFE, 0x04BEFE, 0x5F7AFF, 0x0A1230}, {0x9E4EFF, 0xC77BFF, 0x5B18C9, 0x160032},
            {0xFEBD08, 0xFFD869, 0xFD9808, 0x2B180A}, {0x07DD8E, 0x33F7B3, 0x07A271, 0x0C2320},
            {0xFE2CCE, 0x6E5AFE, 0x08D8FF, 0x1E0142}, {0xFE3664, 0xFD775F, 0xD20007, 0x1C0009}};

    enum Page {
        AUCTION("Auction", I_GAVEL, 132), SETTINGS("Settings", I_SETTINGS, 199),
        RECENT("Recent Auctions", I_HISTORY, 229), THEME("Theme", I_PALETTE, 259);

        final String label;
        final int icon;
        final int y;

        Page(String label, int icon, int y) {
            this.label = label;
            this.icon = icon;
            this.y = y;
        }
    }

    /** A clickable area in design space. A null action just plays the click sound. */
    private record Hotspot(int x, int y, int w, int h, Runnable action) {
        boolean contains(float px, float py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    // Content viewport
    static final int VIEW_X = 232, VIEW_Y = 54, VIEW_R = 1027, VIEW_B = 508;

    private static Page lastPage = Page.AUCTION;

    private Page page = lastPage;
    private final float[] scrollTarget = new float[Page.values().length];
    private final float[] scroll = new float[Page.values().length];
    private final float[] maxScroll = new float[Page.values().length];
    private final List<Module> auctionLeft = Modules.auctionLeft(), auctionRight = Modules.auctionRight();
    private final List<Hotspot> hotspots = new ArrayList<>();
    private boolean clipping;

    private float scale = 1, originX, originY;

    // Theme colours, refreshed every frame from LethalConfig
    private int accent, accentDark, accentLight, tint;
    private float surfaceAlpha;

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

    private void refreshTheme() {
        int p = LethalConfig.preset;
        if (p >= 0 && p < PRESET_COLORS.length) {
            accent = 0xFF000000 | PRESET_COLORS[p][0];
            tint = accent;
        } else {
            accent = DEFAULT_ACCENT;
            tint = DEFAULT_TINT;
        }
        accentDark = Draw.lerpColor(accent, 0xFF000000, 0.55f);
        accentLight = Draw.lerpColor(accent, 0xFFFFFFFF, 0.65f);
        surfaceAlpha = LethalConfig.seeThrough ? 0.72f : 1f;
    }

    /** A panel colour, made translucent when See-Through GUI is on. */
    private int surface(int color) {
        return Draw.alpha(color, surfaceAlpha);
    }

    /** A neutral dark colour with a little of the accent mixed in. */
    private int tinted(int base, float amount) {
        return Draw.lerpColor(base, accent, amount);
    }

    private void hotspot(int x, int y, int w, int h, Runnable action) {
        if (clipping) {
            int top = Math.max(y, VIEW_Y), bottom = Math.min(y + h, VIEW_B);
            if (bottom <= top) {
                return;
            }
            hotspots.add(new Hotspot(x, top, w, bottom - top, action));
        } else {
            hotspots.add(new Hotspot(x, y, w, h, action));
        }
    }

    // ---------------------------------------------------------------- background

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        if (minecraft.level != null && !LethalConfig.frostedBlur) {
            extractMenuBackground(g);
            return;
        }
        super.extractBackground(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        layout();
        refreshTheme();
        hotspots.clear();

        int pi = page.ordinal();
        scroll[pi] += (scrollTarget[pi] - scroll[pi]) * 0.35f;
        if (Math.abs(scrollTarget[pi] - scroll[pi]) < 0.3f) {
            scroll[pi] = scrollTarget[pi];
        }

        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(originX, originY);
        p.scale(scale);

        Draw.texture(g, TEX_FRAME, 0, 0, DW, DH, DW * 2, DH * 2, surface(0xFFFFFFFF));
        drawSidebar(g, toDesignX(mouseX), toDesignY(mouseY));
        if (LethalConfig.ambientBackground) {
            Draw.texture(g, TEX_CONTENT, 232, 7, 795, 502, 795 * 2, 502 * 2, surface(tint));
        } else {
            Draw.round(g, 232, 7, 795, 502, 14, surface(0xFF0E0E14));
            Draw.rect(g, 241, 48, 776, 1, accent);
        }
        drawHeader(g);

        g.enableScissor(VIEW_X, VIEW_Y, VIEW_R, VIEW_B);
        clipping = true;
        int top = Math.round(-scroll[pi]);
        int contentBottom = switch (page) {
            case AUCTION -> drawModules(g, top);
            case SETTINGS -> drawSettings(g, top);
            case RECENT -> drawRecent(g, top);
            case THEME -> drawTheme(g, top);
        };
        clipping = false;
        g.disableScissor();

        float contentHeight = contentBottom - top;
        maxScroll[pi] = Math.max(0, contentHeight - (VIEW_B - VIEW_Y) + 6);
        drawScrollbar(g, contentHeight, scroll[pi]);

        p.popMatrix();
    }

    // ---------------------------------------------------------------- sidebar

    private void drawSidebar(GuiGraphicsExtractor g, float mx, float my) {
        if (LethalConfig.ambientBackground) {
            Draw.texture(g, TEX_SIDEBAR, 8, 7, 215, 500, 430, 1000, surface(tint));
        } else {
            Draw.roundBordered(g, 8, 7, 215, 500, 14, surface(0xFF121118), 0xFF26222C);
        }

        // logo
        Draw.round(g, 27, 23, 38, 44, 8, Draw.alpha(accent, 0.19f));
        Draw.round(g, 32, 27, 11, 35, 3, accent);
        Draw.round(g, 32, 51, 29, 11, 3, accent);
        Draw.text(g, "LETHAL", 75, 37, 15, 0xFFEDE6F2, Draw.LOGO);
        Draw.text(g, "A U C T I O N", 76, 53, 9.5f, 0xFFD9CCE2, Draw.SEMIBOLD);
        Draw.text(g, "v1.0.0", 75, 67, 10.5f, MUTED, Draw.REGULAR);

        Draw.rect(g, 26, 83, 178, 1, 0x66FFFFFF & tinted(0xFF2A2A30, 0.3f));
        Draw.text(g, "MODULES", 26, 105, 12.5f, 0xFF8C8495, Draw.REGULAR);
        Draw.text(g, "GENERAL", 26, 171, 12.5f, 0xFF8C8495, Draw.REGULAR);

        for (Page pg : Page.values()) {
            boolean active = pg == page;
            boolean hover = mx >= 24 && mx < 212 && my >= pg.y - 13 && my < pg.y + 14;
            if (active) {
                Draw.round(g, 24, pg.y - 13, 188, 27, 7, Draw.alpha(tinted(0xFF2A2A32, 0.22f), 0.6f));
                Draw.round(g, 20, pg.y - 7, 3, 14, 1, accent);
            } else if (hover) {
                Draw.round(g, 24, pg.y - 13, 188, 27, 7, 0x40362240);
            }
            int activeColor = Draw.lerpColor(accent, 0xFFFFFFFF, 0.08f);
            Draw.icon(g, pg.icon, 47, pg.y, 21, active ? activeColor : 0xFFA597AD);
            Draw.text(g, pg.label, 75, pg.y, 13, active ? activeColor : 0xFFBBADC4, active ? Draw.SEMIBOLD : Draw.MEDIUM);
            Page target = pg;
            hotspot(24, pg.y - 13, 188, 27, () -> {
                page = target;
                lastPage = target;
            });
        }

        Draw.rect(g, 26, 439, 178, 1, 0x66FFFFFF & tinted(0xFF2A2A30, 0.3f));

        // user card
        Draw.roundBordered(g, 20, 449, 192, 50, 10, surface(0xFF0F1319), 0xFF241A2A);
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
        Draw.round(g, 235, 11, 779, 36, 10, surface(0xFF111219));
        float hx = 253;
        Draw.text(g, "Hello, ", hx, 29, 13, 0xFFC3C3CC, Draw.REGULAR);
        Draw.text(g, "You", hx + Draw.width("Hello, ", 13, Draw.REGULAR), 29, 13, WHITE, Draw.SEMIBOLD);

        Draw.roundBordered(g, 753, 17, 251, 24, 9, surface(0xFF0B0D12), 0xFF1F2128);
        Draw.icon(g, I_SEARCH, 771, 29, 13, 0xFFA4A6AD);
        Draw.text(g, "Search modules...", 787, 29, 12.5f, 0xFFA4A6AD, Draw.REGULAR);
        Draw.roundBordered(g, 960, 20, 38, 18, 6, 0xFF15171D, 0xFF2E3038);
        Draw.icon(g, I_COMMAND, 973, 29, 10, 0xFFC8C9CE);
        Draw.text(g, "K", 982, 29, 11.5f, 0xFFC8C9CE, Draw.MEDIUM);
        hotspot(753, 17, 251, 24, null);
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

    // ---------------------------------------------------------------- auction (module) page

    private int drawModules(GuiGraphicsExtractor g, int top) {
        return Math.max(drawColumn(g, auctionLeft, 233, top), drawColumn(g, auctionRight, 632, top));
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
        if (m.on) {
            Draw.round(g, x - 2, y - 2, w + 4, h + 4, 12, Draw.alpha(accent, 0.25f));
            Draw.roundBordered(g, x, y, w, h, 10, surface(tinted(CARD, 0.08f)), Draw.lerpColor(accent, 0xFF000000, 0.25f));
        } else {
            Draw.roundBordered(g, x, y, w, h, 10, surface(CARD), CARD_BORDER);
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
            keyIcon(g, Math.round(kx) + 3, y + 38);
        }

        toggle(g, x + w - 47, y + 27, m.on, null);

        if (m.expanded()) {
            Draw.rect(g, x + 12, y + 50, w - 24, 1, 0xFF2B2135);
            int cy = y + 72;
            for (Setting s : m.settings) {
                switch (s.kind()) {
                    case TOGGLE -> {
                        Draw.text(g, s.label(), x + 19, cy, 12.5f, TEXT, Draw.MEDIUM);
                        toggle(g, x + w - 47, cy, s.on(), null);
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
                        Draw.rect(g, x + 12, cy - 9, w - 24, 1, 0xFF2B2135);
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

    // ---------------------------------------------------------------- widgets

    private void toggle(GuiGraphicsExtractor g, int x, int cy, boolean on, Runnable action) {
        if (on) {
            Draw.round(g, x, cy - 8, 32, 16, 8, accent);
            Draw.circle(g, x + 24, cy, 6, 0xFFFFFFFF);
        } else {
            Draw.round(g, x, cy - 8, 32, 16, 8, 0xFF2E3139);
            Draw.circle(g, x + 8, cy, 6, 0xFF9EA2AA);
        }
        hotspot(x - 2, cy - 10, 36, 20, action);
    }

    private void slider(GuiGraphicsExtractor g, int x, int cy, int w, float frac) {
        Draw.round(g, x, cy - 2, w, 4, 2, 0xFF30273C);
        int fw = Math.round(w * frac);
        if (fw > 0) {
            Draw.round(g, x, cy - 2, Math.max(4, fw), 4, 2, Draw.lerpColor(accent, 0xFF000000, 0.2f));
        }
        Draw.circle(g, x + fw, cy, 5, 0xFFFFFFFF);
    }

    private void modeBox(GuiGraphicsExtractor g, int x, int cy, int w, String value) {
        Draw.roundBordered(g, x, cy - 12, w, 25, 6, 0xFF050407, tinted(0xFF141018, 0.15f));
        Draw.icon(g, I_LEFT, x + 11, cy, 12, 0xFF9C9CA6);
        Draw.icon(g, I_RIGHT, x + w - 11, cy, 12, 0xFF9C9CA6);
        Draw.textCentered(g, value, x + w / 2f, cy, 12.5f, WHITE, Draw.MEDIUM);
        hotspot(x, cy - 12, w, 25, null);
    }

    private static void keyIcon(GuiGraphicsExtractor g, int cx, int cy) {
        Draw.roundBordered(g, cx - 4, cy - 5, 9, 10, 2, 0x00000000, GRAY);
        Draw.rect(g, cx - 1, cy - 2, 3, 4, GRAY);
    }

    private void button(GuiGraphicsExtractor g, int x, int y, int w, int h, String label, int fill, int border, int color, Runnable action) {
        Draw.roundBordered(g, x, y, w, h, 6, fill, border);
        Draw.textCentered(g, label, x + w / 2f, y + h / 2f + 0.5f, 12.5f, color, Draw.SEMIBOLD);
        hotspot(x, y, w, h, action);
    }

    private void accentButton(GuiGraphicsExtractor g, int x, int y, int w, int h, String label) {
        button(g, x, y, w, h, label, accentDark, Draw.lerpColor(accent, 0xFF000000, 0.3f), accentLight, null);
    }

    // ---------------------------------------------------------------- settings

    private int drawSettings(GuiGraphicsExtractor g, int t) {
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        int cardFill = surface(tinted(0xFF141119, 0.07f)), cardBorder = tinted(0xFF1E1A24, 0.12f);
        int boxFill = surface(tinted(0xFF0F0D14, 0.04f)), boxBorder = 0xFF2A2433;

        Draw.icon(g, I_SETTINGS, 253, t + 80, 22, head);
        Draw.text(g, "Settings", 272, t + 76, 13.5f, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Configure interface, security, and system behavior.", 272, t + 92, 10.5f, SUBTLE, Draw.REGULAR);

        // Interface
        Draw.roundBordered(g, 235, t + 120, 778, 197, 12, cardFill, cardBorder);
        Draw.icon(g, I_DESKTOP, 261, t + 150, 16, head);
        Draw.text(g, "Interface", 280, t + 150, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "GUI Settings", 250, t + 176, 10.5f, SUBTLE, Draw.REGULAR);
        Draw.roundBordered(g, 250, t + 192, 746, 108, 10, boxFill, boxBorder);
        Draw.text(g, "Menu Bind", 268, t + 223, 12.5f, WHITE, Draw.MEDIUM);
        button(g, 906, t + 206, 74, 29, "RSHIFT", 0xFF131219, BUTTON_BORDER, WHITE, null);
        Draw.rect(g, 258, t + 247, 730, 1, 0xFF221A29);
        Draw.text(g, "Quick Friend", 268, t + 278, 12.5f, WHITE, Draw.MEDIUM);
        Draw.round(g, 940, t + 270, 32, 16, 8, 0xFF0A0C0E);
        Draw.circle(g, 948, t + 278, 7, 0xFFF2F2F4);
        hotspot(938, t + 268, 36, 20, null);

        // Sounds
        Draw.roundBordered(g, 235, t + 330, 778, 232, 12, cardFill, cardBorder);
        Draw.icon(g, I_MUSIC, 260, t + 359, 16, head);
        Draw.text(g, "Sounds", 281, t + 360, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Client Sounds", 250, t + 386, 10.5f, SUBTLE, Draw.REGULAR);
        Draw.roundBordered(g, 250, t + 402, 746, 144, 10, boxFill, boxBorder);
        Draw.text(g, "Module Sound", 268, t + 426, 12.5f, WHITE, Draw.MEDIUM);
        toggle(g, 945, t + 426, LethalConfig.moduleSound, () -> {
            LethalConfig.moduleSound = !LethalConfig.moduleSound;
            LethalConfig.save();
        });
        Draw.text(g, "Sound Pack", 268, t + 466, 12.5f, WHITE, Draw.MEDIUM);
        Draw.roundBordered(g, 864, t + 452, 116, 25, 6, 0xFF131219, BUTTON_BORDER);
        Draw.text(g, "Default", 878, t + 465, 12.5f, WHITE, Draw.MEDIUM);
        Draw.icon(g, I_DOWN, 966, t + 465, 9, 0xFF9C9CA6);
        hotspot(864, t + 452, 116, 25, null);
        Draw.text(g, "Volume", 268, t + 506, 12.5f, WHITE, Draw.MEDIUM);
        Draw.textRight(g, "70%", 857, t + 505, 12, 0xFFC3C3CC, Draw.REGULAR);
        slider(g, 868, t + 504, 112, 0.7f);
        return t + 575;
    }

    // ---------------------------------------------------------------- recent auctions

    private int drawRecent(GuiGraphicsExtractor g, int t) {
        Draw.round(g, 246, t + 66, 754, 26, 7, surface(0xFF0D0F13));
        Draw.round(g, 248, t + 67, 249, 24, 6, accentDark);
        Draw.round(g, 248, t + 67, 249, 12, 6, Draw.lerpColor(accent, 0xFF000000, 0.45f));
        Draw.textCentered(g, "My Configs", 372.5f, t + 79.5f, 12.5f, accentLight, Draw.SEMIBOLD);
        Draw.textCentered(g, "Community", 624, t + 79.5f, 12.5f, 0xFFC9CAD0, Draw.MEDIUM);
        Draw.textCentered(g, "Downloads", 875, t + 79.5f, 12.5f, 0xFFC9CAD0, Draw.MEDIUM);
        hotspot(248, t + 67, 249, 24, null);
        hotspot(500, t + 67, 249, 24, null);
        hotspot(751, t + 67, 249, 24, null);

        button(g, 247, t + 103, 88, 23, "Autosave", BUTTON, 0xFF3A3344, WHITE, null);
        Draw.text(g, "Active", 348, t + 115, 12.5f, SUBTLE, Draw.REGULAR);
        Draw.text(g, "test", 348 + Draw.width("Active ", 12.5f, Draw.REGULAR), t + 115, 12.5f, WHITE, Draw.SEMIBOLD);
        accentButton(g, 770, t + 103, 72, 23, "Create");
        button(g, 849, t + 103, 72, 23, "Import", BUTTON, BUTTON_BORDER, WHITE, null);
        button(g, 928, t + 103, 72, 23, "Redeem", BUTTON, BUTTON_BORDER, WHITE, null);

        Draw.roundBordered(g, 242, t + 135, 763, 47, 8, surface(tinted(0xFF26242C, 0.12f)), tinted(0xFF34303C, 0.2f));
        Draw.round(g, 243, t + 142, 3, 32, 1, accent);
        Draw.text(g, "test", 258, t + 154, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "114 modules", 258, t + 171, 12, SUBTLE, Draw.REGULAR);
        button(g, 736, t + 148, 73, 23, "Publish", BUTTON, 0xFF44424E, WHITE, null);
        button(g, 816, t + 148, 68, 23, "Share", 0xFF2E2B35, 0xFF4A4654, Draw.lerpColor(accentLight, 0xFF808080, 0.4f), null);
        accentButton(g, 891, t + 148, 74, 23, "Apply");
        Draw.roundBordered(g, 972, t + 148, 26, 23, 6, BUTTON, 0xFF44424E);
        Draw.icon(g, I_X, 985, t + 159.5f, 11, WHITE);
        hotspot(972, t + 148, 26, 23, null);
        return t + 190;
    }

    // ---------------------------------------------------------------- theme

    private int drawTheme(GuiGraphicsExtractor g, int t) {
        int[] cx = {249, 436, 623, 810};
        for (int i = 0; i < PRESET_NAMES.length; i++) {
            int x = cx[i % 4];
            int y = t + 60 + (i / 4) * 69;
            boolean selected = LethalConfig.preset == i;
            if (selected) {
                Draw.round(g, x - 2, y - 2, 188, 64, 8, Draw.alpha(accent, 0.3f));
                Draw.roundBordered(g, x, y, 184, 60, 6, surface(0xFF24222C), accent);
            } else {
                Draw.round(g, x, y, 184, 60, 6, surface(0xFF24222C));
            }
            Draw.text(g, PRESET_NAMES[i], x + 6, y + 13, 12, 0xFFE0DFE6, selected ? Draw.SEMIBOLD : Draw.REGULAR);
            if (selected) {
                Draw.textRight(g, "Active", x + 178, y + 13, 10.5f, accentLight, Draw.MEDIUM);
            }
            int sx = x + 3, sw = 178, sy = y + 26, sh = 32;
            int seg = Math.round(sw * 0.25f);
            int ox = sx;
            for (int k = 0; k < 4; k++) {
                int w = k < 3 ? seg : sx + sw - ox;
                Draw.rect(g, ox, sy, w, sh, 0xFF000000 | PRESET_COLORS[i][k]);
                ox += w;
            }
            int index = i;
            hotspot(x, y, 184, 60, () -> {
                LethalConfig.preset = LethalConfig.preset == index ? -1 : index;
                LethalConfig.save();
            });
        }

        // Effects
        int a = t + 211;
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        Draw.roundBordered(g, 233, a, 780, 239, 10, surface(tinted(0xFF15121B, 0.08f)), tinted(0xFF221E2A, 0.14f));
        Draw.icon(g, I_SPARKLES, 256, a + 25, 14, head);
        Draw.text(g, "EFFECTS", 276, a + 25, 12.5f, 0xFFCDC3D6, Draw.MEDIUM);
        button(g, 915, a + 13, 82, 24, "Reset", BUTTON, BUTTON_BORDER, WHITE, () -> {
            LethalConfig.resetTheme();
            LethalConfig.save();
        });

        String[][] fx = {{"See-Through GUI", "Allow background blur and transparency."},
                {"Frosted Blur", "Apply frosted glass blur to surfaces."},
                {"Ambient Background", "Enable the ambient gradient background."}};
        boolean[] state = {LethalConfig.seeThrough, LethalConfig.frostedBlur, LethalConfig.ambientBackground};
        for (int i = 0; i < 3; i++) {
            int ry = a + 69 + i * 57;
            Draw.text(g, fx[i][0], 252, ry, 12.5f, WHITE, Draw.SEMIBOLD);
            Draw.text(g, fx[i][1], 252, ry + 16, 10, SUBTLE, Draw.REGULAR);
            int which = i;
            toggle(g, 942, ry + 8, state[i], () -> {
                switch (which) {
                    case 0 -> LethalConfig.seeThrough = !LethalConfig.seeThrough;
                    case 1 -> LethalConfig.frostedBlur = !LethalConfig.frostedBlur;
                    default -> LethalConfig.ambientBackground = !LethalConfig.ambientBackground;
                }
                LethalConfig.save();
            });
            Draw.icon(g, I_RESET, 989, ry + 8, 10, 0xFF8F8597);
            boolean def = which != 0;
            hotspot(982, ry + 1, 14, 14, () -> {
                switch (which) {
                    case 0 -> LethalConfig.seeThrough = def;
                    case 1 -> LethalConfig.frostedBlur = def;
                    default -> LethalConfig.ambientBackground = def;
                }
                LethalConfig.save();
            });
        }
        return a + 255;
    }

    // ---------------------------------------------------------------- input

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return true;
        }
        layout();
        float x = toDesignX(event.x()), y = toDesignY(event.y());
        for (Hotspot h : List.copyOf(hotspots)) {
            if (h.contains(x, y)) {
                if (h.action() != null) {
                    h.action().run();
                }
                LethalAuctionClient.playClick();
                return true;
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
