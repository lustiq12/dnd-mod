package net.luderspieler.dnd.npc;

import java.util.List;
import java.util.Map;

/** The full setup of one NPC instance, written once on first interaction and reused after. */
public record NpcConfiguration(String displayName, String startNodeId, Map<String, NpcDialogNode> nodes,
                               List<NpcTradeEntry> trades, List<String> idleLines) {
}
