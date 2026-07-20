/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.entity.*;
import net.luderspieler.dnd.DndMod;

@EventBusSubscriber
public class DndModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, DndMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<StirgeEntity>> STIRGE = register("stirge",
			EntityType.Builder.<StirgeEntity>of(StirgeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<ScarecrowEntity>> SCARECROW = register("scarecrow",
			EntityType.Builder.<ScarecrowEntity>of(ScarecrowEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<HarpyEntity>> HARPY = register("harpy",
			EntityType.Builder.<HarpyEntity>of(HarpyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<NothicEntity>> NOTHIC = register("nothic",
			EntityType.Builder.<NothicEntity>of(NothicEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 0.6f));
	public static final DeferredHolder<EntityType<?>, EntityType<VampireEntity>> VAMPIRE = register("vampire",
			EntityType.Builder.<VampireEntity>of(VampireEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<MedusaEntity>> MEDUSA = register("medusa",
			EntityType.Builder.<MedusaEntity>of(MedusaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<SpearProjectileEntity>> SPEAR_PROJECTILE = register("spear_projectile",
			EntityType.Builder.<SpearProjectileEntity>of(SpearProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<GelatinousCubeEntity>> GELATINOUS_CUBE = register("gelatinous_cube",
			EntityType.Builder.<GelatinousCubeEntity>of(GelatinousCubeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(1.5f, 1.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<GoristroEntity>> GORISTRO = register("goristro",
			EntityType.Builder.<GoristroEntity>of(GoristroEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<TemplateMobEntity>> TEMPLATE_MOB = register("template_mob",
			EntityType.Builder.<TemplateMobEntity>of(TemplateMobEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<ReusableMobEntity>> REUSABLE_MOB = register("reusable_mob",
			EntityType.Builder.<ReusableMobEntity>of(ReusableMobEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<GoblinHenchmanEntity>> GOBLIN_HENCHMAN = register("goblin_henchman",
			EntityType.Builder.<GoblinHenchmanEntity>of(GoblinHenchmanEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DndMod.MODID, registryname))));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		StirgeEntity.init(event);
		ScarecrowEntity.init(event);
		HarpyEntity.init(event);
		NothicEntity.init(event);
		VampireEntity.init(event);
		MedusaEntity.init(event);
		GelatinousCubeEntity.init(event);
		GoristroEntity.init(event);
		TemplateMobEntity.init(event);
		ReusableMobEntity.init(event);
		GoblinHenchmanEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(STIRGE.get(), StirgeEntity.createAttributes().build());
		event.put(SCARECROW.get(), ScarecrowEntity.createAttributes().build());
		event.put(HARPY.get(), HarpyEntity.createAttributes().build());
		event.put(NOTHIC.get(), NothicEntity.createAttributes().build());
		event.put(VAMPIRE.get(), VampireEntity.createAttributes().build());
		event.put(MEDUSA.get(), MedusaEntity.createAttributes().build());
		event.put(GELATINOUS_CUBE.get(), GelatinousCubeEntity.createAttributes().build());
		event.put(GORISTRO.get(), GoristroEntity.createAttributes().build());
		event.put(TEMPLATE_MOB.get(), TemplateMobEntity.createAttributes().build());
		event.put(REUSABLE_MOB.get(), ReusableMobEntity.createAttributes().build());
		event.put(GOBLIN_HENCHMAN.get(), GoblinHenchmanEntity.createAttributes().build());
	}
}