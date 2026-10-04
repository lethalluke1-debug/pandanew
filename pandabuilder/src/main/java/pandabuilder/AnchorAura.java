package pandabuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * Places a respawn anchor next to the nearest mob, charges it with glowstone and detonates it,
 * all within a single tick. Only runs in your own singleplayer world (not on servers or LAN).
 */
public final class AnchorAura {
    /** How far from the target's feet an anchor may be placed or reused. */
    private static final int PLACE_RADIUS = 2;

    private static int cooldown;
    private static boolean warnedMissing;
    private static boolean warnedNether;

    private AnchorAura() {
    }

    public static void toggle() {
        Settings.anchorAura = !Settings.anchorAura;
        Settings.save();
        warnedMissing = false;
        warnedNether = false;
        AutoTotem.message(Component.literal("Anchor Aura " + (Settings.anchorAura ? "ON" : "OFF"))
                .withStyle(Settings.anchorAura ? ChatFormatting.GREEN : ChatFormatting.RED));
        if (Settings.anchorAura && !isOwnWorld(Minecraft.getInstance())) {
            AutoTotem.message(Component.literal("Anchor Aura only works in your own singleplayer world.")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private static boolean isOwnWorld(Minecraft mc) {
        IntegratedServer server = mc.getSingleplayerServer();
        return server != null && !server.isPublished();
    }

    public static void tick(Minecraft mc) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (!Settings.anchorAura || player == null || level == null || mc.gameMode == null
                || mc.screen != null || player.getHealth() <= 0 || !isOwnWorld(mc)) {
            return;
        }
        // Anchors don't explode in the Nether, they just set your spawn.
        if (level.dimension() == Level.NETHER) {
            if (!warnedNether) {
                warnedNether = true;
                AutoTotem.message(Component.literal("Anchor Aura: anchors don't explode in the Nether.")
                        .withStyle(ChatFormatting.GOLD));
            }
            return;
        }
        warnedNether = false;

        LivingEntity target = findTarget(player, level);
        if (target == null) {
            return;
        }

        Inventory inventory = player.getInventory();
        int anchorSlot = findHotbar(inventory, Items.RESPAWN_ANCHOR);
        int glowstoneSlot = findHotbar(inventory, Items.GLOWSTONE);
        if (glowstoneSlot < 0) {
            warnMissing();
            return;
        }

        int previousSlot = inventory.getSelectedSlot();
        BlockPos anchorPos = findExistingAnchor(player, level, target);
        if (anchorPos == null) {
            if (anchorSlot < 0) {
                warnMissing();
                return;
            }
            BlockHitResult placement = findPlacement(player, level, target);
            if (placement == null) {
                return;
            }
            anchorPos = placement.getBlockPos().relative(placement.getDirection());
            use(mc, player, anchorSlot, placement);
        }
        warnedMissing = false;

        BlockHitResult anchorHit = new BlockHitResult(Vec3.atCenterOf(anchorPos).add(0, 0.5, 0), Direction.UP,
                anchorPos, false);
        BlockState state = level.getBlockState(anchorPos);
        if (!state.is(Blocks.RESPAWN_ANCHOR) || state.getValue(RespawnAnchorBlock.CHARGE) == 0) {
            use(mc, player, glowstoneSlot, anchorHit);
        }
        // Clicking a charged anchor with anything other than glowstone makes it explode.
        int detonateSlot = anchorSlot >= 0 ? anchorSlot : findNonGlowstone(inventory);
        if (detonateSlot >= 0) {
            use(mc, player, detonateSlot, anchorHit);
        }

        inventory.setSelectedSlot(previousSlot);
        cooldown = Math.max(0, Settings.anchorDelay);
    }

    private static void use(Minecraft mc, LocalPlayer player, int hotbarSlot, BlockHitResult hit) {
        player.getInventory().setSelectedSlot(hotbarSlot);
        // useItemOn sends the selected-slot update before the click, so the server sees them in order.
        mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
        player.swing(InteractionHand.MAIN_HAND);
    }

    private static LivingEntity findTarget(LocalPlayer player, ClientLevel level) {
        double range = Settings.anchorRange;
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(range),
                e -> e != player && e.isAlive() && !e.isSpectator() && player.distanceToSqr(e) <= range * range);
        return candidates.stream().min(Comparator.comparingDouble(player::distanceToSqr)).orElse(null);
    }

    private static BlockPos findExistingAnchor(LocalPlayer player, ClientLevel level, LivingEntity target) {
        BlockPos feet = target.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(feet.offset(-PLACE_RADIUS, -1, -PLACE_RADIUS),
                feet.offset(PLACE_RADIUS, PLACE_RADIUS, PLACE_RADIUS))) {
            if (!level.getBlockState(pos).is(Blocks.RESPAWN_ANCHOR) || !inReach(player, pos)) {
                continue;
            }
            double dist = target.distanceToSqr(Vec3.atCenterOf(pos));
            if (dist < bestDist) {
                bestDist = dist;
                best = pos.immutable();
            }
        }
        return best;
    }

    /** Finds a free spot next to the target and the face of a neighbouring block to click to place there. */
    private static BlockHitResult findPlacement(LocalPlayer player, ClientLevel level, LivingEntity target) {
        BlockPos feet = target.blockPosition();
        AABB targetBox = target.getBoundingBox();
        BlockHitResult best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(feet.offset(-PLACE_RADIUS, -1, -PLACE_RADIUS),
                feet.offset(PLACE_RADIUS, PLACE_RADIUS, PLACE_RADIUS))) {
            double dist = target.distanceToSqr(Vec3.atCenterOf(pos));
            if (dist >= bestDist || !inReach(player, pos) || !level.getBlockState(pos).canBeReplaced()) {
                continue;
            }
            AABB blockBox = new AABB(pos);
            if (blockBox.intersects(targetBox) || blockBox.intersects(player.getBoundingBox())
                    || !level.getEntitiesOfClass(LivingEntity.class, blockBox).isEmpty()) {
                continue;
            }
            BlockHitResult hit = supportFace(level, pos.immutable());
            if (hit != null) {
                bestDist = dist;
                best = hit;
            }
        }
        return best;
    }

    private static BlockHitResult supportFace(ClientLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState state = level.getBlockState(neighbor);
            // Skip blocks that would react to being clicked (anchors, chests, furnaces, ...).
            if (state.canBeReplaced() || state.is(Blocks.RESPAWN_ANCHOR) || level.getBlockEntity(neighbor) != null) {
                continue;
            }
            Direction face = dir.getOpposite();
            Vec3 hitVec = Vec3.atCenterOf(neighbor).add(face.getStepX() * 0.5, face.getStepY() * 0.5,
                    face.getStepZ() * 0.5);
            return new BlockHitResult(hitVec, face, neighbor, false);
        }
        return null;
    }

    private static boolean inReach(LocalPlayer player, BlockPos pos) {
        double reach = player.blockInteractionRange();
        return player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) <= reach * reach;
    }

    private static int findHotbar(Inventory inventory, Item item) {
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private static int findNonGlowstone(Inventory inventory) {
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() != Items.GLOWSTONE) {
                return i;
            }
        }
        return -1;
    }

    private static void warnMissing() {
        if (!warnedMissing) {
            warnedMissing = true;
            AutoTotem.message(Component.literal("Anchor Aura: put respawn anchors and glowstone in your hotbar.")
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
