package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class SubclassDefinition {
    private final String id, parentClassId, displayName, description, proficiencies;
    private final ResourceLocation icon;
    private final List<String> abilityLines;

    public SubclassDefinition(String id, String parentClassId, String displayName,
                              String description, List<String> abilityLines, String proficiencies) {
        this.id = id; this.parentClassId = parentClassId;
        this.displayName = displayName; this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.abilityLines = abilityLines;
    }

    public String getId()                 { return id; }
    public String getParentClassId()      { return parentClassId; }
    public String getDisplayName()        { return displayName; }
    public String getDescription()        { return description; }
    public ResourceLocation getIcon()     { return icon; }
    public List<String> getAbilityLines() { return abilityLines; }
    public String getProficiencies()      { return proficiencies; }
}