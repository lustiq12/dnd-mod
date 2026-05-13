package net.luderspieler.dnd.spells;

import net.luderspieler.dnd.init.DndModMobEffects;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

@SuppressWarnings("unused")
public class SpellCasters {

    public static final Set<String> FINISHED_SPELLS = Set.of(
            "CURE_WOUNDS", "HEALING_WORD", "RESTORATION", "AID", "INFLICT_WOUNDS", "BLIGHT", "HOLD_PERSON", "BESTOW_CURSE","THUNDERWAVE", "FIRE_BOLT", "FIREBALL", "FALSE_LIFE", "FEATHER_FALL", "MAGE_ARMOR", "MIND_SPIKE", "MENDING", "CHILL_TOUCH", "WATER_BREATHING", "LEVITATE"
    );
    
    // --- Cantrips ---
    public static void castAcidSplash(ServerPlayer p) { /* Implement Logic */ }
    public static void castChillTouch(ServerPlayer caster, LivingEntity target) {
        Level level = target.level();
        target.hurt(caster.damageSources().source(DamageTypes.FREEZE, caster), 10.0F);
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.0F, 1.0F);
        caster.playSound(SoundEvents.PLAYER_HURT_FREEZE, 1.0F, 1.0F);

    }
    public static void castDancingLights(ServerPlayer p) { /* Implement Logic */ }
    public static void castDruidcraft(ServerPlayer p) { /* Implement Logic */ }
    public static void castEldritchBlast(ServerPlayer p) { /* Implement Logic */ }
    public static void castElementalism(ServerPlayer p) { /* Implement Logic */ }
    public static void castFireBolt(ServerPlayer caster, double range) {
        if (caster.level().isClientSide()) return;

        Vec3 lookVec = caster.getViewVector(1.0F);
        double speed = 5.0;
        Vec3 velocity = lookVec.scale(speed);

        double x = caster.getX() + lookVec.x;
        double y = caster.getY() + caster.getEyeHeight() + lookVec.y * 0.5;
        double z = caster.getZ() + lookVec.z;

        LargeFireball fireball = new LargeFireball(caster.level(), caster, velocity, 0);
        fireball.setPos(x, y, z);
        fireball.setDeltaMovement(velocity);
        fireball.hasImpulse = true;

        // Store range data in NBT
        CompoundTag nbt = fireball.getPersistentData();
        nbt.putDouble("spell_max_range", range);
        nbt.putDouble("spell_start_x", x);
        nbt.putDouble("spell_start_y", y);
        nbt.putDouble("spell_start_z", z);

        caster.level().addFreshEntity(fireball);

        caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    public static void castGuidance(ServerPlayer p) { /* Implement Logic */ }
    public static void castLight(ServerPlayer p) { /* Implement Logic */ }
    public static void castMageHand(ServerPlayer p) { /* Implement Logic */ }
    public static void castMending(ServerPlayer p) {
        ItemStack item = p.getMainHandItem();

         if (item.isDamaged()){
             item.setDamageValue(0);
         }
    }
    public static void castMessage(ServerPlayer p) { /* Implement Logic */ }
    public static void castMinorIllusion(ServerPlayer p) { /* Implement Logic */ }
    public static void castPoisonSpray(ServerPlayer p) { /* Implement Logic */ }
    public static void castPrestidigitation(ServerPlayer p) { /* Implement Logic */ }
    public static void castProduceFlame(ServerPlayer p) { /* Implement Logic */ }
    public static void castRayOfFrost(ServerPlayer p) { /* Implement Logic */ }
    public static void castResistance(ServerPlayer p) { /* Implement Logic */ }
    public static void castSacredFlame(ServerPlayer p) { /* Implement Logic */ }
    public static void castShillelagh(ServerPlayer p) { /* Implement Logic */ }
    public static void castShockingGrasp(ServerPlayer p) { /* Implement Logic */ }
    public static void castSorcerousBurst(ServerPlayer p) { /* Implement Logic */ }
    public static void castSpareTheDying(ServerPlayer p) { /* Implement Logic */ }
    public static void castStarryWisp(ServerPlayer p) { /* Implement Logic */ }
    public static void castThaumaturgy(ServerPlayer p) { /* Implement Logic */ }
    public static void castTrueStrike(ServerPlayer p) { /* Implement Logic */ }
    public static void castViciousMockery(ServerPlayer p) { /* Implement Logic */ }

    // --- Grade 1 ---
    public static void castAlarm(ServerPlayer p) { /* Implement Logic */ }
    public static void castAnimalFriendship(ServerPlayer p) { /* Implement Logic */ }
    public static void castBane(ServerPlayer p) { /* Implement Logic */ }
    public static void castBless(ServerPlayer p) { /* Implement Logic */ }
    public static void castBurningHands(ServerPlayer p) { /* Implement Logic */ }
    public static void castCharmPerson(ServerPlayer p) { /* Implement Logic */ }
    public static void castChromaticOrb(ServerPlayer p) { /* Implement Logic */ }
    public static void castColorSpray(ServerPlayer p) { /* Implement Logic */ }
    public static void castCommand(ServerPlayer p) { /* Implement Logic */ }
    public static void castComprehendLanguages(ServerPlayer p) { /* Implement Logic */ }
    public static void castCreateOrDestroyWater(ServerPlayer p) { /* Implement Logic */ }
    public static void castCureWounds(ServerPlayer caster, LivingEntity target) {
        target.heal(10.0F); // Heilt 5 Herzen
    }
    public static void castDetectEvilAndGood(ServerPlayer p) { /* Implement Logic */ }
    public static void castDetectMagic(ServerPlayer p) { /* Implement Logic */ }
    public static void castDetectPoisonAndDisease(ServerPlayer p) { /* Implement Logic */ }
    public static void castDisguiseSelf(ServerPlayer p) { /* Implement Logic */ }
    public static void castDissonantWhispers(ServerPlayer p) { /* Implement Logic */ }
    public static void castDivineFavor(ServerPlayer p) { /* Implement Logic */ }
    public static void castDivineSmite(ServerPlayer p) { /* Implement Logic */ }
    public static void castEnsnaringStrike(ServerPlayer p) { /* Implement Logic */ }
    public static void castEntangle(ServerPlayer p) { /* Implement Logic */ }
    public static void castExpeditiousRetreat(ServerPlayer p) { /* Implement Logic */ }
    public static void castFaerieFire(ServerPlayer p) { /* Implement Logic */ }
    public static void castFalseLife(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 24000, 2));
    }
    public static void castFeatherFall(ServerPlayer p, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 2));
    }
    public static void castFindFamiliar(ServerPlayer p) { /* Implement Logic */ }
    public static void castFloatingDisk(ServerPlayer p) { /* Implement Logic */ }
    public static void castFogCloud(ServerPlayer p) { /* Implement Logic */ }
    public static void castGoodberry(ServerPlayer p) { /* Implement Logic */ }
    public static void castGrease(ServerPlayer p) { /* Implement Logic */ }
    public static void castGuidingBolt(ServerPlayer p) { /* Implement Logic */ }
    public static void castHealingWord(ServerPlayer caster, LivingEntity target) {
        target.heal(4.5F);
        // Partikel-Effekt (Herzchen)
        ((net.minecraft.server.level.ServerLevel)target.level()).sendParticles(
                net.minecraft.core.particles.ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 5, 0.3, 0.3, 0.3, 0.1);
    }
    public static void castHellishRebuke(ServerPlayer p) { /* Implement Logic */ }
    public static void castHeroism(ServerPlayer p) { /* Implement Logic */ }
    public static void castHex(ServerPlayer p) { /* Implement Logic */ }
    public static void castHideousLaughter(ServerPlayer p) { /* Implement Logic */ }
    public static void castHuntersMark(ServerPlayer p) { /* Implement Logic */ }
    public static void castIceKnife(ServerPlayer p) { /* Implement Logic */ }
    public static void castIdentify(ServerPlayer p) { /* Implement Logic */ }
    public static void castIllusoryScript(ServerPlayer p) { /* Implement Logic */ }
    public static void castInflictWounds(ServerPlayer caster, LivingEntity target) {
        target.hurt(caster.damageSources().source(DamageTypes.WITHER, caster), 18.5F);
        // Nekrotische Partikel
        ((net.minecraft.server.level.ServerLevel)target.level()).sendParticles(
                net.minecraft.core.particles.ParticleTypes.SMOKE, target.getX(), target.getY() + 1, target.getZ(), 10, 0.2, 0.2, 0.2, 0.05);
    }
    public static void castJump(ServerPlayer p) { /* Implement Logic */ }
    public static void castLongstrider(ServerPlayer p) { /* Implement Logic */ }
    public static void castMageArmor(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 24000, 0));
    }
    public static void castMagicMissile(ServerPlayer p) { /* Implement Logic */ }
    public static void castProtectionFromEvilAndGood(ServerPlayer p) { /* Implement Logic */ }
    public static void castPurifyFoodAndDrink(ServerPlayer p) { /* Implement Logic */ }
    public static void castRayOfSickness(ServerPlayer p) { /* Implement Logic */ }
    public static void castSanctuary(ServerPlayer p) { /* Implement Logic */ }
    public static void castSearingSmite(ServerPlayer p) { /* Implement Logic */ }
    public static void castShield(ServerPlayer p) { /* Implement Logic */ }
    public static void castShieldOfFaith(ServerPlayer p) { /* Implement Logic */ }
    public static void castSilentImage(ServerPlayer p) { /* Implement Logic */ }
    public static void castSleep(ServerPlayer p) { /* Implement Logic */ }
    public static void castSpeakWithAnimals(ServerPlayer p) { /* Implement Logic */ }
    public static void castThunderwave(ServerPlayer p) {
        double radius = 5.0;
        // Alle LivingEntities im Umkreis finden
        var entities = p.level().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(radius));

        for (LivingEntity target : entities) {
            if (target != p) { // Den Zauberer selbst schützen

                target.hurt(p.damageSources().source(DamageTypes.MAGIC, p), 5.5F);

                ((net.minecraft.server.level.ServerLevel)p.level()).sendParticles(
                        ParticleTypes.CLOUD,
                        target.getX(), target.getY() + 0.5, target.getZ(), 5, 0.2, 0.2, 0.2, 0.01);
            }
        }

        p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.5F);
    }
    public static void castUnseenServant(ServerPlayer p) { /* Implement Logic */ }

    // --- Grade 2 ---
    public static void castAcidArrow(ServerPlayer p) { /* Logic */ }
    public static void castAid(ServerPlayer caster, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 24000, 1, false, true));
        target.heal(8.0F); // Sofortige Heilung der neuen HP
    }
    public static void castAlterSelf(ServerPlayer p) { /* Logic */ }
    public static void castAnimalMessenger(ServerPlayer p) { /* Logic */ }
    public static void castArcaneLock(ServerPlayer p) { /* Logic */ }
    public static void castArcanistsMagicAura(ServerPlayer p) { /* Logic */ }
    public static void castAugury(ServerPlayer p) { /* Logic */ }
    public static void castBarkskin(ServerPlayer p) { /* Logic */ }
    public static void castBlindnessDeafness(ServerPlayer p) { /* Logic */ }
    public static void castBlur(ServerPlayer p) { /* Logic */ }
    public static void castCalmEmotions(ServerPlayer p) { /* Logic */ }
    public static void castContinualFlame(ServerPlayer p) { /* Logic */ }
    public static void castDarkness(ServerPlayer p) { /* Logic */ }
    public static void castDarkvision(ServerPlayer p) { /* Logic */ }
    public static void castDetectThoughts(ServerPlayer p) { /* Logic */ }
    public static void castDragonsBreath(ServerPlayer p) { /* Logic */ }
    public static void castEnhanceAbility(ServerPlayer p) { /* Logic */ }
    public static void castEnlargeReduce(ServerPlayer p) { /* Logic */ }
    public static void castEnthrall(ServerPlayer p) { /* Logic */ }
    public static void castFindSteed(ServerPlayer p) { /* Logic */ }
    public static void castFindTraps(ServerPlayer p) { /* Logic */ }
    public static void castFlameBlade(ServerPlayer p) { /* Logic */ }
    public static void castFlamingSphere(ServerPlayer p) { /* Logic */ }
    public static void castGentleRepose(ServerPlayer p) { /* Logic */ }
    public static void castGustOfWind(ServerPlayer p) { /* Logic */ }
    public static void castHeatMetal(ServerPlayer p) { /* Logic */ }
    public static void castHoldPerson(ServerPlayer caster, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 10, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 10, false, false));
    }
    public static void castInvisibility(ServerPlayer p) { /* Logic */ }
    public static void castKnock(ServerPlayer p) { /* Logic */ }
    public static void castRestoration(ServerPlayer caster, LivingEntity target) {
        target.removeEffect(MobEffects.BLINDNESS);
        target.removeEffect(MobEffects.SLOWNESS); // Lähmung-Ersatz
        target.removeEffect(MobEffects.POISON);
        target.removeEffect(MobEffects.WITHER);
    }
    public static void castLevitate(ServerPlayer p, LivingEntity target) {
        target.addEffect(new MobEffectInstance(DndModMobEffects.HOVERING, 6000, 0));
    }
    public static void castLocateAnimalsOrPlants(ServerPlayer p) { /* Logic */ }
    public static void castLocateObject(ServerPlayer p) { /* Logic */ }
    public static void castMagicMouth(ServerPlayer p) { /* Logic */ }
    public static void castMagicWeapon(ServerPlayer p) { /* Logic */ }
    public static void castMindSpike(ServerPlayer caster, LivingEntity target) {
        target.hurt(caster.damageSources().source(DamageTypes.MAGIC), 16.0F);
    }
    public static void castMirrorImage(ServerPlayer p) { /* Logic */ }
    public static void castMistyStep(ServerPlayer p) { /* Logic */ }
    public static void castMoonbeam(ServerPlayer p) { /* Logic */ }
    public static void castPassWithoutTrace(ServerPlayer p) { /* Logic */ }
    public static void castPhantalmalForce(ServerPlayer p) { /* Logic */ }
    public static void castPrayerOfHealing(ServerPlayer p) { /* Logic */ }
    public static void castProtectionFromPoison(ServerPlayer p) { /* Logic */ }
    public static void castRayOfEnfeeblement(ServerPlayer p) { /* Logic */ }
    public static void castRopeTrick(ServerPlayer p) { /* Logic */ }
    public static void castScorchingRay(ServerPlayer p) { /* Logic */ }
    public static void castSeeInvisibility(ServerPlayer p) { /* Logic */ }
    public static void castShatter(ServerPlayer p) { /* Logic */ }
    public static void castShiningSmite(ServerPlayer p) { /* Logic */ }
    public static void castSilence(ServerPlayer p) { /* Logic */ }
    public static void castSpiderClimb(ServerPlayer p) { /* Logic */ }
    public static void castSpikeGrowth(ServerPlayer p) { /* Logic */ }
    public static void castSpiritualWeapon(ServerPlayer p) { /* Logic */ }
    public static void castSuggestion(ServerPlayer p) { /* Logic */ }
    public static void castWardingBond(ServerPlayer p) { /* Logic */ }
    public static void castWeb(ServerPlayer p) { /* Logic */ }
    public static void castZoneOfTruth(ServerPlayer p) { /* Logic */ }

    // --- Grade 3 ---
    public static void castAnimateDead(ServerPlayer p) { /* Logic */ }
    public static void castBeaconOfHope(ServerPlayer p) { /* Logic */ }
    public static void castBestowCurse(ServerPlayer caster, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 0));
        target.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 1200, 2));
    }
    public static void castBlink(ServerPlayer p) { /* Logic */ }
    public static void castCallLightning(ServerPlayer p) { /* Logic */ }
    public static void castClairvoyance(ServerPlayer p) { /* Logic */ }
    public static void castConjureAnimals(ServerPlayer p) { /* Logic */ }
    public static void castCounterspell(ServerPlayer p) { /* Logic */ }
    public static void castCreateFoodAndWater(ServerPlayer p) { /* Logic */ }
    public static void castDaylight(ServerPlayer p) { /* Logic */ }
    public static void castDispelMagic(ServerPlayer p) { /* Logic */ }
    public static void castFear(ServerPlayer p) { /* Logic */ }
    public static void castFireball(ServerPlayer caster, double range) {
        if (caster.level().isClientSide()) return;

        Vec3 lookVec = caster.getViewVector(1.0F);
        double speed = 5.0;
        Vec3 velocity = lookVec.scale(speed);

        double x = caster.getX() + lookVec.x;
        double y = caster.getY() + caster.getEyeHeight() + lookVec.y * 0.5;
        double z = caster.getZ() + lookVec.z;

        LargeFireball fireball = new LargeFireball(caster.level(), caster, velocity, 4);
        fireball.setPos(x, y, z);
        fireball.setDeltaMovement(velocity);
        fireball.hasImpulse = true;

        // Store range data in NBT
        CompoundTag nbt = fireball.getPersistentData();
        nbt.putDouble("spell_max_range", range);
        nbt.putDouble("spell_start_x", x);
        nbt.putDouble("spell_start_y", y);
        nbt.putDouble("spell_start_z", z);
        nbt.putString("spell_name", "FIREBALL");

        caster.level().addFreshEntity(fireball);

        caster.level().playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 2.0f, 0.8f);
    }
    public static void castFly(ServerPlayer p) { /* Logic */ }
    public static void castGaseousForm(ServerPlayer p) { /* Logic */ }
    public static void castGlyphOfWarding(ServerPlayer p) { /* Logic */ }
    public static void castHaste(ServerPlayer p) { /* Logic */ }
    public static void castHypnoticPattern(ServerPlayer p) { /* Logic */ }
    public static void castLightningBolt(ServerPlayer p) { /* Logic */ }
    public static void castMagicCircle(ServerPlayer p) { /* Logic */ }
    public static void castMajorImage(ServerPlayer p) { /* Logic */ }
    public static void castMassHealingWord(ServerPlayer p) { /* Logic */ }
    public static void castMeldIntoStone(ServerPlayer p) { /* Logic */ }
    public static void castNondetection(ServerPlayer p) { /* Logic */ }
    public static void castPhantomSteed(ServerPlayer p) { /* Logic */ }
    public static void castPlantGrowth(ServerPlayer p) { /* Logic */ }
    public static void castProtectionFromEnergy(ServerPlayer p) { /* Logic */ }
    public static void castRemoveCurse(ServerPlayer p) { /* Logic */ }
    public static void castRevivify(ServerPlayer p) { /* Logic */ }
    public static void castSending(ServerPlayer p) { /* Logic */ }
    public static void castSleetStorm(ServerPlayer p) { /* Logic */ }
    public static void castSlow(ServerPlayer p) { /* Logic */ }
    public static void castSpeakWithDead(ServerPlayer p) { /* Logic */ }
    public static void castSpeakWithPlants(ServerPlayer p) { /* Logic */ }
    public static void castSpiritGuardians(ServerPlayer p) { /* Logic */ }
    public static void castStinkingCloud(ServerPlayer p) { /* Logic */ }
    public static void castTinyHut(ServerPlayer p) { /* Logic */ }
    public static void castTongues(ServerPlayer p) { /* Logic */ }
    public static void castVampiricTouch(ServerPlayer p) { /* Logic */ }
    public static void castWaterBreathing(ServerPlayer p, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 48000, 0, false, false));
    }
    public static void castWaterWalk(ServerPlayer p) { /* Logic */ }
    public static void castWindWall(ServerPlayer p) { /* Logic */ }

    // --- Grade 4 ---
    public static void castArcaneEye(ServerPlayer p) { /* Logic */ }
    public static void castAuraOfLife(ServerPlayer p) { /* Logic */ }
    public static void castBanishment(ServerPlayer p) { /* Logic */ }
    public static void castBlackTentacles(ServerPlayer p) { /* Logic */ }
    public static void castBlight(ServerPlayer caster, LivingEntity target) {
        target.hurt(caster.damageSources().source(DamageTypes.MAGIC, caster), 38.0F);
    }
    public static void castCharmMonster(ServerPlayer p) { /* Logic */ }
    public static void castCompulsion(ServerPlayer p) { /* Logic */ }
    public static void castConfusion(ServerPlayer p) { /* Logic */ }
    public static void castConjureMinorElementals(ServerPlayer p) { /* Logic */ }
    public static void castConjureWoodlandBeings(ServerPlayer p) { /* Logic */ }
    public static void castControlWater(ServerPlayer p) { /* Logic */ }
    public static void castDeathWard(ServerPlayer p) { /* Logic */ }
    public static void castDimensionDoor(ServerPlayer p) { /* Logic */ }
    public static void castDivination(ServerPlayer p) { /* Logic */ }
    public static void castDominateBeast(ServerPlayer p) { /* Logic */ }
    public static void castFabricate(ServerPlayer p) { /* Logic */ }
    public static void castFaithfulHound(ServerPlayer p) { /* Logic */ }
    public static void castFireShield(ServerPlayer p) { /* Logic */ }
    public static void castFreedomOfMovement(ServerPlayer p) { /* Logic */ }
    public static void castGiantInsect(ServerPlayer p) { /* Logic */ }
    public static void castGreaterInvisibility(ServerPlayer p, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200, 0));
    }
    public static void castGuardianOfFaith(ServerPlayer p) { /* Logic */ }
    public static void castHallucinatoryTerrain(ServerPlayer p) { /* Logic */ }
    public static void castIceStorm(ServerPlayer p) { /* Logic */ }
    public static void castLocateCreature(ServerPlayer p) { /* Logic */ }
    public static void castPhantasmalKiller(ServerPlayer p) { /* Logic */ }
    public static void castPolymorph(ServerPlayer p) { /* Logic */ }
    public static void castPrivateSanctum(ServerPlayer p) { /* Logic */ }
    public static void castResilientSphere(ServerPlayer p) { /* Logic */ }
    public static void castSecretChest(ServerPlayer p) { /* Logic */ }
    public static void castStoneskin(ServerPlayer p) { /* Logic */ }
    public static void castStoneShape(ServerPlayer p) { /* Logic */ }
    public static void castVitriolicSphere(ServerPlayer p) { /* Logic */ }
    public static void castWallOfFire(ServerPlayer p) { /* Logic */ }

    // --- Grade 5 ---
    public static void castAnimateObjects(ServerPlayer p) { /* Logic */ }
    public static void castAntilifeShell(ServerPlayer p) { /* Logic */ }
    public static void castArcaneHand(ServerPlayer p) { /* Logic */ }
    public static void castAwaken(ServerPlayer p) { /* Logic */ }
    public static void castCloudkill(ServerPlayer p) { /* Logic */ }
    public static void castCommune(ServerPlayer p) { /* Logic */ }
    public static void castCommuneWithNature(ServerPlayer p) { /* Logic */ }
    public static void castConeOfCold(ServerPlayer p) { /* Logic */ }
    public static void castConjureElemental(ServerPlayer p) { /* Logic */ }
    public static void castContactOtherPlane(ServerPlayer p) { /* Logic */ }
    public static void castContagion(ServerPlayer p) { /* Logic */ }
    public static void castCreation(ServerPlayer p) { /* Logic */ }
    public static void castDispelEvilAndGood(ServerPlayer p) { /* Logic */ }
    public static void castDominatePerson(ServerPlayer p) { /* Logic */ }
    public static void castDream(ServerPlayer p) { /* Logic */ }
    public static void castFlameStrike(ServerPlayer p) { /* Logic */ }
    public static void castGeas(ServerPlayer p) { /* Logic */ }
    public static void castGreaterRestoration(ServerPlayer p) { /* Logic */ }
    public static void castHallow(ServerPlayer p) { /* Logic */ }
    public static void castHoldMonster(ServerPlayer p) { /* Logic */ }
    public static void castInsectPlague(ServerPlayer p) { /* Logic */ }
    public static void castLegendLore(ServerPlayer p) { /* Logic */ }
    public static void castMassCureWounds(ServerPlayer p) { /* Logic */ }
    public static void castMislead(ServerPlayer p) { /* Logic */ }
    public static void castModifyMemory(ServerPlayer p) { /* Logic */ }
    public static void castPasswall(ServerPlayer p) { /* Logic */ }
    public static void castPlanarBinding(ServerPlayer p) { /* Logic */ }
    public static void castRaiseDead(ServerPlayer p) { /* Logic */ }
    public static void castReincarnate(ServerPlayer p) { /* Logic */ }
    public static void castScrying(ServerPlayer p) { /* Logic */ }
    public static void castSeeming(ServerPlayer p) { /* Logic */ }
    public static void castSummonDragon(ServerPlayer p) { /* Logic */ }
    public static void castTelekinesis(ServerPlayer p) { /* Logic */ }
    public static void castTelepathicBond(ServerPlayer p) { /* Logic */ }
    public static void castTeleportationCircle(ServerPlayer p) { /* Logic */ }
    public static void castTreeStride(ServerPlayer p) { /* Logic */ }
    public static void castWallOfForce(ServerPlayer p) { /* Logic */ }
    public static void castWallOfStone(ServerPlayer p) { /* Logic */ }

    // --- Grade 6 ---
    public static void castBladeBarrier(ServerPlayer p) { /* Logic */ }
    public static void castChainLightning(ServerPlayer p) { /* Logic */ }
    public static void castCircleOfDeath(ServerPlayer p) { /* Logic */ }
    public static void castConjureFey(ServerPlayer p) { /* Logic */ }
    public static void castContingency(ServerPlayer p) { /* Logic */ }
    public static void castCreateUndead(ServerPlayer p) { /* Logic */ }
    public static void castDisintegrate(ServerPlayer p) { /* Logic */ }
    public static void castEyebite(ServerPlayer p) { /* Logic */ }
    public static void castFindThePath(ServerPlayer p) { /* Logic */ }
    public static void castFleshToStone(ServerPlayer p) { /* Logic */ }
    public static void castForbiddance(ServerPlayer p) { /* Logic */ }
    public static void castFreezingSphere(ServerPlayer p) { /* Logic */ }
    public static void castGlobeOfInvulnerability(ServerPlayer p) { /* Logic */ }
    public static void castGuardsAndWards(ServerPlayer p) { /* Logic */ }
    public static void castHarm(ServerPlayer p) { /* Logic */ }
    public static void castHeal(ServerPlayer p) { /* Logic */ }
    public static void castHeroesFeast(ServerPlayer p) { /* Logic */ }
    public static void castInstantSummons(ServerPlayer p) { /* Logic */ }
    public static void castIrresistibleDance(ServerPlayer p) { /* Logic */ }
    public static void castMagicJar(ServerPlayer p) { /* Logic */ }
    public static void castMassSuggestion(ServerPlayer p) { /* Logic */ }
    public static void castMoveEarth(ServerPlayer p) { /* Logic */ }
    public static void castPlanarAlly(ServerPlayer p) { /* Logic */ }
    public static void castProgrammedIllusion(ServerPlayer p) { /* Logic */ }
    public static void castSunbeam(ServerPlayer p) { /* Logic */ }
    public static void castTransportViaPlants(ServerPlayer p) { /* Logic */ }
    public static void castTrueSeeing(ServerPlayer p) { /* Logic */ }
    public static void castWallOfIce(ServerPlayer p) { /* Logic */ }
    public static void castWallOfThorns(ServerPlayer p) { /* Logic */ }
    public static void castWindWalk(ServerPlayer p) { /* Logic */ }
    public static void castWordOfRecall(ServerPlayer p) { /* Logic */ }

    // --- Grade 7 ---
    public static void castArcaneSword(ServerPlayer p) { /* Logic */ }
    public static void castConjureCelestial(ServerPlayer p) { /* Logic */ }
    public static void castDelayedBlastFireball(ServerPlayer p) { /* Logic */ }
    public static void castDivineWord(ServerPlayer p) { /* Logic */ }
    public static void castEtherealness(ServerPlayer p) { /* Logic */ }
    public static void castFingerOfDeath(ServerPlayer p) { /* Logic */ }
    public static void castFireStorm(ServerPlayer p) { /* Logic */ }
    public static void castForcecage(ServerPlayer p) { /* Logic */ }
    public static void castMagnificentMansion(ServerPlayer p) { /* Logic */ }
    public static void castMirageArcane(ServerPlayer p) { /* Logic */ }
    public static void castPlaneShift(ServerPlayer p) { /* Logic */ }
    public static void castPrismaticSpray(ServerPlayer p) { /* Logic */ }
    public static void castProjectImage(ServerPlayer p) { /* Logic */ }
    public static void castRegenerate(ServerPlayer p) { /* Logic */ }
    public static void castResurrection(ServerPlayer p) { /* Logic */ }
    public static void castReverseGravity(ServerPlayer p) { /* Logic */ }
    public static void castSequester(ServerPlayer p) { /* Logic */ }
    public static void castSimulacrum(ServerPlayer p) { /* Logic */ }
    public static void castSymbol(ServerPlayer p) { /* Logic */ }
    public static void castTeleport(ServerPlayer p) { /* Logic */ }

    // --- Grade 8 ---
    public static void castAnimalShapes(ServerPlayer p) { /* Logic */ }
    public static void castAntimagicField(ServerPlayer p) { /* Logic */ }
    public static void castAntipathySympathy(ServerPlayer p) { /* Logic */ }
    public static void castBefuddlement(ServerPlayer p) { /* Logic */ }
    public static void castClone(ServerPlayer p) { /* Logic */ }
    public static void castControlWeather(ServerPlayer p) { /* Logic */ }
    public static void castDemiplane(ServerPlayer p) { /* Logic */ }
    public static void castDominateMonster(ServerPlayer p) { /* Logic */ }
    public static void castEarthquake(ServerPlayer p) { /* Logic */ }
    public static void castGlibness(ServerPlayer p) { /* Logic */ }
    public static void castHolyAura(ServerPlayer p) { /* Logic */ }
    public static void castIncendiaryCloud(ServerPlayer p) { /* Logic */ }
    public static void castMaze(ServerPlayer p) { /* Logic */ }
    public static void castMindBlank(ServerPlayer p) { /* Logic */ }
    public static void castPowerWordStun(ServerPlayer p) { /* Logic */ }
    public static void castSunburst(ServerPlayer p) { /* Logic */ }
    public static void castTsunami(ServerPlayer p) { /* Logic */ }

    // --- Grade 9 ---
    public static void castAstralProjection(ServerPlayer p) { /* Logic */ }
    public static void castForesight(ServerPlayer p) { /* Logic */ }
    public static void castGate(ServerPlayer p) { /* Logic */ }
    public static void castImprisonment(ServerPlayer p) { /* Logic */ }
    public static void castMassHeal(ServerPlayer p) { /* Logic */ }
    public static void castMeteorSwarm(ServerPlayer p) { /* Logic */ }
    public static void castPowerWordHeal(ServerPlayer p) { /* Logic */ }
    public static void castPowerWordKill(ServerPlayer p) { /* Logic */ }
    public static void castPrismaticWall(ServerPlayer p) { /* Logic */ }
    public static void castShapechange(ServerPlayer p) { /* Logic */ }
    public static void castStormOfVengeance(ServerPlayer p) { /* Logic */ }
    public static void castTimeStop(ServerPlayer p) { /* Logic */ }
    public static void castTruePolymorph(ServerPlayer p) { /* Logic */ }
    public static void castTrueResurrection(ServerPlayer p) { /* Logic */ }
    public static void castWeird(ServerPlayer p) { /* Logic */ }
    public static void castWish(ServerPlayer p) { /* Logic */ }
}
