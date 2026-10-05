package pandabuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Digs a tunnel in the direction the player faced when it was turned on. Lava, missing floor and unbreakable
 * blocks ahead are avoided by stepping into the lane to the left or right, tunnelling past, and stepping back
 * onto the original line once it's safe again.
 */
public final class AutoMine {
    /** How many lanes it may wander from the original line while going around something. */
    private static final int MAX_LANE = 3;
    /** Hotbar plus main inventory. */
    private static final int MAIN_INVENTORY_SIZE = 36;

    private static boolean on;
    private static Direction direction = Direction.NORTH;
    /** Where it was turned on; lane 0 runs through here. Lanes count to the right of {@link #direction}. */
    private static BlockPos origin = BlockPos.ZERO;
    private static int targetLane;
    /** For pickaxes that break 3x3: only the head-height block ahead is mined, the pickaxe clears the rest. */
    private static boolean pickaxe3x3;
    /** While Freecam is on: no arm swing, and mouse clicks can't break or place blocks. */
    private static boolean freezeInFreecam;
    /** Fill holes in the floor ahead with stone/dirt from the inventory, so caves are crossed in a straight line. */
    private static boolean fillHoles = true;
    private static int swapCooldown;
    private static int placeCooldown;
    /** Pitch the head turns towards this tick, at most 15 degrees per tick. */
    private static float wantPitch;

    /** Throwaway blocks used to fill holes. */
    private static final Set<Block> FILLERS = Set.of(Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.STONE,
            Blocks.DEEPSLATE, Blocks.DIRT, Blocks.NETHERRACK, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE,
            Blocks.TUFF, Blocks.BLACKSTONE, Blocks.END_STONE);

    // Movement for KeyboardInputMixin, worked out every tick. Not tied to the WASD keys, so Freecam can run too.
    private static float forward; // > 0 forward, < 0 back; size is how hard
    private static boolean jump;
    private static float strafe; // > 0 right, < 0 left; size is how hard
    private static boolean centering;
    private static double lateralOff;

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

    public static boolean fillHoles() {
        return fillHoles;
    }

    public static void toggleFillHoles() {
        fillHoles = !fillHoles;
        save();
    }

    public static boolean freezeInFreecam() {
        return freezeInFreecam;
    }

    public static void toggleFreezeInFreecam() {
        freezeInFreecam = !freezeInFreecam;
        save();
    }

    /** Read by MinecraftMixin: blocks the player's own attack/use/pick clicks. */
    public static boolean blockClicks() {
        return freezeInFreecam && Freecam.isOn();
    }

    public static boolean isMoving() {
        return on && (forward != 0 || strafe != 0);
    }

