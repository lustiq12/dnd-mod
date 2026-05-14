package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.entity.MedusaEntity;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class MedusaIdleCooldownProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof MedusaEntity _datEntSetI)
				_datEntSetI.getEntityData().set(MedusaEntity.DATA_cooldown, (int) ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof MedusaEntity _datEntSetS)
				_datEntSetS.getEntityData().set(MedusaEntity.DATA_anim, "idle");
		}
		if (entity instanceof MedusaEntity _datEntSetI)
			_datEntSetI.getEntityData().set(MedusaEntity.DATA_stone, (int) ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_stone) : 0) + 1));
		if ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_stone) : 0) >= 300) {
			if (entity instanceof MedusaEntity _datEntSetI)
				_datEntSetI.getEntityData().set(MedusaEntity.DATA_stone, 0);
			if (entity instanceof MedusaEntity _datEntSetI)
				_datEntSetI.getEntityData().set(MedusaEntity.DATA_cooldown, 20);
			if (entity instanceof MedusaEntity _datEntSetS)
				_datEntSetS.getEntityData().set(MedusaEntity.DATA_anim, "stone");
			if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null) {
				entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY()),
						((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ())));
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(DndModMobEffects.STUNNED, 16, 1, false, false));
				DndMod.queueServerWork(10, () -> {
					if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(DndModMobEffects.PETRIFIED, 120, 0, false, false));
				});
			}
		}
		if (entity instanceof MedusaEntity _datEntSetI)
			_datEntSetI.getEntityData().set(MedusaEntity.DATA_poison, (int) ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_poison) : 0) + 1));
		if ((entity instanceof MedusaEntity _datEntI ? _datEntI.getEntityData().get(MedusaEntity.DATA_poison) : 0) >= 160) {
			if (entity instanceof MedusaEntity _datEntSetI)
				_datEntSetI.getEntityData().set(MedusaEntity.DATA_poison, 0);
			if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null) {
				entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY()),
						((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ())));
				if ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null) {
					entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getX()), ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getY() + 1),
							((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null).getZ())));
					// Definition der fehlenden Variablen für diesen Block
					if (world instanceof Level projectileLevel && entity instanceof MedusaEntity medusa) {
						LivingEntity target = medusa.getTarget();
						if (target != null) {
							Arrow _arrowToSpawn = new Arrow(projectileLevel, medusa, new ItemStack(Items.ARROW), null);
							_arrowToSpawn.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1, false, true));
							_arrowToSpawn.pickup = AbstractArrow.Pickup.DISALLOWED;
							Vec3 _lookAngle = medusa.getLookAngle();
							_arrowToSpawn.setPos(medusa.getX(), medusa.getEyeY() - 0.1, medusa.getZ());
							_arrowToSpawn.shoot(_lookAngle.x, _lookAngle.y, _lookAngle.z, 10.0F, 0.0F);
							projectileLevel.addFreshEntity(_arrowToSpawn);
						}
					}
				}
			}
		}
	}
}