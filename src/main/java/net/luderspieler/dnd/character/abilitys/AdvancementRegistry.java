package net.luderspieler.dnd.character.abilitys;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.world.entity.player.Player;

import java.util.*;

@SuppressWarnings("unused")
public class AdvancementRegistry {

    private static final Map<String, Map<Integer, List<Ability>>> REGISTRY = new HashMap<>();

    static {
        registerBarbarian();
        registerBard();
        registerCleric();
        registerDruid();
        registerFighter();
        registerMonk();
        registerPaladin();
        registerRanger();
        registerRogue();
        registerSorcerer();
        registerWarlock();
        registerWizard();
    }

    private static void register(String id, int level, Ability... abilities) {
        REGISTRY.computeIfAbsent(id, k -> new HashMap<>())
                .put(level, Arrays.asList(abilities));
    }

    private static void registerBarbarian() {
        String id = "barbarian";
        register(id, 1, Ability.RAGE, Ability.UNARMORED_DEFENSE_BARB, Ability.WEAPON_MASTERY);
        register(id, 2, Ability.DANGER_SENSE, Ability.RECKLESS_ATTACK);
        register(id, 3, Ability.SUBCLASS_FEATURE, Ability.PRIMAL_KNOWLEDGE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.EXTRA_ATTACK, Ability.FAST_MOVEMENT);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 7, Ability.INSTINCTIVE_POUNCE, Ability.FERAL_INSTINCT);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.BRUTAL_STRIKE);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 11, Ability.RELENTLESS_RAGE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 13, Ability.BRUTAL_STRIKE);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 15, Ability.PERSISTENT_RAGE);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 17, Ability.BRUTAL_STRIKE);
        register(id, 18, Ability.INDOMITABLE_MIGHT);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.PRIMAL_CHAMPION);
    }

    private static void registerBard() {
        String id = "bard";
        register(id, 1, Ability.SPELLCASTING, Ability.BARDIC_INSPIRATION);
        register(id, 2, Ability.JACK_OF_ALL_TRADES, Ability.EXPERTISE);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.FONT_OF_INSPIRATION);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 7, Ability.COUNTERCHARM);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.EXPERTISE);
        register(id, 10, Ability.MAGICAL_SECRETS);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.SUPERIOR_INSPIRATION);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.WORDS_OF_CREATION);
    }

    private static void registerCleric() {
        String id = "cleric";
        register(id, 1, Ability.SPELLCASTING, Ability.DIVINE_ORDER);
        register(id, 2, Ability.CHANNEL_DIVINITY);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.SEAR_UNDEAD, Ability.BLESSED_STRIKES);
        register(id, 6, Ability.SUBCLASS_FEATURE, Ability.CHANNEL_DIVINITY);
        register(id, 7, Ability.BLESSED_STRIKES);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.DIVINE_INTERVENTION);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.BLESSED_STRIKES);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.GREATER_DIVINE_INTERVENTION);
    }

    private static void registerDruid() {
        String id = "druid";
        register(id, 1, Ability.SPELLCASTING, Ability.PRIMAL_ORDER, Ability.WILD_SHAPE);
        register(id, 2, Ability.WILD_COMPANION);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.WILD_RESURGENCE);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 7, Ability.ELEMENTAL_FURY);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 15, Ability.ELEMENTAL_FURY);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.BEAST_SPELLS);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.ARCHDRUID);
    }

    private static void registerFighter() {
        String id = "fighter";
        register(id, 1, Ability.FIGHTING_STYLE, Ability.SECOND_WIND, Ability.WEAPON_MASTERY);
        register(id, 2, Ability.ACTION_SURGE, Ability.TACTICAL_MIND);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.EXTRA_ATTACK);
        register(id, 6, Ability.ATTRIBUTE_INCREASE);
        register(id, 7, Ability.TACTICAL_SHIFT, Ability.SUBCLASS_FEATURE);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.INDOMITABLE);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 11, Ability.EXTRA_ATTACK_2);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 13, Ability.TACTICAL_MASTER);
        register(id, 14, Ability.ATTRIBUTE_INCREASE);
        register(id, 15, Ability.SUBCLASS_FEATURE);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 17, Ability.ACTION_SURGE, Ability.INDOMITABLE);
        register(id, 18, Ability.SUBCLASS_FEATURE);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.EXTRA_ATTACK_3);
    }

    private static void registerMonk() {
        String id = "monk";
        register(id, 1, Ability.MARTIAL_ARTS, Ability.UNARMORED_DEFENSE_MONK);
        register(id, 2, Ability.MONKS_FOCUS, Ability.UNCANNY_METABOLISM, Ability.UNARMORED_MOVEMENT);
        register(id, 3, Ability.SUBCLASS_FEATURE, Ability.DEFLECT_ATTACKS);
        register(id, 4, Ability.ATTRIBUTE_INCREASE, Ability.SLOW_FALL);
        register(id, 5, Ability.EXTRA_ATTACK, Ability.STUNNING_STRIKE);
        register(id, 6, Ability.SUBCLASS_FEATURE, Ability.EMPOWERED_STRIKES);
        register(id, 7, Ability.EVASION);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.ACROBATIC_MOVEMENT);
        register(id, 10, Ability.SUBCLASS_FEATURE, Ability.HEIGHTENED_FOCUS, Ability.SELF_RESTORATION);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 13, Ability.DEFLECT_ENERGY);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 15, Ability.PERFECT_FOCUS);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.SUPERIOR_DEFENSE);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.BODY_AND_MIND);
    }

    private static void registerPaladin() {
        String id = "paladin";
        register(id, 1, Ability.LAY_ON_HANDS, Ability.SPELLCASTING, Ability.WEAPON_MASTERY);
        register(id, 2, Ability.PALADINS_SMITE, Ability.FIGHTING_STYLE);
        register(id, 3, Ability.SUBCLASS_FEATURE, Ability.CHANNEL_DIVINITY);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.EXTRA_ATTACK, Ability.FAITHFUL_STEED);
        register(id, 6, Ability.AURA_OF_PROTECTION);
        register(id, 7, Ability.SUBCLASS_FEATURE);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.ABJURE_FOES);
        register(id, 10, Ability.AURA_OF_COURAGE);
        register(id, 11, Ability.RADIANT_SMITE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.RESTORING_TOUCH);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.AURA_EXPANSION);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.OATH_PARAGON);
    }

    private static void registerRanger() {
        String id = "ranger";
        register(id, 1, Ability.SPELLCASTING, Ability.FAVORED_ENEMY, Ability.WEAPON_MASTERY);
        register(id, 2, Ability.FIGHTING_STYLE, Ability.DEFT_EXPLORER);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.EXTRA_ATTACK);
        register(id, 6, Ability.SUBCLASS_FEATURE, Ability.ROVING);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.EXPERTISE);
        register(id, 10, Ability.SUBCLASS_FEATURE, Ability.TIRELESS);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 13, Ability.RELENTLESS_HUNTER);
        register(id, 14, Ability.SUBCLASS_FEATURE, Ability.NATURE_S_VEIL);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 17, Ability.PRECISE_HUNTER);
        register(id, 18, Ability.FERAL_SENSES);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.FOE_SLAYER);
    }

    private static void registerRogue() {
        String id = "rogue";
        register(id, 1, Ability.SNEAK_ATTACK, Ability.THIEVES_CANT, Ability.EXPERTISE);
        register(id, 2, Ability.CUNNING_ACTION, Ability.WEAPON_MASTERY);
        register(id, 3, Ability.SUBCLASS_FEATURE, Ability.STEADY_AIM);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.CUNNING_STRIKE, Ability.UNCANNY_DODGE);
        register(id, 6, Ability.EXPERTISE);
        register(id, 7, Ability.EVASION);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.SUBCLASS_FEATURE);
        register(id, 10, Ability.ATTRIBUTE_INCREASE);
        register(id, 11, Ability.RELIABLE_TALENT);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 13, Ability.SUBCLASS_FEATURE, Ability.DEVIOUS_STRIKES);
        register(id, 14, Ability.BLINDSENSE);
        register(id, 15, Ability.SLIPPERY_MIND);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 17, Ability.SUBCLASS_FEATURE);
        register(id, 18, Ability.ELUSIVE);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.STROKE_OF_LUCK);
    }

    private static void registerSorcerer() {
        String id = "sorcerer";
        register(id, 1, Ability.SPELLCASTING, Ability.INNATE_SORCERY);
        register(id, 2, Ability.FONT_OF_MAGIC, Ability.METAMAGIC, Ability.SORCERY_POINTS);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.SORCEROUS_RESTORATION);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 7, Ability.SORCERY_INCARNATE);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.ARCANE_APOTHEOSIS);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.SORCEROUS_EMINENCE);
    }

    private static void registerWarlock() {
        String id = "warlock";
        register(id, 1, Ability.SPELLCASTING, Ability.PACT_MAGIC, Ability.PACT_BOON);
        register(id, 2, Ability.ELDRITCH_INVOCATIONS, Ability.MAGICAL_CUNNING);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.ELDRITCH_INVOCATIONS);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 7, Ability.ELDRITCH_INVOCATIONS);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 9, Ability.CONTACT_PATRON, Ability.ELDRITCH_INVOCATIONS);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 11, Ability.MYSTIC_ARCANUM);
        register(id, 12, Ability.ATTRIBUTE_INCREASE, Ability.ELDRITCH_INVOCATIONS);
        register(id, 13, Ability.MYSTIC_ARCANUM);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 15, Ability.MYSTIC_ARCANUM, Ability.ELDRITCH_INVOCATIONS);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 17, Ability.MYSTIC_ARCANUM);
        register(id, 18, Ability.ELDRITCH_INVOCATIONS);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.ELDRITCH_MASTER);
    }

    private static void registerWizard() {
        String id = "wizard";
        register(id, 1, Ability.SPELLCASTING, Ability.RITUAL_ADEPT, Ability.ARCANE_RECOVERY);
        register(id, 2, Ability.SCHOLAR);
        register(id, 3, Ability.SUBCLASS_FEATURE);
        register(id, 4, Ability.ATTRIBUTE_INCREASE);
        register(id, 5, Ability.MEMORIZE_SPELL);
        register(id, 6, Ability.SUBCLASS_FEATURE);
        register(id, 8, Ability.ATTRIBUTE_INCREASE);
        register(id, 10, Ability.SUBCLASS_FEATURE);
        register(id, 12, Ability.ATTRIBUTE_INCREASE);
        register(id, 14, Ability.SUBCLASS_FEATURE);
        register(id, 16, Ability.ATTRIBUTE_INCREASE);
        register(id, 18, Ability.SPELL_MASTERY);
        register(id, 19, Ability.EPIC_BOON);
        register(id, 20, Ability.SIGNATURE_SPELLS);
    }

    public static List<Ability> getAbilities(String classId, int level) {
        return REGISTRY.getOrDefault(classId.toLowerCase(), Collections.emptyMap())
                .getOrDefault(level, Collections.emptyList());
    }

    public static boolean playerHasAbility(Player player, Ability ability) {
        // 1. Variablen vom Spieler holen
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        String playerClass = vars.PlayerClass; // Deine Variable für die Klasse
        int playerLevel = (int) vars.PlayerLevel; // Deine Variable für das Level (falls double/float -> casten)

        // Validierung: Hat der Spieler überhaupt eine Klasse?
        if (playerClass == null || playerClass.isEmpty()) {
            return false;
        }

        // 2. Nutze die bereits bestehende Logik, um alle Level bis zum aktuellen zu prüfen
        Map<Integer, List<Ability>> classMap = REGISTRY.get(playerClass.toLowerCase());
        if (classMap == null) return false;

        for (int lvl = 1; lvl <= playerLevel; lvl++) {
            List<Ability> abilities = classMap.get(lvl);
            if (abilities != null && abilities.contains(ability)) {
                return true;
            }
        }

        return false;
    }
}