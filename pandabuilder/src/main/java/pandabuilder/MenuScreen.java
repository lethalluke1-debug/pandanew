package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MenuScreen extends Screen {
    public MenuScreen() {
        super(Component.literal("Panda Builder"));
    }

    @Override
    protected void init() {
        int buttonWidth = 160;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 2 - 20;
        addRenderableWidget(Button.builder(autoTotemLabel(), button -> {
            AutoTotem.toggle();
            button.setMessage(autoTotemLabel());
        }).bounds(x, y, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(anchorAuraLabel(), button -> {
            AnchorAura.toggle();
            button.setMessage(anchorAuraLabel());
        }).bounds(x, y + 24, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(x, y + 52, buttonWidth, 20).build());
    }

    private static Component autoTotemLabel() {
        return Component.literal("Auto Totem: " + (Settings.autoTotem ? "§aON" : "§cOFF"));
    }

    private static Component anchorAuraLabel() {
        return Component.literal("Anchor Aura: " + (Settings.anchorAura ? "§aON" : "§cOFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        String title = "Panda Builder";
        graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, this.height / 2 - 50, -1, true);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String totems = "Totems: " + AutoTotem.countTotems(mc.player.getInventory());
            graphics.item(new ItemStack(Items.TOTEM_OF_UNDYING),
                    (this.width - this.font.width(totems)) / 2 - 20, this.height / 2 - 38);
            graphics.text(this.font, totems, (this.width - this.font.width(totems)) / 2, this.height / 2 - 34,
                    0xFFFFFF55, true);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
