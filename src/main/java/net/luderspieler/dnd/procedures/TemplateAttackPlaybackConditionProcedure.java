package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.TemplateMobEntity;

public class TemplateAttackPlaybackConditionProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity instanceof TemplateMobEntity _datEntS ? _datEntS.getEntityData().get(TemplateMobEntity.DATA_anim) : "").equals("attack");
	}
}