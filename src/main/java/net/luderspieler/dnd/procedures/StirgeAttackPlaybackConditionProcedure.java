package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.StirgeEntity;

public class StirgeAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof StirgeEntity _datEntS ? _datEntS.getEntityData().get(StirgeEntity.DATA_anim) : "").equals("attack");
	}
}