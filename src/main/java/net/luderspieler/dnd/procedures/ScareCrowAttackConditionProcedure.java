package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.entity.ScarecrowEntity;
import net.minecraft.world.entity.Entity;

public class ScareCrowAttackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return !(entity instanceof ScarecrowEntity _datEntL0 && _datEntL0.getEntityData().get(ScarecrowEntity.DATA_frozen));
	}
}