package pandabuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.world.phys.Vec3;

/**
 * Digs a tunnel in the direction the player faced when it was turned on. Lava, missing floor and unbreakable
 * blocks ahead are avoided by stepping into the lane to the left or right, tunnelling past, and stepping back
 * onto the original line once it's safe again.
 */
public final class AutoMine {
    /** How many lanes it may wander from the original line while going around something. */
    private static final int MAX_LANE = 3;

    private static boolean on;
    private static Direction direction = Direction.NORTH;
    /** Where it was turned on; lane 0 runs through here. Lanes count to the right of {@link #direction}. */
    private static BlockPos origin = BlockPos.ZERO;
    private static int targetLane;
    /** For pickaxes that break 3x3: only the head-height block ahead is mined, the pickaxe clears the rest. */
    private static boolean pickaxe3x3;

    // Movement for KeyboardInputMixin, worked out every tick. Not tied to the WASD keys, so Freecam can run too.
    private static boolean forward;
    private static boolean jump;
    private static int strafe; // +1 right, -1 left

    private AutoMine() {}

    public static boolean isOn() {
        return on;
    }

    public static boolean pickaxe3x3() {
        return pickaxe3x3;
    }

    public static void togglePickaxe3x3() {
        pickaxe3x3 = !pickaxe3x3;
        save();
    }

    public static boolean isMoving() {
        return on && (forward || strafe != 0);
    }

    public static boolean forward() { return on && forward; }
    public static boolean jump() { return on && jump; }
    public static boolean left() { return on && strafe < 0; }
    public static boolean right() { return on && strafe > 0; }

    public static void toggle() {
        Minecraft mc = Minecraft.getInstance();
        if (on) {
            stop(mc, null);
            return;
        }
        if (mc.player == null) return;
        on = true;
        direction = mc.player.getDirection();
        origin = mc.player.blockPosition();
        targetLane = 0;
        mc.player.setYRot(direction.toYRot());
        mc.player.setXRot(0);
        AutoTotem.message(Component.literal("Auto Mine: ON (heading " + direction.getName() + ")"));
    }

    public static void tick(Minecraft mc) {
        forward = false;
        jump = false;
        strafe = 0;
        if (!on) return;
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null || mc.gameMode == null) {
            stop(mc, null);
            return;
        }
        if (mc.gui.screen() != null) return;

        player.setYRot(direction.toYRot());
        // In 3x3 mode the view stays level: that already points at the head-height block it mines.
        if (pickaxe3x3) player.setXRot(0);

        Direction right = direction.getClockWise();
        BlockPos pos = player.blockPosition();
        int lane = laneOf(pos, right);

        if (lane != targetLane) {
            sidestep(mc, player, level, pos, right, lane);
            return;
        }

        // Stay centred in the lane so the player doesn't catch on the tunnel walls.
        double off = (player.getX() - (pos.getX() + 0.5)) * right.getStepX()
                + (player.getZ() - (pos.getZ() + 0.5)) * right.getStepZ();
        if (Math.abs(off) > 0.25) strafe = off > 0 ? -1 : 1;

        // Off the original line: step back towards it once both the lane beside and the block past it are safe.
        if (lane != 0) {
            Direction back = lane > 0 ? right.getOpposite() : right;
            BlockPos side = pos.relative(back);
            if (safe(level, side) && safe(level, side.relative(direction))) {
                targetLane = lane + (lane > 0 ? -1 : 1);
                return;
            }
        }

