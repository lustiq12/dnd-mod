package net.luderspieler.dnd.character.choices;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ChoiceExecutor {
    public static void apply(Player player, String choiceID, String selectedValue) {
        switch (choiceID) {

            case "Attribute Increase" -> {
                switch (selectedValue) {
                    case "Strength + 2" -> player.displayClientMessage(Component.literal("attribute increase, Strength + 2"), false);
                    case "Dexterity + 2" -> player.displayClientMessage(Component.literal("attribute increase, Dexterity + 2"), false);
                    case "Constitution + 2" -> player.displayClientMessage(Component.literal("attribute increase, Constitution + 2"), false);
                    case "Intelligence + 2" -> player.displayClientMessage(Component.literal("attribute increase, Intelligence + 2"), false);
                    case "Wisdom + 2" -> player.displayClientMessage(Component.literal("attribute increase, Wisdom + 2"), false);
                    case "Charisma + 2" -> player.displayClientMessage(Component.literal("attribute increase, Charisma + 2"), false);
                }
            }

            case "Fighting Style" -> {
                switch (selectedValue) {
                    case "Archery" -> player.displayClientMessage(Component.literal("fighting style, Archery"), false);
                    case "Defense" -> player.displayClientMessage(Component.literal("fighting style, Defense"), false);
                    case "Dueling" -> player.displayClientMessage(Component.literal("fighting style, Dueling"), false);
                    case "Great Weapon Fighting" -> player.displayClientMessage(Component.literal("fighting style, Great Weapon Fighting"), false);
                    case "Protection" -> player.displayClientMessage(Component.literal("fighting style, Protection"), false);
                    case "Two-Weapon Fighting" -> player.displayClientMessage(Component.literal("fighting style, Two-Weapon Fighting"), false);
                }
            }

            case "Expertise" -> {
                switch (selectedValue) {
                    case "Acrobatics" -> player.displayClientMessage(Component.literal("expertise, Acrobatics"), false);
                    case "Athletics" -> player.displayClientMessage(Component.literal("expertise, Athletics"), false);
                    case "Stealth" -> player.displayClientMessage(Component.literal("expertise, Stealth"), false);
                    case "Perception" -> player.displayClientMessage(Component.literal("expertise, Perception"), false);
                    case "Insight" -> player.displayClientMessage(Component.literal("expertise, Insight"), false);
                    case "Persuasion" -> player.displayClientMessage(Component.literal("expertise, Persuasion"), false);
                }
            }

            case "Epic Boon" -> {
                switch (selectedValue) {
                    case "Boon of Combat Prowess" -> player.displayClientMessage(Component.literal("epic boon, Boon of Combat Prowess"), false);
                    case "Boon of Dimensional Travel" -> player.displayClientMessage(Component.literal("epic boon, Boon of Dimensional Travel"), false);
                    case "Boon of Fortitude" -> player.displayClientMessage(Component.literal("epic boon, Boon of Fortitude"), false);
                    case "Boon of Speed" -> player.displayClientMessage(Component.literal("epic boon, Boon of Speed"), false);
                }
            }

            case "Divine Order" -> {
                switch (selectedValue) {
                    case "Protector" -> player.displayClientMessage(Component.literal("divine order, Protector"), false);
                    case "Thaumaturge" -> player.displayClientMessage(Component.literal("divine order, Thaumaturge"), false);
                }
            }

            case "Primal Order" -> {
                switch (selectedValue) {
                    case "Magician" -> player.displayClientMessage(Component.literal("primal order, Magician"), false);
                    case "Warden" -> player.displayClientMessage(Component.literal("primal order, Warden"), false);
                }
            }

            case "Pact Boon" -> {
                switch (selectedValue) {
                    case "Pact of the Blade" -> player.displayClientMessage(Component.literal("pact boon, Pact of the Blade"), false);
                    case "Pact of the Chain" -> player.displayClientMessage(Component.literal("pact boon, Pact of the Chain"), false);
                    case "Pact of the Tome" -> player.displayClientMessage(Component.literal("pact boon, Pact of the Tome"), false);
                }
            }

            case "Eldritch Invocations" -> {
                switch (selectedValue) {
                    case "Agonizing Blast" -> player.displayClientMessage(Component.literal("eldritch invocations, Agonizing Blast"), false);
                    case "Armor of Shadows" -> player.displayClientMessage(Component.literal("eldritch invocations, Armor of Shadows"), false);
                    case "Eldritch Spear" -> player.displayClientMessage(Component.literal("eldritch invocations, Eldritch Spear"), false);
                    case "Fiendish Vigor" -> player.displayClientMessage(Component.literal("eldritch invocations, Fiendish Vigor"), false);
                }
            }

            case "Scholar" -> {
                switch (selectedValue) {
                    case "Arcana" -> player.displayClientMessage(Component.literal("scholar, Arcana"), false);
                    case "History" -> player.displayClientMessage(Component.literal("scholar, History"), false);
                    case "Investigation" -> player.displayClientMessage(Component.literal("scholar, Investigation"), false);
                    case "Nature" -> player.displayClientMessage(Component.literal("scholar, Nature"), false);
                    case "Religion" -> player.displayClientMessage(Component.literal("scholar, Religion"), false);
                }
            }

            case "Metamagic" -> {
                switch (selectedValue) {
                    case "Careful Spell" -> player.displayClientMessage(Component.literal("metamagic, Careful Spell"), false);
                    case "Distant Spell" -> player.displayClientMessage(Component.literal("metamagic, Distant Spell"), false);
                    case "Empowered Spell" -> player.displayClientMessage(Component.literal("metamagic, Empowered Spell"), false);
                    case "Quickened Spell" -> player.displayClientMessage(Component.literal("metamagic, Quickened Spell"), false);
                    case "Twinned Spell" -> player.displayClientMessage(Component.literal("metamagic, Twinned Spell"), false);
                }
            }

            case "Magical Secrets" -> {
                switch (selectedValue) {
                    case "Learn any Spell from other Classes" -> player.displayClientMessage(Component.literal("magical secrets, Learn any Spell from other Classes"), false);
                }
            }

            case "Subclass Feature" -> {
                switch (selectedValue) {
                    case "Open Subclass Selection Menu" -> player.displayClientMessage(Component.literal("subclass feature, Open Subclass Selection Menu"), false);
                }
            }

        }
    }
}
