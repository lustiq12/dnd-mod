package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Carries a full attribute modifier breakdown for a debug target back to the requesting client. */
@EventBusSubscriber
public record DebugAttributesResponsePacket(String targetUuid, List<DebugAttributesClientState.AttributeEntry> attributes)
        implements CustomPacketPayload {

    public static final Type<DebugAttributesResponsePacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_attributes_response"));

    public static final StreamCodec<FriendlyByteBuf, DebugAttributesResponsePacket> CODEC = StreamCodec.of(
            (FriendlyByteBuf buffer, DebugAttributesResponsePacket message) -> {
                buffer.writeUtf(message.targetUuid());
                buffer.writeVarInt(message.attributes().size());
                for (DebugAttributesClientState.AttributeEntry entry : message.attributes()) {
                    buffer.writeUtf(entry.attributeId());
                    buffer.writeDouble(entry.base());
                    buffer.writeDouble(entry.total());
                    buffer.writeVarInt(entry.modifiers().size());
                    for (DebugAttributesClientState.ModifierEntry mod : entry.modifiers()) {
                        buffer.writeUtf(mod.id());
                        buffer.writeDouble(mod.amount());
                        buffer.writeUtf(mod.operation());
                    }
                }
            },
            (FriendlyByteBuf buffer) -> {
                String uuid = buffer.readUtf();
                int attrCount = buffer.readVarInt();
                List<DebugAttributesClientState.AttributeEntry> attributes = new ArrayList<>(attrCount);
                for (int i = 0; i < attrCount; i++) {
                    String attributeId = buffer.readUtf();
                    double base = buffer.readDouble();
                    double total = buffer.readDouble();
                    int modCount = buffer.readVarInt();
                    List<DebugAttributesClientState.ModifierEntry> modifiers = new ArrayList<>(modCount);
                    for (int j = 0; j < modCount; j++) {
                        modifiers.add(new DebugAttributesClientState.ModifierEntry(
                                buffer.readUtf(), buffer.readDouble(), buffer.readUtf()));
                    }
                    attributes.add(new DebugAttributesClientState.AttributeEntry(attributeId, base, total, modifiers));
                }
                return new DebugAttributesResponsePacket(uuid, attributes);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final DebugAttributesResponsePacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.CLIENTBOUND) return;
        context.enqueueWork(() -> {
            try {
                DebugAttributesClientState.set(new DebugAttributesClientState.Snapshot(
                        UUID.fromString(message.targetUuid()), message.attributes()));
            } catch (IllegalArgumentException ignored) {}
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugAttributesResponsePacket::handle);
    }
}