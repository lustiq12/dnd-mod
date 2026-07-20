package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.GoblinHenchmanEntity;

public class GoblinHenchmanAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof GoblinHenchmanEntity _datEntS ? _datEntS.getEntityData().get(GoblinHenchmanEntity.DATA_anim) : "").equals("attack");
	}
}