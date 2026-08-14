package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.resources.ResourceManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Displays and adjusts only the ResourceManager pools the target actually has via a matching Ability. */
public class DebugResourcesTab implements DebugTab {

    private static final int ROW_H = 18;
    private static final int PAD_X = 5;
    private static final int PAD_Y = 2;

    private final DebugMainScreen screen;
    private int x, y, w, h;

    private DndModVariables.PlayerVariables vars;
    private final List<ResourceManager.ResourcePool> visiblePools = new ArrayList<>();

    private int hoveredMinus = -1;
    private int hoveredPlus = -1;
    private int hoveredMax = -1;

    public DebugResourcesTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Resources";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x + PAD_X; this.y = y + PAD_Y; this.w = w - PAD_X; this.h = h - PAD_Y;
        vars = snapshot != null ? snapshot.vars() : null;

        visiblePools.clear();
        if (vars == null) return;

        Set<String> abilities = new HashSet<>();
        if (vars.Abilities != null && !vars.Abilities.isBlank() && !vars.Abilities.equals("\"\"")) {
            for (String s : vars.Abilities.split(",")) {
                String t = s.trim();
                if (!t.isEmpty()) abilities.add(t);
            }
        }
        for (ResourceManager.ResourcePool pool : ResourceManager.ResourcePool.values()) {
            if (abilities.contains(pool.requiresAbility.name())) visiblePools.add(pool);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        if (visiblePools.isEmpty()) {
            g.drawString(font, "Target has no resource-granting abilities.", x, y, generalConfigs.TEXT_GRAY, false);
            return;
        }

        hoveredMinus = -1; hoveredPlus = -1; hoveredMax = -1;

        for (int i = 0; i < visiblePools.size(); i++) {
            ResourceManager.ResourcePool pool = visiblePools.get(i);
            int ry = y + i * ROW_H;
            if (ry + ROW_H > y + h) break;

            int current = readCurrent(pool);
            int max = readMax(pool);

            g.fill(x, ry, x + w, ry + ROW_H - 2, 0x22000000);
            g.drawString(font, pool.displayName, x + 4, ry + 4, generalConfigs.TEXT_WHITE, false);
            g.drawString(font, current + " / " + max, x + 130, ry + 4, generalConfigs.TEXT_GRAY, false);

            int minusX = x + 200, plusX = x + 222, maxX = x + 244;
            boolean hovMinus = mouseX >= minusX && mouseX < minusX + 16 && mouseY >= ry && mouseY < ry + ROW_H - 2;
            boolean hovPlus = mouseX >= plusX && mouseX < plusX + 16 && mouseY >= ry && mouseY < ry + ROW_H - 2;
            boolean hovMax = mouseX >= maxX && mouseX < maxX + 36 && mouseY >= ry && mouseY < ry + ROW_H - 2;
            if (hovMinus) hoveredMinus = i;
            if (hovPlus) hoveredPlus = i;
            if (hovMax) hoveredMax = i;

            g.drawString(font, "[-]", minusX, ry + 4, hovMinus ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
            g.drawString(font, "[+]", plusX, ry + 4, hovPlus ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_GRAY, false);
            g.drawString(font, "[Max]", maxX, ry + 4, hovMax ? generalConfigs.COLOR_STATUS_SUCCESS : generalConfigs.TEXT_GRAY, false);
        }
    }

    private int readCurrent(ResourceManager.ResourcePool pool) {
        return switch (pool) {
            case FOCUS_POINTS -> AbilityDataUtils.getInt(vars, pool.dataKey, (int) vars.PlayerLevel);
            case LAY_ON_HANDS -> AbilityDataUtils.getInt(vars, pool.dataKey, (int) vars.PlayerLevel * 5);
            default -> AbilityDataUtils.getInt(vars, pool.dataKey, 0);
        };
    }

    private int readMax(ResourceManager.ResourcePool pool) {
        int level = (int) vars.PlayerLevel;
        int profB = (int) vars.ProficiencyBonus;
        int chaMod = Math.floorDiv((int) vars.Charisma - 10, 2);
        int wisMod = Math.floorDiv((int) vars.Wisdom - 10, 2);

        return switch (pool) {
            case FOCUS_POINTS -> level;
            case RAGE -> level >= 17 ? 6 : level >= 12 ? 5 : level >= 6 ? 4 : level >= 3 ? 3 : 2;
            case SORCERY_POINTS -> level;
            case BARDIC_INSP -> Math.max(1, chaMod);
            case WILD_SHAPE -> level >= 17 ? 4 : level >= 6 ? 3 : 2;
            case CHANNEL_DIV -> level >= 18 ? 4 : level >= 6 ? 3 : 2;
            case CH_DIV_PAL -> 2;
            case LAY_ON_HANDS -> level * 5;
            case SECOND_WIND -> level >= 10 ? 4 : level >= 4 ? 3 : 2;
            case ACTION_SURGE -> level >= 17 ? 2 : 1;
            case INNATE_SORCERY -> 2;
            case MAGICAL_CUNNING -> 1;
            case TIRELESS -> Math.max(1, wisMod);
            case NATURES_VEIL -> profB;
            case BREATH_WEAPON, ADRENALINE -> profB;
            case HEALING_HANDS -> 1;
            case FLIGHT -> 1;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        if (hoveredMinus >= 0) {
            ResourceManager.ResourcePool pool = visiblePools.get(hoveredMinus);
            DebugResourceAdjustPacket.send(uuid, pool.name(), -1, false);
            AbilityDataUtils.increment(vars, pool.dataKey, -1);
            return true;
        }
        if (hoveredPlus >= 0) {
            ResourceManager.ResourcePool pool = visiblePools.get(hoveredPlus);
            DebugResourceAdjustPacket.send(uuid, pool.name(), 1, false);
            AbilityDataUtils.increment(vars, pool.dataKey, 1);
            return true;
        }
        if (hoveredMax >= 0) {
            ResourceManager.ResourcePool pool = visiblePools.get(hoveredMax);
            DebugResourceAdjustPacket.send(uuid, pool.name(), 0, true);
            AbilityDataUtils.set(vars, pool.dataKey, readMax(pool));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }
}