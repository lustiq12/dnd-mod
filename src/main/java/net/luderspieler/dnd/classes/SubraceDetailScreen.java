package net.luderspieler.dnd.classes;

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
        // Hintergrund-Overlay
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // Hintergrund-Textur
        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // ── ICON (Anker: leftPos + 10) ──
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                subrace.getIcon(),
                leftPos + 10, topPos + 12,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // ── NAME ──
        g.drawString(this.font, subrace.getDisplayName(), leftPos + 50, topPos + 18, -1);

        // ── PARENT RACE (unter dem Namen) ──
        g.drawString(this.font, "(" + race.getDisplayName() + ")", leftPos + 50, topPos + 30, 0x888888);

        // ── DESCRIPTION (Linke Spalte) ──
        g.drawWordWrap(this.font,
                Component.literal(subrace.getDescription()),
                leftPos + 10, topPos + 52, COL_WIDTH, 0xAAAAAA);

        // ── ABILITIES (Rechte Spalte oder tiefer, hier analog zum ClassScreen rechts) ──
        int rightColX = leftPos + 210;
        int y = topPos + 30;
        g.drawString(this.font, "Subrace Traits:", rightColX, y, -1);
        y += 14;
        for (String line : subrace.getAbilityLines()) {
            g.drawWordWrap(this.font,
                    Component.literal("» " + line),
                    rightColX, y, COL_WIDTH, -1);
            y += 20;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}