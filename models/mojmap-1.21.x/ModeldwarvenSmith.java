// Made with Blockbench 5.1.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModeldwarvenSmith<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "dwarvensmith"), "main");
	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart bone;
	private final ModelPart bone2;

	public ModeldwarvenSmith(ModelPart root) {
		this.Waist = root.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.Body = this.Waist.getChild("Body");
		this.RightArm = this.Waist.getChild("RightArm");
		this.LeftArm = this.Waist.getChild("LeftArm");
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
		this.bone = root.getChild("bone");
		this.bone2 = this.bone.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(),
				PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Head = Waist.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(0, 25)
						.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(32, 25)
						.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition Body = Waist.addOrReplaceChild("Body",
				CubeListBuilder.create().texOffs(0, 41)
						.addBox(-4.0F, 2.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(24, 41)
						.addBox(-4.0F, 2.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition RightArm = Waist.addOrReplaceChild("RightArm",
				CubeListBuilder.create().texOffs(48, 57)
						.addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(64, 15)
						.addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)).texOffs(102, 0)
						.addBox(-2.0F, 7.0F, -5.0F, 2.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)).texOffs(103, 35)
						.addBox(-4.0F, 4.0F, -9.0F, 6.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition LeftArm = Waist.addOrReplaceChild("LeftArm",
				CubeListBuilder.create().texOffs(64, 29)
						.addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(64, 43)
						.addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(48, 41)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg",
				CubeListBuilder.create().texOffs(16, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(32, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition bone = partdefinition.addOrReplaceChild("bone",
				CubeListBuilder.create().texOffs(88, 100)
						.addBox(-6.0F, -2.0F, -4.0F, 12.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(72, 112)
						.addBox(-7.0F, -12.0F, -5.0F, 14.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(64, 99)
						.addBox(-4.0F, -7.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, -13.0F));

		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition cube_r1 = bone2
				.addOrReplaceChild("cube_r1",
						CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}