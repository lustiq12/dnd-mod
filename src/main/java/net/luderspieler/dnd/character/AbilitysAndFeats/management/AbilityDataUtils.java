package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import net.luderspieler.dnd.network.DndModVariables;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Read/write helper for vars.AbilityData — a key-value map stored as a string.
 *
 * Format: {key1=value1,key2=value2}
 * Example: {ToughBonus=12,RageUses=3,RelentlessEndurance_used=1}
 *
 * Values are always stored as strings; use the typed getters (getInt, getDouble, getBool)
 * for convenience. All writes go through set() to keep the format consistent.
 */
public class AbilityDataUtils {

    private static final char OPEN   = '{';
    private static final char CLOSE  = '}';
    private static final char DELIM  = ',';
    private static final char ASSIGN = '=';

    // ── READ ─────────────────────────────────────────────────────────

    /** Returns the raw string value for the given key, or {@code defaultValue} if absent. */
    public static String get(DndModVariables.PlayerVariables vars, String key, String defaultValue) {
        Map<String, String> map = parse(vars.AbilityData);
        return map.getOrDefault(key, defaultValue);
    }

    /** Returns the int value for the given key, or {@code defaultValue} if absent/unparseable. */
    public static int getInt(DndModVariables.PlayerVariables vars, String key, int defaultValue) {
        try { return Integer.parseInt(get(vars, key, String.valueOf(defaultValue)).trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /** Returns the double value for the given key, or {@code defaultValue} if absent/unparseable. */
    public static double getDouble(DndModVariables.PlayerVariables vars, String key, double defaultValue) {
        try { return Double.parseDouble(get(vars, key, String.valueOf(defaultValue)).trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /** Returns true if the key exists and its value is "1" or "true". */
    public static boolean getBool(DndModVariables.PlayerVariables vars, String key) {
        String v = get(vars, key, "0");
        return "1".equals(v) || "true".equalsIgnoreCase(v);
    }

    // ── WRITE ─────────────────────────────────────────────────────────

    /** Stores a string value. Creates or overwrites the key. */
    public static void set(DndModVariables.PlayerVariables vars, String key, String value) {
        Map<String, String> map = parse(vars.AbilityData);
        map.put(key, value);
        vars.AbilityData = serialize(map);
    }

    public static void set(DndModVariables.PlayerVariables vars, String key, int value) {
        set(vars, key, String.valueOf(value));
    }

    public static void set(DndModVariables.PlayerVariables vars, String key, double value) {
        set(vars, key, String.valueOf(value));
    }

    public static void set(DndModVariables.PlayerVariables vars, String key, boolean value) {
        set(vars, key, value ? "1" : "0");
    }

    /** Removes a key from the map. Safe to call if the key doesn't exist. */
    public static void remove(DndModVariables.PlayerVariables vars, String key) {
        Map<String, String> map = parse(vars.AbilityData);
        if (map.remove(key) != null) {
            vars.AbilityData = serialize(map);
        }
    }

    /** Increments an int key by {@code amount}. If absent, starts from 0. */
    public static void increment(DndModVariables.PlayerVariables vars, String key, int amount) {
        set(vars, key, getInt(vars, key, 0) + amount);
    }

    // ── INTERNAL ─────────────────────────────────────────────────────

    /** Parses "{k=v,k2=v2}" into a mutable LinkedHashMap preserving insertion order. */
    public static Map<String, String> parse(String raw) {
        Map<String, String> map = new LinkedHashMap<>();
        if (raw == null || raw.isBlank() || raw.equals("\"\"")) return map;

        // Strip surrounding braces if present
        String trimmed = raw.trim();
        if (trimmed.startsWith(String.valueOf(OPEN)))  trimmed = trimmed.substring(1);
        if (trimmed.endsWith(String.valueOf(CLOSE)))   trimmed = trimmed.substring(0, trimmed.length() - 1);
        if (trimmed.isBlank()) return map;

        for (String entry : trimmed.split(String.valueOf(DELIM))) {
            String[] parts = entry.split(String.valueOf(ASSIGN), 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }

    /** Serializes a map back to "{k=v,k2=v2}" format. */
    private static String serialize(Map<String, String> map) {
        if (map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder().append(OPEN);
        boolean first = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!first) sb.append(DELIM);
            sb.append(e.getKey()).append(ASSIGN).append(e.getValue());
            first = false;
        }
        return sb.append(CLOSE).toString();
    }
}
