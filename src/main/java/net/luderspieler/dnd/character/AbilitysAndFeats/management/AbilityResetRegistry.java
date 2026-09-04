package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import net.luderspieler.dnd.Utils.AbilityDataUtils;
import net.luderspieler.dnd.Utils.AbilityUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * Definiert wann Ability-Ladungen auffüllen (Short/Long Rest) und wie viele es gibt.
 * Getrennt von AbilityCategory (die beschreibt WIE eine Ability auslöst).
 *
 * Aufruf:
 *   resetOnLongRest()  ← SleepingIntereferer
 *   resetOnShortRest() ← zukünftiges Short-Rest-System
 */
public class AbilityResetRegistry {

    /**
     * Abilities deren Ladungen bei SHORT REST vollständig auffüllen.
     * Long Rest zählt auch als Short Rest.
     */
    public static final Set<Ability> SHORT_REST_RESET = Set.of(
            Ability.ACTION_SURGE,             // 2024: Short Rest Refill
            Ability.CHANNEL_DIVINITY,
            Ability.CHANNEL_DIVINITY_PALADIN,
            Ability.FOCUS_POINTS,
            Ability.WILD_SHAPE,
            Ability.STROKE_OF_LUCK            // 2024 Rogue: Short OR Long Rest
    );

    /**
     * Abilities deren Ladungen nur bei LONG REST auffüllen.
     */
    public static final Set<Ability> LONG_REST_RESET = Set.of(
            Ability.RAGE,
            Ability.SECOND_WIND,
            Ability.INDOMITABLE,              // Fighter 9: 1/2/3 Ladungen je Level
            Ability.FLIGHT,
            Ability.BREATH_WEAPON,
            Ability.STONE_CUNNING,
            Ability.HEALING_HANDS,
            Ability.CELESTIAL_REVELATION,
            Ability.ADRENALINE_RUSH,
            Ability.CHANNEL_DIVINITY,         // auch LR (wird durch Short Rest bereits gedeckt)
            Ability.DIVINE_INTERVENTION,
            Ability.BARDIC_INSPIRATION,
            Ability.LAY_ON_HANDS,
            Ability.ABJURE_FOES,
            Ability.TIRELESS,
            Ability.NATURES_VEIL,
            Ability.INNATE_SORCERY,
            Ability.MAGICAL_CUNNING,
            Ability.CONTACT_PATRON,
            Ability.ELDRITCH_MASTER,
            Ability.MYSTIC_ARCANUM,
            Ability.IMPROVED_MYSTIC_ARCANUM_ONE,
            Ability.IMPROVED_MYSTIC_ARCANUM_TWO,
            Ability.IMPROVED_MYSTIC_ARCANUM_THREE,
            Ability.ARCANE_RECOVERY,
            Ability.LARGE_FORM,
            Ability.CLOUDS_JAUNT,
            Ability.FIRES_BURN,
            Ability.FROSTS_CHILL,
            Ability.HILLS_TUMBLE,
            Ability.STONES_ENDURANCE,
            Ability.STORMS_THUNDER,
            Ability.RELENTLESS_HUNTER
    );

    // ── RESET METHODEN ─────────────────────────────────────────────────

    /**
     * Füllt alle SHORT_REST_RESET-Abilities auf.
     * Wird auch von resetOnLongRest() aufgerufen.
     */
    public static void resetOnShortRest(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean changed = false;
        int level = (int) vars.PlayerLevel;

        // Standard Short Rest Resets
        for (Ability ability : SHORT_REST_RESET) {
            if (!AbilityUtils.hasAbility(player, ability)) continue;
            int max = getMaxUses(player, ability, vars);
            if (max > 0) {
                AbilityDataUtils.set(vars, ability.name() + "_uses", max);
                changed = true;
            }
        }

        // Bard Level 5+: Font of Inspiration → BI lädt auch bei Short Rest auf
        if (level >= 5 && AbilityUtils.hasAbility(player, Ability.BARDIC_INSPIRATION)) {
            int max = getMaxUses(player, Ability.BARDIC_INSPIRATION, vars);
            if (max > 0) {
                AbilityDataUtils.set(vars, Ability.BARDIC_INSPIRATION.name() + "_uses", max);
                changed = true;
            }
        }

        // 2024 Spezialregel: RAGE und SECOND_WIND erhalten genau +1 Ladung bei Short Rest
        for (Ability ability : java.util.List.of(Ability.RAGE, Ability.SECOND_WIND)) {
            if (!AbilityUtils.hasAbility(player, ability)) continue;
            int max = getMaxUses(player, ability, vars);
            int current = AbilityDataUtils.getInt(vars, ability.name() + "_uses", 0);
            if (current < max) {
                AbilityDataUtils.set(vars, ability.name() + "_uses", current + 1);
                changed = true;
            }
        }

        if (changed) vars.markSyncDirty();
    }

