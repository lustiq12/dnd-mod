// Made with Blockbench 5.1.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelshadydwarf<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "shadydwarf"), "main");
	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bb_main;

	public Modelshadydwarf(ModelPart root) {
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

		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(),
				PartPose.offset(0.0F, 20.0F, 1.0F));

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
						.addBox(-3.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(-5.0F, -9.0F, 0.0F, -0.9312F, -0.3614F, -0.1572F));

		PartDefinition LeftArm = Waist.addOrReplaceChild("LeftArm",
				CubeListBuilder.create().texOffs(64, 43).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F,
						new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(5.0F, -9.0F, 0.0F, -0.9312F, 0.3614F, 0.1572F));

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(48, 41)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(-1.9F, 13.0F, -1.0F, -0.384F, 0.0177F, 0.0363F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg",
				CubeListBuilder.create().texOffs(16, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(32, 55)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offsetAndRotation(1.9F, 13.0F, -1.0F, -0.3927F, 0.0F, 0.0F));

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, -13.0F));

		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 108)
				.addBox(-9.0F, -6.0F, -2.0F, 18.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

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
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}