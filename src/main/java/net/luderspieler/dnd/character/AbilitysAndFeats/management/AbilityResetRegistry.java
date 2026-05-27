package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * Defines WHEN ability uses refill (short rest / long rest) and HOW MANY uses each ability has.
 * This is entirely separate from AbilityCategory (which defines HOW an ability triggers).
 *
 * Example: FLIGHT is PLAYER_TRIGGERED (category) AND in LONG_REST_RESET (reset schedule).
 *
 * Call resetOnLongRest() from SleepingIntereferer after sleeping.
 * Call resetOnShortRest() from a future short-rest system.
 */
public class AbilityResetRegistry {

    /**
     * Abilities whose uses refill on SHORT REST.
     * Long rest also triggers short rest resets.
     */
    /**
     * Abilities whose uses refill COMPLETELY on a SHORT REST.
     * Long rest also triggers short rest resets.
     */
    public static final Set<Ability> SHORT_REST_RESET = Set.of(
            Ability.ACTION_SURGE,         // Re-verified: Bleibt auch 2024 ein Short-Rest-Refill!
            Ability.CHANNEL_DIVINITY,
            Ability.CHANNEL_DIVINITY_PALADIN,
            Ability.FOCUS_POINTS,
            Ability.WILD_SHAPE,
            Ability.STROKE_OF_LUCK        // 2024 Rogue: Lädt sich nun bei Short ODER Long Rest auf
    );

    /**
     * Abilities whose uses refill COMPLETELY on LONG REST ONLY.
     * These are NOT included in standard short rest resets.
     */
    public static final Set<Ability> LONG_REST_RESET = Set.of(
            Ability.RAGE,                 // Full Reset bei Long Rest, Teil-Refill bei Short Rest
            Ability.SECOND_WIND,          // Full Reset bei Long Rest, Teil-Refill bei Short Rest
            Ability.FLIGHT,
            Ability.BREATH_WEAPON,        // Neu 2024: Skaliert mit PB, Reset bei Long Rest
            Ability.STONE_CUNNING,        // Neu 2024: Skaliert mit PB, Reset bei Long Rest
            Ability.HEALING_HANDS,
            Ability.CELESTIAL_REVELATION,
            Ability.ADRENALINE_RUSH,
            Ability.DIVINE_INTERVENTION,
            Ability.BARDIC_INSPIRATION,   // Full Reset auf Long Rest (Short Rest wird ab Level 5 dynamisch getriggert)
            Ability.LAY_ON_HANDS,
            Ability.ABJURE_FOES,          // 2024: Eigene Ladungen via Cha-Modifikator
            Ability.TIRELESS,             // 2024: Eigene Ladungen via Wis-Modifikator
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
            Ability.LARGE_FORM,           // 2024 Goliath: PB-mal pro Long Rest (aus Short Rest verschoben)
            Ability.CLOUDS_JAUNT,
            Ability.FIRES_BURN,
            Ability.FROSTS_CHILL,
            Ability.HILLS_TUMBLE,
            Ability.STONES_ENDURANCE,
            Ability.STORMS_THUNDER,
            Ability.RELENTLESS_HUNTER
    );

    // ── RESET METHODS ─────────────────────────────────────────────────

     /**
     * Refills all SHORT_REST_RESET abilities the player has.
     * Called on short rest AND as part of long rest.
     */
    public static void resetOnShortRest(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean changed = false;
        int level = (int) vars.PlayerLevel;

        // Standard Short Rest Resets (Vollständiges Auffüllen)
        for (Ability ability : SHORT_REST_RESET) {
            if (!AbilityUtils.hasAbility(player, ability)) continue;
            int max = getMaxUses(player, ability, vars);
            if (max > 0) {
                AbilityDataUtils.set(vars, ability.name() + "_uses", max);
                changed = true;
            }
        }

        // Bard: Font of Inspiration (Level 5+) -> Volles Refill bei Short Rest
        if (level >= 5 && AbilityUtils.hasAbility(player, Ability.BARDIC_INSPIRATION)) {
            int max = getMaxUses(player, Ability.BARDIC_INSPIRATION, vars);
            if (max > 0) {
                AbilityDataUtils.set(vars, Ability.BARDIC_INSPIRATION.name() + "_uses", max);
                changed = true;
            }
        }

        // D&D 2024 Spezialregel: Rage und Second Wind regenerieren GENAU EINE Ladung bei Short Rest
        for (Ability ability : java.util.List.of(Ability.RAGE, Ability.SECOND_WIND)) {
            if (!AbilityUtils.hasAbility(player, ability)) continue;
            int max = getMaxUses(player, ability, vars);
            if (max > 0) {
                // Holt die aktuellen Ladungen (vorausgesetzt die .get Methode existiert in deinen Utils)
                int current = AbilityDataUtils.getInt(vars, ability.name() + "_uses", 0);
                if (current < max) {
                    AbilityDataUtils.set(vars, ability.name() + "_uses", current + 1);
                    changed = true;
                }
            }
        }

        if (changed) vars.markSyncDirty();
    }

