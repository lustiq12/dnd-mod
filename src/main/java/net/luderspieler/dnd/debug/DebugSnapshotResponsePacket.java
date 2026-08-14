package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/** Carries a full PlayerVariables snapshot of a debug target back to the requesting client. */
@EventBusSubscriber
public record DebugSnapshotResponsePacket(String targetUuid, String targetName, DndModVariables.PlayerVariables data)
        implements CustomPacketPayload {

    public static final Type<DebugSnapshotResponsePacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_snapshot_response"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DebugSnapshotResponsePacket> CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buffer, DebugSnapshotResponsePacket message) -> {
                buffer.writeUtf(message.targetUuid());
                buffer.writeUtf(message.targetName());
                TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
                message.data().serialize(output);
                buffer.writeNbt(output.buildResult());
            },
            (RegistryFriendlyByteBuf buffer) -> {
                String uuid = buffer.readUtf();
                String name = buffer.readUtf();
                DndModVariables.PlayerVariables vars = new DndModVariables.PlayerVariables();
                vars.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), buffer.readNbt()));
                return new DebugSnapshotResponsePacket(uuid, name, vars);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final DebugSnapshotResponsePacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.CLIENTBOUND) return;
        context.enqueueWork(() -> {
            try {
                DebugClientState.set(new DebugClientState.Snapshot(
                        UUID.fromString(message.targetUuid()), message.targetName(), message.data()));
            } catch (IllegalArgumentException ignored) {}
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugSnapshotResponsePacket::handle);
    }
}