package net.luderspieler.dnd.character.screens;

import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.character.choices.LevelingChoiceScreen;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class CharacterSheetScreen extends Screen {

    private static final int ICON_SIZE = 32;
    private Player player;
    private RaceDefinition race;
    private SubraceDefinition subrace;
    private ClassDefinition cls;
    private DndModVariables.PlayerVariables vars;

    // Mapping der Stats auf die D&D Modifier IDs
    private static final Map<Holder<Attribute>, ResourceLocation[]> DND_MODIFIERS = Map.of(
            Attributes.MAX_HEALTH, new ResourceLocation[]{ResourceLocation.parse("dnd:species_max_health"), ResourceLocation.parse("dnd:class_max_health")},
            Attributes.ATTACK_DAMAGE, new ResourceLocation[]{ResourceLocation.parse("dnd:species_attack_damage"), ResourceLocation.parse("dnd:class_attack_damage")},
            Attributes.ARMOR, new ResourceLocation[]{ResourceLocation.parse("dnd:species_armor"), ResourceLocation.parse("dnd:class_armor")},
            Attributes.MOVEMENT_SPEED, new ResourceLocation[]{ResourceLocation.parse("dnd:species_movement_speed"), ResourceLocation.parse("dnd:class_movement_speed")},
            Attributes.ATTACK_SPEED, new ResourceLocation[]{ResourceLocation.parse("dnd:species_attack_speed"), ResourceLocation.parse("dnd:class_attack_speed")},
            Attributes.LUCK, new ResourceLocation[]{ResourceLocation.parse("dnd:species_luck"), ResourceLocation.parse("dnd:class_luck")}
    );

    public CharacterSheetScreen() {
        super(Component.literal("Character Sheet"));
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft == null || this.minecraft.player == null) return;

        player = this.minecraft.player;
        vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        race = RaceRegistry.getRace(vars.PlayerRace);
        subrace = RaceRegistry.getSubrace(vars.PlayerSubrace);
        cls = ClassRegistry.getClass(vars.PlayerClass);

        int centerX = this.width / 2;

        // Button oben rechts
        this.addRenderableWidget(Button.builder(Component.literal("Character Advancement"),
                        btn -> Minecraft.getInstance().setScreen(new LevelingChoiceScreen(this))) // 'this' übergeben!
                .bounds(this.width - 170, 10, 160, 20).build());

        // Back Button unten Mitte
        this.addRenderableWidget(Button.builder(Component.literal("Back"), btn -> this.onClose())
                .bounds(centerX - 40, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // Hintergrund
        g.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xE0101010);
        super.render(g, mouseX, mouseY, partial);

        int centerX = this.width / 2;
        int currentY = 20;

        // ── HEADER (Icons, Name, Klasse) ──
        renderCenteredHeader(g, centerX, currentY);
        currentY += 85;

        // Trennlinie
        g.fill(centerX - 100, currentY, centerX + 100, currentY + 1, 0x44FFFFFF);
        currentY += 15;

        // ── STATS & ATTRIBUTE ──
        renderCentralStats(g, centerX, currentY);
    }

    private void renderCenteredHeader(GuiGraphics g, int centerX, int y) {
        // Icons zentrieren
        int totalIconWidth = (ICON_SIZE * 2) + 10;
        int iconX = centerX - (totalIconWidth / 2);

        if (race != null) {
            g.blit(RenderPipelines.GUI_TEXTURED, (subrace != null ? subrace.getIcon() : race.getIcon()),
                    iconX, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
        if (cls != null) {
            g.blit(RenderPipelines.GUI_TEXTURED, cls.getIcon(),
                    iconX + ICON_SIZE + 10, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        int textY = y + ICON_SIZE + 10;

        // Name (Standardgröße ohne Skalierung, um Fehler zu vermeiden)
        String name = (vars.PlayerName == null || vars.PlayerName.isEmpty()) ? "Unnamed Adventurer" : vars.PlayerName;
        g.drawCenteredString(this.font, "§l§6" + name, centerX, textY, -1);

        // Race | Class | Level
        String raceStr = race != null ? race.getDisplayName() : "Unknown";
        String classStr = cls != null ? cls.getDisplayName() : "Unknown";
        String info = "§7" + raceStr + " §8| §7" + classStr + " §8| §fLvl " + (int)vars.PlayerLevel;
        g.drawCenteredString(this.font, info, centerX, textY + 12, -1);
    }

    private void renderCentralStats(GuiGraphics g, int centerX, int y) {
        // ── D&D ATTRIBUTES ──
        g.drawCenteredString(this.font, "§l§eAttributes", centerX, y, -1);
        y += 18;

        // Wir rufen jetzt unsere neue renderStatLine für die 6 Kern-Stats auf
        renderStatLine(g, centerX, y, "Strength");
        y += 11;
        renderStatLine(g, centerX, y, "Dexterity");
        y += 11;
        renderStatLine(g, centerX, y, "Constitution");
        y += 11;
        renderStatLine(g, centerX, y, "Intelligence");
        y += 11;
        renderStatLine(g, centerX, y, "Wisdom");
        y += 11;
        renderStatLine(g, centerX, y, "Charisma");

        y += 15;

        // ── PROFICIENCIES ──
        g.drawCenteredString(this.font, "§l§eProficiencies", centerX, y, -1);
        y += 12;
        // Fix: Falls dein Variablenname in DndModVariables "Proficiencys" ist:
        String profs = (vars.Proficiencys == null || vars.Proficiencys.isEmpty()) ? "None" : vars.Proficiencys.replace("_", " ").replace(",", ", ");
        y = renderWrappedText(g, profs, centerX, y, 220, "§7");

        y += 10;

        // ── PERSONALITY & BACKSTORY (bleiben gleich) ──
        g.drawCenteredString(this.font, "§l§bPersonality", centerX, y, -1);
        y += 12;
        String personality = (vars.PlayerPersonality == null || vars.PlayerPersonality.isEmpty()) ? "No personality traits defined." : vars.PlayerPersonality;
        y = renderWrappedText(g, personality, centerX, y, 220, "§f");

        y += 10;
        g.drawCenteredString(this.font, "§l§dBackstory", centerX, y, -1);
        y += 12;
        String story = (vars.PlayerStory == null || vars.PlayerStory.isEmpty()) ? "This adventurer's past is shrouded in mystery..." : vars.PlayerStory;
        renderWrappedText(g, story, centerX, y, 240, "§o§7");
    }

    // Helper Methode um Text mit Zeilenumbruch zentriert zu rendern
    private int renderWrappedText(GuiGraphics g, String text, int centerX, int y, int width, String colorCode) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(colorCode + text), width);
        for (FormattedCharSequence line : lines) {
            g.drawCenteredString(this.font, line, centerX, y, -1);
            y += 10;
        }
        return y;
    }

    private void renderStatLine(GuiGraphics g, int centerX, int y, String label) {
        if (vars == null) return;

        // Wert aus den Variablen holen
        double value = switch (label) {
            case "Strength" -> vars.Strength;
            case "Dexterity" -> vars.Dexterity;
            case "Constitution" -> vars.Constitution;
            case "Intelligence" -> vars.Intelligence;
            case "Wisdom" -> vars.Wisdom;
            case "Charisma" -> vars.Charisma;
            default -> 10.0;
        };

        int total = (int) value;
        // D&D Modifier berechnen: (Score - 10) / 2
        int modifier = Math.floorDiv(total - 10, 2);
        String bonusStr = (modifier >= 0 ? "+" : "") + modifier;

        // Anzeige: "Strength: 15 (+2)"
        String fullLine = "§7" + label + ": §f" + total + " §a(" + bonusStr + ")";
        g.drawCenteredString(this.font, fullLine, centerX, y, -1);
    }

    private String formatVal(String label, double val) {
        if (label.contains("Speed")) return String.format("%.0f%%", val * 100);
        return String.format("%.1f", val);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }
}