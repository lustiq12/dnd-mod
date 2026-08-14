package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Popup for adding a new key-value entry or editing/removing an existing one in AbilityData/GeneralData. */
public class DebugDataMapEditPopup extends Screen {

    private final DebugMainScreen parent;
    private final String targetUuid;
    private final String mapField;
    private final boolean isNew;
    private final String initialKey;

    private EditBox keyBox;
    private EditBox valueBox;
    private String errorMessage = "";

    private int bx, by, bw, bh;

    public DebugDataMapEditPopup(DebugMainScreen parent, String targetUuid, String mapField,
                                 String key, String value, boolean isNew) {
        super(Component.literal((isNew ? "New entry in " : "Edit ") + mapField));
        this.parent = parent;
        this.targetUuid = targetUuid;
        this.mapField = mapField;
        this.initialKey = key == null ? "" : key;
        this.isNew = isNew;
        this.initialValue = value == null ? "" : value;
    }

    private final String initialValue;

    @Override
    protected void init() {
        bw = 280;
        bh = 110;
        bx = (this.width - bw) / 2;
        by = (this.height - bh) / 2;

        keyBox = new EditBox(this.font, bx + 12, by + 26, bw - 24, 18, Component.literal("Key"));
        keyBox.setMaxLength(128);
        keyBox.setValue(initialKey);
        keyBox.setEditable(isNew);
        addRenderableWidget(keyBox);

        valueBox = new EditBox(this.font, bx + 12, by + 50, bw - 24, 18, Component.literal("Value"));
        valueBox.setMaxLength(1024);
        valueBox.setValue(initialValue);
        addRenderableWidget(valueBox);
        setInitialFocus(isNew ? keyBox : valueBox);

        if (isNew) {
            addRenderableWidget(Button.builder(Component.literal("Save"), b -> save())
                    .bounds(bx + 12, by + bh - 26, (bw - 24) / 2 - 2, 18).build());
            addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> close())
                    .bounds(bx + 12 + (bw - 24) / 2 + 2, by + bh - 26, (bw - 24) / 2 - 2, 18).build());
        } else {
            int third = (bw - 24) / 3;
            addRenderableWidget(Button.builder(Component.literal("Save"), b -> save())
                    .bounds(bx + 12, by + bh - 26, third, 18).build());
            addRenderableWidget(Button.builder(Component.literal("Remove"), b -> remove())
                    .bounds(bx + 12 + third + 4, by + bh - 26, third, 18).build());
            addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> close())
                    .bounds(bx + 12 + (third + 4) * 2, by + bh - 26, third, 18).build());
        }
    }

    private void save() {
        String key = keyBox.getValue().trim();
        if (key.isEmpty()) {
            errorMessage = "§cKey cannot be empty.";
            return;
        }
        DebugDataMapEditPacket.send(targetUuid, mapField, key, valueBox.getValue(), false);
        close();
    }

    private void remove() {
        DebugDataMapEditPacket.send(targetUuid, mapField, initialKey, "", true);
        close();
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

        g.drawCenteredString(this.font, (isNew ? "New entry — " : "Edit — ") + mapField,
                bx + bw / 2, by + 6, generalConfigs.COLOR_ACCENT_GOLD);

        if (!errorMessage.isEmpty()) {
            g.drawCenteredString(this.font, errorMessage, bx + bw / 2, by + bh - 40, 0xFFFF5555);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) { save(); return true; }
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}