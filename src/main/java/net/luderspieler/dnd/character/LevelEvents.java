package net.luderspieler.dnd.character;

import net.luderspieler.dnd.aUtils.AbilityUtils;
import net.luderspieler.dnd.character.choices.ChoiceUpdateSystem;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

        while (nextLevel < 20 && vars.PlayerXP >= getRequiredXP(nextLevel + 1)) {
            nextLevel++;
        }

        if (nextLevel > currentLevel) {
            updatePlayerLevel(serverPlayer, nextLevel, false);
            serverPlayer.displayClientMessage(
                    Component.literal("§6§lLEVEL UP! §fYou are now Level " + nextLevel),
                    false);
        } else {
            vars.markSyncDirty();
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SHARED STATIC HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Setzt das Level des Spielers, aktualisiert alle Abilities (Klasse + Rasse +
     * Subklasse level-gebunden), berechnet Attribute neu und triggert Choice-Updates.
     */
    public static void updatePlayerLevel(ServerPlayer player, int targetLevel, boolean silent) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // 1. Level-Werte setzen
        vars.PlayerLevel     = targetLevel;
        vars.PlayerXP        = getRequiredXP(targetLevel);
        vars.ProficiencyBonus = getProficiencyBonus(targetLevel);

        // 2. Neue Klassen-Abilities freischalten
        //    (ruft addAbility() auf → _uses werden sofort initialisiert)
        AbilityUtils.updateClassAbilities(player);

        // 3. Level-gebundene Rassen-Abilities freischalten
        //    z.B. Dragonborn FLIGHT ab Level 5, Aasimar CELESTIAL_REVELATION ab Level 3
        AbilityUtils.updateRaceAbilitiesForLevel(player, targetLevel);

        // 3.5 Level-gebundene Subklassen-Abilities freischalten
        //     (wirkt nur falls eine Subklasse bereits gewählt wurde und für sie
        //     Einträge in SubclassAbilityRegistry existieren)
        AbilityUtils.updateSubclassAbilitiesForLevel(player, targetLevel);

        // 4. HP, Geschwindigkeit und alle Attribut-Modifier neu berechnen
        CharacterCreationPacket.applyAttrs(player);

        // 5. ASI/Feat/Subclass-Choices aktualisieren
        ChoiceUpdateSystem.updateChoices(player);

        // 6. Sync
        vars.markSyncDirty();

        if (!silent) {
            player.displayClientMessage(
                    Component.literal("§6§lLevel set to " + targetLevel + "!"),
                    false);
        }
    }

    /**
     * Standard D&D 5e/2024 Proficiency Bonus nach Character-Level.
     */
    public static int getProficiencyBonus(int level) {
        if (level >= 17) return 6;
        if (level >= 13) return 5;
        if (level >= 9)  return 4;
        if (level >= 5)  return 3;
        return 2;
    }

    /**
     * Mindest-XP für ein bestimmtes Level.
     */
    public static long getRequiredXP(int level) {
        return switch (level) {
            case 1  -> 0L;
            case 2  -> 24L;
            case 3  -> 72L;
            case 4  -> 216L;
            case 5  -> 520L;
            case 6  -> 1120L;
            case 7  -> 1840L;
            case 8  -> 2720L;
            case 9  -> 3840L;
            case 10 -> 5120L;
            case 11 -> 6800L;
            case 12 -> 8000L;
            case 13 -> 9600L;
            case 14 -> 11200L;
            case 15 -> 13200L;
            case 16 -> 15600L;
            case 17 -> 18000L;
            case 18 -> 21200L;
            case 19 -> 24400L;
            case 20 -> 28400L;
            default -> level > 20 ? 28400L + (long)(level - 20) * 4000L : 0L;        };
    }
}