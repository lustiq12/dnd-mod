// Made with Blockbench 5.1.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelSolar<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "solar"), "main");
	private final ModelPart all;
	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart Body2;
	private final ModelPart bone5;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart bone4;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public ModelSolar(ModelPart root) {
		this.all = root.getChild("all");
		this.Waist = this.all.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.Body = this.Waist.getChild("Body");
		this.Body2 = this.Body.getChild("Body2");
		this.bone5 = this.Body.getChild("bone5");
		this.RightArm = this.Waist.getChild("RightArm");
		this.LeftArm = this.Waist.getChild("LeftArm");
		this.bone4 = this.LeftArm.getChild("bone4");
		this.bone = this.bone4.getChild("bone");
		this.bone2 = this.bone4.getChild("bone2");
		this.bone3 = this.bone4.getChild("bone3");
		this.bone6 = this.bone4.getChild("bone6");
		this.bone7 = this.Waist.getChild("bone7");
		this.RightLeg = this.all.getChild("RightLeg");
		this.LeftLeg = this.all.getChild("LeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(),
				PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Waist = all.addOrReplaceChild("Waist", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Head = Waist.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(84, 31).addBox(-4.0F,
				-8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(44, 106).addBox(-4.0F,
				6.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Body2 = Body.addOrReplaceChild("Body2", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-1.0F, 4.0F, 3.75F, 0.0F, 0.0F, -0.2182F));

		PartDefinition Body_r1 = Body2.addOrReplaceChild("Body_r1",
				CubeListBuilder.create().texOffs(116, 31)
						.addBox(-2.0F, -9.0F, 2.0F, 4.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(32, 114)
						.addBox(-2.0F, -9.0F, 3.0F, 4.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(48, 90)
						.addBox(-2.0F, -9.0F, 4.0F, 4.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(68, 106)
						.addBox(-2.0F, -6.0F, 1.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition bone5 = Body.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(18, 90).addBox(-5.0F,
				-6.0F, -3.0F, 10.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition RightArm = Waist.addOrReplaceChild("RightArm",
				CubeListBuilder.create().texOffs(110, 47)
						.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(110, 63)
						.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition LeftArm = Waist.addOrReplaceChild("LeftArm",
				CubeListBuilder.create().texOffs(110, 79)
						.addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(18, 114)
						.addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition bone4 = LeftArm.addOrReplaceChild("bone4", CubeListBuilder.create(),
				PartPose.offsetAndRotation(1.5F, 11.9F, 2.5F, 0.0F, 0.0F, -0.1309F));

		PartDefinition bone = bone4.addOrReplaceChild("bone", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bone
				.addOrReplaceChild("cube_r1",
						CubeListBuilder.create().texOffs(58, 54).addBox(-1.0F, -2.0F, -27.0F, 2.0F, 2.0F, 24.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		PartDefinition bone2 = bone4.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(0.0F, -1.9F, -5.75F));

		PartDefinition cube_r2 = bone2
				.addOrReplaceChild("cube_r2",
						CubeListBuilder.create().texOffs(58, 80).addBox(-1.0F, -2.0F, -27.0F, 2.0F, 2.0F, 24.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.7489F, 0.0F, 0.0F));

		PartDefinition bone3 = bone4.addOrReplaceChild("bone3", CubeListBuilder.create(),
				PartPose.offset(0.0F, -11.9F, -8.75F));

		PartDefinition cube_r3 = bone3
				.addOrReplaceChild("cube_r3",
						CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -9.0F, -27.0F, 0.0F, 12.0F, 42.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition bone6 = bone4.addOrReplaceChild("bone6", CubeListBuilder.create(),
				PartPose.offset(0.0F, -5.9F, -2.75F));

		PartDefinition cube_r4 = bone6.addOrReplaceChild("cube_r4",
				CubeListBuilder.create().texOffs(0, 90)
						.addBox(0.0F, -9.0F, -11.0F, 0.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(0, 90)
						.addBox(-0.25F, -9.0F, -11.0F, 0.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(0, 90)
						.addBox(-1.0F, -9.0F, -11.0F, 0.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(0, 90)
						.addBox(-0.75F, -9.0F, -11.0F, 0.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(0, 90)
						.addBox(-0.5F, -9.0F, -11.0F, 0.0F, 33.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.5F, -6.0F, -6.5F, 3.1416F, 0.0F, 0.0F));

		PartDefinition bone7 = Waist.addOrReplaceChild("bone7",
				CubeListBuilder.create().texOffs(0, 243)
						.addBox(-1.0F, -2.0F, -6.0F, 2.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)).texOffs(218, -18)
						.addBox(0.0F, -7.0F, -10.0F, 0.0F, 12.0F, 19.0F, new CubeDeformation(0.0F)).texOffs(196, 118)
						.addBox(-0.5F, -5.0F, -28.0F, 1.0F, 8.0F, 28.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.5F, -1.0F, 0.0F, 2.7489F, 0.0F, 0.0F));

		PartDefinition RightLeg = all.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(84, 106).addBox(
				-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

		PartDefinition LeftLeg = all.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(100, 106).addBox(
				-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		all.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}