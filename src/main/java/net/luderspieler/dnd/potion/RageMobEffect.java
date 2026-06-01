package net.luderspieler.dnd.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.luderspieler.dnd.DndMod;

import java.util.function.BiConsumer;

public class RageMobEffect extends MobEffect {
    
    // Wir packen die IDs in Konstanten, damit wir sie unten beim Prüfen wiederverwenden können
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.rage_0");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(DndMod.MODID, "effect.rage_1");

    public RageMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13312);
        // Die Basiswerte beim Registrieren (werden unten dynamisch überschrieben, sind aber als Fallback gut)
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_ID, 4.0, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_ID, 3.0, AttributeModifier.Operation.ADD_VALUE);
    }

	@Override
	public void createModifiers(int amplifier, BiConsumer<Holder<Attribute>, AttributeModifier> output) {
		super.createModifiers(amplifier, output);
	}

	@Override
	public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
		AttributeInstance armorInstance = attributeMap.getInstance(Attributes.ARMOR);
		if (armorInstance != null) {
			armorInstance.removeModifier(ARMOR_ID);
			armorInstance.addPermanentModifier(new AttributeModifier(ARMOR_ID, 3.0, AttributeModifier.Operation.ADD_VALUE));
		}
		
		AttributeInstance damageInstance = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
		if (damageInstance != null) {
			damageInstance.removeModifier(DAMAGE_ID);

			double damageValue = 4.0; // Standard für Level 1-8 (+2 DnD-Schaden)
			if (amplifier >= 16) {
				damageValue = 8.0;    // Level 16-20 (+4 DnD-Schaden)
			} else if (amplifier >= 9) {
				damageValue = 6.0;    // Level 9-15 (+3 DnD-Schaden)
			}

			damageInstance.addPermanentModifier(new AttributeModifier(DAMAGE_ID, damageValue, AttributeModifier.Operation.ADD_VALUE));
		}
	}
}