package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MenuScreen extends Screen {
   public MenuScreen() {
      super(Component.literal("Panda Builder"));
   }

   protected void init() {
      int w = 160;
      int x = (this.width - w) / 2;
      int y = this.height / 2 - 20;
      this.addRenderableWidget(Button.builder(toggleLabel(), b -> {
         AutoTotem.toggle();
         b.setMessage(toggleLabel());
      }).bounds(x, y, w, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose()).bounds(x, y + 26, w, 20).build());
   }

   private static Component toggleLabel() {
      return Component.literal("Auto Totem: " + (Settings.autoTotem ? "§aON" : "§cOFF"));
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(graphics, mouseX, mouseY, delta);
      String title = "Panda Builder";
      graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, this.height / 2 - 50, -1, true);
      Minecraft client = Minecraft.getInstance();
      if (client.player != null) {
         String count = "Totems in inventory: " + AutoTotem.countTotems(client.player.getInventory());
         graphics.item(new ItemStack(Items.TOTEM_OF_UNDYING), (this.width - this.font.width(count)) / 2 - 20, this.height / 2 - 38);
         graphics.text(this.font, count, (this.width - this.font.width(count)) / 2, this.height / 2 - 34, -171, true);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }
}
