package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubraceDefinition {
    private final String id, parentRaceId, displayName, description, proficiencies;
    private final ResourceLocation icon;
    private final List<String> abilityLines;

    public SubraceDefinition(String id, String parentRaceId, String displayName,
                             String description, List<String> abilityLines,
                             String proficiencies) { // Keine Stats mehr im Konstruktor
        this.id = id;
        this.parentRaceId = parentRaceId;
        this.displayName = displayName;
        this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.abilityLines = abilityLines;
    }

    // Damit buildCombinedAttrs() im Screen nicht crashed, geben wir einfach leer zurück
    public Map<String, Integer> getAbilityScoreIncrements() {
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