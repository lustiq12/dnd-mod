package net.luderspieler.dnd.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.luderspieler.dnd.entity.NothicEntity;

import java.util.UUID;

public class DecayingStareOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity focus = null;
		if (!(entity instanceof NothicEntity)) {
			focus = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, entity.getData(DndModVariables.PLAYER_VARIABLES).Decaying_Focus) : null;
		} else {
			focus = world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, (entity instanceof NothicEntity _datEntS ? _datEntS.getEntityData().get(NothicEntity.DATA_focus) : "")) : null;
		}
		if (focus != null) {
			entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((focus.getX()), (focus.getY()), (focus.getZ())));
			if (!(entity instanceof NothicEntity)) {
				if (((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(DndModMobEffects.DECAYING_STARE) ? _livEnt.getEffect(DndModMobEffects.DECAYING_STARE).getDuration() : 0) % 20 == 0)) {
					entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.MAGIC), focus, focus), 3);
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}