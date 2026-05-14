package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.entity.VampireEntity;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class VampireIdleCooldownProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof VampireEntity _datEntI ? _datEntI.getEntityData().get(VampireEntity.DATA_cooldown) : 0) > 0) {
			if (entity instanceof VampireEntity _datEntSetI)
				_datEntSetI.getEntityData().set(VampireEntity.DATA_cooldown, (int) ((entity instanceof VampireEntity _datEntI ? _datEntI.getEntityData().get(VampireEntity.DATA_cooldown) : 0) - 1));
		} else {
			if (entity instanceof VampireEntity _datEntSetS)
				_datEntSetS.getEntityData().set(VampireEntity.DATA_anim, "idle");
		}
		if (entity instanceof VampireEntity _datEntSetI)
			_datEntSetI.getEntityData().set(VampireEntity.DATA_rizz, (int) ((entity instanceof VampireEntity _datEntI ? _datEntI.getEntityData().get(VampireEntity.DATA_rizz) : 0) + 1));
		if ((entity instanceof VampireEntity _datEntI ? _datEntI.getEntityData().get(VampireEntity.DATA_rizz) : 0) >= 800) {
			if (entity instanceof VampireEntity _datEntSetI)
				_datEntSetI.getEntityData().set(VampireEntity.DATA_rizz, 0);
			if (entity instanceof VampireEntity _datEntSetS)
				_datEntSetS.getEntityData().set(VampireEntity.DATA_anim, "rizz");
			if (entity instanceof VampireEntity _datEntSetI)
				_datEntSetI.getEntityData().set(VampireEntity.DATA_cooldown, 60);
			DndMod.queueServerWork(30, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 2), z, 20, 0, 0, 0, 1);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("dnd:harpy_rizz")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("dnd:harpy_rizz")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
			});
			DndMod.queueServerWork(30, () -> {
				{
					final Vec3 _center = new Vec3(x, y, z);
					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
						if (entityiterator instanceof ServerPlayer) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(DndModMobEffects.CHARMED, 240, 0));
							{
								DndModVariables.PlayerVariables _vars = entityiterator.getData(DndModVariables.PLAYER_VARIABLES);
								_vars.Charmer = entity.getStringUUID();
								_vars.markSyncDirty();
							}
						}
					}
				}
			});
		}
	}
}