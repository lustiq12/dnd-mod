package net.luderspieler.dnd.npc.screens;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.npc.NpcTradeEntry;
import net.luderspieler.dnd.npc.NpcTradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NpcTradeScreen extends AbstractContainerScreen<NpcTradeMenu> {

    private static final int OFFER_ROW_H = 18;
    private static final int OFFER_LIST_W = 100;

    public NpcTradeScreen(NpcTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 200;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.fill(x, y, x + this.imageWidth, y + this.imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, x, y, this.imageWidth, this.imageHeight);

        for (int i = 0; i < 3; i++) {
            var slot = this.menu.slots.get(i);
            g.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, generalConfigs.COLOR_HOVER_BG);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(this.font, this.title, 8, 6, generalConfigs.COLOR_ACCENT_GOLD, false);
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
            Item resultItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(trade.resultItem()));
            g.renderItem(new ItemStack(resultItem, trade.resultCount()), x + 2, rowY + 1);
            g.drawString(this.font, trade.resultCount() + "x", x + 22, rowY + 5, generalConfigs.TEXT_WHITE, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = offerListX();
        int y = offerListY();
        List<NpcTradeEntry> trades = this.menu.getTrades();

        for (int i = 0; i < trades.size(); i++) {
            int rowY = y + i * OFFER_ROW_H;
            if (mouseX >= x && mouseX < x + OFFER_LIST_W && mouseY >= rowY && mouseY < rowY + OFFER_ROW_H) {
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