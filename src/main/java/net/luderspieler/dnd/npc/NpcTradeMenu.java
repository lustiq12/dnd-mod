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

public class NpcTradeMenu extends AbstractContainerMenu {

    private final int npcEntityId;
    private final List<NpcTradeEntry> trades;
    private final SimpleContainer tradeContainer = new SimpleContainer(3);
    private int selectedTradeIndex = 0;
    private boolean isUpdatingResult = false;

    public NpcTradeMenu(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readVarInt());
    }

    public NpcTradeMenu(int windowId, Inventory playerInventory, int npcEntityId) {
        super(DndModMenus.NPC_TRADE_MENU.get(), windowId);
        this.npcEntityId = npcEntityId;
        this.trades = resolveTrades(playerInventory.player, npcEntityId);

        this.tradeContainer.addListener(this::slotsChanged);

        // Handels-Slots (Zentriert oben)
        this.addSlot(new Slot(tradeContainer, 0, 46, 34));
        this.addSlot(new Slot(tradeContainer, 1, 72, 34));
        this.addSlot(new TradeResultSlot(tradeContainer, 2, 138, 34));

        // Spieler-Inventar (9 Spalten à 18px = 160px Breite, exakt zentriert bei 200px Panelbreite)
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= trades.size()) return false;
        selectedTradeIndex = id;

        // Vorherige Items aus den Handels-Slots zurück ins Spieler-Inventar legen
        for (int i = 0; i < 2; i++) {
            ItemStack current = tradeContainer.getItem(i);
            if (!current.isEmpty()) {
                player.getInventory().placeItemBackInInventory(current);
                tradeContainer.setItem(i, ItemStack.EMPTY);
            }
        }

        // Auto-Fill aus dem Inventar
        NpcTradeEntry trade = getSelectedTrade();
        if (trade != null) {
            fillInputSlot(player, 0, trade.inputItem(), trade.inputCount());
            if (trade.hasSecondInput()) {
                fillInputSlot(player, 1, trade.secondInputItem(), trade.secondInputCount());
            }
        }

        updateResultSlot();
        return true;
    }

    private void fillInputSlot(Player player, int targetSlot, String itemId, int requiredCount) {
        if (itemId == null || itemId.isBlank() || requiredCount <= 0) return;
        Item expectedItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
        if (expectedItem == null) return;

        int gathered = 0;
        ItemStack filledStack = ItemStack.EMPTY;

        for (int i = 3; i < this.slots.size(); i++) {
            Slot slot = this.slots.get(i);
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && stack.is(expectedItem)) {
                int needed = requiredCount - gathered;
                int take = Math.min(needed, stack.getCount());

                ItemStack split = slot.remove(take);
                if (filledStack.isEmpty()) {
                    filledStack = split;
                } else {
                    filledStack.grow(take);
                }
                gathered += take;

                if (gathered >= requiredCount) break;
            }
        }

        if (!filledStack.isEmpty()) {
            tradeContainer.setItem(targetSlot, filledStack);
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResultSlot();
    }

    private void updateResultSlot() {
        if (isUpdatingResult) return;
        isUpdatingResult = true;
        try {
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
        } finally {
            isUpdatingResult = false;
        }
    }

    private static boolean itemMatches(ItemStack stack, String itemId, int count) {
        if (itemId == null || itemId.isBlank()) return true;
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
    public void removed(Player player) {
        super.removed(player);
        // Schutz vor Duping/Item-Verlust beim Schließen des Fensters
        this.tradeContainer.setItem(2, ItemStack.EMPTY);
        this.clearContainer(player, this.tradeContainer);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    if (index >= 3 && index < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) return ItemStack.EMPTY;
                    } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
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