package net.luderspieler.dnd.character;

import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
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
            default -> 10;
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
            default -> 0;
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
}