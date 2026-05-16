package net.luderspieler.dnd.character.abilitys;

public enum Ability {
    // ==================== BARBARIAN ====================
    RAGE,
    RECKLESS_ATTACK,
    DANGER_SENSE,
    PRIMAL_KNOWLEDGE,
    EXTRA_ATTACK_BARBARIAN,
    FERAL_INSTINCT,
    BRUTAL_CRITICAL,
    RELENTLESS_RAGE,
    PRIMAL_REFLEXES,
    RELENTLESS,
    PRIMAL_CHAMPION,

    // ==================== BARD ====================
    BARDIC_INSPIRATION,
    SPELLCASTING_BARD,
    JACK_OF_ALL_TRADES,
    EXPERTISE,
    FONT_OF_INSPIRATION,
    COUNTERCHARM,
    MAGICAL_SECRETS_BARD,
    PEERLESS_SKILL,
    LEGENDARY_PERFORMANCE,
    SUPERIOR_INSPIRATION,

    // ==================== CLERIC ====================
    SPELLCASTING_CLERIC,
    CHANNEL_DIVINITY,
    HEALING_WORD_IMPROVEMENT,
    DESTROY_UNDEAD,
    POTENT_SPELLCASTING,
    IMPROVED_CHANNEL_DIVINITY,
    DIVINE_STRIKE,
    SUPREME_HEALING,
    DIVINE_INTERVENTION,

    // ==================== DRUID ====================
    SPELLCASTING_DRUID,
    DRUIDIC,
    WILD_SHAPE,
    WILD_COMPANION,
    WILD_FIRE,
    IMPROVED_WILD_SHAPE,
    BEAST_SPELLS,
    THOUSAND_FORMS,
    UNLIMITED_WILD_SHAPE,

    // ==================== FIGHTER ====================
    FIGHTING_STYLE,
    SECOND_WIND,
    ACTION_SURGE,
    MARTIAL_ARCHETYPE,
    EXTRA_ATTACK_FIGHTER,
    ABILITY_SCORE_IMPROVEMENT,
    INDOMITABLE,
    EXTRA_ATTACK_FIGHTER_2,
    SUDDEN_STRIKE,
    SUPERIOR_CRITICAL,
    ACTION_SURGE_MASTERY,
    EXTRA_ATTACK_FIGHTER_3,

    // ==================== MONK ====================
    MARTIAL_ARTS,
    UNARMORED_DEFENSE,
    KI,
    UNARMORED_MOVEMENT,
    MONASTIC_TRADITION,
    SLOW_FALL,
    EXTRA_ATTACK_MONK,
    STUNNING_STRIKE,
    KI_FUELED_ATTACK,
    EVASION,
    PURITY_OF_BODY,
    TONGUE_OF_THE_SUN_AND_MOON,
    DIAMOND_SOUL,
    TIMELESS_BODY,
    EMPTY_BODY,
    PERFECT_SELF,

    // ==================== PALADIN ====================
    LAY_ON_HANDS,
    SPELLCASTING_PALADIN,
    FIGHTING_STYLE_PALADIN,
    CHANNEL_DIVINITY_PALADIN,
    DIVINE_SMITE,
    AURA_OF_PROTECTION,
    BLESSED_WARRIOR,
    AURA_OF_COURAGE,
    IMPROVED_SMITE,
    CLEANSING_TOUCH,
    AURA_IMPROVEMENTS,

    // ==================== RANGER ====================
    DRAKEWARDEN,
    SPELLCASTING_RANGER,
    FIGHTING_STYLE_RANGER,
    RANGERS_QUARRY,
    PRIMAL_ORDER,
    EXTRA_ATTACK_RANGER,
    ROVING,
    PRIMAL_AWARENESS,
    RANGERS_INTUITION,
    FLEET_OF_FOOT,
    FERAL_SENSES,
    FOE_SLAYER,

    // ==================== ROGUE ====================
    EXPERTISE_ROGUE,
    SNEAK_ATTACK,
    CUNNING_ACTION,
    ROGUISH_ARCHETYPE,
    ABILITY_SCORE_IMPROVEMENT_ROGUE,
    UNCANNY_DODGE,
    EXPERTISE_EXPANSION,
    REMARKABLE_DODGE,
    RELIABLE_TALENT,
    BLINDSENSE,
    SLIPPERY_MIND,
    ELUSIVE,
    STROKE_OF_LUCK,

    // ==================== SORCERER ====================
    SPELLCASTING_SORCERER,
    SORCEROUS_RESILIENCE,
    FONT_OF_MAGIC,
    METAMAGIC,
    ABILITY_SCORE_IMPROVEMENT_SORCERER,
    MAGICAL_GUIDANCE,
    SORCEROUS_VERSATILITY,
    MAGICAL_AMPLIFICATION,
    MAGICAL_EPIPHANY,
    SORCEROUS_RESTORATION,

