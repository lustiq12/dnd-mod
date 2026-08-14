package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Root screen of the DnD debug GUI: target selector, tab bar and the active tab's content area. */
public class DebugMainScreen extends Screen {

    private static final int TOP_BAR_H = 22;
    private static final int TAB_BAR_H = 22;
    private static final int MARGIN = 8;

    private final List<DebugTab> tabs = new ArrayList<>();
    private int activeTabIndex = 0;

    private EditBox targetInput;
    private String statusMessage = "";

    private DebugClientState.Snapshot lastSeenSnapshot;

    private int contentX, contentY, contentW, contentH;

    public DebugMainScreen() {
        super(Component.literal("DnD Debug"));
    }

    @Override
    protected void init() {
        super.init();

        contentX = MARGIN;
        contentY = TOP_BAR_H + TAB_BAR_H + MARGIN;
        contentW = this.width - MARGIN * 2;
        contentH = this.height - contentY - MARGIN;

        if (tabs.isEmpty()) {
            tabs.add(new DebugVariablesTab(this));
            tabs.add(new DebugAbilitiesTab(this));
            tabs.add(new DebugStatsTab(this));
            tabs.add(new DebugSpellsTab(this));
            tabs.add(new DebugChoicesTab(this));
            tabs.add(new DebugProficienciesTab(this));
            tabs.add(new DebugFeatsTab(this));
            tabs.add(new DebugResourcesTab(this));
            tabs.add(new DebugDataTab(this));
        }

        rebuildWidgets();
        requestFreshData();
    }

    /** Always re-requests the current target's data on open, since the client-side caches are static and outlive screen closes. */
    private void requestFreshData() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        DebugClientState.Snapshot snap = DebugClientState.get();
        String uuid = snap != null ? snap.uuid().toString() : this.minecraft.player.getStringUUID();
        DebugSnapshotRequestPacket.send(uuid);
        DebugAttributesRequestPacket.send(uuid);
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();

        String prevInput = targetInput != null ? targetInput.getValue() : defaultTargetName();
        targetInput = new EditBox(this.font, MARGIN, 2, 220, 18, Component.literal("Player name or UUID"));
        targetInput.setValue(prevInput);
        this.addRenderableWidget(targetInput);

        this.addRenderableWidget(Button.builder(Component.literal("Load"), b -> loadTarget())
                .bounds(MARGIN + 224, 2, 50, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Self"), b -> loadSelf())
                .bounds(MARGIN + 278, 2, 44, 18).build());

        int tx = MARGIN;
        int ty = TOP_BAR_H;
        for (int i = 0; i < tabs.size(); i++) {
            final int idx = i;
            DebugTab tab = tabs.get(i);
            boolean active = idx == activeTabIndex;
            int w = this.font.width(tab.getTitle()) + 16;
            Button btn = Button.builder(Component.literal((active ? "» " : "") + tab.getTitle()), b -> switchTab(idx))
                    .bounds(tx, ty, w, 18).build();
            btn.active = !active;
            this.addRenderableWidget(btn);
            tx += w + 4;
        }

        DebugClientState.Snapshot snap = DebugClientState.get();
        lastSeenSnapshot = snap;
        tabs.get(activeTabIndex).rebuild(snap, contentX, contentY, contentW, contentH);
    }

    private String defaultTargetName() {
        return this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.getGameProfile().getName() : "";
    }

    private void loadSelf() {
        targetInput.setValue(defaultTargetName());
        loadTarget();
    }

    private void loadTarget() {
        String value = targetInput.getValue().trim();
        if (value.isEmpty()) return;
        String uuid = resolveUuid(value);
        if (uuid == null) {
            statusMessage = "§cCould not resolve player: " + value;
            return;
        }
        statusMessage = "§7Requesting snapshot...";
        DebugSnapshotRequestPacket.send(uuid);
        DebugAttributesRequestPacket.send(uuid);
    }

    private String resolveUuid(String input) {
        try {
            UUID.fromString(input);
            return input;
        } catch (IllegalArgumentException ignored) {}
        if (this.minecraft != null && this.minecraft.getConnection() != null) {
            var info = this.minecraft.getConnection().getPlayerInfo(input);
            if (info != null) return info.getProfile().getId().toString();
        }
        return null;
    }

    private void switchTab(int index) {
        if (index == activeTabIndex) return;
        activeTabIndex = index;
        rebuildWidgets();
    }

    public void addTabWidget(AbstractWidget widget) {
        this.addRenderableWidget(widget);
    }

    public void openPopup(Screen popup) {
        if (this.minecraft != null) this.minecraft.setScreen(popup);
    }

    public Font getFontInstance() {
        return this.font;
    }

    public String currentTargetUuid() {
        DebugClientState.Snapshot snap = DebugClientState.get();
        return snap != null ? snap.uuid().toString() : null;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        DebugClientState.Snapshot snap = DebugClientState.get();
        if (snap != lastSeenSnapshot) {
            statusMessage = "";
            rebuildWidgets();
        }

        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        g.fill(MARGIN, TOP_BAR_H, this.width - MARGIN, TOP_BAR_H + TAB_BAR_H, 0x33000000);
        g.fill(contentX, contentY, contentX + contentW, contentY + contentH, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, contentX, contentY, contentW, contentH);

        DebugTab active = tabs.get(activeTabIndex);
        if (snap == null) {
            g.drawCenteredString(this.font, "No target loaded — enter a player and press Load, or press Self.",
                    contentX + contentW / 2, contentY + contentH / 2, generalConfigs.TEXT_GRAY);
        } else {
            g.drawString(this.font, "Editing: " + snap.name(), contentX + 4, contentY - 10, generalConfigs.COLOR_ACCENT_GOLD, false);
            active.render(g, mouseX, mouseY, partial);
        }

        super.render(g, mouseX, mouseY, partial);

        if (!statusMessage.isEmpty()) {
            g.drawString(this.font, statusMessage, MARGIN, TOP_BAR_H - 10, generalConfigs.TEXT_GRAY, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DebugClientState.Snapshot snap = DebugClientState.get();
        if (snap != null && mouseX >= contentX && mouseX <= contentX + contentW
                && mouseY >= contentY && mouseY <= contentY + contentH) {
            if (tabs.get(activeTabIndex).mouseClicked(mouseX, mouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        DebugClientState.Snapshot snap = DebugClientState.get();
        if (snap != null && tabs.get(activeTabIndex).mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}