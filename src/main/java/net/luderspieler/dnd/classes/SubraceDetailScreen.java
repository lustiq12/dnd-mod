package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SubraceDetailScreen extends Screen {

    private static final int ICON_SIZE   = 32;
    private static final int LEFT_MARGIN = 20;

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

        // ── CHOOSE ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Choose"),
                btn -> {
                    CharacterCreationState.selectedRaceId    = race.getId();
                    CharacterCreationState.selectedSubraceId = subrace.getId();
                    this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter));
                }
        ).bounds(this.width / 2 - 65, this.height - 30, 60, 20).build());

        // ── BACK ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceDetailScreen(race, this.isNewCharacter))
        ).bounds(this.width / 2 + 5, this.height - 30, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);
        super.render(g, mouseX, mouseY, partial);

        // ── ICON ──
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                subrace.getIcon(),
                LEFT_MARGIN, 20,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // ── NAME ──
        g.drawString(this.font, subrace.getDisplayName(), LEFT_MARGIN + ICON_SIZE + 8, 28, -1);

        // ── PARENT RACE ──
        g.drawString(this.font, "(" + race.getDisplayName() + ")", LEFT_MARGIN + ICON_SIZE + 8, 40, 0x888888);

        // ── DESCRIPTION ──
        g.drawWordWrap(this.font,
                Component.literal(subrace.getDescription()),
                LEFT_MARGIN, 65, this.width - LEFT_MARGIN * 2, 0xAAAAAA);

        // ── ABILITIES ──
        int y = 92;
        g.drawString(this.font, "Subrace Traits:", LEFT_MARGIN, y, -1);
        y += 14;
        for (String line : subrace.getAbilityLines()) {
            g.drawWordWrap(this.font,
                    Component.literal("• " + line),
                    LEFT_MARGIN, y, this.width - LEFT_MARGIN * 2, -1);
            y += 20;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}