package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.aUtils.GeneralDataUtils;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Side-by-side key-value browser/editor for AbilityData and GeneralData, using their real parse() utilities. */
public class DebugDataTab implements DebugTab {

    private static final int ROW_H = 14;
    private static final int ADD_ROW_H = 16;
    private static final int PAD_X = 5;
    private static final int PAD_Y = 2;

    private final DebugMainScreen screen;

    private int x, y, w, h;
    private int leftX, leftW, rightX, rightW;
    private int listY, listBottom;
    private int addRowY;

    private DndModVariables.PlayerVariables vars;
    private final List<Map.Entry<String, String>> abilityEntries = new ArrayList<>();
    private final List<Map.Entry<String, String>> generalEntries = new ArrayList<>();

    private int leftScroll = 0;
    private int rightScroll = 0;
    private int hoveredLeftRow = -1;
    private int hoveredRightRow = -1;
    private boolean hoveredLeftAdd = false;
    private boolean hoveredRightAdd = false;

    public DebugDataTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Data";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x + PAD_X; this.y = y + PAD_Y; this.w = w - PAD_X; this.h = h - PAD_Y;
        leftX = this.x; leftW = this.w / 2 - 4;
        rightX = this.x + this.w / 2 + 4; rightW = this.w / 2 - 4;
        listY = this.y + 14;
        addRowY = this.y + this.h - ADD_ROW_H;
        listBottom = addRowY - 2;

        vars = snapshot != null ? snapshot.vars() : null;
        abilityEntries.clear();
        generalEntries.clear();
        if (vars != null) {
            abilityEntries.addAll(AbilityDataUtils.parse(vars.AbilityData).entrySet());
            generalEntries.addAll(GeneralDataUtils.parse(vars.GeneralData).entrySet());
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        g.drawString(font, "AbilityData (" + abilityEntries.size() + ")", leftX, y + 2, generalConfigs.COLOR_ACCENT_GOLD, false);
        g.drawString(font, "GeneralData (" + generalEntries.size() + ")", rightX, y + 2, generalConfigs.COLOR_ACCENT_GOLD, false);

        hoveredLeftRow = renderColumn(g, font, leftX, leftW, abilityEntries, leftScroll, mouseX, mouseY);
        hoveredRightRow = renderColumn(g, font, rightX, rightW, generalEntries, rightScroll, mouseX, mouseY);

        hoveredLeftAdd = mouseX >= leftX && mouseX < leftX + leftW && mouseY >= addRowY && mouseY < addRowY + ADD_ROW_H;
        hoveredRightAdd = mouseX >= rightX && mouseX < rightX + rightW && mouseY >= addRowY && mouseY < addRowY + ADD_ROW_H;
        g.drawString(font, "[+ Add entry]", leftX, addRowY + 2, hoveredLeftAdd ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
        g.drawString(font, "[+ Add entry]", rightX, addRowY + 2, hoveredRightAdd ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);

        g.fill(rightX - 4, y, rightX - 3, y + h, generalConfigs.COLOR_PANEL_EDGE);
    }

    private int renderColumn(GuiGraphics g, Font font, int colX, int colW,
                             List<Map.Entry<String, String>> entries, int scroll, int mouseX, int mouseY) {
        g.enableScissor(colX, listY, colX + colW, listBottom);
        int hovered = -1;
        int visible = Math.max(1, (listBottom - listY) / ROW_H);
        for (int i = 0; i < visible; i++) {
            int idx = i + scroll;
            if (idx >= entries.size()) break;
            Map.Entry<String, String> entry = entries.get(idx);
            int ry = listY + i * ROW_H;

            boolean rowHovered = mouseX >= colX && mouseX < colX + colW && mouseY >= ry && mouseY < ry + ROW_H;
            if (rowHovered) { hovered = idx; g.fill(colX, ry, colX + colW, ry + ROW_H, generalConfigs.COLOR_HOVER_BG); }

            String line = entry.getKey() + " = " + entry.getValue();
            g.drawString(font, trim(font, line, colW - 4), colX + 2, ry + 2,
                    rowHovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE, false);
        }
        g.disableScissor();

        if (entries.isEmpty()) {
            g.drawString(font, "(empty)", colX, listY, generalConfigs.TEXT_GRAY, false);
        }

        return hovered;
    }

    private String trim(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text;
        while (text.length() > 1 && font.width(text + "...") > maxWidth) text = text.substring(0, text.length() - 1);
        return text + "...";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        if (hoveredLeftRow >= 0) {
            Map.Entry<String, String> entry = abilityEntries.get(hoveredLeftRow);
            screen.openPopup(new DebugDataMapEditPopup(screen, uuid, "AbilityData", entry.getKey(), entry.getValue(), false));
            return true;
        }
        if (hoveredRightRow >= 0) {
            Map.Entry<String, String> entry = generalEntries.get(hoveredRightRow);
            screen.openPopup(new DebugDataMapEditPopup(screen, uuid, "GeneralData", entry.getKey(), entry.getValue(), false));
            return true;
        }
        if (hoveredLeftAdd) {
            screen.openPopup(new DebugDataMapEditPopup(screen, uuid, "AbilityData", "", "", true));
            return true;
        }
        if (hoveredRightAdd) {
            screen.openPopup(new DebugDataMapEditPopup(screen, uuid, "GeneralData", "", "", true));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int visible = Math.max(1, (listBottom - listY) / ROW_H);
        if (mouseX >= leftX && mouseX < leftX + leftW && mouseY >= listY && mouseY < listBottom) {
            int max = Math.max(0, abilityEntries.size() - visible);
            leftScroll = Math.max(0, Math.min(max, leftScroll - (int) Math.signum(scrollY)));
            return true;
        }
        if (mouseX >= rightX && mouseX < rightX + rightW && mouseY >= listY && mouseY < listBottom) {
            int max = Math.max(0, generalEntries.size() - visible);
            rightScroll = Math.max(0, Math.min(max, rightScroll - (int) Math.signum(scrollY)));
            return true;
        }
        return false;
    }
}