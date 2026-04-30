package net.luderspieler.dnd.classes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.*;

public class ClassRegistry {

    public static final List<ClassDefinition>    CLASSES    = new ArrayList<>();
    public static final List<SubclassDefinition> SUBCLASSES = new ArrayList<>();

    private static Map<String,Double> attrs(double hp,double dmg,double armor,double spd,double aspd,double luck){
        Map<String,Double> m=new LinkedHashMap<>();
        m.put("Max Health",hp);m.put("Attack Damage",dmg);m.put("Armor",armor);
        m.put("Movement Speed",spd);m.put("Attack Speed",aspd);m.put("Luck",luck);return m;}

    // ── ATTRS ──
    private static final Map<String,Double> BARBARIAN_ATTRS = attrs(+20,+6, +4, +0.02,+0.1, 0);
    private static final Map<String,Double> BARD_ATTRS      = attrs(+2, +2,  0,  +0.03,+0.3,+5);
    private static final Map<String,Double> CLERIC_ATTRS    = attrs(+12,+3, +4,   0,    0,  +4);
    private static final Map<String,Double> DRUID_ATTRS     = attrs(+8, +2, +2,  +0.02, 0,  +3);
    private static final Map<String,Double> FIGHTER_ATTRS   = attrs(+10,+5, +5,   0,  +0.3,  0);
    private static final Map<String,Double> MONK_ATTRS      = attrs(+6, +3, +1,  +0.05,+1.2,  0);
    private static final Map<String,Double> PALADIN_ATTRS   = attrs(+14,+4, +6,   0,    0,    0);
    private static final Map<String,Double> RANGER_ATTRS    = attrs(+4, +4,  0,  +0.03,+0.5,+2);
    private static final Map<String,Double> ROGUE_ATTRS     = attrs(-2, +5,  0,  +0.04,+1.0,+3);
    private static final Map<String,Double> SORCERER_ATTRS  = attrs(-4, +5,  0,  +0.03,+0.3,+4);
    private static final Map<String,Double> WARLOCK_ATTRS   = attrs(-2, +7,  0,  +0.02,+0.3,+5);
    private static final Map<String,Double> WIZARD_ATTRS    = attrs(-6, +2,  0,  +0.04,+0.2,+5);

