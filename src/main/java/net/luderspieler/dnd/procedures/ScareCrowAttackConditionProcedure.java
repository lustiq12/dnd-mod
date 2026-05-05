package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.ScarecrowEntity;

public class ScareCrowAttackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return !(entity instanceof ScarecrowEntity _datEntL0 && _datEntL0.getEntityData().get(ScarecrowEntity.DATA_frozen));
	}
}