package net.luderspieler.dnd.resources;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityResetRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Zentrale Verwaltung aller D&D Ressource-Pools.
 *
 * Alle Pools werden als Schlüssel in vars.AbilityData gespeichert.
 * ResourcePool definiert welche Ability nötig ist, welcher Key benutzt wird,
 * und wie es im HUD dargestellt wird.
 *
 * Nutzung (Server):
 *   int current = ResourceManager.getCurrent(player, ResourcePool.FOCUS_POINTS);
 *   boolean ok  = ResourceManager.spend(player, ResourcePool.FOCUS_POINTS, 2);
 *
 * Nutzung (Client / HUD):
 *   int current = ResourceManager.getCurrent(minecraft.player, ResourcePool.FOCUS_POINTS);
 *   List<ResourcePool> active = ResourceManager.getActiveForDisplay(player);
 */
public class ResourceManager {

    // ══════════════════════════════════════════════════════════════════
    //  POOL-DEFINITIONEN
    // ══════════════════════════════════════════════════════════════════

    public enum ResourcePool {
        // Monk
        FOCUS_POINTS    ("FOCUS_POINTS_remaining",    Ability.FOCUS_POINTS,             "Focus",       0xFF4488FF, DisplayMode.PIPS),

        // Barbarian
        RAGE            ("RAGE_uses",                 Ability.RAGE,                     "Rage",        0xFFFF4422, DisplayMode.PIPS),

        // Sorcerer — eigene Logik, max = PlayerLevel
        SORCERY_POINTS  ("SORCERY_POINTS",            Ability.FONT_OF_MAGIC,            "Sorcery",     0xFFAA44FF, DisplayMode.PIPS),

        // Bard
        BARDIC_INSP     ("BARDIC_INSPIRATION_uses",   Ability.BARDIC_INSPIRATION,       "Inspiration", 0xFFFFD700, DisplayMode.PIPS),

        // Druid
        WILD_SHAPE      ("WILD_SHAPE_uses",           Ability.WILD_SHAPE,               "Wild Shape",  0xFF44AA22, DisplayMode.PIPS),

        // Cleric
        CHANNEL_DIV     ("CHANNEL_DIVINITY_uses",     Ability.CHANNEL_DIVINITY,         "Channel",     0xFFFFFFAA, DisplayMode.PIPS),

        // Paladin
        CH_DIV_PAL      ("CHANNEL_DIVINITY_PAL_uses", Ability.CHANNEL_DIVINITY_PALADIN, "C.Div",       0xFFFFEEBB, DisplayMode.PIPS),
        LAY_ON_HANDS    ("LAY_ON_HANDS_pool",         Ability.LAY_ON_HANDS,             "Lay on Hands",0xFF44FF88, DisplayMode.BAR),

        // Fighter
        SECOND_WIND     ("SECOND_WIND_uses",          Ability.SECOND_WIND,              "2nd Wind",    0xFF44CCFF, DisplayMode.PIPS),
        ACTION_SURGE    ("ACTION_SURGE_uses",         Ability.ACTION_SURGE,             "A.Surge",     0xFFFF8800, DisplayMode.PIPS),

        // Sorcerer — Innate Sorcery aktivierungen
        INNATE_SORCERY  ("INNATE_SORCERY_uses",       Ability.INNATE_SORCERY,           "Inn.Sorcery", 0xFFCC44FF, DisplayMode.PIPS),

        // Warlock
        MAGICAL_CUNNING ("MAGICAL_CUNNING_uses",      Ability.MAGICAL_CUNNING,          "Mag.Cunning", 0xFFAA00FF, DisplayMode.PIPS),

        // Ranger
        TIRELESS        ("TIRELESS_uses",             Ability.TIRELESS,                 "Tireless",    0xFF88FFAA, DisplayMode.PIPS),
        NATURES_VEIL    ("NATURES_VEIL_uses",         Ability.NATURES_VEIL,             "N.Veil",      0xFF44CC88, DisplayMode.PIPS),

