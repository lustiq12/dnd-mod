package net.luderspieler.dnd.potion;

import net.luderspieler.dnd.init.DndModMobEffects;
import net.luderspieler.dnd.procedures.CharmedOnEffectActiveTickProcedure;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class CharmedMobEffect extends MobEffect {
	public CharmedMobEffect() {
		super(MobEffectCategory.HARMFUL, -39169);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		CharmedOnEffectActiveTickProcedure.execute(level, entity.getX(), entity.getY(), entity.getZ(), entity);
		return super.applyEffectTick(level, entity, amplifier);
	}

	@SubscribeEvent
	public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
		Consumable original = Items.HONEY_BOTTLE.components().get(DataComponents.CONSUMABLE);
		if (original != null) {
			List<ConsumeEffect> onConsumeEffects = new ArrayList<>(original.onConsumeEffects());
			onConsumeEffects.add(new RemoveStatusEffectsConsumeEffect(DndModMobEffects.CHARMED));
			Consumable replacementConsumable = new Consumable(original.consumeSeconds(), original.animation(), original.sound(), original.hasConsumeParticles(), onConsumeEffects);
			event.modify(Items.HONEY_BOTTLE, builder -> builder.set(DataComponents.CONSUMABLE, replacementConsumable));
		}
	}
}