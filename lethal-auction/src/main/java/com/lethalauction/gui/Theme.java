package com.lethalauction.gui;

import com.lethalauction.LethalConfig;

/** Theme colours derived from the selected preset. */
public final class Theme {
    static final int DEFAULT_ACCENT = 0xFFE000FD;
    static final int DEFAULT_TINT = 0xFFA611FB;

    private Theme() {
    }

    public static int accent() {
        int p = LethalConfig.preset;
        if (p >= 0 && p < LethalScreen.PRESET_COLORS.length) {
            return 0xFF000000 | LethalScreen.PRESET_COLORS[p][0];
        }
        return DEFAULT_ACCENT;
    }

    /** Colour the greyscale background textures are multiplied by. */
    public static int tint() {
        return LethalConfig.preset >= 0 ? accent() : DEFAULT_TINT;
    }

    public static int accentDark() {
        return Draw.lerpColor(accent(), 0xFF000000, 0.55f);
    }

    public static int accentLight() {
        return Draw.lerpColor(accent(), 0xFFFFFFFF, 0.65f);
    }
}
