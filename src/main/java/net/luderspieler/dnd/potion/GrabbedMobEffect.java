package net.luderspieler.dnd.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.server.level.ServerLevel;

import net.luderspieler.dnd.procedures.GrabbedOnEffectActiveTickProcedure;

public class GrabbedMobEffect extends MobEffect {
	public GrabbedMobEffect() {
		super(MobEffectCategory.HARMFUL, -39169);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		GrabbedOnEffectActiveTickProcedure.execute(level, entity.getX(), entity.getY(), entity.getZ(), entity);
		return super.applyEffectTick(level, entity, amplifier);
	}
}