package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class GenericChoicePopup extends Screen {
    private final LevelingChoiceScreen parent;
    private final String choiceId;
    private final List<String> options;
    private String selection = "";
    private Button confirmBtn;

    public GenericChoicePopup(LevelingChoiceScreen parent, String choiceId) {
        super(Component.literal("Make a choice"));
        this.parent = parent;
        this.choiceId = choiceId;
        this.options = ChoiceRegistry.getOptionsFor(choiceId);
    }

    @Override
    protected void init() {
        int btnW = 140;
        int spacing = 22;
        int totalH = (options.size() * spacing) + 80;
        int boxY = (this.height - totalH) / 2;
        int startY = boxY + 12;

        // Option Buttons
        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i);
            this.addRenderableWidget(Button.builder(Component.literal(opt), b -> {
                this.selection = opt;
                this.confirmBtn.active = true;
            }).bounds(this.width / 2 - btnW / 2, startY + (i * spacing), btnW, 20).build());
        }

        int footerY = startY + (options.size() * spacing) + 15;

        // Back Button (Links)
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 75, footerY, 70, 20).build());

        // Confirm Button (Rechts)
        this.confirmBtn = this.addRenderableWidget(Button.builder(Component.literal("Confirm"), b -> {
            ExecuteChoicePacket.send(new ExecuteChoicePacket(this.choiceId, this.selection));
            this.parent.removeChoiceLocally(this.choiceId);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 + 5, footerY, 70, 20).build());

        this.confirmBtn.active = false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // 1. Hintergrund-Rendering (Parent Screen abdunkeln)
        if (this.parent != null) {
            this.parent.render(g, -1, -1, partial);
            // Nutzt COLOR_SCREEN_OVERLAY für die Abdunklung des Hintergrunds
            g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);
        }

        // 2. Box-Berechnung
        int boxW = 160;
        int spacing = 22;
        int totalH = (options.size() * spacing) + 80;
        int boxX = this.width / 2 - boxW / 2;
        int boxY = (this.height - totalH) / 2;

        // 3. Hintergrund & Rahmen aus der Config
        g.fill(boxX, boxY, boxX + boxW, boxY + totalH, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, boxX, boxY, boxW, totalH);

        // 4. Titel der Auswahl in Gold
        g.drawCenteredString(this.font, "Select " + choiceId, this.width / 2, boxY + 12, generalConfigs.COLOR_ACCENT_GOLD);

        // 5. Anzeige der aktuellen Auswahl in Weiß (unten über den Buttons)
        int footerY = boxY + totalH - 30;
        if (!selection.isEmpty()) {
            g.drawCenteredString(this.font, "Selection: " + selection, this.width / 2, footerY - 15, generalConfigs.TEXT_WHITE);
        }

        super.render(g, mouseX, mouseY, partial);
    }
}