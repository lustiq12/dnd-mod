package net.luderspieler.dnd.npc;

/**
 * One selectable answer inside a dialog node.
 * nextNodeId null/blank ends the conversation when opensTrade is false.
 * When opensTrade is true, nextNodeId is ignored and the trade menu opens instead.
 */
public record NpcDialogOption(String text, String nextNodeId, boolean opensTrade) {
}
