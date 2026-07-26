package net.luderspieler.dnd.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.luderspieler.dnd.character.LevelEvents;
import net.luderspieler.dnd.character.choices.ChoiceUpdateSystem;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.aUtils.AbilityUtils;
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

    private static final SuggestionProvider<CommandSourceStack> ABILITY_SUGGESTIONS = (context, builder) -> {
        List<String> abilities = new ArrayList<>();
        for (Ability a : Ability.values()) {
            abilities.add(a.name());
        }
        return SharedSuggestionProvider.suggest(abilities, builder);
    };

    private static final SuggestionProvider<CommandSourceStack> VARIABLE_SUGGESTIONS = (context, builder) -> {
        List<String> vars = new ArrayList<>();
        for (java.lang.reflect.Field field : DndModVariables.PlayerVariables.class.getDeclaredFields()) {
            if (!field.getName().startsWith("_")) {
                vars.add(field.getName());
            }
        }
        return SharedSuggestionProvider.suggest(vars, builder);
    };

    // ════════════════════════════════════════════════════════════════════════════
    //  REGISTRATION
    // ════════════════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {

        // ── /dnd variable <field> [value] ──
        LiteralArgumentBuilder<CommandSourceStack> variableNode = Commands.literal("variable");
        setupVariableNode(variableNode);

        // ── /dnd abilities ──
        LiteralArgumentBuilder<CommandSourceStack> abilitiesNode = Commands.literal("abilities");
        setupAbilitiesNode(abilitiesNode);

        // ── /dnd update ──
        LiteralArgumentBuilder<CommandSourceStack> updateNode = Commands.literal("update");
        setupUpdateNode(updateNode);

        // ── /dnd spells ... ──
        LiteralArgumentBuilder<CommandSourceStack> spellsNode = Commands.literal("spells");
        setupSpellsNode(spellsNode);

        // ── /dnd slots ──
        LiteralArgumentBuilder<CommandSourceStack> slotsNode = Commands.literal("slots");
        setupSlotsNode(slotsNode);

        LiteralArgumentBuilder<CommandSourceStack> levelNode = Commands.literal("Level")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 20))
                                .executes(c -> setLevelAndSync(c.getSource(), EntityArgument.getPlayer(c, "player"), IntegerArgumentType.getInteger(c, "value")))));


        event.getDispatcher().register(Commands.literal("dnd")
                .requires(s -> s.hasPermission(2))
                .then(variableNode)
                .then(abilitiesNode)
                .then(updateNode)
                .then(spellsNode)
                .then(slotsNode)
                .then(levelNode));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  VARIABLE NODE SETUP
    // ════════════════════════════════════════════════════════════════════════════

    private static void setupVariableNode(LiteralArgumentBuilder<CommandSourceStack> node) {

        // Iteriert flexibel über alle Variablen in PlayerVariables
        for (java.lang.reflect.Field field : DndModVariables.PlayerVariables.class.getDeclaredFields()) {

            // Interne Variablen (die mit '_' beginnen) wie '_syncDirty' überspringen
            if (field.getName().startsWith("_")) continue;

            String fieldName = field.getName();
            Class<?> type = field.getType();

            // ── String fields ──
            if (type == String.class) {
                node.then(Commands.literal(fieldName)
                        .executes(c -> readVariable(c.getSource().getPlayerOrException(), fieldName))
                        .then(Commands.argument("v", StringArgumentType.greedyString())
                                .executes(c -> updateStringVariable(
                                        c.getSource().getPlayerOrException(),
                                        fieldName,
                                        StringArgumentType.getString(c, "v")))));
            }
            // ── Double fields ──
            else if (type == double.class || type == Double.class) {
                node.then(Commands.literal(fieldName)
                        .executes(c -> readVariable(c.getSource().getPlayerOrException(), fieldName))
                        .then(Commands.argument("v", DoubleArgumentType.doubleArg())
                                .executes(c -> updateDoubleVariable(
                                        c.getSource().getPlayerOrException(),
                                        fieldName,
                                        DoubleArgumentType.getDouble(c, "v")))));
            }
            // ── Boolean fields ──
            else if (type == boolean.class || type == Boolean.class) {
                node.then(Commands.literal(fieldName)
                        .executes(c -> readVariable(c.getSource().getPlayerOrException(), fieldName))
                        .then(Commands.argument("v", BoolArgumentType.bool())
                                .executes(c -> updateBoolVariable(
                                        c.getSource().getPlayerOrException(),
                                        fieldName,
                                        BoolArgumentType.getBool(c, "v")))));
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  ABILITIES NODE SETUP
    // ════════════════════════════════════════════════════════════════════════════

    private static void setupAbilitiesNode(LiteralArgumentBuilder<CommandSourceStack> node) {

        // ── /dnd abilities add <player> <ability> ──
        node.then(Commands.literal("add")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests(ABILITY_SUGGESTIONS)
                                .executes(c -> handleAbilityAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "player"),
                                        StringArgumentType.getString(c, "ability"),
                                        "add")))));

        // ── /dnd abilities remove <player> <ability> ──
        node.then(Commands.literal("remove")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests(ABILITY_SUGGESTIONS)
                                .executes(c -> handleAbilityAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "player"),
                                        StringArgumentType.getString(c, "ability"),
                                        "remove")))));

        // ── /dnd abilities clear <player> ──
        node.then(Commands.literal("clear")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> clearAbilities(
                                c.getSource(),
                                EntityArgument.getPlayer(c, "player")))));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  UPDATE NODE SETUP
    // ════════════════════════════════════════════════════════════════════════════

    // ════════════════════════════════════════════════════════════════════════════
    //  SPELLS NODE SETUP (keep existing structure)
    // ════════════════════════════════════════════════════════════════════════════

    private static void setupSpellsNode(LiteralArgumentBuilder<CommandSourceStack> node) {

        node.then(Commands.literal("learn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.greedyString())
                                .suggests(SPELL_SUGGESTIONS)
                                .executes(c -> handleSpellAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"),
                                        "learn")))));

        node.then(Commands.literal("forceLearn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.greedyString())
                                .suggests(SPELL_SUGGESTIONS)
                                .executes(c -> handleSpellAction(
                                        c.getSource(),
                                        EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"),
                                        "force")))));

        node.then(Commands.literal("unlearn")
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
        node.then(Commands.literal("clear").then(targetArg));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SLOTS NODE SETUP
    // ════════════════════════════════════════════════════════════════════════════

    private static void setupSlotsNode(LiteralArgumentBuilder<CommandSourceStack> node) {
        var playerArg = Commands.argument("player", EntityArgument.player());

        // /dnd slots set <player> Grade_1 <value>
        for (int i = 1; i <= 9; i++) {
            final int grade = i;
            playerArg.then(Commands.literal("Grade_" + i)
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 9))
                            .executes(c -> setSpellSlot(c.getSource(), EntityArgument.getPlayer(c, "player"), grade, IntegerArgumentType.getInteger(c, "value")))));
        }

        node.then(Commands.literal("set").then(playerArg));

        // /dnd slots refill <player> -> Macht die Slots VOLL (Maximum)
        node.then(Commands.literal("refill")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> refillSpellSlots(c.getSource(), EntityArgument.getPlayer(c, "player")))));

        // /dnd slots clear <player> -> Macht die Slots LEER (Null)
        node.then(Commands.literal("clear")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> clearSpellSlots(c.getSource(), EntityArgument.getPlayer(c, "player")))));
    }


    private static void setupUpdateNode(LiteralArgumentBuilder<CommandSourceStack> node) {
        // /dnd update Choices <player>
        node.then(Commands.literal("Choices")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> {
                            ServerPlayer player = EntityArgument.getPlayer(c, "player");
                            ChoiceUpdateSystem.updateChoices(player);
                            c.getSource().sendSuccess(() -> Component.literal("§aChoices for " + player.getName().getString() + " recalculated."), true);
                            return 1;
                        })));

        // /dnd update Abilities <player>
        node.then(Commands.literal("Abilities")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> {
                            ServerPlayer player = EntityArgument.getPlayer(c, "player");
                            AbilityUtils.updateClassAbilities(player);
                            c.getSource().sendSuccess(() -> Component.literal("§aAbilities for " + player.getName().getString() + " updated."), true);
                            return 1;
                        })));

        node.then(Commands.literal("Choices")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> {
                            ChoiceUpdateSystem.updateChoices(EntityArgument.getPlayer(c, "player"));
                            c.getSource().sendSuccess(() -> Component.literal("§aChoices updated."), true);
                            return 1;
                        })));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  HANDLERS
    // ════════════════════════════════════════════════════════════════════════════


    private static int setLevelAndSync(CommandSourceStack source, ServerPlayer player, int level) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        vars.PlayerLevel      = (double) level;
        vars.PlayerXP         = LevelEvents.getRequiredXP(level);
        vars.ProficiencyBonus = LevelEvents.getProficiencyBonus(level);
        vars.markSyncDirty();

        AbilityUtils.updateClassAbilities(player);

        AbilityUtils.updateRaceAbilitiesForLevel(player, level);

        // Level-gebundene Subklassen-Abilities (analog zu LevelEvents.updatePlayerLevel())
        AbilityUtils.updateSubclassAbilitiesForLevel(player, level);

        ChoiceUpdateSystem.updateChoices(player);
        CharacterCreationPacket.applyAttrs(player);

        source.sendSuccess(
                () -> Component.literal("§aLevel set to " + level + " — stats, abilities and choices updated."),
                true);
        return 1;
    }

    // ── VARIABLE HANDLERS ────────────────────────────────────────────────────────

    private static int readVariable(ServerPlayer p, String field) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            Object value = vars.getClass().getField(field).get(vars);
            p.sendSystemMessage(Component.literal("§e" + field + "§r = §f" + value));
            return 1;
        } catch (Exception e) {
            p.sendSystemMessage(Component.literal("§cFailed to read " + field + ": " + e.getMessage()));
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

    // ── ABILITY HANDLERS ─────────────────────────────────────────────────────────

    private static int handleAbilityAction(CommandSourceStack source, ServerPlayer player,
                                           String abilityName, String action) {
        try {
            Ability ability = Ability.valueOf(abilityName.toUpperCase());

            if (action.equals("add")) {
                if (AbilityUtils.hasAbility(player, ability)) {
                    source.sendFailure(Component.literal("§cPlayer already has ability: " + ability.name()));
                    return 0;
                }
                AbilityUtils.addAbility(player, ability);
                source.sendSuccess(() -> Component.literal("§aAdded ability: §f" + ability.getDisplayName()), true);
                return 1;
            } else if (action.equals("remove")) {
                if (!AbilityUtils.hasAbility(player, ability)) {
                    source.sendFailure(Component.literal("§cPlayer does not have ability: " + ability.name()));
                    return 0;
                }
                AbilityUtils.removeAbility(player, ability);
                source.sendSuccess(() -> Component.literal("§aRemoved ability: §f" + ability.getDisplayName()), true);
                return 1;
            }
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("§cAbility '" + abilityName + "' does not exist!"));
            return 0;
        }
        return 0;
    }

    private static int clearAbilities(CommandSourceStack source, ServerPlayer player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.Abilities = "";
        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal("§aCleared all abilities."), true);
        return 1;
    }

    // ── UPDATE HANDLERS ──────────────────────────────────────────────────────────

    private static int updateAttributes(CommandSourceStack source, ServerPlayer player) {
        try {
            // applyAttrs wird von anderswo aufgerufen - hier nur die Nachricht
            source.sendSuccess(() -> Component.literal("§aUpdating attributes..."), true);
            // TODO: Rufe applyAttrs(player, null, false) auf wenn die Methode zugänglich ist
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cFailed to update attributes: " + e.getMessage()));
            return 0;
        }
    }

    private static int updateAbilities(CommandSourceStack source, ServerPlayer player) {
        try {
            AbilityUtils.updateClassAbilities(player);
            source.sendSuccess(() -> Component.literal("§aUpdated class abilities for level " + (int)player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cFailed to update abilities: " + e.getMessage()));
            return 0;
        }
    }

    // ── SPELL HANDLERS (existing logic) ──────────────────────────────────────────

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

    // ── SPELL SLOTS HANDLERS ─────────────────────────────────────────────────────

    private static int setSpellSlot(CommandSourceStack source, ServerPlayer player, int grade, int value) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String currentSlots = vars.Spellslots;

        // Validierung: Grad 1 bis 9
        if (grade < 1 || grade > 9) {
            source.sendFailure(Component.literal("§cGrade must be between 1 and 9!"));
            return 0;
        }

        // Falls String ungültig: Initialisiere 9 Nullen
        if (currentSlots == null || currentSlots.length() != 9 || currentSlots.contains(",")) {
            currentSlots = "000000000";
        }

        // Wert auf maximal 9 deckeln
        int finalValue = Math.min(9, Math.max(0, value));

        char[] slotsChars = currentSlots.toCharArray();
        slotsChars[grade - 1] = Character.forDigit(finalValue, 10);

        vars.Spellslots = new String(slotsChars);
        vars.markSyncDirty();

        source.sendSuccess(() -> Component.literal("§aSet spell slot Grade_" + grade + " to §f" + finalValue), true);
        return 1;
    }

    private static int refillSpellSlots(CommandSourceStack source, ServerPlayer player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass.replace("\"", ""));

        if (classDef == null) {
            source.sendFailure(Component.literal("§cClass not found!"));
            return 0;
        }

        // Nutzt die Slot-Tabelle der Klasse für das aktuelle Level
        int level = (int) vars.PlayerLevel;
        int[][] table = classDef.getSpellSlots();

        if (level < 0 || level >= table.length) {
            vars.Spellslots = "000000000";
        } else {
            int[] maxSlots = table[level];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                sb.append(Math.min(9, maxSlots[i]));
            }
            vars.Spellslots = sb.toString();
        }

        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal("§aRefilled spell slots to maximum."), true);
        return 1;
    }

    private static int clearSpellSlots(CommandSourceStack source, ServerPlayer player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Einfach alles auf 0 setzen
        vars.Spellslots = "000000000";

        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal("§6All spell slots cleared to 0."), true);
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