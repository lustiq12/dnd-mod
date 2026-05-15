package net.luderspieler.dnd;

import net.minecraft.client.gui.GuiGraphics;

public class generalConfigs {
    // --- Basic Colors & Overlays ---
    public static final int COLOR_PANEL_BG = 0xEE0D1B2A; // RGB: 13, 27, 42
    public static final int COLOR_PANEL_EDGE = 0xFF55FF55; // RGB: 85, 255, 85
    public static final int COLOR_SCREEN_OVERLAY = 0x88000000; // RGB: 0, 0, 0

    // --- Death / Rest Overlays ---
    public static final int COLOR_DEATH_OVERLAY_TOP = 0xD0101010; // RGB: 16, 16, 16
    public static final int COLOR_DEATH_OVERLAY_BOTTOM = 0xE0101010; // RGB: 16, 16, 16

    // --- Status Colors ---
    public static final int COLOR_STATUS_WIP = 0xFFFFA500; // RGB: 255, 165, 0
    public static final int COLOR_STATUS_SUCCESS = 0xFF55FF55; // RGB: 85, 255, 85
    public static final int COLOR_STATUS_DANGER = 0xFFFF5555; // RGB: 255, 85, 85
    public static final int COLOR_DANGER_RED = 0xFFFF5555; // RGB: 255, 85, 85

    // --- Row Backgrounds & Wheel ---
    public static final int COLOR_ROW_PREPARED = 0x33FFFFFF; // RGB: 255, 255, 255
    public static final int COLOR_ROW_DANGER = 0x44FF4444; // RGB: 255, 68, 68
    public static final int COLOR_ROW_FULL = 0x44FF5555; // RGB: 255, 85, 85
    public static final int COLOR_HOVER_BG = 0x3355FF55; // RGB: 85, 255, 85

    public static final int WHEEL_SEGMENT_IDLE = 0xAA222222; // RGB: 34, 34, 34
    public static final int WHEEL_SEGMENT_HOVER = 0xCC333333; // RGB: 51, 51, 51
    public static final int WHEEL_SEGMENT_SEL = 0xEE444444; // RGB: 68, 68, 68
    public static final int WHEEL_OUTLINE = 0xFF111111; // RGB: 17, 17, 17
    public static final int WHEEL_HUB = 0xFF222222; // RGB: 34, 34, 34
    public static final int WHEEL_CANTRIP = 0xAAFFAA00; // RGB: 255, 170, 0
    public static final int WHEEL_CANTRIP_HOVER = 0xCCFFCC33; // RGB: 255, 204, 51

    // --- Text Colors ---
    public static final int TEXT_WHITE = 0xFFFFFFFF; // RGB: 255, 255, 255
    public static final int TEXT_GRAY = 0xFFAAAAAA; // RGB: 170, 170, 170
    public static final int TEXT_DARK_GRAY = 0xFF888888; // RGB: 136, 136, 136
    public static final int COLOR_ACCENT_GOLD = 0xFFFFD700; // RGB: 255, 215, 0
    public static final int TEXT_HOVER = 0xFFFFFF55; // RGB: 255, 255, 85
    public static final int COLOR_TEXT_SHADOW = 0xFF000000; // RGB: 0, 0, 0

    // Helper method for green border
    public static void renderGreenEdge(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, COLOR_PANEL_EDGE);
        g.fill(x, y + h - 1, x + w, y + h, COLOR_PANEL_EDGE);
        g.fill(x, y, x + 1, y + h, COLOR_PANEL_EDGE);
        g.fill(x + w - 1, y, x + w, y + h, COLOR_PANEL_EDGE);
    }
}