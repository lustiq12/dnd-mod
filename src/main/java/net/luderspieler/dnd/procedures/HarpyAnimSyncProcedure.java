package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.entity.HarpyEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class HarpyAnimSyncProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingIncomingDamageEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getSource(), event.getEntity(), event.getSource().getDirectEntity(), event.getAmount());
		}
	}

	public static void execute(LevelAccessor world, DamageSource damagesource, Entity entity, Entity immediatesourceentity, double amount) {
		execute(null, world, damagesource, entity, immediatesourceentity, amount);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, DamageSource damagesource, Entity entity, Entity immediatesourceentity, double amount) {
		if (damagesource == null || entity == null || immediatesourceentity == null)
			return;
		if (immediatesourceentity instanceof HarpyEntity && amount == 100) {
			if (event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}
			if (!((entity instanceof HarpyEntity _datEntS ? _datEntS.getEntityData().get(HarpyEntity.DATA_anim) : "").equals("rizz"))) {
				if (immediatesourceentity instanceof HarpyEntity _datEntSetI)
					_datEntSetI.getEntityData().set(HarpyEntity.DATA_cooldown, 10);
				if (immediatesourceentity instanceof HarpyEntity _datEntSetS)
					_datEntSetS.getEntityData().set(HarpyEntity.DATA_anim, "attack");
				immediatesourceentity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((entity.getX()), (entity.getY()), (entity.getZ())));
				immediatesourceentity.setDeltaMovement(new Vec3((immediatesourceentity.getLookAngle().x), (immediatesourceentity.getLookAngle().y), (immediatesourceentity.getLookAngle().z)));
				DndMod.queueServerWork(8, () -> {
					entity.hurt(damagesource, 6);
				});
			}
		}
	}
}