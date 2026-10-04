package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaBuilderClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("pandabuilder", "main"));

    private static KeyMapping openKey;
    private static KeyMapping autoTotemKey;
    private static KeyMapping autoXpKey;
    private static KeyMapping autoCrystalKey;

    @Override
    public void onInitializeClient() {
        Settings.load();

        openKey = register("key.pandabuilder.open", InputConstants.KEY_RSHIFT);
        autoTotemKey = register("key.pandabuilder.autototem", InputConstants.UNKNOWN.getValue());
        autoXpKey = register("key.pandabuilder.autoxp", InputConstants.UNKNOWN.getValue());
        autoCrystalKey = register("key.pandabuilder.autocrystal", InputConstants.UNKNOWN.getValue());

        ClientTickEvents.END_CLIENT_TICK.register(PandaBuilderClient::tick);
    }

    private static KeyMapping register(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, InputConstants.Type.KEYSYM, key, CATEGORY));
    }

    private static void tick(Minecraft mc) {
        if (openKey.consumeClick()) mc.gui.setScreen(new MenuScreen());
        if (autoTotemKey.consumeClick()) AutoTotem.toggle();
        if (autoXpKey.consumeClick()) AutoXP.toggle();
        if (autoCrystalKey.consumeClick()) AutoCrystal.toggle();

        AutoTotem.tick(mc);
        AutoXP.tick(mc);
        AutoCrystal.tick(mc);
    }
}
