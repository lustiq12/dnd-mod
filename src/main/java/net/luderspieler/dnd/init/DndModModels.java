/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.luderspieler.dnd.client.model.Modelvampire;
import net.luderspieler.dnd.client.model.Modelscarecrow;
import net.luderspieler.dnd.client.model.Modelnothic;
import net.luderspieler.dnd.client.model.ModelStirge;
import net.luderspieler.dnd.client.model.ModelHarpy;

@EventBusSubscriber(Dist.CLIENT)
public class DndModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelStirge.LAYER_LOCATION, ModelStirge::createBodyLayer);
		event.registerLayerDefinition(Modelvampire.LAYER_LOCATION, Modelvampire::createBodyLayer);
		event.registerLayerDefinition(Modelscarecrow.LAYER_LOCATION, Modelscarecrow::createBodyLayer);
		event.registerLayerDefinition(ModelHarpy.LAYER_LOCATION, ModelHarpy::createBodyLayer);
		event.registerLayerDefinition(Modelnothic.LAYER_LOCATION, Modelnothic::createBodyLayer);
	}
}