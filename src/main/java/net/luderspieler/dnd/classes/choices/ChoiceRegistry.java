package net.luderspieler.dnd.classes.choices;

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
}