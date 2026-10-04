package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class AutoCrystal {
    private static boolean on;
    private static int cooldown;

    private AutoCrystal() {}

    public static void toggle() {
        on = !on;
        AutoTotem.message(Component.literal("Auto Crystal: " + (on ? "ON" : "OFF")));
    }

    public static boolean isOn() {
        return on;
    }

    // Called from PandaBuilderClient with the Minecraft instance; the signature stays (Object) to match it.
    public static void tick(Object client) {
        if (!(client instanceof Minecraft mc)) return;
        if (!on || cooldown > 0) {
            if (cooldown > 0) cooldown--;
            return;
        }
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null) return;

        place(mc, player);
        breakNearby(mc, player);
    }

    private static void place(Minecraft mc, LocalPlayer player) {
        HitResult hit = player.pick(5.0, 1.0f, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = blockHit.getBlockPos();
        if (mc.level.getBlockState(pos).getBlock() != Blocks.OBSIDIAN) return;

        Inventory inv = player.getInventory();
        int slot = findHotbar(inv);
        if (slot < 0) return;

        Vec3 loc = hit.getLocation();
        BlockHitResult target = new BlockHitResult(new Vec3(loc.x, loc.y + 0.1, loc.z), Direction.UP, pos, false);

        int previous = inv.getSelectedSlot();
        inv.setSelectedSlot(slot);
        mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, target);
        inv.setSelectedSlot(previous);
        cooldown = 1;
    }

    private static void breakNearby(Minecraft mc, LocalPlayer player) {
        double x = player.getX(), y = player.getY(), z = player.getZ();
        AABB box = new AABB(x - 6.0, y - 4.0, z - 6.0, x + 7.0, y + 8.0, z + 7.0);
        for (EndCrystal crystal : mc.level.getEntitiesOfClass(EndCrystal.class, box)) {
            mc.gameMode.attack(player, crystal);
        }
    }

    private static int findHotbar(Inventory inv) {
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            if (inv.getItem(i).getItem() == Items.END_CRYSTAL) return i;
        }
        return -1;
    }
}
