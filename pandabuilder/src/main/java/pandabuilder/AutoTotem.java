package pandabuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class AutoTotem {
    private static final int OFFHAND_SWAP_BUTTON = 40;
    private static final int SWAP_COOLDOWN_TICKS = 3;

    private static int cooldown;
    private static boolean warnedNone;

    private AutoTotem() {
    }

    public static void toggle() {
        Settings.autoTotem = !Settings.autoTotem;
        Settings.save();
        warnedNone = false;
        message(Component.literal("Auto Totem " + (Settings.autoTotem ? "ON" : "OFF"))
                .withStyle(Settings.autoTotem ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    public static void tick(Minecraft mc) {
        if (cooldown > 0) {
            cooldown--;
        }
        LocalPlayer player = mc.player;
        if (!Settings.autoTotem || player == null || mc.gameMode == null || player.getHealth() <= 0) {
            return;
        }
        if (player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) {
            warnedNone = false;
            return;
        }
        if (cooldown > 0 || player.containerMenu != player.inventoryMenu
                || !player.containerMenu.getCarried().isEmpty()) {
            return;
        }
        int slot = findTotem(player.getInventory());
        if (slot < 0) {
            if (!warnedNone) {
                warnedNone = true;
                message(Component.literal("Auto Totem: no totems left in your inventory!")
                        .withStyle(ChatFormatting.GOLD));
            }
            return;
        }
        warnedNone = false;
        // Hotbar slots 0-8 are menu slots 36-44; main inventory slots 9-35 map 1:1.
        int menuSlot = slot < 9 ? 36 + slot : slot;
        mc.gameMode.handleContainerInput(player.inventoryMenu.containerId, menuSlot, OFFHAND_SWAP_BUTTON,
                ContainerInput.SWAP, player);
        cooldown = SWAP_COOLDOWN_TICKS;
    }

    public static int countTotems(Inventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static int findTotem(Inventory inventory) {
        for (int i = 9; i < 36; i++) {
            if (inventory.getItem(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    static void message(Component text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("[Panda Builder] ").withStyle(ChatFormatting.AQUA).append(text));
        }
    }
}
