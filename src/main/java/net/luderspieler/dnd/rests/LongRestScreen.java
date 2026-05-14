package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellPrepScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LongRestScreen extends Screen {

    public LongRestScreen() {
        super(Component.literal("Long Rest Preparation"));
    }

    @Override
    protected void init() {
        super.init();

        int cx = this.width / 2;
        int cy = this.height / 2;

        // 1. Variablen vom Spieler abrufen
        if (this.minecraft.player != null) {
            var vars = this.minecraft.player.getData(DndModVariables.PLAYER_VARIABLES);

            // 2. Nur hinzufügen, wenn CanUseMagic wahr ist
            if (vars.CanUseMagic) {
                // ── CHANGE SPELLS ──
                this.addRenderableWidget(Button.builder(
                        Component.literal("Change Spells"),
                        btn -> {
                            this.minecraft.setScreen(new SpellPrepScreen(this));
                        }
                ).bounds(cx - 75, cy - 10, 150, 20).build());
            }
        }

        // ── FINISH / SLEEP (Immer da) ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Finish & Sleep"),
                btn -> {
                    super.onClose();
                }
        ).bounds(cx - 75, this.height - 40, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // 1. Hintergrund-Overlay (Nutzt die zentralen Gradients)
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        super.render(g, mouseX, mouseY, partial);

        // 2. Titel (Zentriert in Gold statt Weiß)
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40,
                generalConfigs.COLOR_ACCENT_GOLD);

        // 3. Beschreibungstext (In Grau für eine saubere Hierarchie)
        g.drawCenteredString(this.font,
                Component.literal("Manage what you did over the long rest you just finished"),
                this.width / 2, this.height / 2 - 26,
                generalConfigs.TEXT_GRAY);
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}