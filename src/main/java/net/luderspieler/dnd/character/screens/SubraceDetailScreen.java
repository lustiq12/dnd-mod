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
import net.minecraft.resources.ResourceLocation;

public class SubraceDetailScreen extends Screen {

    private static final int ICON_SIZE = 32;
    private final int imageWidth = 400;
    private final int imageHeight = 230;
    private final int COL_WIDTH = 180;

    private final RaceDefinition race;
    private final SubraceDefinition subrace;
    private final boolean isNewCharacter;

    public SubraceDetailScreen(RaceDefinition race, SubraceDefinition subrace, boolean isNewCharacter) {
        super(Component.literal(subrace.getDisplayName()));
        this.race = race;
        this.subrace = subrace;
        this.isNewCharacter = isNewCharacter;
    }

    @Override
    protected void init() {
        super.init();
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
        // 1. Hintergrund-Overlay
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // 2. Main Panel Background & Edge
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // 3. ICON
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                subrace.getIcon(),
                leftPos + 10, topPos + 14,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // 4. NAME
        g.drawString(this.font, subrace.getDisplayName(), leftPos + 50, topPos + 22, generalConfigs.TEXT_WHITE);

        // 5. PARENT RACE
        g.drawString(this.font, "(" + race.getDisplayName() + ")", leftPos + 50, topPos + 34, generalConfigs.TEXT_DARK_GRAY);

        // 6. DESCRIPTION
        g.drawWordWrap(this.font,
                Component.literal(subrace.getDescription()),
                leftPos + 10, topPos + 52, COL_WIDTH, generalConfigs.TEXT_GRAY);

        // 7. ABILITIES
        int rightColX = leftPos + 210;
        int y = topPos + 30;

        g.drawString(this.font, "Subrace Traits:", rightColX, y, generalConfigs.COLOR_ACCENT_GOLD);

        y += 14;
        for (String line : subrace.getAbilityLines()) {
            g.drawWordWrap(this.font,
                    Component.literal("» " + line),
                    rightColX, y, COL_WIDTH, generalConfigs.TEXT_WHITE);
            y += 20;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}