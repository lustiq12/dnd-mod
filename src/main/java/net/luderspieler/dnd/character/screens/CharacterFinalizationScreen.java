package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.*;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CharacterFinalizationScreen extends Screen {

    // ── Unveränderte Sizing-Konstanten ──────────────────────────────────────
    private static final int BOX_WIDTH    = 220;
    private static final int ICON_SIZE    = 32;
    private static final int ITEM_SIZE    = 16;
    private static final int COLUMN_WIDTH = 180;

    // ── Header-Layout (feste Y-Offsets vom oberen Rand) ─────────────────────
    // Icons: zwei 16×16-Slots zentriert oben
    private static final int ICON_Y      = 10;
    // Name: goldene Überschrift, 1,8-fach skaliert (~16 px Schrifthöhe auf Screen)
    private static final int NAME_Y      = ICON_Y + ICON_SIZE + 4;   // 30
    // Infoleiste: Race | Class | Lvl 1 in Grau
    private static final int INFO_Y      = NAME_Y + 18;              // 48
    // Trennlinie
    private static final int SEPARATOR_Y = INFO_Y + 12;              // 60
    // Anfang des Inhaltsbereichs (unter Trennlinie)
    private static final int CONTENT_TOP = SEPARATOR_Y + 14;         // 74

    // ── Linke Spalte: Label-Y und Box-Y ─────────────────────────────────────
    private static final int LBL_NAME_Y  = CONTENT_TOP;              // 74
    private static final int BOX_NAME_Y  = LBL_NAME_Y  + 12;        // 86
    private static final int BOX_NAME_H  = 20;

    private static final int LBL_STORY_Y = BOX_NAME_Y  + BOX_NAME_H + 8; // 114
    private static final int BOX_STORY_Y = LBL_STORY_Y + 12;             // 126
    private static final int BOX_STORY_H = 50;

    private static final int LBL_PERS_Y  = BOX_STORY_Y + BOX_STORY_H + 8; // 184
    private static final int BOX_PERS_Y  = LBL_PERS_Y  + 12;              // 196
    private static final int BOX_PERS_H  = 50;

    // ── Rechtes Panel: innerer Padding ──────────────────────────────────────
    private static final int PANEL_PAD   = 8;

    // ── Stat-Tabelle ─────────────────────────────────────────────────────────
    private static final String[] STAT_KEYS = {
            "Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma"
    };
    private static final String[] STAT_ABBR = {
            "STR", "DEX", "CON", "INT", "WIS", "CHA"
    };

    // ── Felder ───────────────────────────────────────────────────────────────
    private final boolean isNewCharacter;
    private EditBox nameBox;
    private EditBox storyBox;
    private EditBox personalityBox;

    private final RaceDefinition    race;
    private final SubraceDefinition subrace;
    private final ClassDefinition   cls;
    private int rpW;

    // ============================================================
    //  Konstruktor – identische Logik wie original
    // ============================================================
    public CharacterFinalizationScreen(boolean isNewCharacter) {
        super(Component.literal("Character Finalization"));
        this.isNewCharacter = isNewCharacter;
        this.race    = RaceRegistry.getRace(CharacterCreationState.selectedRaceId);
        this.subrace = RaceRegistry.getSubrace(CharacterCreationState.selectedSubraceId);
        this.cls     = ClassRegistry.getClass(CharacterCreationState.selectedClassId);
    }

    // ============================================================
    //  init() – Widgets platzieren
    //  Positionen sind auf die render()-Labels abgestimmt.
    // ============================================================
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int leftX   = centerX - 230;   // Linke Spalte beginnt hier

        // ── Eingabefelder ─────────────────────────────────────────────────────
        this.nameBox = new EditBox(
                this.font, leftX, BOX_NAME_Y, BOX_WIDTH, BOX_NAME_H,
                Component.literal("Name"));
        this.addRenderableWidget(this.nameBox);

        // Höhere EditBoxen geben dem Nutzer visuell mehr "Platz" für Story/Pers.
        this.storyBox = new EditBox(
                this.font, leftX, BOX_STORY_Y, BOX_WIDTH, BOX_STORY_H,
                Component.literal("Story"));
        this.addRenderableWidget(this.storyBox);

        this.personalityBox = new EditBox(
                this.font, leftX, BOX_PERS_Y, BOX_WIDTH, BOX_PERS_H,
                Component.literal("Personality"));
        this.addRenderableWidget(this.personalityBox);

        // ── Buttons – zentriert am unteren Rand ──────────────────────────────
        int btnY = this.height - 35;
        this.addRenderableWidget(
                Button.builder(Component.literal("Back"),
                                b -> this.minecraft.setScreen(new ClassDetailScreen(cls, isNewCharacter)))
                        .bounds(centerX - 62, btnY, 55, 20)
                        .build());

        this.addRenderableWidget(
                Button.builder(Component.literal("Finish"), b -> {
                            // ── Paket senden – identische Logik wie original ──────────
                            ClientPacketDistributor.sendToServer(new CharacterCreationPacket(
                                    CharacterCreationState.selectedRaceId,
                                    CharacterCreationState.selectedSubraceId,
                                    CharacterCreationState.selectedClassId,
                                    nameBox.getValue(),
                                    storyBox.getValue(),
                                    personalityBox.getValue()
                            ));
                            CharacterCreationState.reset();
                            this.onClose();
                        }).bounds(centerX + 8, btnY, 55, 20)
                        .build());
    }

    // ============================================================
    //  render() – alles zeichnen
    // ============================================================
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {

        // 1. Hintergrund + dunkles Overlay
        this.renderBackground(g, mouseX, mouseY, partial);
        g.fill(0, 0, this.width, this.height, generalConfigs.COLOR_SCREEN_OVERLAY);

        int centerX = this.width / 2;
        int leftX   = centerX - 230;

        // ══════════════════════════════════════════════════════════
        //  HEADER
        // ══════════════════════════════════════════════════════════

        // Rassen-Icon-Slot (links vom Zentrum)
        int raceSlotX = centerX - ICON_SIZE - 3;
        g.fill(raceSlotX, ICON_Y, raceSlotX + ICON_SIZE, ICON_Y + ICON_SIZE, generalConfigs.TEXT_DARK_GRAY);
        generalConfigs.renderGreenEdge(g, raceSlotX, ICON_Y, ICON_SIZE, ICON_SIZE);

        // Klassen-Icon-Slot (rechts vom Zentrum)
        int clsSlotX = centerX + 3;
        g.fill(clsSlotX, ICON_Y, clsSlotX + ICON_SIZE, ICON_Y + ICON_SIZE, generalConfigs.TEXT_DARK_GRAY);
        generalConfigs.renderGreenEdge(g, clsSlotX, ICON_Y, ICON_SIZE, ICON_SIZE);

        // ── Charaktername: zentriert, Gold, mit Schlagschatten ───────────────
        String displayName = (nameBox != null && !nameBox.getValue().isBlank())
                ? nameBox.getValue() : "— Name —";
        g.drawCenteredString(this.font, displayName, centerX, NAME_Y,
                generalConfigs.COLOR_ACCENT_GOLD);

        // ── Infoleiste: Subrace Race | Class | Lvl 1 ─────────────────────────
        // 1. Strings direkt aus dem CreationState holen und säubern (Unterstriche zu Leerzeichen)
        String rawRace = CharacterCreationState.selectedRaceId.replace("_", " ");
        String rawSubrace = CharacterCreationState.selectedSubraceId.replace("_", " ");
        String rawClass = CharacterCreationState.selectedClassId.replace("_", " ");

// 2. Schnelle Konvertierung in Camel Case / Title Case
        java.util.function.Function<String, String> format = (str) -> {
            if (str == null || str.isBlank()) return "";
            String[] words = str.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String w : words) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
            return sb.toString().trim();
        };

        String cleanRace = format.apply(rawRace);
        String cleanSubrace = format.apply(rawSubrace);
        String cleanClass = format.apply(rawClass);

