package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;

/**
 * Effects that fire exactly once when an ability is added to a player.
 * Called automatically by AbilityUtils.addAbility() for ONE_TIME_TRIGGER abilities.
 * FLIGHT is PLAYER_TRIGGERED — handled in AbilityMethods_PlayerTriggered.
 */
public class AbilityMethods_OneTime {

    public static boolean execute(ServerPlayer player, Ability ability) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean statsChanged = false;

        switch (ability) {
            case PRIMAL_CHAMPION -> { vars.Strength += 4; vars.Constitution += 4; statsChanged = true; }
            case BODY_AND_MIND   -> { vars.Dexterity += 4; vars.Wisdom += 4; statsChanged = true; }
            // SPEED_BONUS_5: applyAttrs already reads the ability list — just trigger a recalc
            case SPEED_BONUS_5   -> statsChanged = true;
            default -> {}
        }

        if (statsChanged) {
            vars.markSyncDirty();
            CharacterCreationPacket.applyAttrs(player);
        }
        return statsChanged;
    }

    /**
     * Re-applies entity-level effects lost on respawn.
     * Called by KeepCharacterPacket after applyAttrs().
     * Stats are preserved via clonePlayer; only MC-entity-level effects need re-applying.
     */
    public static void reapplyEntityEffects(ServerPlayer player) {
        // Re-run always-active tick: restores Night Vision, Unarmored Defense,
        // conditional speed mods, and Dwarven Toughness HP sync.
        AbilityMethods_AlwaysActive.tick(player);
    }
}
