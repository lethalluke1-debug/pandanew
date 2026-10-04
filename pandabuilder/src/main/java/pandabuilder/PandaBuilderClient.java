package pandabuilder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PandaBuilderClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");

    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pandabuilder.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.pandabuilder"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                client.setScreen(new BuilderScreen());
            }
            BuildManager.tick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!BuildManager.isBuilding() || BuildManager.selected() == null || client.options.hudHidden) return;

            int x = 4;
            int y = 4;
            String status = BuildManager.isPaused() ? "Paused: " : "Building: ";
            context.drawTextWithShadow(client.textRenderer, status + BuildManager.selected().fileName, x, y, 0xFF55FFFF);
            y += 12;

            Map<Item, Integer> missing = BuildManager.missing();
            if (missing.isEmpty()) {
                context.drawTextWithShadow(client.textRenderer, "You have every block needed", x, y, 0xFF55FF55);
                return;
            }
            context.drawTextWithShadow(client.textRenderer, "Still need:", x, y, 0xFFFFAA00);
            y += 10;
            int shown = 0;
            for (Map.Entry<Item, Integer> e : missing.entrySet()) {
                if (shown++ == 6) {
                    context.drawTextWithShadow(client.textRenderer, "+" + (missing.size() - 6) + " more", x, y + 4, 0xFFAAAAAA);
                    break;
                }
                context.drawItem(new ItemStack(e.getKey()), x, y);
                context.drawTextWithShadow(client.textRenderer, "x" + e.getValue(), x + 18, y + 4, 0xFFFF5555);
                y += 17;
            }
        });
    }
}