        BlockPos feet = pos.relative(direction);
        if (!safe(level, feet)) {
            goAround(mc, level, pos, right, lane, hazard(level, feet));
            return;
        }
        digForward(mc, player, level, feet);
    }

    /** Picks a free lane to the side; the side nearer the original line first. */
    private static void goAround(Minecraft mc, ClientLevel level, BlockPos pos, Direction right, int lane, String reason) {
        int[] order = lane > 0 ? new int[] {-1, 1} : new int[] {1, -1};
        for (int d : order) {
            int next = lane + d;
            if (Math.abs(next) > MAX_LANE) continue;
            if (safe(level, pos.relative(d > 0 ? right : right.getOpposite()))) {
                targetLane = next;
                AutoTotem.message(Component.literal("Auto Mine: " + reason + " ahead, going around"));
                return;
            }
        }
        stop(mc, reason + " ahead and no safe way around");
    }

    /** Clears the column beside the player and strafes into it. */
    private static void sidestep(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos pos, Direction right, int lane) {
        Direction side = targetLane > lane ? right : right.getOpposite();
        BlockPos feet = pos.relative(side);
        if (!safe(level, feet)) {
            targetLane = lane; // that side became unsafe; plan again next tick
            return;
        }
        BlockPos head = feet.above();
        if (!level.getBlockState(head).isAir()) {
            mine(mc, player, level, head, side);
        } else if (!level.getBlockState(feet).isAir()) {
            mine(mc, player, level, feet, side);
        } else {
            strafe = side == right ? 1 : -1;
        }
    }

    private static void digForward(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos feet) {
        BlockPos head = feet.above();
        // Head height first. A 3x3 pickaxe centred there also clears the feet block and the row above.
        boolean headSolid = !level.getBlockState(head).isAir();
        boolean feetSolid = !level.getBlockState(feet).isAir();

        // In 3x3 mode a lone block at feet height is stepped onto instead: mining it with a 3x3 pickaxe would dig
        // out the floor ahead. Only when there's no headroom to step up is it mined.
        if (pickaxe3x3 && !headSolid && feetSolid
                && level.getBlockState(head.above()).isAir() && level.getBlockState(player.blockPosition().above(2)).isAir()) {
            forward = true;
            jump = true;
            return;
        }

        BlockPos target = headSolid ? head : feetSolid ? feet : null;
        if (target == null) {
            if (!pickaxe3x3) player.setXRot(0);
            forward = true;
            return;
        }
        if (!pickaxe3x3) lookAt(player, target);
        mine(mc, player, level, target, direction);
    }

    private static void mine(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos target, Direction towards) {
        selectBestTool(player.getInventory(), level.getBlockState(target));
        mc.gameMode.continueDestroyBlock(target, towards.getOpposite());
        player.swing(InteractionHand.MAIN_HAND);
    }

    /** Lane number of a block: how many blocks right of the original line it is (negative = left). */
    private static int laneOf(BlockPos pos, Direction right) {
        return (pos.getX() - origin.getX()) * right.getStepX() + (pos.getZ() - origin.getZ()) * right.getStepZ();
    }

    /** Whether the player can stand at {@code feet}: no lava touching it, a floor, nothing unbreakable. */
    private static boolean safe(ClientLevel level, BlockPos feet) {
        return hazard(level, feet) == null;
    }

    private static String hazard(ClientLevel level, BlockPos feet) {
        BlockPos head = feet.above();
        if (nearLava(level, feet) || nearLava(level, head)) return "lava";
        BlockPos floor = feet.below();
        if (level.getBlockState(floor).getCollisionShape(level, floor).isEmpty()) return "hole";
        if (unbreakable(level, feet) || unbreakable(level, head)) return "unbreakable block";
        return null;
    }

    private static boolean unbreakable(ClientLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(level, pos) < 0;
    }

    /** Points the crosshair at the centre of the target's near face, so servers see the player looking at it. */
    private static void lookAt(LocalPlayer player, BlockPos target) {
        Vec3 eye = player.getEyePosition();
        double dx = target.getX() + 0.5 - direction.getStepX() * 0.5 - eye.x;
        double dy = target.getY() + 0.5 - eye.y;
        double dz = target.getZ() + 0.5 - direction.getStepZ() * 0.5 - eye.z;
        player.setXRot((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
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

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pandabuilder-automine.properties");
    }

    public static void load() {
        Path file = file();
        if (!Files.exists(file)) return;
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(file)) {
            p.load(r);
            pickaxe3x3 = Boolean.parseBoolean(p.getProperty("pickaxe3x3", "false"));
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read Auto Mine settings", e);
        }
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("pickaxe3x3", Boolean.toString(pickaxe3x3));
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, Brand.NAME + " Auto Mine settings");
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not save Auto Mine settings", e);
        }
    }

    private static void stop(Minecraft mc, String reason) {
        on = false;
        forward = false;
        jump = false;
        strafe = 0;
        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
        AutoTotem.message(Component.literal(reason == null ? "Auto Mine: OFF" : "Auto Mine stopped: " + reason));
    }
}
