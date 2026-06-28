package net.luderspieler.dnd.character.feats;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Alle 33 allgemeinen Feats (2024 PHB "General Feats") — für jeden Charakter
 * wählbar, ohne Klassenvoraussetzung.
 *
 * Vorgesehene Erweiterungen (noch nicht implementiert):
 *   Fighting Style Feats  — setzen Warrior-Klasse oder Fighting-Style-Feature voraus
 *   Spellcasting Feats    — setzen Spellcasting-Ability oder Pact Magic voraus
 *   Origin Feats          — nur bei Charaktererstellung wählbar
 *   Epic Boons            — setzen Level 19+ voraus
 *
 * Implementierungsstatus pro Feat:
 *   DONE    — voll implementiert
 *   PARTIAL — Stat-Bonus anwendbar, Rest fehlt mangels System
 *   TODO    — nur Marker gesetzt, keinerlei Mechanik
 */
public class FeatRegistry {

    // ── Feat-Datenklasse ─────────────────────────────────────────────
    public enum FeatStatus { DONE, PARTIAL, TODO }

    public record FeatDef(
            String id,             // interner Schlüssel (SCREAMING_SNAKE_CASE)
            String displayName,    // Englisch (Mod-Sprache)
            String germanName,     // Deutsch (für spätere UI-Lokalisierung)
            String description,    // Kurzbeschreibung (Englisch)
            FeatStatus status
    ) {}