    // ==================== WARLOCK ====================
    PACT_MAGIC,
    OTHERWORLDLY_PATRON,
    ELDRITCH_INVOCATIONS,
    PACT_BOON,
    CONTACT_OTHER_PLANE,
    ELDRITCH_VERSATILITY,
    MYSTIC_ARCANUM,
    ELDRITCH_MASTER,
    ELDRITCH_MASTER_MASTERY,

    // ==================== WIZARD ====================
    SPELLCASTING_WIZARD,
    ARCANE_RECOVERY,
    ARCANE_TRADITION,
    ABILITY_SCORE_IMPROVEMENT_WIZARD,
    SPELL_MASTERY,
    SIGNATURE_SPELLS,

    // ==================== RACE: HUMAN ====================
    EXTRA_ABILITY_INCREASE_HUMAN,

    // ==================== RACE: DWARF ====================
    DWARVEN_RESILIENCE,
    DWARVEN_COMBAT_TRAINING,
    TOOL_PROFICIENCY_DWARF,

    // ==================== RACE: ELF ====================
    KEEN_SENSES_ELF,
    FEY_ANCESTRY,
    TRANCE,

    // ==================== RACE: HALFLING ====================
    NATURALLY_STEALTHY,
    FORTUNATE,

    // ==================== RACE: DRAGONBORN ====================
    DRACONIC_RESILIENCE,
    DRACONIC_ANCESTRY,

    // ==================== RACE: GNOME ====================
    GNOME_CUNNING,

    // ==================== RACE: AASIMAR ====================
    CELESTIAL_RESISTANCE,
    HEALING_HANDS,

    // ==================== RACE: TIEFLING ====================
    INFERNAL_RESILIENCE,
    HELLISH_RESISTANCE,

    // ==================== RACE: GOLIATH ====================
    STONE_ENDURANCE,
    POWERFUL_BUILD,

    // ==================== RACE: ORC ====================
    ORC_POWERFUL_BUILD,
    PRIMAL_INTUITION,

    // ==================== SUBRACE: ELF - HIGH ELF ====================
    EXTRA_CANTRIP_HIGH_ELF,

    // ==================== SUBRACE: ELF - WOOD ELF ====================
    MASK_OF_THE_WILD,

    // ==================== SUBRACE: ELF - DROW ====================
    SUNLIGHT_SENSITIVITY,
    DROW_SPELLCASTING,

    // ==================== SUBRACE: DWARF - MOUNTAIN ====================
    ARMOR_TRAINING_MOUNTAIN_DWARF,

    // ==================== SUBRACE: DWARF - HILL ====================
    HILL_DWARF_ABILITY_INCREASE,

    // ==================== SUBRACE: HALFLING - LIGHTFOOT ====================
    // Uses base halfling abilities

    // ==================== SUBRACE: HALFLING - STOUT ====================
    STOUT_RESILIENCE,

    // ==================== SUBRACE: DRAGONBORN - CHROMATIC ====================
    CHROMATIC_BREATH_WEAPON,
    CHROMATIC_DAMAGE_RESISTANCE,

    // ==================== SUBRACE: DRAGONBORN - METALLIC ====================
    METALLIC_BREATH_WEAPON,
    METALLIC_DAMAGE_RESISTANCE,

    // ==================== SUBRACE: DRAGONBORN - GEM ====================
    GEM_BREATH_WEAPON,
    GEM_DAMAGE_RESISTANCE,

    // ==================== SUBRACE: GOLIATH - CLOUD GIANT ====================
    CLOUD_GIANT_ANCESTRY,

    // ==================== SUBRACE: GOLIATH - FIRE GIANT ====================
    FIRE_GIANT_ANCESTRY,

    // ==================== SUBRACE: GOLIATH - FROST GIANT ====================
    FROST_GIANT_ANCESTRY,

    // ==================== SUBRACE: GOLIATH - HILL GIANT ====================
    HILL_GIANT_ANCESTRY,

    // ==================== SUBRACE: GOLIATH - STONE GIANT ====================
    STONE_GIANT_ANCESTRY,

    // ==================== SUBRACE: GOLIATH - STORM GIANT ====================
    STORM_GIANT_ANCESTRY,

    // ==================== SUBRACE: TIEFLING - ABYSSAL ====================
    ABYSSAL_TIEFLING_SPELLS,

    // ==================== SUBRACE: TIEFLING - CHTHONIC ====================
    CHTHONIC_TIEFLING_SPELLS,

    // ==================== SUBRACE: TIEFLING - INFERNAL ====================
    INFERNAL_TIEFLING_SPELLS,

    // ==================== SUBRACE: AASIMAR - CELESTIAL REVELATION ====================
    CELESTIAL_REVELATION_SPELLS;

    /**
     * Gibt eine lesbare Version des Enum-Namens zurück
     */
    public String getDisplayName() {
        return this.name().replace("_", " ");
    }
}