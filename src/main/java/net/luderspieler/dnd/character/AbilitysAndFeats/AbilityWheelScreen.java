package net.luderspieler.dnd.character.AbilitysAndFeats;

import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.luderspieler.dnd.character.AbilitysAndFeats.ActivateAbilityPacket;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDefinitionRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityCategory;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.generalConfigs;
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
 * Radial ability wheel for PLAYER_TRIGGERED abilities.
 * One wheel — no sub-wheels. Click a segment to activate the ability.
 * Open this screen via key binding (handled by the caller).
 */
public class AbilityWheelScreen extends Screen {

    // ── Layout ──────────────────────────────────────────────────────
    private static final int OUTER_RADIUS  = 90;
    private static final int INNER_RADIUS  = 30;
    private static final int LABEL_RADIUS  = 65;
    private static final float HOVER_EXPAND = 6f;

    // ── Colors ──────────────────────────────────────────────────────
    private static final int COL_SEGMENT_IDLE      = 0xAA222233;
    private static final int COL_SEGMENT_HOVER     = 0xCC334466;
    private static final int COL_SEGMENT_DEPLETED  = 0xAA221111;
    private static final int COL_SEGMENT_HOV_DEPL  = 0xCC331111;
    private static final int COL_OUTLINE            = 0xFF111111;
    private static final int COL_HUB               = 0xFF1A1A2E;

    // ── State ────────────────────────────────────────────────────────
    private final List<Ability> abilities = new ArrayList<>();
    private int hoveredSegment = -1;
    private int cx, cy;

    public AbilityWheelScreen() {
        super(Component.empty());
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        super.init();
        cx = this.width  / 2;
        cy = this.height / 2;
        loadAbilities();
    }

    private void loadAbilities() {
        abilities.clear();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        for (Ability a : AbilityUtils.getPlayerAbilities(
                (net.minecraft.server.level.ServerPlayer) null)) {
            // Client-side: read from synced vars.Abilities string directly
            if (AbilityDefinitionRegistry.getCategory(a) == AbilityCategory.PLAYER_TRIGGERED) {
                abilities.add(a);
            }
        }
    }

