package net.luderspieler.dnd.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.luderspieler.dnd.classes.ClassDefinition;
import net.luderspieler.dnd.classes.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

@EventBusSubscriber
public class DndCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> variableNode = Commands.literal("variable");
        setupVariableNode(variableNode);

        LiteralArgumentBuilder<CommandSourceStack> spellsNode = Commands.literal("spells");

        // 1. LEARN
        spellsNode.then(Commands.literal("learn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.string())
                                .executes(c -> handleSpellAction(c.getSource(), EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"), "learn")))));

        // 2. FORCELEARN
        spellsNode.then(Commands.literal("forceLearn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.string())
                                .executes(c -> handleSpellAction(c.getSource(), EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"), "force")))));

        // 3. UNLEARN
        spellsNode.then(Commands.literal("unlearn")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("spellname", StringArgumentType.string())
                                .executes(c -> handleSpellAction(c.getSource(), EntityArgument.getPlayer(c, "target"),
                                        StringArgumentType.getString(c, "spellname"), "unlearn")))));

        // 4. CLEAR
        var targetArg = Commands.argument("target", EntityArgument.player())
                .then(Commands.literal("all").executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), "all")))
                .then(Commands.literal("Cantrip").executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), "0")));

        for (int i = 1; i <= 9; i++) {
            final int grade = i;
            targetArg.then(Commands.literal("Grade_" + i)
                    .executes(c -> clearSpells(c.getSource(), EntityArgument.getPlayer(c, "target"), String.valueOf(grade))));
        }
        spellsNode.then(Commands.literal("clear").then(targetArg));

        event.getDispatcher().register(Commands.literal("dnd")
                .requires(s -> s.hasPermission(2))
                .then(variableNode)
                .then(spellsNode)
        );
    }

    private static int handleSpellAction(CommandSourceStack source, ServerPlayer player, String spellName, String action) {
        final String finalSpellName = spellName.toUpperCase();
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass);

        if (classDef == null) {
            source.sendFailure(Component.literal("§cClass '" + vars.PlayerClass + "' not found!"));
            return 0;
        }

        int grade = classDef.getGradeOfSpell(finalSpellName);
        if (grade == -1) {
            source.sendFailure(Component.literal("§cSpell '" + finalSpellName + "' does not exist in the system!"));
            return 0;
        }

        if (action.equals("learn")) {
            boolean isInSpellList = classDef.getSpellList().stream()
                    .anyMatch(e -> e.name().equalsIgnoreCase(finalSpellName));

            if (!isInSpellList) {
                source.sendFailure(Component.literal("§cThis spell is not part of the " + vars.PlayerClass + " spell list!"));
                return 0;
            }
        }

        String currentList = getListByGrade(vars, grade);
        boolean isKnown = Arrays.asList(currentList.split(",")).contains(finalSpellName);

        if (action.equals("unlearn")) {
            if (!isKnown) {
                source.sendFailure(Component.literal("§cPlayer does not know this spell!"));
                return 0;
            }
            String newList = Arrays.stream(currentList.split(","))
                    .filter(s -> !s.equals(finalSpellName))
                    .collect(Collectors.joining(","));
            setListByGrade(vars, grade, newList);
            vars.markSyncDirty();
            source.sendSuccess(() -> Component.literal("§aSpell '" + finalSpellName + "' removed."), true);
            return 1;
        }

        if (isKnown) {
            source.sendFailure(Component.literal("§cSpell already known!"));
            return 0;
        }

        if (action.equals("learn") && !classDef.canPrepareMore(currentList, (int) vars.PlayerLevel, grade)) {
            int max = classDef.getMaxPreparedForGrade((int) vars.PlayerLevel, grade);
            source.sendFailure(Component.literal("§cPreparation limit for Grade " + grade + " reached (" + max + ")!"));
            return 0;
        }

        String updatedList = (currentList.isEmpty() || currentList.equals("\"\"")) ? finalSpellName : currentList + "," + finalSpellName;
        setListByGrade(vars, grade, updatedList);
        vars.markSyncDirty();
        source.sendSuccess(() -> Component.literal("§aSpell '" + finalSpellName + "' learned (Grade " + grade + ")"), true);
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

    private static void setupVariableNode(LiteralArgumentBuilder<CommandSourceStack> node) {
        String[] strings = {"PlayerClass", "Spellslots", "PlayerSubrace", "PlayerName", "PlayerStory", "PlayerPersonality", "PlayerRace", "PlayerSubclass", "Proficiencys", "PreparedCantrips", "PreparedSpellsLVL1", "PreparedSpellsLVL2", "PreparedSpellsLVL3", "PreparedSpellsLVL4", "PreparedSpellsLVL5", "PreparedSpellsLVL6", "PreparedSpellsLVL7", "PreparedSpellsLVL8", "PreparedSpellsLVL9"};
        for (String f : strings) {
            node.then(Commands.literal(f)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), f))
                    .then(Commands.argument("v", StringArgumentType.greedyString())
                            .executes(c -> updateVariable(c.getSource().getPlayerOrException(), f, StringArgumentType.getString(c, "v")))));
        }

        String[] doubles = {"PlayerLevel", "PlayerXP"};
        for (String f : doubles) {
            node.then(Commands.literal(f)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), f))
                    .then(Commands.argument("v", DoubleArgumentType.doubleArg())
                            .executes(c -> updateVariable(c.getSource().getPlayerOrException(), f, DoubleArgumentType.getDouble(c, "v")))));
        }

        String[] bools = {"FinishedCharacterCreation", "CanUseMagic"};
        for (String f : bools) {
            node.then(Commands.literal(f)
                    .executes(c -> readVariable(c.getSource().getPlayerOrException(), f))
                    .then(Commands.argument("v", BoolArgumentType.bool())
                            .executes(c -> updateVariable(c.getSource().getPlayerOrException(), f, BoolArgumentType.getBool(c, "v")))));
        }
    }

    private static int readVariable(ServerPlayer p, String f) {
        try {
            Object v = p.getData(DndModVariables.PLAYER_VARIABLES).getClass().getField(f).get(p.getData(DndModVariables.PLAYER_VARIABLES));
            p.sendSystemMessage(Component.literal("§bCurrent " + f + ": " + v));
            return 1;
        } catch (Exception e) { return 0; }
    }

    private static int updateVariable(ServerPlayer p, String f, Object v) {
        try {
            DndModVariables.PlayerVariables vars = p.getData(DndModVariables.PLAYER_VARIABLES);
            vars.getClass().getField(f).set(vars, v);
            vars.markSyncDirty();
            p.sendSystemMessage(Component.literal("§aSet " + f + " to " + v));
            return 1;
        } catch (Exception e) { return 0; }
    }

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