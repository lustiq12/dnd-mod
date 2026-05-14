package net.luderspieler.dnd.classes.abilitys;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class AbilityMethods {

    private static final ResourceLocation UNARMORED_MOD_ID = ResourceLocation.fromNamespaceAndPath("dnd", "unarmored_defense");
    private static final TagKey<Item> TAG_SHIELD = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:shields"));

    public static void handleUnarmoredDefense(Player player) {
        var armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr == null) return;

        // 1. Ability Check
        boolean hasAbility = AdvancementRegistry.playerHasAbility(player, Ability.UNARMORED_DEFENSE_BARB) ||
                AdvancementRegistry.playerHasAbility(player, Ability.UNARMORED_DEFENSE_MONK);

        // 2. Armor & Shield Check
        boolean hasArmor = false;
        for (int i = 36; i <= 39; i++) {
            if (!player.getInventory().getItem(i).isEmpty()) {
                hasArmor = true;
                break;
            }
        }
        boolean hasShield = player.getMainHandItem().is(TAG_SHIELD) || player.getOffhandItem().is(TAG_SHIELD);

        // 3. Main Logic (Zentralisiert)
        if (hasAbility && !hasArmor && !hasShield) {
            double dndValue = getDndAttributeValue(player, Attributes.ATTACK_DAMAGE);

            // Modifier nur aktualisieren, wenn nötig (Performance)
            if (!armorAttr.hasModifier(UNARMORED_MOD_ID) || armorAttr.getModifier(UNARMORED_MOD_ID).amount() != dndValue) {
                armorAttr.removeModifier(UNARMORED_MOD_ID);
                armorAttr.addTransientModifier(new AttributeModifier(
                        UNARMORED_MOD_ID,
                        dndValue,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        } else {
            // "removeUnarmoredModifier" Logik direkt hier drin:
            if (armorAttr.hasModifier(UNARMORED_MOD_ID)) {
                armorAttr.removeModifier(UNARMORED_MOD_ID);
            }
        }
    }




    /**
     * Universeller Attribut-Getter für alle D&D Modifier
     */
    public static double getDndAttributeValue(Player player, Holder<Attribute> attribute) {
        var inst = player.getAttribute(attribute);
        if (inst == null) return 0;

        double value = inst.getBaseValue();
        for (AttributeModifier modifier : inst.getModifiers()) {
            if (modifier.id().getNamespace().equals("dnd")) {
                if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    value += modifier.amount();
                }
            }
        }
        return value;
    }
}