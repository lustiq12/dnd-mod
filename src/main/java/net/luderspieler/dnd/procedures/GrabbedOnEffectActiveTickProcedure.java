package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.luderspieler.dnd.network.DndModVariables;

import java.util.UUID;

public class GrabbedOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity charmer = null;
		Vec3 vec = Vec3.ZERO;
		charmer = world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, entity.getData(DndModVariables.PLAYER_VARIABLES).grabber) : null;
		if (charmer != null) {
			vec = ((new Vec3((charmer.getX() - x), (charmer.getY() - y), (charmer.getZ() - z))).normalize()).scale(0.02);
			entity.push((vec.x()), (vec.y() * 0), (vec.z()));
			entity.hurtMarked = true;
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}