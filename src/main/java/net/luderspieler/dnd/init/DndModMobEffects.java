/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.potion.CharmedMobEffect;
import net.luderspieler.dnd.DndMod;

public class DndModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, DndMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> CHARMED = REGISTRY.register("charmed", () -> new CharmedMobEffect());
}