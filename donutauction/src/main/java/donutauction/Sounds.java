package donutauction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/** Interface and auction sounds; all off with the Sounds setting. */
public final class Sounds {
    private Sounds() {}

    private static void play(SoundEvent sound, float pitch, float volume) {
        if (!Config.get().sounds) return;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    public static void click() { play(SoundEvents.UI_BUTTON_CLICK.value(), 1.25f, 0.35f); }
    public static void toggle(boolean on) { play(SoundEvents.NOTE_BLOCK_HAT.value(), on ? 1.7f : 1.2f, 0.45f); }
    public static void select() { play(SoundEvents.NOTE_BLOCK_PLING.value(), 1.9f, 0.25f); }
    public static void error() { play(SoundEvents.NOTE_BLOCK_BIT.value(), 0.6f, 0.5f); }

    public static void start() {
        play(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 0.6f);
        play(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 0.8f);
    }

    public static void bid() {
        play(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.3f, 0.5f);
        play(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.5f, 0.4f);
    }

    /** Countdown tick for the last seconds; higher pitch as it gets closer. */
    public static void tick(int secondsLeft) {
        play(SoundEvents.NOTE_BLOCK_HAT.value(), 1.0f + (5 - secondsLeft) * 0.15f, 0.55f);
    }

    public static void sold() { play(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 0.6f); }
    public static void noSale() { play(SoundEvents.NOTE_BLOCK_BIT.value(), 0.7f, 0.5f); }
}
