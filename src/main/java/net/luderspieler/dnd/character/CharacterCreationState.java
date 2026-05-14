package net.luderspieler.dnd.character;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the player's in-progress selections while navigating the creation screens.
 * Client-side only. Gets cleared when creation finishes or is cancelled.
 */
public class CharacterCreationState {
    public static String selectedRaceId    = "";
    public static String selectedSubraceId = "";
    public static String selectedClassId   = "";

    // Das hier muss in den State, damit buildCombinedAttrs im Screen nicht bei 0 anfängt
    public static Map<String, Integer> baseAttributes = new HashMap<>();

    static {
        resetAttributes();
    }

    public static void resetAttributes() {
        baseAttributes.put("Strength", 10);
        baseAttributes.put("Dexterity", 10);
        baseAttributes.put("Constitution", 10);
        baseAttributes.put("Intelligence", 10);
        baseAttributes.put("Wisdom", 10);
        baseAttributes.put("Charisma", 10);
    }

    public static void reset() {
        selectedRaceId = "";
        selectedSubraceId = "";
        selectedClassId = "";
        resetAttributes();
    }
}