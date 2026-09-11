package net.luderspieler.dnd.npc.network;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.npc.screens.NpcDialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client. Tells the client which dialog node to display for which NPC entity.
 * The node text and options are not sent here - they are read straight off the entity's
 * already-synced DATA_Configuration on the client, the same way any other tracked entity
 * data field works.
 */
public record OpenNpcDialogPacket(int entityId, String nodeId) implements CustomPacketPayload {

    public static final Type<OpenNpcDialogPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "open_npc_dialog"));

    public static final StreamCodec<FriendlyByteBuf, OpenNpcDialogPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenNpcDialogPacket::entityId,
            ByteBufCodecs.STRING_UTF8, OpenNpcDialogPacket::nodeId,
            OpenNpcDialogPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenNpcDialogPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.screen instanceof NpcDialogScreen screen && screen.getEntityId() == pkt.entityId()) {
                screen.updateNode(pkt.nodeId());
            } else {
                mc.setScreen(new NpcDialogScreen(pkt.entityId(), pkt.nodeId()));
                mc.player.displayClientMessage(Component.literal("handle packet"), false);
            }
        });
    }
}
