package net.luderspieler.dnd.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class ModelStirge extends EntityModel<LivingEntityRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("dnd", "model_stirge"), "main");
	public final ModelPart all;
	public final ModelPart bone;
	public final ModelPart bone11;
	public final ModelPart bone10;
	public final ModelPart bone2;
	public final ModelPart bone3;
	public final ModelPart bone4;
	public final ModelPart bone5;
	public final ModelPart bone6;
	public final ModelPart bone7;
	public final ModelPart bone9;

	public ModelStirge(ModelPart root) {
		super(root);
		this.all = root.getChild("all");
		this.bone = this.all.getChild("bone");
		this.bone11 = this.bone.getChild("bone11");
		this.bone10 = this.bone.getChild("bone10");
		this.bone2 = this.bone.getChild("bone2");
		this.bone3 = this.bone2.getChild("bone3");
		this.bone4 = this.bone.getChild("bone4");
		this.bone5 = this.bone.getChild("bone5");
		this.bone6 = this.bone.getChild("bone6");
		this.bone7 = this.bone.getChild("bone7");
		this.bone9 = this.bone.getChild("bone9");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 0.0F));
		PartDefinition bone = all.addOrReplaceChild("bone",
				CubeListBuilder.create().texOffs(22, 24).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(43, 10).addBox(-1.0F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(43, 10)
						.addBox(0.0F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(43, 10).addBox(1.0F, -3.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -2.0F, 0.0F));
		PartDefinition bone11 = bone.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(0, 8).addBox(-9.0F, 0.0F, -2.0F, 9.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -2.0F, 0.0F));
		PartDefinition bone10 = bone.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -2.0F, 9.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -2.0F, 0.0F));
		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, -2.0F));
		PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(22, 16).addBox(-2.0F, 0.0F, -6.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -3.0F));
		PartDefinition bone4 = bone.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(12, 26).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, -2.0F));
		PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, -2.0F));
		PartDefinition bone6 = bone.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(30, 7).addBox(0.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 1.0F));
		PartDefinition bone7 = bone.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 1.0F));
		PartDefinition bone9 = bone.addOrReplaceChild("bone9",
				CubeListBuilder.create().texOffs(21, 38).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-1.5F, -0.1F, 0.1F, 4.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-0.5F, -1.9F, 1.9F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public void setupAnim(LivingEntityRenderState state) {
		float limbSwing = state.walkAnimationPos;
		float limbSwingAmount = state.walkAnimationSpeed;
		float ageInTicks = state.ageInTicks;
		float netHeadYaw = state.yRot;
		float headPitch = state.xRot;

	}
}