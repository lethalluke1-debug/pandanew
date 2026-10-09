package donutauction;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayDeque;
import java.util.Deque;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DonutAuctionClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("donutauction");

    /** Gap between messages/commands this mod sends, so it doesn't trip the server's spam filter. */
    private static final long SEND_GAP_MS = 1500;
    private static final Deque<String> OUTBOX = new ArrayDeque<>();
    private static long lastSent;

    private static KeyMapping openKey;

    @Override
    public void onInitializeClient() {
        Config.load();
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("donutauction", "main"));
        openKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.donutauction.open", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(DonutAuctionClient::tick);
        // Game (system) messages only: player chat can't pose as a payment.
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) Auction.onGameMessage(message.getString());
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("donutauction", "hud"), AuctionHud::extractRenderState);
    }

    private static void tick(Minecraft mc) {
        while (openKey.consumeClick()) mc.gui.setScreen(new AuctionScreen());
        Auction.tick();
        if (mc.getConnection() == null) {
            OUTBOX.clear();
            return;
        }
        if (!OUTBOX.isEmpty() && System.currentTimeMillis() - lastSent >= SEND_GAP_MS) {
            String next = OUTBOX.poll();
            if (next.startsWith("/")) {
                mc.getConnection().sendCommand(next.substring(1));
            } else {
                mc.getConnection().sendChat(next);
            }
            lastSent = System.currentTimeMillis();
        }
    }

    public static void queueChat(String message) {
        OUTBOX.add(message.startsWith("/") ? " " + message : message);
    }

    public static void queueCommand(String command) {
        OUTBOX.add("/" + command);
    }
}
