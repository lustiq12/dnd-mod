package net.luderspieler.dnd.procedures;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;

import net.luderspieler.dnd.entity.GoristroEntity;
import net.luderspieler.dnd.DndMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class GoristroAnimSyncProcedure {
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
		if (immediatesourceentity instanceof GoristroEntity && amount == 100) {
			if (event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}
			if (immediatesourceentity instanceof GoristroEntity _datEntSetI)
				_datEntSetI.getEntityData().set(GoristroEntity.DATA_Cooldown, 20);
			if (immediatesourceentity instanceof GoristroEntity _datEntSetS)
				_datEntSetS.getEntityData().set(GoristroEntity.DATA_anim, "attack");
			DndMod.queueServerWork(15, () -> {
				entity.hurt(damagesource, 27);
			});
		}
	}
}