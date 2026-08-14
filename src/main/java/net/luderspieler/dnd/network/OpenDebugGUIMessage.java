package net.luderspieler.dnd.network;

import net.luderspieler.dnd.character.screens.CharacterSheetScreen;
import net.luderspieler.dnd.debug.DebugMainScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.SectionPos;

import net.luderspieler.dnd.procedures.StirgeIdleCooldownProcedure;
import net.luderspieler.dnd.DndMod;

@EventBusSubscriber
public record OpenDebugGUIMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<OpenDebugGUIMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "key_open_debug_gui"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenDebugGUIMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, OpenDebugGUIMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new OpenDebugGUIMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<OpenDebugGUIMessage> type() {
		return TYPE;
	}

	public static void handleData(final OpenDebugGUIMessage message, final IPayloadContext context) {
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
		if (!(entity.isCreative()))
			return;

		if (type == 0) {

			net.minecraft.client.Minecraft.getInstance().setScreen(new DebugMainScreen());
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(OpenDebugGUIMessage.TYPE, OpenDebugGUIMessage.STREAM_CODEC, OpenDebugGUIMessage::handleData);
	}
}