package net.luderspieler.dnd.entity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.*;
import net.minecraft.world.Difficulty;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;

import net.luderspieler.dnd.procedures.GoristroRoarPlaybackConditionProcedure;
import net.luderspieler.dnd.procedures.GoristroIdlePlaybackConditionProcedure;
import net.luderspieler.dnd.procedures.GoristroIdleCooldownProcedure;
import net.luderspieler.dnd.procedures.GoristroAttackPlaybackConditionProcedure;
import net.luderspieler.dnd.init.DndModEntities;

public class GoristroEntity extends Monster {
	public static final EntityDataAccessor<Integer> DATA_Cooldown = SynchedEntityData.defineId(GoristroEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_anim = SynchedEntityData.defineId(GoristroEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_rawwwrrr = SynchedEntityData.defineId(GoristroEntity.class, EntityDataSerializers.INT);
	public final AnimationState animationState0 = new AnimationState();
	public final AnimationState animationState2 = new AnimationState();
	public final AnimationState animationState3 = new AnimationState();

	public GoristroEntity(EntityType<GoristroEntity> type, Level world) {
		super(type, world);
		xpReward = 720;
		setNoAi(false);
		refreshDimensions();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_Cooldown, 0);
		builder.define(DATA_anim, "idle");
		builder.define(DATA_rawwwrrr, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
			}
		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Player.class, false, false));
	}

	@Override
	public void addAdditionalSaveData(ValueOutput valueOutput) {
		super.addAdditionalSaveData(valueOutput);
		valueOutput.putInt("DataCooldown", this.entityData.get(DATA_Cooldown));
		valueOutput.putString("Dataanim", this.entityData.get(DATA_anim));
		valueOutput.putInt("Datarawwwrrr", this.entityData.get(DATA_rawwwrrr));
	}

	@Override
	public void readAdditionalSaveData(ValueInput valueInput) {
		super.readAdditionalSaveData(valueInput);
		this.entityData.set(DATA_Cooldown, valueInput.getIntOr("DataCooldown", 0));
		this.entityData.set(DATA_anim, valueInput.getStringOr("Dataanim", ""));
		this.entityData.set(DATA_rawwwrrr, valueInput.getIntOr("Datarawwwrrr", 0));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			this.animationState0.animateWhen(GoristroIdlePlaybackConditionProcedure.execute(this), this.tickCount);
			this.animationState2.animateWhen(GoristroAttackPlaybackConditionProcedure.execute(this), this.tickCount);
			this.animationState3.animateWhen(GoristroRoarPlaybackConditionProcedure.execute(this), this.tickCount);
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		GoristroIdleCooldownProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(2f);
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(DndModEntities.GORISTRO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
				RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 620);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 100);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.1);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}
}