package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Popup for editing a single String, double or boolean PlayerVariables field. */
public class DebugTextEditPopup extends Screen {

    public enum Kind { STRING, DOUBLE, BOOLEAN }

    private final DebugMainScreen parent;
    private final String targetUuid;
    private final String fieldName;
    private final String initialValue;
    private final Kind kind;

    private EditBox valueBox;
    private String errorMessage = "";
    private boolean boolValue;

    private int bx, by, bw, bh;

    public DebugTextEditPopup(DebugMainScreen parent, String targetUuid, String fieldName, String initialValue, Kind kind) {
        super(Component.literal("Edit " + fieldName));
        this.parent = parent;
        this.targetUuid = targetUuid;
        this.fieldName = fieldName;
        this.initialValue = initialValue;
        this.kind = kind;
        this.boolValue = Boolean.parseBoolean(initialValue);
    }

    @Override
    protected void init() {
        bw = Math.max(220, Math.min(420, this.font.width(initialValue) + 40));
        bh = 92;
        bx = (this.width - bw) / 2;
        by = (this.height - bh) / 2;

        if (kind == Kind.BOOLEAN) {
            addRenderableWidget(Button.builder(Component.literal("True"), b -> { boolValue = true; save(); })
                    .bounds(bx + 12, by + 40, (bw - 32) / 2, 20).build());
            addRenderableWidget(Button.builder(Component.literal("False"), b -> { boolValue = false; save(); })
                    .bounds(bx + 20 + (bw - 32) / 2, by + 40, (bw - 32) / 2, 20).build());
        } else {
            valueBox = new EditBox(this.font, bx + 12, by + 40, bw - 24, 18, Component.literal(fieldName));
            valueBox.setMaxLength(4096);
            valueBox.setValue(initialValue);
            if (kind == Kind.DOUBLE) {
                valueBox.setFilter(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d*\\.?\\d*"));
            }
            addRenderableWidget(valueBox);
            setInitialFocus(valueBox);

            addRenderableWidget(Button.builder(Component.literal("Save"), b -> save())
                    .bounds(bx + 12, by + bh - 26, (bw - 32) / 2, 18).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(kind == Kind.BOOLEAN ? bx + 12 : bx + 20 + (bw - 32) / 2, by + bh - 26,
                        kind == Kind.BOOLEAN ? bw - 24 : (bw - 32) / 2, 18).build());
    }

    private void save() {
        if (kind == Kind.DOUBLE) {
            String text = valueBox.getValue().trim();
            try {
                Double.parseDouble(text.isEmpty() ? "0" : text);
            } catch (NumberFormatException e) {
                errorMessage = "§cNot a valid number.";
                return;
            }
            DebugSetFieldPacket.send(targetUuid, fieldName, text.isEmpty() ? "0" : text);
        } else if (kind == Kind.BOOLEAN) {
            DebugSetFieldPacket.send(targetUuid, fieldName, String.valueOf(boolValue));
        } else {
            DebugSetFieldPacket.send(targetUuid, fieldName, valueBox.getValue());
        }
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

        g.drawCenteredString(this.font, "Edit " + fieldName, bx + bw / 2, by + 6, generalConfigs.COLOR_ACCENT_GOLD);

        if (kind == Kind.BOOLEAN) {
            g.drawCenteredString(this.font, "Current: " + boolValue, bx + bw / 2, by + 20, generalConfigs.TEXT_GRAY);
        }

        if (!errorMessage.isEmpty()) {
            g.drawCenteredString(this.font, errorMessage, bx + bw / 2, by + bh - 36, 0xFFFF5555);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && kind != Kind.BOOLEAN) { save(); return true; }
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}