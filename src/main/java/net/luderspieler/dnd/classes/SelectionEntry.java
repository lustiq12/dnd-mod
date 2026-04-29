package net.luderspieler.dnd.classes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Common interface for anything that can appear in a selection GUI.
 * Implement this for ClassDefinition, RaceDefinition, SubraceDefinition etc.
 */
public interface SelectionEntry {
    String getId();
    String getDisplayName();
    String getDescription();
    ResourceLocation getIcon();                  // e.g. dnd:textures/screens/icons/BarbarianIcon.png
    Map<String, Double> getAttributeModifiers(); // ordered: displayed in preview
    List<String> getAbilityLines();              // up to 10 lines shown in preview
    List<ItemStack> getStarterItems();
}