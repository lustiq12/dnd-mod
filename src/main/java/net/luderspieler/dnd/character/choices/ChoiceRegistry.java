package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AttributeHandler;
import net.luderspieler.dnd.character.feats.FeatRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ChoiceRegistry {

    private static final Map<String, List<String>> SUBCLASSES = new HashMap<>();

    private static final List<String> ALL_METAMAGIC = List.of(
            "Careful Spell", "Distant Spell", "Empowered Spell", "Extended Spell",
            "Heightened Spell", "Quickened Spell", "Seeking Spell", "Subtle Spell",
            "Transmuted Spell", "Twinned Spell"
    );

    static {
        SUBCLASSES.put("barbarian", Arrays.asList("Berserker", "Wild Heart", "World Tree"));
        SUBCLASSES.put("cleric",    Arrays.asList("Life Domain", "Light Domain", "War Domain"));
        SUBCLASSES.put("fighter",   Arrays.asList("Champion", "Battle Master", "Eldritch Knight"));
        SUBCLASSES.put("warlock",   Arrays.asList("Archfey", "Fiend", "Great Old One"));
        SUBCLASSES.put("sorcerer",  Arrays.asList("Draconic Sorcery", "Wild Magic"));
        SUBCLASSES.put("druid",     Arrays.asList("Circle of the Land", "Circle of the Moon"));
        SUBCLASSES.put("paladin",   Arrays.asList("Oath of Devotion", "Oath of the Ancients"));
    }

    public static List<String> getOptions(String choiceId) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return List.of("Error: No Player");

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String upperId = choiceId.toUpperCase();

        return switch (upperId) {

            case "SUBCLASS" -> SUBCLASSES.getOrDefault(vars.PlayerClass,
                    List.of("No subclasses for class: " + vars.PlayerClass));

            // ABILITY_SCORE_IMPROVEMENT_OR_FEAT wird vollständig in
            // GenericChoicePopup behandelt (dreistufiger Ablauf) — diese
            // Methode wird dafür nicht aufgerufen, aber der leere Default
            // schadet nicht.
            case "ABILITY_SCORE_IMPROVEMENT_OR_FEAT" -> List.of();

            // ── METAMAGIC ──────────────────────────────────────────────
            // METAMAGIC_chosen nutzt SEMIKOLON als internen Trenner.
            // Daher hier split(";"), nicht split(",").
            case "METAMAGIC" -> {
                String chosen = AbilityDataUtils.get(vars, "METAMAGIC_chosen", "");
                if (chosen.isBlank()) yield new ArrayList<>(ALL_METAMAGIC);

                Set<String> chosenSet = Arrays.stream(chosen.split(";"))
                        .map(String::trim)
                        .collect(Collectors.toSet());

                yield ALL_METAMAGIC.stream()
                        .filter(o -> !chosenSet.contains(o))
                        .collect(Collectors.toList());
            }

            case "FIGHTING_STYLE" -> Arrays.asList(
                    "Archery", "Defense", "Dueling",
                    "Great Weapon Fighting", "Protection", "Two-Weapon Fighting");

            case "ELDRITCH_INVOCATION" -> Arrays.asList(
                    "Agonizing Blast", "Armor of Shadows", "Beast Speech",
                    "Devil's Sight", "Mask of Many Faces");

            case "HOLY_ORDER"        -> Arrays.asList("Protector", "Scholar", "Thaumaturge");
            case "PRIMAL_ORDER"      -> Arrays.asList("Magician", "Warden");
            case "PRACTICED_SCHOLAR" -> Arrays.asList("Arcana", "History", "Nature", "Religion");
            case "TOOL_PROFICIENCY"  -> Arrays.asList(
                    "Thieves' Tools", "Alchemist's Supplies",
                    "Smith's Tools", "Brewer's Supplies");

            default -> List.of("No options for: " + choiceId);
        };
    }
}