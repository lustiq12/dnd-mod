package net.luderspieler.dnd.character;

import net.luderspieler.dnd.Utils.AbilityDataUtils;
import net.luderspieler.dnd.Utils.AbilityUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * ── Basis-Wert vs. effektiver Wert ────────────────────────────────────────
 * Jeder Ability Score hat zwei Felder in PlayerVariables:
 *
 *   vars.<Stat>        — BASIS-Wert. 10 + Rassen-/Subrassen-/Klassen-Boni aus
 *                         der Charaktererstellung + alle ASI-/Feat-Erhöhungen.
 *                         Wird NUR von Charaktererstellung, ASI und Feats
 *                         verändert. Das ist der Wert gegen den die 20er-
 *                         Obergrenze von Ability Score Improvement prüft
 *                         (siehe ChoiceRegistry/ChoiceExecutor).
 *
 *   vars.<Stat>Bonus    — TEMPORÄRER Modifier. Wird von allem befüllt das
 *                         einen Stat zeitweise erhöht/senkt: Tränke, Buffs,
 *                         zukünftig Magic Items. NIE von ASI angefasst,
 *                         zählt NICHT gegen die 20er-Grenze.
 *
 * getBaseAttribute() → NUR vars.<Stat>               (ASI-Cap-Prüfungen)
 * getAttribute()     → vars.<Stat> + vars.<Stat>Bonus (alles Spielmechanik-
 *                       Relevante: HP, Schaden, Spell-Save-DC, ...)
 *
 * WICHTIG: vars.<Stat> enthält Rassen-/Klassen-Boni bereits (siehe
 * CharacterCreationPacket.applyDndStats()) — getAttribute() darf sie NICHT
 * nochmal addieren, sonst werden sie doppelt gezählt (war vorher ein Bug).
 *
 * Wer immer ein <Stat>Bonus-Feld ändert (z.B. ein Trank-Effekt), muss danach
 * CharacterCreationPacket.applyAttrs(player) erneut aufrufen, damit der
 * Bonus auch tatsächlich in HP/Schaden/etc. einfließt — applyAttrs() läuft
 * nicht automatisch bei jeder Stat-Änderung, nur bei Chargen/Level-Up/ASI.
 */
public class AttributeHandler {

