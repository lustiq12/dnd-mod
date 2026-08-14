package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.resources.ResourceManager;
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

/** Adjusts a ResourceManager pool for a target player via the pool's real spend/restore/restoreToMax methods. */
@EventBusSubscriber
public record DebugResourceAdjustPacket(String targetUuid, String poolName, int amount, boolean toMax) implements CustomPacketPayload {

    public static final Type<DebugResourceAdjustPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_resource_adjust"));

    public static final StreamCodec<FriendlyByteBuf, DebugResourceAdjustPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugResourceAdjustPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugResourceAdjustPacket::poolName,
            ByteBufCodecs.INT, DebugResourceAdjustPacket::amount,
            ByteBufCodecs.BOOL, DebugResourceAdjustPacket::toMax,
            DebugResourceAdjustPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String poolName, int amount, boolean toMax) {
        ClientPacketDistributor.sendToServer(new DebugResourceAdjustPacket(targetUuid, poolName, amount, toMax));
    }

    public static void handle(final DebugResourceAdjustPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;

            ResourceManager.ResourcePool pool;
            try {
                pool = ResourceManager.ResourcePool.valueOf(message.poolName());
            } catch (IllegalArgumentException e) {
                return;
            }

            if (message.toMax()) {
                ResourceManager.restoreToMax(target, pool);
            } else if (message.amount() > 0) {
                ResourceManager.restore(target, pool, message.amount());
            } else if (message.amount() < 0) {
                ResourceManager.spend(target, pool, -message.amount());
            }

            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugResourceAdjustPacket::handle);
    }
}