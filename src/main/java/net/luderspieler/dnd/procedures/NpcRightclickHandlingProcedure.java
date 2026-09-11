package net.luderspieler.dnd.procedures;

import net.luderspieler.dnd.npc.NpcInteractionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class NpcRightclickHandlingProcedure {
	public static void execute(double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		NpcInteractionHandler.handleInteract(entity, (Player)sourceentity);
	}
}