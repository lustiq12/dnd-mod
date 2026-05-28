package net.luderspieler.dnd.procedures;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;

import net.luderspieler.dnd.entity.ScarecrowEntity;
import net.luderspieler.dnd.DndMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ScareCrowAnimSyncProcedure {
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
		if (immediatesourceentity instanceof ScarecrowEntity && amount == 100) {
			if (event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}
			if (immediatesourceentity instanceof ScarecrowEntity _datEntSetI)
				_datEntSetI.getEntityData().set(ScarecrowEntity.DATA_cd, 10);
			if (immediatesourceentity instanceof ScarecrowEntity _datEntSetS)
				_datEntSetS.getEntityData().set(ScarecrowEntity.DATA_anim, "attack");
			DndMod.queueServerWork(6, () -> {
				entity.hurt(damagesource, 6);
			});
		}
	}
}