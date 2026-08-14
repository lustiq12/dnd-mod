package net.luderspieler.dnd.debug;

import java.util.List;
import java.util.UUID;

/** Client-side cache of the most recently requested attribute modifier breakdown. */
public class DebugAttributesClientState {

    public record ModifierEntry(String id, double amount, String operation) {}
    public record AttributeEntry(String attributeId, double base, double total, List<ModifierEntry> modifiers) {}
    public record Snapshot(UUID uuid, List<AttributeEntry> attributes) {}

    private static Snapshot latest;

    public static void set(Snapshot snapshot) {
        latest = snapshot;
    }

    public static Snapshot get() {
        return latest;
    }
}