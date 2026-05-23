package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.choices.ExecuteChoicePacket;
import net.luderspieler.dnd.character.choices.LevelingChoiceScreen;
import net.luderspieler.dnd.character.definition.SubclassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SubclassSelectionScreen extends Screen {

    private static final int PANEL_W  = 520;
    private static final int PANEL_H  = 290;
    private static final int LIST_W   = 155;
    private static final int ROW_H    = 22;
    private static final int DETAIL_W = PANEL_W - LIST_W - 30;

    private final Screen parent;
    private final List<SubclassDefinition> subclasses;
    private final String className;
    private SubclassDefinition selected = null;
    private int hoveredIdx = -1;

    public SubclassSelectionScreen(Screen parent) {
        super(Component.literal("Choose Your Subclass"));
        this.parent = parent;
        var vars = Minecraft.getInstance().player.getData(DndModVariables.PLAYER_VARIABLES);
        this.className = vars.PlayerClass;
        this.subclasses = ClassRegistry.getSubclassesFor(className);
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - PANEL_W) / 2;
        int topPos  = (this.height - PANEL_H) / 2;
        int btnY    = topPos + PANEL_H - 26;

        this.addRenderableWidget(Button.builder(
                Component.literal("Confirm"),
                btn -> confirm()
        ).bounds(leftPos + PANEL_W / 2 - 85, btnY, 80, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(parent)
        ).bounds(leftPos + PANEL_W / 2 + 5, btnY, 80, 20).build());
    }

    private void confirm() {
        if (selected == null) return;
        ExecuteChoicePacket.send(new ExecuteChoicePacket("SUBCLASS", selected.getDisplayName()));
        if (parent instanceof LevelingChoiceScreen lcs) {
            lcs.removeChoiceLocally("SUBCLASS");
        }
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Background
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - PANEL_W) / 2;
        int topPos  = (this.height - PANEL_H) / 2;

        // Panel background + border
        g.fill(leftPos, topPos, leftPos + PANEL_W, topPos + PANEL_H, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, PANEL_W, PANEL_H);

        // Title
        String title = capitalize(className) + " — Choose a Subclass";
        g.drawCenteredString(this.font, title,
                leftPos + PANEL_W / 2, topPos + 7, generalConfigs.COLOR_ACCENT_GOLD);

        // Horizontal divider below title
        g.fill(leftPos + 1, topPos + 18, leftPos + PANEL_W - 1, topPos + 19,
                generalConfigs.COLOR_PANEL_EDGE);

        // ── LEFT: subclass list ───────────────────────────────────────
        int listX = leftPos + 8;
        int listY = topPos + 24;
        hoveredIdx = -1;

        g.enableScissor(leftPos + 1, topPos + 20, leftPos + LIST_W + 12, topPos + PANEL_H - 28);
        for (int i = 0; i < subclasses.size(); i++) {
            SubclassDefinition sub = subclasses.get(i);
            int rowY = listY + i * ROW_H;
            boolean hov = mouseX >= listX && mouseX < listX + LIST_W + 4
                    && mouseY >= rowY   && mouseY < rowY + ROW_H;
            boolean sel = sub == selected;

            if (hov) hoveredIdx = i;

            if (sel) {
                g.fill(listX - 2, rowY, listX + LIST_W + 4, rowY + ROW_H, 0x55_00BB44);
                generalConfigs.renderGreenEdge(g, listX - 2, rowY, LIST_W + 6, ROW_H);
            } else if (hov) {
                g.fill(listX - 2, rowY, listX + LIST_W + 4, rowY + ROW_H,
                        generalConfigs.COLOR_HOVER_BG);
            }

            int col = sel  ? generalConfigs.COLOR_ACCENT_GOLD
                    : hov  ? generalConfigs.TEXT_HOVER
                      : generalConfigs.TEXT_WHITE;

            g.drawString(this.font, sub.getDisplayName(), listX + 5, rowY + 7, col, false);
        }
        g.disableScissor();

        // Vertical divider
        int divX = leftPos + LIST_W + 14;
        g.fill(divX, topPos + 20, divX + 1, topPos + PANEL_H - 28, generalConfigs.COLOR_PANEL_EDGE);

        // ── RIGHT: subclass details ───────────────────────────────────
        int detailX = divX + 8;
        int detailY = topPos + 24;

        if (selected != null) {
            // Name
            g.drawString(this.font, selected.getDisplayName(),
                    detailX, detailY, generalConfigs.COLOR_ACCENT_GOLD, false);
            detailY += 14;

            // Description
            g.drawWordWrap(this.font,
                    Component.literal(selected.getDescription()),
                    detailX, detailY, DETAIL_W, generalConfigs.TEXT_GRAY);
            detailY += this.font.wordWrapHeight(selected.getDescription(), DETAIL_W) + 8;

            // Feature headline
            g.drawString(this.font, "Key Features:",
                    detailX, detailY, generalConfigs.COLOR_ACCENT_GOLD, false);
            detailY += 12;

            // Ability lines (all, clip at panel boundary)
            int maxY = topPos + PANEL_H - 32;
            for (String line : selected.getAbilityLines()) {
                if (detailY >= maxY) break;
                String text = "» " + line;
                g.drawWordWrap(this.font, Component.literal(text),
                        detailX, detailY, DETAIL_W, generalConfigs.TEXT_WHITE);
                detailY += this.font.wordWrapHeight(text, DETAIL_W) + 3;
            }
        } else {
            // Placeholder
            g.drawCenteredString(this.font, "Select a subclass on the left",
                    divX + DETAIL_W / 2 + 4,
                    topPos + PANEL_H / 2 - 10,
                    generalConfigs.TEXT_GRAY);

            if (subclasses.isEmpty()) {
                g.drawCenteredString(this.font, "(No subclasses registered for " + className + ")",
                        divX + DETAIL_W / 2 + 4,
                        topPos + PANEL_H / 2 + 4,
                        generalConfigs.COLOR_STATUS_DANGER);
            }
        }

        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0 && hoveredIdx >= 0 && hoveredIdx < subclasses.size()) {
            selected = subclasses.get(hoveredIdx);
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 257) { confirm(); return true; } // Enter
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}