package net.luderspieler.dnd.character.AbilitysAndFeats;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDefinitionRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityCategory;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.resources.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Radiales Ability-Wheel mit mehreren Stages.
 *
 * ABILITY_SELECT  – Haupt-Wheel: alle PLAYER_TRIGGERED-Abilities
 * FOCUS_SPEND     – Sub-Wheel: Focus-Point-Ausgaben (Monk)
 * SORCERY_SPEND   – Sub-Wheel: Sorcery-Point → Spell-Slot (Sorcerer)
 * METAMAGIC_SELECT– Sub-wheel: select a Metamagic option (Sorcerer)
 *
 * Alle Aktionen werden via ActivateAbilityPacket gesendet (kein separates
 * UseResourceActionPacket mehr).
 */
public class AbilityWheelScreen extends Screen {

    // ── Stages ───────────────────────────────────────────────────────
    private enum Stage { ABILITY_SELECT, FOCUS_SPEND, SORCERY_SPEND, METAMAGIC_SELECT }

    /**
     * A selectable action within a sub-wheel.
     * @param minLevel Minimum level required for this option (level gating).
     */
    private record SubAction(String name, String detail, int cost, int minLevel,
                             ResourceManager.ResourcePool pool, String actionKey) {}

    // ── Focus-Point-Aktionen (Monk, 2024 PHB) ────────────────────────
    private static final List<SubAction> FOCUS_ACTIONS = List.of(
            new SubAction("Flurry of Blows",  "2 extra Unarmed Strikes (Bonus Action)", 1, 1,
                    ResourceManager.ResourcePool.FOCUS_POINTS, "FLURRY_OF_BLOWS"),
            new SubAction("Patient Defense",  "Dodge as Bonus Action",                  1, 1,
                    ResourceManager.ResourcePool.FOCUS_POINTS, "PATIENT_DEFENSE"),
            new SubAction("Step of the Wind", "Dash or Disengage + Jump ×2",            1, 1,
                    ResourceManager.ResourcePool.FOCUS_POINTS, "STEP_OF_THE_WIND")
    );

