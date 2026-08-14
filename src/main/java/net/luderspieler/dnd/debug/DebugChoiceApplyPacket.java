package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.character.choices.ChoiceExecutor;
import net.luderspieler.dnd.network.DndModVariables;
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

import java.util.ArrayList;
import java.util.List;

/** Applies a choice to a target player, mirroring ExecuteChoicePacket's bookkeeping and ChoiceExecutor call. */
@EventBusSubscriber
public record DebugChoiceApplyPacket(String targetUuid, String choiceId, String selectedValue) implements CustomPacketPayload {

    public static final Type<DebugChoiceApplyPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_choice_apply"));

    public static final StreamCodec<FriendlyByteBuf, DebugChoiceApplyPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugChoiceApplyPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugChoiceApplyPacket::choiceId,
            ByteBufCodecs.STRING_UTF8, DebugChoiceApplyPacket::selectedValue,
            DebugChoiceApplyPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String choiceId, String selectedValue) {
        ClientPacketDistributor.sendToServer(new DebugChoiceApplyPacket(targetUuid, choiceId, selectedValue));
    }

    public static void handle(final DebugChoiceApplyPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;
            if (message.choiceId().isBlank()) return;

            DndModVariables.PlayerVariables vars = target.getData(DndModVariables.PLAYER_VARIABLES);

            List<String> neededList = new ArrayList<>();
            if (vars.ChoicesNeeded != null && !vars.ChoicesNeeded.isBlank()) {
                boolean removed = false;
                for (String s : vars.ChoicesNeeded.split(",")) {
                    String trimmed = s.trim();
                    if (trimmed.isEmpty()) continue;
                    if (!removed && trimmed.equals(message.choiceId())) removed = true;
                    else neededList.add(trimmed);
                }
            }
            vars.ChoicesNeeded = String.join(",", neededList);

            String newEntry = message.choiceId() + ":" + message.selectedValue();
            if (vars.ChoicesMade == null || vars.ChoicesMade.isBlank()) vars.ChoicesMade = newEntry;
            else vars.ChoicesMade += "," + newEntry;

            ChoiceExecutor.apply(target, message.choiceId(), message.selectedValue());

            vars.markSyncDirty();
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugChoiceApplyPacket::handle);
    }
}