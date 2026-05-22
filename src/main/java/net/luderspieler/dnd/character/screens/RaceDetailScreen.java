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
    private final int imageWidth  = 400;
    private final int imageHeight = 230;

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
        int topPos  = (this.height - this.imageHeight) / 2;

        // FIX 1: Prüfen, ob AUCH wirklich Subraces in der Liste sind.
        // Falls die Registry leer ist, fallen wir automatisch auf die direkte Auswahl zurück.
        if (race.hasSubtype() && !subraces.isEmpty()) {
            // Show lineage/ancestry buttons
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
        } else {
            // No lineage — allow direct selection
            int btnX = leftPos + 220;

            // FIX 2: Dynamische Y-Positionsberechnung basierend auf der Textlänge
            int lineY = topPos + 40;
            for (String line : race.getAbilityLines()) {
                if (lineY + 10 > topPos + imageHeight - 30) break;

                // Wir simulieren den Word-Wrap aus der render()-Methode, um zu sehen, wie viele Zeilen der Text braucht
                int wrappedLines = this.font.split(Component.literal("» " + line), SUBRACE_BTN_W).size();

                // Jede Zeile im Minecraft-Font ist ca. 9 Pixel hoch.
                // Wir nehmen das Maximum aus den Standard-20-Pixeln oder der echten Text-Höhe plus kleinem Abstand.
                lineY += Math.max(20, wrappedLines * 9 + 2);
            }

            // Platziere den Button mit 10 Pixeln Abstand unter dem letzten Trait
            int btnY = lineY + 10;

            // Sicherheits-Check: Der Knopf darf nicht über das Panel-Unterteil oder in den Back-Button glitchen
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
            ).bounds(btnX, btnY, SUBRACE_BTN_W, SUBRACE_BTN_H).build());
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

        if (race.hasSubtype()) {
            g.drawString(this.font, "Choose a Lineage:", leftPos + 220, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);
        } else {
            g.drawString(this.font, "Species Traits:", leftPos + 220, topPos + 22, generalConfigs.COLOR_ACCENT_GOLD);
            // Show ability lines on the right side when there are no lineage buttons
            int lineY = topPos + 40;
            for (String line : race.getAbilityLines()) {
                if (lineY + 10 > topPos + imageHeight - 30) break;
                g.drawWordWrap(this.font, Component.literal("» " + line), leftPos + 222, lineY, SUBRACE_BTN_W, generalConfigs.TEXT_WHITE);
                lineY += 20;
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
        g.drawWordWrap(this.font, Component.literal(race.getDescription()), leftPos + 10, attrY, 190, generalConfigs.TEXT_GRAY);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}