package net.luderspieler.dnd.entity;

import net.luderspieler.dnd.init.DndModEntities;
import net.luderspieler.dnd.procedures.ScareCrowAttackConditionProcedure;
import net.luderspieler.dnd.procedures.ScareCrowAttackPlaybackConditionProcedure;
import net.luderspieler.dnd.procedures.ScareCrowIdleCooldownProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class ScarecrowEntity extends Monster {
	public static final EntityDataAccessor<Integer> DATA_cd = SynchedEntityData.defineId(ScarecrowEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_anim = SynchedEntityData.defineId(ScarecrowEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_frozen = SynchedEntityData.defineId(ScarecrowEntity.class, EntityDataSerializers.BOOLEAN);
	public final AnimationState animationState0 = new AnimationState();
	public final AnimationState animationState2 = new AnimationState();

	public ScarecrowEntity(EntityType<ScarecrowEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_cd, 0);
		builder.define(DATA_anim, "idle");
		builder.define(DATA_frozen, false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
			}

			@Override
			public boolean canUse() {
				double x = ScarecrowEntity.this.getX();
				double y = ScarecrowEntity.this.getY();
				double z = ScarecrowEntity.this.getZ();
				Entity entity = ScarecrowEntity.this;
				Level world = ScarecrowEntity.this.level();
				return super.canUse() && ScareCrowAttackConditionProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = ScarecrowEntity.this.getX();
				double y = ScarecrowEntity.this.getY();
				double z = ScarecrowEntity.this.getZ();
				Entity entity = ScarecrowEntity.this;
				Level world = ScarecrowEntity.this.level();
				return super.canContinueToUse() && ScareCrowAttackConditionProcedure.execute(entity);
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Player.class, false, false) {
			@Override
			public boolean canUse() {
				double x = ScarecrowEntity.this.getX();
				double y = ScarecrowEntity.this.getY();
				double z = ScarecrowEntity.this.getZ();
				Entity entity = ScarecrowEntity.this;
				Level world = ScarecrowEntity.this.level();
				return super.canUse() && ScareCrowAttackConditionProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = ScarecrowEntity.this.getX();
				double y = ScarecrowEntity.this.getY();
				double z = ScarecrowEntity.this.getZ();
				Entity entity = ScarecrowEntity.this;
				Level world = ScarecrowEntity.this.level();
				return super.canContinueToUse() && ScareCrowAttackConditionProcedure.execute(entity);
			}
		});
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("entity.generic.death"));
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damagesource, float amount) {
		if (damagesource.getDirectEntity() instanceof AbstractThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud || damagesource.typeHolder().is(NeoForgeMod.POISON_DAMAGE))
			return false;
		if (damagesource.is(DamageTypes.FALL))
			return false;
		if (damagesource.is(DamageTypes.DROWN))
			return false;
		if (damagesource.is(DamageTypes.DRAGON_BREATH))
			return false;
		return super.hurtServer(level, damagesource, amount);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput valueOutput) {
		super.addAdditionalSaveData(valueOutput);
		valueOutput.putInt("Datacd", this.entityData.get(DATA_cd));
		valueOutput.putString("Dataanim", this.entityData.get(DATA_anim));
		valueOutput.putBoolean("Datafrozen", this.entityData.get(DATA_frozen));
	}

	@Override
	public void readAdditionalSaveData(ValueInput valueInput) {
		super.readAdditionalSaveData(valueInput);
		this.entityData.set(DATA_cd, valueInput.getIntOr("Datacd", 0));
		this.entityData.set(DATA_anim, valueInput.getStringOr("Dataanim", ""));
		this.entityData.set(DATA_frozen, valueInput.getBooleanOr("Datafrozen", false));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			this.animationState0.animateWhen(true, this.tickCount);
			this.animationState2.animateWhen(ScareCrowAttackPlaybackConditionProcedure.execute(this), this.tickCount);
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		ScareCrowIdleCooldownProcedure.execute(this);
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(DndModEntities.SCARECROW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
				RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 72);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 100);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		return builder;
	}
}