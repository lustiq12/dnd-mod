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

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
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

        // ── SUBRACE BUTTONS (Rechte Spalte) ──
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

        // ── BACK BUTTON ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceListScreen(this.isNewCharacter))
        ).bounds(leftPos + imageWidth - 70, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.renderBackground(g, mouseX, mouseY, partial);

        int leftPos = (this.width - imageWidth) / 2;
        int topPos = (this.height - imageHeight) / 2;

        // Hintergrund-Textur
        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // Icon & Name
        g.blit(RenderPipelines.GUI_TEXTURED, race.getIcon(), leftPos + 10, topPos + 14, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        g.drawString(this.font, race.getDisplayName(), leftPos + 50, topPos + 22, generalConfigs.TEXT_WHITE);

        // Subrace Header
        g.drawString(this.font, "Choose a Subrace:", leftPos + 220, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);

        // Attributes Sektion
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

        // Traits Sektion
        attrY += 6;
        g.drawString(this.font, "Traits:", leftPos + 10, attrY, generalConfigs.COLOR_ACCENT_GOLD);
        attrY += 12;
        g.drawWordWrap(this.font, Component.literal(race.getDescription()), leftPos + 10, attrY, 180, generalConfigs.TEXT_GRAY);
    }

    // Formatierung für D&D Integer-Werte
    private String formatVal(String key, int val) {
        return String.valueOf(val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}