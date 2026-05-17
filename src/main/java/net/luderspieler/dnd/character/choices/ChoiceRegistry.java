package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceRegistry {
    private static final Map<String, List<String>> SUBCLASSES = new HashMap<>();

    static {
        // Beispiel-Subklassen (Erweitere diese Liste nach Bedarf)
        SUBCLASSES.put("Barbarian", Arrays.asList("Berserker", "Wild Heart", "World Tree"));
        SUBCLASSES.put("Cleric", Arrays.asList("Life Domain", "Light Domain", "War Domain"));
        SUBCLASSES.put("Fighter", Arrays.asList("Champion", "Battle Master", "Eldritch Knight"));
        SUBCLASSES.put("Warlock", Arrays.asList("Archfey", "Fiend", "Great Old One"));
        SUBCLASSES.put("Sorcerer", Arrays.asList("Draconic Sorcery", "Wild Magic"));
        SUBCLASSES.put("Druid", Arrays.asList("Circle of the Land", "Circle of the Moon"));
        SUBCLASSES.put("Paladin", Arrays.asList("Oath of Devotion", "Oath of the Ancients"));
    }

    public static List<String> getOptions(String choiceId) {
        var player = Minecraft.getInstance().player;
        if (player == null) return Arrays.asList("Error: No Player");

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String upperId = choiceId.toUpperCase();

        return switch (upperId) {
            case "SUBCLASS" ->
                    SUBCLASSES.getOrDefault(vars.PlayerClass, Arrays.asList("No Subclasses for " + vars.PlayerClass));

            case "ABILITY_SCORE_IMPROVEMENT" ->
                    Arrays.asList("Strength +2", "Dexterity +2", "Constitution +2", "Intelligence +2", "Wisdom +2", "Charisma +2");

            case "FEAT" ->
                    Arrays.asList("Tough", "Skilled", "Lucky", "Alert", "War Caster", "Sentinel");

            case "FIGHTING_STYLE" ->
                    Arrays.asList("Archery", "Defense", "Dueling", "Great Weapon Fighting", "Protection", "Two-Weapon Fighting");

            case "ELDRITCH_INVOCATION" ->
                    Arrays.asList("Agonizing Blast", "Armor of Shadows", "Beast Speech", "Devil's Sight", "Mask of Many Faces");

            case "METAMAGIC" ->
                    Arrays.asList("Careful Spell", "Distant Spell", "Empowered Spell", "Extended Spell", "Heightened Spell", "Quickened Spell");

            case "HOLY_ORDER" ->
                    Arrays.asList("Protector", "Scholar", "Thaumaturge");

            case "PRIMAL_ORDER" ->
                    Arrays.asList("Magician", "Warden");

            case "PRACTICED_SCHOLAR" ->
                    Arrays.asList("Arcana", "History", "Nature", "Religion");

            case "TOOL_PROFICIENCY" ->
                    Arrays.asList("Thieves' Tools", "Alchemist's Supplies", "Smith's Tools", "Brewer's Supplies");

            default -> Arrays.asList("No options for: " + choiceId);
        };
    }
}