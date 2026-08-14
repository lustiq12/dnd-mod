package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Shows every tracked attribute's base value, each active modifier, and the resulting total. */
public class DebugStatsTab implements DebugTab {

    private static final int LEFT_W = 190;
    private static final int ROW_H = 20;
    private static final int DETAIL_ROW_H = 12;

    private final DebugMainScreen screen;

    private int x, y, w, h;
    private int rightX, rightW;

    private UUID lastRequestedUuid;
    private List<DebugAttributesClientState.AttributeEntry> attributes = List.of();
    private int selected = 0;
    private int hoveredRow = -1;
    private int listScroll = 0;

    private int refreshX, refreshY, refreshW, refreshH;

    public DebugStatsTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Stats";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        rightX = x + LEFT_W + 8;
        rightW = w - LEFT_W - 8;

        if (snapshot == null) {
            attributes = List.of();
            lastRequestedUuid = null;
            return;
        }

        DebugAttributesClientState.Snapshot attrSnap = DebugAttributesClientState.get();
        attributes = (attrSnap != null && attrSnap.uuid().equals(snapshot.uuid())) ? attrSnap.attributes() : List.of();

        if (!snapshot.uuid().equals(lastRequestedUuid)) {
            lastRequestedUuid = snapshot.uuid();
            DebugAttributesRequestPacket.send(snapshot.uuid().toString());
        }

        selected = Math.min(selected, Math.max(0, attributes.size() - 1));
    }

    private void refresh() {
        if (lastRequestedUuid != null) DebugAttributesRequestPacket.send(lastRequestedUuid.toString());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();

        DebugAttributesClientState.Snapshot attrSnap = DebugAttributesClientState.get();
        if (attrSnap != null && attrSnap.uuid().equals(lastRequestedUuid) && attrSnap.attributes() != attributes) {
            attributes = attrSnap.attributes();
            selected = Math.min(selected, Math.max(0, attributes.size() - 1));
        }

        refreshW = 60; refreshH = 12;
        refreshX = rightX + rightW - refreshW - 4;
        refreshY = y + 2;
        boolean refreshHovered = mouseX >= refreshX && mouseX <= refreshX + refreshW
                && mouseY >= refreshY && mouseY <= refreshY + refreshH;
        g.drawString(font, "[Refresh]", refreshX, refreshY, refreshHovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);

        if (attributes.isEmpty()) {
            g.drawString(font, "No attribute data loaded yet.", x + 4, y + 20, generalConfigs.TEXT_GRAY, false);
            return;
        }

        g.enableScissor(x, y, x + LEFT_W, y + h);
        hoveredRow = -1;
        int visibleRows = Math.max(1, h / ROW_H);
        for (int i = 0; i < visibleRows; i++) {
            int idx = i + listScroll;
            if (idx >= attributes.size()) break;
            DebugAttributesClientState.AttributeEntry entry = attributes.get(idx);
            int ry = y + i * ROW_H;

            boolean hovered = mouseX >= x && mouseX < x + LEFT_W && mouseY >= ry && mouseY < ry + ROW_H;
            boolean isSelected = idx == selected;
            if (hovered) hoveredRow = idx;
            if (isSelected) g.fill(x, ry, x + LEFT_W, ry + ROW_H, 0x5500BB44);
            else if (hovered) g.fill(x, ry, x + LEFT_W, ry + ROW_H, generalConfigs.COLOR_HOVER_BG);

            int col = isSelected ? generalConfigs.COLOR_ACCENT_GOLD : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            g.drawString(font, shortName(entry.attributeId()), x + 4, ry + 2, col, false);
            g.drawString(font, formatNum(entry.total()), x + 4, ry + 11, generalConfigs.TEXT_GRAY, false);
        }
        g.disableScissor();

        g.fill(rightX - 4, y, rightX - 3, y + h, generalConfigs.COLOR_PANEL_EDGE);

        if (selected < 0 || selected >= attributes.size()) return;
        DebugAttributesClientState.AttributeEntry entry = attributes.get(selected);

        int ry = y + 15;
        g.drawString(font, entry.attributeId(), rightX, ry, generalConfigs.COLOR_ACCENT_GOLD, false);
        ry += 12;

        g.drawString(font, "Base value: " + formatNum(entry.base()), rightX, ry, generalConfigs.TEXT_WHITE, false);
        ry += DETAIL_ROW_H + 2;

        if (entry.modifiers().isEmpty()) {
            g.drawString(font, "(no active modifiers)", rightX, ry, generalConfigs.TEXT_GRAY, false);
            ry += DETAIL_ROW_H;
        } else {
            g.drawString(font, "Modifiers:", rightX, ry, generalConfigs.TEXT_GRAY, false);
            ry += DETAIL_ROW_H;
            for (DebugAttributesClientState.ModifierEntry mod : entry.modifiers()) {
                String line = mod.id() + "  " + signed(mod.amount()) + "  [" + mod.operation() + "]";
                g.drawString(font, line, rightX + 6, ry, generalConfigs.TEXT_WHITE, false);
                ry += DETAIL_ROW_H;
            }
        }

        ry += 4;
        g.fill(rightX, ry, rightX + rightW - 8, ry + 1, generalConfigs.COLOR_PANEL_EDGE);
        ry += 4;
        g.drawString(font, "Total: " + formatNum(entry.total()), rightX, ry, generalConfigs.COLOR_STATUS_SUCCESS, false);
    }

    private String shortName(String fullId) {
        int idx = fullId.indexOf(':');
        return idx >= 0 ? fullId.substring(idx + 1) : fullId;
    }

    private String formatNum(double val) {
        if (val == Math.rint(val)) return String.valueOf((long) val);
        return String.format(Locale.ROOT, "%.3f", val);
    }

    private String signed(double val) {
        String formatted = formatNum(val);
        return val >= 0 ? "+" + formatted : formatted;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        if (mouseX >= refreshX && mouseX <= refreshX + refreshW && mouseY >= refreshY && mouseY <= refreshY + refreshH) {
            refresh();
            return true;
        }

        if (hoveredRow >= 0) {
            selected = hoveredRow;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < x || mouseX > x + LEFT_W || mouseY < y || mouseY > y + h) return false;
        int visibleRows = Math.max(1, h / ROW_H);
        int maxScroll = Math.max(0, attributes.size() - visibleRows);
        listScroll = Math.max(0, Math.min(maxScroll, listScroll - (int) Math.signum(scrollY)));
        return true;
    }
}