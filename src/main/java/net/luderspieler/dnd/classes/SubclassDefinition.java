package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class SubclassDefinition {

    private final String id;
    private final String parentClassId;
    private final String displayName;
    private final String description;
    private final ResourceLocation icon;
    private final List<String> abilityLines;

    public SubclassDefinition(String id, String parentClassId, String displayName,
                              String description, List<String> abilityLines) {
        this.id = id;
        this.parentClassId = parentClassId;
        this.displayName = displayName;
        this.description = description;
        // this.icon = ResourceLocation.parse("dnd:textures/screens/icons/" + toPascalCase(id) + "SubclassIcon.png");
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.abilityLines = abilityLines;
    }

    private static String toPascalCase(String id) {
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_"))
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        return sb.toString();
    }

    public String getId()                 { return id; }
    public String getParentClassId()      { return parentClassId; }
    public String getDisplayName()        { return displayName; }
    public String getDescription()        { return description; }
    public ResourceLocation getIcon()     { return icon; }
    public List<String> getAbilityLines() { return abilityLines; }
}