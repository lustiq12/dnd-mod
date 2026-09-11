package net.luderspieler.dnd.npc;

import java.util.List;

public record NpcDialogNode(String id, String text, List<NpcDialogOption> options) {
}
