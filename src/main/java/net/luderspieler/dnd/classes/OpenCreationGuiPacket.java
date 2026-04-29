package net.luderspieler.dnd.classes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client.
 * showDeathChoice = true  → open DeathChoiceScreen (had a character, died)
 * showDeathChoice = false → open RaceListScreen directly (first time ever)
 */
public record OpenCreationGuiPacket(boolean showDeathChoice) implements CustomPacketPayload {

    public static final Type<OpenCreationGuiPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:open_creation_gui"));

    public static final StreamCodec<FriendlyByteBuf, OpenCreationGuiPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, OpenCreationGuiPacket::showDeathChoice,
                    OpenCreationGuiPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // ── CLIENT-SIDE HANDLE ──
    public static void handle(OpenCreationGuiPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (pkt.showDeathChoice()) {
                mc.setScreen(new DeathChoiceScreen());
            } else {
                mc.setScreen(new RaceListScreen(false));
            }
        });
    }
}