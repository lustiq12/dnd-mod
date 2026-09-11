package net.luderspieler.dnd.npc;

import java.util.List;
import java.util.Map;

/**
 * Contains all production NPC presets.
 *
 * Add new NPC types here and register them in registerAll().
 */
public final class NpcPresets {

    private NpcPresets() {
    }

    /**
     * Registers all built-in NPC presets.
     * Call this once during mod initialization.
     */
    public static void registerAll() {
        NpcPresetRegistry.register(
                "DwarvenSmithEntity",
                dwarvenSmith()
        );
    }

    // ============================================================
    // DWARVEN SMITH
    // ============================================================

    public static NpcPreset dwarvenSmith() {

        // --------------------------------------------------------
        // Greeting
        // --------------------------------------------------------

        NpcDialogNode greeting = new NpcDialogNode(
                "greeting",
                "Hmph. A customer. Welcome to my forge. Don't touch anything unless you want to lose a finger.",
                List.of(
                        new NpcDialogOption(
                                "Show me what you've got.",
                                "shop",
                                false
                        ),
                        new NpcDialogOption(
                                "I need something forged.",
                                "forge",
                                false
                        ),
                        new NpcDialogOption(
                                "Who are you?",
                                "about",
                                false
                        ),
                        new NpcDialogOption(
                                "Tell me about this place.",
                                "lore",
                                false
                        ),
                        new NpcDialogOption(
                                "I'll come back later.",
                                "farewell",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Shop
        // --------------------------------------------------------

        NpcDialogNode shop = new NpcDialogNode(
                "shop",
                "Tools, steel and weapons. Everything a sensible adventurer needs. The good stuff isn't cheap.",
                List.of(
                        new NpcDialogOption(
                                "Let's trade.",
                                null,
                                true
                        ),
                        new NpcDialogOption(
                                "What makes your equipment different?",
                                "quality",
                                false
                        ),
                        new NpcDialogOption(
                                "Back.",
                                "greeting",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Forge
        // --------------------------------------------------------

        NpcDialogNode forge = new NpcDialogNode(
                "forge",
                "A proper weapon takes more than hot metal and a hammer. Bring me good material, and I'll make something worth carrying.",
                List.of(
                        new NpcDialogOption(
                                "Can you forge weapons?",
                                "weapons",
                                false
                        ),
                        new NpcDialogOption(
                                "Can you forge armor?",
                                "armor",
                                false
                        ),
                        new NpcDialogOption(
                                "Can you repair equipment?",
                                "repair",
                                false
                        ),
                        new NpcDialogOption(
                                "Back.",
                                "greeting",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Weapons
        // --------------------------------------------------------

        NpcDialogNode weapons = new NpcDialogNode(
                "weapons",
                "Axes are my specialty. But I can work most weapons, provided you bring proper steel.",
                List.of(
                        new NpcDialogOption(
                                "What makes a good weapon?",
                                "quality",
                                false
                        ),
                        new NpcDialogOption(
                                "Back.",
                                "forge",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Armor
        // --------------------------------------------------------

        NpcDialogNode armor = new NpcDialogNode(
                "armor",
                "Heavy plates. Strong joints. Good balance. Armor should protect you without turning you into a walking anvil.",
                List.of(
                        new NpcDialogOption(
                                "Sounds expensive.",
                                "quality",
                                false
                        ),
                        new NpcDialogOption(
                                "Back.",
                                "forge",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Repair
        // --------------------------------------------------------

        NpcDialogNode repair = new NpcDialogNode(
                "repair",
                "Broken edge, damaged plate, bent fitting — all fixable. The price depends on how badly you've mistreated it.",
                List.of(
                        new NpcDialogOption(
                                "I'll bring something in.",
                                "greeting",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Quality
        // --------------------------------------------------------

        NpcDialogNode quality = new NpcDialogNode(
                "quality",
                "My grandfather taught me the old way. No shortcuts. Proper heat, proper tempering, proper steel. A blade made here should outlive its owner.",
                List.of(
                        new NpcDialogOption(
                                "Let's see the merchandise.",
                                "shop",
                                false
                        ),
                        new NpcDialogOption(
                                "Back to the forge.",
                                "forge",
                                false
                        ),
                        new NpcDialogOption(
                                "Tell me about your family.",
                                "family",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // About
        // --------------------------------------------------------

        NpcDialogNode about = new NpcDialogNode(
                "about",
                "Borin Stonehand. Third generation smith. My family has worked this forge longer than most kingdoms last.",
                List.of(
                        new NpcDialogOption(
                                "Tell me about your family.",
                                "family",
                                false
                        ),
                        new NpcDialogOption(
                                "How did you become a smith?",
                                "family",
                                false
                        ),
                        new NpcDialogOption(
                                "Interesting.",
                                "greeting",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Family
        // --------------------------------------------------------

        NpcDialogNode family = new NpcDialogNode(
                "family",
                "My father taught me. His father taught him. The old man used to say that a smith's hammer should ring louder than his voice.",
                List.of(
                        new NpcDialogOption(
                                "Sounds like a strict man.",
                                "lore",
                                false
                        ),
                        new NpcDialogOption(
                                "I understand.",
                                "greeting",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Lore
        // --------------------------------------------------------

        NpcDialogNode lore = new NpcDialogNode(
                "lore",
                "These mountains were built on iron. Every tunnel, every bridge and every fortress owes its strength to the forge. Remember that the next time you see dwarf stonework.",
                List.of(
                        new NpcDialogOption(
                                "I will remember that.",
                                "greeting",
                                false
                        ),
                        new NpcDialogOption(
                                "Tell me more about the forge.",
                                "forge",
                                false
                        )
                )
        );

        // --------------------------------------------------------
        // Farewell
        // --------------------------------------------------------

        NpcDialogNode farewell = new NpcDialogNode(
                "farewell",
                "Travel safely. And keep your blade dry.",
                List.of()
        );

        // --------------------------------------------------------
        // Dialog node map
        // --------------------------------------------------------

        Map<String, NpcDialogNode> dialogNodes = Map.ofEntries(
                Map.entry("greeting", greeting),
                Map.entry("shop", shop),
                Map.entry("forge", forge),
                Map.entry("weapons", weapons),
                Map.entry("armor", armor),
                Map.entry("repair", repair),
                Map.entry("quality", quality),
                Map.entry("about", about),
                Map.entry("family", family),
                Map.entry("lore", lore),
                Map.entry("farewell", farewell)
        );

        // --------------------------------------------------------
        // Trades
        // --------------------------------------------------------

        List<NpcTradeEntry> trades = List.of(
                // Materials
                new NpcTradeEntry(
                        "minecraft:emerald",
                        4,
                        "",
                        0,
                        "minecraft:iron_ingot",
                        8
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        7,
                        "",
                        0,
                        "minecraft:iron_block",
                        1
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        3,
                        "",
                        0,
                        "minecraft:coal",
                        16
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        12,
                        "",
                        0,
                        "minecraft:diamond",
                        1
                ),

                // Weapons
                new NpcTradeEntry(
                        "minecraft:emerald",
                        14,
                        "",
                        0,
                        "minecraft:iron_sword",
                        1
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        18,
                        "minecraft:iron_ingot",
                        5,
                        "minecraft:iron_axe",
                        1
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        24,
                        "",
                        0,
                        "minecraft:diamond_sword",
                        1
                ),

                // Armor
                new NpcTradeEntry(
                        "minecraft:emerald",
                        18,
                        "",
                        0,
                        "minecraft:iron_chestplate",
                        1
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        22,
                        "",
                        0,
                        "minecraft:iron_leggings",
                        1
                ),

                // Rare / special
                new NpcTradeEntry(
                        "minecraft:emerald",
                        32,
                        "minecraft:diamond",
                        2,
                        "minecraft:netherite_scrap",
                        1
                ),

                new NpcTradeEntry(
                        "minecraft:emerald",
                        40,
                        "",
                        0,
                        "minecraft:anvil",
                        1
                )
        );

        // --------------------------------------------------------
        // Final preset
        // --------------------------------------------------------

        return new NpcPreset(
                List.of(
                        "Borin Stonehand",
                        "Thrain Ironfist",
                        "Garrik Blackanvil",
                        "Dorin Deepforge",
                        "Kazrik Emberhammer"
                ),

                "greeting",

                dialogNodes,

                trades,

                4,
                7,

                List.of(
                        "The forge never sleeps.",
                        "Steel remembers every hammer blow.",
                        "A dull blade is an insult to its smith.",
                        "Keep your hands clear of the anvil.",
                        "Good steel. Good fire. That's all you need.",
                        "The mountains provide. The forge transforms.",
                        "A proper axe should feel like an extension of your arm."
                )
        );
    }
}