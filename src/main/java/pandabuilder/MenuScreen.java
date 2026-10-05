package pandabuilder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

/** Click GUI: sidebar with tabs on the left, header with search on top, two columns of module cards. */
public class MenuScreen extends Screen {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");

    // Sword logo, drawn 2px per cell. S = blade edge, W = blade shine, G = guard, H = grip, P = pommel.
    private static final String[] SWORD = {
            "..........SW",
            ".........SWS",
            "........SWS.",
            ".......SWS..",
            "......SWS...",
            "..G..SWS....",
            "...GSWS.....",
            "....GS......",
            "...H.G......",
            "..H...G.....",
            ".H..........",
            "P...........",
    };

    private static final int SIDEBAR_W = 108;
    private static final int TAB_H = 18;
    private static final int CARD_H = 34;
    private static final int GAP = 6;

    private static Module.Tab selectedTab = Module.Tab.PVP;
    private static String query = "";
    /** Settings page being shown (Module.STORAGE_ESP / Module.AUTO_MINE), or null for the module cards. */
    private static String settingsPage;
    private static int scroll;

    /** Module whose key is being rebound; the next key press is captured for it. */
    private Module binding;

    /** A switch on a settings page. Removable ones show an x instead of a switch and are removed on click. */
    private record Option(String label, int swatch, BooleanSupplier on, Runnable toggle, boolean removable) {
        Option(String label, int swatch, BooleanSupplier on, Runnable toggle) {
            this(label, swatch, on, toggle, false);
        }
    }

    private record Section(String title, List<Option> options, String hint) {}

    /** A positioned element of a settings page: an option, or (option == null) a line of text. */
    private record Placed(int x, int y, Option option, String text, int color) {}

    private int contentHeight;
    private String cachedQuery;
    private List<Block> cachedResults = List.of();

    private EditBox search;
    private int x0, y0, w, h;

    public MenuScreen() {
        super(Component.literal(Brand.NAME));
    }

    @Override
    protected void init() {
        w = Math.clamp(this.width - 24, 320, 460);
        h = Math.clamp(this.height - 24, 200, 260);
        x0 = (this.width - w) / 2;
        y0 = (this.height - h) / 2;

        search = new EditBox(this.font, searchX() + 16, y0 + 14, searchW() - 22, 10, Component.literal("Search"));
        search.setBordered(false);
        search.setMaxLength(32);
        search.setTextColor(Brand.TEXT);
        search.setHint(Component.literal("Search modules...").withColor(Brand.FAINT));
        search.setValue(query);
        search.setResponder(s -> {
            query = s;
            scroll = 0;
            // On the Storage ESP page the box searches blocks; anywhere else it searches modules.
            if (!s.isBlank() && !Module.STORAGE_ESP.equals(settingsPage)) settingsPage = null;
        });
        addRenderableWidget(search);
    }

    // ---- layout ----

    private int mainX() { return x0 + SIDEBAR_W + 8; }
    private int mainW() { return x0 + w - 8 - mainX(); }
    private int searchW() { return Math.min(120, mainW() / 2 - 10); }
    private int searchX() { return mainX() + mainW() - searchW() - 4; }
    private int cardsY() { return y0 + 38; }
    private int cardW() { return (mainW() - GAP) / 2; }
    private int cardX(int i) { return mainX() + (i % 2) * (cardW() + GAP); }
    private int cardY(int i) { return cardsY() + (i / 2) * (CARD_H + GAP); }
    private int tabY(int i) { return y0 + 68 + i * (TAB_H + 4); }
    private int gearX(int i) { return cardX(i) + cardW() - 44; }

    // Settings pages: a header row (back, title, on/off), then scrollable sections of options, 3 per row.
    private static final int OPT_H = 18;
    private int optW() { return (mainW() - 2 * GAP) / 3; }
    private int optX(int i) { return mainX() + (i % 3) * (optW() + GAP); }
    private int settingsTop() { return cardsY() + 22; }
    private int settingsBottom() { return y0 + h - 8; }

