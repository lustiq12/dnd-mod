package net.luderspieler.dnd.classes;

import java.util.*;

public class RaceRegistry {

    public static final List<RaceDefinition>    RACES    = new ArrayList<>();
    public static final List<SubraceDefinition> SUBRACES = new ArrayList<>();

    private static Map<String,Double> attrs(double hp,double dmg,double armor,double spd,double aspd,double luck){
        Map<String,Double> m=new LinkedHashMap<>();
        m.put("Max Health",hp);m.put("Attack Damage",dmg);m.put("Armor",armor);
        m.put("Movement Speed",spd);m.put("Attack Speed",aspd);m.put("Luck",luck);return m;}

    // ── ATTRS ──
    private static final Map<String,Double> AASIMAR_ATTRS    = attrs(+8,  +2, +2,   0,     0,   +4);
    private static final Map<String,Double> DRAGONBORN_ATTRS = attrs(+6,  +4, +3,   0,     0,    0);
    private static final Map<String,Double> DWARF_ATTRS      = attrs(+10,  0, +4,  -0.02,  0,    0);
    private static final Map<String,Double> ELF_ATTRS        = attrs(-2,   0, -1,  +0.05, +0.5, +3);
    private static final Map<String,Double> GNOME_ATTRS      = attrs(-4,  -1,  0,  +0.02, +0.3, +5);
    private static final Map<String,Double> GOLIATH_ATTRS    = attrs(+12, +4, +3,  +0.01,  0,    0);
    private static final Map<String,Double> HALFLING_ATTRS   = attrs(-2,  -1,  0,  +0.03, +0.3, +6);
    private static final Map<String,Double> HUMAN_ATTRS      = attrs(+4,  +1, +1,  +0.01,  0,  +2);
    private static final Map<String,Double> ORC_ATTRS        = attrs(+8,  +6, +2,  +0.02,  0,    0);
    private static final Map<String,Double> TIEFLING_ATTRS   = attrs(-2,  +4, +1,  +0.02,  0,  +3);

    // ── PROFICIENCIES ──
    // Races grant mostly no extra armor/weapon proficiencies beyond class
    // Exceptions (as per official D&D 5e/2024):
    private static final String P_NONE       = "";
    private static final String P_DWARF      = "war_weapons"; // Battleaxe, handaxe, throwing hammer, warhammer
    private static final String P_MOUNTAIN_D = "light_armor,medium_armor"; // Mountain Dwarf bonus
    private static final String P_GOLIATH    = ""; // No extra prof

    // ── ABILITY LINES (main races) ──
    private static final List<String> AASIMAR_AB    = List.of("Darkvision: See in darkness up to 60 feet","Celestial Resistance: Resistant to necrotic and radiant","Healing Hands: Touch to heal HP equal to your level","Light Bearer: Know the Light cantrip for free");
    private static final List<String> DRAGONBORN_AB = List.of("Draconic Ancestry: Choose a dragon type and element","Breath Weapon: Exhale destructive elemental energy","Damage Resistance: Resistance to your ancestry element","Darkvision: See in darkness up to 60 feet");
    private static final List<String> DWARF_AB      = List.of("Darkvision: See in darkness up to 60 feet","Dwarven Resilience: Resistant to poison damage and effects","Dwarven Toughness: HP maximum increases by one per level","Stonecunning: Tremorsense along stone floors and walls");
    private static final List<String> ELF_AB        = List.of("Darkvision: See in darkness up to 60 feet","Fey Ancestry: Advantage on saves against charm effects","Keen Senses: Proficiency in the Perception skill","Trance: Rest fully in only four hours of meditation");
    private static final List<String> GNOME_AB      = List.of("Darkvision: See in darkness up to 60 feet","Gnomish Cunning: Advantage on INT WIS CHA magic saves","Gnomish Lineage: Choose forest or rock gnome traits");
    private static final List<String> GOLIATH_AB    = List.of("Large Form: Temporarily become Large size once per day","Powerful Build: Count as one size larger for carry weight","Stone's Endurance: Reduce damage taken as a reaction","Natural Athlete: Proficiency in the Athletics skill");
    private static final List<String> HALFLING_AB   = List.of("Lucky: Reroll 1s on attack rolls ability checks and saves","Brave: Advantage on saving throws against frightened","Halfling Nimbleness: Move through larger creatures' spaces","Naturally Stealthy: Hide behind larger creatures");
    private static final List<String> HUMAN_AB      = List.of("Resourceful: Gain Heroic Inspiration after each long rest","Skilled: Proficiency in any three skills of your choice","Versatile: Gain an Origin feat at character creation");
    private static final List<String> ORC_AB        = List.of("Darkvision: See in darkness up to 120 feet","Adrenaline Rush: Dash as bonus action and gain temp HP","Relentless Endurance: Drop to 1 HP instead of 0 once","Powerful Build: Count as one size larger for carry weight");
    private static final List<String> TIEFLING_AB   = List.of("Darkvision: See in darkness up to 60 feet","Hellish Resistance: Resistance to fire damage","Infernal Legacy: Know Thaumaturgy and Hellish Rebuke","Fiendish Lineage: Choose abyssal chthonic or infernal");

