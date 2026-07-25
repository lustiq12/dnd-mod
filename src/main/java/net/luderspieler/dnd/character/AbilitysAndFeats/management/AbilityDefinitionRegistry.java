package net.luderspieler.dnd.character.AbilitysAndFeats.management;

import java.util.EnumMap;
import java.util.Map;

/**
 * Ordnet jede Ability ihrer AbilityCategory zu.
 * Alles nicht explizit gelistete ist PASSIVE_TRACKED (default via getOrDefault).
 *
 * Kategorien-Logik:
 *   ONE_TIME_TRIGGER  — Feuert einmal beim Hinzufügen (Stat-Boosts, permanente Unlocks)
 *   ALWAYS_ACTIVE     — Hat eine Implementierung in AlwaysActive.tick() oder applyAttrs(),
 *                       oder ist ein passiver Attribut-Modifier der dort hingehört.
 *   PLAYER_TRIGGERED  — Spieler aktiviert aktiv über das Ability Wheel (Bonus Action,
 *                       Action, Reaction mit Wahl). Auch "bei Kampfbeginn wenn du es willst".
 *   SELF_TRIGGERED    — Feuert automatisch auf Game-Events — "whenever X happens" OHNE "you can".
 *                       Keine Spielerwahl in der Aktivierung.
 *   PASSIVE_TRACKED   — Nur als Marker gespeichert: Lineage-Marker, Sprach-Abilities,
 *                       Spell-System-Abilities, noch nicht implementierbare Passivs.
 */
public class AbilityDefinitionRegistry {

    private static final Map<Ability, AbilityCategory> REGISTRY = new EnumMap<>(Ability.class);

