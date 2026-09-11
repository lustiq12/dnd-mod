package net.luderspieler.dnd.npc.network;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.npc.NpcInteractionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client -> Server. Player picked answer optionIndex in the currently open dialog node. */
public record ChooseNpcDialogOptionPacket(int entityId, int optionIndex) implements CustomPacketPayload {

    public static final Type<ChooseNpcDialogOptionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "choose_npc_dialog_option"));

    public static final StreamCodec<FriendlyByteBuf, ChooseNpcDialogOptionPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ChooseNpcDialogOptionPacket::entityId,
            ByteBufCodecs.VAR_INT, ChooseNpcDialogOptionPacket::optionIndex,
            ChooseNpcDialogOptionPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ChooseNpcDialogOptionPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                NpcInteractionHandler.handleOptionChosen(serverPlayer, pkt.entityId(), pkt.optionIndex());
            }
        });
    }
}
