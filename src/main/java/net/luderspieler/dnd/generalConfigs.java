package net.luderspieler.dnd;

import net.minecraft.client.gui.GuiGraphics;

public class generalConfigs {

    // ── Panels & Overlays ─────────────────────────────────────────────
    public static final int COLOR_PANEL_BG           = 0xEE0D1B2A;
    public static final int COLOR_PANEL_EDGE         = 0xFF55FF55;
    public static final int COLOR_SCREEN_OVERLAY     = 0x88000000;

    // ── Death / Rest Overlays ─────────────────────────────────────────
    public static final int COLOR_DEATH_OVERLAY_TOP    = 0xD0101010;
    public static final int COLOR_DEATH_OVERLAY_BOTTOM = 0xE0101010;

    // ── Status Colors ─────────────────────────────────────────────────
    public static final int COLOR_STATUS_WIP     = 0xFFFFA500;
    public static final int COLOR_STATUS_SUCCESS = 0xFF55FF55;
    public static final int COLOR_STATUS_DANGER  = 0xFFFF5555;
    public static final int COLOR_DANGER_RED     = 0xFFFF5555;

    // ── Row Backgrounds ───────────────────────────────────────────────
    public static final int COLOR_ROW_PREPARED = 0x33FFFFFF;
    public static final int COLOR_ROW_DANGER   = 0x44FF4444;
    public static final int COLOR_ROW_FULL     = 0x44FF5555;
    public static final int COLOR_HOVER_BG     = 0x3355FF55;

    // ── Spell / Ability Wheel — uniform plain-gray segments ────────────
    private static final int WHEEL_SEGMENT_PLAIN = 0x30808080;

    public static final int WHEEL_SEGMENT_IDLE  = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_HOVER = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_OUTLINE       = 0xFF4A4A4A;
    public static final int WHEEL_HUB           = 0xFF1A1A2E;
    public static final int WHEEL_CANTRIP       = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_CANTRIP_HOVER = WHEEL_SEGMENT_PLAIN;

    // ── Wheel — state colors (fill is always WHEEL_SEGMENT_PLAIN) ──────
    public static final int WHEEL_SEGMENT_DEPLETED     = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_DEPL_HOVER   = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_LOCKED       = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_LOCKED_HOVER = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_LEVEL        = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SEGMENT_LEVEL_HOVER  = WHEEL_SEGMENT_PLAIN;

    // ── Wheel — resource pool colors (fill is always WHEEL_SEGMENT_PLAIN) ──
    public static final int WHEEL_FP_IDLE  = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_FP_HOVER = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SP_IDLE  = WHEEL_SEGMENT_PLAIN;
    public static final int WHEEL_SP_HOVER = WHEEL_SEGMENT_PLAIN;

    // ── Text ──────────────────────────────────────────────────────────
    public static final int TEXT_WHITE        = 0xFFFFFFFF;
    public static final int TEXT_GRAY         = 0xFFAAAAAA;
    public static final int TEXT_DARK_GRAY    = 0xFF888888;
    public static final int TEXT_SLOT_DEPLETED  = 0xFFB08080;
    public static final int TEXT_HOVER        = 0xFFFFFF55;
    public static final int COLOR_ACCENT_GOLD = 0xFFFFD700;
    public static final int COLOR_TEXT_SHADOW = 0xFF000000;

    // ── HUD ───────────────────────────────────────────────────────────
    public static final int HUD_BACKGROUND       = 0x77000000;
    public static final int HUD_PIP_EMPTY        = 0x55FFFFFF;
    public static final int HUD_BAR_BACKGROUND   = 0x44FFFFFF;
    public static final int HUD_BAR_BORDER_LIGHT = 0x66FFFFFF;
    public static final int HUD_BAR_BORDER_DARK  = 0x33FFFFFF;

    // ── Helper ────────────────────────────────────────────────────────
    public static void renderGreenEdge(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x,         y,         x + w,     y + 1,     COLOR_PANEL_EDGE);
        g.fill(x,         y + h - 1, x + w,     y + h,     COLOR_PANEL_EDGE);
        g.fill(x,         y,         x + 1,     y + h,     COLOR_PANEL_EDGE);
        g.fill(x + w - 1, y,         x + w,     y + h,     COLOR_PANEL_EDGE);
    }
}