    /**
     * Füllt alle LONG_REST_RESET-Abilities auf.
     * Ruft anschließend resetOnShortRest() auf (Long Rest schließt Short Rest ein).
     */
    public static void resetOnLongRest(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean changed = false;

        for (Ability ability : LONG_REST_RESET) {
            if (!AbilityUtils.hasAbility(player, ability)) continue;
            int max = getMaxUses(player, ability, vars);
            if (max > 0) {
                AbilityDataUtils.set(vars, ability.name() + "_uses", max);
                changed = true;
            }
        }

        // Aktiv/Readied-Flags des letzten Kampfes löschen
        var map = AbilityDataUtils.parse(vars.AbilityData);
        map.entrySet().removeIf(e -> {
            String k = e.getKey();
            return k.endsWith("_active")
                    || k.endsWith("_readied")
                    || k.equals("RelentlessEndurance_used")
                    || k.equals("RAGE_active")
                    || k.equals("RECKLESS_active")
                    || k.equals("BRUTAL_STRIKE_readied")
                    || k.equals("ACTION_SURGE_active")
                    || k.equals("CUNNING_STRIKE_readied")
                    || k.equals("DEVIOUS_STRIKES_readied")
                    || k.equals("SMITE_readied")
                    || k.equals("RADIANT_SMITE_readied")
                    || k.equals("SACRED_WEAPON_active")
                    || k.equals("TACTICAL_MIND_readied")
                    || k.equals("TACTICAL_MASTER_readied")
                    || k.equals("INDOMITABLE_readied")
                    || k.equals("STEADY_AIM_active")
                    || k.equals("PEERLESS_SKILL_readied")
                    || k.equals("METAMAGIC_readied")
                    || k.equals("RELENTLESS_RAGE_used_this_rage");
        });
        vars.AbilityData = serializeMap(map);

        if (changed) vars.markSyncDirty();

        // Short Rest Resets ebenfalls füllen
        resetOnShortRest(player);
    }

    // ── MAX USES ──────────────────────────────────────────────────────

    /**
     * Gibt die maximalen Ladungen zurück.
     * -1 = keine Ladungs-Verwaltung (unbegrenzt oder extern geregelt).
     */
    public static int getMaxUses(ServerPlayer player, Ability ability,
                                 DndModVariables.PlayerVariables vars) {
        int level  = (int) vars.PlayerLevel;
        int profB  = (int) vars.ProficiencyBonus;
        int chaMod = Math.floorDiv((int) vars.Charisma - 10, 2);
        int wisMod = Math.floorDiv((int) vars.Wisdom   - 10, 2);

        return switch (ability) {

            // ── BARBARIAN ────────────────────────────────────────────
            case RAGE -> level >= 17 ? 6 : level >= 12 ? 5 : level >= 6 ? 4 : level >= 3 ? 3 : 2;

            // ── FIGHTER ──────────────────────────────────────────────
            case SECOND_WIND  -> level >= 10 ? 4 : level >= 4 ? 3 : 2;
            case ACTION_SURGE -> level >= 17 ? 2 : 1;
            // 2024 PHB: 1 Ladung (Level 9), 2 (Level 13), 3 (Level 17)
            case INDOMITABLE  -> level >= 17 ? 3 : level >= 13 ? 2 : 1;

            // ── BARD ─────────────────────────────────────────────────
            case BARDIC_INSPIRATION -> Math.max(1, chaMod);

            // ── CLERIC ───────────────────────────────────────────────
            case CHANNEL_DIVINITY    -> level >= 18 ? 4 : level >= 6 ? 3 : 2;
            case DIVINE_INTERVENTION -> 1;

            // ── DRUID ────────────────────────────────────────────────
            case WILD_SHAPE -> level >= 17 ? 4 : level >= 6 ? 3 : 2;

            // ── MONK ─────────────────────────────────────────────────
            case FOCUS_POINTS -> level;

            // ── PALADIN ──────────────────────────────────────────────
            case CHANNEL_DIVINITY_PALADIN -> 2;
            case LAY_ON_HANDS             -> level * 5;
            case ABJURE_FOES              -> Math.max(1, chaMod);

            // ── RANGER ───────────────────────────────────────────────
            case TIRELESS     -> Math.max(1, wisMod);
            case NATURES_VEIL -> profB;

            // ── ROGUE ────────────────────────────────────────────────
            case STROKE_OF_LUCK -> 1;

            // ── SORCERER ─────────────────────────────────────────────
            case INNATE_SORCERY -> 2;

            // ── WARLOCK ──────────────────────────────────────────────
            case MAGICAL_CUNNING -> 1;
            case CONTACT_PATRON  -> 1;
            case ELDRITCH_MASTER -> 1;
            case MYSTIC_ARCANUM,
                 IMPROVED_MYSTIC_ARCANUM_ONE,
                 IMPROVED_MYSTIC_ARCANUM_TWO,
                 IMPROVED_MYSTIC_ARCANUM_THREE -> 1;

            // ── WIZARD ───────────────────────────────────────────────
            case ARCANE_RECOVERY -> 1;

            // ── SPEZIES ──────────────────────────────────────────────
            case FLIGHT              -> 1;
            case BREATH_WEAPON       -> profB;
            case STONE_CUNNING       -> profB;
            case LARGE_FORM          -> profB;
            case HEALING_HANDS       -> 1;
            case CELESTIAL_REVELATION-> 1;
            case ADRENALINE_RUSH     -> profB;
            case CLOUDS_JAUNT,
                 FIRES_BURN,
                 FROSTS_CHILL,
                 HILLS_TUMBLE,
                 STONES_ENDURANCE,
                 STORMS_THUNDER      -> profB;

            default -> -1; // keine Ladungs-Verwaltung
        };
    }

    /** Convenience-Overload ohne vorher geholte vars. */
    public static int getMaxUses(ServerPlayer player, Ability ability) {
        return getMaxUses(player, ability, player.getData(DndModVariables.PLAYER_VARIABLES));
    }

    // ── HELPERS ───────────────────────────────────────────────────────

    public static String serializeMap(java.util.Map<String, String> map) {
        if (map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(',');
            sb.append(e.getKey()).append('=').append(e.getValue());
            first = false;
        }
        return sb.append('}').toString();
    }
}