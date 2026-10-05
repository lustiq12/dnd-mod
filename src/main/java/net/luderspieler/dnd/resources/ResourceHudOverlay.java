package net.luderspieler.dnd.resources;

import net.luderspieler.dnd.config.DndClientConfig;
import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.List;

/**
 * Ressource-Pool HUD — untere linke Ecke.
 *
 * Layout pro Zeile (PIPS-Modus, max ≤ PIP_SWITCH_THRESHOLD):
 *   [Icon] Name  ■ ■ ■ □ □   3/5
 *
 * Layout pro Zeile (BAR-Modus oder max > PIP_SWITCH_THRESHOLD):
 *   [Icon] Name  [████░░░░]  18/50
 *
 * Alle Farben kommen aus generalConfigs (keine lokalen Farb-Konstanten mehr,
 * außer wo es um reine Alpha-Blends auf einer generalConfigs-Basisfarbe geht).
 */
public class ResourceHudOverlay {

    // ── Layout ────────────────────────────────────────────────────────
    private static final int MARGIN_LEFT          = 5;
    private static final int MARGIN_TOP           = 5;   // Über Hotbar
    private static final int ROW_HEIGHT           = 14;
    private static final int PIP_SIZE             = 7;
    private static final int PIP_GAP              = 2;
    private static final int PIP_SWITCH_THRESHOLD = 12;   // Ab > 12 → BAR
    private static final int BAR_WIDTH            = 60;
    private static final int BAR_HEIGHT           = 5;
    private static final int ICON_SIZE            = 7;
    private static final int ICON_TEXT_GAP        = 3;
    private static final int MAX_SANE_POOL        = 500;  // Sanity-Cap

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        if (!DndClientConfig.RESOURCE_OVERLAY_ENABLED.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        Player player = mc.player;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!vars.FinishedCharacterCreation) return;

        List<ResourceManager.ResourcePool> pools = ResourceManager.getActiveForDisplay(player);
        if (pools.isEmpty()) return;

        GuiGraphics g    = event.getGuiGraphics();
        Font        font = mc.font;

        int nameColWPre = 0;
        for (ResourceManager.ResourcePool pool : pools) {
            nameColWPre = Math.max(nameColWPre, font.width(pool.displayName));
        }
        nameColWPre += ICON_SIZE + ICON_TEXT_GAP + 4;
        int valueColWPre  = Math.max(BAR_WIDTH, PIP_SWITCH_THRESHOLD * (PIP_SIZE + PIP_GAP));
        int numberColWPre = font.width("999/999") + 4;
        int panelWPre     = nameColWPre + valueColWPre + numberColWPre + 4;
        int panelHPre     = pools.size() * ROW_HEIGHT + 4;

        float scale = DndClientConfig.RESOURCE_OVERLAY_SCALE.get().floatValue();
        int offsetX = DndClientConfig.RESOURCE_OVERLAY_OFFSET_X.get();
        int offsetY = DndClientConfig.RESOURCE_OVERLAY_OFFSET_Y.get();
        DndClientConfig.Anchor anchor = DndClientConfig.RESOURCE_OVERLAY_ANCHOR.get();

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int scaledPanelW = (int) (panelWPre * scale);
        int scaledPanelH = (int) (panelHPre * scale);

