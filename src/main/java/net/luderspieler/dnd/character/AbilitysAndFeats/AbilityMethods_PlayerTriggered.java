package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.aUtils.AbilityUtils;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Arrays;
import java.util.Set;

/**
 * Implementierungen für alle PLAYER_TRIGGERED-Abilities.
 * Aufgerufen vom Ability Wheel über ActivateAbilityPacket.
 *
 * Konventionen:
 *   _uses    → AbilityData-Schlüssel für verbleibende Ladungen
 *   _readied → Flag: Ability ist auf den nächsten Treffer/Ereignis vorbereitet
 *   _active  → Flag: Ability ist aktuell aktiv (zeitlich begrenzt)
 *
 * SORCEROUS_RESTORATION ist nun SELF_TRIGGERED und wird hier NICHT mehr aufgerufen.
 * Es wird stattdessen durch AbilityResetRegistry.resetOnShortRest() ausgelöst.
 */
public class AbilityMethods_PlayerTriggered {

    // ── ENTRY POINT ───────────────────────────────────────────────────

    public static boolean activate(ServerPlayer player, Ability ability) {
        return activate(player, ability, "");
    }

    public static boolean activate(ServerPlayer player, Ability ability, String subAction) {
        if (!AbilityUtils.hasAbility(player, ability)) return false;
        return switch (ability) {

            // ── DWARF ────────────────────────────────────────────────────
            case STONE_CUNNING           -> activateStoneCunning(player);

            // ── DRAGONBORN ───────────────────────────────────────────────
            case FLIGHT                  -> activateFlight(player);
            case BREATH_WEAPON           -> activateBreathWeapon(player);

            // ── GOLIATH ──────────────────────────────────────────────────
            case LARGE_FORM              -> activateLargeForm(player);
            case CLOUDS_JAUNT            -> activateCloudsJaunt(player);
            case FIRES_BURN              -> activateFiresBurn(player);
            case FROSTS_CHILL            -> activateFrostsChill(player);
            case HILLS_TUMBLE            -> activateHillsTumble(player);
            case STONES_ENDURANCE        -> activateStonesEndurance(player);
            case STORMS_THUNDER          -> activateStormsThunder(player);

            // ── AASIMAR ──────────────────────────────────────────────────
            case HEALING_HANDS           -> activateHealingHands(player);
            case CELESTIAL_REVELATION    -> activateCelestialRevelation(player);

            // ── ORC ──────────────────────────────────────────────────────
            case ADRENALINE_RUSH         -> activateAdrenalineRush(player);

            // ── BARBARIAN ────────────────────────────────────────────────
            case RAGE                    -> activateRage(player);
            case RECKLESS_ATTACK         -> activateRecklessAttack(player);
            case BRUTAL_STRIKE           -> activateBrutalStrike(player);

            // ── BARD ─────────────────────────────────────────────────────
            case BARDIC_INSPIRATION      -> activateBardicInspiration(player);
            case COUNTERCHARM            -> activateCountercharm(player);
            case PEERLESS_SKILL          -> activatePeerlessSkill(player);

            // ── CLERIC ───────────────────────────────────────────────────
            case CHANNEL_DIVINITY        -> activateChannelDivinity(player);
            case DIVINE_INTERVENTION,
                 IMPROVED_DIVINE_INTERVENTION_ONE -> activateDivineIntervention(player);

            // ── DRUID ────────────────────────────────────────────────────
            case WILD_SHAPE              -> activateWildShape(player);
            case WILD_COMPANION          -> activateWildCompanion(player);
            case WILD_RESURGENCE         -> activateWildResurgence(player);

            // ── FIGHTER ──────────────────────────────────────────────────
            case SECOND_WIND             -> activateSecondWind(player);
            case ACTION_SURGE            -> activateActionSurge(player);
            case TACTICAL_MIND           -> activateTacticalMind(player);
            case INDOMITABLE             -> activateIndomitable(player);
            case TACTICAL_MASTER         -> activateTacticalMaster(player);

            // ── MONK ─────────────────────────────────────────────────────
            // subAction gibt die konkrete Focus-Aktion an (FLURRY_OF_BLOWS usw.)
            case FOCUS_POINTS            -> activateFocusPoints(player, subAction);
            case UNCANNY_METABOLISM      -> activateUncannyMetabolism(player);
            case DEFLECT_ATTACKS         -> activateDeflectAttacks(player);
            case SLOW_FALL               -> activateSlowFall(player);
            case STUNNING_STRIKE         -> activateStunningStrike(player);
            case SELF_RESTORATION        -> activateSelfRestoration(player);
            case SUPERIOR_DEFENSE        -> activateSuperiorDefense(player);

            // ── PALADIN ──────────────────────────────────────────────────
            case LAY_ON_HANDS            -> activateLayOnHands(player);
            case PALADINS_SMITE          -> activatePaladinsSmite(player);
            case RADIANT_SMITE           -> activateRadiantSmite(player);
            case CHANNEL_DIVINITY_PALADIN-> activateChannelDivinityPaladin(player);
            case FAITHFUL_STEED          -> activateFaithfulSteed(player);
            case ABJURE_FOES             -> activateAbjureFoes(player);
            case RESTORING_TOUCH         -> activateRestoringTouch(player);

            // ── RANGER ───────────────────────────────────────────────────
            case TIRELESS                -> activateTireless(player);
            case NATURES_VEIL            -> activateNaturesVeil(player);

            // ── ROGUE ────────────────────────────────────────────────────
            case CUNNING_ACTION          -> activateCunningAction(player);
            case STEADY_AIM              -> activateSteadyAim(player);
            case CUNNING_STRIKE          -> activateCunningStrike(player);
            case UNCANNY_DODGE           -> activateUncannyDodge(player);
            case DEVIOUS_STRIKES         -> activateDeviousStrikes(player);
            case STROKE_OF_LUCK          -> activateStrokeOfLuck(player);

            // ── SORCERER ─────────────────────────────────────────────────
            case INNATE_SORCERY          -> activateInnateSorcery(player);
            // subAction = "SLOT_1" … "SLOT_5"
            // subAction = "SLOT_1" … "SLOT_5" (SP→Slot) ODER "SLOT_TO_SP_1" … "SLOT_TO_SP_5" (Slot→SP)
            case FONT_OF_MAGIC           -> activateFontOfMagicDispatch(player, subAction);
            // subAction = "CAREFUL_SPELL" … "TWINNED_SPELL"
            case METAMAGIC               -> activateMetamagic(player, subAction);
            case ARCANE_APOTHEOSIS       -> activateArcaneApotheosis(player);

            // ── WARLOCK ──────────────────────────────────────────────────
            case MAGICAL_CUNNING         -> activateMagicalCunning(player);
            case CONTACT_PATRON          -> activateContactPatron(player);
            case ELDRITCH_MASTER         -> activateEldritchMaster(player);
            case MYSTIC_ARCANUM,
                 IMPROVED_MYSTIC_ARCANUM_ONE,
                 IMPROVED_MYSTIC_ARCANUM_TWO,
                 IMPROVED_MYSTIC_ARCANUM_THREE -> activateMysticArcanum(player, ability);

            // ── WIZARD ───────────────────────────────────────────────────
            case ARCANE_RECOVERY         -> activateArcaneRecovery(player);
            case MEMORIZE_SPELLS         -> activateMemorizeSpells(player);

            default -> false;
        };
    }

