package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Spell Preparation GUI
 *
 * Layout:
 *   Left panel  — all spells available to the player's class, grouped by grade tab
 *   Right panel — currently prepared spells (from PreparedCantrips / PreparedSpellsLVL1..9)
 *   Clicking a spell on the left adds it to prepared (if canPrepareMore)
 *   Clicking a spell on the right removes it from prepared
 *   "Confirm" button sends PrepareSpellsPacket to server
 */
public class SpellPrepScreen extends Screen {

    // ── Layout ──────────────────────────────────────────────────
    private static final int PANEL_W     = 160;
    private static final int PANEL_H     = 200;
    private static final int ROW_H       = 13;
    private static final int TAB_H       = 14;
    private static final int TAB_W       = 28;
    private static final int SCROLL_STEP = ROW_H;

    // ── Colors ──────────────────────────────────────────────────
    private static final int COL_BG          = 0xEE0D1B2A;
    private static final int COL_PANEL       = 0xCC1C2541;
    private static final int COL_PANEL_EDGE  = 0xFF53D8FB;
    private static final int COL_ROW_HOVER   = 0x882E4A8C;
    private static final int COL_ROW_FULL    = 0x44FF2222;  // can't add more
    private static final int COL_TAB_ACTIVE  = 0xFF2E4A8C;
    private static final int COL_TAB_IDLE    = 0xFF1C2541;
    private static final int COL_TEXT        = 0xFFFFFFFF;
    private static final int COL_TEXT_DIM    = 0xFF888888;
    private static final int COL_TEXT_HOV    = 0xFF53D8FB;
    private static final int COL_REMOVE      = 0xFFFF6666;
    private static final int COL_CANTRIP_TAB = 0xFFA06010;
    private static final int COL_TEXT_WIP = 0xFFFFA500; // Orange für Work in Progress


    private final Screen parent;


    // ── State ────────────────────────────────────────────────────
    private int selectedGrade = 0;       // 0 = Cantrips, 1-9 = spell grades
    private int scrollLeft    = 0;
    private int scrollRight   = 0;
    private int hoveredLeft   = -1;
    private int hoveredRight  = -1;

    /** All spells of each grade available to this class  (grade 0..9 → list of spell IDs) */
    private final List<List<String>> availableByGrade = new ArrayList<>();

    /** Currently prepared spells — mutable local copy, synced on Confirm */
    private final List<String>[] preparedByGrade;

    private int lx, ly, rx, ry; // panel top-left coords

    @SuppressWarnings("unchecked")
    public SpellPrepScreen(Screen parent) {
        super(Component.literal("Prepare Spells"));
        this.parent = parent; // Das hier ist wichtig!

        preparedByGrade = new ArrayList[10];
        for (int i = 0; i < 10; i++) preparedByGrade[i] = new ArrayList<>();
        for (int i = 0; i < 10; i++) availableByGrade.add(new ArrayList<>());
    }

    // ════════════════════════════════════════════════════════════
    //  INIT
    // ════════════════════════════════════════════════════════════