    // ── Sorcery-Point-Aktionen (Sorcerer, 2024 PHB) ──────────────────
    // SP → Slot (Font of Magic, Hin-Richtung)
    private static final List<SubAction> SORCERY_ACTIONS = List.of(
            new SubAction("Spell Slot 1", "2 SP → 1st-level slot", 2, 2,
                    ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_1"),
            new SubAction("Spell Slot 2", "3 SP → 2nd-level slot", 3, 3,
                    ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_2"),
            new SubAction("Spell Slot 3", "5 SP → 3rd-level slot", 5, 5,
                    ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_3"),
            new SubAction("Spell Slot 4", "6 SP → 4th-level slot", 6, 7,
                    ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_4"),
            new SubAction("Spell Slot 5", "7 SP → 5th-level slot", 7, 9,
                    ResourceManager.ResourcePool.SORCERY_POINTS, "SLOT_5")
    );

    // Slot → SP (Font of Magic, reverse direction).
    // This list is built dynamically in getSlotToSpActions() (only shows slots > 0).
    // Grades 6-9 cannot be converted per the 2024 PHB.
    private List<SubAction> getSlotToSpActions(net.minecraft.world.entity.player.Player player) {
        var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);
        String slots = vars.Spellslots != null ? vars.Spellslots : "000000000";
        List<SubAction> list = new java.util.ArrayList<>();
        String[] labels = {"1st", "2nd", "3rd", "4th", "5th"};
        for (int i = 0; i < 5; i++) {
            int count = (slots.length() > i) ? (slots.charAt(i) - '0') : 0;
            if (count < 1) continue; // no slot of this grade available
            list.add(new SubAction(
                    "Slot Lv." + (i + 1) + " → SP",
                    (i + 1) + " SP  (has " + count + " " + labels[i] + ")",
                    0, // cost isn't used for SP-gating here; slot availability is checked server-side
                    2,
                    ResourceManager.ResourcePool.SORCERY_POINTS,
                    "SLOT_TO_SP_" + (i + 1)
            ));
        }
        return list;
    }

    // ── Metamagic-Konstanten ─────────────────────────────────────────
    private static final Map<String, Integer> METAMAGIC_SP_COSTS = Map.of(
            "Distant Spell",    1,
            "Empowered Spell",  1,
            "Extended Spell",   1,
            "Transmuted Spell", 1,
            "Twinned Spell",    1
    );

    private static final Map<String, String> METAMAGIC_DETAILS = Map.of(
            "Distant Spell",    "Double range, or Touch becomes 30 ft",
            "Empowered Spell",  "+CHA modifier flat damage/healing on your next spell",
            "Extended Spell",   "Double the spell's effect duration",
            "Transmuted Spell", "Bypass the target's resistance to the spell's damage type",
            "Twinned Spell",    "Hit a second target (costs 1 extra spell level in SP)"
    );

    // ── Layout ───────────────────────────────────────────────────────
    private static final int   OUTER_RADIUS  = 90;
    private static final int   INNER_RADIUS  = 30;
    private static final int   LABEL_RADIUS  = 65;
    private static final float HOVER_EXPAND  = 6f;

    // ── State ─────────────────────────────────────────────────────────
    private Stage stage          = Stage.ABILITY_SELECT;
    private int   hoveredSegment   = -1;
    private int   hoveredSubAction = -1;
    private int   cx, cy;

    public AbilityWheelScreen() { super(Component.empty()); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        super.init();
        cx = this.width  / 2;
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
                    "Focus Points", generalConfigs.WHEEL_FP_IDLE, generalConfigs.WHEEL_FP_HOVER);

            case SORCERY_SPEND  -> {
                // Combine SP→Slot (static list) and Slot→SP (dynamic, based on available slots)
                Player p = Minecraft.getInstance().player;
                List<SubAction> fontActions = new java.util.ArrayList<>(SORCERY_ACTIONS);
                if (p != null) fontActions.addAll(getSlotToSpActions(p));
                renderSubWheel(g, mouseX, mouseY, fontActions,
                        ResourceManager.ResourcePool.SORCERY_POINTS,
                        "Sorcery Points", generalConfigs.WHEEL_SP_IDLE, generalConfigs.WHEEL_SP_HOVER);
            }

            case METAMAGIC_SELECT -> {
                Player p = Minecraft.getInstance().player;
                List<SubAction> meta = p != null ? getMetamagicActions(p) : List.of();
                if (meta.isEmpty()) {
                    int hubR = (int) (INNER_RADIUS * 1.5f);
                    drawCircle(g, cx, cy, hubR, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);
                    drawCentered(g, "No Metamagic", cx, cy - 4, generalConfigs.TEXT_GRAY);
                    drawCentered(g, "chosen yet",   cx, cy + 6, generalConfigs.TEXT_GRAY);
                } else {
                    renderSubWheel(g, mouseX, mouseY, meta,
                            ResourceManager.ResourcePool.SORCERY_POINTS,
                            "Metamagic", generalConfigs.WHEEL_SP_IDLE, generalConfigs.WHEEL_SP_HOVER);
                }
            }
        }

        super.render(g, mouseX, mouseY, partial);
    }

    // ── Haupt-Wheel ──────────────────────────────────────────────────

    private void renderMainWheel(GuiGraphics g, int mouseX, int mouseY) {
        List<Ability> abilities = getClientAbilities();
        float scale  = getScale(abilities.size());
        int   outerR = (int) (OUTER_RADIUS * scale);
        int   hubR   = (int) (INNER_RADIUS * scale);
        int   labelR = (int) (LABEL_RADIUS * scale);

        if (abilities.isEmpty()) {
            drawCircle(g, cx, cy, hubR + 10, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);
            drawCentered(g, "No abilities", cx, cy - 4, generalConfigs.TEXT_GRAY);
            return;
        }

        int    count      = abilities.size();
        double sliceAngle = (2 * Math.PI) / count;
        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double dist       = Math.hypot(mouseX - cx, mouseY - cy);

        hoveredSegment = -1;
        if (dist > hubR && dist < outerR + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSegment = (int) (angle / sliceAngle) % count;
        }

        var clientVars = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getData(DndModVariables.PLAYER_VARIABLES) : null;

        for (int i = 0; i < count; i++) {
            Ability ability  = abilities.get(i);
            boolean hovered  = i == hoveredSegment;
            boolean depleted = isDepleted(ability, clientVars);
            double  start    = -Math.PI / 2 + i * sliceAngle;
            double  end      = start + sliceAngle;
            int     curOuter = hovered ? outerR + (int) HOVER_EXPAND : outerR;

            int color;
            if (depleted) {
                color = hovered ? generalConfigs.WHEEL_SEGMENT_DEPL_HOVER
                        : generalConfigs.WHEEL_SEGMENT_DEPLETED;
            } else {
                color = hovered ? generalConfigs.WHEEL_SEGMENT_HOVER
                        : generalConfigs.WHEEL_SEGMENT_IDLE;
            }

            drawSegment(g, cx, cy, hubR, curOuter, start, end, color, generalConfigs.WHEEL_OUTLINE);

            double mid   = (start + end) / 2;
            int    lx    = cx + (int) (labelR * Math.cos(mid));
            int    ly    = cy + (int) (labelR * Math.sin(mid));
            String name  = formatAbilityName(ability);
            String uses  = getUsesString(ability, clientVars);
            int textCol  = depleted ? 0xFF885555
                    : hovered  ? generalConfigs.TEXT_HOVER
                      : generalConfigs.TEXT_WHITE;

            drawCentered(g, name, lx, ly - (uses.isEmpty() ? 0 : 4), textCol);
            if (!uses.isEmpty())
                drawCentered(g, uses, lx, ly + 6,
                        depleted ? 0xFF663333 : generalConfigs.TEXT_GRAY);
        }

        drawCircle(g, cx, cy, hubR, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);
        drawCentered(g, "Abilities", cx, cy - 4, generalConfigs.TEXT_WHITE);

        if (hoveredSegment >= 0 && hoveredSegment < count) {
            g.drawCenteredString(this.font, abilities.get(hoveredSegment).getDisplayName(),
                    cx, cy + outerR + 20, generalConfigs.COLOR_ACCENT_GOLD);
        }
    }

    // ── Sub-Wheel (Focus / Sorcery / Metamagic) ───────────────────────

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

        float  scale      = getScale(actions.size());
        int    outerR     = (int) (OUTER_RADIUS * scale);
        int    hubR       = (int) (INNER_RADIUS * scale);
        int    labelR     = (int) (LABEL_RADIUS * scale);
        int    count      = actions.size();
        double sliceAngle = (2 * Math.PI) / count;
        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);
        double dist       = Math.hypot(mouseX - cx, mouseY - cy);

