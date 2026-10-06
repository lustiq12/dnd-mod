package net.luderspieler.dnd.spells;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.definition.ClassDefinition;
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
    private static final int OUTER_RADIUS = 105;
    private static final int INNER_RADIUS = 40;
    private static final int HUB_RADIUS = 35;
    private static final int LABEL_RADIUS_OUTER = 68;
    private static final int LABEL_RADIUS_INNER = 62;
    private static final float HOVER_EXPAND = 6f;
    private static final int OUTLINE_WIDTH = 2;

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
        // dim background slightly (Nutzt jetzt Config)
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

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
        float scale = 1.5f;

        int currentOuterRadius = (int) (OUTER_RADIUS * scale);
        int currentHubRadius = HUB_RADIUS;

        int currentLabelRadius = currentHubRadius + (currentOuterRadius - currentHubRadius) / 2;

        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double sliceAngle = (2 * Math.PI) / segments;

        hoveredSegment = -1;
        double dist = Math.sqrt((mouseX - cx) * (mouseX - cx) + (mouseY - cy) * (mouseY - cy));

        if (dist > currentHubRadius && dist < currentOuterRadius + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSegment = (int) (angle / sliceAngle) % segments;
        }

        for (int i = 0; i < segments; i++) {
            double start = -Math.PI / 2 + i * sliceAngle;
            double end = start + sliceAngle;
            boolean hovered = i == hoveredSegment;

            int outerR = hovered ? currentOuterRadius + (int) HOVER_EXPAND : currentOuterRadius;

            int color = (i == 0) ? (hovered ? generalConfigs.WHEEL_CANTRIP_HOVER : generalConfigs.WHEEL_CANTRIP)
                    : (hovered ? generalConfigs.WHEEL_SEGMENT_HOVER : generalConfigs.WHEEL_SEGMENT_IDLE);

            drawSegment(g, cx, cy, currentHubRadius, outerR, start, end, color, generalConfigs.WHEEL_OUTLINE, hovered);

            double mid = (start + end) / 2;
            int lx = cx + (int) (currentLabelRadius * Math.cos(mid));
            int ly = cy + (int) (currentLabelRadius * Math.sin(mid));

            String slotInfo = getSlotInfo(i);
            String label = (i == 0) ? "Cantrip" : "Grade " + i + slotInfo;

            boolean hasContent = hasSpellsAtLevel(i);
            boolean depleted = i > 0 && isGradeDepleted(i);
            int textColor;
            if (depleted) {
                textColor = generalConfigs.TEXT_SLOT_DEPLETED;
            } else if (hasContent) {
                textColor = hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            } else {
                textColor = generalConfigs.TEXT_DARK_GRAY;
            }

            drawCenteredShadow(g, label, lx, ly, textColor);
        }

        drawCircle(g, cx, cy, currentHubRadius, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);
        drawCenteredShadow(g, "Spells", cx, cy - 4, generalConfigs.TEXT_WHITE);
    }

    // ── STAGE 2: Spell selector ──────────────────────────

    private void renderSpellWheel(GuiGraphics g, int mouseX, int mouseY) {
        int segments = currentSpells.isEmpty() ? 1 : currentSpells.size();

        float scale = (segments >= 12) ? 3.0f : (segments >= 6 ? 2.0f : 1.0f);

        int currentOuterRadius = (int) (OUTER_RADIUS * scale);
        int currentHubRadius = HUB_RADIUS;

        int currentLabelRadius = currentHubRadius + (currentOuterRadius - currentHubRadius) / 2;

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
            drawSegment(g, cx, cy, currentHubRadius, currentOuterRadius, -Math.PI / 2, Math.PI * 1.5, generalConfigs.WHEEL_SEGMENT_IDLE, generalConfigs.WHEEL_OUTLINE, false);
            drawCenteredShadow(g, "No spells prepared", cx, cy - 50, generalConfigs.TEXT_GRAY);
        } else {
            for (int i = 0; i < segments; i++) {
                double start = -Math.PI / 2 + i * sliceAngle;
                double end = start + sliceAngle;
                boolean hovered = i == hoveredSpell;
                int outerR = hovered ? currentOuterRadius + (int) HOVER_EXPAND : currentOuterRadius;
                int color = hovered ? generalConfigs.WHEEL_SEGMENT_HOVER : generalConfigs.WHEEL_SEGMENT_IDLE;
                drawSegment(g, cx, cy, currentHubRadius, outerR, start, end, color, generalConfigs.WHEEL_OUTLINE, hovered);

                double mid = (start + end) / 2;
                int lx = cx + (int) (currentLabelRadius * Math.cos(mid));
                int ly = cy + (int) (currentLabelRadius * Math.sin(mid));
                drawCenteredShadow(g, formatSpellId(currentSpells.get(i)), lx, ly, hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE);

                String spellId = currentSpells.get(i);
                SpellChoices.Choice choice = SpellChoices.get(spellId);
                if (choice != null) {
                    drawCenteredShadow(g, choice.label() + ": " + SpellChoices.getSelectedDisplay(spellId), lx, ly + 10,
                            hovered ? generalConfigs.TEXT_HOVER : generalConfigs.COLOR_ACCENT_GOLD);
                }
            }
        }

        drawCircle(g, cx, cy, currentHubRadius, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);

        String levelLabel = (selectedLevel == 0) ? "Cantrip" : "Grade " + selectedLevel + getSlotInfo(selectedLevel);
        drawCenteredShadow(g, levelLabel, cx, cy - 8, generalConfigs.TEXT_WHITE);
        drawCenteredShadow(g, "Back", cx, cy + 8, generalConfigs.TEXT_WHITE);
    }

    // ══════════════════════════════════════════════════════
    // INPUT
    // ══════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (button == 0) {
            if (stage == Stage.LEVEL_SELECT) {
                if (dist <= HUB_RADIUS) {
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

        if (button == 1) {
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
        if (key == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (stage == Stage.SPELL_SELECT && scrollY != 0
                && hoveredSpell >= 0 && hoveredSpell < currentSpells.size()) {
            String spellId = currentSpells.get(hoveredSpell);
            if (SpellChoices.has(spellId)) {
                SpellChoices.cycle(spellId, scrollY < 0);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
        CastSpellPacket.send(spellId, level, SpellChoices.getSelectedId(spellId));
        this.onClose();
    }

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

    private void drawCircle(GuiGraphics g, int ox, int oy, int radius, int fillColor, int outlineColor) {
        int hubColor = generalConfigs.WHEEL_SEGMENT_IDLE;
        // Ein einziger sauberer Pass ohne Überlappungen
        fillSector(g, ox, oy, 0, radius, 0, Math.PI * 2, hubColor, 0);
    }

    private void drawSegment(GuiGraphics g, int ox, int oy,
                             int innerR, int outerR,
                             double startAngle, double endAngle,
                             int fillColor, int outlineColor, boolean hovered) {

        int rOut = hovered ? outerR + (int) HOVER_EXPAND : outerR;
        // OUTLINE_WIDTH übergibt die Breite des Spalts in Pixeln (2)
        fillSector(g, ox, oy, innerR + 2, rOut, startAngle, endAngle, fillColor, OUTLINE_WIDTH);
    }

    private void fillSector(GuiGraphics g, int ox, int oy, int innerR, int outerR,
                            double startAngle, double endAngle, int color, int gapPx) {
        if (outerR <= innerR) return;

        double start = startAngle;
        double end = endAngle;
        while (end < start) end += Math.PI * 2;

        boolean fullCircle = end - start >= Math.PI * 2 - 1.0e-6;

        // Vorberechnungen für den exakten Pixel-Abstand zu den Linien
        double sinStart = Math.sin(start);
        double cosStart = Math.cos(start);
        double sinEnd = Math.sin(end);
        double cosEnd = Math.cos(end);

        // Die Hälfte des Spaltes (z.B. 1px bei einem gewünschten Spalt von 2px),
        // da benachbarte Stücke ebenfalls um 1px zurückweichen.
        double halfGap = gapPx / 2.0;

        int left = ox - outerR - 1;
        int right = ox + outerR + 1;
        int top = oy - outerR - 1;
        int bottom = oy + outerR + 1;

        for (int y = top; y <= bottom; y++) {
            double dy = y + 0.5 - oy;
            int spanStart = Integer.MIN_VALUE;

            for (int x = left; x <= right; x++) {
                double dx = x + 0.5 - ox;
                double distSq = dx * dx + dy * dy;

                boolean inside = distSq >= (double) innerR * innerR && distSq <= (double) outerR * outerR;

                if (inside && !fullCircle) {
                    double ang = Math.atan2(dy, dx);
                    while (ang < start) ang += Math.PI * 2;

                    if (ang > end) {
                        inside = false;
                    } else if (halfGap > 0) {
                        // Berechnet die echte, senkrechte Distanz in Pixeln zur Start- und End-Trennlinie
                        double distToStart = Math.abs(dx * sinStart - dy * cosStart);
                        double distToEnd = Math.abs(dx * sinEnd - dy * cosEnd);

                        // Wenn der Pixel zu nah an einer der beiden Trennlinien liegt -> ausblenden
                        if (distToStart < halfGap || distToEnd < halfGap) {
                            inside = false;
                        }
                    }
                }

                if (inside) {
                    if (spanStart == Integer.MIN_VALUE) spanStart = x;
                } else if (spanStart != Integer.MIN_VALUE) {
                    g.fill(spanStart, y, x, y + 1, color);
                    spanStart = Integer.MIN_VALUE;
                }
            }
            if (spanStart != Integer.MIN_VALUE) {
                g.fill(spanStart, y, right + 1, y + 1, color);
            }
        }
    }

    private void drawLine(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        if (x1 == x2) {
            g.fill(x1, Math.min(y1, y2), x1 + 1, Math.max(y1, y2) + 1, color);
        } else if (y1 == y2) {
            g.fill(Math.min(x1, x2), y1, Math.max(x1, x2) + 1, y1 + 1, color);
        } else {
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


    private void drawCenteredShadow(GuiGraphics g, String text, int x, int y, int color) {
        int w = this.font.width(text);
        int tx = x - w / 2;
        int ty = y - this.font.lineHeight / 2;
        // Nutzt jetzt die Schattenfarbe aus der Config
        g.drawString(this.font, text, tx + 1, ty + 1, generalConfigs.COLOR_TEXT_SHADOW, false);
        g.drawString(this.font, text, tx, ty, color, false);
    }

    private record SlotStatus(int current, int max) {}

    private SlotStatus computeSlotStatus(int grade) {
        if (grade <= 0) return new SlotStatus(0, 0);

        Player player = Minecraft.getInstance().player;
        if (player == null) return new SlotStatus(0, 0);

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass.replace("\"", ""));
        if (classDef == null || classDef.getSpellSlots() == null) return new SlotStatus(0, 0);

        int[][] allSlots = classDef.getSpellSlots();
        int levelIdx = (int) vars.PlayerLevel;
        if (levelIdx >= allSlots.length) levelIdx = allSlots.length - 1;
        if (levelIdx < 0) levelIdx = 0;

        int maxSlots = 0;
        if (grade - 1 < allSlots[levelIdx].length) maxSlots = allSlots[levelIdx][grade - 1];
        if (maxSlots <= 0) return new SlotStatus(0, 0);

        int currentSlots = 0;
        String rawSlots = vars.Spellslots != null ? vars.Spellslots.replace("\"", "") : "000000000";
        if (rawSlots.length() >= grade) currentSlots = Character.getNumericValue(rawSlots.charAt(grade - 1));

        return new SlotStatus(currentSlots, maxSlots);
    }

    private String getSlotInfo(int grade) {
        SlotStatus status = computeSlotStatus(grade);
        return status.max() <= 0 ? "" : " " + status.current() + "/" + status.max();
    }

    private boolean isGradeDepleted(int grade) {
        SlotStatus status = computeSlotStatus(grade);
        return status.max() > 0 && status.current() <= 0;
    }
}