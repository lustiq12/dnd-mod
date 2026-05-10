package net.luderspieler.dnd.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.luderspieler.dnd.DndMod;

import java.util.function.Supplier;

@EventBusSubscriber
public class DndModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DndMod.MODID);
	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new PlayerVariables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		DndMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@SubscribeEvent
	public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerTickUpdateSyncPlayerVariables(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && player.getData(PLAYER_VARIABLES)._syncDirty) {
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
			player.getData(PLAYER_VARIABLES)._syncDirty = false;
		}
	}

	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event) {
		PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
		PlayerVariables clone = new PlayerVariables();
		clone.PlayerClass = original.PlayerClass;
		clone.PlayerLevel = original.PlayerLevel;
		clone.Spellslots = original.Spellslots;
		clone.PlayerSubrace = original.PlayerSubrace;
		clone.PlayerName = original.PlayerName;
		clone.PlayerStory = original.PlayerStory;
		clone.PlayerPersonality = original.PlayerPersonality;
		clone.PlayerRace = original.PlayerRace;
		clone.PlayerSubclass = original.PlayerSubclass;
		clone.PlayerXP = original.PlayerXP;
		clone.Proficiencys = original.Proficiencys;
		clone.CanUseMagic = original.CanUseMagic;
		clone.PreparedCantrips = original.PreparedCantrips;
		clone.PreparedSpellsLVL1 = original.PreparedSpellsLVL1;
		clone.PreparedSpellsLVL2 = original.PreparedSpellsLVL2;
		clone.PreparedSpellsLVL3 = original.PreparedSpellsLVL3;
		clone.PreparedSpellsLVL4 = original.PreparedSpellsLVL4;
		clone.PreparedSpellsLVL5 = original.PreparedSpellsLVL5;
		clone.PreparedSpellsLVL6 = original.PreparedSpellsLVL6;
		clone.PreparedSpellsLVL7 = original.PreparedSpellsLVL7;
		clone.PreparedSpellsLVL8 = original.PreparedSpellsLVL8;
		clone.PreparedSpellsLVL9 = original.PreparedSpellsLVL9;
		if (!event.isWasDeath()) {
			clone.FinishedCharacterCreation = original.FinishedCharacterCreation;
			clone.TargetingRange = original.TargetingRange;
			clone.TargetingMode = original.TargetingMode;
			clone.TargetingAmount = original.TargetingAmount;
			clone.TargetingSpell = original.TargetingSpell;
			clone.targetUUIDS = original.targetUUIDS;
			clone.TargetingModeType = original.TargetingModeType;
			clone.AbilityData = original.AbilityData;
			clone.Charmer = original.Charmer;
			clone.grabber = original.grabber;
			clone.Decaying_Focus = original.Decaying_Focus;
		}
		event.getEntity().setData(PLAYER_VARIABLES, clone);
	}

	public static class PlayerVariables implements ValueIOSerializable {
		boolean _syncDirty = false;
		public String PlayerClass = "\"\"";
		public double PlayerLevel = 0;
		public String Spellslots = "\"\"";
		public String PlayerSubrace = "\"\"";
		public String PlayerName = "\"\"";
		public String PlayerStory = "\"\"";
		public String PlayerPersonality = "\"\"";
		public boolean FinishedCharacterCreation = false;
		public String PlayerRace = "\"\"";
		public String PlayerSubclass = "\"\"";
		public double PlayerXP = 0;
		public String Proficiencys = "\"\"";
		public boolean CanUseMagic = false;
		public String PreparedCantrips = "\"\"";
		public String PreparedSpellsLVL1 = "\"\"";
		public String PreparedSpellsLVL2 = "\"\"";
		public String PreparedSpellsLVL3 = "\"\"";
		public String PreparedSpellsLVL4 = "\"\"";
		public String PreparedSpellsLVL5 = "\"\"";
		public String PreparedSpellsLVL6 = "\"\"";
		public String PreparedSpellsLVL7 = "\"\"";
		public String PreparedSpellsLVL8 = "\"\"";
		public String PreparedSpellsLVL9 = "\"\"";
		public double TargetingRange = 0;
		public boolean TargetingMode = false;
		public double TargetingAmount = 0;
		public String TargetingSpell = "\"\"";
		public String targetUUIDS = "\"\"";
		public String TargetingModeType = "\"\"";
		public String AbilityData = "\"\"";
		public String Charmer = "\"\"";
		public String grabber = "\"\"";
		public String Decaying_Focus = "\"\"";

		@Override
		public void serialize(ValueOutput output) {
			output.putString("PlayerClass", PlayerClass);
			output.putDouble("PlayerLevel", PlayerLevel);
			output.putString("Spellslots", Spellslots);
			output.putString("PlayerSubrace", PlayerSubrace);
			output.putString("PlayerName", PlayerName);
			output.putString("PlayerStory", PlayerStory);
			output.putString("PlayerPersonality", PlayerPersonality);
			output.putBoolean("FinishedCharacterCreation", FinishedCharacterCreation);
			output.putString("PlayerRace", PlayerRace);
			output.putString("PlayerSubclass", PlayerSubclass);
			output.putDouble("PlayerXP", PlayerXP);
			output.putString("Proficiencys", Proficiencys);
			output.putBoolean("CanUseMagic", CanUseMagic);
			output.putString("PreparedCantrips", PreparedCantrips);
			output.putString("PreparedSpellsLVL1", PreparedSpellsLVL1);
			output.putString("PreparedSpellsLVL2", PreparedSpellsLVL2);
			output.putString("PreparedSpellsLVL3", PreparedSpellsLVL3);
			output.putString("PreparedSpellsLVL4", PreparedSpellsLVL4);
			output.putString("PreparedSpellsLVL5", PreparedSpellsLVL5);
			output.putString("PreparedSpellsLVL6", PreparedSpellsLVL6);
			output.putString("PreparedSpellsLVL7", PreparedSpellsLVL7);
			output.putString("PreparedSpellsLVL8", PreparedSpellsLVL8);
			output.putString("PreparedSpellsLVL9", PreparedSpellsLVL9);
			output.putDouble("TargetingRange", TargetingRange);
			output.putBoolean("TargetingMode", TargetingMode);
			output.putDouble("TargetingAmount", TargetingAmount);
			output.putString("TargetingSpell", TargetingSpell);
			output.putString("targetUUIDS", targetUUIDS);
			output.putString("TargetingModeType", TargetingModeType);
			output.putString("AbilityData", AbilityData);
			output.putString("Charmer", Charmer);
			output.putString("grabber", grabber);
			output.putString("Decaying_Focus", Decaying_Focus);
		}

		@Override
		public void deserialize(ValueInput input) {
			PlayerClass = input.getStringOr("PlayerClass", "");
			PlayerLevel = input.getDoubleOr("PlayerLevel", 0);
			Spellslots = input.getStringOr("Spellslots", "");
			PlayerSubrace = input.getStringOr("PlayerSubrace", "");
			PlayerName = input.getStringOr("PlayerName", "");
			PlayerStory = input.getStringOr("PlayerStory", "");
			PlayerPersonality = input.getStringOr("PlayerPersonality", "");
			FinishedCharacterCreation = input.getBooleanOr("FinishedCharacterCreation", false);
			PlayerRace = input.getStringOr("PlayerRace", "");
			PlayerSubclass = input.getStringOr("PlayerSubclass", "");
			PlayerXP = input.getDoubleOr("PlayerXP", 0);
			Proficiencys = input.getStringOr("Proficiencys", "");
			CanUseMagic = input.getBooleanOr("CanUseMagic", false);
			PreparedCantrips = input.getStringOr("PreparedCantrips", "");
			PreparedSpellsLVL1 = input.getStringOr("PreparedSpellsLVL1", "");
			PreparedSpellsLVL2 = input.getStringOr("PreparedSpellsLVL2", "");
			PreparedSpellsLVL3 = input.getStringOr("PreparedSpellsLVL3", "");
			PreparedSpellsLVL4 = input.getStringOr("PreparedSpellsLVL4", "");
			PreparedSpellsLVL5 = input.getStringOr("PreparedSpellsLVL5", "");
			PreparedSpellsLVL6 = input.getStringOr("PreparedSpellsLVL6", "");
			PreparedSpellsLVL7 = input.getStringOr("PreparedSpellsLVL7", "");
			PreparedSpellsLVL8 = input.getStringOr("PreparedSpellsLVL8", "");
			PreparedSpellsLVL9 = input.getStringOr("PreparedSpellsLVL9", "");
			TargetingRange = input.getDoubleOr("TargetingRange", 0);
			TargetingMode = input.getBooleanOr("TargetingMode", false);
			TargetingAmount = input.getDoubleOr("TargetingAmount", 0);
			TargetingSpell = input.getStringOr("TargetingSpell", "");
			targetUUIDS = input.getStringOr("targetUUIDS", "");
			TargetingModeType = input.getStringOr("TargetingModeType", "");
			AbilityData = input.getStringOr("AbilityData", "");
			Charmer = input.getStringOr("Charmer", "");
			grabber = input.getStringOr("grabber", "");
			Decaying_Focus = input.getStringOr("Decaying_Focus", "");
		}

		public void markSyncDirty() {
			_syncDirty = true;
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> {
			TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
			message.data.serialize(output);
			buffer.writeNbt(output.buildResult());
		}, (RegistryFriendlyByteBuf buffer) -> {
			PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
			message.data.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), buffer.readNbt()));
			return message;
		});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, context.player().registryAccess());
					message.data.serialize(output);
					context.player().getData(PLAYER_VARIABLES).deserialize(TagValueInput.create(ProblemReporter.DISCARDING, context.player().registryAccess(), output.buildResult()));
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}