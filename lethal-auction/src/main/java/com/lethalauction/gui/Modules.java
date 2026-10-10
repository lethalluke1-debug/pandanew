package com.lethalauction.gui;

import java.util.ArrayList;
import java.util.List;

/** Display-only module data for the module pages. Nothing here does anything in game. */
public final class Modules {
    private Modules() {
    }

    public enum Kind { TOGGLE, MODE, SLIDER, SUBHEADER, RESET }

    public record Setting(Kind kind, String label, boolean on, String value, float frac) {
        static Setting toggle(String label, boolean on) {
            return new Setting(Kind.TOGGLE, label, on, null, 0);
        }

        static Setting mode(String label, String value) {
            return new Setting(Kind.MODE, label, false, value, 0);
        }

        static Setting slider(String label, float frac) {
            return new Setting(Kind.SLIDER, label, false, null, frac);
        }

        static Setting sub(String label) {
            return new Setting(Kind.SUBHEADER, label, false, null, 0);
        }

        static Setting reset() {
            return new Setting(Kind.RESET, "Reset", false, null, 0);
        }
    }

    public static final class Module {
        final String name;
        boolean diamond;
        String key;
        boolean on;
        final List<Setting> settings = new ArrayList<>();

        Module(String name) {
            this.name = name;
        }

        Module diamond() {
            diamond = true;
            return this;
        }

        Module key(String key) {
            this.key = key;
            return this;
        }

        Module on() {
            on = true;
            return this;
        }

        Module with(Setting... s) {
            settings.addAll(List.of(s));
            return this;
        }

        boolean expanded() {
            return !settings.isEmpty();
        }
    }

    public record Columns(List<Module> left, List<Module> right) {
    }

    private static Module m(String name) {
        return new Module(name);
    }

    public static Columns combat() {
        return new Columns(
                List.of(m("Auto Inventory Totem"), m("Auto Crystal"), m("Crystal Optimizer").diamond(),
                        m("Silent Aim"), m("AutoTrap").diamond(), m("AutoClicker"), m("Safe Anchor")),
                List.of(m("Anchor Macro").key("E"), m("Auto Totem").diamond(), m("Aim Assist"),
                        m("Reach").diamond(), m("AutoCart").diamond(), m("Trigger Bot"), m("Auto XP")));
    }

    public static Columns movement() {
        return new Columns(
                List.of(m("Freecam").key("Page Up"), m("KeepSprint"), m("Flight").diamond(),
                        m("Bridge Build").diamond(), m("Sprint")),
                List.of(m("Auto Walk"),
                        m("SpeedHack").diamond().with(
                                Setting.mode("Mode", "Strafe"),
                                Setting.slider("Speed: 1.40", 0.10f),
                                Setting.toggle("Sprint Only", false),
                                Setting.toggle("Auto Jump", true),
                                Setting.reset()),
                        m("Auto Spear"), m("Pearl Catch Assist")));
    }

    public static Columns donut() {
        return new Columns(
                List.of(m("Auction Overlay"), m("Price Per Item"), m("Deal Alerts")),
                List.of(m("Price History"), m("Quick Sell"), m("Search Shortcut")));
    }

    public static Columns visuals() {
        return new Columns(
                List.of(m("Show HUD").on().with(
                        Setting.toggle("Brand", true),
                        Setting.toggle("Performance", true),
                        Setting.toggle("Clock", true),
                        Setting.toggle("Build Progress", true),
                        Setting.toggle("Coordinates", true),
                        Setting.toggle("Potions", true),
                        Setting.mode("Effect Style", "Compact"),
                        Setting.toggle("Effect Icons", true),
                        Setting.toggle("Module List", true),
                        Setting.toggle("Radar", true),
                        Setting.sub("+ Radar"),
                        Setting.toggle("Target HUD", true),
                        Setting.toggle("Target Players Only", true),
                        Setting.toggle("Keystrokes", true))),
                List.of(m("Spotify HUD"), m("StorageESP"), m("BlockESP"), m("Block Overlay"),
                        m("Spawner Finder"), m("No Flame"), m("Pearl Prediction")));
    }

    public static Columns misc() {
        return new Columns(
                List.of(m("Fast Place").on().with(
                                Setting.toggle("Only XP", false),
                                Setting.toggle("Blocks", true),
                                Setting.toggle("Items", true),
                                Setting.slider("Delay: 0", 0f),
                                Setting.toggle("Anticheat Safe", false),
                                Setting.reset()),
                        m("Fastbreak").diamond(), m("Auto Promo").diamond(), m("Custom Crosshair")),
                List.of(m("Fake Player"), m("AutoTool"), m("Time Changer"), m("Name Protect").on(),
                        m("SkinProtect"), m("Potion Refill"), m("Zoom")));
    }
}
