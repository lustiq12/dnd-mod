package net.luderspieler.dnd.spells.targeting;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.luderspieler.dnd.network.DndModVariables;
import java.util.*;
import java.util.function.Consumer;

public class SpellCasterHelper {

    public static void loadSpellForTargeting(ServerPlayer player, String spellName, double range) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingModeType = "FREE_AIM";
        vars.TargetingRange = range;
        vars.TargetingSpell = spellName; // WICHTIG
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }

    public static void startTargeting(ServerPlayer player, double range, int amount, String spellName) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingModeType = "ENTITY";
        vars.TargetingRange = range;
        vars.TargetingAmount = (double) amount;
        vars.TargetingSpell = spellName; // WICHTIG: Hier muss der Name rein!
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }

    // Diese Methode brauchen wir für TargetingEvents
    public static void forceCastExistingTargets(ServerPlayer caster, Consumer<List<LivingEntity>> onComplete) {
        var vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        List<String> uuidList = Arrays.stream(vars.targetUUIDS.split(","))
                .filter(s -> !s.isEmpty())
                .toList();

        List<LivingEntity> targets = new ArrayList<>();
        for (String s : uuidList) {
            try {
                var e = ((ServerLevel)caster.level()).getEntity(UUID.fromString(s));
                if (e instanceof LivingEntity living) targets.add(living);
            } catch (Exception ignored) {}
        }
        onComplete.accept(targets);
    }
    public static void renderPreview(ServerPlayer caster, double range) {
        var vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        Vec3 eyePos = caster.getEyePosition();
        Vec3 lookVec = caster.getViewVector(1.0F);

        // 1. Block-Raycast für Distanzbegrenzung
        HitResult blockHit = caster.pick(range, 0.0f, false);
        double maxDist = range;
        Vec3 blockPos = null;
        boolean hitBlock = false;

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            maxDist = eyePos.distanceTo(blockHit.getLocation());
            blockPos = blockHit.getLocation();
            hitBlock = true;
        }

        // 2. Entity-Raycast
        Vec3 reachVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                caster, eyePos, reachVec, searchBox, (e) -> e instanceof LivingEntity, maxDist * maxDist
        );

        Vec3 targetPos = null;

        // 3. Modus-Logik für Partikel
        if ("FREE_AIM".equals(vars.TargetingModeType)) {
            if (entityHit != null) {
                targetPos = entityHit.getLocation();
            } else if (hitBlock) {
                targetPos = blockPos;
            }
        } else {
            // Nur Partikel wenn Entity getroffen
            if (entityHit != null) {
                targetPos = entityHit.getLocation();
            }
        }

        if (targetPos != null) {
            drawVisualTrail(caster, targetPos);
        }
    }

    public static void tryPickTarget(ServerPlayer caster, double range, int maxAmount, Consumer<List<LivingEntity>> onComplete) {
        DndModVariables.PlayerVariables vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        LivingEntity target = getLookingAtEntity(caster, range);

        List<String> uuidList = new ArrayList<>(Arrays.asList(vars.targetUUIDS.split(","))
                .stream().filter(s -> !s.isEmpty()).toList());

        if (target != null) {
            String uuid = target.getStringUUID();
            if (!uuidList.contains(uuid) && uuidList.size() < maxAmount) {
                uuidList.add(uuid);
                vars.targetUUIDS = String.join(",", uuidList);
                vars.markSyncDirty();

                // JETZT GLOWEN: Erst beim Auswählen
                sendGlowPacket(caster, target, true);

                caster.displayClientMessage(Component.literal("§6Target chosen! (" + uuidList.size() + "/" + maxAmount + ")"), true);
            }
        }

        if (uuidList.size() >= maxAmount && maxAmount > 0) {
            executeFinalCast(caster, vars, uuidList, onComplete);
        }
    }

    private static void executeFinalCast(ServerPlayer caster, DndModVariables.PlayerVariables vars, List<String> uuidStrings, Consumer<List<LivingEntity>> onComplete) {
        List<LivingEntity> targets = new ArrayList<>();
        for (String s : uuidStrings) {
            try {
                Entity e = ((ServerLevel)caster.level()).getEntity(UUID.fromString(s));
                if (e instanceof LivingEntity living) {
                    targets.add(living);
                    sendGlowPacket(caster, living, false);
                }
            } catch (Exception ignored) {}
        }
        vars.targetUUIDS = "";
        vars.markSyncDirty();
        onComplete.accept(targets);
    }

    private static LivingEntity getLookingAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        HitResult blockHit = player.pick(range, 0.0f, false);
        double maxDist = (blockHit.getType() == HitResult.Type.BLOCK) ? eyePos.distanceTo(blockHit.getLocation()) : range;

        Vec3 reachVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
        var hit = ProjectileUtil.getEntityHitResult(player, eyePos, reachVec, searchBox, (e) -> e instanceof LivingEntity, maxDist * maxDist);
        return (hit != null && hit.getEntity() instanceof LivingEntity living) ? living : null;
    }

    public static void sendGlowPacket(ServerPlayer caster, Entity target, boolean active) {
        byte flags = target.getEntityData().get(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0));
        byte newFlags = active ? (byte)(flags | 0x40) : (byte)(flags & ~0x40);
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0), newFlags));
        caster.connection.send(new ClientboundSetEntityDataPacket(target.getId(), data));
    }

    private static void drawVisualTrail(ServerPlayer caster, Vec3 targetPos) {
        if (caster.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, targetPos.x, targetPos.y, targetPos.z, 50, 0.2, 0.2, 0.2, 0.05);
            level.sendParticles(ParticleTypes.ENCHANT, caster.getX(), caster.getY(), caster.getZ(), 5, 0.5, 0.3, 0.5, 0.05);
        }
    }
}