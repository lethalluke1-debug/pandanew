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

    private static Module m(String name) {
        return new Module(name);
    }

    public static List<Module> auctionLeft() {
        return List.of(m("Auto Inventory Totem"), m("Auto Crystal"), m("Crystal Optimizer").diamond(),
                m("Silent Aim"), m("AutoTrap").diamond(), m("AutoClicker"), m("Safe Anchor"));
    }

    public static List<Module> auctionRight() {
        return List.of(m("Anchor Macro").key("E"), m("Auto Totem").diamond(), m("Aim Assist"),
                m("Reach").diamond(), m("AutoCart").diamond(), m("Trigger Bot"), m("Auto XP"));
    }
}
