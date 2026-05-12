package net.luderspieler.dnd.spells;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luderspieler.dnd.classes.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Two-stage spell wheel:
 * Stage 1: outer ring with 10 segments (0=Cantrip, 1-9=Spell Level)
 * Stage 2: inner ring populated from PreparedCantrips / PreparedSpellsLVL1..9
 * segment count = number of prepared spells at chosen level
 * Selecting a spell in stage 2 fires CastSpellProcedure.execute(player, spellId, level)
 */
public class SpellWheelScreen extends Screen {

    // ── Layout ──
    private static final int OUTER_RADIUS = 90;
    private static final int INNER_RADIUS = 40;
    private static final int HUB_RADIUS = 18;
    private static final int LABEL_RADIUS_OUTER = 68;
    private static final int LABEL_RADIUS_INNER = 62;
    private static final float HOVER_EXPAND = 6f;

    // ── Colors ──
    // Hintergrund-Segmente (Grautöne mit Transparenz)
    private static final int COL_SEGMENT_IDLE = -1440546270; // 0xAA222222
    private static final int COL_SEGMENT_HOVER = -869055693; // 0xCC333333
    private static final int COL_SEGMENT_SEL = -297515964;   // 0xEE444444

    // Konturen & Zentrum (Voll deckend / Opaque)
    private static final int COL_OUTLINE = -15658735; // 0xFF111111
    private static final int COL_HUB = -14540254;     // 0xFF222222

    // Texte & Highlights
    private static final int COL_TEXT = -1;           // 0xFFFFFFFF (Reinweiß)
    private static final int COL_TEXT_HOVER = -171;    // 0xFFFFFF55 (MC-Gelb)
    private static final int COL_TEXT_DIM = -5592406;  // 0xFFAAAAAA (Standard-Grau)

    // Cantrips (Gold-Kontrast)
    private static final int COL_CANTRIP = -1426093568;       // 0xAAFFAA00
    private static final int COL_CANTRIP_HOVER = -855610317;  // 0xCCFFCC33

    // ── State ──
    private enum Stage { LEVEL_SELECT, SPELL_SELECT }
    private Stage stage = Stage.LEVEL_SELECT;

    private int selectedLevel = -1; // 0-9
    private int hoveredSegment = -1;

    private List<String> currentSpells = new ArrayList<>(); // parsed spell ids
    private int hoveredSpell = -1;
    private int cx, cy; // screen center

    public SpellWheelScreen() {
        super(Component.empty());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        cx = this.width / 2;
        cy = this.height / 2;
    }

    // ══════════════════════════════════════════════════════
    // RENDER
    // ══════════════════════════════════════════════════════

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // dim background slightly
        g.fill(0, 0, this.width, this.height, 1426063360);

