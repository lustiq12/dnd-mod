package net.luderspieler.dnd.network;

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
			if (entity instanceof Player player) {
				DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
				if (vars.CanUseMagic) {
					var mc = net.minecraft.client.Minecraft.getInstance();
					mc.setScreen(new SpellWheelScreen());
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(CastSpellMessage.TYPE, CastSpellMessage.STREAM_CODEC, CastSpellMessage::handleData);
	}
}