    static {

        // ══════════════════════════════════════════════════════════════
        //  ONE_TIME_TRIGGER
        //  Feuert genau einmal beim Freischalten. Nur für permanente Stat-Boosts.
        // ══════════════════════════════════════════════════════════════
        oneTime(
                Ability.PRIMAL_CHAMPION,   // Barbarian 20: +4 STR/CON
                Ability.BODY_AND_MIND,     // Monk 20: +4 DEX/WIS
                Ability.SPEED_BONUS_5      // Verschiedene: permanente +5ft-Geschwindigkeit
        );

        // ══════════════════════════════════════════════════════════════
        //  ALWAYS_ACTIVE
        //  Passive Effekte die regelmäßig geprüft/gesetzt werden.
        //  ✓ = aktuell implementiert in AlwaysActive.tick() oder applyAttrs()
        //  (aspirational) = geplant, wird implementiert wenn das System bereit ist
        // ══════════════════════════════════════════════════════════════
        alwaysActive(
                // ── Implementiert ─────────────────────────────────────
                Ability.UNARMORED_DEFENSE,    // ✓ AlwaysActive: AC-Berechnung je Klasse
                Ability.FAST_MOVEMENT,         // ✓ AlwaysActive: +10ft ohne Heavy Armor
                Ability.UNARMORED_MOVEMENT,    // ✓ AlwaysActive: Monk Geschwindigkeitsbonus
                Ability.DARKVISION_60,         // ✓ AlwaysActive: Night Vision Effekt
                Ability.DARKVISION_120,        // ✓ AlwaysActive: Night Vision Effekt
                Ability.DWARVEN_TOUGHNESS,     // ✓ AlwaysActive: +Level HP

                Ability.ROVING,                // ✓ applyAttrs: +10ft Geschwindigkeit Ranger 6

                // ── Kampf-Passivs (aspirational) ──────────────────────
                Ability.WEAPON_MASTERY,
                Ability.FIGHTING_STYLE,
                Ability.FIGHTING_STYLE_PALADIN,
                Ability.FIGHTING_STYLE_RANGER,
                Ability.EXTRA_ATTACK_BARBARIAN,
                Ability.EXTRA_ATTACK_FIGHTER,
                Ability.EXTRA_ATTACK_MONK,
                Ability.EXTRA_ATTACK_PALADIN,
                Ability.EXTRA_ATTACK_RANGER,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_ONE,
                Ability.IMPROVED_EXTRA_ATTACK_FIGHTER_TWO,
                Ability.MARTIAL_ARTS,          // Monk: Unarmed-Strike-Scaling
                Ability.EMPOWERED_STRIKES,     // Monk 6: Unarmed zählt als magisch

                // ── Barbar-Passivs ─────────────────────────────────────
                Ability.PERSISTENT_RAGE,       // Rage endet nicht ungewollt
                Ability.INDOMITABLE_MIGHT,     // STR-Score statt Roll bei STR-Checks

                // ── Skill-Passivs (aspirational, aber zentral für Klassen) ─
                Ability.JACK_OF_ALL_TRADES,    // Bard: halbe Proficiency auf ungelernte Checks
                Ability.EXPERTISE,             // Bard/Rogue: doppelte Proficiency
                Ability.EXPERTISE_ROGUE,
                Ability.IMPROVED_EXPERTISE_ONE,
                Ability.IMPROVED_EXPERTISE_ROGUE_ONE,
                Ability.RELIABLE_TALENT,       // Rogue 7: Minimum 10 auf Proficiency-Checks

                // ── Schutz-Passivs (aspirational) ─────────────────────
                Ability.ELUSIVE,               // Rogue 18: keine Advantage auf Angriffe
                Ability.HEIGHTENED_FOCUS,      // Monk 10: Bonus auf Focus-Techniken

                // ── Ranger-Passivs (aspirational) ─────────────────────
                Ability.RELENTLESS_HUNTER,     // Hunter's Mark braucht keine Konzentration
                Ability.PRECISE_HUNTER,        // Advantage auf Hunter's Mark-Ziel
                Ability.FERAL_SENSES,          // Monk: keine blinden Winkel, wahre Sinne
                Ability.FOE_SLAYER,            // Ranger 20: passiver Bonus auf Favored Enemy

                // ── Spezies-Passivs (implementiert / aspirational) ────
                Ability.DWARVEN_RESILIENCE,    // Resistance gegen Gift (aspirational)
                Ability.FEY_ANCESTRY,          // Advantage gegen Charm, kein magischer Schlaf
                Ability.GNOME_CUNNING,         // Advantage auf INT/WIS/CHA-Saves gegen Magie
                Ability.BRAVE,                 // Halfling: Advantage gegen Frightened
                Ability.CELESTIAL_RESISTANCE,  // Aasimar: Resistance Nekrotik + Radiant
                Ability.FIENDISH_RESISTANCE,   // Tiefling: Resistance Feuer
                Ability.DAMAGE_RESISTANCE_DRAGONBORN // Dragonborn: Resistance zum Ancestry-Element
        );

        // ══════════════════════════════════════════════════════════════
        //  PLAYER_TRIGGERED  →  Erscheint im Ability Wheel
        //
        //  Faustregel: "you can [do X] as a Bonus Action / Action / Reaction"
        //  Auch Dinge die beim Kampfbeginn GEWÄHLT werden (z.B. Uncanny Metabolism:
        //  du kannst einen Focus Point ausgeben — das ist eine Entscheidung).
        // ══════════════════════════════════════════════════════════════
        playerTriggered(
                // ── Barbarian ─────────────────────────────────────────
                Ability.RAGE,
                Ability.RECKLESS_ATTACK,       // Entscheidung vor dem Angriff
                Ability.BRUTAL_STRIKE,         // Entscheidung bei Reckless Attack: forgo Advantage

                // ── Bard ──────────────────────────────────────────────
                Ability.BARDIC_INSPIRATION,
                Ability.COUNTERCHARM,          // 2024: Bonus Action, schützt Verbündete
                Ability.PEERLESS_SKILL,        // Bard 15: BI-Würfel auf eigenen Check

                // ── Cleric ────────────────────────────────────────────
                Ability.CHANNEL_DIVINITY,
                Ability.DIVINE_INTERVENTION,
                Ability.IMPROVED_DIVINE_INTERVENTION_ONE, // Cleric 20: auto-succeeds (same action)

                // ── Druid ─────────────────────────────────────────────
                Ability.WILD_SHAPE,
                Ability.WILD_COMPANION,        // Find Familiar als Magic Action
                Ability.WILD_RESURGENCE,       // Tausch: Wild Shape Slot ↔ Spell Slot

                // ── Fighter ───────────────────────────────────────────
                Ability.SECOND_WIND,
                Ability.ACTION_SURGE,
                Ability.TACTICAL_MIND,         // "you can expend Second Wind when you fail..."
                Ability.INDOMITABLE,           // Reaction: Rettungswurf wiederholen
                Ability.TACTICAL_MASTER,       // Wählt Mastery-Eigenschaft pro Angriff

                // ── Monk ──────────────────────────────────────────────
                Ability.FOCUS_POINTS,          // Öffnet Sub-Wahl: Flurry, Patient Defense, etc.
                Ability.UNCANNY_METABOLISM,    // "When you roll Initiative, you CAN expend 1 Focus Point"
                Ability.DEFLECT_ATTACKS,       // Reaction: Schadens-Reduktion
                Ability.SLOW_FALL,             // Reaction: Fallschaden reduzieren
                Ability.STUNNING_STRIKE,       // Entscheidung nach Treffer: Focus Point ausgeben
                Ability.SELF_RESTORATION,      // Ende des Zuges: Zustand beenden
                Ability.SUPERIOR_DEFENSE,      // 3 Focus Points: Resistance auf alle außer Force

                // ── Paladin ───────────────────────────────────────────
                Ability.LAY_ON_HANDS,
                Ability.PALADINS_SMITE,        // Entscheidung nach Treffer: Slot ausgeben
                Ability.RADIANT_SMITE,
                Ability.CHANNEL_DIVINITY_PALADIN,
                Ability.FAITHFUL_STEED,
                Ability.ABJURE_FOES,
                Ability.RESTORING_TOUCH,       // Erweiterung von Lay on Hands

                // ── Ranger ────────────────────────────────────────────
                Ability.TIRELESS,              // "you can give yourself Temp HP"
                Ability.NATURES_VEIL,          // Bonus Action: Invisible für 1 Runde

                // ── Rogue ─────────────────────────────────────────────
                Ability.CUNNING_ACTION,
                Ability.STEADY_AIM,            // Bonus Action: Advantage, Geschw. = 0
                Ability.CUNNING_STRIKE,        // Entscheidung bei Sneak Attack: Effekt kaufen
                Ability.UNCANNY_DODGE,         // Reaction: Schaden halbieren
                Ability.DEVIOUS_STRIKES,       // Erweiterte Cunning Strike Optionen (Wheel-Eintrag)
                Ability.STROKE_OF_LUCK,

                // ── Sorcerer ──────────────────────────────────────────
                Ability.INNATE_SORCERY,        // Bonus Action: Spell Attack Advantage
                Ability.FONT_OF_MAGIC,         // Konvertiert Sorcery Points ↔ Spell Slots
                Ability.METAMAGIC,             // Wählt Metamagic-Option beim Zaubern
                Ability.ARCANE_APOTHEOSIS,     // Sorcerer 20: Metamagic gratis während Innate Sorcery

                // ── Warlock ───────────────────────────────────────────
                Ability.MAGICAL_CUNNING,
                Ability.CONTACT_PATRON,
                Ability.MYSTIC_ARCANUM,
                Ability.IMPROVED_MYSTIC_ARCANUM_ONE,   // War fälschlicherweise ALWAYS_ACTIVE
                Ability.IMPROVED_MYSTIC_ARCANUM_TWO,
                Ability.IMPROVED_MYSTIC_ARCANUM_THREE,
                Ability.ELDRITCH_MASTER,

                // ── Wizard ────────────────────────────────────────────
                Ability.ARCANE_RECOVERY,
                Ability.MEMORIZE_SPELLS,

                // ── Spezies / Rassen ──────────────────────────────────
                Ability.STONE_CUNNING,         // Dwarf: Bonus Action, Tremorsense 60ft
                Ability.BREATH_WEAPON,         // Dragonborn: Aktive Nutzung, ProfBonus-Mal/LR
                Ability.FLIGHT,                // Dragonborn Level 5: Flug, 1× LR
                Ability.HEALING_HANDS,         // Aasimar: Touch-Heal = ProfBonus HP
                Ability.CELESTIAL_REVELATION,  // Aasimar Level 3: Himmelsform aktivieren
                Ability.LARGE_FORM,            // Goliath: Large werden
                Ability.CLOUDS_JAUNT,          // Goliath (Cloud): Teleport 30ft
                Ability.FIRES_BURN,            // Goliath (Fire): +Feuer-Schaden on hit
                Ability.FROSTS_CHILL,          // Goliath (Frost): Verlangsamung on hit
                Ability.HILLS_TUMBLE,          // Goliath (Hill): Prone on hit
                Ability.STONES_ENDURANCE,      // Goliath (Stone): Reaction, Schadens-Reduktion
                Ability.STORMS_THUNDER,        // Goliath (Storm): Reaction nach Treffer
                Ability.ADRENALINE_RUSH        // Orc: Bonus Action Dash + Temp HP
        );

        // ══════════════════════════════════════════════════════════════
        //  SELF_TRIGGERED
        //  Feuert AUTOMATISCH auf Game-Events. Kein "you can" — es passiert einfach.
        //  Beispiele: "When you roll Initiative, you regain..." (kein "you can")
        //             "When you take damage, ..." (Reaktion, keine Wahl)
        // ══════════════════════════════════════════════════════════════
        selfTriggered(
                // ── Automatische Rettungswürfe / Überleben ────────────
                Ability.DANGER_SENSE,          // Barb 2: Advantage auf DEX-Saves vs sichtbare Gefahren
                Ability.RELENTLESS_RAGE,       // Barb 11: Auto-Save wenn beim Wüten auf 0 HP
                Ability.RELENTLESS_ENDURANCE,  // Orc: auf 1 HP statt 0 fallen — 1×/LR, automatisch
                Ability.EVASION,               // Monk/Rogue: kein/halber Schaden auf DEX-Saves

                // ── Auto bei Initiative-Wurf (kein "you can") ─────────
                Ability.FERAL_INSTINCT,        // Barb 7: "you regain one expended use of Rage"
                Ability.INSTINCTIVE_POUNCE,    // Barb 7: halbe Bewegung bei Initiative (auto)
                Ability.SUPERIOR_INSPIRATION,  // Bard 18: "you regain one use of Bardic Inspiration"
                Ability.PERFECT_FOCUS,         // Monk 15: "you regain 4 Focus Points" (kein "you can")

                // ── Auto bei anderen Kampf-Aktionen ───────────────────
                Ability.TACTICAL_SHIFT,        // Fighter 5: halbe Bewg. ohne OA bei Action Surge (auto)
                Ability.STUDIED_ATTACKS,       // Fighter 13: Advantage nach Miss (auto, kein "you can")

                // ── Auto bei Treffern ─────────────────────────────────
                Ability.BLESSED_STRIKES,       // Cleric 7: Bonus-Schaden beim ersten Treffer/Runde
                Ability.IMPROVED_BLESSED_STRIKES_ONE,
                Ability.ELEMENTAL_FURY,        // Druid 7: Bonus-Schaden beim ersten Treffer/Runde
                Ability.IMPROVED_ELEMENTAL_FURY_ONE,
                Ability.RADIANT_STRIKES,       // Paladin 11: +1d8 Radiant auf jeden Nahkampftreffer
                Ability.SNEAK_ATTACK,          // Rogue: auto Bonus-Schaden bei Bedingung erfüllt

                // ── Auto bei Turn Undead / Channel Divinity ───────────
                Ability.SMITE_UNDEAD,          // Cleric 5: löst bei Turn Undead automatisch aus

                // ── Auto-Auren (kein Aktivieren nötig) ────────────────
                Ability.AURA_OF_PROTECTION,    // Paladin 6: CHA zu Saves für nahe Verbündete
                Ability.AURA_OF_COURAGE,       // Paladin 10: Frightened-Immunität-Aura

                // ── Auto bei Short Rest ───────────────────────────────
                Ability.SORCEROUS_RESTORATION, // Sorcerer 5: Sorcery Points bei Short Rest regain

                // ── Spezies-Auto-Trigger ───────────────────────────────
                Ability.RESOURCEFUL,           // Human: Heroic Inspiration bei Long Rest automatisch
                Ability.LUCKY,                 // Halfling: 1en bei Würfen automatisch neu würfeln

                // Damage type immunities/resistances — no player choice, auto-applied on hit
                Ability.FIRE_DAMAGE_IMMUNITY, Ability.FIRE_DAMAGE_RESISTANCE,
                Ability.COLD_DAMAGE_IMMUNITY, Ability.COLD_DAMAGE_RESISTANCE,
                Ability.LIGHTNING_DAMAGE_IMMUNITY, Ability.LIGHTNING_DAMAGE_RESISTANCE,
                Ability.THUNDER_DAMAGE_IMMUNITY, Ability.THUNDER_DAMAGE_RESISTANCE,
                Ability.FORCE_DAMAGE_IMMUNITY, Ability.FORCE_DAMAGE_RESISTANCE,
                Ability.NECROTIC_DAMAGE_IMMUNITY, Ability.NECROTIC_DAMAGE_RESISTANCE,
                Ability.POISON_DAMAGE_IMMUNITY, Ability.POISON_DAMAGE_RESISTANCE,
                Ability.ACID_DAMAGE_IMMUNITY, Ability.ACID_DAMAGE_RESISTANCE,
                Ability.PSYCHIC_DAMAGE_IMMUNITY, Ability.PSYCHIC_DAMAGE_RESISTANCE,
                Ability.RADIANT_DAMAGE_IMMUNITY, Ability.RADIANT_DAMAGE_RESISTANCE,
                Ability.BLUDGEONING_DAMAGE_IMMUNITY, Ability.BLUDGEONING_DAMAGE_RESISTANCE,
                Ability.PIERCING_DAMAGE_IMMUNITY, Ability.PIERCING_DAMAGE_RESISTANCE,
                Ability.SLASHING_DAMAGE_IMMUNITY, Ability.SLASHING_DAMAGE_RESISTANCE
        );

        // Alles nicht explizit gelistete → PASSIVE_TRACKED (via getOrDefault)
        //
        // Explizit PASSIVE_TRACKED (als Dokumentation):
        //
        // Spell-System (kein Wheel, kein Tick — das Zaubersystem regelt alles):
        //   SPELLCASTING_BARD/CLERIC/DRUID/PALADIN/RANGER/SORCERER/WIZARD, PACT_MAGIC
        //
        // Rassen-/Subclass-Marker (keine Minecraft-Effekte, nur Buchhaltung):
        //   DRACONIC_ANCESTRY, GIANT_ANCESTRY, OTHERWORLDLY_GIFT
        //   ELVEN_LINEAGE, HIGH_ELF_LINEAGE, WOOD_ELF_LINEAGE, DROW_LINEAGE
        //   GNOMISH_LINEAGE, FOREST_GNOME_LINEAGE, ROCK_GNOME_LINEAGE
        //   ABYSSAL_LINEAGE, CHTHONIC_LINEAGE, INFERNAL_LINEAGE
        // registry.register(Ability.DRACONIC_RESILIENCE, AbilityCategory.PASSIVE_TRACKED,
        //     "Draconic Resilience",
        //     "+1 max HP per Sorcerer level. Unarmored AC = 10 + DEX + CHA.");
        //
        // registry.register(Ability.WILD_MAGIC_SURGE, AbilityCategory.PASSIVE_TRACKED,
        //     "Wild Magic Surge",
        //     "Wild magic may surge when you cast a Sorcerer spell of 1st level or higher.");
        //
        // registry.register(Ability.IMPROVED_CRITICAL, AbilityCategory.PASSIVE_TRACKED,
        //     "Improved Critical",
        //     "You score a critical hit on a 19 or 20.");
        //
        // registry.register(Ability.BATTLE_MASTER, AbilityCategory.PASSIVE_TRACKED,
        //     "Battle Master",
        //     "You learn Maneuvers and gain Superiority Dice to fuel them.");
        //
        // registry.register(Ability.ELDRITCH_KNIGHT_SPELLCASTING, AbilityCategory.PASSIVE_TRACKED,
        //     "Eldritch Knight Spellcasting",
        //     "You learn spells from the Wizard list, focused on abjuration and evocation.");
        //
        // registry.register(Ability.FRENZY, AbilityCategory.PASSIVE_TRACKED,
        //     "Frenzy",
        //     "When you enter a rage, you can choose to frenzy. While frenzied, make one extra attack.");
        //
        // registry.register(Ability.CIRCLE_OF_THE_MOON, AbilityCategory.PASSIVE_TRACKED,
        //     "Circle Forms",
        //     "You can use Wild Shape to transform into more powerful beasts.");
        //
        // registry.register(Ability.SACRED_WEAPON, AbilityCategory.PLAYER_TRIGGERED,
        //     "Sacred Weapon",
        //     "Channel Divinity: your weapon glows, dealing radiant bonus damage.");
        //
        // registry.register(Ability.DIVINE_HEALTH, AbilityCategory.PASSIVE_TRACKED,
        //     "Divine Health",
        //     "The divine magic flowing through you makes you immune to disease.");
        //
        // Passiv durch das Spell-System geregelt:
        //   MAGICAL_SECRETS, RITUAL_ADEPT, SPELL_MASTERY, SIGNATURE_SPELLS
        //   LIGHT_BEARER (Aasimar: kennt Light-Cantrip — Spell-System)
        //   WORDS_OF_CREATION (Bard 20: passiver Modifier auf Spells — Spell-System)
        //   SORCEROUS_BURST (es ist bereits Spells.Cantrip — Spell-System)
        //   FAVORED_ENEMY (2024 PHB: Hunter's Mark immer vorbereitet — Spell-System)
        //
        // Passive Modifikatoren auf bestehende Abilities (keine eigene Logik):
        //   FONT_OF_INSPIRATION (regelt wie BI auflädt — Reset-System)
        //   IMPROVED_BRUTAL_STRIKE_ONE/TWO (mehr Optionen bei Brutal Strike)
        //   IMPROVED_ACTION_SURGE_ONE (2 Ladungen statt 1)
        //   IMPROVED_INDOMITABLE_ONE/TWO (mehr Ladungen)
        //   IMPROVED_CUNNING_STRIKE_ONE (mehr CS-Optionen)
        //   DEFLECT_ENERGY (erweitert Deflect Attacks auf alle Schadenstypen)
        //   ACROBATIC_MOVEMENT, DISCIPLINED_SURVIVOR (keine MC-Implementierung)
        //   SLIPPERY_MIND (Save-Proficiency, kein MC-Hook)
        //
        // Sprach-/Wissens-Features:
        //   DRUIDIC, THIEVES_CANT (Sprachen, keine MC-Effekte)
        //   SCHOLAR, DEFT_EXPLORER, IMPROVED_DEFT_EXPLORER_ONE (Sprachkenntnisse/Skills)
        //   DIVINE_ORDER, PRIMAL_ORDER, PRIMAL_KNOWLEDGE (passive Wahl-Boni)
        //
        // Physische Passivs (kein MC-Attribut-Mapping):
        //   POWERFUL_BUILD, POWERFUL_BUILD_ORC (Traglast)
        //   HALFLING_NIMBLENESS, NATURALLY_STEALTHY, KEEN_SENSES, TRANCE, VERSATILE
        //
        // Archdruid, Beast Spells, Sorcerous Incarnation (Modifikatoren auf andere Systeme):
        //   ARCHDRUID, BEAST_SPELLS, SORCEROUS_INCARNATION, ELDRITCH_INVOCATIONS
    }

    // ── HELPER-METHODEN ──────────────────────────────────────────────

    private static void oneTime(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ONE_TIME_TRIGGER);
    }

    private static void alwaysActive(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.ALWAYS_ACTIVE);
    }

    private static void playerTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.PLAYER_TRIGGERED);
    }

    private static void selfTriggered(Ability... abilities) {
        for (Ability a : abilities) REGISTRY.put(a, AbilityCategory.SELF_TRIGGERED);
    }

    /**
     * Gibt die Kategorie der Ability zurück.
     * Default: PASSIVE_TRACKED für alles nicht explizit gelistete.
     */
    public static AbilityCategory getCategory(Ability ability) {
        return REGISTRY.getOrDefault(ability, AbilityCategory.PASSIVE_TRACKED);
    }
}