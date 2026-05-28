package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent from client to server when the player activates an ability from the Ability Wheel.
 * Register in DndModNetworkRegistry:
 *   reg.playToServer(ActivateAbilityPacket.TYPE, ActivateAbilityPacket.CODEC, ActivateAbilityPacket::handle);
 */
public record ActivateAbilityPacket(String abilityName) implements CustomPacketPayload {

    public static final Type<ActivateAbilityPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:activate_ability"));

    public static final StreamCodec<FriendlyByteBuf, ActivateAbilityPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ActivateAbilityPacket::abilityName,
                    ActivateAbilityPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    /** Send from client. */
    public static void send(Ability ability) {
        ClientPacketDistributor.sendToServer(new ActivateAbilityPacket(ability.name()));
    }

    /** Server-side handler. */
    public static void handle(ActivateAbilityPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            Ability ability;
            try {
                ability = Ability.valueOf(pkt.abilityName());
            } catch (IllegalArgumentException e) {
                return; // unknown ability name — ignore
            }

            boolean triggered = AbilityMethods_PlayerTriggered.activate(player, ability);
            if (!triggered) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "§c" + ability.getDisplayName() + " is not available right now."),
                        true
                );
            }
        });
    }
}
