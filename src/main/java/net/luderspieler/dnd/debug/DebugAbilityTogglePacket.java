package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.Utils.AbilityUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Adds or removes an Ability on a target player, routed through AbilityUtils so _uses stays consistent. */
@EventBusSubscriber
public record DebugAbilityTogglePacket(String targetUuid, String abilityName, boolean add) implements CustomPacketPayload {

    public static final Type<DebugAbilityTogglePacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_ability_toggle"));

    public static final StreamCodec<FriendlyByteBuf, DebugAbilityTogglePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugAbilityTogglePacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugAbilityTogglePacket::abilityName,
            ByteBufCodecs.BOOL, DebugAbilityTogglePacket::add,
            DebugAbilityTogglePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String abilityName, boolean add) {
        ClientPacketDistributor.sendToServer(new DebugAbilityTogglePacket(targetUuid, abilityName, add));
    }

    public static void handle(final DebugAbilityTogglePacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;

            try {
                Ability ability = Ability.valueOf(message.abilityName());
                if (message.add()) AbilityUtils.addAbility(target, ability);
                else AbilityUtils.removeAbility(target, ability);
                DebugNetUtils.sendSnapshot(requester, target);
            } catch (IllegalArgumentException e) {
                DebugNetUtils.fail(requester, "Unknown ability: " + message.abilityName());
            }
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugAbilityTogglePacket::handle);
    }
}