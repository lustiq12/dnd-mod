package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

public class ChoiceUpdateSystem {

    public static void updateChoices(Player player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Anpassung an deine Variablennamen
        int level = (int) vars.PlayerLevel;
        String species = vars.PlayerRace;
        String subrace = vars.PlayerSubrace; // Wichtig für Human/Goliath etc.
        String clazz = vars.PlayerClass;

        List<String> needed = new ArrayList<>();

        // --- 1. SUBCLASS CHECK ---
        // In 2024 wählen ALLE Klassen ihre Subclass auf Level 3
        if (level >= 3 && !hasMadeChoice(vars.ChoicesMade, "SUBCLASS")) {
            needed.add("SUBCLASS");
        }

        // --- 2. ASI / FEAT CHECK ---
        int maxASIs = calculateMaxASIs(level, clazz, species, subrace);
        int currentASIs = countChoices(vars.ChoicesMade, "ABILITY_SCORE_IMPROVEMENT");

        // Füge so viele ASI-Choices hinzu, wie noch offen sind
        for (int i = 0; i < (maxASIs - currentASIs); i++) {
            needed.add("ABILITY_SCORE_IMPROVEMENT");
        }

        // --- 3. SPEZIFISCHE KLASSEN-CHOICES (Beispiele) ---
        if (clazz.equals("Fighter") && level >= 1 && !hasMadeChoice(vars.ChoicesMade, "FIGHTING_STYLE")) {
            needed.add("FIGHTING_STYLE");
        }

        if (clazz.equals("Warlock") && level >= 1 && !hasMadeChoice(vars.ChoicesMade, "ELDRITCH_INVOCATION")) {
            // Hier müsste man die Anzahl der Invocations pro Level prüfen
            int maxInvocations = (level >= 1) ? 2 : 0; // Vereinfachtes Beispiel
            int currentInvocations = countChoices(vars.ChoicesMade, "ELDRITCH_INVOCATION");
            for (int i = 0; i < (maxInvocations - currentInvocations); i++) {
                needed.add("ELDRITCH_INVOCATION");
            }
        }

        // Ergebnisse speichern
        vars.ChoicesNeeded = String.join(",", needed);
        vars.markSyncDirty();
    }

    private static int calculateMaxASIs(int level, String clazz, String species, String subrace) {
        int count = 0;

        // Der menschliche Bonus (oft ein Start-Feat oder ASI auf Level 1)
        if ("Human".equalsIgnoreCase(species)) {
            count += 1;
        }

        // Standard ASI Progression (2024 PHB)
        if (level >= 4) count++;
        if (level >= 8) count++;
        if (level >= 12) count++;
        if (level >= 16) count++;
        if (level >= 19) count++;

        // Fighter Extra ASIs (Level 6 und 14)
        if ("Fighter".equals(clazz)) {
            if (level >= 6) count++;
            if (level >= 14) count++;
        }

        // Rogue Extra ASI (Level 10)
        if ("Rogue".equals(clazz) && level >= 10) {
            count++;
        }

        return count;
    }

    private static boolean hasMadeChoice(String choicesMade, String choiceId) {
        if (choicesMade == null || choicesMade.isBlank()) return false;
        for (String s : choicesMade.split(",")) {
            if (s.trim().startsWith(choiceId + ":")) return true;
        }
        return false;
    }

    private static int countChoices(String choicesMade, String choiceId) {
        if (choicesMade == null || choicesMade.isBlank()) return 0;
        int count = 0;
        for (String s : choicesMade.split(",")) {
            if (s.trim().startsWith(choiceId + ":")) count++;
        }
        return count;
    }
}