package com.lethalauction;

import com.lethalauction.gui.LethalScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class LethalAuctionClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("lethalauction", "main"));

    private static KeyMapping openMenu;

    // Dev-only: -Dlethalauction.devcycle=true opens the menu on the title screen and steps through every page.
    private static final boolean DEV_CYCLE = Boolean.getBoolean("lethalauction.devcycle");
    private static final String[] DEV_PAGES = {"combat", "movement", "donut", "visuals", "misc", "settings", "configs", "theme", "socials"};
    private int devTicks = -1;

    @Override
    public void onInitializeClient() {
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
                mc.gui.setScreen(new LethalScreen().showPage(DEV_PAGES[i]));
                System.out.println("LA_PAGE " + DEV_PAGES[i]);
            } else if (i == DEV_PAGES.length) {
                System.out.println("LA_DONE");
            }
        }
        devTicks++;
    }
}
