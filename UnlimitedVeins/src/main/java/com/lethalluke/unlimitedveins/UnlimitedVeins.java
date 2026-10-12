package com.lethalluke.unlimitedveins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class UnlimitedVeins implements ModInitializer {
    // Large safety cap to prevent accidental client/server freezes on absurdly huge veins.
    // Set to 1_000_000 if you want an even higher ceiling.
    private static final int MAX_BLOCKS_PER_VEIN = 100_000;
    private static final ThreadLocal<Boolean> BREAKING_VEIN = ThreadLocal.withInitial(() -> false);

    @Override
    public void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) return;
            if (BREAKING_VEIN.get()) return;
            if (!isOre(state)) return;

            BREAKING_VEIN.set(true);
            try {
                mineConnected(level, serverPlayer, pos, state);
            } finally {
                BREAKING_VEIN.set(false);
            }
        });
    }

    private static boolean isOre(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return path.endsWith("_ore") || path.equals("ancient_debris");
    }

    private static void mineConnected(ServerLevel level, ServerPlayer player, BlockPos origin, BlockState originalState) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin.immutable());
        int mined = 0;

        // 26-neighbour search: connected diagonally as well as up/down/north/south/east/west.
        while (!queue.isEmpty() && mined < MAX_BLOCKS_PER_VEIN) {
            BlockPos current = queue.removeFirst();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos next = current.offset(dx, dy, dz);
                        if (!visited.add(next.immutable())) continue;
                        if (!level.hasChunkAt(next)) continue;

                        BlockState found = level.getBlockState(next);
                        if (!found.is(originalState.getBlock())) continue;

                        // destroyBlock uses normal drops, tool/player context, and ore XP behavior.
                        if (level.destroyBlock(next, true, player)) {
                            mined++;
                            queue.addLast(next);
                        }
                    }
                }
            }
        }
    }
}