    // ── PROFICIENCIES ──
    // Tag names that match your item tags under dnd:armor/* and dnd:weapons/*
    private static final String P_BARB  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String P_BARD  = "light_armor,simple_weapons,war_weapons";
    private static final String P_CLER  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String P_DRUI  = "light_armor,medium_armor,shields,simple_weapons";
    private static final String P_FIGH  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons";
    private static final String P_MONK  = "simple_weapons";
    private static final String P_PALA  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons";
    private static final String P_RANG  = "light_armor,medium_armor,shields,simple_weapons,war_weapons";
    private static final String P_ROGU  = "light_armor,simple_weapons,war_weapons";
    private static final String P_SORC  = "simple_weapons";
    private static final String P_WARL  = "light_armor,simple_weapons,war_weapons";
    private static final String P_WIZA  = "simple_weapons";
    // Subclass overrides (only when different from parent)
    private static final String P_VALOR = "light_armor,medium_armor,shields,simple_weapons,war_weapons"; // College of Valor gains martial
    private static final String P_WAR_D = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons"; // War Domain gains heavy
    private static final String P_ELDR  = "light_armor,medium_armor,heavy_armor,shields,simple_weapons,war_weapons"; // Eldritch Knight keeps full

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
    private static final List<String> BARBARIAN_AB = List.of("Rage: Bonus damage while raging","Reckless Attack: Advantage on melee attacks","Danger Sense: Advantage on DEX saves vs traps","Fast Movement: +10ft speed without heavy armor","Feral Instinct: Advantage on initiative rolls");
    private static final List<String> BARD_AB      = List.of("Bardic Inspiration: Grant allies a d6 bonus die","Jack of All Trades: Half proficiency to all skills","Song of Rest: Bonus healing during short rests","Expertise: Double proficiency in two chosen skills","Magical Secrets: Learn spells from any class list");
    private static final List<String> CLERIC_AB    = List.of("Channel Divinity: Harness divine energy twice per rest","Turn Undead: Frighten nearby undead creatures","Destroy Undead: Instantly destroy weak undead","Divine Intervention: Call upon your deity for aid","Blessed Strikes: Bonus radiant damage on attacks");
    private static final List<String> DRUID_AB     = List.of("Wild Shape: Transform into a beast form","Timeless Body: Age at one tenth the normal rate","Beast Spells: Cast spells while in Wild Shape","Archdruid: Unlimited Wild Shape uses");
    private static final List<String> FIGHTER_AB   = List.of("Second Wind: Recover HP as a bonus action","Action Surge: Take one additional action per rest","Extra Attack: Attack up to four times per action","Indomitable: Reroll a failed saving throw");
    private static final List<String> MONK_AB      = List.of("Flurry of Blows: Two extra unarmed strikes with ki","Patient Defense: Dodge as a bonus action","Step of the Wind: Dash or Disengage as bonus action","Stunning Strike: Spend ki to stun a creature on hit","Evasion: No damage on successful DEX saves");
    private static final List<String> PALADIN_AB   = List.of("Lay on Hands: Pool of HP to distribute as healing","Divine Smite: Spend spell slot for radiant damage","Aura of Protection: Charisma modifier to all saves","Aura of Courage: Nearby allies immune to frightened","Cleansing Touch: Remove spells with a touch");
    private static final List<String> RANGER_AB    = List.of("Favored Enemy: Bonus against a chosen creature type","Natural Explorer: Expertise navigating chosen terrain","Primeval Awareness: Sense creatures by type nearby","Hide in Plain Sight: +10 to hide when motionless","Vanish: Hide as bonus action, can't be tracked");
    private static final List<String> ROGUE_AB     = List.of("Sneak Attack: Extra damage when you have advantage","Thieves' Cant: Secret language of the criminal world","Cunning Action: Dash, Hide, or Disengage as bonus","Uncanny Dodge: Halve damage from one attack per turn","Evasion: Avoid damage entirely on successful DEX saves");
    private static final List<String> SORCERER_AB  = List.of("Sorcery Points: Fuel metamagic and spell conversion","Metamagic: Modify spells (Twinned, Quickened, etc.)","Font of Magic: Convert between points and spell slots","Innate Sorcery: Bonus to spell attack and save DC");
    private static final List<String> WARLOCK_AB   = List.of("Eldritch Blast: Primary damage cantrip beam","Invocations: Customize abilities and spell access","Pact Boon: Choose weapon, tome, chain or talisman","Mystic Arcanum: Cast a high-level spell once per day","Eldritch Master: Recover all spell slots in one minute");
    private static final List<String> WIZARD_AB    = List.of("Arcane Recovery: Recover spell slots on short rest","Spellbook: Learn and prepare spells from written sources","Spell Mastery: Cast two spells without using spell slots","Signature Spell: Cast two 3rd-level spells for free");

