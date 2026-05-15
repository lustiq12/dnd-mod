package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class RaceDetailScreen extends Screen {

    private static final int ICON_SIZE       = 32;
    private final int imageWidth = 400;
    private final int imageHeight = 230;

    private static final int SUBRACE_BTN_W   = 160;
    private static final int SUBRACE_BTN_H   = 20;
    private static final int SUBRACE_GAP     = 4;

    private final RaceDefinition race;
    private final boolean isNewCharacter;
    private final List<SubraceDefinition> subraces;

    public RaceDetailScreen(RaceDefinition race, boolean isNewCharacter) {
        super(Component.literal(race.getDisplayName()));
        this.race = race;
        this.isNewCharacter = isNewCharacter;
        this.subraces = RaceRegistry.getSubracesFor(race.getId());
    }

    @Override
    protected void init() {
        super.init();

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        int subraceStartX = leftPos + 220;
        int subraceStartY = topPos + 40;
        int count = Math.min(subraces.size(), 6);
        for (int i = 0; i < count; i++) {
            SubraceDefinition subrace = subraces.get(i);
            int by = subraceStartY + i * (SUBRACE_BTN_H + SUBRACE_GAP);
            this.addRenderableWidget(Button.builder(
                    Component.literal(subrace.getDisplayName()),
                    btn -> this.minecraft.setScreen(new SubraceDetailScreen(race, subrace, this.isNewCharacter))
            ).bounds(subraceStartX, by, SUBRACE_BTN_W, SUBRACE_BTN_H).build());
        }

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceListScreen(this.isNewCharacter))
        ).bounds(leftPos + imageWidth - 70, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // 1. Full Screen Overlay
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - imageWidth) / 2;
        int topPos = (this.height - imageHeight) / 2;

        // 2. Main Panel Background & Edge
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // Icon & Name
        g.blit(RenderPipelines.GUI_TEXTURED, race.getIcon(), leftPos + 10, topPos + 14, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        g.drawString(this.font, race.getDisplayName(), leftPos + 50, topPos + 22, generalConfigs.TEXT_WHITE);

        g.drawString(this.font, "Choose a Subrace:", leftPos + 220, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);

        int attrY = topPos + 60;
        g.drawString(this.font, "Attributes:", leftPos + 10, attrY, generalConfigs.COLOR_ACCENT_GOLD);
        attrY += 12;
        for (Map.Entry<String, Integer> e : race.getAbilityScoreIncrements().entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            String line = e.getKey() + ": " + sign + e.getValue();
            g.drawString(this.font, line, leftPos + 10, attrY, generalConfigs.TEXT_GRAY);
            attrY += 10;
        }

        attrY += 6;
        g.drawString(this.font, "Traits:", leftPos + 10, attrY, generalConfigs.COLOR_ACCENT_GOLD);
        attrY += 12;
        g.drawWordWrap(this.font, Component.literal(race.getDescription()), leftPos + 10, attrY, 180, generalConfigs.TEXT_GRAY);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}