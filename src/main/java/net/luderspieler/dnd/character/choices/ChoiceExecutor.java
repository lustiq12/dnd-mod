package net.luderspieler.dnd.character.choices;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static net.luderspieler.dnd.character.network.CharacterCreationPacket.applyAttrs;

public class ChoiceExecutor {
    public static void apply(Player player, String choiceID, String selectedValue) {
        var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);

        switch (choiceID) {
            case "ABILITY_SCORE_IMPROVEMENT":
                // "Strength +2" -> "Strength"
                String stat = selectedValue.split(" ")[0];
                if (stat.equalsIgnoreCase("Strength")) vars.Strength += 2;
                if (stat.equalsIgnoreCase("Dexterity")) vars.Dexterity += 2;
                if (stat.equalsIgnoreCase("Constitution")) vars.Constitution += 2;
                if (stat.equalsIgnoreCase("Intelligence")) vars.Intelligence += 2;
                if (stat.equalsIgnoreCase("Wisdom")) vars.Wisdom += 2;
                if (stat.equalsIgnoreCase("Charisma")) vars.Charisma += 2;
                applyAttrs((ServerPlayer) player);
                break;

            case "SUBCLASS":
                vars.PlayerSubclass = selectedValue;
                break;
        }

    }
}