        hoveredSubAction = -1;
        if (dist > hubR && dist < outerR + HOVER_EXPAND) {
            double angle = mouseAngle + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            hoveredSubAction = (int) (angle / sliceAngle) % count;
        }

        for (int i = 0; i < count; i++) {
            SubAction action  = actions.get(i);
            boolean   hovered = i == hoveredSubAction;
            boolean   hasRes  = current >= action.cost();
            boolean   hasLvl  = playerLevel >= action.minLevel();
            boolean   canUse  = hasRes && hasLvl;

            double start    = -Math.PI / 2 + i * sliceAngle;
            double end      = start + sliceAngle;
            int    curOuter = hovered ? outerR + (int) HOVER_EXPAND : outerR;

            int color;
            if (canUse) {
                color = hovered ? hoverColor : idleColor;
            } else if (!hasLvl) {
                color = hovered ? generalConfigs.WHEEL_SEGMENT_LEVEL_HOVER
                        : generalConfigs.WHEEL_SEGMENT_LEVEL;
            } else {
                color = hovered ? generalConfigs.WHEEL_SEGMENT_LOCKED_HOVER
                        : generalConfigs.WHEEL_SEGMENT_LOCKED;
            }

            drawSegment(g, cx, cy, hubR, curOuter, start, end, color, generalConfigs.WHEEL_OUTLINE);

            double mid   = (start + end) / 2;
            int    lx    = cx + (int) (labelR * Math.cos(mid));
            int    ly    = cy + (int) (labelR * Math.sin(mid));
            int    textC = canUse ? (hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE)
                    : 0xFF886644;

            drawCentered(g, action.name(), lx, ly - 6, textC);
            String costStr = action.cost() + " " + pool.displayName;
            drawCentered(g, costStr, lx, ly + 3,
                    canUse ? generalConfigs.TEXT_GRAY : 0xFF554433);
            if (!hasLvl)
                drawCentered(g, "Lvl " + action.minLevel(), lx, ly + 12, 0xFF888844);
        }

        // Hub
        drawCircle(g, cx, cy, hubR, generalConfigs.WHEEL_HUB, generalConfigs.WHEEL_OUTLINE);
        drawCentered(g, current + "/" + max, cx, cy - 5, generalConfigs.TEXT_WHITE);
        drawCentered(g, "← Back",           cx, cy + 4, generalConfigs.TEXT_GRAY);

