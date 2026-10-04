package pandabuilder;

import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaBuilderClient implements ClientModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("pandabuilder");
   private static final Category CATEGORY = Category.register(Identifier.fromNamespaceAndPath("pandabuilder", "main"));
   private static KeyMapping openKey;
   private static KeyMapping autoTotemKey;

   public void onInitializeClient() {
      Settings.load();
      openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pandabuilder.open", Type.KEYSYM, 344, CATEGORY));
      // Unbound by default (-1), set it in Controls if you want a hotkey.
      autoTotemKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.pandabuilder.autototem", Type.KEYSYM, -1, CATEGORY));
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         while (openKey.consumeClick()) {
            client.gui.setScreen(new BuilderScreen());
         }

         while (autoTotemKey.consumeClick()) {
            AutoTotem.toggle();
         }

         BuildManager.tick(client);
         AutoTotem.tick(client);
      });
      HudElementRegistry.attachElementBefore(
         VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("pandabuilder", "status"), PandaBuilderClient::extractHud
      );
   }

   private static void extractHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
      Minecraft client = Minecraft.getInstance();
      if (BuildManager.selected() != null) {
         if (BuildManager.isBuilding() || Settings.checklistHud) {
            int x = 4;
            int y = 4;
            String status = !BuildManager.isBuilding() ? "Schematic: " : (BuildManager.isPaused() ? "Paused: " : "Building: ");
            graphics.text(client.font, status + BuildManager.selected().fileName, x, y, -11141121, true);
            y += 12;
            Map<Item, Integer> missing = BuildManager.missing();
            if (missing.isEmpty()) {
               graphics.text(client.font, "You have every block needed", x, y, -11141291, true);
            } else {
               graphics.text(client.font, "Still need:", x, y, -22016, true);
               y += 10;
               int shown = 0;

               for (Entry<Item, Integer> e : missing.entrySet()) {
                  if (shown++ == 6) {
                     graphics.text(client.font, "+" + (missing.size() - 6) + " more", x, y + 4, -5592406, true);
                     break;
                  }

                  graphics.item(new ItemStack((ItemLike)e.getKey()), x, y);
                  graphics.text(client.font, "x" + e.getValue(), x + 18, y + 4, -43691, true);
                  y += 17;
               }
            }
         }
      }
   }
}
