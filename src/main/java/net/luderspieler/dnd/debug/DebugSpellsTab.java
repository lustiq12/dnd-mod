package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.Spells;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;
import java.util.stream.Collectors;

/** Prepared-spell toggles per grade plus a spell slot editor, mirroring SpellPrepScreen's data sources. */
public class DebugSpellsTab implements DebugTab {

    private static final int ROW_A_H = 16;
    private static final int CONTENT_GAP = 6;
    private static final int SLOT_PANEL_W = 150;
    private static final int SLOT_ROW_H = 18;
    private static final int CHIP_W = 170;
    private static final int CHIP_H = 26;
    private static final int CHIP_GAP = 4;
    private static final int PAD_X = 5;
    private static final int PAD_Y = 2;

    private static final List<List<String>> GRADE_SPELL_NAMES = buildGradeSpellNames();
    private static final String[] GRADE_LABELS = {"C", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private final DebugMainScreen screen;

    private int x, y, w, h;
    private int gradesY, slotX, slotY, gridX, gridY, gridW, gridH;

    private int selectedGrade = 0;
    private int gridScroll = 0;

    private DndModVariables.PlayerVariables vars;
    private ClassDefinition classDef;

    private int hoveredGradeTab = -1;
    private int hoveredChip = -1;
    private int hoveredSlotMinus = -1;
    private int hoveredSlotPlus = -1;
    private boolean hoveredRefill = false;
    private boolean hoveredClear = false;

    public DebugSpellsTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Spells";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x + PAD_X; this.y = y + PAD_Y; this.w = w - PAD_X; this.h = h - PAD_Y;

        gradesY = this.y;
        gridX = this.x + SLOT_PANEL_W + 8;
        int contentTop = this.y + ROW_A_H + CONTENT_GAP;
        slotX = this.x; slotY = contentTop;
        gridY = contentTop;
        gridW = this.w - SLOT_PANEL_W - 8;
        gridH = this.h - ROW_A_H - CONTENT_GAP;

        vars = snapshot != null ? snapshot.vars() : null;
        classDef = vars != null ? ClassRegistry.getClass(vars.PlayerClass) : null;
        gridScroll = 0;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        Set<String> prepared = parsePrepared(selectedGrade);
        int max = classDef != null ? classDef.getMaxPreparedForGrade((int) vars.PlayerLevel, selectedGrade) : 0;

        renderTopRow(g, font, mouseX, mouseY, prepared.size(), max);
        renderSlotPanel(g, font, mouseX, mouseY);
        renderSpellGrid(g, font, mouseX, mouseY, prepared);
    }

    /** "Spell Slots" label, the grade tab bar (shifted to start where the spell grid starts), and the prepared/max header. */
    private void renderTopRow(GuiGraphics g, Font font, int mouseX, int mouseY, int preparedCount, int max) {
        g.drawString(font, "Spell Slots", x, gradesY + 3, generalConfigs.COLOR_ACCENT_GOLD, false);

        hoveredGradeTab = -1;
        int tx = gridX;
        for (int i = 0; i < GRADE_LABELS.length; i++) {
            int tw = font.width(GRADE_LABELS[i]) + 12;
            boolean hovered = mouseX >= tx && mouseX < tx + tw && mouseY >= gradesY && mouseY < gradesY + ROW_A_H;
            boolean selected = i == selectedGrade;
            if (hovered) hoveredGradeTab = i;
            if (selected) g.fill(tx, gradesY, tx + tw, gradesY + ROW_A_H, 0x5500BB44);
            else if (hovered) g.fill(tx, gradesY, tx + tw, gradesY + ROW_A_H, generalConfigs.COLOR_HOVER_BG);
            int col = selected ? generalConfigs.COLOR_ACCENT_GOLD : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            g.drawString(font, GRADE_LABELS[i], tx + 6, gradesY + 3, col, false);
            tx += tw + 2;
        }

        String header = preparedCount + " / " + max + " prepared";
        int headerColor = (max > 0 && preparedCount > max) ? generalConfigs.COLOR_STATUS_DANGER : generalConfigs.TEXT_GRAY;
        g.drawString(font, header, tx + 10, gradesY + 3, headerColor, false);
    }

