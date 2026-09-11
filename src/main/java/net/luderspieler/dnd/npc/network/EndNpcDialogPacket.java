package net.luderspieler.dnd.npc.network;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.npc.screens.NpcDialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server -> Client. Closes the dialog screen if it is currently showing the given NPC. */
public record EndNpcDialogPacket(int entityId) implements CustomPacketPayload {

    public static final Type<EndNpcDialogPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "end_npc_dialog"));

    public static final StreamCodec<FriendlyByteBuf, EndNpcDialogPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EndNpcDialogPacket::entityId,
            EndNpcDialogPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EndNpcDialogPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.screen instanceof NpcDialogScreen screen && screen.getEntityId() == pkt.entityId()) {
                mc.setScreen(null);
            }
        });
    }
}
