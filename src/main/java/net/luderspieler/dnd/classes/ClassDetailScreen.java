package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassDetailScreen extends Screen {

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("dnd:textures/screens/preview_gui.png");
    private static final int ICON_SIZE  = 48;
    private static final int ITEM_SIZE  = 16;
    private final int imageWidth  = 637;
    private final int imageHeight = 420;

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
        int leftPos = (this.width - imageWidth) / 2;
        int topPos  = (this.height - imageHeight) / 2;
        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Choose"), btn -> {
            CharacterCreationState.selectedClassId = cls.getId();
            this.minecraft.setScreen(new CharacterFinalizationScreen(isNewCharacter));
        }).bounds(centerX - 100, topPos + imageHeight - 35, 90, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"), btn ->
                this.minecraft.setScreen(new ClassListScreen(isNewCharacter))
        ).bounds(centerX + 10, topPos + imageHeight - 35, 90, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);

        int leftPos = (this.width - imageWidth) / 2;
        int topPos  = (this.height - imageHeight) / 2;

        g.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0,
                imageWidth, imageHeight, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        // ── ICON & NAME ──
        g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(), leftPos + 20, topPos + 20, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        g.drawString(this.font, cls.getDisplayName(), leftPos + 80, topPos + 35, -1, true);

        // ── STARTER ITEMS ──
        int itemRowY = topPos + 80;
        g.drawString(this.font, "Starter Items:", leftPos + 20, itemRowY, -1, true);
        itemRowY += 15;
        List<ItemStack> items = cls.getStarterItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int ix = leftPos + 20 + i * (ITEM_SIZE + 5);
            g.renderItem(stack, ix, itemRowY);

            if (mouseX >= ix && mouseX < ix + ITEM_SIZE && mouseY >= itemRowY && mouseY < itemRowY + ITEM_SIZE && !stack.isEmpty()) {
                List<Component> textLines = Screen.getTooltipFromItem(this.minecraft, stack);
                Optional<TooltipComponent> tooltipImage = stack.getTooltipImage();
                List<ClientTooltipComponent> finalTooltip = textLines.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(java.util.stream.Collectors.toList());
                tooltipImage.ifPresent(image -> finalTooltip.add(1, ClientTooltipComponent.create(image)));
                g.renderTooltip(this.font, finalTooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, ResourceLocation.withDefaultNamespace("textures/gui/tooltip_background.png"));
            }
        }

        // ── STATS (12px Abstand) ──
        RaceDefinition race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        Map<String, Double> combined = combinedAttrs(race, cls);
        int attrY = itemRowY + 30;
        g.drawString(this.font, "Total Stats:", leftPos + 20, attrY, -1, true);
        attrY += 15;
        for (Map.Entry<String, Double> e : combined.entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            g.drawString(this.font, e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue()), leftPos + 20, attrY, -1, true);
            attrY += 12; // Dein Referenz-Abstand
        }

        for (Map.Entry<String, Double> e : combined.entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            g.drawString(this.font, e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue()), leftPos + 20, attrY, -1, true);
            attrY += 12;
        }

        // NEU: Abgegrenzte Zeile unter den Attributen
        attrY += 2; // Kleiner extra Puffer zur optischen Abgrenzung
        g.drawString(this.font, "Level up health increase: " + cls.getClassHealth(), leftPos + 20, attrY, -1, true);
        attrY += 12;

        // ── DESCRIPTION ──
        g.drawWordWrap(this.font, Component.literal(cls.getDescription()), leftPos + 20, attrY + 15, 250, -1);
        // ── CLASS ABILITIES (Dynamisches Flow-Layout mit Smart-Split) ──
        int rightColumnStart = leftPos + 300;
        int columnWidth = 150;
        int columnGap = 165;
        int abYHeader = topPos + 40;
        int lineSpacing = 12;

        g.drawString(this.font, "Class Progression (1-20):", rightColumnStart, abYHeader, -1, true);

        int currentYLeft = abYHeader + 20;
        int currentYRight = abYHeader + 20;

        List<String> abilities = cls.getAbilityLines();
        for (int i = 0; i < abilities.size(); i++) {
            boolean isLeftColumn = i < 10;
            int x = isLeftColumn ? rightColumnStart : rightColumnStart + columnGap;
            int y = isLeftColumn ? currentYLeft : currentYRight;

            String rawText = abilities.get(i);

            // Prüfen: Passt der gesamte Text in eine Zeile?
            if (this.font.width(rawText) <= columnWidth) {
                g.drawString(this.font, rawText, x, y, -1, true);
                y += lineSpacing;
            } else {
                // Wenn nicht: Wir splitten an der Klammer, um den Spell Slot nach unten zu schieben
                String[] parts = rawText.split("(?=\\()", 2);
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (trimmed.isEmpty()) continue;

                    // Falls ein Teil (z.B. ein extrem langer Name) immer noch zu lang ist,
                    // nutzen wir den normalen Wrap
                    List<FormattedCharSequence> wrapped = this.font.split(Component.literal(trimmed), columnWidth);
                    for (FormattedCharSequence line : wrapped) {
                        g.drawString(this.font, line, x, y, -1, true);
                        y += lineSpacing;
                    }
                }
            }

            // Y-Tracker aktualisieren
            if (isLeftColumn) currentYLeft = y;
            else currentYRight = y;
        }
        }

    private Map<String, Double> combinedAttrs(RaceDefinition race, ClassDefinition cls) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : cls.getAttributeModifiers().entrySet()) result.put(e.getKey(), e.getValue());
        if (race != null) for (Map.Entry<String, Double> e : race.getAttributeModifiers().entrySet()) result.merge(e.getKey(), e.getValue(), Double::sum);
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