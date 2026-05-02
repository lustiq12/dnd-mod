package net.luderspieler.dnd.spells;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PrepareSpellsPacket(
        String cantrips, String lvl1, String lvl2, String lvl3, String lvl4,
        String lvl5, String lvl6, String lvl7, String lvl8, String lvl9
) implements CustomPacketPayload {

    public static final Type<PrepareSpellsPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:prepare_spells"));

    // Manueller Codec für mehr als 6 Felder
    public static final StreamCodec<FriendlyByteBuf, PrepareSpellsPacket> CODEC = new StreamCodec<>() {
        @Override
        public PrepareSpellsPacket decode(FriendlyByteBuf buf) {
            return new PrepareSpellsPacket(
                    buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(),
                    buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf()
            );
        }

        @Override
        public void encode(FriendlyByteBuf buf, PrepareSpellsPacket pkt) {
            buf.writeUtf(pkt.cantrips); buf.writeUtf(pkt.lvl1); buf.writeUtf(pkt.lvl2);
            buf.writeUtf(pkt.lvl3); buf.writeUtf(pkt.lvl4); buf.writeUtf(pkt.lvl5);
            buf.writeUtf(pkt.lvl6); buf.writeUtf(pkt.lvl7); buf.writeUtf(pkt.lvl8);
            buf.writeUtf(pkt.lvl9);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(PrepareSpellsPacket pkt) {
        ClientPacketDistributor.sendToServer(pkt);
    }

    public static void handle(final PrepareSpellsPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            // Zugriff auf die Variablen wie in deinem Beispiel
            net.luderspieler.dnd.network.DndModVariables.PlayerVariables vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);

            // Daten setzen
            vars.PreparedCantrips = data.cantrips;
            vars.PreparedSpellsLVL1 = data.lvl1;
            vars.PreparedSpellsLVL2 = data.lvl2;
            vars.PreparedSpellsLVL3 = data.lvl3;
            vars.PreparedSpellsLVL4 = data.lvl4;
            vars.PreparedSpellsLVL5 = data.lvl5;
            vars.PreparedSpellsLVL6 = data.lvl6;
            vars.PreparedSpellsLVL7 = data.lvl7;
            vars.PreparedSpellsLVL8 = data.lvl8;
            vars.PreparedSpellsLVL9 = data.lvl9;

            // Das ist der entscheidende Teil aus deinem Beispiel:
            vars.markSyncDirty();
        });
    }
}