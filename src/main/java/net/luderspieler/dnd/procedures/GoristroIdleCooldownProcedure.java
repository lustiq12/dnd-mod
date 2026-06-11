package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.luderspieler.dnd.entity.GoristroEntity;
import net.luderspieler.dnd.DndMod;

import java.util.Comparator;

public class GoristroIdleCooldownProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof GoristroEntity _datEntI ? _datEntI.getEntityData().get(GoristroEntity.DATA_Cooldown) : 0) > 0) {
			if (entity instanceof GoristroEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoristroEntity.DATA_Cooldown, (int) ((entity instanceof GoristroEntity _datEntI ? _datEntI.getEntityData().get(GoristroEntity.DATA_Cooldown) : 0) - 1));
		} else {
			if (entity instanceof GoristroEntity _datEntSetS)
				_datEntSetS.getEntityData().set(GoristroEntity.DATA_anim, "idle");
		}
		if ((entity instanceof GoristroEntity _datEntI ? _datEntI.getEntityData().get(GoristroEntity.DATA_rawwwrrr) : 0) < 300) {
			if (entity instanceof GoristroEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoristroEntity.DATA_rawwwrrr, (int) ((entity instanceof GoristroEntity _datEntI ? _datEntI.getEntityData().get(GoristroEntity.DATA_rawwwrrr) : 0) + 1));
		} else {
			if (entity instanceof GoristroEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoristroEntity.DATA_Cooldown, 70);
			if (entity instanceof GoristroEntity _datEntSetS)
				_datEntSetS.getEntityData().set(GoristroEntity.DATA_anim, "roar");
			DndMod.queueServerWork(50, () -> {
				{
					final Vec3 _center = new Vec3(x, y, z);
					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(36 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
						if (!(entityiterator == entity)) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 1, false, false));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1, false, false));
						}
					}
				}
			});
			if (entity instanceof GoristroEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoristroEntity.DATA_rawwwrrr, -1);
		}
	}
}