package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.StirgeEntity;

public class StirgeIdleCooldownProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof StirgeEntity _datEntI ? _datEntI.getEntityData().get(StirgeEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof StirgeEntity _datEntSetI)
				_datEntSetI.getEntityData().set(StirgeEntity.DATA_cooldown, (int) ((entity instanceof StirgeEntity _datEntI ? _datEntI.getEntityData().get(StirgeEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof StirgeEntity _datEntSetS)
				_datEntSetS.getEntityData().set(StirgeEntity.DATA_anim, "idle");
		}
	}
}