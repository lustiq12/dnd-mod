// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelGoblinWarrior<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "goblinwarrior"), "main");
	private final ModelPart bone3;
	private final ModelPart bone2;
	private final ModelPart bone;
	private final ModelPart bone4;
	private final ModelPart bone10;
	private final ModelPart bone5;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart bone8;
	private final ModelPart bone9;
	private final ModelPart bone11;

	public ModelGoblinWarrior(ModelPart root) {
		this.bone3 = root.getChild("bone3");
		this.bone2 = root.getChild("bone2");
		this.bone = root.getChild("bone");
		this.bone4 = this.bone.getChild("bone4");
		this.bone10 = this.bone4.getChild("bone10");
		this.bone5 = this.bone.getChild("bone5");
		this.bone6 = this.bone.getChild("bone6");
		this.bone7 = this.bone6.getChild("bone7");
		this.bone8 = this.bone6.getChild("bone8");
		this.bone9 = this.bone6.getChild("bone9");
		this.bone11 = this.bone.getChild("bone11");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(16, 28)
				.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.0F, 19.0F, 0.0F));

		PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(24, 28)
				.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.0F, 19.0F, 0.0F));

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 12).addBox(
				-3.0F, -7.0F, -1.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, -1.0F));

		PartDefinition bone4 = bone.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(0, 23).addBox(-2.0F,
				0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -7.0F, 1.0F));

		PartDefinition bone10 = bone4.addOrReplaceChild("bone10",
				CubeListBuilder.create().texOffs(20, 22)
						.addBox(0.0F, -2.0F, -3.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(20, 9)
						.addBox(0.5F, -3.0F, -13.0F, 0.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-1.5F, 7.25F, 1.0F));

		PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(8, 23).addBox(0.0F,
				0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -7.0F, 1.0F));

		PartDefinition bone6 = bone.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F,
				-6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 1.0F));

		PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F,
				-1.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -3.0F));

		PartDefinition bone8 = bone6.addOrReplaceChild("bone8",
				CubeListBuilder.create().texOffs(28, 0).addBox(0.0F, -2.0F, 0.0F, 6.0F, 5.0F, 0.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition bone9 = bone6.addOrReplaceChild("bone9",
				CubeListBuilder.create().texOffs(28, 5).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 5.0F, 0.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition bone11 = bone.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(48, 0).addBox(-4.0F,
				-4.0F, -1.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 4.1F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}