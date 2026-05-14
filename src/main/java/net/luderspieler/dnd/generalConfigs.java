package net.luderspieler.dnd;

import net.minecraft.client.gui.GuiGraphics;

public class generalConfigs {
    // --- Basis Farben & Overlays ---
    public static final int COLOR_PANEL_BG = 0xEE0D1B2A;
    public static final int COLOR_PANEL_EDGE = 0xFF55FF55;
    public static final int COLOR_SCREEN_OVERLAY = 0x88000000;

    // --- Death / Rest Overlays (Diese haben gefehlt) ---
    public static final int COLOR_DEATH_OVERLAY_TOP = 0xD0101010;
    public static final int COLOR_DEATH_OVERLAY_BOTTOM = 0xE0101010;

    // --- Status Farben ---
    public static final int COLOR_STATUS_WIP = 0xFFFFA500;
    public static final int COLOR_STATUS_SUCCESS = 0xFF55FF55;
    public static final int COLOR_STATUS_DANGER = 0xFFFF5555;
    public static final int COLOR_DANGER_RED = 0xFFFF5555; // Alias für die Kompatibilität

    // --- Zeilen-Hintergründe & Wheel ---
    public static final int COLOR_ROW_PREPARED = 0x33FFFFFF;
    public static final int COLOR_ROW_DANGER = 0x44FF4444;
    public static final int COLOR_ROW_FULL = 0x44FF5555;
    public static final int COLOR_HOVER_BG = 0x3355FF55;

    public static final int WHEEL_SEGMENT_IDLE = 0xAA222222;
    public static final int WHEEL_SEGMENT_HOVER = 0xCC333333;
    public static final int WHEEL_SEGMENT_SEL = 0xEE444444;
    public static final int WHEEL_OUTLINE = 0xFF111111;
    public static final int WHEEL_HUB = 0xFF222222;
    public static final int WHEEL_CANTRIP = 0xAAFFAA00;
    public static final int WHEEL_CANTRIP_HOVER = 0xCCFFCC33;

    // --- Text Farben ---
    public static final int TEXT_WHITE = 0xFFFFFFFF;
    public static final int TEXT_GRAY = 0xFFAAAAAA;
    public static final int TEXT_DARK_GRAY = 0xFF888888;
    public static final int COLOR_ACCENT_GOLD = 0xFFFFD700;
    public static final int TEXT_HOVER = 0xFFFFFF55;
    public static final int COLOR_TEXT_SHADOW = 0xFF000000;

    // Hilfsmethode für den grünen Rahmen
    public static void renderGreenEdge(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, COLOR_PANEL_EDGE);
        g.fill(x, y + h - 1, x + w, y + h, COLOR_PANEL_EDGE);
        g.fill(x, y, x + 1, y + h, COLOR_PANEL_EDGE);
        g.fill(x + w - 1, y, x + w, y + h, COLOR_PANEL_EDGE);
    }
}