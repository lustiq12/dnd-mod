package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ordnet jeder Subklasse (per id, z.B. "berserker") ihre level-gebundenen
 * Abilities zu — analog zu AbilityRegistry.RACE_LEVELED_ABILITIES.
 *
 * SCOPE-HINWEIS:
 * Die meisten 2024-PHB-Subklassen haben aktuell KEINE passenden Einträge im
 * Ability-Enum — ihre Features existieren bisher nur als Flavour-Text in
 * ClassRegistry's *_LEVELING_DESCRIPTIONS-Listen (z.B. "Frenzy: Extra attack
 * each turn while raging"), ohne mechanische Implementierung.
 *
 * Alle ~40 Subklassen × ~4 Features vollständig mit neuen Ability-Enum-Werten,
 * Kategorisierung (PLAYER_TRIGGERED/SELF_TRIGGERED/...) und tatsächlicher
 * Spiellogik auszustatten ist ein eigenes großes Vorhaben.
 *
 * Diese Klasse fixt die PIPELINE: Subklasse wählen (ChoiceExecutor) und
 * Level-Up (LevelEvents/DndCommand) rufen jetzt tatsächlich
 * AbilityUtils.updateSubclassAbilitiesForLevel() auf — vorher passierte
 * beim Subklassen-Wählen GAR NICHTS außer vars.PlayerSubclass zu setzen.
 *
 * Befülle SUBCLASS_ABILITIES hier, sobald für eine Subklassen-Ability ein
 * passender Ability-Enum-Wert + eine Implementierung existiert.
 */
public class SubclassAbilityRegistry {

    private static final Map<String, Map<Integer, List<Ability>>> SUBCLASS_ABILITIES = new HashMap<>();

    // Aktuell absichtlich leer — siehe Scope-Hinweis oben. Beispiel für das
    // erwartete Format, sobald Inhalte ergänzt werden:
    //
    // static {
    //     SUBCLASS_ABILITIES.put("berserker", new LinkedHashMap<>() {{
    //         put(3, List.of(Ability.FRENZY));
    //         put(6, List.of(Ability.MINDLESS_RAGE));
    //     }});
    // }

    /** Gibt die Level→Abilities-Map einer Subklasse zurück (leer falls keine definiert ist). */
    public static Map<Integer, List<Ability>> getAbilities(String subclassId) {
        if (subclassId == null) return Map.of();
        return SUBCLASS_ABILITIES.getOrDefault(subclassId, Map.of());
    }
}