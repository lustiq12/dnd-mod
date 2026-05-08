package net.luderspieler.dnd.classes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CharacterFinalizationScreen extends Screen {

    private static final int BOX_WIDTH  = 220;
    private static final int ICON_SIZE  = 16;
    private static final int ITEM_SIZE  = 16;
    private static final int COLUMN_WIDTH = 180; // Leicht erhöht für mehr Textplatz

    private final boolean isNewCharacter;
    private EditBox nameBox;
    private EditBox storyBox;
    private EditBox personalityBox;

    private RaceDefinition    race;
    private SubraceDefinition subrace;
    private ClassDefinition   cls;

    public CharacterFinalizationScreen(boolean isNewCharacter) {
        super(Component.literal("Finalize Your Character"));
        this.isNewCharacter = isNewCharacter;
    }

    @Override
    protected void init() {
        super.init();
        race    = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        subrace = RaceRegistry.getSubrace(CharacterCreationState.selectedSubraceId);
        cls     = ClassRegistry.getClass(CharacterCreationState.selectedClassId);

        int inputX = this.width / 2 - BOX_WIDTH / 2;

        nameBox = new EditBox(this.font, inputX, 30, BOX_WIDTH, 14, Component.literal("Name"));
        nameBox.setMaxLength(32);
        this.addRenderableWidget(nameBox);

        storyBox = new EditBox(this.font, inputX, 55, BOX_WIDTH, 14, Component.literal("Backstory"));
        storyBox.setMaxLength(256);
        this.addRenderableWidget(storyBox);

        personalityBox = new EditBox(this.font, inputX, 80, BOX_WIDTH, 14, Component.literal("Personality"));
        personalityBox.setMaxLength(256);
        this.addRenderableWidget(personalityBox);

        this.addRenderableWidget(Button.builder(Component.literal("Create Character"), btn -> createCharacter())
                .bounds(this.width / 2 - 60, this.height - 22, 120, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"), btn -> this.minecraft.setScreen(new ClassListScreen(isNewCharacter)))
                .bounds(8, this.height - 22, 50, 18).build());
    }

    private void createCharacter() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        CharacterCreationPacket.send(CharacterCreationState.selectedRaceId, CharacterCreationState.selectedSubraceId, CharacterCreationState.selectedClassId,
                nameBox.getValue().isEmpty() ? "Adventurer" : nameBox.getValue(), storyBox.getValue(), personalityBox.getValue());
        CharacterCreationState.reset();
        this.minecraft.player.closeContainer();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);
        super.render(g, mouseX, mouseY, partial);

        int cx = this.width / 2;
        int divY = 105;
        g.fill(20, divY, this.width - 20, divY + 1, 0x44FFFFFF);

        // Header Labels
        int inputLabelX = cx - BOX_WIDTH / 2;
        g.drawString(this.font, "Name:", inputLabelX, 20, -1, true);
        g.drawString(this.font, "Backstory:", inputLabelX, 45, -1, true);
        g.drawString(this.font, "Personality:", inputLabelX, 70, -1, true);

        // Spalten-Konfiguration (30 Pixel weiter nach außen verschoben)
        int leftX = inputLabelX - 50;
        int rightX = cx + (BOX_WIDTH / 2) - 110 + 50; // Angepasst für Symmetrie nach rechts
        int currentLY = divY + 8;

        // ── KLASSE (Links Oben) ──
        if (cls != null) {
            g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(), leftX, currentLY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            g.drawString(this.font, cls.getDisplayName(), leftX + 20, currentLY + 4, -1, true);
            currentLY += 20;

            for (String feat : cls.getAbilityLines()) {
                List<FormattedCharSequence> lines = this.font.split(Component.literal("» " + feat), COLUMN_WIDTH);
                for (FormattedCharSequence line : lines) {
                    g.drawString(this.font, line, leftX, currentLY, -1, true);
                    currentLY += 10;
                }
            }
        }

        // ── SPEZIES (Darunter) ──
        currentLY += 10;
        if (race != null) {
            g.blit(RenderPipelines.GUI_TEXTURED, (subrace != null ? subrace.getIcon() : race.getIcon()), leftX, currentLY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            g.drawString(this.font, (subrace != null ? subrace.getDisplayName() : race.getDisplayName()), leftX + 20, currentLY + 4, -1, true);
            currentLY += 20;

            for (String trait : race.getAbilityLines()) {
                List<FormattedCharSequence> lines = this.font.split(Component.literal("» " + trait), COLUMN_WIDTH);
                for (FormattedCharSequence line : lines) {
                    g.drawString(this.font, line, leftX, currentLY, -1, true);
                    currentLY += 10;
                }
            }
        }

        // ── RECHTE SEITE ──
        int currentRY = divY + 8;

        // Starter Items
        g.drawString(this.font, "Starter Items:", rightX, currentRY, -1, true);
        currentRY += 12;
        if (cls != null) {
            List<ItemStack> items = cls.getStarterItems();
            for (int i = 0; i < items.size(); i++) {
                int ix = rightX + i * 18;
                g.renderItem(items.get(i), ix, currentRY);
                renderMyTooltip(g, items.get(i), ix, currentRY, mouseX, mouseY);
            }
        }

        // Stats
        currentRY += 25;
        g.drawString(this.font, "Final Stats:", rightX, currentRY, -1, true);
        currentRY += 12;
        for (Map.Entry<String, Double> e : buildCombinedAttrs().entrySet()) {
            if (e.getValue() == 0) continue;
            g.drawString(this.font, e.getKey() + ": " + (e.getValue() > 0 ? "+" : "") + formatVal(e.getKey(), e.getValue()), rightX, currentRY, -1, true);
            currentRY += 10;
        }

        // NEU: Abgegrenzte Zeile unter den Final Stats
        currentRY += 2; // Kleiner Puffer
        if (cls != null) {
            g.drawString(this.font, "Level up health increase: " + cls.getClassHealth(), rightX, currentRY, -1, true);
            currentRY += 10;
        }

        // Proficiencies
        currentRY += 10;
        g.drawString(this.font, "Proficiencies:", rightX, currentRY, -1, true);
        currentRY += 12;
        for (String prof : buildCombinedProfs().split(",")) {
            if (prof.isBlank()) continue;
            List<FormattedCharSequence> lines = this.font.split(Component.literal("- " + prof.trim().replace("_", " ")), COLUMN_WIDTH);
            for (FormattedCharSequence line : lines) {
                g.drawString(this.font, line, rightX, currentRY, -1, true);
                currentRY += 10;
            }
        }
    }

    private void renderMyTooltip(GuiGraphics g, ItemStack stack, int ix, int iy, int mouseX, int mouseY) {
        if (mouseX >= ix && mouseX < ix + ITEM_SIZE && mouseY >= iy && mouseY < iy + ITEM_SIZE && !stack.isEmpty()) {
            List<Component> textLines = Screen.getTooltipFromItem(this.minecraft, stack);
            List<ClientTooltipComponent> finalTooltip = textLines.stream()
                    .map(Component::getVisualOrderText).map(ClientTooltipComponent::create)
                    .collect(java.util.stream.Collectors.toList());
            stack.getTooltipImage().ifPresent(image -> finalTooltip.add(1, ClientTooltipComponent.create(image)));
            g.renderTooltip(this.font, finalTooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, ResourceLocation.withDefaultNamespace("textures/gui/tooltip_background.png"));
        }
    }

    private Map<String, Double> buildCombinedAttrs() {
        Map<String, Double> result = new LinkedHashMap<>();
        if (race != null) race.getAttributeModifiers().forEach((k, v) -> result.merge(k, v, Double::sum));
        if (cls != null) cls.getAttributeModifiers().forEach((k, v) -> result.merge(k, v, Double::sum));
        return result;
    }

    private String buildCombinedProfs() {
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
        if (race != null) addProfs(set, race.getProficiencies());
        if (subrace != null) addProfs(set, subrace.getProficiencies());
        if (cls != null) addProfs(set, cls.getProficiencies());
        return String.join(",", set);
    }

    private void addProfs(java.util.LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) if (!p.trim().isEmpty()) set.add(p.trim());
    }

    private String formatVal(String key, double val) {
        if (key.contains("Speed")) return String.format("%.0f%%", val * 100);
        return (val % 1 == 0) ? String.format("%.0f", val) : String.format("%.1f", val);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
}