package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Central hub for passive ability processing.
 * Delegates to AbilityMethods_AlwaysActive every INTERVAL ticks.
 *
 * Register AbilityMethods_SelfTriggered separately in DndMod:
 *   NeoForge.EVENT_BUS.register(new AbilityMethods_SelfTriggered());
 */
public class AbilityPassiveTriggers {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!player.getData(DndModVariables.PLAYER_VARIABLES).FinishedCharacterCreation) return;

        if (player.tickCount % AbilityMethods_AlwaysActive.INTERVAL != 0) return;

        AbilityMethods_AlwaysActive.tick(serverPlayer);
    }
}
