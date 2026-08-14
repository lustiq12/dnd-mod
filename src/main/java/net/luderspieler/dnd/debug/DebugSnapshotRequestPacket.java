package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Asks the server for a fresh PlayerVariables snapshot of the given target. */
@EventBusSubscriber
public record DebugSnapshotRequestPacket(String targetUuid) implements CustomPacketPayload {

    public static final Type<DebugSnapshotRequestPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_snapshot_request"));

    public static final StreamCodec<FriendlyByteBuf, DebugSnapshotRequestPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugSnapshotRequestPacket::targetUuid,
            DebugSnapshotRequestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid) {
        ClientPacketDistributor.sendToServer(new DebugSnapshotRequestPacket(targetUuid));
    }

    public static void handle(final DebugSnapshotRequestPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) {
                DebugNetUtils.fail(requester, "Target player not found or not online.");
                return;
            }
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugSnapshotRequestPacket::handle);
    }
}