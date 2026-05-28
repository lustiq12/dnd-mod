package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;

/**
 * Implementations for all PLAYER_TRIGGERED abilities.
 * These are called by the Ability Wheel (not yet implemented).
 * Each method is a self-contained action the player can activate.
 *
 * Use-tracking convention:
 *   Uses remaining are stored in AbilityData with key "<ABILITY_NAME>_uses".
 *   e.g. AbilityDataUtils.getInt(vars, "FLIGHT_uses", 1)
 *   Reset on long/short rest is handled by SleepingIntereferer (long rest)
 *   and a future ShortRest system.
 */
public class AbilityMethods_PlayerTriggered {

    // ── ENTRY POINT ───────────────────────────────────────────────────

    /**
     * Called by the ability wheel when the player activates an ability.
     * Returns true if the ability was successfully triggered.
     */
    public static boolean activate(ServerPlayer player, Ability ability) {
        if (!AbilityUtils.hasAbility(player, ability)) return false;
        return switch (ability) {

            // ── DRAGONBORN ──────────────────────────────────────────────
            case FLIGHT                  -> activateFlight(player);

            // ── GOLIATH ─────────────────────────────────────────────────
            case LARGE_FORM              -> activateLargeForm(player);
            case CLOUDS_JAUNT            -> activateCloudsJaunt(player);
            case FIRES_BURN              -> activateFiresBurn(player);
            case FROSTS_CHILL            -> activateFrostsChill(player);
            case HILLS_TUMBLE            -> activateHillsTumble(player);
            case STONES_ENDURANCE        -> activateStonesEndurance(player);
            case STORMS_THUNDER          -> activateStormsThunder(player);

            // ── AASIMAR ─────────────────────────────────────────────────
            case HEALING_HANDS           -> activateHealingHands(player);
            case CELESTIAL_REVELATION    -> activateCelestialRevelation(player);

            // ── ORC ─────────────────────────────────────────────────────
            case ADRENALINE_RUSH         -> activateAdrenalineRush(player);

            // ── BARBARIAN ───────────────────────────────────────────────
            case RAGE                    -> activateRage(player);
            case RECKLESS_ATTACK         -> activateRecklessAttack(player);

            // ── FIGHTER ─────────────────────────────────────────────────
            case SECOND_WIND             -> activateSecondWind(player);
            case ACTION_SURGE            -> activateActionSurge(player);

            // ── BARD ────────────────────────────────────────────────────
            case BARDIC_INSPIRATION      -> activateBardicInspiration(player);
            case COUNTERCHARM            -> activateCountercharm(player);
            case PEERLESS_SKILL          -> activatePeerlessSkill(player);

            // ── CLERIC ──────────────────────────────────────────────────
            case CHANNEL_DIVINITY        -> activateChannelDivinity(player);
            case DIVINE_INTERVENTION     -> activateDivineIntervention(player);

            // ── DRUID ───────────────────────────────────────────────────
            case WILD_SHAPE              -> activateWildShape(player);
            case WILD_RESURGENCE         -> activateWildResurgence(player);

            // ── MONK ────────────────────────────────────────────────────
            case FOCUS_POINTS            -> activateFocusPoints(player);
            case SLOW_FALL               -> activateSlowFall(player);
            case DEFLECT_ATTACKS         -> activateDeflectAttacks(player);
            case STUNNING_STRIKE         -> activateStunningStrike(player);
            case SUPERIOR_DEFENSE        -> activateSuperiorDefense(player);
            case UNCANNY_METABOLISM      -> activateUncannyMetabolism(player);

            // ── PALADIN ─────────────────────────────────────────────────
            case LAY_ON_HANDS            -> activateLayOnHands(player);
            case CHANNEL_DIVINITY_PALADIN-> activateChannelDivinityPaladin(player);
            case ABJURE_FOES             -> activateAbjureFoes(player);
            case PALADINS_SMITE          -> activatePaladinsSmite(player);

            // ── RANGER ──────────────────────────────────────────────────
            case TIRELESS                -> activateTireless(player);
            case NATURES_VEIL            -> activateNaturesVeil(player);

            // ── ROGUE ───────────────────────────────────────────────────
            case CUNNING_ACTION          -> activateCunningAction(player);
            case STEADY_AIM              -> activateSteadyAim(player);

            // ── SORCERER ────────────────────────────────────────────────
            case INNATE_SORCERY          -> activateInnateSorcery(player);
            case FONT_OF_MAGIC           -> activateFontOfMagic(player);
            case SORCEROUS_RESTORATION   -> activateSorcerousRestoration(player);

            // ── WARLOCK ─────────────────────────────────────────────────
            case MAGICAL_CUNNING         -> activateMagicalCunning(player);
            case CONTACT_PATRON          -> activateContactPatron(player);
            case ELDRITCH_MASTER         -> activateEldritchMaster(player);
            case MYSTIC_ARCANUM,
                 IMPROVED_MYSTIC_ARCANUM_ONE,
                 IMPROVED_MYSTIC_ARCANUM_TWO,
                 IMPROVED_MYSTIC_ARCANUM_THREE -> activateMysticArcanum(player, ability);

            // ── WIZARD ──────────────────────────────────────────────────
            case ARCANE_RECOVERY         -> activateArcaneRecovery(player);
            case MEMORIZE_SPELLS         -> activateMemorizeSpells(player);

            default -> false;
        };
    }

