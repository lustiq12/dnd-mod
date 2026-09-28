package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.Utils.GeneralDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry of spells with selectable options (damage type, mode, ...).
 * Client side: remembers the selection per spell for the wheel GUI.
 * Server side: the validated selection is stored in GeneralData under DATA_KEY.
 * Option ids must not contain ',' or '=' (GeneralData format).
 */
public final class SpellChoices {

    public record Option(String id, String display) {}

    public record Choice(String label, List<Option> options) {}

    public static final String DATA_KEY = "SPELL_CHOICE";

    private static final Map<String, Choice> REGISTRY = new HashMap<>();
    private static final Map<String, Integer> CLIENT_SELECTION = new HashMap<>();

    private static final Map<String, String> DAMAGE_NAMES = new LinkedHashMap<>();

    static {
        DAMAGE_NAMES.put("ACID", "Acid");
        DAMAGE_NAMES.put("COLD", "Cold");
        DAMAGE_NAMES.put("FIRE", "Fire");
        DAMAGE_NAMES.put("LIGHTNING", "Lightning");
        DAMAGE_NAMES.put("POISON", "Poison");
        DAMAGE_NAMES.put("NECROTIC", "Necrotic");
        DAMAGE_NAMES.put("PSYCHIC", "Psychic");
        DAMAGE_NAMES.put("RADIANT", "Radiant");
        DAMAGE_NAMES.put("THUNDER", "Thunder");
        DAMAGE_NAMES.put("BLUDGEONING", "Bludgeoning");
        DAMAGE_NAMES.put("PIERCING", "Piercing");
        DAMAGE_NAMES.put("SLASHING", "Slashing");

        // Cantrips
        reg("THORN_WHIP", "Pull", opts("PULL", "Yes", "NO_PULL", "No"));
        reg("SORCEROUS_BURST", "Damage Type", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "POISON", "PSYCHIC", "THUNDER"));
        reg("RESISTANCE", "Save Bonus Type", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "POISON", "NECROTIC", "RADIANT", "THUNDER", "BLUDGEONING", "PIERCING", "SLASHING"));
        reg("SHILLELAGH", "Damage", opts("WEAPON", "Weapon", "FORCE", "Force"));
        reg("TRUE_STRIKE", "Damage", opts("WEAPON", "Weapon", "RADIANT", "Radiant"));
        reg("GUIDANCE", "Skill", skills());
        reg("DRUIDCRAFT", "Effect", opts("FORECAST", "Predict Weather", "BLOOM", "Bloom Bud", "SENSORY", "Sensory Effect", "FLAME", "Light/Snuff Flame"));
        reg("PRESTIDIGITATION", "Effect", opts("SPARKS", "Magic Effect", "FLAME", "Light/Snuff Flame", "CLEAN", "Clean/Soil", "TEMPERATURE", "Chill/Warm/Flavor", "MARK", "Mark", "TRINKET", "Create Trinket"));
        reg("THAUMATURGY", "Effect", opts("VOICE", "Boom Voice", "FLAMES", "Flames", "TREMORS", "Tremors", "SOUND", "Sound", "DOORS", "Doors/Windows", "EYES", "Eye Color"));

        // Grade 1
        reg("CHROMATIC_ORB", "Damage Type", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "POISON", "THUNDER"));
        reg("ALARM", "Alarm Type", opts("AUDIBLE", "Audible", "MENTAL", "Mental"));
        reg("COMMAND", "Command", opts("APPROACH", "Approach", "DROP", "Drop", "FLEE", "Flee", "GROVEL", "Grovel", "HALT", "Halt"));
        reg("CREATE_OR_DESTROY_WATER", "Mode", opts("CREATE", "Create", "DESTROY", "Destroy"));
        reg("HEX", "Ability", opts("STRENGTH", "Strength", "DEXTERITY", "Dexterity", "CONSTITUTION", "Constitution", "INTELLIGENCE", "Intelligence", "WISDOM", "Wisdom", "CHARISMA", "Charisma"));
        reg("PROTECTION_FROM_EVIL_AND_GOOD", "Protected Type", opts("ABERRATION", "Aberration", "CELESTIAL", "Celestial", "ELEMENTAL", "Elemental", "FEY", "Fey", "FIEND", "Fiend", "UNDEAD", "Undead"));

        // Grade 2
        reg("ALTER_SELF", "Form", opts("AQUATIC", "Aquatic", "APPEARANCE", "Appearance", "WEAPON_BLUDGEONING", "Natural Weapon (Bludgeoning)", "WEAPON_PIERCING", "Natural Weapon (Piercing)", "WEAPON_SLASHING", "Natural Weapon (Slashing)"));
        reg("CALM_EMOTIONS", "Effect", opts("SUPPRESS", "Suppress Charm/Fear", "INDIFFERENT", "Indifference"));
        reg("DRAGON_S_BREATH", "Damage Type", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "POISON"));
        reg("ENHANCE_ABILITY", "Enhancement", opts("BEARS_ENDURANCE", "Bear's Strength", "CATS_GRACE", "Cat's Grace", "BULLS_ENDURANCE", "Bull's Endurance", "FOXS_CUNNING", "Fox's Cunning", "OWLS_WISDOM", "Owl's Wisdom", "EAGLES_SPLENDOR", "Eagle's Splendor"));
        reg("ENLARGE_REDUCE", "Mode", opts("ENLARGE", "Enlarge", "REDUCE", "Reduce"));
        reg("ARCANIST_S_MAGIC_AURA", "Mode", opts("FALSE_AURA", "False Aura", "CREATURE_TYPE", "Creature Type"));

        // Grade 3
        reg("BESTOW_CURSE", "Curse", opts("ABILITY", "Ability Disadvantage", "ATTACKS", "Attack Disadvantage", "TURN_SAVE", "Lose Turns", "EXTRA_DAMAGE", "Extra Damage"));
        reg("GLYPH_OF_WARDING", "Glyph Damage", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "THUNDER"));
        reg("PROTECTION_FROM_ENERGY", "Resistance", dmg("ACID", "COLD", "FIRE", "LIGHTNING", "THUNDER"));

        // Grade 4
        reg("CONTROL_WATER", "Effect", opts("FLOOD", "Flood", "PART", "Part Water", "LOWER", "Lower Water", "WHIRLPOOL", "Whirlpool"));

        // Grade 5
        reg("ANIMATE_OBJECTS", "Object Size", opts("TINY", "Tiny", "SMALL", "Small", "MEDIUM", "Medium", "LARGE", "Large", "HUGE", "Huge"));
        reg("CREATION", "Material", opts("VEGETABLE", "Vegetable", "CLOTH", "Silk", "MINERAL", "Mineral", "PRECIOUS_METAL", "Precious Metal", "GEMSTONE", "Gemstone"));
        reg("DISPEL_EVIL_AND_GOOD", "Mode", opts("PROTECTION", "Protective Aura", "DISPEL", "Force Dispel", "END_POSSESSION", "End Possession"));
        reg("HALLOW", "Area Effect", opts("COURAGE", "Courage", "DARKNESS", "Darkness", "DAYLIGHT", "Daylight", "ENERGY_PROTECTION", "Energy Protection", "ENERGY_VULNERABILITY", "Energy Vulnerability", "EVERLASTING_REST", "Everlasting Rest", "FEAR", "Fear", "SILENCE", "Silence", "TONGUES", "Tongues"));

        // Grade 6
        reg("CONTAGION", "Disease", opts("BLINDING_SICKNESS", "Blinding Sickness", "FILTH_FEVER", "Filth Fever", "FLESH_ROT", "Flesh Rot", "MINDFIRE", "Mindfire", "SEIZURE", "Seizure", "SLIMY_DOOM", "Slimy Doom"));

        // Grade 7
        reg("SYMBOL", "Effect", opts("DISCORD", "Discord", "FEAR", "Fear", "HOPELESSNESS", "Hopelessness", "INSANITY", "Insanity", "PAIN", "Pain", "SLEEP", "Sleep", "DEATH", "Death", "STUNNING", "Stunning"));

        // Grade 9
        reg("TRUE_POLYMORPH", "Mode", opts("CREATURE_TO_CREATURE", "Creature > Creature", "CREATURE_TO_OBJECT", "Creature > Object", "OBJECT_TO_CREATURE", "Object > Creature"));
    }

    private SpellChoices() {
    }

    // Registry helpers

    private static void reg(String spellId, String label, List<Option> options) {
        REGISTRY.put(spellId, new Choice(label, options));
    }

    private static List<Option> opts(String... idAndDisplayPairs) {
        List<Option> list = new ArrayList<>();
        for (int i = 0; i + 1 < idAndDisplayPairs.length; i += 2) {
            list.add(new Option(idAndDisplayPairs[i], idAndDisplayPairs[i + 1]));
        }
        return list;
    }

    private static List<Option> dmg(String... damageIds) {
        List<Option> list = new ArrayList<>();
        for (String id : damageIds) list.add(new Option(id, DAMAGE_NAMES.get(id)));
        return list;
    }

    private static List<Option> skills() {
        String[] names = {"Acrobatics", "Animal Handling", "Arcana", "Athletics", "Deception", "History",
                "Insight", "Intimidation", "Investigation", "Medicine", "Nature", "Perception",
                "Performance", "Persuasion", "Religion", "Sleight of Hand", "Stealth", "Survival"};
        List<Option> list = new ArrayList<>();
        for (String n : names) list.add(new Option(n.toUpperCase().replace(' ', '_'), n));
        return list;
    }

    // Lookup

    public static Choice get(String spellId) {
        return REGISTRY.get(spellId);
    }

    public static boolean has(String spellId) {
        return REGISTRY.containsKey(spellId);
    }

    // Client selection (wheel GUI)

    public static int getSelectedIndex(String spellId) {
        Choice choice = REGISTRY.get(spellId);
        if (choice == null) return 0;
        int idx = CLIENT_SELECTION.getOrDefault(spellId, 0);
        return Math.floorMod(idx, choice.options().size());
    }

    public static void cycle(String spellId, boolean forward) {
        Choice choice = REGISTRY.get(spellId);
        if (choice == null) return;
        int size = choice.options().size();
        CLIENT_SELECTION.put(spellId, Math.floorMod(getSelectedIndex(spellId) + (forward ? 1 : -1), size));
    }

    /** Returns "" for spells without choices. */
    public static String getSelectedId(String spellId) {
        Choice choice = REGISTRY.get(spellId);
        return choice == null ? "" : choice.options().get(getSelectedIndex(spellId)).id();
    }

    public static String getSelectedDisplay(String spellId) {
        Choice choice = REGISTRY.get(spellId);
        return choice == null ? "" : choice.options().get(getSelectedIndex(spellId)).display();
    }

    // Server side

    /** Validates a client-sent option id, falling back to the first option. */
    public static String resolve(String spellId, String requested) {
        Choice choice = REGISTRY.get(spellId);
        if (choice == null) return "";
        for (Option o : choice.options()) {
            if (o.id().equals(requested)) return o.id();
        }
        return choice.options().get(0).id();
    }

    /** Option chosen for the most recent cast, "" if none. */
    public static String getActive(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return GeneralDataUtils.get(vars, DATA_KEY, "");
    }
}