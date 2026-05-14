package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.entity.NothicEntity;
import net.minecraft.world.entity.Entity;

public class NothicIdlePlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof NothicEntity _datEntS ? _datEntS.getEntityData().get(NothicEntity.DATA_anim) : "").equals("idle");
	}
}