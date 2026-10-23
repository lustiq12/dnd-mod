package net.luderspieler.dnd.npc;

import net.luderspieler.dnd.init.DndModMenus;
import net.luderspieler.dnd.item.CoinBagHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NpcTradeMenu extends AbstractContainerMenu {

    public static final int CONFIRM_BUTTON_ID = 999;

    /** A coin price made of exactly one denomination, payable with loose coins. */
    private record CoinCost(CoinBagHelper.CoinType type, int count) {}

    private final int npcEntityId;
    private final Player player;
    private final List<NpcTradeEntry> trades;
    private final SimpleContainer tradeContainer = new SimpleContainer(1);
    private int selectedTradeIndex = -1;

    public NpcTradeMenu(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readVarInt());
    }

    public NpcTradeMenu(int windowId, Inventory playerInventory, int npcEntityId) {
        super(DndModMenus.NPC_TRADE_MENU.get(), windowId);
        this.npcEntityId = npcEntityId;
        this.player = playerInventory.player;
        this.trades = resolveTrades(this.player, npcEntityId);

        // Cost slot: holds the offered items for loose-coin and item-cost trades; stays empty when paying from the coin bag.
        this.addSlot(new Slot(tradeContainer, 0, 92, 34));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 20 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 20 + col * 18, 142));
        }
    }

    private static List<NpcTradeEntry> resolveTrades(Player player, int entityId) {
        Entity entity = player.level().getEntity(entityId);
        if (entity == null) return List.of();
        NpcConfiguration config = NpcJson.configFromJson(NpcDataAccess.getConfiguration(entity));
        return config == null ? List.of() : config.trades();
    }

    public NpcTradeEntry getSelectedTrade() {
        return (selectedTradeIndex >= 0 && selectedTradeIndex < trades.size()) ? trades.get(selectedTradeIndex) : null;
    }

    public List<NpcTradeEntry> getTrades() {
        return trades;
    }

    public int getSelectedTradeIndex() {
        return selectedTradeIndex;
    }

    /** For UI feedback: whether the selected trade could be completed right now. */
    public boolean canCompleteSelectedTrade() {
        NpcTradeEntry trade = getSelectedTrade();
        return trade != null && costMet(trade) && canReceiveResult(trade);
    }

    /** True when the selected trade is a coin trade that is paid from an equipped coin bag. */
    public boolean isPayingFromCoinBag() {
        NpcTradeEntry trade = getSelectedTrade();
        return trade != null && trade.isCoinCost() && CoinBagHelper.hasCoinBagEquipped(player);
    }

    /** True when the selected coin price mixes denominations and therefore needs a coin bag. */
    public boolean needsCoinBagForSelectedCost() {
        NpcTradeEntry trade = getSelectedTrade();
        return trade != null && trade.isCoinCost() && !CoinBagHelper.hasCoinBagEquipped(player) && singleCoinCost(trade) == null;
    }

    private static CoinCost singleCoinCost(NpcTradeEntry trade) {
        if (!trade.isCoinCost()) return null;
        CoinBagHelper.CoinType[] types = CoinBagHelper.CoinType.values();
        CoinCost found = null;
        for (int i = 0; i < trade.costCoins().length && i < types.length; i++) {
            if (trade.costCoins()[i] <= 0) continue;
            if (found != null) return null;
            found = new CoinCost(types[i], trade.costCoins()[i]);
        }
        return found;
    }

    private boolean slotHolds(Item item, int count) {
        ItemStack offered = tradeContainer.getItem(0);
        return item != null && !offered.isEmpty() && offered.is(item) && offered.getCount() >= count;
    }

    private boolean costMet(NpcTradeEntry trade) {
        if (trade.isCoinCost()) {
            // A coin bag pays any price with any coins; loose coins must match the exact denomination.
            if (CoinBagHelper.hasCoinBagEquipped(player)) {
                return CoinBagHelper.canAffordCopperValue(player, CoinBagHelper.coinsToCopperValue(trade.costCoins()));
            }
            CoinCost cost = singleCoinCost(trade);
            return cost != null && slotHolds(CoinBagHelper.getCoinItem(cost.type()), cost.count());
        }
        Item expected = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(trade.costItem()));
        return slotHolds(expected, trade.costItemCount());
    }

    private boolean canReceiveResult(NpcTradeEntry trade) {
        return !trade.isCoinResult() || CoinBagHelper.hasCoinBagEquipped(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // The screen calls this on the client only to track the selection. Moving items and paying
        // must run on the server alone, otherwise the client duplicates items until the next resync.
        if (id == CONFIRM_BUTTON_ID) {
            if (!player.level().isClientSide()) executeSelectedTrade(player);
            return true;
        }

        if (id < 0 || id >= trades.size()) return false;
        selectedTradeIndex = id;
        if (player.level().isClientSide()) return true;

        ItemStack currentlyOffered = tradeContainer.getItem(0);
        if (!currentlyOffered.isEmpty()) {
            player.getInventory().placeItemBackInInventory(currentlyOffered);
            tradeContainer.setItem(0, ItemStack.EMPTY);
        }

        NpcTradeEntry trade = getSelectedTrade();
        if (trade == null) return true;

        if (!trade.isCoinCost()) {
            fillCostSlot(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(trade.costItem())));
        } else if (!CoinBagHelper.hasCoinBagEquipped(player)) {
            CoinCost cost = singleCoinCost(trade);
            if (cost != null) fillCostSlot(CoinBagHelper.getCoinItem(cost.type()));
        }
        return true;
    }

    /** Moves as many matching items as fit into one stack (up to the max stack size) from the inventory into the cost slot. */
    private void fillCostSlot(Item item) {
        if (item == null) return;
        int cap = item.getDefaultInstance().getMaxStackSize();

        int gathered = 0;
        ItemStack filledStack = ItemStack.EMPTY;

        for (int i = 1; i < this.slots.size() && gathered < cap; i++) {
            Slot slot = this.slots.get(i);
            ItemStack stack = slot.getItem();
            if (stack.isEmpty() || !stack.is(item)) continue;

            int take = Math.min(cap - gathered, stack.getCount());
            ItemStack split = slot.remove(take);
            if (filledStack.isEmpty()) {
                filledStack = split;
            } else {
                filledStack.grow(take);
            }
            gathered += take;
        }

        if (!filledStack.isEmpty()) {
            tradeContainer.setItem(0, filledStack);
        }
    }

    private void executeSelectedTrade(Player executingPlayer) {
        NpcTradeEntry trade = getSelectedTrade();
        if (trade == null) return;
        if (!costMet(trade) || !canReceiveResult(trade)) return;

        if (trade.isCoinCost()) {
            if (CoinBagHelper.hasCoinBagEquipped(executingPlayer)) {
                CoinBagHelper.payCopperValue(executingPlayer, CoinBagHelper.coinsToCopperValue(trade.costCoins()));
            } else {
                tradeContainer.removeItem(0, singleCoinCost(trade).count());
            }
        } else {
            tradeContainer.removeItem(0, trade.costItemCount());
        }

        if (trade.isCoinResult()) {
            CoinBagHelper.depositCoins(executingPlayer, trade.resultCoins());
        } else {
            Item resultItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(trade.resultItem()));
            executingPlayer.getInventory().placeItemBackInInventory(new ItemStack(resultItem, trade.resultItemCount()));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.tradeContainer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    if (index >= 1 && index < 28) {
                        if (!this.moveItemStackTo(itemstack1, 28, 37, false)) return ItemStack.EMPTY;
                    } else if (index >= 28 && index < 37 && !this.moveItemStackTo(itemstack1, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        Entity entity = player.level().getEntity(npcEntityId);
        return entity != null && entity.isAlive() && player.distanceToSqr(entity) <= 64.0;
    }
}