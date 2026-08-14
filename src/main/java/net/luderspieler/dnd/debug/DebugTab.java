package net.luderspieler.dnd.debug;

import net.minecraft.client.gui.GuiGraphics;

/** Contract for a single page of the debug GUI, plugged into DebugMainScreen's tab bar. */
public interface DebugTab {

    String getTitle();

    /** Called whenever the active target snapshot changes or the tab becomes active. */
    void rebuild(DebugClientState.Snapshot snapshot, int x, int y, int w, int h);

    void render(GuiGraphics g, int mouseX, int mouseY, float partialTick);

    default boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) { return false; }
}