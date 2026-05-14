package net.luderspieler.dnd.classes.choices;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class LevelingChoiceScreen extends Screen {
    private final Screen parent;
    public final List<String> choices = new ArrayList<>();
    private int hoveredIdx = -1;
    private boolean initialized = false;

    public LevelingChoiceScreen(Screen parent) {
        super(Component.literal("Level Up Choices"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (!initialized) {
            var vars = Minecraft.getInstance().player.getData(DndModVariables.PLAYER_VARIABLES);
            this.choices.clear();
            if (vars.ChoicesNeeded != null && !vars.ChoicesNeeded.isBlank()) {
                for (String s : vars.ChoicesNeeded.split(",")) {
                    if (!s.isBlank()) this.choices.add(s.trim());
                }
            }
            initialized = true;
        }
    }

    public void removeChoiceLocally(String id) {
        this.choices.remove(id);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Parent im Hintergrund zeichnen
        if (this.parent != null) {
            this.parent.render(g, -1, -1, partial);
            g.fill(0, 0, this.width, this.height, 0x88000000); // Einfache Abdunkelung
        }

        // Dynamische Box-Größe berechnen
        int boxWidth = 180;
        int rowH = 18;
        int boxHeight = (choices.size() * rowH) + 30;
        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2;

        g.fill(x, y, x + boxWidth, y + boxHeight, 0xEE0D1B2A);
        g.drawCenteredString(this.font, "Ausstehende Wahlen", x + boxWidth / 2, y + 8, 0xFFFFD700);

        hoveredIdx = -1;
        for (int i = 0; i < choices.size(); i++) {
            int rowY = y + 25 + (i * rowH);
            boolean hov = mouseX >= x && mouseX <= x + boxWidth && mouseY >= rowY && mouseY < rowY + rowH;
            if (hov) {
                hoveredIdx = i;
                g.fill(x + 2, rowY, x + boxWidth - 2, rowY + rowH, 0x33FFFFFF);
            }
            g.drawString(this.font, choices.get(i).replace("_", " "), x + 10, rowY + 4, hov ? 0xFF53D8FB : 0xFFFFFFFF);
        }
        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (hoveredIdx >= 0 && hoveredIdx < choices.size()) {
            Minecraft.getInstance().setScreen(new GenericChoicePopup(this, choices.get(hoveredIdx)));
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }
}