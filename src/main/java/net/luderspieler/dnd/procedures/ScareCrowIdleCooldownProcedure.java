package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.luderspieler.dnd.entity.ScarecrowEntity;

import java.util.Comparator;

public class ScareCrowIdleCooldownProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof ScarecrowEntity _datEntI ? _datEntI.getEntityData().get(ScarecrowEntity.DATA_cd) : 0) > 0) {
			if (entity instanceof ScarecrowEntity _datEntSetI)
				_datEntSetI.getEntityData().set(ScarecrowEntity.DATA_cd, (int) ((entity instanceof ScarecrowEntity _datEntI ? _datEntI.getEntityData().get(ScarecrowEntity.DATA_cd) : 0) - 1));
		} else {
			if (entity instanceof ScarecrowEntity _datEntSetS)
				_datEntSetS.getEntityData().set(ScarecrowEntity.DATA_anim, "idle");
		}
		{
			LevelAccessor world = entity.level();
			final Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
			boolean isBeingWatched = false;
			for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(100 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
				if (entityiterator instanceof Player _player) {
					if (entity instanceof ScarecrowEntity _scarecrow
							&& _scarecrow.isLookingAtMe(_player, 0.5, false, true, new double[]{_scarecrow.getEyeY(), _scarecrow.getY() + 0.5 * (double) _scarecrow.getScale(), (_scarecrow.getEyeY() + _scarecrow.getY()) / 2.0})) {
						isBeingWatched = true;
						break; // Ein Spieler reicht, um sie einzufrieren
					}
				}
			}
			if (entity instanceof ScarecrowEntity _scarecrowData) {
				// Setzt frozen auf true wenn gesehen, sonst auf false
				_scarecrowData.getEntityData().set(ScarecrowEntity.DATA_frozen, isBeingWatched);
				// DEBUG: Optional, damit du siehst ob es klappt
				// if (isBeingWatched) ((Player)world.getNearestPlayer(_center.x, _center.y, _center.z, 10, false)).displayClientMessage(Component.literal("Frozen: true"), true);
			}
		}
		if (entity instanceof ScarecrowEntity _datEntL4 && _datEntL4.getEntityData().get(ScarecrowEntity.DATA_frozen)) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 1, 254, true, false));
		}
	}
}