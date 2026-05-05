package net.luderspieler.dnd.spells.targeting;

import net.luderspieler.dnd.network.AirClickPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellCasters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber
public class TargetingEvents {

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
    public static void onFireballHit(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event) {
        if (event.getEntity() instanceof LargeFireball fireball) {
            CompoundTag nbt = fireball.getPersistentData();
            net.minecraft.world.level.Level world = fireball.level();

            // Wir prüfen auf "spell_max_range", da deine SpellCasters dies setzen
            // Falls du explizit "spell_name" nutzen willst, stelle sicher, dass du es in SpellCasters.java hinzufügst.
            if (nbt.contains("spell_max_range")) {
                // Den Namen ziehen wir aus der NBT-Logik (Standardwert "" falls nicht vorhanden)
                String spellName = nbt.getString("spell_name").orElse("");

                // Wichtig: Wir nutzen den Namen "FIREBALL", da du dies so vereinheitlichen wolltest.
                if ("FIREBALL".equals(spellName)) {
                    // 1. Standard-Explosion abbrechen
                    event.setCanceled(true);

                    Vec3 hitPos = event.getRayTraceResult().getLocation();
                    double radius = 6.0; // Der Radius für die Fireball-Explosion

                    // 2. Alle LivingEntities im Umkreis finden
                    List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class,
                            new AABB(hitPos.x - radius, hitPos.y - radius, hitPos.z - radius,
                                    hitPos.x + radius, hitPos.y + radius, hitPos.z + radius));

                    for (LivingEntity target : targets) {
                        // 3. Schadensberechnung
                        // wir nutzen die DamageSource des Projektils oder des Schützen.
                        Entity owner = fireball.getOwner();

                        // Schaden: 30 (entspricht ca. 8d6 im Durchschnitt, hier fixiert auf 15 Herzen)
                        target.hurt(world.damageSources().fireball(fireball, owner), 30.0F);

                        // Optional: Ziel in Brand setzen
                        target.setRemainingFireTicks(100);
                    }

                    // 4. Visuelle Effekte (Explosion ohne Blockschaden)
                    if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                                hitPos.x, hitPos.y, hitPos.z, 1, 0, 0, 0, 0);
                        world.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                                net.minecraft.sounds.SoundSource.PLAYERS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
                    }

                    // 5. Projektil entfernen
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
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleCasting(event.getEntity(), event.getHand());
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleCasting(event.getEntity(), event.getHand());
    }

    public static void handleCasting(net.minecraft.world.entity.player.Player p, InteractionHand hand) {
        if (p instanceof ServerPlayer player && hand == InteractionHand.MAIN_HAND) {
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            if (!vars.TargetingMode) return;

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
        }
    }

    private static void resetTargeting(DndModVariables.PlayerVariables vars) {
        vars.TargetingMode = false;
        vars.TargetingModeType = "";
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }
}