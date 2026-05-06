package net.luderspieler.dnd.classes.abilitys;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


public class AbilityPassiveTriggers {

    private static final ResourceLocation UNARMORED_MOD_ID = ResourceLocation.fromNamespaceAndPath("dnd", "unarmored_defense");
    private static final TagKey<Item> TAG_SHIELD = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:shields"));

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        // 1. Ability Check
        if (!AdvancementRegistry.playerHasAbility(player, Ability.UNARMORED_DEFENSE_BARB) &&
                !AdvancementRegistry.playerHasAbility(player, Ability.UNARMORED_DEFENSE_MONK)) {
            removeUnarmoredModifier(player);
            return;
        }

        // 2. Armor & Shield Check (deine bewährte Logik)
        boolean hasArmor = false;
        for (int i = 36; i <= 39; i++) {
            if (!player.getInventory().getItem(i).isEmpty()) {
                hasArmor = true;
                break;
            }
        }
        boolean hasShield = player.getMainHandItem().is(TAG_SHIELD) || player.getOffhandItem().is(TAG_SHIELD);

        // 3. DnD-spezifische Berechnung
        if (!hasArmor && !hasShield) {
            double dndValue = calculateDndAttackValue(player);
            applyUnarmoredModifier(player, dndValue);
        } else {
            removeUnarmoredModifier(player);
        }
    }

    private double calculateDndAttackValue(Player player) {
        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr == null) return 0;

        // Wir starten mit dem nackten Basiswert des Spielers (meist 1.0)
        double value = attackAttr.getBaseValue();

        // Wir gehen alle Modifier durch und addieren nur die, die von deiner Mod ("dnd") kommen
        for (AttributeModifier modifier : attackAttr.getModifiers()) {
            if (modifier.id().getNamespace().equals("dnd")) {
                // Wichtig: Wir prüfen hier nur auf ADD_VALUE (Addition).
                // Falls du auch Multiplikatoren nutzt, müsste man die Logik erweitern.
                if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    value += modifier.amount();
                }
            }
        }
        return value;
    }

    // Modifier Hilfsmethoden (Bleiben gleich für die Sauberkeit)
    private void applyUnarmoredModifier(Player player, double value) {
        var attributeInstance = player.getAttribute(Attributes.ARMOR);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(UNARMORED_MOD_ID);
            attributeInstance.addTransientModifier(new AttributeModifier(
                    UNARMORED_MOD_ID,
                    value,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    private void removeUnarmoredModifier(Player player) {
        var attributeInstance = player.getAttribute(Attributes.ARMOR);
        if (attributeInstance != null && attributeInstance.hasModifier(UNARMORED_MOD_ID)) {
            attributeInstance.removeModifier(UNARMORED_MOD_ID);
        }
    }
}
