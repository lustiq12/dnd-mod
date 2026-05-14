package net.luderspieler.dnd.potion;

import net.luderspieler.dnd.procedures.DecayingStareOnEffectActiveTickProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DecayingStareMobEffect extends MobEffect {
	public DecayingStareMobEffect() {
		super(MobEffectCategory.HARMFUL, -1);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		DecayingStareOnEffectActiveTickProcedure.execute(level, entity);
		return super.applyEffectTick(level, entity, amplifier);
	}
}