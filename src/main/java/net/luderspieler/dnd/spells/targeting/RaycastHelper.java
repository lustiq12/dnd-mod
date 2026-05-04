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

public class RaycastHelper {

    public static void startTargeting(ServerPlayer player, double range, int amount, String spellName) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingRange = range;
        vars.TargetingAmount = (double) amount;
        vars.TargetingSpell = spellName;
        vars.targetUUIDS = ""; // Liste leeren beim Start
        vars.markSyncDirty(); //
        player.displayClientMessage(Component.literal("§d[System] Targeting mode active!"), true);
    }

    public static void renderPreview(ServerPlayer caster, double range) {
        HitResult hit = caster.pick(range, 0.0f, false);
        drawVisualTrail(caster, hit.getLocation());

        LivingEntity hovered = getLookingAtEntity(caster, range);
        if (hovered != null) {
            var vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
            String uuid = hovered.getStringUUID();

            // Nur wenn das Ziel NOCH NICHT ausgewählt wurde, nutzen wir den Timer
            if (!vars.targetUUIDS.contains(uuid)) {
                sendGlowPacket(caster, hovered, true);
                var server = caster.getServer();
                if (server != null) {
                    int targetTick = server.getTickCount() + 2;
                    server.execute(new net.minecraft.server.TickTask(targetTick, () -> {
                        // Nur ausschalten, wenn es zwischenzeitlich nicht angeklickt wurde
                        if (hovered.isAlive() && !caster.getData(DndModVariables.PLAYER_VARIABLES).targetUUIDS.contains(uuid)) {
                            sendGlowPacket(caster, hovered, false);
                        }
                    }));
                }
            } else {
                // Falls es schon ausgewählt ist, einfach nur leuchten lassen (Sicherheit)
                sendGlowPacket(caster, hovered, true);
            }
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

                // WICHTIG: Hier kein Timer! Das Ziel soll dauerhaft leuchten.
                sendGlowPacket(caster, target, true);

                caster.displayClientMessage(Component.literal("§6Target chosen! (" + uuidList.size() + "/" + maxAmount + ")"), true);
            }
        } else if (!uuidList.isEmpty()) {
            executeFinalCast(caster, vars, uuidList, onComplete);
        }

        if (uuidList.size() >= maxAmount && maxAmount > 0) {
            executeFinalCast(caster, vars, uuidList, onComplete);
        }
    }

    private static void executeFinalCast(ServerPlayer caster, DndModVariables.PlayerVariables vars, List<String> uuidStrings, Consumer<List<LivingEntity>> onComplete) {
        List<LivingEntity> targets = new ArrayList<>();
        List<String> names = new ArrayList<>(); // Liste für die Namen

        for (String s : uuidStrings) {
            try {
                Entity e = ((ServerLevel)caster.level()).getEntity(UUID.fromString(s));
                if (e instanceof LivingEntity living) {
                    targets.add(living);
                    names.add(living.getName().getString()); // Namen speichern
                    sendGlowPacket(caster, living, false); // Glowing am Ende entfernen
                }
            } catch (Exception ignored) {}
        }

        // Nachricht mit allen Namen ausgeben
        if (!names.isEmpty()) {
            String allNames = String.join(", ", names);
            caster.displayClientMessage(Component.literal("§aCast spell on: §f" + allNames), false);
        }

        vars.targetUUIDS = "";
        vars.markSyncDirty();
        onComplete.accept(targets);
    }

    // Hilfsmethoden
    private static LivingEntity getLookingAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
        var hit = ProjectileUtil.getEntityHitResult(player, eyePos, reachVec, searchBox, (e) -> e instanceof LivingEntity, range * range);
        return (hit != null && hit.getEntity() instanceof LivingEntity living) ? living : null;
    }

    private static void sendGlowPacket(ServerPlayer caster, net.minecraft.world.entity.Entity target, boolean active) {
        byte flags = target.getEntityData().get(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0));
        byte newFlags = active ? (byte)(flags | 0x40) : (byte)(flags & ~0x40);
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0), newFlags));
        caster.connection.send(new ClientboundSetEntityDataPacket(target.getId(), data));
    }

    private static void drawVisualTrail(ServerPlayer caster, Vec3 targetPos) {
        if (caster.level() instanceof ServerLevel level) {
            Vec3 start = caster.getEyePosition();
            double dist = start.distanceTo(targetPos);
            Vec3 dir = targetPos.subtract(start).normalize();
            for (double d = 0; d < dist; d += 0.5) {
                Vec3 p = start.add(dir.scale(d));
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        }
    }
}