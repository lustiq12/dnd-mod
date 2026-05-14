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
        // Hier definierst du deine Choices und deren Optionen
        REGISTRY.put("Attribute Increase", List.of("Attack Damage + 1", "Attack Speed + 0.1", "Speed + 10%", "Max Health + 4",""));
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
        return switch (ability) {
            case ATTRIBUTE_INCREASE -> "Attribute Increase";
            // Add more when ChoiceRegistry has them, e.g.:
            // case FIGHTING_STYLE   -> "Fighting Style";
            // case EXPERTISE        -> "Expertise";
            // case MAGICAL_SECRETS  -> "Magical Secrets";
            // case METAMAGIC        -> "Metamagic";
            default -> null;
        };
    }

}