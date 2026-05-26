package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Passive effects evaluated every INTERVAL ticks.
 * Called by AbilityPassiveTriggers.onPlayerTick().
 */
public class AbilityMethods_AlwaysActive {

    public static final int INTERVAL            = 20;
    private static final int DARKVISION_REFRESH = 600;

    private static final TagKey<Item> TAG_HEAVY_ARMOR = TagKey.create(
            Registries.ITEM, ResourceLocation.parse("dnd:heavy_armor"));

    public static void tick(ServerPlayer player) {
        int tick = player.tickCount;
        handleDarkvision(player, tick);
        handleUnarmoredDefense(player);
        handleFastMovement(player);
        handleUnarmoredMovement(player);
        handleDwarvenToughness(player);
    }

    private static void handleDarkvision(ServerPlayer player, int tick) {
        if (tick % DARKVISION_REFRESH != 0) return;
        boolean has60  = AbilityUtils.hasAbility(player, Ability.DARKVISION_60);
        boolean has120 = AbilityUtils.hasAbility(player, Ability.DARKVISION_120);
        if (has60 || has120) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, 40_000, 0, false, false, false));
        }
    }

    private static void handleUnarmoredDefense(ServerPlayer player) {
        final String MOD_ID = "dnd:armor_unarmored_defense";
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_DEFENSE)) {
            removeArmorMod(player, MOD_ID); return;
        }
        if (isWearingArmor(player)) {
            removeArmorMod(player, MOD_ID); return;
        }
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int dexMod = mod((int) vars.Dexterity);
        int bonus = switch (vars.PlayerClass.toLowerCase()) {
            case "barbarian" -> dexMod + mod((int) vars.Constitution);
            case "monk"      -> dexMod + mod((int) vars.Wisdom);
            default          -> dexMod;
        };
        applyArmorMod(player, MOD_ID, Math.max(0, bonus));
    }

    private static void handleFastMovement(ServerPlayer player) {
        final String MOD_ID = "dnd:speed_10ft_fast_movement";
        if (!AbilityUtils.hasAbility(player, Ability.FAST_MOVEMENT)) {
            removeSpeedMod(player, MOD_ID); return;
        }
        if (isWearingTagged(player, TAG_HEAVY_ARMOR)) removeSpeedMod(player, MOD_ID);
        else applySpeedMod(player, MOD_ID, 0.030);
    }

    private static void handleUnarmoredMovement(ServerPlayer player) {
        final String MOD_ID = "dnd:speed_unarmored_movement";
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_MOVEMENT)) {
            removeSpeedMod(player, MOD_ID); return;
        }
        if (isWearingArmor(player)) { removeSpeedMod(player, MOD_ID); return; }
        int level = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;
        double bonus = level >= 18 ? 0.090 : level >= 14 ? 0.075
                     : level >= 10 ? 0.060 : level >= 6  ? 0.045 : 0.030;
        applySpeedMod(player, MOD_ID, bonus);
    }

    /**
     * Writes ToughBonus into AbilityData so applyAttrs can include it in max HP.
     * Only calls applyAttrs when the value actually changes (avoids infinite loops).
     */
    private static void handleDwarvenToughness(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.DWARVEN_TOUGHNESS)) return;
        var vars    = player.getData(DndModVariables.PLAYER_VARIABLES);
        int expected = (int) vars.PlayerLevel;
        int current  = AbilityDataUtils.getInt(vars, "ToughBonus", 0);
        if (current != expected) {
            AbilityDataUtils.set(vars, "ToughBonus", expected);
            vars.markSyncDirty();
            net.luderspieler.dnd.character.network.CharacterCreationPacket.applyAttrs(player);
        }
    }

    private static void applyArmorMod(ServerPlayer player, String idStr, double value) {
        var inst = player.getAttribute(Attributes.ARMOR);
        if (inst == null) return;
        ResourceLocation id = ResourceLocation.parse(idStr);
        inst.removeModifier(id);
        if (value != 0) inst.addPermanentModifier(
                new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
    }

    private static void removeArmorMod(ServerPlayer player, String idStr) {
        var inst = player.getAttribute(Attributes.ARMOR);
        if (inst != null) inst.removeModifier(ResourceLocation.parse(idStr));
    }

    private static void applySpeedMod(ServerPlayer player, String idStr, double value) {
        var inst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst == null) return;
        ResourceLocation id = ResourceLocation.parse(idStr);
        inst.removeModifier(id);
        if (value != 0) inst.addPermanentModifier(
                new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
    }

    private static void removeSpeedMod(ServerPlayer player, String idStr) {
        var inst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst != null) inst.removeModifier(ResourceLocation.parse(idStr));
    }

    private static boolean isWearingArmor(ServerPlayer player) {
        for (int i = 36; i <= 39; i++)
            if (!player.getInventory().getItem(i).isEmpty()) return true;
        return false;
    }

    private static boolean isWearingTagged(ServerPlayer player, TagKey<Item> tag) {
        for (int i = 36; i <= 39; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.isEmpty() && s.is(tag)) return true;
        }
        return false;
    }

    private static int mod(int score) { return Math.floorDiv(score - 10, 2); }
}
