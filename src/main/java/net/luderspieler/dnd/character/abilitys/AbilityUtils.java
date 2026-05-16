package net.luderspieler.dnd.character.abilitys;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility-Klasse für Ability-Operationen
 * Speichert Abilities als komma-getrennte String-Liste in vars.Abilities
 */
public class AbilityUtils {

    /**
     * Checked ob ein Spieler eine bestimmte Ability hat
     * Einfache true/false Abfrage
     */
    public static boolean hasAbility(ServerPlayer player, Ability ability) {
        if (player == null || ability == null) return false;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> playerAbilities = parseAbilitiesString(vars.Abilities);

        return playerAbilities.contains(ability);
    }

    /**
     * Fügt eine Ability zu einem Spieler hinzu
     * Wird z.B. vom Choice-System verwendet
     */
    public static void addAbility(ServerPlayer player, Ability ability) {
        if (player == null || ability == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> abilities = parseAbilitiesString(vars.Abilities);

        if (!abilities.contains(ability)) {
            abilities.add(ability);
            vars.Abilities = listToString(abilities);
            vars.markSyncDirty();
        }
    }

    /**
     * Entfernt eine Ability von einem Spieler
     * Wird z.B. vom Choice-System verwendet wenn Spieler eine andere Ability wählt
     */
    public static void removeAbility(ServerPlayer player, Ability ability) {
        if (player == null || ability == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> abilities = parseAbilitiesString(vars.Abilities);

        if (abilities.remove(ability)) {
            vars.Abilities = listToString(abilities);
            vars.markSyncDirty();
        }
    }

    /**
     * Gibt alle Abilities eines Spielers als List zurück
     */
    public static List<Ability> getPlayerAbilities(ServerPlayer player) {
        if (player == null) return new ArrayList<>();

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return parseAbilitiesString(vars.Abilities);
    }

    /**
     * Updated alle Klassen-Abilities für einen Spieler basierend auf Level
     * Wird aufgerufen wenn der Spieler Level-up macht
     */
    public static void updateClassAbilities(ServerPlayer player) {
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String playerClass = vars.PlayerClass;
        int playerLevel = (int) vars.PlayerLevel;

        List<Ability> currentAbilities = parseAbilitiesString(vars.Abilities);
        var classAbilities = AbilityRegistry.getClassAbilities(playerClass);

        // Füge alle Abilities hinzu die der Spieler auf seinem Level haben sollte
        for (int level = 1; level <= playerLevel; level++) {
            if (classAbilities.containsKey(level)) {
                for (Ability ability : classAbilities.get(level)) {
                    if (!currentAbilities.contains(ability)) {
                        currentAbilities.add(ability);
                    }
                }
            }
        }

        vars.Abilities = listToString(currentAbilities);
        vars.markSyncDirty();
    }

    /**
     * Fügt alle Rassen- und Subrace-Abilities für einen Spieler hinzu
     * Wird aufgerufen wenn der Spieler eine Rasse/Subrace wählt
     */
    public static void addRaceAbilities(ServerPlayer player) {
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> abilities = parseAbilitiesString(vars.Abilities);

        // Füge Rassen-Abilities hinzu
        String playerRace = vars.PlayerRace;
        abilities.addAll(AbilityRegistry.getRaceAbilities(playerRace));

        // Füge Subrace-Abilities hinzu falls vorhanden
        String playerSubrace = vars.PlayerSubrace;
        if (playerSubrace != null && !playerSubrace.isEmpty() && !playerSubrace.equals("\"\"")) {
            abilities.addAll(AbilityRegistry.getSubraceAbilities(playerRace, playerSubrace));
        }

        // Entferne Duplikate durch Konvertierung zu Set
        List<Ability> uniqueAbilities = new ArrayList<>(new HashSet<>(abilities));

        vars.Abilities = listToString(uniqueAbilities);
        vars.markSyncDirty();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Konvertiert einen komma-getrennten String zu einer List von Abilities
     */
    private static List<Ability> parseAbilitiesString(String abilitiesString) {
        List<Ability> abilities = new ArrayList<>();

        if (abilitiesString == null || abilitiesString.isEmpty() || abilitiesString.equals("\"\"")) {
            return abilities;
        }

        String[] names = abilitiesString.split(",");
        for (String name : names) {
            try {
                Ability ability = Ability.valueOf(name.trim());
                abilities.add(ability);
            } catch (IllegalArgumentException e) {
                // Ignoriere ungültige Ability-Namen
            }
        }

        return abilities;
    }

    /**
     * Konvertiert eine List von Abilities zu einem komma-getrennten String
     */
    private static String listToString(List<Ability> abilities) {
        List<String> names = new ArrayList<>();
        for (Ability ability : abilities) {
            names.add(ability.name());
        }
        return String.join(",", names);
    }
}