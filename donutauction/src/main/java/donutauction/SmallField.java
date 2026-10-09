package donutauction;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

/** A text box drawn with the small font (vanilla text boxes are always full size). Typing goes at the end. */
public final class SmallField {
    private static final int KEY_BACKSPACE = 259, KEY_ENTER = 257, KEY_TAB = 258, KEY_ESCAPE = 256, KEY_KP_ENTER = 335;

    public int x, y, w;
    public final int h = 14;
    private final String hint;
    private final int max;
    private final Consumer<String> onChange;
    private String value;
    private boolean focused;
    private boolean selectAll;

    public SmallField(int x, int y, int w, String value, String hint, int max, Consumer<String> onChange) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.value = value == null ? "" : value;
        this.hint = hint;
        this.max = max;
        this.onChange = onChange;
    }

    public String value() { return value; }
    public boolean focused() { return focused; }

    /** Sets the text without counting it as typing. */
    public void setSilently(String v) {
        value = v == null ? "" : v;
    }

    public void render(GuiGraphicsExtractor g, Font font, int mx, int my) {
        boolean hover = Ui.inside(mx, my, x, y, w, h);
        Ui.box(g, x, y, w, h, 4, Ui.FIELD, focused ? Ui.accent() : hover ? Ui.alpha(Ui.accent(), 0x70) : Ui.BORDER);
        int inner = w - 10;
        if (value.isEmpty() && !focused) {
            Ui.text(g, font, Ui.ellipsize(font, hint, inner), x + 5, y + 4.5f, Ui.FAINT);
            return;
        }
        // Show the end of long text so what you're typing stays visible.
        String shown = value;
        while (!shown.isEmpty() && Ui.width(font, shown) > inner - 3) shown = shown.substring(1);
        int tw = Ui.width(font, shown);
        if (selectAll && focused && !value.isEmpty()) g.fill(x + 5, y + 3, x + 5 + tw, y + 11, Ui.alpha(Ui.accent(), 0x70));
        Ui.text(g, font, shown, x + 5, y + 4.5f, Ui.TEXT);
        if (focused && (System.currentTimeMillis() / 500) % 2 == 0) {
            g.fill(x + 5 + tw + 1, y + 3, x + 5 + tw + 2, y + 11, Ui.accent());
        }
    }

    /** Focuses on a click inside, unfocuses on a click outside. Returns true if the click was inside. */
    public boolean click(double mx, double my) {
        boolean in = Ui.inside(mx, my, x, y, w, h);
        focused = in;
        selectAll = false;
        return in;
    }

    public void unfocus() {
        focused = false;
        selectAll = false;
    }

    public boolean charTyped(CharacterEvent e) {
        if (!focused || !e.isAllowedChatCharacter()) return false;
        if (selectAll) {
            value = "";
            selectAll = false;
        }
        if (value.length() < max) {
            value += e.codepointAsString();
            onChange.accept(value);
        }
        return true;
    }

    public boolean keyPressed(KeyEvent e) {
        if (!focused) return false;
        int key = e.key();
        if (key == KEY_ESCAPE || key == KEY_ENTER || key == KEY_KP_ENTER || key == KEY_TAB) {
            unfocus();
            return true;
        }
        if (e.isSelectAll()) {
            selectAll = true;
            return true;
        }
        if (e.isPaste()) {
            String clip = Minecraft.getInstance().keyboardHandler.getClipboard().replaceAll("[\\r\\n\\t]", "");
            if (selectAll) value = "";
            selectAll = false;
            value = (value + clip).substring(0, Math.min(max, value.length() + clip.length()));
            onChange.accept(value);
            return true;
        }
        if (key == KEY_BACKSPACE) {
            if (selectAll || e.hasControlDown()) {
                value = "";
            } else if (!value.isEmpty()) {
                value = value.substring(0, value.length() - 1);
            }
            selectAll = false;
            onChange.accept(value);
            return true;
        }
        return true; // swallow other keys while typing (so E doesn't close the menu, etc.)
    }
}