// 3. Rassen-Teil: "Subrace (Race)" oder nur "Race" (Falls keine Subrace gewählt wurde oder sie leer ist)
        String racePart = (!cleanSubrace.isEmpty())
                ? cleanSubrace + " (" + (!cleanRace.isEmpty() ? cleanRace : "?") + ")"
                : (!cleanRace.isEmpty() ? cleanRace : "?");

// 4. Klassen-Teil
        String classPart = !cleanClass.isEmpty() ? cleanClass : "?";

// 5. Finaler Info-Text für den Creation Screen
        String infoText = racePart + " | " + classPart + " | Lvl 1";
        g.drawCenteredString(this.font, infoText, centerX, INFO_Y, generalConfigs.TEXT_GRAY);

        // ── Trennlinie ────────────────────────────────────────────────────────
        g.hLine(15, this.width - 15, SEPARATOR_Y, generalConfigs.TEXT_GRAY);

        // ══════════════════════════════════════════════════════════
        //  LINKE SPALTE – nur Labels (Widgets rendern über super.render)
        // ══════════════════════════════════════════════════════════

        g.drawString(this.font, "Character Name", leftX, LBL_NAME_Y,  generalConfigs.TEXT_WHITE);
        g.drawString(this.font, "Backstory",      leftX, LBL_STORY_Y, generalConfigs.TEXT_WHITE);
        g.drawString(this.font, "Personality",    leftX, LBL_PERS_Y,  generalConfigs.TEXT_WHITE);

        // ══════════════════════════════════════════════════════════
        //  RECHTES PANEL – dunkler Hintergrund mit grünem Rahmen
        // ══════════════════════════════════════════════════════════

        int rpX = centerX + 10;
        int rpY = SEPARATOR_Y + 5;
        int rpW = this.width - rpX - 20;
        int rpH = this.height - rpY - 45;

        int rx = rpX + PANEL_PAD;   // Panel-Inhalts-X
        int ry = rpY + 10;          // Panel-Inhalts-Y (Cursor)

        // ── Finale Attribute ──────────────────────────────────────────────────
        g.drawString(this.font, "Final Attributes", rx, ry,
                generalConfigs.COLOR_ACCENT_GOLD, true);
        ry += 12;

        Map<String, Integer> attrs = buildCombinedAttrs();
        int colW = 68;   // Zweite Spalte eng daneben

        for (int i = 0; i < STAT_KEYS.length; i++) {
            int val = attrs.getOrDefault(STAT_KEYS[i], 10);
            int mod = (val - 10) / 2;     // D&D-Modifier-Formel
            String sign   = mod >= 0 ? "+" : "";
            String abbStr = STAT_ABBR[i] + ": ";
            String valStr = String.valueOf(val);
            String modStr = " (" + sign + mod + ")";

            int col = i % 2;   // 0 = links, 1 = rechts
            int row = i / 2;   // 3 Zeilen
            int sx  = rx + col * colW;
            int sy  = ry + row * 12;

            int modColor = mod > 0 ? generalConfigs.COLOR_STATUS_SUCCESS
                    : mod < 0 ? generalConfigs.COLOR_STATUS_DANGER
                      :           generalConfigs.TEXT_GRAY;

            int aw = this.font.width(abbStr);
            int vw = this.font.width(valStr);

            g.drawString(this.font, abbStr, sx,          sy, generalConfigs.TEXT_GRAY,  true);
            g.drawString(this.font, valStr, sx + aw,     sy, generalConfigs.TEXT_WHITE, true);
            g.drawString(this.font, modStr, sx + aw + vw, sy, modColor,                true);
        }
        ry += 3 * 12 + 8;   // 3 Zeilen à 12 px + Abstand

        // ── Starting Equipment ────────────────────────────────────────────────
        g.drawString(this.font, "Starting Equipment", rx, ry,
                generalConfigs.COLOR_ACCENT_GOLD, true);
        ry += 12;

        if (cls != null) {
            List<ItemStack> items = cls.getStarterItems();
            for (int i = 0; i < items.size(); i++) {
                int slotSize = ITEM_SIZE + 4;   // 2 px extra auf jeder Seite
                int ix = rx + i * (slotSize + 3);
                // Slot-Hintergrund + Rahmen
                g.fill(ix, ry, ix + slotSize, ry + slotSize, generalConfigs.TEXT_DARK_GRAY);
                generalConfigs.renderGreenEdge(g, ix, ry, slotSize, slotSize);
                g.renderItem(items.get(i), ix + 2, ry + 2);
                renderMyTooltip(g, items.get(i), ix, ry, mouseX, mouseY);
            }
        }
        ry += (ITEM_SIZE + 4) + 12;

        // ── Proficiencies ─────────────────────────────────────────────────────
        g.drawString(this.font, "Proficiencies", rx, ry,
                generalConfigs.COLOR_ACCENT_GOLD, true);
        ry += 12;

        String profs = buildCombinedProfs();
        // Jede Proficiency in "War Weapons"-Format bringen und nach je 3 umbrechen
        String[] profArr = profs.equals("None") ? new String[]{"None"} : profs.split(", ");
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        for (int i = 0; i < profArr.length; i++) {
            if (line.length() > 0) line.append(", ");
            line.append(profArr[i]);
            lineCount++;
            boolean isLast = (i == profArr.length - 1);
            if (lineCount == 3 || isLast) {
                g.drawString(this.font, line.toString(), rx, ry, generalConfigs.TEXT_GRAY, true);
                ry += 10;
                line = new StringBuilder();
                lineCount = 0;
            }
        }

        // ── Widgets (EditBoxen, Buttons) über allem ────────────────────────
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(g, mouseX, mouseY, partial);
        }
    }

    // ============================================================
    //  Private Helper – unveränderte Logik aus Original
    // ============================================================

    /** Tooltip für Equipment-Slots */
    private void renderMyTooltip(GuiGraphics g, ItemStack stack,
                                 int ix, int iy, int mouseX, int mouseY) {
        if (mouseX >= ix && mouseX < ix + ITEM_SIZE
                && mouseY >= iy && mouseY < iy + ITEM_SIZE
                && !stack.isEmpty()) {
            List<Component> textLines = Screen.getTooltipFromItem(this.minecraft, stack);
            List<ClientTooltipComponent> finalTooltip = textLines.stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .collect(java.util.stream.Collectors.toList());
            stack.getTooltipImage().ifPresent(
                    image -> finalTooltip.add(1, ClientTooltipComponent.create(image)));
            g.renderTooltip(this.font, finalTooltip, mouseX, mouseY,
                    DefaultTooltipPositioner.INSTANCE,
                    ResourceLocation.withDefaultNamespace(
                            "textures/gui/tooltip_background.png"));
        }
    }

    /** Basiswerte + Rassen- + Subras- + Klassen-Boni zusammenrechnen */
    private Map<String, Integer> buildCombinedAttrs() {
        Map<String, Integer> result =
                new LinkedHashMap<>(CharacterCreationState.baseAttributes);
        if (race    != null) race.getAbilityScoreIncrements()
                .forEach((k, v) -> result.merge(k, v, Integer::sum));
        if (subrace != null) subrace.getAbilityScoreIncrements()
                .forEach((k, v) -> result.merge(k, v, Integer::sum));
        if (cls     != null) cls.getAbilityScoreIncrements()
                .forEach((k, v) -> result.merge(k, v, Integer::sum));
        return result;
    }

    /** Proficiencies aus Rasse, Subrasse und Klasse dedupliziert zusammenführen */
    private String buildCombinedProfs() {
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
        if (race    != null) addProfs(set, race.getProficiencies());
        if (subrace != null) addProfs(set, subrace.getProficiencies());
        if (cls     != null) addProfs(set, cls.getProficiencies());
        return set.isEmpty() ? "None" : String.join(", ", set);
    }

    private void addProfs(java.util.LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) continue;
            // snake_case / kebab-case → "Title Case"  (z.B. "war_weapons" → "War Weapons")
            String[] words = trimmed.replace('-', '_').split("_");
            StringBuilder formatted = new StringBuilder();
            for (String word : words) {
                if (word.isEmpty()) continue;
                if (formatted.length() > 0) formatted.append(' ');
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) formatted.append(word.substring(1).toLowerCase());
            }
            set.add(formatted.toString());
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
