package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;

import net.luderspieler.dnd.entity.SolarEntity;
import net.luderspieler.dnd.DndMod;

public class SolarIdleCooldownProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 1, true, false));
		if ((entity instanceof SolarEntity _datEntI ? _datEntI.getEntityData().get(SolarEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof SolarEntity _datEntSetI)
				_datEntSetI.getEntityData().set(SolarEntity.DATA_cooldown, (int) ((entity instanceof SolarEntity _datEntI ? _datEntI.getEntityData().get(SolarEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof SolarEntity _datEntSetS)
				_datEntSetS.getEntityData().set(SolarEntity.DATA_anim, "idle");
		}
		if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null) {
			if (world.dayTime() % 160 == 0 && (new Vec3(x, y, z)).distanceTo((new Vec3(((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY()),
					((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ())))) > 10) {
				if (entity instanceof SolarEntity _datEntSetI)
					_datEntSetI.getEntityData().set(SolarEntity.DATA_cooldown, 40);
				if (entity instanceof SolarEntity _datEntSetS)
					_datEntSetS.getEntityData().set(SolarEntity.DATA_anim, "bow");
				DndMod.queueServerWork(37, () -> {
					if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null) {
						if (((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > 0) {
							(entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).hurt(new DamageSource(world.holderOrThrow(DamageTypes.ARROW), entity), 101);
						} else {
							(entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).hurt(new DamageSource(world.holderOrThrow(DamageTypes.ARROW), entity), 60);
						}
					}
				});
			}
		}
	}
}