package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.character.feats.FeatRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/** Grid of every general feat with its real implementation status; click applies or removes the FEAT_x marker. */
public class DebugFeatsTab implements DebugTab {

    private static final int MIN_CHIP_W = 190;
    private static final int CHIP_H = 30;
    private static final int CHIP_GAP = 4;
    private static final int PAD_X = 8;
    private static final int PAD_Y = 8;

    private final DebugMainScreen screen;
    private int x, y, w, h;
    private int contentLeft, contentTop, contentW, contentH;

    private DndModVariables.PlayerVariables vars;
    private final List<FeatRegistry.FeatDef> feats = FeatRegistry.getAllGeneralFeats();

    private int scroll = 0;
    private int hoveredChip = -1;

    public DebugFeatsTab(DebugMainScreen screen) {
        this.screen = screen;
    }

    @Override
    public String getTitle() {
        return "Feats";
    }

    @Override
    public void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        contentLeft = x + PAD_X;
        contentTop = y + PAD_Y;
        contentW = w - PAD_X;
        contentH = h - PAD_Y;
        vars = snapshot != null ? snapshot.vars() : null;
        scroll = 0;
    }

    private int chipColumns() {
        return Math.max(1, contentW / (MIN_CHIP_W + CHIP_GAP));
    }

    private int chipWidth(int cols) {
        return (contentW - (cols - 1) * CHIP_GAP) / cols;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Font font = screen.getFontInstance();
        if (vars == null) return;

        int cols = chipColumns();
        int chipW = chipWidth(cols);
        int rows = (int) Math.ceil(feats.size() / (double) cols);
        int visibleRows = Math.max(1, contentH / (CHIP_H + CHIP_GAP));
        int maxScroll = Math.max(0, rows - visibleRows);
        scroll = Math.min(scroll, maxScroll);

        g.enableScissor(contentLeft, contentTop, contentLeft + contentW, contentTop + contentH);
        hoveredChip = -1;
        for (int i = 0; i < feats.size(); i++) {
            int row = i / cols - scroll;
            int col = i % cols;
            int cx = contentLeft + col * (chipW + CHIP_GAP);
            int cy = contentTop + row * (CHIP_H + CHIP_GAP);
            if (cy + CHIP_H < contentTop || cy > contentTop + contentH) continue;

            FeatRegistry.FeatDef feat = feats.get(i);
            boolean taken = hasFeat(feat.id());
            boolean hovered = mouseX >= cx && mouseX < cx + chipW && mouseY >= cy && mouseY < cy + CHIP_H;
            if (hovered) hoveredChip = i;

            int bg;
            if (taken) bg = hovered ? 0x77CC3333 : 0x5500BB44;
            else bg = hovered ? generalConfigs.COLOR_HOVER_BG : 0x22000000;
            g.fill(cx, cy, cx + chipW, cy + CHIP_H, bg);
            generalConfigs.renderGreenEdge(g, cx, cy, chipW, CHIP_H);

            int dot = switch (feat.status()) {
                case DONE -> 0xFF00FF00;
                case PARTIAL -> 0xFFFFAA00;
                case TODO -> 0xFF888888;
            };
            g.fill(cx + 4, cy + 4, cx + 10, cy + 10, dot);

            int nameCol = taken ? (hovered ? generalConfigs.COLOR_STATUS_DANGER : generalConfigs.COLOR_STATUS_SUCCESS)
                    : (hovered ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE);
            g.drawString(font, feat.displayName(), cx + 14, cy + 3, nameCol, false);

            String subLine = taken && hovered ? "click to remove marker (effects not undone)" : feat.description();
            int subCol = taken && hovered ? generalConfigs.COLOR_STATUS_WIP : generalConfigs.TEXT_GRAY;
            g.drawString(font, trim(font, subLine, chipW - 8), cx + 4, cy + 16, subCol, false);
        }
        g.disableScissor();

        if (rows > visibleRows) {
            int maxS = Math.max(1, rows - visibleRows);
            int thumbH = Math.max(10, contentH * visibleRows / rows);
            int thumbY = contentTop + scroll * (contentH - thumbH) / maxS;
            g.fill(x + w - 3, contentTop, x + w - 1, contentTop + contentH, 0x33FFFFFF);
            g.fill(x + w - 3, thumbY, x + w - 1, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    private boolean hasFeat(String featId) {
        if (vars.Feats == null) return false;
        return vars.Feats.contains("FEAT_" + featId);
    }

    private String trim(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text;
        while (text.length() > 1 && font.width(text + "...") > maxWidth) text = text.substring(0, text.length() - 1);
        return text + "...";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || hoveredChip < 0 || vars == null) return false;
        String uuid = screen.currentTargetUuid();
        if (uuid == null) return false;

        FeatRegistry.FeatDef feat = feats.get(hoveredChip);
        String marker = "FEAT_" + feat.id();

        if (hasFeat(feat.id())) {
            // Only removes the marker, since FeatRegistry has no unapply — mechanical effects it granted (stat bumps, AbilityData flags) stay.
            DebugListModifyPacket.send(uuid, "Feats", marker, false);
            vars.Feats = vars.Feats.replace("," + marker, "").replace(marker + ",", "").replace(marker, "");
        } else {
            DebugFeatApplyPacket.send(uuid, feat.id());
            vars.Feats = (vars.Feats == null || vars.Feats.isBlank()) ? marker : vars.Feats + "," + marker;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < contentLeft || mouseX > contentLeft + contentW || mouseY < contentTop || mouseY > contentTop + contentH) return false;
        scroll = Math.max(0, scroll - (int) Math.signum(scrollY));
        return true;
    }
}