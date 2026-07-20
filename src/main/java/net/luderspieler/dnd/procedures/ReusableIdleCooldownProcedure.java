package net.luderspieler.dnd.procedures;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;

public class ReusableIdleCooldownProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;

        try {
            // Liest die statischen Felder "DATA_cooldown" und "DATA_anim" dynamisch aus der Mob-Klasse
            Class<?> clazz = entity.getClass();
            Field cooldownField = getField(clazz, "DATA_cooldown");
            Field animField = getField(clazz, "DATA_anim");

            if (cooldownField != null && animField != null) {
                @SuppressWarnings("unchecked")
                EntityDataAccessor<Integer> dataCooldown = (EntityDataAccessor<Integer>) cooldownField.get(null);
                @SuppressWarnings("unchecked")
                EntityDataAccessor<String> dataAnim = (EntityDataAccessor<String>) animField.get(null);

                int currentCooldown = entity.getEntityData().get(dataCooldown);

                if (currentCooldown > 0) {
                    entity.getEntityData().set(dataCooldown, currentCooldown - 1);
                } else {
                    entity.getEntityData().set(dataAnim, "idle");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hilfsmethode, um Felder auch in Superklassen zu finden
    private static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}