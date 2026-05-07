package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.NothicEntity;

public class NothicIdleCooldownProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof NothicEntity _datEntSetI)
				_datEntSetI.getEntityData().set(NothicEntity.DATA_cooldown, (int) ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof NothicEntity _datEntSetS)
				_datEntSetS.getEntityData().set(NothicEntity.DATA_anim, "test");
		}
	}
}