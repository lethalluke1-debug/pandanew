/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 *  net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1132
 *  net.minecraft.class_1144
 *  net.minecraft.class_124
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2168
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_5498
 *  net.minecraft.class_591
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
import com.lethalauction.m.K52;
import com.lethalauction.m.K54;
import com.lethalauction.m.K57;
import com.lethalauction.m.K58;
import com.lethalauction.m.K60;
import com.lethalauction.m.K7;
import com.lethalauction.m.K9;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1132;
import net.minecraft.class_1144;
import net.minecraft.class_124;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2168;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3675;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_5498;
import net.minecraft.class_591;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LethalAuctionClient
implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"lethalauction");
    private static final class_3414 CLICK = LethalAuctionClient.sound("click");
    private static final class_3414 COIN = LethalAuctionClient.sound("coin");
    private static class_1109 lastClick;
    private static class_1109 lastCoin;
    private static final class_304.class_11900 CATEGORY;
    private static class_304 openMenu;
    private static class_304 figureWaypoint;
    private static final boolean DEV_CYCLE;
    private static final String[] DEV_PAGES;
    private int devTicks = -1;
    private boolean devWorldRequested;
    private boolean licenseChecked;
    private boolean wasMaintenance;
    private boolean greeted;

    public static class_304 openMenu() {
        return openMenu;
    }

    public static class_304 figureWaypoint() {
        return figureWaypoint;
    }

    private static class_3414 sound(String name) {
        return class_3414.method_47908((class_2960)class_2960.method_60655((String)"lethalauction", (String)name));
    }

    private static void onBidResult(Auctions.C66 r, String original) {
        if (r.a() == Auctions.C66.C113.UNREADABLE) {
            LOGGER.warn("Unrecognised payment message: {}", (Object)original);
        } else {
            LethalAuctionClient.playCoin();
        }
    }

    public static void playClick() {
        if (K9.g) {
            class_1144 sounds = class_310.method_1551().method_1483();
            if (lastClick != null) {
                sounds.method_4870((class_1113)lastClick);
            }
            if (!(K9.h <= 0.0f)) {
                lastClick = class_1109.method_4757((class_3414)CLICK, (float)1.0f, (float)(0.5f * K9.h));
                sounds.method_4873((class_1113)lastClick);
            }
        }
    }

    public static void playHover() {
        if (!K9.g || K9.h <= 0.0f) {
            return;
        }
        class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)CLICK, (float)1.6f, (float)(0.2f * K9.h)));
    }

    public static void playCoin() {
        class_1144 sounds = class_310.method_1551().method_1483();
        if (lastCoin != null) {
            sounds.method_4870((class_1113)lastCoin);
        }
        if (!(K9.h <= 0.0f)) {
            lastCoin = class_1109.method_4757((class_3414)COIN, (float)1.0f, (float)(0.35f * K9.h));
            sounds.method_4873((class_1113)lastCoin);
        }
    }

    public void onInitializeClient() {
        K9.a();
        K37.s();
        Auctions.a();
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"auction"), (HudElement)new K15());
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"profit"), (HudElement)new K27());
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"stats"), (HudElement)new K30());
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"music"), (HudElement)new K25());
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"stream"), (HudElement)new K58());
        HudElementRegistry.addLast((class_2960)class_2960.method_60655((String)"lethalauction", (String)"wheel"), (HudElement)new K60());
        HudElementRegistry.replaceElement((class_2960)VanillaHudElements.CROSSHAIR, K16::new);
        WorldRenderEvents.AFTER_ENTITIES.register(K52::a);
        WorldRenderEvents.AFTER_ENTITIES.register(K12::a);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
            if (renderer instanceof class_1007) {
                class_1007 avatar = (class_1007)renderer;
                helper.register((class_3887)new K7((class_3883<class_10055, class_591>)((class_3883)avatar)));
            }
        });
        K5.a();
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof class_442) {
                if (K33.a()) {
                    Screens.getButtons((class_437)screen).removeIf(b -> b.method_25369() != null && b.method_25369().getString().toLowerCase().contains("copyright"));
                }
                ScreenMouseEvents.allowMouseClick((class_437)screen).register((scr, event) -> event.method_74245() != 0 || !K33.a(event.comp_4798(), event.comp_4799(), scr.field_22789, scr.field_22790));
            }
            if (!(screen instanceof K20 || screen instanceof K22 || screen instanceof K23 || screen instanceof K34 || screen instanceof K19)) {
                ScreenEvents.afterRender((class_437)screen).register((scr, graphics, mx, my, delta) -> {
                    K25.a(graphics);
                    K58.a(graphics);
                    K60.a(graphics);
                });
            }
        });
        openMenu = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.lethalauction.open", class_3675.class_307.field_1668, 344, CATEGORY));
        K40.a = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.lethalauction.freelook", class_3675.class_307.field_1668, 342, CATEGORY));
        K40.b = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.lethalauction.zoom", class_3675.class_307.field_1668, 67, CATEGORY));
        figureWaypoint = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.lethalauction.waypoint", class_3675.class_307.field_1668, 86, CATEGORY));
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> K35.a(command));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String plain = class_124.method_539((String)message.getString());
            if (plain != null) {
                Auctions.C66 result;
                if (K37.b()) {
                    Transactions.a(plain);
                    K54.a(plain);
                }
                if ((result = Auctions.b(plain)) != null) {
                    LethalAuctionClient.onBidResult(result, plain);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            for (Auctions.C65 done : Auctions.e()) {
                if (!done.hasBid()) continue;
                mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3417.field_15195, (float)1.0f, (float)0.8f));
            }
            this.licenseTick(mc);
            K30.a(mc);
            K5.a(mc);
            K40.a(mc);
            K46.a(mc);
            K57.k();
            K54.s();
            K38.a(mc);
            K12.d(mc);
            while (figureWaypoint.method_1436()) {
                K12.a(mc);
            }
            while (openMenu.method_1436()) {
                if (K37.g()) {
                    if (mc.field_1755 instanceof K34) continue;
                    mc.method_1507((class_437)new K34());
                    continue;
                }
                if (K37.a() && K37.k()) {
                    if (mc.field_1755 instanceof K23) continue;
                    mc.method_1507((class_437)new K23());
                    continue;
                }
                if (!(K37.d() || this.greeted && K37.a())) {
                    if (mc.field_1755 instanceof K22) continue;
                    this.greeted = true;
                    mc.method_1507((class_437)new K22(mc.field_1755, K20::new));
                    continue;
                }
                if (mc.field_1755 instanceof K20) continue;
                this.greeted = true;
                mc.method_1507((class_437)new K20());
            }
            if (DEV_CYCLE) {
                this.devTick(mc);
            }
        });
    }

    private void licenseTick(class_310 mc) {
        boolean maintenance;
        if (!this.licenseChecked && mc.method_18506() == null) {
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
            if (maintenance && mc.field_1755 instanceof K20) {
                mc.method_1507((class_437)new K23());
            }
        }
        if (K37.r() && !K37.d()) {
            if (K37.g()) {
                if (mc.field_1755 instanceof K20) {
                    mc.method_1507((class_437)new K34());
                }
            } else {
                K21.a("Lethal Client locked", K37.m());
                if (mc.field_1755 instanceof K20 || mc.field_1755 instanceof K34) {
                    mc.method_1507((class_437)new K22(null, K20::new));
                }
            }
        }
    }

    private void devTick(class_310 mc) {
        if (this.devTicks < 0) {
            if (mc.field_1687 != null && mc.field_1724 != null && mc.field_1755 == null && mc.method_18506() == null) {
                this.devTicks = 0;
            } else if (mc.field_1755 instanceof class_442 && mc.method_18506() == null && !this.devWorldRequested) {
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
                        Auctions.a(new class_1799((class_1935)class_1802.field_8477), 64, 12500L, 300L, 90);
                        Auctions.b("7SullV paid you $15.2K.");
                    } else if (parts.length > 1 && parts[1].equals("bid")) {
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 45000000L, 377000000L, 90);
                        Auctions.b("\u00bb 7SullV \u1d18\u1d00\u026a\u1d05 \u028f\u1d0f\u1d1c $51.1\u1d0d.");
                    } else if (parts.length > 1 && parts[1].equals("low")) {
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 0L, 0L, 7);
                    } else if (parts.length > 1 && parts[1].equals("seed")) {
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8477), 64, 10000L, 500L, 1);
                        Auctions.b("You received $12,500 from 7SullV");
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 45000000L, 0L, 1);
                        K9.b = 4;
                        K9.b();
                    } else if (parts.length > 1 && parts[1].equals("select")) {
                        K4.h();
                        K4.a("Lethal Test.png");
                    } else if (parts.length > 1 && parts[1].equals("crop")) {
                        K4.a(mc.field_1697.toPath().resolve("test-photo.png"));
                    } else if (parts.length > 1 && parts[1].equals("timer")) {
                        K9.k = true;
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 0L, 0L, 125);
                        Auctions.b("You received $51.1M from 7SullV");
                        mc.field_1690.method_31043(class_5498.field_26665);
                    } else if (parts.length > 1 && parts[1].equals("sb")) {
                        class_1132 server = mc.method_1576();
                        if (server != null) {
                            server.execute(() -> {
                                class_2168 src = server.method_3739();
                                for (String cmd : new String[]{"scoreboard objectives add lethal dummy {\"text\":\"LethalLukeYT\",\"color\":\"white\"}", "scoreboard objectives setdisplay sidebar lethal", "scoreboard players set A lethal 1", "scoreboard players set B lethal 2"}) {
                                    server.method_3734().method_44252(src, cmd);
                                }
                            });
                        }
                        K9.aS = 1.3f;
                        K9.aT = true;
                        K9.aX = 0;
                        K9.aU = true;
                        mc.field_1690.method_31043(class_5498.field_26664);
                    } else if (parts.length > 1 && parts[1].equals("capewin")) {
                        K9.k = true;
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 0L, 0L, 1);
                        Auctions.b("You received $51.1M from LethalLukeYT");
                        mc.field_1690.method_31043(class_5498.field_26665);
                    } else if (parts.length > 1 && parts[1].equals("head")) {
                        K9.aU = true;
                        K9.aT = true;
                        K9.aX = 1;
                    } else if (parts.length > 1 && parts[1].equals("zoom")) {
                        K4.a(mc.field_1697.toPath().resolve("test-photo.png"));
                        if (K4.b() != null) {
                            K4.b().a(2.5f);
                        }
                    } else if (parts.length > 1 && parts[1].equals("back")) {
                        mc.field_1690.method_31043(class_5498.field_26665);
                    } else if (parts.length > 1 && parts[1].equals("win")) {
                        Auctions.g();
                        Auctions.a(new class_1799((class_1935)class_1802.field_8833), 1, 0L, 0L, 1);
                        Auctions.b("You received $51.1M from 7SullV");
                    }
                    mc.method_1507(!parts[0].equals("hud") && !parts[0].equals("view") ? screen : null);
                    System.out.println("LA_PAGE " + DEV_PAGES[i].replace(':', '-'));
                } else if (i == DEV_PAGES.length) {
                    System.out.println("LA_DONE");
                }
            }
            ++this.devTicks;
        }
    }

    static {
        CATEGORY = class_304.class_11900.method_74698((class_2960)class_2960.method_60655((String)"lethalauction", (String)"main"));
        DEV_CYCLE = Boolean.getBoolean("lethalauction.devcycle");
        DEV_PAGES = System.getProperty("lethalauction.devpages", "hud:bid,hud:low,hud:win").split(",");
    }
}

