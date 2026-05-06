/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.DndMod;

public class DndModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, DndMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> HARPY_RIZZ = REGISTRY.register("harpy_rizz", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("dnd", "harpy_rizz")));
}