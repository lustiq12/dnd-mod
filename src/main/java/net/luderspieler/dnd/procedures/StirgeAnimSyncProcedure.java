package net.luderspieler.dnd.procedures;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import net.luderspieler.dnd.entity.StirgeEntity;
import net.luderspieler.dnd.DndMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class StirgeAnimSyncProcedure {
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
		if (immediatesourceentity instanceof StirgeEntity && amount == 100) {
			if (event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}
			if (immediatesourceentity instanceof StirgeEntity _datEntSetI)
				_datEntSetI.getEntityData().set(StirgeEntity.DATA_cooldown, 15);
			if (immediatesourceentity instanceof StirgeEntity _datEntSetS)
				_datEntSetS.getEntityData().set(StirgeEntity.DATA_anim, "attack");
			immediatesourceentity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((entity.getX()), (entity.getY()), (entity.getZ())));
			immediatesourceentity.setDeltaMovement(new Vec3((immediatesourceentity.getLookAngle().x), (immediatesourceentity.getLookAngle().y), (immediatesourceentity.getLookAngle().z)));
			DndMod.queueServerWork(6, () -> {
				entity.hurt(damagesource, 4);
			});
		}
	}
}