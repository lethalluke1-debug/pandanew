package com.lethalauction;

import com.lethalauction.gui.LethalScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
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
    private static final String[] DEV_PAGES = {"auction", "settings", "recent", "theme", "theme:electric", "auction:flat"};
    private int devTicks = -1;

    /** Plays the menu click sound, if module sounds are enabled. */
    public static void playClick() {
        if (LethalConfig.moduleSound) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(CLICK, 1.0f, 0.7f));
        }
    }

    @Override
    public void onInitializeClient() {
        LethalConfig.load();
        openMenu = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lethalauction.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
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
            if (mc.gui.screen() instanceof TitleScreen) {
                devTicks = 0;
            }
            return;
        }
        if (devTicks % 120 == 0) {
            int i = devTicks / 120;
            if (i < DEV_PAGES.length) {
                String[] parts = DEV_PAGES[i].split(":");
                if (parts.length > 1 && parts[1].equals("electric")) {
                    LethalConfig.preset = 2;
                    LethalConfig.seeThrough = true;
                } else if (parts.length > 1 && parts[1].equals("flat")) {
                    LethalConfig.ambientBackground = false;
                    LethalConfig.frostedBlur = false;
                }
                mc.gui.setScreen(new LethalScreen().showPage(parts[0]));
                System.out.println("LA_PAGE " + DEV_PAGES[i].replace(':', '-'));
            } else if (i == DEV_PAGES.length) {
                LethalConfig.resetTheme();
                System.out.println("LA_DONE");
            }
        }
        devTicks++;
    }
}
