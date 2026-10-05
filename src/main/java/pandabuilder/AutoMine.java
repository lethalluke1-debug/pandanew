package pandabuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/** Digs a 1 wide, 2 tall tunnel in the direction the player faced when it was turned on. */
public final class AutoMine {
    private static boolean on;
    private static Direction direction = Direction.NORTH;

    private AutoMine() {}

    public static boolean isOn() {
        return on;
    }

    public static void toggle() {
        Minecraft mc = Minecraft.getInstance();
        if (on) {
            stop(mc, null);
            return;
        }
        if (mc.player == null) return;
        on = true;
        direction = mc.player.getDirection();
        mc.player.setYRot(direction.toYRot());
        mc.player.setXRot(0);
        AutoTotem.message(Component.literal("Auto Mine: ON (heading " + direction.getName() + ")"));
    }

    public static void tick(Minecraft mc) {
        if (!on) return;
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null || mc.gameMode == null) {
            stop(mc, null);
            return;
        }
        if (mc.gui.screen() != null) {
            mc.options.keyUp.setDown(false);
            return;
        }

        player.setYRot(direction.toYRot());

        BlockPos feet = player.blockPosition().relative(direction);
        BlockPos head = feet.above();

        if (nearLava(level, feet) || nearLava(level, head)) {
            stop(mc, "lava ahead");
            return;
        }
        BlockPos floor = feet.below();
        if (level.getBlockState(floor).getCollisionShape(level, floor).isEmpty()) {
            stop(mc, "no floor ahead");
            return;
        }

        BlockPos target = !level.getBlockState(head).isAir() ? head
                : !level.getBlockState(feet).isAir() ? feet : null;

        if (target == null) {
            mc.options.keyUp.setDown(true);
            return;
        }

        BlockState state = level.getBlockState(target);
        if (state.getDestroySpeed(level, target) < 0) {
            stop(mc, "unbreakable block");
            return;
        }
        mc.options.keyUp.setDown(false);
        selectBestTool(player.getInventory(), state);
        mc.gameMode.continueDestroyBlock(target, direction.getOpposite());
        player.swing(InteractionHand.MAIN_HAND);
    }

    private static boolean nearLava(ClientLevel level, BlockPos pos) {
        if (isLava(level, pos)) return true;
        for (Direction d : Direction.values()) {
            if (isLava(level, pos.relative(d))) return true;
        }
        return false;
    }

    private static boolean isLava(ClientLevel level, BlockPos pos) {
        Fluid fluid = level.getFluidState(pos).getType();
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }

    private static void selectBestTool(Inventory inv, BlockState state) {
        int best = inv.getSelectedSlot();
        float bestSpeed = inv.getItem(best).getDestroySpeed(state);
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            float speed = inv.getItem(i).getDestroySpeed(state);
            if (speed > bestSpeed) {
                best = i;
                bestSpeed = speed;
            }
        }
        inv.setSelectedSlot(best);
    }

    private static void stop(Minecraft mc, String reason) {
        on = false;
        mc.options.keyUp.setDown(false);
        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
        AutoTotem.message(Component.literal(reason == null ? "Auto Mine: OFF" : "Auto Mine stopped: " + reason));
    }
}
