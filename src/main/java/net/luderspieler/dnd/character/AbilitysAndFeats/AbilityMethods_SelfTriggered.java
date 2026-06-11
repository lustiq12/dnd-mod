package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Automatische Ability-Effekte die auf Game-Events reagieren.
 * Registriert in DndMod: NeoForge.EVENT_BUS.register(new AbilityMethods_SelfTriggered());
 *
 * ── INITIATIVE-BASIERTE ABILITIES ─────────────────────────────────────────────
 * Minecraft hat kein Initiative-System. Der Workaround:
 *   DndMod.onInitiative(player) aufrufen wenn Kampf beginnt.
 * Das kann z.B. bei erstem Treffer oder aus einer Keybinding getriggert werden.
 * Betrifft: FERAL_INSTINCT, INSTINCTIVE_POUNCE, SUPERIOR_INSPIRATION, PERFECT_FOCUS,
 *           UNCANNY_METABOLISM (ist PLAYER_TRIGGERED, Spieler entscheidet selbst).
 * ──────────────────────────────────────────────────────────────────────────────
 */
public class AbilityMethods_SelfTriggered {

    // ══════════════════════════════════════════════════════════════════
    //  INITIATIVE  (manuell aufrufbar, kein direktes MC-Event)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Wird aufgerufen wenn der Spieler Initiative würfelt / Kampf beginnt.
     * Aufruf-Punkte (Beispiele):
     *   - Erster Treffer gegen / von einem Mob
     *   - Keybinding "Roll Initiative"
     *   - /dnd rollinitiative <player>
     */
    public static void onInitiative(ServerPlayer player) {
        if (!player.getData(DndModVariables.PLAYER_VARIABLES).FinishedCharacterCreation) return;

        handleFeralInstinct(player);
        handleSuperiorInspiration(player);
        handlePerfectFocus(player);
        // UNCANNY_METABOLISM ist PLAYER_TRIGGERED — Spieler entscheidet selbst via Wheel
        // INSTINCTIVE_POUNCE: Bewegung als Teil der Initiative — TODO wenn Bewegungssystem steht
    }

