// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelCustomModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "custommodel"), "main");
	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart Hat;
	private final ModelPart Body;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;

	public ModelCustomModel(ModelPart root) {
		this.Waist = root.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.Hat = this.Head.getChild("Hat");
		this.Body = this.Waist.getChild("Body");
		this.bone = this.Waist.getChild("bone");
		this.bone2 = this.Waist.getChild("bone2");
		this.bone3 = root.getChild("bone3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(),
				PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Head = Waist.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F,
				-8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Hat = Head.addOrReplaceChild("Hat",
				CubeListBuilder.create().texOffs(32, 0)
						.addBox(-4.0F, -4.25F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.5F)).texOffs(15, 41)
						.addBox(-8.0F, 0.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition Body = Waist.addOrReplaceChild("Body",
				CubeListBuilder.create().texOffs(0, 16)
						.addBox(-4.0F, -10.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(24, 16)
						.addBox(-4.0F, -10.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition bone = Waist.addOrReplaceChild("bone", CubeListBuilder.create(),
				PartPose.offset(4.0F, -11.0F, 0.0F));

		PartDefinition cube_r1 = bone
				.addOrReplaceChild("cube_r1",
						CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 10.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition bone2 = Waist.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(-4.0F, -11.0F, 0.0F));

		PartDefinition cube_r2 = bone2
				.addOrReplaceChild("cube_r2",
						CubeListBuilder.create().texOffs(4, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 10.0F, 2.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 48).addBox(
				-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}