    /**
     * Refills all LONG_REST_RESET abilities the player has.
     * Also calls resetOnShortRest() since a long rest includes a short rest.
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

        // Also clear active/readied flags from the last session
        var map = AbilityDataUtils.parse(vars.AbilityData);
        map.entrySet().removeIf(e -> e.getKey().endsWith("_active")
                || e.getKey().endsWith("_readied")
                || e.getKey().equals("RelentlessEndurance_used")
                || e.getKey().equals("RAGE_active")
                || e.getKey().equals("RECKLESS_active"));
        vars.AbilityData = serializeMap(map);

        if (changed) vars.markSyncDirty();

        // Short rest abilities also refill on long rest
        resetOnShortRest(player);
    }

    // ── MAX USES ──────────────────────────────────────────────────────

    /**
     * Returns the maximum number of uses for an ability at the player's current level/stats.
     * Returns -1 if the ability has no use tracking (unlimited or handled elsewhere).
     */
    public static int getMaxUses(ServerPlayer player, Ability ability,
                                 DndModVariables.PlayerVariables vars) {
        int level   = (int) vars.PlayerLevel;
        int profB   = (int) vars.ProficiencyBonus;
        int chaMod  = Math.floorDiv((int) vars.Charisma - 10, 2);
        int wisMod  = Math.floorDiv((int) vars.Wisdom   - 10, 2);

        return switch (ability) {

            // ── BARBARIAN ────────────────────────────────────────────
            case RAGE -> rageUses(level);

            // ── FIGHTER ──────────────────────────────────────────────
            case SECOND_WIND   -> level >= 10 ? 4 : level >= 4 ? 3 : 2;
            case ACTION_SURGE  -> level >= 17 ? 2 : 1;

            // ── BARD ─────────────────────────────────────────────────
            case BARDIC_INSPIRATION -> Math.max(1, chaMod);

            // ── CLERIC ───────────────────────────────────────────────
            case CHANNEL_DIVINITY         -> level >= 18 ? 4 : level >= 6 ? 3 : 2; // 2024: Skaliert auf 2, 3, 4 Ladungen
            case DIVINE_INTERVENTION      -> 1;

            // ── DRUID ────────────────────────────────────────────────
            case WILD_SHAPE -> level >= 17 ? 4 : level >= 6 ? 3 : 2; // 2024: Skaliert auf 2, 3, 4 Ladungen

            // ── MONK ─────────────────────────────────────────────────
            case FOCUS_POINTS  -> level; // 1 point per Monk level

            // ── PALADIN ──────────────────────────────────────────────
            case CHANNEL_DIVINITY_PALADIN -> 2;
            case LAY_ON_HANDS             -> level * 5; // HP pool
            case ABJURE_FOES              -> Math.max(1, chaMod); // 2024: Eigener Pool basierend auf Charisma-Modifikator

            // ── RANGER ───────────────────────────────────────────────
            case TIRELESS     -> Math.max(1, wisMod); // 2024: Skaliert mit Weisheits-Modifikator (Min 1)
            case NATURES_VEIL -> profB;

            // ── ROGUE ────────────────────────────────────────────────
            case STROKE_OF_LUCK -> 1;

            // ── SORCERER ─────────────────────────────────────────────
            case INNATE_SORCERY -> 2;

            // ── WARLOCK ──────────────────────────────────────────────
            case MAGICAL_CUNNING -> 1;
            case CONTACT_PATRON  -> 1;
            case ELDRITCH_MASTER -> 1;
            case MYSTIC_ARCANUM, IMPROVED_MYSTIC_ARCANUM_ONE,
                 IMPROVED_MYSTIC_ARCANUM_TWO, IMPROVED_MYSTIC_ARCANUM_THREE -> 1;

            // ── WIZARD ───────────────────────────────────────────────
            case ARCANE_RECOVERY -> 1;

            // ── SPECIES & SUBRACES ───────────────────────────────────
            case FLIGHT              -> 1;
            case BREATH_WEAPON       -> profB; // 2024 Dragonborn: PB-mal pro Long Rest
            case STONE_CUNNING       -> profB; // 2024 Dwarf: PB-mal pro Long Rest
            case LARGE_FORM          -> profB; // 2024 Goliath: PB-mal pro Long Rest
            case HEALING_HANDS       -> 1;
            case CELESTIAL_REVELATION-> 1;
            case ADRENALINE_RUSH     -> profB;
            case CLOUDS_JAUNT, FIRES_BURN, FROSTS_CHILL,
                 HILLS_TUMBLE, STONES_ENDURANCE, STORMS_THUNDER -> profB;

            default -> -1; // no use tracking
        };
    }

    /** Convenience overload that reads vars internally. */
    public static int getMaxUses(ServerPlayer player, Ability ability) {
        return getMaxUses(player, ability,
                player.getData(DndModVariables.PLAYER_VARIABLES));
    }

    // ── HELPERS ───────────────────────────────────────────────────────

    private static int rageUses(int level) {
        if (level >= 17) return 6;
        if (level >= 12) return 5;
        if (level >= 6)  return 4;
        if (level >= 3)  return 3;
        return 2;
    }

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