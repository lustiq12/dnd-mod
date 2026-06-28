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

    // ── Spell / Ability Wheel — Basisfarben ───────────────────────────
    public static final int WHEEL_SEGMENT_IDLE  = 0xAA222233;
    public static final int WHEEL_SEGMENT_HOVER = 0xCC334466;
    public static final int WHEEL_SEGMENT_SEL   = 0xEE444455;
    public static final int WHEEL_OUTLINE       = 0xFF111111;
    public static final int WHEEL_HUB           = 0xFF1A1A2E;
    public static final int WHEEL_CANTRIP       = 0xAAFFAA00;
    public static final int WHEEL_CANTRIP_HOVER = 0xCCFFCC33;

    // ── Wheel — Zustandsfarben ────────────────────────────────────────
    /** Segment ist verbraucht (keine Ladungen). */
    public static final int WHEEL_SEGMENT_DEPLETED     = 0xAA221111;
    public static final int WHEEL_SEGMENT_DEPL_HOVER   = 0xCC331111;
    /** Segment ist gesperrt (nicht genug Ressource). */
    public static final int WHEEL_SEGMENT_LOCKED       = 0xAA332211;
    public static final int WHEEL_SEGMENT_LOCKED_HOVER = 0xCC443322;
    /** Segment ist level-gesperrt. */
    public static final int WHEEL_SEGMENT_LEVEL        = 0xAA222211;
    public static final int WHEEL_SEGMENT_LEVEL_HOVER  = 0xCC444422;

    // ── Wheel — Ressourcen-Poolfarben ─────────────────────────────────
    /** Focus-Points-Blau (Monk). */
    public static final int WHEEL_FP_IDLE  = 0xAA1122AA;
    public static final int WHEEL_FP_HOVER = 0xCC2244CC;
    /** Sorcery-Points-Lila (Sorcerer). */
    public static final int WHEEL_SP_IDLE  = 0xAA441177;
    public static final int WHEEL_SP_HOVER = 0xCC6622AA;

    // ── Text ──────────────────────────────────────────────────────────
    public static final int TEXT_WHITE        = 0xFFFFFFFF;
    public static final int TEXT_GRAY         = 0xFFAAAAAA;
    public static final int TEXT_DARK_GRAY    = 0xFF888888;
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