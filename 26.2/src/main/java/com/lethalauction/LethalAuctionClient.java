/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 *  net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.KeyMapping$Category
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.model.player.PlayerModel
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.entity.player.AvatarRenderer
 *  net.minecraft.client.renderer.entity.state.AvatarRenderState
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.server.IntegratedServer
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.lethalauction;

import com.lethalauction.auction.Auctions;
import com.lethalauction.auction.Transactions;
import com.lethalauction.m.K12;
import com.lethalauction.m.K15;
import com.lethalauction.m.K16;
import com.lethalauction.m.K19;
import com.lethalauction.m.K20;
import com.lethalauction.m.K21;
import com.lethalauction.m.K22;
import com.lethalauction.m.K23;
import com.lethalauction.m.K25;
import com.lethalauction.m.K27;
import com.lethalauction.m.K30;
import com.lethalauction.m.K33;
import com.lethalauction.m.K34;
import com.lethalauction.m.K35;
import com.lethalauction.m.K37;
import com.lethalauction.m.K38;
import com.lethalauction.m.K4;
import com.lethalauction.m.K40;
import com.lethalauction.m.K46;
import com.lethalauction.m.K5;
import com.lethalauction.m.K53;
import com.lethalauction.m.K55;
import com.lethalauction.m.K58;
import com.lethalauction.m.K59;
import com.lethalauction.m.K61;
import com.lethalauction.m.K7;
import com.lethalauction.m.K9;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LethalAuctionClient
implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"lethalauction");
    private static final SoundEvent CLICK = LethalAuctionClient.sound("click");
    private static final SoundEvent COIN = LethalAuctionClient.sound("coin");
    private static SimpleSoundInstance lastClick;
    private static SimpleSoundInstance lastCoin;
    private static final KeyMapping.Category CATEGORY;
    private static KeyMapping openMenu;
    private static KeyMapping figureWaypoint;
    private static final boolean DEV_CYCLE;
    private static final String[] DEV_PAGES;
    private int devTicks = -1;
    private boolean devWorldRequested;
    private boolean licenseChecked;
    private boolean wasMaintenance;
    private boolean greeted;

    public static KeyMapping openMenu() {
        return openMenu;
    }

    public static KeyMapping figureWaypoint() {
        return figureWaypoint;
    }

    private static SoundEvent sound(String name) {
        return SoundEvent.createVariableRangeEvent((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)name));
    }

    private static void onBidResult(Auctions.C67 r, String original) {
        if (r.a() == Auctions.C67.C118.UNREADABLE) {
            LOGGER.warn("Unrecognised payment message: {}", (Object)original);
        } else {
            LethalAuctionClient.playCoin();
        }
    }

    public static void playClick() {
        if (K9.g) {
            SoundManager sounds = Minecraft.getInstance().getSoundManager();
            if (lastClick != null) {
                sounds.stop((SoundInstance)lastClick);
            }
            if (!(K9.h <= 0.0f)) {
                lastClick = SimpleSoundInstance.forUI((SoundEvent)CLICK, (float)1.0f, (float)(0.5f * K9.h));
                sounds.play((SoundInstance)lastClick);
            }
        }
    }

    public static void playHover() {
        if (!K9.g || K9.h <= 0.0f) {
            return;
        }
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)CLICK, (float)1.6f, (float)(0.2f * K9.h)));
    }

    public static void playCoin() {
        SoundManager sounds = Minecraft.getInstance().getSoundManager();
        if (lastCoin != null) {
            sounds.stop((SoundInstance)lastCoin);
        }
        if (!(K9.h <= 0.0f)) {
            lastCoin = SimpleSoundInstance.forUI((SoundEvent)COIN, (float)1.0f, (float)(0.35f * K9.h));
            sounds.play((SoundInstance)lastCoin);
        }
    }

    public void onInitializeClient() {
        K9.a();
        K37.s();
        Auctions.a();
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"auction"), (HudElement)new K15());
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"profit"), (HudElement)new K27());
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"stats"), (HudElement)new K30());
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"music"), (HudElement)new K25());
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"stream"), (HudElement)new K59());
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"wheel"), (HudElement)new K61());
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.CROSSHAIR, K16::new);
        LevelRenderEvents.COLLECT_SUBMITS.register(K53::a);
        LevelRenderEvents.COLLECT_SUBMITS.register(K12::a);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
            if (renderer instanceof AvatarRenderer) {
                AvatarRenderer avatar = (AvatarRenderer)renderer;
                helper.register((RenderLayer)new K7((RenderLayerParent<AvatarRenderState, PlayerModel>)((RenderLayerParent)avatar)));
            }
        });
        K5.a();
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen) {
                if (K33.a()) {
                    Screens.getWidgets((Screen)screen).removeIf(b -> b.getMessage() != null && b.getMessage().getString().toLowerCase().contains("copyright"));
                }
                ScreenMouseEvents.allowMouseClick((Screen)screen).register((scr, event) -> event.button() != 0 || !K33.a(event.x(), event.y(), scr.width, scr.height));
            }
            if (!(screen instanceof K20 || screen instanceof K22 || screen instanceof K23 || screen instanceof K34 || screen instanceof K19)) {
                ScreenEvents.afterForeground((Screen)screen).register((scr, graphics, mx, my, delta) -> {
                    K25.a(graphics);
                    K59.a(graphics);
                    K61.a(graphics);
                });
            }
        });
        openMenu = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.lethalauction.open", InputConstants.Type.KEYSYM, 344, CATEGORY));
        K40.a = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.lethalauction.freelook", InputConstants.Type.KEYSYM, 342, CATEGORY));
        K40.b = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.lethalauction.zoom", InputConstants.Type.KEYSYM, 67, CATEGORY));
        figureWaypoint = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.lethalauction.waypoint", InputConstants.Type.KEYSYM, 86, CATEGORY));
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> K35.a(command));
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (!K9.hideSmall || K9.hideSmallStr.isBlank()) {
                return true;
            }
            try {
                String plain = ChatFormatting.stripFormatting((String)message.getString());
                if (plain == null) {
                    return true;
                }
                long amt = Transactions.matchAmount(plain);
                if (amt < 0L) {
                    return true;
                }
                long thr = Auctions.e(K9.hideSmallStr);
                if (thr > 0L && amt <= thr) {
                    return false;
                }
            } catch (Exception e) {
                // never swallow other chat
            }
            return true;
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String plain = ChatFormatting.stripFormatting((String)message.getString());
            if (plain != null) {
                Auctions.C67 result;
                if (K37.b()) {
                    Transactions.a(plain);
                    K55.a(plain);
                }
                if ((result = Auctions.b(plain)) != null) {
                    LethalAuctionClient.onBidResult(result, plain);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            for (Auctions.C66 done : Auctions.e()) {
                if (!done.hasBid()) continue;
                mc.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, (float)1.0f, (float)0.8f));
            }
            this.licenseTick(mc);
            K30.a(mc);
            K5.a(mc);
            K40.a(mc);
            K46.a(mc);
            K58.k();
            K55.s();
            K38.a(mc);
            K12.d(mc);
            while (figureWaypoint.consumeClick()) {
                K12.a(mc);
            }
            while (openMenu.consumeClick()) {
                if (K37.g()) {
                    if (mc.gui.screen() instanceof K34) continue;
                    mc.gui.setScreen((Screen)new K34());
                    continue;
                }
                if (K37.a() && K37.k()) {
                    if (mc.gui.screen() instanceof K23) continue;
                    mc.gui.setScreen((Screen)new K23());
                    continue;
                }
                if (!(K37.d() || this.greeted && K37.a())) {
                    if (mc.gui.screen() instanceof K22) continue;
                    this.greeted = true;
                    mc.gui.setScreen((Screen)new K22(mc.gui.screen(), K20::new));
                    continue;
                }
                if (mc.gui.screen() instanceof K20) continue;
                this.greeted = true;
                mc.gui.setScreen((Screen)new K20());
            }
            if (DEV_CYCLE) {
                this.devTick(mc);
            }
        });
    }

    private void licenseTick(Minecraft mc) {
        boolean maintenance;
        if (!this.licenseChecked && mc.gui.overlay() == null) {
            this.licenseChecked = true;
            K37.t();
        }
        boolean bl = maintenance = K37.a() && K37.k();
        if (maintenance != this.wasMaintenance) {
            this.wasMaintenance = maintenance;
            if (maintenance) {
                K21.a("Lethal Client", "Under maintenance", 10000L);
            } else {
                K21.a("Lethal Client", "Back online");
            }
            if (maintenance && mc.gui.screen() instanceof K20) {
                mc.gui.setScreen((Screen)new K23());
            }
        }
        if (K37.r() && !K37.d()) {
            if (K37.g()) {
                if (mc.gui.screen() instanceof K20) {
                    mc.gui.setScreen((Screen)new K34());
                }
            } else {
                K21.a("Lethal Client locked", K37.m());
                if (mc.gui.screen() instanceof K20 || mc.gui.screen() instanceof K34) {
                    mc.gui.setScreen((Screen)new K22(null, K20::new));
                }
            }
        }
    }

    private void devTick(Minecraft mc) {
        if (this.devTicks < 0) {
            if (mc.level != null && mc.player != null && mc.gui.screen() == null && mc.gui.overlay() == null) {
                this.devTicks = 0;
            } else if (mc.gui.screen() instanceof TitleScreen && mc.gui.overlay() == null && !this.devWorldRequested) {
                this.devWorldRequested = true;
                System.out.println("LA_TITLE");
            }
        } else {
            if (this.devTicks % 120 == 0) {
                int i = this.devTicks / 120;
                if (i < DEV_PAGES.length) {
                    String[] parts = DEV_PAGES[i].split(":");
                    K20 screen = new K20().a(parts[0]);
                    if (parts.length > 1 && parts[1].equals("filled")) {
                        screen.a();
                    } else if (parts.length > 1 && parts[1].equals("one")) {
                        Auctions.a(new ItemStack((ItemLike)Items.DIAMOND), 64, 12500L, 300L, 90);
                        Auctions.b("7SullV paid you $15.2K.");
                    } else if (parts.length > 1 && parts[1].equals("bid")) {
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 45000000L, 377000000L, 90);
                        Auctions.b("\u00bb 7SullV \u1d18\u1d00\u026a\u1d05 \u028f\u1d0f\u1d1c $51.1\u1d0d.");
                    } else if (parts.length > 1 && parts[1].equals("low")) {
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 0L, 0L, 7);
                    } else if (parts.length > 1 && parts[1].equals("seed")) {
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.DIAMOND), 64, 10000L, 500L, 1);
                        Auctions.b("You received $12,500 from 7SullV");
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 45000000L, 0L, 1);
                        K9.b = 4;
                        K9.b();
                    } else if (parts.length > 1 && parts[1].equals("select")) {
                        K4.h();
                        K4.a("Lethal Test.png");
                    } else if (parts.length > 1 && parts[1].equals("crop")) {
                        K4.a(mc.gameDirectory.toPath().resolve("test-photo.png"));
                    } else if (parts.length > 1 && parts[1].equals("timer")) {
                        K9.k = true;
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 0L, 0L, 125);
                        Auctions.b("You received $51.1M from 7SullV");
                        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                    } else if (parts.length > 1 && parts[1].equals("sb")) {
                        IntegratedServer server = mc.getSingleplayerServer();
                        if (server != null) {
                            server.execute(() -> {
                                CommandSourceStack src = server.createCommandSourceStack();
                                for (String cmd : new String[]{"scoreboard objectives add lethal dummy {\"text\":\"LethalLukeYT\",\"color\":\"white\"}", "scoreboard objectives setdisplay sidebar lethal", "scoreboard players set A lethal 1", "scoreboard players set B lethal 2"}) {
                                    server.getCommands().performPrefixedCommand(src, cmd);
                                }
                            });
                        }
                        K9.aS = 1.3f;
                        K9.aT = true;
                        K9.aX = 0;
                        K9.aU = true;
                        mc.options.setCameraType(CameraType.FIRST_PERSON);
                    } else if (parts.length > 1 && parts[1].equals("capewin")) {
                        K9.k = true;
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 0L, 0L, 1);
                        Auctions.b("You received $51.1M from LethalLukeYT");
                        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                    } else if (parts.length > 1 && parts[1].equals("head")) {
                        K9.aU = true;
                        K9.aT = true;
                        K9.aX = 1;
                    } else if (parts.length > 1 && parts[1].equals("zoom")) {
                        K4.a(mc.gameDirectory.toPath().resolve("test-photo.png"));
                        if (K4.b() != null) {
                            K4.b().a(2.5f);
                        }
                    } else if (parts.length > 1 && parts[1].equals("back")) {
                        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                    } else if (parts.length > 1 && parts[1].equals("win")) {
                        Auctions.g();
                        Auctions.a(new ItemStack((ItemLike)Items.ELYTRA), 1, 0L, 0L, 1);
                        Auctions.b("You received $51.1M from 7SullV");
                    }
                    mc.gui.setScreen(!parts[0].equals("hud") && !parts[0].equals("view") ? screen : null);
                    System.out.println("LA_PAGE " + DEV_PAGES[i].replace(':', '-'));
                } else if (i == DEV_PAGES.length) {
                    System.out.println("LA_DONE");
                }
            }
            ++this.devTicks;
        }
    }

    static {
        CATEGORY = KeyMapping.Category.register((Identifier)Identifier.fromNamespaceAndPath((String)"lethalauction", (String)"main"));
        DEV_CYCLE = Boolean.getBoolean("lethalauction.devcycle");
        DEV_PAGES = System.getProperty("lethalauction.devpages", "hud:bid,hud:low,hud:win").split(",");
    }
}

