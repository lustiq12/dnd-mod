package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class RaceDetailScreen extends Screen {

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
    private static final int ICON_SIZE       = 32;
    private final int imageWidth = 400;
    private final int imageHeight = 212;

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
        // Startet nun bei 40, damit der erste Button kurz unter dem Header (bei 22) liegt
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

        // ── BACK BUTTON (Jetzt unten RECHTS im GUI-Fenster) ──
        // x = leftPos + imageWidth - Breite des Buttons (60) - kleiner Rand (10)
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new RaceListScreen(this.isNewCharacter))
        ).bounds(leftPos + imageWidth - 70, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // ── ICON ──
        g.blit(RenderPipelines.GUI_TEXTURED, race.getIcon(), leftPos + 10, topPos + 14, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        // ── NAME (Y = topPos + 22) ──
        g.drawString(this.font, race.getDisplayName(), leftPos + 50, topPos + 22, -1);

        // ── SUBRACE HEADER (Gleiche Höhe wie Name: topPos + 22) ──
        g.drawString(this.font, "Choose a Subrace:", leftPos + 220, topPos + 22, -1);

        // ── ATTRIBUTES ──
        int attrY = topPos + 60;
        g.drawString(this.font, "Attributes:", leftPos + 10, attrY, -1);
        attrY += 12;
        for (Map.Entry<String, Double> e : race.getAttributeModifiers().entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            String line = e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue());
            g.drawString(this.font, line, leftPos + 10, attrY, -1);
            attrY += 10;
        }

        // ── RACIAL ABILITIES ──
        attrY += 6;
        g.drawString(this.font, "Traits:", leftPos + 10, attrY, -1);
        attrY += 12;
        for (String line : race.getAbilityLines()) {
            g.drawWordWrap(this.font, Component.literal("• " + line), leftPos + 10, attrY, 180, -1);
            attrY += 18;
        }
    }

    private String formatVal(String key, double val) {
        if (key.equals("Movement Speed")) return String.format("%.0f%%", val * 100);
        if (key.equals("Attack Speed"))   return String.format("+%.1f", val);
        return String.format("%.0f", val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}