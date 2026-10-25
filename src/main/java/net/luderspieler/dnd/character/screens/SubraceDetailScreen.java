package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.CharacterCreationState;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class SubraceDetailScreen extends Screen {

    private static final int ICON_SIZE = 32;
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 230;
    private static final int SCREEN_MARGIN = 5;

    // Panel size is clamped to the screen in updateSize()
    private int imageWidth = MAX_WIDTH;
    private int imageHeight = MAX_HEIGHT;

    private final RaceDefinition race;
    private final SubraceDefinition subrace;
    private final boolean isNewCharacter;

    public SubraceDetailScreen(RaceDefinition race, SubraceDefinition subrace, boolean isNewCharacter) {
        super(Component.literal(subrace.getDisplayName()));
        this.race = race;
        this.subrace = subrace;
        this.isNewCharacter = isNewCharacter;
    }

    private void updateSize() {
        this.imageWidth  = Math.min(MAX_WIDTH,  this.width  - 2 * SCREEN_MARGIN);
        this.imageHeight = Math.min(MAX_HEIGHT, this.height - 2 * SCREEN_MARGIN);
    }

    // Two equal columns; gives the original 180 wide columns at full size
    private int colWidth() {
        return imageWidth / 2 - 20;
    }

    @Override
    protected void init() {
        super.init();
        updateSize();
        int topPos = (this.height - this.imageHeight) / 2;
        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(
                Component.literal("Choose"),
                btn -> {
                    CharacterCreationState.selectedRaceId    = race.getId();
                    CharacterCreationState.selectedSubraceId = subrace.getId();
                    this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter));
                }
        ).bounds(centerX - 65, topPos + imageHeight - 28, 60, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceDetailScreen(race, this.isNewCharacter))
        ).bounds(centerX + 5, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        g.blit(
                RenderPipelines.GUI_TEXTURED,
                subrace.getIcon(),
                leftPos + 10, topPos + 14,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        g.drawString(this.font, subrace.getDisplayName(), leftPos + 50, topPos + 22, generalConfigs.TEXT_WHITE);
        g.drawString(this.font, "(" + race.getDisplayName() + ")", leftPos + 50, topPos + 34, generalConfigs.TEXT_DARK_GRAY);

        g.drawWordWrap(this.font,
                Component.literal(subrace.getDescription()),
                leftPos + 10, topPos + 52, colWidth(), generalConfigs.TEXT_GRAY);

        int rightColX = leftPos + imageWidth / 2 + 10;
        int y = topPos + 30;

        g.drawString(this.font, "Subrace Traits:", rightColX, y, generalConfigs.COLOR_ACCENT_GOLD);

        y += 14;
        for (String line : subrace.getAbilityLines()) {
            // Stop before the Choose/Back buttons instead of drawing over them
            if (y + 10 > topPos + imageHeight - 30) break;
            Component text = Component.literal("» " + line);
            g.drawWordWrap(this.font, text, rightColX, y, colWidth(), generalConfigs.TEXT_WHITE);
            int wrappedLines = this.font.split(text, colWidth()).size();
            y += Math.max(20, wrappedLines * 9 + 2);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}