package net.luderspieler.dnd.character.definition;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class RaceDefinition {
    private final String id;
    private final String displayName;
    private final String description;
    private final String proficiencies;
    private final ResourceLocation icon;
    private final Map<String, Integer> abilityScoreIncrements; // Die Boni der Rasse
    private final List<String> abilityLines;

    public RaceDefinition(String id, String displayName, String description,
                          Map<String, Integer> abilityScoreIncrements, List<String> abilityLines,
                          String proficiencies) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.abilityScoreIncrements = abilityScoreIncrements;
        this.abilityLines = abilityLines;
    }

    /**
     * Statische Hilfsmethode zur Berechnung des D&D Modifiers.
     * Formel: floor((Score - 10) / 2)
     */
    public static int getModifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ResourceLocation getIcon() { return icon; }

    /**
     * Gibt die Attribut-Boni der Rasse zurück.
     */
    public Map<String, Integer> getAbilityScoreIncrements() {
        return abilityScoreIncrements;
    }

    public List<String> getAbilityLines() { return abilityLines; }
    public String getProficiencies() { return proficiencies; }
}