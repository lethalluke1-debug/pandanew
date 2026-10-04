package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PandaBuilderClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("pandabuilder", "main"));

    private static KeyMapping openKey;

    @Override
    public void onInitializeClient() {
        Settings.load();
        openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.pandabuilder.open", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.consumeClick()) {
                client.gui.setScreen(new BuilderScreen());
            }
            BuildManager.tick(client);
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("pandabuilder", "status"), PandaBuilderClient::extractHud);
    }

    private static void extractHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (BuildManager.selected() == null) return;
        if (!BuildManager.isBuilding() && !Settings.checklistHud) return;

        int x = 4;
        int y = 4;
        String status = !BuildManager.isBuilding() ? "Schematic: " : BuildManager.isPaused() ? "Paused: " : "Building: ";
        graphics.text(client.font, status + BuildManager.selected().fileName, x, y, 0xFF55FFFF, true);
        y += 12;

        Map<Item, Integer> missing = BuildManager.missing();
        if (missing.isEmpty()) {
            graphics.text(client.font, "You have every block needed", x, y, 0xFF55FF55, true);
            return;
        }
        graphics.text(client.font, "Still need:", x, y, 0xFFFFAA00, true);
        y += 10;
        int shown = 0;
        for (Map.Entry<Item, Integer> e : missing.entrySet()) {
            if (shown++ == 6) {
                graphics.text(client.font, "+" + (missing.size() - 6) + " more", x, y + 4, 0xFFAAAAAA, true);
                break;
            }
            graphics.item(new ItemStack(e.getKey()), x, y);
            graphics.text(client.font, "x" + e.getValue(), x + 18, y + 4, 0xFFFF5555, true);
            y += 17;
        }
    }
}
