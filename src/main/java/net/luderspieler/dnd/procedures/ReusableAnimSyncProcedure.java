package net.luderspieler.dnd.procedures;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;

import net.luderspieler.dnd.DndMod;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

@EventBusSubscriber
public class ReusableAnimSyncProcedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingIncomingDamageEvent event) {
        if (event.getEntity() != null) {
            execute(event, event.getEntity().level(), event.getSource(), event.getEntity(), event.getSource().getDirectEntity(), event.getAmount());
        }
    }

    public static void execute(LevelAccessor world, DamageSource damagesource, Entity entity, Entity immediatesourceentity, double amount) {
        execute(null, world, damagesource, entity, immediatesourceentity, amount);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, DamageSource damagesource, Entity entity, Entity immediatesourceentity, double amount) {
        if (damagesource == null || entity == null || immediatesourceentity == null)
            return;

        if (amount == 1234) {
            EntityDataAccessor<Integer> dataCooldown = getEntityDataAccessor(immediatesourceentity, "DATA_cooldown");
            EntityDataAccessor<String> dataAnim = getEntityDataAccessor(immediatesourceentity, "DATA_anim");

            // Überprüft, ob der Angreifer überhaupt über diese Felder verfügt
            if (dataCooldown != null && dataAnim != null) {
                if (event instanceof ICancellableEvent _cancellable) {
                    _cancellable.setCanceled(true);
                }

                // Werte dynamisch setzen
                immediatesourceentity.getEntityData().set(dataCooldown, 15);
                immediatesourceentity.getEntityData().set(dataAnim, "attack");

                DndMod.queueServerWork(6, () -> {
                    entity.hurt(damagesource, 6);
                });
            }
        }
    }

    // Hilfsmethode zur Reflection-Abfrage
    @SuppressWarnings("unchecked")
    private static <T> EntityDataAccessor<T> getEntityDataAccessor(Entity entity, String fieldName) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (EntityDataAccessor<T>) field.get(null);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                break;
            }
        }
        return null;
    }
}