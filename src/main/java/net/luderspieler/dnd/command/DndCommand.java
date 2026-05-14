package net.luderspieler.dnd.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.LevelEvents;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellCasters;
import net.luderspieler.dnd.spells.Spells;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.luderspieler.dnd.character.choices.ChoiceRegistry.addChoicesForLevel;

@EventBusSubscriber
public class DndCommand {

    private static final SuggestionProvider<CommandSourceStack> SPELL_SUGGESTIONS = (context, builder) -> {
        List<String> allSpells = new ArrayList<>();
        allSpells.add("*");
        Stream.of(
                Spells.Cantrip.values(), Spells.Grade1.values(), Spells.Grade2.values(),
                Spells.Grade3.values(), Spells.Grade4.values(), Spells.Grade5.values(),
                Spells.Grade6.values(), Spells.Grade7.values(), Spells.Grade8.values(),
                Spells.Grade9.values()
        ).flatMap(Arrays::stream).forEach(e -> allSpells.add(e.name()));
        return SharedSuggestionProvider.suggest(allSpells, builder);
    };

    // ── String variables ────────────────────────────────────────────────────────
    private static final String[] STRING_VARS = {
            "PlayerClass", "Spellslots", "PlayerSubrace", "PlayerName", "PlayerStory",
            "PlayerPersonality", "PlayerRace", "PlayerSubclass", "Proficiencys",
            "PreparedCantrips", "PreparedSpellsLVL1", "PreparedSpellsLVL2", "PreparedSpellsLVL3",
            "PreparedSpellsLVL4", "PreparedSpellsLVL5", "PreparedSpellsLVL6", "PreparedSpellsLVL7",
            "PreparedSpellsLVL8", "PreparedSpellsLVL9", "TargetingSpell", "targetUUIDS",
            "TargetingModeType", "AbilityData", "Charmer", "grabber", "Decaying_Focus", "ChoicesNeeded"
    };

    // ── Double variables ─────────────────────────────────────────────────────────
    // PlayerLevel is handled separately so applyAttrs can be called after setting it.
    private static final String[] DOUBLE_VARS = {
            "PlayerXP", "TargetingRange", "TargetingAmount",
            "Strength", "Dexterity", "Constitution",
            "Intelligence", "Wisdom", "Charisma", "ProficiencyBonus"
    };

    // ── Boolean variables ────────────────────────────────────────────────────────
    private static final String[] BOOL_VARS = {
            "FinishedCharacterCreation", "CanUseMagic", "TargetingMode"
    };

    // ════════════════════════════════════════════════════════════════════════════
    //  REGISTRATION
    // ════════════════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {

        // ── /dnd variable <field> [value] ──
        LiteralArgumentBuilder<CommandSourceStack> variableNode = Commands.literal("variable");
        setupVariableNode(variableNode);

        // ── /dnd spells ... ──
        LiteralArgumentBuilder<CommandSourceStack> spellsNode = Commands.literal("spells");