    /**
     * FERAL_INSTINCT (Barbarian 7) — Bei Initiative: 1 Rage-Ladung zurückgewinnen.
     * "When you roll Initiative, you regain one expended use of Rage." (kein "you can")
     */
    private static void handleFeralInstinct(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.FERAL_INSTINCT)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int level = (int) vars.PlayerLevel;
        int maxRage = level >= 17 ? 6 : level >= 12 ? 5 : level >= 6 ? 4 : level >= 3 ? 3 : 2;
        int current = AbilityDataUtils.getInt(vars, "RAGE_uses", maxRage);
        if (current < maxRage) {
            AbilityDataUtils.set(vars, "RAGE_uses", current + 1);
            vars.markSyncDirty();
        }
    }

    /**
     * SUPERIOR_INSPIRATION (Bard 18) — Bei Initiative: Wenn keine BI-Ladungen,
     * automatisch 1 zurückgewinnen.
     */
    private static void handleSuperiorInspiration(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.SUPERIOR_INSPIRATION)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int maxBI = Math.max(1, Math.floorDiv((int) vars.Charisma - 10, 2));
        int current = AbilityDataUtils.getInt(vars, "BARDIC_INSPIRATION_uses", maxBI);
        if (current <= 0) {
            AbilityDataUtils.set(vars, "BARDIC_INSPIRATION_uses", 1);
            vars.markSyncDirty();
        }
    }

    /**
     * PERFECT_FOCUS (Monk 15) — Bei Initiative: Wenn keine Focus Points,
     * automatisch 4 zurückgewinnen.
     * "When you roll Initiative and have no Focus Points remaining, you regain 4 Focus Points."
     */
    private static void handlePerfectFocus(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.PERFECT_FOCUS)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int current = AbilityDataUtils.getInt(vars, "FOCUS_POINTS_remaining", (int) vars.PlayerLevel);
        if (current <= 0) {
            AbilityDataUtils.set(vars, "FOCUS_POINTS_remaining", 4);
            vars.markSyncDirty();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  INCOMING DAMAGE (vor Anwendung)
    // ══════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        handleUncannyDodge(player, event);
        handleEvasion(player, event);
        handleLucky(player, event);
        handleStonesEnduranceProc(player, event);
    }

    /**
     * UNCANNY_DODGE (Rogue 5) — Reaction: Schaden von sichtbarem Angreifer halbieren.
     * Halbiert automatisch wenn ein direkter Angreifer vorhanden ist.
     */
    private void handleUncannyDodge(ServerPlayer player, LivingIncomingDamageEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.UNCANNY_DODGE)) return;
        if (event.getSource().getDirectEntity() == null) return;
        // Nur wenn der Spieler den Angreifer "sehen" kann (kein Blindness-Effekt)
        if (player.hasEffect(MobEffects.BLINDNESS)) return;
        event.setAmount(event.getAmount() / 2.0f);
    }

    /**
     * EVASION (Monk 7 / Rogue 7) — Schaden durch Flächenzauber halbieren / auf 0 setzen.
     * DEX-Save-Effekte: Explosionen, Projektile ohne direkten Treffer.
     * Annäherung: Schaden durch Explosion wird auf 0 gesetzt (Save bestanden),
     * anderer AoE-Schaden wird halbiert (Save misslungen).
     */
    private void handleEvasion(ServerPlayer player, LivingIncomingDamageEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.EVASION)) return;
        DamageSource source = event.getSource();
        // Kein direkter Angreifer = AoE / indirekter Schaden → DEX-Save würde greifen
        if (source.getDirectEntity() != null) return;
        boolean explosion = source.is(net.minecraft.world.damagesource.DamageTypes.EXPLOSION)
                || source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_EXPLOSION);
        if (explosion) {
            // Save bestanden: kein Schaden
            event.setAmount(0.0f);
        } else if (source.is(net.minecraft.world.damagesource.DamageTypes.MAGIC)
                || source.is(net.minecraft.world.damagesource.DamageTypes.INDIRECT_MAGIC)) {
            // Save misslungen (pessimistisch): halber Schaden
            event.setAmount(event.getAmount() / 2.0f);
        }
    }

    /**
     * LUCKY (Halfling) — Wenn Schaden von einem kritischen Treffer kommt
     * (Annäherung für "Würfel zeigt 1"): Schaden halbieren.
     * TODO: Auf Angriffswürfe hoochen wenn NeoForge CriticalHitEvent nutzbar ist.
     */
    private void handleLucky(ServerPlayer player, LivingIncomingDamageEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.LUCKY)) return;
        // Echter Hook auf "Nat 1" nicht möglich ohne eigenes Würfelsystem.
        // Annäherung: Kritischer-Treffer-Schaden halbieren (der Halfling würfelt neu).
        // TODO: mit echtem Würfelsystem verknüpfen wenn verfügbar
    }

    /**
     * STONES_ENDURANCE (Goliath) — Reaction: Vorbereiteteten Schadensreduktions-Wert anwenden.
     * Der Wert wird in activateStonesEndurance() berechnet und in AbilityData gespeichert.
     */
    private void handleStonesEnduranceProc(ServerPlayer player, LivingIncomingDamageEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.STONES_ENDURANCE)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int reduction = AbilityDataUtils.getInt(vars, "STONES_ENDURANCE_reduction", 0);
        if (reduction <= 0) return;
        event.setAmount(Math.max(0, event.getAmount() - reduction));
        AbilityDataUtils.set(vars, "STONES_ENDURANCE_reduction", 0);
        vars.markSyncDirty();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DAMAGE TAKEN (nach Anwendung)
    // ══════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public void onDamageTaken(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        handleStormsThunderRetaliation(player, event);
    }

    /**
     * STORMS_THUNDER (Goliath Storm) — Reaction nach Treffer:
     * Angreifer nimmt 1d8 Donner-Schaden.
     */
    private void handleStormsThunderRetaliation(ServerPlayer player, LivingDamageEvent.Post event) {
        if (!AbilityUtils.hasAbility(player, Ability.STORMS_THUNDER)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!AbilityDataUtils.getBool(vars, "STORMS_THUNDER_readied")) return;
        AbilityDataUtils.set(vars, "STORMS_THUNDER_readied", false);
        vars.markSyncDirty();
        LivingEntity attacker = null;
        if (event.getSource().getEntity() instanceof LivingEntity le) attacker = le;
        if (attacker == null) return;
        int damage = 1 + player.getRandom().nextInt(8);
        attacker.hurt(player.damageSources().playerAttack(player), damage);
        if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                    attacker.getX(), attacker.getY() + 1, attacker.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  LIVING DEATH
    // ══════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (handleRelentlessRage(player, event)) return;
        handleRelentlessEndurance(player, event);
    }

    /**
     * RELENTLESS_RAGE (Barbarian 11) — CON-Save: auf 1 HP fallen statt zu sterben
     * während Rage aktiv ist.
     * Vereinfacht: greift wenn RAGE_active = true.
     */
    private boolean handleRelentlessRage(ServerPlayer player, LivingDeathEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.RELENTLESS_RAGE)) return false;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!AbilityDataUtils.getBool(vars, "RAGE_active")) return false;
        // CON-Save (vereinfacht: immer erfolgreiche Rettung wenn nicht bereits benutzt)
        String usedKey = "RELENTLESS_RAGE_used_this_rage";
        if (AbilityDataUtils.getBool(vars, usedKey)) return false;
        event.setCanceled(true);
        player.setHealth(1.0f);
        AbilityDataUtils.set(vars, usedKey, true);
        vars.markSyncDirty();
        return true;
    }

    /**
     * RELENTLESS_ENDURANCE (Orc) — Auf 1 HP fallen statt 0, 1× pro Long Rest.
     */
    private void handleRelentlessEndurance(ServerPlayer player, LivingDeathEvent event) {
        if (!AbilityUtils.hasAbility(player, Ability.RELENTLESS_ENDURANCE)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String usedFlag = "RelentlessEndurance_used";
        if (vars.Feats != null && vars.Feats.contains(usedFlag)) return;
        event.setCanceled(true);
        player.setHealth(1.0f);
        vars.Feats = (vars.Feats == null || vars.Feats.isBlank())
                ? usedFlag : vars.Feats + "," + usedFlag;
        vars.markSyncDirty();
    }

    // ══════════════════════════════════════════════════════════════════
    //  PLAYER TICK — für zeitabhängige Self-Triggered-Effekte
    // ══════════════════════════════════════════════════════════════════

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!player.getData(DndModVariables.PLAYER_VARIABLES).FinishedCharacterCreation) return;

        // Alle 20 Ticks (1 Sek.) prüfen — für Auren und kontinuierliche Effekte
        if (player.tickCount % 20 == 0) {
            handleAuraOfProtection(player);
            handleAuraOfCourage(player);
        }

        // Jedes Tick: TACTICAL_SHIFT nach Action Surge
        handleTacticalShift(player);
    }

    /**
     * AURA_OF_PROTECTION (Paladin 6) — Nahe Verbündete erhalten +CHA auf Saving Throws.
     * In Minecraft: Nahestehende Spieler erhalten kurz einen Resistance-Effekt als Annäherung.
     * TODO: Eigenes Save-System für korrekte Implementierung.
     */
    private void handleAuraOfProtection(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.AURA_OF_PROTECTION)) return;
        // TODO: Alle ServerPlayer im Radius von 10 Blöcken mit CHA-Bonus auf ihre nächsten
        // Saving Throws stärken. Erfordert eigenes Save-System.
    }

    /**
     * AURA_OF_COURAGE (Paladin 10) — Nahe Verbündete können nicht frightened werden.
     * TODO: Frightened-Effekt von DndModMobEffects entfernen wenn aktiv.
     */
    private void handleAuraOfCourage(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.AURA_OF_COURAGE)) return;
        // TODO: Nahe Spieler/Entities: CHARMED-Mob-Effekt entfernen wenn durch Charmed.
    }

    /**
     * TACTICAL_SHIFT (Fighter 5) — Wenn Action Surge aktiv: Spieler bewegt sich
     * ohne Opportunity Attacks. In Minecraft (keine OAs): Speed-Boost als Annäherung.
     */
    private void handleTacticalShift(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.TACTICAL_SHIFT)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!AbilityDataUtils.getBool(vars, "ACTION_SURGE_active")) return;
        // Speed-Boost für kurze Zeit (keine OAs in MC — reines Flavour-Feature hier)
        player.addEffect(new MobEffectInstance(
                MobEffects.SPEED, 5, 0, false, false, false));
    }

    // ══════════════════════════════════════════════════════════════════
    //  OUTGOING HIT — statische Utility-Methode
    //  Aufruf aus dem Angriffs-Event wenn das Kampfsystem steht.
    // ══════════════════════════════════════════════════════════════════

    /**
     * Berechnet zusätzlichen Schaden aus SELF_TRIGGERED On-Hit-Abilities.
     * Aufzurufen wenn ein Spieler-Angriff trifft.
     *
     * @param attacker   angreifender Spieler
     * @param target     getroffene Entität
     * @param baseDamage Schaden vor Boni
     * @return zusätzlicher Schaden
     */
    public static float onHit(ServerPlayer attacker, LivingEntity target, float baseDamage) {
        float bonus = 0;

        // RADIANT_STRIKES (Paladin 11) — +1d8 Radiant bei jedem Nahkampftreffer
        if (AbilityUtils.hasAbility(attacker, Ability.RADIANT_STRIKES)) {
            bonus += 1 + attacker.getRandom().nextInt(8);
            // TODO: Als Radiant-Schadenstyp sobald custom damage types existieren
        }

        // ELEMENTAL_FURY (Druid 7) — +1d6 Elementar beim ersten Treffer pro Runde
        if (AbilityUtils.hasAbility(attacker, Ability.ELEMENTAL_FURY)) {
            bonus += handleElementalFury(attacker);
        }

        // BLESSED_STRIKES (Cleric 7) — +1d8 Radiant/Nekrotik beim ersten Treffer pro Runde
        if (AbilityUtils.hasAbility(attacker, Ability.BLESSED_STRIKES)) {
            bonus += handleBlessedStrikes(attacker);
        }

        // FIRES_BURN (Goliath Fire) — readied: +1d10 Feuer-Schaden auf nächsten Treffer
        if (AbilityUtils.hasAbility(attacker, Ability.FIRES_BURN)) {
            var vars = attacker.getData(DndModVariables.PLAYER_VARIABLES);
            if (AbilityDataUtils.getBool(vars, "FIRES_BURN_readied")) {
                bonus += 1 + attacker.getRandom().nextInt(10);
                AbilityDataUtils.set(vars, "FIRES_BURN_readied", false);
                vars.markSyncDirty();
                target.setRemainingFireTicks(40);
            }
        }

        // FROSTS_CHILL (Goliath Frost) — readied: Verlangsamung auf Ziel
        if (AbilityUtils.hasAbility(attacker, Ability.FROSTS_CHILL)) {
            var vars = attacker.getData(DndModVariables.PLAYER_VARIABLES);
            if (AbilityDataUtils.getBool(vars, "FROSTS_CHILL_readied")) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.SLOWNESS, 40, 1, false, false));
                AbilityDataUtils.set(vars, "FROSTS_CHILL_readied", false);
                vars.markSyncDirty();
            }
        }

        // HILLS_TUMBLE (Goliath Hill) — readied: Ziel zu Boden werfen (Prone-Annäherung)
        if (AbilityUtils.hasAbility(attacker, Ability.HILLS_TUMBLE)) {
            var vars = attacker.getData(DndModVariables.PLAYER_VARIABLES);
            if (AbilityDataUtils.getBool(vars, "HILLS_TUMBLE_readied")) {
                // Prone-Annäherung: Schwerkraft-Burst
                target.addEffect(new MobEffectInstance(
                        MobEffects.LEVITATION, 1, 255, false, false));
                AbilityDataUtils.set(vars, "HILLS_TUMBLE_readied", false);
                vars.markSyncDirty();
            }
        }

        // STUNNING_STRIKE (Monk 5) — readied: 1 Tick CON-Save → Stunned
        if (AbilityUtils.hasAbility(attacker, Ability.STUNNING_STRIKE)) {
            var vars = attacker.getData(DndModVariables.PLAYER_VARIABLES);
            if (AbilityDataUtils.getBool(vars, "STUNNING_STRIKE_readied")) {
                // CON-Save vereinfacht: immer Stunned (TODO Save-System)
                target.addEffect(new MobEffectInstance(
                        net.luderspieler.dnd.init.DndModMobEffects.STUNNED,
                        100, 0, false, false));
                AbilityDataUtils.set(vars, "STUNNING_STRIKE_readied", false);
                vars.markSyncDirty();
            }
        }

        return bonus;
    }

    // ── On-Hit Helpers ────────────────────────────────────────────────

    /**
     * ELEMENTAL_FURY — +1d6 Elementar auf ersten Treffer pro "Runde" (20 Ticks).
     * Verfolgt via ELEMENTAL_FURY_last_tick in AbilityData.
     */
    private static float handleElementalFury(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int lastHitTick = AbilityDataUtils.getInt(vars, "ELEMENTAL_FURY_last_tick", -100);
        int currentTick = player.tickCount;
        if (currentTick - lastHitTick < 20) return 0; // Schon in dieser "Runde" benutzt
        AbilityDataUtils.set(vars, "ELEMENTAL_FURY_last_tick", currentTick);
        vars.markSyncDirty();
        return 1 + player.getRandom().nextInt(6); // 1d6
    }

    /**
     * BLESSED_STRIKES — +1d8 Radiant/Nekrotik auf ersten Treffer pro "Runde" (20 Ticks).
     */
    private static float handleBlessedStrikes(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int lastHitTick = AbilityDataUtils.getInt(vars, "BLESSED_STRIKES_last_tick", -100);
        int currentTick = player.tickCount;
        if (currentTick - lastHitTick < 20) return 0;
        AbilityDataUtils.set(vars, "BLESSED_STRIKES_last_tick", currentTick);
        vars.markSyncDirty();
        return 1 + player.getRandom().nextInt(8); // 1d8
    }
}