        if (stage == Stage.LEVEL_SELECT) {
            renderLevelWheel(g, mouseX, mouseY);
        } else {
            renderSpellWheel(g, mouseX, mouseY);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    // ── STAGE 1: Level selector ──────────────────────────

    private void renderLevelWheel(GuiGraphics g, int mouseX, int mouseY) {
        int segments = 10;
        float scale = 1.5f; // Dein gewünschter Scale-Faktor

        // Skalierte Radien berechnen
        int currentOuterRadius = (int) (OUTER_RADIUS * scale);
        int currentHubRadius = (int) (HUB_RADIUS * scale);
        int currentLabelRadius = (int) (LABEL_RADIUS_OUTER * scale);

        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double sliceAngle = (2 * Math.PI) / segments;

        hoveredSegment = -1;
        double dist = Math.sqrt((mouseX - cx) * (mouseX - cx) + (mouseY - cy) * (mouseY - cy));

        // Maus-Abfrage an neuen Radius anpassen
        if (dist > currentHubRadius && dist < currentOuterRadius + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSegment = (int) (angle / sliceAngle) % segments;
        }

        for (int i = 0; i < segments; i++) {
            double start = -Math.PI / 2 + i * sliceAngle;
            double end = start + sliceAngle;
            boolean hovered = i == hoveredSegment;

            // Radius bei Hover leicht vergrößern
            int outerR = hovered ? currentOuterRadius + (int) HOVER_EXPAND : currentOuterRadius;

            int color = (i == 0) ? (hovered ? COL_CANTRIP_HOVER : COL_CANTRIP) : (hovered ? COL_SEGMENT_HOVER : COL_SEGMENT_IDLE);

            // Zeichnen mit skalierten Werten
            drawSegment(g, cx, cy, currentHubRadius, outerR, start, end, color, COL_OUTLINE);

            double mid = (start + end) / 2;
            int lx = cx + (int) (currentLabelRadius * Math.cos(mid));
            int ly = cy + (int) (currentLabelRadius * Math.sin(mid));

            String slotInfo = getSlotInfo(i);
            String label = (i == 0) ? "Cantrip" : "Grade " + i + slotInfo;

            boolean hasContent = hasSpellsAtLevel(i);
            int textColor = hasContent ? (hovered ? COL_TEXT_HOVER : COL_TEXT) : COL_TEXT_DIM;

            drawCenteredShadow(g, label, lx, ly, textColor);
        }

        // Zentraler Hub ebenfalls skaliert
        drawCircle(g, cx, cy, currentHubRadius, COL_HUB, COL_OUTLINE);
        drawCenteredShadow(g, "Spells", cx, cy - 4, COL_TEXT);
    }
    // ── STAGE 2: Spell selector ──────────────────────────

    private void renderSpellWheel(GuiGraphics g, int mouseX, int mouseY) {
        int segments = currentSpells.isEmpty() ? 1 : currentSpells.size();

        float scale = (segments >= 12) ? 3.0f : (segments >= 6 ? 2.0f : 1.0f);
        int currentOuterRadius = (int) (OUTER_RADIUS * scale);
        int currentHubRadius = (int) (HUB_RADIUS * scale);
        int currentLabelRadius = (int) (LABEL_RADIUS_INNER * scale);

        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double sliceAngle = (2 * Math.PI) / segments;

        hoveredSpell = -1;
        double dist = Math.sqrt((mouseX - cx) * (mouseX - cx) + (mouseY - cy) * (mouseY - cy));
        if (!currentSpells.isEmpty() && dist > currentHubRadius && dist < currentOuterRadius + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSpell = (int) (angle / sliceAngle) % segments;
        }

        if (currentSpells.isEmpty()) {
            drawSegment(g, cx, cy, currentHubRadius, currentOuterRadius, -Math.PI / 2, Math.PI * 1.5, COL_SEGMENT_IDLE, COL_OUTLINE);
            drawCenteredShadow(g, "No spells prepared", cx, cy + 16, COL_TEXT_DIM);
        } else {
            for (int i = 0; i < segments; i++) {
                double start = -Math.PI / 2 + i * sliceAngle;
                double end = start + sliceAngle;
                boolean hovered = i == hoveredSpell;
                int outerR = hovered ? currentOuterRadius + (int) HOVER_EXPAND : currentOuterRadius;
                int color = hovered ? COL_SEGMENT_HOVER : COL_SEGMENT_IDLE;
                drawSegment(g, cx, cy, currentHubRadius, outerR, start, end, color, COL_OUTLINE);

                double mid = (start + end) / 2;
                int lx = cx + (int) (currentLabelRadius * Math.cos(mid));
                int ly = cy + (int) (currentLabelRadius * Math.sin(mid));
                drawCenteredShadow(g, formatSpellId(currentSpells.get(i)), lx, ly, hovered ? COL_TEXT_HOVER : COL_TEXT);
            }
        }

        drawCircle(g, cx, cy, currentHubRadius, COL_HUB, COL_OUTLINE);

        // Label im Hub mit Slots
        String levelLabel = (selectedLevel == 0) ? "Cantrip" : "Grade " + selectedLevel + getSlotInfo(selectedLevel);
        drawCenteredShadow(g, levelLabel, cx, cy - 8
                , COL_TEXT);
        drawCenteredShadow(g, "Back", cx, cy + 8, -1);
    }

    // ══════════════════════════════════════════════════════
    // INPUT
    // ══════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (button == 0) { // left click
            if (stage == Stage.LEVEL_SELECT) {
                if (dist <= HUB_RADIUS) {
                    // hub click — close
                    this.onClose();
                    return true;
                }
                if (hoveredSegment >= 0 && hoveredSegment <= 9) {
                    loadSpellsForLevel(hoveredSegment);
                    stage = Stage.SPELL_SELECT;
                    return true;
                }
            } else {
                if (dist <= HUB_RADIUS) {
                    // back to level wheel
                    stage = Stage.LEVEL_SELECT;
                    hoveredSegment = -1;
                    return true;
                }
                if (hoveredSpell >= 0 && hoveredSpell < currentSpells.size()) {
                    castSpell(currentSpells.get(hoveredSpell), selectedLevel);
                    return true;
                }
            }
        }

