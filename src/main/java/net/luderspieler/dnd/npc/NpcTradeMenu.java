package net.luderspieler.dnd.npc;

import net.luderspieler.dnd.init.DndModMenus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Two input slots and one result slot serve the currently selected offer out of npc.trades();
 * clickMenuButton() switches which offer is active, the same vanilla mechanism used by
 * villager/loom/stonecutter selection lists (see NpcTradeScreen for the offer-list clicks).
 */
public class NpcTradeMenu extends AbstractContainerMenu {

    private final int npcEntityId;
    private final List<NpcTradeEntry> trades;
    private final SimpleContainer tradeContainer = new SimpleContainer(3);
    private int selectedTradeIndex = 0;

    public NpcTradeMenu(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readVarInt());
    }

    public NpcTradeMenu(int windowId, Inventory playerInventory, int npcEntityId) {
        super(DndModMenus.NPC_TRADE_MENU.get(), windowId);
        this.npcEntityId = npcEntityId;
        this.trades = resolveTrades(playerInventory.player, npcEntityId);

        this.addSlot(new Slot(tradeContainer, 0, 36, 34));
        this.addSlot(new Slot(tradeContainer, 1, 62, 34));
        this.addSlot(new TradeResultSlot(tradeContainer, 2, 122, 34));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= trades.size()) return false;
        selectedTradeIndex = id;
        tradeContainer.setItem(0, ItemStack.EMPTY);
        tradeContainer.setItem(1, ItemStack.EMPTY);
        tradeContainer.setItem(2, ItemStack.EMPTY);
        return true;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResultSlot();
    }

    private void updateResultSlot() {
        NpcTradeEntry trade = getSelectedTrade();
        if (trade == null) {
            tradeContainer.setItem(2, ItemStack.EMPTY);
            return;
        }
        ItemStack inputA = tradeContainer.getItem(0);
        ItemStack inputB = tradeContainer.getItem(1);
        boolean matches = itemMatches(inputA, trade.inputItem(), trade.inputCount())
                && (!trade.hasSecondInput() || itemMatches(inputB, trade.secondInputItem(), trade.secondInputCount()));
        tradeContainer.setItem(2, matches ? buildResultStack(trade) : ItemStack.EMPTY);
    }

    private static boolean itemMatches(ItemStack stack, String itemId, int count) {
        Item expected = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
        return !stack.isEmpty() && stack.is(expected) && stack.getCount() >= count;
    }

    private static ItemStack buildResultStack(NpcTradeEntry trade) {
        Item resultItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(trade.resultItem()));
        return new ItemStack(resultItem, trade.resultCount());
    }

    private void consumeInputs() {
        NpcTradeEntry trade = getSelectedTrade();
        if (trade == null) return;
        tradeContainer.getItem(0).shrink(trade.inputCount());
        if (trade.hasSecondInput()) tradeContainer.getItem(1).shrink(trade.secondInputCount());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index < 3) {
            if (index == 2) return ItemStack.EMPTY;
            if (!this.moveItemStackTo(original, 3, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(original, 0, 2, false)) return ItemStack.EMPTY;
        }

        if (original.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        Entity entity = player.level().getEntity(npcEntityId);
        return entity != null && entity.isAlive() && player.distanceToSqr(entity) <= 64.0;
    }

    private class TradeResultSlot extends Slot {
        TradeResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            consumeInputs();
            updateResultSlot();
            super.onTake(player, stack);
        }
    }
}