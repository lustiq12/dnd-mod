package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class ClassDefinition {

    private final String id;
    private final String displayName;
    private final String description;
    private final ResourceLocation icon;
    private final Map<String, Double> attributeModifiers;
    private final List<String> abilityLines;
    private final List<ItemStack> starterItems;

    public ClassDefinition(String id, String displayName, String description,
                           Map<String, Double> attributeModifiers,
                           List<String> abilityLines,
                           List<ItemStack> starterItems) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        // this.icon = ResourceLocation.parse("dnd:textures/screens/icons/" + toPascalCase(id) + "Icon.png");
        this.icon = ResourceLocation.parse("dnd:textures/screens/classiconplaceholder.png");
        this.attributeModifiers = attributeModifiers;
        this.abilityLines = abilityLines;
        this.starterItems = starterItems;
    }

    static String toPascalCase(String id) {
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_"))
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        return sb.toString();
    }

    public String getId()                              { return id; }
    public String getDisplayName()                     { return displayName; }
    public String getDescription()                     { return description; }
    public ResourceLocation getIcon()                  { return icon; }
    public Map<String, Double> getAttributeModifiers() { return attributeModifiers; }
    public List<String> getAbilityLines()              { return abilityLines; }
    public List<ItemStack> getStarterItems()           { return starterItems; }
}