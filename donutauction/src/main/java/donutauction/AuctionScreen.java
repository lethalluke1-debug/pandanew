package donutauction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Donut Auction menu: sidebar pages on the left, a header on top, the page on the right. */
public class AuctionScreen extends Screen {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");

    // Donut logo, 2px per cell. P = frosting, p = frosting shade, D = dough, d = dough shade, s/y/b = sprinkles.
    private static final String[] DONUT = {
            "....PPPP....",
            "..PPsPPyPP..",
            ".PPPPPPPbPP.",
            ".PyPP..PPPp.",
            "PPPP....PsPp",
            "PbP......PPp",
            "DPp......pPd",
            "DDPP....PPdd",
            ".DDpPbPPpdd.",
            ".DDDDDDDDdd.",
            "..DDDDDDdd..",
            "....dddd....",
    };

    private enum Page {
        NEW("New Auction", "+"), PRESETS("Presets", "☰"), HISTORY("History", "⌛"),
        SETTINGS("Settings", "⚙"), THEME("Theme", "◐");

        final String title, icon;

        Page(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }
    }

    private static final int SIDEBAR_W = 100;
    private static final int CELL = 18;
    private static final int RIGHT_W = 128;

    // Kept between openings of the menu.
    private static Page page = Page.NEW;
    private static String search = "";
    private static String selectedItem;
    private static String fMin = "", fQty = "1", fTimer = "60", fWorth = "";
    private static boolean worthEdited;
    private static int gridScroll, listScroll;

    private int x0, y0, w, h;
    private EditBox searchBox, minBox, qtyBox, timerBox, worthBox, apiBox, ruleBox;
    private boolean settingWorth;
    private List<Item> gridItems = List.of();
    private String gridQuery;
    private String status = "";
    private long statusAt;

    public AuctionScreen() {
        super(Component.literal("Donut Auction"));
    }

    // ---- layout ----

    private int mainX() { return x0 + SIDEBAR_W + 6; }
    private int mainW() { return x0 + w - 6 - mainX(); }
    private int top() { return y0 + 32; }
    private int bottom() { return y0 + h - 6; }
    private int gridW() { return mainW() - RIGHT_W - 6; }
    private int rightX() { return mainX() + mainW() - RIGHT_W; }
    private int gridTop() { return top() + 20; }
    private int cols() { return Math.max(1, (gridW() - 6) / CELL); }
    private int visibleRows() { return Math.max(1, (bottom() - gridTop() - 4) / CELL); }
    private int navY(int i) { return y0 + 62 + i * 16 + (i >= 3 ? 14 : 0); }

    @Override
    protected void init() {
        w = Math.clamp(this.width - 20, 360, 480);
        h = Math.clamp(this.height - 20, 220, 280);
        x0 = (this.width - w) / 2;
        y0 = (this.height - h) / 2;
        searchBox = minBox = qtyBox = timerBox = worthBox = apiBox = ruleBox = null;

        switch (page) {
            case NEW -> {
                searchBox = field(mainX() + 14, top() + 3, gridW() - 20, search, "Search items...", 40, s -> {
                    search = s;
                    gridScroll = 0;
                });
                int rx = rightX();
                int fy = top() + 50;
                minBox = field(rx + 4, fy + 2, RIGHT_W - 8, fMin, "none", 16, s -> fMin = s);
                qtyBox = field(rx + 4, fy + 28, RIGHT_W / 2 - 10, fQty, "1", 4, s -> fQty = s);
                timerBox = field(rx + RIGHT_W / 2 + 4, fy + 28, RIGHT_W / 2 - 8, fTimer, "60", 4, s -> fTimer = s);
                worthBox = field(rx + 4, fy + 54, RIGHT_W - 8, fWorth, "auto", 16, s -> {
                    fWorth = s;
                    if (!settingWorth) worthEdited = true;
                });
            }
            case SETTINGS -> {
                Config c = Config.get();
                int fx = mainX() + 6;
                apiBox = field(fx + 4, settingsFieldY(0) + 2, mainW() - 20, c.apiKey, "Run /api in game, paste the key", 200, s -> {
                    c.apiKey = s.trim();
                    WorthService.clear();
                    Config.save();
                });
                ruleBox = field(fx + 4, settingsFieldY(1) + 2, mainW() - 20, c.ruleText, "Shown on the HUD", 60, s -> {
                    c.ruleText = s;
                    Config.save();
                });
            }
            default -> { }
        }
    }

    /** A borderless text box; its frame is drawn by the page. */
    private EditBox field(int x, int y, int fw, String value, String hint, int max, Consumer<String> onChange) {
        EditBox box = new EditBox(font, x, y, fw, 10, Component.empty());
        box.setBordered(false);
        box.setMaxLength(max);
        box.setTextColor(Ui.TEXT);
        box.setValue(value);
        box.setHint(Component.literal(hint).withColor(Ui.FAINT));
        box.setResponder(onChange);
        addRenderableWidget(box);
        return box;
    }

