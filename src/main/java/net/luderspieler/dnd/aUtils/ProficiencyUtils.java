package net.luderspieler.dnd.aUtils;

import java.util.LinkedHashSet;

public class ProficiencyUtils {

    /** Adds a proficiency key derived from a display name, deduplicated against existing entries. */
    public static void addProficiency(net.luderspieler.dnd.network.DndModVariables.PlayerVariables vars, String displayName) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (vars.Proficiencys != null && !vars.Proficiencys.isBlank() && !vars.Proficiencys.equals("\"\"")) {
            for (String p : vars.Proficiencys.split(",")) {
                String t = p.trim();
                if (!t.isEmpty()) set.add(t);
            }
        }
        set.add(toProficiencyKey(displayName));
        vars.Proficiencys = String.join(",", set);
    }

    public static String toProficiencyKey(String displayName) {
        return displayName.trim().toLowerCase().replace("'", "").replace(" ", "_");
    }

}
