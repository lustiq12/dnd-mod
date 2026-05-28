package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.ScarecrowEntity;

public class ScareCrowIdlePlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof ScarecrowEntity _datEntS ? _datEntS.getEntityData().get(ScarecrowEntity.DATA_anim) : "").equals("idle");
	}
}