package net.luderspieler.dnd.npc;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;

/**
 * Reads/writes the DATA_Configuration and DATA_Data fields any entity may declare, without a
 * compile-time reference to that entity's class. Same reflection pattern as
 * ReusableIdleCooldownProcedure/ReusableAnimSyncProcedure.
 *
 * To make an entity NPC-capable, add both of these fields to it (same shape as any other
 * MCreator SynchedEntityData string field), register them in defineSynchedData with a default
 * of "", and save/load them in addAdditionalSaveData/readAdditionalSaveData:
 *
 *   public static final EntityDataAccessor<String> DATA_Configuration =
 *           SynchedEntityData.defineId(YourEntity.class, EntityDataSerializers.STRING);
 *   public static final EntityDataAccessor<String> DATA_Data =
 *           SynchedEntityData.defineId(YourEntity.class, EntityDataSerializers.STRING);
 */
public class NpcDataAccess {

    private static final String CONFIGURATION_FIELD = "DATA_Configuration";
    private static final String DATA_FIELD = "DATA_Data";

    private NpcDataAccess() {
    }

    public static boolean isNpc(Entity entity) {
        return entity != null && findField(entity, CONFIGURATION_FIELD) != null && findField(entity, DATA_FIELD) != null;
    }

    public static String getConfiguration(Entity entity) {
        return getString(entity, CONFIGURATION_FIELD);
    }

    public static void setConfiguration(Entity entity, String value) {
        setString(entity, CONFIGURATION_FIELD, value);
    }

    public static String getData(Entity entity) {
        return getString(entity, DATA_FIELD);
    }

    public static void setData(Entity entity, String value) {
        setString(entity, DATA_FIELD, value);
    }

    @SuppressWarnings("unchecked")
    private static String getString(Entity entity, String fieldName) {
        Field field = findField(entity, fieldName);
        if (field == null) return "";
        try {
            EntityDataAccessor<String> accessor = (EntityDataAccessor<String>) field.get(null);
            String value = entity.getEntityData().get(accessor);
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private static void setString(Entity entity, String fieldName, String value) {
        Field field = findField(entity, fieldName);
        if (field == null) return;
        try {
            EntityDataAccessor<String> accessor = (EntityDataAccessor<String>) field.get(null);
            entity.getEntityData().set(accessor, value);
        } catch (Exception ignored) {
        }
    }

    private static Field findField(Entity entity, String fieldName) {
        Class<?> clazz = entity.getClass();
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
