package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.character.feats.FeatRegistry;
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

/** Applies a general feat to a target player through the real FeatRegistry.apply logic. */
@EventBusSubscriber
public record DebugFeatApplyPacket(String targetUuid, String featId) implements CustomPacketPayload {

    public static final Type<DebugFeatApplyPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_feat_apply"));

    public static final StreamCodec<FriendlyByteBuf, DebugFeatApplyPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugFeatApplyPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugFeatApplyPacket::featId,
            DebugFeatApplyPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String featId) {
        ClientPacketDistributor.sendToServer(new DebugFeatApplyPacket(targetUuid, featId));
    }

    public static void handle(final DebugFeatApplyPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;
            if (FeatRegistry.hasFeat(target, message.featId())) return;
            FeatRegistry.apply(target, message.featId());
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugFeatApplyPacket::handle);
    }
}