        int startX = switch (anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> offsetX;
            case TOP_CENTER, BOTTOM_CENTER -> (screenW - scaledPanelW) / 2 + offsetX;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenW - scaledPanelW - offsetX;
        };
        int startY = switch (anchor) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> offsetY;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> screenH - scaledPanelH - offsetY;
        };

        g.pose().pushMatrix();
        g.pose().translate(startX, startY);
        g.pose().scale(scale, scale);

        // ── Name-Spalte: Breite nach längstem aktiven Pool-Name ───────
        int nameColW = 0;
        for (ResourceManager.ResourcePool pool : pools) {
            nameColW = Math.max(nameColW, font.width(pool.displayName));
        }
        nameColW += ICON_SIZE + ICON_TEXT_GAP + 4;

        // ── Panel-Maße ────────────────────────────────────────────────
        int valueColW  = Math.max(BAR_WIDTH, PIP_SWITCH_THRESHOLD * (PIP_SIZE + PIP_GAP));
        int numberColW = font.width("999/999") + 4;
        int panelW     = nameColW + valueColW + numberColW + 4;
        int panelH     = pools.size() * ROW_HEIGHT + 4;

        g.fill(-2, -2, panelW, panelH, generalConfigs.HUD_BACKGROUND);

        for (int i = 0; i < pools.size(); i++) {
            ResourceManager.ResourcePool pool = pools.get(i);
            int rowY    = i * ROW_HEIGHT;
            int current = ResourceManager.getCurrent(player, pool);
            int max     = ResourceManager.getMaxCached(player, pool);

            if (max <= 0 || max > MAX_SANE_POOL) continue;
            current = Math.min(current, max);

            int textCol = current > 0 ? generalConfigs.TEXT_WHITE : generalConfigs.TEXT_GRAY;

            // ── Icon ──────────────────────────────────────────────────
            int iconAlpha = current > 0 ? 0xFF000000 : 0x66000000;
            int iconCol   = iconAlpha | (pool.color & 0x00FFFFFF);
            int iconX     = 0;
            int iconY     = rowY + (ROW_HEIGHT - ICON_SIZE) / 2;
            g.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, iconCol);

            // ── Name ──────────────────────────────────────────────────
            int nameX = ICON_SIZE + ICON_TEXT_GAP;
            g.drawString(font, pool.displayName, nameX, rowY + (ROW_HEIGHT - 8) / 2, textCol, false);

            // ── Wert: Pips oder Bar ────────────────────────────────────
            int valueX = nameColW;
            boolean useBar = pool.displayMode == ResourceManager.ResourcePool.DisplayMode.BAR
                    || max > PIP_SWITCH_THRESHOLD;
            if (useBar) {
                drawBar(g, valueX, rowY, current, max, pool.color);
            } else {
                drawPips(g, valueX, rowY, current, max, pool.color);
            }

            // ── Zahl rechts ───────────────────────────────────────────
            int numberX = nameColW + valueColW + 4;
            String label = current + "/" + max;
            g.drawString(font, label, numberX, rowY + (ROW_HEIGHT - 8) / 2, textCol, false);
        }

        g.pose().popMatrix();
    }

    // ── RENDER-HELPERS ─────────────────────────────────────────────────

    private static void drawPips(GuiGraphics g, int x, int y,
                                 int current, int max, int color) {
        int pipY = y + (ROW_HEIGHT - PIP_SIZE) / 2;
        for (int i = 0; i < max; i++) {
            int px = x + i * (PIP_SIZE + PIP_GAP);
            if (i < current) {
                g.fill(px, pipY, px + PIP_SIZE, pipY + PIP_SIZE, color);
                // Glanz — reiner Alpha-Highlight auf der Pool-Farbe, bleibt lokal
                g.fill(px + 1, pipY + 1, px + 4, pipY + 4, 0x22FFFFFF);
                g.fill(px + 2, pipY + 2, px + 3, pipY + 3, 0x22FFFFFF);
            } else {
                g.fill(px, pipY, px + PIP_SIZE, pipY + PIP_SIZE, generalConfigs.HUD_PIP_EMPTY);
            }
        }
    }

    private static void drawBar(GuiGraphics g, int x, int y, int current, int max, int color) {
        int barY = y + (ROW_HEIGHT - BAR_HEIGHT) / 2;

        g.fill(x, barY, x + BAR_WIDTH, barY + BAR_HEIGHT, generalConfigs.HUD_BAR_BACKGROUND);

        int fillW = max > 0 ? (int) ((current / (float) max) * BAR_WIDTH) : 0;
        if (fillW > 0) {
            g.fill(x, barY, x + fillW, barY + BAR_HEIGHT, color);
        }

        // Rahmen (oben/unten/links/rechts)
        g.fill(x,                  barY,                  x + BAR_WIDTH, barY + 1,
                generalConfigs.HUD_BAR_BORDER_LIGHT);
        g.fill(x,                  barY + BAR_HEIGHT - 1, x + BAR_WIDTH, barY + BAR_HEIGHT,
                generalConfigs.HUD_BAR_BORDER_DARK);
        g.fill(x,                  barY,                  x + 1,         barY + BAR_HEIGHT,
                generalConfigs.HUD_BAR_BORDER_LIGHT);
        g.fill(x + BAR_WIDTH - 1,  barY,                  x + BAR_WIDTH, barY + BAR_HEIGHT,
                generalConfigs.HUD_BAR_BORDER_DARK);
    }
}