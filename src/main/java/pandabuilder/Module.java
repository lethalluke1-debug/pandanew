package pandabuilder;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;

/** One toggleable feature as shown in the menu. */
public record Module(String name, String description, Tab tab, BooleanSupplier on, Runnable toggle,
                     Supplier<KeyMapping> key, boolean hasSettings) {

    public Module(String name, String description, Tab tab, BooleanSupplier on, Runnable toggle,
                  Supplier<KeyMapping> key) {
        this(name, description, tab, on, toggle, key, false);
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

    public boolean isOn() {
        return on.getAsBoolean();
    }
}
