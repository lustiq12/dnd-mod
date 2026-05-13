package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class BlockUpdater {

    /**
     * Registriert ein Update für einen Block.
     * Format im String: x,y,z,expiryTime,action,blockName;
     */
    public static void registerBlockForUpdate(ServerLevel level, BlockPos pos, long delay, String action) {
        DndModVariables.WorldVariables vars = DndModVariables.WorldVariables.get(level);
        long expiry = level.getGameTime() + delay;

        // Wir speichern den Registry-Namen des Blocks an dieser Position (z.B. "minecraft:light")
        String blockName = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();

        String entry = pos.getX() + "," + pos.getY() + "," + pos.getZ() + "," + expiry + "," + action + "," + blockName + ";";

        // Verhindert, dass Anführungszeichen aus der Initialisierung stören
        if (vars.BlocksToUpdate.equals("\"\"")) vars.BlocksToUpdate = "";

        vars.BlocksToUpdate += entry;
        vars.markSyncDirty();
    }

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (level.getGameTime() % 20 != 0) return;

            DndModVariables.WorldVariables vars = DndModVariables.WorldVariables.get(level);
            if (vars.BlocksToUpdate.isEmpty() || vars.BlocksToUpdate.equals("\"\"")) return;

            String[] entries = vars.BlocksToUpdate.split(";");
            StringBuilder remainingBlocks = new StringBuilder();
            boolean changed = false;
            long currentTime = level.getGameTime();

            for (String entry : entries) {
                if (entry.isBlank()) continue;
                try {
                    String[] data = entry.split(",");
                    BlockPos pos = new BlockPos(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                    long expiry = Long.parseLong(data[3]);
                    String action = data[4];
                    String expectedBlockName = data[5];

                    if (currentTime >= expiry) {
                        String currentBlockName = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();

                        if (currentBlockName.equals(expectedBlockName)) {
                            handleAction(level, pos, action);
                        }
                        changed = true;
                    } else {
                        remainingBlocks.append(entry).append(";");
                    }
                } catch (Exception e) {
                    changed = true;
                }
            }

            if (changed) {
                vars.BlocksToUpdate = remainingBlocks.toString();
                vars.markSyncDirty();
            }
        }
    }

    private void handleAction(ServerLevel level, BlockPos pos, String action) {
        switch (action) {
            case "DESPAWN" -> {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }

        }
    }
}