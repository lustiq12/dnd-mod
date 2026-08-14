package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.network.DndModVariables;

import java.util.UUID;

/** Client-side cache of the most recently requested debug snapshot, read by the debug GUI. */
public class DebugClientState {

    public record Snapshot(UUID uuid, String name, DndModVariables.PlayerVariables vars) {}

    private static Snapshot latest;

    public static void set(Snapshot snapshot) {
        latest = snapshot;
    }

    public static Snapshot get() {
        return latest;
    }
}