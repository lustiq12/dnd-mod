package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.luderspieler.dnd.entity.NothicEntity;

import java.util.Comparator;

public class NothicIdleCooldownProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean done = false;
		if ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof NothicEntity _datEntSetI)
				_datEntSetI.getEntityData().set(NothicEntity.DATA_cooldown, (int) ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof NothicEntity _datEntSetS)
				_datEntSetS.getEntityData().set(NothicEntity.DATA_anim, "test");
		}
		if (entity instanceof NothicEntity _datEntSetI)
			_datEntSetI.getEntityData().set(NothicEntity.DATA_stare_cooldown, (int) ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_stare_cooldown) : 0) + 1));
		if ((entity instanceof NothicEntity _datEntI ? _datEntI.getEntityData().get(NothicEntity.DATA_stare_cooldown) : 0) >= 240) {
			if (entity instanceof NothicEntity _datEntSetI)
				_datEntSetI.getEntityData().set(NothicEntity.DATA_stare_cooldown, 0);
			done = false;
			{
				final Vec3 _center = new Vec3(x, y, z);
				for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
					if (!done && entityiterator instanceof Player) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(DndModMobEffects.DECAYING_STARE, 120, 0, false, false));
						{
							DndModVariables.PlayerVariables _vars = entityiterator.getData(DndModVariables.PLAYER_VARIABLES);
							_vars.Decaying_Focus = entity.getStringUUID();
							_vars.markSyncDirty();
						}
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(DndModMobEffects.DECAYING_STARE, 120, 0, false, false));
						if (entity instanceof NothicEntity _datEntSetS)
							_datEntSetS.getEntityData().set(NothicEntity.DATA_focus, (entityiterator.getStringUUID()));
						done = true;
					}
				}
			}
		}
	}
}