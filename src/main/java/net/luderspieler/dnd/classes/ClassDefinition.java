package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.spells.Spells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Map;

public class ClassDefinition {
    // Grundlegende Identifikation & UI
    private final String id;
    private final String displayName;
    private final String description;
    private final ResourceLocation icon;

    // Stats & Kampf
    private final int classHealth;
    private final String proficiencies;
    private final Map<String, Double> attributeModifiers;
    private final List<String> abilityLines;
    private final List<ItemStack> starterItems;

    // Magie-System
    private final int[][] spellSlots;
    private final int[][] preparedAmount;
    private final List<Enum<?>> spellList;

    public ClassDefinition(String id, String displayName, String description, int classHealth,
                           Map<String, Double> attributeModifiers, List<String> abilityLines,
                           List<ItemStack> starterItems, String proficiencies,
                           int[][] spellSlots, int[][] preparedAmount, List<Enum<?>> spellList) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.classHealth = classHealth;
        this.attributeModifiers = attributeModifiers;
        this.abilityLines = abilityLines;
        this.starterItems = starterItems;
        this.proficiencies = proficiencies;
        this.spellSlots = spellSlots;
        this.preparedAmount = preparedAmount;
        this.spellList = spellList;
        this.icon = ResourceLocation.fromNamespaceAndPath("dnd", "textures/screens/classiconplaceholder.png");
    }

    // --- MAGIE LOGIK ---

    public int getMaxPreparedForGrade(int level, int grade) {
        if (level < 0 || level >= preparedAmount.length || grade < 0 || grade > 9) return 0;
        return preparedAmount[level][grade];
    }

    public int[] getLimitsForLevel(int level) {
        return (level >= 0 && level < preparedAmount.length) ? preparedAmount[level] : new int[10];
    }

    public int getTotalPreparedSpells(int level) {
        int total = 0;
        int[] limits = getLimitsForLevel(level);
        for (int i = 1; i <= 9; i++) total += limits[i];
        return total;
    }

    /**
     * Prüft, ob basierend auf dem gespeicherten String noch Platz für einen Zauber dieses Grads ist.
     */
    public boolean canPrepareMore(String currentSpellsString, int level, int grade) {
        int maxAllowed = getMaxPreparedForGrade(level, grade);
        if (maxAllowed <= 0) return false;
        if (currentSpellsString == null || currentSpellsString.isEmpty()) return true;

        long count = java.util.Arrays.stream(currentSpellsString.split(","))
                .filter(spell -> getGradeOfSpell(spell.trim()) == grade)
                .count();

        return count < maxAllowed;
    }

    public List<String> getSpellsForGrade(int grade) {
        return this.spellList.stream()
                .filter(s -> getGradeOfSpell(s.name()) == grade)
                .map(Enum::name)
                .toList();
    }

    public int getGradeOfSpell(String spellName) {
        if (checkEnum(Spells.Cantrip.class, spellName)) return 0;
        if (checkEnum(Spells.Grade1.class, spellName))  return 1;
        if (checkEnum(Spells.Grade2.class, spellName))  return 2;
        if (checkEnum(Spells.Grade3.class, spellName))  return 3;
        if (checkEnum(Spells.Grade4.class, spellName))  return 4;
        if (checkEnum(Spells.Grade5.class, spellName))  return 5;
        if (checkEnum(Spells.Grade6.class, spellName))  return 6;
        if (checkEnum(Spells.Grade7.class, spellName))  return 7;
        if (checkEnum(Spells.Grade8.class, spellName))  return 8;
        if (checkEnum(Spells.Grade9.class, spellName))  return 9;
        return -1;
    }

    private boolean checkEnum(Class<? extends Enum<?>> enumClass, String name) {
        for (Enum<?> e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    // --- GETTER ---

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getClassHealth() { return classHealth; }
    public ResourceLocation getIcon() { return icon; }
    public String getProficiencies() { return proficiencies; }
    public Map<String, Double> getAttributeModifiers() { return attributeModifiers; }
    public List<String> getAbilityLines() { return abilityLines; }
    public List<ItemStack> getStarterItems() { return starterItems; }
    public int[][] getSpellSlots() { return spellSlots; }
    public int[][] getPreparedAmount() { return preparedAmount; }
    public List<Enum<?>> getSpellList() { return spellList; }
}