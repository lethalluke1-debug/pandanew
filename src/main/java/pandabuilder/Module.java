package pandabuilder;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;

/** One toggleable feature as shown in the menu. */
public record Module(String name, String description, Tab tab, BooleanSupplier on, Runnable toggle,
                     Supplier<KeyMapping> key, String settings) {

    public static final String STORAGE_ESP = "storage_esp";
    public static final String AUTO_MINE = "auto_mine";

    public Module(String name, String description, Tab tab, BooleanSupplier on, Runnable toggle,
                  Supplier<KeyMapping> key) {
        this(name, description, tab, on, toggle, key, null);
    }

    public enum Tab {
        PVP("PvP", "⚔"),
        CHEATING("Cheating", "◉");

        public final String title;
        public final String icon;

        Tab(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }
    }

    public boolean hasSettings() {
        return settings != null;
    }

    public boolean isOn() {
        return on.getAsBoolean();
    }
}
