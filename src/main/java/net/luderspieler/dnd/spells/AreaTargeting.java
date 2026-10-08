package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.DndMod;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Entity queries and particle helpers for area shapes that start at the caster
 * and point along the view direction. Distances are in blocks (1 block = 3 ft).
 * The caster is never part of the result.
 */
public final class AreaTargeting {

    private AreaTargeting() {}

    /**
     * Living entities inside a cone whose apex is the caster's eye.
     *
     * @param length     cone length in blocks
     * @param fullAngle  opening angle in degrees (15 ft cone = about 53)
     */
    public static List<LivingEntity> entitiesInCone(ServerPlayer caster, double length, double fullAngle) {
        Vec3 origin = caster.getEyePosition();
        Vec3 dir = caster.getViewVector(1.0F).normalize();
        double cosHalf = Math.cos(Math.toRadians(fullAngle / 2.0));
        AABB search = new AABB(origin, origin.add(dir.scale(length))).inflate(length * Math.tan(Math.toRadians(fullAngle / 2.0)) + 1.0);

        return caster.level().getEntitiesOfClass(LivingEntity.class, search,
                e -> e != caster && e.isAlive() && insideCone(e, origin, dir, length, cosHalf));
    }

    private static boolean insideCone(LivingEntity e, Vec3 origin, Vec3 dir, double length, double cosHalf) {
        // Closest hitbox point so large entities at the cone edge still count
        Vec3 p = closestPoint(e.getBoundingBox(), origin);
        Vec3 to = p.subtract(origin);
        double dist = to.length();
        if (dist > length) return false;
        if (dist < 1.0E-4) return true;
        return to.scale(1.0 / dist).dot(dir) >= cosHalf;
    }

    /**
     * Living entities inside a cylinder whose axis starts at the caster's eye and
     * runs along the view direction.
     *
     * @param horizontalOnly ignore the pitch so the axis stays level
     */
    public static List<LivingEntity> entitiesInCylinder(ServerPlayer caster, double length, double radius, boolean horizontalOnly) {
        Vec3 origin = caster.getEyePosition();
        Vec3 dir = caster.getViewVector(1.0F);
        if (horizontalOnly) dir = new Vec3(dir.x, 0, dir.z);
        if (dir.lengthSqr() < 1.0E-6) return List.of();
        dir = dir.normalize();

        Vec3 end = origin.add(dir.scale(length));
        AABB search = new AABB(origin, end).inflate(radius + 1.0);
        final Vec3 axis = dir;

        return caster.level().getEntitiesOfClass(LivingEntity.class, search,
                e -> e != caster && e.isAlive() && insideCylinder(e, origin, axis, length, radius));
    }

    private static boolean insideCylinder(LivingEntity e, Vec3 origin, Vec3 axis, double length, double radius) {
        Vec3 center = e.getBoundingBox().getCenter();
        Vec3 to = center.subtract(origin);
        double along = to.dot(axis);
        if (along < 0 || along > length) return false;
        double perpSqr = to.lengthSqr() - along * along;
        double reach = radius + e.getBbWidth() / 2.0;
        return perpSqr <= reach * reach;
    }

    /**
     * Every particle starts at the origin and flies in its own direction inside the cone,
     * with a slightly different speed, so the cone spreads out instead of moving as a whole.
     */
    public static void spawnConeParticles(ServerLevel level, Vec3 origin, Vec3 dir, double fullAngle,
                                          ParticleOptions particle, int count, double minSpeed, double maxSpeed) {
        Vec3 d = dir.normalize();
        // Any vector not parallel to d gives a stable orthonormal basis
        Vec3 up = Math.abs(d.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = d.cross(up).normalize();
        Vec3 realUp = right.cross(d).normalize();
        double tanHalf = Math.tan(Math.toRadians(fullAngle / 2.0));

        for (int i = 0; i < count; i++) {
            double r = tanHalf * Math.sqrt(level.random.nextDouble());
            double a = level.random.nextDouble() * Math.PI * 2.0;
            Vec3 v = d.add(right.scale(Math.cos(a) * r)).add(realUp.scale(Math.sin(a) * r)).normalize();
            double speed = minSpeed + level.random.nextDouble() * (maxSpeed - minSpeed);
            // Count 0 makes the offset arguments act as the velocity direction
            level.sendParticles(particle, origin.x, origin.y, origin.z, 0, v.x, v.y, v.z, speed);
        }
    }

    /**
     * Same cone shape as {@link #spawnConeParticles}, for particle types that ignore the velocity
     * they are given (dust, item particles). Each particle is re-spawned one tick later
     * further along its own direction, so it still appears to travel outwards.
     */
    public static void spawnConeParticlesStepped(ServerLevel level, Vec3 origin, Vec3 dir, double length, double fullAngle,
                                                 ParticleOptions particle, int count, int steps) {
        Vec3 d = dir.normalize();
        Vec3 up = Math.abs(d.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = d.cross(up).normalize();
        Vec3 realUp = right.cross(d).normalize();
        double tanHalf = Math.tan(Math.toRadians(fullAngle / 2.0));

        Vec3[] dirs = new Vec3[count];
        double[] reach = new double[count];
        for (int i = 0; i < count; i++) {
            double r = tanHalf * Math.sqrt(level.random.nextDouble());
            double a = level.random.nextDouble() * Math.PI * 2.0;
            dirs[i] = d.add(right.scale(Math.cos(a) * r)).add(realUp.scale(Math.sin(a) * r)).normalize();
            reach[i] = 0.5 + level.random.nextDouble() * 0.5;
        }

        for (int step = 1; step <= steps; step++) {
            final double progress = (double) step / steps;
            Runnable spawn = () -> {
                for (int i = 0; i < count; i++) {
                    Vec3 pos = origin.add(dirs[i].scale(length * reach[i] * progress));
                    level.sendParticles(particle, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                }
            };
            if (step == 1) spawn.run();
            else DndMod.queueServerWork(step - 1, spawn);
        }
    }

    private static Vec3 closestPoint(AABB box, Vec3 p) {
        return new Vec3(
                Math.max(box.minX, Math.min(p.x, box.maxX)),
                Math.max(box.minY, Math.min(p.y, box.maxY)),
                Math.max(box.minZ, Math.min(p.z, box.maxZ)));
    }
}
