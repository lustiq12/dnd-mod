package net.luderspieler.dnd.character.AbilitysAndFeats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.Utils.AbilityDataUtils;
import net.luderspieler.dnd.Utils.AbilityUtils;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.init.DndModMobEffects;
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
 * Passive Effekte die im Tick-Interval ausgewertet werden.
 * Aufgerufen von AbilityPassiveTriggers.onPlayerTick() alle INTERVAL Ticks.
 */
public class AbilityMethods_AlwaysActive {

    public static final int INTERVAL            = 20;  // 1 Sekunde
    private static final int DARKVISION_REFRESH = 600; // 30 Sekunden

    private static final TagKey<Item> TAG_HEAVY_ARMOR = TagKey.create(
            Registries.ITEM, ResourceLocation.parse("dnd:heavy_armor"));

    // ── ENTRY POINT ───────────────────────────────────────────────────

    public static void tick(ServerPlayer player) {
        int tick = player.tickCount;

        // ── Sinne & Wahrnehmung ──────────────────────────────────────
        handleDarkvision(player, tick);

        // ── Defensive Passivs ─────────────────────────────────────────
        handleUnarmoredDefense(player);
        handleDwarvenToughness(player);
        handleFeyAncestry(player, tick);
        handleBrave(player, tick);

        // ── Bewegung & Geschwindigkeit ─────────────────────────────────
        handleFastMovement(player);
        handleUnarmoredMovement(player);
    }

    // ══════════════════════════════════════════════════════════════════
    //  SINNE & WAHRNEHMUNG
    // ══════════════════════════════════════════════════════════════════

    private static void handleDarkvision(ServerPlayer player, int tick) {
        if (tick % DARKVISION_REFRESH != 0) return;
        boolean has60  = AbilityUtils.hasAbility(player, Ability.DARKVISION_60);
        boolean has120 = AbilityUtils.hasAbility(player, Ability.DARKVISION_120);
        if (has60 || has120) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, 40_000, 0, false, false, false));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DEFENSIVE PASSIVS
    // ══════════════════════════════════════════════════════════════════

    /**
     * UNARMORED_DEFENSE — Rüstungsbonus ohne Rüstung.
     * Barbarian: DEX + CON. Monk: DEX + WIS. Andere: DEX.
     */
    private static void handleUnarmoredDefense(ServerPlayer player) {
        final String MOD_ID = "dnd:armor_unarmored_defense";
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_DEFENSE)) {
            removeArmorMod(player, MOD_ID);
            return;
        }
        if (isWearingArmor(player)) {
            removeArmorMod(player, MOD_ID);
            return;
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

    /**
     * DWARVEN_TOUGHNESS — Max HP + Level (einmalig über AbilityData, wird bei Level-Up aktualisiert).
     */
    private static void handleDwarvenToughness(ServerPlayer player) {
        if (!AbilityUtils.hasAbility(player, Ability.DWARVEN_TOUGHNESS)) return;
        var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int expected = (int) vars.PlayerLevel;
        int current  = AbilityDataUtils.getInt(vars, "ToughBonus", 0);
        if (current != expected) {
            AbilityDataUtils.set(vars, "ToughBonus", expected);
            vars.markSyncDirty();
            CharacterCreationPacket.applyAttrs(player);
        }
    }

    /**
     * FEY_ANCESTRY — Immunität gegen Charmed; kein magischer Schlaf.
     * Entfernt CHARMED-Effekt wenn vorhanden, verhindert Levitation durch Schlaf-Effekte.
     */
    private static void handleFeyAncestry(ServerPlayer player, int tick) {
        if (!AbilityUtils.hasAbility(player, Ability.FEY_ANCESTRY)) return;
        // CHARMED entfernen
        if (player.hasEffect(DndModMobEffects.CHARMED)) {
            player.removeEffect(DndModMobEffects.CHARMED);
            // Charmer-Var bereinigen
            var vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.Charmer = "";
            vars.markSyncDirty();
        }
        // Kein magischer Schlaf — Blindness/Sleep-Effekte entfernen (Minecraft-Annäherung)
        if (player.hasEffect(MobEffects.BLINDNESS)) {
            var eff = player.getEffect(MobEffects.BLINDNESS);
            // Nur entfernen wenn durch Magie (kein Ambient-Effekt)
            if (eff != null && !eff.isAmbient()) {
                player.removeEffect(MobEffects.BLINDNESS);
            }
        }
    }

    /**
     * BRAVE (Halfling) — Advantage gegen Frightened.
     * Minecraft-Annäherung: Slowness ≥ 4 (Frightened-Proxy) wird sofort entfernt.
     * TODO: Mit eigenem Fear-Effekt verknüpfen wenn verfügbar.
     */
    private static void handleBrave(ServerPlayer player, int tick) {
        if (!AbilityUtils.hasAbility(player, Ability.BRAVE)) return;
        var slow = player.getEffect(MobEffects.SLOWNESS);
        // Slowness ≥ 4 interpretieren wir als "frightened" (von DnD-Kreaturen)
        if (slow != null && slow.getAmplifier() >= 3 && !slow.isAmbient()) {
            player.removeEffect(MobEffects.SLOWNESS);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  BEWEGUNG & GESCHWINDIGKEIT
    // ══════════════════════════════════════════════════════════════════

    /**
     * FAST_MOVEMENT (Barbarian 5) — +10ft ohne Heavy Armor.
     */
    private static void handleFastMovement(ServerPlayer player) {
        final String MOD_ID = "dnd:speed_10ft_fast_movement";
        if (!AbilityUtils.hasAbility(player, Ability.FAST_MOVEMENT)) {
            removeSpeedMod(player, MOD_ID);
            return;
        }
        if (isWearingTagged(player, TAG_HEAVY_ARMOR)) {
            removeSpeedMod(player, MOD_ID);
        } else {
            applySpeedMod(player, MOD_ID, 0.030); // +10ft ≈ 0.030 in MC units
        }
    }

    /**
     * UNARMORED_MOVEMENT (Monk) — Geschwindigkeitsbonus ohne Rüstung, skaliert mit Level.
     * Level 2: +10ft, 6: +15ft, 10: +20ft, 14: +25ft, 18: +30ft.
     */
    private static void handleUnarmoredMovement(ServerPlayer player) {
        final String MOD_ID = "dnd:speed_unarmored_movement";
        if (!AbilityUtils.hasAbility(player, Ability.UNARMORED_MOVEMENT)) {
            removeSpeedMod(player, MOD_ID);
            return;
        }
        if (isWearingArmor(player)) {
            removeSpeedMod(player, MOD_ID);
            return;
        }
        int level = (int) player.getData(DndModVariables.PLAYER_VARIABLES).PlayerLevel;
        double bonus = level >= 18 ? 0.090
                : level >= 14 ? 0.075
                  : level >= 10 ? 0.060
                    : level >= 6  ? 0.045
                      : 0.030;
        applySpeedMod(player, MOD_ID, bonus);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ATTRIBUTE-MODIFIER HELPERS
    // ══════════════════════════════════════════════════════════════════

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

    // ── Equipment Checks ──────────────────────────────────────────────

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