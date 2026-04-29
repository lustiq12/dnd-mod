package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.classes.CharacterCreationPacket;
import net.luderspieler.dnd.classes.CharacterCreationState;
import net.luderspieler.dnd.classes.ClassListScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CharacterFinalizationScreen extends Screen {

    private static final int LEFT_MARGIN = 30;
    private static final int BOX_WIDTH   = 300;

    private final boolean isNewCharacter;

    private EditBox nameBox;
    private EditBox storyBox;
    private EditBox personalityBox;

    public CharacterFinalizationScreen(boolean isNewCharacter) {
        super(Component.literal("Finalize Your Character"));
        this.isNewCharacter = isNewCharacter;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = (this.width - BOX_WIDTH) / 2;

        // ── CHARACTER NAME ──
        nameBox = new EditBox(this.font, centerX, 50, BOX_WIDTH, 20, Component.literal("Character Name"));
        nameBox.setMaxLength(32);
        nameBox.setHint(Component.literal("Enter character name..."));
        this.addRenderableWidget(nameBox);

        // ── BACKSTORY ──
        storyBox = new EditBox(this.font, centerX, 100, BOX_WIDTH, 20, Component.literal("Backstory"));
        storyBox.setMaxLength(256);
        storyBox.setHint(Component.literal("Enter your backstory..."));
        this.addRenderableWidget(storyBox);

        // ── PERSONALITY ──
        personalityBox = new EditBox(this.font, centerX, 150, BOX_WIDTH, 20, Component.literal("Personality"));
        personalityBox.setMaxLength(256);
        personalityBox.setHint(Component.literal("Describe your personality..."));
        this.addRenderableWidget(personalityBox);

        // ── CREATE CHARACTER ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Create Character"),
                btn -> createCharacter()
        ).bounds(this.width / 2 - 70, this.height - 40, 140, 20).build());

        // ── BACK ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter))
        ).bounds(LEFT_MARGIN, this.height - 40, 60, 20).build());
    }

    private void createCharacter() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        String name        = nameBox.getValue().trim();
        String story       = storyBox.getValue().trim();
        String personality = personalityBox.getValue().trim();

        if (name.isEmpty()) name = "Adventurer";

        // Send packet to server with all selections
        CharacterCreationPacket.send(
                CharacterCreationState.selectedRaceId,
                CharacterCreationState.selectedSubraceId,
                CharacterCreationState.selectedClassId,
                name,
                story,
                personality
        );

        CharacterCreationState.reset();
        this.minecraft.player.closeContainer();
        // Screen will be closed by the server response
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        super.render(g, mouseX, mouseY, partial);

        g.drawCenteredString(this.font, this.title, this.width / 2, 14, -1);

        int centerX = (this.width - BOX_WIDTH) / 2;
        g.drawString(this.font, "Character Name:", centerX, 38, -1);
        g.drawString(this.font, "Backstory:", centerX, 88, -1);
        g.drawString(this.font, "Personality:", centerX, 138, -1);

        // Summary of selections
        int sy = 185;
        g.drawString(this.font, "Summary:", centerX, sy, -1);
        g.drawString(this.font, "Species: " + CharacterCreationState.selectedRaceId + " / " + CharacterCreationState.selectedSubraceId, centerX, sy + 12, 0xAAAAAA);
        g.drawString(this.font, "Class: "   + CharacterCreationState.selectedClassId, centerX, sy + 22, 0xAAAAAA);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}