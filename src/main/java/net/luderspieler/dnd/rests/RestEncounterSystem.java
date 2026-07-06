package net.luderspieler.dnd.rests;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handles wilderness encounter rolling and mob spawning during a long rest.
 *
 * TODO: Replace Pillager with custom DnD mob types (Orc, Goblin, Bandit)
 *       once those entities exist. Swap EntityType.PILLAGER for your type.
 *
 * Encounter chance scales with difficulty:
 *   Peaceful → 0 %  |  Easy → 15 %  |  Normal → 30 %  |  Hard → 50 %
 */
public class RestEncounterSystem {

    public static boolean rollEncounter(ServerPlayer player) {
        float chance = switch (player.level().getDifficulty()) {
            case PEACEFUL -> 1.00f;
            case EASY     -> 1.00f;
            case NORMAL   -> 1.00f;
            case HARD     -> 1.00f;
        };
        return player.getRandom().nextFloat() < chance;
    }

    /** Spawns 2-4 enemies in a ring 4-8 blocks from the player. */
    public static void spawnWave(ServerPlayer player) {
        // ServerPlayer.level() returns ServerLevel in 1.21.x.
        ServerLevel level = (ServerLevel) player.level();
        int count = 2 + player.getRandom().nextInt(3); // 2, 3 or 4

        for (int i = 0; i < count; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2;
            double dist  = 4 + player.getRandom().nextDouble() * 4; // 4-8 blocks

            double x = player.getX() + Math.cos(angle) * dist;
            double z = player.getZ() + Math.sin(angle) * dist;
            double y = findSurfaceY(level, (int) x, (int) player.getY(), (int) z);

            // TODO: swap for custom mob type when available.
            Pillager mob = EntityType.PILLAGER.create(level, EntitySpawnReason.EVENT);
            if (mob == null) continue;

            mob.setPos(x, y, z);
            mob.setYRot((float) Math.toDegrees(angle + Math.PI));
            mob.finalizeSpawn(level,
                    level.getCurrentDifficultyAt(BlockPos.containing(x, y, z)),
                    EntitySpawnReason.EVENT, null);
            level.addFreshEntity(mob);
        }

        player.displayClientMessage(
                Component.literal("§4⚔  Your camp is under attack!"), false);
        player.displayClientMessage(
                Component.literal("§cEnemies emerge from the darkness..."), false);
    }

    /** Walks downward from startY to find the first solid surface. */
    private static double findSurfaceY(ServerLevel level, int x, int startY, int z) {
        for (int y = startY + 5; y > startY - 10; y--) {
            BlockPos pos    = new BlockPos(x, y, z);
            BlockState foot = level.getBlockState(pos);
            BlockState head = level.getBlockState(pos.above());
            // isSolidRender() takes no arguments in 1.21.x.
            if (foot.isSolidRender() && head.isAir()) {
                return y + 1;
            }
        }
        return startY; // fallback
    }
}