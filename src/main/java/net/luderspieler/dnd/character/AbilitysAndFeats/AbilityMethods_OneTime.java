package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

/**
 * Effects that fire exactly once when an ability is added to a player.
 * Called by AbilityUtils.addAbility() after storing the ability.
 *
 * Rules:
 *  - Only modify vars fields (Strength, Dexterity, etc.) here — never game-state.
 *  - Always call CharacterCreationPacket.applyAttrs() at the end if stats changed.
 *  - Do NOT add MobEffects here; use AbilityMethods_AlwaysActive for those.
 */
public class AbilityMethods_OneTime {

    /**
     * Dispatches to the correct one-time handler for the given ability.
     * Returns true if any stat change was made (caller should then call applyAttrs).
     */
    public static boolean execute(ServerPlayer player, Ability ability) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean statsChanged = false;

        switch (ability) {

            // ── BARBARIAN ───────────────────────────────────────────────
            case PRIMAL_CHAMPION -> {
                // +4 STR and +4 CON — can exceed 20
                vars.Strength     += 4;
                vars.Constitution += 4;
                statsChanged = true;
            }

            // ── MONK ────────────────────────────────────────────────────
            case BODY_AND_MIND -> {
                // +4 DEX and +4 WIS — can exceed 20
                vars.Dexterity += 4;
                vars.Wisdom    += 4;
                statsChanged = true;
            }

            // ── DRAGONBORN ──────────────────────────────────────────────
            case FLIGHT -> {
                // Unlock fly speed at level 5 via attribute
                applyFlySpeed(player, 0.1);  // ~equivalent to walking speed in MC units
            }

            // ── SPECIES SPEED ────────────────────────────────────────────
            case SPEED_BONUS_5 -> {
                // +5ft (≈ +0.015 in MC units, 1 MC block = 3.28ft)
                applySpeedMod(player, "dnd:speed_bonus_5", 0.015);
            }

            // ── AASIMAR CAPSTONE ─────────────────────────────────────────
            // Celestial Revelation is PLAYER_TRIGGERED, no one-time stat change.

            // ── PALADIN: Aura expansion at 18 ────────────────────────────
            // AURA_EXPANSION has no direct stat effect — just changes aura radius,
            // tracked via ability presence.

            default -> { /* no one-time effect */ }
        }

        if (statsChanged) {
            vars.markSyncDirty();
            CharacterCreationPacket.applyAttrs(player);
        }

        return statsChanged;
    }

    /**
     * Re-executes all one-time abilities on the player.
     * Called when a player respawns and keeps their character,
     * so entity-level effects (fly speed, attribute mods) are reapplied.
     * Does NOT re-add stat bonuses (they are preserved in vars).
     */
    public static void reapplyEntityEffects(ServerPlayer player) {
        var utils = net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils
                .getPlayerAbilities(player);

        for (Ability ability : utils) {
            switch (ability) {
                case FLIGHT      -> applyFlySpeed(player, 0.1);
                case SPEED_BONUS_5 -> applySpeedMod(player, "dnd:speed_bonus_5", 0.015);
                default -> { /* entity-only effects only */ }
            }
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────

    private static void applyFlySpeed(ServerPlayer player, double value) {
        var inst = player.getAttribute(Attributes.FLYING_SPEED);
        if (inst == null) return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("dnd", "ability_fly_speed");
        inst.removeModifier(id);
        inst.addPermanentModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
        player.setNoGravity(false); // gravity stays; player needs to press jump to fly
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    private static void applySpeedMod(ServerPlayer player, String idStr, double value) {
        var inst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst == null) return;
        ResourceLocation id = ResourceLocation.parse(idStr);
        inst.removeModifier(id);
        if (value != 0) {
            inst.addPermanentModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}