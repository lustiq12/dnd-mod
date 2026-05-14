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

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
    private static final int ICON_SIZE = 32;
    private final int imageWidth = 400;
    private final int imageHeight = 230;
    private final int COL_WIDTH = 180; // Breite für die Textspalten innerhalb des GUIs

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

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        int centerX = this.width / 2;

        // ── CHOOSE BUTTON ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Choose"),
                btn -> {
                    CharacterCreationState.selectedRaceId    = race.getId();
                    CharacterCreationState.selectedSubraceId = subrace.getId();
                    this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter));
                }
        ).bounds(centerX - 65, topPos + imageHeight - 28, 60, 20).build());

        // ── BACK BUTTON ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceDetailScreen(race, this.isNewCharacter))
        ).bounds(centerX + 5, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // 1. Hintergrund-Overlay (Nutzt jetzt die Death-Gradients für Konsistenz in Listen/Details)
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // 2. Hintergrund-Textur
        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // 3. ICON (Anker: leftPos + 10)
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                subrace.getIcon(),
                leftPos + 10, topPos + 12,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // 4. NAME (Weiß aus Config)
        g.drawString(this.font, subrace.getDisplayName(), leftPos + 50, topPos + 18, generalConfigs.TEXT_WHITE);

        // 5. PARENT RACE (Dunkelgrau aus Config)
        g.drawString(this.font, "(" + race.getDisplayName() + ")", leftPos + 50, topPos + 30, generalConfigs.TEXT_DARK_GRAY);

        // 6. DESCRIPTION (Linke Spalte, Grau aus Config)
        g.drawWordWrap(this.font,
                Component.literal(subrace.getDescription()),
                leftPos + 10, topPos + 52, COL_WIDTH, generalConfigs.TEXT_GRAY);

        // 7. ABILITIES (Rechte Spalte)
        int rightColX = leftPos + 210;
        int y = topPos + 30;

        // Überschrift in Gold
        g.drawString(this.font, "Subrace Traits:", rightColX, y, generalConfigs.COLOR_ACCENT_GOLD);

        y += 14;
        for (String line : subrace.getAbilityLines()) {
            // Trait-Inhalt in Weiß (oder Grau, falls dir das lieber ist)
            g.drawWordWrap(this.font,
                    Component.literal("» " + line),
                    rightColX, y, COL_WIDTH, generalConfigs.TEXT_WHITE);
            y += 20;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}