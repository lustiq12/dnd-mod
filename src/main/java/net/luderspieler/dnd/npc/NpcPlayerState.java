package net.luderspieler.dnd.npc;

/** Per-player memory of an NPC. Extend this once conversation history or quests are needed. */
public record NpcPlayerState(boolean hasMet) {
}
