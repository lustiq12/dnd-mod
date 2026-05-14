package net.luderspieler.dnd.character.network;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KeepCharacterPacket() implements CustomPacketPayload {

    public static final Type<KeepCharacterPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:keep_character"));

    public static final StreamCodec<FriendlyByteBuf, KeepCharacterPacket> CODEC =
            StreamCodec.unit(new KeepCharacterPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send() {
        ClientPacketDistributor.sendToServer(new KeepCharacterPacket());
    }

    public static void handle(KeepCharacterPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

            // WICHTIG: Erst Variablen sichern
            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();

            CharacterCreationPacket.applyAttrs(player, null, false);
        });
    }
}