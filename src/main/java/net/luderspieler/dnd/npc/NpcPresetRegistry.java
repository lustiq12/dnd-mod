package net.luderspieler.dnd.npc;

import java.util.HashMap;
import java.util.Map;

/**
 * Presets are keyed by the entity class' simple name (e.g. "DwarvenSmithEntity") instead of
 * Class<?> so this registry never needs a compile-time reference to any specific entity class.
 * Call register() once from your mod's setup code for every entity that should act as an NPC.
 */
public class NpcPresetRegistry {

    private static final Map<String, NpcPreset> PRESETS = new HashMap<>();

    private NpcPresetRegistry() {
    }

    public static void register(String entitySimpleClassName, NpcPreset preset) {
        PRESETS.put(entitySimpleClassName, preset);
    }

    public static NpcPreset get(String entitySimpleClassName) {
        return PRESETS.get(entitySimpleClassName);
    }
}
