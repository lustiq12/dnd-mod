package net.luderspieler.dnd.rests;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server.
 * Sent when the player clicks "Finish Long Rest" on LongRestScreen (Screen 2).
 * Server applies all long-rest benefits at this point.
 */
public record ApplyLongRestPacket(BlockPos bedPos) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("dnd", "apply_long_rest");
    public static final Type<ApplyLongRestPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, ApplyLongRestPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeBlockPos(pkt.bedPos()),
                    buf        -> new ApplyLongRestPacket(buf.readBlockPos())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(BlockPos bedPos) {
        ClientPacketDistributor.sendToServer(new ApplyLongRestPacket(bedPos));
    }

    public static void handle(ApplyLongRestPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            SleepingIntereferer.applyLongRestBenefits(player, pkt.bedPos());
        });
    }
}