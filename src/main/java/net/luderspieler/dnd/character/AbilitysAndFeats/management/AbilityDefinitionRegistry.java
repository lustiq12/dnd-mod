package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.EnumMap;
import java.util.Map;

/**
 * Maps every Ability to its AbilityCategory.
 * Everything not explicitly listed defaults to PASSIVE_TRACKED.
 */
public class AbilityDefinitionRegistry {

    private static final Map<Ability, AbilityCategory> REGISTRY = new EnumMap<>(Ability.class);
    /**
     * Assign all of the abilities to their respective categories
     */
    static {
        // ── ONE_TIME_TRIGGER ─────────────────────────────────────────
        // Rein passive Stat-Boosts, die nur EINMALIG beim Freischalten wirken.
        oneTime(
                Ability.PRIMAL_CHAMPION,
                Ability.BODY_AND_MIND,
                Ability.SPEED_BONUS_5
        );

        // ── ALWAYS_ACTIVE ─────────────────────────────────────────────
        // Permanente, rein passive Hintergrund-Regeln (Keine Spielerentscheidung).
        alwaysActive(
                // --- Allgemein & Kampfgrundlagen ---
                Ability.UNARMORED_DEFENSE,
                Ability.WEAPON_MASTERY,
                Ability.FIGHTING_STYLE,
                Ability.FIGHTING_STYLE_PALADIN,
                Ability.FIGHTING_STYLE_RANGER,
                Ability.EXTRA_ATTACK_BARBARIAN,
                Ability.EXTRA_ATTACK_FIGHTER,
                Ability.EXTRA_ATTACK_MONK,
                Ability.EXTRA_ATTACK_PALADIN,
                Ability.EXTRA_ATTACK_RANGER,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_ONE,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_TWO,

                // --- Klassenspezifische Passiv-Mechaniken ---
                Ability.PRIMAL_KNOWLEDGE,
                Ability.FAST_MOVEMENT,
                Ability.IMPROVED_BRUTAL_STRIKE_ONE,
                Ability.PERSISTENT_RAGE,
                Ability.IMPROVED_BRUTAL_STRIKE_TWO,
                Ability.INDOMITABLE_MIGHT,
                Ability.JACK_OF_ALL_TRADES,
                Ability.EXPERTISE,
                Ability.FONT_OF_INSPIRATION,
                Ability.IMPROVED_EXPERTISE_ONE,
                Ability.MAGICAL_SECRETS,
                Ability.DIVINE_ORDER,
                Ability.IMPROVED_BLESSED_STRIKES_ONE,
                Ability.IMPROVED_DIVINE_INTERVENTION_ONE,
                Ability.DRUIDIC,
                Ability.PRIMAL_ORDER,
                Ability.IMPROVED_ELEMENTAL_FURY_ONE,
                Ability.BEAST_SPELLS,
                Ability.ARCHDRUID,
                Ability.IMPROVED_INDOMITABLE_ONE,
                Ability.IMPROVED_ACTION_SURGE_ONE,
                Ability.IMPROVED_INDOMITABLE_TWO,
                Ability.MARTIAL_ARTS,
                Ability.UNARMORED_MOVEMENT,
                Ability.EMPOWERED_STRIKES,
                Ability.ACROBATIC_MOVEMENT,
                Ability.HEIGHTENED_FOCUS,
                Ability.DEFLECT_ENERGY,
                Ability.DISCIPLINED_SURVIVOR,
                Ability.AURA_EXPANSION,
                Ability.DEFT_EXPLORER,
                Ability.ROVING,
                Ability.IMPROVED_DEFT_EXPLORER_ONE,
                Ability.RELENTLESS_HUNTER,
                Ability.PRECISE_HUNTER,
                Ability.FERAL_SENSES,
                Ability.FOE_SLAYER,
                Ability.EXPERTISE_ROGUE,
                Ability.THIEVES_CANT,
                Ability.IMPROVED_EXPERTISE_ROGUE_ONE,
                Ability.RELIABLE_TALENT,
                Ability.IMPROVED_CUNNING_STRIKE_ONE,
                Ability.SLIPPERY_MIND,
                Ability.ELUSIVE,
                Ability.SORCEROUS_INCARNATION,
                Ability.ELDRITCH_INVOCATIONS,
                Ability.IMPROVED_MYSTIC_ARCANUM_ONE,
                Ability.IMPROVED_MYSTIC_ARCANUM_TWO,
                Ability.IMPROVED_MYSTIC_ARCANUM_THREE,
                Ability.RITUAL_ADEPT,
                Ability.SCHOLAR,
                Ability.SPELL_MASTERY,
                Ability.SIGNATURE_SPELLS,

                // --- Volks-Passive & Resistenzen ---
                Ability.VERSATILE,
                Ability.DARKVISION_60,
                Ability.DARKVISION_120,
                Ability.DWARVEN_RESILIENCE,
                Ability.DWARVEN_TOUGHNESS,
                Ability.FEY_ANCESTRY,
                Ability.KEEN_SENSES,
                Ability.TRANCE,
                Ability.ELVEN_LINEAGE,
                Ability.HIGH_ELF_LINEAGE,
                Ability.WOOD_ELF_LINEAGE,
                Ability.SPEED_WOOD_ELF,
                Ability.DROW_LINEAGE,
                Ability.BRAVE,
                Ability.HALFLING_NIMBLENESS,
                Ability.NATURALLY_STEALTHY,
                Ability.DRACONIC_ANCESTRY,
                Ability.DAMAGE_RESISTANCE_DRAGONBORN,
                Ability.GNOMISH_LINEAGE,
                Ability.FOREST_GNOME_LINEAGE,
                Ability.ROCK_GNOME_LINEAGE,
                Ability.CELESTIAL_RESISTANCE,
                Ability.OTHERWORLDLY_GIFT,
                Ability.FIENDISH_RESISTANCE,
                Ability.ABYSSAL_LINEAGE,
                Ability.CHTHONIC_LINEAGE,
                Ability.INFERNAL_LINEAGE,
                Ability.GIANT_ANCESTRY,
                Ability.POWERFUL_BUILD,
                Ability.SPEED_GOLIATH,
                Ability.POWERFUL_BUILD_ORC
        );

        // ── PLAYER_TRIGGERED ─────────────────────────────────────────
        // ALLES, wo der Spieler selbst drückt, toggelt oder entscheidet. Landet im Wheel!
        playerTriggered(
                // --- Aktive Zauberklassen-Zugriffe ---
                Ability.SPELLCASTING_BARD,
                Ability.SPELLCASTING_CLERIC,
                Ability.SPELLCASTING_DRUID,
                Ability.SPELLCASTING_PALADIN,
                Ability.SPELLCASTING_RANGER,
                Ability.SPELLCASTING_SORCERER,
                Ability.SPELLCASTING_WIZARD,
                Ability.PACT_MAGIC,

                // --- Klassenspezifische Toggles & Aktionen ---
                Ability.RAGE,
                Ability.RECKLESS_ATTACK,
                Ability.INSTINCTIVE_POUNCE,
                Ability.BRUTAL_STRIKE,
                Ability.BARDIC_INSPIRATION,
                Ability.COUNTERCHARM,          // 2024: Aktive Reaktion bei failed Ally-Saves
                Ability.PEERLESS_SKILL,
                Ability.WORDS_OF_CREATION,
                Ability.CHANNEL_DIVINITY,
                Ability.DIVINE_INTERVENTION,
                Ability.WILD_SHAPE,
                Ability.WILD_COMPANION,
                Ability.WILD_RESURGENCE,
                Ability.SECOND_WIND,
                Ability.ACTION_SURGE,
                Ability.TACTICAL_MIND,
                Ability.INDOMITABLE,           // Reaktion: Spieler entscheidet, neu zu würfeln
                Ability.TACTICAL_MASTER,       // Treffereffekt: Spieler wählt Mastery-Wechsel
                Ability.FOCUS_POINTS,
                Ability.UNCANNY_METABOLISM,
                Ability.DEFLECT_ATTACKS,       // Reaktion
                Ability.SLOW_FALL,             // Reaktion
                Ability.STUNNING_STRIKE,       // On-Hit Toggle
                Ability.SELF_RESTORATION,      // Aktive Zustandsbeendigung
                Ability.SUPERIOR_DEFENSE,
                Ability.LAY_ON_HANDS,
                Ability.PALADINS_SMITE,        // Aktivierte Bonus-Aktion Smites
                Ability.CHANNEL_DIVINITY_PALADIN,
                Ability.FAITHFUL_STEED,
                Ability.ABJURE_FOES,
                Ability.RESTORING_TOUCH,
                Ability.FAVORED_ENEMY,         // Aktive Hunter's Mark Markierung
                Ability.TIRELESS,              // Aktive Temp-HP Generierung
                Ability.NATURES_VEIL,
                Ability.CUNNING_ACTION,
                Ability.STEADY_AIM,
                Ability.CUNNING_STRIKE,        // On-Hit Schadensopferung für Effekte
                Ability.UNCANNY_DODGE,         // Reaktion
                Ability.DEVIOUS_STRIKES,       // On-Hit Schadensopferung für Effekte
                Ability.STROKE_OF_LUCK,
                Ability.INNATE_SORCERY,
                Ability.SORCEROUS_BURST,
                Ability.FONT_OF_MAGIC,
                Ability.METAMAGIC,
                Ability.ARCANE_APOTHEOSIS,
                Ability.MAGICAL_CUNNING,
                Ability.CONTACT_PATRON,
                Ability.MYSTIC_ARCANUM,
                Ability.ELDRITCH_MASTER,
                Ability.ARCANE_RECOVERY,
                Ability.MEMORIZE_SPELLS,

                // --- Aktive Volks- & Unterrassen-Fähigkeiten ---
                Ability.STONE_CUNNING,         // Aktivierbarer Tremorsense
                Ability.BREATH_WEAPON,
                Ability.FLIGHT,
                Ability.HEALING_HANDS,
                Ability.LIGHT_BEARER,
                Ability.CELESTIAL_REVELATION,
                Ability.LARGE_FORM,
                Ability.CLOUDS_JAUNT,
                Ability.FIRES_BURN,            // Treffereffekt: Wahlweise Zusatzschaden
                Ability.FROSTS_CHILL,          // Treffereffekt: Wahlweise Verlangsamung
                Ability.HILLS_TUMBLE,          // Treffereffekt: Wahlweise Prone
                Ability.STONES_ENDURANCE,      // Reaktion
                Ability.STORMS_THUNDER,        // Reaktion
                Ability.ADRENALINE_RUSH
        );

        // ── SELF_TRIGGERED ────────────────────────────────────────────
        // Rein automatische Code-Trigger, Auren und On-Hit-Procs OHNE Spieler-Input.
        selfTriggered(
                // --- Automatische Rettungswürfe & HP-Retter ---
                Ability.DANGER_SENSE,
                Ability.RELENTLESS_RAGE,
                Ability.RELENTLESS_ENDURANCE,
                Ability.EVASION,

                // --- Automatische Refills bei Initiative ---
                Ability.FERAL_INSTINCT,
                Ability.SUPERIOR_INSPIRATION,
                Ability.PERFECT_FOCUS,

                // --- Reine automatische System-Auren ---
                Ability.AURA_OF_PROTECTION,
                Ability.AURA_OF_COURAGE,

                // --- Automatische Kampf-Boni / On-Hit Procs ---
                Ability.SMITE_UNDEAD,          // Löst auto bei Turn Undead aus
                Ability.BLESSED_STRIKES,       // Auto-Schaden beim ersten Treffer
                Ability.ELEMENTAL_FURY,        // Auto-Schaden beim ersten Treffer
                Ability.TACTICAL_SHIFT,        // Auto-Bewegung bei Action Surge
                Ability.STUDIED_ATTACKS,       // Auto-Vorteil nach Fehlschlag
                Ability.RADIANT_STRIKES,       // Auto-Zusatzschaden auf jeden Hit
                Ability.SNEAK_ATTACK,          // Auto-Zusatzschaden unter Bedingungen
                Ability.SORCEROUS_RESTORATION, // Auto-Refill bei Short Rest

                // --- Volks-Automatisierungen ---
                Ability.RESOURCEFUL,           // Auto-Inspiration bei Long Rest
                Ability.LUCKY                  // Halfling: Würfelt die 1 automatisch neu
        );
    }

    // ── HELPERS ───────────────────────────────────────────────────────

    private static void oneTime(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ONE_TIME_TRIGGER);
    }
    private static void alwaysActive(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ALWAYS_ACTIVE);
    }
    private static void playerTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.PLAYER_TRIGGERED);
    }
    private static void selfTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.SELF_TRIGGERED);
    }
    private static void shortRestTrigger(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.SHORT_REST_TRIGGER);
    }
    private static void longRestTrigger(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.LONG_REST_TRIGGER);
    }

    /** Returns the category for the given ability. Defaults to PASSIVE_TRACKED. */
    public static AbilityCategory getCategory(Ability ability) {
        return REGISTRY.getOrDefault(ability, AbilityCategory.PASSIVE_TRACKED);
    }
}
