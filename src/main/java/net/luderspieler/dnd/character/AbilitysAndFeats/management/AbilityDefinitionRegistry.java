package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.EnumMap;
import java.util.Map;

/**
 * Maps every Ability to its AbilityCategory.
 * Everything not explicitly listed defaults to PASSIVE_TRACKED.
 */
public class AbilityDefinitionRegistry {

    private static final Map<Ability, AbilityCategory> REGISTRY = new EnumMap<>(Ability.class);

    static {
        // ── ONE_TIME_TRIGGER ─────────────────────────────────────────
        // Fired once when the ability is added to the player.
        oneTime(
                // Barbarian capstone: +4 STR +4 CON
                Ability.PRIMAL_CHAMPION,
                // Monk capstone: +4 DEX +4 WIS
                Ability.BODY_AND_MIND,
                // Dragonborn level 5: fly speed unlocked
                Ability.FLIGHT,
                // Speed bonuses (species / class)
                Ability.SPEED_BONUS_5
        );

        // ── ALWAYS_ACTIVE ─────────────────────────────────────────────
        // Re-evaluated every CHECK_INTERVAL ticks in AbilityPassiveTriggers.
        alwaysActive(
                // Darkvision (Night Vision reapplication)
                Ability.DARKVISION_60,
                Ability.DARKVISION_120,
                // AC calculations
                Ability.UNARMORED_DEFENSE,
                // Speed modifiers
                Ability.FAST_MOVEMENT,
                Ability.UNARMORED_MOVEMENT,
                Ability.ROVING,
                // Resistance tracking (applied via effect renewal)
                Ability.DWARVEN_RESILIENCE,
                Ability.FEY_ANCESTRY,
                Ability.BRAVE,
                Ability.GNOME_CUNNING,
                // HP bonus per level (checked and applied every tick if needed)
                Ability.DWARVEN_TOUGHNESS
        );

        // ── PLAYER_TRIGGERED ─────────────────────────────────────────
        // Activated by the player from the ability wheel.
        playerTriggered(
                // Barbarian
                Ability.RAGE,
                // Fighter
                Ability.SECOND_WIND,
                Ability.ACTION_SURGE,
                // Bard
                Ability.BARDIC_INSPIRATION,
                // Cleric
                Ability.CHANNEL_DIVINITY,
                Ability.DIVINE_INTERVENTION,
                // Druid
                Ability.WILD_SHAPE,
                // Monk — BonusAction abilities
                Ability.FOCUS_POINTS,
                Ability.SLOW_FALL,
                Ability.DEFLECT_ATTACKS,
                Ability.STUNNING_STRIKE,
                Ability.SUPERIOR_DEFENSE,
                Ability.UNCANNY_METABOLISM,
                // Paladin
                Ability.LAY_ON_HANDS,
                Ability.CHANNEL_DIVINITY_PALADIN,
                Ability.ABJURE_FOES,
                Ability.PALADINS_SMITE,
                // Ranger
                Ability.TIRELESS,
                Ability.NATURES_VEIL,
                // Rogue
                Ability.CUNNING_ACTION,
                Ability.STEADY_AIM,
                // Sorcerer
                Ability.INNATE_SORCERY,
                Ability.FONT_OF_MAGIC,
                Ability.SORCEROUS_RESTORATION,
                // Warlock
                Ability.MAGICAL_CUNNING,
                Ability.CONTACT_PATRON,
                // Wizard
                Ability.ARCANE_RECOVERY,
                Ability.MEMORIZE_SPELLS,
                // Aasimar
                Ability.HEALING_HANDS,
                Ability.CELESTIAL_REVELATION,
                // Orc
                Ability.ADRENALINE_RUSH,
                // Goliath ancestry actives
                Ability.CLOUDS_JAUNT,
                Ability.FIRES_BURN,
                Ability.FROSTS_CHILL,
                Ability.HILLS_TUMBLE,
                Ability.STONES_ENDURANCE,
                Ability.STORMS_THUNDER
        );

        // ── SELF_TRIGGERED ────────────────────────────────────────────
        // Fires automatically in response to a game event.
        selfTriggered(
                // Drop to 1 HP instead of dying
                Ability.RELENTLESS_RAGE,
                Ability.RELENTLESS_ENDURANCE,
                // Rogue / Monk
                Ability.UNCANNY_DODGE,
                Ability.EVASION,
                // Cunning Strike on hit
                Ability.CUNNING_STRIKE,
                Ability.DEVIOUS_STRIKES,
                // Paladin aura effects (proc when ally would be harmed)
                Ability.AURA_OF_PROTECTION,
                Ability.AURA_OF_COURAGE,
                // Barbarian
                Ability.BRUTAL_STRIKE,
                Ability.DANGER_SENSE,
                Ability.RECKLESS_ATTACK,
                // Radiant Strikes (on hit bonus damage)
                Ability.RADIANT_STRIKES,
                // Sneak Attack (on hit proc)
                Ability.SNEAK_ATTACK
        );

        // ── SHORT_REST_TRIGGER ────────────────────────────────────────
        shortRestTrigger(
                Ability.ACTION_SURGE,     // regain use
                Ability.SECOND_WIND,      // regain use
                Ability.CHANNEL_DIVINITY,
                Ability.CHANNEL_DIVINITY_PALADIN,
                Ability.FOCUS_POINTS,
                Ability.ARCANE_RECOVERY,
                Ability.MAGICAL_CUNNING
        );

        // ── LONG_REST_TRIGGER ─────────────────────────────────────────
        longRestTrigger(
                Ability.RAGE,
                Ability.LAY_ON_HANDS,
                Ability.WILD_SHAPE,
                Ability.BARDIC_INSPIRATION,
                Ability.DIVINE_INTERVENTION,
                Ability.INNATE_SORCERY,
                Ability.RELENTLESS_ENDURANCE,
                Ability.ADRENALINE_RUSH,
                Ability.NATURES_VEIL,
                Ability.TIRELESS,
                Ability.CLOUDS_JAUNT,
                Ability.FIRES_BURN,
                Ability.FROSTS_CHILL,
                Ability.HILLS_TUMBLE,
                Ability.STONES_ENDURANCE,
                Ability.STORMS_THUNDER,
                Ability.HEALING_HANDS,
                Ability.CELESTIAL_REVELATION,
                Ability.CONTACT_PATRON,
                Ability.MAGICAL_CUNNING
        );

        // Everything else not listed above → PASSIVE_TRACKED by default.
    }

    // ── HELPERS ───────────────────────────────────────────────────────

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
    private static void shortRestTrigger(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.SHORT_REST_TRIGGER);
    }
    private static void longRestTrigger(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.LONG_REST_TRIGGER);
    }

    /** Returns the category for the given ability. Defaults to PASSIVE_TRACKED. */
    public static AbilityCategory getCategory(Ability ability) {
        return REGISTRY.getOrDefault(ability, AbilityCategory.PASSIVE_TRACKED);
    }
}