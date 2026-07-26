package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.character.AttributeHandler;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.character.feats.FeatRegistry;
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
 * Generisches Choice-Popup.
 *
 * Für ABILITY_SCORE_IMPROVEMENT_OR_FEAT zeigt es einen dreistufigen Ablauf:
 *   Stage 1 (MODE_SELECT) — +2 auf einen Stat / +1 auf zwei Stats / Feat nehmen
 *   Stage 2 (STAT_SINGLE/STAT_DUAL/FEAT_SELECT) — konkrete Auswahl
 *
 * Alle anderen Choice-Typen zeigen eine einfache flache Button-Liste (DIRECT).
 */
public class GenericChoicePopup extends Screen {

    // ── Sub-States ────────────────────────────────────────────────────
    private enum SubState { DIRECT, MODE_SELECT, STAT_SINGLE, STAT_DUAL, FEAT_SELECT }

    private static final String ASI_ID   = "ABILITY_SCORE_IMPROVEMENT_OR_FEAT";
    private static final String[] STATS  = { "Strength","Dexterity","Constitution",
            "Intelligence","Wisdom","Charisma" };

    // ── Felder ────────────────────────────────────────────────────────
    private final LevelingChoiceScreen parent;
    private final String choiceId;
    private final boolean isAsi;
    private SubState subState;

    // DIRECT state
    private final List<String> directOptions;
    private String directSelection = "";
    private Button confirmBtn;

    // ASI state
    private final List<String> availableStats = new ArrayList<>();
    private final List<String> selectedStats  = new ArrayList<>();
    private FeatRegistry.FeatDef selectedFeat = null;
    private List<FeatRegistry.FeatDef> eligibleFeats;

    // Feat-Scroll
    private int featScroll    = 0;
    private int hoveredFeat   = -1;
    private static final int FEAT_ROW_H = 16;
    private static final int VISIBLE_FEATS = 10;

    // Box-Koordinaten (für render)
    private int bx, by, bw, bh;

