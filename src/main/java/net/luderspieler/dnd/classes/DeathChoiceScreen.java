package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class DeathChoiceScreen extends Screen {

    public DeathChoiceScreen() {
        super(Component.literal("Your character has fallen..."));
    }

    @Override
    protected void init() {
        super.init();

        int cx = this.width / 2;
        int cy = this.height / 2;

        // ── CREATE NEW CHARACTER ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Create New Character"),
                btn -> this.minecraft.setScreen(new RaceListScreen(true))
        ).bounds(cx - 170, cy + 10, 130, 20).build());

        // ── KEEP EXISTING CHARACTER ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Keep Existing Character"),
                btn -> {
                    // Tell server to restore FinishedCharacterCreation = true
                    KeepCharacterPacket.send();
                    this.minecraft.player.closeContainer();
                }
        ).bounds(cx + 40, cy + 10, 130, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        super.render(g, mouseX, mouseY, partial);

        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 20, -1);
        g.drawCenteredString(this.font,
                Component.literal("Do you wish to create a new character or continue as before?"),
                this.width / 2, this.height / 2 - 6, -1);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}