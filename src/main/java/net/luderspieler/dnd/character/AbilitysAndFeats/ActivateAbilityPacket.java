package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: Spieler aktiviert eine Ability aus dem Ability-Wheel.
 *
 * subAction ist ein optionaler Bezeichner für Sub-Aktionen, z. B.:
 *   FOCUS_POINTS  + "FLURRY_OF_BLOWS"
 *   FONT_OF_MAGIC + "SLOT_1"
 *   METAMAGIC     + "CAREFUL_SPELL"
 *
 * Kein UseResourceActionPacket mehr – alles läuft über dieses eine Packet.
 *
 * Registrierung in DndModNetworkRegistry:
 *   reg.playToServer(ActivateAbilityPacket.TYPE,
 *                    ActivateAbilityPacket.CODEC,
 *                    ActivateAbilityPacket::handle);
 */
public record ActivateAbilityPacket(String abilityName, String subAction)
        implements CustomPacketPayload {

    public static final Type<ActivateAbilityPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:activate_ability"));

    public static final StreamCodec<FriendlyByteBuf, ActivateAbilityPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ActivateAbilityPacket::abilityName,
                    ByteBufCodecs.STRING_UTF8, ActivateAbilityPacket::subAction,
                    ActivateAbilityPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // ── Client-Seite: Senden ─────────────────────────────────────────

    /** Normale Ability-Aktivierung ohne Sub-Aktion. */
    public static void send(Ability ability) {
        ClientPacketDistributor.sendToServer(new ActivateAbilityPacket(ability.name(), ""));
    }

    /**
     * Ability-Aktivierung mit Sub-Aktion.
     * Wird z. B. vom Sub-Wheel gesendet wenn der Spieler eine konkrete
     * Focus-Point-, Sorcery-Point- oder Metamagic-Option auswählt.
     */
    public static void send(Ability ability, String subAction) {
        ClientPacketDistributor.sendToServer(
                new ActivateAbilityPacket(ability.name(), subAction == null ? "" : subAction));
    }

    // ── Server-Seite: Handler ─────────────────────────────────────────

    public static void handle(ActivateAbilityPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            Ability ability;
            try {
                ability = Ability.valueOf(pkt.abilityName());
            } catch (IllegalArgumentException e) {
                return; // Unbekannte Ability – ignorieren
            }

            boolean triggered = AbilityMethods_PlayerTriggered.activate(
                    player, ability, pkt.subAction());

            if (!triggered) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "§c" + ability.getDisplayName() + " is not available right now."),
                        true);
            }
        });
    }
}