        spellsNode.then(Commands.literal("learn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.greedyString())
                                .suggests(SPELL_SUGGESTIONS)
                                .executes(c -> handleSpellAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"),
                                        "learn")))));

        spellsNode.then(Commands.literal("forceLearn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.greedyString())
                                .suggests(SPELL_SUGGESTIONS)
                                .executes(c -> handleSpellAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"),
                                        "force")))));

        spellsNode.then(Commands.literal("unlearn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.greedyString())
                                .suggests(SPELL_SUGGESTIONS)
                                .executes(c -> handleSpellAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"),
                                        "unlearn")))));

        var targetArg = Commands.argument("target", EntityArgument.player())
                .then(Commands.literal("all")
                        .executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), "all")))
                .then(Commands.literal("Cantrip")
                        .executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), "0")));
        for (int i = 1; i <= 9; i++) {
            final int grade = i;
            targetArg.then(Commands.literal("Grade_" + i)
                    .executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), String.valueOf(grade))));
        }
        spellsNode.then(Commands.literal("clear").then(targetArg));

        event.getDispatcher().register(Commands.literal("dnd")
                .requires(s -> s.hasPermission(2))
                .then(variableNode)
                .then(spellsNode));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  VARIABLE NODE SETUP
    // ════════════════════════════════════════════════════════════════════════════

    private static void setupVariableNode(LiteralArgumentBuilder<CommandSourceStack> node) {

        // ── String fields ──
        for (String field : STRING_VARS) {
            node.then(Commands.literal(field)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), field))
                    .then(Commands.argument("v", StringArgumentType.greedyString())
                            .executes(c -> updateStringVariable(
                                    c.getSource().getPlayerOrException(),
                                    field,
                                    StringArgumentType.getString(c, "v")))));
        }

        // ── Double fields (generic, no side-effects) ──
        for (String field : DOUBLE_VARS) {
            node.then(Commands.literal(field)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), field))
                    .then(Commands.argument("v", DoubleArgumentType.doubleArg())
                            .executes(c -> updateDoubleVariable(
                                    c.getSource().getPlayerOrException(),
                                    field,
                                    DoubleArgumentType.getDouble(c, "v")))));
        }

        // ── PlayerLevel — special: also calls applyAttrs and updates ProficiencyBonus ──
        node.then(Commands.literal("PlayerLevel")
                .executes(c -> readVariable(c.getSource().getPlayerOrException(), "PlayerLevel"))
                .then(Commands.argument("v", DoubleArgumentType.doubleArg(1, 20))
                        .executes(c -> setPlayerLevel(
                                c.getSource().getPlayerOrException(),
                                DoubleArgumentType.getDouble(c, "v"),
                                c.getSource()))));

        // ── Boolean fields ──
        for (String field : BOOL_VARS) {
            node.then(Commands.literal(field)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), field))
                    .then(Commands.argument("v", BoolArgumentType.bool())
                            .executes(c -> updateBoolVariable(
                                    c.getSource().getPlayerOrException(),
                                    field,
                                    BoolArgumentType.getBool(c, "v")))));
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  VARIABLE HANDLERS
    // ════════════════════════════════════════════════════════════════════════════

    private static int readVariable(ServerPlayer p, String field) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            Object v = vars.getClass().getField(field).get(vars);
            p.sendSystemMessage(Component.literal("§bCurrent " + field + ": §f" + v));
            return 1;
        } catch (Exception e) {
            p.sendSystemMessage(Component.literal("§cField not found: " + field));
            return 0;
        }
    }

    private static int updateStringVariable(ServerPlayer p, String field, String value) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            vars.getClass().getField(field).set(vars, value);
            vars.markSyncDirty();
            p.sendSystemMessage(Component.literal("§aSet §e" + field + " §ato §f" + value));
            return 1;
        } catch (Exception e) {
            p.sendSystemMessage(Component.literal("§cFailed to set " + field + ": " + e.getMessage()));
            return 0;
        }
    }

    private static int updateDoubleVariable(ServerPlayer p, String field, double value) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            vars.getClass().getField(field).set(vars, value);
            vars.markSyncDirty();
            p.sendSystemMessage(Component.literal("§aSet §e" + field + " §ato §f" + value));
            return 1;
        } catch (Exception e) {
            p.sendSystemMessage(Component.literal("§cFailed to set " + field + ": " + e.getMessage()));
            return 0;
        }
    }

    private static int updateBoolVariable(ServerPlayer p, String field, boolean value) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            vars.getClass().getField(field).set(vars, value);
            vars.markSyncDirty();
            p.sendSystemMessage(Component.literal("§aSet §e" + field + " §ato §f" + value));
            return 1;
        } catch (Exception e) {
            p.sendSystemMessage(Component.literal("§cFailed to set " + field + ": " + e.getMessage()));
            return 0;
        }
    }

    // ── PlayerLevel with side effects ────────────────────────────────────────────

    private static int setPlayerLevel(ServerPlayer p, double newLevel, CommandSourceStack source) {
        DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);

        int oldLevel = (int) vars.PlayerLevel;
        int targetLevel = (int) newLevel;

        // 1. Level setzen
        vars.PlayerLevel = newLevel;

        // 2. XP auf das Minimum für dieses Level setzen (via LevelEvents Helper)
        vars.PlayerXP = LevelEvents.getRequiredXP(targetLevel);

        // 3. Proficiency Bonus aktualisieren
        vars.ProficiencyBonus = LevelEvents.getProficiencyBonus(targetLevel);

        // 4. Choices hinzufügen, falls man Level aufsteigt (nicht bei Downlevel)
        if (targetLevel > oldLevel) {
            for (int lvl = oldLevel + 1; lvl <= targetLevel; lvl++) {
                addChoicesForLevel(vars, vars.PlayerClass, lvl);
            }
        }

        // 5. Attribute neu berechnen
        CharacterCreationPacket.applyAttrs(p, null, false);

        vars.markSyncDirty();

        source.sendSuccess(() -> Component.literal(
                "§aSet §ePlayerLevel §ato §f" + targetLevel +
                        " §7(XP: " + vars.PlayerXP + ", ProfBonus: +" + (int) vars.ProficiencyBonus + ")"
        ), true);

        p.displayClientMessage(Component.literal("§6§lLevel set to " + targetLevel + "!"), false);

        return 1;
    }

    /**
     * Standard D&D 5e proficiency bonus by character level.
     */
    private static int getProficiencyBonus(int level) {
        if (level >= 17) return 6;
        if (level >= 13) return 5;
        if (level >= 9)  return 4;
        if (level >= 5)  return 3;
        return 2; // levels 1-4
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SPELL HANDLERS
    // ════════════════════════════════════════════════════════════════════════════

    private static int handleSpellAction(CommandSourceStack source, ServerPlayer player,
                                         String spellName, String action) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass);

        if (classDef == null) {
            source.sendFailure(Component.literal("§cClass '" + vars.PlayerClass + "' not found!"));
            return 0;
        }

        // ── Wildcard: add/force-add all finished spells ──
        if (spellName.trim().equals("*") && (action.equals("learn") || action.equals("force"))) {
            int count = 0;
            for (String s : SpellCasters.FINISHED_SPELLS) {
                int g = classDef.getGradeOfSpell(s);
                if (g == -1) continue;

                if (action.equals("learn")) {
                    boolean isInList = classDef.getSpellList().stream()
                            .anyMatch(e -> e.name().equalsIgnoreCase(s));
                    if (!isInList) continue;
                }

                String current = getListByGrade(vars, g);
                if (Arrays.asList(current.split(",")).contains(s)) continue;
                if (action.equals("learn") && !classDef.canPrepareMore(current, (int) vars.PlayerLevel, g)) continue;

                String updated = (current.isEmpty() || current.equals("\"\"")) ? s : current + "," + s;
                setListByGrade(vars, g, updated);
                count++;
            }
            vars.markSyncDirty();
            int finalCount = count;
            source.sendSuccess(() -> Component.literal("§aAdded " + finalCount + " spells."), true);
            return count;
        }

        // ── Single spell ──
        final String spellId = spellName.toUpperCase().trim();
        int grade = classDef.getGradeOfSpell(spellId);
        if (grade == -1) {
            source.sendFailure(Component.literal("§cSpell '" + spellId + "' does not exist!"));
            return 0;
        }

        if (action.equals("learn")) {
            boolean inList = classDef.getSpellList().stream()
                    .anyMatch(e -> e.name().equalsIgnoreCase(spellId));
            if (!inList) {
                source.sendFailure(Component.literal(
                        "§cThis spell is not on the " + vars.PlayerClass + " spell list!"));
                return 0;
            }
        }

        String currentList = getListByGrade(vars, grade);
        boolean isKnown = Arrays.asList(currentList.split(",")).contains(spellId);

        if (action.equals("unlearn")) {
            if (!isKnown) {
                source.sendFailure(Component.literal("§cPlayer does not know this spell!"));
                return 0;
            }
            String newList = Arrays.stream(currentList.split(","))
                    .filter(s -> !s.equals(spellId))
                    .collect(Collectors.joining(","));
            setListByGrade(vars, grade, newList);
            vars.markSyncDirty();
            source.sendSuccess(() -> Component.literal("§aRemoved '" + spellId + "'."), true);
            return 1;
        }

        if (isKnown) {
            source.sendFailure(Component.literal("§cSpell already known!"));
            return 0;
        }

        if (action.equals("learn") && !classDef.canPrepareMore(currentList, (int) vars.PlayerLevel, grade)) {
            int max = classDef.getMaxPreparedForGrade((int) vars.PlayerLevel, grade);
            source.sendFailure(Component.literal(
                    "§cPreparation limit for Grade " + grade + " reached (" + max + ")!"));
            return 0;
        }

        String updated = (currentList.isEmpty() || currentList.equals("\"\""))
                ? spellId : currentList + "," + spellId;
        setListByGrade(vars, grade, updated);
        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal(
                "§aLearned '" + spellId + "' (Grade " + grade + ")."), true);
        return 1;
    }

    private static int clearSpells(CommandSourceStack source, ServerPlayer player, String mode) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (mode.equals("all")) {
            for (int i = 0; i <= 9; i++) setListByGrade(vars, i, "");
        } else {
            try {
                setListByGrade(vars, Integer.parseInt(mode), "");
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal("§aSpell list cleared."), true);
        return 1;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    private static String getListByGrade(DndModVariables.PlayerVariables v, int g) {
        return switch (g) {
            case 0 -> v.PreparedCantrips;
            case 1 -> v.PreparedSpellsLVL1;
            case 2 -> v.PreparedSpellsLVL2;
            case 3 -> v.PreparedSpellsLVL3;
            case 4 -> v.PreparedSpellsLVL4;
            case 5 -> v.PreparedSpellsLVL5;
            case 6 -> v.PreparedSpellsLVL6;
            case 7 -> v.PreparedSpellsLVL7;
            case 8 -> v.PreparedSpellsLVL8;
            case 9 -> v.PreparedSpellsLVL9;
            default -> "";
        };
    }

    private static void setListByGrade(DndModVariables.PlayerVariables v, int g, String s) {
        switch (g) {
            case 0 -> v.PreparedCantrips = s;
            case 1 -> v.PreparedSpellsLVL1 = s;
            case 2 -> v.PreparedSpellsLVL2 = s;
            case 3 -> v.PreparedSpellsLVL3 = s;
            case 4 -> v.PreparedSpellsLVL4 = s;
            case 5 -> v.PreparedSpellsLVL5 = s;
            case 6 -> v.PreparedSpellsLVL6 = s;
            case 7 -> v.PreparedSpellsLVL7 = s;
            case 8 -> v.PreparedSpellsLVL8 = s;
            case 9 -> v.PreparedSpellsLVL9 = s;
        }
    }
}