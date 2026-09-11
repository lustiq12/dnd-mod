package net.luderspieler.dnd.npc.network;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.npc.NpcInteractionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client -> Server. Sent when the player closes the dialog screen, ending the conversation. */
public record CloseNpcDialogPacket() implements CustomPacketPayload {

    public static final Type<CloseNpcDialogPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "close_npc_dialog"));

    public static final StreamCodec<FriendlyByteBuf, CloseNpcDialogPacket> CODEC =
            StreamCodec.unit(new CloseNpcDialogPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CloseNpcDialogPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                NpcInteractionHandler.handleClosed(serverPlayer);
            }
        });
    }
}