    private Module settingsModule() {
        for (Module m : Modules.ALL) if (settingsPage.equals(m.settings())) return m;
        return null;
    }

    private List<Section> sections() {
        if (Module.AUTO_MINE.equals(settingsPage)) {
            return List.of(
                    new Section("MODE", List.of(
                            new Option("3x3 Pickaxe", 0, AutoMine::pickaxe3x3, AutoMine::togglePickaxe3x3),
                            new Option("Fill Holes", 0, AutoMine::fillHoles, AutoMine::toggleFillHoles),
                            new Option("Hug Cave Walls", 0, AutoMine::hugWalls, AutoMine::toggleHugWalls)),
                            "Fill Holes bridges gaps. Hug Cave Walls tunnels along a cave's wall instead of across it."),
                    new Section("LAVA", List.of(
                            new Option("Go Over", 0, AutoMine::goOver, AutoMine::toggleGoOver),
                            new Option("Go Under", 0, AutoMine::goUnder, AutoMine::toggleGoUnder)),
                            "Lets it stair up or down past lava. It picks the shortest safe way: up, down, left or right."),
                    new Section("FREECAM", List.of(new Option("Freeze in Freecam", 0, AutoMine::freezeInFreecam,
                            AutoMine::toggleFreezeInFreecam)), "No arm swing, and your clicks can't break blocks. Auto Mine keeps digging."));
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (!q.isEmpty()) {
            List<Option> results = new ArrayList<>();
            for (Block b : searchBlocks(q)) {
                results.add(new Option(b.getName().getString(), StorageEsp.colorOf(b), () -> StorageEsp.isCustom(b),
                        () -> StorageEsp.toggleCustom(b)));
            }
            return List.of(new Section("ADD BLOCKS \u2022 click to add or remove", results,
                    results.isEmpty() ? "No blocks match \"" + query.trim() + "\"" : null));
        }
        List<Option> custom = new ArrayList<>();
        for (Block b : StorageEsp.customBlocks()) {
            custom.add(new Option(b.getName().getString(), StorageEsp.colorOf(b), () -> true,
                    () -> StorageEsp.toggleCustom(b), true));
        }
        List<Option> display = List.of(
                new Option("Boxes", 0, StorageEsp::boxes, StorageEsp::toggleBoxes),
                new Option("Tracers", 0, StorageEsp::tracers, StorageEsp::toggleTracers),
                new Option("Markers", 0, StorageEsp::markers, StorageEsp::toggleMarkers));
        List<Option> types = new ArrayList<>();
        for (StorageEsp.Type t : StorageEsp.Type.values()) {
            types.add(new Option(t.label, t.color, () -> StorageEsp.isEnabled(t), () -> StorageEsp.toggleType(t)));
        }
        return List.of(
                new Section("DISPLAY", display, null),
                new Section("BLOCK TYPES", types, null),
                new Section("CUSTOM BLOCKS", custom,
                        custom.isEmpty() ? "Type a block name in the search box above to add it." : null));
    }

    /** Blocks whose name or id contains the query; names starting with it come first. */
    private List<Block> searchBlocks(String q) {
        if (q.equals(cachedQuery)) return cachedResults;
        String id = q.replace(' ', '_');
        cachedQuery = q;
        cachedResults = BuiltInRegistries.BLOCK.stream()
                .filter(b -> !b.defaultBlockState().isAir())
                .filter(b -> b.getName().getString().toLowerCase(Locale.ROOT).contains(q)
                        || BuiltInRegistries.BLOCK.getKey(b).getPath().contains(id))
                .sorted(Comparator.comparing((Block b) -> !b.getName().getString().toLowerCase(Locale.ROOT).startsWith(q))
                        .thenComparing(b -> b.getName().getString().length()))
                .limit(60)
                .toList();
        return cachedResults;
    }

    /** Positions every section title, option and hint; also updates contentHeight for scrolling. */
    private List<Placed> layout() {
        List<Placed> out = new ArrayList<>();
        int top = settingsTop();
        int y = top + 2 - scroll;
        for (Section sec : sections()) {
            out.add(new Placed(mainX(), y, null, sec.title(), Brand.FAINT));
            y += 11;
            List<Option> opts = sec.options();
            for (int i = 0; i < opts.size(); i++) {
                out.add(new Placed(optX(i), y + (i / 3) * (OPT_H + 3), opts.get(i), null, 0));
            }
            y += ((opts.size() + 2) / 3) * (OPT_H + 3);
            if (sec.hint() != null) {
                out.add(new Placed(mainX(), y + 1, null, sec.hint(), Brand.MUTED));
                y += 13;
            }
            y += 6;
        }
        contentHeight = y + scroll - top;
        return out;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight - (settingsBottom() - settingsTop()));
    }

