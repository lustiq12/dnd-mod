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

// Made with Blockbench 5.1.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelshadydwarf extends EntityModel<LivingEntityRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("dnd", "modelshadydwarf"), "main");
	public final ModelPart Waist;
	public final ModelPart Head;
	public final ModelPart Body;
	public final ModelPart RightArm;
	public final ModelPart LeftArm;
	public final ModelPart RightLeg;
	public final ModelPart LeftLeg;
	public final ModelPart bone;
	public final ModelPart bone2;
	public final ModelPart bb_main;

	public Modelshadydwarf(ModelPart root) {
		super(root);
		this.Waist = root.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.Body = this.Waist.getChild("Body");
		this.RightArm = this.Waist.getChild("RightArm");
		this.LeftArm = this.Waist.getChild("LeftArm");
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
		this.bone = root.getChild("bone");
		this.bone2 = this.bone.getChild("bone2");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 1.0F));
		PartDefinition Head = Waist.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(32, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, -10.0F, 0.0F));
		PartDefinition Body = Waist.addOrReplaceChild("Body",
				CubeListBuilder.create().texOffs(0, 41).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(24, 41).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition RightArm = Waist.addOrReplaceChild("RightArm",
				CubeListBuilder.create().texOffs(48, 57).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(64, 15).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(-5.0F, -9.0F, 0.0F, -0.9312F, -0.3614F, -0.1572F));
		PartDefinition LeftArm = Waist.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(5.0F, -9.0F, 0.0F, -0.9312F, 0.3614F, 0.1572F));
		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(48, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(-1.9F, 13.0F, -1.0F, -0.384F, 0.0177F, 0.0363F));
		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg",
				CubeListBuilder.create().texOffs(16, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(32, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(1.9F, 13.0F, -1.0F, -0.3927F, 0.0F, 0.0F));
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -13.0F));
		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(-2.0F, -12.0F, 0.0F));
		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 108).addBox(-9.0F, -6.0F, -2.0F, 18.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public void setupAnim(LivingEntityRenderState state) {
		float limbSwing = state.walkAnimationPos;
		float limbSwingAmount = state.walkAnimationSpeed;
		float ageInTicks = state.ageInTicks;
		float netHeadYaw = state.yRot;
		float headPitch = state.xRot;

	}

}