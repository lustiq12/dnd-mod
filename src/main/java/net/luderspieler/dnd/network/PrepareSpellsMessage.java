package net.luderspieler.dnd.network;

import net.luderspieler.dnd.spells.SpellPrepScreen;
import net.luderspieler.dnd.spells.SpellWheelScreen;
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
public record PrepareSpellsMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<PrepareSpellsMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "key_prepare_spells"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PrepareSpellsMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PrepareSpellsMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new PrepareSpellsMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<PrepareSpellsMessage> type() {
		return TYPE;
	}

	public static void handleData(final PrepareSpellsMessage message, final IPayloadContext context) {
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
			var mc = net.minecraft.client.Minecraft.getInstance();
			mc.setScreen(new SpellPrepScreen(null));

		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(PrepareSpellsMessage.TYPE, PrepareSpellsMessage.STREAM_CODEC, PrepareSpellsMessage::handleData);
	}
}