    // ── USE TRACKING HELPERS ──────────────────────────────────────────

    private static boolean hasUse(ServerPlayer player, String key, int maxDefault) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return AbilityDataUtils.getInt(vars, key, maxDefault) > 0;
    }

    private static void consumeUse(ServerPlayer player, String key, int maxDefault) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int current = AbilityDataUtils.getInt(vars, key, maxDefault);
        AbilityDataUtils.set(vars, key, Math.max(0, current - 1));
        vars.markSyncDirty();
    }

    // ── IMPLEMENTATIONS ───────────────────────────────────────────────

    /** FLIGHT (Dragonborn) — 10 min fly speed, 1× per long rest. */
    private static boolean activateFlight(ServerPlayer player) {
        if (!hasUse(player, "FLIGHT_uses", 1)) return false;
        consumeUse(player, "FLIGHT_uses", 1);
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
        // TODO: schedule removal after 12000 ticks (10 min) via DndMod.queueServerWork
        DndMod_queueFlyRevoke(player, 120);
        return true;
    }

    private static void DndMod_queueFlyRevoke(ServerPlayer player, int ticks) {
        net.luderspieler.dnd.DndMod.queueServerWork(ticks, () -> {
            if (player.isAlive()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        });
    }

    /** LARGE_FORM (Goliath) — become Large for 10 min, 1× per short rest. */
    private static boolean activateLargeForm(ServerPlayer player) {
        if (!hasUse(player, "LARGE_FORM_uses", 1)) return false;
        consumeUse(player, "LARGE_FORM_uses", 1);
        // TODO: implement entity scale change when NeoForge scale attribute is available
        // Attributes.SCALE or similar — mark in AbilityData for now
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "LARGE_FORM_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** HEALING_HANDS (Aasimar) — touch to heal ProficiencyBonus HP, 1× per long rest. */
    private static boolean activateHealingHands(ServerPlayer player) {
        if (!hasUse(player, "HEALING_HANDS_uses", 1)) return false;
        consumeUse(player, "HEALING_HANDS_uses", 1);
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        // Heal self for now; targeting system will redirect this to a chosen entity later
        player.heal((float) vars.ProficiencyBonus);
        return true;
    }

    /** CELESTIAL_REVELATION (Aasimar) — activate chosen form for 1 min, 1× per long rest. */
    private static boolean activateCelestialRevelation(ServerPlayer player) {
        if (!hasUse(player, "CELESTIAL_REVELATION_uses", 1)) return false;
        consumeUse(player, "CELESTIAL_REVELATION_uses", 1);
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, "CELESTIAL_REVELATION_active", true);
        vars.markSyncDirty();
        // TODO: apply visual effects and healing/damage pulses based on chosen form
        return true;
    }

    /** ADRENALINE_RUSH (Orc) — Dash + temp HP equal to ProfBonus, uses = ProfBonus per LR. */
    private static boolean activateAdrenalineRush(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "ADRENALINE_RUSH_uses", maxUses)) return false;
        consumeUse(player, "ADRENALINE_RUSH_uses", maxUses);
        // Grant temp HP equal to ProficiencyBonus
        player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) vars.ProficiencyBonus);
        // Dash effect: speed boost for 1 tick is done through the wheel activation
        // TODO: grant movement boost for current turn
        return true;
    }

    /** RAGE (Barbarian) — resist B/P/S damage, bonus damage, adv on STR. */
    private static boolean activateRage(ServerPlayer player) {
        // TODO: needs Rage tracker (active state, duration, bonus damage per level)
        // Set flag in AbilityData
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, "RAGE_active", true);
        AbilityDataUtils.set(vars, "RAGE_ticks_remaining", 200); // ~10 sec placeholder
        vars.markSyncDirty();
        return true;
    }

    /** RECKLESS_ATTACK (Barbarian) — player declares before their attack action. */
    private static boolean activateRecklessAttack(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, "RECKLESS_active", true);
        vars.markSyncDirty();
        return true;
    }

    /** SECOND_WIND (Fighter) — heal 1d10 + Fighter level. */
    private static boolean activateSecondWind(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = 2; // scales with level but 2 is lvl1 default
        if (!hasUse(player, "SECOND_WIND_uses", maxUses)) return false;
        consumeUse(player, "SECOND_WIND_uses", maxUses);
        int heal = 1 + player.getRandom().nextInt(10) + (int) vars.PlayerLevel;
        player.heal(heal * 2f); // ×2 for hearts
        return true;
    }

    /** ACTION_SURGE (Fighter) — grants an extra action this turn. */
    private static boolean activateActionSurge(ServerPlayer player) {
        if (!hasUse(player, "ACTION_SURGE_uses", 1)) return false;
        consumeUse(player, "ACTION_SURGE_uses", 1);
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "ACTION_SURGE_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** BARDIC_INSPIRATION — give a creature an inspiration die. */
    private static boolean activateBardicInspiration(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = Math.max(1, (int)((vars.Charisma - 10) / 2.0));
        if (!hasUse(player, "BARDIC_INSPIRATION_uses", maxUses)) return false;
        consumeUse(player, "BARDIC_INSPIRATION_uses", maxUses);
        // TODO: send inspiration to targeted creature via targeting system
        return true;
    }

    /** CHANNEL_DIVINITY (Cleric) — Turn Undead or Divine Spark. */
    private static boolean activateChannelDivinity(ServerPlayer player) {
        if (!hasUse(player, "CHANNEL_DIVINITY_uses", 2)) return false;
        consumeUse(player, "CHANNEL_DIVINITY_uses", 2);
        // TODO: show sub-choice popup (Turn Undead / Divine Spark)
        return true;
    }

    /** DIVINE_INTERVENTION (Cleric) — call on deity, 1× per LR. */
    private static boolean activateDivineIntervention(ServerPlayer player) {
        if (!hasUse(player, "DIVINE_INTERVENTION_uses", 1)) return false;
        consumeUse(player, "DIVINE_INTERVENTION_uses", 1);
        // TODO: DM-equivalent effect selection
        return true;
    }

    /** WILD_SHAPE (Druid) — transform into a beast, 2 uses per SR. */
    private static boolean activateWildShape(ServerPlayer player) {
        if (!hasUse(player, "WILD_SHAPE_uses", 2)) return false;
        consumeUse(player, "WILD_SHAPE_uses", 2);
        // TODO: beast form morph system
        return true;
    }

    /** WILD_RESURGENCE (Druid) — spend Wild Shape use for 1st-level slot or vice versa. */
    private static boolean activateWildResurgence(ServerPlayer player) {
        // TODO: sub-choice (which direction to convert)
        return true;
    }

    /** FOCUS_POINTS (Monk) — show sub-choice: Flurry / Patient Defense / Step of the Wind. */
    private static boolean activateFocusPoints(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxPoints = (int) vars.PlayerLevel;
        if (!hasUse(player, "FOCUS_POINTS_remaining", maxPoints)) return false;
        // TODO: sub-choice popup
        return true;
    }

    /** SLOW_FALL (Monk) — reduce fall damage by 5 × Monk level. */
    private static boolean activateSlowFall(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "SLOW_FALL_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        // TODO: hook into fall damage event
        return true;
    }

    /** DEFLECT_ATTACKS (Monk) — reduce incoming damage as Reaction. */
    private static boolean activateDeflectAttacks(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "DEFLECT_ATTACKS_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** STUNNING_STRIKE (Monk) — 1 Focus Point to attempt to stun on hit. */
    private static boolean activateStunningStrike(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining",
                (int) vars.PlayerLevel);
        if (remaining < 1) return false;
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - 1);
        AbilityDataUtils.set(vars, "STUNNING_STRIKE_readied", true);
        vars.markSyncDirty();
        return true;
    }

    /** SUPERIOR_DEFENSE (Monk lvl 18) — 3 Focus Points, resist all damage except Force. */
    private static boolean activateSuperiorDefense(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining",
                (int) vars.PlayerLevel);
        if (remaining < 3) return false;
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - 3);
        AbilityDataUtils.set(vars, "SUPERIOR_DEFENSE_active", true);
        vars.markSyncDirty();
        // TODO: apply resistance to all damage types except Force for 1 turn
        return true;
    }

    /** UNCANNY_METABOLISM (Monk) — on Initiative: regain Focus Points + HP. */
    private static boolean activateUncannyMetabolism(ServerPlayer player) {
        // Called automatically by SelfTriggered on Initiative; also exposed here.
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int heal  = player.getRandom().nextInt(
                switch ((int) vars.PlayerLevel) {
                    case 5,6,7,8,9,10 -> 8; case 11,12,13,14,15,16 -> 10; default -> 12;
                }) + 1;
        int wismod = Math.floorDiv((int) vars.Wisdom - 10, 2);
        player.heal((heal + wismod) * 2f);
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", (int) vars.PlayerLevel);
        vars.markSyncDirty();
        return true;
    }

    /** LAY_ON_HANDS (Paladin) — heal from pool. */
    private static boolean activateLayOnHands(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int pool = AbilityDataUtils.getInt(vars, "LAY_ON_HANDS_pool",
                (int) vars.PlayerLevel * 5);
        if (pool <= 0) return false;
        // Heal self for up to 5 HP per activation (targeting will redirect this later)
        int heal = Math.min(5, pool);
        player.heal(heal * 2f);
        AbilityDataUtils.set(vars, "LAY_ON_HANDS_pool", pool - heal);
        vars.markSyncDirty();
        return true;
    }

    /** CHANNEL_DIVINITY — Paladin version (Sacred Weapon). */
    private static boolean activateChannelDivinityPaladin(ServerPlayer player) {
        if (!hasUse(player, "CHANNEL_DIVINITY_PAL_uses", 2)) return false;
        consumeUse(player, "CHANNEL_DIVINITY_PAL_uses", 2);
        // TODO: apply Sacred Weapon glow + CHA attack bonus
        return true;
    }

    /** ABJURE_FOES (Paladin lvl 9) — frighten enemies, 1× per LR. */
    private static boolean activateAbjureFoes(ServerPlayer player) {
        if (!hasUse(player, "ABJURE_FOES_uses", 1)) return false;
        consumeUse(player, "ABJURE_FOES_uses", 1);
        // TODO: apply Frightened to nearby entities
        return true;
    }

    /** PALADIN'S SMITE — expend spell slot for radiant damage on hit. */
    private static boolean activatePaladinsSmite(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "SMITE_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** TIRELESS (Ranger) — gain Temp HP + reduce Exhaustion. */
    private static boolean activateTireless(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "TIRELESS_uses", maxUses)) return false;
        consumeUse(player, "TIRELESS_uses", maxUses);
        int wismod = Math.floorDiv((int) vars.Wisdom - 10, 2);
        int tempHp = 1 + player.getRandom().nextInt(8) + wismod;
        player.setAbsorptionAmount(player.getAbsorptionAmount() + tempHp * 2f);
        return true;
    }

    /** NATURE'S VEIL (Ranger lvl 14) — Invisible until start of next turn. */
    private static boolean activateNaturesVeil(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "NATURES_VEIL_uses", maxUses)) return false;
        consumeUse(player, "NATURES_VEIL_uses", maxUses);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.INVISIBILITY, 40, 0, false, false));
        return true;
    }

    /** CUNNING_ACTION (Rogue) — Dash, Disengage, or Hide as BonusAction. */
    private static boolean activateCunningAction(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "CUNNING_ACTION_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        // TODO: sub-choice Dash/Disengage/Hide
        return true;
    }

    /** STEADY_AIM (Rogue) — Advantage on next attack, speed = 0 this turn. */
    private static boolean activateSteadyAim(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "STEADY_AIM_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** INNATE_SORCERY (Sorcerer) — advantage on spell attacks for 1 min, 2× per LR. */
    private static boolean activateInnateSorcery(ServerPlayer player) {
        if (!hasUse(player, "INNATE_SORCERY_uses", 2)) return false;
        consumeUse(player, "INNATE_SORCERY_uses", 2);
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "INNATE_SORCERY_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }

    /** FONT_OF_MAGIC (Sorcerer) — convert Sorcery Points ↔ Spell Slots. */
    private static boolean activateFontOfMagic(ServerPlayer player) {
        // TODO: sub-choice (which direction, how many points/slots)
        return true;
    }

    /** SORCEROUS_RESTORATION (Sorcerer) — regain 4 Sorcery Points on SR. */
    private static boolean activateSorcerousRestoration(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.increment(vars, "SORCERY_POINTS", 4);
        vars.markSyncDirty();
        return true;
    }

    /** MAGICAL_CUNNING (Warlock) — regain half Pact Magic slots. */
    private static boolean activateMagicalCunning(ServerPlayer player) {
        if (!hasUse(player, "MAGICAL_CUNNING_uses", 1)) return false;
        consumeUse(player, "MAGICAL_CUNNING_uses", 1);
        // TODO: integrate with spell slot system
        return true;
    }

    /** CONTACT_PATRON (Warlock) — cast Commune for free, 1× per LR. */
    private static boolean activateContactPatron(ServerPlayer player) {
        if (!hasUse(player, "CONTACT_PATRON_uses", 1)) return false;
        consumeUse(player, "CONTACT_PATRON_uses", 1);
        // TODO: trigger Commune spell effect
        return true;
    }

    /** ELDRITCH_MASTER (Warlock) — 1-min ritual, regain all Pact Magic slots. */
    private static boolean activateEldritchMaster(ServerPlayer player) {
        if (!hasUse(player, "ELDRITCH_MASTER_uses", 1)) return false;
        consumeUse(player, "ELDRITCH_MASTER_uses", 1);
        // TODO: schedule slot refill after 1200 ticks (1 min)
        return true;
    }

    /** MYSTIC_ARCANUM — cast the stored arcanum spell, 1× per LR per arcanum level. */
    private static boolean activateMysticArcanum(ServerPlayer player, Ability which) {
        String key = which.name() + "_uses";
        if (!hasUse(player, key, 1)) return false;
        consumeUse(player, key, 1);
        // TODO: trigger the stored arcanum spell via spell system
        return true;
    }

    /** ARCANE_RECOVERY (Wizard) — regain spell slots after Short Rest, 1× per LR. */
    private static boolean activateArcaneRecovery(ServerPlayer player) {
        if (!hasUse(player, "ARCANE_RECOVERY_uses", 1)) return false;
        consumeUse(player, "ARCANE_RECOVERY_uses", 1);
        // TODO: sub-choice which slots to recover (up to half Wizard level)
        return true;
    }

    /** MEMORIZE_SPELLS (Wizard lvl 5) — swap prepared spells after Short Rest. */
    private static boolean activateMemorizeSpells(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "MEMORIZE_SPELLS_uses", maxUses)) return false;
        consumeUse(player, "MEMORIZE_SPELLS_uses", maxUses);
        // TODO: open SpellPrepScreen restricted to current grade
        return true;
    }

    // Goliath ancestry — stubs
    private static boolean activateCloudsJaunt(ServerPlayer player)  {
        if (!hasUse(player, "CLOUDS_JAUNT_uses", (int)player.getData(DndModVariables.PLAYER_VARIABLES).ProficiencyBonus)) return false;
        consumeUse(player, "CLOUDS_JAUNT_uses", (int)player.getData(DndModVariables.PLAYER_VARIABLES).ProficiencyBonus);
        // TODO: teleport 30ft to visible unoccupied space
        return true;
    }
    private static boolean activateFiresBurn(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES), "FIRES_BURN_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }
    private static boolean activateFrostsChill(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES), "FROSTS_CHILL_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }
    private static boolean activateHillsTumble(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES), "HILLS_TUMBLE_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }
    private static boolean activateStonesEndurance(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES), "STONES_ENDURANCE_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }
    private static boolean activateStormsThunder(ServerPlayer player) {
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES), "STORMS_THUNDER_readied", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }
    private static boolean activateCountercharm(ServerPlayer player)  { return true; /* TODO */ }
    private static boolean activatePeerlessSkill(ServerPlayer player) { return true; /* TODO */ }
}
