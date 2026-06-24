package net.luderspieler.dnd.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class ScarecrowEntityIsHurtProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double possibleX = 0;
		double possibleY = 0;
		if ((world.getBlockState(BlockPos.containing(x, y - 0.5, z))) == Blocks.DIRT_PATH.defaultBlockState()) {
			possibleY = z + Mth.nextInt(RandomSource.create(), -10, 10);
			possibleX = x + Mth.nextInt(RandomSource.create(), -10, 10);
			while (!((world.getBlockState(BlockPos.containing(possibleX, y - 0.5, possibleY))) == Blocks.DIRT_PATH.defaultBlockState())) {
				possibleY = z + Mth.nextInt(RandomSource.create(), -10, 10);
				possibleX = x + Mth.nextInt(RandomSource.create(), -10, 10);
			}
			{
				Entity _ent = entity;
				_ent.teleportTo(possibleX, y, possibleY);
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport(possibleX, y, possibleY, _ent.getYRot(), _ent.getXRot());
			}
		}
	}
}