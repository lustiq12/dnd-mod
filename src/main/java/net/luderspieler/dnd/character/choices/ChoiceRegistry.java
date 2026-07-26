package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Liefert für jede ChoiceID die wählbaren Optionen.
 *
 * WICHTIG: Die Subklassen-Namen sind 1:1 die Display-Namen aus ClassRegistry
 * (dritter Parameter im SubclassDefinition-Konstruktor). Abweichungen
 * würden resolveSubclassId() in AbilityUtils zum Scheitern bringen.
 */
public class ChoiceRegistry {

    // ── Subklassen: exakt wie in ClassRegistry.SUBCLASSES ────────────────────
    private static final Map<String, List<String>> SUBCLASSES = new LinkedHashMap<>();
    static {
        SUBCLASSES.put("barbarian", List.of(
                "Path of the Berserker", "Path of the Wild Heart",
                "Path of the World Tree", "Path of the Zealot"));
        SUBCLASSES.put("bard", List.of(
                "College of Dance", "College of Glamour",
                "College of Lore", "College of Valor"));
        SUBCLASSES.put("cleric", List.of(
                "Life Domain", "Light Domain",
                "Trickery Domain", "War Domain"));
        SUBCLASSES.put("druid", List.of(
                "Circle of the Land", "Circle of the Moon",
                "Circle of the Sea", "Circle of the Stars"));
        SUBCLASSES.put("fighter", List.of(
                "Battle Master", "Champion",
                "Eldritch Knight", "Psi Warrior"));
        SUBCLASSES.put("monk", List.of(
                "Warrior of Mercy", "Warrior of Shadow",
                "Warrior of the Elements", "Warrior of the Open Hand"));
        SUBCLASSES.put("paladin", List.of(
                "Oath of Devotion", "Oath of Glory",
                "Oath of the Ancients", "Oath of Vengeance"));
        SUBCLASSES.put("ranger", List.of(
                "Beast Master", "Fey Wanderer",
                "Gloom Stalker", "Hunter"));
        SUBCLASSES.put("rogue", List.of(
                "Arcane Trickster", "Assassin",
                "Soulknife", "Thief"));
        SUBCLASSES.put("sorcerer", List.of(
                "Draconic Sorcery", "Wild Magic",
                "Aberrant Sorcery", "Clockwork Sorcery"));
        SUBCLASSES.put("warlock", List.of(
                "Archfey Patron", "Fiend Patron",
                "Great Old One Patron", "Celestial Patron"));
        SUBCLASSES.put("wizard", List.of(
                "Abjurer", "Diviner",
                "Evoker", "Illusionist"));
    }

    // ── Metamagic (alle Optionen nach 2024 PHB) ───────────────────────────────
    private static final List<String> ALL_METAMAGIC = List.of(
            "Careful Spell", "Distant Spell", "Empowered Spell", "Extended Spell",
            "Heightened Spell", "Quickened Spell", "Seeking Spell", "Subtle Spell",
            "Transmuted Spell", "Twinned Spell"
    );

    // ─────────────────────────────────────────────────────────────────────────

    public static List<String> getOptions(String choiceId) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return List.of("Error: No Player");

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String upperId = choiceId.toUpperCase();

        return switch (upperId) {

            case "SUBCLASS" -> SUBCLASSES.getOrDefault(vars.PlayerClass,
                    List.of("No subclasses defined for: " + vars.PlayerClass));

            // GenericChoicePopup handled intern (3-Stufen-Ablauf), kein List nötig.
            case "ABILITY_SCORE_IMPROVEMENT_OR_FEAT" -> List.of();

            // METAMAGIC: bereits gewählte Options ausfiltern.
            // Trenner = Semikolon (Komma ist AbilityData-Top-Level-Trenner).
            case "METAMAGIC" -> {
                String chosen = AbilityDataUtils.get(vars, "METAMAGIC_chosen", "");
                if (chosen.isBlank()) yield new ArrayList<>(ALL_METAMAGIC);
                Set<String> chosenSet = Arrays.stream(chosen.split(";"))
                        .map(String::trim).collect(Collectors.toSet());
                yield ALL_METAMAGIC.stream()
                        .filter(o -> !chosenSet.contains(o))
                        .collect(Collectors.toList());
            }

            case "FIGHTING_STYLE" -> List.of(
                    "Archery", "Defense", "Dueling",
                    "Great Weapon Fighting", "Protection", "Two-Weapon Fighting");

            case "ELDRITCH_INVOCATION" -> {
                List<String> all = List.of(
                        "Agonizing Blast", "Armor of Shadows", "Beast Speech",
                        "Devil's Sight", "Mask of Many Faces");
                String chosen = AbilityDataUtils.get(vars, "EldritchInvocations_chosen", "");
                if (chosen.isBlank()) yield new ArrayList<>(all);
                Set<String> chosenSet = Arrays.stream(chosen.split(";"))
                        .map(String::trim).collect(Collectors.toSet());
                yield all.stream().filter(o -> !chosenSet.contains(o)).collect(Collectors.toList());
            }

            // Klassen-spezifische Choices
            case "HOLY_ORDER"        -> List.of("Protector", "Scholar", "Thaumaturge");
            case "PRIMAL_ORDER"      -> List.of("Magician", "Warden");
            case "RANGER_COMPANION"  -> List.of("Beast of the Land", "Beast of the Sea", "Beast of the Sky");
            case "RANGER_EXPERTISE"  -> List.of("Stealth", "Survival", "Perception", "Nature", "Investigation");
            case "MONK_WEAPON"       -> List.of("Simple Weapons", "Short Swords");

            case "TOOL_PROFICIENCY" -> {
                List<String> all = List.of(
                        "Thieves' Tools", "Alchemist's Supplies",
                        "Smith's Tools", "Brewer's Supplies");
                String existing = vars.Proficiencys == null ? "" : vars.Proficiencys;
                yield all.stream()
                        .filter(o -> !existing.contains(toProficiencyKey(o)))
                        .collect(Collectors.toList());
            }

            case "BARDIC_COLLEGE_SKILL" -> {
                List<String> all = List.of(
                        "Acrobatics", "Animal Handling", "Arcana", "Athletics",
                        "Deception", "History", "Insight", "Intimidation",
                        "Investigation", "Medicine", "Nature", "Perception",
                        "Performance", "Persuasion", "Religion", "Sleight of Hand",
                        "Stealth", "Survival");
                String chosen = AbilityDataUtils.get(vars, "BardExpertiseSkills_chosen", "");
                if (chosen.isBlank()) yield new ArrayList<>(all);
                Set<String> chosenSet = Arrays.stream(chosen.split(";"))
                        .map(String::trim).collect(Collectors.toSet());
                yield all.stream().filter(o -> !chosenSet.contains(o)).collect(Collectors.toList());
            }

            case "DRACONIC_ANCESTRY" -> List.of(
                    "Black Dragon", "Blue Dragon", "Brass Dragon", "Bronze Dragon", "Copper Dragon",
                    "Gold Dragon", "Green Dragon", "Red Dragon", "Silver Dragon", "White Dragon");

            default -> List.of("No options defined for: " + choiceId);
        };
    }

    /** Normalizes a display name into the snake_case key format used in vars.Proficiencys. */
    private static String toProficiencyKey(String displayName) {
        return displayName.trim().toLowerCase().replace("'", "").replace(" ", "_");
    }
}