package net.luderspieler.dnd.npc;

import com.google.gson.Gson;

/** Shared Gson instance and null/blank-safe helpers for reading and writing NPC data strings. */
public class NpcJson {

    private static final Gson GSON = new Gson();

    private NpcJson() {
    }

    public static String toJson(Object value) {
        return GSON.toJson(value);
    }

    public static NpcConfiguration configFromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return GSON.fromJson(json, NpcConfiguration.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static NpcData dataFromJsonOrEmpty(String json) {
        if (json == null || json.isBlank()) return NpcData.empty();
        try {
            NpcData parsed = GSON.fromJson(json, NpcData.class);
            return parsed == null ? NpcData.empty() : parsed;
        } catch (Exception e) {
            return NpcData.empty();
        }
    }
}
