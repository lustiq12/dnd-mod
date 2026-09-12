package net.luderspieler.dnd.npc.screens;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.npc.NpcConfiguration;
import net.luderspieler.dnd.npc.NpcDataAccess;
import net.luderspieler.dnd.npc.NpcDialogNode;
import net.luderspieler.dnd.npc.NpcDialogOption;
import net.luderspieler.dnd.npc.NpcJson;
import net.luderspieler.dnd.npc.network.ChooseNpcDialogOptionPacket;
import net.luderspieler.dnd.npc.network.CloseNpcDialogPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * The NPC's whole dialog tree is read straight off the entity via NpcDataAccess (it is already
 * synced like any other MCreator entity data field) - the server only needs to say which node
 * id is currently active.
 */
public class NpcDialogScreen extends Screen {

    private static final int BUBBLE_W = 220;
    private static final int BUBBLE_H = 200;
    private static final int BUST_SIZE = 100;
    private static final int BUST_SCALE = 30;

    private final int entityId;
    private String currentNodeId;
    private NpcConfiguration config;

    public NpcDialogScreen(int entityId, String nodeId) {
        super(Component.literal("Dialog"));
        this.entityId = entityId;
        this.currentNodeId = nodeId;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    protected void init() {
        super.init();
        loadConfig();
        rebuildOptionButtons();
    }

    public void updateNode(String nodeId) {
        this.currentNodeId = nodeId;
        loadConfig();
        this.clearWidgets();
        rebuildOptionButtons();
    }

    private void loadConfig() {
        Entity entity = this.minecraft.level == null ? null : this.minecraft.level.getEntity(entityId);
        this.config = entity == null ? null : NpcJson.configFromJson(NpcDataAccess.getConfiguration(entity));
    }

    private NpcDialogNode currentNode() {
        return config == null ? null : config.nodes().get(currentNodeId);
    }

    private void rebuildOptionButtons() {
        NpcDialogNode node = currentNode();
        if (node == null) return;

        // Dynamische Positionierung analog zu renderPlayerBubble
        int busX = this.width - BUST_SIZE - 20;
        int bubbleX = busX - BUBBLE_W - 10;
        int bubbleY = this.height - BUBBLE_H - 40;

        List<NpcDialogOption> options = node.options();

        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            int rowY = bubbleY + 10 + i * 14;

            // Button sitzt bei bubbleX + 6 (Breite 12), der Text startet genau dahinter bei bubbleX + 22
            this.addRenderableWidget(Button.builder(Component.literal(">"), b -> chooseOption(optionIndex))
                    .bounds(bubbleX + 6, rowY, 12, 12).build());
        }
    }

    private void chooseOption(int optionIndex) {
        ClientPacketDistributor.sendToServer(new ChooseNpcDialogOptionPacket(entityId, optionIndex));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

        NpcDialogNode node = currentNode();
        Entity npcEntity = this.minecraft.level == null ? null : this.minecraft.level.getEntity(entityId);

        renderNpcBubble(g, npcEntity, node);
        renderPlayerBubble(g, node);

        super.render(g, mouseX, mouseY, partial);
    }

    private void renderNpcBubble(GuiGraphics g, Entity npcEntity, NpcDialogNode node) {
        int busX = 20;
        int busY = 20;

        g.fill(busX, busY, busX + BUST_SIZE, busY + BUST_SIZE, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, busX, busY, BUST_SIZE, BUST_SIZE);
        if (npcEntity instanceof LivingEntity living) {
            renderBust(g, busX, busY, living);
        }

        int bubbleX = busX + BUST_SIZE + 10;
        int bubbleY = busY;
        g.fill(bubbleX, bubbleY, bubbleX + BUBBLE_W, bubbleY + BUBBLE_H, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, bubbleX, bubbleY, BUBBLE_W, BUBBLE_H);

        String name = config != null ? config.displayName() : "";
        g.drawString(this.font, name, bubbleX + 6, bubbleY + 4, generalConfigs.COLOR_ACCENT_GOLD, false);
        if (node != null) {
            g.drawWordWrap(this.font, Component.literal(node.text()), bubbleX + 6, bubbleY + 16, BUBBLE_W - 12, generalConfigs.TEXT_WHITE);
        }
    }

    private void renderPlayerBubble(GuiGraphics g, NpcDialogNode node) {
        int busX = this.width - BUST_SIZE - 20;
        int busY = this.height - BUST_SIZE - 40; // Anpassen, falls nötig

        // Setzt die Bubble genau 10px links neben die Büste (analog zu NPC)
        int bubbleX = busX - BUBBLE_W - 10;
        int bubbleY = this.height - BUBBLE_H - 40;

        g.fill(bubbleX, bubbleY, bubbleX + BUBBLE_W, bubbleY + BUBBLE_H, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, bubbleX, bubbleY, BUBBLE_W, BUBBLE_H);

        if (node != null) {
            List<NpcDialogOption> options = node.options();
            for (int i = 0; i < options.size(); i++) {
                int rowY = bubbleY + 10 + i * 14;
                g.drawString(this.font, options.get(i).text(), bubbleX + 22, rowY, generalConfigs.TEXT_WHITE, false);
            }
        }

        if (this.minecraft.player != null) {
            g.fill(busX, busY, busX + BUST_SIZE, busY + BUST_SIZE, generalConfigs.COLOR_PANEL_BG);
            generalConfigs.renderGreenEdge(g, busX, busY, BUST_SIZE, BUST_SIZE);
            renderBust(g, busX, busY, this.minecraft.player);
        }
    }

    private static void renderBust(GuiGraphics g, int busX, int busY, LivingEntity living) {
        InventoryScreen.renderEntityInInventoryFollowsAngle(
                g,
                busX, busY, busX + BUST_SIZE, busY + (int)(BUST_SIZE*1.5),
                BUST_SCALE,
                -living.getBbHeight() / (2.0f * living.getScale()),
                -1f, -1f,
                living
        );
    }

    @Override
    public void onClose() {
        ClientPacketDistributor.sendToServer(new CloseNpcDialogPacket());
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}