package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SubraceDefinition {
    private final String id, parentRaceId, displayName, description, proficiencies;
    private final ResourceLocation icon;
    private final List<String> abilityLines;
    // NEU: Hier werden die Stats (Strength, Dexterity etc.) gespeichert
    private final Map<String, Integer> abilityScoreIncrements;

    public SubraceDefinition(String id, String parentRaceId, String displayName,
                             String description, List<String> abilityLines,
                             String proficiencies, Map<String, Integer> stats) {
        this.id = id;
        this.parentRaceId = parentRaceId;
        this.displayName = displayName;
        this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.abilityLines = abilityLines;
        // Falls stats null ist, erstellen wir eine leere Map, um Fehler zu vermeiden
        this.abilityScoreIncrements = stats != null ? stats : new HashMap<>();
    }

    // Das Packet ruft diese Methode auf:
    public Map<String, Integer> getAbilityScoreIncrements() {
        return abilityScoreIncrements;
    }

    // Diese Methode löschen wir oder lassen sie leer, da du keine Doubles mehr nutzt
    public Map<String, Double> getAttributeModifiers() {
        return new HashMap<>();
    }

    public String getId()                 { return id; }
    public String getParentRaceId()       { return parentRaceId; }
    public String getDisplayName()        { return displayName; }
    public String getDescription()        { return description; }
    public ResourceLocation getIcon()     { return icon; }
    public List<String> getAbilityLines() { return abilityLines; }
    public String getProficiencies()      { return proficiencies; }
}