    private static final List<String> BERSERKER_AB       = List.of("Frenzy: Extra attack each turn while raging","Mindless Rage: Immune to charm and frightened","Intimidating Presence: Frighten a creature nearby","Retaliation: React to damage with a melee attack");
    private static final List<String> WILD_HEART_AB      = List.of("Bestial Soul: Choose a natural weapon type","Animal Speaker: Cast Beast Sense and Speak with Animals","Totemic Attunement: Gain a beast power while raging");
    private static final List<String> WORLD_TREE_AB      = List.of("Vitality of the Tree: Gain temp HP on rage start","Branches of the Tree: Teleport allies to your side","Travel Along the Tree: Teleport between locations");
    private static final List<String> ZEALOT_AB          = List.of("Divine Fury: Add radiant or necrotic bonus damage","Warrior of the Gods: Easier resurrection at no cost","Fanatical Focus: Reroll failed saves while raging","Zealous Presence: Grant advantage to all nearby allies");
    private static final List<String> COLLEGE_DANCE_AB   = List.of("Dazzling Footwork: Gain AC bonus from Charisma","Inspiring Movement: Move an ally when they use inspiration","Tandem Footwork: Grant initiative bonus to all allies");
    private static final List<String> COLLEGE_GLAMOUR_AB = List.of("Mantle of Inspiration: Grant temporary HP to allies","Enthralling Performance: Charm up to five creatures","Mantle of Majesty: Command spell as a bonus action free");
    private static final List<String> COLLEGE_LORE_AB    = List.of("Cutting Words: Reduce an enemy attack or ability roll","Additional Magical Secrets: Learn two spells early","Peerless Skill: Add Bardic Inspiration die to your checks");
    private static final List<String> COLLEGE_VALOR_AB   = List.of("Combat Inspiration: Inspiration can boost damage or AC","Extra Attack: Attack twice each action","Battle Magic: Make a bonus attack after casting a spell");
    private static final List<String> LIFE_DOMAIN_AB     = List.of("Disciple of Life: Healing spells restore extra HP","Preserve Life: Rapidly distribute healing to many allies","Blessed Healer: Heal yourself when you heal others","Supreme Healing: Roll maximum on all healing dice");
    private static final List<String> LIGHT_DOMAIN_AB    = List.of("Warding Flare: Impose disadvantage on one attacker","Radiance of the Dawn: Deal radiant damage to all undead","Corona of Light: Sunlight aura causes enemy disadvantage");
    private static final List<String> TRICKERY_DOMAIN_AB = List.of("Blessing of the Trickster: Grant stealth advantage","Invoke Duplicity: Create a perfect illusory duplicate","Cloak of Shadows: Turn invisible as an action");
    private static final List<String> WAR_DOMAIN_AB      = List.of("War Priest: Make a bonus attack as a bonus action","Guided Strike: Add +10 to your attack roll once","War God's Blessing: Grant +10 to an ally's attack roll");
    private static final List<String> CIRCLE_LAND_AB     = List.of("Natural Recovery: Recover spell slots on short rest","Land's Stride: Move through non-magical plants freely","Nature's Sanctuary: Beasts and plants avoid attacking you");
    private static final List<String> CIRCLE_MOON_AB     = List.of("Combat Wild Shape: Use Wild Shape as a bonus action","Elemental Wild Shape: Transform into an elemental","Thousand Forms: Cast Alter Self at will for free");
    private static final List<String> CIRCLE_SEA_AB      = List.of("Wrath of the Sea: Surround yourself with crashing waves","Aquatic Affinity: Gain swim speed and water breathing","Stormborn: Gain fly speed during stormy conditions");
    private static final List<String> CIRCLE_STARS_AB    = List.of("Star Map: Know Guiding Bolt and navigation skills","Starry Form: Assume a glowing constellation form","Twinkling Constellations: Enhance your starry form further");
    private static final List<String> BATTLE_MASTER_AB   = List.of("Superiority Dice: Fuel powerful combat maneuvers","Maneuvers: Choose from many tactical combat options","Know Your Enemy: Study a creature's combat statistics","Relentless: Regain a superiority die on initiative roll");
    private static final List<String> CHAMPION_AB        = List.of("Improved Critical: Score a critical hit on a 19 or 20","Remarkable Athlete: Boost athletic and jumping checks","Additional Fighting Style: Learn a second fighting style","Superior Critical: Score a critical hit on 18 through 20");
    private static final List<String> ELDRITCH_KNIGHT_AB = List.of("Spellcasting: Cast abjuration and evocation spells","Weapon Bond: Teleport your bonded weapon to your hand","War Magic: Make a bonus attack after casting a cantrip","Eldritch Strike: Weaken enemy saves after you hit them");
    private static final List<String> PSI_WARRIOR_AB     = List.of("Psionic Power: Pool of psionic energy dice to spend","Protective Field: Spend a die to reduce incoming damage","Psionic Strike: Deal extra psychic damage on a hit","Telekinetic Movement: Move objects or creatures with mind");
    private static final List<String> WARRIOR_MERCY_AB   = List.of("Hand of Healing: Spend ki to heal on a hit","Hand of Harm: Add poison damage to an unarmed strike","Physician's Touch: Cure conditions while healing allies","Flurry of Healing and Harm: Heal and harm during flurry");
    private static final List<String> WARRIOR_SHADOW_AB  = List.of("Shadow Arts: Cast darkness and silence using ki","Shadow Step: Teleport between dim light or darkness","Cloak of Shadows: Turn invisible in dim light or dark","Opportunist: React to hit an enemy already struck");
    private static final List<String> WARRIOR_ELEMENTS_AB= List.of("Elemental Attunement: Perform minor elemental tricks","Fangs of the Fire Snake: Extend strikes with flame reach","Fist of Four Thunders: Create a shockwave to push foes","Ride the Wind: Gain fly speed for one turn using ki");
    private static final List<String> WARRIOR_OPEN_AB    = List.of("Open Hand Technique: Push trip or deny enemy reaction","Wholeness of Body: Heal yourself for three times your HP","Tranquility: Gain sanctuary effect until you attack","Quivering Palm: Set up lethal internal vibrations on foe");
    private static final List<String> OATH_DEVOTION_AB   = List.of("Sacred Weapon: Add Charisma modifier to attack rolls","Turn the Unholy: Turn fiends and undead creatures","Aura of Devotion: You and allies immune to charmed","Holy Nimbus: Sunlight aura damages undead nearby");
    private static final List<String> OATH_GLORY_AB      = List.of("Inspiring Smite: Distribute temp HP after a divine smite","Peerless Athlete: Advantage on athletics and acrobatics","Aura of Alacrity: Allies gain movement speed near you","Glorious Defense: Add Charisma to an ally's AC as reaction");
    private static final List<String> OATH_ANCIENTS_AB   = List.of("Nature's Wrath: Restrain a foe with spectral vines","Turn the Faithless: Turn fey and fiend creatures","Aura of Warding: Resistance to damage from spells","Undying Sentinel: Stay at 1 HP instead of falling once");
    private static final List<String> OATH_VENGEANCE_AB  = List.of("Vow of Enmity: Advantage on all attacks vs one foe","Inquisitor's Might: Add radiant or psychic bonus damage","Relentless Avenger: Gain speed after an opportunity attack","Soul of Vengeance: React to hit the target of your vow");
    private static final List<String> BEAST_MASTER_AB    = List.of("Primal Companion: Bond with a magical beast companion","Exceptional Training: Command beast as a bonus action","Bestial Fury: Your beast companion attacks twice","Share Spells: Target your beast with self-only spells");
    private static final List<String> FEY_WANDERER_AB    = List.of("Dreadful Strikes: Add psychic bonus damage on hits","Fey Wanderer Magic: Access extra spells from the Feywild","Otherworldly Glamour: Add Wisdom to Charisma checks","Beguiling Twist: Redirect charm or fear effects to others");
    private static final List<String> GLOOM_STALKER_AB   = List.of("Dread Ambusher: Bonus attack and speed in first round","Umbral Sight: You become invisible to darkvision","Iron Mind: Gain proficiency in Wisdom saving throws","Stalker's Flurry: Make an extra attack when you miss");
    private static final List<String> HUNTER_AB          = List.of("Hunter's Prey: Choose Colossus Slayer Horde Breaker or Giant Killer","Defensive Tactics: Choose a passive defensive bonus","Multiattack: Fire multiple projectiles in one action","Superior Hunter's Defense: Evasion or Stand Against the Tide");
    private static final List<String> ARCANE_TRICKSTER_AB= List.of("Mage Hand Legerdemain: Enhanced invisible magical hand","Magical Ambush: Foes have disadvantage on saves if hidden","Versatile Trickster: Use Mage Hand to distract enemies","Spell Thief: Steal a spell directly from a caster");
    private static final List<String> ASSASSIN_AB        = List.of("Assassinate: Advantage on surprised foes, auto critical","Infiltration Expertise: Create false identities perfectly","Impostor: Perfectly mimic any person you have studied","Death Strike: Double damage against a surprised target");
    private static final List<String> SOULKNIFE_AB       = List.of("Psychic Blades: Materialize blades from your own mind","Soul Blades: Spend psionic dice for extra blade effects","Psychic Veil: Turn invisible using psionic energy","Rend Mind: Stun a target with a Psychic Blades attack");
    private static final List<String> THIEF_AB           = List.of("Fast Hands: Use an object or tool as a bonus action","Second-Story Work: Climb faster and jump farther","Supreme Sneak: Advantage on stealth at half speed","Use Magic Device: Ignore class restrictions on magic items");
    private static final List<String> DRACONIC_AB        = List.of("Draconic Resilience: Natural AC bonus and extra HP","Elemental Affinity: Bonus damage to your dragon element","Dragon Wings: Sprout real wings and gain fly speed","Draconic Presence: Fear or charm creatures in your aura");
    private static final List<String> WILD_MAGIC_AB      = List.of("Wild Magic Surge: Random magical effect after casting","Tides of Chaos: Gain advantage then trigger a surge","Bend Luck: Spend points to alter any creature's rolls","Controlled Chaos: Choose from two surge table results");
    private static final List<String> ABERRANT_AB        = List.of("Psionic Spells: Access extra spells from far realms","Telepathic Speech: Communicate telepathically over distance","Psionic Sorcery: Cast spells without material components","Psychic Defenses: Resistance to psychic damage");
    private static final List<String> CLOCKWORK_AB       = List.of("Clockwork Magic: Restore cosmic balance with spells","Restore Balance: Cancel advantage or disadvantage effects","Bastion of Law: Absorb incoming damage to protect an ally","Trance of Order: Treat any roll below 9 as a 10");
    private static final List<String> ARCHFEY_AB         = List.of("Fey Presence: Charm or frighten creatures in a cube","Misty Escape: Turn invisible and teleport after taking damage","Beguiling Defenses: Immune to charm and reflect it back","Dark Delirium: Trap a creature in an illusory dream");
    private static final List<String> FIEND_AB           = List.of("Dark One's Blessing: Gain temp HP on killing a creature","Dark One's Own Luck: Add d10 to ability checks or saves","Fiendish Resilience: Choose damage resistance each rest","Hurl Through Hell: Banish then deal massive psychic damage");
    private static final List<String> GREAT_OLD_ONE_AB   = List.of("Awakened Mind: Speak telepathically to any creature","Entropic Ward: Impose disadvantage on an attacker","Thought Shield: Resistance to psychic and thought reading","Create Thrall: Charm a creature indefinitely with a touch");
    private static final List<String> CELESTIAL_AB       = List.of("Healing Light: Pool of d6 dice for healing creatures","Radiant Soul: Fly speed and add Charisma to radiant damage","Celestial Resilience: Grant temp HP to you and nearby allies","Searing Vengeance: Rise from 0 HP with a radiant explosion");
    private static final List<String> ABJURER_AB         = List.of("Arcane Ward: Create a magical shield absorbing damage","Projected Ward: Share your arcane ward with a nearby ally","Improved Abjuration: Bonus to abjuration ability checks","Spell Resistance: Advantage on saves and resistance vs spells");
    private static final List<String> DIVINER_AB         = List.of("Portent: Replace any roll with your prophecy dice rolls","Expert Divination: Recover spell slots when divining","The Third Eye: Gain truesight ethereal sight or darkvision","Greater Portent: Roll three prophecy dice each long rest");
    private static final List<String> EVOKER_AB          = List.of("Sculpt Spells: Exclude chosen creatures from your AoE","Potent Cantrip: Cantrips deal half damage even on saves","Empowered Evocation: Add Intelligence to evocation damage","Overchannel: Maximize spell damage but take necrotic backlash");
    private static final List<String> ILLUSIONIST_AB     = List.of("Improved Illusions: Create sounds within illusion spells","Malleable Illusions: Change existing illusions as an action","Illusory Self: An illusory duplicate blocks one incoming attack","Illusory Reality: Make one component of your illusion real");

