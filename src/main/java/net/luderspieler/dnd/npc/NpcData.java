package net.luderspieler.dnd.npc;

import java.util.HashMap;
import java.util.Map;

/** Runtime state of an NPC, keyed by player UUID string. */
public record NpcData(Map<String, NpcPlayerState> playerStates) {

    public static NpcData empty() {
        return new NpcData(new HashMap<>());
    }
}
