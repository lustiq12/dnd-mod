/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.DndMod;

@EventBusSubscriber
public class DndModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DndMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DND_WEAPONS = REGISTRY.register("dnd_weapons",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.dnd.dnd_weapons")).icon(() -> new ItemStack(DndModItems.SPEAR.get())).displayItems((parameters, tabData) -> {
				tabData.accept(DndModItems.SPEAR.get());
			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(DndModItems.STIRGE_SPAWN_EGG.get());
			tabData.accept(DndModItems.SCARECROW_SPAWN_EGG.get());
			tabData.accept(DndModItems.HARPY_SPAWN_EGG.get());
			tabData.accept(DndModItems.NOTHIC_SPAWN_EGG.get());
			tabData.accept(DndModItems.VAMPIRE_SPAWN_EGG.get());
			tabData.accept(DndModItems.MEDUSA_SPAWN_EGG.get());
			tabData.accept(DndModItems.GELATINOUS_CUBE_SPAWN_EGG.get());
			tabData.accept(DndModItems.GORISTRO_SPAWN_EGG.get());
			tabData.accept(DndModItems.GOBLIN_HENCHMAN_SPAWN_EGG.get());
			tabData.accept(DndModItems.DWARVEN_SMITH_SPAWN_EGG.get());
		}
	}
}