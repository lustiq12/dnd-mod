package net.luderspieler.dnd.npc;

import java.util.List;
import java.util.Map;

/**
 * Static, hand-written definition for one NPC entity type. Never modified at runtime.
 * minTrades/maxTrades control how many entries get picked from tradePool on first interaction.
 */
public record NpcPreset(List<String> namePool, String startNodeId, Map<String, NpcDialogNode> dialogNodes,
                        List<NpcTradeEntry> tradePool, int minTrades, int maxTrades, List<String> idleLines) {
}
