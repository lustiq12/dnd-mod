package net.luderspieler.dnd.character.registrys;

import net.luderspieler.dnd.character.definition.SubclassDefinition;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.spells.Spells;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassRegistry {

    public static final List<ClassDefinition>    CLASSES    = new ArrayList<>();
    public static final List<SubclassDefinition> SUBCLASSES = new ArrayList<>();

    private static Map<String, Integer> attrs(int str, int dex, int con, int intel, int wis, int cha) {
        Map<String, Integer> m = new java.util.LinkedHashMap<>();
        m.put("Strength", str);
        m.put("Dexterity", dex);
        m.put("Constitution", con);
        m.put("Intelligence", intel);
        m.put("Wisdom", wis);
        m.put("Charisma", cha);
        return m;
    }

    // Health Definitions (Hit Dice)
    public static final int CLASS_HEALTH_BARBARIAN = 12;
    public static final int CLASS_HEALTH_FIGHTER = 10;
    public static final int CLASS_HEALTH_PALADIN = 10;
    public static final int CLASS_HEALTH_RANGER = 10;
    public static final int CLASS_HEALTH_BARD = 8;
    public static final int CLASS_HEALTH_CLERIC = 8;
    public static final int CLASS_HEALTH_DRUID = 8;
    public static final int CLASS_HEALTH_MONK = 8;
    public static final int CLASS_HEALTH_ROGUE = 8;
    public static final int CLASS_HEALTH_WARLOCK = 8;
    public static final int CLASS_HEALTH_SORCERER = 6;
    public static final int CLASS_HEALTH_WIZARD = 6;

    // --- Can use Magic ---
    public static final boolean BARB_CAN_USE_MAGIC = false;
    public static final boolean BARD_CAN_USE_MAGIC = true;
    public static final boolean CLER_CAN_USE_MAGIC = true;
    public static final boolean DRUI_CAN_USE_MAGIC = true;
    public static final boolean FIGH_CAN_USE_MAGIC = false; // (Subklassen wie Eldritch Knight separat)
    public static final boolean MONK_CAN_USE_MAGIC = false; // (Nutzt Ki, keine klassischen Spells)
    public static final boolean PALA_CAN_USE_MAGIC = true;
    public static final boolean RANG_CAN_USE_MAGIC = true;
    public static final boolean ROGU_CAN_USE_MAGIC = false;
    public static final boolean SORC_CAN_USE_MAGIC = true;
    public static final boolean WARL_CAN_USE_MAGIC = true;
    public static final boolean WIZ_CAN_USE_MAGIC = true;

    // Spalten-Index:  0  1  2  3  4  5  6  7  8
    // Zauber-Grad:    1  2  3  4  5  6  7  8  9

    private static final int[][] WIZ_S = {
            {0,0,0,0,0,0,0,0,0}, {2,0,0,0,0,0,0,0,0}, {3,0,0,0,0,0,0,0,0}, {4,2,0,0,0,0,0,0,0}, {4,3,0,0,0,0,0,0,0},
            {4,3,2,0,0,0,0,0,0}, {4,3,3,0,0,0,0,0,0}, {4,3,3,1,0,0,0,0,0}, {4,3,3,2,0,0,0,0,0}, {4,3,3,3,1,0,0,0,0},
            {4,3,3,3,2,0,0,0,0}, {4,3,3,3,2,1,0,0,0}, {4,3,3,3,2,1,0,0,0}, {4,3,3,3,2,1,1,0,0}, {4,3,3,3,2,1,1,0,0},
            {4,3,3,3,2,1,1,1,0}, {4,3,3,3,2,1,1,1,0}, {4,3,3,3,2,1,1,1,1}, {4,3,3,3,3,1,1,1,1}, {4,3,3,3,3,2,1,1,1},
            {4,3,3,3,3,2,2,1,1}
    };

    // Cleric, Druid, Bard und Sorcerer nutzen dieselbe Progression wie Wizard
    private static final int[][] CLE_S = WIZ_S;
    private static final int[][] DRU_S = WIZ_S;
    private static final int[][] BAR_S = WIZ_S;
    private static final int[][] SOR_S = WIZ_S;

    private static final int[][] PAL_S = {
            {0,0,0,0,0,0,0,0,0}, {0,0,0,0,0,0,0,0,0}, {2,0,0,0,0,0,0,0,0}, {3,0,0,0,0,0,0,0,0}, {3,0,0,0,0,0,0,0,0},
            {4,2,0,0,0,0,0,0,0}, {4,2,0,0,0,0,0,0,0}, {4,3,0,0,0,0,0,0,0}, {4,3,0,0,0,0,0,0,0}, {4,3,2,0,0,0,0,0,0},
            {4,3,2,0,0,0,0,0,0}, {4,3,3,0,0,0,0,0,0}, {4,3,3,0,0,0,0,0,0}, {4,3,3,1,0,0,0,0,0}, {4,3,3,1,0,0,0,0,0},
            {4,3,3,2,0,0,0,0,0}, {4,3,3,2,0,0,0,0,0}, {4,3,3,3,1,0,0,0,0}, {4,3,3,3,1,0,0,0,0}, {4,3,3,3,2,0,0,0,0},
            {4,3,3,3,2,0,0,0,0}
    };
    private static final int[][] RAN_S = PAL_S;

    private static final int[][] WAR_S = {
            {0,0,0,0,0,0,0,0,0}, {1,0,0,0,0,0,0,0,0}, {2,0,0,0,0,0,0,0,0}, {0,2,0,0,0,0,0,0,0}, {0,2,0,0,0,0,0,0,0},
            {0,0,2,0,0,0,0,0,0}, {0,0,2,0,0,0,0,0,0}, {0,0,0,2,0,0,0,0,0}, {0,0,0,2,0,0,0,0,0}, {0,0,0,0,2,0,0,0,0},
            {0,0,0,0,2,0,0,0,0}, {0,0,0,0,3,1,0,0,0}, {0,0,0,0,3,1,0,0,0}, {0,0,0,0,3,1,1,0,0}, {0,0,0,0,3,1,1,0,0},
            {0,0,0,0,3,1,1,1,0}, {0,0,0,0,3,1,1,1,0}, {0,0,0,0,4,1,1,1,1}, {0,0,0,0,4,1,1,1,1}, {0,0,0,0,4,1,1,1,1},
            {0,0,0,0,4,1,1,1,1}
    };

    private static final int[][] NONE_S = new int[21][9];

    /// --- Hilfsvariable für Klassen ohne Magie ---
    private static final int[][] NONE_P = new int[21][10];

    // --- FULL CASTER (Wizard, Cleric, Druid, Bard, Sorcerer) ---

    // Format pro Zeile: {Cantrip, Lvl1, Lvl2, Lvl3, Lvl4, Lvl5, Lvl6, Lvl7, Lvl8, Lvl9}
    private static final int[][] FULL_CASTER_TABLE = {
            {0,0,0,0,0,0,0,0,0,0}, // Lvl 0
            {2,2,0,0,0,0,0,0,0,0}, {2,3,0,0,0,0,0,0,0,0}, {2,4,2,0,0,0,0,0,0,0}, {3,5,3,0,0,0,0,0,0,0}, // 1-4
            {3,6,3,2,0,0,0,0,0,0}, {3,7,3,3,0,0,0,0,0,0}, {3,8,3,3,1,0,0,0,0,0}, {3,9,3,3,2,0,0,0,0,0}, // 5-8
            {3,10,3,3,3,1,0,0,0,0}, {4,11,3,3,3,2,0,0,0,0}, {4,12,3,3,3,2,1,0,0,0}, {4,12,3,3,3,2,1,0,0,0}, // 9-12
            {4,13,3,3,3,2,1,1,0,0}, {4,13,3,3,3,2,1,1,0,0}, {4,14,3,3,3,2,1,1,1,0}, {4,14,3,3,3,2,1,1,1,0}, // 13-16
            {4,15,3,3,3,2,1,1,1,1}, {4,15,3,3,3,3,1,1,1,1}, {4,15,3,3,3,3,2,1,1,1}, {4,15,3,3,3,3,2,2,1,1}  // 17-20
    };

    // Zuweisung der Tabellen an deine Variablennamen
    private static final int[][] WIZ_MAX_SPELLS = FULL_CASTER_TABLE;
    private static final int[][] CLE_MAX_SPELLS = FULL_CASTER_TABLE;
    private static final int[][] DRU_MAX_SPELLS = FULL_CASTER_TABLE;
    private static final int[][] BAR_MAX_SPELLS = FULL_CASTER_TABLE;
    private static final int[][] SOR_MAX_SPELLS = FULL_CASTER_TABLE;

    // --- HALF CASTER (Paladin, Ranger) ---
    private static final int[][] HALF_CASTER_TABLE = {
            {0,0,0,0,0,0,0,0,0,0}, // Lvl 0
            {0,2,0,0,0,0,0,0,0,0}, {0,2,0,0,0,0,0,0,0,0}, {0,3,0,0,0,0,0,0,0,0}, {0,3,0,0,0,0,0,0,0,0}, // 1-4
            {0,4,2,0,0,0,0,0,0,0}, {0,4,2,0,0,0,0,0,0,0}, {0,4,3,0,0,0,0,0,0,0}, {0,4,3,0,0,0,0,0,0,0}, // 5-8
            {0,4,3,2,0,0,0,0,0,0}, {0,4,3,2,0,0,0,0,0,0}, {0,4,3,3,0,0,0,0,0,0}, {0,4,3,3,0,0,0,0,0,0}, // 9-12
            {0,4,3,3,1,0,0,0,0,0}, {0,4,3,3,1,0,0,0,0,0}, {0,4,3,3,2,0,0,0,0,0}, {0,4,3,3,2,0,0,0,0,0}, // 13-16
            {0,4,3,3,3,1,0,0,0,0}, {0,4,3,3,3,1,0,0,0,0}, {0,4,3,3,3,2,0,0,0,0}, {0,4,3,3,3,2,0,0,0,0}  // 17-20
    };

    private static final int[][] PAL_MAX_SPELLS = HALF_CASTER_TABLE;
    private static final int[][] RAN_MAX_SPELLS = HALF_CASTER_TABLE;

    // --- WARLOCK (Sonderregeln) ---
    private static final int[][] WAR_MAX_SPELLS = {
            {0,0,0,0,0,0,0,0,0,0},
            {2,2,0,0,0,0,0,0,0,0}, {2,3,0,0,0,0,0,0,0,0}, {2,0,4,0,0,0,0,0,0,0}, {3,0,5,0,0,0,0,0,0,0},
            {3,0,0,6,0,0,0,0,0,0}, {3,0,0,7,0,0,0,0,0,0}, {3,0,0,0,8,0,0,0,0,0}, {3,0,0,0,9,0,0,0,0,0},
            {3,0,0,0,0,10,0,0,0,0}, {4,0,0,0,0,10,0,0,0,0}, {4,0,0,0,0,11,1,0,0,0}, {4,0,0,0,0,11,1,0,0,0},
            {4,0,0,0,0,12,1,1,0,0}, {4,0,0,0,0,12,1,1,0,0}, {4,0,0,0,0,13,1,1,1,0}, {4,0,0,0,0,13,1,1,1,0},
            {4,0,0,0,0,14,1,1,1,1}, {4,0,0,0,0,14,1,1,1,1}, {4,0,0,0,0,15,1,1,1,1}, {4,0,0,0,0,15,1,1,1,1}
    };




    // ── SPELLS ──
    private static final List<Enum<?>> EMPTY_SPELLS = List.of();

    // Klassenlisten
    private static final List<Enum<?>> WIZARD_SPELLS = List.of(
            Spells.Cantrip.ACID_SPLASH, Spells.Cantrip.CHILL_TOUCH, Spells.Cantrip.DANCING_LIGHTS, Spells.Cantrip.ELEMENTALISM, Spells.Cantrip.FIRE_BOLT, Spells.Cantrip.LIGHT, Spells.Cantrip.MAGE_HAND, Spells.Cantrip.MENDING, Spells.Cantrip.MESSAGE, Spells.Cantrip.MINOR_ILLUSION, Spells.Cantrip.POISON_SPRAY, Spells.Cantrip.PRESTIDIGITATION, Spells.Cantrip.RAY_OF_FROST, Spells.Cantrip.SHOCKING_GRASP, Spells.Cantrip.TRUE_STRIKE,
            Spells.Grade1.ALARM, Spells.Grade1.BURNING_HANDS, Spells.Grade1.CHARM_PERSON, Spells.Grade1.CHROMATIC_ORB, Spells.Grade1.COLOR_SPRAY, Spells.Grade1.COMPREHEND_LANGUAGES, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DISGUISE_SELF, Spells.Grade1.EXPEDITIOUS_RETREAT, Spells.Grade1.FALSE_LIFE, Spells.Grade1.FEATHER_FALL, Spells.Grade1.FIND_FAMILIAR, Spells.Grade1.FLOATING_DISK, Spells.Grade1.FOG_CLOUD, Spells.Grade1.GREASE, Spells.Grade1.HIDEOUS_LAUGHTER, Spells.Grade1.ICE_KNIFE, Spells.Grade1.IDENTIFY, Spells.Grade1.ILLUSORY_SCRIPT, Spells.Grade1.JUMP, Spells.Grade1.LONGSTRIDER, Spells.Grade1.MAGE_ARMOR, Spells.Grade1.MAGIC_MISSILE, Spells.Grade1.PROTECTION_FROM_EVIL_AND_GOOD, Spells.Grade1.RAY_OF_SICKNESS, Spells.Grade1.SHIELD, Spells.Grade1.SILENT_IMAGE, Spells.Grade1.SLEEP, Spells.Grade1.THUNDERWAVE, Spells.Grade1.UNSEEN_SERVANT,
            Spells.Grade2.ACID_ARROW, Spells.Grade2.ALTER_SELF, Spells.Grade2.ARCANE_LOCK, Spells.Grade2.ARCANIST_S_MAGIC_AURA, Spells.Grade2.AUGURY, Spells.Grade2.BLUR, Spells.Grade2.CONTINUAL_FLAME, Spells.Grade2.DARKNESS, Spells.Grade2.DARKVISION, Spells.Grade2.DETECT_THOUGHTS, Spells.Grade2.DRAGON_S_BREATH, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.ENLARGE_REDUCE, Spells.Grade2.FLAMING_SPHERE, Spells.Grade2.GENTLE_REPOSE, Spells.Grade2.GUST_OF_WIND, Spells.Grade2.HOLD_PERSON, Spells.Grade2.INVISIBILITY, Spells.Grade2.KNOCK, Spells.Grade2.LEVITATE, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.MAGIC_MOUTH, Spells.Grade2.MAGIC_WEAPON, Spells.Grade2.MIND_SPIKE, Spells.Grade2.MIRROR_IMAGE, Spells.Grade2.MISTY_STEP, Spells.Grade2.PHANTASMAL_FORCE, Spells.Grade2.RAY_OF_ENFEEBLEMENT, Spells.Grade2.ROPE_TRICK, Spells.Grade2.SCORCHING_RAY, Spells.Grade2.SEE_INVISIBILITY, Spells.Grade2.SHATTER, Spells.Grade2.SPIDER_CLIMB, Spells.Grade2.SUGGESTION, Spells.Grade2.WEB,
            Spells.Grade3.ANIMATE_DEAD, Spells.Grade3.BESTOW_CURSE, Spells.Grade3.BLINK, Spells.Grade3.CLAIRVOYANCE, Spells.Grade3.COUNTERSPELL, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.FEAR, Spells.Grade3.FIREBALL, Spells.Grade3.FLY, Spells.Grade3.GASEOUS_FORM, Spells.Grade3.GLYPH_OF_WARDING, Spells.Grade3.HASTE, Spells.Grade3.HYPNOTIC_PATTERN, Spells.Grade3.LIGHTNING_BOLT, Spells.Grade3.MAGIC_CIRCLE, Spells.Grade3.MAJOR_IMAGE, Spells.Grade3.NONDETECTION, Spells.Grade3.PHANTOM_STEED, Spells.Grade3.PROTECTION_FROM_ENERGY, Spells.Grade3.REMOVE_CURSE, Spells.Grade3.SENDING, Spells.Grade3.SLEET_STORM, Spells.Grade3.SLOW, Spells.Grade3.SPEAK_WITH_DEAD, Spells.Grade3.STINKING_CLOUD, Spells.Grade3.TINY_HUT, Spells.Grade3.TONGUES, Spells.Grade3.VAMPIRIC_TOUCH, Spells.Grade3.WATER_BREATHING,
            Spells.Grade4.ARCANE_EYE, Spells.Grade4.BANISHMENT, Spells.Grade4.BLACK_TENTACLES, Spells.Grade4.BLIGHT, Spells.Grade4.CHARM_MONSTER, Spells.Grade4.CONFUSION, Spells.Grade4.CONJURE_MINOR_ELEMENTALS, Spells.Grade4.CONTROL_WATER, Spells.Grade4.DIMENSION_DOOR, Spells.Grade4.DIVINATION, Spells.Grade4.FABRICATE, Spells.Grade4.FAITHFUL_HOUND, Spells.Grade4.FIRE_SHIELD, Spells.Grade4.GREATER_INVISIBILITY, Spells.Grade4.HALLUCINATORY_TERRAIN, Spells.Grade4.ICE_STORM, Spells.Grade4.LOCATE_CREATURE, Spells.Grade4.PHANTASMAL_KILLER, Spells.Grade4.POLYMORPH, Spells.Grade4.PRIVATE_SANCTUM, Spells.Grade4.RESILIENT_SPHERE, Spells.Grade4.SECRET_CHEST, Spells.Grade4.STONESKIN, Spells.Grade4.STONE_SHAPE, Spells.Grade4.VITRIOLIC_SPHERE, Spells.Grade4.WALL_OF_FIRE,
            Spells.Grade5.ANIMATE_OBJECTS, Spells.Grade5.ARCANE_HAND, Spells.Grade5.CLOUDKILL, Spells.Grade5.CONE_OF_COLD, Spells.Grade5.CONJURE_ELEMENTAL, Spells.Grade5.CONTACT_OTHER_PLANE, Spells.Grade5.CREATION, Spells.Grade5.DOMINATE_PERSON, Spells.Grade5.DREAM, Spells.Grade5.GEAS, Spells.Grade5.HOLD_MONSTER, Spells.Grade5.LEGEND_LORE, Spells.Grade5.MISLEAD, Spells.Grade5.MODIFY_MEMORY, Spells.Grade5.PASSWALL, Spells.Grade5.PLANAR_BINDING, Spells.Grade5.SCRYING, Spells.Grade5.SEEMING, Spells.Grade5.SUMMON_DRAGON, Spells.Grade5.TELEKINESIS, Spells.Grade5.TELEPATHIC_BOND, Spells.Grade5.TELEPORTATION_CIRCLE, Spells.Grade5.WALL_OF_FORCE, Spells.Grade5.WALL_OF_STONE,
            Spells.Grade6.CHAIN_LIGHTNING, Spells.Grade6.CIRCLE_OF_DEATH, Spells.Grade6.CONTINGENCY, Spells.Grade6.CREATE_UNDEAD, Spells.Grade6.DISINTEGRATE, Spells.Grade6.EYEBITE, Spells.Grade6.FLESH_TO_STONE, Spells.Grade6.FREEZING_SPHERE, Spells.Grade6.GLOBE_OF_INVULNERABILITY, Spells.Grade6.GUARDS_AND_WARDS, Spells.Grade6.INSTANT_SUMMONS, Spells.Grade6.IRRESISTIBLE_DANCE, Spells.Grade6.MAGIC_JAR, Spells.Grade6.MASS_SUGGESTION, Spells.Grade6.MOVE_EARTH, Spells.Grade6.PROGRAMMED_ILLUSION, Spells.Grade6.SUNBEAM, Spells.Grade6.TRUE_SEEING, Spells.Grade6.WALL_OF_ICE,
            Spells.Grade7.ARCANE_SWORD, Spells.Grade7.DELAYED_BLAST_FIREBALL, Spells.Grade7.ETHEREALNESS, Spells.Grade7.FINGER_OF_DEATH, Spells.Grade7.FORCECAGE, Spells.Grade7.MAGNIFICENT_MANSION, Spells.Grade7.MIRAGE_ARCANE, Spells.Grade7.PLANE_SHIFT, Spells.Grade7.PRISMATIC_SPRAY, Spells.Grade7.PROJECT_IMAGE, Spells.Grade7.REVERSE_GRAVITY, Spells.Grade7.SEQUESTER, Spells.Grade7.SIMULACRUM, Spells.Grade7.SYMBOL, Spells.Grade7.TELEPORT,
            Spells.Grade8.ANTIMAGIC_FIELD, Spells.Grade8.BEFUDDLEMENT, Spells.Grade8.CLONE, Spells.Grade8.CONTROL_WEATHER, Spells.Grade8.DEMIPLANE, Spells.Grade8.DOMINATE_MONSTER, Spells.Grade8.INCENDIARY_CLOUD, Spells.Grade8.MAZE, Spells.Grade8.MIND_BLANK, Spells.Grade8.POWER_WORD_STUN, Spells.Grade8.SUNBURST,
            Spells.Grade9.ASTRAL_PROJECTION, Spells.Grade9.FORESIGHT, Spells.Grade9.GATE, Spells.Grade9.IMPRISONMENT, Spells.Grade9.METEOR_SWARM, Spells.Grade9.POWER_WORD_KILL, Spells.Grade9.PRISMATIC_WALL, Spells.Grade9.SHAPECHANGE, Spells.Grade9.TIME_STOP, Spells.Grade9.TRUE_POLYMORPH, Spells.Grade9.WEIRD, Spells.Grade9.WISH
    );

    private static final List<Enum<?>> CLERIC_SPELLS = List.of(
            Spells.Cantrip.GUIDANCE, Spells.Cantrip.LIGHT, Spells.Cantrip.MENDING, Spells.Cantrip.RESISTANCE, Spells.Cantrip.SACRED_FLAME, Spells.Cantrip.SPARE_THE_DYING, Spells.Cantrip.THAUMATURGY,
            Spells.Grade1.BANE, Spells.Grade1.BLESS, Spells.Grade1.COMMAND, Spells.Grade1.CREATE_OR_DESTROY_WATER, Spells.Grade1.CURE_WOUNDS, Spells.Grade1.DETECT_EVIL_AND_GOOD, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DETECT_POISON_AND_DISEASE, Spells.Grade1.GUIDING_BOLT, Spells.Grade1.HEALING_WORD, Spells.Grade1.INFLICT_WOUNDS, Spells.Grade1.PROTECTION_FROM_EVIL_AND_GOOD, Spells.Grade1.PURIFY_FOOD_AND_DRINK, Spells.Grade1.SANCTUARY, Spells.Grade1.SHIELD_OF_FAITH,
            Spells.Grade2.AID, Spells.Grade2.AUGURY, Spells.Grade2.CALM_EMOTIONS, Spells.Grade2.CONTINUAL_FLAME, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.FIND_TRAPS, Spells.Grade2.GENTLE_REPOSE, Spells.Grade2.HOLD_PERSON, Spells.Grade2.LESSER_RESTORATION, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.PRAYER_OF_HEALING, Spells.Grade2.PROTECTION_FROM_POISON, Spells.Grade2.SILENCE, Spells.Grade2.SPIRITUAL_WEAPON, Spells.Grade2.WARDING_BOND, Spells.Grade2.ZONE_OF_TRUTH,
            Spells.Grade3.ANIMATE_DEAD, Spells.Grade3.BEACON_OF_HOPE, Spells.Grade3.BESTOW_CURSE, Spells.Grade3.CLAIRVOYANCE, Spells.Grade3.CREATE_FOOD_AND_WATER, Spells.Grade3.DAYLIGHT, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.GLYPH_OF_WARDING, Spells.Grade3.MAGIC_CIRCLE, Spells.Grade3.MASS_HEALING_WORD, Spells.Grade3.MELD_INTO_STONE, Spells.Grade3.PROTECTION_FROM_ENERGY, Spells.Grade3.REMOVE_CURSE, Spells.Grade3.REVIVIFY, Spells.Grade3.SENDING, Spells.Grade3.SPEAK_WITH_DEAD, Spells.Grade3.SPIRIT_GUARDIANS, Spells.Grade3.TONGUES, Spells.Grade3.WATER_WALK,
            Spells.Grade4.AURA_OF_LIFE, Spells.Grade4.BANISHMENT, Spells.Grade4.CONTROL_WATER, Spells.Grade4.DEATH_WARD, Spells.Grade4.DIVINATION, Spells.Grade4.FREEDOM_OF_MOVEMENT, Spells.Grade4.GUARDIAN_OF_FAITH, Spells.Grade4.LOCATE_CREATURE, Spells.Grade4.STONE_SHAPE,
            Spells.Grade5.COMMUNE, Spells.Grade5.CONTAGION, Spells.Grade5.DISPEL_EVIL_AND_GOOD, Spells.Grade5.FLAME_STRIKE, Spells.Grade5.GEAS, Spells.Grade5.GREATER_RESTORATION, Spells.Grade5.HALLOW, Spells.Grade5.INSECT_PLAGUE, Spells.Grade5.LEGEND_LORE, Spells.Grade5.MASS_CURE_WOUNDS, Spells.Grade5.PLANAR_BINDING, Spells.Grade5.RAISE_DEAD, Spells.Grade5.SCRYING,
            Spells.Grade6.BLADE_BARRIER, Spells.Grade6.CREATE_UNDEAD, Spells.Grade6.FIND_THE_PATH, Spells.Grade6.FORBIDDANCE, Spells.Grade6.HARM, Spells.Grade6.HEAL, Spells.Grade6.HEROES_FEAST, Spells.Grade6.PLANAR_ALLY, Spells.Grade6.SUNBEAM, Spells.Grade6.TRUE_SEEING, Spells.Grade6.WORD_OF_RECALL,
            Spells.Grade7.CONJURE_CELESTIAL, Spells.Grade7.DIVINE_WORD, Spells.Grade7.ETHEREALNESS, Spells.Grade7.FIRE_STORM, Spells.Grade7.PLANE_SHIFT, Spells.Grade7.REGENERATE, Spells.Grade7.RESURRECTION, Spells.Grade7.SYMBOL,
            Spells.Grade8.ANTIMAGIC_FIELD, Spells.Grade8.CONTROL_WEATHER, Spells.Grade8.EARTHQUAKE, Spells.Grade8.HOLY_AURA, Spells.Grade8.SUNBURST,
            Spells.Grade9.ASTRAL_PROJECTION, Spells.Grade9.GATE, Spells.Grade9.MASS_HEAL, Spells.Grade9.POWER_WORD_HEAL, Spells.Grade9.TRUE_RESURRECTION
    );

    private static final List<Enum<?>> SORCERER_SPELLS = List.of(
            Spells.Cantrip.ACID_SPLASH, Spells.Cantrip.CHILL_TOUCH, Spells.Cantrip.DANCING_LIGHTS, Spells.Cantrip.ELEMENTALISM, Spells.Cantrip.FIRE_BOLT, Spells.Cantrip.LIGHT, Spells.Cantrip.MAGE_HAND, Spells.Cantrip.MENDING, Spells.Cantrip.MESSAGE, Spells.Cantrip.MINOR_ILLUSION, Spells.Cantrip.POISON_SPRAY, Spells.Cantrip.PRESTIDIGITATION, Spells.Cantrip.RAY_OF_FROST, Spells.Cantrip.SHOCKING_GRASP, Spells.Cantrip.SORCEROUS_BURST, Spells.Cantrip.TRUE_STRIKE,
            Spells.Grade1.BURNING_HANDS, Spells.Grade1.CHARM_PERSON, Spells.Grade1.CHROMATIC_ORB, Spells.Grade1.COLOR_SPRAY, Spells.Grade1.COMPREHEND_LANGUAGES, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DISGUISE_SELF, Spells.Grade1.EXPEDITIOUS_RETREAT, Spells.Grade1.FALSE_LIFE, Spells.Grade1.FEATHER_FALL, Spells.Grade1.FOG_CLOUD, Spells.Grade1.GREASE, Spells.Grade1.ICE_KNIFE, Spells.Grade1.JUMP, Spells.Grade1.MAGE_ARMOR, Spells.Grade1.MAGIC_MISSILE, Spells.Grade1.RAY_OF_SICKNESS, Spells.Grade1.SHIELD, Spells.Grade1.SILENT_IMAGE, Spells.Grade1.SLEEP, Spells.Grade1.THUNDERWAVE,
            Spells.Grade2.ALTER_SELF, Spells.Grade2.BLUR, Spells.Grade2.DARKNESS, Spells.Grade2.DARKVISION, Spells.Grade2.DETECT_THOUGHTS, Spells.Grade2.DRAGON_S_BREATH, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.ENLARGE_REDUCE, Spells.Grade2.FLAME_BLADE, Spells.Grade2.FLAMING_SPHERE, Spells.Grade2.GUST_OF_WIND, Spells.Grade2.HOLD_PERSON, Spells.Grade2.INVISIBILITY, Spells.Grade2.KNOCK, Spells.Grade2.LEVITATE, Spells.Grade2.MAGIC_WEAPON, Spells.Grade2.MIND_SPIKE, Spells.Grade2.MIRROR_IMAGE, Spells.Grade2.MISTY_STEP, Spells.Grade2.PHANTASMAL_FORCE, Spells.Grade2.SCORCHING_RAY, Spells.Grade2.SEE_INVISIBILITY, Spells.Grade2.SHATTER, Spells.Grade2.SPIDER_CLIMB, Spells.Grade2.SUGGESTION, Spells.Grade2.WEB,
            Spells.Grade3.BLINK, Spells.Grade3.CLAIRVOYANCE, Spells.Grade3.COUNTERSPELL, Spells.Grade3.DAYLIGHT, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.FEAR, Spells.Grade3.FIREBALL, Spells.Grade3.FLY, Spells.Grade3.GASEOUS_FORM, Spells.Grade3.HASTE, Spells.Grade3.HYPNOTIC_PATTERN, Spells.Grade3.LIGHTNING_BOLT, Spells.Grade3.MAJOR_IMAGE, Spells.Grade3.PROTECTION_FROM_ENERGY, Spells.Grade3.SLEET_STORM, Spells.Grade3.SLOW, Spells.Grade3.STINKING_CLOUD, Spells.Grade3.TONGUES, Spells.Grade3.VAMPIRIC_TOUCH, Spells.Grade3.WATER_BREATHING, Spells.Grade3.WATER_WALK,
            Spells.Grade4.BANISHMENT, Spells.Grade4.BLIGHT, Spells.Grade4.CHARM_MONSTER, Spells.Grade4.CONFUSION, Spells.Grade4.DIMENSION_DOOR, Spells.Grade4.DOMINATE_BEAST, Spells.Grade4.FIRE_SHIELD, Spells.Grade4.GREATER_INVISIBILITY, Spells.Grade4.ICE_STORM, Spells.Grade4.POLYMORPH, Spells.Grade4.STONESKIN, Spells.Grade4.VITRIOLIC_SPHERE, Spells.Grade4.WALL_OF_FIRE,
            Spells.Grade5.ANIMATE_OBJECTS, Spells.Grade5.ARCANE_HAND, Spells.Grade5.CLOUDKILL, Spells.Grade5.CONE_OF_COLD, Spells.Grade5.CREATION, Spells.Grade5.DOMINATE_PERSON, Spells.Grade5.HOLD_MONSTER, Spells.Grade5.INSECT_PLAGUE, Spells.Grade5.SEEMING, Spells.Grade5.TELEKINESIS, Spells.Grade5.TELEPORTATION_CIRCLE, Spells.Grade5.WALL_OF_STONE,
            Spells.Grade6.CHAIN_LIGHTNING, Spells.Grade6.CIRCLE_OF_DEATH, Spells.Grade6.DISINTEGRATE, Spells.Grade6.EYEBITE, Spells.Grade6.FLESH_TO_STONE, Spells.Grade6.FREEZING_SPHERE, Spells.Grade6.GLOBE_OF_INVULNERABILITY, Spells.Grade6.MASS_SUGGESTION, Spells.Grade6.MOVE_EARTH, Spells.Grade6.SUNBEAM, Spells.Grade6.TRUE_SEEING,
            Spells.Grade7.DELAYED_BLAST_FIREBALL, Spells.Grade7.ETHEREALNESS, Spells.Grade7.FINGER_OF_DEATH, Spells.Grade7.FIRE_STORM, Spells.Grade7.PLANE_SHIFT, Spells.Grade7.PRISMATIC_SPRAY, Spells.Grade7.REVERSE_GRAVITY, Spells.Grade7.TELEPORT,
            Spells.Grade8.DEMIPLANE, Spells.Grade8.DOMINATE_MONSTER, Spells.Grade8.EARTHQUAKE, Spells.Grade8.INCENDIARY_CLOUD, Spells.Grade8.POWER_WORD_STUN, Spells.Grade8.SUNBURST,
            Spells.Grade9.GATE, Spells.Grade9.METEOR_SWARM, Spells.Grade9.POWER_WORD_KILL, Spells.Grade9.TIME_STOP, Spells.Grade9.WISH
    );

    private static final List<Enum<?>> DRUID_SPELLS = List.of(
            Spells.Cantrip.DRUIDCRAFT, Spells.Cantrip.ELEMENTALISM, Spells.Cantrip.GUIDANCE, Spells.Cantrip.MENDING, Spells.Cantrip.MESSAGE, Spells.Cantrip.POISON_SPRAY, Spells.Cantrip.PRODUCE_FLAME, Spells.Cantrip.RESISTANCE, Spells.Cantrip.SHILLELAGH, Spells.Cantrip.SPARE_THE_DYING, Spells.Cantrip.STARRY_WISP,
            Spells.Grade1.ANIMAL_FRIENDSHIP, Spells.Grade1.CHARM_PERSON, Spells.Grade1.CREATE_OR_DESTROY_WATER, Spells.Grade1.CURE_WOUNDS, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DETECT_POISON_AND_DISEASE, Spells.Grade1.ENTANGLE, Spells.Grade1.FAERIE_FIRE, Spells.Grade1.FOG_CLOUD, Spells.Grade1.GOODBERRY, Spells.Grade1.HEALING_WORD, Spells.Grade1.ICE_KNIFE, Spells.Grade1.JUMP, Spells.Grade1.LONGSTRIDER, Spells.Grade1.PROTECTION_FROM_EVIL_AND_GOOD, Spells.Grade1.PURIFY_FOOD_AND_DRINK, Spells.Grade1.SPEAK_WITH_ANIMALS, Spells.Grade1.THUNDERWAVE,
            Spells.Grade2.AID, Spells.Grade2.ANIMAL_MESSENGER, Spells.Grade2.AUGURY, Spells.Grade2.BARKSKIN, Spells.Grade2.CONTINUAL_FLAME, Spells.Grade2.DARKVISION, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.ENLARGE_REDUCE, Spells.Grade2.FIND_TRAPS, Spells.Grade2.FLAME_BLADE, Spells.Grade2.FLAMING_SPHERE, Spells.Grade2.GUST_OF_WIND, Spells.Grade2.HEAT_METAL, Spells.Grade2.HOLD_PERSON, Spells.Grade2.LESSER_RESTORATION, Spells.Grade2.LOCATE_ANIMALS_OR_PLANTS, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.MOONBEAM, Spells.Grade2.PASS_WITHOUT_TRACE, Spells.Grade2.PROTECTION_FROM_POISON, Spells.Grade2.SPIKE_GROWTH,
            Spells.Grade3.CALL_LIGHTNING, Spells.Grade3.CONJURE_ANIMALS, Spells.Grade3.DAYLIGHT, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.MELD_INTO_STONE, Spells.Grade3.PLANT_GROWTH, Spells.Grade3.PROTECTION_FROM_ENERGY, Spells.Grade3.REVIVIFY, Spells.Grade3.SLEET_STORM, Spells.Grade3.SPEAK_WITH_PLANTS, Spells.Grade3.WATER_BREATHING, Spells.Grade3.WATER_WALK, Spells.Grade3.WIND_WALL,
            Spells.Grade4.BLIGHT, Spells.Grade4.CHARM_MONSTER, Spells.Grade4.CONFUSION, Spells.Grade4.CONJURE_MINOR_ELEMENTALS, Spells.Grade4.CONJURE_WOODLAND_BEINGS, Spells.Grade4.CONTROL_WATER, Spells.Grade4.DIVINATION, Spells.Grade4.DOMINATE_BEAST, Spells.Grade4.FIRE_SHIELD, Spells.Grade4.FREEDOM_OF_MOVEMENT, Spells.Grade4.GIANT_INSECT, Spells.Grade4.HALLUCINATORY_TERRAIN, Spells.Grade4.ICE_STORM, Spells.Grade4.LOCATE_CREATURE, Spells.Grade4.POLYMORPH, Spells.Grade4.STONESKIN, Spells.Grade4.STONE_SHAPE, Spells.Grade4.WALL_OF_FIRE,
            Spells.Grade5.ANTILIFE_SHELL, Spells.Grade5.AWAKEN, Spells.Grade5.COMMUNE_WITH_NATURE, Spells.Grade5.CONE_OF_COLD, Spells.Grade5.CONJURE_ELEMENTAL, Spells.Grade5.CONTAGION, Spells.Grade5.GEAS, Spells.Grade5.GREATER_RESTORATION, Spells.Grade5.INSECT_PLAGUE, Spells.Grade5.MASS_CURE_WOUNDS, Spells.Grade5.PLANAR_BINDING, Spells.Grade5.REINCARNATE, Spells.Grade5.SCRYING, Spells.Grade5.TREE_STRIDE, Spells.Grade5.WALL_OF_STONE,
            Spells.Grade6.CONJURE_FEY, Spells.Grade6.FIND_THE_PATH, Spells.Grade6.FLESH_TO_STONE, Spells.Grade6.HEAL, Spells.Grade6.HEROES_FEAST, Spells.Grade6.MOVE_EARTH, Spells.Grade6.SUNBEAM, Spells.Grade6.TRANSPORT_VIA_PLANTS, Spells.Grade6.WALL_OF_THORNS, Spells.Grade6.WIND_WALK,
            Spells.Grade7.FIRE_STORM, Spells.Grade7.MIRAGE_ARCANE, Spells.Grade7.PLANE_SHIFT, Spells.Grade7.REGENERATE, Spells.Grade7.REVERSE_GRAVITY, Spells.Grade7.SYMBOL,
            Spells.Grade8.ANIMAL_SHAPES, Spells.Grade8.BEFUDDLEMENT, Spells.Grade8.CONTROL_WEATHER, Spells.Grade8.EARTHQUAKE, Spells.Grade8.INCENDIARY_CLOUD, Spells.Grade8.SUNBURST, Spells.Grade8.TSUNAMI,
            Spells.Grade9.FORESIGHT, Spells.Grade9.SHAPECHANGE, Spells.Grade9.STORM_OF_VENGEANCE, Spells.Grade9.TRUE_RESURRECTION
    );

    private static final List<Enum<?>> WARLOCK_SPELLS = List.of(
            Spells.Cantrip.CHILL_TOUCH, Spells.Cantrip.ELDRITCH_BLAST, Spells.Cantrip.MAGE_HAND, Spells.Cantrip.MINOR_ILLUSION, Spells.Cantrip.POISON_SPRAY, Spells.Cantrip.PRESTIDIGITATION, Spells.Cantrip.TRUE_STRIKE,
            Spells.Grade1.BANE, Spells.Grade1.CHARM_PERSON, Spells.Grade1.COMPREHEND_LANGUAGES, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.EXPEDITIOUS_RETREAT, Spells.Grade1.HELLISH_REBUKE, Spells.Grade1.HEX, Spells.Grade1.HIDEOUS_LAUGHTER, Spells.Grade1.ILLUSORY_SCRIPT, Spells.Grade1.PROTECTION_FROM_EVIL_AND_GOOD, Spells.Grade1.SPEAK_WITH_ANIMALS, Spells.Grade1.UNSEEN_SERVANT,
            Spells.Grade2.DARKNESS, Spells.Grade2.ENTHRALL, Spells.Grade2.HOLD_PERSON, Spells.Grade2.INVISIBILITY, Spells.Grade2.MIND_SPIKE, Spells.Grade2.MIRROR_IMAGE, Spells.Grade2.MISTY_STEP, Spells.Grade2.RAY_OF_ENFEEBLEMENT, Spells.Grade2.SPIDER_CLIMB, Spells.Grade2.SUGGESTION,
            Spells.Grade3.COUNTERSPELL, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.FEAR, Spells.Grade3.FLY, Spells.Grade3.GASEOUS_FORM, Spells.Grade3.HYPNOTIC_PATTERN, Spells.Grade3.MAGIC_CIRCLE, Spells.Grade3.MAJOR_IMAGE, Spells.Grade3.REMOVE_CURSE, Spells.Grade3.TONGUES, Spells.Grade3.VAMPIRIC_TOUCH,
            Spells.Grade4.BANISHMENT, Spells.Grade4.BLIGHT, Spells.Grade4.CHARM_MONSTER, Spells.Grade4.DIMENSION_DOOR, Spells.Grade4.HALLUCINATORY_TERRAIN,
            Spells.Grade5.CONTACT_OTHER_PLANE, Spells.Grade5.DREAM, Spells.Grade5.HOLD_MONSTER, Spells.Grade5.MISLEAD, Spells.Grade5.PLANAR_BINDING, Spells.Grade5.SCRYING, Spells.Grade5.TELEPORTATION_CIRCLE,
            Spells.Grade6.CIRCLE_OF_DEATH, Spells.Grade6.CREATE_UNDEAD, Spells.Grade6.EYEBITE, Spells.Grade6.TRUE_SEEING,
            Spells.Grade7.ETHEREALNESS, Spells.Grade7.FINGER_OF_DEATH, Spells.Grade7.FORCECAGE, Spells.Grade7.PLANE_SHIFT,
            Spells.Grade8.BEFUDDLEMENT, Spells.Grade8.DEMIPLANE, Spells.Grade8.DOMINATE_MONSTER, Spells.Grade8.GLIBNESS, Spells.Grade8.POWER_WORD_STUN,
            Spells.Grade9.ASTRAL_PROJECTION, Spells.Grade9.FORESIGHT, Spells.Grade9.GATE, Spells.Grade9.IMPRISONMENT, Spells.Grade9.POWER_WORD_KILL, Spells.Grade9.TRUE_POLYMORPH, Spells.Grade9.WEIRD
    );

    private static final List<Enum<?>> BARD_SPELLS = List.of(
            Spells.Cantrip.DANCING_LIGHTS, Spells.Cantrip.LIGHT, Spells.Cantrip.MAGE_HAND, Spells.Cantrip.MENDING, Spells.Cantrip.MESSAGE, Spells.Cantrip.MINOR_ILLUSION, Spells.Cantrip.PRESTIDIGITATION, Spells.Cantrip.STARRY_WISP, Spells.Cantrip.TRUE_STRIKE, Spells.Cantrip.VICIOUS_MOCKERY,
            Spells.Grade1.ANIMAL_FRIENDSHIP, Spells.Grade1.BANE, Spells.Grade1.CHARM_PERSON, Spells.Grade1.COLOR_SPRAY, Spells.Grade1.COMMAND, Spells.Grade1.COMPREHEND_LANGUAGES, Spells.Grade1.CURE_WOUNDS, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DISGUISE_SELF, Spells.Grade1.DISSONANT_WHISPERS, Spells.Grade1.FAERIE_FIRE, Spells.Grade1.FEATHER_FALL, Spells.Grade1.HEALING_WORD, Spells.Grade1.HEROISM, Spells.Grade1.HIDEOUS_LAUGHTER, Spells.Grade1.IDENTIFY, Spells.Grade1.ILLUSORY_SCRIPT, Spells.Grade1.LONGSTRIDER, Spells.Grade1.SILENT_IMAGE, Spells.Grade1.SLEEP, Spells.Grade1.SPEAK_WITH_ANIMALS, Spells.Grade1.THUNDERWAVE, Spells.Grade1.UNSEEN_SERVANT,
            Spells.Grade2.AID, Spells.Grade2.ANIMAL_MESSENGER, Spells.Grade2.CALM_EMOTIONS, Spells.Grade2.DETECT_THOUGHTS, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.ENLARGE_REDUCE, Spells.Grade2.ENTHRALL, Spells.Grade2.HEAT_METAL, Spells.Grade2.HOLD_PERSON, Spells.Grade2.INVISIBILITY, Spells.Grade2.KNOCK, Spells.Grade2.LESSER_RESTORATION, Spells.Grade2.LOCATE_ANIMALS_OR_PLANTS, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.MAGIC_MOUTH, Spells.Grade2.MIRROR_IMAGE, Spells.Grade2.PHANTASMAL_FORCE, Spells.Grade2.SEE_INVISIBILITY, Spells.Grade2.SHATTER, Spells.Grade2.SILENCE, Spells.Grade2.SUGGESTION, Spells.Grade2.ZONE_OF_TRUTH,
            Spells.Grade3.BESTOW_CURSE, Spells.Grade3.CLAIRVOYANCE, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.FEAR, Spells.Grade3.GLYPH_OF_WARDING, Spells.Grade3.HYPNOTIC_PATTERN, Spells.Grade3.MAJOR_IMAGE, Spells.Grade3.MASS_HEALING_WORD, Spells.Grade3.NONDETECTION, Spells.Grade3.PLANT_GROWTH, Spells.Grade3.SENDING, Spells.Grade3.SLOW, Spells.Grade3.SPEAK_WITH_DEAD, Spells.Grade3.SPEAK_WITH_PLANTS, Spells.Grade3.STINKING_CLOUD, Spells.Grade3.TINY_HUT, Spells.Grade3.TONGUES,
            Spells.Grade4.CHARM_MONSTER, Spells.Grade4.COMPULSION, Spells.Grade4.CONFUSION, Spells.Grade4.DIMENSION_DOOR, Spells.Grade4.FREEDOM_OF_MOVEMENT, Spells.Grade4.GREATER_INVISIBILITY, Spells.Grade4.HALLUCINATORY_TERRAIN, Spells.Grade4.LOCATE_CREATURE, Spells.Grade4.PHANTASMAL_KILLER, Spells.Grade4.POLYMORPH,
            Spells.Grade5.ANIMATE_OBJECTS, Spells.Grade5.AWAKEN, Spells.Grade5.DOMINATE_PERSON, Spells.Grade5.DREAM, Spells.Grade5.GEAS, Spells.Grade5.GREATER_RESTORATION, Spells.Grade5.HOLD_MONSTER, Spells.Grade5.LEGEND_LORE, Spells.Grade5.MASS_CURE_WOUNDS, Spells.Grade5.MISLEAD, Spells.Grade5.MODIFY_MEMORY, Spells.Grade5.PLANAR_BINDING, Spells.Grade5.RAISE_DEAD, Spells.Grade5.SCRYING, Spells.Grade5.SEEMING, Spells.Grade5.TELEPATHIC_BOND, Spells.Grade5.TELEPORTATION_CIRCLE,
            Spells.Grade6.EYEBITE, Spells.Grade6.FIND_THE_PATH, Spells.Grade6.GUARDS_AND_WARDS, Spells.Grade6.HEROES_FEAST, Spells.Grade6.IRRESISTIBLE_DANCE, Spells.Grade6.MASS_SUGGESTION, Spells.Grade6.PROGRAMMED_ILLUSION, Spells.Grade6.TRUE_SEEING,
            Spells.Grade7.ARCANE_SWORD, Spells.Grade7.ETHEREALNESS, Spells.Grade7.FORCECAGE, Spells.Grade7.MAGNIFICENT_MANSION, Spells.Grade7.MIRAGE_ARCANE, Spells.Grade7.PRISMATIC_SPRAY, Spells.Grade7.PROJECT_IMAGE, Spells.Grade7.REGENERATE, Spells.Grade7.RESURRECTION, Spells.Grade7.SYMBOL, Spells.Grade7.TELEPORT,
            Spells.Grade8.BEFUDDLEMENT, Spells.Grade8.DOMINATE_MONSTER, Spells.Grade8.GLIBNESS, Spells.Grade8.MIND_BLANK, Spells.Grade8.POWER_WORD_STUN,
            Spells.Grade9.FORESIGHT, Spells.Grade9.POWER_WORD_HEAL, Spells.Grade9.POWER_WORD_KILL, Spells.Grade9.PRISMATIC_WALL, Spells.Grade9.TRUE_POLYMORPH
    );

    private static final List<Enum<?>> PALADIN_SPELLS = List.of(
            Spells.Grade1.BLESS, Spells.Grade1.COMMAND, Spells.Grade1.CURE_WOUNDS, Spells.Grade1.DETECT_EVIL_AND_GOOD, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DETECT_POISON_AND_DISEASE, Spells.Grade1.DIVINE_FAVOR, Spells.Grade1.DIVINE_SMITE, Spells.Grade1.HEROISM, Spells.Grade1.PROTECTION_FROM_EVIL_AND_GOOD, Spells.Grade1.PURIFY_FOOD_AND_DRINK, Spells.Grade1.SEARING_SMITE, Spells.Grade1.SHIELD_OF_FAITH,
            Spells.Grade2.AID, Spells.Grade2.FIND_STEED, Spells.Grade2.GENTLE_REPOSE, Spells.Grade2.LESSER_RESTORATION, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.MAGIC_WEAPON, Spells.Grade2.PRAYER_OF_HEALING, Spells.Grade2.PROTECTION_FROM_POISON, Spells.Grade2.SHINING_SMITE, Spells.Grade2.WARDING_BOND, Spells.Grade2.ZONE_OF_TRUTH,
            Spells.Grade3.CREATE_FOOD_AND_WATER, Spells.Grade3.DAYLIGHT, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.MAGIC_CIRCLE, Spells.Grade3.REMOVE_CURSE, Spells.Grade3.REVIVIFY,
            Spells.Grade4.AURA_OF_LIFE, Spells.Grade4.BANISHMENT, Spells.Grade4.DEATH_WARD, Spells.Grade4.LOCATE_CREATURE,
            Spells.Grade5.DISPEL_EVIL_AND_GOOD, Spells.Grade5.GEAS, Spells.Grade5.GREATER_RESTORATION, Spells.Grade5.RAISE_DEAD
    );

    private static final List<Enum<?>> RANGER_SPELLS = List.of(
            Spells.Grade1.ALARM, Spells.Grade1.ANIMAL_FRIENDSHIP, Spells.Grade1.CURE_WOUNDS, Spells.Grade1.DETECT_MAGIC, Spells.Grade1.DETECT_POISON_AND_DISEASE, Spells.Grade1.ENSNARING_STRIKE, Spells.Grade1.ENTANGLE, Spells.Grade1.FOG_CLOUD, Spells.Grade1.GOODBERRY, Spells.Grade1.HUNTER_S_MARK, Spells.Grade1.JUMP, Spells.Grade1.LONGSTRIDER, Spells.Grade1.SPEAK_WITH_ANIMALS,
            Spells.Grade2.AID, Spells.Grade2.ANIMAL_MESSENGER, Spells.Grade2.BARKSKIN, Spells.Grade2.DARKVISION, Spells.Grade2.ENHANCE_ABILITY, Spells.Grade2.FIND_TRAPS, Spells.Grade2.GUST_OF_WIND, Spells.Grade2.LESSER_RESTORATION, Spells.Grade2.LOCATE_ANIMALS_OR_PLANTS, Spells.Grade2.LOCATE_OBJECT, Spells.Grade2.MAGIC_WEAPON, Spells.Grade2.PASS_WITHOUT_TRACE, Spells.Grade2.PROTECTION_FROM_POISON, Spells.Grade2.SILENCE, Spells.Grade2.SPIKE_GROWTH,
            Spells.Grade3.CONJURE_ANIMALS, Spells.Grade3.DAYLIGHT, Spells.Grade3.DISPEL_MAGIC, Spells.Grade3.MELD_INTO_STONE, Spells.Grade3.NONDETECTION, Spells.Grade3.PLANT_GROWTH, Spells.Grade3.PROTECTION_FROM_ENERGY, Spells.Grade3.REVIVIFY, Spells.Grade3.SPEAK_WITH_PLANTS, Spells.Grade3.WATER_BREATHING, Spells.Grade3.WATER_WALK, Spells.Grade3.WIND_WALL,
            Spells.Grade4.CONJURE_WOODLAND_BEINGS, Spells.Grade4.DOMINATE_BEAST, Spells.Grade4.FREEDOM_OF_MOVEMENT, Spells.Grade4.LOCATE_CREATURE, Spells.Grade4.STONESKIN,
            Spells.Grade5.COMMUNE_WITH_NATURE, Spells.Grade5.GREATER_RESTORATION, Spells.Grade5.TREE_STRIDE
    );


    // ── ATTRS ──
    private static final Map<String, Integer> BARBARIAN_ATTRIBUTES = attrs(2, 0, 1, 0, 0, 0); // +2 Str, +1 Con
    private static final Map<String, Integer> BARD_ATTRIBUTES      = attrs(0, 1, 0, 0, 0, 2); // +2 Cha, +1 Dex
    private static final Map<String, Integer> CLERIC_ATTRIBUTES    = attrs(1, 0, 0, 0, 2, 0); // +2 Wis, +1 Str
    private static final Map<String, Integer> DRUID_ATTRIBUTES     = attrs(0, 0, 1, 0, 2, 0); // +2 Wis, +1 Con
    private static final Map<String, Integer> FIGHTER_ATTRIBUTES   = attrs(2, 0, 1, 0, 0, 0); // +2 Str, +1 Con
    private static final Map<String, Integer> MONK_ATTRIBUTES      = attrs(0, 2, 0, 0, 1, 0); // +2 Dex, +1 Wis
    private static final Map<String, Integer> PALADIN_ATTRIBUTES   = attrs(1, 0, 0, 0, 0, 2); // +2 Cha, +1 Str
    private static final Map<String, Integer> RANGER_ATTRIBUTES    = attrs(0, 2, 0, 0, 1, 0); // +2 Dex, +1 Wis
    private static final Map<String, Integer> ROGUE_ATTRIBUTES     = attrs(0, 2, 1, 0, 0, 0); // +2 Dex, +1 Con
    private static final Map<String, Integer> SORCERER_ATTRIBUTES  = attrs(0, 0, 1, 0, 0, 2); // +2 Cha, +1 Con
    private static final Map<String, Integer> WARLOCK_ATTRIBUTES   = attrs(0, 0, 0, 0, 1, 2); // +2 Cha, +1 Wis
    private static final Map<String, Integer> WIZARD_ATTRIBUTES    = attrs(0, 0, 1, 2, 0, 0); // +2 Int, +1 Con
    // ── PROFICIENCIES ──
    // Tag names that match your item tags under dnd:armor/* and dnd:weapons/*
    private static final String PROFICIENCYS_BARB  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_BARD  = "light_armor,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_CLER  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_DRUI  = "light_armor,medium_armor,shields,simple_weapons";
    private static final String PROFICIENCYS_FIGH  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_MONK  = "simple_weapons";
    private static final String PROFICIENCYS_PALA  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_RANG  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_ROGU  = "light_armor,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_SORC  = "simple_weapons";
    private static final String PROFICIENCYS_WARL  = "light_armor,simple_weapons,war_weapons";
    private static final String PROFICIENCYS_WIZA  = "simple_weapons";
    // Subclass overrides (only when different from parent)
    private static final String PROFICIENCYS_VALOR = "light_armor,medium_armor,shields,simple_weapons,war_weapons"; // College of Valor gains martial
    private static final String PROFICIENCYS_WAR_D = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons"; // War Domain gains heavy
    private static final String PROFICIENCYS_ELDR  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons"; // Eldritch Knight keeps full

    // ── STARTER ITEMS  (class is responsible for all items now) ──
    private static final List<ItemStack> BARBARIAN_ITEMS = List.of(new ItemStack(Items.IRON_AXE), new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.COOKED_BEEF, 10));
    private static final List<ItemStack> BARD_ITEMS      = List.of(new ItemStack(Items.STICK), new ItemStack(Items.BOOK), new ItemStack(Items.BREAD, 6));
    private static final List<ItemStack> CLERIC_ITEMS    = List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.GOLDEN_APPLE));
    private static final List<ItemStack> DRUID_ITEMS     = List.of(new ItemStack(Items.STICK), new ItemStack(Items.OAK_SAPLING, 3), new ItemStack(Items.WHEAT, 8));
    private static final List<ItemStack> FIGHTER_ITEMS   = List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_HELMET), new ItemStack(Items.SHIELD));
    private static final List<ItemStack> MONK_ITEMS      = List.of(new ItemStack(Items.STICK), new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.BREAD, 6));
    private static final List<ItemStack> PALADIN_ITEMS   = List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_HELMET), new ItemStack(Items.SHIELD));
    private static final List<ItemStack> RANGER_ITEMS    = List.of(new ItemStack(Items.BOW), new ItemStack(Items.ARROW, 32), new ItemStack(Items.LEATHER_CHESTPLATE));
    private static final List<ItemStack> ROGUE_ITEMS     = List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.BREAD, 6));
    private static final List<ItemStack> SORCERER_ITEMS  = List.of(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BOOK), new ItemStack(Items.GOLD_INGOT, 3));
    private static final List<ItemStack> WARLOCK_ITEMS   = List.of(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BOOK), new ItemStack(Items.GOLD_INGOT, 3));
    private static final List<ItemStack> WIZARD_ITEMS    = List.of(new ItemStack(Items.STICK), new ItemStack(Items.ENCHANTED_BOOK), new ItemStack(Items.LAPIS_LAZULI, 10));

    // ── ABILITIES ──
    private static final List<String> BARBARIAN_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Rage, Unarmored Defense, Weapon Mastery",
            "Lvl 2: Danger Sense, Reckless Attack",
            "Lvl 3: Barbarian Subclass, Primal Knowledge",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Extra Attack, Fast Movement",
            "Lvl 6: Subclass Feature",
            "Lvl 7: Instinctive Pounce, Feral Instinct",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Brutal Strike",
            "Lvl 10: Subclass Feature",
            "Lvl 11: Relentless Rage",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Brutal Strike (Improvement)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Persistent Rage",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Brutal Strike (Improvement)",
            "Lvl 18: Indomitable Might",
            "Lvl 19: Epic Boon",
            "Lvl 20: Primal Champion"
    );

    private static final List<String> BARD_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Bardic Inspiration (2x Lvl 1 SSl)",
            "Lvl 2: Jack of All Trades, Expertise (+1x Lvl 1 SSl)",
            "Lvl 3: Bardic College (Subclass) (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat (+1x Lvl 2 SSl)",
            "Lvl 5: Font of Inspiration (+2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature (+1x Lvl 3 SSl)",
            "Lvl 7: Countercharm (+1x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat (+1x Lvl 4 SSl)",
            "Lvl 9: Expertise (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 10: Magical Secrets (+1x Lvl 5 SSl)",
            "Lvl 11: Spell Slot (+1x Lvl 6 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 7 SSl)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Spell Slot (+1x Lvl 8 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 9 SSl)",
            "Lvl 18: Superior Inspiration (+1x Lvl 5 SSl)",
            "Lvl 19: Epic Boon (+1x Lvl 6 SSl)",
            "Lvl 20: Words of Creation (+1x Lvl 7 SSl)"
    );

    private static final List<String> CLERIC_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Divine Order (2x Lvl 1 SSl)",
            "Lvl 2: Channel Divinity (+1x Lvl 1 SSl)",
            "Lvl 3: Cleric Subclass (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat (+1x Lvl 2 SSl)",
            "Lvl 5: Sear Undead, Blessed Strikes (+2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature, Channel Divinity (+1x Lvl 3 SSl)",
            "Lvl 7: Blessed Strikes (Improvement) (+1x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat (+1x Lvl 4 SSl)",
            "Lvl 9: Divine Intervention (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 10: Subclass Feature (+1x Lvl 5 SSl)",
            "Lvl 11: Spell Slot (+1x Lvl 6 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 7 SSl)",
            "Lvl 14: Blessed Strikes (Improvement)",
            "Lvl 15: Spell Slot (+1x Lvl 8 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 9 SSl)",
            "Lvl 18: Spell Slot (+1x Lvl 5 SSl)",
            "Lvl 19: Epic Boon (+1x Lvl 6 SSl)",
            "Lvl 20: Greater Divine Intervention (+1x Lvl 7 SSl)"
    );

    private static final List<String> DRUID_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Primal Order, Wild Shape (2x Lvl 1 SSl)",
            "Lvl 2: Wild Companion (+1x Lvl 1 SSl)",
            "Lvl 3: Druid Subclass (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat (+1x Lvl 2 SSl)",
            "Lvl 5: Wild Resurgence (+2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature (+1x Lvl 3 SSl)",
            "Lvl 7: Elemental Fury (+1x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat (+1x Lvl 4 SSl)",
            "Lvl 9: Spell Slot (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 10: Subclass Feature (+1x Lvl 5 SSl)",
            "Lvl 11: Spell Slot (+1x Lvl 6 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 7 SSl)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Elemental Fury (Improvement) (+1x Lvl 8 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 9 SSl)",
            "Lvl 18: Beast Spells (+1x Lvl 5 SSl)",
            "Lvl 19: Epic Boon (+1x Lvl 6 SSl)",
            "Lvl 20: Archdruid (+1x Lvl 7 SSl)"
    );

    private static final List<String> FIGHTER_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Fighting Style, Second Wind, Weapon Mastery",
            "Lvl 2: Action Surge, Tactical Mind",
            "Lvl 3: Fighter Subclass",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Extra Attack",
            "Lvl 6: Attribute Increase / Feat",
            "Lvl 7: Tactical Shift, Subclass Feature",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Indomitable",
            "Lvl 10: Subclass Feature",
            "Lvl 11: Extra Attack (2)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Tactical Master",
            "Lvl 14: Attribute Increase / Feat",
            "Lvl 15: Subclass Feature",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Action Surge (2), Indomitable (2)",
            "Lvl 18: Subclass Feature",
            "Lvl 19: Epic Boon",
            "Lvl 20: Extra Attack (3)"
    );

    private static final List<String> MONK_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Martial Arts, Unarmored Defense",
            "Lvl 2: Monk's Focus, Uncanny Metabolism, Unarmored Movement",
            "Lvl 3: Monk Subclass, Deflect Attacks",
            "Lvl 4: Attribute Increase / Feat, Slow Fall",
            "Lvl 5: Extra Attack, Stunning Strike",
            "Lvl 6: Subclass Feature, Empowered Strikes",
            "Lvl 7: Evasion",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Acrobatic Movement",
            "Lvl 10: Subclass Feature, Heightened Focus, Self-Restoration",
            "Lvl 11: Martial Arts Scaling",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Deflect Energy",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Perfect Focus",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Martial Arts Scaling",
            "Lvl 18: Superior Defense",
            "Lvl 19: Epic Boon",
            "Lvl 20: Body and Mind"
    );

    private static final List<String> PALADIN_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Lay on Hands, Spellcasting, Weapon Mastery (2x Lvl 1 SSl)",
            "Lvl 2: Paladin's Smite, Fighting Style",
            "Lvl 3: Paladin Subclass, Channel Divinity (+1x Lvl 1 SSl)",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Extra Attack, Faithful Steed (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 6: Aura of Protection",
            "Lvl 7: Subclass Feature (+1x Lvl 2 SSl)",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Abjure Foes (+2x Lvl 3 SSl)",
            "Lvl 10: Aura of Courage",
            "Lvl 11: Radiant Smite (+1x Lvl 3 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 4 SSl)",
            "Lvl 14: Restoring Touch",
            "Lvl 15: Spell Slot (+1x Lvl 4 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 18: Aura Expansion",
            "Lvl 19: Epic Boon (+1x Lvl 5 SSl)",
            "Lvl 20: Oath Paragon"
    );

    private static final List<String> RANGER_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Favored Enemy, Weapon Mastery (2x Lvl 1 SSl)",
            "Lvl 2: Fighting Style, Deft Explorer",
            "Lvl 3: Ranger Subclass (+1x Lvl 1 SSl)",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Extra Attack (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 6: Subclass Feature, Roving",
            "Lvl 7: Spell Slot (+1x Lvl 2 SSl)",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Expertise (+2x Lvl 3 SSl)",
            "Lvl 10: Subclass Feature, Tireless",
            "Lvl 11: Spell Slot (+1x Lvl 3 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Relentless Hunter (+1x Lvl 4 SSl)",
            "Lvl 14: Subclass Feature, Nature's Veil",
            "Lvl 15: Spell Slot (+1x Lvl 4 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Precise Hunter (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 18: Feral Senses",
            "Lvl 19: Epic Boon (+1x Lvl 5 SSl)",
            "Lvl 20: Foe Slayer"
    );

    private static final List<String> ROGUE_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Sneak Attack, Thieves' Cant, Expertise",
            "Lvl 2: Cunning Action, Weapon Mastery",
            "Lvl 3: Rogue Subclass, Steady Aim",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Cunning Strike, Uncanny Dodge",
            "Lvl 6: Expertise",
            "Lvl 7: Evasion",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Subclass Feature",
            "Lvl 10: Attribute Increase / Feat",
            "Lvl 11: Reliable Talent",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Subclass Feature, Devious Strikes",
            "Lvl 14: Blindsense",
            "Lvl 15: Slippery Mind",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Subclass Feature",
            "Lvl 18: Elusive",
            "Lvl 19: Epic Boon",
            "Lvl 20: Stroke of Luck"
    );

    private static final List<String> SORCERER_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Innate Sorcery (2x Lvl 1 SSl)",
            "Lvl 2: Sorcery Points, Font of Magic, Metamagic (+1x Lvl 1 SSl)",
            "Lvl 3: Sorcerous Subclass (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat (+1x Lvl 2 SSl)",
            "Lvl 5: Sorcerous Restoration (+2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature (+1x Lvl 3 SSl)",
            "Lvl 7: Sorcery Incarnate (+1x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat (+1x Lvl 4 SSl)",
            "Lvl 9: Spell Slot (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 10: Subclass Feature (+1x Lvl 5 SSl)",
            "Lvl 11: Spell Slot (+1x Lvl 6 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 7 SSl)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Spell Slot (+1x Lvl 8 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 9 SSl)",
            "Lvl 18: Arcane Apotheosis (+1x Lvl 5 SSl)",
            "Lvl 19: Epic Boon (+1x Lvl 6 SSl)",
            "Lvl 20: Sorcerous Eminence (+1x Lvl 7 SSl)"
    );

    private static final List<String> WARLOCK_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Pact Magic, Pact Boon (1x Lvl 1 SSl)",
            "Lvl 2: Eldritch Invocations, Magical Cunning (+1x Lvl 1 SSl)",
            "Lvl 3: Warlock Subclass (2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat",
            "Lvl 5: Eldritch Invocation (2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature",
            "Lvl 7: Eldritch Invocation (2x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat",
            "Lvl 9: Contact Patron, Eldritch Invocation (2x Lvl 5 SSl)",
            "Lvl 10: Subclass Feature",
            "Lvl 11: Mystic Arcanum (Level 6) (3x Lvl 5 SSl)",
            "Lvl 12: Attribute Increase / Feat, Eldritch Invocation",
            "Lvl 13: Mystic Arcanum (Level 7)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Mystic Arcanum (Level 8), Eldritch Invocation",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Mystic Arcanum (Level 9) (4x Lvl 5 SSl)",
            "Lvl 18: Eldritch Invocation",
            "Lvl 19: Epic Boon",
            "Lvl 20: Eldritch Master"
    );

    private static final List<String> WIZARD_LEVELING_DESCRIPTIONS = List.of(
            "Lvl 1: Spellcasting, Ritual Adept, Arcane Recovery (2x Lvl 1 SSl)",
            "Lvl 2: Scholar (+1x Lvl 1 SSl)",
            "Lvl 3: Wizard Subclass (+1x Lvl 1, 2x Lvl 2 SSl)",
            "Lvl 4: Attribute Increase / Feat (+1x Lvl 2 SSl)",
            "Lvl 5: Memorize Spell (+2x Lvl 3 SSl)",
            "Lvl 6: Subclass Feature (+1x Lvl 3 SSl)",
            "Lvl 7: Spell Slot (+1x Lvl 4 SSl)",
            "Lvl 8: Attribute Increase / Feat (+1x Lvl 4 SSl)",
            "Lvl 9: Spell Slot (+1x Lvl 4, 1x Lvl 5 SSl)",
            "Lvl 10: Subclass Feature (+1x Lvl 5 SSl)",
            "Lvl 11: Spell Slot (+1x Lvl 6 SSl)",
            "Lvl 12: Attribute Increase / Feat",
            "Lvl 13: Spell Slot (+1x Lvl 7 SSl)",
            "Lvl 14: Subclass Feature",
            "Lvl 15: Spell Slot (+1x Lvl 8 SSl)",
            "Lvl 16: Attribute Increase / Feat",
            "Lvl 17: Spell Slot (+1x Lvl 9 SSl)",
            "Lvl 18: Spell Mastery (+1x Lvl 5 SSl)",
            "Lvl 19: Epic Boon (+1x Lvl 6 SSl)",
            "Lvl 20: Signature Spells (+1x Lvl 7 SSl)"
    );

    private static final List<String> BERSERKER_LEVELING_DESCRIPTIONS       = List.of("Frenzy: Extra attack each turn while raging","Mindless Rage: Immune to charm and frightened","Intimidating Presence: Frighten a creature nearby","Retaliation: React to damage with a melee attack");
    private static final List<String> WILD_HEART_LEVELING_DESCRIPTIONS      = List.of("Bestial Soul: Choose a natural weapon type","Animal Speaker: Cast Beast Sense and Speak with Animals","Totemic Attunement: Gain a beast power while raging");
    private static final List<String> WORLD_TREE_LEVELING_DESCRIPTIONS      = List.of("Vitality of the Tree: Gain temp HP on rage start","Branches of the Tree: Teleport allies to your side","Travel Along the Tree: Teleport between locations");
    private static final List<String> ZEALOT_LEVELING_DESCRIPTIONS          = List.of("Divine Fury: Add radiant or necrotic bonus damage","Warrior of the Gods: Easier resurrection at no cost","Fanatical Focus: Reroll failed saves while raging","Zealous Presence: Grant advantage to all nearby allies");
    private static final List<String> COLLEGE_DANCE_LEVELING_DESCRIPTIONS   = List.of("Dazzling Footwork: Gain AC bonus from Charisma","Inspiring Movement: Move an ally when they use inspiration","Tandem Footwork: Grant initiative bonus to all allies");
    private static final List<String> COLLEGE_GLAMOUR_LEVELING_DESCRIPTIONS = List.of("Mantle of Inspiration: Grant temporary HP to allies","Enthralling Performance: Charm up to five creatures","Mantle of Majesty: Command spell as a bonus action free");
    private static final List<String> COLLEGE_SPELLSORE_LEVELING_DESCRIPTIONS    = List.of("Cutting Words: Reduce an enemy attack or ability roll","Additional Magical Secrets: Learn two spells early","Peerless Skill: Add Bardic Inspiration die to your checks");
    private static final List<String> COLLEGE_VALOR_LEVELING_DESCRIPTIONS   = List.of("Combat Inspiration: Inspiration can boost damage or AC","Extra Attack: Attack twice each action","Battle Magic: Make a bonus attack after casting a spell");
    private static final List<String> LIFE_DOMAIN_LEVELING_DESCRIPTIONS     = List.of("Disciple of Life: Healing spells restore extra HP","Preserve Life: Rapidly distribute healing to many allies","Blessed Healer: Heal yourself when you heal others","Supreme Healing: Roll maximum on all healing dice");
    private static final List<String> LIGHT_DOMAIN_LEVELING_DESCRIPTIONS    = List.of("Warding Flare: Impose disadvantage on one attacker","Radiance of the Dawn: Deal radiant damage to all undead","Corona of Light: Sunlight aura causes enemy disadvantage");
    private static final List<String> TRICKERY_DOMAIN_LEVELING_DESCRIPTIONS = List.of("Blessing of the Trickster: Grant stealth advantage","Invoke Duplicity: Create a perfect illusory duplicate","Cloak of Shadows: Turn invisible as an action");
    private static final List<String> WAR_DOMAIN_LEVELING_DESCRIPTIONS      = List.of("War Priest: Make a bonus attack as a bonus action","Guided Strike: Add +10 to your attack roll once","War God's Blessing: Grant +10 to an ally's attack roll");
    private static final List<String> CIRCLE_SPELLSAND_LEVELING_DESCRIPTIONS     = List.of("Natural Recovery: Recover spell slots on short rest","Land's Stride: Move through non-magical plants freely","Nature's Sanctuary: Beasts and plants avoid attacking you");
    private static final List<String> CIRCLE_MOON_LEVELING_DESCRIPTIONS     = List.of("Combat Wild Shape: Use Wild Shape as a bonus action","Elemental Wild Shape: Transform into an elemental","Thousand Forms: Cast Alter Self at will for free");
    private static final List<String> CIRCLE_SEA_LEVELING_DESCRIPTIONS      = List.of("Wrath of the Sea: Surround yourself with crashing waves","Aquatic Affinity: Gain swim speed and water breathing","Stormborn: Gain fly speed during stormy conditions");
    private static final List<String> CIRCLE_STARS_LEVELING_DESCRIPTIONS    = List.of("Star Map: Know Guiding Bolt and navigation skills","Starry Form: Assume a glowing constellation form","Twinkling Constellations: Enhance your starry form further");
    private static final List<String> BATTLE_MASTER_LEVELING_DESCRIPTIONS   = List.of("Superiority Dice: Fuel powerful combat maneuvers","Maneuvers: Choose from many tactical combat options","Know Your Enemy: Study a creature's combat statistics","Relentless: Regain a superiority die on initiative roll");
    private static final List<String> CHAMPION_LEVELING_DESCRIPTIONS        = List.of("Improved Critical: Score a critical hit on a 19 or 20","Remarkable Athlete: Boost athletic and jumping checks","Additional Fighting Style: Learn a second fighting style","Superior Critical: Score a critical hit on 18 through 20");
    private static final List<String> ELDRITCH_KNIGHT_LEVELING_DESCRIPTIONS = List.of("Spellcasting: Cast abjuration and evocation spells","Weapon Bond: Teleport your bonded weapon to your hand","War Magic: Make a bonus attack after casting a cantrip","Eldritch Strike: Weaken enemy saves after you hit them");
    private static final List<String> PSI_WARRIOR_LEVELING_DESCRIPTIONS     = List.of("Psionic Power: Pool of psionic energy dice to spend","Protective Field: Spend a die to reduce incoming damage","Psionic Strike: Deal extra psychic damage on a hit","Telekinetic Movement: Move objects or creatures with mind");
    private static final List<String> WARRIOR_MERCY_LEVELING_DESCRIPTIONS   = List.of("Hand of Healing: Spend ki to heal on a hit","Hand of Harm: Add poison damage to an unarmed strike","Physician's Touch: Cure conditions while healing allies","Flurry of Healing and Harm: Heal and harm during flurry");
    private static final List<String> WARRIOR_SHADOW_LEVELING_DESCRIPTIONS  = List.of("Shadow Arts: Cast darkness and silence using ki","Shadow Step: Teleport between dim light or darkness","Cloak of Shadows: Turn invisible in dim light or dark","Opportunist: React to hit an enemy already struck");
    private static final List<String> WARRIOR_ELEMENTS_LEVELING_DESCRIPTIONS= List.of("Elemental Attunement: Perform minor elemental tricks","Fangs of the Fire Snake: Extend strikes with flame reach","Fist of Four Thunders: Create a shockwave to push foes","Ride the Wind: Gain fly speed for one turn using ki");
    private static final List<String> WARRIOR_OPEN_LEVELING_DESCRIPTIONS    = List.of("Open Hand Technique: Push trip or deny enemy reaction","Wholeness of Body: Heal yourself for three times your HP","Tranquility: Gain sanctuary effect until you attack","Quivering Palm: Set up lethal internal vibrations on foe");
    private static final List<String> OATH_DEVOTION_LEVELING_DESCRIPTIONS   = List.of("Sacred Weapon: Add Charisma modifier to attack rolls","Turn the Unholy: Turn fiends and undead creatures","Aura of Devotion: You and allies immune to charmed","Holy Nimbus: Sunlight aura damages undead nearby");
    private static final List<String> OATH_GLORY_LEVELING_DESCRIPTIONS      = List.of("Inspiring Smite: Distribute temp HP after a divine smite","Peerless Athlete: Advantage on athletics and acrobatics","Aura of Alacrity: Allies gain movement speed near you","Glorious Defense: Add Charisma to an ally's AC as reaction");
    private static final List<String> OATH_ANCIENTS_LEVELING_DESCRIPTIONS   = List.of("Nature's Wrath: Restrain a foe with spectral vines","Turn the Faithless: Turn fey and fiend creatures","Aura of Warding: Resistance to damage from spells","Undying Sentinel: Stay at 1 HP instead of falling once");
    private static final List<String> OATH_VENGEANCE_LEVELING_DESCRIPTIONS  = List.of("Vow of Enmity: Advantage on all attacks vs one foe","Inquisitor's Might: Add radiant or psychic bonus damage","Relentless Avenger: Gain speed after an opportunity attack","Soul of Vengeance: React to hit the target of your vow");
    private static final List<String> BEAST_MASTER_LEVELING_DESCRIPTIONS    = List.of("Primal Companion: Bond with a magical beast companion","Exceptional Training: Command beast as a bonus action","Bestial Fury: Your beast companion attacks twice","Share Spells: Target your beast with self-only spells");
    private static final List<String> FEY_WANDERER_LEVELING_DESCRIPTIONS    = List.of("Dreadful Strikes: Add psychic bonus damage on hits","Fey Wanderer Magic: Access extra spells from the Feywild","Otherworldly Glamour: Add Wisdom to Charisma checks","Beguiling Twist: Redirect charm or fear effects to others");
    private static final List<String> GLOOM_STALKER_LEVELING_DESCRIPTIONS   = List.of("Dread Ambusher: Bonus attack and speed in first round","Umbral Sight: You become invisible to darkvision","Iron Mind: Gain proficiency in Wisdom saving throws","Stalker's Flurry: Make an extra attack when you miss");
    private static final List<String> HUNTER_LEVELING_DESCRIPTIONS          = List.of("Hunter's Prey: Choose Colossus Slayer Horde Breaker or Giant Killer","Defensive Tactics: Choose a passive defensive bonus","Multiattack: Fire multiple projectiles in one action","Superior Hunter's Defense: Evasion or Stand Against the Tide");
    private static final List<String> ARCANE_TRICKSTER_LEVELING_DESCRIPTIONS= List.of("Mage Hand Legerdemain: Enhanced invisible magical hand","Magical Ambush: Foes have disadvantage on saves if hidden","Versatile Trickster: Use Mage Hand to distract enemies","Spell Thief: Steal a spell directly from a caster");
    private static final List<String> ASSASSIN_LEVELING_DESCRIPTIONS        = List.of("Assassinate: Advantage on surprised foes, auto critical","Infiltration Expertise: Create false identities perfectly","Impostor: Perfectly mimic any person you have studied","Death Strike: Double damage against a surprised target");
    private static final List<String> SOULKNIFE_LEVELING_DESCRIPTIONS       = List.of("Psychic Blades: Materialize blades from your own mind","Soul Blades: Spend psionic dice for extra blade effects","Psychic Veil: Turn invisible using psionic energy","Rend Mind: Stun a target with a Psychic Blades attack");
    private static final List<String> THIEF_LEVELING_DESCRIPTIONS           = List.of("Fast Hands: Use an object or tool as a bonus action","Second-Story Work: Climb faster and jump farther","Supreme Sneak: Advantage on stealth at half speed","Use Magic Device: Ignore class restrictions on magic items");
    private static final List<String> DRACONIC_LEVELING_DESCRIPTIONS        = List.of("Draconic Resilience: Natural AC bonus and extra HP","Elemental Affinity: Bonus damage to your dragon element","Dragon Wings: Sprout real wings and gain fly speed","Draconic Presence: Fear or charm creatures in your aura");
    private static final List<String> WILD_MAGIC_LEVELING_DESCRIPTIONS      = List.of("Wild Magic Surge: Random magical effect after casting","Tides of Chaos: Gain advantage then trigger a surge","Bend Luck: Spend points to alter any creature's rolls","Controlled Chaos: Choose from two surge table results");
    private static final List<String> ABERRANT_LEVELING_DESCRIPTIONS        = List.of("Psionic Spells: Access extra spells from far realms","Telepathic Speech: Communicate telepathically over distance","Psionic Sorcery: Cast spells without material components","Psychic Defenses: Resistance to psychic damage");
    private static final List<String> CLOCKWORK_LEVELING_DESCRIPTIONS       = List.of("Clockwork Magic: Restore cosmic balance with spells","Restore Balance: Cancel advantage or disadvantage effects","Bastion of Law: Absorb incoming damage to protect an ally","Trance of Order: Treat any roll below 9 as a 10");
    private static final List<String> ARCHFEY_LEVELING_DESCRIPTIONS         = List.of("Fey Presence: Charm or frighten creatures in a cube","Misty Escape: Turn invisible and teleport after taking damage","Beguiling Defenses: Immune to charm and reflect it back","Dark Delirium: Trap a creature in an illusory dream");
    private static final List<String> FIEND_LEVELING_DESCRIPTIONS           = List.of("Dark One's Blessing: Gain temp HP on killing a creature","Dark One's Own Luck: Add d10 to ability checks or saves","Fiendish Resilience: Choose damage resistance each rest","Hurl Through Hell: Banish then deal massive psychic damage");
    private static final List<String> GREAT_OLD_ONE_LEVELING_DESCRIPTIONS   = List.of("Awakened Mind: Speak telepathically to any creature","Entropic Ward: Impose disadvantage on an attacker","Thought Shield: Resistance to psychic and thought reading","Create Thrall: Charm a creature indefinitely with a touch");
    private static final List<String> CELESTIAL_LEVELING_DESCRIPTIONS       = List.of("Healing Light: Pool of d6 dice for healing creatures","Radiant Soul: Fly speed and add Charisma to radiant damage","Celestial Resilience: Grant temp HP to you and nearby allies","Searing Vengeance: Rise from 0 HP with a radiant explosion");
    private static final List<String> ABJURER_LEVELING_DESCRIPTIONS         = List.of("Arcane Ward: Create a magical shield absorbing damage","Projected Ward: Share your arcane ward with a nearby ally","Improved Abjuration: Bonus to abjuration ability checks","Spell Resistance: Advantage on saves and resistance vs spells");
    private static final List<String> DIVINER_LEVELING_DESCRIPTIONS         = List.of("Portent: Replace any roll with your prophecy dice rolls","Expert Divination: Recover spell slots when divining","The Third Eye: Gain truesight ethereal sight or darkvision","Greater Portent: Roll three prophecy dice each long rest");
    private static final List<String> EVOKER_LEVELING_DESCRIPTIONS          = List.of("Sculpt Spells: Exclude chosen creatures from your AoE","Potent Cantrip: Cantrips deal half damage even on saves","Empowered Evocation: Add Intelligence to evocation damage","Overchannel: Maximize spell damage but take necrotic backlash");
    private static final List<String> ILLUSIONIST_LEVELING_DESCRIPTIONS     = List.of("Improved Illusions: Create sounds within illusion spells","Malleable Illusions: Change existing illusions as an action","Illusory Self: An illusory duplicate blocks one incoming attack","Illusory Reality: Make one component of your illusion real");

    static {
        // BARBARIAN (+2 STR, +1 CON)
        CLASSES.add(new ClassDefinition("barbarian", "Barbarian", "A fierce warrior driven by primal rage.", CLASS_HEALTH_BARBARIAN, BARBARIAN_ATTRIBUTES, BARBARIAN_LEVELING_DESCRIPTIONS, BARBARIAN_ITEMS, PROFICIENCYS_BARB, NONE_S, NONE_P, EMPTY_SPELLS, false, ""));
        SUBCLASSES.add(new SubclassDefinition("berserker", "barbarian", "Path of the Berserker", "Channels rage into devastating attacks.", BERSERKER_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARB));
        SUBCLASSES.add(new SubclassDefinition("wild_heart", "barbarian", "Path of the Wild Heart", "Draws on the spirits of beasts.", WILD_HEART_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARB));
        SUBCLASSES.add(new SubclassDefinition("world_tree", "barbarian", "Path of the World Tree", "Taps into the cosmic power of the World Tree.", WORLD_TREE_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARB));
        SUBCLASSES.add(new SubclassDefinition("zealot", "barbarian", "Path of the Zealot", "Infuses rage with divine fury.", ZEALOT_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARB));

        // BARD (+2 CHA, +1 DEX) - Spellcasting: Charisma
        CLASSES.add(new ClassDefinition("bard", "Bard", "A master of music, magic, and inspiration.", CLASS_HEALTH_BARD, BARD_ATTRIBUTES, BARD_LEVELING_DESCRIPTIONS, BARD_ITEMS, PROFICIENCYS_BARD, BAR_S, BAR_MAX_SPELLS, BARD_SPELLS, true, "Charisma"));
        SUBCLASSES.add(new SubclassDefinition("college_dance", "bard", "College of Dance", "Uses movement as a spellcasting focus.", COLLEGE_DANCE_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_glamour", "bard", "College of Glamour", "Weaves enchantments of fey-touched beauty.", COLLEGE_GLAMOUR_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_lore", "bard", "College of Lore", "Collects knowledge from every discipline.", COLLEGE_SPELLSORE_LEVELING_DESCRIPTIONS, PROFICIENCYS_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_valor", "bard", "College of Valor", "Combines combat with bardic inspiration.", COLLEGE_VALOR_LEVELING_DESCRIPTIONS, PROFICIENCYS_VALOR));

        // CLERIC (+2 WIS, +1 STR) - Spellcasting: Wisdom
        CLASSES.add(new ClassDefinition("cleric", "Cleric", "A divine spellcaster empowered by their deity.", CLASS_HEALTH_CLERIC, CLERIC_ATTRIBUTES, CLERIC_LEVELING_DESCRIPTIONS, CLERIC_ITEMS, PROFICIENCYS_CLER, CLE_S, CLE_MAX_SPELLS, CLERIC_SPELLS, true, "Wisdom"));
        SUBCLASSES.add(new SubclassDefinition("life_domain", "cleric", "Life Domain", "Devoted to preserving and restoring life.", LIFE_DOMAIN_LEVELING_DESCRIPTIONS, PROFICIENCYS_CLER));
        SUBCLASSES.add(new SubclassDefinition("light_domain", "cleric", "Light Domain", "Channels the power of fire and radiance.", LIGHT_DOMAIN_LEVELING_DESCRIPTIONS, PROFICIENCYS_CLER));
        SUBCLASSES.add(new SubclassDefinition("trickery_domain", "cleric", "Trickery Domain", "A deity of deception blesses this cleric.", TRICKERY_DOMAIN_LEVELING_DESCRIPTIONS, PROFICIENCYS_CLER));
        SUBCLASSES.add(new SubclassDefinition("war_domain", "cleric", "War Domain", "Blessed for combat by a god of war.", WAR_DOMAIN_LEVELING_DESCRIPTIONS, PROFICIENCYS_WAR_D));

        // DRUID (+2 WIS, +1 CON) - Spellcasting: Wisdom
        CLASSES.add(new ClassDefinition("druid", "Druid", "A guardian of nature and its cycles.", CLASS_HEALTH_DRUID, DRUID_ATTRIBUTES, DRUID_LEVELING_DESCRIPTIONS, DRUID_ITEMS, PROFICIENCYS_DRUI, DRU_S, DRU_MAX_SPELLS, DRUID_SPELLS, true, "Wisdom"));
        SUBCLASSES.add(new SubclassDefinition("circle_land", "druid", "Circle of the Land", "Draws magical power from the terrain.", CIRCLE_SPELLSAND_LEVELING_DESCRIPTIONS, PROFICIENCYS_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_moon", "druid", "Circle of the Moon", "Shapeshifts into powerful beasts.", CIRCLE_MOON_LEVELING_DESCRIPTIONS, PROFICIENCYS_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_sea", "druid", "Circle of the Sea", "Commands the power of wind and wave.", CIRCLE_SEA_LEVELING_DESCRIPTIONS, PROFICIENCYS_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_stars", "druid", "Circle of the Stars", "Reads power from the constellations.", CIRCLE_STARS_LEVELING_DESCRIPTIONS, PROFICIENCYS_DRUI));

        // FIGHTER (+2 STR, +1 CON)
        CLASSES.add(new ClassDefinition("fighter", "Fighter", "A versatile master of weaponry and armor.", CLASS_HEALTH_FIGHTER, FIGHTER_ATTRIBUTES, FIGHTER_LEVELING_DESCRIPTIONS, FIGHTER_ITEMS, PROFICIENCYS_FIGH, NONE_S, NONE_P, EMPTY_SPELLS, false, ""));
        SUBCLASSES.add(new SubclassDefinition("battle_master", "fighter", "Battle Master", "Uses cunning maneuvers to control the battlefield.", BATTLE_MASTER_LEVELING_DESCRIPTIONS, PROFICIENCYS_FIGH));
        SUBCLASSES.add(new SubclassDefinition("champion", "fighter", "Champion", "An elite warrior of pure physical excellence.", CHAMPION_LEVELING_DESCRIPTIONS, PROFICIENCYS_FIGH));
        SUBCLASSES.add(new SubclassDefinition("eldritch_knight", "fighter", "Eldritch Knight", "Combines martial training with arcane magic.", ELDRITCH_KNIGHT_LEVELING_DESCRIPTIONS, PROFICIENCYS_ELDR));
        SUBCLASSES.add(new SubclassDefinition("psi_warrior", "fighter", "Psi Warrior", "Augments combat with psionic power.", PSI_WARRIOR_LEVELING_DESCRIPTIONS, PROFICIENCYS_FIGH));

        // MONK (+2 DEX, +1 WIS)
        CLASSES.add(new ClassDefinition("monk", "Monk", "A master of martial arts and ki energy.", CLASS_HEALTH_MONK, MONK_ATTRIBUTES, MONK_LEVELING_DESCRIPTIONS, MONK_ITEMS, PROFICIENCYS_MONK, NONE_S, NONE_P, EMPTY_SPELLS, false, ""));
        SUBCLASSES.add(new SubclassDefinition("warrior_mercy", "monk", "Warrior of Mercy", "Heals allies and debilitates foes.", WARRIOR_MERCY_LEVELING_DESCRIPTIONS, PROFICIENCYS_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_shadow", "monk", "Warrior of Shadow", "Blends combat and shadow magic.", WARRIOR_SHADOW_LEVELING_DESCRIPTIONS, PROFICIENCYS_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_elements", "monk", "Warrior of the Elements", "Channels elemental energy through strikes.", WARRIOR_ELEMENTS_LEVELING_DESCRIPTIONS, PROFICIENCYS_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_open_hand", "monk", "Warrior of the Open Hand", "The purest expression of martial discipline.", WARRIOR_OPEN_LEVELING_DESCRIPTIONS, PROFICIENCYS_MONK));

        // PALADIN (+1 STR, +2 CHA) - Spellcasting: Charisma
        CLASSES.add(new ClassDefinition("paladin", "Paladin", "A holy warrior bound by a sacred oath.", CLASS_HEALTH_PALADIN, PALADIN_ATTRIBUTES, PALADIN_LEVELING_DESCRIPTIONS, PALADIN_ITEMS, PROFICIENCYS_PALA, PAL_S, PAL_MAX_SPELLS, PALADIN_SPELLS, true, "Charisma"));
        SUBCLASSES.add(new SubclassDefinition("oath_devotion", "paladin", "Oath of Devotion", "Upholds the ideal of a shining knight.", OATH_DEVOTION_LEVELING_DESCRIPTIONS, PROFICIENCYS_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_glory", "paladin", "Oath of Glory", "Strives to reach the heights of heroic legend.", OATH_GLORY_LEVELING_DESCRIPTIONS, PROFICIENCYS_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_ancients", "paladin", "Oath of the Ancients", "Preserves the light of life against darkness.", OATH_ANCIENTS_LEVELING_DESCRIPTIONS, PROFICIENCYS_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_vengeance", "paladin", "Oath of Vengeance", "Pursues and punishes evildoers relentlessly.", OATH_VENGEANCE_LEVELING_DESCRIPTIONS, PROFICIENCYS_PALA));

        // RANGER (+2 DEX, +1 WIS) - Spellcasting: Wisdom
        CLASSES.add(new ClassDefinition("ranger", "Ranger", "A skilled hunter of the wilderness.", CLASS_HEALTH_RANGER, RANGER_ATTRIBUTES, RANGER_LEVELING_DESCRIPTIONS, RANGER_ITEMS, PROFICIENCYS_RANG, RAN_S, RAN_MAX_SPELLS, RANGER_SPELLS, true, "Wisdom"));
        SUBCLASSES.add(new SubclassDefinition("beast_master", "ranger", "Beast Master", "Forms a deep bond with an animal companion.", BEAST_MASTER_LEVELING_DESCRIPTIONS, PROFICIENCYS_RANG));
        SUBCLASSES.add(new SubclassDefinition("fey_wanderer", "ranger", "Fey Wanderer", "Channels the magic of the Feywild.", FEY_WANDERER_LEVELING_DESCRIPTIONS, PROFICIENCYS_RANG));
        SUBCLASSES.add(new SubclassDefinition("gloom_stalker", "ranger", "Gloom Stalker", "Thrives in the darkest places of the world.", GLOOM_STALKER_LEVELING_DESCRIPTIONS, PROFICIENCYS_RANG));
        SUBCLASSES.add(new SubclassDefinition("hunter", "ranger", "Hunter", "Specializes in tracking and slaying prey.", HUNTER_LEVELING_DESCRIPTIONS, PROFICIENCYS_RANG));

        // ROGUE (+2 DEX, +1 CON)
        CLASSES.add(new ClassDefinition("rogue", "Rogue", "A cunning trickster who strikes from shadows.", CLASS_HEALTH_ROGUE, ROGUE_ATTRIBUTES, ROGUE_LEVELING_DESCRIPTIONS, ROGUE_ITEMS, PROFICIENCYS_ROGU, NONE_S, NONE_P, EMPTY_SPELLS, false, ""));
        SUBCLASSES.add(new SubclassDefinition("arcane_trickster", "rogue", "Arcane Trickster", "Enhances roguish talents with illusion magic.", ARCANE_TRICKSTER_LEVELING_DESCRIPTIONS, PROFICIENCYS_ROGU));
        SUBCLASSES.add(new SubclassDefinition("assassin", "rogue", "Assassin", "Trained to eliminate targets swiftly and silently.", ASSASSIN_LEVELING_DESCRIPTIONS, PROFICIENCYS_ROGU));
        SUBCLASSES.add(new SubclassDefinition("soulknife", "rogue", "Soulknife", "Focuses psionic energy into blade-like constructs.", SOULKNIFE_LEVELING_DESCRIPTIONS, PROFICIENCYS_ROGU));
        SUBCLASSES.add(new SubclassDefinition("thief", "rogue", "Thief", "Master of speed, stealth and sleight of hand.", THIEF_LEVELING_DESCRIPTIONS, PROFICIENCYS_ROGU));

        // SORCERER (+2 CHA, +1 CON) - Spellcasting: Charisma
        CLASSES.add(new ClassDefinition("sorcerer", "Sorcerer", "Born with innate magical power.", CLASS_HEALTH_SORCERER, SORCERER_ATTRIBUTES, SORCERER_LEVELING_DESCRIPTIONS, SORCERER_ITEMS, PROFICIENCYS_SORC, SOR_S, SOR_MAX_SPELLS, SORCERER_SPELLS, true, "Charisma"));
        SUBCLASSES.add(new SubclassDefinition("draconic_sorcery", "sorcerer", "Draconic Sorcery", "Dragon blood fuels your raw magical might.", DRACONIC_LEVELING_DESCRIPTIONS, PROFICIENCYS_SORC));
        SUBCLASSES.add(new SubclassDefinition("wild_magic", "sorcerer", "Wild Magic", "Magic surges with chaotic unpredictable power.", WILD_MAGIC_LEVELING_DESCRIPTIONS, PROFICIENCYS_SORC));
        SUBCLASSES.add(new SubclassDefinition("aberrant_sorcery", "sorcerer", "Aberrant Sorcery", "Touched by alien forces from beyond reality.", ABERRANT_LEVELING_DESCRIPTIONS, PROFICIENCYS_SORC));
        SUBCLASSES.add(new SubclassDefinition("clockwork_sorcery", "sorcerer", "Clockwork Sorcery", "Channels the ordered magic of clockwork planes.", CLOCKWORK_LEVELING_DESCRIPTIONS, PROFICIENCYS_SORC));

        // WARLOCK (+2 CHA, +1 WIS) - Spellcasting: Charisma
        CLASSES.add(new ClassDefinition("warlock", "Warlock", "Gains power through a pact with a dark entity.", CLASS_HEALTH_WARLOCK, WARLOCK_ATTRIBUTES, WARLOCK_LEVELING_DESCRIPTIONS, WARLOCK_ITEMS, PROFICIENCYS_WARL, WAR_S, WAR_MAX_SPELLS, WARLOCK_SPELLS, true, "Charisma"));
        SUBCLASSES.add(new SubclassDefinition("archfey_patron", "warlock", "Archfey Patron", "Pact with a powerful lord of the Feywild.", ARCHFEY_LEVELING_DESCRIPTIONS, PROFICIENCYS_WARL));
        SUBCLASSES.add(new SubclassDefinition("fiend_patron", "warlock", "Fiend Patron", "Pact made with a powerful devil for raw power.", FIEND_LEVELING_DESCRIPTIONS, PROFICIENCYS_WARL));
        SUBCLASSES.add(new SubclassDefinition("great_old_one", "warlock", "Great Old One Patron", "Bound to an unknowable cosmic entity.", GREAT_OLD_ONE_LEVELING_DESCRIPTIONS, PROFICIENCYS_WARL));
        SUBCLASSES.add(new SubclassDefinition("celestial_patron", "warlock", "Celestial Patron", "Pact with a being of the Upper Planes.", CELESTIAL_LEVELING_DESCRIPTIONS, PROFICIENCYS_WARL));

        // WIZARD (+2 INT, +1 CON) - Spellcasting: Intelligence
        CLASSES.add(new ClassDefinition("wizard", "Wizard", "Master of studied spellcasting.", CLASS_HEALTH_WIZARD, WIZARD_ATTRIBUTES, WIZARD_LEVELING_DESCRIPTIONS, WIZARD_ITEMS, PROFICIENCYS_WIZA, WIZ_S, WIZ_MAX_SPELLS, WIZARD_SPELLS, true, "Intelligence"));
        SUBCLASSES.add(new SubclassDefinition("abjurer", "wizard", "Abjurer", "Specializes in protective and warding magic.", ABJURER_LEVELING_DESCRIPTIONS, PROFICIENCYS_WIZA));
        SUBCLASSES.add(new SubclassDefinition("diviner", "wizard", "Diviner", "Peers into the past, present and future.", DIVINER_LEVELING_DESCRIPTIONS, PROFICIENCYS_WIZA));
        SUBCLASSES.add(new SubclassDefinition("evoker", "wizard", "Evoker", "Channels raw elemental forces into destruction.", EVOKER_LEVELING_DESCRIPTIONS, PROFICIENCYS_WIZA));
        SUBCLASSES.add(new SubclassDefinition("illusionist", "wizard", "Illusionist", "Bends reality through weaves of illusion.", ILLUSIONIST_LEVELING_DESCRIPTIONS, PROFICIENCYS_WIZA));
    }

    public static List<SubclassDefinition> getSubclassesFor(String parentId) {
        List<SubclassDefinition> out = new ArrayList<>();
        for (SubclassDefinition s : SUBCLASSES) if (s.getParentClassId().equals(parentId)) out.add(s);
        return out;
    }
    public static ClassDefinition getClass(String id) { for (ClassDefinition c : CLASSES) if (c.getId().equals(id)) return c; return null; }
    public static SubclassDefinition getSubclass(String id) { for (SubclassDefinition s : SUBCLASSES) if (s.getId().equals(id)) return s; return null; }
}