    @Override
    protected void init() {
        super.init();

        // Panel positions
        int totalW = PANEL_W * 2 + 20;
        lx = (this.width - totalW) / 2;
        ly = (this.height - PANEL_H - TAB_H - 30) / 2 + TAB_H;
        rx = lx + PANEL_W + 20;
        ry = ly;

        loadData();

        // ── Grade tabs (0-9) ──
        for (int g = 0; g < 10; g++) {
            final int grade = g;
            int tx = lx + g * TAB_W;
            int ty = ly - TAB_H;
            this.addRenderableWidget(Button.builder(
                    Component.literal(g == 0 ? "C" : String.valueOf(g)),
                    btn -> { selectedGrade = grade; scrollLeft = 0; scrollRight = 0; }
            ).bounds(tx, ty, TAB_W - 1, TAB_H).build());
        }

        // ── Confirm button ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Confirm"),
                btn -> saveAndClose()
        ).bounds(lx + PANEL_W / 2 - 30, ly + PANEL_H + 16, 60, 16).build());

        // ── Close button ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Cancel"),
                btn -> this.onClose()
        ).bounds(rx + PANEL_W / 2 - 30, ly + PANEL_H + 16, 60, 16).build());
    }

    private void loadData() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Load available spells from ClassRegistry
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        if (cls != null) {
            // ClassDefinition exposes getSpellsForGrade(int grade) returning List<String>
            // If your ClassDefinition uses a different method name, adjust here
            for (int g = 0; g < 10; g++) {
                List<String> spells = cls.getSpellsForGrade(g);
                if (spells != null) availableByGrade.get(g).addAll(spells);
            }
        }

        // Load current prepared spells into local copy
        preparedByGrade[0].addAll(parseCSV(vars.PreparedCantrips));
        preparedByGrade[1].addAll(parseCSV(vars.PreparedSpellsLVL1));
        preparedByGrade[2].addAll(parseCSV(vars.PreparedSpellsLVL2));
        preparedByGrade[3].addAll(parseCSV(vars.PreparedSpellsLVL3));
        preparedByGrade[4].addAll(parseCSV(vars.PreparedSpellsLVL4));
        preparedByGrade[5].addAll(parseCSV(vars.PreparedSpellsLVL5));
        preparedByGrade[6].addAll(parseCSV(vars.PreparedSpellsLVL6));
        preparedByGrade[7].addAll(parseCSV(vars.PreparedSpellsLVL7));
        preparedByGrade[8].addAll(parseCSV(vars.PreparedSpellsLVL8));
        preparedByGrade[9].addAll(parseCSV(vars.PreparedSpellsLVL9));
    }

    // ════════════════════════════════════════════════════════════
    //  RENDER
    // ════════════════════════════════════════════════════════════

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Hintergrund (Nutzt Overlay-Farbe statt COL_BG)
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

        // Panels zeichnen (Nutzen intern generalConfigs)
        drawPanel(g, lx, ly, PANEL_W, PANEL_H, "Available Spells");
        drawPanel(g, rx, ry, PANEL_W, PANEL_H, "Prepared Spells");

        List<String> available = availableByGrade.get(selectedGrade);
        List<String> prepared  = preparedByGrade[selectedGrade];

        // ── Left panel rows (Available) ──
        hoveredLeft = -1;
        g.enableScissor(lx + 1, ly + 10, lx + PANEL_W - 1, ly + PANEL_H - 1);
        int leftRows = (PANEL_H - 12) / ROW_H;
        for (int i = 0; i < leftRows; i++) {
            int idx = i + scrollLeft;
            if (idx >= available.size()) break;

            String spell = available.get(idx);
            boolean inPrep = prepared.contains(spell);
            boolean isFinished = SpellCasters.FINISHED_SPELLS.contains(spell);
            boolean canAdd = !inPrep && canPrepareMore(selectedGrade, prepared.size());

            int rowY = ly + 10 + i * ROW_H;
            boolean hov = mouseX >= lx + 2 && mouseX < lx + PANEL_W - 2
                    && mouseY >= rowY && mouseY < rowY + ROW_H;
            if (hov) hoveredLeft = idx;

            // Zeilen-Hintergrund Logik
            int rowBg = inPrep ? generalConfigs.COLOR_ROW_PREPARED :
                    (hov && canAdd ? generalConfigs.COLOR_HOVER_BG :
                            (!canAdd && !inPrep ? generalConfigs.COLOR_ROW_FULL : 0));

            if (rowBg != 0) g.fill(lx + 2, rowY, lx + PANEL_W - 2, rowY + ROW_H, rowBg);

            // Text Farblogik
            int textCol;
            if (inPrep) {
                textCol = generalConfigs.TEXT_DARK_GRAY; // Ersetzt COL_TEXT_DIM
            } else if (!isFinished) {
                textCol = generalConfigs.COLOR_STATUS_WIP; // Das Orange
            } else {
                textCol = hov ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            }

            g.drawString(this.font, formatId(spell), lx + 5, rowY + 2, textCol, false);

            if (inPrep) {
                g.drawString(this.font, "✓", lx + PANEL_W - 12, rowY + 2, generalConfigs.COLOR_STATUS_SUCCESS, false);
            }
        }
        g.disableScissor();

        // ── Right panel rows (Prepared) ──
        hoveredRight = -1;
        g.enableScissor(rx + 1, ry + 10, rx + PANEL_W - 1, ry + PANEL_H - 1);
        int rightRows = (PANEL_H - 12) / ROW_H;
        for (int i = 0; i < rightRows; i++) {
            int idx = i + scrollRight;
            if (idx >= prepared.size()) break;

            String spell = prepared.get(idx);
            boolean isFinished = SpellCasters.FINISHED_SPELLS.contains(spell);

            int rowY = ry + 10 + i * ROW_H;
            boolean hov = mouseX >= rx + 2 && mouseX < rx + PANEL_W - 2
                    && mouseY >= rowY && mouseY < rowY + ROW_H;
            if (hov) hoveredRight = idx;

            if (hov) {
                g.fill(rx + 2, rowY, rx + PANEL_W - 2, rowY + ROW_H, generalConfigs.COLOR_ROW_DANGER);
            }

            // Text Farbe rechts
            int textColR = isFinished ? generalConfigs.TEXT_WHITE : generalConfigs.COLOR_STATUS_WIP;

            g.drawString(this.font, formatId(spell), rx + 5, rowY + 2, textColR, false);

            if (hov) {
                g.drawString(this.font, "✗", rx + PANEL_W - 12, rowY + 2, generalConfigs.COLOR_STATUS_DANGER, false);
            }
        }
        g.disableScissor();

        // ── Slot counter ──
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);
            var cls = net.luderspieler.dnd.character.registrys.ClassRegistry.getClass(vars.PlayerClass);
            if (cls != null) {
                int max  = getMaxPrepared(cls, vars, selectedGrade);
                int used = prepared.size();
                String counter = used + " / " + max + " prepared";

                int counterCol = used >= max ? generalConfigs.COLOR_STATUS_DANGER : generalConfigs.COLOR_STATUS_SUCCESS;

                g.drawString(this.font, counter,
                        rx + PANEL_W / 2 - this.font.width(counter) / 2,
                        ry + PANEL_H + 2, counterCol, false);
            }
        }

        // ── Grade indicator ──
        String gradeLabel = selectedGrade == 0 ? "Cantrips" : "Grade " + selectedGrade + " Spells";
        g.drawString(this.font, gradeLabel,
                lx + PANEL_W / 2 - this.font.width(gradeLabel) / 2,
                ly - 10 - 20, // TAB_H manuell eingerechnet falls variabel
                generalConfigs.COLOR_ACCENT_GOLD, false);

        super.render(g, mouseX, mouseY, partial);
    }

    private void drawPanel(GuiGraphics g, int x, int y, int w, int h, String title) {
        g.fill(x, y, x + w, y + h, generalConfigs.COLOR_PANEL_BG);

        generalConfigs.renderGreenEdge(g, x, y, w, h);

        int tw = this.font.width(title);
        g.drawString(this.font, title, x + w / 2 - tw / 2, y + 2, generalConfigs.COLOR_ACCENT_GOLD, false);

        g.fill(x + 1, y + 9, x + w - 1, y + 10, generalConfigs.COLOR_PANEL_EDGE);
    }

    // ════════════════════════════════════════════════════════════
    //  INPUT
    // ════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0) {
            List<String> available = availableByGrade.get(selectedGrade);
            List<String> prepared  = preparedByGrade[selectedGrade];

            // Click on left panel — add spell
            if (hoveredLeft >= 0 && hoveredLeft < available.size()) {
                String spell = available.get(hoveredLeft);
                if (!prepared.contains(spell) && canPrepareMore(selectedGrade, prepared.size())) {
                    prepared.add(spell);
                }
                return true;
            }

            // Click on right panel — remove spell
            if (hoveredRight >= 0 && hoveredRight < prepared.size()) {
                prepared.remove(hoveredRight);
                if (scrollRight > 0 && scrollRight >= prepared.size()) scrollRight--;
                return true;
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        int delta = scrollY > 0 ? -1 : 1;
        boolean overLeft  = mx >= lx && mx < lx + PANEL_W && my >= ly && my < ly + PANEL_H;
        boolean overRight = mx >= rx && mx < rx + PANEL_W && my >= ry && my < ry + PANEL_H;

        if (overLeft) {
            scrollLeft = Math.max(0, Math.min(scrollLeft + delta,
                    Math.max(0, availableByGrade.get(selectedGrade).size() - visibleRows())));
        } else if (overRight) {
            scrollRight = Math.max(0, Math.min(scrollRight + delta,
                    Math.max(0, preparedByGrade[selectedGrade].size() - visibleRows())));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { this.onClose(); return true; }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void onClose() {
        // Wenn ein Parent existiert, dahin zurück; sonst ganz schließen
        if (this.minecraft != null) {
            if (this.parent != null) {
                this.minecraft.setScreen(this.parent);
            } else {
                // Dies nutzt die Screen-Standard-Schließen-Logik
                this.minecraft.setScreen(null);
            }
        }
    }

    // ════════════════════════════════════════════════════════════
    //  SAVE / CLOSE
    // ════════════════════════════════════════════════════════════

    private void saveAndClose() {
        String[] csvs = new String[10];
        for (int i = 0; i < 10; i++) csvs[i] = toCSV(preparedByGrade[i]);

        // Sendet die Daten an den Server
        PrepareSpellsPacket.send(new PrepareSpellsPacket(
                csvs[0], csvs[1], csvs[2], csvs[3], csvs[4],
                csvs[5], csvs[6], csvs[7], csvs[8], csvs[9]
       ));

        // Kehrt zum LongRestScreen zurück
        if (this.minecraft != null && this.parent != null) {
            this.minecraft.setScreen(this.parent);
        } else {
            this.onClose();
        }
    }

    // ════════════════════════════════════════════════════════════
    //  LOGIC HELPERS
    // ════════════════════════════════════════════════════════════

    /**
     * Returns true if the player can prepare one more spell of this grade.
     * Uses the preparedAmount array from ClassDefinition.
     *
     * If your ClassDefinition exposes a different method, replace the body here.
     */
    private boolean canPrepareMore(int grade, int currentCount) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        if (cls == null) return false;
        int max = getMaxPrepared(cls, vars, grade);
        return currentCount < max;
    }

    /**
     * Returns the max prepared spells of a given grade for the player's current level.
     * Reads from ClassDefinition.preparedAmount[level][grade].
     * Adjust the method call if your ClassDefinition uses a different getter.
     */
    private int getMaxPrepared(ClassDefinition cls, DndModVariables.PlayerVariables vars, int grade) {
        int level = (int) Math.max(1, Math.min(20, vars.PlayerLevel)); // 0-indexed
        int[][] pa = cls.getPreparedAmount(); // int[20][10]
        if (pa == null || level >= pa.length || grade >= pa[level].length) return 0;
        return pa[level][grade];
    }

    private int visibleRows() { return (PANEL_H - 12) / ROW_H; }

    // ════════════════════════════════════════════════════════════
    //  STRING UTILITIES
    // ════════════════════════════════════════════════════════════

    private List<String> parseCSV(String csv) {
        List<String> list = new ArrayList<>();
        if (csv == null || csv.isBlank()) return list;
        for (String s : csv.split(",")) { String t = s.trim(); if (!t.isEmpty()) list.add(t); }
        return list;
    }

    private String toCSV(List<String> list) {
        return String.join(",", list);
    }

    /** "FIRE_BOLT" → "Fire Bolt" */
    private String formatId(String id) {
        if (id == null || id.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_"))
            if (!part.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1).toLowerCase());
            }
        return sb.toString();
    }
}