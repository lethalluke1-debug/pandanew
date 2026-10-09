package donutauction;

import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.Game;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DonutAuctionClient implements ClientModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("donutauction");
   private static final long SEND_GAP_MS = 1500L;
   private static final Deque<String> OUTBOX = new ArrayDeque<>();
   private static long lastSent;
   private static KeyMapping openKey;

   public void onInitializeClient() {
      donutauction.Config.load();
      Category var1 = Category.register(Identifier.fromNamespaceAndPath("donutauction", "main"));
      openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.donutauction.open", Type.KEYSYM, 344, var1));
      ClientTickEvents.END_CLIENT_TICK.register(donutauction.DonutAuctionClient::tick);
      ClientReceiveMessageEvents.GAME.register((Game)(var0, var1x) -> {
         if (!var1x) {
            donutauction.Auction.onGameMessage(var0.getString());
         }
      });
      HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("donutauction", "hud"), donutauction.AuctionHud::extractRenderState);
   }

   private static void tick(Minecraft var0) {
      while (openKey.consumeClick()) {
         var0.gui.setScreen(new donutauction.AuctionScreen());
      }

      donutauction.Auction.tick();
      if (var0.getConnection() == null) {
         OUTBOX.clear();
      } else {
         if (!OUTBOX.isEmpty() && System.currentTimeMillis() - lastSent >= 1500L) {
            String var1 = OUTBOX.poll();
            if (var1.startsWith("/")) {
               var0.getConnection().sendCommand(var1.substring(1));
            } else {
               var0.getConnection().sendChat(var1);
            }

            lastSent = System.currentTimeMillis();
         }
      }
   }

   public static void queueChat(String var0) {
      OUTBOX.add(var0.startsWith("/") ? " " + var0 : var0);
   }

   public static void queueCommand(String var0) {
      OUTBOX.add("/" + var0);
   }
}