    private String keyLabel(Module m) {
        if (m == binding) return "Press a key...";
        KeyMapping key = m.key().get();
        return key == null || key.isUnbound() ? "None" : key.getTranslatedKeyMessage().getString();
    }

    private int chipX(int i) { return cardX(i) + 9 + font.width("KeyBind:") + 4; }
    private int chipW(Module m) { return font.width(keyLabel(m)) + 8; }

    private List<Module> visibleModules() {
        String q = query.trim().toLowerCase(Locale.ROOT);
        List<Module> out = new ArrayList<>();
        for (Module m : Modules.ALL) {
            boolean match = q.isEmpty() ? m.tab() == selectedTab : m.name().toLowerCase(Locale.ROOT).contains(q);
            if (match) out.add(m);
        }
        return out;
    }

    // ---- drawing ----

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        roundRect(g, x0 - 1, y0 - 1, w + 2, h + 2, 7, Brand.BORDER);
        roundRect(g, x0, y0, w, h, 6, Brand.WINDOW);

        drawSidebar(g, mouseX, mouseY);
        drawHeader(g);
        if (settingsPage != null) {
            drawSettings(g, mouseX, mouseY);
        } else {
            drawCards(g, mouseX, mouseY);
        }
        search.setHint(Component.literal(Module.STORAGE_ESP.equals(settingsPage) ? "Search blocks..." : "Search modules...")
                .withColor(Brand.FAINT));

        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    private void drawSidebar(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int sx = x0 + 6, sy = y0 + 6, sw = SIDEBAR_W - 6, sh = h - 12;
        roundRect(g, sx, sy, sw, sh, 5, Brand.SIDEBAR);

        drawSword(g, sx + 8, sy + 8);
        // First word white, the rest in the accent colour, stacked next to the logo.
        String[] words = Brand.NAME.toUpperCase(Locale.ROOT).split(" ", 2);
        g.text(font, Component.literal(words[0]).withStyle(ChatFormatting.BOLD), sx + 36, sy + 7, Brand.TEXT, false);
        if (words.length > 1) {
            g.text(font, Component.literal(words[1]).withStyle(ChatFormatting.BOLD), sx + 36, sy + 17, Brand.ACCENT, false);
        }
        g.text(font, Brand.VERSION, sx + 8, sy + 31, Brand.FAINT, false);

        g.fill(sx + 8, sy + 43, sx + sw - 8, sy + 44, Brand.BORDER);
        g.text(font, "MODULES", sx + 8, sy + 49, Brand.FAINT, false);

        Module.Tab[] tabs = Module.Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            Module.Tab tab = tabs[i];
            int ty = tabY(i);
            boolean active = query.isBlank() && tab == selectedTab;
            boolean hover = inside(mouseX, mouseY, sx + 4, ty, sw - 8, TAB_H);
            if (active) {
                roundRect(g, sx + 4, ty, sw - 8, TAB_H, 4, Brand.ACCENT_SOFT);
                g.fill(sx + 1, ty + 3, sx + 3, ty + TAB_H - 3, Brand.ACCENT);
            } else if (hover) {
                roundRect(g, sx + 4, ty, sw - 8, TAB_H, 4, Brand.CARD_HOVER);
            }
            int color = active ? Brand.ACCENT : Brand.MUTED;
            g.text(font, tab.icon, sx + 12, ty + 5, color, false);
            g.text(font, tab.title, sx + 28, ty + 5, active ? Brand.ACCENT : Brand.TEXT, false);
        }