    private void setPage(Page p) {
        page = p;
        listScroll = 0;
        rebuildWidgets();
    }

    private void flash(String message) {
        status = message;
        statusAt = System.currentTimeMillis();
    }

    // ---- drawing ----

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        Ui.round(g, x0 - 1, y0 - 1, w + 2, h + 2, 8, Ui.alpha(Ui.accent(), 0x55));
        Ui.round(g, x0, y0, w, h, 7, Ui.WINDOW);
        drawSidebar(g, mouseX, mouseY);
        drawHeader(g);
        switch (page) {
            case NEW -> drawNew(g, mouseX, mouseY);
            case PRESETS -> drawPresets(g, mouseX, mouseY);
            case HISTORY -> drawHistory(g, mouseX, mouseY);
            case SETTINGS -> drawSettings(g, mouseX, mouseY);
            case THEME -> drawTheme(g, mouseX, mouseY);
        }
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        if (!status.isEmpty() && System.currentTimeMillis() - statusAt < 3000) {
            int sw = Ui.width(font, status) + 12;
            Ui.box(g, x0 + (w - sw) / 2, y0 + h - 18, sw, 12, 5, Ui.CARD, Ui.alpha(Ui.accent(), 0xA0));
            Ui.text(g, font, status, x0 + (w - sw) / 2f + 6, y0 + h - 15, Ui.TEXT);
        }
    }

    private void drawSidebar(GuiGraphicsExtractor g, int mx, int my) {
        int sx = x0 + 5, sy = y0 + 5, sw = SIDEBAR_W - 4, sh = h - 10;
        Ui.round(g, sx, sy, sw, sh, 6, Ui.SIDEBAR);

        drawDonut(g, sx + 7, sy + 7);
        Ui.text(g, font, Component.literal("DONUT").withStyle(ChatFormatting.BOLD), sx + 35, sy + 8, Ui.TEXT, 0.85f);
        Ui.text(g, font, Component.literal("AUCTION").withStyle(ChatFormatting.BOLD), sx + 35, sy + 17, Ui.accent(), 0.85f);
        Ui.text(g, font, Component.literal("by Lethal"), sx + 35, sy + 26, Ui.FAINT, 0.6f);
        g.fill(sx + 7, sy + 38, sx + sw - 7, sy + 39, Ui.BORDER);

        Ui.text(g, font, Component.literal("AUCTION"), sx + 7, sy + 44, Ui.FAINT, 0.6f);
        Ui.text(g, font, Component.literal("GENERAL"), sx + 7, navY(3) - 10, Ui.FAINT, 0.6f);
        Page[] pages = Page.values();
        for (int i = 0; i < pages.length; i++) {
            int ny = navY(i);
            boolean active = pages[i] == page;
            boolean hover = Ui.inside(mx, my, sx + 3, ny, sw - 6, 14);
            if (active) {
                Ui.round(g, sx + 3, ny, sw - 6, 14, 4, Ui.alpha(Ui.accent(), 0x38));
                g.fill(sx, ny + 3, sx + 2, ny + 11, Ui.accent());
            } else if (hover) {
                Ui.round(g, sx + 3, ny, sw - 6, 14, 4, Ui.CARD_HOVER);
            }
            int color = active ? Ui.accent() : hover ? Ui.TEXT : Ui.MUTED;
            Ui.text(g, font, pages[i].icon, sx + 9, ny + 3.5f, color);
            Ui.text(g, font, pages[i].title, sx + 21, ny + 3.5f, active ? Ui.TEXT : color);
        }

        // Seller card.
        int cy = sy + sh - 28;
        Ui.round(g, sx + 3, cy, sw - 6, 24, 5, Ui.CARD);
        if (minecraft.player != null) {
            PlayerFaceExtractor.extractRenderState(g, minecraft.player.getSkin(), sx + 8, cy + 4, 16);
        }
        Ui.text(g, font, Ui.ellipsize(font, minecraft.getUser().getName(), sw - 40), sx + 28, cy + 5, Ui.TEXT);
        Ui.text(g, font, Auction.running() ? "Auction live" : "Seller", sx + 28, cy + 14, Auction.running() ? Ui.accent() : Ui.FAINT);
        Ui.round(g, sx + sw - 13, cy + 9, 5, 5, 2, Auction.running() ? Ui.accent() : Ui.GREEN);
    }

    private void drawDonut(GuiGraphicsExtractor g, int x, int y) {
        for (int r = 0; r < DONUT.length; r++) {
            for (int c = 0; c < DONUT[r].length(); c++) {
                int color = switch (DONUT[r].charAt(c)) {
                    case 'P' -> Ui.accent();
                    case 'p' -> Ui.alpha(darker(Ui.accent()), 0xFF);
                    case 'D' -> 0xFFD9A066;
                    case 'd' -> 0xFFB07A45;
                    case 's' -> 0xFFFFFFFF;
                    case 'y' -> 0xFFFDE047;
                    case 'b' -> 0xFF60A5FA;
                    default -> 0;
                };
                if (color != 0) g.fill(x + c * 2, y + r * 2, x + c * 2 + 2, y + r * 2 + 2, color);
            }
        }
    }

    private static int darker(int c) {
        int r = (c >> 16 & 0xFF) * 3 / 4, gg = (c >> 8 & 0xFF) * 3 / 4, b = (c & 0xFF) * 3 / 4;
        return 0xFF000000 | r << 16 | gg << 8 | b;
    }

    private void drawHeader(GuiGraphicsExtractor g) {
        int hx = mainX(), hy = y0 + 6, hw = mainW();
        Ui.round(g, hx, hy, hw, 20, 5, Ui.SIDEBAR);
        String name = minecraft.getUser().getName();
        Ui.text(g, font, "Hello, ", hx + 8, hy + 7, Ui.MUTED);
        Ui.bold(g, font, name, hx + 8 + Ui.width(font, "Hello, "), hy + 7, Ui.TEXT);
        String time = LocalTime.now().format(CLOCK);
        Ui.bold(g, font, time, hx + hw / 2f - Ui.boldWidth(font, time) / 2f, hy + 7, Ui.accent());

        Auction a = Auction.current();
        String chip = a != null ? "LIVE " + Ui.clock(a.remainingMs()) : page.title;
        int cw = Ui.width(font, chip) + 12;
        Ui.box(g, hx + hw - cw - 6, hy + 4, cw, 12, 6, a != null ? Ui.alpha(Ui.accent(), 0x40) : Ui.FIELD, a != null ? Ui.accent() : Ui.BORDER);
        Ui.text(g, font, chip, hx + hw - cw, hy + 7, a != null ? Ui.TEXT : Ui.MUTED);
        g.fill(hx + 4, hy + 21, hx + hw - 4, hy + 22, Ui.alpha(Ui.accent(), 0xC0));
    }

    // ---- New Auction ----

    private List<Item> items() {
        String q = search.trim().toLowerCase(Locale.ROOT);
        if (q.equals(gridQuery)) return gridItems;
        Set<Item> out = new LinkedHashSet<>();
        // Inventory first, so what you're about to sell is right there.
        if (minecraft.player != null) {
            for (int i = 0; i < 36; i++) {
                ItemStack s = minecraft.player.getInventory().getItem(i);
                if (!s.isEmpty() && matches(s.getItem(), q)) out.add(s.getItem());
            }
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR && matches(item, q)) out.add(item);
        }
        gridQuery = q;
        gridItems = new ArrayList<>(out);
        return gridItems;
    }

    private static boolean matches(Item item, String q) {
        if (q.isEmpty()) return true;
        return new ItemStack(item).getHoverName().getString().toLowerCase(Locale.ROOT).contains(q)
                || BuiltInRegistries.ITEM.getKey(item).getPath().contains(q.replace(' ', '_'));
    }

    private void drawNew(GuiGraphicsExtractor g, int mx, int my) {
        int gx = mainX(), gw = gridW();
        // Search + grid panel.
        Ui.round(g, gx, top(), gw, bottom() - top(), 6, Ui.SIDEBAR);
        Ui.box(g, gx + 4, top() + 1, gw - 8, 13, 4, Ui.FIELD, searchBox != null && searchBox.isFocused() ? Ui.accent() : Ui.BORDER);
        Ui.text(g, font, "⌕", gx + 7, top() + 4, Ui.MUTED);
        List<Item> items = items();
        String count = items.size() + " items";
        Ui.text(g, font, count, gx + gw - 8 - Ui.width(font, count), top() + 4, Ui.FAINT);

        int cols = cols(), rows = visibleRows();
        int maxScroll = Math.max(0, (items.size() + cols - 1) / cols - rows);
        gridScroll = Math.clamp(gridScroll, 0, maxScroll);
        int ox = gx + (gw - cols * CELL) / 2;
        Item hovered = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = (gridScroll + r) * cols + c;
                if (idx >= items.size()) break;
                Item item = items.get(idx);
                int cx = ox + c * CELL, cy = gridTop() + r * CELL;
                String id = BuiltInRegistries.ITEM.getKey(item).toString();
                boolean sel = id.equals(selectedItem);
                boolean hov = Ui.inside(mx, my, cx, cy, CELL - 1, CELL - 1);
                Ui.round(g, cx, cy, CELL - 1, CELL - 1, 3, sel ? Ui.alpha(Ui.accent(), 0x60) : hov ? Ui.CARD_HOVER : Ui.CARD);
                g.item(new ItemStack(item), cx + 1, cy + 1);
                if (hov) hovered = item;
            }
        }
        if (maxScroll > 0) {
            int trackH = rows * CELL;
            int thumbH = Math.max(10, trackH * rows / (rows + maxScroll));
            int thumbY = gridTop() + (trackH - thumbH) * gridScroll / maxScroll;
            g.fill(gx + gw - 4, gridTop(), gx + gw - 2, gridTop() + trackH, Ui.TRACK_OFF);
            g.fill(gx + gw - 4, thumbY, gx + gw - 2, thumbY + thumbH, Ui.accent());
        }
        if (hovered != null) g.setTooltipForNextFrame(font, new ItemStack(hovered).getHoverName(), mx, my);

        drawForm(g, mx, my);
    }

    private void drawForm(GuiGraphicsExtractor g, int mx, int my) {
        int rx = rightX(), rw = RIGHT_W, y = top();

        // Preview card.
        Ui.box(g, rx, y, rw, 40, 6, Ui.SIDEBAR, Ui.BORDER);
        Ui.box(g, rx + 6, y + 8, 24, 24, 5, Ui.FIELD, Ui.BORDER);
        if (selectedItem == null) {
            Ui.text(g, font, "No item yet", rx + 36, y + 11, Ui.TEXT);
            Ui.text(g, font, "Pick one on the left", rx + 36, y + 21, Ui.FAINT);
        } else {
            ItemStack stack = Auction.stackOf(selectedItem);
            g.item(stack, rx + 10, y + 12);
            Ui.text(g, font, Ui.ellipsize(font, stack.getHoverName().getString(), rw - 42), rx + 36, y + 8, Ui.TEXT);
            WorthService.Result r = WorthService.get(selectedItem);
            if (r.status() == WorthService.Status.OK) {
                Ui.text(g, font, "Worth " + Money.format(r.each()) + " each", rx + 36, y + 17, Ui.GOLD);
                if (!worthEdited) autoFillWorth(r.each());
            } else {
                Ui.text(g, font, Ui.ellipsize(font, r.detail(), rw - 42), rx + 36, y + 17,
                        r.status() == WorthService.Status.LOADING ? Ui.MUTED : Ui.FAINT);
            }
            boolean have = minecraft.player != null && minecraft.player.getInventory().countItem(stack.getItem()) > 0;
            Ui.text(g, font, have ? "In your inventory" : "Not in your inventory", rx + 36, y + 26, have ? Ui.GREEN : Ui.RED);
        }

        int fy = y + 50;
        label(g, "Minimum bid", rx, fy - 8);
        frame(g, rx, fy, rw, minBox);
        label(g, "Quantity", rx, fy + 18);
        frame(g, rx, fy + 26, rw / 2 - 4, qtyBox);
        label(g, "Timer (sec)", rx + rw / 2, fy + 18);
        frame(g, rx + rw / 2, fy + 26, rw / 2, timerBox);
        label(g, "Worth of one item", rx, fy + 44);
        frame(g, rx, fy + 52, rw, worthBox);

        // Total worth = worth of one item x quantity.
        double each = Money.parse(fWorth);
        int qty = parseInt(fQty, 1);
        String total = each > 0 ? Money.format(each * Math.max(1, qty)) : "-";
        Ui.text(g, font, "Total worth", rx + 2, fy + 72, Ui.MUTED);
        Ui.bold(g, font, total, rx + rw - 2 - Ui.boldWidth(font, total), fy + 72, Ui.GOLD);
        if (qty > 1 && each > 0) {
            String calc = Money.format(each) + " x " + qty;
            Ui.text(g, font, calc, rx + rw - 2 - Ui.width(font, calc), fy + 81, Ui.FAINT);
        }

        int by = bottom() - 32;
        if (Auction.running()) {
            Ui.button(g, font, rx, by, rw, 14, "End now (sell)", Ui.inside(mx, my, rx, by, rw, 14), true, true);
            Ui.button(g, font, rx, by + 17, rw, 14, "Cancel auction", Ui.inside(mx, my, rx, by + 17, rw, 14), false, true);
        } else {
            boolean ok = selectedItem != null;
            Ui.button(g, font, rx, by, rw, 14, "Start Auction", ok && Ui.inside(mx, my, rx, by, rw, 14), true, ok);
            Ui.button(g, font, rx, by + 17, rw, 14, "Save as preset", ok && Ui.inside(mx, my, rx, by + 17, rw, 14), false, ok);
        }
    }

    private void autoFillWorth(double each) {
        String v = Money.format(each);
        if (worthBox != null && !v.equals(worthBox.getValue())) {
            settingWorth = true;
            worthBox.setValue(v);
            settingWorth = false;
        }
        fWorth = v;
    }

    private void label(GuiGraphicsExtractor g, String s, int x, int y) {
        Ui.text(g, font, s, x + 2, y, Ui.MUTED);
    }

    private void frame(GuiGraphicsExtractor g, int x, int y, int fw, EditBox box) {
        boolean focused = box != null && box.isFocused();
        Ui.box(g, x, y, fw, 14, 4, Ui.FIELD, focused ? Ui.accent() : Ui.BORDER);
    }

    private static int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void startFromForm() {
        if (selectedItem == null || Auction.running()) return;
        double min = Money.parse(fMin), worth = Money.parse(fWorth);
        int qty = parseInt(fQty, -1), timer = parseInt(fTimer, -1);
        if (min < 0) { flash("Minimum bid isn't a number"); return; }
        if (qty < 1 || qty > 2304) { flash("Quantity must be 1-2304"); return; }
        if (timer < 5 || timer > 3600) { flash("Timer must be 5-3600 seconds"); return; }
        Auction.start(selectedItem, qty, min, Math.max(0, worth), timer);
        onClose();
    }

    private void saveFromForm() {
        if (selectedItem == null) return;
        String name = Auction.stackOf(selectedItem).getHoverName().getString();
        Config.get().presets.add(new Preset(name, selectedItem, Math.max(1, parseInt(fQty, 1)),
                Math.max(0, Money.parse(fMin)), Math.clamp(parseInt(fTimer, 60), 5, 3600), Math.max(0, Money.parse(fWorth))));
        Config.save();
        flash("Saved preset \"" + name + "\"");
    }

    private void loadPreset(Preset p) {
        selectedItem = p.itemId;
        fMin = p.minBid > 0 ? Money.format(p.minBid) : "";
        fQty = Integer.toString(p.quantity);
        fTimer = Integer.toString(p.timerSec);
        fWorth = p.worthEach > 0 ? Money.format(p.worthEach) : "";
        worthEdited = p.worthEach > 0;
        search = "";
        setPage(Page.NEW);
    }

    // ---- Presets ----

    private static final int CARD_H = 50;

    private int presetX(int i) { return mainX() + (i % 2) * ((mainW() - 6) / 2 + 6); }
    private int presetY(int i) { return top() + 16 + (i / 2) * (CARD_H + 6) - listScroll; }
    private int presetW() { return (mainW() - 6) / 2; }

    private void drawPresets(GuiGraphicsExtractor g, int mx, int my) {
        List<Preset> presets = Config.get().presets;
        Ui.bold(g, font, "Presets", mainX() + 2, top() + 2, Ui.TEXT);
        Ui.text(g, font, presets.size() + " saved", mainX() + 6 + Ui.boldWidth(font, "Presets"), top() + 2, Ui.FAINT);
        if (presets.isEmpty()) {
            Ui.text(g, font, "Set up an auction on New Auction and press \"Save as preset\".", mainX() + 2, top() + 20, Ui.MUTED);
            return;
        }
        g.enableScissor(mainX() - 2, top() + 14, mainX() + mainW() + 2, bottom());
        for (int i = 0; i < presets.size(); i++) {
            Preset p = presets.get(i);
            int x = presetX(i), y = presetY(i), pw = presetW();
            if (y + CARD_H < top() || y > bottom()) continue;
            boolean hover = Ui.inside(mx, my, x, y, pw, CARD_H);
            Ui.box(g, x, y, pw, CARD_H, 6, hover ? Ui.CARD_HOVER : Ui.CARD, hover ? Ui.alpha(Ui.accent(), 0xA0) : Ui.BORDER);
            Ui.round(g, x + 6, y + 6, 4, 4, 2, Ui.accent());
            Ui.bold(g, font, Ui.ellipsize(font, p.name, pw - 24), x + 13, y + 5, Ui.TEXT);
            g.item(Auction.stackOf(p.itemId), x + 6, y + 15);
            String line = (p.minBid > 0 ? "Min " + Money.format(p.minBid) : "No min")
                    + (p.quantity > 1 ? "  x" + p.quantity : "") + "  " + p.timerSec + "s";
            Ui.text(g, font, Ui.ellipsize(font, line, pw - 30), x + 26, y + 16, Ui.MUTED);
            if (p.worthEach > 0) Ui.text(g, font, "Worth " + Money.format(p.worthEach * p.quantity), x + 26, y + 24, Ui.GOLD);
            int bw = (pw - 16 - 14) / 2;
            Ui.button(g, font, x + 4, y + 35, bw, 11, "Load", Ui.inside(mx, my, x + 4, y + 35, bw, 11), false, true);
            boolean canStart = !Auction.running();
            Ui.button(g, font, x + 8 + bw, y + 35, bw, 11, "Start", canStart && Ui.inside(mx, my, x + 8 + bw, y + 35, bw, 11), true, canStart);
            Ui.button(g, font, x + pw - 18, y + 35, 14, 11, "×", Ui.inside(mx, my, x + pw - 18, y + 35, 14, 11), false, true);
        }
        g.disableScissor();
    }

    private boolean clickPresets(double mx, double my) {
        List<Preset> presets = Config.get().presets;
        if (!Ui.inside(mx, my, mainX(), top() + 14, mainW(), bottom() - top() - 14)) return false;
        for (int i = 0; i < presets.size(); i++) {
            Preset p = presets.get(i);
            int x = presetX(i), y = presetY(i), pw = presetW();
            int bw = (pw - 16 - 14) / 2;
            if (Ui.inside(mx, my, x + 4, y + 35, bw, 11)) {
                loadPreset(p);
                return true;
            }
            if (Ui.inside(mx, my, x + 8 + bw, y + 35, bw, 11)) {
                if (Auction.running()) return true;
                double worth = p.worthEach;
                if (worth <= 0) {
                    WorthService.Result r = WorthService.get(p.itemId);
                    if (r.status() == WorthService.Status.OK) worth = r.each();
                }
                Auction.start(p.itemId, p.quantity, p.minBid, worth, p.timerSec);
                onClose();
                return true;
            }
            if (Ui.inside(mx, my, x + pw - 18, y + 35, 14, 11)) {
                presets.remove(i);
                Config.save();
                flash("Deleted preset \"" + p.name + "\"");
                return true;
            }
        }
        return false;
    }

    // ---- History ----

    private void drawHistory(GuiGraphicsExtractor g, int mx, int my) {
        List<HistoryEntry> hist = Config.get().history;
        Ui.bold(g, font, "History", mainX() + 2, top() + 2, Ui.TEXT);
        int cx = mainX() + mainW() - 40;
        if (!hist.isEmpty()) Ui.button(g, font, cx, top(), 40, 11, "Clear", Ui.inside(mx, my, cx, top(), 40, 11), false, true);
        if (hist.isEmpty()) {
            Ui.text(g, font, "Finished auctions show up here.", mainX() + 2, top() + 20, Ui.MUTED);
            return;
        }
        g.enableScissor(mainX() - 2, top() + 14, mainX() + mainW() + 2, bottom());
        for (int i = 0; i < hist.size(); i++) {
            HistoryEntry e = hist.get(i);
            int y = top() + 16 + i * 24 - listScroll;
            if (y + 22 < top() || y > bottom()) continue;
            Ui.box(g, mainX(), y, mainW(), 21, 5, Ui.CARD, Ui.BORDER);
            ItemStack stack = Auction.stackOf(e.itemId);
            g.item(stack, mainX() + 4, y + 2);
            Ui.text(g, font, stack.getHoverName().getString() + (e.quantity > 1 ? " x" + e.quantity : ""), mainX() + 24, y + 4, Ui.TEXT);
            String result = e.cancelled ? "Cancelled" : e.winner == null ? "No bids" : "Sold to " + e.winner + " for $" + Money.format(e.price);
            Ui.text(g, font, result, mainX() + 24, y + 12, e.winner != null ? Ui.MUTED : Ui.FAINT);
            if (e.worthTotal > 0) {
                String worth = "Worth " + Money.format(e.worthTotal);
                Ui.text(g, font, worth, mainX() + mainW() - 6 - Ui.width(font, worth), y + 4, Ui.GOLD);
                if (e.winner != null) {
                    double diff = e.price - e.worthTotal;
                    String d = (diff >= 0 ? "+" : "-") + Money.format(Math.abs(diff));
                    Ui.text(g, font, d, mainX() + mainW() - 6 - Ui.width(font, d), y + 12, diff >= 0 ? Ui.GREEN : Ui.RED);
                }
            }
        }
        g.disableScissor();
    }

    // ---- Settings ----

    private record Toggle(String label, String hint, java.util.function.BooleanSupplier get, Runnable flip) {}

    private List<Toggle> toggles() {
        Config c = Config.get();
        return List.of(
                new Toggle("Announce in chat", "Start, warnings and result in chat", () -> c.announce, () -> c.announce = !c.announce),
                new Toggle("Announce new top bids", "At most every 2.5s", () -> c.announceBids, () -> c.announceBids = !c.announceBids),
                new Toggle("10 second warning", "Chat message near the end", () -> c.timeWarnings, () -> c.timeWarnings = !c.timeWarnings),
                new Toggle("Anti-snipe", "A late top bid resets the timer to 10s", () -> c.antiSnipe, () -> c.antiSnipe = !c.antiSnipe),
                new Toggle("Auto refund", "/pay back outbid and losing bids", () -> c.autoRefund, () -> c.autoRefund = !c.autoRefund));
    }

    private int toggleY(int i) { return top() + 14 + (i / 2) * 26; }
    private int toggleX(int i) { return mainX() + (i % 2) * ((mainW() - 6) / 2 + 6); }
    private int toggleW() { return (mainW() - 6) / 2; }
    private int settingsFieldY(int i) { return top() + 14 + 3 * 26 + 12 + i * 26; }

    private void drawSettings(GuiGraphicsExtractor g, int mx, int my) {
        Ui.text(g, font, "AUCTIONS", mainX() + 2, top() + 4, Ui.FAINT);
        List<Toggle> ts = toggles();
        for (int i = 0; i < ts.size(); i++) {
            Toggle t = ts.get(i);
            int x = toggleX(i), y = toggleY(i), tw = toggleW();
            boolean on = t.get().getAsBoolean();
            boolean hover = Ui.inside(mx, my, x, y, tw, 22);
            Ui.box(g, x, y, tw, 22, 5, hover ? Ui.CARD_HOVER : Ui.CARD, on ? Ui.alpha(Ui.accent(), 0xA0) : Ui.BORDER);
            Ui.text(g, font, t.label(), x + 6, y + 4, Ui.TEXT);
            Ui.text(g, font, Ui.ellipsize(font, t.hint(), tw - 34), x + 6, y + 13, Ui.FAINT);
            Ui.toggle(g, x + tw - 24, y + 7, on);
        }
        int fy0 = settingsFieldY(0), fy1 = settingsFieldY(1);
        label(g, "DonutSMP API key (for worth)", mainX() + 6, fy0 - 9);
        frame(g, mainX() + 6, fy0, mainW() - 12, apiBox);
        label(g, "Rule text", mainX() + 6, fy1 - 9);
        frame(g, mainX() + 6, fy1, mainW() - 12, ruleBox);
        Ui.text(g, font, "Bids are read from \"Name paid you $X\" messages.", mainX() + 8, fy1 + 20, Ui.FAINT);
    }

    // ---- Theme ----

    private int swatchX(int i) { return mainX() + 6 + i * 26; }

    private void drawTheme(GuiGraphicsExtractor g, int mx, int my) {
        Config c = Config.get();
        int y = top() + 4;
        Ui.text(g, font, "ACCENT", mainX() + 2, y, Ui.FAINT);
        for (int i = 0; i < Ui.ACCENTS.length; i++) {
            int x = swatchX(i);
            boolean sel = (c.accent | 0xFF000000) == Ui.ACCENTS[i];
            if (sel) Ui.round(g, x - 2, y + 10, 24, 24, 7, Ui.TEXT);
            Ui.round(g, x, y + 12, 20, 20, 6, Ui.ACCENTS[i]);
            if (Ui.inside(mx, my, x, y + 12, 20, 20)) g.setTooltipForNextFrame(font, Component.literal(Ui.ACCENT_NAMES[i]), mx, my);
        }

        y += 44;
        Ui.text(g, font, "TEXT SIZE", mainX() + 2, y, Ui.FAINT);
        segment(g, mx, my, mainX() + 6, y + 10, "Small", "Normal", c.smallText);

        y += 36;
        Ui.text(g, font, "AUCTION HUD", mainX() + 2, y, Ui.FAINT);
        segment(g, mx, my, mainX() + 6, y + 10, "Top", "Bottom", c.hudTop);
        int tx = mainX() + 6 + 2 * 60 + 12;
        boolean hover = Ui.inside(mx, my, tx, y + 10, 80, 14);
        Ui.box(g, tx, y + 10, 80, 14, 4, hover ? Ui.CARD_HOVER : Ui.CARD, Ui.BORDER);
        Ui.text(g, font, "Show HUD", tx + 6, y + 13.5f, Ui.TEXT);
        Ui.toggle(g, tx + 80 - 22, y + 12, c.showHud);

        y += 36;
        Ui.text(g, font, "PREVIEW", mainX() + 2, y, Ui.FAINT);
        Ui.box(g, mainX() + 6, y + 10, 150, 14, 4, Ui.alpha(Ui.accent(), 0xD0), Ui.accent());
        Ui.text(g, font, "Start Auction", mainX() + 6 + (150 - Ui.width(font, "Start Auction")) / 2f, y + 13.5f, Ui.TEXT);
        Ui.toggle(g, mainX() + 164, y + 12, true);
        Ui.bold(g, font, "Worth 377M", mainX() + 190, y + 13.5f, Ui.GOLD);
    }

    private void segment(GuiGraphicsExtractor g, int mx, int my, int x, int y, String a, String b, boolean firstOn) {
        for (int i = 0; i < 2; i++) {
            boolean on = (i == 0) == firstOn;
            int sx = x + i * 60;
            boolean hover = Ui.inside(mx, my, sx, y, 58, 14);
            Ui.box(g, sx, y, 58, 14, 4, on ? Ui.alpha(Ui.accent(), 0x50) : hover ? Ui.CARD_HOVER : Ui.CARD, on ? Ui.accent() : Ui.BORDER);
            String s = i == 0 ? a : b;
            Ui.text(g, font, s, sx + (58 - Ui.width(font, s)) / 2f, y + 3.5f, on ? Ui.TEXT : Ui.MUTED);
        }
    }

    private boolean clickTheme(double mx, double my) {
        Config c = Config.get();
        int y = top() + 4;
        for (int i = 0; i < Ui.ACCENTS.length; i++) {
            if (Ui.inside(mx, my, swatchX(i), y + 12, 20, 20)) {
                c.accent = Ui.ACCENTS[i];
                Config.save();
                return true;
            }
        }
        y += 44;
        if (Ui.inside(mx, my, mainX() + 6, y + 10, 58, 14)) { c.smallText = true; Config.save(); return true; }
        if (Ui.inside(mx, my, mainX() + 66, y + 10, 58, 14)) { c.smallText = false; Config.save(); return true; }
        y += 36;
        if (Ui.inside(mx, my, mainX() + 6, y + 10, 58, 14)) { c.hudTop = true; Config.save(); return true; }
        if (Ui.inside(mx, my, mainX() + 66, y + 10, 58, 14)) { c.hudTop = false; Config.save(); return true; }
        int tx = mainX() + 6 + 2 * 60 + 12;
        if (Ui.inside(mx, my, tx, y + 10, 80, 14)) { c.showHud = !c.showHud; Config.save(); return true; }
        return false;
    }

    // ---- input ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        if (event.button() != 0) return false;
        double mx = event.x(), my = event.y();

        Page[] pages = Page.values();
        for (int i = 0; i < pages.length; i++) {
            if (Ui.inside(mx, my, x0 + 8, navY(i), SIDEBAR_W - 10, 14)) {
                setPage(pages[i]);
                return true;
            }
        }
        boolean handled = switch (page) {
            case NEW -> clickNew(mx, my);
            case PRESETS -> clickPresets(mx, my);
            case HISTORY -> clickHistory(mx, my);
            case SETTINGS -> clickSettings(mx, my);
            case THEME -> clickTheme(mx, my);
        };
        if (!handled) setFocused(null);
        return handled;
    }

    private boolean clickNew(double mx, double my) {
        List<Item> items = items();
        int cols = cols(), rows = visibleRows();
        int ox = mainX() + (gridW() - cols * CELL) / 2;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = (gridScroll + r) * cols + c;
                if (idx >= items.size()) break;
                if (Ui.inside(mx, my, ox + c * CELL, gridTop() + r * CELL, CELL - 1, CELL - 1)) {
                    String id = BuiltInRegistries.ITEM.getKey(items.get(idx)).toString();
                    if (!id.equals(selectedItem)) {
                        selectedItem = id;
                        worthEdited = false;
                        fWorth = "";
                        if (worthBox != null) {
                            settingWorth = true;
                            worthBox.setValue("");
                            settingWorth = false;
                        }
                    }
                    return true;
                }
            }
        }
        int rx = rightX(), by = bottom() - 32;
        if (Ui.inside(mx, my, rx, by, RIGHT_W, 14)) {
            if (Auction.running()) {
                Auction.endNow();
                flash("Auction ended");
            } else {
                startFromForm();
            }
            return true;
        }
        if (Ui.inside(mx, my, rx, by + 17, RIGHT_W, 14)) {
            if (Auction.running()) {
                Auction.cancel();
                flash("Auction cancelled");
            } else {
                saveFromForm();
            }
            return true;
        }
        return false;
    }

    private boolean clickHistory(double mx, double my) {
        int cx = mainX() + mainW() - 40;
        if (!Config.get().history.isEmpty() && Ui.inside(mx, my, cx, top(), 40, 11)) {
            Config.get().history.clear();
            Config.save();
            return true;
        }
        return false;
    }

    private boolean clickSettings(double mx, double my) {
        List<Toggle> ts = toggles();
        for (int i = 0; i < ts.size(); i++) {
            if (Ui.inside(mx, my, toggleX(i), toggleY(i), toggleW(), 22)) {
                ts.get(i).flip().run();
                Config.save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (page == Page.NEW && Ui.inside(mx, my, mainX(), gridTop(), gridW(), bottom() - gridTop())) {
            gridScroll -= (int) Math.signum(scrollY);
            return true;
        }
        if (page == Page.PRESETS || page == Page.HISTORY) {
            int rows = page == Page.PRESETS ? (Config.get().presets.size() + 1) / 2 : Config.get().history.size();
            int content = page == Page.PRESETS ? rows * (CARD_H + 6) : rows * 24;
            int max = Math.max(0, content - (bottom() - top() - 16));
            listScroll = Math.clamp(listScroll - Math.round(scrollY * 14), 0, max);
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
