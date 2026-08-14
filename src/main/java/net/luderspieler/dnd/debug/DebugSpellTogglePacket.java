package net.luderspieler.dnd.debug;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Adds or removes a single prepared spell for a grade; force skips list/slot validation. */
@EventBusSubscriber
public record DebugSpellTogglePacket(String targetUuid, int grade, String spellId, boolean add, boolean force)
        implements CustomPacketPayload {

    public static final Type<DebugSpellTogglePacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:debug_spell_toggle"));

    public static final StreamCodec<FriendlyByteBuf, DebugSpellTogglePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DebugSpellTogglePacket::targetUuid,
            ByteBufCodecs.INT, DebugSpellTogglePacket::grade,
            ByteBufCodecs.STRING_UTF8, DebugSpellTogglePacket::spellId,
            ByteBufCodecs.BOOL, DebugSpellTogglePacket::add,
            ByteBufCodecs.BOOL, DebugSpellTogglePacket::force,
            DebugSpellTogglePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String targetUuid, int grade, String spellId, boolean add, boolean force) {
        ClientPacketDistributor.sendToServer(new DebugSpellTogglePacket(targetUuid, grade, spellId, add, force));
    }

    public static void handle(final DebugSpellTogglePacket message, final IPayloadContext context) {
        if (context.flow() != PacketFlow.SERVERBOUND) return;
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            ServerPlayer target = DebugNetUtils.resolveTarget(requester, message.targetUuid());
            if (target == null) return;
            if (message.grade() < 0 || message.grade() > 9) return;

            DndModVariables.PlayerVariables vars = target.getData(DndModVariables.PLAYER_VARIABLES);
            ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
            if (cls == null) {
                DebugNetUtils.fail(requester, "Target has no valid class.");
                return;
            }

            String spellId = message.spellId().toUpperCase().trim();
            String current = getListByGrade(vars, message.grade());
            boolean known = Arrays.asList(current.split(",")).contains(spellId);

            if (message.add()) {
                if (known) return;
                if (!message.force()) {
                    boolean inList = cls.getSpellList().stream().anyMatch(e -> e.name().equalsIgnoreCase(spellId));
                    if (!inList) { DebugNetUtils.fail(requester, spellId + " is not on this class's spell list."); return; }
                    if (!cls.canPrepareMore(current, (int) vars.PlayerLevel, message.grade())) {
                        DebugNetUtils.fail(requester, "Preparation limit reached for grade " + message.grade());
                        return;
                    }
                }
                String updated = (current.isEmpty() || current.equals("\"\"")) ? spellId : current + "," + spellId;
                setListByGrade(vars, message.grade(), updated);
            } else {
                if (!known) return;
                String updated = Arrays.stream(current.split(","))
                        .filter(s -> !s.equals(spellId))
                        .collect(Collectors.joining(","));
                setListByGrade(vars, message.grade(), updated);
            }

            vars.markSyncDirty();
            DebugNetUtils.sendSnapshot(requester, target);
        });
    }

    private static String getListByGrade(DndModVariables.PlayerVariables v, int g) {
        return switch (g) {
            case 0 -> v.PreparedCantrips;
            case 1 -> v.PreparedSpellsLVL1;
            case 2 -> v.PreparedSpellsLVL2;
            case 3 -> v.PreparedSpellsLVL3;
            case 4 -> v.PreparedSpellsLVL4;
            case 5 -> v.PreparedSpellsLVL5;
            case 6 -> v.PreparedSpellsLVL6;
            case 7 -> v.PreparedSpellsLVL7;
            case 8 -> v.PreparedSpellsLVL8;
            case 9 -> v.PreparedSpellsLVL9;
            default -> "";
        };
    }

    private static void setListByGrade(DndModVariables.PlayerVariables v, int g, String s) {
        switch (g) {
            case 0 -> v.PreparedCantrips = s;
            case 1 -> v.PreparedSpellsLVL1 = s;
            case 2 -> v.PreparedSpellsLVL2 = s;
            case 3 -> v.PreparedSpellsLVL3 = s;
            case 4 -> v.PreparedSpellsLVL4 = s;
            case 5 -> v.PreparedSpellsLVL5 = s;
            case 6 -> v.PreparedSpellsLVL6 = s;
            case 7 -> v.PreparedSpellsLVL7 = s;
            case 8 -> v.PreparedSpellsLVL8 = s;
            case 9 -> v.PreparedSpellsLVL9 = s;
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        DndMod.addNetworkMessage(TYPE, CODEC, DebugSpellTogglePacket::handle);
    }
}