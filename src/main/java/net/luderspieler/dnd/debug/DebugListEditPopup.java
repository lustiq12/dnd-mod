package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Popup for adding/removing entries of a comma-separated PlayerVariables field. */
public class DebugListEditPopup extends Screen {

    private static final int ROW_H = 16;
    private static final int VISIBLE_ROWS = 8;

    private final DebugMainScreen parent;
    private final String targetUuid;
    private final String fieldName;
    private final List<String> entries = new ArrayList<>();

    private EditBox addBox;
    private int scroll = 0;
    private int hoveredRemove = -1;

    private int bx, by, bw, bh;
    private int listTop, listBottom;

    public DebugListEditPopup(DebugMainScreen parent, String targetUuid, String fieldName, String rawValue) {
        super(Component.literal("Edit " + fieldName));
        this.parent = parent;
        this.targetUuid = targetUuid;
        this.fieldName = fieldName;
        if (rawValue != null && !rawValue.isBlank() && !rawValue.equals("\"\"")) {
            for (String s : rawValue.split(",")) {
                String t = s.trim();
                if (!t.isEmpty()) entries.add(t);
            }
        }
    }

    @Override
    protected void init() {
        bw = 300;
        bh = VISIBLE_ROWS * ROW_H + 90;
        bx = (this.width - bw) / 2;
        by = (this.height - bh) / 2;

        listTop = by + 24;
        listBottom = listTop + VISIBLE_ROWS * ROW_H;

        addBox = new EditBox(this.font, bx + 12, listBottom + 8, bw - 90, 18, Component.literal("New entry"));
        addBox.setMaxLength(256);
        addRenderableWidget(addBox);
        setInitialFocus(addBox);

        addRenderableWidget(Button.builder(Component.literal("Add"), b -> addEntry())
                .bounds(bx + bw - 70, listBottom + 8, 58, 18).build());

        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
                .bounds(bx + bw / 2 - 40, by + bh - 24, 80, 18).build());
    }

    private void addEntry() {
        String value = addBox.getValue().trim();
        if (value.isEmpty() || entries.contains(value)) return;
        DebugListModifyPacket.send(targetUuid, fieldName, value, true);
        entries.add(value);
        addBox.setValue("");
    }

    private void removeEntry(String value) {
        DebugListModifyPacket.send(targetUuid, fieldName, value, false);
        entries.remove(value);
        int maxScroll = Math.max(0, entries.size() - VISIBLE_ROWS);
        scroll = Math.min(scroll, maxScroll);
    }

    private void close() {
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        close();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        parent.render(g, -1, -1, partial);
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

        g.fill(bx, by, bx + bw, by + bh, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, bx, by, bw, bh);

        g.drawCenteredString(this.font, fieldName + "  (" + entries.size() + ")", bx + bw / 2, by + 6, generalConfigs.COLOR_ACCENT_GOLD);

        g.enableScissor(bx + 1, listTop, bx + bw - 1, listBottom);
        hoveredRemove = -1;
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = i + scroll;
            if (idx >= entries.size()) break;
            String entry = entries.get(idx);
            int ry = listTop + i * ROW_H;
            boolean hovered = mouseX >= bx + 4 && mouseX < bx + bw - 4 && mouseY >= ry && mouseY < ry + ROW_H;
            if (hovered) {
                hoveredRemove = idx;
                g.fill(bx + 4, ry, bx + bw - 4, ry + ROW_H, generalConfigs.COLOR_ROW_DANGER);
            }
            g.drawString(this.font, entry, bx + 8, ry + 4, generalConfigs.TEXT_WHITE, false);
            g.drawString(this.font, "✗", bx + bw - 16, ry + 4,
                    hovered ? generalConfigs.COLOR_STATUS_DANGER : generalConfigs.TEXT_DARK_GRAY, false);
        }
        g.disableScissor();

        if (entries.isEmpty()) {
            g.drawCenteredString(this.font, "(no entries)", bx + bw / 2, listTop + 8, generalConfigs.TEXT_GRAY);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredRemove >= 0 && hoveredRemove < entries.size()) {
            removeEntry(entries.get(hoveredRemove));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, entries.size() - VISIBLE_ROWS);
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) { addEntry(); return true; }
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}