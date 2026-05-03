package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.spells.targeting.RaycastHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import static net.luderspieler.dnd.spells.SpellCasters.*;

/**
 * Entry point for all spell effects.
 * Includes all spells defined in Spells.java.
 */
public class CastSpellProcedure {

    public static void execute(ServerPlayer player, String spellId, int level) {
        if (spellId == null || spellId.isBlank()) return;

        switch (spellId) {
            // ── CANTRIPS (level 0) ──────────────────────────────
            case "ACID_SPLASH" -> castAcidSplash(player);
            case "CHILL_TOUCH" -> castChillTouch(player);
            case "DANCING_LIGHTS" -> castDancingLights(player);
            case "DRUIDCRAFT" -> castDruidcraft(player);
            case "ELDRITCH_BLAST" -> castEldritchBlast(player);
            case "ELEMENTALISM" -> castElementalism(player);
            case "FIRE_BOLT" -> castFireBolt(player);
            case "GUIDANCE" -> castGuidance(player);
            case "LIGHT" -> castLight(player);
            case "MAGE_HAND" -> castMageHand(player);
            case "MENDING" -> castMending(player);
            case "MESSAGE" -> castMessage(player);
            case "MINOR_ILLUSION" -> castMinorIllusion(player);
            case "POISON_SPRAY" -> castPoisonSpray(player);
            case "PRESTIDIGITATION" -> castPrestidigitation(player);
            case "PRODUCE_FLAME" -> castProduceFlame(player);
            case "RAY_OF_FROST" -> castRayOfFrost(player);
            case "RESISTANCE" -> castResistance(player);
            case "SACRED_FLAME" -> castSacredFlame(player);
            case "SHILLELAGH" -> castShillelagh(player);
            case "SHOCKING_GRASP" -> castShockingGrasp(player);
            case "SORCEROUS_BURST" -> castSorcerousBurst(player);
            case "SPARE_THE_DYING" -> castSpareTheDying(player);
            case "STARRY_WISP" -> castStarryWisp(player);
            case "THAUMATURGY" -> castThaumaturgy(player);
            case "TRUE_STRIKE" -> castTrueStrike(player);
            case "VICIOUS_MOCKERY" -> castViciousMockery(player);

            // ── Grade 1 ─────────────────────────────────────────
            case "ALARM" -> castAlarm(player);
            case "ANIMAL_FRIENDSHIP" -> castAnimalFriendship(player);
            case "BANE" -> castBane(player);
            case "BLESS" -> castBless(player);
            case "BURNING_HANDS" -> castBurningHands(player);
            case "CHARM_PERSON" -> castCharmPerson(player);
            case "CHROMATIC_ORB" -> castChromaticOrb(player);
            case "COLOR_SPRAY" -> castColorSpray(player);
            case "COMMAND" -> castCommand(player);
            case "COMPREHEND_LANGUAGES" -> castComprehendLanguages(player);
            case "CREATE_OR_DESTROY_WATER" -> castCreateOrDestroyWater(player);
            case "CURE_WOUNDS" -> RaycastHelper.startTargeting(player, 2.0, 1, "CURE_WOUNDS");
            case "DETECT_EVIL_AND_GOOD" -> castDetectEvilAndGood(player);
            case "DETECT_MAGIC" -> castDetectMagic(player);
            case "DETECT_POISON_AND_DISEASE" -> castDetectPoisonAndDisease(player);
            case "DISGUISE_SELF" -> castDisguiseSelf(player);
            case "DISSONANT_WHISPERS" -> castDissonantWhispers(player);
            case "DIVINE_FAVOR" -> castDivineFavor(player);
            case "DIVINE_SMITE" -> castDivineSmite(player);
            case "ENSNARING_STRIKE" -> castEnsnaringStrike(player);
            case "ENTANGLE" -> castEntangle(player);
            case "EXPEDITIOUS_RETREAT" -> castExpeditiousRetreat(player);
            case "FAERIE_FIRE" -> castFaerieFire(player);
            case "FALSE_LIFE" -> castFalseLife(player);
            case "FEATHER_FALL" -> castFeatherFall(player);
            case "FIND_FAMILIAR" -> castFindFamiliar(player);
            case "FLOATING_DISK" -> castFloatingDisk(player);
            case "FOG_CLOUD" -> castFogCloud(player);
            case "GOODBERRY" -> castGoodberry(player);
            case "GREASE" -> castGrease(player);
            case "GUIDING_BOLT" -> castGuidingBolt(player);
            case "HEALING_WORD" ->
                    RaycastHelper.startTargeting(player, 18.0, 1, "HEALING_WORD");
            case "HELLISH_REBUKE" -> castHellishRebuke(player);
            case "HEROISM" -> castHeroism(player);
            case "HEX" -> castHex(player);
            case "HIDEOUS_LAUGHTER" -> castHideousLaughter(player);
            case "HUNTER_S_MARK" -> castHuntersMark(player);
            case "ICE_KNIFE" -> castIceKnife(player);
            case "IDENTIFY" -> castIdentify(player);
            case "ILLUSORY_SCRIPT" -> castIllusoryScript(player);
            case "INFLICT_WOUNDS" ->
                    RaycastHelper.startTargeting(player, 2.0, 1, "INFLICT_WOUNDS");
            case "JUMP" -> castJump(player);
            case "LONGSTRIDER" -> castLongstrider(player);
            case "MAGE_ARMOR" -> castMageArmor(player);
            case "MAGIC_MISSILE" -> castMagicMissile(player);
            case "PROTECTION_FROM_EVIL_AND_GOOD" -> castProtectionFromEvilAndGood(player);
            case "PURIFY_FOOD_AND_DRINK" -> castPurifyFoodAndDrink(player);
            case "RAY_OF_SICKNESS" -> castRayOfSickness(player);
            case "SANCTUARY" -> castSanctuary(player);
            case "SEARING_SMITE" -> castSearingSmite(player);
            case "SHIELD" -> castShield(player);
            case "SHIELD_OF_FAITH" -> castShieldOfFaith(player);
            case "SILENT_IMAGE" -> castSilentImage(player);
            case "SLEEP" -> castSleep(player);
            case "SPEAK_WITH_ANIMALS" -> castSpeakWithAnimals(player);
            case "THUNDERWAVE" -> castThunderwave(player);
            case "UNSEEN_SERVANT" -> castUnseenServant(player);

            // ── Grade 2 ─────────────────────────────────────────
            case "ACID_ARROW" -> castAcidArrow(player);
            case "AID" ->
                    RaycastHelper.startTargeting(player, 9.0, 3, "AID");
            case "ALTER_SELF" -> castAlterSelf(player);
            case "ANIMAL_MESSENGER" -> castAnimalMessenger(player);
            case "ARCANE_LOCK" -> castArcaneLock(player);
            case "ARCANIST_S_MAGIC_AURA" -> castArcanistsMagicAura(player);
            case "AUGURY" -> castAugury(player);
            case "BARKSKIN" -> castBarkskin(player);
            case "BLINDNESS_DEAFNESS" -> castBlindnessDeafness(player);
            case "BLUR" -> castBlur(player);
            case "CALM_EMOTIONS" -> castCalmEmotions(player);
            case "CONTINUAL_FLAME" -> castContinualFlame(player);
            case "DARKNESS" -> castDarkness(player);
            case "DARKVISION" -> castDarkvision(player);
            case "DETECT_THOUGHTS" -> castDetectThoughts(player);
            case "DRAGON_S_BREATH" -> castDragonsBreath(player);
            case "ENHANCE_ABILITY" -> castEnhanceAbility(player);
            case "ENLARGE_REDUCE" -> castEnlargeReduce(player);
            case "ENTHRALL" -> castEnthrall(player);
            case "FIND_STEED" -> castFindSteed(player);
            case "FIND_TRAPS" -> castFindTraps(player);
            case "FLAME_BLADE" -> castFlameBlade(player);
            case "FLAMING_SPHERE" -> castFlamingSphere(player);
            case "GENTLE_REPOSE" -> castGentleRepose(player);
            case "GUST_OF_WIND" -> castGustOfWind(player);
            case "HEAT_METAL" -> castHeatMetal(player);
            case "HOLD_PERSON" ->
                    RaycastHelper.startTargeting(player, 18.0, 1, "HOLD_PERSON");
            case "INVISIBILITY" -> castInvisibility(player);
            case "KNOCK" -> castKnock(player);
            case "RESTORATION" ->
                    RaycastHelper.startTargeting(player, 2.0, 1, "RESTORATION");
            case "LEVITATE" -> castLevitate(player);
            case "LOCATE_ANIMALS_OR_PLANTS" -> castLocateAnimalsOrPlants(player);
            case "LOCATE_OBJECT" -> castLocateObject(player);
            case "MAGIC_MOUTH" -> castMagicMouth(player);
            case "MAGIC_WEAPON" -> castMagicWeapon(player);
            case "MIND_SPIKE" -> castMindSpike(player);
            case "MIRROR_IMAGE" -> castMirrorImage(player);
            case "MISTY_STEP" -> castMistyStep(player);
            case "MOONBEAM" -> castMoonbeam(player);
            case "PASS_WITHOUT_TRACE" -> castPassWithoutTrace(player);
            case "PHANTASMAL_FORCE" -> castPhantalmalForce(player);
            case "PRAYER_OF_HEALING" -> castPrayerOfHealing(player);
            case "PROTECTION_FROM_POISON" -> castProtectionFromPoison(player);
            case "RAY_OF_ENFEEBLEMENT" -> castRayOfEnfeeblement(player);
            case "ROPE_TRICK" -> castRopeTrick(player);
            case "SCORCHING_RAY" -> castScorchingRay(player);
            case "SEE_INVISIBILITY" -> castSeeInvisibility(player);
            case "SHATTER" -> castShatter(player);
            case "SHINING_SMITE" -> castShiningSmite(player);
            case "SILENCE" -> castSilence(player);
            case "SPIDER_CLIMB" -> castSpiderClimb(player);
            case "SPIKE_GROWTH" -> castSpikeGrowth(player);
            case "SPIRITUAL_WEAPON" -> castSpiritualWeapon(player);
            case "SUGGESTION" -> castSuggestion(player);
            case "WARDING_BOND" -> castWardingBond(player);
            case "WEB" -> castWeb(player);
            case "ZONE_OF_TRUTH" -> castZoneOfTruth(player);

            // ── Grade 3 ─────────────────────────────────────────
            case "ANIMATE_DEAD" -> castAnimateDead(player);
            case "BEACON_OF_HOPE" -> castBeaconOfHope(player);
            case "BESTOW_CURSE" ->
                    RaycastHelper.startTargeting(player, 2.0, 1, "BESTOW_CURSE");
            case "BLINK" -> castBlink(player);
            case "CALL_LIGHTNING" -> castCallLightning(player);
            case "CLAIRVOYANCE" -> castClairvoyance(player);
            case "CONJURE_ANIMALS" -> castConjureAnimals(player);
            case "COUNTERSPELL" -> castCounterspell(player);
            case "CREATE_FOOD_AND_WATER" -> castCreateFoodAndWater(player);
            case "DAYLIGHT" -> castDaylight(player);
            case "DISPEL_MAGIC" -> castDispelMagic(player);
            case "FEAR" -> castFear(player);
            case "FIREBALL" -> castFireball(player);
            case "FLY" -> castFly(player);
            case "GASEOUS_FORM" -> castGaseousForm(player);
            case "GLYPH_OF_WARDING" -> castGlyphOfWarding(player);
            case "HASTE" -> castHaste(player);
            case "HYPNOTIC_PATTERN" -> castHypnoticPattern(player);
            case "LIGHTNING_BOLT" -> castLightningBolt(player);
            case "MAGIC_CIRCLE" -> castMagicCircle(player);
            case "MAJOR_IMAGE" -> castMajorImage(player);
            case "MASS_HEALING_WORD" -> castMassHealingWord(player);
            case "MELD_INTO_STONE" -> castMeldIntoStone(player);
            case "NONDETECTION" -> castNondetection(player);
            case "PHANTOM_STEED" -> castPhantomSteed(player);
            case "PLANT_GROWTH" -> castPlantGrowth(player);
            case "PROTECTION_FROM_ENERGY" -> castProtectionFromEnergy(player);
            case "REMOVE_CURSE" -> castRemoveCurse(player);
            case "REVIVIFY" -> castRevivify(player);
            case "SENDING" -> castSending(player);
            case "SLEET_STORM" -> castSleetStorm(player);
            case "SLOW" -> castSlow(player);
            case "SPEAK_WITH_DEAD" -> castSpeakWithDead(player);
            case "SPEAK_WITH_PLANTS" -> castSpeakWithPlants(player);
            case "SPIRIT_GUARDIANS" -> castSpiritGuardians(player);
            case "STINKING_CLOUD" -> castStinkingCloud(player);
            case "TINY_HUT" -> castTinyHut(player);
            case "TONGUES" -> castTongues(player);
            case "VAMPIRIC_TOUCH" -> castVampiricTouch(player);
            case "WATER_BREATHING" -> castWaterBreathing(player);
            case "WATER_WALK" -> castWaterWalk(player);
            case "WIND_WALL" -> castWindWall(player);

            // ── Grade 4 ─────────────────────────────────────────
            case "ARCANE_EYE" -> castArcaneEye(player);
            case "AURA_OF_LIFE" -> castAuraOfLife(player);
            case "BANISHMENT" -> castBanishment(player);
            case "BLACK_TENTACLES" -> castBlackTentacles(player);
            case "BLIGHT" ->
                    RaycastHelper.startTargeting(player, 18.0, 1, "BLIGHT");
            case "CHARM_MONSTER" -> castCharmMonster(player);
            case "COMPULSION" -> castCompulsion(player);
            case "CONFUSION" -> castConfusion(player);
            case "CONJURE_MINOR_ELEMENTALS" -> castConjureMinorElementals(player);
            case "CONJURE_WOODLAND_BEINGS" -> castConjureWoodlandBeings(player);
            case "CONTROL_WATER" -> castControlWater(player);
            case "DEATH_WARD" -> castDeathWard(player);
            case "DIMENSION_DOOR" -> castDimensionDoor(player);
            case "DIVINATION" -> castDivination(player);
            case "DOMINATE_BEAST" -> castDominateBeast(player);
            case "FABRICATE" -> castFabricate(player);
            case "FAITHFUL_HOUND" -> castFaithfulHound(player);
            case "FIRE_SHIELD" -> castFireShield(player);
            case "FREEDOM_OF_MOVEMENT" -> castFreedomOfMovement(player);
            case "GIANT_INSECT" -> castGiantInsect(player);
            case "GREATER_INVISIBILITY" -> castGreaterInvisibility(player);
            case "GUARDIAN_OF_FAITH" -> castGuardianOfFaith(player);
            case "HALLUCINATORY_TERRAIN" -> castHallucinatoryTerrain(player);
            case "ICE_STORM" -> castIceStorm(player);
            case "LOCATE_CREATURE" -> castLocateCreature(player);
            case "PHANTASMAL_KILLER" -> castPhantasmalKiller(player);
            case "POLYMORPH" -> castPolymorph(player);
            case "PRIVATE_SANCTUM" -> castPrivateSanctum(player);
            case "RESILIENT_SPHERE" -> castResilientSphere(player);
            case "SECRET_CHEST" -> castSecretChest(player);
            case "STONESKIN" -> castStoneskin(player);
            case "STONE_SHAPE" -> castStoneShape(player);
            case "VITRIOLIC_SPHERE" -> castVitriolicSphere(player);
            case "WALL_OF_FIRE" -> castWallOfFire(player);

            // ── Grade 5 ─────────────────────────────────────────
            case "ANIMATE_OBJECTS" -> castAnimateObjects(player);
            case "ANTILIFE_SHELL" -> castAntilifeShell(player);
            case "ARCANE_HAND" -> castArcaneHand(player);
            case "AWAKEN" -> castAwaken(player);
            case "CLOUDKILL" -> castCloudkill(player);
            case "COMMUNE" -> castCommune(player);
            case "COMMUNE_WITH_NATURE" -> castCommuneWithNature(player);
            case "CONE_OF_COLD" -> castConeOfCold(player);
            case "CONJURE_ELEMENTAL" -> castConjureElemental(player);
            case "CONTACT_OTHER_PLANE" -> castContactOtherPlane(player);
            case "CONTAGION" -> castContagion(player);
            case "CREATION" -> castCreation(player);
            case "DISPEL_EVIL_AND_GOOD" -> castDispelEvilAndGood(player);
            case "DOMINATE_PERSON" -> castDominatePerson(player);
            case "DREAM" -> castDream(player);
            case "FLAME_STRIKE" -> castFlameStrike(player);
            case "GEAS" -> castGeas(player);
            case "GREATER_RESTORATION" -> castGreaterRestoration(player);
            case "HALLOW" -> castHallow(player);
            case "HOLD_MONSTER" -> castHoldMonster(player);
            case "INSECT_PLAGUE" -> castInsectPlague(player);
            case "LEGEND_LORE" -> castLegendLore(player);
            case "MASS_CURE_WOUNDS" -> castMassCureWounds(player);
            case "MISLEAD" -> castMislead(player);
            case "MODIFY_MEMORY" -> castModifyMemory(player);
            case "PASSWALL" -> castPasswall(player);
            case "PLANAR_BINDING" -> castPlanarBinding(player);
            case "RAISE_DEAD" -> castRaiseDead(player);
            case "REINCARNATE" -> castReincarnate(player);
            case "SCRYING" -> castScrying(player);
            case "SEEMING" -> castSeeming(player);
            case "SUMMON_DRAGON" -> castSummonDragon(player);
            case "TELEKINESIS" -> castTelekinesis(player);
            case "TELEPATHIC_BOND" -> castTelepathicBond(player);
            case "TELEPORTATION_CIRCLE" -> castTeleportationCircle(player);
            case "TREE_STRIDE" -> castTreeStride(player);
            case "WALL_OF_FORCE" -> castWallOfForce(player);
            case "WALL_OF_STONE" -> castWallOfStone(player);

            // ── Grade 6 ─────────────────────────────────────────
            case "BLADE_BARRIER" -> castBladeBarrier(player);
            case "CHAIN_LIGHTNING" -> castChainLightning(player);
            case "CIRCLE_OF_DEATH" -> castCircleOfDeath(player);
            case "CONJURE_FEY" -> castConjureFey(player);
            case "CONTINGENCY" -> castContingency(player);
            case "CREATE_UNDEAD" -> castCreateUndead(player);
            case "DISINTEGRATE" -> castDisintegrate(player);
            case "EYEBITE" -> castEyebite(player);
            case "FIND_THE_PATH" -> castFindThePath(player);
            case "FLESH_TO_STONE" -> castFleshToStone(player);
            case "FORBIDDANCE" -> castForbiddance(player);
            case "FREEZING_SPHERE" -> castFreezingSphere(player);
            case "GLOBE_OF_INVULNERABILITY" -> castGlobeOfInvulnerability(player);
            case "GUARDS_AND_WARDS" -> castGuardsAndWards(player);
            case "HARM" -> castHarm(player);
            case "HEAL" -> castHeal(player);
            case "HEROES_FEAST" -> castHeroesFeast(player);
            case "INSTANT_SUMMONS" -> castInstantSummons(player);
            case "IRRESISTIBLE_DANCE" -> castIrresistibleDance(player);
            case "MAGIC_JAR" -> castMagicJar(player);
            case "MASS_SUGGESTION" -> castMassSuggestion(player);
            case "MOVE_EARTH" -> castMoveEarth(player);
            case "PLANAR_ALLY" -> castPlanarAlly(player);
            case "PROGRAMMED_ILLUSION" -> castProgrammedIllusion(player);
            case "SUNBEAM" -> castSunbeam(player);
            case "TRANSPORT_VIA_PLANTS" -> castTransportViaPlants(player);
            case "TRUE_SEEING" -> castTrueSeeing(player);
            case "WALL_OF_ICE" -> castWallOfIce(player);
            case "WALL_OF_THORNS" -> castWallOfThorns(player);
            case "WIND_WALK" -> castWindWalk(player);
            case "WORD_OF_RECALL" -> castWordOfRecall(player);

            // ── Grade 7 ─────────────────────────────────────────
            case "ARCANE_SWORD" -> castArcaneSword(player);
            case "CONJURE_CELESTIAL" -> castConjureCelestial(player);
            case "DELAYED_BLAST_FIREBALL" -> castDelayedBlastFireball(player);
            case "DIVINE_WORD" -> castDivineWord(player);
            case "ETHEREALNESS" -> castEtherealness(player);
            case "FINGER_OF_DEATH" -> castFingerOfDeath(player);
            case "FIRE_STORM" -> castFireStorm(player);
            case "FORCECAGE" -> castForcecage(player);
            case "MAGNIFICENT_MANSION" -> castMagnificentMansion(player);
            case "MIRAGE_ARCANE" -> castMirageArcane(player);
            case "PLANE_SHIFT" -> castPlaneShift(player);
            case "PRISMATIC_SPRAY" -> castPrismaticSpray(player);
            case "PROJECT_IMAGE" -> castProjectImage(player);
            case "REGENERATE" -> castRegenerate(player);
            case "RESURRECTION" -> castResurrection(player);
            case "REVERSE_GRAVITY" -> castReverseGravity(player);
            case "SEQUESTER" -> castSequester(player);
            case "SIMULACRUM" -> castSimulacrum(player);
            case "SYMBOL" -> castSymbol(player);
            case "TELEPORT" -> castTeleport(player);

            // ── Grade 8 ─────────────────────────────────────────
            case "ANIMAL_SHAPES" -> castAnimalShapes(player);
            case "ANTIMAGIC_FIELD" -> castAntimagicField(player);
            case "ANTIPATHY_SYMPATHY" -> castAntipathySympathy(player);
            case "BEFUDDLEMENT" -> castBefuddlement(player);
            case "CLONE" -> castClone(player);
            case "CONTROL_WEATHER" -> castControlWeather(player);
            case "DEMIPLANE" -> castDemiplane(player);
            case "DOMINATE_MONSTER" -> castDominateMonster(player);
            case "EARTHQUAKE" -> castEarthquake(player);
            case "GLIBNESS" -> castGlibness(player);
            case "HOLY_AURA" -> castHolyAura(player);
            case "INCENDIARY_CLOUD" -> castIncendiaryCloud(player);
            case "MAZE" -> castMaze(player);
            case "MIND_BLANK" -> castMindBlank(player);
            case "POWER_WORD_STUN" -> castPowerWordStun(player);
            case "SUNBURST" -> castSunburst(player);
            case "TSUNAMI" -> castTsunami(player);

            // ── Grade 9 ─────────────────────────────────────────
            case "ASTRAL_PROJECTION" -> castAstralProjection(player);
            case "FORESIGHT" -> castForesight(player);
            case "GATE" -> castGate(player);
            case "IMPRISONMENT" -> castImprisonment(player);
            case "MASS_HEAL" -> castMassHeal(player);
            case "METEOR_SWARM" -> castMeteorSwarm(player);
            case "POWER_WORD_HEAL" -> castPowerWordHeal(player);
            case "POWER_WORD_KILL" -> castPowerWordKill(player);
            case "PRISMATIC_WALL" -> castPrismaticWall(player);
            case "SHAPECHANGE" -> castShapechange(player);
            case "STORM_OF_VENGEANCE" -> castStormOfVengeance(player);
            case "TIME_STOP" -> castTimeStop(player);
            case "TRUE_POLYMORPH" -> castTruePolymorph(player);
            case "TRUE_RESURRECTION" -> castTrueResurrection(player);
            case "WEIRD" -> castWeird(player);
            case "WISH" -> castWish(player);

            default -> player.sendSystemMessage(Component.literal("[DnD] Unknown spell: " + spellId));
        }
    }
}