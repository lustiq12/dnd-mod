package net.luderspieler.dnd.character.choices;

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
        super(Component.literal("Wahl treffen"));
        this.parent = parent;
        this.choiceId = choiceId;
        this.options = ChoiceRegistry.getOptionsFor(choiceId); // Daten aus Registry laden
    }

    @Override
    protected void init() {
        int btnW = 140;
        int spacing = 22;
        int totalH = (options.size() * spacing) + 40;
        int startY = (this.height - totalH) / 2 + 20;

        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i);
            this.addRenderableWidget(Button.builder(Component.literal(opt), b -> {
                this.selection = opt;
                this.confirmBtn.active = true;
            }).bounds(this.width / 2 - btnW / 2, startY + (i * spacing), btnW, 20).build());
        }

        this.confirmBtn = this.addRenderableWidget(Button.builder(Component.literal("Bestätigen"), b -> {
            ExecuteChoicePacket.send(new ExecuteChoicePacket(this.choiceId, this.selection));
            this.parent.removeChoiceLocally(this.choiceId);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 40, startY + (options.size() * spacing) + 10, 80, 20).build());

        this.confirmBtn.active = false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Rendert den LevelingChoiceScreen (der wiederum das CharacterSheet rendert)
        if (this.parent != null) {
            this.parent.render(g, -1, -1, partial);
            // Kleiner zusätzlicher Schleier, damit das Popup besser lesbar ist
            g.fill(0, 0, this.width, this.height, 0x44000000);
        }

        int boxW = 160;
        int boxH = (options.size() * 22) + 60;
        g.fill(this.width / 2 - boxW / 2, (this.height - boxH) / 2, this.width / 2 + boxW / 2, (this.height + boxH) / 2, 0xFF1C2541);

        super.render(g, mouseX, mouseY, partial);
    }
}