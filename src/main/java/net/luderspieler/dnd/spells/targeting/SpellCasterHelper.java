package net.luderspieler.dnd.spells.targeting;

import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SpellCasterHelper {

    // ══════════════════════════════════════════════════════════════════
    //  METAMAGIC — Konsumierung beim Zauber-Start
    //
    //  Die aktiven Metamagic-Flags (gesetzt von AbilityMethods_PlayerTriggered
    //  .activateMetamagic()) werden hier gelesen und auf Range/Amount
    //  angewendet, sobald der Spieler den Zauber tatsächlich zu wirken
    //  beginnt (loadSpellForTargeting / startTargeting / startTargetingBlock).
    //  Das jeweilige Flag wird dabei sofort konsumiert (gelöscht), damit es
    //  nicht versehentlich auf den nächsten Zauber durchschlägt.
    // ══════════════════════════════════════════════════════════════════

    /** DISTANT_SPELL — Reichweite verdoppeln (oder Touch → 30ft, hier vereinfacht: ×2). */
    private static double applyDistantSpell(ServerPlayer player, double baseRange) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!AbilityDataUtils.getBool(vars, "DISTANT_SPELL_active")) return baseRange;
        AbilityDataUtils.set(vars, "DISTANT_SPELL_active", false);
        vars.markSyncDirty();
        player.displayClientMessage(Component.literal("§5Distant Spell: range doubled!"), true);
        return baseRange * 2.0;
    }

    /**
     * TWINNED_SPELL — erlaubt bei Einzelziel-Zaubern ein zweites Ziel.
     * Greift nur wenn der Basis-Amount exakt 1 ist (Twinned Spell gilt laut
     * 2024 PHB nur für Zauber die normalerweise genau ein Ziel haben).
     */
    private static int applyTwinnedSpell(ServerPlayer player, int baseAmount) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!AbilityDataUtils.getBool(vars, "TWINNED_SPELL_active")) return baseAmount;
        if (baseAmount != 1) return baseAmount; // gilt nicht für AoE/Multi-Target-Zauber

        AbilityDataUtils.set(vars, "TWINNED_SPELL_active", false);
        vars.markSyncDirty();
        player.displayClientMessage(Component.literal("§5Twinned Spell: second target allowed!"), true);
        return baseAmount + 1;
    }

    /**
     * Räumt alle noch aktiven Metamagic-Flags auf, die in diesem Cast nicht
     * konsumiert wurden. Betrifft aktuell Careful/Empowered/Extended/
     * Heightened/Quickened/Seeking/Subtle/Transmuted Spell — deren
     * mechanische Effekte (Auto-Save, Reroll, Komponenten-Verzicht, etc.)
     * mangels Saving-Throw-/Combat-System im Mod noch nicht implementiert
     * sind. Wird trotzdem konsequent aufgeräumt, damit kein Flag über den
     * gewirkten Zauber hinaus "hängen bleibt".
     *
     * TODO: Sobald ein Save-/Component-System existiert, hier echte Effekte
     * für die restlichen Optionen verdrahten statt nur das Flag zu löschen.
     *
     * Aufruf: CastSpellPacket.handle(), direkt nach CastSpellProcedure.execute().
     */
    public static void clearRemainingMetamagicFlags(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        boolean changed = false;
        for (String key : new String[]{
                "CAREFUL_SPELL_active", "DISTANT_SPELL_active", "EMPOWERED_SPELL_active",
                "EXTENDED_SPELL_active", "HEIGHTENED_SPELL_active", "QUICKENED_SPELL_active",
                "SEEKING_SPELL_active", "SUBTLE_SPELL_active", "TRANSMUTED_SPELL_active",
                "TWINNED_SPELL_active"
        }) {
            if (AbilityDataUtils.getBool(vars, key)) {
                AbilityDataUtils.set(vars, key, false);
                changed = true;
            }
        }
        if (changed) vars.markSyncDirty();
    }

    // ══════════════════════════════════════════════════════════════════
    //  TARGETING-EINSTIEGSPUNKTE (Metamagic wird hier angewendet)
    // ══════════════════════════════════════════════════════════════════

    public static void loadSpellForTargeting(ServerPlayer player, String spellName, double range) {
        range = applyDistantSpell(player, range);

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingModeType = "FREE_AIM";
        vars.TargetingRange = range;
        vars.TargetingSpell = spellName;
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }

    public static void startTargeting(ServerPlayer player, double range, int amount, String spellName) {
        range  = applyDistantSpell(player, range);
        amount = applyTwinnedSpell(player, amount);

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingModeType = "ENTITY";
        vars.TargetingRange = range;
        vars.TargetingAmount = (double) amount;
        vars.TargetingSpell = spellName;
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }

    public static void startTargetingBlock(ServerPlayer player, double range, String spellName) {
        range = applyDistantSpell(player, range);

        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        vars.TargetingMode = true;
        vars.TargetingModeType = "BLOCK";
        vars.TargetingRange = range;
        vars.TargetingSpell = spellName;
        vars.targetUUIDS = "";
        vars.markSyncDirty();
    }

    // Diese Methode brauchen wir für TargetingEvents
    public static void forceCastExistingTargets(ServerPlayer caster, Consumer<List<LivingEntity>> onComplete) {
        var vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        List<String> uuidList = Arrays.stream(vars.targetUUIDS.split(","))
                .filter(s -> !s.isEmpty())
                .toList();

        List<LivingEntity> targets = new ArrayList<>();
        for (String s : uuidList) {
            try {
                var e = ((ServerLevel)caster.level()).getEntity(UUID.fromString(s));
                if (e instanceof LivingEntity living) targets.add(living);
            } catch (Exception ignored) {}
        }
        onComplete.accept(targets);
    }
    public static void renderPreview(ServerPlayer caster, double range) {
        var vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        Vec3 eyePos = caster.getEyePosition();
        Vec3 lookVec = caster.getViewVector(1.0F);

        // 1. Block-Raycast für Distanzbegrenzung
        HitResult blockHit = caster.pick(range, 0.0f, false);
        double maxDist = range;
        Vec3 blockPos = null;
        boolean hitBlock = false;

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            maxDist = eyePos.distanceTo(blockHit.getLocation());
            blockPos = blockHit.getLocation();
            hitBlock = true;
        }

        // 2. Entity-Raycast
        Vec3 reachVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                caster, eyePos, reachVec, searchBox, (e) -> e instanceof LivingEntity, maxDist * maxDist
        );

        Vec3 targetPos = null;

        if ("FREE_AIM".equals(vars.TargetingModeType) || "BLOCK".equals(vars.TargetingModeType)) {
            if (entityHit != null) {
                targetPos = entityHit.getLocation();
            } else if (hitBlock) {
                targetPos = blockPos;
            }
        } else {
            // Nur Partikel wenn Entity getroffen
            if (entityHit != null) {
                targetPos = entityHit.getLocation();
            }
        }

        if (targetPos != null) {
            drawVisualTrail(caster, targetPos);
        }
    }

    public static void tryPickTarget(ServerPlayer caster, double range, int maxAmount, Consumer<List<LivingEntity>> onComplete) {
        DndModVariables.PlayerVariables vars = caster.getData(DndModVariables.PLAYER_VARIABLES);
        LivingEntity target = getLookingAtEntity(caster, range);

        List<String> uuidList = new ArrayList<>(Arrays.asList(vars.targetUUIDS.split(","))
                .stream().filter(s -> !s.isEmpty()).toList());

        if (target != null) {
            String uuid = target.getStringUUID();
            if (!uuidList.contains(uuid) && uuidList.size() < maxAmount) {
                uuidList.add(uuid);
                vars.targetUUIDS = String.join(",", uuidList);
                vars.markSyncDirty();

                // JETZT GLOWEN: Erst beim Auswählen
                sendGlowPacket(caster, target, true);

                caster.displayClientMessage(Component.literal("§6Target chosen! (" + uuidList.size() + "/" + maxAmount + ")"), true);
            }
        }

        if (uuidList.size() >= maxAmount && maxAmount > 0) {
            executeFinalCast(caster, vars, uuidList, onComplete);
        }
    }

    private static void executeFinalCast(ServerPlayer caster, DndModVariables.PlayerVariables vars, List<String> uuidStrings, Consumer<List<LivingEntity>> onComplete) {
        List<LivingEntity> targets = new ArrayList<>();
        for (String s : uuidStrings) {
            try {
                Entity e = ((ServerLevel)caster.level()).getEntity(UUID.fromString(s));
                if (e instanceof LivingEntity living) {
                    targets.add(living);
                    sendGlowPacket(caster, living, false);
                }
            } catch (Exception ignored) {}
        }
        vars.targetUUIDS = "";
        vars.markSyncDirty();
        onComplete.accept(targets);
    }

    private static LivingEntity getLookingAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        HitResult blockHit = player.pick(range, 0.0f, false);
        double maxDist = (blockHit.getType() == HitResult.Type.BLOCK) ? eyePos.distanceTo(blockHit.getLocation()) : range;

        Vec3 reachVec = eyePos.add(lookVec.scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);
        var hit = ProjectileUtil.getEntityHitResult(player, eyePos, reachVec, searchBox, (e) -> e instanceof LivingEntity, maxDist * maxDist);
        return (hit != null && hit.getEntity() instanceof LivingEntity living) ? living : null;
    }

    public static void resetTargeting(ServerPlayer player) {
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (!vars.targetUUIDS.isEmpty()) {
            for (String uuidStr : vars.targetUUIDS.split(",")) {
                try {
                    // Wir casten player.level() zu ServerLevel, um getEntity nutzen zu können
                    if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        net.minecraft.world.entity.Entity target = serverLevel.getEntity(java.util.UUID.fromString(uuidStr.trim()));
                        if (target != null) {
                            sendGlowPacket(player, target, false);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        // 2. Variablen zurücksetzen
        vars.TargetingMode = false;
        vars.TargetingModeType = "";
        vars.TargetingRange = 0;
        vars.TargetingSpell = "";
        vars.targetUUIDS = ""; // Hier umbenannt
        vars.markSyncDirty();
    }

    public static void sendGlowPacket(ServerPlayer caster, Entity target, boolean active) {
        byte flags = target.getEntityData().get(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0));
        byte newFlags = active ? (byte)(flags | 0x40) : (byte)(flags & ~0x40);
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(net.minecraft.network.syncher.EntityDataSerializers.BYTE.createAccessor(0), newFlags));
        caster.connection.send(new ClientboundSetEntityDataPacket(target.getId(), data));
    }

    private static void drawVisualTrail(ServerPlayer caster, Vec3 targetPos) {
        if (caster.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, targetPos.x, targetPos.y, targetPos.z, 50, 0.2, 0.2, 0.2, 0.05);
            level.sendParticles(ParticleTypes.ENCHANT, caster.getX(), caster.getY(), caster.getZ(), 5, 0.5, 0.3, 0.5, 0.05);
        }
    }
}