    // ── Alle 33 allgemeinen Feats ────────────────────────────────────
    public static final List<FeatDef> GENERAL_FEATS = List.of(
            new FeatDef("ALERT",           "Alert",             "Wachsam",
                    "+2 Initiative, can't be surprised.",                               FeatStatus.TODO),
            new FeatDef("ATHLETE",         "Athlete",           "Athlet",
                    "+1 STR/DEX, climb at full speed, jump from prone uses only 5ft.", FeatStatus.PARTIAL),
            new FeatDef("CHARGER",         "Charger",           "Stürmer",
                    "Dash + bonus attack with extra damage or push.",                   FeatStatus.TODO),
            new FeatDef("CROSSBOW_EXPERT", "Crossbow Expert",   "Armbrust-Experte",
                    "Ignore loading, no disadvantage in melee, bonus hand-crossbow.",  FeatStatus.TODO),
            new FeatDef("DUAL_WIELDER",    "Dual Wielder",      "Beidhändiger Kampf",
                    "+1 AC when dual-wielding, use any one-handed weapons.",            FeatStatus.TODO),
            new FeatDef("DUNGEON_DELVER",  "Dungeon Delver",    "Gewölbekundiger",
                    "Advantage on trap saves, detect secret doors, resist trap damage.",FeatStatus.TODO),
            new FeatDef("DURABLE",         "Durable",           "Zäh",
                    "+1 CON, minimum CON modifier on Hit Dice recovery.",               FeatStatus.PARTIAL),
            new FeatDef("FEY_TOUCHED",     "Fey Touched",       "Fey-Berührt",
                    "+1 INT/WIS/CHA, learn Misty Step + 1 divination/enchantment.",    FeatStatus.PARTIAL),
            new FeatDef("GREAT_WEAPON_MASTER", "Great Weapon Master", "Meister großer Waffen",
                    "Heavy weapon attacks add proficiency bonus to damage.",             FeatStatus.TODO),
            new FeatDef("HEALER",          "Healer",            "Heiler",
                    "Heal with healer's kit, stabilize restores 1 HP.",                FeatStatus.TODO),
            new FeatDef("KEEN_MIND",       "Keen Mind",         "Scharfsinnig",
                    "+1 INT, always know direction and time.",                          FeatStatus.PARTIAL),
            new FeatDef("LIGHTLY_ARMORED", "Lightly Armored",   "Leicht gerüstet",
                    "+1 STR/DEX, light armor proficiency.",                             FeatStatus.PARTIAL),
            new FeatDef("LINGUIST",        "Linguist",          "Linguist",
                    "+1 INT, learn 3 languages, create ciphers.",                      FeatStatus.PARTIAL),
            new FeatDef("LUCKY",           "Lucky",             "Glückspilz",
                    "3 luck points per day to reroll attacks, saves or enemy attacks.", FeatStatus.TODO),
            new FeatDef("MAGE_SLAYER",     "Mage Slayer",       "Magiertöter",
                    "Reaction attack on nearby spell, advantage on concentration saves.",FeatStatus.TODO),
            new FeatDef("MAGIC_INITIATE",  "Magic Initiate",    "Magieinitiierte",
                    "Learn 2 cantrips + 1 1st-level spell from any class list.",        FeatStatus.TODO),
            new FeatDef("MARTIAL_ADEPT",   "Martial Adept",     "Kampfmeister-Adept",
                    "Learn 2 maneuvers and get 1 superiority die.",                    FeatStatus.TODO),
            new FeatDef("MOBILE",          "Mobile",            "Mobil",
                    "+10ft speed, dash ignores difficult terrain, no OA after attacking.",FeatStatus.PARTIAL),
            new FeatDef("MOUNTED_COMBATANT","Mounted Combatant", "Berittener Kämpfer",
                    "Advantage on attacks vs unmounted creatures, protect mount.",      FeatStatus.TODO),
            new FeatDef("OBSERVANT",       "Observant",         "Aufmerksam",
                    "+1 INT/WIS, read lips, +5 passive Perception/Investigation.",     FeatStatus.PARTIAL),
            new FeatDef("POLEARM_MASTER",  "Polearm Master",    "Stangenwaffen-Meister",
                    "Bonus attack with polearm butt, OA when enemies enter reach.",    FeatStatus.TODO),
            new FeatDef("RESILIENT",       "Resilient",         "Resistent",
                    "+1 to one ability score, proficiency in its saving throw.",        FeatStatus.PARTIAL),
            new FeatDef("SAVAGE_ATTACKER", "Savage Attacker",   "Wilder Angreifer",
                    "Once per turn, reroll a melee weapon damage die.",                FeatStatus.TODO),
            new FeatDef("SENTINEL",        "Sentinel",          "Wächter",
                    "Hit stops movement, OA on Disengage, OA when ally is attacked.",  FeatStatus.TODO),
            new FeatDef("SHADOW_TOUCHED",  "Shadow Touched",    "Schatten-Berührt",
                    "+1 INT/WIS/CHA, learn Invisibility + 1 illusion/necromancy.",     FeatStatus.PARTIAL),
            new FeatDef("SHARPSHOOTER",    "Sharpshooter",      "Scharfschütze",
                    "No disadvantage at long range, ignore 3/4 cover.",                FeatStatus.TODO),
            new FeatDef("SHIELD_MASTER",   "Shield Master",     "Schildmeister",
                    "Bonus shove, add shield to DEX saves, no damage on success.",     FeatStatus.TODO),
            new FeatDef("SKILLED",         "Skilled",           "Begabt",
                    "Proficiency in 3 skills or tools of your choice.",                FeatStatus.TODO),
            new FeatDef("TAVERN_BRAWLER",  "Tavern Brawler",    "Kneipenschläger",
                    "+1 STR/CON, proficient with improvised weapons, grapple bonus.",  FeatStatus.PARTIAL),
            new FeatDef("TELEKINETIC",     "Telekinetic",       "Telekinetisch",
                    "+1 INT/WIS/CHA, Mage Hand cantrip, push creatures with bonus action.",FeatStatus.PARTIAL),
            new FeatDef("TELEPATHIC",      "Telepathic",        "Telepathisch",
                    "+1 INT/WIS/CHA, Detect Thoughts spell, speak telepathically.",    FeatStatus.PARTIAL),
            new FeatDef("TOUGH",           "Tough",             "Robust",
                    "+2 max HP per level.",                                             FeatStatus.DONE),
            new FeatDef("WEAPON_MASTER",   "Weapon Master",     "Waffenmeister",
                    "+1 STR/DEX, proficiency with 4 weapons.",                         FeatStatus.PARTIAL)
    );

    // ── Eligibility ──────────────────────────────────────────────────

    /**
     * Gibt alle Feats zurück, die der Spieler aktuell wählen darf.
     * Aktuell: alle 33 allgemeinen Feats sind für jeden wählbar.
     * Wenn ein Feat schon genommen wurde (steht in vars.Feats), wird es rausgefiltert.
     */
    public static List<FeatDef> getEligibleFeats(Player player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        String taken = vars.Feats == null ? "" : vars.Feats;

        List<FeatDef> result = new ArrayList<>();
        for (FeatDef feat : GENERAL_FEATS) {
            String marker = featMarker(feat.id());
            if (!taken.contains(marker)) result.add(feat);
        }
        return result;
    }

    /** Gibt alle Feats zurück, unabhängig von Eligibility (für Anzeige). */
    public static List<FeatDef> getAllGeneralFeats() {
        return GENERAL_FEATS;
    }

    // ── Application ──────────────────────────────────────────────────

    /**
     * Wendet einen Feat an. Setzt immer den Marker in vars.Feats.
     * Mechanische Effekte nur soweit im Mod umsetzbar (siehe FeatStatus).
     */
    public static void apply(ServerPlayer player, String featId) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // Marker setzen
        String marker = featMarker(featId);
        if (vars.Feats == null || vars.Feats.isBlank() || vars.Feats.equals("\"\"")) {
            vars.Feats = marker;
        } else if (!vars.Feats.contains(marker)) {
            vars.Feats += "," + marker;
        }

