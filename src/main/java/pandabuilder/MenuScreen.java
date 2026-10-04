package pandabuilder;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MenuScreen extends Screen {
    public MenuScreen() {
        super(Component.literal("Panda Builder"));
    }

    @Override
    protected void init() {
        int w = 180;
        int x = (this.width - w) / 2;
        int y = this.height / 2 - 40;

        addRenderableWidget(Button.builder(label("Auto Totem", Settings.autoTotem), b -> {
            AutoTotem.toggle();
            b.setMessage(label("Auto Totem", Settings.autoTotem));
        }).bounds(x, y, w, 20).build());

        addRenderableWidget(Button.builder(label("Auto XP", AutoXP.isOn()), b -> {
            AutoXP.toggle();
            b.setMessage(label("Auto XP", AutoXP.isOn()));
        }).bounds(x, y + 24, w, 20).build());

        addRenderableWidget(Button.builder(label("Auto Crystal", AutoCrystal.isOn()), b -> {
            AutoCrystal.toggle();
            b.setMessage(label("Auto Crystal", AutoCrystal.isOn()));
        }).bounds(x, y + 48, w, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(x, y + 72, w, 20).build());
    }

    private static Component label(String name, boolean on) {
        return Component.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
