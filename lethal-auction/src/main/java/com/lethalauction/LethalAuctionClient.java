package com.lethalauction;

import com.lethalauction.auction.Auctions;
import com.lethalauction.gui.AuctionHud;
import com.lethalauction.gui.LethalScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LethalAuctionClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("lethalauction");
    private static final SoundEvent CLICK = sound("click");
    private static final SoundEvent COIN = sound("coin");
    private static SimpleSoundInstance lastClick;
    private static SimpleSoundInstance lastCoin;

    private static SoundEvent sound(String name) {
        return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("lethalauction", name));
    }

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("lethalauction", "main"));

    private static KeyMapping openMenu;

    // Dev-only: -Dlethalauction.devcycle=true opens the menu on the title screen and steps through every page.
    private static final boolean DEV_CYCLE = Boolean.getBoolean("lethalauction.devcycle");
    private static final String[] DEV_PAGES =
            System.getProperty("lethalauction.devpages", "hud:bid,hud:low,hud:win").split(",");
    private int devTicks = -1;
    private boolean devWorldRequested;

    /** Plays a sound for an accepted bid and logs payments the mod could not read. */
    private static void onBidResult(Auctions.BidResult r, String original) {
        if (r.kind() == Auctions.BidResult.Kind.UNREADABLE) {
            LOGGER.warn("Unrecognised payment message: {}", original);
        } else {
            // Every payment during an auction gets the coin sound, even if it was not a new top bid.
            playCoin();
        }
    }

    /** Plays the menu click sound, if module sounds are enabled. */
    public static void playClick() {
        if (!LethalConfig.moduleSound) {
            return;
        }
        var sounds = Minecraft.getInstance().getSoundManager();
        // Stop the previous click first so fast clicks never stack on top of each other.
        if (lastClick != null) {
            sounds.stop(lastClick);
        }
        if (LethalConfig.volume <= 0) {
            return;
        }
        lastClick = SimpleSoundInstance.forUI(CLICK, 1.0f, 0.5f * LethalConfig.volume);
        sounds.play(lastClick);
    }

    /** Plays the coin sound for an accepted payment (always on, it is an alert rather than a click). */
    public static void playCoin() {
        var sounds = Minecraft.getInstance().getSoundManager();
        // Restart the chime for each payment so back-to-back payments are always heard.
        if (lastCoin != null) {
            sounds.stop(lastCoin);
        }
        if (LethalConfig.volume <= 0) {
            return;
        }
        lastCoin = SimpleSoundInstance.forUI(COIN, 1.0f, 0.35f * LethalConfig.volume);
        sounds.play(lastCoin);
    }

    @Override
    public void onInitializeClient() {
        LethalConfig.load();
        Auctions.load();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lethalauction", "auction"), new AuctionHud());
        openMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lethalauction.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        // Payments from the server count as bids on the running auction. Only system messages are used,
        // so players cannot fake a payment by typing it in chat.
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String plain = ChatFormatting.stripFormatting(message.getString());
            if (plain == null) {
                return;
            }
            Auctions.BidResult result = Auctions.onServerMessage(plain);
            if (result != null) {
                onBidResult(result, plain);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            for (Auctions.Auction done : Auctions.tick()) {
                if (done.hasBid()) {
                    mc.keyboardHandler.setClipboard(done.topBidder());
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 0.8f));
                }
            }
            while (openMenu.consumeClick()) {
                if (!(mc.gui.screen() instanceof LethalScreen)) {
                    mc.gui.setScreen(new LethalScreen());
                }
            }
            if (DEV_CYCLE) {
                devTick(mc);
            }
        });
    }

    private void devTick(Minecraft mc) {
        if (devTicks < 0) {
            if (mc.level != null && mc.player != null && mc.gui.screen() == null && mc.gui.overlay() == null) {
                devTicks = 0;
            } else if (mc.gui.screen() instanceof TitleScreen && mc.gui.overlay() == null && !devWorldRequested) {
                devWorldRequested = true;
                System.out.println("LA_TITLE");
            }
            return;
        }
        if (devTicks % 120 == 0) {
            int i = devTicks / 120;
            if (i < DEV_PAGES.length) {
                String[] parts = DEV_PAGES[i].split(":");
                LethalScreen screen = new LethalScreen().showPage(parts[0]);
                if (parts.length > 1 && parts[1].equals("filled")) {
                    screen.devFillForm();
                } else if (parts.length > 1 && parts[1].equals("one")) {
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND), 64, 12_500, 300, 90);
                    Auctions.onServerMessage("7SullV paid you $15.2K.");
                } else if (parts.length > 1 && parts[1].equals("bid")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 45_000_000, 377_000_000, 90);
                    Auctions.onServerMessage("» 7SullV ᴘᴀɪᴅ ʏᴏᴜ $51.1ᴍ.");
                } else if (parts.length > 1 && parts[1].equals("low")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 7);
                } else if (parts.length > 1 && parts[1].equals("seed")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND), 64, 10_000, 500, 1);
                    Auctions.onServerMessage("You received $12,500 from 7SullV");
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 45_000_000, 0, 1);
                    LethalConfig.preset = 4;
                    LethalConfig.save();
                } else if (parts.length > 1 && parts[1].equals("select")) {
                    com.lethalauction.cape.Capes.rescan();
                    com.lethalauction.cape.Capes.select("Lethal Test.png");
                } else if (parts.length > 1 && parts[1].equals("crop")) {
                    com.lethalauction.cape.Capes.devOpenCropper(mc.gameDirectory.toPath().resolve("test-photo.png"));
                } else if (parts.length > 1 && parts[1].equals("timer")) {
                    LethalConfig.auctionCape = true;
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 125);
                    Auctions.onServerMessage("You received $51.1M from 7SullV");
                    mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
                } else if (parts.length > 1 && parts[1].equals("sb")) {
                    var server = mc.getSingleplayerServer();
                    if (server != null) {
                        server.execute(() -> {
                            var src = server.createCommandSourceStack();
                            for (String cmd : new String[] {
                                    "scoreboard objectives add lethal dummy {\"text\":\"LethalLukeYT\",\"color\":\"white\"}",
                                    "scoreboard objectives setdisplay sidebar lethal",
                                    "scoreboard players set A lethal 1", "scoreboard players set B lethal 2"}) {
                                server.getCommands().performPrefixedCommand(src, cmd);
                            }
                        });
                    }
                    LethalConfig.sbScale = 1.3f;
                    LethalConfig.sbRounded = true;
                    LethalConfig.sbBackground = 0;
                    LethalConfig.sbHead = true;
                    mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
                } else if (parts.length > 1 && parts[1].equals("capewin")) {
                    LethalConfig.auctionCape = true;
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 1);
                    Auctions.onServerMessage("You received $51.1M from LethalLukeYT");
                    mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
                } else if (parts.length > 1 && parts[1].equals("head")) {
                    LethalConfig.sbHead = true;
                    LethalConfig.sbRounded = true;
                    LethalConfig.sbBackground = 1;
                } else if (parts.length > 1 && parts[1].equals("zoom")) {
                    com.lethalauction.cape.Capes.devOpenCropper(mc.gameDirectory.toPath().resolve("test-photo.png"));
                    if (com.lethalauction.cape.Capes.pending() != null) {
                        com.lethalauction.cape.Capes.pending().setZoom(2.5f);
                    }
                } else if (parts.length > 1 && parts[1].equals("back")) {
                    mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
                } else if (parts.length > 1 && parts[1].equals("win")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 1);
                    Auctions.onServerMessage("You received $51.1M from 7SullV");
                }
                mc.gui.setScreen(parts[0].equals("hud") || parts[0].equals("view") ? null : screen);
                System.out.println("LA_PAGE " + DEV_PAGES[i].replace(':', '-'));
            } else if (i == DEV_PAGES.length) {
                System.out.println("LA_DONE");
            }
        }
        devTicks++;
    }
}
