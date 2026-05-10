/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;

import net.luderspieler.dnd.DndMod;

import java.util.function.Function;

public class DndModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(DndMod.MODID);
	public static final DeferredItem<Item> STIRGE_SPAWN_EGG;
	public static final DeferredItem<Item> SCARECROW_SPAWN_EGG;
	public static final DeferredItem<Item> HARPY_SPAWN_EGG;
	public static final DeferredItem<Item> NOTHIC_SPAWN_EGG;
	public static final DeferredItem<Item> VAMPIRE_SPAWN_EGG;
	public static final DeferredItem<Item> MEDUSA_SPAWN_EGG;
	static {
		STIRGE_SPAWN_EGG = register("stirge_spawn_egg", properties -> new SpawnEggItem(DndModEntities.STIRGE.get(), properties));
		SCARECROW_SPAWN_EGG = register("scarecrow_spawn_egg", properties -> new SpawnEggItem(DndModEntities.SCARECROW.get(), properties));
		HARPY_SPAWN_EGG = register("harpy_spawn_egg", properties -> new SpawnEggItem(DndModEntities.HARPY.get(), properties));
		NOTHIC_SPAWN_EGG = register("nothic_spawn_egg", properties -> new SpawnEggItem(DndModEntities.NOTHIC.get(), properties));
		VAMPIRE_SPAWN_EGG = register("vampire_spawn_egg", properties -> new SpawnEggItem(DndModEntities.VAMPIRE.get(), properties));
		MEDUSA_SPAWN_EGG = register("medusa_spawn_egg", properties -> new SpawnEggItem(DndModEntities.MEDUSA.get(), properties));
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> supplier) {
		return REGISTRY.registerItem(name, supplier, new Item.Properties());
	}
}