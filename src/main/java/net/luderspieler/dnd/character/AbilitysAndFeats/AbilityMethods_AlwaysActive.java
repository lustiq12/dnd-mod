package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
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
 * Passive effects checked and applied on a regular tick interval.
 * Called from AbilityPassiveTriggers.onPlayerTick() every INTERVAL ticks.
 *
 * One method per logical group. Add new always-active abilities here.
 */
public class AbilityMethods_AlwaysActive {

    /** Ticks between passive checks (~1 second). */
    public static final int INTERVAL = 20;

    /** Ticks between Night Vision reapplication (every 30 seconds). */
    private static final int DARKVISION_RENEW_INTERVAL = 600;

    private static final TagKey<Item> TAG_ANY_ARMOR = TagKey.create(Registries.ITEM,
            ResourceLocation.parse("dnd:any_armor"));

    // ── ENTRY POINT ───────────────────────────────────────────────────

    /** Called every INTERVAL ticks for each server-side player. */
    public static void tick(ServerPlayer player) {
        int tick = player.tickCount;

        handleDarkvision(player, tick);
        handleUnarmoredDefense(player);
        handleFastMovement(player);
        handleUnarmoredMovement(player);
        handleDwarvenToughness(player);
    }

    // ── DARKVISION ────────────────────────────────────────────────────

    /**
     * Reapplies Night Vision for players with Darkvision.
     * Uses a long duration so it never visibly expires.
     * Icon and particles are hidden to avoid UI clutter.
     */
    private static void handleDarkvision(ServerPlayer player, int tick) {
        if (tick % DARKVISION_RENEW_INTERVAL != 0) return;

        boolean has60  = AbilityUtils.hasAbility(player, Ability.DARKVISION_60);
        boolean has120 = AbilityUtils.hasAbility(player, Ability.DARKVISION_120);

        if (has60 || has120) {
            // Duration 40 000 ticks (~33 min); renewed every 30 s to prevent expiry flash.
            // Note: MC Night Vision doesn't distinguish 60ft/120ft range natively.
            // Range difference should be tracked elsewhere (e.g. for targeting range).
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    40_000, 0,
                    false, false, false   // ambient=false, particles=false, icon=false
            ));
        }
    }

    // ── UNARMORED DEFENSE ─────────────────────────────────────────────

    /**
     * Grants an armor attribute bonus when the player is not wearing armor.
     *
     * Barbarian: 10 + DEX mod + CON mod  →  bonus = DEX mod + CON mod
     * Monk:      10 + DEX mod + WIS mod  →  bonus = DEX mod + WIS mod
     *
     * Since Minecraft's base AC is 0, we add the mods directly as armor.
     * The "10" floor is already built into MC's damage formula baseline.
     */
    private static void handleUnarmoredDefense(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_DEFENSE)) {
            removeArmorMod(player, "dnd:unarmored_defense");
            return;
        }

        boolean wearingArmor = isWearingArmor(player);
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        if (wearingArmor) {
            removeArmorMod(player, "dnd:unarmored_defense");
            return;
        }

        int dexMod = modifier((int) vars.Dexterity);
        int bonus;

        switch (vars.PlayerClass.toLowerCase()) {
            case "barbarian" -> bonus = dexMod + modifier((int) vars.Constitution);
            case "monk"      -> bonus = dexMod + modifier((int) vars.Wisdom);
            default          -> bonus = dexMod; // fallback
        }

        applyArmorMod(player, "dnd:unarmored_defense", Math.max(0, bonus));
    }

    // ── FAST MOVEMENT (Barbarian lvl 5) ──────────────────────────────

    /** +10ft speed (~0.03 MC units) when not wearing heavy armor. */
    private static void handleFastMovement(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.FAST_MOVEMENT)) {
            removeSpeedMod(player, "dnd:fast_movement");
            return;
        }
        boolean wearingHeavy = isWearingTaggedArmor(player,
                TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:heavy_armor")));
        if (wearingHeavy) {
            removeSpeedMod(player, "dnd:fast_movement");
        } else {
            applySpeedMod(player, "dnd:fast_movement", 0.03);
        }
    }

    // ── UNARMORED MOVEMENT (Monk lvl 2) ──────────────────────────────

    /** +10ft speed when not wearing armor or shield. Scales at higher levels. */
    private static void handleUnarmoredMovement(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_MOVEMENT)) {
            removeSpeedMod(player, "dnd:unarmored_movement");
            return;
        }
        if (isWearingArmor(player)) {
            removeSpeedMod(player, "dnd:unarmored_movement");
            return;
        }
        // Bonus scales: +10 at lvl2, +15 at lvl6, +20 at lvl10, +25 at lvl14, +30 at lvl18
        int level = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;
        double bonus = level >= 18 ? 0.09
                : level >= 14 ? 0.075
                  : level >= 10 ? 0.06
                    : level >= 6  ? 0.045
                      : 0.03;
        applySpeedMod(player, "dnd:unarmored_movement", bonus);
    }

    // ── DWARVEN TOUGHNESS ─────────────────────────────────────────────

    /**
     * Adds +1 HP per character level to vars.ToughBonus (if not already accounted for).
     * The actual HP is recalculated by applyAttrs which reads ToughBonus.
     * We store the expected value and only call applyAttrs if it changed.
     */
    private static void handleDwarvenToughness(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.DWARVEN_TOUGHNESS)) return;

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int expected = level; // +1 HP per level

        if (vars.ToughBonus < expected) {
            vars.ToughBonus = expected;
            vars.markSyncDirty();
            net.luderspieler.dnd.character.network.CharacterCreationPacket.applyAttrs(player);
        }
    }

    // ── ATTRIBUTE HELPERS ─────────────────────────────────────────────

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
        for (int i = 36; i <= 39; i++) {
            if (!player.getInventory().getItem(i).isEmpty()) return true;
        }
        return false;
    }

    private static boolean isWearingTaggedArmor(ServerPlayer player, TagKey<Item> tag) {
        for (int i = 36; i <= 39; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(tag)) return true;
        }
        return false;
    }

    private static int modifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }
}