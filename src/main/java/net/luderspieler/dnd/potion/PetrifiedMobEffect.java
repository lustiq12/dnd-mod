package net.luderspieler.dnd.potion;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.init.DndModMobEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
public class PetrifiedMobEffect extends MobEffect {
	public PetrifiedMobEffect() {
		super(MobEffectCategory.NEUTRAL, -6711040);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.petrified_0"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.petrified_1"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.petrified_2"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	@SubscribeEvent
	public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
		Consumable original = Items.HONEY_BOTTLE.components().get(DataComponents.CONSUMABLE);
		if (original != null) {
			List<ConsumeEffect> onConsumeEffects = new ArrayList<>(original.onConsumeEffects());
			onConsumeEffects.add(new RemoveStatusEffectsConsumeEffect(DndModMobEffects.PETRIFIED));
			Consumable replacementConsumable = new Consumable(original.consumeSeconds(), original.animation(), original.sound(), original.hasConsumeParticles(), onConsumeEffects);
			event.modify(Items.HONEY_BOTTLE, builder -> builder.set(DataComponents.CONSUMABLE, replacementConsumable));
		}
	}
}