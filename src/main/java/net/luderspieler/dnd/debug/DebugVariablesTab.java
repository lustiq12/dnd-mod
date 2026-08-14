package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.GuiGraphics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Reflects every PlayerVariables field into an editable, scrollable row list. */
public class DebugVariablesTab implements DebugTab {

    private static final int ROW_H = 16;

    /** Fields that are actually comma-separated lists, based on their real .split(",") usage elsewhere in the mod. */
    private static final Set<String> LIST_FIELDS = Set.of(
            "Abilities", "Feats", "Proficiencys", "ChoicesNeeded", "ChoicesMade",
            "PreparedCantrips", "PreparedSpellsLVL1", "PreparedSpellsLVL2", "PreparedSpellsLVL3",
            "PreparedSpellsLVL4", "PreparedSpellsLVL5", "PreparedSpellsLVL6", "PreparedSpellsLVL7",
            "PreparedSpellsLVL8", "PreparedSpellsLVL9", "targetUUIDS"
    );

    private enum Kind { STRING, LIST, DOUBLE, BOOLEAN }

    private record Row(String name, Kind kind, String preview) {}

    private final DebugMainScreen screen;
    private final List<Row> rows = new ArrayList<>();

    private int x, y, w, h;
    private int scroll = 0;
    private int hoveredRow = -1;

    public DebugVariablesTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Variables";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        rows.clear();
        if (snapshot == null) return;

        DndModVariables.PlayerVariables vars = snapshot.vars();
        for (Field field : DndModVariables.PlayerVariables.class.getDeclaredFields()) {
            if (field.getName().startsWith("_")) continue;
            Class<?> type = field.getType();
            try {
                Object value = field.get(vars);
                if (type == String.class) {
                    Kind kind = LIST_FIELDS.contains(field.getName()) ? Kind.LIST : Kind.STRING;
                    rows.add(new Row(field.getName(), kind, preview((String) value)));
                } else if (type == double.class || type == Double.class) {
                    rows.add(new Row(field.getName(), Kind.DOUBLE, String.valueOf(value)));
                } else if (type == boolean.class || type == Boolean.class) {
                    rows.add(new Row(field.getName(), Kind.BOOLEAN, String.valueOf(value)));
                }
            } catch (IllegalAccessException ignored) {}
        }

        int maxScroll = Math.max(0, rows.size() - visibleRows());
        scroll = Math.min(scroll, maxScroll);
    }

    private String preview(String raw) {
        if (raw == null || raw.isBlank() || raw.equals("\"\"")) return "(empty)";
        return raw.length() > 60 ? raw.substring(0, 57) + "..." : raw;
    }

    private int visibleRows() {
        return Math.max(1, h / ROW_H);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.enableScissor(x, y, x + w, y + h);

        hoveredRow = -1;
        int rowsShown = visibleRows();
        for (int i = 0; i < rowsShown; i++) {
            int idx = i + scroll;
            if (idx >= rows.size()) break;
            Row row = rows.get(idx);
            int ry = y + i * ROW_H;

            boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= ry && mouseY < ry + ROW_H;
            if (hovered) hoveredRow = idx;
            if (hovered) g.fill(x, ry, x + w, ry + ROW_H, generalConfigs.COLOR_HOVER_BG);

            int nameCol = hovered ? generalConfigs.TEXT_HOVER : generalConfigs.COLOR_ACCENT_GOLD;
            g.drawString(screen.getFontInstance(), row.name() + " [" + row.kind() + "]", x + 4, ry + 4, nameCol, false);

            g.drawString(screen.getFontInstance(), row.preview(), x + 260, ry + 4, generalConfigs.TEXT_WHITE, false);

            String hint = "Click to edit";
            int hintW = screen.getFontInstance().width(hint);
            g.drawString(screen.getFontInstance(), hint, x + w - hintW - 4, ry + 4,
                    hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_DARK_GRAY, false);
        }

        g.disableScissor();

        if (rows.size() > rowsShown) {
            int maxScroll = Math.max(1, rows.size() - rowsShown);
            int thumbH = Math.max(10, h * rowsShown / rows.size());
            int thumbY = y + scroll * (h - thumbH) / maxScroll;
            g.fill(x + w - 3, y, x + w - 1, y + h, 0x33FFFFFF);
            g.fill(x + w - 3, thumbY, x + w - 1, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || hoveredRow < 0 || hoveredRow >= rows.size()) return false;
        Row row = rows.get(hoveredRow);
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        switch (row.kind()) {
            case STRING -> screen.openPopup(new DebugTextEditPopup(screen, uuid, row.name(),
                    currentRawValue(row.name()), DebugTextEditPopup.Kind.STRING));
            case DOUBLE -> screen.openPopup(new DebugTextEditPopup(screen, uuid, row.name(),
                    currentRawValue(row.name()), DebugTextEditPopup.Kind.DOUBLE));
            case BOOLEAN -> screen.openPopup(new DebugTextEditPopup(screen, uuid, row.name(),
                    currentRawValue(row.name()), DebugTextEditPopup.Kind.BOOLEAN));
            case LIST -> screen.openPopup(new DebugListEditPopup(screen, uuid, row.name(), currentRawValue(row.name())));
        }
        return true;
    }

    private String currentRawValue(String fieldName) {
        DebugClientState.Snapshot snap = DebugClientState.get();
        if (snap == null) return "";
        try {
            Field field = DndModVariables.PlayerVariables.class.getField(fieldName);
            Object v = field.get(snap.vars());
            return v == null ? "" : String.valueOf(v);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < x || mouseX > x + w || mouseY < y || mouseY > y + h) return false;
        int maxScroll = Math.max(0, rows.size() - visibleRows());
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(scrollY)));
        return true;
    }
}