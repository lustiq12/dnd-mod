package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.CastSpellProcedure;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public record CastSpellPacket(String spellId, int level) implements CustomPacketPayload {

    public static final Type<CastSpellPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:cast_spell"));

    public static final StreamCodec<FriendlyByteBuf, CastSpellPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, CastSpellPacket::spellId,
                    ByteBufCodecs.INT,         CastSpellPacket::level,
                    CastSpellPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // In CastSpellPacket.java
    public static void send(String spell, int grade) {
        // Hier fehlte das zweite Argument (int) im Konstruktor
        ClientPacketDistributor.sendToServer(new CastSpellPacket(spell, grade));
    }

    /** Server-side handler */
    public static void handle(CastSpellPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            int level = pkt.level();

            // Validate: spell is prepared
            String prepared = level == 0 ? vars.PreparedCantrips : levelVar(vars, level);
            if (prepared == null || !prepared.contains(pkt.spellId())) return;

            // Consume slot (cantrips are free)
            if (level > 0) {
                String slots = vars.Spellslots;
                if (slots == null || slots.length() < level) return;
                int slotCount = slots.charAt(level - 1) - '0';
                if (slotCount <= 0) return;
                char[] arr = slots.toCharArray();
                arr[level - 1] = (char)('0' + (slotCount - 1));
                vars.Spellslots = new String(arr);
                vars.markSyncDirty();
            }

            CastSpellProcedure.execute(player, pkt.spellId(), level);
        });
    }

    private static String levelVar(DndModVariables.PlayerVariables v, int level) {
        return switch (level) {
            case 1 -> v.PreparedSpellsLVL1; case 2 -> v.PreparedSpellsLVL2;
            case 3 -> v.PreparedSpellsLVL3; case 4 -> v.PreparedSpellsLVL4;
            case 5 -> v.PreparedSpellsLVL5; case 6 -> v.PreparedSpellsLVL6;
            case 7 -> v.PreparedSpellsLVL7; case 8 -> v.PreparedSpellsLVL8;
            case 9 -> v.PreparedSpellsLVL9; default -> "";
        };
    }
}