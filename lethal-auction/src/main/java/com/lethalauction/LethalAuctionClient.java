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
    private static final SoundEvent CLICK =
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("lethalauction", "click"));

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("lethalauction", "main"));

    private static KeyMapping openMenu;

    // Dev-only: -Dlethalauction.devcycle=true opens the menu on the title screen and steps through every page.
    private static final boolean DEV_CYCLE = Boolean.getBoolean("lethalauction.devcycle");
    private static final String[] DEV_PAGES = {"auction:filled", "recent:one", "hud:bid", "hud:low", "hud:win"};
    private int devTicks = -1;
    private boolean devWorldRequested;

    /** Plays the menu click sound, if module sounds are enabled. */
    public static void playClick() {
        if (LethalConfig.moduleSound) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(CLICK, 1.0f, 0.7f));
        }
    }

    @Override
    public void onInitializeClient() {
        LethalConfig.load();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lethalauction", "auction"), new AuctionHud());
        openMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lethalauction.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        // Payments from the server count as bids on the running auction. Only system messages are used,
        // so players cannot fake a payment by typing it in chat.
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay && Auctions.onServerMessage(ChatFormatting.stripFormatting(message.getString())) != null) {
                playClick();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            for (Auctions.Auction done : Auctions.tick()) {
                if (done.hasBid()) {
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
                    Auctions.onServerMessage("7SullV paid you $51.1M.");
                } else if (parts.length > 1 && parts[1].equals("low")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 9);
                } else if (parts.length > 1 && parts[1].equals("win")) {
                    Auctions.clear();
                    Auctions.start(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ELYTRA), 1, 0, 0, 1);
                    Auctions.onServerMessage("You received $51.1M from 7SullV");
                }
                mc.gui.setScreen(parts[0].equals("hud") ? null : screen);
                System.out.println("LA_PAGE " + DEV_PAGES[i].replace(':', '-'));
            } else if (i == DEV_PAGES.length) {
                LethalConfig.resetTheme();
                System.out.println("LA_DONE");
            }
        }
        devTicks++;
    }
}
