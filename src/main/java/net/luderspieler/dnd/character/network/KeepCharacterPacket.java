package net.luderspieler.dnd.character.network;

import net.luderspieler.dnd.character.AbilitysAndFeats.AbilityMethods_OneTime;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KeepCharacterPacket() implements CustomPacketPayload {

    public static final Type<KeepCharacterPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:keep_character"));

    public static final StreamCodec<FriendlyByteBuf, KeepCharacterPacket> CODEC =
            StreamCodec.unit(new KeepCharacterPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send() {
        ClientPacketDistributor.sendToServer(new KeepCharacterPacket());
    }

    public static void handle(KeepCharacterPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();

            // Recalculate all stat-based attributes (HP, speed, etc.)
            CharacterCreationPacket.applyAttrs(player);

            // Re-apply entity-level ability effects lost on respawn
            // (Night Vision, Unarmored Defense armor mod, conditional speed mods)
            AbilityMethods_OneTime.reapplyEntityEffects(player); // ← NEU

            // Reset long-rest ability uses that were active before death
            resetAbilityUsesOnRespawn(player);                   // ← NEU
        });
    }

    private static void resetAbilityUsesOnRespawn(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Clear all "_uses" and "_active" entries in AbilityData
        // so the player starts with full uses after "sleeping" (=dying counts as long rest here).
        var map = AbilityDataUtils.parse(vars.AbilityData);
        map.entrySet().removeIf(e -> {
            String key = e.getKey();
            return key.endsWith("_uses") || key.endsWith("_active") || key.endsWith("_readied");
        });
        // Preserve permanent values like ToughBonus
        vars.AbilityData = "{" + map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(java.util.stream.Collectors.joining(",")) + "}";
        if (vars.AbilityData.equals("{}")) vars.AbilityData = "";

        vars.markSyncDirty();
    }
}