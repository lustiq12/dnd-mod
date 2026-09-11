package net.luderspieler.dnd.npc;

import java.util.List;
import java.util.Map;

/**
 * Not wired up anywhere by default. Copy this pattern for your real presets and call
 * NpcPresetRegistry.register("DwarvenSmithEntity", preset) once from your mod's setup code,
 * after DwarvenSmithEntity has DATA_Configuration/DATA_Data fields (see NpcDataAccess).
 */
public class NpcPresetExamples {

    private NpcPresetExamples() {
    }

    public static NpcPreset exampleBlacksmithPreset() {
        NpcDialogNode greeting = new NpcDialogNode("greeting",
                "Welcome to my forge, traveler.",
                List.of(
                        new NpcDialogOption("What do you sell?", null, true),
                        new NpcDialogOption("Just passing by.", "farewell", false)
                ));

        NpcDialogNode farewell = new NpcDialogNode("farewell", "Safe travels, then.", List.of());

        return new NpcPreset(
                List.of("Little John", "Borin Ironhand", "Gundren Rockseeker"),
                "greeting",
                Map.of("greeting", greeting, "farewell", farewell),
                List.of(
                        new NpcTradeEntry("minecraft:emerald", 5, "", 0, "minecraft:iron_ingot", 3),
                        new NpcTradeEntry("minecraft:emerald", 8, "", 0, "minecraft:diamond", 1),
                        new NpcTradeEntry("minecraft:iron_ingot", 4, "minecraft:coal", 2, "minecraft:iron_sword", 1)
                ),
                2, 3,
                List.of("The forge never sleeps.", "Mind the sparks.")
        );
    }
}
