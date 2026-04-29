package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClassListScreen extends Screen {

    private static final int BUTTON_WIDTH  = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ICON_SIZE     = 16;
    private static final int COLS          = 4;
    private static final int H_GAP         = 10;
    private static final int V_GAP         = 10;

    private final boolean isNewCharacter;

    public ClassListScreen(boolean isNewCharacter) {
        super(Component.literal("Choose Your Class"));
        this.isNewCharacter = isNewCharacter;
    }

    @Override
    protected void init() {
        super.init();
        List<ClassDefinition> classes = ClassRegistry.CLASSES;

        // Horizontale Zentrierung
        int totalW = COLS * BUTTON_WIDTH + (COLS - 1) * H_GAP;
        int startX = (this.width - totalW) / 2;

        // Vertikale Zentrierung des gesamten Blocks
        int rows = (int) Math.ceil((double) classes.size() / COLS);
        int totalH = rows * BUTTON_HEIGHT + (rows - 1) * V_GAP;
        int startY = (this.height - totalH) / 2;

        for (int i = 0; i < classes.size(); i++) {
            ClassDefinition cls = classes.get(i);
            int col = i % COLS;
            int row = i / COLS;
            int bx = startX + col * (BUTTON_WIDTH + H_GAP);
            int by = startY + row * (BUTTON_HEIGHT + V_GAP);

            this.addRenderableWidget(Button.builder(
                    Component.literal(cls.getDisplayName()),
                    btn -> this.minecraft.setScreen(new ClassDetailScreen(cls, this.isNewCharacter))
            ).bounds(bx, by, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }

        // ── BACK Button (Zentriert am unteren Rand) ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> {
                    RaceDefinition race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
                    if (race != null)
                        this.minecraft.setScreen(new RaceDetailScreen(race, this.isNewCharacter));
                    else
                        this.minecraft.setScreen(new RaceListScreen(this.isNewCharacter));
                }
        ).bounds(this.width / 2 - 30, this.height - 30, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);

        List<ClassDefinition> classes = ClassRegistry.CLASSES;
        int totalW = COLS * BUTTON_WIDTH + (COLS - 1) * H_GAP;
        int startX = (this.width - totalW) / 2;
        int rows = (int) Math.ceil((double) classes.size() / COLS);
        int startY = (this.height - (rows * BUTTON_HEIGHT + (rows - 1) * V_GAP)) / 2;

        for (int i = 0; i < classes.size(); i++) {
            ClassDefinition cls = classes.get(i);
            int col = i % COLS;
            int row = i / COLS;
            int bx = startX + col * (BUTTON_WIDTH + H_GAP);
            int by = startY + row * (BUTTON_HEIGHT + V_GAP);
            g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(), bx + 2, by + 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
        super.render(g, mouseX, mouseY, partial);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}