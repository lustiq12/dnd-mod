package net.luderspieler.dnd.agameplay;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.init.DndModDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class GeneralDamageHandling {

    public static void handle(ServerPlayer player, LivingIncomingDamageEvent event) {
        handleDamageTypeResistances(player, event);
        handleCritsAndFailures(event);
    }

    private static void handleCritsAndFailures(LivingIncomingDamageEvent event) {
        var sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) return;

        int d20 = (int) (Math.random() * 20) + 1;

        if (d20 == 1) {
            event.setAmount(0.0f);
        }
        else if (d20 == 20) {
            event.setAmount(event.getAmount() * 2.0f);
        }

        // TODO: check for Abilities that change crit behavior
    }

    /**
     * Checks incoming damage against the D&D damage-type immunities and resistances
     * stored in vars.Abilities (e.g. FIRE_DAMAGE_IMMUNITY, FIRE_DAMAGE_RESISTANCE).
     * Dragonborn get resistance to their chosen Draconic Ancestry damage type via
     * GeneralData instead, since that varies per player. Immunity takes precedence
     * over resistance.
     */
    private static void handleDamageTypeResistances(ServerPlayer player, LivingIncomingDamageEvent event) {
        String typeKey = resolveDndDamageTypeKey(event.getSource());
        if (typeKey == null) return;

        if (AbilityUtils.hasAbility(player, Ability.valueOf(typeKey + "_DAMAGE_IMMUNITY"))) {
            event.setAmount(0.0f);
            return;
        }

        boolean resistant = AbilityUtils.hasAbility(player, Ability.valueOf(typeKey + "_DAMAGE_RESISTANCE"));
        if (resistant) {
            event.setAmount(event.getAmount() / 2.0f);
        }
    }

    /**
     * Maps a Minecraft DamageSource to the closest D&D 2024 damage type.
     * Returns null for types outside this mapping.
     */
    private static String resolveDndDamageTypeKey(DamageSource source) {
        if (source.is(DndModDamageTypes.FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR) || source.is(DamageTypes.CAMPFIRE)
                || source.is(DamageTypes.FIREBALL) || source.is(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            return "FIRE";
        }
        if (source.is(DndModDamageTypes.COLD) || source.is(DamageTypes.FREEZE)) return "COLD";
        if (source.is(DndModDamageTypes.LIGHTNING) || source.is(DamageTypes.LIGHTNING_BOLT)) return "LIGHTNING";
        if (source.is(DndModDamageTypes.THUNDER) || source.is(DamageTypes.SONIC_BOOM)) return "THUNDER";
        if (source.is(DndModDamageTypes.FORCE) || source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)
                || source.is(DamageTypes.WIND_CHARGE) || source.is(DamageTypes.FIREWORKS)) {
            return "FORCE";
        }
        if (source.is(DndModDamageTypes.NECROTIC) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.WITHER_SKULL)) {
            return "NECROTIC";
        }
        if (source.is(DndModDamageTypes.POISON) || source.typeHolder().is(NeoForgeMod.POISON_DAMAGE)) return "POISON";
        if (source.is(DndModDamageTypes.ACID)) return "ACID";
        if (source.is(DndModDamageTypes.PSYCHIC)) return "PSYCHIC";
        if (source.is(DndModDamageTypes.RADIANT)) return "RADIANT";
        if (source.is(DndModDamageTypes.BLUDGEONING) || source.is(DamageTypes.FALL) || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.FALLING_STALACTITE)
                || source.is(DamageTypes.STALAGMITE) || source.is(DamageTypes.CRAMMING)) {
            return "BLUDGEONING";
        }
        if (source.is(DndModDamageTypes.PIERCING) || source.is(DamageTypes.ARROW) || source.is(DamageTypes.TRIDENT)
                || source.is(DamageTypes.MOB_PROJECTILE) || source.is(DamageTypes.THROWN) || source.is(DamageTypes.STING)
                || source.is(DamageTypes.SPIT)) {
            return "PIERCING";
        }
        if (source.is(DndModDamageTypes.SLASHING)) return "SLASHING";
        if (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
            return resolveMeleeWeaponDamageTypeKey(source);
        }
        return null;
    }

    /**
     * Melee attacks are split by the attacker's main-hand weapon: axe/sword deal
     * slashing, mace and empty hand deal bludgeoning, any other weapon pierces.
     */
    private static String resolveMeleeWeaponDamageTypeKey(DamageSource source) {
        if (!(source.getDirectEntity() instanceof LivingEntity attacker)) return "BLUDGEONING";
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty() || weapon.is(Items.MACE)) return "BLUDGEONING";
        if (weapon.getItem() instanceof AxeItem || weapon.is(ItemTags.SWORDS)) return "SLASHING";
        return "PIERCING";
    }
}
