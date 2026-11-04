package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.SolarEntity;

public class SolarAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof SolarEntity _datEntS ? _datEntS.getEntityData().get(SolarEntity.DATA_anim) : "").equals("attack");
	}
}