    static {
        CLASSES.add(new ClassDefinition("barbarian","Barbarian","A fierce warrior driven by primal rage.",         BARBARIAN_ATTRS,BARBARIAN_AB,BARBARIAN_ITEMS,P_BARB));
        SUBCLASSES.add(new SubclassDefinition("berserker",       "barbarian","Path of the Berserker",      "Channels rage into devastating attacks.",         BERSERKER_AB,   P_BARB));
        SUBCLASSES.add(new SubclassDefinition("wild_heart",      "barbarian","Path of the Wild Heart",     "Draws on the spirits of beasts.",                 WILD_HEART_AB,  P_BARB));
        SUBCLASSES.add(new SubclassDefinition("world_tree",      "barbarian","Path of the World Tree",     "Taps into the cosmic power of the World Tree.",   WORLD_TREE_AB,  P_BARB));
        SUBCLASSES.add(new SubclassDefinition("zealot",          "barbarian","Path of the Zealot",         "Infuses rage with divine fury.",                  ZEALOT_AB,      P_BARB));

        CLASSES.add(new ClassDefinition("bard",    "Bard",    "A master of music, magic, and inspiration.",      BARD_ATTRS,    BARD_AB,    BARD_ITEMS,    P_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_dance",   "bard","College of Dance",         "Uses movement as a spellcasting focus.",           COLLEGE_DANCE_AB,   P_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_glamour", "bard","College of Glamour",       "Weaves enchantments of fey-touched beauty.",       COLLEGE_GLAMOUR_AB, P_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_lore",    "bard","College of Lore",          "Collects knowledge from every discipline.",        COLLEGE_LORE_AB,    P_BARD));
        SUBCLASSES.add(new SubclassDefinition("college_valor",   "bard","College of Valor",         "Combines combat with bardic inspiration.",         COLLEGE_VALOR_AB,   P_VALOR));

        CLASSES.add(new ClassDefinition("cleric",  "Cleric",  "A divine spellcaster empowered by their deity.",  CLERIC_ATTRS,  CLERIC_AB,  CLERIC_ITEMS,  P_CLER));
        SUBCLASSES.add(new SubclassDefinition("life_domain",     "cleric","Life Domain",             "Devoted to preserving and restoring life.",        LIFE_DOMAIN_AB,     P_CLER));
        SUBCLASSES.add(new SubclassDefinition("light_domain",    "cleric","Light Domain",            "Channels the power of fire and radiance.",         LIGHT_DOMAIN_AB,    P_CLER));
        SUBCLASSES.add(new SubclassDefinition("trickery_domain", "cleric","Trickery Domain",         "A deity of deception blesses this cleric.",        TRICKERY_DOMAIN_AB, P_CLER));
        SUBCLASSES.add(new SubclassDefinition("war_domain",      "cleric","War Domain",              "Blessed for combat by a god of war.",              WAR_DOMAIN_AB,      P_WAR_D));

        CLASSES.add(new ClassDefinition("druid",   "Druid",   "A guardian of nature and its cycles.",            DRUID_ATTRS,   DRUID_AB,   DRUID_ITEMS,   P_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_land",     "druid","Circle of the Land",       "Draws magical power from the terrain.",            CIRCLE_LAND_AB,     P_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_moon",     "druid","Circle of the Moon",       "Shapeshifts into powerful beasts.",                CIRCLE_MOON_AB,     P_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_sea",      "druid","Circle of the Sea",        "Commands the power of wind and wave.",             CIRCLE_SEA_AB,      P_DRUI));
        SUBCLASSES.add(new SubclassDefinition("circle_stars",    "druid","Circle of the Stars",      "Reads power from the constellations.",             CIRCLE_STARS_AB,    P_DRUI));

        CLASSES.add(new ClassDefinition("fighter", "Fighter", "A versatile master of weaponry and armor.",       FIGHTER_ATTRS, FIGHTER_AB, FIGHTER_ITEMS, P_FIGH));
        SUBCLASSES.add(new SubclassDefinition("battle_master",   "fighter","Battle Master",          "Uses cunning maneuvers to control the battlefield.",BATTLE_MASTER_AB,  P_FIGH));
        SUBCLASSES.add(new SubclassDefinition("champion",        "fighter","Champion",               "An elite warrior of pure physical excellence.",    CHAMPION_AB,        P_FIGH));
        SUBCLASSES.add(new SubclassDefinition("eldritch_knight", "fighter","Eldritch Knight",        "Combines martial training with arcane magic.",     ELDRITCH_KNIGHT_AB, P_ELDR));
        SUBCLASSES.add(new SubclassDefinition("psi_warrior",     "fighter","Psi Warrior",            "Augments combat with psionic power.",              PSI_WARRIOR_AB,     P_FIGH));

        CLASSES.add(new ClassDefinition("monk",    "Monk",    "A master of martial arts and ki energy.",         MONK_ATTRS,    MONK_AB,    MONK_ITEMS,    P_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_mercy",   "monk","Warrior of Mercy",          "Heals allies and debilitates foes.",               WARRIOR_MERCY_AB,   P_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_shadow",  "monk","Warrior of Shadow",         "Blends combat and shadow magic.",                  WARRIOR_SHADOW_AB,  P_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_elements","monk","Warrior of the Elements",   "Channels elemental energy through strikes.",       WARRIOR_ELEMENTS_AB,P_MONK));
        SUBCLASSES.add(new SubclassDefinition("warrior_open_hand","monk","Warrior of the Open Hand", "The purest expression of martial discipline.",     WARRIOR_OPEN_AB,    P_MONK));

        CLASSES.add(new ClassDefinition("paladin", "Paladin", "A holy warrior bound by a sacred oath.",          PALADIN_ATTRS, PALADIN_AB, PALADIN_ITEMS, P_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_devotion",   "paladin","Oath of Devotion",       "Upholds the ideal of a shining knight.",           OATH_DEVOTION_AB,   P_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_glory",      "paladin","Oath of Glory",          "Strives to reach the heights of heroic legend.",   OATH_GLORY_AB,      P_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_ancients",   "paladin","Oath of the Ancients",   "Preserves the light of life against darkness.",    OATH_ANCIENTS_AB,   P_PALA));
        SUBCLASSES.add(new SubclassDefinition("oath_vengeance",  "paladin","Oath of Vengeance",      "Pursues and punishes evildoers relentlessly.",     OATH_VENGEANCE_AB,  P_PALA));

        CLASSES.add(new ClassDefinition("ranger",  "Ranger",  "A skilled hunter of the wilderness.",             RANGER_ATTRS,  RANGER_AB,  RANGER_ITEMS,  P_RANG));
        SUBCLASSES.add(new SubclassDefinition("beast_master",    "ranger","Beast Master",             "Forms a deep bond with an animal companion.",      BEAST_MASTER_AB,    P_RANG));
        SUBCLASSES.add(new SubclassDefinition("fey_wanderer",    "ranger","Fey Wanderer",             "Channels the magic of the Feywild.",               FEY_WANDERER_AB,    P_RANG));
        SUBCLASSES.add(new SubclassDefinition("gloom_stalker",   "ranger","Gloom Stalker",            "Thrives in the darkest places of the world.",      GLOOM_STALKER_AB,   P_RANG));
        SUBCLASSES.add(new SubclassDefinition("hunter",          "ranger","Hunter",                   "Specializes in tracking and slaying prey.",        HUNTER_AB,          P_RANG));

        CLASSES.add(new ClassDefinition("rogue",   "Rogue",   "A cunning trickster who strikes from shadows.",   ROGUE_ATTRS,   ROGUE_AB,   ROGUE_ITEMS,   P_ROGU));
        SUBCLASSES.add(new SubclassDefinition("arcane_trickster","rogue","Arcane Trickster",          "Enhances roguish talents with illusion magic.",    ARCANE_TRICKSTER_AB,P_ROGU));
        SUBCLASSES.add(new SubclassDefinition("assassin",        "rogue","Assassin",                  "Trained to eliminate targets swiftly and silently.",ASSASSIN_AB,       P_ROGU));
        SUBCLASSES.add(new SubclassDefinition("soulknife",       "rogue","Soulknife",                 "Focuses psionic energy into blade-like constructs.",SOULKNIFE_AB,      P_ROGU));
        SUBCLASSES.add(new SubclassDefinition("thief",           "rogue","Thief",                     "Master of speed, stealth and sleight of hand.",    THIEF_AB,           P_ROGU));

        CLASSES.add(new ClassDefinition("sorcerer","Sorcerer","Born with innate magical power.",                 SORCERER_ATTRS,SORCERER_AB,SORCERER_ITEMS,P_SORC));
        SUBCLASSES.add(new SubclassDefinition("draconic_sorcery","sorcerer","Draconic Sorcery",       "Dragon blood fuels your raw magical might.",       DRACONIC_AB,        P_SORC));
        SUBCLASSES.add(new SubclassDefinition("wild_magic",      "sorcerer","Wild Magic",             "Magic surges with chaotic unpredictable power.",   WILD_MAGIC_AB,      P_SORC));
        SUBCLASSES.add(new SubclassDefinition("aberrant_sorcery","sorcerer","Aberrant Sorcery",       "Touched by alien forces from beyond reality.",     ABERRANT_AB,        P_SORC));
        SUBCLASSES.add(new SubclassDefinition("clockwork_sorcery","sorcerer","Clockwork Sorcery",     "Channels the ordered magic of clockwork planes.",  CLOCKWORK_AB,       P_SORC));

        CLASSES.add(new ClassDefinition("warlock", "Warlock", "Gains power through a pact with a dark entity.",  WARLOCK_ATTRS, WARLOCK_AB, WARLOCK_ITEMS, P_WARL));
        SUBCLASSES.add(new SubclassDefinition("archfey_patron",  "warlock","Archfey Patron",          "Pact with a powerful lord of the Feywild.",        ARCHFEY_AB,         P_WARL));
        SUBCLASSES.add(new SubclassDefinition("fiend_patron",    "warlock","Fiend Patron",            "Pact made with a powerful devil for raw power.",   FIEND_AB,           P_WARL));
        SUBCLASSES.add(new SubclassDefinition("great_old_one",   "warlock","Great Old One Patron",    "Bound to an unknowable cosmic entity.",            GREAT_OLD_ONE_AB,   P_WARL));
        SUBCLASSES.add(new SubclassDefinition("celestial_patron","warlock","Celestial Patron",        "Pact with a being of the Upper Planes.",           CELESTIAL_AB,       P_WARL));

        CLASSES.add(new ClassDefinition("wizard",  "Wizard",  "Master of studied spellcasting.",                 WIZARD_ATTRS,  WIZARD_AB,  WIZARD_ITEMS,  P_WIZA));
        SUBCLASSES.add(new SubclassDefinition("abjurer",         "wizard","Abjurer",                  "Specializes in protective and warding magic.",     ABJURER_AB,         P_WIZA));
        SUBCLASSES.add(new SubclassDefinition("diviner",         "wizard","Diviner",                  "Peers into the past, present and future.",         DIVINER_AB,         P_WIZA));
        SUBCLASSES.add(new SubclassDefinition("evoker",          "wizard","Evoker",                   "Channels raw elemental forces into destruction.",  EVOKER_AB,          P_WIZA));
        SUBCLASSES.add(new SubclassDefinition("illusionist",     "wizard","Illusionist",              "Bends reality through weaves of illusion.",        ILLUSIONIST_AB,     P_WIZA));
    }

    public static List<SubclassDefinition> getSubclassesFor(String parentId) {
        List<SubclassDefinition> out = new ArrayList<>();
        for (SubclassDefinition s : SUBCLASSES) if (s.getParentClassId().equals(parentId)) out.add(s);
        return out;
    }
    public static ClassDefinition getClass(String id) { for (ClassDefinition c : CLASSES) if (c.getId().equals(id)) return c; return null; }
    public static SubclassDefinition getSubclass(String id) { for (SubclassDefinition s : SUBCLASSES) if (s.getId().equals(id)) return s; return null; }
}