        // Spezies
        BREATH_WEAPON   ("BREATH_WEAPON_uses",        Ability.BREATH_WEAPON,            "Breath",      0xFFFF6600, DisplayMode.PIPS),
        ADRENALINE      ("ADRENALINE_RUSH_uses",      Ability.ADRENALINE_RUSH,          "Adrenaline",  0xFFFF2200, DisplayMode.PIPS),
        HEALING_HANDS   ("HEALING_HANDS_uses",        Ability.HEALING_HANDS,            "Heal.Hands",  0xFFFFAAAA, DisplayMode.PIPS),
        FLIGHT          ("FLIGHT_uses",               Ability.FLIGHT,                   "Flight",      0xFF88CCFF, DisplayMode.PIPS);

        /** Welcher Anzeigemodus wird im HUD verwendet. */
        public enum DisplayMode {
            PIPS,   // Einzelne Punkte (gut für kleine Werte ≤ 12)
            BAR     // Fortschrittsbalken (gut für große Pools wie LAY_ON_HANDS)
        }

        public final String    dataKey;
        public final Ability   requiresAbility;
        public final String    displayName;
        public final int       color;       // ARGB
        public final DisplayMode displayMode;

        ResourcePool(String dataKey, Ability requiresAbility, String displayName,
                     int color, DisplayMode displayMode) {
            this.dataKey         = dataKey;
            this.requiresAbility = requiresAbility;
            this.displayName     = displayName;
            this.color           = color;
            this.displayMode     = displayMode;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  API — LESEN
    // ══════════════════════════════════════════════════════════════════

    /**
     * Gibt den aktuellen Wert des Pools zurück.
     * Funktioniert sowohl Server- als auch Client-seitig (via syncte Vars).
     */
    public static int getCurrent(Player player, ResourcePool pool) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return switch (pool) {
            case FOCUS_POINTS  -> AbilityDataUtils.getInt(vars, pool.dataKey, (int) vars.PlayerLevel);
            case LAY_ON_HANDS  -> AbilityDataUtils.getInt(vars, pool.dataKey, (int) vars.PlayerLevel * 5);
            default            -> AbilityDataUtils.getInt(vars, pool.dataKey, 0);
        };
    }

    /**
     * Gibt die Maximalladungen zurück.
     * Nur auf dem Server korrekt (nutzt AbilityResetRegistry).
     */
    public static int getMax(ServerPlayer player, ResourcePool pool) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (pool == ResourcePool.SORCERY_POINTS) {
            return (int) vars.PlayerLevel; // 1 Sorcery Point pro Sorcerer-Level
        }
        return Math.max(0, AbilityResetRegistry.getMaxUses(player, pool.requiresAbility, vars));
    }

    /**
     * Gibt die gecachte Max-Ladung aus AbilityData zurück — für Client-Seite.
     * Wird bei jedem Server-Update mitgesendet via vars.
     */
    public static int getMaxCached(Player player, ResourcePool pool) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int profB = (int) vars.ProficiencyBonus;
        int chaMod = Math.floorDiv((int) vars.Charisma - 10, 2);
        int wisMod = Math.floorDiv((int) vars.Wisdom - 10, 2);

