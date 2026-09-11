package net.luderspieler.dnd.npc;

import net.luderspieler.dnd.npc.network.ChooseNpcDialogOptionPacket;
import net.luderspieler.dnd.npc.network.EndNpcDialogPacket;
import net.luderspieler.dnd.npc.network.OpenNpcDialogPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Server-side logic for NPC dialog. Called from NpcRightclickHandlingProcedure and from the
 * dialog packet handlers below. Never touches the client.
 */
public class NpcInteractionHandler {

    private NpcInteractionHandler() {
    }

    /** Called from the mobInteract() hook procedure. */
    public static void handleInteract(Entity entity, Player player) {
        if (entity.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!NpcDataAccess.isNpc(entity)) return;

        NpcConfiguration config = NpcJson.configFromJson(NpcDataAccess.getConfiguration(entity));
        if (config == null) config = NpcConfigInitializer.initialize(entity);
        if (config == null || config.nodes().isEmpty()) return;

        markMet(entity, serverPlayer);

        NpcDialogSessionManager.start(serverPlayer, entity, config.startNodeId());
        PacketDistributor.sendToPlayer(serverPlayer, new OpenNpcDialogPacket(entity.getId(), config.startNodeId()));
    }

    /** Called from ChooseNpcDialogOptionPacket.handle(). */
    public static void handleOptionChosen(ServerPlayer player, int entityId, int optionIndex) {
        Integer activeEntityId = NpcDialogSessionManager.getActiveEntityId(player);
        String activeNodeId = NpcDialogSessionManager.getActiveNodeId(player);
        if (activeEntityId == null || activeNodeId == null || activeEntityId != entityId) return;

        Entity entity = player.level().getEntity(entityId);
        if (entity == null) return;

        NpcConfiguration config = NpcJson.configFromJson(NpcDataAccess.getConfiguration(entity));
        if (config == null) return;

        NpcDialogNode node = config.nodes().get(activeNodeId);
        if (node == null || optionIndex < 0 || optionIndex >= node.options().size()) return;

        NpcDialogOption option = node.options().get(optionIndex);

        if (option.opensTrade()) {
            NpcDialogSessionManager.end(player);
            MenuProvider provider = new SimpleMenuProvider(
                    (windowId, inventory, p) -> new NpcTradeMenu(windowId, inventory, entityId),
                    Component.literal(config.displayName()));
            player.openMenu(provider, buf -> buf.writeVarInt(entityId));
            return;
        }

        if (option.nextNodeId() == null || option.nextNodeId().isBlank()) {
            NpcDialogSessionManager.end(player);
            PacketDistributor.sendToPlayer(player, new EndNpcDialogPacket(entityId));
            return;
        }

        NpcDialogSessionManager.advance(player, option.nextNodeId());
        PacketDistributor.sendToPlayer(player, new OpenNpcDialogPacket(entityId, option.nextNodeId()));
    }

    /** Called from CloseNpcDialogPacket.handle(). */
    public static void handleClosed(ServerPlayer player) {
        NpcDialogSessionManager.end(player);
    }

    private static void markMet(Entity entity, ServerPlayer player) {
        NpcData data = NpcJson.dataFromJsonOrEmpty(NpcDataAccess.getData(entity));
        data.playerStates().put(player.getStringUUID(), new NpcPlayerState(true));
        NpcDataAccess.setData(entity, NpcJson.toJson(data));
    }
}