    // ── ABILITY LINES (subraces) ──
    private static final List<String> CELESTIAL_REVELATION_AB  = List.of("Healing Radiance: Heal nearby allies as bonus action","Radiant Consumption: Emit blinding light and deal AoE damage","Necrotic Shroud: Frighten nearby creatures with dark wings");
    private static final List<String> CHROMATIC_DRAGONBORN_AB  = List.of("Chromatic Warding: Immunity to your ancestry damage type","Chromatic Breath: Larger area breath weapon option","Scales of the Dragon: Natural armor bonus from chromatic scale");
    private static final List<String> METALLIC_DRAGONBORN_AB   = List.of("Metallic Breath Weapon: Second breath with debuff effect","Draconic Flight: Sprout wings and fly once per long rest","Scales of the Dragon: Natural armor from metallic scale");
    private static final List<String> GEM_DRAGONBORN_AB        = List.of("Psionic Mind: Telepathic communication at 30 feet","Gem Flight: Fly speed equal to your walking speed briefly","Scales of the Dragon: Psionic armor from gem scale");
    private static final List<String> HILL_DWARF_AB            = List.of("Dwarven Toughness: HP maximum increases by one per level","Wisdom of Stone: Proficiency in History and Insight");
    private static final List<String> MOUNTAIN_DWARF_AB        = List.of("Dwarven Armor Training: Proficiency with light and medium armor","Mountain Born: Resistant to cold, native to high altitude");
    private static final List<String> DROW_AB                  = List.of("Superior Darkvision: See in darkness up to 120 feet","Sunlight Sensitivity: Disadvantage in direct sunlight","Drow Magic: Know Dancing Lights Faerie Fire and Darkness");
    private static final List<String> HIGH_ELF_AB              = List.of("Cantrip: Know one wizard cantrip of your choice","Extra Language: Speak read and write one extra language");
    private static final List<String> WOOD_ELF_AB              = List.of("Fleet of Foot: Walking speed increases to 35 feet","Mask of the Wild: Hide when lightly obscured by nature");
    private static final List<String> FOREST_GNOME_AB          = List.of("Natural Illusionist: Know Minor Illusion cantrip for free","Speak with Small Beasts: Communicate with small animals");
    private static final List<String> ROCK_GNOME_AB            = List.of("Artificer's Lore: Bonus to History checks about magic items","Tinker: Construct tiny clockwork devices with your tools");
    private static final List<String> CLOUD_GIANT_AB           = List.of("Cloud's Jaunt: Teleport up to 30 feet as bonus action","Mist Form: Partially become mist to resist one attack");
    private static final List<String> FIRE_GIANT_AB            = List.of("Fire's Burn: Deal extra fire damage on a hit once per turn","Flame Stride: Leave fire trail behind when you Dash");
    private static final List<String> FROST_GIANT_AB           = List.of("Frost's Chill: Reduce enemy speed on a hit once per turn","Ice Walk: Move across icy surfaces without penalty");
    private static final List<String> HILL_GIANT_AB            = List.of("Hill's Tumble: Knock a large or smaller creature prone on hit","Enduring Stone: Advantage on Constitution saving throws");
    private static final List<String> STONE_GIANT_AB           = List.of("Stone's Endurance: Reduce incoming damage as a reaction","Earthen Grip: Restrain a creature by summoning stone hands");
    private static final List<String> STORM_GIANT_AB           = List.of("Storm's Thunder: Deal thunder damage after being hit","Lightning Strike: Cast Lightning Bolt once per long rest");
    private static final List<String> LIGHTFOOT_AB             = List.of("Naturally Stealthy: Hide behind creatures larger than you","Lightfoot Luck: Reroll one ability check per short rest");
    private static final List<String> STOUT_AB                 = List.of("Stout Resilience: Advantage against poison saves and resistance","Dwarven Toughness: Gain one additional HP per level");
    private static final List<String> VERSATILE_HUMAN_AB       = List.of("Bonus Feat: Gain one additional Origin feat","Bonus Skill: Gain proficiency in one additional skill","Adaptable: Once per rest swap one prepared skill proficiency");
    private static final List<String> ADRENALINE_RUSH_AB       = List.of("Rush: When you use Adrenaline Rush gain extra temp HP","Surge: Gain advantage on next attack after using Rush","Warrior's Fortitude: Reduce exhaustion level once per day");
    private static final List<String> ABYSSAL_TIEFLING_AB      = List.of("Abyssal Arcana: Randomly gain access to a new spell daily","Spawn of Chaos: Once per day cast a random 1st level spell");
    private static final List<String> CHTHONIC_TIEFLING_AB     = List.of("Bleakness of the Dead: Know Spare the Dying and Inflict Wounds","Undying Soul: When reduced to 0 HP stay at 1 HP once per rest");
    private static final List<String> INFERNAL_TIEFLING_AB     = List.of("Legacy of Avernus: Know Hellish Rebuke and Darkness spells","Infernal Constitution: Resistant to cold fire and poison");

