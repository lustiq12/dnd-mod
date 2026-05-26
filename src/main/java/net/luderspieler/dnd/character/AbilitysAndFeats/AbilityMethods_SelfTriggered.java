package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Event-based ability effects that trigger automatically in response to game events.
 * Register this class in DndMod constructor:
 *   NeoForge.EVENT_BUS.register(new AbilityMethods_SelfTriggered());
 *
 * Note: Last-attacker / last-attacked tracking is NOT implemented here yet.
 * Abilities that require it (e.g. Reckless Attack, Radiant Strikes targeting)
 * are stubs marked with TODO.
 */
public class AbilityMethods_SelfTriggered {

    // ── INCOMING DAMAGE (before it's applied) ────────────────────────

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        handleUncannDodge(player, event);
    }

    /**
     * UNCANNY DODGE — halve incoming damage as a Reaction (auto-triggered here).
     * Rogue level 5 / Monk level 7 (Evasion handles saves, this handles direct hits).
     * Simplified: applies once per hit automatically (proper Reaction system TODO).
     */
    private void handleUncannDodge(ServerPlayer player, LivingIncomingDamageEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.UNCANNY_DODGE)) return;

        // Only trigger if we can see the attacker (source has direct entity)
        if (event.getSource().getDirectEntity() == null) return;

        // Halve the damage
        event.setAmount(event.getAmount() / 2.0f);
    }

    // ── DAMAGE TAKEN (after it's applied) ────────────────────────────

    @SubscribeEvent
    public void onDamageTaken(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        handleSnackAttackProc(player, event.getSource(), event.getNewDamage());
    }

    /**
     * Placeholder for effects that proc after taking damage.
     * E.g. Danger Sense (advantage on DEX saves) can't be implemented
     * without a custom save system — tracked via ability presence.
     */
    private void handleSnackAttackProc(ServerPlayer player, DamageSource source, float damage) {
        // TODO: Storm's Thunder-like retaliation for non-Goliath abilities
        // Currently Goliath reactions are in their own procedure classes.
    }

    // ── LIVING DEATH (player would die) ──────────────────────────────

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (handleRelentlessRage(player, event)) return;
        handleRelentlessEndurance(player, event);
    }

    /**
     * RELENTLESS RAGE (Barbarian lvl 11) — CON save to drop to 1 HP instead of dying while raging.
     * Simplified: if player has RELENTLESS_RAGE and their HP would reach 0, drop to 1.
     * Proper implementation needs a Rage-active tracker (TODO when ability wheel is done).
     */
    private boolean handleRelentlessRage(ServerPlayer player, LivingDeathEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.RELENTLESS_RAGE)) return false;

        // TODO: check if player is currently Raging once ability wheel tracks that state.
        // For now: unconditional save at cost of one use (tracked via var).
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Use RelentlessRageUses variable — add "RelentlessRageUses" to DndModVariables if needed.
        // Skipping use-tracking until variables are added.
        // Placeholder: always triggers (will need to be restricted later).
        event.setCanceled(true);
        player.setHealth(1.0f);
        return true;
    }

    /**
     * RELENTLESS ENDURANCE (Orc) — drop to 1 HP instead of 0, once per long rest.
     * Checks a flag stored in vars.Feats as a workaround until a dedicated variable is added.
     */
    private void handleRelentlessEndurance(ServerPlayer player, LivingDeathEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.RELENTLESS_ENDURANCE)) return;

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String usedFlag = "RelentlessEndurance_used";

        // Check if already used this long rest
        if (vars.Feats != null && vars.Feats.contains(usedFlag)) return;

        event.setCanceled(true);
        player.setHealth(1.0f);

        // Mark as used (cleared in SleepingIntereferer on long rest)
        vars.Feats = (vars.Feats == null || vars.Feats.isBlank())
                ? usedFlag : vars.Feats + "," + usedFlag;
        vars.markSyncDirty();
    }

    // ── OUTGOING ATTACK HIT ───────────────────────────────────────────
    // Sneak Attack, Brutal Strike, Radiant Strikes etc. need the attack event.
    // These are stubs — proper implementation requires tracking last-hit target.

    /**
     * Call this from wherever attack logic resolves (e.g. a future AttackEvent handler).
     * Place the call in the attack resolution code once that system exists.
     *
     * @param attacker   the player who attacked
     * @param target     the entity that was hit
     * @param baseDamage damage before any bonus
     * @return additional damage to add
     */
    public static float onHit(ServerPlayer attacker, LivingEntity target, float baseDamage) {
        float bonus = 0;

        // RADIANT STRIKES (Paladin lvl 11): +1d8 Radiant on melee hit
        if (AbilityUtils.hasAbility(attacker, Ability.RADIANT_STRIKES)) {
            bonus += 1 + attacker.getRandom().nextInt(8); // 1d8
            // TODO: apply as Radiant damage type once custom damage types exist
        }

        // SNEAK ATTACK (Rogue): handled externally via CastSpellProcedure equivalent.
        // Tracked here for completeness but implementation is in combat system.

        return bonus;
    }
}
