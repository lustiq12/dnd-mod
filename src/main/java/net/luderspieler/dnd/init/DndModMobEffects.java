/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.potion.*;
import net.luderspieler.dnd.DndMod;

public class DndModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, DndMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> CHARMED = REGISTRY.register("charmed", () -> new CharmedMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> GRABBED = REGISTRY.register("grabbed", () -> new GrabbedMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> STUNNED = REGISTRY.register("stunned", () -> new StunnedMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> DECAYING_STARE = REGISTRY.register("decaying_stare", () -> new DecayingStareMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> PETRIFIED = REGISTRY.register("petrified", () -> new PetrifiedMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> HOVERING = REGISTRY.register("hovering", () -> new HoveringMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> RAGE = REGISTRY.register("rage", () -> new RageMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> LARGE_FORM = REGISTRY.register("large_form", () -> new LargeFormMobEffect());
}