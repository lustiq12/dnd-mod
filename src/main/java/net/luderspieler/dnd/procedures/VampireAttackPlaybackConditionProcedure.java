package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.VampireEntity;

public class VampireAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof VampireEntity _datEntS ? _datEntS.getEntityData().get(VampireEntity.DATA_anim) : "").equals("attack");
	}
}