    /** Always visible, since spell slots are shared across every grade, not just the currently selected tab. */
    private void renderSlotPanel(GuiGraphics g, Font font, int mouseX, int mouseY) {
        String rawSlots = normalizeSlots(vars.Spellslots);
        int level = (int) vars.PlayerLevel;

        hoveredSlotMinus = -1;
        hoveredSlotPlus = -1;
        for (int grade = 1; grade <= 9; grade++) {
            int ry = slotY + (grade - 1) * SLOT_ROW_H;
            int current = rawSlots.charAt(grade - 1) - '0';
            int max = maxSlotsFor(grade, level);

            g.drawString(font, "G" + grade + ":", slotX, ry + 4, generalConfigs.TEXT_WHITE, false);

            int minusX = slotX + 26;
            int valueX = minusX + 12;
            int plusX = valueX + 26;

            boolean hovMinus = mouseX >= minusX && mouseX < minusX + 10 && mouseY >= ry && mouseY < ry + SLOT_ROW_H - 2;
            boolean hovPlus = mouseX >= plusX && mouseX < plusX + 10 && mouseY >= ry && mouseY < ry + SLOT_ROW_H - 2;
            if (hovMinus) hoveredSlotMinus = grade;
            if (hovPlus) hoveredSlotPlus = grade;

            g.drawString(font, "-", minusX, ry + 4, hovMinus ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
            g.drawString(font, current + "/" + max, valueX, ry + 4, generalConfigs.TEXT_WHITE, false);
            g.drawString(font, "+", plusX, ry + 4, hovPlus ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
        }

        int btnY = slotY + 9 * SLOT_ROW_H + 8;
        hoveredRefill = mouseX >= slotX && mouseX < slotX + 60 && mouseY >= btnY && mouseY < btnY + 14;
        hoveredClear = mouseX >= slotX + 64 && mouseX < slotX + 124 && mouseY >= btnY && mouseY < btnY + 14;
        g.drawString(font, "[Refill]", slotX, btnY, hoveredRefill ? generalConfigs.COLOR_STATUS_SUCCESS : generalConfigs.TEXT_GRAY, false);
        g.drawString(font, "[Clear]", slotX + 64, btnY, hoveredClear ? generalConfigs.COLOR_STATUS_DANGER : generalConfigs.TEXT_GRAY, false);
    }

    private void renderSpellGrid(GuiGraphics g, Font font, int mouseX, int mouseY, Set<String> prepared) {
        List<String> names = GRADE_SPELL_NAMES.get(selectedGrade);
        Set<String> classSpells = classDef != null
                ? classDef.getSpellList().stream().map(e -> e.name().toUpperCase()).collect(Collectors.toSet())
                : null;

        int cols = Math.max(1, gridW / (CHIP_W + CHIP_GAP));
        int rows = (int) Math.ceil(names.size() / (double) cols);
        int visibleRows = Math.max(1, gridH / (CHIP_H + CHIP_GAP));
        int maxScroll = Math.max(0, rows - visibleRows);
        gridScroll = Math.min(gridScroll, maxScroll);

        g.enableScissor(gridX, gridY, gridX + gridW, gridY + gridH);
        hoveredChip = -1;
        for (int i = 0; i < names.size(); i++) {
            int row = i / cols - gridScroll;
            int col = i % cols;
            int cx = gridX + col * (CHIP_W + CHIP_GAP);
            int cy = gridY + row * (CHIP_H + CHIP_GAP);
            if (cy + CHIP_H < gridY || cy > gridY + gridH) continue;

            String name = names.get(i);
            boolean active = prepared.contains(name);
            boolean inClassList = classSpells == null || classSpells.contains(name);
            boolean hovered = mouseX >= cx && mouseX < cx + CHIP_W && mouseY >= cy && mouseY < cy + CHIP_H;
            if (hovered) hoveredChip = i;

            int bg;
            int nameCol;
            if (active && inClassList) {
                bg = 0x5500BB44; nameCol = generalConfigs.COLOR_STATUS_SUCCESS;
            } else if (active) {
                bg = 0x55CC3333; nameCol = generalConfigs.COLOR_STATUS_DANGER;
            } else if (hovered) {
                bg = generalConfigs.COLOR_HOVER_BG; nameCol = generalConfigs.TEXT_HOVER;
            } else {
                bg = 0x22000000; nameCol = inClassList ? generalConfigs.TEXT_WHITE : generalConfigs.TEXT_DARK_GRAY;
            }

            g.fill(cx, cy, cx + CHIP_W, cy + CHIP_H, bg);
            generalConfigs.renderGreenEdge(g, cx, cy, CHIP_W, CHIP_H);
            g.drawString(font, formatSpellName(name), cx + 4, cy + 3, nameCol, false);

            if (!inClassList) {
                int labelCol = active ? 0xFFFFCCCC : generalConfigs.COLOR_STATUS_WIP;
                g.drawString(font, "not on class list", cx + 4, cy + 14, labelCol, false);
            }
        }
        g.disableScissor();

        if (rows > visibleRows) {
            int maxS = Math.max(1, rows - visibleRows);
            int thumbH = Math.max(10, gridH * visibleRows / rows);
            int thumbY = gridY + gridScroll * (gridH - thumbH) / maxS;
            g.fill(x + w - 3, gridY, x + w - 1, gridY + gridH, 0x33FFFFFF);
            g.fill(x + w - 3, thumbY, x + w - 1, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    private String formatSpellName(String enumName) {
        StringBuilder sb = new StringBuilder();
        for (String part : enumName.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private Set<String> parsePrepared(int grade) {
        String raw = switch (grade) {
            case 0 -> vars.PreparedCantrips;
            case 1 -> vars.PreparedSpellsLVL1;
            case 2 -> vars.PreparedSpellsLVL2;
            case 3 -> vars.PreparedSpellsLVL3;
            case 4 -> vars.PreparedSpellsLVL4;
            case 5 -> vars.PreparedSpellsLVL5;
            case 6 -> vars.PreparedSpellsLVL6;
            case 7 -> vars.PreparedSpellsLVL7;
            case 8 -> vars.PreparedSpellsLVL8;
            case 9 -> vars.PreparedSpellsLVL9;
            default -> "";
        };
        Set<String> set = new HashSet<>();
        if (raw != null && !raw.isBlank() && !raw.equals("\"\"")) {
            for (String s : raw.split(",")) {
                String t = s.trim();
                if (!t.isEmpty()) set.add(t);
            }
        }
        return set;
    }

    private String normalizeSlots(String raw) {
        if (raw == null || raw.length() != 9 || raw.contains(",")) return "000000000";
        return raw;
    }

    private int maxSlotsFor(int grade, int level) {
        if (classDef == null || classDef.getSpellSlots() == null) return 0;
        int[][] table = classDef.getSpellSlots();
        int levelIdx = Math.max(0, Math.min(level, table.length - 1));
        if (grade - 1 >= table[levelIdx].length) return 0;
        return table[levelIdx][grade - 1];
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        if (hoveredGradeTab >= 0) {
            selectedGrade = hoveredGradeTab;
            gridScroll = 0;
            return true;
        }

        if (hoveredSlotMinus > 0 || hoveredSlotPlus > 0) {
            int grade = hoveredSlotMinus > 0 ? hoveredSlotMinus : hoveredSlotPlus;
            adjustSlot(uuid, grade, hoveredSlotPlus > 0);
            return true;
        }

        if (hoveredRefill) {
            if (classDef != null) {
                String slots = CharacterCreationPacket.resetSpellSlots(classDef, (int) vars.PlayerLevel);
                DebugSetFieldPacket.send(uuid, "Spellslots", slots);
                vars.Spellslots = slots;
            }
            return true;
        }

        if (hoveredClear) {
            DebugSetFieldPacket.send(uuid, "Spellslots", "000000000");
            vars.Spellslots = "000000000";
            return true;
        }

        if (hoveredChip >= 0) {
            List<String> names = GRADE_SPELL_NAMES.get(selectedGrade);
            if (hoveredChip >= names.size()) return false;
            String name = names.get(hoveredChip);
            boolean nowActive = !parsePrepared(selectedGrade).contains(name);
            DebugSpellTogglePacket.send(uuid, selectedGrade, name, nowActive, true);
            adjustLocalListField(fieldNameForGrade(selectedGrade), name, nowActive);
            return true;
        }

        return false;
    }

    private void adjustSlot(String uuid, int grade, boolean increase) {
        String slots = normalizeSlots(vars.Spellslots);
        char[] arr = slots.toCharArray();
        int current = arr[grade - 1] - '0';
        int max = maxSlotsFor(grade, (int) vars.PlayerLevel);
        int updated = increase ? Math.min(9, current + 1) : Math.max(0, current - 1);
        if (increase) updated = Math.min(updated, Math.max(9, max));
        arr[grade - 1] = (char) ('0' + updated);
        String result = new String(arr);
        DebugSetFieldPacket.send(uuid, "Spellslots", result);
        vars.Spellslots = result;
    }

    private String fieldNameForGrade(int grade) {
        return switch (grade) {
            case 0 -> "PreparedCantrips";
            case 1 -> "PreparedSpellsLVL1";
            case 2 -> "PreparedSpellsLVL2";
            case 3 -> "PreparedSpellsLVL3";
            case 4 -> "PreparedSpellsLVL4";
            case 5 -> "PreparedSpellsLVL5";
            case 6 -> "PreparedSpellsLVL6";
            case 7 -> "PreparedSpellsLVL7";
            case 8 -> "PreparedSpellsLVL8";
            case 9 -> "PreparedSpellsLVL9";
            default -> "";
        };
    }

    private void adjustLocalListField(String fieldName, String item, boolean add) {
        try {
            java.lang.reflect.Field field = DndModVariables.PlayerVariables.class.getField(fieldName);
            String current = (String) field.get(vars);
            List<String> entries = new ArrayList<>();
            if (current != null && !current.isBlank() && !current.equals("\"\"")) {
                for (String s : current.split(",")) {
                    String t = s.trim();
                    if (!t.isEmpty()) entries.add(t);
                }
            }
            if (add) { if (!entries.contains(item)) entries.add(item); }
            else entries.remove(item);
            field.set(vars, String.join(",", entries));
        } catch (Exception ignored) {}
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < gridX || mouseX > gridX + gridW || mouseY < gridY || mouseY > gridY + gridH) return false;
        gridScroll = Math.max(0, gridScroll - (int) Math.signum(scrollY));
        return true;
    }

    private static List<List<String>> buildGradeSpellNames() {
        List<List<String>> result = new ArrayList<>();
        result.add(namesOf(Spells.Cantrip.values()));
        result.add(namesOf(Spells.Grade1.values()));
        result.add(namesOf(Spells.Grade2.values()));
        result.add(namesOf(Spells.Grade3.values()));
        result.add(namesOf(Spells.Grade4.values()));
        result.add(namesOf(Spells.Grade5.values()));
        result.add(namesOf(Spells.Grade6.values()));
        result.add(namesOf(Spells.Grade7.values()));
        result.add(namesOf(Spells.Grade8.values()));
        result.add(namesOf(Spells.Grade9.values()));
        return result;
    }

    private static List<String> namesOf(Enum<?>[] values) {
        List<String> names = new ArrayList<>();
        for (Enum<?> e : values) names.add(e.name());
        return names;
    }
}