package net.luderspieler.dnd.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.luderspieler.dnd.DndMod;

public class LargeFormMobEffect extends MobEffect {
	public LargeFormMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
		this.addAttributeModifier(Attributes.SCALE, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.large_form_0"), 0.75, AttributeModifier.Operation.ADD_VALUE);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.large_form_1"), 0.03, AttributeModifier.Operation.ADD_VALUE);
	}
}