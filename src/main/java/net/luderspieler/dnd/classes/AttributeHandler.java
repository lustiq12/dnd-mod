package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class AttributeHandler {

    /**
     * 1. Attribute(name) -> Gibt den finalen Wert zurück (Basis + Boni).
     */
    public static int getAttribute(Player player, String attributeName) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Basis-Wert (das was gewürfelt wurde)
        int baseValue = (int) switch (attributeName.toLowerCase()) {
            case "strength" -> vars.Strength;
            case "dexterity" -> vars.Dexterity;
            case "constitution" -> vars.Constitution;
            case "intelligence" -> vars.Intelligence;
            case "wisdom" -> vars.Wisdom;
            case "charisma" -> vars.Charisma;
            default -> 10;
        };

        // Bonus von der Klasse holen
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass);
        int classBonus = (classDef != null) ? classDef.getAbilityScoreIncrements().getOrDefault(attributeName, 0) : 0;

        // Bonus von der Rasse holen (Falls du RaceRegistry hast)
        RaceDefinition raceDef = RaceRegistry.getRace(vars.PlayerRace);
        int raceBonus = (raceDef != null) ? raceDef.getAbilityScoreIncrements().getOrDefault(attributeName, 0) : 0;

        return baseValue + classBonus + raceBonus;
    }

    /**
     * 2. Attributebonus(name) -> Der klassische D&D Modifier (+1, +2, etc.)
     */
    public static int getAttributeBonus(Player player, String attributeName) {
        int totalScore = getAttribute(player, attributeName);
        return Math.floorDiv(totalScore - 10, 2);
    }

    /**
     * 3. SpellCastingModifier() -> Findet das Attribut der Klasse und gibt dessen Bonus zurück.
     */
    public static int getSpellCastingModifier(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass);

        if (classDef == null || classDef.getSpellcastingAttribute() == null) return 0;

        return getAttributeBonus(player, classDef.getSpellcastingAttribute());
    }

    /**
     * 4. SpellSavingThrow() -> 8 + Proficiency + Spellcasting Modifier.
     */
    public static int getSpellSavingThrow(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return 8 + (int) vars.ProficiencyBonus + getSpellCastingModifier(player);
    }
}