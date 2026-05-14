package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.character.abilitys.Ability;
import net.luderspieler.dnd.character.abilitys.AdvancementRegistry;
import net.luderspieler.dnd.network.DndModVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceRegistry {
    private static final Map<String, List<String>> REGISTRY = new HashMap<>();

    static {
        // --- Basis / Gemeinsame Entscheidungen ---
        REGISTRY.put("Attribute Increase", List.of(
                "Strength + 2", "Dexterity + 2", "Constitution + 2",
                "Intelligence + 2", "Wisdom + 2", "Charisma + 2"
        ));

        REGISTRY.put("Fighting Style", List.of(
                "Archery", "Defense", "Dueling", "Great Weapon Fighting", "Protection", "Two-Weapon Fighting"
        ));

        REGISTRY.put("Expertise", List.of(
                "Acrobatics", "Athletics", "Stealth", "Perception", "Insight", "Persuasion"
        ));

        REGISTRY.put("Epic Boon", List.of(
                "Boon of Combat Prowess", "Boon of Dimensional Travel", "Boon of Fortitude", "Boon of Speed"
        ));

        // --- Klassen-Spezifische Entscheidungen ---
        // Cleric & Druid Orders
        REGISTRY.put("Divine Order", List.of("Protector", "Thaumaturge"));
        REGISTRY.put("Primal Order", List.of("Magician", "Warden"));

        // Warlock Pacts & Invocations
        REGISTRY.put("Pact Boon", List.of("Pact of the Blade", "Pact of the Chain", "Pact of the Tome"));
        REGISTRY.put("Eldritch Invocations", List.of("Agonizing Blast", "Armor of Shadows", "Eldritch Spear", "Fiendish Vigor"));

        // Wizard Scholar
        REGISTRY.put("Scholar", List.of("Arcana", "History", "Investigation", "Nature", "Religion"));

        // Sorcerer Metamagic
        REGISTRY.put("Metamagic", List.of("Careful Spell", "Distant Spell", "Empowered Spell", "Quickened Spell", "Twinned Spell"));

        // Bard Secrets
        REGISTRY.put("Magical Secrets", List.of("Learn any Spell from other Classes"));

        // --- Subclass Trigger ---
        // Dieser Key wird von SUBCLASS_FEATURE erzeugt
        REGISTRY.put("Subclass Feature", List.of("Open Subclass Selection Menu"));
    }

    public static List<String> getOptionsFor(String choiceId) {
        return REGISTRY.getOrDefault(choiceId, List.of("No Options Found"));
    }

    public static boolean hasChoice(String choiceId) {
        return REGISTRY.containsKey(choiceId);
    }

    public static void addChoicesForLevel(DndModVariables.PlayerVariables vars,
                                          String classId, int level) {
        List<Ability> abilities = AdvancementRegistry.getAbilities(classId, level);
        List<String> toAdd = new ArrayList<>();

        for (Ability ability : abilities) {
            String choiceId = choiceIdForAbility(ability);
            if (choiceId == null) continue;

            // Only add if ChoiceRegistry actually knows this choice
            if (ChoiceRegistry.hasChoice(choiceId)) {
                toAdd.add(choiceId);
            }
        }

        if (toAdd.isEmpty()) return;

        String existing = vars.ChoicesNeeded;
        if (existing == null || existing.isBlank() || existing.equals("\"\"")) {
            vars.ChoicesNeeded = String.join(",", toAdd);
        } else {
            vars.ChoicesNeeded = existing + "," + String.join(",", toAdd);
        }
    }

    private static String choiceIdForAbility(Ability ability) {
        if (ability == null) return null;


        String name = ability.name().replace("_", " ").toLowerCase();

        StringBuilder formatted = new StringBuilder();
        for (String word : name.split(" ")) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

}