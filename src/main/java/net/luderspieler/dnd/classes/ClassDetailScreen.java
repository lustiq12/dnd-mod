package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.classes.RaceDefinition;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassDetailScreen extends Screen {

    private static final int ICON_SIZE    = 32;
    private static final int LEFT_MARGIN  = 20;
    private static final int RIGHT_X      = 220;
    private static final int COL_WIDTH    = 180;

    private final ClassDefinition cls;
    private final boolean isNewCharacter;

    public ClassDetailScreen(ClassDefinition cls, boolean isNewCharacter) {
        super(Component.literal(cls.getDisplayName()));
        this.cls = cls;
        this.isNewCharacter = isNewCharacter;
    }

    @Override
    protected void init() {
        super.init();

        // ── CHOOSE ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Choose"),
                btn -> {
                    CharacterCreationState.selectedClassId = cls.getId();
                    this.minecraft.setScreen(new CharacterFinalizationScreen(this.isNewCharacter));
                }
        ).bounds(this.width / 2 - 65, this.height - 30, 60, 20).build());

        // ── BACK ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter))
        ).bounds(this.width / 2 + 5, this.height - 30, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);
        super.render(g, mouseX, mouseY, partial);

        // ── ICON ──
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                cls.getIcon(),
                LEFT_MARGIN, 14,
                0, 0,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE
        );

        // ── NAME ──
        g.drawString(this.font, cls.getDisplayName(), LEFT_MARGIN + ICON_SIZE + 8, 22, -1);

        // ── DESCRIPTION ──
        g.drawWordWrap(this.font,
                Component.literal(cls.getDescription()),
                LEFT_MARGIN, 52, COL_WIDTH, 0xAAAAAA);

        // ── COMBINED STATS PREVIEW (race + class) ──
        RaceDefinition race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        Map<String, Double> combined = combinedAttrs(race, cls);

        int attrY = 80;
        g.drawString(this.font, "Total Stats (Race + Class):", LEFT_MARGIN, attrY, -1);
        attrY += 12;
        for (Map.Entry<String, Double> e : combined.entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            String line = e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue());
            g.drawString(this.font, line, LEFT_MARGIN, attrY, -1);
            attrY += 10;
        }

        // ── CLASS ABILITIES (right column) ──
        int abY = 30;
        g.drawString(this.font, "Class Features:", RIGHT_X, abY, -1);
        abY += 14;
        for (String line : cls.getAbilityLines()) {
            g.drawWordWrap(this.font, Component.literal("• " + line), RIGHT_X, abY, COL_WIDTH, -1);
            abY += 20;
        }
    }

    /** Add race attrs + class attrs together into one map */
    private Map<String, Double> combinedAttrs(RaceDefinition race, ClassDefinition cls) {
        Map<String, Double> result = new LinkedHashMap<>();
        // seed with class keys in order
        for (Map.Entry<String, Double> e : cls.getAttributeModifiers().entrySet())
            result.put(e.getKey(), e.getValue());
        // add race values
        if (race != null) {
            for (Map.Entry<String, Double> e : race.getAttributeModifiers().entrySet())
                result.merge(e.getKey(), e.getValue(), Double::sum);
        }
        return result;
    }

    private String formatVal(String key, double val) {
        if (key.equals("Movement Speed")) return String.format("%.0f%%", val * 100);
        if (key.equals("Attack Speed"))   return String.format("%.1f", val);
        return String.format("%.0f", val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}