package net.luderspieler.dnd.network;

import net.luderspieler.dnd.spells.targeting.TargetingEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.luderspieler.dnd.DndMod;

public record AirClickPacket() implements CustomPacketPayload {
    public static final Type<AirClickPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "air_click"));

    public static final StreamCodec<FriendlyByteBuf, AirClickPacket> CODEC = StreamCodec.unit(new AirClickPacket());

    @Override
    public Type<AirClickPacket> type() {
        return TYPE;
    }

    public static void handle(final AirClickPacket message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // Wir führen die Logik auf dem Server aus, da das Paket dort ankommt
                TargetingEvents.handleCasting(player, InteractionHand.MAIN_HAND);
            }
        });
    }
}