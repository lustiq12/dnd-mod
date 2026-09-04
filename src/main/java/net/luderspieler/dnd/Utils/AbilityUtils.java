package net.luderspieler.dnd.Utils;

import net.luderspieler.dnd.character.AbilitysAndFeats.AbilityMethods_OneTime;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.*;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility-Klasse für Ability-Operationen.
 * Speichert Abilities als komma-getrennte String-Liste in vars.Abilities.
 *
 * WICHTIG: Alle Methoden, die Abilities hinzufügen, müssen addAbility() verwenden.
 * Nur addAbility() initialisiert _uses in AbilityData korrekt.
 * Direktes Manipulieren von vars.Abilities überspringt die Use-Initialisierung!
 */
public class AbilityUtils {

    /**
     * Prüft ob ein Spieler eine bestimmte Ability hat.
     */
    public static boolean hasAbility(ServerPlayer player, Ability ability) {
        if (player == null || ability == null) return false;
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return parseAbilitiesString(vars.Abilities).contains(ability);
    }

    /**
     * Fügt eine Ability hinzu und initialisiert sofort ihre _uses in AbilityData.
     * Idempotent — mehrfache Aufrufe für dieselbe Ability sind sicher.
     * Feuert ONE_TIME_TRIGGER beim ersten Hinzufügen.
     */
    public static void addAbility(ServerPlayer player, Ability ability) {
        if (player == null || ability == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> abilities = parseAbilitiesString(vars.Abilities);

        // ── Darkvision-Hierarchie ────────────────────────────────────
        if (ability == Ability.DARKVISION_60 && abilities.contains(Ability.DARKVISION_120)) return;
        if (ability == Ability.DARKVISION_120) abilities.remove(Ability.DARKVISION_60);

        // ── Duplikat-Check ───────────────────────────────────────────
        if (abilities.contains(ability)) return;

        abilities.add(ability);
        vars.Abilities = listToString(abilities);

        // ── _uses sofort initialisieren ───────────────────────────────
        int maxUses = AbilityResetRegistry.getMaxUses(player, ability, vars);
        if (maxUses > 0) {
            AbilityDataUtils.set(vars, ability.name() + "_uses", maxUses);
        }

        vars.markSyncDirty();

        // ── ONE_TIME_TRIGGER sofort feuern ───────────────────────────
        AbilityCategory category = AbilityDefinitionRegistry.getCategory(ability);
        if (category == AbilityCategory.ONE_TIME_TRIGGER) {
            AbilityMethods_OneTime.execute(player, ability);
        }
    }

    /**
     * Entfernt eine Ability von einem Spieler.
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
     * Gibt alle Abilities eines Spielers zurück.
     */
    public static List<Ability> getPlayerAbilities(ServerPlayer player) {
        if (player == null) return new ArrayList<>();
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return parseAbilitiesString(vars.Abilities);
    }

    // ── KLASSEN-ABILITIES ─────────────────────────────────────────────

    /**
     * Fügt alle Klassen-Abilities für das aktuelle Level hinzu.
     * Ruft addAbility() für jede neue Ability auf → _uses werden korrekt gesetzt.
     * Idempotent — bereits vorhandene Abilities werden übersprungen.
     */
    public static void updateClassAbilities(ServerPlayer player) {
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int playerLevel = (int) vars.PlayerLevel;
        var classAbilities = AbilityRegistry.getClassAbilities(vars.PlayerClass);

        for (int level = 1; level <= playerLevel; level++) {
            List<Ability> abilitiesAtLevel = classAbilities.get(level);
            if (abilitiesAtLevel == null) continue;
            for (Ability ability : abilitiesAtLevel) {
                addAbility(player, ability); // dedupliziert intern + initialisiert _uses
            }
        }
    }

    // ── RASSEN-ABILITIES ──────────────────────────────────────────────

    /**
     * Fügt alle Rassen- und Subrace-Abilities für einen Spieler hinzu (Level 1 Basis
     * + level-gebundene Abilities bis zum aktuellen Level).
     */
    public static void addRaceAbilities(ServerPlayer player) {
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String playerRace = vars.PlayerRace;
        String playerSubrace = vars.PlayerSubrace;

        for (Ability ability : AbilityRegistry.getRaceAbilities(playerRace)) {
            addAbility(player, ability);
        }

        if (playerSubrace != null && !playerSubrace.isEmpty() && !playerSubrace.equals("\"\"")) {
            for (Ability ability : AbilityRegistry.getSubraceAbilities(playerRace, playerSubrace)) {
                addAbility(player, ability);
            }
        }

        updateRaceAbilitiesForLevel(player, (int) vars.PlayerLevel);
    }

    /**
     * Fügt Rassen-Abilities hinzu, die erst bei einem bestimmten Level freigeschaltet werden.
     * Muss bei jedem Level-Up aufgerufen werden.
     */
    public static void updateRaceAbilitiesForLevel(ServerPlayer player, int level) {
        if (player == null || level < 1) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        List<Ability> raceAbilities = AbilityRegistry.getRaceAbilities(vars.PlayerRace);

        for (Ability ability : raceAbilities)
            addAbility(player, ability); // Duplikate intern behandelt
    }

    // ── SUBKLASSEN-ABILITIES ────────────────────────────────────────────

    /**
     * Fügt Subklassen-Abilities hinzu, die bis zum aktuellen Level freigeschaltet
     * sind (level-gated, analog zu updateRaceAbilitiesForLevel). Muss aufgerufen
     * werden wenn:
     *   - der Spieler eine Subklasse wählt (ChoiceExecutor "SUBCLASS")
     *   - der Spieler ein Level-Up erreicht (LevelEvents, DndCommand)
     *
     * Liest vars.PlayerSubclass (Display-Name, z.B. "Path of the Berserker"),
     * löst das über die SUBCLASSES-Liste in ClassRegistry zur id auf
     * ("berserker") und fragt damit SubclassAbilityRegistry ab.
     */
    public static void updateSubclassAbilitiesForLevel(ServerPlayer player, int level) {
        if (player == null || level < 1) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String subclassId = resolveSubclassId(vars);
        if (subclassId == null) return;

        Map<Integer, List<Ability>> leveled = SubclassAbilityRegistry.getAbilities(subclassId);
        for (Map.Entry<Integer, List<Ability>> entry : leveled.entrySet()) {
            if (entry.getKey() <= level) {
                for (Ability ability : entry.getValue()) {
                    addAbility(player, ability);
                }
            }
        }
    }

    /**
     * vars.PlayerSubclass speichert den Display-Namen (z.B. "Path of the
     * Berserker"), SubclassAbilityRegistry arbeitet aber mit der id
     * (z.B. "berserker"). Diese Hilfsmethode löst das über ClassRegistry auf.
     */
    private static String resolveSubclassId(DndModVariables.PlayerVariables vars) {
        if (vars.PlayerSubclass == null || vars.PlayerSubclass.isBlank()
                || vars.PlayerSubclass.equals("\"\"")) return null;

        for (var sub : ClassRegistry.SUBCLASSES) {
            if (sub.getDisplayName().equals(vars.PlayerSubclass)) return sub.getId();
        }
        return null;
    }

    // ==================== HELPER METHODS ====================

    private static List<Ability> parseAbilitiesString(String abilitiesString) {
        List<Ability> abilities = new ArrayList<>();
        if (abilitiesString == null || abilitiesString.isEmpty() || abilitiesString.equals("\"\"")) {
            return abilities;
        }
        for (String name : abilitiesString.split(",")) {
            try {
                abilities.add(Ability.valueOf(name.trim()));
            } catch (IllegalArgumentException ignored) {}
        }
        return abilities;
    }

    private static String listToString(List<Ability> abilities) {
        List<String> names = new ArrayList<>();
        for (Ability ability : abilities) names.add(ability.name());
        return String.join(",", names);
    }
}