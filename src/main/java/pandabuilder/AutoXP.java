package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public final class AutoXP {
    private static boolean on;

    private AutoXP() {}

    public static void toggle() {
        on = !on;
        AutoTotem.message(Component.literal("Auto XP: " + (on ? "ON" : "OFF")));
    }

    public static boolean isOn() {
        return on;
    }

    // Called from PandaBuilderClient with the Minecraft instance; the signature stays (Object) to match it.
    public static void tick(Object client) {
        if (!(client instanceof Minecraft mc)) return;
        if (!on) return;
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null) return;

        Inventory inv = player.getInventory();
        int slot = findHotbar(inv);
        if (slot < 0) return;

        int previous = inv.getSelectedSlot();
        inv.setSelectedSlot(slot);
        mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
        inv.setSelectedSlot(previous);
    }

    private static int findHotbar(Inventory inv) {
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            if (inv.getItem(i).getItem() == Items.EXPERIENCE_BOTTLE) return i;
        }
        return -1;
    }
}
