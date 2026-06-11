package net.luderspieler.dnd.resources;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Gesendet vom Client wenn der Spieler eine Sub-Aktion aus einem Resource-Screen wählt.
 * Beispiel: FocusPointSpendScreen → "FOCUS_POINTS" + "FLURRY_OF_BLOWS"
 *
 * Registrierung in DndModNetworkRegistry:
 *   reg.playToServer(UseResourceActionPacket.TYPE,
 *                    UseResourceActionPacket.CODEC,
 *                    UseResourceActionPacket::handle);
 */
public record UseResourceActionPacket(String pool, String action) implements CustomPacketPayload {

    public static final Type<UseResourceActionPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:use_resource_action"));

    public static final StreamCodec<FriendlyByteBuf, UseResourceActionPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, UseResourceActionPacket::pool,
                    ByteBufCodecs.STRING_UTF8, UseResourceActionPacket::action,
                    UseResourceActionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(ResourceManager.ResourcePool pool, String action) {
        ClientPacketDistributor.sendToServer(new UseResourceActionPacket(pool.name(), action));
    }

    public static void handle(UseResourceActionPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            ResourceManager.ResourcePool pool;
            try {
                pool = ResourceManager.ResourcePool.valueOf(pkt.pool());
            } catch (IllegalArgumentException e) {
                return;
            }

            // Kosten prüfen bevor Aktion ausgeführt wird
            int cost = getCost(pool, pkt.action());
            if (cost < 0) {
                // Unbekannte Aktion
                player.displayClientMessage(
                        Component.literal("§cUnknown action: " + pkt.action()), true);
                return;
            }
            if (!ResourceManager.spend(player, pool, cost)) {
                player.displayClientMessage(
                        Component.literal("§cNot enough " + pool.displayName + "!"), true);
                return;
            }

            // Aktion ausführen
            executeAction(player, pool, pkt.action());
        });
    }

    // ── KOSTEN PRO AKTION ─────────────────────────────────────────────

    private static int getCost(ResourceManager.ResourcePool pool, String action) {
        return switch (pool) {
            case FOCUS_POINTS -> switch (action) {
                case "FLURRY_OF_BLOWS"  -> 1;
                case "PATIENT_DEFENSE"  -> 1;
                case "STEP_OF_THE_WIND" -> 1;
                default -> -1;
            };
            case SORCERY_POINTS -> switch (action) {
                case "SLOT_1" -> 2;
                case "SLOT_2" -> 3;
                case "SLOT_3" -> 5;
                case "SLOT_4" -> 6;
                case "SLOT_5" -> 7;
                case "CONVERT_SLOT_1" -> -1; // Gibt SP, kostet Slot → separates System
                default -> -1;
            };
            default -> -1;
        };
    }

    // ── AKTIONS-IMPLEMENTIERUNGEN ─────────────────────────────────────

    private static void executeAction(ServerPlayer player,
                                      ResourceManager.ResourcePool pool,
                                      String action) {
        switch (pool) {
            case FOCUS_POINTS -> executeFocusPointAction(player, action);
            case SORCERY_POINTS -> executeSorceryPointAction(player, action);
            default -> {}
        }
    }

    // ── FOCUS POINTS ──────────────────────────────────────────────────

    private static void executeFocusPointAction(ServerPlayer player, String action) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        switch (action) {
            case "FLURRY_OF_BLOWS" -> {
                // Minecraft-Annäherung: Kurzer Angriffsgeschwindigkeits-Boost (2 Extra-Hits)
                // Echte Implementierung: Nächste 2 Unarmed Strikes auf nächsten Mob anwenden
                player.addEffect(new MobEffectInstance(
                        MobEffects.HASTE, 10, 2, false, false, false));
                AbilityDataUtils.set(vars, "FLURRY_remaining", 2);
                vars.markSyncDirty();
                player.displayClientMessage(
                        Component.literal("§9Flurry of Blows ready!"), true);
                // TODO: Flurry-Treffer in Attack-Event auslösen wenn FLURRY_remaining > 0
            }
            case "PATIENT_DEFENSE" -> {
                // Dodge-Aktion: Resistance + Speed für 1 Sekunde
                player.addEffect(new MobEffectInstance(
                        MobEffects.RESISTANCE, 20, 0, false, false, false));
                player.addEffect(new MobEffectInstance(
                        MobEffects.SPEED, 20, 1, false, false, false));
                AbilityDataUtils.set(vars, "PATIENT_DEFENSE_active", true);
                vars.markSyncDirty();
                net.luderspieler.dnd.DndMod.queueServerWork(20, () -> {
                    AbilityDataUtils.set(vars, "PATIENT_DEFENSE_active", false);
                    vars.markSyncDirty();
                });
                player.displayClientMessage(
                        Component.literal("§9Patient Defense!"), true);
            }
            case "STEP_OF_THE_WIND" -> {
                // Disengage/Dash: Speed-Boost + erhöhte Sprungkraft für 1 Sekunde
                player.addEffect(new MobEffectInstance(
                        MobEffects.SPEED, 40, 1, false, false, false));
                player.addEffect(new MobEffectInstance(
                        MobEffects.JUMP_BOOST, 40, 1, false, false, false));
                player.displayClientMessage(
                        Component.literal("§9Step of the Wind!"), true);
                // TODO: Disengage (keine Opportunity Attacks) wenn OA-System implementiert
            }
        }
    }

    // ── SORCERY POINTS ────────────────────────────────────────────────

    private static void executeSorceryPointAction(ServerPlayer player, String action) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        // Spell Slot aus Sorcery Points erstellen
        int grade = switch (action) {
            case "SLOT_1" -> 1;
            case "SLOT_2" -> 2;
            case "SLOT_3" -> 3;
            case "SLOT_4" -> 4;
            case "SLOT_5" -> 5;
            default -> 0;
        };
        if (grade == 0) return;
        // Slot zur Spellslots-Zeichenkette hinzufügen
        String slots = vars.Spellslots != null
                ? vars.Spellslots.replace("\"", "") : "000000000";
        if (slots.length() < 9) slots = "000000000";
        char[] arr = slots.toCharArray();
        int idx = grade - 1;
        int current = arr[idx] - '0';
        arr[idx] = (char) ('0' + Math.min(9, current + 1));
        vars.Spellslots = new String(arr);
        vars.markSyncDirty();
        player.displayClientMessage(
                Component.literal("§5Created Grade " + grade + " spell slot!"), true);
    }
}