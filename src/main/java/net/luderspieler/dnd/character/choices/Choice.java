package net.luderspieler.dnd.character.choices;

public enum Choice {
    SUBCLASS,
    ABILITY_SCORE_IMPROVEMENT,
    FEAT,
    FIGHTING_STYLE,
    ELDRITCH_INVOCATION,
    METAMAGIC,
    HOLY_ORDER,
    PRIMAL_ORDER,
    PRACTICED_SCHOLAR,
    TOOL_PROFICIENCY,
    DRACONIC_ANCESTRY;

    /**
     * Hilfsmethode, um ein Enum aus einem String (ID) zu finden,
     * nützlich für den ChoiceExecutor oder das Popup.
     */
    public static Choice fromId(String id) {
        try {
            return Choice.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}