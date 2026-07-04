package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

public class ChoiceUpdateSystem {

    public static void updateChoices(Player player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int    level = (int) vars.PlayerLevel;
        String clazz = vars.PlayerClass; // immer lowercase

        List<String> needed = new ArrayList<>();

        // ── 1. SUBCLASS ───────────────────────────────────────────────────────
        // 2024 PHB: alle Klassen wählen ihre Subclass auf Level 3.
        if (level >= 3 && !hasMadeChoice(vars.ChoicesMade, "SUBCLASS")) {
            needed.add("SUBCLASS");
        }

        // ── 2. ABILITY SCORE IMPROVEMENT OR FEAT ─────────────────────────────
        // Standard: Level 4 / 8 / 12 / 16 / 19.
        // Fighter extra: 6 + 14. Rogue extra: 10.
        int maxAsi = calculateMaxASIs(level, clazz);
        int curAsi = countChoices(vars.ChoicesMade, "ABILITY_SCORE_IMPROVEMENT_OR_FEAT");
        for (int i = 0; i < (maxAsi - curAsi); i++) {
            needed.add("ABILITY_SCORE_IMPROVEMENT_OR_FEAT");
        }

        // ── 3. KLASSEN-SPEZIFISCHE CHOICES ────────────────────────────────────

        // FIGHTER: Fighting Style auf Level 1
        if ("fighter".equals(clazz) && level >= 1
                && !hasMadeChoice(vars.ChoicesMade, "FIGHTING_STYLE")) {
            needed.add("FIGHTING_STYLE");
        }
        // RANGER: Fighting Style auf Level 2
        if ("ranger".equals(clazz) && level >= 2
                && !hasMadeChoice(vars.ChoicesMade, "FIGHTING_STYLE")) {
            needed.add("FIGHTING_STYLE");
        }
        // PALADIN: Fighting Style auf Level 2
        if ("paladin".equals(clazz) && level >= 2
                && !hasMadeChoice(vars.ChoicesMade, "FIGHTING_STYLE")) {
            needed.add("FIGHTING_STYLE");
        }

        // WARLOCK: Eldritch Invocations ab Level 2 (2 Stück; wachsen mit Level)
        if ("warlock".equals(clazz)) {
            int maxInv = level >= 9 ? 7 : level >= 7 ? 6 : level >= 5 ? 5
                                                           : level >= 3 ? 4 : level >= 2 ? 2 : 0;
            int curInv = countChoices(vars.ChoicesMade, "ELDRITCH_INVOCATION");
            for (int i = 0; i < (maxInv - curInv); i++) needed.add("ELDRITCH_INVOCATION");
        }

        // CLERIC: Holy Order auf Level 2
        if ("cleric".equals(clazz) && level >= 2
                && !hasMadeChoice(vars.ChoicesMade, "HOLY_ORDER")) {
            needed.add("HOLY_ORDER");
        }

        // DRUID: Primal Order auf Level 2
        if ("druid".equals(clazz) && level >= 2
                && !hasMadeChoice(vars.ChoicesMade, "PRIMAL_ORDER")) {
            needed.add("PRIMAL_ORDER");
        }

        // BARD: Expertise-Skills auf Level 2 (2 Skills)
        if ("bard".equals(clazz) && level >= 2) {
            int maxEx = level >= 10 ? 4 : 2;
            int curEx = countChoices(vars.ChoicesMade, "BARDIC_COLLEGE_SKILL");
            for (int i = 0; i < (maxEx - curEx); i++) needed.add("BARDIC_COLLEGE_SKILL");
        }

        // SORCERER: Metamagic — 2 ab Level 2, 3 ab Level 10, 4 ab Level 17
        if ("sorcerer".equals(clazz)) {
            int maxMeta = level >= 17 ? 4 : level >= 10 ? 3 : level >= 2 ? 2 : 0;
            int curMeta = countChoices(vars.ChoicesMade, "METAMAGIC");
            for (int i = 0; i < (maxMeta - curMeta); i++) needed.add("METAMAGIC");
        }

        // ROGUE: Tool Proficiency auf Level 1
        if ("rogue".equals(clazz) && level >= 1
                && !hasMadeChoice(vars.ChoicesMade, "TOOL_PROFICIENCY")) {
            needed.add("TOOL_PROFICIENCY");
        }

        vars.ChoicesNeeded = String.join(",", needed);
        vars.markSyncDirty();
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static int calculateMaxASIs(int level, String clazz) {
        // Standard-ASIs (alle Klassen): 4 / 8 / 12 / 16 / 19
        int count = 0;
        if (level >= 4)  count++;
        if (level >= 8)  count++;
        if (level >= 12) count++;
        if (level >= 16) count++;
        if (level >= 19) count++;

        // Fighter: extra auf Level 6 und 14
        if ("fighter".equals(clazz)) {
            if (level >= 6)  count++;
            if (level >= 14) count++;
        }
        // Rogue: extra auf Level 10
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