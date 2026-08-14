package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Editor for ChoicesNeeded / ChoicesMade, backed directly by ChoiceExecutor and ChoiceUpdateSystem. */
public class DebugChoicesTab implements DebugTab {

    // Real choice IDs pulled from ChoiceExecutor's switch statement, offered as quick-fill only.
    private static final String[] KNOWN_IDS = {
            "SUBCLASS", "ABILITY_SCORE_IMPROVEMENT_OR_FEAT", "FIGHTING_STYLE", "HOLY_ORDER",
            "PRIMAL_ORDER", "ELDRITCH_INVOCATION", "BARDIC_COLLEGE_SKILL", "TOOL_PROFICIENCY",
            "METAMAGIC", "DRACONIC_ANCESTRY"
    };

    private static final int ROW_H = 14;
    private static final int VISIBLE_ROWS = 6;
    private static final int CHIP_ROW_H = 12;
    private static final int PAD_X = 6;
    private static final int PAD_Y = 2;

    private final DebugMainScreen screen;

    private int x, y, w, h;
    private int neededX, neededW, madeX, madeW, listY, listBottom;
    private int formY;

    private EditBox neededAddBox;
    private EditBox choiceIdBox;
    private EditBox valueBox;

    private DndModVariables.PlayerVariables vars;
    private final List<String> neededEntries = new ArrayList<>();
    private final List<String> madeEntries = new ArrayList<>();

    private int neededScroll = 0;
    private int madeScroll = 0;
    private int hoveredNeededRemove = -1;
    private int hoveredMadeRemove = -1;
    private int hoveredChip = -1;

    public DebugChoicesTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Choices";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x + PAD_X; this.y = y + PAD_Y; this.w = w - PAD_X * 2; this.h = h - PAD_Y;
        vars = snapshot != null ? snapshot.vars() : null;

        neededX = this.x; madeX = this.x + this.w / 2 + 4;
        neededW = this.w / 2 - 4; madeW = this.w / 2 - 4;
        listY = this.y + 34;
        listBottom = listY + VISIBLE_ROWS * ROW_H;
        formY = listBottom + 40;

        neededEntries.clear();
        madeEntries.clear();
        if (vars != null) {
            splitInto(vars.ChoicesNeeded, neededEntries);
            splitInto(vars.ChoicesMade, madeEntries);
        }

        String targetUuid = screen.currentTargetUuid();
        Font font = screen.getFontInstance();

        neededAddBox = new EditBox(font, neededX, listBottom + 4, neededW - 50, 16, Component.literal("Choice ID"));
        screen.addTabWidget(neededAddBox);
        screen.addTabWidget(Button.builder(Component.literal("Add"), b -> {
            String v = neededAddBox.getValue().trim();
            if (!v.isEmpty() && targetUuid != null) {
                // allowDuplicates=true: choices like ELDRITCH_INVOCATION legitimately appear multiple times in ChoicesNeeded.
                DebugListModifyPacket.send(targetUuid, "ChoicesNeeded", v, true, true);
                neededEntries.add(v);
                neededAddBox.setValue("");
            }
        }).bounds(neededX + neededW - 46, listBottom + 4, 46, 16).build());

        screen.addTabWidget(Button.builder(Component.literal("Recalculate Needed"), b -> {
            if (targetUuid != null) DebugChoiceRecalcPacket.send(targetUuid);
        }).bounds(madeX, listBottom + 4, madeW, 16).build());

        // Reserve exactly as much vertical space as the quick-fill chips need, so the input row below never overlaps them.
        int chipRows = countChipRows(font, this.w);
        int inputY = formY + 10 + chipRows * CHIP_ROW_H + 6;

