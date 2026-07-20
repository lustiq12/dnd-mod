package net.luderspieler.dnd.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.luderspieler.dnd.entity.GoblinHenchmanEntity;

public class GoblinHenchmanIdleCooldownProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof GoblinHenchmanEntity _datEntI ? _datEntI.getEntityData().get(GoblinHenchmanEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof GoblinHenchmanEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoblinHenchmanEntity.DATA_cooldown, (int) ((entity instanceof GoblinHenchmanEntity _datEntI ? _datEntI.getEntityData().get(GoblinHenchmanEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof GoblinHenchmanEntity _datEntSetS)
				_datEntSetS.getEntityData().set(GoblinHenchmanEntity.DATA_anim, "idle");
		}
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= 7) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 1, false, true));
		}
	}
}