        // Tooltip
        if (hoveredSubAction >= 0 && hoveredSubAction < count) {
            SubAction a     = actions.get(hoveredSubAction);
            boolean   hasR  = current >= a.cost();
            boolean   hasL  = playerLevel >= a.minLevel();
            String    tip;
            if (!hasL) tip = a.detail() + "  [requires Level " + a.minLevel() + "]";
            else if (!hasR) tip = a.detail() + "  [not enough " + pool.displayName + "]";
            else            tip = a.detail();
            g.drawCenteredString(this.font, tip, cx, cy + outerR + 20,
                    generalConfigs.COLOR_ACCENT_GOLD);
        }

        // Pool-Label oben
        g.drawCenteredString(this.font, poolLabel + "  (" + current + "/" + max + ")",
                cx, cy - outerR - 20, generalConfigs.TEXT_WHITE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  INPUT
    // ══════════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        double dist = Math.hypot(mx - cx, my - cy);

        if (btn == 1) { navigateBack(); return true; }

        if (btn == 0) {
            switch (stage) {
                case ABILITY_SELECT   -> handleMainWheelClick(dist);
                case FOCUS_SPEND      -> handleSubWheelClick(dist, FOCUS_ACTIONS,
                        Ability.FOCUS_POINTS);
                case SORCERY_SPEND    -> {
                    Player p2 = Minecraft.getInstance().player;
                    List<SubAction> fa = new java.util.ArrayList<>(SORCERY_ACTIONS);
                    if (p2 != null) fa.addAll(getSlotToSpActions(p2));
                    handleSubWheelClick(dist, fa, Ability.FONT_OF_MAGIC);
                }
                case METAMAGIC_SELECT -> {
                    Player p = Minecraft.getInstance().player;
                    handleSubWheelClick(dist,
                            p != null ? getMetamagicActions(p) : List.of(),
                            Ability.METAMAGIC);
                }
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    private void handleMainWheelClick(double dist) {
        List<Ability> abilities = getClientAbilities();
        int hubR = (int) (INNER_RADIUS * getScale(abilities.size()));

        if (dist <= hubR) { this.onClose(); return; }

        if (hoveredSegment >= 0 && hoveredSegment < abilities.size()) {
            Ability chosen = abilities.get(hoveredSegment);
            // Open sub-wheels
            if      (chosen == Ability.FOCUS_POINTS) stage = Stage.FOCUS_SPEND;
            else if (chosen == Ability.FONT_OF_MAGIC) stage = Stage.SORCERY_SPEND;
            else if (chosen == Ability.METAMAGIC)     stage = Stage.METAMAGIC_SELECT;
            else {
                // Normale Ability → Packet senden
                ActivateAbilityPacket.send(chosen);
                this.onClose();
            }
        }
    }

    /**
     * Klick im Sub-Wheel: validiert client-seitig und sendet ActivateAbilityPacket
     * mit der Sub-Aktion. Kein UseResourceActionPacket mehr.
     */
    private void handleSubWheelClick(double dist, List<SubAction> actions,
                                     Ability targetAbility) {
        float scale = getScale(actions.size());
        int   hubR  = (int) (INNER_RADIUS * scale);

        if (dist <= hubR) { navigateBack(); return; }

        if (hoveredSubAction >= 0 && hoveredSubAction < actions.size()) {
            SubAction action = actions.get(hoveredSubAction);
            Player    player = Minecraft.getInstance().player;
            if (player == null) return;

            int current     = ResourceManager.getCurrent(player, action.pool());
            int playerLevel = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;

            if (current < action.cost() || playerLevel < action.minLevel()) {
                // Zu wenig Ressource oder Level zu niedrig — Screen bleibt offen
                return;
            }

            ActivateAbilityPacket.send(targetAbility, action.actionKey());
            this.onClose();
        }
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { this.onClose(); return true; }
        return super.keyPressed(key, b, c);
    }

    private void navigateBack() {
        if (stage != Stage.ABILITY_SELECT) stage = Stage.ABILITY_SELECT;
        else this.onClose();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DATEN-HELPERS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Liest die Metamagic-Optionen des Spielers aus AbilityData["METAMAGIC_chosen"]
     * and builds the SubAction list for the sub-wheel from it.
     */
    private List<SubAction> getMetamagicActions(Player player) {
        String raw = AbilityDataUtils.get(
                player.getData(DndModVariables.PLAYER_VARIABLES),
                "METAMAGIC_chosen", "");
        if (raw.isBlank()) return List.of();

        List<SubAction> list = new ArrayList<>();
        // METAMAGIC_chosen nutzt SEMIKOLON als Trenner (Komma ist in
        // AbilityData reserves commas for top-level key=value pairs).
        for (String n : raw.split(";")) {
            n = n.trim();
            if (n.isBlank()) continue;
            int    cost   = METAMAGIC_SP_COSTS.getOrDefault(n, 1);
            String key    = n.toUpperCase().replace(" ", "_"); // "Careful Spell" → "CAREFUL_SPELL"
            String detail = METAMAGIC_DETAILS.getOrDefault(n, cost + " SP");
            list.add(new SubAction(n, detail, cost, 3,
                    ResourceManager.ResourcePool.SORCERY_POINTS, key));
        }
        return list;
    }

    private List<Ability> getClientAbilities() {
        List<Ability> list = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        if (player == null) return list;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (vars.Abilities == null || vars.Abilities.isBlank()) return list;

        for (String name : vars.Abilities.split(",")) {
            try {
                Ability a = Ability.valueOf(name.trim());
                if (AbilityDefinitionRegistry.getCategory(a) == AbilityCategory.PLAYER_TRIGGERED)
                    list.add(a);
            } catch (IllegalArgumentException ignored) {}
        }
        return list;
    }

    private boolean isDepleted(Ability ability, DndModVariables.PlayerVariables vars) {
        if (vars == null) return false;
        Player player = Minecraft.getInstance().player;

        if (ability == Ability.FOCUS_POINTS && player != null)
            return ResourceManager.getCurrent(player, ResourceManager.ResourcePool.FOCUS_POINTS) <= 0;
        if (ability == Ability.FONT_OF_MAGIC && player != null)
            return ResourceManager.getCurrent(player, ResourceManager.ResourcePool.SORCERY_POINTS) <= 0;
        if (ability == Ability.METAMAGIC && player != null)
            return ResourceManager.getCurrent(player, ResourceManager.ResourcePool.SORCERY_POINTS) <= 0;

        String key = ability.name() + "_uses";
        var map = AbilityDataUtils.parse(vars.AbilityData);
        if (!map.containsKey(key)) return false;
        try { return Integer.parseInt(map.get(key)) <= 0; }
        catch (NumberFormatException e) { return false; }
    }

    private String getUsesString(Ability ability, DndModVariables.PlayerVariables vars) {
        if (vars == null) return "";
        Player player = Minecraft.getInstance().player;

        if (ability == Ability.FOCUS_POINTS && player != null) {
            int cur = ResourceManager.getCurrent(player, ResourceManager.ResourcePool.FOCUS_POINTS);
            int max = ResourceManager.getMaxCached(player, ResourceManager.ResourcePool.FOCUS_POINTS);
            return cur + "/" + max;
        }
        if ((ability == Ability.FONT_OF_MAGIC || ability == Ability.METAMAGIC) && player != null) {
            int cur = ResourceManager.getCurrent(player, ResourceManager.ResourcePool.SORCERY_POINTS);
            int max = ResourceManager.getMaxCached(player, ResourceManager.ResourcePool.SORCERY_POINTS);
            return cur + "/" + max + " SP";
        }

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
        for (String suffix : new String[]{ "_BARBARIAN","_BARD","_CLERIC","_DRUID",
                "_FIGHTER","_MONK","_PALADIN","_RANGER","_ROGUE","_SORCERER",
                "_WARLOCK","_WIZARD" }) {
            if (raw.endsWith(suffix)) {
                raw = raw.substring(0, raw.length() - suffix.length());
                break;
            }
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
    //  RENDER-PRIMITIVEN
    // ══════════════════════════════════════════════════════════════════

    private void drawSegment(GuiGraphics g, int ox, int oy,
                             int innerR, int outerR,
                             double startAngle, double endAngle,
                             int fillColor, int outlineColor) {
        int    steps = 32;
        double range = endAngle - startAngle;
        int    a     = (fillColor >> 24) & 0xFF;
        int    r     = (fillColor >> 16) & 0xFF;
        int    gr    = (fillColor >>  8) & 0xFF;
        int    b     = fillColor & 0xFF;

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
        g.drawString(this.font, text, x - w / 2,     y - this.font.lineHeight / 2,
                color, false);
    }
}