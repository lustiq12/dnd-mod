package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.entity.HarpyEntity;
import net.minecraft.world.entity.Entity;

public class HarpyRizzPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof HarpyEntity _datEntS ? _datEntS.getEntityData().get(HarpyEntity.DATA_anim) : "").equals("rizz");
	}
}