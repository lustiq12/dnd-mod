package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityCategory;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDefinitionRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;

/** Grid of every known Ability, grouped by class/species using the real AbilityRegistry data. */
public class DebugAbilitiesTab implements DebugTab {

    private static final int LEFT_W = 130;
    private static final int CATEGORY_ROW_H = 16;
    private static final int MIN_CHIP_W = 150;
    private static final int CHIP_H = 30;
    private static final int CHIP_GAP = 4;
    private static final int PAD = 8;

    private static final Map<String, List<Ability>> GROUPS = buildGroups();

    private final DebugMainScreen screen;

    private int x, y, w, h;
    private int rightX, rightW;
    private int contentTop, contentH;

    private final List<String> groupNames = new ArrayList<>(GROUPS.keySet());
    private int selectedGroup = 0;
    private int hoveredCategory = -1;
    private int hoveredChip = -1;

    private int gridScroll = 0;
    private Set<Ability> activeAbilities = Set.of();
    private Map<String, String> abilityData = Map.of();

    public DebugAbilitiesTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Abilities";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        rightX = x + LEFT_W + 8;
        rightW = w - LEFT_W - 8;
        contentTop = y + PAD;
        contentH = h - PAD;

        if (snapshot == null) {
            activeAbilities = Set.of();
            abilityData = Map.of();
            return;
        }