    // ── USE-TRACKING HELPERS ──────────────────────────────────────────

    private static boolean hasUse(ServerPlayer player, String key, int maxDefault) {
        return AbilityDataUtils.getInt(
                player.getData(DndModVariables.PLAYER_VARIABLES), key, maxDefault) > 0;
    }

    private static void consumeUse(ServerPlayer player, String key, int maxDefault) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, key,
                Math.max(0, AbilityDataUtils.getInt(vars, key, maxDefault) - 1));
        vars.markSyncDirty();
    }

    private static void setFlag(ServerPlayer player, String key, boolean value) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, key, value);
        vars.markSyncDirty();
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — DWARF
    // ══════════════════════════════════════════════════════════════════

    /**
     * STONE_CUNNING (Dwarf) — Bonus Action: Tremorsense 60ft für 10 min,
     * ProficiencyBonus-mal pro Long Rest.
     * Als Minecraft-Annäherung: Night Vision (sensorische Verstärkung) + TODO Tremorsense.
     */
    private static boolean activateStoneCunning(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "STONE_CUNNING_uses", maxUses)) return false;
        consumeUse(player, "STONE_CUNNING_uses", maxUses);
        // Night Vision als Platzhalter für Tremorsense — 12000 Ticks = 10 min
        player.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION, 12000, 0, false, false, false));
        // TODO: Tremorsense implementieren (Entitäten durch Blöcke erspüren)
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — DRAGONBORN
    // ══════════════════════════════════════════════════════════════════

    /**
     * FLIGHT (Dragonborn Level 5) — 10 min Flug, 1× pro Long Rest.
     */
    private static boolean activateFlight(ServerPlayer player) {
        if (!hasUse(player, "FLIGHT_uses", 1)) return false;
        consumeUse(player, "FLIGHT_uses", 1);
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
        net.luderspieler.dnd.DndMod.queueServerWork(12000, () -> {
            if (player.isAlive()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        });
        return true;
    }

    /**
     * BREATH_WEAPON (Dragonborn) — Aktive Nutzung, ProficiencyBonus-mal pro Long Rest.
     * Schadenstyp abhängig von DRACONIC_ANCESTRY (in AbilityData gespeichert).
     */
    private static boolean activateBreathWeapon(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "BREATH_WEAPON_uses", maxUses)) return false;
        consumeUse(player, "BREATH_WEAPON_uses", maxUses);
        // TODO: AoE-Angriff basierend auf Ancestral-Element aus AbilityData
        // Schaden skaliert mit Character-Level:
        //   Level 1-4:   2d6, Level 5-10: 3d6, Level 11-16: 4d6, Level 17+: 5d6
        // Form: Kegel (15ft) oder Linie (30ft × 5ft) je nach Drachen-Typ
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — GOLIATH
    // ══════════════════════════════════════════════════════════════════

    /**
     * LARGE_FORM (Goliath) — Wird Large für 10 min, ProfBonus-mal pro Long Rest.
     */
    private static boolean activateLargeForm(ServerPlayer player) {
        if (!hasUse(player, "LARGE_FORM_uses", 1)) return false;
        consumeUse(player, "LARGE_FORM_uses", 1);
        player.addEffect(new MobEffectInstance(DndModMobEffects.LARGE_FORM, 6000));
        AbilityDataUtils.set(player.getData(DndModVariables.PLAYER_VARIABLES),
                "LARGE_FORM_active", true);
        player.getData(DndModVariables.PLAYER_VARIABLES).markSyncDirty();
        return true;
    }



    /** CLOUDS_JAUNT — Teleport 30ft zu sichtbarem freiem Ort, ProfBonus/LR. */
    private static boolean activateCloudsJaunt(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "CLOUDS_JAUNT_uses", maxUses)) return false;
        consumeUse(player, "CLOUDS_JAUNT_uses", maxUses);
        // TODO: Teleport 30ft (ähnlich Misty Step) zum Blickziel
        return true;
    }

    /** FIRES_BURN — On-Hit: +1d10 Feuerschaden, ProfBonus/LR. */
    private static boolean activateFiresBurn(ServerPlayer player) {
        setFlag(player, "FIRES_BURN_readied", true);
        return true;
    }

    /** FROSTS_CHILL — On-Hit: Ziel wird verlangsamt (Speed -10ft bis nächster eigener Zug), ProfBonus/LR. */
    private static boolean activateFrostsChill(ServerPlayer player) {
        setFlag(player, "FROSTS_CHILL_readied", true);
        return true;
    }

    /** HILLS_TUMBLE — On-Hit: Ziel wird Prone (Large oder kleiner), ProfBonus/LR. */
    private static boolean activateHillsTumble(ServerPlayer player) {
        setFlag(player, "HILLS_TUMBLE_readied", true);
        return true;
    }

    /** STONES_ENDURANCE — Reaction: Schadens-Reduktion 1d12 + CON-Modifier, ProfBonus/LR. */
    private static boolean activateStonesEndurance(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "STONES_ENDURANCE_uses", maxUses)) return false;
        consumeUse(player, "STONES_ENDURANCE_uses", maxUses);
        int conMod = Math.floorDiv((int) vars.Constitution - 10, 2);
        int reduction = (1 + player.getRandom().nextInt(12) + conMod) * 2; // ×2 für Minecraft-Herzen
        float newHealth = Math.max(1.0f, player.getHealth() + Math.min(0, reduction));
        // Reduktion: wird auf nächsten eingehenden Schaden angewendet
        AbilityDataUtils.set(vars, "STONES_ENDURANCE_reduction", Math.max(0, reduction));
        vars.markSyncDirty();
        // TODO: Auf nächsten eingehenden Schaden anwenden via LivingIncomingDamageEvent
        return true;
    }

    /** STORMS_THUNDER — Reaction nach Treffer: Angreifer nimmt 1d8 Donner-Schaden, ProfBonus/LR. */
    private static boolean activateStormsThunder(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "STORMS_THUNDER_uses", maxUses)) return false;
        consumeUse(player, "STORMS_THUNDER_uses", maxUses);
        setFlag(player, "STORMS_THUNDER_readied", true);
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — AASIMAR
    // ══════════════════════════════════════════════════════════════════

    /**
     * HEALING_HANDS (Aasimar) — Berühr-Heilung: ProficiencyBonus HP, 1× pro Long Rest.
     * Heilt sich selbst (Targeting-System leitet später auf Ziel weiter).
     */
    private static boolean activateHealingHands(ServerPlayer player) {
        if (!hasUse(player, "HEALING_HANDS_uses", 1)) return false;
        consumeUse(player, "HEALING_HANDS_uses", 1);
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        player.heal((float) vars.ProficiencyBonus * 2);
        return true;
    }

    /**
     * CELESTIAL_REVELATION (Aasimar Level 3) — Himmelsform aktivieren, 1× pro Long Rest.
     * Form (Necrotic Shroud, Radiant Consumption, Healing Radiance) in AbilityData wählbar.
     */
    private static boolean activateCelestialRevelation(ServerPlayer player) {
        if (!hasUse(player, "CELESTIAL_REVELATION_uses", 1)) return false;
        consumeUse(player, "CELESTIAL_REVELATION_uses", 1);
        setFlag(player, "CELESTIAL_REVELATION_active", true);
        // TODO: je nach gewählter Form (CELESTIAL_REVELATION_form in AbilityData):
        //   "necrotic_shroud"  → Frightened-AoE + Nekrotik-Schaden-Bonus
        //   "radiant_consump." → Radiant-Schaden-Aura
        //   "healing_radiance" → Heilungs-Bonus-Aktion
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — ORC
    // ══════════════════════════════════════════════════════════════════

    /**
     * ADRENALINE_RUSH (Orc) — Bonus Action Dash + Temp HP gleich ProfBonus, ProfBonus/LR.
     */
    private static boolean activateAdrenalineRush(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "ADRENALINE_RUSH_uses", maxUses)) return false;
        consumeUse(player, "ADRENALINE_RUSH_uses", maxUses);
        player.setAbsorptionAmount(player.getAbsorptionAmount() + (float) vars.ProficiencyBonus * 2);
        // TODO: Dash-Bewegungsbonus für diesen Zug
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — BARBARIAN
    // ══════════════════════════════════════════════════════════════════

    /**
     * RAGE (Barbarian) — Wut aktivieren: B/P/S-Resistenz, STR-Vorteil, Bonusschaden.
     * Ladungen pro Long Rest: 2 (lvl1) bis 6 (lvl17+).
     */
    private static boolean activateRage(ServerPlayer player) {

        if (!hasUse(player, "RAGE_uses", 1)) return false;
        consumeUse(player, "RAGE_uses", 1);

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        player.addEffect(new MobEffectInstance(DndModMobEffects.RAGE, 1200, (int)vars.PlayerLevel));

        AbilityDataUtils.set(vars, "RAGE_active", true);
        AbilityDataUtils.set(vars, "RAGE_ticks_remaining", 1200);
        vars.markSyncDirty();
        return true;
    }

    /**
     * RECKLESS_ATTACK (Barbarian Level 2) — Angriffe mit Vorteil, Gegner auch.
     */
    private static boolean activateRecklessAttack(ServerPlayer player) {
        setFlag(player, "RECKLESS_active", true);
        return true;
    }

    /**
     * BRUTAL_STRIKE (Barbarian Level 9) — Verzichtet auf Reckless-Vorteil für Bonus-Effekt.
     * Optionen: Forceful Blow (Knockback + Schaden) oder Hamstring Blow (Speed-Debuff).
     */
    private static boolean activateBrutalStrike(ServerPlayer player) {
        if (!AbilityDataUtils.getBool(
                player.getData(DndModVariables.PLAYER_VARIABLES), "RECKLESS_active")) {
            return false; // Nur bei aktivem Reckless Attack nutzbar
        }
        setFlag(player, "BRUTAL_STRIKE_readied", true);
        // TODO: Sub-Wahl (Forceful Blow / Hamstring Blow) via Targeting-System
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — BARD
    // ══════════════════════════════════════════════════════════════════

    /**
     * BARDIC_INSPIRATION (Bard Level 1) — Inspirierende Würfel (d6→d12), CHA-Mod Ladungen/LR.
     * Ab Level 5 (Font of Inspiration): auch Short Rest Recharge.
     */
    private static boolean activateBardicInspiration(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = Math.max(1, Math.floorDiv((int) vars.Charisma - 10, 2));
        if (!hasUse(player, "BARDIC_INSPIRATION_uses", maxUses)) return false;
        consumeUse(player, "BARDIC_INSPIRATION_uses", maxUses);
        // TODO: Targeting-System → gewählte Kreatur erhält Inspiration-Würfel
        return true;
    }

    /**
     * COUNTERCHARM (Bard Level 7) — Bonus Action: Verbündete erhalten Vorteil auf
     * Charm/Frighten-Saves bis Beginn des nächsten Zuges.
     */
    private static boolean activateCountercharm(ServerPlayer player) {
        setFlag(player, "COUNTERCHARM_active", true);
        net.luderspieler.dnd.DndMod.queueServerWork(20, () ->
                setFlag(player, "COUNTERCHARM_active", false));
        // TODO: Aura auf nahe Verbündete anwenden
        return true;
    }

    /**
     * PEERLESS_SKILL (Bard Level 15) — Bardicwürfel zu eigenem Ability-Check addieren.
     */
    private static boolean activatePeerlessSkill(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = Math.max(1, Math.floorDiv((int) vars.Charisma - 10, 2));
        if (!hasUse(player, "BARDIC_INSPIRATION_uses", maxUses)) return false;
        consumeUse(player, "BARDIC_INSPIRATION_uses", maxUses);
        setFlag(player, "PEERLESS_SKILL_readied", true);
        // TODO: Auf nächsten Ability-Check anwenden
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — CLERIC
    // ══════════════════════════════════════════════════════════════════

    /**
     * CHANNEL_DIVINITY (Cleric Level 2) — Turn Undead oder Divine Spark.
     * 2 Ladungen/SR (mehr auf höheren Levels).
     */
    private static boolean activateChannelDivinity(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.PlayerLevel >= 18 ? 4 : (int) vars.PlayerLevel >= 6 ? 3 : 2;
        if (!hasUse(player, "CHANNEL_DIVINITY_uses", maxUses)) return false;
        consumeUse(player, "CHANNEL_DIVINITY_uses", maxUses);
        // TODO: Sub-Wahl (Turn Undead / Divine Spark) über Wheel-Popup
        return true;
    }

    /**
     * DIVINE_INTERVENTION (Cleric Level 10 / IMPROVED Level 20) —
     * Level 20: auto-succeeds. Ruft die Gottheit zu Hilfe.
     */
    private static boolean activateDivineIntervention(ServerPlayer player) {
        if (!hasUse(player, "DIVINE_INTERVENTION_uses", 1)) return false;
        consumeUse(player, "DIVINE_INTERVENTION_uses", 1);
        // TODO: DM-äquivalente Effekt-Auswahl (starke Heilung, Schutz, Schaden)
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — DRUID
    // ══════════════════════════════════════════════════════════════════

    /**
     * WILD_SHAPE (Druid Level 2) — Verwandelt sich in ein Tier, 2 Ladungen/SR.
     */
    private static boolean activateWildShape(ServerPlayer player) {
        if (!hasUse(player, "WILD_SHAPE_uses", 2)) return false;
        consumeUse(player, "WILD_SHAPE_uses", 2);
        // TODO: Tierform-Morphing-System (Entitäts-Wechsel oder Attribut-Override)
        return true;
    }

    /**
     * WILD_COMPANION (Druid Level 2) — Find Familiar als Magic Action ohne Materialien.
     */
    private static boolean activateWildCompanion(ServerPlayer player) {
        if (!hasUse(player, "WILD_SHAPE_uses", 2)) return false;
        consumeUse(player, "WILD_SHAPE_uses", 2);
        // TODO: Find Familiar via Zaubersystem
        return true;
    }

    /**
     * WILD_RESURGENCE (Druid Level 5) — Wild Shape Slot ↔ Spell Slot konvertieren.
     */
    private static boolean activateWildResurgence(ServerPlayer player) {
        // TODO: Sub-Wahl Richtung (Wild Shape → Slot 1st-lvl ODER Slot → Wild Shape Ladung)
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — FIGHTER
    // ══════════════════════════════════════════════════════════════════

    /**
     * SECOND_WIND (Fighter Level 1) — Heilung 1d10 + Fighter-Level.
     * 2 Ladungen/SR (bis zu 4 auf höheren Levels).
     * Short Rest: +1 Ladung.
     */
    private static boolean activateSecondWind(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int maxUses = level >= 10 ? 4 : level >= 4 ? 3 : 2;
        if (!hasUse(player, "SECOND_WIND_uses", maxUses)) return false;
        consumeUse(player, "SECOND_WIND_uses", maxUses);
        int heal = 1 + player.getRandom().nextInt(10) + level;
        player.heal(heal * 2.0f);
        return true;
    }

    /**
     * ACTION_SURGE (Fighter Level 2) — Gewährt eine zusätzliche Aktion diesen Zug.
     * 1 Ladung/SR (2 ab Level 17).
     */
    private static boolean activateActionSurge(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = AbilityUtils.hasAbility(player, Ability.IMPROVED_ACTION_SURGE_ONE) ? 2 : 1;
        if (!hasUse(player, "ACTION_SURGE_uses", maxUses)) return false;
        consumeUse(player, "ACTION_SURGE_uses", maxUses);
        setFlag(player, "ACTION_SURGE_active", true);
        // TACTICAL_SHIFT (SELF_TRIGGERED) prüft ACTION_SURGE_active und gewährt Bewegung
        return true;
    }

    /**
     * TACTICAL_MIND (Fighter Level 2) — Bei misslungenem Ability-Check:
     * Second Wind ausgeben → 1d10 addieren.
     */
    private static boolean activateTacticalMind(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int swMax = level >= 10 ? 4 : level >= 4 ? 3 : 2;
        if (!hasUse(player, "SECOND_WIND_uses", swMax)) return false;
        consumeUse(player, "SECOND_WIND_uses", swMax);
        setFlag(player, "TACTICAL_MIND_readied", true);
        // TODO: Auf nächsten Ability-Check anwenden (kein direkter MC-Hook vorhanden)
        return true;
    }

    /**
     * INDOMITABLE (Fighter Level 9) — Reaction: misslungener Saving Throw neu würfeln.
     * 1 Ladung/LR (Level 13: 2, Level 17: 3). TODO: In AbilityResetRegistry eintragen.
     */
    private static boolean activateIndomitable(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int maxUses = level >= 17 ? 3 : level >= 13 ? 2 : 1;
        if (!hasUse(player, "INDOMITABLE_uses", maxUses)) return false;
        consumeUse(player, "INDOMITABLE_uses", maxUses);
        setFlag(player, "INDOMITABLE_readied", true);
        // TODO: Auf nächsten Saving Throw anwenden + Fighter-Level als Bonus
        return true;
    }

    /**
     * TACTICAL_MASTER (Fighter Level 9) — Tauscht Weapon Mastery Property beim Angriff
     * gegen Push, Slow oder Topple.
     */
    private static boolean activateTacticalMaster(ServerPlayer player) {
        setFlag(player, "TACTICAL_MASTER_readied", true);
        // TODO: Sub-Wahl (Push / Slow / Topple) für den nächsten Angriff
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — MONK
    // ══════════════════════════════════════════════════════════════════

    /**
     * FOCUS_POINTS (Monk Level 2) – Sub-Aktionen via Ability-Wheel.
     *
     * Ohne subAction: gibt true zurück wenn noch Punkte vorhanden
     * (das Sub-Wheel wird client-seitig geöffnet, kein Packet nötig).
     *
     * Mit subAction: gibt die entsprechende Technik aus.
     *   "FLURRY_OF_BLOWS"  – 1 FP, Haste-Effekt + Flag
     *   "PATIENT_DEFENSE"  – 1 FP, Resistance + Speed für 1 Sek.
     *   "STEP_OF_THE_WIND" – 1 FP, Speed + Jump-Boost für 2 Sek.
     */
    private static boolean activateFocusPoints(ServerPlayer player, String subAction) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining",
                (int) vars.PlayerLevel);

        // Kein subAction → Sub-Wheel ist client-seitig schon offen; nichts zu tun.
        if (subAction == null || subAction.isBlank()) {
            return remaining > 0;
        }

        int cost = switch (subAction) {
            case "FLURRY_OF_BLOWS", "PATIENT_DEFENSE", "STEP_OF_THE_WIND" -> 1;
            default -> -1;
        };

        if (cost < 0 || remaining < cost) return false;

        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - cost);
        vars.markSyncDirty();

        return switch (subAction) {
            case "FLURRY_OF_BLOWS" -> {
                player.addEffect(new MobEffectInstance(
                        MobEffects.HASTE, 10, 2, false, false, false));
                AbilityDataUtils.set(vars, "FLURRY_remaining", 2);
                vars.markSyncDirty();
                player.displayClientMessage(
                        Component.literal("§9Flurry of Blows ready!"), true);
                yield true;
            }
            case "PATIENT_DEFENSE" -> {
                player.addEffect(new MobEffectInstance(
                        MobEffects.RESISTANCE, 20, 0, false, false, false));
                player.addEffect(new MobEffectInstance(
                        MobEffects.SPEED, 20, 1, false, false, false));
                AbilityDataUtils.set(vars, "PATIENT_DEFENSE_active", true);
                vars.markSyncDirty();
                net.luderspieler.dnd.DndMod.queueServerWork(20, () -> {
                    AbilityDataUtils.set(vars, "PATIENT_DEFENSE_active", false);
                    vars.markSyncDirty();
                });
                player.displayClientMessage(
                        Component.literal("§9Patient Defense!"), true);
                yield true;
            }
            case "STEP_OF_THE_WIND" -> {
                player.addEffect(new MobEffectInstance(
                        MobEffects.SPEED, 40, 1, false, false, false));
                player.addEffect(new MobEffectInstance(
                        MobEffects.JUMP_BOOST, 40, 1, false, false, false));
                player.displayClientMessage(
                        Component.literal("§9Step of the Wind!"), true);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * UNCANNY_METABOLISM (Monk Level 2) — Bei Initiative: 1 Focus Point ausgeben
     * um ProfBonus Focus Points zurückzuerhalten + 1 Treffer-Würfel heilen.
     * "you CAN expend 1 Focus Point" → PLAYER_TRIGGERED.
     */
    private static boolean activateUncannyMetabolism(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", (int) vars.PlayerLevel);
        if (remaining < 1) return false;
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - 1);
        // Zurückgewinnung: ProfBonus Focus Points
        int regain = Math.min((int) vars.ProficiencyBonus,
                (int) vars.PlayerLevel - AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", 0));
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining",
                AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", 0) + regain);
        // Heilung: 1 Trefferwürfel (d8/d10/d12 je Level, Wisdom-Modifier addiert)
        int wismod = Math.floorDiv((int) vars.Wisdom - 10, 2);
        int dieSize = (int) vars.PlayerLevel >= 17 ? 12 : (int) vars.PlayerLevel >= 11 ? 10 : 8;
        player.heal((1 + player.getRandom().nextInt(dieSize) + wismod) * 2.0f);
        vars.markSyncDirty();
        return true;
    }

    /**
     * DEFLECT_ATTACKS (Monk Level 3) — Reaction: Eingehenden Schaden reduzieren.
     * Wenn auf 0 reduziert: als Wurfgeschoss zurückwerfen (1 Focus Point).
     */
    private static boolean activateDeflectAttacks(ServerPlayer player) {
        setFlag(player, "DEFLECT_ATTACKS_readied", true);
        return true;
    }

    /**
     * SLOW_FALL (Monk Level 4) — Reaction: Fallschaden um 5 × Monk-Level reduzieren.
     */
    private static boolean activateSlowFall(ServerPlayer player) {
        setFlag(player, "SLOW_FALL_active", true);
        // TODO: In LivingFallEvent einhaken und Schaden reduzieren
        return true;
    }

    /**
     * STUNNING_STRIKE (Monk Level 5) — On-Hit: 1 Focus Point → Ziel muss CON-Save bestehen
     * oder wird bis Ende des nächsten Zuges betäubt.
     */
    private static boolean activateStunningStrike(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", (int) vars.PlayerLevel);
        if (remaining < 1) return false;
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - 1);
        setFlag(player, "STUNNING_STRIKE_readied", true);
        vars.markSyncDirty();
        return true;
    }

    /**
     * SELF_RESTORATION (Monk Level 10) — Am Ende des eigenen Zuges: einen Zustand beenden
     * (Charmed, Frightened, Poisoned etc.).
     */
    private static boolean activateSelfRestoration(ServerPlayer player) {
        // Entfernt den ersten zutreffenden negativen Effekt
        for (net.minecraft.core.Holder<MobEffect> effect : java.util.List.of(

                DndModMobEffects.CHARMED,
                MobEffects.POISON,
                MobEffects.BLINDNESS,
                MobEffects.WEAKNESS,
                MobEffects.SLOWNESS
        )) {
            if (player.hasEffect(effect)) {
                player.removeEffect(effect);
                break;
            }
        }
        // TODO: DndModMobEffects.FRIGHTENED entfernen
        return true;
    }

    /**
     * SUPERIOR_DEFENSE (Monk Level 18) — 3 Focus Points: Resistance auf alle Schadenstypen
     * außer Force für 1 Runde.
     */
    private static boolean activateSuperiorDefense(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int remaining = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", (int) vars.PlayerLevel);
        if (remaining < 3) return false;
        AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", remaining - 3);
        setFlag(player, "SUPERIOR_DEFENSE_active", true);
        vars.markSyncDirty();
        // TODO: Resistance auf alle Schadenstypen außer Force für 1 Runde (20 Ticks)
        net.luderspieler.dnd.DndMod.queueServerWork(20, () ->
                setFlag(player, "SUPERIOR_DEFENSE_active", false));
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — PALADIN
    // ══════════════════════════════════════════════════════════════════

    /**
     * LAY_ON_HANDS (Paladin Level 1) — Heilung aus HP-Pool (5 × Level), 1 Long Rest.
     * Heilt sich selbst (5 HP pro Aktivierung); Targeting leitet später auf Ziel.
     */
    private static boolean activateLayOnHands(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int pool = AbilityDataUtils.getInt(vars, "LAY_ON_HANDS_pool", (int) vars.PlayerLevel * 5);
        if (pool <= 0) return false;
        int heal = Math.min(5, pool);
        player.heal(heal * 2.0f);
        AbilityDataUtils.set(vars, "LAY_ON_HANDS_pool", pool - heal);
        vars.markSyncDirty();
        return true;
    }

    /**
     * PALADINS_SMITE (Paladin Level 2) — On-Hit: Spell-Slot ausgeben für Radiant-Schaden.
     */
    private static boolean activatePaladinsSmite(ServerPlayer player) {
        setFlag(player, "SMITE_readied", true);
        // TODO: Sub-Wahl Slot-Grad → Schaden: 2d8 + 1d8/Grad über 1
        return true;
    }

    /**
     * RADIANT_SMITE (Paladin Level 2) — Smite emittiert Licht und kann blenden.
     * Muss zusammen mit Paladins Smite aktiv sein.
     */
    private static boolean activateRadiantSmite(ServerPlayer player) {
        setFlag(player, "RADIANT_SMITE_readied", true);
        return true;
    }

    /**
     * CHANNEL_DIVINITY_PALADIN (Paladin Level 3) — Sacred Weapon: +CHA zu Angriff für 1 min.
     * 2 Ladungen/SR.
     */
    private static boolean activateChannelDivinityPaladin(ServerPlayer player) {
        if (!hasUse(player, "CHANNEL_DIVINITY_PAL_uses", 2)) return false;
        consumeUse(player, "CHANNEL_DIVINITY_PAL_uses", 2);
        setFlag(player, "SACRED_WEAPON_active", true);
        // TODO: CHA-Modifier auf Angriffswürfe + Glüh-Effekt auf Waffe
        return true;
    }

    /**
     * FAITHFUL_STEED (Paladin Level 5) — Phantom Steed herbeirufen oder wiederbeleben.
     */
    private static boolean activateFaithfulSteed(ServerPlayer player) {
        // TODO: Phantom Steed / Unicorn Entität spawnen (eigene Entity-Klasse nötig)
        return true;
    }

    /**
     * ABJURE_FOES (Paladin Level 9) — CHA-Mod Kreaturen werden frightened/geläuhmt, 1× LR.
     */
    private static boolean activateAbjureFoes(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = Math.max(1, Math.floorDiv((int) vars.Charisma - 10, 2));
        if (!hasUse(player, "ABJURE_FOES_uses", maxUses)) return false;
        consumeUse(player, "ABJURE_FOES_uses", maxUses);
        // TODO: Frightened-Effekt auf nahe Kreaturen (WIS-Save gegen Spell Save DC)
        return true;
    }

    /**
     * RESTORING_TOUCH (Paladin Level 14) — Erweiterung von Lay on Hands:
     * 5 HP aus dem Pool ausgeben um einen Zustand zu beenden.
     */
    private static boolean activateRestoringTouch(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int pool = AbilityDataUtils.getInt(vars, "LAY_ON_HANDS_pool", (int) vars.PlayerLevel * 5);
        if (pool < 5) return false;
        AbilityDataUtils.set(vars, "LAY_ON_HANDS_pool", pool - 5);
        vars.markSyncDirty();
        // Entfernt einen Zustand beim Ziel (hier: beim Caster selbst als Platzhalter)
        // TODO: Targeting → Zustand beim Ziel beenden (Blinded, Charmed, Frightened, etc.)
        activateSelfRestoration(player);
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — RANGER
    // ══════════════════════════════════════════════════════════════════

    /**
     * TIRELESS (Ranger Level 10) — Temp HP = 1d8 + WIS-Mod, ProfBonus-mal/LR.
     */
    private static boolean activateTireless(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "TIRELESS_uses", maxUses)) return false;
        consumeUse(player, "TIRELESS_uses", maxUses);
        int wismod = Math.floorDiv((int) vars.Wisdom - 10, 2);
        int tempHp = 1 + player.getRandom().nextInt(8) + wismod;
        player.setAbsorptionAmount(player.getAbsorptionAmount() + tempHp * 2.0f);
        return true;
    }

    /**
     * NATURES_VEIL (Ranger Level 14) — Unsichtbar bis Beginn des nächsten Zuges,
     * ProfBonus-mal/LR.
     */
    private static boolean activateNaturesVeil(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "NATURES_VEIL_uses", maxUses)) return false;
        consumeUse(player, "NATURES_VEIL_uses", maxUses);
        // 40 Ticks ≈ 2 Sek. als Platzhalter für "bis Beginn nächster Zug"
        player.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY, 40, 0, false, false));
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — ROGUE
    // ══════════════════════════════════════════════════════════════════

    /**
     * CUNNING_ACTION (Rogue Level 2) — Bonus Action: Dash, Disengage oder Hide.
     */
    private static boolean activateCunningAction(ServerPlayer player) {
        setFlag(player, "CUNNING_ACTION_readied", true);
        // TODO: Sub-Wahl (Dash / Disengage / Hide)
        return true;
    }

    /**
     * STEADY_AIM (Rogue Level 3) — Bonus Action: Advantage auf nächsten Angriff,
     * Geschwindigkeit = 0 diesen Zug.
     */
    private static boolean activateSteadyAim(ServerPlayer player) {
        setFlag(player, "STEADY_AIM_active", true);
        // TODO: Geschwindigkeit auf 0 setzen, Advantage auf nächsten Angriff
        return true;
    }

    /**
     * CUNNING_STRIKE (Rogue Level 5) — Gibt Sneak-Attack-Würfel für Effekte aus.
     * Optionen: Poison (1W), Trip (1W), Withdraw (1W), Distract (2W).
     */
    private static boolean activateCunningStrike(ServerPlayer player) {
        setFlag(player, "CUNNING_STRIKE_readied", true);
        // TODO: Sub-Wahl Effekt; Würfelanzahl von Sneak Attack abziehen
        return true;
    }

    /**
     * UNCANNY_DODGE (Rogue Level 5) — Reaction: Schaden eines sichtbaren Angreifers halbieren.
     * Hinweis: SelfTriggered implementiert dies bereits automatisch. Dieser Wheel-Eintrag
     * erlaubt dem Spieler die manuelle Kontrolle (z.B. als Toggle).
     */
    private static boolean activateUncannyDodge(ServerPlayer player) {
        // Die eigentliche Implementierung läuft in AbilityMethods_SelfTriggered.handleUncannDodge()
        // Hier als manuelle Bestätigung falls die Auto-Impl. auf expliziten Toggle umgestellt wird.
        setFlag(player, "UNCANNY_DODGE_readied", true);
        return true;
    }

    /**
     * DEVIOUS_STRIKES (Rogue Level 14) — Erweiterte Cunning Strike Optionen:
     * Daze (2W), Knock Out (6W), Obscure (3W).
     */
    private static boolean activateDeviousStrikes(ServerPlayer player) {
        setFlag(player, "DEVIOUS_STRIKES_readied", true);
        // TODO: Sub-Wahl (Daze / Knock Out / Obscure)
        return true;
    }

    /**
     * STROKE_OF_LUCK (Rogue Level 20) — Miss → Hit oder fehlgeschlagener Check → 20.
     * 1 Ladung/SR oder LR.
     */
    private static boolean activateStrokeOfLuck(ServerPlayer player) {
        if (!hasUse(player, "STROKE_OF_LUCK_uses", 1)) return false;
        consumeUse(player, "STROKE_OF_LUCK_uses", 1);
        setFlag(player, "STROKE_OF_LUCK_active", true);
        // TODO: Nächsten Miss → Hit umwandeln oder fehlgeschlagenen Check auf 20 setzen
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — SORCERER
    // ══════════════════════════════════════════════════════════════════

    /**
     * INNATE_SORCERY (Sorcerer Level 1) — Bonus Action: Advantage auf Spell Attacks
     * für 1 min, 2× pro Long Rest.
     */
    private static boolean activateInnateSorcery(ServerPlayer player) {
        if (!hasUse(player, "INNATE_SORCERY_uses", 2)) return false;
        consumeUse(player, "INNATE_SORCERY_uses", 2);
        setFlag(player, "INNATE_SORCERY_active", true);
        net.luderspieler.dnd.DndMod.queueServerWork(1200, () -> {
            if (player.isAlive()) setFlag(player, "INNATE_SORCERY_active", false);
        });
        return true;
    }

    /**
     * FONT_OF_MAGIC hat zwei Richtungen — SP→Slot und Slot→SP. Diese
     * Dispatch-Methode routet anhand des subAction-Präfixes zur richtigen
     * Implementierung, statt das im aufrufenden activate()-switch zu tun.
     */
    private static boolean activateFontOfMagicDispatch(ServerPlayer player, String subAction) {
        if (subAction != null && subAction.startsWith("SLOT_TO_SP_")) {
            return activateSlotToSorceryPoints(player, subAction);
        }
        return activateFontOfMagic(player, subAction);
    }

    /**
     * FONT_OF_MAGIC (Sorcerer Level 2) – Sorcery Points → Spell Slot.
     * subAction = "SLOT_1" … "SLOT_5"
     */
    private static boolean activateFontOfMagic(ServerPlayer player, String subAction) {
        if (subAction == null || subAction.isBlank()) return false;

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int cost = switch (subAction) {
            case "SLOT_1" -> 2;
            case "SLOT_2" -> 3;
            case "SLOT_3" -> 5;
            case "SLOT_4" -> 6;
            case "SLOT_5" -> 7;
            default -> -1;
        };
        int minLevel = switch (subAction) {
            case "SLOT_1" -> 2;
            case "SLOT_2" -> 3;
            case "SLOT_3" -> 5;
            case "SLOT_4" -> 7;
            case "SLOT_5" -> 9;
            default -> 99;
        };
        int grade = switch (subAction) {
            case "SLOT_1" -> 1;
            case "SLOT_2" -> 2;
            case "SLOT_3" -> 3;
            case "SLOT_4" -> 4;
            case "SLOT_5" -> 5;
            default -> 0;
        };

        if (cost < 0 || grade == 0 || (int) vars.PlayerLevel < minLevel) return false;

        int current = AbilityDataUtils.getInt(vars, "SORCERY_POINTS", 0);
        if (current < cost) return false;

        AbilityDataUtils.set(vars, "SORCERY_POINTS", current - cost);

        // Spell-Slot zur Spellslots-Zeichenkette hinzufügen
        String slots = vars.Spellslots != null
                ? vars.Spellslots.replace("\"", "") : "000000000";
        if (slots.length() < 9) slots = "000000000";
        char[] arr = slots.toCharArray();
        arr[grade - 1] = (char) ('0' + Math.min(9, arr[grade - 1] - '0' + 1));
        vars.Spellslots = new String(arr);
        vars.markSyncDirty();

        player.displayClientMessage(
                Component.literal("§5Created Grade " + grade + " spell slot!"), true);
        return true;
    }

    /**
     * METAMAGIC (Sorcerer Level 2) — Wählt Metamagic-Option für nächsten Zauber.
     * Kostet Sorcery Points je nach Option.
     */
    private static boolean activateMetamagic(ServerPlayer player, String subAction) {
        if (subAction == null || subAction.isBlank()) return false;

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Prüfen ob der Spieler diese Option überhaupt gewählt hat.
        // METAMAGIC_chosen nutzt SEMIKOLON als Trenner (kein Komma, da
        // AbilityData Kommas als Top-Level-Key-Trenner reserviert).
        String chosen = AbilityDataUtils.get(vars, "METAMAGIC_chosen", "");
        String displayName = actionKeyToDisplayName(subAction); // "CAREFUL_SPELL" → "Careful Spell"
        Set<String> chosenSet = Arrays.stream(chosen.split(";"))
                .map(String::trim).collect(java.util.stream.Collectors.toSet());
        if (!chosenSet.contains(displayName)) return false;

        // Kosten bestimmen
        int cost = switch (subAction) {
            case "DISTANT_SPELL", "EMPOWERED_SPELL", "EXTENDED_SPELL",
                 "TRANSMUTED_SPELL", "TWINNED_SPELL" -> 1;
            default -> -1;
        };
        if (cost < 0) return false;

        int current = AbilityDataUtils.getInt(vars, "SORCERY_POINTS", 0);
        if (current < cost) return false;

        AbilityDataUtils.set(vars, "SORCERY_POINTS", current - cost);
        // Flagge setzen (Integer 1 = aktiv, nicht boolean "true" — getBool()
        // ist je nach AbilityDataUtils-Implementierung mehrdeutig; getInt() != 0 ist eindeutig).
        AbilityDataUtils.set(vars, subAction + "_active", 1);
        vars.markSyncDirty();

        player.displayClientMessage(
                Component.literal("§5" + displayName + " readied!"), true);
        return true;
    }

    /**
     * Hilfsmethode: "CAREFUL_SPELL" → "Careful Spell"
     * Wird von activateMetamagic() genutzt.
     */
    private static String actionKeyToDisplayName(String key) {
        StringBuilder sb = new StringBuilder();
        for (String word : key.split("_")) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * ARCANE_APOTHEOSIS (Sorcerer Level 20) — Während Innate Sorcery: 1× pro Zug
     * Metamagic gratis (ohne Sorcery Points).
     */
    private static boolean activateArcaneApotheosis(ServerPlayer player) {
        if (!AbilityDataUtils.getBool(
                player.getData(DndModVariables.PLAYER_VARIABLES), "INNATE_SORCERY_active")) {
            return false; // Nur aktiv während Innate Sorcery
        }
        setFlag(player, "ARCANE_APOTHEOSIS_active", true);
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — WARLOCK
    // ══════════════════════════════════════════════════════════════════

    /** MAGICAL_CUNNING — Hälfte der Pact Magic Slots zurückgewinnen, 1× LR. */
    private static boolean activateMagicalCunning(ServerPlayer player) {
        if (!hasUse(player, "MAGICAL_CUNNING_uses", 1)) return false;
        consumeUse(player, "MAGICAL_CUNNING_uses", 1);
        // TODO: Spell-Slot-System: halbe Pact-Magic-Slots wiederherstellen
        return true;
    }

    /** CONTACT_PATRON — Commune gratis casten, 1× LR. */
    private static boolean activateContactPatron(ServerPlayer player) {
        if (!hasUse(player, "CONTACT_PATRON_uses", 1)) return false;
        consumeUse(player, "CONTACT_PATRON_uses", 1);
        // TODO: Commune-Zauber via Zaubersystem auslösen
        return true;
    }

    /** ELDRITCH_MASTER — 1-min-Ritual: alle Pact Magic Slots zurückgewinnen, 1× LR. */
    private static boolean activateEldritchMaster(ServerPlayer player) {
        if (!hasUse(player, "ELDRITCH_MASTER_uses", 1)) return false;
        consumeUse(player, "ELDRITCH_MASTER_uses", 1);
        // TODO: Nach 1200 Ticks (1 min) alle Slots wiederherstellen
        return true;
    }

    /**
     * MYSTIC_ARCANUM / IMPROVED_MYSTIC_ARCANUM — Arcanum-Spell 1× pro LR casten.
     * Welcher Spell je Arcanum-Stufe wird in AbilityData gespeichert.
     */
    private static boolean activateMysticArcanum(ServerPlayer player, Ability which) {
        String key = which.name() + "_uses";
        if (!hasUse(player, key, 1)) return false;
        consumeUse(player, key, 1);
        // TODO: Arcanum-Spell via Zaubersystem auslösen (gespeicherter Spell-Name)
        // Slot-Grade: MYSTIC_ARCANUM=6, IMPROVED_ONE=7, IMPROVED_TWO=8, IMPROVED_THREE=9
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  IMPLEMENTIERUNGEN — WIZARD
    // ══════════════════════════════════════════════════════════════════

    /**
     * ARCANE_RECOVERY (Wizard Level 1) — Short Rest: Spell Slots bis Wizard-Level/2 zurückgewinnen.
     * 1× pro Long Rest.
     */
    private static boolean activateArcaneRecovery(ServerPlayer player) {
        if (!hasUse(player, "ARCANE_RECOVERY_uses", 1)) return false;
        consumeUse(player, "ARCANE_RECOVERY_uses", 1);
        // TODO: Sub-Wahl welche Slots zurückgewinnen (Summe der Grade ≤ Wizard-Level/2)
        return true;
    }

    /**
     * MEMORIZE_SPELLS (Wizard Level 5) — Short Rest: einen vorbereiteten Zauber austauschen.
     * ProfBonus-mal pro Long Rest.
     */
    private static boolean activateMemorizeSpells(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxUses = (int) vars.ProficiencyBonus;
        if (!hasUse(player, "MEMORIZE_SPELLS_uses", maxUses)) return false;
        consumeUse(player, "MEMORIZE_SPELLS_uses", maxUses);
        // TODO: SpellPrepScreen öffnen (eingeschränkt auf aktuellen Grad)
        return true;
    }

    // ═══════════════════════════════════════════════════════════════════════
//  activateSlotToSorceryPoints() — Font of Magic (Slot → SP)
//
//  Sorcerer 2024 PHB: Du kannst einen Spell-Slot verbrauchen und dafür
//  Sorcery Points in Höhe des Slot-Grades gewinnen. Ein Cantrip-Slot
//  kann nicht konvertiert werden. Jeder Slot (Grade 1-5) kann umgewandelt
//  werden; Grade 6-9 Slots sind laut 2024 PHB von dieser Umwandlung
//  AUSGESCHLOSSEN (man kann sie nicht in SP zurückverwandeln).
//
//  subAction Beispiele: "SLOT_TO_SP_1", "SLOT_TO_SP_2", ..., "SLOT_TO_SP_5"
//  Wird bereits über activateFontOfMagicDispatch() oben automatisch geroutet.
//          }
//          return activateFontOfMagic(player, subAction);
// ═══════════════════════════════════════════════════════════════════════

    private static boolean activateSlotToSorceryPoints(ServerPlayer player, String subAction) {
        // subAction = "SLOT_TO_SP_1" bis "SLOT_TO_SP_5"
        int grade;
        try {
            grade = Integer.parseInt(subAction.replace("SLOT_TO_SP_", ""));
        } catch (NumberFormatException e) { return false; }

        if (grade < 1 || grade > 5) return false; // Grade 6-9 sind nicht konvertierbar

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Spell-Slot vorhanden?
        String slots = vars.Spellslots != null ? vars.Spellslots : "000000000";
        if (slots.length() < grade) return false;

        int available = slots.charAt(grade - 1) - '0';
        if (available < 1) {
            player.displayClientMessage(
                    Component.literal("§cNo Grade " + grade + " spell slot available!"), true);
            return false;
        }

        // Slot abziehen
        char[] arr = slots.toCharArray();
        arr[grade - 1] = (char) ('0' + (available - 1));
        vars.Spellslots = new String(arr);

        // Sorcery Points gewinnen (= Slot-Grade)
        int currentSP = AbilityDataUtils.getInt(vars, "SORCERY_POINTS", 0);
        int maxSP     = (int) vars.PlayerLevel; // 2024 PHB: max SP = Sorcerer-Level
        int gained    = Math.min(grade, maxSP - currentSP); // nicht über Maximum
        AbilityDataUtils.set(vars, "SORCERY_POINTS", currentSP + gained);

        vars.markSyncDirty();

        player.displayClientMessage(
                Component.literal("§5Font of Magic: Grade " + grade + " slot → " + gained + " SP"),
                true);
        return true;
    }
}