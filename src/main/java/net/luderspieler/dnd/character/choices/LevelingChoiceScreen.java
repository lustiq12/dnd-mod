package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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

        // Zentrierter Back-Button am unteren Ende der Liste
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            Minecraft.getInstance().setScreen(this.parent);
        }).bounds(this.width / 2 - 40, this.height / 2 + (choices.size() * 18) + 20, 80, 20).build());
    }

    public void removeChoiceLocally(String id) {
        this.choices.remove(id);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // 1. Hintergrund-Abdunklung
        if (this.parent != null) {
            this.parent.render(g, -1, -1, partial);
            g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);
        }

        // 2. Box-Berechnung
        int boxWidth = 180;
        int rowH = 18;
        int boxHeight = (choices.size() * rowH) + 50;
        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2;

        // 3. Hintergrund & Rahmen aus Config
        g.fill(x, y, x + boxWidth, y + boxHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, x, y, boxWidth, boxHeight);

        // 4. Titel
        g.drawCenteredString(this.font, "Pending Choices", x + boxWidth / 2, y + 8, generalConfigs.COLOR_ACCENT_GOLD);

        // 5. Auswahl-Liste
        hoveredIdx = -1;
        for (int i = 0; i < choices.size(); i++) {
            int rowY = y + 25 + (i * rowH);
            boolean hov = mouseX >= x && mouseX <= x + boxWidth && mouseY >= rowY && mouseY < rowY + rowH;

            if (hov) {
                hoveredIdx = i;
                g.fill(x + 2, rowY, x + boxWidth - 2, rowY + rowH, generalConfigs.COLOR_HOVER_BG);
            }

            // Textfarbe ändert sich bei Hover (alles über Config)
            int textColor = hov ? generalConfigs.TEXT_HOVER : generalConfigs.TEXT_WHITE;
            g.drawString(this.font, choices.get(i), x + 10, rowY + 4, textColor);
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