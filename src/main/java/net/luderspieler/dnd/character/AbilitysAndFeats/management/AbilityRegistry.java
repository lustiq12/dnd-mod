package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.*;

/**
 * Maps each class/species to the abilities they gain and at which level.
 * All entries reflect the 2024 Player's Handbook feature progression.
 * ASI/Feat choices are NOT listed here — they are handled by ChoiceUpdateSystem.
 */
public class AbilityRegistry {

    // ==================== CLASS ABILITIES ====================

    public static final Map<Integer, List<Ability>> BARBARIAN = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.RAGE, Ability.UNARMORED_DEFENSE, Ability.WEAPON_MASTERY));
        put(2,  Arrays.asList(Ability.DANGER_SENSE, Ability.RECKLESS_ATTACK));
        put(3,  Arrays.asList(Ability.PRIMAL_KNOWLEDGE));
        put(5,  Arrays.asList(Ability.EXTRA_ATTACK_BARBARIAN, Ability.FAST_MOVEMENT));
        put(7,  Arrays.asList(Ability.FERAL_INSTINCT, Ability.INSTINCTIVE_POUNCE));
        put(9,  Arrays.asList(Ability.BRUTAL_STRIKE));
        put(11, Arrays.asList(Ability.RELENTLESS_RAGE));
        put(13, Arrays.asList(Ability.IMPROVED_BRUTAL_STRIKE_ONE));
        put(15, Arrays.asList(Ability.PERSISTENT_RAGE));
        put(17, Arrays.asList(Ability.IMPROVED_BRUTAL_STRIKE_TWO));
        put(18, Arrays.asList(Ability.INDOMITABLE_MIGHT));
        put(20, Arrays.asList(Ability.PRIMAL_CHAMPION));
    }};

    public static final Map<Integer, List<Ability>> BARD = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_BARD, Ability.BARDIC_INSPIRATION));
        put(2,  Arrays.asList(Ability.JACK_OF_ALL_TRADES, Ability.EXPERTISE));
        put(5,  Arrays.asList(Ability.FONT_OF_INSPIRATION));
        put(7,  Arrays.asList(Ability.COUNTERCHARM));
        put(9,  Arrays.asList(Ability.IMPROVED_EXPERTISE_ONE));
        put(10, Arrays.asList(Ability.MAGICAL_SECRETS));
        put(15, Arrays.asList(Ability.PEERLESS_SKILL));
        put(18, Arrays.asList(Ability.SUPERIOR_INSPIRATION));
        put(20, Arrays.asList(Ability.WORDS_OF_CREATION));
    }};

    public static final Map<Integer, List<Ability>> CLERIC = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_CLERIC, Ability.DIVINE_ORDER));
        put(2,  Arrays.asList(Ability.CHANNEL_DIVINITY));
        put(5,  Arrays.asList(Ability.SMITE_UNDEAD));
        put(7,  Arrays.asList(Ability.BLESSED_STRIKES));
        put(10, Arrays.asList(Ability.DIVINE_INTERVENTION));
        put(14, Arrays.asList(Ability.IMPROVED_BLESSED_STRIKES_ONE));
        put(20, Arrays.asList(Ability.IMPROVED_DIVINE_INTERVENTION_ONE));
    }};

    public static final Map<Integer, List<Ability>> DRUID = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_DRUID, Ability.DRUIDIC, Ability.PRIMAL_ORDER));
        put(2,  Arrays.asList(Ability.WILD_SHAPE, Ability.WILD_COMPANION));
        put(5,  Arrays.asList(Ability.WILD_RESURGENCE));
        put(7,  Arrays.asList(Ability.ELEMENTAL_FURY));
        put(15, Arrays.asList(Ability.IMPROVED_ELEMENTAL_FURY_ONE));
        put(18, Arrays.asList(Ability.BEAST_SPELLS));
        put(20, Arrays.asList(Ability.ARCHDRUID));
    }};

    public static final Map<Integer, List<Ability>> FIGHTER = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.FIGHTING_STYLE, Ability.SECOND_WIND, Ability.WEAPON_MASTERY));
        put(2,  Arrays.asList(Ability.ACTION_SURGE, Ability.TACTICAL_MIND));
        put(5,  Arrays.asList(Ability.EXTRA_ATTACK_FIGHTER, Ability.TACTICAL_SHIFT));
        put(9,  Arrays.asList(Ability.INDOMITABLE, Ability.TACTICAL_MASTER));
        put(11, Arrays.asList(Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_ONE));
        put(13, Arrays.asList(Ability.STUDIED_ATTACKS, Ability.IMPROVED_INDOMITABLE_ONE));
        put(17, Arrays.asList(Ability.IMPROVED_ACTION_SURGE_ONE, Ability.IMPROVED_INDOMITABLE_TWO));
        put(20, Arrays.asList(Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_TWO));
    }};

    public static final Map<Integer, List<Ability>> MONK = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.MARTIAL_ARTS, Ability.UNARMORED_DEFENSE));
        put(2,  Arrays.asList(Ability.FOCUS_POINTS, Ability.UNARMORED_MOVEMENT, Ability.UNCANNY_METABOLISM));
        put(3,  Arrays.asList(Ability.DEFLECT_ATTACKS));
        put(4,  Arrays.asList(Ability.SLOW_FALL));
        put(5,  Arrays.asList(Ability.EXTRA_ATTACK_MONK, Ability.STUNNING_STRIKE));
        put(6,  Arrays.asList(Ability.EMPOWERED_STRIKES));
        put(7,  Arrays.asList(Ability.EVASION));
        put(9,  Arrays.asList(Ability.ACROBATIC_MOVEMENT));
        put(10, Arrays.asList(Ability.HEIGHTENED_FOCUS, Ability.SELF_RESTORATION));
        put(13, Arrays.asList(Ability.DEFLECT_ENERGY));
        put(14, Arrays.asList(Ability.DISCIPLINED_SURVIVOR));
        put(15, Arrays.asList(Ability.PERFECT_FOCUS));
        put(18, Arrays.asList(Ability.SUPERIOR_DEFENSE));
        put(20, Arrays.asList(Ability.BODY_AND_MIND));
    }};

    public static final Map<Integer, List<Ability>> PALADIN = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.LAY_ON_HANDS, Ability.SPELLCASTING_PALADIN, Ability.WEAPON_MASTERY));
        put(2,  Arrays.asList(Ability.FIGHTING_STYLE_PALADIN, Ability.PALADINS_SMITE));
        put(3,  Arrays.asList(Ability.CHANNEL_DIVINITY_PALADIN));
        put(5,  Arrays.asList(Ability.EXTRA_ATTACK_PALADIN, Ability.FAITHFUL_STEED));
        put(6,  Arrays.asList(Ability.AURA_OF_PROTECTION));
        put(9,  Arrays.asList(Ability.ABJURE_FOES));
        put(10, Arrays.asList(Ability.AURA_OF_COURAGE));
        put(11, Arrays.asList(Ability.RADIANT_STRIKES));
        put(14, Arrays.asList(Ability.RESTORING_TOUCH));
        put(18, Arrays.asList(Ability.AURA_EXPANSION));
    }};

    public static final Map<Integer, List<Ability>> RANGER = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_RANGER, Ability.FAVORED_ENEMY, Ability.WEAPON_MASTERY));
        put(2,  Arrays.asList(Ability.FIGHTING_STYLE_RANGER, Ability.DEFT_EXPLORER));
        put(5,  Arrays.asList(Ability.EXTRA_ATTACK_RANGER));
        put(6,  Arrays.asList(Ability.ROVING));
        put(9,  Arrays.asList(Ability.IMPROVED_DEFT_EXPLORER_ONE));
        put(10, Arrays.asList(Ability.TIRELESS));
        put(13, Arrays.asList(Ability.RELENTLESS_HUNTER));
        put(14, Arrays.asList(Ability.NATURES_VEIL));
        put(17, Arrays.asList(Ability.PRECISE_HUNTER));
        put(18, Arrays.asList(Ability.FERAL_SENSES));
        put(20, Arrays.asList(Ability.FOE_SLAYER));
    }};

    public static final Map<Integer, List<Ability>> ROGUE = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.EXPERTISE_ROGUE, Ability.SNEAK_ATTACK, Ability.THIEVES_CANT, Ability.WEAPON_MASTERY));
        put(2,  Arrays.asList(Ability.CUNNING_ACTION));
        put(3,  Arrays.asList(Ability.STEADY_AIM));
        put(5,  Arrays.asList(Ability.CUNNING_STRIKE, Ability.UNCANNY_DODGE));
        put(6,  Arrays.asList(Ability.IMPROVED_EXPERTISE_ROGUE_ONE));
        put(7,  Arrays.asList(Ability.EVASION, Ability.RELIABLE_TALENT));
        put(11, Arrays.asList(Ability.IMPROVED_CUNNING_STRIKE_ONE));
        put(14, Arrays.asList(Ability.DEVIOUS_STRIKES));
        put(15, Arrays.asList(Ability.SLIPPERY_MIND));
        put(18, Arrays.asList(Ability.ELUSIVE));
        put(20, Arrays.asList(Ability.STROKE_OF_LUCK));
    }};

    public static final Map<Integer, List<Ability>> SORCERER = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_SORCERER, Ability.INNATE_SORCERY, Ability.SORCEROUS_BURST));
        put(2,  Arrays.asList(Ability.FONT_OF_MAGIC, Ability.METAMAGIC));
        put(5,  Arrays.asList(Ability.SORCEROUS_RESTORATION));
        put(7,  Arrays.asList(Ability.SORCEROUS_INCARNATION));
        put(20, Arrays.asList(Ability.ARCANE_APOTHEOSIS));
    }};

    public static final Map<Integer, List<Ability>> WARLOCK = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.PACT_MAGIC, Ability.ELDRITCH_INVOCATIONS));
        put(2,  Arrays.asList(Ability.MAGICAL_CUNNING));
        put(9,  Arrays.asList(Ability.CONTACT_PATRON));
        put(11, Arrays.asList(Ability.MYSTIC_ARCANUM));
        put(13, Arrays.asList(Ability.IMPROVED_MYSTIC_ARCANUM_ONE));
        put(15, Arrays.asList(Ability.IMPROVED_MYSTIC_ARCANUM_TWO));
        put(17, Arrays.asList(Ability.IMPROVED_MYSTIC_ARCANUM_THREE));
        put(20, Arrays.asList(Ability.ELDRITCH_MASTER));
    }};

    public static final Map<Integer, List<Ability>> WIZARD = new LinkedHashMap<>() {{
        put(1,  Arrays.asList(Ability.SPELLCASTING_WIZARD, Ability.ARCANE_RECOVERY, Ability.RITUAL_ADEPT));
        put(2,  Arrays.asList(Ability.SCHOLAR));
        put(5,  Arrays.asList(Ability.MEMORIZE_SPELLS));
        put(18, Arrays.asList(Ability.SPELL_MASTERY));
        put(20, Arrays.asList(Ability.SIGNATURE_SPELLS));
    }};

    // ==================== SPECIES ABILITIES ====================

    public static final List<Ability> HUMAN = Arrays.asList(
            Ability.RESOURCEFUL, Ability.VERSATILE
    );

    public static final List<Ability> DWARF = Arrays.asList(
            Ability.DARKVISION_60, Ability.DWARVEN_RESILIENCE,
            Ability.DWARVEN_TOUGHNESS, Ability.STONE_CUNNING
    );

    public static final List<Ability> ELF = Arrays.asList(
            Ability.DARKVISION_60, Ability.FEY_ANCESTRY,
            Ability.KEEN_SENSES, Ability.TRANCE, Ability.ELVEN_LINEAGE
    );

    public static final List<Ability> HALFLING = Arrays.asList(
            Ability.BRAVE, Ability.HALFLING_NIMBLENESS,
            Ability.LUCKY, Ability.NATURALLY_STEALTHY
    );

    public static final List<Ability> DRAGONBORN = Arrays.asList(
            Ability.DRACONIC_ANCESTRY, Ability.BREATH_WEAPON,
            Ability.DAMAGE_RESISTANCE_DRAGONBORN, Ability.DARKVISION_60
    );

    public static final List<Ability> GNOME = Arrays.asList(
            Ability.DARKVISION_60, Ability.GNOME_CUNNING, Ability.GNOMISH_LINEAGE
    );

    /** Base Aasimar abilities. Celestial Revelation is a level-3 choice added by ChoiceUpdateSystem. */
    public static final List<Ability> AASIMAR = Arrays.asList(
            Ability.CELESTIAL_RESISTANCE, Ability.DARKVISION_60,
            Ability.HEALING_HANDS, Ability.LIGHT_BEARER
    );

    public static final List<Ability> TIEFLING = Arrays.asList(
            Ability.DARKVISION_60, Ability.OTHERWORLDLY_GIFT, Ability.FIENDISH_RESISTANCE
    );

    public static final List<Ability> GOLIATH = Arrays.asList(
            Ability.GIANT_ANCESTRY, Ability.LARGE_FORM,
            Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH
    );

    public static final List<Ability> ORC = Arrays.asList(
            Ability.ADRENALINE_RUSH, Ability.DARKVISION_120,
            Ability.RELENTLESS_ENDURANCE, Ability.POWERFUL_BUILD_ORC
    );

    // ==================== LINEAGE / SUBRACE ABILITIES ====================

    public static final Map<String, List<Ability>> ELF_SUBRACES = new HashMap<>() {{
        put("high_elf",  Arrays.asList(Ability.DARKVISION_60, Ability.FEY_ANCESTRY, Ability.KEEN_SENSES, Ability.TRANCE, Ability.HIGH_ELF_LINEAGE));
        put("wood_elf",  Arrays.asList(Ability.DARKVISION_60, Ability.FEY_ANCESTRY, Ability.KEEN_SENSES, Ability.TRANCE, Ability.WOOD_ELF_LINEAGE, Ability.SPEED_WOOD_ELF));
        put("drow",      Arrays.asList(Ability.DARKVISION_120, Ability.FEY_ANCESTRY, Ability.KEEN_SENSES, Ability.TRANCE, Ability.DROW_LINEAGE));
    }};

    public static final Map<String, List<Ability>> GOLIATH_SUBRACES = new HashMap<>() {{
        put("cloud_giant", Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.CLOUDS_JAUNT));
        put("fire_giant",  Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.FIRES_BURN));
        put("frost_giant", Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.FROSTS_CHILL));
        put("hill_giant",  Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.HILLS_TUMBLE));
        put("stone_giant", Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.STONES_ENDURANCE));
        put("storm_giant", Arrays.asList(Ability.GIANT_ANCESTRY, Ability.LARGE_FORM, Ability.POWERFUL_BUILD, Ability.SPEED_GOLIATH, Ability.STORMS_THUNDER));
    }};

    public static final Map<String, List<Ability>> TIEFLING_SUBRACES = new HashMap<>() {{
        put("abyssal_tiefling",  Arrays.asList(Ability.DARKVISION_60, Ability.OTHERWORLDLY_GIFT, Ability.FIENDISH_RESISTANCE, Ability.ABYSSAL_LINEAGE));
        put("chthonic_tiefling", Arrays.asList(Ability.DARKVISION_60, Ability.OTHERWORLDLY_GIFT, Ability.FIENDISH_RESISTANCE, Ability.CHTHONIC_LINEAGE));
        put("infernal_tiefling", Arrays.asList(Ability.DARKVISION_60, Ability.OTHERWORLDLY_GIFT, Ability.FIENDISH_RESISTANCE, Ability.INFERNAL_LINEAGE));
    }};

    public static final Map<String, List<Ability>> GNOME_SUBRACES = new HashMap<>() {{
        put("forest_gnome", Arrays.asList(Ability.DARKVISION_60, Ability.GNOME_CUNNING, Ability.GNOMISH_LINEAGE, Ability.FOREST_GNOME_LINEAGE));
        put("rock_gnome",   Arrays.asList(Ability.DARKVISION_60, Ability.GNOME_CUNNING, Ability.GNOMISH_LINEAGE, Ability.ROCK_GNOME_LINEAGE));
    }};

    /** Dragonborn ancestry differences are primarily data (damage type), not distinct Ability entries. */
    public static final Map<String, List<Ability>> DRAGONBORN_SUBRACES = new HashMap<>();
    /** No subraces in 2024 PHB. */
    public static final Map<String, List<Ability>> DWARF_SUBRACES    = new HashMap<>();
    public static final Map<String, List<Ability>> HALFLING_SUBRACES = new HashMap<>();
    /** Aasimar Celestial Revelation is a level-3 choice, not a creation-time subrace. */
    public static final Map<String, List<Ability>> AASIMAR_SUBRACES  = new HashMap<>();

    public static final Map<String, Map<Integer, List<Ability>>> RACE_LEVELED_ABILITIES =
            new java.util.HashMap<>() {{
                // Dragonborn: Flügel ab Level 5
                put("dragonborn", new LinkedHashMap<>() {{
                    put(5, Arrays.asList(Ability.FLIGHT));
                }});

                // Aasimar: Celestial Revelation ab Level 3 (war bisher als ChoiceUpdateSystem-TODO
                // markiert aber nie implementiert — jetzt korrekt level-gated)
                put("aasimar", new LinkedHashMap<>() {{
                    put(3, Arrays.asList(Ability.CELESTIAL_REVELATION));
                }});
            }};

    // ==================== GETTER METHODS ====================

    /** Returns the level-to-abilities map for the given class id. */
    public static Map<Integer, List<Ability>> getClassAbilities(String className) {
        return switch (className.toLowerCase()) {
            case "barbarian" -> BARBARIAN;
            case "bard"      -> BARD;
            case "cleric"    -> CLERIC;
            case "druid"     -> DRUID;
            case "fighter"   -> FIGHTER;
            case "monk"      -> MONK;
            case "paladin"   -> PALADIN;
            case "ranger"    -> RANGER;
            case "rogue"     -> ROGUE;
            case "sorcerer"  -> SORCERER;
            case "warlock"   -> WARLOCK;
            case "wizard"    -> WIZARD;
            default          -> new HashMap<>();
        };
    }

    /** Returns the base species abilities for the given race id. */
    public static List<Ability> getRaceAbilities(String raceName) {
        return switch (raceName.toLowerCase()) {
            case "human"     -> new ArrayList<>(HUMAN);
            case "dwarf"     -> new ArrayList<>(DWARF);
            case "elf"       -> new ArrayList<>(ELF);
            case "halfling"  -> new ArrayList<>(HALFLING);
            case "dragonborn"-> new ArrayList<>(DRAGONBORN);
            case "gnome"     -> new ArrayList<>(GNOME);
            case "aasimar"   -> new ArrayList<>(AASIMAR);
            case "tiefling"  -> new ArrayList<>(TIEFLING);
            case "goliath"   -> new ArrayList<>(GOLIATH);
            case "orc"       -> new ArrayList<>(ORC);
            default          -> new ArrayList<>();
        };
    }

    /** Returns the lineage/ancestry abilities for the given subrace id. */
    public static List<Ability> getSubraceAbilities(String raceName, String subraceName) {
        return switch (raceName.toLowerCase()) {
            case "elf"       -> new ArrayList<>(ELF_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "dwarf"     -> new ArrayList<>(DWARF_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "halfling"  -> new ArrayList<>(HALFLING_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "dragonborn"-> new ArrayList<>(DRAGONBORN_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "goliath"   -> new ArrayList<>(GOLIATH_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "tiefling"  -> new ArrayList<>(TIEFLING_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "aasimar"   -> new ArrayList<>(AASIMAR_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "gnome"     -> new ArrayList<>(GNOME_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            default          -> new ArrayList<>();
        };
    }
}