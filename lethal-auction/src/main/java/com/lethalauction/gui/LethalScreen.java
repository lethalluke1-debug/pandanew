package com.lethalauction.gui;

import com.lethalauction.LethalAuctionClient;
import com.lethalauction.LethalConfig;
import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Auctions.Auction;
import com.lethalauction.cape.AuctionCape;
import com.lethalauction.cape.CapeImages;
import com.lethalauction.cape.Capes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Util;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

/**
 * The Lethal Auction menu. Everything is laid out in a fixed 1036x518 "design space" that is scaled to
 * the window, so it keeps the same proportions at every GUI scale. The sidebar tabs, theme presets, theme
 * effects, the module sound toggle and the auction pages work; other buttons only play the click sound.
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

    // Icon code points (Tabler Icons)
    static final int I_GAVEL = 0xef90, I_HISTORY = 0xebea, I_SETTINGS = 0xeb20, I_PALETTE = 0xeb01,
            I_SEARCH = 0xeb1c, I_LEFT = 0xea60, I_RIGHT = 0xea61, I_DOWN = 0xea5f, I_DESKTOP = 0xea89,
            I_MUSIC = 0xeafc, I_X = 0xeb55, I_RESET = 0xeb15, I_SPARKLES = 0xf6d7, I_COMMAND = 0xea78,
            I_HANGER = 0xee9a, I_UPLOAD = 0xeb47, I_FOLDER = 0xeaad, I_TRASH = 0xeb41;

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
        RECENT("Recent Auctions", I_HISTORY, 229), THEME("Theme", I_PALETTE, 259), CAPE("Cape", I_HANGER, 289),
        SCOREBOARD("Scoreboard", 0xef11, 319);

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

    private static Page lastPage = savedPage();

    private Page page = lastPage;
    private final float[] scrollTarget = new float[Page.values().length];
    private final float[] scroll = new float[Page.values().length];
    private final float[] maxScroll = new float[Page.values().length];
    private final List<Hotspot> hotspots = new ArrayList<>();
    private boolean clipping;

    private float scale = 1, originX, originY;
    /** When the current tab was opened; drives the top-to-bottom reveal. */
    private long pageShownAt = System.currentTimeMillis();
    private static final float REVEAL_MILLIS = 320f;

    // Theme colours, refreshed every frame from LethalConfig
    private int accent, accentDark, accentLight, tint;
    private float surfaceAlpha;

    public LethalScreen() {
        super(Component.literal("Lethal Auction"));
        minBid.value = LethalConfig.formMinBid;
        worth.value = LethalConfig.formWorth;
        note.value = LethalConfig.formNote;
    }

    private static Page savedPage() {
        for (Page p : Page.values()) {
            if (p.name().equals(LethalConfig.lastPage)) {
                return p;
            }
        }
        return Page.AUCTION;
    }

    /** Remembers the open tab and the auction form values for next time. */
    private void saveState() {
        LethalConfig.lastPage = page.name();
        LethalConfig.formMinBid = minBid.value;
        LethalConfig.formQuantity = quantity.value.isEmpty() ? "1" : quantity.value;
        LethalConfig.formTimer = timer.value.isEmpty() ? "60" : timer.value;
        LethalConfig.formWorth = worth.value;
        LethalConfig.formNote = note.value;
        LethalConfig.save();
    }

    @Override
    public void removed() {
        saveState();
        super.removed();
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
        accent = Theme.accent();
        tint = Theme.tint();
        accentDark = Theme.accentDark();
        accentLight = Theme.accentLight();
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
        rawMouseX = mouseX;
        rawMouseY = mouseY;
        mouseDX = toDesignX(mouseX);
        mouseDY = toDesignY(mouseY);
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

        // Reveal the tab from top to bottom when it is opened.
        float k = Math.min(1f, (System.currentTimeMillis() - pageShownAt) / REVEAL_MILLIS);
        float ease = 1 - (float) Math.pow(1 - k, 3);
        int revealBottom = VIEW_Y + Math.round((VIEW_B - VIEW_Y) * ease);
        g.enableScissor(VIEW_X, VIEW_Y, VIEW_R, Math.max(VIEW_Y + 1, revealBottom));
        Draw.globalAlpha = 0.35f + 0.65f * ease;
        clipping = true;
        int top = Math.round(-scroll[pi] - (1 - ease) * 12);
        int contentBottom = switch (page) {
            case AUCTION -> drawNewAuction(g, top);
            case SETTINGS -> drawSettings(g, top);
            case RECENT -> drawRecent(g, top);
            case THEME -> drawTheme(g, top);
            case CAPE -> drawCape(g, top);
            case SCOREBOARD -> drawScoreboard(g, top);
        };
        clipping = false;
        Draw.globalAlpha = 1f;
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
        Draw.round(g, 24, 25, 40, 40, 12, Draw.alpha(accent, 0.22f));
        Draw.round(g, 26, 27, 36, 36, 10, accentDark);
        Draw.round(g, 26, 27, 36, 27, 10, Draw.lerpColor(accent, accentDark, 0.25f));
        Draw.round(g, 26, 27, 36, 18, 10, accent);
        Draw.icon(g, I_GAVEL, 44, 45, 20, 0xFFFFFFFF);
        Draw.text(g, "LETHAL", 74, 45, 12, 0xFFEDE6F2, Draw.LOGO);

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
                if (page != target) {
                    pageShownAt = System.currentTimeMillis();
                }
                page = target;
                lastPage = target;
                LethalConfig.lastPage = target.name();
            });
        }

        Draw.rect(g, 26, 439, 178, 1, 0x66FFFFFF & tinted(0xFF2A2A30, 0.3f));

        // user card
        Draw.roundBordered(g, 20, 449, 192, 50, 10, surface(0xFF0F1319), 0xFF241A2A);
        PlayerFaceExtractor.extractRenderState(g, skin(), 31, 459, 28);
        Draw.text(g, fit(username(), 100, 13, Draw.SEMIBOLD), 75, 465, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Lifetime", 75, 483, 11.5f, MUTED, Draw.REGULAR);
        Draw.circle(g, 191, 473, 7, 0x3343CC7F);
        Draw.circle(g, 191, 473, 4.5f, 0x7743CC7F);
        Draw.circle(g, 191, 473, 3.5f, 0xFF43CC7F);
    }

    private static String username() {
        return Minecraft.getInstance().getUser().getName();
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
        Draw.text(g, username(), hx + Draw.width("Hello, ", 13, Draw.REGULAR), 29, 13, WHITE, Draw.SEMIBOLD);

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

    // ---------------------------------------------------------------- new auction page

    private static final int GRID_X = 247, GRID_Y = 128, GRID_W = 381, GRID_H = 369;
    private static final int CELL = 38, PITCH = 41, COLS = 9;

    private final TextBox search = TextBox.any(40);
    private final TextBox minBid = TextBox.amount(12);
    private final TextBox quantity = TextBox.digits(LethalConfig.formQuantity, 5);
    private final TextBox timer = TextBox.digits(LethalConfig.formTimer, 5);
    private final TextBox worth = TextBox.amount(12);
    private final TextBox note = TextBox.any(32);
    private final List<TextBox> fieldOrder = List.of(search, minBid, quantity, timer, worth, note);
    private TextBox focused;
    private List<ItemStack> allItems = List.of(), filtered = List.of();
    private String filteredFor;
    private ItemStack selected = ItemStack.EMPTY;
    private float gridScroll, gridScrollTarget, gridMaxScroll;
    private float mouseDX, mouseDY;
    private int rawMouseX, rawMouseY;
    private long startedMessageUntil, cancelledMessageUntil;

    private List<ItemStack> items() {
        if (allItems.isEmpty()) {
            List<ItemStack> list = new ArrayList<>();
            try {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item != Items.AIR) {
                        list.add(new ItemStack(item));
                    }
                }
            } catch (RuntimeException e) {
                // Item data is not ready while the game is still loading; try again next frame.
                return List.of();
            }
            allItems = list;
            filteredFor = null;
        }
        String q = search.value.trim().toLowerCase(Locale.ROOT);
        if (!q.equals(filteredFor)) {
            filteredFor = q;
            if (q.isEmpty()) {
                filtered = allItems;
            } else {
                String idQuery = q.replace(' ', '_');
                List<ItemStack> list = new ArrayList<>();
                for (ItemStack stack : allItems) {
                    if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(q)
                            || BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains(idQuery)) {
                        list.add(stack);
                    }
                }
                filtered = list;
            }
            gridScroll = gridScrollTarget = 0;
        }
        return filtered;
    }

    private boolean overGrid(float x, float y) {
        return x >= GRID_X && x < GRID_X + GRID_W && y >= GRID_Y && y < GRID_Y + GRID_H;
    }

    private int drawNewAuction(GuiGraphicsExtractor g, int t) {
        int cardFill = surface(tinted(0xFF141119, 0.07f)), cardBorder = tinted(0xFF1E1A24, 0.12f);
        List<ItemStack> list = items();

        // ---- left: item picker
        Draw.roundBordered(g, 235, 58, 405, 447, 12, cardFill, cardBorder);
        Draw.circle(g, 254, 76, 3.5f, accent);
        Draw.text(g, "New Auction", 263, 76, 13.5f, WHITE, Draw.SEMIBOLD);
        Draw.textRight(g, list.size() + (list.size() == 1 ? " item" : " items"), 627, 76, 12, SUBTLE, Draw.REGULAR);
        field(g, search, 247, 92, 381, 28, "Search items...");

        Draw.roundBordered(g, GRID_X, GRID_Y, GRID_W, GRID_H, 10, surface(0xFF0C0B11), 0xFF221E2A);
        int x0 = GRID_X + 8, y0 = GRID_Y + 8, viewH = GRID_H - 16;
        int rows = (list.size() + COLS - 1) / COLS;
        int contentH = Math.max(0, rows * PITCH - (PITCH - CELL));
        gridMaxScroll = Math.max(0, contentH - viewH);
        gridScrollTarget = Math.max(0, Math.min(gridMaxScroll, gridScrollTarget));
        gridScroll += (gridScrollTarget - gridScroll) * 0.35f;
        if (Math.abs(gridScrollTarget - gridScroll) < 0.3f) {
            gridScroll = gridScrollTarget;
        }
        int scrollPx = Math.round(gridScroll);
        int gridTop = GRID_Y + 2, gridBottom = GRID_Y + GRID_H - 2;

        g.enableScissor(GRID_X + 1, gridTop, GRID_X + GRID_W - 1, gridBottom);
        int firstRow = Math.max(0, (scrollPx - 8) / PITCH);
        int lastRow = Math.min(rows - 1, (scrollPx + viewH + 8) / PITCH + 1);
        ItemStack hovered = ItemStack.EMPTY;
        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = 0; col < COLS; col++) {
                int i = row * COLS + col;
                if (i >= list.size()) {
                    break;
                }
                ItemStack stack = list.get(i);
                int cx = x0 + col * PITCH, cy = y0 + row * PITCH - scrollPx;
                boolean isSelected = !selected.isEmpty() && selected.getItem() == stack.getItem();
                boolean hover = overGrid(mouseDX, mouseDY) && mouseDX >= cx && mouseDX < cx + CELL
                        && mouseDY >= cy && mouseDY < cy + CELL && mouseDY >= gridTop && mouseDY < gridBottom;
                if (isSelected) {
                    Draw.roundBordered(g, cx, cy, CELL, CELL, 7, tinted(0xFF16151C, 0.25f), accent);
                } else {
                    Draw.round(g, cx, cy, CELL, CELL, 7, hover ? 0xFF24222C : 0xFF16151C);
                }
                if (hover) {
                    hovered = stack;
                }
                Matrix3x2fStack pose = g.pose();
                pose.pushMatrix();
                pose.translate(cx + 7, cy + 7);
                pose.scale(1.5f);
                g.item(stack, 0, 0);
                pose.popMatrix();

                int top = Math.max(cy, gridTop), bottom = Math.min(cy + CELL, gridBottom);
                if (bottom > top) {
                    hotspot(cx, top, CELL, bottom - top, () -> selected = stack);
                }
            }
        }
        g.disableScissor();
        if (list.isEmpty()) {
            Draw.textCentered(g, "No items match your search", GRID_X + GRID_W / 2f, GRID_Y + 40, 12.5f, SUBTLE, Draw.REGULAR);
        }
        if (gridMaxScroll > 0) {
            float thumbH = Math.max(24, viewH * (float) viewH / contentH);
            int ty = Math.round(GRID_Y + 8 + (viewH - thumbH) * (gridScroll / gridMaxScroll));
            Draw.round(g, GRID_X + GRID_W - 6, ty, 3, Math.round(thumbH), 1, Draw.alpha(accent, 0.75f));
        }
        if (!hovered.isEmpty()) {
            g.setTooltipForNextFrame(font, hovered.getHoverName(), rawMouseX, rawMouseY);
        }

        // ---- right: auction details
        Draw.roundBordered(g, 652, 58, 361, 82, 12, cardFill, cardBorder);
        Draw.round(g, 664, 69, 60, 60, 10, surface(0xFF0C0B11));
        if (selected.isEmpty()) {
            Draw.text(g, "No item yet", 738, 88, 13, WHITE, Draw.SEMIBOLD);
            Draw.text(g, "Pick one on the left", 738, 108, 12, SUBTLE, Draw.REGULAR);
        } else {
            Matrix3x2fStack pose = g.pose();
            pose.pushMatrix();
            pose.translate(674, 79);
            pose.scale(2.5f);
            g.item(selected, 0, 0);
            pose.popMatrix();
            Draw.text(g, fit(selected.getHoverName().getString(), 260, 14, Draw.SEMIBOLD), 738, 99, 14, WHITE, Draw.SEMIBOLD);
        }

        Draw.text(g, "Minimum bid", 664, 162, 12.5f, TEXT, Draw.MEDIUM);
        field(g, minBid, 652, 172, 361, 32, "e.g. 10k");
        Draw.text(g, "Quantity", 664, 222, 12.5f, TEXT, Draw.MEDIUM);
        field(g, quantity, 652, 232, 174, 32, "1");
        Draw.text(g, "Timer (sec)", 851, 222, 12.5f, TEXT, Draw.MEDIUM);
        field(g, timer, 839, 232, 174, 32, "60");
        Draw.text(g, "Worth of one item", 664, 282, 12.5f, TEXT, Draw.MEDIUM);
        field(g, worth, 652, 292, 361, 32, "Optional");
        Draw.text(g, "Note on the auction card", 664, 342, 12.5f, TEXT, Draw.MEDIUM);
        field(g, note, 652, 352, 361, 32, "e.g. Pay over = I keep");

        long bid = Auctions.parseAmount(minBid.value);
        int qty = parseInt(quantity.value);
        int secs = parseInt(timer.value);
        long each = worth.value.isBlank() ? 0 : Auctions.parseAmount(worth.value);
        boolean ready = !selected.isEmpty() && bid > 0 && qty > 0 && secs > 0 && each >= 0;

        String hint = null;
        int hintColor = SUBTLE;
        if (System.currentTimeMillis() < startedMessageUntil) {
            hint = "Auction started! See Recent Auctions.";
            hintColor = accentLight;
        } else if (System.currentTimeMillis() < cancelledMessageUntil) {
            hint = "Auction cancelled.";
            hintColor = 0xFFFF8A95;
        } else if (!selected.isEmpty() && !minBid.value.isEmpty() && bid <= 0) {
            hint = "Enter a valid minimum bid (e.g. 2500 or 2.5k).";
        } else if (ready && each > 0) {
            hint = "Total worth: " + Auctions.formatMoney(each * qty);
        }
        if (hint != null) {
            Draw.textCentered(g, hint, 832.5f, 403, 12, hintColor, Draw.MEDIUM);
        }

        if (ready) {
            button(g, 652, 420, 361, 32, "Start Auction", accentDark, Draw.lerpColor(accent, 0xFF000000, 0.15f), accentLight, () -> {
                Auctions.start(selected, qty, bid, each, secs, note.value.trim());
                saveState();
                startedMessageUntil = System.currentTimeMillis() + 3000;
                resetForm();
            });
        } else {
            Draw.roundBordered(g, 652, 420, 361, 32, 6, surface(0xFF15141B), 0xFF23212A);
            Draw.textCentered(g, "Start Auction", 832.5f, 436.5f, 12.5f, 0xFF5E5C66, Draw.SEMIBOLD);
        }
        Auction running = Auctions.running();
        if (running != null) {
            button(g, 652, 462, 361, 32, "Cancel Auction", surface(0xFF2A0F14), 0xFF7A2230, 0xFFFF8A95, () -> {
                running.cancel();
                cancelledMessageUntil = System.currentTimeMillis() + 3000;
                startedMessageUntil = 0;
            });
        } else {
            button(g, 652, 462, 361, 32, "Cancel", surface(0xFF15141B), BUTTON_BORDER, WHITE, this::resetForm);
        }
        return t + 452;
    }

    private void resetForm() {
        selected = ItemStack.EMPTY;
        minBid.value = "";
        quantity.value = "1";
        timer.value = "60";
        worth.value = "";
        focused = null;
    }

    private static int parseInt(String s) {
        try {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Shortens text with "..." so it fits in maxWidth. */
    private static String fit(String s, float maxWidth, float size, Identifier font) {
        if (Draw.width(s, size, font) <= maxWidth) {
            return s;
        }
        while (s.length() > 1 && Draw.width(s + "...", size, font) > maxWidth) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "...";
    }

    private void field(GuiGraphicsExtractor g, TextBox box, int x, int y, int w, int h, String placeholder) {
        boolean isFocused = focused == box;
        Draw.roundBordered(g, x, y, w, h, 8, surface(0xFF0C0B11), isFocused ? accent : 0xFF2A2633);
        float cy = y + h / 2f;
        float maxW = w - 26;
        if (box.value.isEmpty() && !isFocused) {
            Draw.text(g, placeholder, x + 12, cy, 13, 0xFF5E5C66, Draw.MEDIUM);
        } else {
            String shown = box.value;
            while (!shown.isEmpty() && Draw.width(shown, 13, Draw.MEDIUM) > maxW) {
                shown = shown.substring(1);
            }
            if (isFocused && box.allSelected && !shown.isEmpty()) {
                Draw.round(g, x + 10, Math.round(cy - 9), Math.round(Draw.width(shown, 13, Draw.MEDIUM) + 4), 18, 3,
                        Draw.alpha(accent, 0.45f));
            }
            Draw.text(g, shown, x + 12, cy, 13, WHITE, Draw.MEDIUM);
            if (isFocused && !box.allSelected && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cx = Math.round(x + 13 + Draw.width(shown, 13, Draw.MEDIUM));
                Draw.rect(g, cx, Math.round(cy - 7), 1, 14, WHITE);
            }
        }
        hotspot(x, y, w, h, () -> {
            focused = box;
            box.allSelected = false;
        });
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
        Draw.roundBordered(g, 235, t + 120, 778, 145, 12, cardFill, cardBorder);
        Draw.icon(g, I_DESKTOP, 261, t + 150, 16, head);
        Draw.text(g, "Interface", 280, t + 150, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "GUI Settings", 250, t + 176, 10.5f, SUBTLE, Draw.REGULAR);
        Draw.roundBordered(g, 250, t + 192, 746, 56, 10, boxFill, boxBorder);
        Draw.text(g, "Menu Bind", 268, t + 220, 12.5f, WHITE, Draw.MEDIUM);
        button(g, 906, t + 206, 74, 29, "RSHIFT", 0xFF131219, BUTTON_BORDER, WHITE, null);

        // Sounds
        int s0 = t + 278;
        Draw.roundBordered(g, 235, s0, 778, 184, 12, cardFill, cardBorder);
        Draw.icon(g, I_MUSIC, 260, s0 + 29, 16, head);
        Draw.text(g, "Sounds", 281, s0 + 30, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Client Sounds", 250, s0 + 56, 10.5f, SUBTLE, Draw.REGULAR);
        Draw.roundBordered(g, 250, s0 + 72, 746, 96, 10, boxFill, boxBorder);
        Draw.text(g, "Module Sound", 268, s0 + 96, 12.5f, WHITE, Draw.MEDIUM);
        toggle(g, 945, s0 + 96, LethalConfig.moduleSound, () -> {
            LethalConfig.moduleSound = !LethalConfig.moduleSound;
            LethalConfig.save();
        });
        Draw.rect(g, 258, s0 + 119, 730, 1, 0xFF221A29);
        Draw.text(g, "Volume", 268, s0 + 143, 12.5f, WHITE, Draw.MEDIUM);
        Draw.textRight(g, Math.round(LethalConfig.volume * 100) + "%", 822, s0 + 142, 12, 0xFFC3C3CC, Draw.MEDIUM);
        volumeSliderY = s0 + 142;
        slider(g, VOLUME_X, volumeSliderY, VOLUME_W, LethalConfig.volume);
        hotspot(VOLUME_X - 6, volumeSliderY - 9, VOLUME_W + 12, 18, () -> {
            draggingVolume = true;
            setVolumeFromMouse(mouseDX);
        });
        return t + 475;
    }

    private static final int VOLUME_X = 836, VOLUME_W = 144;
    private int volumeSliderY;
    private boolean draggingVolume;

    private void setVolumeFromMouse(float designX) {
        LethalConfig.volume = Math.round(Math.max(0f, Math.min(1f, (designX - VOLUME_X) / VOLUME_W)) * 100) / 100f;
    }

    // ---------------------------------------------------------------- recent auctions

    private int drawRecent(GuiGraphicsExtractor g, int t) {
        List<Auction> recent = Auctions.recent();
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        Draw.icon(g, I_HISTORY, 253, t + 80, 17, head);
        Draw.text(g, "Recent Auctions", 272, t + 77, 13, WHITE, Draw.SEMIBOLD);
        int bx = Math.round(272 + Draw.width("Recent Auctions", 13, Draw.SEMIBOLD) + 10);
        String count = Integer.toString(recent.size());
        int bw = Math.max(26, Math.round(Draw.width(count, 11.5f, Draw.SEMIBOLD) + 16));
        Draw.round(g, bx, t + 66, bw, 18, 9, accentDark);
        Draw.textCentered(g, count, bx + bw / 2f, t + 75, 11.5f, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Your auction history. Press Redo to run one again.", 272, t + 94, 10.5f, SUBTLE, Draw.REGULAR);
        button(g, 892, t + 69, 108, 26, "Clear History", BUTTON, BUTTON_BORDER, recent.isEmpty() ? 0xFF6E6A78 : WHITE,
                recent.isEmpty() ? null : Auctions::clear);

        if (recent.isEmpty()) {
            Draw.roundBordered(g, 241, t + 105, 765, 135, 8, surface(0xFF0F1319), 0xFF201A2C);
            Draw.icon(g, I_GAVEL, 623, t + 132, 22, 0xFF3A3E46);
            Draw.textCentered(g, "No auctions yet", 623, t + 162, 12.5f, WHITE, Draw.SEMIBOLD);
            Draw.textCentered(g, "Pick an item on the Auction tab and press Start Auction.", 623, t + 179, 12.5f, 0xFFC3C5CB, Draw.REGULAR);
            button(g, 568, t + 199, 111, 27, "New Auction", 0xFF14151B, 0xFF2C2E35, WHITE, () -> {
                pageShownAt = System.currentTimeMillis();
                page = Page.AUCTION;
                lastPage = Page.AUCTION;
            });
            return t + 250;
        }

        int y = t + 108;
        for (Auction a : recent) {
            boolean live = !a.ended();
            Draw.roundBordered(g, 241, y, 765, 54, 10, surface(CARD), CARD_BORDER);
            if (live) {
                Draw.round(g, 242, y + 11, 3, 32, 1, accent);
            }
            Draw.round(g, 253, y + 9, 36, 36, 8, surface(0xFF0C0B11));
            Matrix3x2fStack pose = g.pose();
            pose.pushMatrix();
            pose.translate(259, y + 15);
            pose.scale(1.5f);
            g.item(a.item(), 0, 0);
            pose.popMatrix();

            String name = fit(a.item().getHoverName().getString(), 200, 13, Draw.SEMIBOLD);
            Draw.text(g, name, 301, y + 20, 13, WHITE, Draw.SEMIBOLD);
            String sub = "x" + a.quantity() + "  ·  " + (a.hasBid()
                    ? (live ? "top bid by " : "won by ") + a.topBidder() : a.timerSeconds() + "s timer");
            Draw.text(g, fit(sub, 205, 11.5f, Draw.REGULAR), 301, y + 37, 11.5f, SUBTLE, Draw.REGULAR);

            stat(g, 515, y, "MIN BID", a.minimumBid() > 0 ? Auctions.formatMoney(a.minimumBid()) : "None");
            stat(g, 612, y, "TOP BID", a.hasBid() ? Auctions.formatMoney(a.topBid()) : "-");
            stat(g, 709, y, "WORTH EACH", a.worthEach() > 0 ? Auctions.formatMoney(a.worthEach()) : "-");

            if (live) {
                Draw.roundBordered(g, 806, y + 15, 74, 24, 6, surface(0xFF15141B), 0xFF23212A);
                Draw.textCentered(g, "Redo", 843, y + 27.5f, 12, 0xFF5E5C66, Draw.SEMIBOLD);
            } else {
                Auction again = a;
                boolean busy = Auctions.running() != null;
                if (busy) {
                    Draw.roundBordered(g, 806, y + 15, 74, 24, 6, surface(0xFF15141B), 0xFF23212A);
                    Draw.textCentered(g, "Redo", 843, y + 27.5f, 12, 0xFF5E5C66, Draw.SEMIBOLD);
                } else {
                    button(g, 806, y + 15, 74, 24, "Redo", accentDark, Draw.lerpColor(accent, 0xFF000000, 0.15f), accentLight,
                            () -> Auctions.redo(again));
                }
            }

            if (live) {
                Draw.round(g, 888, y + 15, 76, 24, 12, accentDark);
                Draw.textCentered(g, Auctions.formatTime(a.secondsLeft()), 926, y + 27.5f, 12, accentLight, Draw.SEMIBOLD);
            } else if (a.cancelled()) {
                Draw.round(g, 888, y + 15, 76, 24, 12, 0xFF2A0F14);
                Draw.textCentered(g, "Cancelled", 926, y + 27.5f, 11.5f, 0xFFFF8A95, Draw.SEMIBOLD);
            } else if (a.hasBid()) {
                Draw.round(g, 888, y + 15, 76, 24, 12, 0xFF123524);
                Draw.textCentered(g, "Sold", 926, y + 27.5f, 12, 0xFF45E08A, Draw.SEMIBOLD);
            } else {
                Draw.round(g, 888, y + 15, 76, 24, 12, 0xFF24222C);
                Draw.textCentered(g, "Ended", 926, y + 27.5f, 12, 0xFF9C9CA6, Draw.SEMIBOLD);
            }
            Draw.roundBordered(g, 972, y + 15, 24, 24, 6, BUTTON, 0xFF44424E);
            Draw.icon(g, I_X, 984, y + 27, 11, WHITE);
            Auction target = a;
            hotspot(972, y + 15, 24, 24, () -> Auctions.remove(target));
            y += 62;
        }
        return y;
    }

    private void stat(GuiGraphicsExtractor g, int x, int y, String label, String value) {
        Draw.text(g, label, x, y + 19, 9.5f, 0xFF8C8495, Draw.MEDIUM);
        Draw.text(g, value, x, y + 36, 13, WHITE, Draw.SEMIBOLD);
    }

    // ---------------------------------------------------------------- cape

    private static final int I_PENCIL = 0xeb04;
    private static final int CROP_BOX_X = 241, CROP_BOX_W = 500, CROP_BOX_H = 360;
    private Capes.Cape renaming;
    private final TextBox renameBox = TextBox.any(24);
    private final TextBox capeName = TextBox.any(24);
    private Capes.Pending cropFor;
    private boolean draggingCrop;
    private float dragStartMouseX, dragStartMouseY;
    private int dragStartCropX, dragStartCropY;
    private float cropScale = 1;
    private int cropTop;

    private int drawCape(GuiGraphicsExtractor g, int t) {
        Capes.Pending pending = Capes.pending();
        if (pending != null) {
            return drawCropper(g, t, pending);
        }
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        Draw.icon(g, I_HANGER, 253, t + 80, 18, head);
        Draw.text(g, "Cape", 272, t + 77, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Only you can see this cape. It replaces the cape you are wearing.", 272, t + 94, 10.5f, SUBTLE, Draw.REGULAR);
        List<Capes.Cape> capes = Capes.all();
        boolean full = capes.size() >= Capes.MAX_SLOTS;
        if (full) {
            Draw.roundBordered(g, 778, t + 69, 120, 26, 6, surface(0xFF15141B), 0xFF23212A);
            Draw.textCentered(g, "Slots full", 838, t + 82.5f, 12.5f, 0xFF5E5C66, Draw.SEMIBOLD);
        } else {
            button(g, 778, t + 69, 120, 26, "Upload Cape", accentDark, Draw.lerpColor(accent, 0xFF000000, 0.15f), accentLight, Capes::upload);
        }
        button(g, 904, t + 69, 100, 26, "Open Folder", BUTTON, BUTTON_BORDER, WHITE, () -> {
            Capes.rescan();
            Util.getPlatform().openPath(Capes.FOLDER);
        });
        String status = Capes.status();
        if (!status.isEmpty()) {
            Draw.textRight(g, status, 1004, t + 108, 11, accentLight, Draw.MEDIUM);
        }

        // None + 5 slots
        Capes.Cape current = Capes.selected();
        int y = t + 120;
        for (int i = 0; i <= Capes.MAX_SLOTS; i++) {
            int x = 233 + i * 131;
            if (i == 0) {
                capeCard(g, x, y, null, current == null);
            } else if (i - 1 < capes.size()) {
                Capes.Cape cape = capes.get(i - 1);
                capeCard(g, x, y, cape, current != null && current.name().equals(cape.name()));
            } else {
                emptySlot(g, x, y);
            }
        }

        // dotted divider
        int dy = y + 194;
        for (int x = 241; x < 1005; x += 10) {
            Draw.rect(g, x, dy, 5, 1, 0x40FFFFFF);
        }
        return drawAuctionCapeSection(g, dy + 22);
    }

    private void capeCard(GuiGraphicsExtractor g, int x, int y, Capes.Cape cape, boolean selected) {
        int w = 122, h = 178;
        if (selected) {
            Draw.round(g, x - 3, y - 3, w + 6, h + 6, 10, Draw.alpha(accent, 0.18f));
            Draw.roundBordered(g, x - 1, y - 1, w + 2, h + 2, 9, 0x00000000, accent);
            Draw.roundBordered(g, x, y, w, h, 8, surface(0xFF1B1A22), Draw.alpha(accent, 0.6f));
        } else {
            Draw.round(g, x, y, w, h, 8, surface(0xFF1B1A22));
        }
        Draw.round(g, x + 6, y + 6, w - 12, 110, 6, surface(0xFF0C0B11));
        int pcx = x + w / 2, pcy = y + 61;
        if (cape == null) {
            Draw.icon(g, I_X, pcx, pcy, 30, 0xFF4A4654);
        } else {
            capePreview(g, cape.texture(), cape.width(), cape.height(), pcx, pcy, 36, 58);
        }
        if (selected) {
            Draw.circle(g, x + w - 15, y + 15, 6, accent);
            Draw.circle(g, x + w - 15, y + 15, 4.8f, 0xFF0B0E14);
            Draw.circle(g, x + w - 15, y + 15, 3.6f, accent);
        }
        int nameColor = selected ? Draw.lerpColor(accent, 0xFFFFFFFF, 0.15f) : 0xFFE0DFE6;
        if (cape == null) {
            Draw.textCentered(g, "Normal cape", pcx, y + 134, 12, nameColor, Draw.SEMIBOLD);
            Draw.textCentered(g, "Your real cape", pcx, y + 154, 10.5f, SUBTLE, Draw.REGULAR);
            hotspot(x, y, w, h, () -> Capes.select(""));
            return;
        }
        if (renaming == cape) {
            field(g, renameBox, x + 6, y + 122, w - 12, 24, "Name");
            button(g, x + 6, y + 150, w - 12, 22, "Save", accentDark, Draw.lerpColor(accent, 0xFF000000, 0.15f), accentLight, this::commitRename);
            return;
        }
        String label = fit(cape.name().replaceAll("(?i)\\.png$", ""), w - 16, 12, Draw.SEMIBOLD);
        Draw.textCentered(g, label, pcx, y + 134, 12, nameColor, Draw.SEMIBOLD);
        // rename + delete
        int bw = (w - 18) / 2;
        Capes.Cape target = cape;
        Draw.roundBordered(g, x + 6, y + 148, bw, 24, 6, BUTTON, BUTTON_BORDER);
        Draw.icon(g, I_PENCIL, x + 6 + bw / 2f, y + 160, 13, accentLight);
        hotspot(x + 6, y + 148, bw, 24, () -> {
            renaming = target;
            renameBox.value = target.name().replaceAll("(?i)\\.png$", "");
            renameBox.allSelected = true;
            focused = renameBox;
        });
        Draw.roundBordered(g, x + 12 + bw, y + 148, bw, 24, 6, 0xFF2A0F14, 0xFF5A1E28);
        Draw.icon(g, I_TRASH, x + 12 + bw + bw / 2f, y + 160, 13, 0xFFFF8A95);
        hotspot(x + 12 + bw, y + 148, bw, 24, () -> Capes.delete(target));
        hotspot(x, y, w, 140, () -> Capes.select(target.name()));
    }

    private void emptySlot(GuiGraphicsExtractor g, int x, int y) {
        int w = 122, h = 178;
        int dash = Draw.alpha(0xFFFFFFFF, 0.18f);
        for (int i = 6; i < w - 6; i += 8) {
            Draw.rect(g, x + i, y, 4, 1, dash);
            Draw.rect(g, x + i, y + h - 1, 4, 1, dash);
        }
        for (int i = 6; i < h - 6; i += 8) {
            Draw.rect(g, x, y + i, 1, 4, dash);
            Draw.rect(g, x + w - 1, y + i, 1, 4, dash);
        }
        Draw.icon(g, I_UPLOAD, x + w / 2f, y + 72, 22, 0xFF6E6A78);
        Draw.textCentered(g, "Empty slot", x + w / 2f, y + 104, 11.5f, 0xFF8C8495, Draw.MEDIUM);
        Draw.textCentered(g, "Click to upload", x + w / 2f, y + 120, 10, 0xFF6E6A78, Draw.REGULAR);
        hotspot(x, y, w, h, Capes::upload);
    }

    private void commitRename() {
        if (renaming != null) {
            if (!Capes.rename(renaming, renameBox.value)) {
                return;
            }
        }
        renaming = null;
        focused = null;
    }

    /** Animated, theme-coloured section that previews and toggles the live auction cape. */
    private int drawAuctionCapeSection(GuiGraphicsExtractor g, int y) {
        Draw.textCentered(g, "Auction Cape", 623, y + 4, 15, WHITE, Draw.SEMIBOLD);
        Draw.textCentered(g, "Wear a live timer on your back during auctions. Your chosen cape comes back afterwards.",
                623, y + 22, 10.5f, SUBTLE, Draw.REGULAR);

        int cx = 333, cy = y + 38, cw = 580, ch = 150;
        float tm = (System.currentTimeMillis() % 100_000L) / 1000f;
        Draw.round(g, cx - 3, cy - 3, cw + 6, ch + 6, 16, Draw.alpha(accent, 0.12f + 0.06f * (float) Math.sin(tm * 2)));
        Draw.roundBordered(g, cx, cy, cw, ch, 14, surface(Draw.lerpColor(0xFF0B0A10, accent, 0.10f)), Draw.alpha(accent, 0.55f));
        g.enableScissor(cx + 1, Math.max(cy + 1, VIEW_Y), cx + cw - 1, Math.min(cy + ch - 1, VIEW_B));
        for (int i = 0; i < 6; i++) {
            float bx = cx + cw * (0.15f + 0.7f * (0.5f + 0.5f * (float) Math.sin(tm * (0.35f + i * 0.09f) + i * 1.7f)));
            float by = cy + ch * (0.5f + 0.42f * (float) Math.sin(tm * (0.5f + i * 0.07f) + i * 2.3f));
            float r = 38 + (i % 3) * 12;
            int col = i % 2 == 0 ? accent : Draw.lerpColor(accent, 0xFFFFFFFF, 0.35f);
            Draw.circle(g, bx, by, r, Draw.alpha(col, 0.10f));
            Draw.circle(g, bx, by, r * 0.55f, Draw.alpha(col, 0.10f));
        }
        for (int i = -ch; i < cw; i += 26) {
            int sx = cx + i + (int) ((tm * 18) % 26);
            for (int k = 0; k < ch; k += 2) {
                Draw.rect(g, sx + k, cy + k, 2, 2, 0x0DFFFFFF);
            }
        }
        g.disableScissor();

        // live preview of the cape itself
        Auctions.Auction live = Auctions.current();
        AuctionCape.update(live != null && !live.cancelled() ? live : null);
        Draw.round(g, cx + 22, cy + 16, 84, 118, 10, 0xAA06050A);
        capePreview(g, AuctionCape.TEXTURE, AuctionCape.WIDTH, AuctionCape.HEIGHT, cx + 64, cy + 75, 64, 102, true);

        Draw.text(g, "Live timer cape", cx + 128, cy + 30, 14, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Switches on by itself when an auction starts and", cx + 128, cy + 52, 11, 0xFFD6D2DE, Draw.REGULAR);
        Draw.text(g, "shows the time left, the top bid and a progress bar.", cx + 128, cy + 68, 11, 0xFFD6D2DE, Draw.REGULAR);
        Draw.text(g, "When nothing is being auctioned you keep your cape.", cx + 128, cy + 84, 11, 0xFFD6D2DE, Draw.REGULAR);
        Draw.text(g, LethalConfig.auctionCape ? "Enabled" : "Disabled", cx + 128, cy + 118, 12.5f,
                LethalConfig.auctionCape ? accentLight : 0xFF9C9CA6, Draw.SEMIBOLD);
        toggle(g, cx + cw - 58, cy + 118, LethalConfig.auctionCape, () -> {
            LethalConfig.auctionCape = !LethalConfig.auctionCape;
            LethalConfig.save();
        });
        return cy + ch + 16;
    }

    private static void capePreview(GuiGraphicsExtractor g, Identifier tex, int texW, int texH, int cx, int cy, int w, int h) {
        capePreview(g, tex, texW, texH, cx, cy, w, h, false);
    }

    /** Draws a cape's outside face (and, unless single, its inside face) centred on (cx, cy). */
    private static void capePreview(GuiGraphicsExtractor g, Identifier tex, int texW, int texH, int cx, int cy, int w, int h, boolean single) {
        float sx = texW / 64f, sy = texH / 32f;
        int rw = Math.round(10 * sx), rh = Math.round(16 * sy);
        if (single) {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, cx - w / 2, cy - h / 2, sx, sy, w, h, rw, rh, texW, texH);
            return;
        }
        g.blit(RenderPipelines.GUI_TEXTURED, tex, cx - w - 3, cy - h / 2, sx, sy, w, h, rw, rh, texW, texH);
        g.blit(RenderPipelines.GUI_TEXTURED, tex, cx + 3, cy - h / 2, 12 * sx, sy, w, h, rw, rh, texW, texH);
    }

    /** Lets the player drag a fixed-size frame over their picture to choose the cape area. */
    private int drawCropper(GuiGraphicsExtractor g, int t, Capes.Pending p) {
        if (cropFor != p) {
            cropFor = p;
            capeName.value = p.suggestedName;
            capeName.allSelected = false;
        }
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        Draw.icon(g, I_HANGER, 253, t + 80, 18, head);
        Draw.text(g, "Pick your cape area", 272, t + 77, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Drag the frame to choose which part of the picture goes on your cape.", 272, t + 94, 10.5f, SUBTLE, Draw.REGULAR);

        int bx = CROP_BOX_X, by = t + 112, bw = CROP_BOX_W, bh = CROP_BOX_H;
        cropTop = by;
        Draw.roundBordered(g, bx, by, bw, bh, 10, surface(0xFF0C0B11), 0xFF2A2633);
        int iw = p.image.getWidth(), ih = p.image.getHeight();
        cropScale = Math.min((bw - 24) / (float) iw, (bh - 24) / (float) ih);
        int dw = Math.round(iw * cropScale), dh = Math.round(ih * cropScale);
        int ix = bx + (bw - dw) / 2, iy = by + (bh - dh) / 2;
        g.blit(RenderPipelines.GUI_TEXTURED, p.texture(), ix, iy, 0, 0, dw, dh, iw, ih, iw, ih);

        int fx = ix + Math.round(p.cropX * cropScale), fy = iy + Math.round(p.cropY * cropScale);
        int fw = Math.round(p.cropW * cropScale), fh = Math.round(p.cropH * cropScale);
        int shade = 0xB0000000;
        Draw.rect(g, ix, iy, dw, fy - iy, shade);
        Draw.rect(g, ix, fy + fh, dw, iy + dh - fy - fh, shade);
        Draw.rect(g, ix, fy, fx - ix, fh, shade);
        Draw.rect(g, fx + fw, fy, ix + dw - fx - fw, fh, shade);
        Draw.roundBordered(g, fx - 1, fy - 1, fw + 2, fh + 2, 3, 0x00000000, accent);
        Draw.roundBordered(g, fx, fy, fw, fh, 2, 0x00000000, Draw.alpha(0xFFFFFFFF, 0.5f));
        if (p.cropW < iw && fw > 40) {
            Draw.icon(g, I_LEFT, fx + 10, fy + fh / 2f, 14, 0xFFFFFFFF);
            Draw.icon(g, I_RIGHT, fx + fw - 10, fy + fh / 2f, 14, 0xFFFFFFFF);
        }
        hotspot(ix, iy, dw, dh, () -> {
            draggingCrop = true;
            dragStartMouseX = mouseDX;
            dragStartMouseY = mouseDY;
            dragStartCropX = p.cropX;
            dragStartCropY = p.cropY;
        });

        // preview + name + save
        int px = 757;
        Draw.text(g, "Preview", px, t + 122, 12.5f, TEXT, Draw.MEDIUM);
        Draw.round(g, px, t + 134, 248, 186, 10, surface(0xFF0C0B11));
        int pw = 96, ph = Math.round(pw / CapeImages.FACE_ASPECT);
        g.blit(RenderPipelines.GUI_TEXTURED, p.texture(), px + 124 - pw / 2, t + 227 - ph / 2, p.cropX, p.cropY, pw, ph,
                p.cropW, p.cropH, iw, ih);
        Draw.text(g, "Name", px, t + 332, 12.5f, TEXT, Draw.MEDIUM);
        field(g, capeName, px, t + 342, 248, 28, "Cape name");
        Draw.text(g, "Zoom", px, t + 386, 12.5f, TEXT, Draw.MEDIUM);
        Draw.textRight(g, String.format(java.util.Locale.ROOT, "%.1fx", p.zoom), px + 248, t + 386, 12, 0xFFC3C3CC, Draw.MEDIUM);
        dragSliderOn(g, px + 52, t + 386, 150, (p.zoom - 1f) / 4f, f -> p.setZoom(1f + f * 4f));
        button(g, px, t + 404, 248, 28, "Save Cape", accentDark, Draw.lerpColor(accent, 0xFF000000, 0.15f), accentLight, () -> {
            Capes.pending().suggestedName = capeName.value.isBlank() ? Capes.pending().suggestedName : capeName.value;
            Capes.savePending();
            focused = null;
        });
        button(g, px, t + 438, 248, 28, "Cancel", surface(0xFF15141B), BUTTON_BORDER, WHITE, () -> {
            Capes.cancelPending();
            focused = null;
        });
        return t + 480;
    }

    // ---------------------------------------------------------------- scoreboard

    /** A slider being dragged: its track and what to set. */
    private record SliderDrag(int x, int w, java.util.function.Consumer<Float> set) {
    }

    private SliderDrag sliderDrag;

    private void dragSlider(int x, int cy, int w, float frac, java.util.function.Consumer<Float> set) {
        dragSliderOn(g(), x, cy, w, frac, set);
    }

    private void dragSliderOn(GuiGraphicsExtractor g, int x, int cy, int w, float frac, java.util.function.Consumer<Float> set) {
        slider(g, x, cy, w, frac);
        hotspot(x - 6, cy - 9, w + 12, 18, () -> {
            sliderDrag = new SliderDrag(x, w, set);
            set.accept(Math.max(0f, Math.min(1f, (mouseDX - x) / w)));
        });
    }

    private GuiGraphicsExtractor currentG;

    private GuiGraphicsExtractor g() {
        return currentG;
    }

    private int drawScoreboard(GuiGraphicsExtractor g, int t) {
        currentG = g;
        int head = Draw.lerpColor(accent, 0xFFFFFFFF, 0.05f);
        int cardFill = surface(tinted(0xFF141119, 0.07f)), cardBorder = tinted(0xFF1E1A24, 0.12f);
        Draw.icon(g, 0xef11, 253, t + 80, 17, head);
        Draw.text(g, "Scoreboard", 272, t + 77, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Change the size, corners and background of the scoreboard on the right of your screen.", 272, t + 94, 10.5f, SUBTLE, Draw.REGULAR);
        button(g, 912, t + 69, 88, 26, "Reset", BUTTON, BUTTON_BORDER, WHITE, () -> {
            LethalConfig.sbScale = 1f;
            LethalConfig.sbRounded = false;
            LethalConfig.sbBackground = 0;
            LethalConfig.sbHue = 0.8f;
            LethalConfig.sbOpacity = 0.45f;
            LethalConfig.save();
        });

        int x0 = 235, w0 = 500;
        // Size
        int y = t + 116;
        Draw.roundBordered(g, x0, y, w0, 64, 12, cardFill, cardBorder);
        Draw.text(g, "Size", x0 + 18, y + 22, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Make it bigger or smaller", x0 + 18, y + 42, 10.5f, SUBTLE, Draw.REGULAR);
        Draw.textRight(g, Math.round(LethalConfig.sbScale * 100) + "%", x0 + 260, y + 32, 12, 0xFFC3C3CC, Draw.MEDIUM);
        dragSlider(x0 + 276, y + 32, 200, (LethalConfig.sbScale - 0.5f), f -> {
            LethalConfig.sbScale = Math.round((0.5f + f) * 20) / 20f;
        });

        // Corners
        y += 76;
        Draw.roundBordered(g, x0, y, w0, 64, 12, cardFill, cardBorder);
        Draw.text(g, "Rounded corners", x0 + 18, y + 22, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Smooth corners instead of a square box", x0 + 18, y + 42, 10.5f, SUBTLE, Draw.REGULAR);
        toggle(g, x0 + w0 - 52, y + 32, LethalConfig.sbRounded, () -> {
            LethalConfig.sbRounded = !LethalConfig.sbRounded;
            LethalConfig.save();
        });

        // Player head
        y += 76;
        Draw.roundBordered(g, x0, y, w0, 64, 12, cardFill, cardBorder);
        Draw.text(g, "Player head", x0 + 18, y + 22, 13, WHITE, Draw.SEMIBOLD);
        Draw.text(g, "Show your head next to the name at the top", x0 + 18, y + 42, 10.5f, SUBTLE, Draw.REGULAR);
        toggle(g, x0 + w0 - 52, y + 32, LethalConfig.sbHead, () -> {
            LethalConfig.sbHead = !LethalConfig.sbHead;
            LethalConfig.save();
        });

        // Background
        y += 76;
        boolean custom = LethalConfig.sbBackground == 2;
        int bh = custom ? 168 : 96;
        Draw.roundBordered(g, x0, y, w0, bh, 12, cardFill, cardBorder);
        Draw.text(g, "Background", x0 + 18, y + 22, 13, WHITE, Draw.SEMIBOLD);
        int segX = x0 + 18, segY = y + 40, segW = w0 - 36, opt = (segW - 6) / 4;
        Draw.round(g, segX, segY, segW, 32, 8, 0xFF0C0B11);
        for (int i = 0; i < 4; i++) {
            int ox = segX + 3 + i * opt;
            boolean on = LethalConfig.sbBackground == i;
            if (on) {
                Draw.round(g, ox, segY + 3, opt, 26, 6, accentDark);
            }
            Draw.textCentered(g, ScoreboardStyle.BACKGROUNDS[i], ox + opt / 2f, segY + 16.5f, 12, on ? accentLight : 0xFFC9CAD0, Draw.SEMIBOLD);
            int which = i;
            hotspot(ox, segY + 3, opt, 26, () -> {
                LethalConfig.sbBackground = which;
                LethalConfig.save();
            });
        }
        if (custom) {
            Draw.text(g, "Colour", x0 + 18, y + 96, 12, TEXT, Draw.MEDIUM);
            int hx = x0 + 120, hw = w0 - 138;
            for (int i = 0; i < hw; i++) {
                Draw.rect(g, hx + i, y + 91, 1, 10, 0xFF000000 | java.awt.Color.HSBtoRGB(i / (float) hw, 0.75f, 0.85f));
            }
            int kx = hx + Math.round(LethalConfig.sbHue * hw);
            Draw.circle(g, kx, y + 96, 7, 0xFFFFFFFF);
            Draw.circle(g, kx, y + 96, 5, 0xFF000000 | java.awt.Color.HSBtoRGB(LethalConfig.sbHue, 0.75f, 0.85f));
            hotspot(hx - 6, y + 87, hw + 12, 18, () -> {
                sliderDrag = new SliderDrag(hx, hw, f -> LethalConfig.sbHue = f);
                LethalConfig.sbHue = Math.max(0f, Math.min(1f, (mouseDX - hx) / hw));
            });
            Draw.text(g, "Opacity", x0 + 18, y + 132, 12, TEXT, Draw.MEDIUM);
            Draw.textRight(g, Math.round(LethalConfig.sbOpacity * 100) + "%", x0 + w0 - 18, y + 132, 12, 0xFFC3C3CC, Draw.MEDIUM);
            dragSlider(x0 + 120, y + 132, w0 - 190, LethalConfig.sbOpacity, f -> LethalConfig.sbOpacity = Math.round(f * 100) / 100f);
        }

        // Live preview
        int px = 751, pw = 254, ph = 300, py = t + 116;
        Draw.roundBordered(g, px, py, pw, ph, 12, surface(0xFF0C0B11), cardBorder);
        Draw.text(g, "Preview", px + 14, py + 18, 12, TEXT, Draw.MEDIUM);
        drawScoreboardPreview(g, px + pw / 2, py + ph / 2 + 12);
        return Math.max(y + bh, py + ph) + 12;
    }

    // 7x7 pixel icons for the preview: dollar, shard, sword, skull
    private static final String[] PREVIEW_ICONS = {
            "0001000011111001010000111110000101001111100001000", // $
            "0001000001110011111110111110001110001101100100010", // shard
            "0000011000011100011101011100011100000100001010000", // sword
            "0111110111111110010011111111011111001010100000000"}; // skull
    private static final int[] PREVIEW_ICON_COLORS = {0xFF3FD13F, 0xFFA83FFF, 0xFFE0303A, 0xFFFF8A1F};
    private static final String[] PREVIEW_VALUES = {"1T", "1000", "1", "1"};

    /** Draws a sample sidebar (DonutSMP style) using the same style code the real scoreboard uses. */
    private void drawScoreboardPreview(GuiGraphicsExtractor g, int cx, int cy) {
        var font = minecraft.font;
        int lineH = 10;
        String title = "Player";
        int width = font.width(title) + (LethalConfig.sbHead ? 10 : 0);
        for (String v : PREVIEW_VALUES) {
            width = Math.max(width, 11 + font.width(v));
        }
        width += 6;
        int h = PREVIEW_VALUES.length * lineH + 2;
        Matrix3x2fStack p = g.pose();
        p.pushMatrix();
        p.translate(cx, cy);
        p.scale(1.8f * LethalConfig.sbScale);
        int x0 = -width / 2, x1 = x0 + width, bottom = h / 2, bodyTop = bottom - h;
        ScoreboardStyle.titleFill(g, x0 - 2, bodyTop - lineH - 1, x1, bodyTop - 1, 0x66000000);
        ScoreboardStyle.bodyFill(g, x0 - 2, bodyTop - 1, x1, bottom, 0x4C000000);
        int titleX = -font.width(title) / 2 + (LethalConfig.sbHead ? 5 : 0);
        ScoreboardStyle.title(g, font, Component.literal(title), titleX, bodyTop - lineH, 0xFFFFFFFF, false);
        for (int i = 0; i < PREVIEW_VALUES.length; i++) {
            int ly = bodyTop + 1 + i * lineH;
            String bits = PREVIEW_ICONS[i];
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 7; c++) {
                    if (bits.charAt(r * 7 + c) == '1') {
                        Draw.rect(g, x0 + c, ly + r, 1, 1, PREVIEW_ICON_COLORS[i]);
                    }
                }
            }
            g.text(font, PREVIEW_VALUES[i], x0 + 11, ly, 0xFFFFFFFF, false);
        }
        p.popMatrix();
    }

    // ---------------------------------------------------------------- theme

    private int drawTheme(GuiGraphicsExtractor g, int t) {
        int[] cx = {249, 436, 623, 810};
        for (int i = 0; i < PRESET_NAMES.length; i++) {
            int x = cx[i % 4];
            int y = t + 60 + (i / 4) * 69;
            boolean selected = LethalConfig.preset == i;
            if (selected) {
                Draw.round(g, x - 3, y - 3, 190, 66, 9, Draw.alpha(accent, 0.18f));
                Draw.roundBordered(g, x - 1, y - 1, 186, 62, 7, 0x00000000, accent);
                Draw.roundBordered(g, x, y, 184, 60, 6, surface(0xFF1B1A22), Draw.alpha(accent, 0.6f));
            } else {
                Draw.round(g, x, y, 184, 60, 6, surface(0xFF24222C));
            }
            Draw.text(g, PRESET_NAMES[i], x + 6, y + 13, 12, selected ? Draw.lerpColor(accent, 0xFFFFFFFF, 0.15f) : 0xFFE0DFE6,
                    selected ? Draw.SEMIBOLD : Draw.REGULAR);
            if (selected) {
                Draw.circle(g, x + 174, y + 12, 6, accent);
                Draw.circle(g, x + 174, y + 12, 4.8f, 0xFF0B0E14);
                Draw.circle(g, x + 174, y + 12, 3.6f, accent);
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
        focused = null;
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
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (sliderDrag != null) {
            layout();
            sliderDrag.set().accept(Math.max(0f, Math.min(1f, (toDesignX(event.x()) - sliderDrag.x()) / sliderDrag.w())));
            return true;
        }
        Capes.Pending crop = Capes.pending();
        if (draggingCrop && crop != null) {
            layout();
            float mx = toDesignX(event.x()), my = toDesignY(event.y());
            crop.moveTo(dragStartCropX + Math.round((mx - dragStartMouseX) / cropScale),
                    dragStartCropY + Math.round((my - dragStartMouseY) / cropScale));
            return true;
        }
        if (draggingVolume) {
            layout();
            setVolumeFromMouse(toDesignX(event.x()));
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (sliderDrag != null) {
            sliderDrag = null;
            LethalConfig.save();
            return true;
        }
        if (draggingCrop) {
            draggingCrop = false;
            return true;
        }
        if (draggingVolume) {
            draggingVolume = false;
            LethalConfig.save();
            LethalAuctionClient.playClick();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        layout();
        if (page == Page.AUCTION && overGrid(toDesignX(mouseX), toDesignY(mouseY))) {
            gridScrollTarget = (float) Math.max(0, Math.min(gridMaxScroll, gridScrollTarget - scrollY * PITCH));
            return true;
        }
        int pi = page.ordinal();
        scrollTarget[pi] = (float) Math.max(0, Math.min(maxScroll[pi], scrollTarget[pi] - scrollY * 32));
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (focused != null) {
            focused.type(event.codepointAsString());
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (focused != null) {
            int key = event.key();
            if (focused == renameBox && (event.isConfirmation() || event.isEscape())) {
                if (event.isConfirmation()) {
                    commitRename();
                } else {
                    renaming = null;
                    focused = null;
                }
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                focused.backspace(event.hasControlDown());
            } else if (key == GLFW.GLFW_KEY_DELETE) {
                if (focused.allSelected) {
                    focused.backspace(true);
                }
            } else if (event.isSelectAll()) {
                focused.allSelected = !focused.value.isEmpty();
            } else if (event.isEscape() || event.isConfirmation()) {
                focused = null;
            } else if (event.isCycleFocus() && fieldOrder.contains(focused)) {
                int i = fieldOrder.indexOf(focused);
                int next = event.hasShiftDown() ? i - 1 + fieldOrder.size() : i + 1;
                focused = fieldOrder.get(next % fieldOrder.size());
            } else if (event.isPaste()) {
                focused.type(minecraft.keyboardHandler.getClipboard());
            } else if (event.isCut()) {
                minecraft.keyboardHandler.setClipboard(focused.value);
                focused.value = "";
                focused.allSelected = false;
            } else if (event.isCopy()) {
                minecraft.keyboardHandler.setClipboard(focused.value);
            } else if (key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_RIGHT || key == GLFW.GLFW_KEY_HOME
                    || key == GLFW.GLFW_KEY_END) {
                focused.allSelected = false;
            }
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    /** Dev helper: fills the new auction form so a test run can capture it. */
    public void devFillForm() {
        selected = new ItemStack(Items.ELYTRA);
        minBid.value = "45k";
        worth.value = "50k";
        search.value = "ely";
        note.value = "Pay over = I keep";
        focused = search;
        search.allSelected = true;
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
