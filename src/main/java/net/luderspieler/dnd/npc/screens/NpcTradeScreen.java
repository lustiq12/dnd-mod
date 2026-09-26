package net.luderspieler.dnd.npc.screens;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.item.CoinBagHelper;
import net.luderspieler.dnd.npc.NpcTradeEntry;
import net.luderspieler.dnd.npc.NpcTradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NpcTradeScreen extends AbstractContainerScreen<NpcTradeMenu> {

    private static final int OFFER_ROW_H = 18;
    private static final int OFFER_LIST_W = 160;
    private static final CoinBagHelper.CoinType[] COIN_TYPES = CoinBagHelper.CoinType.values();
    private static final String[] COIN_SUFFIX = {"cp", "sp", "ep", "gp", "pp"};

    private Button confirmButton;

    public NpcTradeScreen(NpcTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 200;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        confirmButton = this.addRenderableWidget(Button.builder(Component.literal("Trade"), b -> confirmTrade())
                .bounds(x + this.imageWidth / 2 - 30, y + 54, 60, 18).build());
    }

    private void confirmTrade() {
        this.menu.clickMenuButton(this.minecraft.player, NpcTradeMenu.CONFIRM_BUTTON_ID);
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, NpcTradeMenu.CONFIRM_BUTTON_ID);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.fill(x, y, x + this.imageWidth, y + this.imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, x, y, this.imageWidth, this.imageHeight);

        for (Slot slot : this.menu.slots) {
            g.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, generalConfigs.COLOR_HOVER_BG);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(this.font, this.title, 8, 6, generalConfigs.COLOR_ACCENT_GOLD, false);

        NpcTradeEntry selected = this.menu.getSelectedTrade();
        confirmButton.visible = selected != null;
        if (selected == null) return;

        confirmButton.active = this.menu.canCompleteSelectedTrade();
        if (!confirmButton.active && selected.isCoinResult() && !CoinBagHelper.hasCoinBagEquipped(this.minecraft.player)) {
            g.drawString(this.font, "Needs a coin bag on your belt", 8, 20, generalConfigs.COLOR_STATUS_DANGER, false);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        super.render(g, mouseX, mouseY, partial);
        renderOfferList(g, mouseX, mouseY);
        this.renderTooltip(g, mouseX, mouseY);
    }

    private void renderOfferList(GuiGraphics g, int mouseX, int mouseY) {
        int x = offerListX();
        int y = offerListY();
        List<NpcTradeEntry> trades = this.menu.getTrades();

        for (int i = 0; i < trades.size(); i++) {
            int rowY = y + i * OFFER_ROW_H;
            boolean selected = i == this.menu.getSelectedTradeIndex();
            boolean hovered = mouseX >= x && mouseX < x + OFFER_LIST_W && mouseY >= rowY && mouseY < rowY + OFFER_ROW_H;

            if (selected) g.fill(x, rowY, x + OFFER_LIST_W, rowY + OFFER_ROW_H, 0x5500BB44);
            else if (hovered) g.fill(x, rowY, x + OFFER_LIST_W, rowY + OFFER_ROW_H, generalConfigs.COLOR_HOVER_BG);

            NpcTradeEntry trade = trades.get(i);
            int renderX = x + 2;
            renderX = renderSide(g, trade.isCoinCost(), trade.costCoins(), trade.costItem(), trade.costItemCount(), renderX, rowY);

            g.drawString(this.font, "->", renderX, rowY + 5, generalConfigs.COLOR_ACCENT_GOLD, false);
            renderX += this.font.width("->") + 4;

            renderSide(g, trade.isCoinResult(), trade.resultCoins(), trade.resultItem(), trade.resultItemCount(), renderX, rowY);
        }
    }

    /** Draws either the coin breakdown or a single item icon+count for one side of a trade; returns the x position after it. */
    private int renderSide(GuiGraphics g, boolean isCoins, int[] coins, String itemId, int itemCount, int renderX, int rowY) {
        if (isCoins) {
            boolean any = false;
            for (int i = 0; i < coins.length && i < COIN_TYPES.length; i++) {
                if (coins[i] <= 0) continue;
                if (any) {
                    g.drawString(this.font, "+", renderX, rowY + 5, generalConfigs.TEXT_GRAY, false);
                    renderX += this.font.width("+") + 2;
                }
                Item coinItem = CoinBagHelper.getCoinItem(COIN_TYPES[i]);
                if (coinItem != null) {
                    g.renderItem(new ItemStack(coinItem, Math.min(64, coins[i])), renderX, rowY + 1);
                    renderX += 18;
                }
                String label = coins[i] + COIN_SUFFIX[i];
                g.drawString(this.font, label, renderX, rowY + 5, generalConfigs.TEXT_WHITE, false);
                renderX += this.font.width(label) + 4;
                any = true;
            }
            return renderX;
        }

        Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
        g.renderItem(new ItemStack(item, itemCount), renderX, rowY + 1);
        renderX += 18;
        String countStr = itemCount + "x";
        g.drawString(this.font, countStr, renderX, rowY + 5, generalConfigs.TEXT_WHITE, false);
        return renderX + this.font.width(countStr) + 4;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = offerListX();
        int y = offerListY();
        List<NpcTradeEntry> trades = this.menu.getTrades();

        for (int i = 0; i < trades.size(); i++) {
            int rowY = y + i * OFFER_ROW_H;
            if (mouseX >= x && mouseX < x + OFFER_LIST_W && mouseY >= rowY && mouseY < rowY + OFFER_ROW_H) {
                this.menu.clickMenuButton(this.minecraft.player, i);
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int offerListX() {
        return (this.width - this.imageWidth) / 2 + this.imageWidth + 4;
    }

    private int offerListY() {
        return (this.height - this.imageHeight) / 2;
    }
}