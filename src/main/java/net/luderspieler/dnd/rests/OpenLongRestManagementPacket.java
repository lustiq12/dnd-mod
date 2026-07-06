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
 * Sent from SleepingIntereferer.onWakeUp() to open the management screen
 * (Screen 2: spell prep + long rest finish).
 * Carries the bed position (for bonus scanning) and whether the rest succeeded.
 *
 * Registration (DndModNetworkRegistry):
 *   registrar.playToClient(OpenLongRestManagementPacket.TYPE,
 *       OpenLongRestManagementPacket.CODEC, OpenLongRestManagementPacket::handle);
 */
public record OpenLongRestManagementPacket(BlockPos bedPos, boolean wasSuccessful)
        implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("dnd", "open_long_rest_management");
    public static final Type<OpenLongRestManagementPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, OpenLongRestManagementPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> { buf.writeBlockPos(pkt.bedPos()); buf.writeBoolean(pkt.wasSuccessful()); },
                    buf        -> new OpenLongRestManagementPacket(buf.readBlockPos(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(ServerPlayer player, BlockPos bedPos, boolean wasSuccessful) {
        PacketDistributor.sendToPlayer(player,
                new OpenLongRestManagementPacket(bedPos, wasSuccessful));
    }

    public static void handle(OpenLongRestManagementPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() ->
                Minecraft.getInstance().execute(() ->
                        Minecraft.getInstance().setScreen(
                                new LongRestScreen(pkt.bedPos(), pkt.wasSuccessful()))
                )
        );
    }
}