        choiceIdBox = new EditBox(font, this.x, inputY, 150, 16, Component.literal("Choice ID"));
        screen.addTabWidget(choiceIdBox);
        valueBox = new EditBox(font, this.x + 156, inputY, this.w - 156 - 70, 16, Component.literal("Selected value"));
        screen.addTabWidget(valueBox);
        screen.addTabWidget(Button.builder(Component.literal("Apply"), b -> {
            String id = choiceIdBox.getValue().trim();
            String val = valueBox.getValue().trim();
            if (!id.isEmpty() && targetUuid != null) {
                DebugChoiceApplyPacket.send(targetUuid, id, val);
                madeEntries.add(id + ":" + val);
                neededEntries.remove(id);
            }
        }).bounds(this.x + this.w - 64, inputY, 64, 16).build());
    }

    private int countChipRows(Font font, int width) {
        int cx = 0;
        int rows = 1;
        for (String id : KNOWN_IDS) {
            int cw = font.width(id) + 8;
            if (cx + cw > width) { cx = 0; rows++; }
            cx += cw + 4;
        }
        return rows;
    }

    private void splitInto(String raw, List<String> out) {
        if (raw == null || raw.isBlank() || raw.equals("\"\"")) return;
        for (String s : raw.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        g.drawString(font, "Needed (" + neededEntries.size() + ")", neededX, y + 4, generalConfigs.COLOR_ACCENT_GOLD, false);
        g.drawString(font, "Made (" + madeEntries.size() + ")", madeX, y + 4, generalConfigs.COLOR_ACCENT_GOLD, false);
        g.drawString(font, "removing here only edits the raw list — already granted effects are not undone",
                madeX, y + 16, generalConfigs.COLOR_STATUS_WIP, false);

        g.enableScissor(neededX, listY, neededX + neededW, listBottom);
        hoveredNeededRemove = -1;
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = i + neededScroll;
            if (idx >= neededEntries.size()) break;
            int ry = listY + i * ROW_H;
            boolean hovered = mouseX >= neededX && mouseX < neededX + neededW && mouseY >= ry && mouseY < ry + ROW_H;
            if (hovered) { hoveredNeededRemove = idx; g.fill(neededX, ry, neededX + neededW, ry + ROW_H, generalConfigs.COLOR_ROW_DANGER); }
            g.drawString(font, neededEntries.get(idx), neededX + 2, ry + 2, generalConfigs.TEXT_WHITE, false);
            g.drawString(font, "✗", neededX + neededW - 10, ry + 2, generalConfigs.TEXT_DARK_GRAY, false);
        }
        g.disableScissor();

        g.enableScissor(madeX, listY, madeX + madeW, listBottom);
        hoveredMadeRemove = -1;
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = i + madeScroll;
            if (idx >= madeEntries.size()) break;
            int ry = listY + i * ROW_H;
            boolean hovered = mouseX >= madeX && mouseX < madeX + madeW && mouseY >= ry && mouseY < ry + ROW_H;
            if (hovered) { hoveredMadeRemove = idx; g.fill(madeX, ry, madeX + madeW, ry + ROW_H, generalConfigs.COLOR_ROW_DANGER); }
            g.drawString(font, madeEntries.get(idx), madeX + 2, ry + 2, generalConfigs.TEXT_WHITE, false);
            g.drawString(font, "✗", madeX + madeW - 10, ry + 2, generalConfigs.TEXT_DARK_GRAY, false);
        }
        g.disableScissor();

        g.drawString(font, "Apply choice — quick fill:", x, formY, generalConfigs.TEXT_GRAY, false);
        hoveredChip = -1;
        int cx = x;
        int cy = formY + 10;
        for (int i = 0; i < KNOWN_IDS.length; i++) {
            String id = KNOWN_IDS[i];
            int cw = font.width(id) + 8;
            if (cx + cw > x + w) { cx = x; cy += CHIP_ROW_H; }
            boolean hovered = mouseX >= cx && mouseX < cx + cw && mouseY >= cy && mouseY < cy + 11;
            if (hovered) hoveredChip = i;
            g.drawString(font, id, cx + 4, cy + 1, hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
            cx += cw + 4;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();

        if (hoveredNeededRemove >= 0 && uuid != null) {
            String v = neededEntries.get(hoveredNeededRemove);
            DebugListModifyPacket.send(uuid, "ChoicesNeeded", v, false);
            neededEntries.remove(hoveredNeededRemove);
            return true;
        }
        if (hoveredMadeRemove >= 0 && uuid != null) {
            String v = madeEntries.get(hoveredMadeRemove);
            DebugListModifyPacket.send(uuid, "ChoicesMade", v, false);
            madeEntries.remove(hoveredMadeRemove);
            return true;
        }
        if (hoveredChip >= 0) {
            String id = KNOWN_IDS[hoveredChip];
            if (choiceIdBox != null) choiceIdBox.setValue(id);
            if (neededAddBox != null) neededAddBox.setValue(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= neededX && mouseX < neededX + neededW && mouseY >= listY && mouseY < listBottom) {
            int max = Math.max(0, neededEntries.size() - VISIBLE_ROWS);
            neededScroll = Math.max(0, Math.min(max, neededScroll - (int) Math.signum(scrollY)));
            return true;
        }
        if (mouseX >= madeX && mouseX < madeX + madeW && mouseY >= listY && mouseY < listBottom) {
            int max = Math.max(0, madeEntries.size() - VISIBLE_ROWS);
            madeScroll = Math.max(0, Math.min(max, madeScroll - (int) Math.signum(scrollY)));
            return true;
        }
        return false;
    }
}