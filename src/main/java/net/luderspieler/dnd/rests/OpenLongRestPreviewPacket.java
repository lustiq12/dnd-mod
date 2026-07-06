package net.luderspieler.dnd.rests;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client.
 * Sent from SleepingIntereferer.onBedEnter() when all rest conditions are met.
 * Opens LongRestPreviewScreen (Screen 1) on the client.
 *
 * Replaces the old OpenLongRestScreenPacket — delete that file.
 *
 * Registration (DndModNetworkRegistry):
 *   registrar.playToClient(OpenLongRestPreviewPacket.TYPE,
 *       OpenLongRestPreviewPacket.CODEC, OpenLongRestPreviewPacket::handle);
 */
public record OpenLongRestPreviewPacket(BlockPos bedPos) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("dnd", "open_long_rest_preview");
    public static final Type<OpenLongRestPreviewPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, OpenLongRestPreviewPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeBlockPos(pkt.bedPos()),
                    buf        -> new OpenLongRestPreviewPacket(buf.readBlockPos())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(ServerPlayer player, BlockPos bedPos) {
        PacketDistributor.sendToPlayer(player, new OpenLongRestPreviewPacket(bedPos));
    }

    public static void handle(OpenLongRestPreviewPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() ->
                Minecraft.getInstance().execute(() ->
                        Minecraft.getInstance().setScreen(
                                new LongRestPreviewScreen(pkt.bedPos()))
                )
        );
    }
}