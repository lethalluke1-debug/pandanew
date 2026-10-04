package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaBuilderClient implements ClientModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");
   private static final Category CATEGORY = Category.register(Identifier.fromNamespaceAndPath("pandabuilder", "main"));
   private static KeyMapping openKey;
   private static KeyMapping autoTotemKey;

   public void onInitializeClient() {
      Settings.load();
      // Right Shift opens the menu, same key as before.
      openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pandabuilder.open", Type.KEYSYM, 344, CATEGORY));
      // Unbound by default (-1), set it in Controls if you want a hotkey.
      autoTotemKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pandabuilder.autototem", Type.KEYSYM, -1, CATEGORY));
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         while (openKey.consumeClick()) {
            client.gui.setScreen(new MenuScreen());
         }

         while (autoTotemKey.consumeClick()) {
            AutoTotem.toggle();
         }

         AutoTotem.tick(client);
      });
   }
}