        DndModVariables.PlayerVariables vars = snapshot.vars();
        Set<Ability> active = new HashSet<>();
        if (vars.Abilities != null && !vars.Abilities.isBlank() && !vars.Abilities.equals("\"\"")) {
            for (String name : vars.Abilities.split(",")) {
                try { active.add(Ability.valueOf(name.trim())); } catch (IllegalArgumentException ignored) {}
            }
        }
        activeAbilities = active;
        abilityData = AbilityDataUtils.parse(vars.AbilityData);
    }

    private int chipColumns() {
        return Math.max(1, rightW / (MIN_CHIP_W + CHIP_GAP));
    }

    private int chipWidth(int cols) {
        return (rightW - (cols - 1) * CHIP_GAP) / cols;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();

        hoveredCategory = -1;
        for (int i = 0; i < groupNames.size(); i++) {
            int ry = contentTop + i * CATEGORY_ROW_H;
            if (ry + CATEGORY_ROW_H > y + h) break;
            boolean hovered = mouseX >= x && mouseX < x + LEFT_W && mouseY >= ry && mouseY < ry + CATEGORY_ROW_H;
            boolean selected = i == selectedGroup;
            if (hovered) hoveredCategory = i;
            if (selected) g.fill(x, ry, x + LEFT_W, ry + CATEGORY_ROW_H, 0x5500BB44);
            else if (hovered) g.fill(x, ry, x + LEFT_W, ry + CATEGORY_ROW_H, generalConfigs.COLOR_HOVER_BG);
            int col = selected ? generalConfigs.COLOR_ACCENT_GOLD : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            String name = groupNames.get(i);
            g.drawString(font, name + " (" + GROUPS.get(name).size() + ")", x + 4, ry + 4, col, false);
        }

        g.fill(rightX - 4, contentTop, rightX - 3, contentTop + contentH, generalConfigs.COLOR_PANEL_EDGE);

        List<Ability> abilities = GROUPS.get(groupNames.get(selectedGroup));
        int cols = chipColumns();
        int chipW = chipWidth(cols);
        int rows = (int) Math.ceil(abilities.size() / (double) cols);
        int visibleRows = Math.max(1, contentH / (CHIP_H + CHIP_GAP));
        int maxScroll = Math.max(0, rows - visibleRows);
        gridScroll = Math.min(gridScroll, maxScroll);

        g.enableScissor(rightX, contentTop, rightX + rightW, contentTop + contentH);
        hoveredChip = -1;
        for (int i = 0; i < abilities.size(); i++) {
            int row = i / cols - gridScroll;
            int col = i % cols;

            int cx = rightX + col * (chipW + CHIP_GAP);
            int cy = contentTop + row * (CHIP_H + CHIP_GAP);
            if (cy + CHIP_H < contentTop || cy > contentTop + contentH) continue;

            Ability ability = abilities.get(i);
            boolean active = activeAbilities.contains(ability);
            boolean hovered = mouseX >= cx && mouseX < cx + chipW && mouseY >= cy && mouseY < cy + CHIP_H;
            if (hovered) hoveredChip = i;

            int bg = active ? 0x5500BB44 : hovered ? generalConfigs.COLOR_HOVER_BG : 0x22000000;
            g.fill(cx, cy, cx + chipW, cy + CHIP_H, bg);
            generalConfigs.renderGreenEdge(g, cx, cy, chipW, CHIP_H);

            int nameCol = active ? generalConfigs.COLOR_STATUS_SUCCESS : hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            g.drawString(font, trim(font, ability.getDisplayName(), chipW - 8), cx + 4, cy + 3, nameCol, false);

            AbilityCategory category = AbilityDefinitionRegistry.getCategory(ability);
            String detail = category.name();
            String usesKey = ability.name() + "_uses";
            if (abilityData.containsKey(usesKey)) detail += "  " + abilityData.get(usesKey) + " left";
            g.drawString(font, trim(font, detail, chipW - 8), cx + 4, cy + 15, generalConfigs.TEXT_GRAY, false);
        }
        g.disableScissor();

        if (rows > visibleRows) {
            int maxS = Math.max(1, rows - visibleRows);
            int thumbH = Math.max(10, contentH * visibleRows / rows);
            int thumbY = contentTop + gridScroll * (contentH - thumbH) / maxS;
            g.fill(x + w - 3, contentTop, x + w - 1, contentTop + contentH, 0x33FFFFFF);
            g.fill(x + w - 3, thumbY, x + w - 1, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    private String trim(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text;
        while (text.length() > 1 && font.width(text + "...") > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + "...";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        if (hoveredCategory >= 0) {
            selectedGroup = hoveredCategory;
            gridScroll = 0;
            return true;
        }

        if (hoveredChip >= 0) {
            List<Ability> abilities = GROUPS.get(groupNames.get(selectedGroup));
            if (hoveredChip >= abilities.size()) return false;
            Ability ability = abilities.get(hoveredChip);
            String uuid = screen.currentTargetUuid();
            if (uuid == null) return false;
            boolean nowActive = !activeAbilities.contains(ability);
            DebugAbilityTogglePacket.send(uuid, ability.name(), nowActive);
            if (nowActive) activeAbilities.add(ability); else activeAbilities.remove(ability);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < rightX || mouseX > rightX + rightW || mouseY < contentTop || mouseY > contentTop + contentH) return false;
        gridScroll = Math.max(0, gridScroll - (int) Math.signum(scrollY));
        return true;
    }

    private static Map<String, List<Ability>> buildGroups() {
        Map<String, List<Ability>> groups = new LinkedHashMap<>();

        String[] classNames = {"barbarian", "bard", "cleric", "druid", "fighter", "monk",
                "paladin", "ranger", "rogue", "sorcerer", "warlock", "wizard"};
        for (String cls : classNames) {
            Map<Integer, List<Ability>> byLevel = AbilityRegistry.getClassAbilities(cls);
            LinkedHashSet<Ability> flat = new LinkedHashSet<>();
            for (int lvl = 1; lvl <= 20; lvl++) {
                List<Ability> atLevel = byLevel.get(lvl);
                if (atLevel != null) flat.addAll(atLevel);
            }
            if (!flat.isEmpty()) groups.put(capitalize(cls), new ArrayList<>(flat));
        }

        String[] raceNames = {"human", "dwarf", "elf", "halfling", "dragonborn",
                "gnome", "aasimar", "tiefling", "goliath", "orc"};
        for (String race : raceNames) {
            List<Ability> list = AbilityRegistry.getRaceAbilities(race);
            if (!list.isEmpty()) groups.put(capitalize(race), list);
        }

        List<Ability> damageTypes = new ArrayList<>();
        for (Ability a : Ability.values()) {
            if (a.name().endsWith("_DAMAGE_IMMUNITY") || a.name().endsWith("_DAMAGE_RESISTANCE")) {
                damageTypes.add(a);
            }
        }
        if (!damageTypes.isEmpty()) groups.put("Damage Types", damageTypes);

        Set<Ability> categorized = new HashSet<>();
        groups.values().forEach(categorized::addAll);
        List<Ability> other = new ArrayList<>();
        for (Ability a : Ability.values()) {
            if (!categorized.contains(a)) other.add(a);
        }
        if (!other.isEmpty()) groups.put("Other", other);

        return groups;
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}