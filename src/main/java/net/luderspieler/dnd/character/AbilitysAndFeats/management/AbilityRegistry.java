package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.*;

/**
 * Registry die speichert welche Abilities zu welcher Klasse/Rasse gehören
 * und auf welchem Level sie verfügbar sind
 */
public class AbilityRegistry {

    // ==================== CLASS ABILITIES ====================

    public static final Map<Integer, List<Ability>> BARBARIAN = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.RAGE, Ability.RECKLESS_ATTACK));
        put(2, Arrays.asList(Ability.DANGER_SENSE));
        put(3, Arrays.asList(Ability.PRIMAL_KNOWLEDGE));
        put(5, Arrays.asList(Ability.EXTRA_ATTACK_BARBARIAN));
        put(7, Arrays.asList(Ability.FERAL_INSTINCT));
        put(9, Arrays.asList(Ability.BRUTAL_CRITICAL));
        put(11, Arrays.asList(Ability.RELENTLESS_RAGE));
        put(13, Arrays.asList(Ability.PRIMAL_REFLEXES));
        put(15, Arrays.asList(Ability.RELENTLESS));
        put(17, Arrays.asList(Ability.PRIMAL_CHAMPION));
    }};

    public static final Map<Integer, List<Ability>> BARD = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.BARDIC_INSPIRATION, Ability.SPELLCASTING_BARD));
        put(2, Arrays.asList(Ability.JACK_OF_ALL_TRADES));
        put(3, Arrays.asList(Ability.EXPERTISE));
        put(5, Arrays.asList(Ability.FONT_OF_INSPIRATION));
        put(6, Arrays.asList(Ability.COUNTERCHARM));
        put(8, Arrays.asList(Ability.MAGICAL_SECRETS_BARD));
        put(10, Arrays.asList(Ability.PEERLESS_SKILL));
        put(14, Arrays.asList(Ability.LEGENDARY_PERFORMANCE));
        put(20, Arrays.asList(Ability.SUPERIOR_INSPIRATION));
    }};

    public static final Map<Integer, List<Ability>> CLERIC = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.SPELLCASTING_CLERIC, Ability.CHANNEL_DIVINITY));
        put(2, Arrays.asList(Ability.HEALING_WORD_IMPROVEMENT));
        put(3, Arrays.asList(Ability.DESTROY_UNDEAD));
        put(5, Arrays.asList(Ability.POTENT_SPELLCASTING));
        put(6, Arrays.asList(Ability.IMPROVED_CHANNEL_DIVINITY));
        put(8, Arrays.asList(Ability.DIVINE_STRIKE));
        put(10, Arrays.asList(Ability.SUPREME_HEALING));
        put(17, Arrays.asList(Ability.DIVINE_INTERVENTION));
    }};

    public static final Map<Integer, List<Ability>> DRUID = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.SPELLCASTING_DRUID, Ability.DRUIDIC));
        put(2, Arrays.asList(Ability.WILD_SHAPE));
        put(3, Arrays.asList(Ability.WILD_COMPANION));
        put(4, Arrays.asList(Ability.WILD_FIRE));
        put(6, Arrays.asList(Ability.IMPROVED_WILD_SHAPE));
        put(8, Arrays.asList(Ability.BEAST_SPELLS));
        put(9, Arrays.asList(Ability.THOUSAND_FORMS));
        put(18, Arrays.asList(Ability.UNLIMITED_WILD_SHAPE));
    }};

    public static final Map<Integer, List<Ability>> FIGHTER = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.FIGHTING_STYLE, Ability.SECOND_WIND));
        put(2, Arrays.asList(Ability.ACTION_SURGE));
        put(3, Arrays.asList(Ability.MARTIAL_ARCHETYPE));
        put(5, Arrays.asList(Ability.EXTRA_ATTACK_FIGHTER));
        put(6, Arrays.asList(Ability.ABILITY_SCORE_IMPROVEMENT));
        put(9, Arrays.asList(Ability.INDOMITABLE));
        put(11, Arrays.asList(Ability.EXTRA_ATTACK_FIGHTER_2));
        put(13, Arrays.asList(Ability.SUDDEN_STRIKE));
        put(15, Arrays.asList(Ability.SUPERIOR_CRITICAL));
        put(17, Arrays.asList(Ability.ACTION_SURGE_MASTERY));
        put(20, Arrays.asList(Ability.EXTRA_ATTACK_FIGHTER_3));
    }};

    public static final Map<Integer, List<Ability>> MONK = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.MARTIAL_ARTS, Ability.UNARMORED_DEFENSE));
        put(2, Arrays.asList(Ability.KI, Ability.UNARMORED_MOVEMENT));
        put(3, Arrays.asList(Ability.MONASTIC_TRADITION));
        put(4, Arrays.asList(Ability.SLOW_FALL));
        put(5, Arrays.asList(Ability.EXTRA_ATTACK_MONK, Ability.STUNNING_STRIKE));
        put(6, Arrays.asList(Ability.KI_FUELED_ATTACK));
        put(7, Arrays.asList(Ability.EVASION));
        put(10, Arrays.asList(Ability.PURITY_OF_BODY));
        put(13, Arrays.asList(Ability.TONGUE_OF_THE_SUN_AND_MOON));
        put(14, Arrays.asList(Ability.DIAMOND_SOUL));
        put(15, Arrays.asList(Ability.TIMELESS_BODY));
        put(18, Arrays.asList(Ability.EMPTY_BODY));
        put(20, Arrays.asList(Ability.PERFECT_SELF));
    }};

    public static final Map<Integer, List<Ability>> PALADIN = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.LAY_ON_HANDS, Ability.SPELLCASTING_PALADIN));
        put(2, Arrays.asList(Ability.FIGHTING_STYLE_PALADIN, Ability.CHANNEL_DIVINITY_PALADIN));
        put(3, Arrays.asList(Ability.DIVINE_SMITE));
        put(6, Arrays.asList(Ability.AURA_OF_PROTECTION));
        put(7, Arrays.asList(Ability.BLESSED_WARRIOR));
        put(10, Arrays.asList(Ability.AURA_OF_COURAGE));
        put(11, Arrays.asList(Ability.IMPROVED_SMITE));
        put(14, Arrays.asList(Ability.CLEANSING_TOUCH));
        put(18, Arrays.asList(Ability.AURA_IMPROVEMENTS));
    }};

    public static final Map<Integer, List<Ability>> RANGER = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.DRAKEWARDEN, Ability.SPELLCASTING_RANGER));
        put(2, Arrays.asList(Ability.FIGHTING_STYLE_RANGER, Ability.RANGERS_QUARRY));
        put(3, Arrays.asList(Ability.PRIMAL_ORDER));
        put(5, Arrays.asList(Ability.EXTRA_ATTACK_RANGER));
        put(6, Arrays.asList(Ability.ROVING));
        put(7, Arrays.asList(Ability.PRIMAL_AWARENESS));
        put(9, Arrays.asList(Ability.RANGERS_INTUITION));
        put(11, Arrays.asList(Ability.FLEET_OF_FOOT));
        put(15, Arrays.asList(Ability.FERAL_SENSES));
        put(18, Arrays.asList(Ability.FOE_SLAYER));
    }};

    public static final Map<Integer, List<Ability>> ROGUE = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.EXPERTISE_ROGUE, Ability.SNEAK_ATTACK));
        put(2, Arrays.asList(Ability.CUNNING_ACTION));
        put(3, Arrays.asList(Ability.ROGUISH_ARCHETYPE));
        put(4, Arrays.asList(Ability.ABILITY_SCORE_IMPROVEMENT_ROGUE));
        put(5, Arrays.asList(Ability.UNCANNY_DODGE));
        put(6, Arrays.asList(Ability.EXPERTISE_EXPANSION));
        put(9, Arrays.asList(Ability.REMARKABLE_DODGE));
        put(11, Arrays.asList(Ability.RELIABLE_TALENT));
        put(14, Arrays.asList(Ability.BLINDSENSE));
        put(15, Arrays.asList(Ability.SLIPPERY_MIND));
        put(17, Arrays.asList(Ability.ELUSIVE));
        put(20, Arrays.asList(Ability.STROKE_OF_LUCK));
    }};

    public static final Map<Integer, List<Ability>> SORCERER = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.SPELLCASTING_SORCERER, Ability.SORCEROUS_RESILIENCE));
        put(2, Arrays.asList(Ability.FONT_OF_MAGIC));
        put(3, Arrays.asList(Ability.METAMAGIC));
        put(4, Arrays.asList(Ability.ABILITY_SCORE_IMPROVEMENT_SORCERER));
        put(5, Arrays.asList(Ability.MAGICAL_GUIDANCE));
        put(6, Arrays.asList(Ability.SORCEROUS_VERSATILITY));
        put(11, Arrays.asList(Ability.MAGICAL_AMPLIFICATION));
        put(14, Arrays.asList(Ability.MAGICAL_EPIPHANY));
        put(20, Arrays.asList(Ability.SORCEROUS_RESTORATION));
    }};

    public static final Map<Integer, List<Ability>> WARLOCK = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.PACT_MAGIC, Ability.OTHERWORLDLY_PATRON));
        put(2, Arrays.asList(Ability.ELDRITCH_INVOCATIONS));
        put(3, Arrays.asList(Ability.PACT_BOON));
        put(5, Arrays.asList(Ability.CONTACT_OTHER_PLANE));
        put(6, Arrays.asList(Ability.ELDRITCH_VERSATILITY));
        put(11, Arrays.asList(Ability.MYSTIC_ARCANUM));
        put(14, Arrays.asList(Ability.ELDRITCH_MASTER));
        put(20, Arrays.asList(Ability.ELDRITCH_MASTER_MASTERY));
    }};

    public static final Map<Integer, List<Ability>> WIZARD = new LinkedHashMap<Integer, List<Ability>>() {{
        put(1, Arrays.asList(Ability.SPELLCASTING_WIZARD, Ability.ARCANE_RECOVERY));
        put(2, Arrays.asList(Ability.ARCANE_TRADITION));
        put(4, Arrays.asList(Ability.ABILITY_SCORE_IMPROVEMENT_WIZARD));
        put(10, Arrays.asList(Ability.SPELL_MASTERY));
        put(18, Arrays.asList(Ability.SIGNATURE_SPELLS));
    }};

    // ==================== RACE ABILITIES ====================

    public static final List<Ability> HUMAN = Arrays.asList(Ability.EXTRA_ABILITY_INCREASE_HUMAN);

    public static final List<Ability> DWARF = Arrays.asList(
            Ability.DWARVEN_RESILIENCE,
            Ability.DWARVEN_COMBAT_TRAINING,
            Ability.TOOL_PROFICIENCY_DWARF
    );

    public static final List<Ability> ELF = Arrays.asList(
            Ability.KEEN_SENSES_ELF,
            Ability.FEY_ANCESTRY,
            Ability.TRANCE
    );

    public static final List<Ability> HALFLING = Arrays.asList(
            Ability.NATURALLY_STEALTHY,
            Ability.FORTUNATE
    );

    public static final List<Ability> DRAGONBORN = Arrays.asList(
            Ability.DRACONIC_RESILIENCE,
            Ability.DRACONIC_ANCESTRY
    );

    public static final List<Ability> GNOME = Arrays.asList(Ability.GNOME_CUNNING);

    public static final List<Ability> AASIMAR = Arrays.asList(
            Ability.CELESTIAL_RESISTANCE,
            Ability.HEALING_HANDS
    );

    public static final List<Ability> TIEFLING = Arrays.asList(
            Ability.INFERNAL_RESILIENCE,
            Ability.HELLISH_RESISTANCE
    );

    public static final List<Ability> GOLIATH = Arrays.asList(
            Ability.STONE_ENDURANCE,
            Ability.POWERFUL_BUILD
    );

    public static final List<Ability> ORC = Arrays.asList(
            Ability.ORC_POWERFUL_BUILD,
            Ability.PRIMAL_INTUITION
    );

    // ==================== SUBRACE ABILITIES ====================

    public static final Map<String, List<Ability>> ELF_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("high_elf", Arrays.asList(Ability.KEEN_SENSES_ELF, Ability.FEY_ANCESTRY, Ability.TRANCE, Ability.EXTRA_CANTRIP_HIGH_ELF));
        put("wood_elf", Arrays.asList(Ability.KEEN_SENSES_ELF, Ability.FEY_ANCESTRY, Ability.TRANCE, Ability.MASK_OF_THE_WILD));
        put("drow", Arrays.asList(Ability.KEEN_SENSES_ELF, Ability.FEY_ANCESTRY, Ability.TRANCE, Ability.SUNLIGHT_SENSITIVITY, Ability.DROW_SPELLCASTING));
    }};

    public static final Map<String, List<Ability>> DWARF_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("hill_dwarf", Arrays.asList(Ability.DWARVEN_RESILIENCE, Ability.DWARVEN_COMBAT_TRAINING, Ability.TOOL_PROFICIENCY_DWARF, Ability.HILL_DWARF_ABILITY_INCREASE));
        put("mountain_dwarf", Arrays.asList(Ability.DWARVEN_RESILIENCE, Ability.DWARVEN_COMBAT_TRAINING, Ability.TOOL_PROFICIENCY_DWARF, Ability.ARMOR_TRAINING_MOUNTAIN_DWARF));
    }};

    public static final Map<String, List<Ability>> HALFLING_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("lightfoot", Arrays.asList(Ability.NATURALLY_STEALTHY, Ability.FORTUNATE));
        put("stout_halfling", Arrays.asList(Ability.NATURALLY_STEALTHY, Ability.FORTUNATE, Ability.STOUT_RESILIENCE));
    }};

    public static final Map<String, List<Ability>> DRAGONBORN_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("chromatic_dragonborn", Arrays.asList(Ability.DRACONIC_RESILIENCE, Ability.DRACONIC_ANCESTRY, Ability.CHROMATIC_BREATH_WEAPON, Ability.CHROMATIC_DAMAGE_RESISTANCE));
        put("metallic_dragonborn", Arrays.asList(Ability.DRACONIC_RESILIENCE, Ability.DRACONIC_ANCESTRY, Ability.METALLIC_BREATH_WEAPON, Ability.METALLIC_DAMAGE_RESISTANCE));
        put("gem_dragonborn", Arrays.asList(Ability.DRACONIC_RESILIENCE, Ability.DRACONIC_ANCESTRY, Ability.GEM_BREATH_WEAPON, Ability.GEM_DAMAGE_RESISTANCE));
    }};

    public static final Map<String, List<Ability>> GOLIATH_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("cloud_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.CLOUD_GIANT_ANCESTRY));
        put("fire_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.FIRE_GIANT_ANCESTRY));
        put("frost_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.FROST_GIANT_ANCESTRY));
        put("hill_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.HILL_GIANT_ANCESTRY));
        put("stone_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.STONE_GIANT_ANCESTRY));
        put("storm_giant", Arrays.asList(Ability.STONE_ENDURANCE, Ability.POWERFUL_BUILD, Ability.STORM_GIANT_ANCESTRY));
    }};

    public static final Map<String, List<Ability>> TIEFLING_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("abyssal_tiefling", Arrays.asList(Ability.INFERNAL_RESILIENCE, Ability.HELLISH_RESISTANCE, Ability.ABYSSAL_TIEFLING_SPELLS));
        put("chthonic_tiefling", Arrays.asList(Ability.INFERNAL_RESILIENCE, Ability.HELLISH_RESISTANCE, Ability.CHTHONIC_TIEFLING_SPELLS));
        put("infernal_tiefling", Arrays.asList(Ability.INFERNAL_RESILIENCE, Ability.HELLISH_RESISTANCE, Ability.INFERNAL_TIEFLING_SPELLS));
    }};

    public static final Map<String, List<Ability>> AASIMAR_SUBRACES = new HashMap<String, List<Ability>>() {{
        put("celestial_revelation", Arrays.asList(Ability.CELESTIAL_RESISTANCE, Ability.HEALING_HANDS, Ability.CELESTIAL_REVELATION_SPELLS));
    }};

    // ==================== GETTER METHODS ====================

    /**
     * Gibt die Ability-Map für eine Klasse zurück
     */
    public static Map<Integer, List<Ability>> getClassAbilities(String className) {
        return switch (className.toLowerCase()) {
            case "barbarian" -> BARBARIAN;
            case "bard" -> BARD;
            case "cleric" -> CLERIC;
            case "druid" -> DRUID;
            case "fighter" -> FIGHTER;
            case "monk" -> MONK;
            case "paladin" -> PALADIN;
            case "ranger" -> RANGER;
            case "rogue" -> ROGUE;
            case "sorcerer" -> SORCERER;
            case "warlock" -> WARLOCK;
            case "wizard" -> WIZARD;
            default -> new HashMap<>();
        };
    }

    /**
     * Gibt die Abilities für eine Rasse zurück
     */
    public static List<Ability> getRaceAbilities(String raceName) {
        return switch (raceName.toLowerCase()) {
            case "human" -> new ArrayList<>(HUMAN);
            case "dwarf" -> new ArrayList<>(DWARF);
            case "elf" -> new ArrayList<>(ELF);
            case "halfling" -> new ArrayList<>(HALFLING);
            case "dragonborn" -> new ArrayList<>(DRAGONBORN);
            case "gnome" -> new ArrayList<>(GNOME);
            case "aasimar" -> new ArrayList<>(AASIMAR);
            case "tiefling" -> new ArrayList<>(TIEFLING);
            case "goliath" -> new ArrayList<>(GOLIATH);
            case "orc" -> new ArrayList<>(ORC);
            default -> new ArrayList<>();
        };
    }

    /**
     * Gibt die Abilities für eine Subrace zurück
     */
    public static List<Ability> getSubraceAbilities(String raceName, String subraceName) {
        return switch (raceName.toLowerCase()) {
            case "elf" -> new ArrayList<>(ELF_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "dwarf" -> new ArrayList<>(DWARF_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "halfling" -> new ArrayList<>(HALFLING_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "dragonborn" -> new ArrayList<>(DRAGONBORN_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "goliath" -> new ArrayList<>(GOLIATH_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "tiefling" -> new ArrayList<>(TIEFLING_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            case "aasimar" -> new ArrayList<>(AASIMAR_SUBRACES.getOrDefault(subraceName, new ArrayList<>()));
            default -> new ArrayList<>();
        };
    }
}