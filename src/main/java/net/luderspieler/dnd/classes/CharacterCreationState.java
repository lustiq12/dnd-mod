package net.luderspieler.dnd.classes;

/**
 * Holds the player's in-progress selections while navigating the creation screens.
 * Client-side only. Gets cleared when creation finishes or is cancelled.
 */
public class CharacterCreationState {

    public static String selectedRaceId    = "";
    public static String selectedSubraceId = "";
    public static String selectedClassId   = "";

    public static void reset() {
        selectedRaceId    = "";
        selectedSubraceId = "";
        selectedClassId   = "";
    }
}