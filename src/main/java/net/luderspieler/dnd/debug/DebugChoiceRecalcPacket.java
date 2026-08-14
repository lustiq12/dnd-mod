package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.character.choices.ChoiceUpdateSystem;
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

/** Recalculates ChoicesNeeded for a target player, same effect as /dnd update Choices. */
@EventBusSubscriber
public record DebugChoiceRecalcPacket(String targetUuid) implements CustomPacketPayload {

    public static final Type<DebugChoiceRecalcPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_choice_recalc"));

    public static final StreamCodec<FriendlyByteBuf, DebugChoiceRecalcPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugChoiceRecalcPacket::targetUuid,
            DebugChoiceRecalcPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid) {
        ClientPacketDistributor.sendToServer(new DebugChoiceRecalcPacket(targetUuid));
    }

    public static void handle(final DebugChoiceRecalcPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;
            ChoiceUpdateSystem.updateChoices(target);
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugChoiceRecalcPacket::handle);
    }
}