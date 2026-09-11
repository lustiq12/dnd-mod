package net.luderspieler.dnd.npc;

import net.luderspieler.dnd.DndMod;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Picks a name and a random subset of trades from the matching preset, then writes the result into DATA_Configuration. */
public class NpcConfigInitializer {

    private NpcConfigInitializer() {
    }

    public static NpcConfiguration initialize(Entity entity) {
        String className = entity.getClass().getSimpleName();

        NpcPreset preset = NpcPresetRegistry.get(className);

        DndMod.LOGGER.info("NPC INIT: class = {}", className);
        DndMod.LOGGER.info("NPC INIT: preset = {}", preset);

        if (preset == null) {
            DndMod.LOGGER.error("NPC INIT: NO PRESET FOR '{}'", className);
            return null;
        }

        RandomSource random = entity.level().getRandom();

        String name = preset.namePool().isEmpty()
                ? ""
                : preset.namePool().get(
                random.nextInt(preset.namePool().size())
        );

        List<NpcTradeEntry> pool = new ArrayList<>(preset.tradePool());
        Collections.shuffle(pool, new Random(random.nextLong()));

        int range = Math.max(0, preset.maxTrades() - preset.minTrades());

        int amount = Math.min(
                pool.size(),
                preset.minTrades()
                        + (range > 0
                        ? random.nextInt(range + 1)
                        : 0)
        );

        List<NpcTradeEntry> chosenTrades =
                new ArrayList<>(pool.subList(0, amount));

        NpcConfiguration config = new NpcConfiguration(
                name,
                preset.startNodeId(),
                preset.dialogNodes(),
                chosenTrades,
                preset.idleLines()
        );

        NpcDataAccess.setConfiguration(
                entity,
                NpcJson.toJson(config)
        );

        return config;
    }
}