        // Mechanik anwenden
        switch (featId) {

            // ── DONE ─────────────────────────────────────────────────
            case "TOUGH" -> AbilityDataUtils.set(vars, "FeatToughBonus", true);

            // ── PARTIAL (Stat-Bonus sofort, Rest TODO) ────────────────
            case "ATHLETE"          -> bumpStat(vars, "Strength", 1);     // STR oder DEX wählbar; vereinfacht STR
            case "DURABLE"          -> bumpStat(vars, "Constitution", 1);
            case "FEY_TOUCHED"      -> bumpStat(vars, "Intelligence", 1);  // INT/WIS/CHA wählbar; TODO: Spells
            case "KEEN_MIND"        -> bumpStat(vars, "Intelligence", 1);
            case "LIGHTLY_ARMORED"  -> bumpStat(vars, "Dexterity", 1);    // STR/DEX wählbar; TODO: Armor prof
            case "LINGUIST"         -> bumpStat(vars, "Intelligence", 1);  // TODO: Sprachen
            case "MOBILE"           -> {
                // +10ft Speed — tritt sofort über applyAttrs() in Kraft da
                // CharacterCreationPacket.applyAttrs() Speed-Boni liest.
                AbilityDataUtils.set(vars, "FeatMobileBonus", true);
                // TODO: Dash ignoriert Difficult Terrain, keine OA nach eigenen Angriffen
            }
            case "OBSERVANT"        -> bumpStat(vars, "Intelligence", 1);  // INT/WIS wählbar; TODO: Perception
            case "RESILIENT"        -> {
                // TODO: der Spieler soll einen Stat wählen — vereinfacht: CON
                bumpStat(vars, "Constitution", 1);
                // TODO: Saving Throw Proficiency
            }
            case "SHADOW_TOUCHED"   -> bumpStat(vars, "Intelligence", 1);  // INT/WIS/CHA; TODO: Spells
            case "TAVERN_BRAWLER"   -> bumpStat(vars, "Strength", 1);     // STR/CON; TODO: Grapple
            case "TELEKINETIC"      -> bumpStat(vars, "Intelligence", 1);  // INT/WIS/CHA; TODO: Mage Hand/Push
            case "TELEPATHIC"       -> bumpStat(vars, "Intelligence", 1);  // INT/WIS/CHA; TODO: Detect Thoughts
            case "WEAPON_MASTER"    -> bumpStat(vars, "Strength", 1);     // STR/DEX; TODO: Weapon profs

            // ── TODO (nur Marker, keinerlei Mechanik) ─────────────────
            case "ALERT", "CHARGER", "CROSSBOW_EXPERT", "DUAL_WIELDER",
                 "DUNGEON_DELVER", "GREAT_WEAPON_MASTER", "HEALER",
                 "LUCKY", "MAGE_SLAYER", "MAGIC_INITIATE", "MARTIAL_ADEPT",
                 "MOUNTED_COMBATANT", "POLEARM_MASTER", "SAVAGE_ATTACKER",
                 "SENTINEL", "SHARPSHOOTER", "SHIELD_MASTER", "SKILLED" -> {
                // Mechanik TODO — Marker wurde oben gesetzt.
            }
        }

        vars.markSyncDirty();
        CharacterCreationPacket.applyAttrs(player);
    }

    /** Gibt true zurück wenn der Spieler diesen Feat bereits besitzt. */
    public static boolean hasFeat(Player player, String featId) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        if (vars.Feats == null) return false;
        return vars.Feats.contains(featMarker(featId));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static String featMarker(String featId) {
        return "FEAT_" + featId;
    }

    /**
     * Erhöht einen Basis-Stat (vars.<Stat>) um amount, HARD-CAP bei 20.
     * Ändert nur den Basiswert — nicht <Stat>Bonus — damit ASI-Cap-Checks
     * korrekt bleiben.
     */
    private static void bumpStat(DndModVariables.PlayerVariables vars, String stat, int amount) {
        switch (stat) {
            case "Strength"     -> vars.Strength     = Math.min(20, vars.Strength + amount);
            case "Dexterity"    -> vars.Dexterity    = Math.min(20, vars.Dexterity + amount);
            case "Constitution" -> vars.Constitution = Math.min(20, vars.Constitution + amount);
            case "Intelligence" -> vars.Intelligence = Math.min(20, vars.Intelligence + amount);
            case "Wisdom"       -> vars.Wisdom       = Math.min(20, vars.Wisdom + amount);
            case "Charisma"     -> vars.Charisma     = Math.min(20, vars.Charisma + amount);
        }
    }
}