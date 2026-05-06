/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.luderspieler.dnd.client.renderer.StirgeRenderer;
import net.luderspieler.dnd.client.renderer.ScarecrowRenderer;
import net.luderspieler.dnd.client.renderer.HarpyRenderer;

@EventBusSubscriber(Dist.CLIENT)
public class DndModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(DndModEntities.STIRGE.get(), StirgeRenderer::new);
		event.registerEntityRenderer(DndModEntities.SCARECROW.get(), ScarecrowRenderer::new);
		event.registerEntityRenderer(DndModEntities.HARPY.get(), HarpyRenderer::new);
	}
}