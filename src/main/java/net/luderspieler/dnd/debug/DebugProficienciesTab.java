package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.aUtils.ProficiencyUtils;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Toggles for known armor/weapon proficiency tags (ProficiencyCheckProcedure) plus current-list management. */
public class DebugProficienciesTab implements DebugTab {

    private static final String[] ARMOR_WEAPON_TAGS = {
            "light_armor", "medium_armor", "heavy_armor", "shields", "simple_weapons", "war_weapons"
    };

    // Real tool proficiency options from ChoiceRegistry's TOOL_PROFICIENCY case.
    private static final String[] TOOL_OPTIONS = {
            "Thieves' Tools", "Alchemist's Supplies", "Smith's Tools", "Brewer's Supplies"
    };

    private static final int ROW_H = 14;
    private static final int CHIP_ROW_H = 14;
    private static final int PAD_X = 5;
    private static final int PAD_Y = 2;

    private final DebugMainScreen screen;
    private int x, y, w, h;
    private int listY, listBottom;

    private DndModVariables.PlayerVariables vars;
    private final List<String> current = new ArrayList<>();

    private EditBox freeformBox;
    private int scroll = 0;
    private int hoveredRemove = -1;
    private int hoveredChip = -1;
    private final List<String> chipIds = new ArrayList<>();

    public DebugProficienciesTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Proficiencies";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x + PAD_X; this.y = y + PAD_Y; this.w = w - PAD_X; this.h = h - PAD_Y;
        vars = snapshot != null ? snapshot.vars() : null;

        current.clear();
        if (vars != null && vars.Proficiencys != null && !vars.Proficiencys.isBlank() && !vars.Proficiencys.equals("\"\"")) {
            for (String s : vars.Proficiencys.split(",")) {
                String t = s.trim();
                if (!t.isEmpty()) current.add(t);
            }
        }

        listY = this.y + 60;
        listBottom = this.y + this.h - 26;

        String uuid = screen.currentTargetUuid();
        freeformBox = new EditBox(screen.getFontInstance(), this.x, this.y + this.h - 20, this.w - 60, 16, Component.literal("Custom proficiency name"));
        screen.addTabWidget(freeformBox);
        screen.addTabWidget(Button.builder(Component.literal("Add"), b -> {
            String raw = freeformBox.getValue().trim();
            if (raw.isEmpty() || uuid == null) return;
            String key = ProficiencyUtils.toProficiencyKey(raw);
            if (!current.contains(key)) {
                DebugListModifyPacket.send(uuid, "Proficiencys", key, true);
                current.add(key);
            }
            freeformBox.setValue("");
        }).bounds(this.x + this.w - 56, this.y + this.h - 20, 56, 16).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        g.drawString(font, "Known tags (click to toggle):", x, y + 2, generalConfigs.TEXT_GRAY, false);

        chipIds.clear();
        hoveredChip = -1;
        int cx = x, cy = y + 12;

        for (String tag : ARMOR_WEAPON_TAGS) {
            cx = renderChip(g, font, tag, tag, cx, cy, mouseX, mouseY);
        }
        for (String tool : TOOL_OPTIONS) {
            String key = ProficiencyUtils.toProficiencyKey(tool);
            cx = renderChip(g, font, tool, key, cx, cy, mouseX, mouseY);
        }

        g.drawString(font, "Current (" + current.size() + "):", x, listY - 10, generalConfigs.COLOR_ACCENT_GOLD, false);
        g.enableScissor(x, listY, x + w, listBottom);
        hoveredRemove = -1;
        int visible = Math.max(1, (listBottom - listY) / ROW_H);
        for (int i = 0; i < visible; i++) {
            int idx = i + scroll;
            if (idx >= current.size()) break;
            int ry = listY + i * ROW_H;
            boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= ry && mouseY < ry + ROW_H;
            if (hovered) { hoveredRemove = idx; g.fill(x, ry, x + w, ry + ROW_H, generalConfigs.COLOR_ROW_DANGER); }
            g.drawString(font, current.get(idx), x + 2, ry + 2, generalConfigs.TEXT_WHITE, false);
            g.drawString(font, "✗", x + w - 10, ry + 2, generalConfigs.TEXT_DARK_GRAY, false);
        }
        g.disableScissor();
    }

    private int renderChip(GuiGraphics g, Font font, String label, String key, int cx, int cy, int mouseX, int mouseY) {
        int cw = font.width(label) + 10;
        if (cx + cw > x + w) { cx = x; cy += CHIP_ROW_H; }
        boolean active = current.contains(key);
        boolean hovered = mouseX >= cx && mouseX < cx + cw && mouseY >= cy && mouseY < cy + 11;
        if (hovered) hoveredChip = chipIds.size();
        int col = active ? generalConfigs.COLOR_STATUS_SUCCESS : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
        g.drawString(font, (active ? "✓ " : "") + label, cx + 4, cy + 1, col, false);
        chipIds.add(key);
        return cx + cw + 4;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        if (hoveredChip >= 0) {
            String key = chipIds.get(hoveredChip);
            boolean nowActive = !current.contains(key);
            DebugListModifyPacket.send(uuid, "Proficiencys", key, nowActive);
            if (nowActive) current.add(key); else current.remove(key);
            return true;
        }

        if (hoveredRemove >= 0) {
            String v = current.get(hoveredRemove);
            DebugListModifyPacket.send(uuid, "Proficiencys", v, false);
            current.remove(hoveredRemove);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < x || mouseX > x + w || mouseY < listY || mouseY > listBottom) return false;
        int visible = Math.max(1, (listBottom - listY) / ROW_H);
        int max = Math.max(0, current.size() - visible);
        scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(scrollY)));
        return true;
    }
}