        // Bekannte Maximalwerte lokal berechnen (ohne ServerPlayer)
        return switch (pool) {
            case FOCUS_POINTS   -> level;
            case RAGE           -> level >= 17 ? 6 : level >= 12 ? 5 : level >= 6 ? 4 : level >= 3 ? 3 : 2;
            case SORCERY_POINTS -> level;
            case BARDIC_INSP    -> Math.max(1, chaMod);
            case WILD_SHAPE     -> level >= 17 ? 4 : level >= 6 ? 3 : 2;
            case CHANNEL_DIV    -> level >= 18 ? 4 : level >= 6 ? 3 : 2;
            case CH_DIV_PAL     -> 2;
            case LAY_ON_HANDS   -> level * 5;
            case SECOND_WIND    -> level >= 10 ? 4 : level >= 4 ? 3 : 2;
            case ACTION_SURGE   -> level >= 17 ? 2 : 1;
            case INNATE_SORCERY -> 2;
            case MAGICAL_CUNNING-> 1;
            case TIRELESS       -> Math.max(1, wisMod);
            case NATURES_VEIL   -> profB;
            case BREATH_WEAPON,
                 ADRENALINE     -> profB;
            case HEALING_HANDS  -> 1;
            case FLIGHT         -> 1;
        };
    }

    // ══════════════════════════════════════════════════════════════════
    //  API — SCHREIBEN (nur Server)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Gibt `amount` Punkte aus dem Pool aus.
     * @return true wenn genug Punkte vorhanden, false sonst.
     */
    public static boolean spend(ServerPlayer player, ResourcePool pool, int amount) {
        int current = getCurrent(player, pool);
        if (current < amount) return false;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, pool.dataKey, current - amount);
        vars.markSyncDirty();
        return true;
    }

    /**
     * Fügt `amount` Punkte zum Pool hinzu (bis zum Maximum).
     */
    public static void restore(ServerPlayer player, ResourcePool pool, int amount) {
        int max     = getMax(player, pool);
        int current = getCurrent(player, pool);
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        AbilityDataUtils.set(vars, pool.dataKey, Math.min(max, current + amount));
        vars.markSyncDirty();
    }

    /** Füllt den Pool auf Maximum auf. */
    public static void restoreToMax(ServerPlayer player, ResourcePool pool) {
        restore(player, pool, getMax(player, pool));
    }

    // ══════════════════════════════════════════════════════════════════
    //  HUD-HILFE — welche Pools sollen angezeigt werden
    // ══════════════════════════════════════════════════════════════════

    /**
     * Gibt alle Pools zurück die im HUD angezeigt werden sollen.
     * Client-seitig nutzbar — liest aus syncten vars.Abilities.
     */
    public static List<ResourcePool> getActiveForDisplay(Player player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!vars.FinishedCharacterCreation) return List.of();

        String abilities = vars.Abilities == null ? "" : vars.Abilities;
        List<ResourcePool> result = new ArrayList<>();

        for (ResourcePool pool : ResourcePool.values()) {
            if (!abilities.contains(pool.requiresAbility.name())) continue;
            int max = getMaxCached(player, pool);
            if (max <= 0) continue;
            // Immer anzeigen wenn Pool nicht leer ist, oder bei wichtigen Pools immer
            int current = getCurrent(player, pool);
            if (current > 0 || isAlwaysShown(pool)) {
                result.add(pool);
            }
        }
        return result;
    }

    /** Diese Pools werden immer gezeigt (auch wenn 0 Ladungen). */
    private static boolean isAlwaysShown(ResourcePool pool) {
        return switch (pool) {
            case FOCUS_POINTS, SORCERY_POINTS, RAGE, LAY_ON_HANDS,
                 BARDIC_INSP, WILD_SHAPE, CHANNEL_DIV -> true;
            default -> false;
        };
    }

    // ══════════════════════════════════════════════════════════════════
    //  RESETS (nur Server) — Aufruf via SleepingInterferer
    // ══════════════════════════════════════════════════════════════════

    /**
     * Füllt alle Long-Rest-Pools auf Maximum auf.
     * Aufruf in SleepingInterferer nach AbilityResetRegistry.resetOnLongRest().
     */
    public static void resetForLongRest(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        for (ResourcePool pool : ResourcePool.values()) {
            if (SHORT_REST_ONLY.contains(pool)) continue; // Short-Rest-Pools werden separat behandelt
            int max = getMax(player, pool);
            if (max > 0) {
                AbilityDataUtils.set(vars, pool.dataKey, max);
            }
        }
        // Short-Rest-Pools ebenfalls bei Long Rest auffüllen (Long Rest ⊇ Short Rest)
        for (ResourcePool pool : SHORT_REST_ONLY) {
            int max = getMax(player, pool);
            if (max > 0) AbilityDataUtils.set(vars, pool.dataKey, max);
        }
        vars.markSyncDirty();
    }

    /**
     * Füllt nur Short-Rest-Pools auf (Second Wind, Action Surge).
     * Für zukünftige Short-Rest-Mechanik (Command /dnd rest short oder Item).
     */
    public static void resetForShortRest(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        for (ResourcePool pool : SHORT_REST_ONLY) {
            int max = getMax(player, pool);
            if (max > 0) AbilityDataUtils.set(vars, pool.dataKey, max);
        }
        vars.markSyncDirty();
    }

    /**
     * Pools die auf Short Rest auffüllen (und daher AUCH auf Long Rest, da LR ⊇ SR).
     * Alle anderen Pools sind Long-Rest-only.
     */
    private static final java.util.Set<ResourcePool> SHORT_REST_ONLY = java.util.Set.of(
            ResourcePool.SECOND_WIND,
            ResourcePool.ACTION_SURGE
    );
}