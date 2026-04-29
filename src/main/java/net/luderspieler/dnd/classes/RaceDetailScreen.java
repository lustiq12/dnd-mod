package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

public class RaceDetailScreen extends Screen {

    private static final int ICON_SIZE       = 32;
    private static final int LEFT_MARGIN     = 20;
    private static final int RIGHT_PANEL_X   = 220;
    private static final int SUBRACE_BTN_W   = 160;
    private static final int SUBRACE_BTN_H   = 20;
    private static final int SUBRACE_GAP     = 6;

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

        // ── SUBRACE BUTTONS (right side, up to 6) ──
        int subraceStartX = RIGHT_PANEL_X;
        int subraceStartY = 60;
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
        ).bounds(LEFT_MARGIN, this.height - 30, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        super.render(g, mouseX, mouseY, partial);

        // ── ICON ──
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                race.getIcon(),
                LEFT_MARGIN, 20,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // ── NAME ──
        g.drawString(this.font, race.getDisplayName(), LEFT_MARGIN + ICON_SIZE + 8, 26, 0xFFFFFF);

        // ── DESCRIPTION ──
        g.drawWordWrap(this.font,
                Component.literal(race.getDescription()),
                LEFT_MARGIN, 62, 180, 0xAAAAAA);

        // ── ATTRIBUTES ──
        int attrY = 110;
        g.drawString(this.font, "Attributes:", LEFT_MARGIN, attrY, 0xFFD700);
        attrY += 12;
        for (Map.Entry<String, Double> e : race.getAttributeModifiers().entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            String line = e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue());
            g.drawString(this.font, line, LEFT_MARGIN, attrY, 0xFFFFFF);
            attrY += 10;
        }

        // ── RACIAL ABILITIES (left side, below attrs) ──
        attrY += 6;
        g.drawString(this.font, "Traits:", LEFT_MARGIN, attrY, 0xFFD700);
        attrY += 12;
        for (String line : race.getAbilityLines()) {
            g.drawWordWrap(this.font, Component.literal("• " + line), LEFT_MARGIN, attrY, 180, 0xCCCCCC);
            attrY += 18;
        }

        // ── SUBRACE HEADER (right side) ──
        g.drawString(this.font, "Choose a Subrace:", RIGHT_PANEL_X, 44, 0xFFD700);
    }

    /** Format movement speed as percentage, others as plain number */
    private String formatVal(String key, double val) {
        if (key.equals("Movement Speed")) return String.format("%.0f%%", val * 100);
        if (key.equals("Attack Speed"))   return String.format("+%.1f", val);
        return String.format("%.0f", val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}