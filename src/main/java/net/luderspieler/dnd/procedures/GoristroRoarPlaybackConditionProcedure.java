package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.GoristroEntity;

public class GoristroRoarPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof GoristroEntity _datEntS ? _datEntS.getEntityData().get(GoristroEntity.DATA_anim) : "").equals("roar");
	}
}