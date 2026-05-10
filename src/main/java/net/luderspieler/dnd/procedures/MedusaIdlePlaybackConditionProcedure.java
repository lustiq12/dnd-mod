package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.MedusaEntity;

public class MedusaIdlePlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof MedusaEntity _datEntS ? _datEntS.getEntityData().get(MedusaEntity.DATA_anim) : "").equals("idle");
	}
}