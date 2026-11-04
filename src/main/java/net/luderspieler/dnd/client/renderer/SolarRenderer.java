package net.luderspieler.dnd.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.AnimationDefinition;

import net.luderspieler.dnd.entity.SolarEntity;
import net.luderspieler.dnd.client.model.animations.solarAnimation;
import net.luderspieler.dnd.client.model.ModelSolar;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

public class SolarRenderer extends MobRenderer<SolarEntity, LivingEntityRenderState, ModelSolar> {
	private SolarEntity entity = null;
	private final ResourceLocation entityTexture = ResourceLocation.parse("dnd:textures/entities/solar.png");

	public SolarRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(ModelSolar.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public void extractRenderState(SolarEntity entity, LivingEntityRenderState state, float partialTicks) {
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

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
		poseStack.scale(1.75f, 1.75f, 1.75f);
	}

	private static final class AnimatedModel extends ModelSolar {
		private SolarEntity entity = null;
		private final KeyframeAnimation keyframeAnimation0;
		private final KeyframeAnimation keyframeAnimation1;
		private final KeyframeAnimation keyframeAnimation2;
		private final KeyframeAnimation keyframeAnimation3;

		public AnimatedModel(ModelPart root) {
			super(root);
			this.keyframeAnimation0 = safeBake(solarAnimation.idle);
			this.keyframeAnimation1 = safeBake(solarAnimation.walk);
			this.keyframeAnimation2 = safeBake(solarAnimation.sword);
			this.keyframeAnimation3 = safeBake(solarAnimation.bow);
		}

		private KeyframeAnimation safeBake(AnimationDefinition source) {
			try {
				return source.bake(root);
			} catch (IllegalArgumentException e) {
				return new AnimationDefinition(0, false, Map.of()).bake(root);
			}
		}

		public void setEntity(SolarEntity entity) {
			this.entity = entity;
		}

		@Override
		public void setupAnim(LivingEntityRenderState state) {
			this.root().getAllParts().forEach(ModelPart::resetPose);
			this.keyframeAnimation0.apply(entity.animationState0, state.ageInTicks, 1f);
			this.keyframeAnimation1.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1f, 1f);
			this.keyframeAnimation2.apply(entity.animationState2, state.ageInTicks, 1f);
			this.keyframeAnimation3.apply(entity.animationState3, state.ageInTicks, 1f);
			super.setupAnim(state);
		}
	}
}