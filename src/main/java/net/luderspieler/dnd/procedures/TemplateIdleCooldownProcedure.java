package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.Entity;

import net.luderspieler.dnd.entity.TemplateMobEntity;

public class TemplateIdleCooldownProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof TemplateMobEntity _datEntI ? _datEntI.getEntityData().get(TemplateMobEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof TemplateMobEntity _datEntSetI)
				_datEntSetI.getEntityData().set(TemplateMobEntity.DATA_cooldown, (int) ((entity instanceof TemplateMobEntity _datEntI ? _datEntI.getEntityData().get(TemplateMobEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof TemplateMobEntity _datEntSetS)
				_datEntSetS.getEntityData().set(TemplateMobEntity.DATA_anim, "idle");
		}
	}
}