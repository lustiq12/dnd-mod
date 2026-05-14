package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.*;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CharacterFinalizationScreen extends Screen {

    private static final int BOX_WIDTH  = 220;
    private static final int ICON_SIZE  = 16;
    private static final int ITEM_SIZE  = 16;
    private static final int COLUMN_WIDTH = 180;

    private final boolean isNewCharacter;
    private EditBox nameBox;
    private EditBox storyBox;
    private EditBox personalityBox;

    private RaceDefinition race;
    private SubraceDefinition subrace;
    private ClassDefinition cls;

    public CharacterFinalizationScreen(boolean isNewCharacter) {
        super(Component.literal("Character Finalization"));
        this.isNewCharacter = isNewCharacter;
        this.race = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        this.subrace = RaceRegistry.getSubrace(CharacterCreationState.selectedSubraceId);
        this.cls = ClassRegistry.getClass(CharacterCreationState.selectedClassId);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;

        // Falls CharacterCreationState keine Felder für Name/Story hat, nutzen wir hier leere Strings
        this.nameBox = new EditBox(this.font, centerX - 110, startY, BOX_WIDTH, 20, Component.literal("Name"));
        this.addRenderableWidget(this.nameBox);

        this.storyBox = new EditBox(this.font, centerX - 110, startY + 40, BOX_WIDTH, 20, Component.literal("Story"));
        this.addRenderableWidget(this.storyBox);

        this.personalityBox = new EditBox(this.font, centerX - 110, startY + 80, BOX_WIDTH, 20, Component.literal("Personality"));
        this.addRenderableWidget(this.personalityBox);

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.minecraft.setScreen(new ClassDetailScreen(cls, isNewCharacter)))
                .bounds(centerX - 110, this.height - 40, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Finish"), b -> {
            // Paket mit allen Auswahlen und den Texten aus den Boxen senden
            ClientPacketDistributor.sendToServer(new CharacterCreationPacket(
                    CharacterCreationState.selectedRaceId,
                    CharacterCreationState.selectedSubraceId,
                    CharacterCreationState.selectedClassId,
                    nameBox.getValue(),
                    storyBox.getValue(),
                    personalityBox.getValue()
            ));

            // WICHTIG: Danach den State resetten
            CharacterCreationState.reset();

            this.onClose();
        }).bounds(centerX + 10, this.height - 40, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.renderBackground(g, mouseX, mouseY, partial);
        super.render(g, mouseX, mouseY, partial);

        int centerX = this.width / 2;
        int rightX = centerX + 120;
        int currentRY = 40;

        g.drawString(this.font, "Name:", centerX - 110, 30, -1);
        g.drawString(this.font, "Backstory:", centerX - 110, 70, -1);
        g.drawString(this.font, "Personality:", centerX - 110, 110, -1);

        g.drawString(this.font, "Summary:", rightX, currentRY, -1, true);
        currentRY += 15;
        if (race != null) g.drawString(this.font, "Race: " + race.getDisplayName(), rightX, currentRY, 0xAAAAAA, true);
        currentRY += 10;
        if (subrace != null) g.drawString(this.font, "Subrace: " + subrace.getDisplayName(), rightX, currentRY, 0xAAAAAA, true);
        currentRY += 10;
        if (cls != null) g.drawString(this.font, "Class: " + cls.getDisplayName(), rightX, currentRY, 0xAAAAAA, true);

        currentRY += 25;
        g.drawString(this.font, "Final Stats:", rightX, currentRY, -1, true);
        currentRY += 12;

        for (Map.Entry<String, Integer> e : buildCombinedAttrs().entrySet()) {
            if (e.getValue() == 0) continue;
            String sign = e.getValue() > 0 ? "+" : "";
            g.drawString(this.font, e.getKey() + ": " + sign + formatVal(e.getKey(), e.getValue()), rightX, currentRY, -1, true);
            currentRY += 10;
        }

        currentRY += 15;
        g.drawString(this.font, "Starting Equipment:", rightX, currentRY, -1, true);
        currentRY += 15;

        if (cls != null) {
            List<ItemStack> items = cls.getStarterItems();
            for (int i = 0; i < items.size(); i++) {
                int ix = rightX + i * 18;
                g.renderItem(items.get(i), ix, currentRY);
                renderMyTooltip(g, items.get(i), ix, currentRY, mouseX, mouseY);
            }
        }

        // Proficiencies Anzeige (War in deinem Original drin)
        currentRY += 25;
        g.drawString(this.font, "Proficiencies:", rightX, currentRY, -1, true);
        currentRY += 12;
        String profs = buildCombinedProfs();
        List<FormattedCharSequence> wrappedProfs = this.font.split(Component.literal(profs), 150);
        for (FormattedCharSequence line : wrappedProfs) {
            g.drawString(this.font, line, rightX, currentRY, 0xAAAAAA, true);
            currentRY += 10;
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

    private Map<String, Integer> buildCombinedAttrs() {
        // 1. Start with the base values (Default 10 or rolled values) from the State
        Map<String, Integer> result = new LinkedHashMap<>(CharacterCreationState.baseAttributes);

        // 2. Add Race bonuses
        if (race != null) {
            race.getAbilityScoreIncrements().forEach((k, v) -> result.merge(k, v, Integer::sum));
        }

        // 3. Add Subrace bonuses
        if (subrace != null) {
            subrace.getAbilityScoreIncrements().forEach((k, v) -> result.merge(k, v, Integer::sum));
        }

        // Note: Usually classes in D&D don't give Ability Score Increments at level 1,
        // but if yours does, keep this line:
        if (cls != null) {
            cls.getAbilityScoreIncrements().forEach((k, v) -> result.merge(k, v, Integer::sum));
        }

        return result;
    }

    private String buildCombinedProfs() {
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
        if (race != null) addProfs(set, race.getProficiencies());
        if (subrace != null) addProfs(set, subrace.getProficiencies());
        if (cls != null) addProfs(set, cls.getProficiencies());
        return set.isEmpty() ? "None" : String.join(", ", set);
    }

    private void addProfs(java.util.LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) if (!p.trim().isEmpty()) set.add(p.trim());
    }

    private String formatVal(String key, int val) {
        return String.valueOf(val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}