        if (button == 1) { // right click = back / close
            if (stage == Stage.SPELL_SELECT) {
                stage = Stage.LEVEL_SELECT;
            } else {
                this.onClose();
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        // ESC or the hotkey closes
        if (key == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    // ══════════════════════════════════════════════════════
    // DATA HELPERS
    // ══════════════════════════════════════════════════════

    private void loadSpellsForLevel(int level) {
        selectedLevel = level;
        currentSpells.clear();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String raw = level == 0
                ? vars.PreparedCantrips
                : getPreparedSpellsForLevel(vars, level);

        if (raw == null || raw.isBlank()) return;

        for (String s : raw.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) currentSpells.add(t);
        }
    }

    /** Read PreparedSpellsLVL1 through PreparedSpellsLVL9 */
    private String getPreparedSpellsForLevel(DndModVariables.PlayerVariables vars, int level) {
        return switch (level) {
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
    }

    private boolean hasSpellsAtLevel(int level) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String raw = level == 0 ? vars.PreparedCantrips : getPreparedSpellsForLevel(vars, level);
        return raw != null && !raw.isBlank();
    }

    private void castSpell(String spellId, int level) {
        // Wir müssen die Namen benutzen, die oben in (String spellId, int level) stehen!
        CastSpellPacket.send(spellId, level);
        this.onClose();
    }

    /** "fire_bolt" → "Fire Bolt" */
    private String formatSpellId(String id) {
        if (id == null || id.isBlank()) return "";
        String[] parts = id.trim().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════
    // DRAWING PRIMITIVES
    // ══════════════════════════════════════════════════════

    /**
     * Draws a pie-slice segment between innerR and outerR.
     * Uses triangle-fan approximation with N steps.
     */
    private void drawSegment(GuiGraphics g, int ox, int oy,
                             int innerR, int outerR,
                             double startAngle, double endAngle,
                             int fillColor, int outlineColor) {
        int steps = 32;
        double range = endAngle - startAngle;

        int a = (fillColor >> 24) & 0xFF;
        int r = (fillColor >> 16) & 0xFF;
        int gr = (fillColor >> 8) & 0xFF;
        int b = fillColor & 0xFF;

        // Wir bleiben bei debugQuads, da dies bei dir erkannt wird
        VertexConsumer buffer = net.minecraft.client.Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.debugQuads());

        org.joml.Matrix3x2f matrix = g.pose();

        for (int i = 0; i < steps; i++) {
            double a1 = startAngle + range * i / steps;
            double a2 = startAngle + range * (i + 1) / steps;

            float ix1 = ox + (float) (innerR * Math.cos(a1));
            float iy1 = oy + (float) (innerR * Math.sin(a1));
            float ox1 = ox + (float) (outerR * Math.cos(a1));
            float oy1 = oy + (float) (outerR * Math.sin(a1));
            float ix2 = ox + (float) (innerR * Math.cos(a2));
            float iy2 = oy + (float) (innerR * Math.sin(a2));
            float ox2 = ox + (float) (outerR * Math.cos(a2));
            float oy2 = oy + (float) (outerR * Math.sin(a2));

            addRawVertex(buffer, matrix, ix1, iy1, r, gr, b, a);
            addRawVertex(buffer, matrix, ox1, oy1, r, gr, b, a);
            addRawVertex(buffer, matrix, ox2, oy2, r, gr, b, a);
            addRawVertex(buffer, matrix, ix2, iy2, r, gr, b, a);
        }

        // Das hier ist der eigentliche "Hintergrund-Bringer":
        net.minecraft.client.Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }

    // Angepasste Hilfsmethode für Matrix3x2f (1.21 Standard)
    private void addRawVertex(VertexConsumer buffer, org.joml.Matrix3x2f matrix, float x, float y, int r, int g, int b, int a) {
        float tx = matrix.m00() * x + matrix.m10() * y + matrix.m20();
        float ty = matrix.m01() * x + matrix.m11() * y + matrix.m21();
        buffer.addVertex(tx, ty, 0.0f).setColor(r, g, b, a);
    }

    private void drawArc(GuiGraphics g, int ox, int oy, int r,
                         double startAngle, double endAngle, int color) {
        int steps = 32;
        double range = endAngle - startAngle;
        for (int i = 0; i < steps; i++) {
            double a1 = startAngle + range * i / steps;
            double a2 = startAngle + range * (i + 1) / steps;
            drawLine(g,
                    ox + (int) (r * Math.cos(a1)), oy + (int) (r * Math.sin(a1)),
                    ox + (int) (r * Math.cos(a2)), oy + (int) (r * Math.sin(a2)),
                    color);
        }
    }

    private void drawLine(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        if (x1 == x2) {
            g.fill(x1, Math.min(y1, y2), x1 + 1, Math.max(y1, y2) + 1, color);
        } else if (y1 == y2) {
            g.fill(Math.min(x1, x2), y1, Math.max(x1, x2) + 1, y1 + 1, color);
        } else {
            // Bresenham
            int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
            int dy = -Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
            int err = dx + dy;
            int x = x1, y = y1;
            while (true) {
                g.fill(x, y, x + 1, y + 1, color);
                if (x == x2 && y == y2) break;
                int e2 = 2 * err;
                if (e2 >= dy) {
                    err += dy;
                    x += sx;
                }
                if (e2 <= dx) {
                    err += dx;
                    y += sy;
                }
            }
        }
    }

    private void drawCircle(GuiGraphics g, int ox, int oy, int r, int fillColor, int outlineColor) {
        drawSegment(g, ox, oy, 0, r, -Math.PI / 2, Math.PI * 1.5, fillColor, outlineColor);
    }

    private void drawCenteredShadow(GuiGraphics g, String text, int x, int y, int color) {
        int w = this.font.width(text);
        int tx = x - w / 2;
        int ty = y - this.font.lineHeight / 2;
        // shadow
        g.drawString(this.font, text, tx + 1, ty + 1, 0xFF000000, false);
        g.drawString(this.font, text, tx, ty, color, false);
    }

    private String getSlotInfo(int grade) {
        if (grade <= 0) return "";

        Player player = Minecraft.getInstance().player;
        if (player == null) return "";

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        net.luderspieler.dnd.classes.ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass.replace("\"", ""));

        if (classDef == null || classDef.getSpellSlots() == null) return "";

        int[][] allSlots = classDef.getSpellSlots();
        int levelIdx = (int) vars.PlayerLevel - 1;
        if (levelIdx < 0) levelIdx = 0;

        int maxSlots = 0;
        // Falls dein Array Grade 1 an Index 1 hat, ist 'grade' richtig.
        // Falls Grade 1 an Index 0 liegt, müsstest du hier 'grade - 1' nutzen.
        if (levelIdx < allSlots.length && grade < allSlots[levelIdx].length) {
            maxSlots = allSlots[levelIdx][grade];
        }

        // Wenn keine Slots möglich sind (Max = 0), zeigen wir gar nichts an
        if (maxSlots <= 0) return "";

        int currentSlots = 0;
        String rawSlots = vars.Spellslots.replace("\"", "");
        if (rawSlots.length() >= grade) {
            currentSlots = Character.getNumericValue(rawSlots.charAt(grade - 1));
        }

        return " " + currentSlots + "/" + maxSlots;
    }
}