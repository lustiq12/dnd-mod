package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

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
            // Hier definierst du "player" als ServerPlayer
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.FinishedCharacterCreation = true;

            ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);

            if (cls != null) {
                // Du nutzt hier einfach "player", da dieser bereits als ServerPlayer validiert wurde
                CharacterCreationPacket.applyAttrs(player, cls.getAttributeModifiers(), false);

                // Falls deine Rasse auch Attribute hat, solltest du diese hier ebenfalls
                // erneut anwenden, damit nach dem Tod alles wieder da ist:
                RaceDefinition race = RaceRegistry.getRace(vars.PlayerRace);
                if (race != null) {
                    CharacterCreationPacket.applyAttrs(player, race.getAttributeModifiers(), true);
                }
            }

            vars.markSyncDirty();
        });
    }
}