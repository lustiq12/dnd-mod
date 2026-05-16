package net.luderspieler.dnd.character;

import net.luderspieler.dnd.character.abilitys.Ability;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class LevelEvents {

    // ════════════════════════════════════════════════════════════════════════════
    //  XP EVENT
    // ════════════════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public void onXpChange(PlayerXpEvent.XpChange event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        int amount = event.getAmount();
        if (amount <= 0) return;

        DndModVariables.PlayerVariables vars = serverPlayer.getData(DndModVariables.PLAYER_VARIABLES);
        vars.PlayerXP += amount;

        int currentLevel = (int) vars.PlayerLevel;
        int nextLevel = currentLevel;

        // Berechne wie viele Level aufgestiegen wurde
        while (nextLevel < 20 && vars.PlayerXP >= getRequiredXP(nextLevel + 1)) {
            nextLevel++;
        }

        if (nextLevel > currentLevel) {
            // Nutze die gruppierte Methode für das Level-Up
            updatePlayerLevel(serverPlayer, nextLevel, false);

            serverPlayer.displayClientMessage(
                    Component.literal("§6§lLEVEL UP! §fYou are now Level " + nextLevel),
                    false);
        } else {
            // Falls kein Level-Up, nur XP synchronisieren
            vars.markSyncDirty();
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SHARED STATIC HELPERS  (used by DndCommand as well)
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Reads the abilities unlocked at {@code level} for {@code classId},
     * maps any ability that requires a player choice to its choice-ID,
     * and appends those IDs to {@code vars.ChoicesNeeded}.
     *
     * To add new choice types, extend {@link #choiceIdForAbility(Ability)}.
     */

    /**
     * Maps a class ability to the choice-ID shown in {@link net.luderspieler.dnd.character.choices.LevelingChoiceScreen}.
     * Returns {@code null} for abilities that need no player input.
     *
     * Extend this switch as you add more entries to ChoiceRegistry.
     */


    /**
     * Update player level by setting proficiency bonus, setting xp if needed when set manually, applying attributes and
     * adding the character advancement choices needed
     */
    public static void updatePlayerLevel(ServerPlayer player, int targetLevel, boolean silent) {
        // Variablen intern abrufen
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int oldLevel = (int) vars.PlayerLevel;

        // 1. Werte setzen
        vars.PlayerLevel = targetLevel;
        vars.PlayerXP = getRequiredXP(targetLevel);
        vars.ProficiencyBonus = getProficiencyBonus(targetLevel);


        // 3. Stats & HP aktualisieren (nutzt das Packet-Backend)
        CharacterCreationPacket.applyAttrs(player, null, false);

        // 4. Speichern & Sync
        vars.markSyncDirty();

        if (!silent) {
            player.displayClientMessage(
                    Component.literal("§6§lLevel set to " + targetLevel + "!"),
                    false
            );
        }
    }
    /**
     * Standard D&D 5e proficiency bonus by character level.
     * Public so DndCommand can reuse it.
     */
    public static int getProficiencyBonus(int level) {
        if (level >= 17) return 6;
        if (level >= 13) return 5;
        if (level >= 9)  return 4;
        if (level >= 5)  return 3;
        return 2; // levels 1-4
    }

    /**
     * Minimum XP required to reach {@code level}.
     * Public so DndCommand can set PlayerXP correctly.
     */
    public static long getRequiredXP(int level) {
        return switch (level) {
            case 1  -> 0L;
            case 2  -> 300L;
            case 3  -> 900L;
            case 4  -> 2700L;
            case 5  -> 6500L;
            case 6  -> 14000L;
            case 7  -> 23000L;
            case 8  -> 34000L;
            case 9  -> 48000L;
            case 10 -> 64000L;
            case 11 -> 85000L;
            case 12 -> 100000L;
            case 13 -> 120000L;
            case 14 -> 140000L;
            case 15 -> 165000L;
            case 16 -> 195000L;
            case 17 -> 225000L;
            case 18 -> 265000L;
            case 19 -> 305000L;
            case 20 -> 355000L;
            default -> level > 20 ? 355000L + (long)(level - 20) * 50000L : 0L;
        };
    }
}