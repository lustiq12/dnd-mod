package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server.
 * Sent when the player clicks "Begin Rest" on LongRestPreviewScreen.
 * Server rolls for encounter (wilderness) then puts the player to sleep.
 * Also stores the bed position in AbilityData so onWakeUp() can read it.
 */
public record BeginRestPacket(BlockPos bedPos) implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("dnd", "begin_rest");
    public static final Type<BeginRestPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, BeginRestPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeBlockPos(pkt.bedPos()),
                    buf        -> new BeginRestPacket(buf.readBlockPos())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // Client → Server: use ClientPacketDistributor in NeoForge 1.21.x.
    public static void send(BlockPos bedPos) {
        ClientPacketDistributor.sendToServer(new BeginRestPacket(bedPos));
    }

    public static void handle(BeginRestPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            RestEnvironmentScanner.ScanResult scan =
                    RestEnvironmentScanner.scan(player.level(), pkt.bedPos());

            // Wilderness encounter roll.
            if (scan.isWilderness() && RestEncounterSystem.rollEncounter(player)) {
                RestEncounterSystem.spawnWave(player);
                return; // abort — no sleep
            }

            // Store bed position in AbilityData so onWakeUp can retrieve it.
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            AbilityDataUtils.set(vars, "LONG_REST_BED",
                    pkt.bedPos().getX() + ";" + pkt.bedPos().getY() + ";" + pkt.bedPos().getZ());
            vars.markSyncDirty();

            // startSleepInBed() returns Either<BedSleepingProblem, Unit>.
            // left() = problem present (failed), right() = Unit (success).
            player.startSleepInBed(pkt.bedPos())
                    .left()
                    .ifPresent(problem ->
                            player.displayClientMessage(
                                    net.minecraft.network.chat.Component.literal(
                                            "§c⚠ Could not sleep (" + problem.name().replace('_', ' ').toLowerCase() + ")"), false));
        });
    }
}