        // Profile card at the bottom.
        int cy = sy + sh - 32;
        roundRect(g, sx + 4, cy, sw - 8, 28, 5, Brand.CARD);
        if (minecraft.player != null) {
            PlayerFaceExtractor.extractRenderState(g, minecraft.player.getSkin(), sx + 10, cy + 6, 16);
        } else {
            g.fill(sx + 10, cy + 6, sx + 26, cy + 22, Brand.TRACK_OFF);
        }
        g.text(font, minecraft.getUser().getName(), sx + 31, cy + 6, Brand.TEXT, false);
        g.text(font, "Lifetime", sx + 31, cy + 16, Brand.FAINT, false);
        roundRect(g, sx + sw - 18, cy + 11, 6, 6, 3, Brand.ONLINE);
    }

    private void drawSword(GuiGraphicsExtractor g, int x, int y) {
        for (int r = 0; r < SWORD.length; r++) {
            for (int c = 0; c < SWORD[r].length(); c++) {
                int color = switch (SWORD[r].charAt(c)) {
                    case 'S' -> 0xFFB8BEC8;
                    case 'W' -> 0xFFFFFFFF;
                    case 'G' -> Brand.ACCENT;
                    case 'H' -> 0xFF5A3A2A;
                    case 'P' -> Brand.ACCENT;
                    default -> 0;
                };
                if (color != 0) g.fill(x + c * 2, y + r * 2, x + c * 2 + 2, y + r * 2 + 2, color);
            }
        }
    }

    private void drawHeader(GuiGraphicsExtractor g) {
        int hx = mainX(), hy = y0 + 6, hw = mainW();
        roundRect(g, hx, hy, hw, 24, 5, Brand.SIDEBAR);

        g.text(font, "Hello, ", hx + 8, hy + 8, Brand.MUTED, false);
        g.text(font, Component.literal(minecraft.getUser().getName()).withStyle(ChatFormatting.BOLD),
                hx + 8 + font.width("Hello, "), hy + 8, Brand.TEXT, false);

        String time = LocalTime.now().format(CLOCK);
        int textEnd = hx + 8 + font.width("Hello, " + minecraft.getUser().getName()) + 8;
        int timeX = Math.max(textEnd, (hx + searchX()) / 2 - font.width(time) / 2);
        g.text(font, Component.literal(time).withStyle(ChatFormatting.BOLD), timeX, hy + 8, Brand.ACCENT, false);

        int bx = searchX(), by = hy + 4;
        roundRect(g, bx - 1, by - 1, searchW() + 2, 18, 5, Brand.BORDER);
        roundRect(g, bx, by, searchW(), 16, 4, Brand.WINDOW);
        g.text(font, "⌕", bx + 5, by + 4, Brand.MUTED, false);

        g.fill(hx + 4, hy + 25, hx + hw - 4, hy + 26, Brand.ACCENT);
    }

    private void drawCards(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        List<Module> modules = visibleModules();
        if (modules.isEmpty()) {
            g.centeredText(font, "No modules match \"" + query + "\"", mainX() + mainW() / 2, cardsY() + 20, Brand.MUTED);
            return;
        }
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            int x = cardX(i), y = cardY(i), cw = cardW();
            boolean on = m.isOn();
            boolean hover = inside(mouseX, mouseY, x, y, cw, CARD_H);

            if (on) {
                roundRect(g, x - 2, y - 2, cw + 4, CARD_H + 4, 7, Brand.ACCENT_GLOW);
                roundRect(g, x - 1, y - 1, cw + 2, CARD_H + 2, 6, Brand.ACCENT);
                roundRect(g, x, y, cw, CARD_H, 5, Brand.CARD_ON);
            } else {
                roundRect(g, x, y, cw, CARD_H, 5, hover ? Brand.CARD_HOVER : Brand.CARD);
            }

            g.text(font, m.name(), x + 9, y + 7, Brand.TEXT, false);
            int infoX = x + 9 + font.width(m.name()) + 5;
            g.text(font, "ⓘ", infoX, y + 7, Brand.MUTED, false);
            if (inside(mouseX, mouseY, infoX - 1, y + 5, 10, 11)) {
                g.setTooltipForNextFrame(font, Component.literal(m.description()), mouseX, mouseY);
            }

            g.text(font, "KeyBind:", x + 9, y + 20, Brand.FAINT, false);
            String keyName = keyLabel(m);
            int chipX = chipX(i);
            boolean chipHover = inside(mouseX, mouseY, chipX, y + 18, chipW(m), 11);
            roundRect(g, chipX, y + 18, chipW(m), 11, 3, m == binding ? Brand.ACCENT_SOFT : chipHover ? Brand.BORDER : Brand.TRACK_OFF);
            g.text(font, keyName, chipX + 4, y + 20, m == binding ? Brand.ACCENT : chipHover ? Brand.TEXT : Brand.MUTED, false);
            if (chipHover && m != binding) {
                g.setTooltipForNextFrame(font, Component.literal("Click, then press a key. Esc clears it."), mouseX, mouseY);
            }

            if (m.hasSettings()) {
                boolean gearHover = inside(mouseX, mouseY, gearX(i), y + 9, 12, 14);
                g.text(font, "\u2699", gearX(i) + 2, y + 12, gearHover ? Brand.ACCENT : Brand.MUTED, false);
                if (gearHover) g.setTooltipForNextFrame(font, Component.literal("Settings"), mouseX, mouseY);
            }

            drawSwitch(g, x + cw - 30, y + 12, on);
        }
    }

    private void drawSettings(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        Module module = settingsModule();
        int bx = mainX(), by = cardsY();
        boolean backHover = inside(mouseX, mouseY, bx, by, 44, 16);
        roundRect(g, bx, by, 44, 16, 4, backHover ? Brand.CARD_HOVER : Brand.CARD);
        g.text(font, "\u2190 Back", bx + 6, by + 4, backHover ? Brand.TEXT : Brand.MUTED, false);
        g.text(font, Component.literal(module.name()).withStyle(ChatFormatting.BOLD), bx + 52, by + 4, Brand.TEXT, false);
        drawSwitch(g, mainX() + mainW() - 24, by + 3, module.isOn());

        List<Placed> placed = layout();
        scroll = Math.clamp(scroll, 0, maxScroll());
        int top = settingsTop(), bottom = settingsBottom();
        boolean mouseIn = inside(mouseX, mouseY, mainX(), top, mainW(), bottom - top);
        g.enableScissor(mainX() - 2, top, mainX() + mainW() + 2, bottom);
        for (Placed p : placed) {
            if (p.y() + OPT_H < top || p.y() > bottom) continue;
            if (p.option() == null) {
                g.text(font, p.text(), p.x(), p.y(), p.color(), false);
            } else {
                drawOption(g, p.option(), p.x(), p.y(), mouseIn ? mouseX : -1, mouseIn ? mouseY : -1);
            }
        }
        g.disableScissor();

        // Scrollbar when the page is taller than the area.
        int max = maxScroll();
        if (max > 0) {
            int trackH = bottom - top;
            int thumbH = Math.max(12, trackH * trackH / contentHeight);
            int thumbY = top + (trackH - thumbH) * scroll / max;
            g.fill(mainX() + mainW() + 3, top, mainX() + mainW() + 5, bottom, Brand.TRACK_OFF);
            g.fill(mainX() + mainW() + 3, thumbY, mainX() + mainW() + 5, thumbY + thumbH, Brand.ACCENT);
        }
    }

    private void drawOption(GuiGraphicsExtractor g, Option o, int x, int y, int mouseX, int mouseY) {
        int ow = optW();
        boolean on = o.on().getAsBoolean();
        boolean hover = inside(mouseX, mouseY, x, y, ow, OPT_H);
        if (on && !o.removable()) {
            roundRect(g, x - 1, y - 1, ow + 2, OPT_H + 2, 5, Brand.ACCENT);
            roundRect(g, x, y, ow, OPT_H, 4, Brand.CARD_ON);
        } else {
            roundRect(g, x, y, ow, OPT_H, 4, hover ? Brand.CARD_HOVER : Brand.CARD);
        }
        int tx = x + 6;
        if (o.swatch() != 0) {
            roundRect(g, tx, y + 6, 6, 6, 2, o.swatch());
            tx += 10;
        }
        int right = o.removable() ? x + ow - 14 : x + ow - 28;
        g.text(font, ellipsize(o.label(), right - tx), tx, y + 5, on ? Brand.TEXT : Brand.MUTED, false);
        if (o.removable()) {
            g.text(font, "\u00D7", x + ow - 11, y + 5, hover ? Brand.ACCENT : Brand.MUTED, false);
            if (hover) g.setTooltipForNextFrame(font, Component.literal("Remove " + o.label()), mouseX, mouseY);
        } else {
            drawSwitch(g, x + ow - 24, y + 4, on);
            if (hover && font.width(o.label()) > right - tx) {
                g.setTooltipForNextFrame(font, Component.literal(o.label()), mouseX, mouseY);
            }
        }
    }

    private String ellipsize(String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text;
        String dots = "...";
        int end = text.length();
        while (end > 0 && font.width(text.substring(0, end) + dots) > maxWidth) end--;
        return text.substring(0, end) + dots;
    }

    private void drawSwitch(GuiGraphicsExtractor g, int x, int y, boolean on) {
        roundRect(g, x, y, 20, 10, 5, on ? Brand.ACCENT : Brand.TRACK_OFF);
        int knobX = on ? x + 11 : x + 1;
        roundRect(g, knobX, y + 1, 8, 8, 4, on ? Brand.TEXT : Brand.KNOB_OFF);
    }

    /** Filled rectangle with rounded corners, built from horizontal strips. */
    private static void roundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        g.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ---- input ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        binding = null;
        if (super.mouseClicked(event, doubleClick)) return true;
        if (event.button() != 0) return false;
        double mx = event.x(), my = event.y();

        Module.Tab[] tabs = Module.Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            if (inside(mx, my, x0 + 10, tabY(i), SIDEBAR_W - 14, TAB_H)) {
                selectedTab = tabs[i];
                settingsPage = null;
                search.setValue("");
                return true;
            }
        }

        if (settingsPage != null) return clickSettings(mx, my);

        List<Module> modules = visibleModules();
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            int y = cardY(i);
            if (m.hasSettings() && inside(mx, my, gearX(i), y + 9, 12, 14)) {
                search.setValue("");
                settingsPage = m.settings();
                scroll = 0;
                return true;
            }
            if (inside(mx, my, chipX(i), y + 18, chipW(m), 11)) {
                binding = m;
                return true;
            }
            if (inside(mx, my, cardX(i), y, cardW(), CARD_H)) {
                m.toggle().run();
                return true;
            }
        }
        search.setFocused(false);
        return false;
    }

    private boolean clickSettings(double mx, double my) {
        if (inside(mx, my, mainX(), cardsY(), 44, 16)) {
            settingsPage = null;
            search.setValue("");
            return true;
        }
        if (inside(mx, my, mainX() + mainW() - 24, cardsY() + 3, 20, 10)) {
            settingsModule().toggle().run();
            return true;
        }
        if (!inside(mx, my, mainX(), settingsTop(), mainW(), settingsBottom() - settingsTop())) return false;
        for (Placed p : layout()) {
            if (p.option() != null && inside(mx, my, p.x(), p.y(), optW(), OPT_H)) {
                p.option().toggle().run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (settingsPage != null) {
            scroll = Math.clamp(scroll - Math.round(scrollY * 14), 0, maxScroll());
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (binding != null) {
            KeyMapping key = binding.key().get();
            if (key != null) {
                key.setKey(event.key() == InputConstants.KEY_ESCAPE ? InputConstants.UNKNOWN : InputConstants.getKey(event));
                KeyMapping.resetMapping();
                minecraft.options.save();
            }
            binding = null;
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
