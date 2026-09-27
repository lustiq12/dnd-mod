package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.EnumMap;
import java.util.Map;

/**
 * Maps each Ability to its corresponding AbilityCategory.
 */
public class AbilityDefinitionRegistry {

    private static final Map<Ability, AbilityCategory> REGISTRY = new EnumMap<>(Ability.class);

    static {

        oneTime(
                Ability.PRIMAL_CHAMPION,
                Ability.BODY_AND_MIND,
                Ability.SPEED_BONUS_5
        );

        alwaysActive(
                Ability.UNARMORED_DEFENSE,
                Ability.FAST_MOVEMENT,
                Ability.UNARMORED_MOVEMENT,
                Ability.DARKVISION_60,
                Ability.DARKVISION_120,
                Ability.DWARVEN_TOUGHNESS,

                Ability.ROVING,

                Ability.WEAPON_MASTERY,
                Ability.FIGHTING_STYLE,
                Ability.FIGHTING_STYLE_PALADIN,
                Ability.FIGHTING_STYLE_RANGER,
                Ability.EXTRA_ATTACK_BARBARIAN,
                Ability.EXTRA_ATTACK_FIGHTER,
                Ability.EXTRA_ATTACK_MONK,
                Ability.EXTRA_ATTACK_PALADIN,
                Ability.EXTRA_ATTACK_RANGER,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_ONE,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_TWO,
                Ability.MARTIAL_ARTS,
                Ability.EMPOWERED_STRIKES,

                Ability.PERSISTENT_RAGE,
                Ability.INDOMITABLE_MIGHT,

                Ability.JACK_OF_ALL_TRADES,
                Ability.EXPERTISE,
                Ability.EXPERTISE_ROGUE,
                Ability.IMPROVED_EXPERTISE_ONE,
                Ability.IMPROVED_EXPERTISE_ROGUE_ONE,
                Ability.RELIABLE_TALENT,

                Ability.ELUSIVE,
                Ability.HEIGHTENED_FOCUS,

                Ability.RELENTLESS_HUNTER,
                Ability.PRECISE_HUNTER,
                Ability.FERAL_SENSES,
                Ability.FOE_SLAYER,

                Ability.DWARVEN_RESILIENCE,
                Ability.FEY_ANCESTRY,
                Ability.GNOME_CUNNING,
                Ability.BRAVE,
                Ability.CELESTIAL_RESISTANCE,
                Ability.FIENDISH_RESISTANCE,
                Ability.DAMAGE_RESISTANCE_DRAGONBORN
        );

        playerTriggered(
                Ability.RAGE,
                Ability.RECKLESS_ATTACK,
                Ability.BRUTAL_STRIKE,

                Ability.BARDIC_INSPIRATION,
                Ability.COUNTERCHARM,
                Ability.PEERLESS_SKILL,

                Ability.CHANNEL_DIVINITY,
                Ability.DIVINE_INTERVENTION,
                Ability.IMPROVED_DIVINE_INTERVENTION_ONE,

                Ability.WILD_SHAPE,
                Ability.WILD_COMPANION,
                Ability.WILD_RESURGENCE,

                Ability.SECOND_WIND,
                Ability.ACTION_SURGE,
                Ability.TACTICAL_MIND,
                Ability.INDOMITABLE,
                Ability.TACTICAL_MASTER,

                Ability.FOCUS_POINTS,
                Ability.UNCANNY_METABOLISM,
                Ability.DEFLECT_ATTACKS,
                Ability.SLOW_FALL,
                Ability.STUNNING_STRIKE,
                Ability.SELF_RESTORATION,
                Ability.SUPERIOR_DEFENSE,

                Ability.LAY_ON_HANDS,
                Ability.PALADINS_SMITE,
                Ability.RADIANT_SMITE,
                Ability.CHANNEL_DIVINITY_PALADIN,
                Ability.FAITHFUL_STEED,
                Ability.ABJURE_FOES,
                Ability.RESTORING_TOUCH,

                Ability.TIRELESS,
                Ability.NATURES_VEIL,

                Ability.CUNNING_ACTION,
                Ability.STEADY_AIM,
                Ability.CUNNING_STRIKE,
                Ability.UNCANNY_DODGE,
                Ability.DEVIOUS_STRIKES,
                Ability.STROKE_OF_LUCK,

                Ability.INNATE_SORCERY,
                Ability.FONT_OF_MAGIC,
                Ability.METAMAGIC,
                Ability.ARCANE_APOTHEOSIS,

                Ability.MAGICAL_CUNNING,
                Ability.CONTACT_PATRON,
                Ability.MYSTIC_ARCANUM,
                Ability.IMPROVED_MYSTIC_ARCANUM_ONE,
                Ability.IMPROVED_MYSTIC_ARCANUM_TWO,
                Ability.IMPROVED_MYSTIC_ARCANUM_THREE,
                Ability.ELDRITCH_MASTER,

                Ability.ARCANE_RECOVERY,
                Ability.MEMORIZE_SPELLS,

                Ability.STONE_CUNNING,
                Ability.BREATH_WEAPON,
                Ability.FLIGHT,
                Ability.HEALING_HANDS,
                Ability.CELESTIAL_REVELATION,
                Ability.LARGE_FORM,
                Ability.CLOUDS_JAUNT,
                Ability.FIRES_BURN,
                Ability.FROSTS_CHILL,
                Ability.HILLS_TUMBLE,
                Ability.STONES_ENDURANCE,
                Ability.STORMS_THUNDER,
                Ability.ADRENALINE_RUSH
        );

        selfTriggered(
                Ability.DANGER_SENSE,
                Ability.RELENTLESS_RAGE,
                Ability.RELENTLESS_ENDURANCE,
                Ability.EVASION,

                Ability.FERAL_INSTINCT,
                Ability.INSTINCTIVE_POUNCE,
                Ability.SUPERIOR_INSPIRATION,
                Ability.PERFECT_FOCUS,

                Ability.TACTICAL_SHIFT,
                Ability.STUDIED_ATTACKS,

                Ability.BLESSED_STRIKES,
                Ability.IMPROVED_BLESSED_STRIKES_ONE,
                Ability.ELEMENTAL_FURY,
                Ability.IMPROVED_ELEMENTAL_FURY_ONE,
                Ability.RADIANT_STRIKES,
                Ability.SNEAK_ATTACK,

                Ability.SMITE_UNDEAD,

                Ability.AURA_OF_PROTECTION,
                Ability.AURA_OF_COURAGE,

                Ability.SORCEROUS_RESTORATION,

                Ability.RESOURCEFUL,
                Ability.LUCKY,

                Ability.FIRE_DAMAGE_IMMUNITY, Ability.FIRE_DAMAGE_RESISTANCE,
                Ability.COLD_DAMAGE_IMMUNITY, Ability.COLD_DAMAGE_RESISTANCE,
                Ability.LIGHTNING_DAMAGE_IMMUNITY, Ability.LIGHTNING_DAMAGE_RESISTANCE,
                Ability.THUNDER_DAMAGE_IMMUNITY, Ability.THUNDER_DAMAGE_RESISTANCE,
                Ability.FORCE_DAMAGE_IMMUNITY, Ability.FORCE_DAMAGE_RESISTANCE,
                Ability.NECROTIC_DAMAGE_IMMUNITY, Ability.NECROTIC_DAMAGE_RESISTANCE,
                Ability.POISON_DAMAGE_IMMUNITY, Ability.POISON_DAMAGE_RESISTANCE,
                Ability.ACID_DAMAGE_IMMUNITY, Ability.ACID_DAMAGE_RESISTANCE,
                Ability.PSYCHIC_DAMAGE_IMMUNITY, Ability.PSYCHIC_DAMAGE_RESISTANCE,
                Ability.RADIANT_DAMAGE_IMMUNITY, Ability.RADIANT_DAMAGE_RESISTANCE,
                Ability.BLUDGEONING_DAMAGE_IMMUNITY, Ability.BLUDGEONING_DAMAGE_RESISTANCE,
                Ability.PIERCING_DAMAGE_IMMUNITY, Ability.PIERCING_DAMAGE_RESISTANCE,
                Ability.SLASHING_DAMAGE_IMMUNITY, Ability.SLASHING_DAMAGE_RESISTANCE
        );
    }

    private static void oneTime(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ONE_TIME_TRIGGER);
    }

    private static void alwaysActive(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ALWAYS_ACTIVE);
    }

    private static void playerTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.PLAYER_TRIGGERED);
    }

    private static void selfTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.SELF_TRIGGERED);
    }

    /**
     * Returns the category of the given Ability.
     * Defaults to PASSIVE_TRACKED for any ability not explicitly registered.
     */
    public static AbilityCategory getCategory(Ability ability) {
        return REGISTRY.getOrDefault(ability, AbilityCategory.PASSIVE_TRACKED);
    }
}