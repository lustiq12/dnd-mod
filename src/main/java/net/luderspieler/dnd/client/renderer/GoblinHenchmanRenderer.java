package net.luderspieler.dnd.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.AnimationDefinition;

import net.luderspieler.dnd.entity.GoblinHenchmanEntity;
import net.luderspieler.dnd.client.model.animations.GoblinHenchmanAnimation;
import net.luderspieler.dnd.client.model.ModelGoblinHenchman;

import java.util.Map;

public class GoblinHenchmanRenderer extends MobRenderer<GoblinHenchmanEntity, LivingEntityRenderState, ModelGoblinHenchman> {
	private GoblinHenchmanEntity entity = null;
	private final ResourceLocation entityTexture = ResourceLocation.parse("dnd:textures/entities/goblinhenchman.png");

	public GoblinHenchmanRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(ModelGoblinHenchman.LAYER_LOCATION)), 0.3f);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public void extractRenderState(GoblinHenchmanEntity entity, LivingEntityRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		this.entity = entity;
		if (this.model instanceof AnimatedModel) {
			((AnimatedModel) this.model).setEntity(entity);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(LivingEntityRenderState state) {
		return entityTexture;
	}

	private static final class AnimatedModel extends ModelGoblinHenchman {
		private GoblinHenchmanEntity entity = null;
		private final KeyframeAnimation keyframeAnimation0;
		private final KeyframeAnimation keyframeAnimation1;
		private final KeyframeAnimation keyframeAnimation2;

		public AnimatedModel(ModelPart root) {
			super(root);
			this.keyframeAnimation0 = safeBake(GoblinHenchmanAnimation.idle);
			this.keyframeAnimation1 = safeBake(GoblinHenchmanAnimation.walk);
			this.keyframeAnimation2 = safeBake(GoblinHenchmanAnimation.attack);
		}

		private KeyframeAnimation safeBake(AnimationDefinition source) {
			try {
				return source.bake(root);
			} catch (IllegalArgumentException e) {
				return new AnimationDefinition(0, false, Map.of()).bake(root);
			}
		}

		public void setEntity(GoblinHenchmanEntity entity) {
			this.entity = entity;
		}

		@Override
		public void setupAnim(LivingEntityRenderState state) {
			this.root().getAllParts().forEach(ModelPart::resetPose);
			this.keyframeAnimation0.apply(entity.animationState0, state.ageInTicks, 1f);
			this.keyframeAnimation1.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1f, 1f);
			this.keyframeAnimation2.apply(entity.animationState2, state.ageInTicks, 1f);
			super.setupAnim(state);
		}
	}
}