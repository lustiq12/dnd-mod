package net.luderspieler.dnd.npc;

/** A single trade offer. secondInputItem may be blank if the trade only needs one input item. */
public record NpcTradeEntry(String inputItem, int inputCount, String secondInputItem, int secondInputCount,
                            String resultItem, int resultCount) {

    public boolean hasSecondInput() {
        return secondInputItem != null && !secondInputItem.isBlank();
    }
}
