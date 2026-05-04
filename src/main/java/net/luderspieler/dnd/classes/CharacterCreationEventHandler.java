package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.classes.OpenCreationGuiPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Registered on the NeoForge event bus by DndModNetworkRegistry.
 *
 * - LivingDeathEvent:        resets FinishedCharacterCreation when player dies
 * - PlayerLoggedInEvent:     opens GUI on first join if not finished
 * - PlayerRespawnEvent:      opens GUI after respawn if not finished
 *                            (fires AFTER the player is alive — safe, no death screen conflict)
 */

public class CharacterCreationEventHandler {

    // ── DEATH: reset the flag so the GUI opens after respawn ──
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;



        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.FinishedCharacterCreation = false;
        vars.markSyncDirty();
        // Do NOT open any GUI here — player is still dying, death screen is showing.
        // The GUI will open in onPlayerRespawn once the player is alive again.
    }

    // ── FIRST JOIN: open immediately if they haven't created a character ──
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;



        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!vars.FinishedCharacterCreation) {
            // First ever join: no existing character, go straight to creation
            boolean hasExisting = hasExistingCharacter(vars);
            PacketDistributor.sendToPlayer(player, new OpenCreationGuiPacket(hasExisting));
        }
    }

    // ── RESPAWN: player is alive in world, safe to open GUI now ──
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;




        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!vars.FinishedCharacterCreation) {
            boolean hasExisting = hasExistingCharacter(vars);
            // hasExisting = true → show "Keep / New" choice
            // hasExisting = false → go straight to race list (shouldn't happen on respawn normally)
            PacketDistributor.sendToPlayer(player, new OpenCreationGuiPacket(hasExisting));
        }
    }

    /** Player has an existing character if race and class are set */
    private boolean hasExistingCharacter(DndModVariables.PlayerVariables vars) {
        return vars.PlayerRace  != null && !vars.PlayerRace.isEmpty()
                && vars.PlayerClass != null && !vars.PlayerClass.isEmpty();
    }
}