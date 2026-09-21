package net.luderspieler.dnd.network;

import net.luderspieler.dnd.item.CoinBagItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CoinBagScrollPacket(int slotId, int newIndex) implements CustomPacketPayload {

    public static final Type<CoinBagScrollPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:coin_bag_scroll"));

    public static final StreamCodec<FriendlyByteBuf, CoinBagScrollPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CoinBagScrollPacket::slotId,
                    ByteBufCodecs.INT, CoinBagScrollPacket::newIndex,
                    CoinBagScrollPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(int slotId, int newIndex) {
        ClientPacketDistributor.sendToServer(new CoinBagScrollPacket(slotId, newIndex));
    }

    public static void handle(CoinBagScrollPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            if (player.containerMenu != null && pkt.slotId() >= 0 && pkt.slotId() < player.containerMenu.slots.size()) {
                var stack = player.containerMenu.getSlot(pkt.slotId()).getItem();
                if (stack.getItem() instanceof CoinBagItem) {
                    CoinBagItem.setSelectedIndex(stack, pkt.newIndex());
                }
            }
        });
    }
}