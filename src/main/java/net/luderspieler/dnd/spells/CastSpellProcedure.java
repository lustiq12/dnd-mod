package net.luderspieler.dnd.spells;

import net.minecraft.server.level.ServerPlayer;

/**
 * Entry point for all spell effects.
 * Called by CastSpellPacket after validation (slot consumed, spell prepared).
 *
 * Add your spell implementations in the switch below.
 * spellId matches the enum/string IDs used in PreparedSpells variables,
 * e.g. "FIRE_BOLT", "MAGIC_MISSILE", "CURE_WOUNDS".
 *
 * level = 0 for cantrips, 1-9 for leveled spells.
 */
public class CastSpellProcedure {

    public static void execute(ServerPlayer player, String spellId, int level) {
        if (spellId == null || spellId.isBlank()) return;

        switch (spellId.toUpperCase()) {

            // ── CANTRIPS (level 0) ──────────────────────────────
            case "FIRE_BOLT" -> castFireBolt(player);
            case "MAGE_HAND" -> castMageHand(player);
            case "LIGHT"     -> castLight(player);
            case "PRESTIDIGITATION" -> castPrestidigitation(player);

            // ── LEVEL 1 ─────────────────────────────────────────
            case "MAGIC_MISSILE"  -> castMagicMissile(player, level);
            case "CURE_WOUNDS"    -> castCureWounds(player, level);
            case "BURNING_HANDS"  -> castBurningHands(player, level);
            case "SHIELD"         -> castShield(player);
            case "THUNDERWAVE"    -> castThunderwave(player, level);

            // ── LEVEL 2 ─────────────────────────────────────────
            case "MISTY_STEP"     -> castMistyStep(player);
            case "SCORCHING_RAY"  -> castScorchingRay(player, level);
            case "HOLD_PERSON"    -> castHoldPerson(player, level);

            // ── LEVEL 3 ─────────────────────────────────────────
            case "FIREBALL"       -> castFireball(player, level);
            case "LIGHTNING_BOLT" -> castLightningBolt(player, level);
            case "COUNTERSPELL"   -> castCounterspell(player, level);

            // ── LEVEL 4 ─────────────────────────────────────────
            case "BANISHMENT"     -> castBanishment(player, level);
            case "ICE_STORM"      -> castIceStorm(player, level);

            // ── LEVEL 5 ─────────────────────────────────────────
            case "CONE_OF_COLD"   -> castConeOfCold(player, level);
            case "HOLD_MONSTER"   -> castHoldMonster(player, level);

            // Add more cases as you implement spells

            default -> player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "[DnD] Unknown spell: " + spellId));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  SPELL IMPLEMENTATIONS
    //  Replace the stub bodies below with your actual spell logic.
    //  Use MCreator procedures, particle effects, entity damage etc.
    // ════════════════════════════════════════════════════════════

    // ── Cantrips ─────────────────────────────────────────────────

    private static void castFireBolt(ServerPlayer player) {
        // TODO: fire projectile at crosshair target
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Fire Bolt!"));
    }

    private static void castMageHand(ServerPlayer player) {
        // TODO: spawn mage hand entity
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Mage Hand!"));
    }

    private static void castLight(ServerPlayer player) {
        // TODO: apply glowing effect or place light source
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Light!"));
    }

    private static void castPrestidigitation(ServerPlayer player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Prestidigitation!"));
    }

    // ── Level 1 ──────────────────────────────────────────────────

    private static void castMagicMissile(ServerPlayer player, int level) {
        // TODO: fire (2+level) guaranteed-hit projectiles
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Magic Missile (lvl " + level + ")!"));
    }

    private static void castCureWounds(ServerPlayer player, int level) {
        // Heal: 1d8 + modifier per level (simplified: 6 + 2*level)
        float heal = 6 + 2 * level;
        player.heal(heal);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Cure Wounds! Healed " + (int)heal + " HP."));
    }

    private static void castBurningHands(ServerPlayer player, int level) {
        // TODO: AoE fire cone in front of player
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Burning Hands (lvl " + level + ")!"));
    }

    private static void castShield(ServerPlayer player) {
        // TODO: apply temporary absorption/armor effect
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Shield!"));
    }

    private static void castThunderwave(ServerPlayer player, int level) {
        // TODO: knockback + thunder damage in 15ft cube
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Thunderwave (lvl " + level + ")!"));
    }

    // ── Level 2 ──────────────────────────────────────────────────

    private static void castMistyStep(ServerPlayer player) {
        // TODO: short-range teleport to crosshair
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Misty Step!"));
    }

    private static void castScorchingRay(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Scorching Ray (lvl " + level + ")!"));
    }

    private static void castHoldPerson(ServerPlayer player, int level) {
        // TODO: apply freeze/slowness effect to target entity
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Hold Person (lvl " + level + ")!"));
    }

    // ── Level 3 ──────────────────────────────────────────────────

    private static void castFireball(ServerPlayer player, int level) {
        // TODO: explosion + fire damage at target point
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Fireball (lvl " + level + ")!"));
    }

    private static void castLightningBolt(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Lightning Bolt (lvl " + level + ")!"));
    }

    private static void castCounterspell(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Counterspell (lvl " + level + ")!"));
    }

    // ── Level 4 ──────────────────────────────────────────────────

    private static void castBanishment(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Banishment (lvl " + level + ")!"));
    }

    private static void castIceStorm(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Ice Storm (lvl " + level + ")!"));
    }

    // ── Level 5 ──────────────────────────────────────────────────

    private static void castConeOfCold(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Cone of Cold (lvl " + level + ")!"));
    }

    private static void castHoldMonster(ServerPlayer player, int level) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Hold Monster (lvl " + level + ")!"));
    }
}