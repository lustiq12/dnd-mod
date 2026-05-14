package net.luderspieler.dnd.character.choices;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ExecuteChoicePacket(String choiceId, String selectedValue) implements CustomPacketPayload {
    public static final Type<ExecuteChoicePacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:execute_choice"));

    public static final StreamCodec<FriendlyByteBuf, ExecuteChoicePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ExecuteChoicePacket::choiceId,
            ByteBufCodecs.STRING_UTF8, ExecuteChoicePacket::selectedValue,
            ExecuteChoicePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // Statische Methode passend zu deinem Stil
    public static void send(ExecuteChoicePacket pkt) {
        net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(pkt);
    }

    public static void handle(final ExecuteChoicePacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);

            // Eintrag aus der Liste entfernen
            List<String> list = new ArrayList<>();
            if (!vars.ChoicesNeeded.isBlank()) {
                for (String s : vars.ChoicesNeeded.split(",")) {
                    if (!s.trim().equals(data.choiceId())) {
                        list.add(s.trim());
                    }
                }
            }
            vars.ChoicesNeeded = String.join(",", list);

            // TODO: Hier deine Choice-Logik-Klasse aufrufen
            // ChoiceExecutor.apply(player, data.choiceId(), data.selectedValue());

            vars.markSyncDirty();
        });
    }
}