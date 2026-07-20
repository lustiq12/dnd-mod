/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.luderspieler.dnd.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.luderspieler.dnd.client.model.*;

@EventBusSubscriber(Dist.CLIENT)
public class DndModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelStirge.LAYER_LOCATION, ModelStirge::createBodyLayer);
		event.registerLayerDefinition(Modelnothic.LAYER_LOCATION, Modelnothic::createBodyLayer);
		event.registerLayerDefinition(Modelshadydwarf.LAYER_LOCATION, Modelshadydwarf::createBodyLayer);
		event.registerLayerDefinition(Modelmedusa.LAYER_LOCATION, Modelmedusa::createBodyLayer);
		event.registerLayerDefinition(Modelvampire.LAYER_LOCATION, Modelvampire::createBodyLayer);
		event.registerLayerDefinition(ModeldwarvenSmith.LAYER_LOCATION, ModeldwarvenSmith::createBodyLayer);
		event.registerLayerDefinition(ModelGoblinWarrior.LAYER_LOCATION, ModelGoblinWarrior::createBodyLayer);
		event.registerLayerDefinition(Modelgoristro.LAYER_LOCATION, Modelgoristro::createBodyLayer);
		event.registerLayerDefinition(Modelscarecrow.LAYER_LOCATION, Modelscarecrow::createBodyLayer);
		event.registerLayerDefinition(ModelSolar.LAYER_LOCATION, ModelSolar::createBodyLayer);
		event.registerLayerDefinition(ModelHarpy.LAYER_LOCATION, ModelHarpy::createBodyLayer);
		event.registerLayerDefinition(Modelgelatinous_cube.LAYER_LOCATION, Modelgelatinous_cube::createBodyLayer);
		event.registerLayerDefinition(ModelGoblinHenchman.LAYER_LOCATION, ModelGoblinHenchman::createBodyLayer);
	}
}