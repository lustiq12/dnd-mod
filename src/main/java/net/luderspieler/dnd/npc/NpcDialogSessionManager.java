package net.luderspieler.dnd.npc;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Tracks which dialog node each player currently sees. Never persisted - conversations always restart fresh. */
@EventBusSubscriber
public class NpcDialogSessionManager {

    private record Session(int entityId, String nodeId) {
    }

    private static final Map<UUID, Session> ACTIVE = new HashMap<>();

    private NpcDialogSessionManager() {
    }

    public static void start(ServerPlayer player, Entity npc, String nodeId) {
        ACTIVE.put(player.getUUID(), new Session(npc.getId(), nodeId));
    }

    public static void advance(ServerPlayer player, String nodeId) {
        Session current = ACTIVE.get(player.getUUID());
        if (current == null) return;
        ACTIVE.put(player.getUUID(), new Session(current.entityId(), nodeId));
    }

    public static void end(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
    }

    public static Integer getActiveEntityId(ServerPlayer player) {
        Session session = ACTIVE.get(player.getUUID());
        return session == null ? null : session.entityId();
    }

    public static String getActiveNodeId(ServerPlayer player) {
        Session session = ACTIVE.get(player.getUUID());
        return session == null ? null : session.nodeId();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE.remove(event.getEntity().getUUID());
    }
}