    public static boolean forward() { return on && forward > 0; }
    public static boolean back() { return on && forward < 0; }
    public static float forwardAmount() { return on ? forward : 0.0f; }
    public static boolean jump() { return on && jump; }
    public static boolean left() { return on && strafe < 0; }
    public static boolean right() { return on && strafe > 0; }
    public static float strafeAmount() { return on ? Math.abs(strafe) : 0.0f; }

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
        forward = 0;
        jump = false;
        strafe = 0;
        if (swapCooldown > 0) swapCooldown--;
        if (placeCooldown > 0) placeCooldown--;
        if (!on) return;
        wantPitch = 0.0f;
        step(mc);
        LocalPlayer player = mc.player;
        if (on && player != null) {
            player.setXRot(player.getXRot() + Math.clamp(wantPitch - player.getXRot(), -15.0f, 15.0f));
        }
    }

    private static void step(Minecraft mc) {
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null || mc.gameMode == null) {
            stop(mc, null);
            return;
        }
        // Keeps going with chat, inventory or the pause menu open (tabbing out opens the pause menu).

        player.setYRot(direction.toYRot());

        Direction right = direction.getClockWise();
        BlockPos pos = player.blockPosition();
        int lane = laneOf(pos, right);

        if (lane != targetLane) {
            sidestep(mc, player, level, pos, right, lane);
            return;
        }

        // Stay centred in the lane so the player doesn't catch on the tunnel walls. Starts past 0.3 blocks off,
        // stops within 0.08, and eases off near the centre so it doesn't overshoot and wobble side to side.
        double off = (player.getX() - (pos.getX() + 0.5)) * right.getStepX()
                + (player.getZ() - (pos.getZ() + 0.5)) * right.getStepZ();
        if (Math.abs(off) > 0.3) centering = true;
        if (Math.abs(off) < 0.08) centering = false;
        if (centering) strafe = (float) -Math.copySign(Math.clamp(Math.abs(off) * 2.5, 0.2, 1.0), off);
        lateralOff = off;

        // Off the original line: step back towards it once both the lane beside and the block past it are safe.
        if (lane != 0) {
            Direction back = lane > 0 ? right.getOpposite() : right;
            BlockPos side = pos.relative(back);
            if (safe(level, player, side) && safe(level, player, side.relative(direction))) {
                targetLane = lane + (lane > 0 ? -1 : 1);
                return;
            }
        }

        BlockPos feet = pos.relative(direction);
        if (!safe(level, player, feet)) {
            goAround(mc, level, player, pos, right, lane, hazard(level, player, feet));
            return;
        }
        digForward(mc, player, level, feet);
    }

    /** Picks a free lane to the side; the side nearer the original line first. */
    private static void goAround(Minecraft mc, ClientLevel level, LocalPlayer player, BlockPos pos, Direction right, int lane,
                                 String reason) {
        int[] order = lane > 0 ? new int[] {-1, 1} : new int[] {1, -1};
        for (int d : order) {
            int next = lane + d;
            if (Math.abs(next) > MAX_LANE) continue;
            if (safe(level, player, pos.relative(d > 0 ? right : right.getOpposite()))) {
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
        if (!safe(level, player, feet)) {
            targetLane = lane; // that side became unsafe; plan again next tick
            return;
        }
        BlockPos head = feet.above();
        // Line up with the hole first: the player is 0.6 wide and the hole 1 wide, so if momentum carried them
        // towards the next block, strafing just pushes against the corner of the wall.
        if (!alignAlong(player, pos, direction, 0.1)) return;
        if (blocks(level, head)) {
            mine(mc, player, level, head, side);
        } else if (blocks(level, feet)) {
            mine(mc, player, level, feet, side);
        } else if (noFloor(level, feet.below())) {
            fill(mc, player, level, feet.below(), pos.below(), side);
        } else {
            strafe = side == right ? 1.0f : -1.0f;
        }
    }

    private static void digForward(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos feet) {
        BlockPos head = feet.above();
        // Head height first. A 3x3 pickaxe centred there also clears the feet block and the row above.
        // Water, plants, vines and the like don't count: they're walked through, not mined.
        boolean headSolid = blocks(level, head);
        boolean feetSolid = blocks(level, feet);

        // In 3x3 mode without Fill Holes, a lone block at feet height is stepped onto instead: mining it with a 3x3
        // pickaxe would dig out the floor ahead. With Fill Holes it's mined and any floor it takes is filled back in,
        // so the tunnel keeps its height instead of climbing over every bump in a cave.
        if (pickaxe3x3 && !fillHoles && !headSolid && feetSolid
                && !blocks(level, head.above()) && !blocks(level, player.blockPosition().above(2))) {
            forward = 1.0f;
            jump = true;
            return;
        }

        BlockPos target = headSolid ? head : feetSolid ? feet : null;
        if (target == null) {
            // Tunnel ahead is clear. Cave floor missing? Fill it first so the line stays at the same height.
            if (noFloor(level, feet.below())) {
                fill(mc, player, level, feet.below(), player.blockPosition().below(), direction);
                return;
            }
            // Just stepped into a new lane: centre in it before walking, or the hole edges catch the player.
            forward = Math.abs(lateralOff) > 0.2 ? 0.0f : 1.0f;
            return;
        }
        // In 3x3 mode the view stays level: that already points at the head-height block it mines.
        if (!pickaxe3x3) wantPitch = pitchTo(player, faceCenter(target, direction.getOpposite()));
        mine(mc, player, level, target, direction);
    }

    private static void mine(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos target, Direction towards) {
        selectBestTool(mc, player, level.getBlockState(target));
        Direction face = towards.getOpposite();
        // Like vanilla: only swing and show crack particles on ticks where breaking actually progressed (not during
        // the short delay after a block breaks), so mining looks smooth instead of restarting the swing every tick.
        if (!mc.gameMode.continueDestroyBlock(target, face)) return;
        if (!blockClicks()) level.addBreakingBlockEffect(target, face);
        swing(mc, player);
    }

    private static void swing(Minecraft mc, LocalPlayer player) {
        if (blockClicks()) {
            // Frozen in Freecam: no swing animation, but the server still gets the swing. Anti-cheat plugins reject
            // block breaks without one, which made the player rubber-band.
            mc.getConnection().send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        } else {
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    /**
     * Moves forward/back until the player's centre is within {@code tolerance} of the block centre along
     * {@code axis}. Returns true once aligned.
     */
    private static boolean alignAlong(LocalPlayer player, BlockPos pos, Direction axis, double tolerance) {
        double off = (player.getX() - (pos.getX() + 0.5)) * axis.getStepX()
                + (player.getZ() - (pos.getZ() + 0.5)) * axis.getStepZ();
        if (Math.abs(off) <= tolerance) return true;
        forward = (float) -Math.copySign(Math.clamp(Math.abs(off) * 2.5, 0.2, 1.0), off);
        return false;
    }

    /** Lane number of a block: how many blocks right of the original line it is (negative = left). */
    private static int laneOf(BlockPos pos, Direction right) {
        return (pos.getX() - origin.getX()) * right.getStepX() + (pos.getZ() - origin.getZ()) * right.getStepZ();
    }

    /**
     * Whether the player can stand at {@code feet}: no lava touching it, nothing unbreakable, and a floor, or a
     * hole it can fill.
     */
    private static boolean safe(ClientLevel level, LocalPlayer player, BlockPos feet) {
        return hazard(level, player, feet) == null;
    }

    private static String hazard(ClientLevel level, LocalPlayer player, BlockPos feet) {
        BlockPos head = feet.above();
        if (nearLava(level, feet) || nearLava(level, head)) return "lava";
        if (unbreakable(level, feet) || unbreakable(level, head)) return "unbreakable block";
        BlockPos floor = feet.below();
        if (noFloor(level, floor)) {
            if (!fillHoles) return "hole";
            if (!level.getBlockState(floor).canBeReplaced()) return "hole";
            if (findFiller(player.getInventory()) < 0) return "hole (no stone or dirt to fill it)";
        }
        return null;
    }

    /** A block in the way that has to be mined. Air, water, grass, vines, lichen and so on don't. */
    private static boolean blocks(ClientLevel level, BlockPos pos) {
        return !level.getBlockState(pos).canBeReplaced();
    }

    private static boolean noFloor(ClientLevel level, BlockPos floor) {
        return level.getBlockState(floor).getCollisionShape(level, floor).isEmpty();
    }

    /** Places a filler block at {@code floor} by clicking the {@code face} side of {@code support}. */
    private static void fill(Minecraft mc, LocalPlayer player, ClientLevel level, BlockPos floor, BlockPos support,
                             Direction face) {
        Vec3 hit = faceCenter(support, face);
        wantPitch = pitchTo(player, hit);
        if (placeCooldown > 0 || noFloor(level, support)) return;
        Inventory inv = player.getInventory();
        int slot = findFiller(inv);
        if (slot < 0) return;
        if (slot >= Inventory.getSelectionSize()) {
            // Filler only in the main inventory: swap it into the hand first.
            if (player.containerMenu != player.inventoryMenu || swapCooldown > 0) return;
            mc.gameMode.handleContainerInput(player.inventoryMenu.containerId, slot, inv.getSelectedSlot(),
                    ContainerInput.SWAP, player);
            swapCooldown = 4;
            return;
        }
        inv.setSelectedSlot(slot);
        // Wait until the head is roughly aimed at the face, so the click looks like a normal placement.
        if (Math.abs(wantPitch - player.getXRot()) > 10.0f) return;
        InteractionResult result = mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(hit, face, support, false));
        if (result.consumesAction()) swing(mc, player);
        placeCooldown = 3;
    }

    private static int findFiller(Inventory inv) {
        int selected = inv.getSelectedSlot();
        if (isFiller(inv.getItem(selected))) return selected;
        for (int i = 0; i < MAIN_INVENTORY_SIZE; i++) {
            if (isFiller(inv.getItem(i))) return i;
        }
        return -1;
    }

    private static boolean isFiller(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && FILLERS.contains(item.getBlock());
    }

    private static Vec3 faceCenter(BlockPos pos, Direction face) {
        return Vec3.atCenterOf(pos).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
    }

    private static boolean unbreakable(ClientLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(level, pos) < 0;
    }

    /** Pitch that points the crosshair at {@code point}, so servers see the player looking at what it clicks. */
    private static float pitchTo(LocalPlayer player, Vec3 point) {
        Vec3 eye = player.getEyePosition();
        double dx = point.x - eye.x, dy = point.y - eye.y, dz = point.z - eye.z;
        return (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
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

    /**
     * Holds the fastest tool for the block (a shovel for gravel, a pickaxe for stone). Hotbar tools are selected;
     * a better one in the main inventory is swapped into the selected hotbar slot.
     */
    private static void selectBestTool(Minecraft mc, LocalPlayer player, BlockState state) {
        Inventory inv = player.getInventory();
        int selected = inv.getSelectedSlot();
        int best = selected;
        float bestSpeed = inv.getItem(selected).getDestroySpeed(state);
        for (int i = 0; i < MAIN_INVENTORY_SIZE; i++) {
            float speed = inv.getItem(i).getDestroySpeed(state);
            if (speed > bestSpeed) {
                best = i;
                bestSpeed = speed;
            }
        }
        if (best < Inventory.getSelectionSize()) {
            inv.setSelectedSlot(best);
            return;
        }
        // Main inventory slots 9-35 have the same index in the inventory menu. Skipped while another container
        // (chest, furnace, ...) is open, and briefly after a swap so the server can catch up.
        if (player.containerMenu != player.inventoryMenu || swapCooldown > 0) return;
        mc.gameMode.handleContainerInput(player.inventoryMenu.containerId, best, selected, ContainerInput.SWAP, player);
        swapCooldown = 4;
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
            freezeInFreecam = Boolean.parseBoolean(p.getProperty("freezeInFreecam", "false"));
            fillHoles = Boolean.parseBoolean(p.getProperty("fillHoles", "true"));
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not read Auto Mine settings", e);
        }
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("pickaxe3x3", Boolean.toString(pickaxe3x3));
        p.setProperty("freezeInFreecam", Boolean.toString(freezeInFreecam));
        p.setProperty("fillHoles", Boolean.toString(fillHoles));
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, Brand.NAME + " Auto Mine settings");
        } catch (IOException e) {
            PandaBuilderClient.LOGGER.warn("Could not save Auto Mine settings", e);
        }
    }

    private static void stop(Minecraft mc, String reason) {
        on = false;
        forward = 0;
        jump = false;
        strafe = 0;
        centering = false;
        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
        AutoTotem.message(Component.literal(reason == null ? "Auto Mine: OFF" : "Auto Mine stopped: " + reason));
    }
}
