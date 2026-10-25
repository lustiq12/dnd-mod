package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.CharacterCreationState;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public class RaceDetailScreen extends Screen {

    private static final int ICON_SIZE     = 32;
    private static final int SUBRACE_BTN_W = 160;
    private static final int SUBRACE_BTN_H = 20;
    private static final int SUBRACE_GAP   = 4;
    private static final int MAX_WIDTH     = 400;
    private static final int MAX_HEIGHT    = 230;
    private static final int SCREEN_MARGIN = 5;

    // Panel size is clamped to the screen in updateSize()
    private int imageWidth  = MAX_WIDTH;
    private int imageHeight = MAX_HEIGHT;

    private final RaceDefinition race;
    private final boolean isNewCharacter;
    private final List<SubraceDefinition> subraces;

    public RaceDetailScreen(RaceDefinition race, boolean isNewCharacter) {
        super(Component.literal(race.getDisplayName()));
        this.race = race;
        this.isNewCharacter = isNewCharacter;
        this.subraces = RaceRegistry.getSubracesFor(race.getId());
    }

    private void updateSize() {
        this.imageWidth  = Math.min(MAX_WIDTH,  this.width  - 2 * SCREEN_MARGIN);
        this.imageHeight = Math.min(MAX_HEIGHT, this.height - 2 * SCREEN_MARGIN);
    }

    // Right column geometry: the original fixed values at full size, proportional on narrow panels
    private int rightOffset() {
        return imageWidth >= MAX_WIDTH ? 220 : imageWidth / 2 + 5;
    }

    private int rightWidth() {
        return imageWidth >= MAX_WIDTH ? SUBRACE_BTN_W : imageWidth - rightOffset() - 8;
    }

    private int leftWidth() {
        return imageWidth >= MAX_WIDTH ? 190 : rightOffset() - 10 - 8;
    }

    /** Row height of one trait line, shared by init() (button placement) and render() (text placement). */
    private int traitRowHeight(String line) {
        int wrappedLines = this.font.split(Component.literal("» " + line), rightWidth()).size();
        return Math.max(20, wrappedLines * 9 + 2);
    }

    @Override
    protected void init() {
        super.init();
        updateSize();
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos  = (this.height - this.imageHeight) / 2;

        if (race.hasSubtype() && !subraces.isEmpty()) {
            // Show lineage/ancestry buttons
            int subraceStartX = leftPos + rightOffset();
            int subraceStartY = topPos + 40;
            int count = Math.min(subraces.size(), 6);
            for (int i = 0; i < count; i++) {
                SubraceDefinition subrace = subraces.get(i);
                int by = subraceStartY + i * (SUBRACE_BTN_H + SUBRACE_GAP);
                this.addRenderableWidget(Button.builder(
                        Component.literal(subrace.getDisplayName()),
                        btn -> this.minecraft.setScreen(new SubraceDetailScreen(race, subrace, this.isNewCharacter))
                ).bounds(subraceStartX, by, rightWidth(), SUBRACE_BTN_H).build());
            }
        } else {
            // No lineage — allow direct selection
            int btnX = leftPos + rightOffset();

            int lineY = topPos + 40;
            for (String line : race.getAbilityLines()) {
                if (lineY + 10 > topPos + imageHeight - 30) break;
                lineY += traitRowHeight(line);
            }

            int btnY = lineY + 10;

            // The button must not run into the Back button at the panel bottom
            int maxBtnY = topPos + imageHeight - 28 - SUBRACE_BTN_H - 4;
            if (btnY > maxBtnY) {
                btnY = maxBtnY;
            }

            this.addRenderableWidget(Button.builder(
                    Component.literal("Choose " + race.getDisplayName()),
                    btn -> {
                        CharacterCreationState.selectedRaceId    = race.getId();
                        CharacterCreationState.selectedSubraceId = "";
                        this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter));
                    }
            ).bounds(btnX, btnY, rightWidth(), SUBRACE_BTN_H).build());
        }

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceListScreen(this.isNewCharacter))
        ).bounds(leftPos + imageWidth - 70, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - imageWidth) / 2;
        int topPos  = (this.height - imageHeight) / 2;

        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        g.blit(RenderPipelines.GUI_TEXTURED, race.getIcon(),
                leftPos + 10, topPos + 14, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        g.drawString(this.font, race.getDisplayName(), leftPos + 50, topPos + 22, generalConfigs.TEXT_WHITE);

        int rightX = leftPos + rightOffset();
        if (race.hasSubtype()) {
            g.drawString(this.font, "Choose a Lineage:", rightX, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);
        } else {
            g.drawString(this.font, "Species Traits:", rightX, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);
            // Show ability lines on the right side when there are no lineage buttons
            int lineY = topPos + 40;
            for (String line : race.getAbilityLines()) {
                if (lineY + 10 > topPos + imageHeight - 30) break;
                g.drawWordWrap(this.font, Component.literal("» " + line), rightX + 2, lineY, rightWidth(), generalConfigs.TEXT_WHITE);
                lineY += traitRowHeight(line);
            }
        }

        int attrY = topPos + 60;
        g.drawString(this.font, "Attributes:", leftPos + 10, attrY, generalConfigs.COLOR_ACCENT_GOLD);
        attrY += 12;
        for (Map.Entry<String, Integer> e : race.getAbilityScoreIncrements().entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            g.drawString(this.font, e.getKey() + ": " + sign + e.getValue(), leftPos + 10, attrY, generalConfigs.TEXT_GRAY);
            attrY += 10;
        }

        attrY += 6;
        g.drawString(this.font, "Traits:", leftPos + 10, attrY, generalConfigs.COLOR_ACCENT_GOLD);
        attrY += 12;
        g.drawWordWrap(this.font, Component.literal(race.getDescription()), leftPos + 10, attrY, leftWidth(), generalConfigs.TEXT_GRAY);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}