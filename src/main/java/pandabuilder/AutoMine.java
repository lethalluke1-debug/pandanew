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

/** Digs a 1 wide, 2 tall tunnel in the direction the player faced when it was turned on. */
public final class AutoMine {
    private static boolean on;
    private static Direction direction = Direction.NORTH;
    private static boolean walking;
    private static boolean jumping;
    /** For pickaxes that break 3x3: only the head-height block ahead is mined, the pickaxe clears the rest. */
    private static boolean pickaxe3x3;

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

    /** Read by KeyboardInputMixin, which walks the player forward. Not tied to the W key, so Freecam can use it. */
    public static boolean isWalking() {
        return on && walking;
    }

    public static boolean isJumping() {
        return on && walking && jumping;
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
        walking = false;
        jumping = false;
        if (mc.gui.screen() != null) return;

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

        // Head height first. A 3x3 pickaxe centred there also clears the feet block and the row above.
        boolean headSolid = !level.getBlockState(head).isAir();
        boolean feetSolid = !level.getBlockState(feet).isAir();
        BlockPos target = headSolid ? head : feetSolid ? feet : null;

        // In 3x3 mode a lone block at feet height is stepped onto instead: mining it with a 3x3 pickaxe would dig
        // out the floor ahead. Only when there's no headroom to step up is it mined.
        if (pickaxe3x3 && !headSolid && feetSolid
                && level.getBlockState(head.above()).isAir() && level.getBlockState(player.blockPosition().above(2)).isAir()) {
            player.setXRot(0);
            walking = true;
            jumping = true;
            return;
        }

        if (target == null) {
            player.setXRot(0);
            walking = true;
            return;
        }

        BlockState state = level.getBlockState(target);
        if (state.getDestroySpeed(level, target) < 0) {
            stop(mc, "unbreakable block");
            return;
        }
        lookAt(player, target);
        selectBestTool(player.getInventory(), state);
        mc.gameMode.continueDestroyBlock(target, direction.getOpposite());
        player.swing(InteractionHand.MAIN_HAND);
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
        walking = false;
        jumping = false;
        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
        AutoTotem.message(Component.literal(reason == null ? "Auto Mine: OFF" : "Auto Mine stopped: " + reason));
    }
}
