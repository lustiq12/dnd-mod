/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.luderspieler.dnd.client.renderer.*;

@EventBusSubscriber(Dist.CLIENT)
public class DndModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(DndModEntities.STIRGE.get(), StirgeRenderer::new);
		event.registerEntityRenderer(DndModEntities.SCARECROW.get(), ScarecrowRenderer::new);
		event.registerEntityRenderer(DndModEntities.HARPY.get(), HarpyRenderer::new);
		event.registerEntityRenderer(DndModEntities.NOTHIC.get(), NothicRenderer::new);
		event.registerEntityRenderer(DndModEntities.VAMPIRE.get(), VampireRenderer::new);
		event.registerEntityRenderer(DndModEntities.MEDUSA.get(), MedusaRenderer::new);
	}
}