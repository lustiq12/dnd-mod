package net.luderspieler.dnd.character.choices;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.aUtils.AbilityUtils;
import net.luderspieler.dnd.character.feats.FeatRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;

import static net.luderspieler.dnd.aUtils.ProficiencyUtils.addProficiency;
import static net.luderspieler.dnd.character.network.CharacterCreationPacket.applyAttrs;

public class ChoiceExecutor {

    public static void apply(Player player, String choiceID, String selectedValue) {
        var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);

        switch (choiceID) {

            case "FIGHTING_STYLE" -> {
                AbilityDataUtils.set(vars, "FightingStyle", selectedValue);
                vars.markSyncDirty();
            }

            case "HOLY_ORDER" -> {
                AbilityDataUtils.set(vars, "HolyOrder", selectedValue);
                vars.markSyncDirty();
            }

            case "PRIMAL_ORDER" -> {
                AbilityDataUtils.set(vars, "PrimalOrder", selectedValue);
                vars.markSyncDirty();
            }

            case "ELDRITCH_INVOCATION" -> {
                String existing = AbilityDataUtils.get(vars, "EldritchInvocations_chosen", "");
                AbilityDataUtils.set(vars, "EldritchInvocations_chosen",
                        existing.isBlank() ? selectedValue : existing + ";" + selectedValue);
                vars.markSyncDirty();
            }

            case "BARDIC_COLLEGE_SKILL" -> {
                String existing = AbilityDataUtils.get(vars, "BardExpertiseSkills_chosen", "");
                AbilityDataUtils.set(vars, "BardExpertiseSkills_chosen",
                        existing.isBlank() ? selectedValue : existing + ";" + selectedValue);
                vars.markSyncDirty();
            }

            case "TOOL_PROFICIENCY" -> {
                addProficiency(vars, selectedValue);
                vars.markSyncDirty();
            }

            // ── ABILITY SCORE IMPROVEMENT OR FEAT ─────────────────────────
            // selectedValue ist entweder:
            //   "Feat: <FEAT_ID>"              → Feat statt ASI (FeatRegistry kümmert sich drum)
            //   "Strength +2"                  → 1 Stat, +2
            //   "Strength +1,Dexterity +1"     → 2 Stats, je +1
            case "ABILITY_SCORE_IMPROVEMENT_OR_FEAT" -> {
                if (selectedValue.startsWith("Feat: ")) {
                    String featId = selectedValue.substring("Feat: ".length());
                    if (player instanceof ServerPlayer sp) {
                        FeatRegistry.apply(sp, featId);
                    }
                } else {
                    // Stat-Parsing: Komma trennt hier zwei Stat-Teile, nicht
                    // AbilityData-Keys, also ist direktes split(",") korrekt.
                    for (String part : selectedValue.split(",")) {
                        String[] split = part.trim().split(" \\+");
                        if (split.length != 2) continue;
                        String stat = split[0].trim();
                        int amount;
                        try { amount = Integer.parseInt(split[1].trim()); }
                        catch (NumberFormatException e) { continue; }

                        // Hard-Cap bei 20 auf den BASIS-Wert.
                        switch (stat) {
                            case "Strength"     -> vars.Strength     = Math.min(20, vars.Strength + amount);
                            case "Dexterity"    -> vars.Dexterity    = Math.min(20, vars.Dexterity + amount);
                            case "Constitution" -> vars.Constitution = Math.min(20, vars.Constitution + amount);
                            case "Intelligence" -> vars.Intelligence = Math.min(20, vars.Intelligence + amount);
                            case "Wisdom"       -> vars.Wisdom       = Math.min(20, vars.Wisdom + amount);
                            case "Charisma"     -> vars.Charisma     = Math.min(20, vars.Charisma + amount);
                        }
                    }
                    if (player instanceof ServerPlayer sp) applyAttrs(sp);
                }
                vars.markSyncDirty();
            }

            // ── SUBCLASS ──────────────────────────────────────────────────
            case "SUBCLASS" -> {
                vars.PlayerSubclass = selectedValue;
                if (player instanceof ServerPlayer sp) {
                    AbilityUtils.updateSubclassAbilitiesForLevel(sp, (int) vars.PlayerLevel);
                    // Wichtig: Subklassen-Abilities wie DRACONIC_RESILIENCE wirken
                    // sich auf abgeleitete Stats (max HP etc.) aus — ohne diesen
                    // Aufruf bleibt die Ability zwar in vars.Abilities sichtbar,
                    // aber die AttributeModifier (z.B. MAX_HEALTH) werden nie neu
                    // berechnet, bis der Spieler levelt oder /dnd applyattrs läuft.
                    applyAttrs(sp);
                }
                vars.markSyncDirty();
            }

            // ── METAMAGIC ─────────────────────────────────────────────────
            // WICHTIG: METAMAGIC_chosen nutzt SEMIKOLON als Trenner zwischen
            // den gewählten Optionen, NICHT Komma. Grund: AbilityData trennt
            // seine Top-Level-Einträge (key=value-Paare) an Kommas — ein Wert
            // der selbst Kommas enthält, wird beim nächsten parse() zerstört.
            // Semikolon taucht im AbilityData-Format nicht auf, ist also sicher.
            case "METAMAGIC" -> {
                String existing = AbilityDataUtils.get(vars, "METAMAGIC_chosen", "");
                AbilityDataUtils.set(vars, "METAMAGIC_chosen",
                        existing.isBlank() ? selectedValue : existing + ";" + selectedValue);

                if (player instanceof ServerPlayer sp
                        && !AbilityUtils.hasAbility(sp, Ability.METAMAGIC)) {
                    AbilityUtils.addAbility(sp, Ability.METAMAGIC);
                }

                vars.markSyncDirty();
                if (player instanceof ServerPlayer sp) applyAttrs(sp);
            }

            // ── DRACONIC ANCESTRY ──────────────────────────────────────────
            case "DRACONIC_ANCESTRY" -> {
                String damageType = switch (selectedValue) {
                    case "Black Dragon", "Copper Dragon" -> "ACID";
                    case "Blue Dragon", "Bronze Dragon" -> "LIGHTNING";
                    case "Brass Dragon", "Gold Dragon", "Red Dragon" -> "FIRE";
                    case "Green Dragon" -> "POISON";
                    case "Silver Dragon", "White Dragon" -> "COLD";
                    default -> "";
                };
                if (!damageType.isBlank()) {
                    AbilityDataUtils.set(vars, "DraconicAncestryDamageType", damageType);

                    String abilityName = damageType + "_DAMAGE_RESISTANCE";

                    Ability ability = Ability.valueOf(abilityName);
                    AbilityUtils.addAbility((ServerPlayer) player, ability);
                }
                vars.markSyncDirty();
            }
        }
    }

}