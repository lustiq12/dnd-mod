package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public class RaceDefinition {
    private final String id, displayName, description, proficiencies;
    private final ResourceLocation icon;
    private final Map<String, Double> attributeModifiers;
    private final List<String> abilityLines;

    public RaceDefinition(String id, String displayName, String description,
                          Map<String, Double> attributeModifiers, List<String> abilityLines,
                          String proficiencies) {
        this.id = id; this.displayName = displayName; this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.attributeModifiers = attributeModifiers; this.abilityLines = abilityLines;
    }

    public String getId()                              { return id; }
    public String getDisplayName()                     { return displayName; }
    public String getDescription()                     { return description; }
    public ResourceLocation getIcon()                  { return icon; }
    public Map<String, Double> getAttributeModifiers() { return attributeModifiers; }
    public List<String> getAbilityLines()              { return abilityLines; }
    public String getProficiencies()                   { return proficiencies; }
}