/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.luderspieler.dnd.DndMod;

@EventBusSubscriber
public class DndModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DndMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DN_D_MOBS = REGISTRY.register("dn_d_mobs",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.dnd.dn_d_mobs")).icon(() -> new ItemStack(Blocks.AIR)).displayItems((parameters, tabData) -> {
				tabData.accept(DndModItems.STIRGE_SPAWN_EGG.get());
				tabData.accept(DndModItems.SCARECROW_SPAWN_EGG.get());
				tabData.accept(DndModItems.HARPY_SPAWN_EGG.get());
				tabData.accept(DndModItems.NOTHIC_SPAWN_EGG.get());
				tabData.accept(DndModItems.VAMPIRE_SPAWN_EGG.get());
				tabData.accept(DndModItems.MEDUSA_SPAWN_EGG.get());
				tabData.accept(DndModItems.GELATINOUS_CUBE_SPAWN_EGG.get());
				tabData.accept(DndModItems.GORISTRO_SPAWN_EGG.get());
				tabData.accept(DndModItems.GOBLIN_HENCHMAN_SPAWN_EGG.get());
			}).withSearchBar().build());
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DND_EQUIPMENT = REGISTRY.register("dnd_equipment",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.dnd.dnd_equipment")).icon(() -> new ItemStack(DndModItems.SPEAR.get())).displayItems((parameters, tabData) -> {
				tabData.accept(DndModItems.SPEAR.get());
			}).withSearchBar().withTabsBefore(DN_D_MOBS.getId()).build());
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DN_D_OTHER_ITEMS = REGISTRY.register("dn_d_other_items",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.dnd.dn_d_other_items")).icon(() -> new ItemStack(Blocks.AIR)).displayItems((parameters, tabData) -> {
				tabData.accept(DndModItems.COPPER_COIN.get());
				tabData.accept(DndModItems.SILVER_COIN.get());
				tabData.accept(DndModItems.GOLD_COIN.get());
				tabData.accept(DndModItems.PLATINUM_COIN.get());
				tabData.accept(DndModItems.COIN_BAG.get());
			}).withSearchBar().withTabsBefore(DND_EQUIPMENT.getId()).build());

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
		} else if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			tabData.accept(DndModBlocks.SCARECROW_BLOCK.get().asItem());
		}
	}
}