/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

import net.luderspieler.dnd.block.ScarecrowBlockBlock;
import net.luderspieler.dnd.DndMod;

import java.util.function.Function;

public class DndModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(DndMod.MODID);
	public static final DeferredBlock<Block> SCARECROW_BLOCK;
	static {
		SCARECROW_BLOCK = register("scarecrow_block", ScarecrowBlockBlock::new);
	}

	// Start of user code block custom blocks
	// End of user code block custom blocks
	private static <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, ? extends B> supplier) {
		return REGISTRY.registerBlock(name, supplier);
	}
}