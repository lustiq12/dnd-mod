package net.luderspieler.dnd.npc;

/**
 * A single trade offer. Exactly one of (costCoins, costItem) defines what the player pays,
 * and exactly one of (resultCoins, resultItem) defines what the player receives.
 * costCoins/resultCoins follow CoinBagHelper.CoinType's declaration order:
 * [copper, silver, electrum, gold, platinum].
 */
public record NpcTradeEntry(int[] costCoins, String costItem, int costItemCount,
                            int[] resultCoins, String resultItem, int resultItemCount) {

    public boolean isCoinCost() {
        return costCoins != null && costCoins.length > 0;
    }

    public boolean isCoinResult() {
        return resultCoins != null && resultCoins.length > 0;
    }
}