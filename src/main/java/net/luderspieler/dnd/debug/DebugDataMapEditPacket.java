package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.aUtils.GeneralDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
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

/** Edits a single key in AbilityData or GeneralData, the two string-encoded key-value maps. */
@EventBusSubscriber
public record DebugDataMapEditPacket(String targetUuid, String mapField, String key, String value, boolean remove)
        implements CustomPacketPayload {

    public static final Type<DebugDataMapEditPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_data_map_edit"));

    public static final StreamCodec<FriendlyByteBuf, DebugDataMapEditPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugDataMapEditPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugDataMapEditPacket::mapField,
            ByteBufCodecs.STRING_UTF8, DebugDataMapEditPacket::key,
            ByteBufCodecs.STRING_UTF8, DebugDataMapEditPacket::value,
            ByteBufCodecs.BOOL, DebugDataMapEditPacket::remove,
            DebugDataMapEditPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String mapField, String key, String value, boolean remove) {
        ClientPacketDistributor.sendToServer(new DebugDataMapEditPacket(targetUuid, mapField, key, value, remove));
    }

    public static void handle(final DebugDataMapEditPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;

            DndModVariables.PlayerVariables vars = target.getData(DndModVariables.PLAYER_VARIABLES);

            if ("AbilityData".equals(message.mapField())) {
                if (message.remove()) AbilityDataUtils.remove(vars, message.key());
                else AbilityDataUtils.set(vars, message.key(), message.value());
            } else if ("GeneralData".equals(message.mapField())) {
                if (message.remove()) GeneralDataUtils.remove(vars, message.key());
                else GeneralDataUtils.set(vars, message.key(), message.value());
            } else {
                DebugNetUtils.fail(requester, "Unknown data map: " + message.mapField());
                return;
            }

            vars.markSyncDirty();
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugDataMapEditPacket::handle);
    }
}