    public GenericChoicePopup(LevelingChoiceScreen parent, String choiceId) {
        super(Component.literal("Make a choice"));
        this.parent   = parent;
        this.choiceId = choiceId;
        this.isAsi    = ASI_ID.equalsIgnoreCase(choiceId);
        this.directOptions = isAsi ? List.of() : ChoiceRegistry.getOptions(choiceId);
        this.subState = isAsi ? SubState.MODE_SELECT : SubState.DIRECT;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INIT
    // ══════════════════════════════════════════════════════════════════

    @Override
    protected void init() {
        super.init();
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();

        Player player = Minecraft.getInstance().player;

        switch (subState) {

            // ── DIRECT (alle nicht-ASI-Choices) ──────────────────────
            case DIRECT -> {
                int spacing = 22;
                bw = 160; bh = directOptions.size() * spacing + 70;
                bx = width / 2 - bw / 2; by = (height - bh) / 2;
                int startY = by + 22;

                for (String opt : directOptions) {
                    addRenderableWidget(Button.builder(Component.literal(opt), b -> {
                        directSelection = opt;
                        confirmBtn.active = true;
                    }).bounds(width / 2 - 70, startY + directOptions.indexOf(opt) * spacing, 140, 18).build());
                }

                int footerY = by + bh - 26;
                addRenderableWidget(Button.builder(Component.literal("Back"),
                                b -> minecraft.setScreen(parent))
                        .bounds(width / 2 - 75, footerY, 68, 18).build());

                confirmBtn = addRenderableWidget(Button.builder(Component.literal("Confirm"), b -> {
                    ExecuteChoicePacket.send(new ExecuteChoicePacket(choiceId, directSelection));
                    parent.removeChoiceLocally(choiceId);
                    applyOptimisticUpdate(directSelection);
                    minecraft.setScreen(parent);
                }).bounds(width / 2 + 7, footerY, 68, 18).build());
                confirmBtn.active = false;
            }

            // ── MODE_SELECT ───────────────────────────────────────────
            case MODE_SELECT -> {
                bw = 200; bh = 110;
                bx = width / 2 - bw / 2; by = height / 2 - bh / 2;

                addRenderableWidget(Button.builder(
                        Component.literal("+2 to one Ability Score"), b -> {
                            buildAvailableStats();
                            subState = SubState.STAT_SINGLE;
                            rebuildWidgets();
                        }).bounds(bx + 10, by + 24, bw - 20, 18).build());

                addRenderableWidget(Button.builder(
                        Component.literal("+1 to two Ability Scores"), b -> {
                            buildAvailableStats();
                            subState = SubState.STAT_DUAL;
                            rebuildWidgets();
                        }).bounds(bx + 10, by + 46, bw - 20, 18).build());

                addRenderableWidget(Button.builder(
                        Component.literal("Take a Feat"), b -> {
                            if (player != null) eligibleFeats = FeatRegistry.getEligibleFeats(player);
                            else eligibleFeats = FeatRegistry.getAllGeneralFeats();
                            featScroll = 0; hoveredFeat = -1;
                            subState = SubState.FEAT_SELECT;
                            rebuildWidgets();
                        }).bounds(bx + 10, by + 68, bw - 20, 18).build());

                addRenderableWidget(Button.builder(Component.literal("Back"),
                                b -> minecraft.setScreen(parent))
                        .bounds(bx + 10, by + bh - 22, bw - 20, 16).build());
            }

            // ── STAT_SINGLE ───────────────────────────────────────────
            case STAT_SINGLE -> {
                bw = 180; bh = availableStats.size() * 20 + 60;
                bx = width / 2 - bw / 2; by = height / 2 - bh / 2;

                for (int i = 0; i < availableStats.size(); i++) {
                    String stat = availableStats.get(i);
                    int base = player != null
                            ? AttributeHandler.getBaseAttribute(player, stat) : 10;
                    String label = stat + "  (" + base + " → " + Math.min(20, base + 2) + ")";
                    addRenderableWidget(Button.builder(Component.literal(label), b -> {
                        ExecuteChoicePacket.send(new ExecuteChoicePacket(choiceId, stat + " +2"));
                        parent.removeChoiceLocally(choiceId);
                        applyOptimisticUpdate(stat + " +2");
                        minecraft.setScreen(parent);
                    }).bounds(bx + 10, by + 22 + availableStats.indexOf(stat) * 20, bw - 20, 16).build());
                }

                addRenderableWidget(Button.builder(Component.literal("← Back"), b -> {
                    subState = SubState.MODE_SELECT; rebuildWidgets();
                }).bounds(bx + 10, by + bh - 22, bw - 20, 16).build());
            }

            // ── STAT_DUAL ─────────────────────────────────────────────
            case STAT_DUAL -> {
                bw = 200; bh = availableStats.size() * 20 + 80;
                bx = width / 2 - bw / 2; by = height / 2 - bh / 2;

                for (int i = 0; i < availableStats.size(); i++) {
                    String stat = availableStats.get(i);
                    int base = player != null
                            ? AttributeHandler.getBaseAttribute(player, stat) : 10;
                    String tick = selectedStats.contains(stat) ? "✔ " : "   ";
                    String label = tick + stat + "  (" + base + " → " + Math.min(20, base + 1) + ")";
                    addRenderableWidget(Button.builder(Component.literal(label), b -> {
                        if (selectedStats.contains(stat)) { selectedStats.remove(stat); }
                        else if (selectedStats.size() < 2) { selectedStats.add(stat); }
                        rebuildWidgets();
                    }).bounds(bx + 10, by + 22 + i * 20, bw - 20, 16).build());
                }

                int footerY = by + bh - 40;
                addRenderableWidget(Button.builder(Component.literal("← Back"), b -> {
                    selectedStats.clear();
                    subState = SubState.MODE_SELECT; rebuildWidgets();
                }).bounds(bx + 10, footerY, (bw - 24) / 2, 16).build());

                Button confirm = addRenderableWidget(Button.builder(Component.literal("Confirm"), b -> {
                    String val = selectedStats.get(0) + " +1," + selectedStats.get(1) + " +1";
                    ExecuteChoicePacket.send(new ExecuteChoicePacket(choiceId, val));
                    parent.removeChoiceLocally(choiceId);
                    applyOptimisticUpdate(val);
                    minecraft.setScreen(parent);
                }).bounds(bx + 14 + (bw - 24) / 2, footerY, (bw - 24) / 2, 16).build());
                confirm.active = selectedStats.size() == 2;
            }

            // ── FEAT_SELECT ───────────────────────────────────────────
            case FEAT_SELECT -> {
                bw = 320; bh = VISIBLE_FEATS * FEAT_ROW_H + 70;
                bx = width / 2 - bw / 2; by = height / 2 - bh / 2;

                int footerY = by + bh - 26;
                addRenderableWidget(Button.builder(Component.literal("← Back"), b -> {
                    selectedFeat = null;
                    subState = SubState.MODE_SELECT; rebuildWidgets();
                }).bounds(bx + 10, footerY, 90, 16).build());

                confirmBtn = addRenderableWidget(Button.builder(Component.literal("Take Feat"), b -> {
                    if (selectedFeat == null) return;
                    String val = "Feat: " + selectedFeat.id();
                    ExecuteChoicePacket.send(new ExecuteChoicePacket(choiceId, val));
                    parent.removeChoiceLocally(choiceId);
                    applyOptimisticUpdate(val);
                    minecraft.setScreen(parent);
                }).bounds(bx + bw - 100, footerY, 90, 16).build());
                confirmBtn.active = false;
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDER
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Hintergrund
        if (parent != null) { parent.render(g, -1, -1, partial); }
        g.fill(0, 0, width, height, generalConfigs.COLOR_SCREEN_OVERLAY);

        // Box
        g.fill(bx, by, bx + bw, by + bh, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, bx, by, bw, bh);

        // Titel
        String title = switch (subState) {
            case DIRECT        -> choiceId.replace("_", " ");
            case MODE_SELECT   -> "Ability Score Improvement or Feat";
            case STAT_SINGLE   -> "Choose one stat (+2)";
            case STAT_DUAL     -> "Choose two stats (+1 each)";
            case FEAT_SELECT   -> "Choose a Feat (" + (eligibleFeats != null ? eligibleFeats.size() : 0) + " available)";
        };
        g.drawCenteredString(font, title, width / 2, by + 6, generalConfigs.COLOR_ACCENT_GOLD);

        // Feat-Liste (custom rendering, nicht als Buttons)
        if (subState == SubState.FEAT_SELECT && eligibleFeats != null) {
            int listTop = by + 22;
            int listBot = by + bh - 32;
            g.enableScissor(bx + 1, listTop, bx + bw - 1, listBot);

            hoveredFeat = -1;
            for (int i = 0; i < eligibleFeats.size(); i++) {
                int rowIdx = i - featScroll;
                if (rowIdx < 0 || rowIdx >= VISIBLE_FEATS) continue;
                FeatRegistry.FeatDef feat = eligibleFeats.get(i);
                int ry = listTop + rowIdx * FEAT_ROW_H;

                boolean hov = mouseX >= bx + 4 && mouseX < bx + bw - 4
                        && mouseY >= ry && mouseY < ry + FEAT_ROW_H;
                boolean sel = feat.equals(selectedFeat);

                if (sel) g.fill(bx + 4, ry, bx + bw - 4, ry + FEAT_ROW_H, 0x55_00BB44);
                else if (hov) g.fill(bx + 4, ry, bx + bw - 4, ry + FEAT_ROW_H, generalConfigs.COLOR_HOVER_BG);

                if (hov) hoveredFeat = i;

                int col = sel  ? generalConfigs.COLOR_ACCENT_GOLD
                        : hov  ? generalConfigs.TEXT_HOVER
                          : generalConfigs.TEXT_WHITE;

                // Status-Dot: grün = DONE, orange = PARTIAL, grau = TODO
                int dot = switch (feat.status()) {
                    case DONE    -> 0xFF00FF00;
                    case PARTIAL -> 0xFFFFAA00;
                    case TODO    -> 0xFF888888;
                };
                g.fill(bx + 6, ry + 5, bx + 12, ry + 11, dot);

                g.drawString(font, feat.displayName(), bx + 16, ry + 4, col, false);

                // Kurzbeschreibung rechts (geclippt)
                String desc = feat.description();
                int descX = bx + 120;
                if (descX < bx + bw - 10)
                    g.drawString(font, desc, descX, ry + 4, generalConfigs.TEXT_GRAY, false);
            }
            g.disableScissor();

            // Scrollbar-Indikator
            if (eligibleFeats.size() > VISIBLE_FEATS) {
                int trackH = FEAT_ROW_H * VISIBLE_FEATS;
                int thumbH = Math.max(10, trackH * VISIBLE_FEATS / eligibleFeats.size());
                int thumbY = listTop + featScroll * (trackH - thumbH) / (eligibleFeats.size() - VISIBLE_FEATS);
                g.fill(bx + bw - 5, listTop, bx + bw - 2, listTop + trackH, 0x44FFFFFF);
                g.fill(bx + bw - 5, thumbY, bx + bw - 2, thumbY + thumbH, 0xAAFFFFFF);
            }

            // Hover-Tooltip unter der Liste
            if (hoveredFeat >= 0 && hoveredFeat < eligibleFeats.size()) {
                FeatRegistry.FeatDef hf = eligibleFeats.get(hoveredFeat);
                String statusLabel = switch (hf.status()) {
                    case DONE    -> "§a[Implemented]";
                    case PARTIAL -> "§6[Partial]";
                    case TODO    -> "§7[Not yet implemented]";
                };
                g.drawCenteredString(font, hf.germanName() + " — " + statusLabel,
                        width / 2, listBot - 10, generalConfigs.TEXT_GRAY);
            }

            // STAT_DUAL: zeige gewählte Stats
        } else if (subState == SubState.STAT_DUAL && !selectedStats.isEmpty()) {
            String info = "Selected: " + String.join(" + ", selectedStats)
                    + (selectedStats.size() == 1 ? "  (pick one more)" : "");
            g.drawCenteredString(font, info, width / 2, by + bh - 52, generalConfigs.TEXT_GRAY);
        }

        super.render(g, mouseX, mouseY, partial);
    }

    // ══════════════════════════════════════════════════════════════════
    //  INPUT
    // ══════════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (subState == SubState.FEAT_SELECT && btn == 0 && hoveredFeat >= 0
                && eligibleFeats != null && hoveredFeat < eligibleFeats.size()) {
            selectedFeat = eligibleFeats.get(hoveredFeat);
            if (confirmBtn != null) confirmBtn.active = true;
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (subState == SubState.FEAT_SELECT && eligibleFeats != null) {
            int max = Math.max(0, eligibleFeats.size() - VISIBLE_FEATS);
            featScroll = (int) Math.max(0, Math.min(max, featScroll - sy));
            return true;
        }
        return super.mouseScrolled(mx, my, sx, sy);
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════

    private void buildAvailableStats() {
        availableStats.clear();
        Player player = Minecraft.getInstance().player;
        for (String stat : STATS) {
            int base = player != null ? AttributeHandler.getBaseAttribute(player, stat) : 10;
            if (base < 20) availableStats.add(stat);
        }
    }

    /**
     * Optimistisches Client-Update nach dem Senden des Packets.
     * Verhindert, dass ein zweiter METAMAGIC- oder ASI-Picker im selben
     * LevelingChoiceScreen-Durchlauf noch den alten Stand sieht.
     */
    private void applyOptimisticUpdate(String value) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        if (ASI_ID.equalsIgnoreCase(choiceId)) {
            if (value.startsWith("Feat: ")) {
                // Feat-Marker optimistisch setzen
                String featId = value.substring("Feat: ".length());
                String marker = "FEAT_" + featId;
                if (vars.Feats == null || vars.Feats.isBlank()) vars.Feats = marker;
                else if (!vars.Feats.contains(marker)) vars.Feats += "," + marker;
            } else {
                // Stat-Boni optimistisch anwenden
                for (String part : value.split(",")) {
                    String[] s = part.trim().split(" \\+");
                    if (s.length != 2) continue;
                    String stat = s[0].trim();
                    int amount = Integer.parseInt(s[1].trim());
                    bumpLocal(vars, stat, amount);
                }
            }
        } else if ("METAMAGIC".equalsIgnoreCase(choiceId)) {
            // Semikolon-Trenner (METAMAGIC_chosen darf kein Komma enthalten —
            // AbilityData nutzt Komma als Top-Level-Trenner zwischen Keys)
            String existing = AbilityDataUtils.get(vars, "METAMAGIC_chosen", "");
            AbilityDataUtils.set(vars, "METAMAGIC_chosen",
                    existing.isBlank() ? value : existing + ";" + value);
        }
    }

    private static void bumpLocal(DndModVariables.PlayerVariables vars, String stat, int amount) {
        switch (stat) {
            case "Strength"     -> vars.Strength     = Math.min(20, vars.Strength + amount);
            case "Dexterity"    -> vars.Dexterity    = Math.min(20, vars.Dexterity + amount);
            case "Constitution" -> vars.Constitution = Math.min(20, vars.Constitution + amount);
            case "Intelligence" -> vars.Intelligence = Math.min(20, vars.Intelligence + amount);
            case "Wisdom"       -> vars.Wisdom       = Math.min(20, vars.Wisdom + amount);
            case "Charisma"     -> vars.Charisma     = Math.min(20, vars.Charisma + amount);
        }
    }
}