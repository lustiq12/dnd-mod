package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.EntityDataAccessor;

import java.lang.reflect.Field;

public class ReusableAttackPlaybackConditionProcedure {

    public static boolean execute(Entity entity) {
        if (entity == null)
            return false;

        EntityDataAccessor<String> dataAnim = getEntityDataAccessor(entity, "DATA_anim");

        if (dataAnim != null) {
            String animValue = entity.getEntityData().get(dataAnim);
            return "attack".equals(animValue);
        }

        return false;
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