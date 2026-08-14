package net.luderspieler.dnd.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

/** Shared permission check and snapshot dispatch used by every debug packet handler. */
public class DebugNetUtils {

    public static ServerPlayer resolveTarget(ServerPlayer requester, String targetUuid) {
        if (!requester.hasPermissions(2)) return null;
        if (requester.getServer() == null) return null;
        try {
            UUID uuid = UUID.fromString(targetUuid);
            return requester.getServer().getPlayerList().getPlayer(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void sendSnapshot(ServerPlayer requester, ServerPlayer target) {
        PacketDistributor.sendToPlayer(requester, new DebugSnapshotResponsePacket(
                target.getStringUUID(), target.getName().getString(),
                target.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES)));
    }

    public static void fail(ServerPlayer requester, String message) {
        requester.sendSystemMessage(Component.literal("§c[Debug] " + message));
    }
}