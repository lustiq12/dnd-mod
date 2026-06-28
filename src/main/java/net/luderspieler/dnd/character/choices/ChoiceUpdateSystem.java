package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

public class ChoiceUpdateSystem {

    public static void updateChoices(Player player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int    level  = (int) vars.PlayerLevel;
        String clazz  = vars.PlayerClass; // immer lowercase

        List<String> needed = new ArrayList<>();

        // ── 1. SUBCLASS ───────────────────────────────────────────────
        // Alle 2024-PHB-Klassen wählen ihre Subclass auf Level 3.
        if (level >= 3 && !hasMadeChoice(vars.ChoicesMade, "SUBCLASS")) {
            needed.add("SUBCLASS");
        }

        // ── 2. ABILITY SCORE IMPROVEMENT OR FEAT ──────────────────────
        // Die neue Choice-ID ersetzt "ABILITY_SCORE_IMPROVEMENT".
        // Standard 2024 PHB: Level 4 / 8 / 12 / 16 / 19.
        int maxAsi = calculateMaxASIs(level, clazz);
        int curAsi = countChoices(vars.ChoicesMade, "ABILITY_SCORE_IMPROVEMENT_OR_FEAT");
        for (int i = 0; i < (maxAsi - curAsi); i++) {
            needed.add("ABILITY_SCORE_IMPROVEMENT_OR_FEAT");
        }

        // ── 3. KLASSEN-SPEZIFISCHE CHOICES ───────────────────────────
        if ("fighter".equals(clazz) && level >= 1
                && !hasMadeChoice(vars.ChoicesMade, "FIGHTING_STYLE")) {
            needed.add("FIGHTING_STYLE");
        }

        if ("warlock".equals(clazz)) {
            int maxInv = level >= 1 ? 2 : 0;
            int curInv = countChoices(vars.ChoicesMade, "ELDRITCH_INVOCATION");
            for (int i = 0; i < (maxInv - curInv); i++) needed.add("ELDRITCH_INVOCATION");
        }

        // ── 4. SORCERER: METAMAGIC ────────────────────────────────────
        // 2024 PHB: 2 Optionen ab Level 2, 3 ab Level 10, 4 ab Level 17.
        if ("sorcerer".equals(clazz)) {
            int maxMeta = level >= 17 ? 4 : level >= 10 ? 3 : level >= 2 ? 2 : 0;
            int curMeta = countChoices(vars.ChoicesMade, "METAMAGIC");
            for (int i = 0; i < (maxMeta - curMeta); i++) needed.add("METAMAGIC");
        }

        vars.ChoicesNeeded = String.join(",", needed);
        vars.markSyncDirty();
    }

    // ─────────────────────────────────────────────────────────────────

    private static int calculateMaxASIs(int level, String clazz) {
        int count = 0;
        if (level >= 4)  count++;
        if (level >= 8)  count++;
        if (level >= 12) count++;
        if (level >= 16) count++;
        if (level >= 19) count++;

        // Fighter Extra ASIs (Level 6 + 14)
        if ("fighter".equals(clazz)) {
            if (level >= 6)  count++;
            if (level >= 14) count++;
        }
        // Rogue Extra ASI (Level 10)
        if ("rogue".equals(clazz) && level >= 10) count++;

        return count;
    }

    private static boolean hasMadeChoice(String choicesMade, String choiceId) {
        if (choicesMade == null || choicesMade.isBlank()) return false;
        for (String s : choicesMade.split(",")) {
            if (s.trim().startsWith(choiceId + ":")) return true;
        }
        return false;
    }

    public static int countChoices(String choicesMade, String choiceId) {
        if (choicesMade == null || choicesMade.isBlank()) return 0;
        int count = 0;
        for (String s : choicesMade.split(",")) {
            if (s.trim().startsWith(choiceId + ":")) count++;
        }
        return count;
    }
}