    /**
     * Client-safe ability list — reads directly from the local player's synced vars.
     */
    private List<Ability> getClientAbilities() {
        List<Ability> list = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        if (player == null) return list;

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (vars.Abilities == null || vars.Abilities.isBlank()) return list;

        for (String name : vars.Abilities.split(",")) {
            try {
                Ability a = Ability.valueOf(name.trim());
                if (AbilityDefinitionRegistry.getCategory(a) == AbilityCategory.PLAYER_TRIGGERED) {
                    list.add(a);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        return list;
    }

    // ── RENDER ───────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

        List<Ability> displayAbilities = getClientAbilities();
        float scale = getScale();

        if (displayAbilities.isEmpty()) {
            int hubR = (int)(INNER_RADIUS * scale);
            drawCircle(g, cx, cy, hubR + 10, COL_HUB, COL_OUTLINE);
            drawCenteredShadow(g, "No abilities", cx, cy - 5, generalConfigs.TEXT_GRAY);
            super.render(g, mouseX, mouseY, partial);
            return;
        }

        int count = displayAbilities.size();
        int outerR = (int)(OUTER_RADIUS * scale);
        int hubR   = (int)(INNER_RADIUS * scale);
        int labelR = (int)(LABEL_RADIUS * scale);

        double sliceAngle = (2 * Math.PI) / count;
        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double dist = Math.sqrt((mouseX - cx) * (double)(mouseX - cx)
                + (mouseY - cy) * (double)(mouseY - cy));

        hoveredSegment = -1;
        if (dist > hubR && dist < outerR + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSegment = (int)(angle / sliceAngle) % count;
        }

        var clientVars = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getData(DndModVariables.PLAYER_VARIABLES)
                : null;

        for (int i = 0; i < count; i++) {
            Ability ability = displayAbilities.get(i);
            boolean hovered  = i == hoveredSegment;
            boolean depleted = isDepleted(ability, clientVars);

            double start = -Math.PI / 2 + i * sliceAngle;
            double end   = start + sliceAngle;
            int curOuter = hovered ? outerR + (int)HOVER_EXPAND : outerR;

            int color = depleted
                    ? (hovered ? COL_SEGMENT_HOV_DEPL : COL_SEGMENT_DEPLETED)
                    : (hovered ? COL_SEGMENT_HOVER    : COL_SEGMENT_IDLE);

            drawSegment(g, cx, cy, hubR, curOuter, start, end, color, COL_OUTLINE);

            // Label (berechnet wie im SpellWheelScreen)
            double mid = (start + end) / 2;
            int lx = cx + (int)(labelR * Math.cos(mid));
            int ly = cy + (int)(labelR * Math.sin(mid));

            // Ability name (formatted)
            String name  = formatAbilityName(ability);
            String uses  = getUsesString(ability, clientVars);
            int textCol  = depleted ? 0xFF885555
                    : hovered  ? generalConfigs.TEXT_HOVER
                      : generalConfigs.TEXT_WHITE;

            drawCenteredShadow(g, name, lx, ly - (uses.isEmpty() ? 0 : 4), textCol);
            if (!uses.isEmpty()) {
                int usesCol = depleted ? 0xFF663333 : generalConfigs.TEXT_GRAY;
                drawCenteredShadow(g, uses, lx, ly + 6, usesCol);
            }
        }

        // Hub
        drawCircle(g, cx, cy, hubR, COL_HUB, COL_OUTLINE);
        drawCenteredShadow(g, "Abilities", cx, cy - 4, generalConfigs.TEXT_WHITE);

        // Tooltip für ausgewählte Fähigkeit — immer 20px unter dem äußersten Rand
        if (hoveredSegment >= 0 && hoveredSegment < count) {
            Ability hov = displayAbilities.get(hoveredSegment);
            String tip = hov.getDisplayName();
            g.drawCenteredString(this.font, tip,
                    cx, cy + outerR + 20, generalConfigs.COLOR_ACCENT_GOLD);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    // ── INPUT ─────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0) {
            double dist = Math.sqrt((mx - cx) * (mx - cx) + (my - cy) * (my - cy));
            int hubR = (int)(INNER_RADIUS * getScale());

            if (dist <= hubR) {
                this.onClose();
                return true;
            }

            List<Ability> list = getClientAbilities();
            if (hoveredSegment >= 0 && hoveredSegment < list.size()) {
                Ability chosen = list.get(hoveredSegment);
                //ActivateAbilityPacket.send(chosen);
                this.onClose();
                return true;
            }
        }

        if (btn == 1) {
            this.onClose();
            return true;
        }

        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { this.onClose(); return true; } // ESC
        return super.keyPressed(key, b, c);
    }

    // ── USE / DEPLETION HELPERS ───────────────────────────────────────

    private boolean isDepleted(Ability ability,
                               DndModVariables.PlayerVariables vars) {
        if (vars == null) return false;
        String key = ability.name() + "_uses";
        var map = AbilityDataUtils.parse(vars.AbilityData);
        if (!map.containsKey(key)) return false; // no use tracking = always available
        try { return Integer.parseInt(map.get(key)) <= 0; }
        catch (NumberFormatException e) { return false; }
    }

    private String getUsesString(Ability ability,
                                 DndModVariables.PlayerVariables vars) {
        if (vars == null) return "";
        String key = ability.name() + "_uses";
        var map = AbilityDataUtils.parse(vars.AbilityData);
        if (!map.containsKey(key)) return "";
        try {
            int remaining = Integer.parseInt(map.get(key));
            return remaining == 0 ? "✗ Used" : remaining + " left";
        } catch (NumberFormatException e) { return ""; }
    }

    private float getScale() {
        int count = getClientAbilities().size();
        return (count >= 12) ? 3.0f : (count >= 6 ? 2.0f : 1.5f);
    }

    // ── FORMATTING ────────────────────────────────────────────────────

    private String formatAbilityName(Ability ability) {
        String raw = ability.name();
        // Remove class suffixes like _BARBARIAN, _FIGHTER etc.
        for (String suffix : new String[]{"_BARBARIAN","_BARD","_CLERIC","_DRUID",
                "_FIGHTER","_MONK","_PALADIN","_RANGER","_ROGUE","_SORCERER",
                "_WARLOCK","_WIZARD"}) {
            if (raw.endsWith(suffix)) { raw = raw.substring(0, raw.length() - suffix.length()); break; }
        }
        // Title case with underscores as spaces
        StringBuilder sb = new StringBuilder();
        for (String part : raw.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        // Truncate long names to fit segment
        String result = sb.toString();
        return result.length() > 12 ? result.substring(0, 11) + "." : result;
    }

    // ── DRAWING PRIMITIVES (adapted from SpellWheelScreen) ─────────────

    private void drawSegment(GuiGraphics g, int ox, int oy,
                             int innerR, int outerR,
                             double startAngle, double endAngle,
                             int fillColor, int outlineColor) {
        int steps = 32;
        double range = endAngle - startAngle;
        int a = (fillColor >> 24) & 0xFF, r = (fillColor >> 16) & 0xFF,
                gr = (fillColor >> 8) & 0xFF, b = fillColor & 0xFF;

        VertexConsumer buffer = Minecraft.getInstance().renderBuffers()
                .bufferSource().getBuffer(RenderType.debugQuads());
        org.joml.Matrix3x2f matrix = g.pose();

        for (int i = 0; i < steps; i++) {
            double a1 = startAngle + range * i / steps;
            double a2 = startAngle + range * (i + 1) / steps;
            float ix1 = ox + (float)(innerR * Math.cos(a1)), iy1 = oy + (float)(innerR * Math.sin(a1));
            float ox1 = ox + (float)(outerR * Math.cos(a1)), oy1 = oy + (float)(outerR * Math.sin(a1));
            float ix2 = ox + (float)(innerR * Math.cos(a2)), iy2 = oy + (float)(innerR * Math.sin(a2));
            float ox2 = ox + (float)(outerR * Math.cos(a2)), oy2 = oy + (float)(outerR * Math.sin(a2));
            addVertex(buffer, matrix, ix1, iy1, r, gr, b, a);
            addVertex(buffer, matrix, ox1, oy1, r, gr, b, a);
            addVertex(buffer, matrix, ox2, oy2, r, gr, b, a);
            addVertex(buffer, matrix, ix2, iy2, r, gr, b, a);
        }
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }

    private void drawCircle(GuiGraphics g, int ox, int oy, int r,
                            int fillColor, int outlineColor) {
        drawSegment(g, ox, oy, 0, r, -Math.PI / 2, Math.PI * 1.5, fillColor, outlineColor);
    }

    private void addVertex(VertexConsumer buf, org.joml.Matrix3x2f m,
                           float x, float y, int r, int gr, int b, int a) {
        float tx = m.m00() * x + m.m10() * y + m.m20();
        float ty = m.m01() * x + m.m11() * y + m.m21();
        buf.addVertex(tx, ty, 0f).setColor(r, gr, b, a);
    }

    private void drawCenteredShadow(GuiGraphics g, String text, int x, int y, int color) {
        int w = this.font.width(text);
        g.drawString(this.font, text, x - w / 2 + 1, y - this.font.lineHeight / 2 + 1,
                generalConfigs.COLOR_TEXT_SHADOW, false);
        g.drawString(this.font, text, x - w / 2, y - this.font.lineHeight / 2, color, false);
    }
}