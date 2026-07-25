package net.luderspieler.dnd.client.screens;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.units.qual.h;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(Dist.CLIENT)
public class ChoiceNeededOverlay {
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("dnd:textures/screens/info.png");

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}

		DndModVariables.PlayerVariables vars = entity.getData(DndModVariables.PLAYER_VARIABLES);

		if (vars != null) {
			if (vars.ChoicesNeeded != null && !vars.ChoicesNeeded.isBlank()) {
				event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, IMAGE_0, w - 13, 0, 0, 0, 16, 16, 16, 16);

				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.dnd.choice_needed.label_choice_needed_press_r"), w - 145, 5, -1, false);
			}
		}
	}
}