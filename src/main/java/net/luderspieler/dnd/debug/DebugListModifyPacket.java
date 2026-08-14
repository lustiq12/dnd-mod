package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Adds or removes one entry from a comma-separated PlayerVariables String field. allowDuplicates skips the dedup check on add, needed for fields like ChoicesNeeded that can legitimately repeat an ID. */
@EventBusSubscriber
public record DebugListModifyPacket(String targetUuid, String fieldName, String item, boolean add, boolean allowDuplicates) implements CustomPacketPayload {

    public static final Type<DebugListModifyPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_list_modify"));

    public static final StreamCodec<FriendlyByteBuf, DebugListModifyPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugListModifyPacket::targetUuid,
            ByteBufCodecs.STRING_UTF8, DebugListModifyPacket::fieldName,
            ByteBufCodecs.STRING_UTF8, DebugListModifyPacket::item,
            ByteBufCodecs.BOOL, DebugListModifyPacket::add,
            ByteBufCodecs.BOOL, DebugListModifyPacket::allowDuplicates,
            DebugListModifyPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, String fieldName, String item, boolean add) {
        send(targetUuid, fieldName, item, add, false);
    }

    public static void send(String targetUuid, String fieldName, String item, boolean add, boolean allowDuplicates) {
        ClientPacketDistributor.sendToServer(new DebugListModifyPacket(targetUuid, fieldName, item, add, allowDuplicates));
    }

    public static void handle(final DebugListModifyPacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;

            try {
                DndModVariables.PlayerVariables vars = target.getData(DndModVariables.PLAYER_VARIABLES);
                Field field = vars.getClass().getField(message.fieldName());
                if (field.getType() != String.class) return;

                String current = (String) field.get(vars);
                List<String> entries = new ArrayList<>();
                if (current != null && !current.isBlank() && !current.equals("\"\"")) {
                    for (String s : current.split(",")) {
                        String t = s.trim();
                        if (!t.isEmpty()) entries.add(t);
                    }
                }

                if (message.add()) {
                    if (message.allowDuplicates() || !entries.contains(message.item())) entries.add(message.item());
                } else {
                    entries.remove(message.item());
                }

                field.set(vars, String.join(",", entries));
                vars.markSyncDirty();
                DebugNetUtils.sendSnapshot(requester, target);
            } catch (Exception e) {
                DebugNetUtils.fail(requester, "Failed to modify " + message.fieldName() + ": " + e.getMessage());
            }
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugListModifyPacket::handle);
    }
}