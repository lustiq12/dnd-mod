package net.luderspieler.dnd.character.network;

import net.luderspieler.dnd.character.AbilitysAndFeats.AbilityMethods_OneTime;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityResetRegistry;
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

            // 1. Alle Stat-basierten Attribute neu berechnen (HP, Speed, etc.)
            CharacterCreationPacket.applyAttrs(player);

            // 2. Entity-Level Ability-Effekte reaktivieren
            //    (Night Vision für Darkvision, Unarmored Defense AC, Speed-Mods)
            AbilityMethods_OneTime.reapplyEntityEffects(player);

            // 3. Tod = Long Rest: Ladungen auf Maximum setzen + Flags bereinigen
            //
            //    BUGFIX: Frühere Version löschte alle _uses-Einträge ohne sie neu zu
            //    initialisieren → Spieler hatte nach Respawn keinerlei Ability-Ladungen
            //    bis zum nächsten Schlaf. Jetzt korrekt über resetOnLongRest():
            //      - Alle Long/Short-Rest-Abilities auf Max-Ladungen setzen
            //      - _active / _readied Flags bereinigen
            //      - ToughBonus und andere permanente Werte bleiben erhalten
            AbilityResetRegistry.resetOnLongRest(player);
        });
    }
}