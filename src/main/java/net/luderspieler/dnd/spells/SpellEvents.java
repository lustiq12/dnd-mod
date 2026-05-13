package net.luderspieler.dnd.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

public class SpellEvents {

    @SubscribeEvent
    public void onFireballHit(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event) {
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
    public void onEntityTick(EntityTickEvent.Post event) {
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
}
