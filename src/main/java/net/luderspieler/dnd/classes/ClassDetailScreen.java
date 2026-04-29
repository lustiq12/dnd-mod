package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassDetailScreen extends Screen {

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
    private static final int ICON_SIZE = 32;
    private final int imageWidth = 400;
    private final int imageHeight = 212;

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

        // Dynamische Positionen basierend auf der aktuellen Fenstergröße
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        int centerX = this.width / 2;

        // ── CHOOSE BUTTON (Links von der Mitte positioniert) ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Choose"),
                btn -> {
                    CharacterCreationState.selectedClassId = cls.getId();
                    this.minecraft.setScreen(new CharacterFinalizationScreen(this.isNewCharacter));
                }
        ).bounds(centerX - 65, topPos + imageHeight - 28, 60, 20).build());

        // ── BACK BUTTON (Rechts von der Mitte positioniert) ──
        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> this.minecraft.setScreen(new ClassListScreen(this.isNewCharacter))
        ).bounds(centerX + 5, topPos + imageHeight - 28, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // ── ICON (Endet bei Y = topPos + 44) ──
        g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(),
                leftPos + 10, topPos + 12,
                0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        // ── NAME ──
        g.drawString(this.font, cls.getDisplayName(), leftPos + 50, topPos + 22, -1);

        // ── STATS PREVIEW (Jetzt höher geschoben) ──
        RaceDefinition race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        Map<String, Double> combined = combinedAttrs(race, cls);

        // topPos + 60 ist ca. 1.5 Zeilenabstände unter dem Icon-Ende
        int attrY = topPos + 60;
        g.drawString(this.font, "Total Stats (Race + Class):", leftPos + 10, attrY, -1);
        attrY += 12;
        for (Map.Entry<String, Double> e : combined.entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            String line = e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue());
            g.drawString(this.font, line, leftPos + 10, attrY, -1);
            attrY += 10;
        }

        // ── DESCRIPTION (Unter die Stats geschoben, damit nichts überlappt) ──
        // attrY ist durch die Schleife oben nun dynamisch am Ende der Stats
        int descY = attrY + 8;
        g.drawWordWrap(this.font, Component.literal(cls.getDescription()),
                leftPos + 10, descY, 180, 0xAAAAAA);

        // ── CLASS ABILITIES (Rechte Spalte bleibt gleich) ──
        int rightColX = leftPos + 200;
        int abY = topPos + 30;
        g.drawString(this.font, "Class Features:", rightColX, abY, -1);
        abY += 14;
        for (String line : cls.getAbilityLines()) {
            g.drawWordWrap(this.font, Component.literal("• " + line), rightColX, abY, 180, -1);
            abY += 20;
        }
    }

    private Map<String, Double> combinedAttrs(RaceDefinition race, ClassDefinition cls) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : cls.getAttributeModifiers().entrySet())
            result.put(e.getKey(), e.getValue());
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