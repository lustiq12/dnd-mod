package net.luderspieler.dnd.spells.targeting;

import net.luderspieler.dnd.network.AirClickPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellCasters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EventBusSubscriber
public class TargetingEvents {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity() instanceof ServerPlayer player && player.isShiftKeyDown()) {
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            resetTargeting(vars);
            player.displayClientMessage(Component.literal("LeftClickEmptyReset"), false);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && player.isShiftKeyDown()) {
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            resetTargeting(vars);
            player.displayClientMessage(Component.literal("LeftClickBlockReset"), false);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LargeFireball fireball) {
            CompoundTag nbt = fireball.getPersistentData();
            if (nbt.contains("spell_max_range")) {
                // Optionals müssen mit .orElse() entpackt werden
                double maxRange = nbt.getDouble("spell_max_range").orElse(0.0);

                double startX = nbt.getDouble("spell_start_x").orElse(0.0);
                double startY = nbt.getDouble("spell_start_y").orElse(0.0);
                double startZ = nbt.getDouble("spell_start_z").orElse(0.0);

                Vec3 startPos = new Vec3(startX, startY, startZ);

                if (fireball.position().distanceTo(startPos) >= maxRange) {
                    fireball.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            if (vars.TargetingMode) {
                SpellCasterHelper.renderPreview(player, vars.TargetingRange);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        var vars = event.getEntity().getData(DndModVariables.PLAYER_VARIABLES);
        if (vars.TargetingMode) {
            ClientPacketDistributor.sendToServer(new AirClickPacket());
            event.getEntity().displayClientMessage(Component.literal("RightClickEmpty"), false);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleCasting(event.getEntity(), event.getHand());
        event.getEntity().displayClientMessage(Component.literal("RightClickBlock"), false);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleCasting(event.getEntity(), event.getHand());
    }

    public static void handleCasting(net.minecraft.world.entity.player.Player p, InteractionHand hand) {
        if (p instanceof ServerPlayer player && hand == InteractionHand.MAIN_HAND) {
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            if (!vars.TargetingMode) return;

            // Sneak + Right Click: Spieler selbst als Target hinzufügen
            if (player.isShiftKeyDown()) {
                String uuid = player.getStringUUID();
                List<String> uuidList = new ArrayList<>(Arrays.asList(vars.targetUUIDS.split(","))
                        .stream().filter(s -> !s.isEmpty()).toList());

                if (!uuidList.contains(uuid) && uuidList.size() < vars.TargetingAmount) {
                    uuidList.add(uuid);
                    vars.targetUUIDS = String.join(",", uuidList);
                    vars.markSyncDirty();
                    SpellCasterHelper.sendGlowPacket(player, player, true);
                    player.displayClientMessage(Component.literal("§6Target chosen! (" + uuidList.size() + "/" + (int)vars.TargetingAmount + ")"), true);
                }

                if (uuidList.size() >= vars.TargetingAmount && vars.TargetingAmount > 0) {
                    confirmAndCast(player, vars);
                }
                return;
            }

            HitResult precisionHit = player.pick(vars.TargetingRange, 0.0f, false);
            boolean lookingAtAnything = (precisionHit.getType() != HitResult.Type.MISS);

            if ("FREE_AIM".equals(vars.TargetingModeType)) {
                castSelectedSpell(player, vars, null);
                resetTargeting(vars);
            } else {
                // Entity-Mode
                SpellCasterHelper.tryPickTarget(player, vars.TargetingRange, (int) vars.TargetingAmount, (targets) -> {
                    // Falls wir Ziele zurückbekommen (Limit erreicht oder Ziel ausgewählt)
                    if (!targets.isEmpty()) {
                        for (LivingEntity target : targets) {
                            castSelectedSpell(player, vars, target);
                        }
                        resetTargeting(vars);
                    } else if (lookingAtAnything) {
                        // Nichts Neues getroffen, aber auf Boden geklickt -> Vorzeitig beenden
                        confirmAndCast(player, vars);
                    }
                });
            }
        }
    }

    private static void confirmAndCast(ServerPlayer player, DndModVariables.PlayerVariables vars) {
        // Wir holen die Ziele, die wir bisher gesammelt haben
        SpellCasterHelper.forceCastExistingTargets(player, (targets) -> {
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    castSelectedSpell(player, vars, target);
                }
            }
            resetTargeting(vars);
        });
    }

    private static void castSelectedSpell(ServerPlayer player, DndModVariables.PlayerVariables vars, LivingEntity target) {
        switch (vars.TargetingSpell) {
            case "FIREBALL" -> SpellCasters.castFireball(player, vars.TargetingRange);
            case "FIRE_BOLT" -> SpellCasters.castFireBolt(player, vars.TargetingRange);
            case "CURE_WOUNDS" -> SpellCasters.castCureWounds(player, target);
            case "HEALING_WORD" -> SpellCasters.castHealingWord(player, target);
            case "RESTORATION" -> SpellCasters.castRestoration(player, target);
            case "AID" -> SpellCasters.castAid(player, target);
            case "INFLICT_WOUNDS" -> SpellCasters.castInflictWounds(player, target);
            case "BLIGHT" -> SpellCasters.castBlight(player, target);
            case "HOLD_PERSON" -> SpellCasters.castHoldPerson(player, target);
            case "BESTOW_CURSE" -> SpellCasters.castBestowCurse(player, target);
            case "FEATHER_FALL" -> SpellCasters.castFeatherFall(player, target);
            case "MIND_SPIKE" -> SpellCasters.castMindSpike(player, target);
            case "CHILL_TOUCH" -> SpellCasters.castChillTouch(player, target);
            case "WATER_BREATHING" -> SpellCasters.castWaterBreathing(player, target);
            case "GREATER_INVISIBILITY" -> SpellCasters.castGreaterInvisibility(player, target);
        }
    }

    private static void resetTargeting(DndModVariables.PlayerVariables vars) {
        vars.TargetingMode = false;
        vars.TargetingModeType = "";
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }
}