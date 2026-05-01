package net.luderspieler.dnd.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.luderspieler.dnd.DndMod;

@EventBusSubscriber
public record CastSpellMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<CastSpellMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "key_cast_spell"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CastSpellMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, CastSpellMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new CastSpellMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<CastSpellMessage> type() {
		return TYPE;
	}

	public static void handleData(final CastSpellMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(CastSpellMessage.TYPE, CastSpellMessage.STREAM_CODEC, CastSpellMessage::handleData);
	}
}