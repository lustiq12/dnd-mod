package net.luderspieler.dnd.network;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.character.screens.CharacterSheetScreen;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber
public record CharacterSheetMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<CharacterSheetMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "key_character_sheet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CharacterSheetMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, CharacterSheetMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new CharacterSheetMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<CharacterSheetMessage> type() {
		return TYPE;
	}

	public static void handleData(final CharacterSheetMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player(), message.eventType, message.pressedms);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		// security measure to prevent arbitrary chunk generation
		if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)))
			return;
		if (type == 0) {

			net.minecraft.client.Minecraft.getInstance().setScreen(new CharacterSheetScreen());
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(CharacterSheetMessage.TYPE, CharacterSheetMessage.STREAM_CODEC, CharacterSheetMessage::handleData);
	}
}