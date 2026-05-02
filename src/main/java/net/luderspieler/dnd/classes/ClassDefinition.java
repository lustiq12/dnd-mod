package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.spells.Spells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Map;

public class ClassDefinition {
    private final String id, displayName, description, proficiencies;
    private final ResourceLocation icon;
    private final Map<String, Double> attributeModifiers;
    private final List<String> abilityLines;
    private final List<ItemStack> starterItems;
    private final int[][] spellSlots;

    // Neues Format: [Level 0-20][Grad 0-9]
    private final int[][] preparedAmount;
    private final List<Enum<?>> spellList;

    public ClassDefinition(String id, String displayName, String description,
                           Map<String, Double> attributeModifiers, List<String> abilityLines,
                           List<ItemStack> starterItems, String proficiencies,
                           int[][] spellSlots, int[][] preparedAmount, List<Enum<?>> spellList) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.proficiencies = proficiencies;
        this.icon = ResourceLocation.fromNamespaceAndPath("dnd", "textures/screens/classiconplaceholder.png");
        this.attributeModifiers = attributeModifiers;
        this.abilityLines = abilityLines;
        this.starterItems = starterItems;
        this.spellSlots = spellSlots;
        this.preparedAmount = preparedAmount;
        this.spellList = spellList;
    }

    // --- HILFSMETHODEN FÜR DAS SPELL-SYSTEM ---

    /**
     * Gibt zurück, wie viele Zauber eines bestimmten Grads auf einem bestimmten Level vorbereitet werden dürfen.
     * @param level Das aktuelle Level des Spielers (1-20)
     * @param grade Der Zaubergrad (0 für Cantrip, 1-9 für Grades)
     */
    public int getMaxPreparedForGrade(int level, int grade) {
        if (level < 0 || level >= preparedAmount.length) return 0;
        if (grade < 0 || grade > 9) return 0;
        return preparedAmount[level][grade];
    }

    /**
     * Gibt das gesamte Limit-Array für ein bestimmtes Level zurück.
     * Index 0 = Cantrips, Index 1-9 = Grades.
     */
    public int[] getLimitsForLevel(int level) {
        if (level < 0 || level >= preparedAmount.length) return new int[10];
        return preparedAmount[level];
    }

    /**
     * Prüft, ob die Klasse auf diesem Level überhaupt Zauber dieses Grads wirken kann.
     */
    public boolean canCastGrade(int level, int grade) {
        return getMaxPreparedForGrade(level, grade) > 0;
    }

    /**
     * Berechnet die Gesamtzahl aller vorbereitbaren Zauber (exklusive Cantrips) für ein Level.
     */
    public int getTotalPreparedSpells(int level) {
        int total = 0;
        int[] limits = getLimitsForLevel(level);
        for (int i = 1; i <= 9; i++) { // Startet bei 1, um Cantrips zu ignorieren
            total += limits[i];
        }
        return total;
    }

    /**
     * Prüft, ob ein weiterer Zauber eines bestimmten Grads hinzugefügt werden darf.
     * @param currentSpellsString Der String aus der MCreator-Variable (z.B. "FIREBALL,FLY,LIGHT")
     * @param level Das aktuelle Level des Spielers
     * @param grade Der Grad des Zaubers, den man hinzufügen möchte
     * @return true, wenn noch Platz ist, false wenn das Limit erreicht wurde.
     */
    public boolean canPrepareMore(String currentSpellsString, int level, int grade) {
        int maxAllowed = getMaxPreparedForGrade(level, grade);

        // Wenn das Limit 0 ist (z.B. Paladin auf Level 1 hat keine Grad 2 Slots), direkt false
        if (maxAllowed <= 0) return false;

        // Falls der String leer ist, haben wir definitiv noch Platz
        if (currentSpellsString == null || currentSpellsString.isEmpty()) return true;

        // Wir zerlegen den String und zählen, wie viele Zauber des gesuchten Grads schon drin sind
        String[] preparedArray = currentSpellsString.split(",");
        int count = 0;

        for (String spellName : preparedArray) {
            // Hier ist der Clou: Wir müssen herausfinden, welchen Grad der gespeicherte Spell hat.
            // Das geht am besten über eine kleine Hilfsfunktion (siehe unten).
            if (getGradeOfSpell(spellName) == grade) {
                count++;
            }
        }

        return count < maxAllowed;
    }

    public List<String> getSpellsForGrade(int grade) {
        return this.spellList.stream() // Hier ist der richtige Name der Liste!
                .filter(s -> this.getGradeOfSpell(s.name()) == grade) // Nutzt deine interne Hilfsmethode
                .map(Enum::name) // Konvertiert das Enum-Objekt in einen String
                .toList();
    }

    /**
     * Hilfsmethode, um den Grad eines Zaubers anhand seines Namens zu finden.
     * Diese Methode muss alle Enums in deiner Spells-Klasse durchsuchen.
     */
    public int getGradeOfSpell(String spellName) {
        try {
            if (checkEnum(Spells.Cantrip.class, spellName)) return 0;
            if (checkEnum(Spells.Grade1.class, spellName)) return 1;
            if (checkEnum(Spells.Grade2.class, spellName)) return 2;
            if (checkEnum(Spells.Grade3.class, spellName)) return 3;
            if (checkEnum(Spells.Grade4.class, spellName)) return 4;
            if (checkEnum(Spells.Grade5.class, spellName)) return 5;
            if (checkEnum(Spells.Grade6.class, spellName)) return 6;
            if (checkEnum(Spells.Grade7.class, spellName)) return 7;
            if (checkEnum(Spells.Grade8.class, spellName)) return 8;
            if (checkEnum(Spells.Grade9.class, spellName)) return 9;
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    private boolean checkEnum(Class<? extends Enum<?>> enumClass, String name) {
        for (Enum<?> e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    // --- STANDARD GETTER ---
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ResourceLocation getIcon() { return icon; }
    public Map<String, Double> getAttributeModifiers() { return attributeModifiers; }
    public List<String> getAbilityLines() { return abilityLines; }
    public List<ItemStack> getStarterItems() { return starterItems; }
    public String getProficiencies() { return proficiencies; }
    public int[][] getSpellSlots() { return spellSlots; }
    public int[][] getPreparedAmount() { return preparedAmount; }
    public List<Enum<?>> getSpellList() { return spellList; }
}