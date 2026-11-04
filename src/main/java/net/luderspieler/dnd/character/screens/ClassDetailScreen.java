package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.CharacterCreationState;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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

    private static final int ICON_SIZE  = 48;
    private static final int ITEM_SIZE  = 16;
    private static final int MAX_WIDTH  = 637;
    private static final int MAX_HEIGHT = 420;
    private static final int SCREEN_MARGIN = 5;
    // Space reserved at the panel bottom for the button row
    private static final int FOOTER_H = 40;
    // Below this panel width the two-column layout is replaced by two switchable pages
    private static final int WIDE_THRESHOLD = 600;

    private int imageWidth  = MAX_WIDTH;
    private int imageHeight = MAX_HEIGHT;

    private int scroll = 0;
    private int contentHeight = 0;

    // Narrow layout only: false = overview page, true = class progression page
    private boolean showProgression = false;
    private Button pageButton;

    private final ClassDefinition cls;
    private final boolean isNewCharacter;

    public ClassDetailScreen(ClassDefinition cls, boolean isNewCharacter) {
        super(Component.literal(cls.getDisplayName()));
        this.cls = cls;
        this.isNewCharacter = isNewCharacter;
    }

    private void updateSize() {
        this.imageWidth  = Math.min(MAX_WIDTH,  this.width  - 2 * SCREEN_MARGIN);
        this.imageHeight = Math.min(MAX_HEIGHT, this.height - 2 * SCREEN_MARGIN);
    }

    private boolean wide() {
        return imageWidth >= WIDE_THRESHOLD;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight - (imageHeight - FOOTER_H));
    }

    @Override
    protected void init() {
        super.init();
        updateSize();
        int topPos  = (this.height - imageHeight) / 2;
        int centerX = this.width / 2;
        int btnY = topPos + imageHeight - 30;

        Button.OnPress choose = btn -> {
            CharacterCreationState.selectedClassId = cls.getId();
            this.minecraft.setScreen(new CharacterFinalizationScreen(isNewCharacter));
        };
        Button.OnPress back = btn -> this.minecraft.setScreen(new ClassListScreen(isNewCharacter));

        if (wide()) {
            this.addRenderableWidget(Button.builder(Component.literal("Choose"), choose)
                    .bounds(centerX - 100, btnY, 90, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("Back"), back)
                    .bounds(centerX + 10, btnY, 90, 20).build());
        } else {
            int startX = centerX - 128;
            this.addRenderableWidget(Button.builder(Component.literal("Choose"), choose)
                    .bounds(startX, btnY, 80, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("Back"), back)
                    .bounds(startX + 88, btnY, 80, 20).build());
            pageButton = this.addRenderableWidget(Button.builder(pageButtonLabel(), btn -> {
                showProgression = !showProgression;
                scroll = 0;
                pageButton.setMessage(pageButtonLabel());
            }).bounds(startX + 176, btnY, 80, 20).build());
        }
    }

    private Component pageButtonLabel() {
        return Component.literal(showProgression ? "Overview" : "Progression");
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos  = (this.height - this.imageHeight) / 2;

        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, generalConfigs.COLOR_PANEL_BG);
        generalConfigs.renderGreenEdge(g, leftPos, topPos, imageWidth, imageHeight);

        super.render(g, mouseX, mouseY, partial);

        int originY = topPos - scroll;
        g.enableScissor(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - FOOTER_H);
        int endY = drawContent(g, mouseX, mouseY, leftPos, topPos, originY, wide());
        g.disableScissor();

        contentHeight = endY - originY + 10;
        scroll = Math.min(scroll, maxScroll());

        if (maxScroll() > 0) {
            int trackTop = topPos + 3;
            int trackH = imageHeight - FOOTER_H - 6;
            int thumbH = Math.max(10, trackH * trackH / contentHeight);
            int thumbY = trackTop + (trackH - thumbH) * scroll / maxScroll();
            g.fill(leftPos + imageWidth - 4, trackTop, leftPos + imageWidth - 2, trackTop + trackH, 0x33FFFFFF);
            g.fill(leftPos + imageWidth - 4, thumbY, leftPos + imageWidth - 2, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    /** Draws everything inside the scrollable area and returns the y coordinate of its lower end. */
    private int drawContent(GuiGraphics g, int mouseX, int mouseY, int leftPos, int topPos, int originY, boolean wide) {
        int viewBottom = topPos + imageHeight - FOOTER_H;
        List<String> abilities = cls.getAbilityLines();
        int lineSpacing = 12;

        if (!wide && showProgression) {
            int textWidth = imageWidth - 46;
            int y = originY + 14;
            g.drawString(this.font, cls.getDisplayName() + " - Class Progression", leftPos + 20, y, generalConfigs.COLOR_ACCENT_GOLD, true);
            y += 18;
            for (String rawText : abilities) {
                List<FormattedCharSequence> wrapped = this.font.split(Component.literal(rawText), textWidth);
                for (FormattedCharSequence line : wrapped) {
                    g.drawString(this.font, line, leftPos + 20, y, generalConfigs.TEXT_WHITE, true);
                    y += lineSpacing;
                }
            }
            return y;
        }

        g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(), leftPos + 20, originY + 20, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        g.drawString(this.font, cls.getDisplayName(), leftPos + 76, originY + 24, generalConfigs.TEXT_WHITE);

        int itemRowY = originY + 80;
        g.drawString(this.font, "Starter Items:", leftPos + 20, itemRowY, generalConfigs.COLOR_ACCENT_GOLD, true);
        itemRowY += 15;
        List<ItemStack> items = cls.getStarterItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int ix = leftPos + 20 + i * (ITEM_SIZE + 5);
            g.renderItem(stack, ix, itemRowY);
            if (mouseX >= ix && mouseX < ix + ITEM_SIZE && mouseY >= itemRowY && mouseY < itemRowY + ITEM_SIZE
                    && mouseY > topPos && mouseY < viewBottom && !stack.isEmpty()) {
                List<Component> textLines = Screen.getTooltipFromItem(this.minecraft, stack);
                Optional<TooltipComponent> tooltipImage = stack.getTooltipImage();
                List<ClientTooltipComponent> finalTooltip = textLines.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(java.util.stream.Collectors.toList());
                tooltipImage.ifPresent(image -> finalTooltip.add(1, ClientTooltipComponent.create(image)));
                g.renderTooltip(this.font, finalTooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, ResourceLocation.withDefaultNamespace("textures/gui/tooltip_background.png"));
            }
        }

        RaceDefinition race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        Map<String, Integer> combined = combinedAttrs(race, cls);
        int attrY = itemRowY + 30;
        g.drawString(this.font, "Total Stats:", leftPos + 20, attrY, generalConfigs.COLOR_ACCENT_GOLD, true);
        attrY += 15;
        for (Map.Entry<String, Integer> e : combined.entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            g.drawString(this.font, e.getKey() + ": " + sign + e.getValue(), leftPos + 20, attrY, generalConfigs.TEXT_WHITE, true);
            attrY += 12;
        }

        attrY += 2;
        g.drawString(this.font, "Level up health increase: " + cls.getClassHealth(), leftPos + 20, attrY, generalConfigs.COLOR_ACCENT_GOLD, true);
        attrY += 15;

        int descWidth = wide ? 250 : imageWidth - 46;
        g.drawWordWrap(this.font, Component.literal(cls.getDescription()), leftPos + 20, attrY, descWidth, generalConfigs.TEXT_GRAY);
        int descEnd = attrY + this.font.wordWrapHeight(cls.getDescription(), descWidth);

        if (!wide) {
            return descEnd;
        }

        int rightColumnStart = leftPos + 300;
        int columnWidth = 150;
        int columnGap = 165;
        int abYHeader = originY + 40;

        g.drawString(this.font, "Class Progression (1-20):", rightColumnStart, abYHeader, generalConfigs.COLOR_ACCENT_GOLD, true);

        int currentYLeft = abYHeader + 20;
        int currentYRight = abYHeader + 20;
        for (int i = 0; i < abilities.size(); i++) {
            boolean isLeftColumn = i < 10;
            int x = isLeftColumn ? rightColumnStart : rightColumnStart + columnGap;
            int y = isLeftColumn ? currentYLeft : currentYRight;
            String rawText = abilities.get(i);
            if (this.font.width(rawText) <= columnWidth) {
                g.drawString(this.font, rawText, x, y, generalConfigs.TEXT_WHITE, true);
                y += lineSpacing;
            } else {
                String[] parts = rawText.split("(?=\\()", 2);
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (trimmed.isEmpty()) continue;
                    List<FormattedCharSequence> wrapped = this.font.split(Component.literal(trimmed), columnWidth);
                    for (FormattedCharSequence line : wrapped) {
                        g.drawString(this.font, line, x, y, generalConfigs.TEXT_WHITE, true);
                        y += lineSpacing;
                    }
                }
            }
            if (isLeftColumn) currentYLeft = y; else currentYRight = y;
        }
        return Math.max(descEnd, Math.max(currentYLeft, currentYRight));
    }

    private Map<String, Integer> combinedAttrs(RaceDefinition race, ClassDefinition cls) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : cls.getAbilityScoreIncrements().entrySet()) {
            result.put(e.getKey(), e.getValue());
        }
        if (race != null) {
            for (Map.Entry<String, Integer> e : race.getAbilityScoreIncrements().entrySet()) {
                result.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }
        return result;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll - (int) (scrollY * 12)));
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}