    /**
     * Effektiver Wert = Basis-Wert + temporärer Bonus.
     * Für alles Spielmechanik-Relevante verwenden.
     */
    public static int getAttribute(Player player, String attributeName) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return getBaseAttribute(player, attributeName) + getBonus(vars, attributeName);
    }

    /**
     * Reiner Basis-Wert ohne temporäre Boni. Verwenden für:
     *   - Ability Score Improvement (20er-Obergrenze prüfen + erhöhen)
     *   - Anzeige des "festen" Charakterwerts (z.B. Character Sheet)
     */
    public static int getBaseAttribute(Player player, String attributeName) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return (int) switch (attributeName.toLowerCase()) {
            case "strength" -> vars.Strength;
            case "dexterity" -> vars.Dexterity;
            case "constitution" -> vars.Constitution;
            case "intelligence" -> vars.Intelligence;
            case "wisdom" -> vars.Wisdom;
            case "charisma" -> vars.Charisma;
            default -> throw new IllegalArgumentException("Unknown attribute name " + attributeName);
        };
    }

    private static int getBonus(DndModVariables.PlayerVariables vars, String attributeName) {
        return (int) switch (attributeName.toLowerCase()) {
            case "strength" -> vars.StrengthBonus;
            case "dexterity" -> vars.DexterityBonus;
            case "constitution" -> vars.ConstitutionBonus;
            case "intelligence" -> vars.IntelligenceBonus;
            case "wisdom" -> vars.WisdomBonus;
            case "charisma" -> vars.CharismaBonus;
            default -> throw new IllegalArgumentException("Unknown attribute name " + attributeName);
        };
    }

    /**
     * Der klassische D&D Modifier (+1, +2, etc.) — nutzt den EFFEKTIVEN Wert
     * (Basis + Bonus), da Trank-Buffs z.B. den Spellcasting-Modifier
     * beeinflussen sollen.
     */
    public static int getAttributeBonus(Player player, String attributeName) {
        int totalScore = getAttribute(player, attributeName);
        return Math.floorDiv(totalScore - 10, 2);
    }

    /**
     * SpellCastingModifier() -> Findet das Attribut der Klasse und gibt dessen Bonus zurück.
     */
    public static int getSpellCastingModifier(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        ClassDefinition classDef = ClassRegistry.getClass(vars.PlayerClass);

        if (classDef == null || classDef.getSpellcastingAttribute() == null) return 0;

        return getAttributeBonus(player, classDef.getSpellcastingAttribute());
    }

    /**
     * SpellSavingThrow() -> 8 + Proficiency + Spellcasting Modifier.
     */
    public static int getSpellSavingThrow(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        return 8 + (int) vars.ProficiencyBonus + getSpellCastingModifier(player);
    }

    public static void applyAttrs(ServerPlayer player) {

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int level = (int) vars.PlayerLevel;

        // Nutzt den EFFEKTIVEN Wert (Basis + <Stat>Bonus aus Tränken/Buffs/
        // zukünftigen Items) — nicht den reinen Basiswert. Wer einen
        // <Stat>Bonus ändert, muss applyAttrs() danach erneut aufrufen,
        // damit es hier einfließt (siehe AttributeHandler-Doc).
        int strM = AttributeHandler.getAttributeBonus(player, "strength");
        int dexM = AttributeHandler.getAttributeBonus(player, "dexterity");
        int conM = AttributeHandler.getAttributeBonus(player, "constitution");
        int intM = AttributeHandler.getAttributeBonus(player, "intelligence");
        int wisM = AttributeHandler.getAttributeBonus(player, "wisdom");
        int chaM = AttributeHandler.getAttributeBonus(player, "charisma");

        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);

        if (cls == null) {
            throw new IllegalStateException("Fehler: Spieler " + player.getName().getString() + " hat keine gültige Dnd-Klasse definiert! (Klassen-ID: " + vars.PlayerClass + ")");
        }
        // No x2 here as you stated the values are already doubled
        int hpPerLvl = cls.getClassHealth();

        double abilitySpeedBonus = 0.0;

        if (AbilityUtils.hasAbility(player, Ability.SPEED_BONUS_5)) {
            abilitySpeedBonus += 0.015; // +5ft — Wood Elf, Goliath
        }
        if (AbilityUtils.hasAbility(player, Ability.ROVING)) {
            abilitySpeedBonus += 0.030; // +10ft — Ranger lvl 6
        }
        if (abilitySpeedBonus != 0) {
            updateMod(player, Attributes.MOVEMENT_SPEED, "dnd:speed_ability_constant", abilitySpeedBonus);
        } else {
            // Entfernen falls keine Speed-Ability mehr vorhanden
            var speedInst = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedInst != null) {
                speedInst.removeModifier(
                        net.minecraft.resources.ResourceLocation.parse("dnd:speed_ability_constant"));
            }
        }


        // --- STRENGTH ---
        updateMod(player, Attributes.ATTACK_DAMAGE, "dnd:str_dmg", strM * 1.5);
        updateMod(player, Attributes.BLOCK_BREAK_SPEED, "dnd:str_mining", Math.max(-0.5, strM * 0.15));
        updateMod(player, Attributes.ATTACK_KNOCKBACK, "dnd:str_kb", Math.max(0, strM * 0.3));
        updateMod(player, Attributes.KNOCKBACK_RESISTANCE, "dnd:str_kb_res", Math.max(0, strM * 0.1));

        // --- DEXTERITY ---
        updateMod(player, Attributes.ATTACK_SPEED, "dnd:dex_ats", dexM * 0.15);
        updateMod(player, Attributes.SNEAKING_SPEED, "dnd:dex_sneak", dexM * 0.05);
        updateMod(player, Attributes.JUMP_STRENGTH, "dnd:dex_jump", dexM * 0.03);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED, "dnd:dex_swim", dexM * 0.1);

        // --- HEALTH (CONSTITUTION) ---
        double levelHpBonus = (hpPerLvl * level) - 20.0;
        double constitutionHpBonus = (conM * 2.0) * level;
        int featToughBonus = AbilityDataUtils.getInt(vars, "FeatToughBonus", 0) != 0 ? level * 4 : 0;
        int toughBonus = AbilityDataUtils.getInt(vars, "ToughBonus", 0) * 2;
        int draconicHpBonus = AbilityUtils.hasAbility(player, Ability.DRACONIC_RESILIENCE) ? level * 2 : 0;

        // Hard-cap check to prevent total health from dropping below 1 heart (2 HP)
        double totalBonusSum = levelHpBonus + constitutionHpBonus + featToughBonus + toughBonus + draconicHpBonus;
        if (totalBonusSum <= -20.0) {
            levelHpBonus += (-18.0 - totalBonusSum);
        }

        updateMod(player, Attributes.MAX_HEALTH, "dnd:level_hp", levelHpBonus);
        updateMod(player, Attributes.MAX_HEALTH, "dnd:con_hp", constitutionHpBonus);
        updateMod(player, Attributes.MAX_HEALTH, "dnd:feat_tough_hp", featToughBonus);
        updateMod(player, Attributes.MAX_HEALTH, "dnd:tough_hp", toughBonus);
        updateMod(player, Attributes.MAX_HEALTH, "dnd:draconic_hp", draconicHpBonus);


        // --- CONSTITUTION ---
        updateMod(player, Attributes.OXYGEN_BONUS, "dnd:con_oxy", conM * 20.0);
        updateMod(player, Attributes.SAFE_FALL_DISTANCE, "dnd:con_fall_dist", conM * 1.5);
        updateMod(player, Attributes.BURNING_TIME, "dnd:con_burn", conM * -0.1);

        // --- INTELLIGENCE ---
        updateMod(player, Attributes.MINING_EFFICIENCY, "dnd:int_eff", intM * 2.0);
        updateMod(player, Attributes.BLOCK_INTERACTION_RANGE, "dnd:int_reach", intM * 0.2);
        updateMod(player, Attributes.SUBMERGED_MINING_SPEED, "dnd:int_sub_mining", intM * 0.2);

        // --- WISDOM ---
        updateMod(player, Attributes.ENTITY_INTERACTION_RANGE, "dnd:wis_ent_reach", wisM * 0.3);
        updateMod(player, Attributes.STEP_HEIGHT, "dnd:wis_step", (wisM >= 2) ? 0.5 : 0.0);
        updateMod(player, Attributes.FALL_DAMAGE_MULTIPLIER, "dnd:wis_fall_dmg", wisM * -0.06);

        // --- CHARISMA ---
        updateMod(player, Attributes.LUCK, "dnd:cha_luck", (double) chaM);
        updateMod(player, Attributes.TEMPT_RANGE, "dnd:cha_tempt", chaM * 3.0);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.NAMETAG_DISTANCE, "dnd:cha_name", chaM * 2.0);

        player.setHealth(player.getMaxHealth());
    }

    private static void updateMod(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, String idStr, double val) {
        var inst = player.getAttribute(attr);
        if (inst != null) {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("dnd", idStr.replace("dnd:", ""));

            inst.removeModifier(loc);
            if (val != 0) {
                inst.addPermanentModifier(new AttributeModifier(loc, val, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }
}