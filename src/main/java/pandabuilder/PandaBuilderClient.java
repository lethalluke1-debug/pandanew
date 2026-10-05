package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
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
    private static KeyMapping espKey;
    private static KeyMapping autoMineKey;
    private static KeyMapping freecamKey;

    @Override
    public void onInitializeClient() {
        Settings.load();

        openKey = register("key.pandabuilder.open", InputConstants.KEY_RSHIFT);
        autoTotemKey = register("key.pandabuilder.autototem", InputConstants.UNKNOWN.getValue());
        autoXpKey = register("key.pandabuilder.autoxp", InputConstants.UNKNOWN.getValue());
        autoCrystalKey = register("key.pandabuilder.autocrystal", InputConstants.UNKNOWN.getValue());
        espKey = register("key.pandabuilder.esp", InputConstants.UNKNOWN.getValue());
        autoMineKey = register("key.pandabuilder.automine", InputConstants.UNKNOWN.getValue());
        freecamKey = register("key.pandabuilder.freecam", InputConstants.UNKNOWN.getValue());

        ClientTickEvents.END_CLIENT_TICK.register(PandaBuilderClient::tick);
        HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath("pandabuilder", "esp"), EspHud::extractRenderState);
    }

    private static KeyMapping register(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, InputConstants.Type.KEYSYM, key, CATEGORY));
    }

    public static KeyMapping autoTotemKey() { return autoTotemKey; }
    public static KeyMapping autoXpKey() { return autoXpKey; }
    public static KeyMapping autoCrystalKey() { return autoCrystalKey; }
    public static KeyMapping espKey() { return espKey; }
    public static KeyMapping autoMineKey() { return autoMineKey; }
    public static KeyMapping freecamKey() { return freecamKey; }

    private static void tick(Minecraft mc) {
        if (openKey.consumeClick()) mc.gui.setScreen(new MenuScreen());
        if (autoTotemKey.consumeClick()) AutoTotem.toggle();
        if (autoXpKey.consumeClick()) AutoXP.toggle();
        if (autoCrystalKey.consumeClick()) AutoCrystal.toggle();
        if (espKey.consumeClick()) Esp.toggle();
        if (autoMineKey.consumeClick()) AutoMine.toggle();
        if (freecamKey.consumeClick()) Freecam.toggle();

        AutoTotem.tick(mc);
        AutoXP.tick(mc);
        AutoCrystal.tick(mc);
        AutoMine.tick(mc);
        Freecam.tick(mc);
    }
}
