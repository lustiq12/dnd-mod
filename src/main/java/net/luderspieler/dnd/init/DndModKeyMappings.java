/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.luderspieler.dnd.network.PrepareSpellsMessage;
import net.luderspieler.dnd.network.CastSpellMessage;

@EventBusSubscriber(Dist.CLIENT)
public class DndModKeyMappings {
	public static final KeyMapping CAST_SPELL = new KeyMapping("key.dnd.cast_spell", GLFW.GLFW_KEY_F, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new CastSpellMessage(0, 0));
				CastSpellMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping PREPARE_SPELLS = new KeyMapping("key.dnd.prepare_spells", GLFW.GLFW_KEY_G, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketDistributor.sendToServer(new PrepareSpellsMessage(0, 0));
				PrepareSpellsMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(CAST_SPELL);
		event.register(PREPARE_SPELLS);
	}

	@EventBusSubscriber(Dist.CLIENT)
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				CAST_SPELL.consumeClick();
				PREPARE_SPELLS.consumeClick();
			}
		}
	}
}