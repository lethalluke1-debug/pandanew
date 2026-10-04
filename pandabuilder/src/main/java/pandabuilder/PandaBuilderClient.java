package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaBuilderClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("pandabuilder", "main"));

    private static KeyMapping openKey;
    private static KeyMapping autoTotemKey;
    private static KeyMapping anchorAuraKey;

    @Override
    public void onInitializeClient() {
        Settings.load();
        // 344 = GLFW_KEY_RIGHT_SHIFT; -1 = unbound by default.
        openKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.pandabuilder.open", InputConstants.Type.KEYSYM, 344, CATEGORY));
        autoTotemKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.pandabuilder.autototem", InputConstants.Type.KEYSYM, -1, CATEGORY));
        anchorAuraKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.pandabuilder.anchoraura", InputConstants.Type.KEYSYM, -1, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) {
                mc.gui.setScreen(new MenuScreen());
            }
            while (autoTotemKey.consumeClick()) {
                AutoTotem.toggle();
            }
            while (anchorAuraKey.consumeClick()) {
                AnchorAura.toggle();
            }
            AutoTotem.tick(mc);
            AnchorAura.tick(mc);
        });
    }
}
