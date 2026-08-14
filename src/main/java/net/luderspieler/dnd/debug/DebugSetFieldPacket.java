package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
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

import java.lang.reflect.Field;

/** Sets a single PlayerVariables field on a target player via reflection, mirroring /dnd variable. */
@EventBusSubscriber
public record DebugSetFieldPacket(String targetUuid, String fieldName, String rawValue) implements CustomPacketPayload {

    public static final Type<DebugSetFieldPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_set_field"));

    public static final StreamCodec<FriendlyByteBuf, DebugSetFieldPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugSetFieldPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugSetFieldPacket::fieldName,
            ByteBufCodecs.STRING_UTF8, DebugSetFieldPacket::rawValue,
            DebugSetFieldPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String fieldName, String rawValue) {
        ClientPacketDistributor.sendToServer(new DebugSetFieldPacket(targetUuid, fieldName, rawValue));
    }

    public static void handle(final DebugSetFieldPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;

            try {
                DndModVariables.PlayerVariables vars = target.getData(DndModVariables.PLAYER_VARIABLES);
                Field field = vars.getClass().getField(message.fieldName());
                Class<?> t = field.getType();

                if (t == String.class) field.set(vars, message.rawValue());
                else if (t == double.class || t == Double.class) field.set(vars, Double.parseDouble(message.rawValue()));
                else if (t == boolean.class || t == Boolean.class) field.set(vars, Boolean.parseBoolean(message.rawValue()));
                else return;

                vars.markSyncDirty();
                DebugNetUtils.sendSnapshot(requester, target);
            } catch (Exception e) {
                DebugNetUtils.fail(requester, "Failed to set " + message.fieldName() + ": " + e.getMessage());
            }
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugSetFieldPacket::handle);
    }
}