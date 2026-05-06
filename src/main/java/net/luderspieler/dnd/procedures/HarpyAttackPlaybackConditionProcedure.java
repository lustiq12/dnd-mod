package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.HarpyEntity;

public class HarpyAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof HarpyEntity _datEntS ? _datEntS.getEntityData().get(HarpyEntity.DATA_anim) : "").equals("attack");
	}
}