    static {
        // No starterItems — items come from class only
        RACES.add(new RaceDefinition("aasimar",    "Aasimar",    "Touched by the divine, born to serve the light.",   AASIMAR_ATTRS,    AASIMAR_AB,    P_NONE));
        SUBRACES.add(new SubraceDefinition("celestial_revelation","aasimar",   "Celestial Revelation","Unleashes the full power of divine heritage.",   CELESTIAL_REVELATION_AB, P_NONE));

        RACES.add(new RaceDefinition("dragonborn", "Dragonborn", "Proud dragon-blooded warriors.",                    DRAGONBORN_ATTRS, DRAGONBORN_AB, P_NONE));
        SUBRACES.add(new SubraceDefinition("chromatic_dragonborn","dragonborn","Chromatic Dragonborn","Descended from the chromatic dragons.",          CHROMATIC_DRAGONBORN_AB, P_NONE));
        SUBRACES.add(new SubraceDefinition("metallic_dragonborn", "dragonborn","Metallic Dragonborn", "Noble lineage of the metallic dragons.",         METALLIC_DRAGONBORN_AB,  P_NONE));
        SUBRACES.add(new SubraceDefinition("gem_dragonborn",      "dragonborn","Gem Dragonborn",      "Descended from the rare gem dragons.",           GEM_DRAGONBORN_AB,       P_NONE));

        RACES.add(new RaceDefinition("dwarf",      "Dwarf",      "Stout and resilient, masters of stone and steel.",  DWARF_ATTRS,      DWARF_AB,      P_DWARF));
        SUBRACES.add(new SubraceDefinition("hill_dwarf",          "dwarf",     "Hill Dwarf",          "Hardy and wise, blessed with great vitality.",   HILL_DWARF_AB,           P_DWARF));
        SUBRACES.add(new SubraceDefinition("mountain_dwarf",      "dwarf",     "Mountain Dwarf",      "Trained for war, clad in the finest armor.",     MOUNTAIN_DWARF_AB,       P_MOUNTAIN_D));

        RACES.add(new RaceDefinition("elf",        "Elf",        "Ancient, graceful and deeply tied to magic.",       ELF_ATTRS,        ELF_AB,        P_NONE));
        SUBRACES.add(new SubraceDefinition("drow",                "elf",       "Drow",                "Dark elves from the depths of the Underdark.",   DROW_AB,                 P_NONE));
        SUBRACES.add(new SubraceDefinition("high_elf",            "elf",       "High Elf",            "Devoted to magic and lore of ancient times.",    HIGH_ELF_AB,             P_NONE));
        SUBRACES.add(new SubraceDefinition("wood_elf",            "elf",       "Wood Elf",            "Swift hunters of the deep forest.",              WOOD_ELF_AB,             P_NONE));

        RACES.add(new RaceDefinition("gnome",      "Gnome",      "Curious and inventive, full of wonder.",            GNOME_ATTRS,      GNOME_AB,      P_NONE));
        SUBRACES.add(new SubraceDefinition("forest_gnome",        "gnome",     "Forest Gnome",        "Friends of animals and nature's secrets.",       FOREST_GNOME_AB,         P_NONE));
        SUBRACES.add(new SubraceDefinition("rock_gnome",          "gnome",     "Rock Gnome",          "Tinkerers and craftsmen of the earth.",          ROCK_GNOME_AB,           P_NONE));

        RACES.add(new RaceDefinition("goliath",    "Goliath",    "Towering warriors born of the mountains.",          GOLIATH_ATTRS,    GOLIATH_AB,    P_NONE));
        SUBRACES.add(new SubraceDefinition("cloud_giant",         "goliath",   "Cloud Giant Ancestry","Born of the sky, blessed with grace and wind.",  CLOUD_GIANT_AB,          P_NONE));
        SUBRACES.add(new SubraceDefinition("fire_giant",          "goliath",   "Fire Giant Ancestry", "Forged in flame, relentless in battle.",         FIRE_GIANT_AB,           P_NONE));
        SUBRACES.add(new SubraceDefinition("frost_giant",         "goliath",   "Frost Giant Ancestry","Born of the frozen north, unyielding as ice.",   FROST_GIANT_AB,          P_NONE));
        SUBRACES.add(new SubraceDefinition("hill_giant",          "goliath",   "Hill Giant Ancestry", "Massive and enduring, born to weather any storm.",HILL_GIANT_AB,          P_NONE));
        SUBRACES.add(new SubraceDefinition("stone_giant",         "goliath",   "Stone Giant Ancestry","Still as stone, armored like the earth itself.", STONE_GIANT_AB,          P_NONE));
        SUBRACES.add(new SubraceDefinition("storm_giant",         "goliath",   "Storm Giant Ancestry","Commands the power of thunder and lightning.",   STORM_GIANT_AB,          P_NONE));

        RACES.add(new RaceDefinition("halfling",   "Halfling",   "Small but extraordinarily lucky.",                  HALFLING_ATTRS,   HALFLING_AB,   P_NONE));
        SUBRACES.add(new SubraceDefinition("lightfoot",           "halfling",  "Lightfoot Halfling",  "Nimble and stealthy, with a natural silver tongue.",LIGHTFOOT_AB,         P_NONE));
        SUBRACES.add(new SubraceDefinition("stout_halfling",      "halfling",  "Stout Halfling",      "Tougher than they look, with dwarven blood.",    STOUT_AB,                P_NONE));

        RACES.add(new RaceDefinition("human",      "Human",      "Adaptable and ambitious, found everywhere.",        HUMAN_ATTRS,      HUMAN_AB,      P_NONE));
        SUBRACES.add(new SubraceDefinition("versatile_human",     "human",     "Versatile Human",     "Masters of adaptability, excelling in any field.",VERSATILE_HUMAN_AB,     P_NONE));

        RACES.add(new RaceDefinition("orc",        "Orc",        "Powerful and fierce warriors of the wilds.",        ORC_ATTRS,        ORC_AB,        P_NONE));
        SUBRACES.add(new SubraceDefinition("adrenaline_rush",     "orc",       "Adrenaline Rush",     "Pushes beyond limits in the heat of battle.",    ADRENALINE_RUSH_AB,      P_NONE));

        RACES.add(new RaceDefinition("tiefling",   "Tiefling",   "Bearing infernal heritage, often mistrusted.",      TIEFLING_ATTRS,   TIEFLING_AB,   P_NONE));
        SUBRACES.add(new SubraceDefinition("abyssal_tiefling",    "tiefling",  "Abyssal Tiefling",    "Touched by demon blood, chaotic and swift.",     ABYSSAL_TIEFLING_AB,     P_NONE));
        SUBRACES.add(new SubraceDefinition("chthonic_tiefling",   "tiefling",  "Chthonic Tiefling",   "Marked by the deepest underworld forces.",       CHTHONIC_TIEFLING_AB,    P_NONE));
        SUBRACES.add(new SubraceDefinition("infernal_tiefling",   "tiefling",  "Infernal Tiefling",   "Descended from devils, burning with dark power.", INFERNAL_TIEFLING_AB,   P_NONE));
    }

    public static List<SubraceDefinition> getSubracesFor(String parentId) {
        List<SubraceDefinition> out = new ArrayList<>();
        for (SubraceDefinition s : SUBRACES) if (s.getParentRaceId().equals(parentId)) out.add(s);
        return out;
    }
    public static RaceDefinition getRace(String id) { for (RaceDefinition r : RACES) if (r.getId().equals(id)) return r; return null; }
    public static SubraceDefinition getSubrace(String id) { for (SubraceDefinition s : SUBRACES) if (s.getId().equals(id)) return s; return null; }
}