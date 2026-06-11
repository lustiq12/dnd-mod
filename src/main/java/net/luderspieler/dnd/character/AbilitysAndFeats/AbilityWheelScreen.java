package net.luderspieler.dnd.character.AbilitysAndFeats;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDefinitionRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityCategory;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.resources.ResourceManager;
import net.luderspieler.dnd.resources.UseResourceActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Radiales Ability-Wheel mit zwei Stages — analog zu SpellWheelScreen.
 *
 * Stage ABILITY_SELECT — Haupt-Wheel mit allen PLAYER_TRIGGERED Abilities.
 * Stage FOCUS_SPEND    — Sub-Wheel für Focus-Point-Ausgaben (Monk).
 * Stage SORCERY_SPEND  — Sub-Wheel für Sorcery-Point-Ausgaben (Sorcerer).
 *
 * Navigation:
 *   Hub klicken          → eine Stage zurück (oder schließen)
 *   FOCUS_POINTS wählen  → FOCUS_SPEND
 *   FONT_OF_MAGIC wählen → SORCERY_SPEND
 *   Rechtsklick          → eine Stage zurück
 *   ESC                  → schließen
 */
public class AbilityWheelScreen extends Screen {

    // ── Stages ───────────────────────────────────────────────────────
    private enum Stage { ABILITY_SELECT, FOCUS_SPEND, SORCERY_SPEND }

    /**
     * Eine wählbare Aktion im Sub-Wheel.
     * @param minLevel  Mindest-Sorcerer/Monk-Level für diese Option (Level-Gating)
     */
    private record SubAction(String name, String detail, int cost, int minLevel,
                             ResourceManager.ResourcePool pool, String actionKey) {}

    // ── Focus-Point-Aktionen (2024 PHB, Monk) ────────────────────────
    // Alle drei Basis-Optionen gibt es ab Level 1 (werden mit FOCUS_POINTS gegeben).
    // Höhere FP-Abilities (STUNNING_STRIKE etc.) erscheinen im Haupt-Wheel sobald gelernt.
    private static final List<SubAction> FOCUS_ACTIONS = List.of(
            new SubAction("Flurry of Blows",  "2 extra Unarmed Strikes (Bonus Action)", 1, 1, ResourceManager.ResourcePool.FOCUS_POINTS, "FLURRY_OF_BLOWS"),
            new SubAction("Patient Defense",  "Dodge als Bonus Action",                 1, 1, ResourceManager.ResourcePool.FOCUS_POINTS, "PATIENT_DEFENSE"),
            new SubAction("Step of the Wind", "Dash oder Disengage + Jump-Reichweite×2",1, 1, ResourceManager.ResourcePool.FOCUS_POINTS, "STEP_OF_THE_WIND")
    );

