package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.*;

/**
 * Ordnet jeder Subklasse (per ID aus ClassRegistry, z.B. "draconic_sorcery")
 * ihre level-gebundenen Abilities zu.
 *
 * IDs kommen aus SubclassDefinition.getId() — erster Parameter im Konstruktor.
 * vars.PlayerSubclass speichert den Display-Namen; resolveSubclassId() in
 * AbilityUtils macht die Übersetzung Display-Name → ID.
 *
 * Neue Ability-Enum-Werte die hier referenziert werden MÜSSEN in Ability.java
 * und AbilityDefinitionRegistry ergänzt werden — siehe Ability_SUBCLASS_ADDITIONS.java.
 */
public class SubclassAbilityRegistry {

    private static final Map<String, Map<Integer, List<Ability>>> SUBCLASS_ABILITIES
            = new HashMap<>();

    static {

        // ── SORCERER ─────────────────────────────────────────────────────────
        SUBCLASS_ABILITIES.put("draconic_sorcery", new LinkedHashMap<>() {{
            put(3,  List.of(Ability.DRACONIC_RESILIENCE));
            // put(6,  List.of(Ability.ELEMENTAL_AFFINITY));   // TODO
            // put(14, List.of(Ability.DRAGON_WINGS));         // TODO
        }});

        SUBCLASS_ABILITIES.put("wild_magic", new LinkedHashMap<>() {{
            put(3, List.of(Ability.WILD_MAGIC_SURGE));
        }});

        // aberrant_sorcery / clockwork_sorcery → TODO (keine Ability-Enum-Werte noch)

        // ── FIGHTER ──────────────────────────────────────────────────────────
        SUBCLASS_ABILITIES.put("champion", new LinkedHashMap<>() {{
            put(3, List.of(Ability.IMPROVED_CRITICAL));
        }});

        SUBCLASS_ABILITIES.put("battle_master", new LinkedHashMap<>() {{
            put(3, List.of(Ability.BATTLE_MASTER));
        }});

        SUBCLASS_ABILITIES.put("eldritch_knight", new LinkedHashMap<>() {{
            put(3, List.of(Ability.ELDRITCH_KNIGHT_SPELLCASTING));
        }});

        // psi_warrior → TODO

        // ── BARBARIAN ────────────────────────────────────────────────────────
        SUBCLASS_ABILITIES.put("berserker", new LinkedHashMap<>() {{
            put(3, List.of(Ability.FRENZY));
        }});

        // wild_heart / world_tree / zealot → TODO

        // ── DRUID ────────────────────────────────────────────────────────────
        SUBCLASS_ABILITIES.put("circle_moon", new LinkedHashMap<>() {{
            put(3, List.of(Ability.CIRCLE_OF_THE_MOON));
        }});

        // circle_land / circle_sea / circle_stars → TODO

        // ── PALADIN ──────────────────────────────────────────────────────────
        SUBCLASS_ABILITIES.put("oath_devotion", new LinkedHashMap<>() {{
            put(3, List.of(Ability.SACRED_WEAPON, Ability.DIVINE_HEALTH));
        }});

        // oath_glory / oath_ancients / oath_vengeance → TODO

        // ── BARD / CLERIC / MONK / RANGER / ROGUE / WARLOCK / WIZARD ─────────
        // → alle TODO (keine passenden Ability-Enum-Werte noch)
    }

    /**
     * @param subclassId Die ID aus SubclassDefinition, z.B. "draconic_sorcery"
     *                   (NICHT der Display-Name "Draconic Sorcery").
     */
    public static Map<Integer, List<Ability>> getAbilities(String subclassId) {
        if (subclassId == null || subclassId.isBlank()) return Map.of();
        return SUBCLASS_ABILITIES.getOrDefault(subclassId, Map.of());
    }
}