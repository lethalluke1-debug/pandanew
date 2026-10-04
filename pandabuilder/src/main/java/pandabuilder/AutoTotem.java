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
   // Button 40 on a SWAP click swaps the clicked slot with the offhand, same as pressing F over a slot.
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
      message(
         Component.literal("Auto Totem " + (Settings.autoTotem ? "ON" : "OFF"))
            .withStyle(Settings.autoTotem ? ChatFormatting.GREEN : ChatFormatting.RED)
      );
   }

   public static void tick(Minecraft client) {
      if (cooldown > 0) {
         cooldown--;
      }

      LocalPlayer player = client.player;
      if (!Settings.autoTotem || player == null || client.gameMode == null || player.getHealth() <= 0.0F) {
         return;
      }

      if (player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) {
         warnedNone = false;
         return;
      }

      // Only click while the player's own inventory menu is active (no chest/furnace open) and nothing is on the cursor.
      if (cooldown > 0 || player.containerMenu != player.inventoryMenu || !player.containerMenu.getCarried().isEmpty()) {
         return;
      }

      int slot = findTotem(player.getInventory());
      if (slot < 0) {
         if (!warnedNone) {
            warnedNone = true;
            message(Component.literal("Auto Totem: no totems left in your inventory!").withStyle(ChatFormatting.GOLD));
         }
         return;
      }

      warnedNone = false;
      int menuSlot = slot < 9 ? 36 + slot : slot;
      client.gameMode.handleContainerInput(player.inventoryMenu.containerId, menuSlot, OFFHAND_SWAP_BUTTON, ContainerInput.SWAP, player);
      cooldown = SWAP_COOLDOWN_TICKS;
   }

   /** Totems in the main inventory, hotbar and offhand. */
   public static int countTotems(Inventory inv) {
      int count = 0;

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
            count += stack.getCount();
         }
      }

      return count;
   }

   private static int findTotem(Inventory inv) {
      // Main inventory first so hotbar totems stay where the player put them.
      for (int i = 9; i < 36; i++) {
         if (inv.getItem(i).getItem() == Items.TOTEM_OF_UNDYING) {
            return i;
         }
      }

      for (int i = 0; i < 9; i++) {
         if (inv.getItem(i).getItem() == Items.TOTEM_OF_UNDYING) {
            return i;
         }
      }

      return -1;
   }

   private static void message(Component text) {
      Minecraft client = Minecraft.getInstance();
      if (client.player != null) {
         client.player.sendSystemMessage(Component.literal("[Panda Builder] ").withStyle(ChatFormatting.AQUA).append(text));
      }
   }
}