    /**
     * Font of Magic — Spell Slot erstellen (Bonus Action).
     * Kosten und Mindest-Level exakt nach 2024 PHB Sorcerer-Tabelle.
     * Slots verschwinden nach Long Rest.
     */
    private static final List<SubAction> SORCERY_ACTIONS = List.of(
            new SubAction("Spell Slot 1",  "2 SP → Spell Slot Level 1",  2, 2, ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_1"),
            new SubAction("Spell Slot 2",  "3 SP → Spell Slot Level 2",  3, 3, ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_2"),
            new SubAction("Spell Slot 3",  "5 SP → Spell Slot Level 3",  5, 5, ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_3"),
            new SubAction("Spell Slot 4",  "6 SP → Spell Slot Level 4",  6, 7, ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_4"),
            new SubAction("Spell Slot 5",  "7 SP → Spell Slot Level 5",  7, 9, ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_5")
    );

    // ── Layout ───────────────────────────────────────────────────────
    private static final int OUTER_RADIUS  = 90;
    private static final int INNER_RADIUS  = 30;
    private static final int LABEL_RADIUS  = 65;
    private static final float HOVER_EXPAND = 6f;

    // ── Farben ───────────────────────────────────────────────────────
    private static final int COL_IDLE      = 0xAA222233;
    private static final int COL_HOVER     = 0xCC334466;
    private static final int COL_DEPLETED  = 0xAA221111;
    private static final int COL_HOV_DEPL  = 0xCC331111;
    private static final int COL_OUTLINE   = 0xFF111111;
    private static final int COL_HUB       = 0xFF1A1A2E;
    // Sub-Wheel
    private static final int COL_FP_IDLE   = 0xAA1122AA;
    private static final int COL_FP_HOVER  = 0xCC2244CC;
    private static final int COL_SP_IDLE   = 0xAA441177;
    private static final int COL_SP_HOVER  = 0xCC6622AA;
    private static final int COL_CANT      = 0xAA332211;
    private static final int COL_CANT_HOV  = 0xCC443322;
    private static final int COL_LEVEL     = 0xAA222211;  // Zu niedriges Level (gelblich-grau)
    private static final int COL_LEVEL_HOV = 0xCC444422;

    // ── State ─────────────────────────────────────────────────────────
    private Stage stage = Stage.ABILITY_SELECT;
    private int hoveredSegment   = -1;
    private int hoveredSubAction = -1;
    private int cx, cy;

    public AbilityWheelScreen() { super(Component.empty()); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        super.init();
        cx = this.width / 2;
        cy = this.height / 2;
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDER
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);
        switch (stage) {
            case ABILITY_SELECT -> renderMainWheel(g, mouseX, mouseY);
            case FOCUS_SPEND    -> renderSubWheel(g, mouseX, mouseY, FOCUS_ACTIONS,
                    ResourceManager.ResourcePool.FOCUS_POINTS,
                    "Focus Points", COL_FP_IDLE, COL_FP_HOVER);
            case SORCERY_SPEND  -> renderSubWheel(g, mouseX, mouseY, SORCERY_ACTIONS,
                    ResourceManager.ResourcePool.SORCERY_POINTS,
                    "Sorcery Points", COL_SP_IDLE, COL_SP_HOVER);
        }
        super.render(g, mouseX, mouseY, partial);
    }

    // ── Stage 1: Haupt-Wheel ──────────────────────────────────────────

    private void renderMainWheel(GuiGraphics g, int mouseX, int mouseY) {
        List<Ability> abilities = getClientAbilities();
        float scale = getScale(abilities.size());
        int outerR  = (int)(OUTER_RADIUS * scale);
        int hubR    = (int)(INNER_RADIUS * scale);
        int labelR  = (int)(LABEL_RADIUS * scale);

        if (abilities.isEmpty()) {
            drawCircle(g, cx, cy, hubR + 10, COL_HUB, COL_OUTLINE);
            drawCentered(g, "No abilities", cx, cy - 5, generalConfigs.TEXT_GRAY);
            super.render(g, mouseX, mouseY, 0);
            return;
        }

        int   count      = abilities.size();
        double sliceAngle = (2 * Math.PI) / count;
        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double dist       = Math.hypot(mouseX - cx, mouseY - cy);

        hoveredSegment = -1;
        if (dist > hubR && dist < outerR + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSegment = (int)(angle / sliceAngle) % count;
        }

        var clientVars = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getData(DndModVariables.PLAYER_VARIABLES) : null;

        for (int i = 0; i < count; i++) {
            Ability ability   = abilities.get(i);
            boolean hovered   = i == hoveredSegment;
            boolean depleted  = isDepleted(ability, clientVars);
            double  start     = -Math.PI / 2 + i * sliceAngle;
            double  end       = start + sliceAngle;
            int     curOuter  = hovered ? outerR + (int)HOVER_EXPAND : outerR;
            int     color     = depleted
                    ? (hovered ? COL_HOV_DEPL : COL_DEPLETED)
                    : (hovered ? COL_HOVER    : COL_IDLE);

            drawSegment(g, cx, cy, hubR, curOuter, start, end, color, COL_OUTLINE);

            double mid  = (start + end) / 2;
            int    lx   = cx + (int)(labelR * Math.cos(mid));
            int    ly   = cy + (int)(labelR * Math.sin(mid));
            String name = formatAbilityName(ability);
            String uses = getUsesString(ability, clientVars);
            int textCol = depleted ? 0xFF885555 : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;

            drawCentered(g, name, lx, ly - (uses.isEmpty() ? 0 : 4), textCol);
            if (!uses.isEmpty())
                drawCentered(g, uses, lx, ly + 6, depleted ? 0xFF663333 : generalConfigs.TEXT_GRAY);
        }

        drawCircle(g, cx, cy, hubR, COL_HUB, COL_OUTLINE);
        drawCentered(g, "Abilities", cx, cy - 4, generalConfigs.TEXT_WHITE);

        if (hoveredSegment >= 0 && hoveredSegment < count) {
            g.drawCenteredString(this.font, abilities.get(hoveredSegment).getDisplayName(),
                    cx, cy + outerR + 20, generalConfigs.COLOR_ACCENT_GOLD);
        }
    }

    // ── Stage 2: Sub-Wheel für Resource-Pool ─────────────────────────

    private void renderSubWheel(GuiGraphics g, int mouseX, int mouseY,
                                List<SubAction> actions,
                                ResourceManager.ResourcePool pool,
                                String poolLabel,
                                int idleColor, int hoverColor) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        int current     = ResourceManager.getCurrent(player, pool);
        int max         = ResourceManager.getMaxCached(player, pool);
        int playerLevel = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;

        float  scale       = getScale(actions.size());
        int    outerR      = (int)(OUTER_RADIUS * scale);
        int    hubR        = (int)(INNER_RADIUS * scale);
        int    labelR      = (int)(LABEL_RADIUS * scale);
        int    count       = actions.size();
        double sliceAngle  = (2 * Math.PI) / count;
        double mouseAngle  = Math.atan2(mouseY - cy, mouseX - cx);
        double dist        = Math.hypot(mouseX - cx, mouseY - cy);

        hoveredSubAction = -1;
        if (dist > hubR && dist < outerR + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSubAction = (int)(angle / sliceAngle) % count;
        }

        for (int i = 0; i < count; i++) {
            SubAction action     = actions.get(i);
            boolean   hovered    = i == hoveredSubAction;
            boolean   hasEnough  = current >= action.cost();
            boolean   hasLevel   = playerLevel >= action.minLevel();
            boolean   canUse     = hasEnough && hasLevel;

            double start    = -Math.PI / 2 + i * sliceAngle;
            double end      = start + sliceAngle;
            int    curOuter = hovered ? outerR + (int)HOVER_EXPAND : outerR;

            int color;
            if (canUse) {
                color = hovered ? hoverColor : idleColor;
            } else if (!hasLevel) {
                color = hovered ? COL_LEVEL_HOV : COL_LEVEL;
            } else {
                color = hovered ? COL_CANT_HOV : COL_CANT;
            }

            drawSegment(g, cx, cy, hubR, curOuter, start, end, color, COL_OUTLINE);

            double mid   = (start + end) / 2;
            int    lx    = cx + (int)(labelR * Math.cos(mid));
            int    ly    = cy + (int)(labelR * Math.sin(mid));
            int    textC = canUse
                    ? (hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE)
                    : 0xFF886644;

            drawCentered(g, action.name(), lx, ly - 6, textC);

            // Kosten-Zeile
            String costStr = action.cost() + " " + pool.displayName;
            drawCentered(g, costStr, lx, ly + 3, canUse ? generalConfigs.TEXT_GRAY : 0xFF554433);

            // Level-Anforderung wenn locked
            if (!hasLevel) {
                drawCentered(g, "Lvl " + action.minLevel(), lx, ly + 12, 0xFF888844);
            }
        }

        // Hub: zeigt aktuellen Ressourcenstand
        drawCircle(g, cx, cy, hubR, COL_HUB, COL_OUTLINE);
        drawCentered(g, current + "/" + max, cx, cy - 5, generalConfigs.TEXT_WHITE);
        drawCentered(g, "← Back",           cx, cy + 4, generalConfigs.TEXT_GRAY);

        // Tooltip der gewählten Aktion am unteren Rand
        if (hoveredSubAction >= 0 && hoveredSubAction < count) {
            SubAction a        = actions.get(hoveredSubAction);
            boolean   hasEnough = current >= a.cost();
            boolean   hasLevel  = playerLevel >= a.minLevel();
            String    tooltip;
            if (!hasLevel) {
                tooltip = a.detail() + "  [benötigt Sorcerer Level " + a.minLevel() + "]";
            } else if (!hasEnough) {
                tooltip = a.detail() + "  [nicht genug " + pool.displayName + "]";
            } else {
                tooltip = a.detail();
            }
            g.drawCenteredString(this.font, tooltip, cx, cy + outerR + 20,
                    generalConfigs.COLOR_ACCENT_GOLD);
        }

        // Pool-Name oben
        g.drawCenteredString(this.font, poolLabel + "  (" + current + "/" + max + ")",
                cx, cy - outerR - 20, generalConfigs.TEXT_WHITE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  INPUT
    // ══════════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        double dist = Math.hypot(mx - cx, my - cy);

        // Rechtsklick: immer zurück / schließen
        if (btn == 1) {
            navigateBack();
            return true;
        }

        if (btn == 0) {
            switch (stage) {
                case ABILITY_SELECT -> handleMainWheelClick(dist);
                case FOCUS_SPEND   -> handleSubWheelClick(dist, FOCUS_ACTIONS);
                case SORCERY_SPEND -> handleSubWheelClick(dist, SORCERY_ACTIONS);
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    private void handleMainWheelClick(double dist) {
        List<Ability> abilities = getClientAbilities();
        int hubR = (int)(INNER_RADIUS * getScale(abilities.size()));

        if (dist <= hubR) { this.onClose(); return; }

        if (hoveredSegment >= 0 && hoveredSegment < abilities.size()) {
            Ability chosen = abilities.get(hoveredSegment);
            // Abilities die ein Sub-Wheel öffnen
            if (chosen == Ability.FOCUS_POINTS) {
                stage = Stage.FOCUS_SPEND;
            } else if (chosen == Ability.FONT_OF_MAGIC) {
                stage = Stage.SORCERY_SPEND;
            } else {
                // Normale Ability aktivieren
                ActivateAbilityPacket.send(chosen);
                this.onClose();
            }
        }
    }

    private void handleSubWheelClick(double dist, List<SubAction> actions) {
        float scale = getScale(actions.size());
        int   hubR  = (int)(INNER_RADIUS * scale);

        if (dist <= hubR) {
            navigateBack();
            return;
        }

        if (hoveredSubAction >= 0 && hoveredSubAction < actions.size()) {
            SubAction action  = actions.get(hoveredSubAction);
            Player    player  = Minecraft.getInstance().player;
            if (player == null) return;

            int current     = ResourceManager.getCurrent(player, action.pool());
            int playerLevel = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;

            if (current < action.cost() || playerLevel < action.minLevel()) {
                return; // Zu wenig Punkte oder Level — Screen bleibt offen (Spieler sieht warum)
            }
            UseResourceActionPacket.send(action.pool(), action.actionKey());
            this.onClose();
        }
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { this.onClose(); return true; } // ESC
        return super.keyPressed(key, b, c);
    }

    private void navigateBack() {
        if (stage != Stage.ABILITY_SELECT) {
            stage = Stage.ABILITY_SELECT;
        } else {
            this.onClose();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DATEN-HELPERS
    // ══════════════════════════════════════════════════════════════════

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

    /**
     * Prüft ob eine Ability keine Ladungen mehr hat.
     * Berücksichtigt Resource-Pool-Abilities mit eigenen Keys.
     */
    private boolean isDepleted(Ability ability, DndModVariables.PlayerVariables vars) {
        if (vars == null) return false;
        Player player = Minecraft.getInstance().player;

        // Spezielle Resource-Pool-Abilities
        if (ability == Ability.FOCUS_POINTS && player != null) {
            return ResourceManager.getCurrent(player, ResourceManager.ResourcePool.FOCUS_POINTS) <= 0;
        }
        if (ability == Ability.FONT_OF_MAGIC && player != null) {
            return ResourceManager.getCurrent(player, ResourceManager.ResourcePool.SORCERY_POINTS) <= 0;
        }

        // Standard: _uses Key
        String key = ability.name() + "_uses";
        var map = AbilityDataUtils.parse(vars.AbilityData);
        if (!map.containsKey(key)) return false;
        try { return Integer.parseInt(map.get(key)) <= 0; }
        catch (NumberFormatException e) { return false; }
    }

    /**
     * Ladungsanzeige unter dem Ability-Namen im Wheel.
     */
    private String getUsesString(Ability ability, DndModVariables.PlayerVariables vars) {
        if (vars == null) return "";
        Player player = Minecraft.getInstance().player;

        // Spezielle Resource-Pool-Abilities
        if (ability == Ability.FOCUS_POINTS && player != null) {
            int cur = ResourceManager.getCurrent(player, ResourceManager.ResourcePool.FOCUS_POINTS);
            int max = ResourceManager.getMaxCached(player, ResourceManager.ResourcePool.FOCUS_POINTS);
            return cur + "/" + max;
        }
        if (ability == Ability.FONT_OF_MAGIC && player != null) {
            int cur = ResourceManager.getCurrent(player, ResourceManager.ResourcePool.SORCERY_POINTS);
            int max = ResourceManager.getMaxCached(player, ResourceManager.ResourcePool.SORCERY_POINTS);
            return cur + "/" + max + " SP";
        }

        // Standard: _uses Key
        String key = ability.name() + "_uses";
        var map = AbilityDataUtils.parse(vars.AbilityData);
        if (!map.containsKey(key)) return "";
        try {
            int remaining = Integer.parseInt(map.get(key));
            return remaining == 0 ? "✗ Used" : remaining + " left";
        } catch (NumberFormatException e) { return ""; }
    }

    private float getScale(int segmentCount) {
        return segmentCount >= 12 ? 3.0f : segmentCount >= 6 ? 2.0f : 1.5f;
    }

    private String formatAbilityName(Ability ability) {
        String raw = ability.name();
        for (String suffix : new String[]{"_BARBARIAN","_BARD","_CLERIC","_DRUID",
                "_FIGHTER","_MONK","_PALADIN","_RANGER","_ROGUE","_SORCERER","_WARLOCK","_WIZARD"}) {
            if (raw.endsWith(suffix)) { raw = raw.substring(0, raw.length() - suffix.length()); break; }
        }
        StringBuilder sb = new StringBuilder();
        for (String part : raw.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        String result = sb.toString();
        return result.length() > 12 ? result.substring(0, 11) + "." : result;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ZEICHEN-PRIMITIVEN (identisch SpellWheelScreen)
    // ══════════════════════════════════════════════════════════════════

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

    private void drawCircle(GuiGraphics g, int ox, int oy, int r, int fill, int outline) {
        drawSegment(g, ox, oy, 0, r, -Math.PI / 2, Math.PI * 1.5, fill, outline);
    }

    private void addVertex(VertexConsumer buf, org.joml.Matrix3x2f m,
                           float x, float y, int r, int gr, int b, int a) {
        float tx = m.m00() * x + m.m10() * y + m.m20();
        float ty = m.m01() * x + m.m11() * y + m.m21();
        buf.addVertex(tx, ty, 0f).setColor(r, gr, b, a);
    }

    private void drawCentered(GuiGraphics g, String text, int x, int y, int color) {
        int w = this.font.width(text);
        g.drawString(this.font, text, x - w / 2 + 1, y - this.font.lineHeight / 2 + 1,
                generalConfigs.COLOR_TEXT_SHADOW, false);
        g.drawString(this.font, text, x - w / 2, y - this.font.lineHeight / 2, color, false);
    }
}