// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelnothic<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "nothic"), "main");
	private final ModelPart all;
	private final ModelPart bone;
	private final ModelPart bone11;
	private final ModelPart bone7;
	private final ModelPart bone8;
	private final ModelPart bone9;
	private final ModelPart bone10;
	private final ModelPart bone3;
	private final ModelPart bone4;
	private final ModelPart bone5;
	private final ModelPart bone6;

	public Modelnothic(ModelPart root) {
		this.all = root.getChild("all");
		this.bone = this.all.getChild("bone");
		this.bone11 = this.bone.getChild("bone11");
		this.bone7 = this.bone.getChild("bone7");
		this.bone8 = this.bone7.getChild("bone8");
		this.bone9 = this.bone.getChild("bone9");
		this.bone10 = this.bone9.getChild("bone10");
		this.bone3 = this.all.getChild("bone3");
		this.bone4 = this.bone3.getChild("bone4");
		this.bone5 = this.all.getChild("bone5");
		this.bone6 = this.bone5.getChild("bone6");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all",
				CubeListBuilder.create().texOffs(0, 11)
						.addBox(-3.0F, -2.0F, -8.0F, 6.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(22, 22)
						.addBox(0.0F, -8.0F, -8.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 16.0F, 8.0F));

		PartDefinition cube_r1 = all.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(22, 22).addBox(0.0F, -6.0F, -7.0F, 0.0F, 6.0F, 8.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r2 = all.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(22, 22).addBox(0.0F, -6.0F, -7.0F, 0.0F, 6.0F, 8.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition bone = all.addOrReplaceChild("bone",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, 0.0F, -7.0F, 8.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(30, 0)
						.addBox(0.0F, -6.0F, -7.0F, 0.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -2.0F, -8.0F));

		PartDefinition cube_r3 = bone
				.addOrReplaceChild("cube_r3",
						CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, -6.0F, -7.0F, 0.0F, 6.0F, 7.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r4 = bone
				.addOrReplaceChild("cube_r4",
						CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, -6.0F, -7.0F, 0.0F, 6.0F, 7.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition bone11 = bone.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(0, 22).addBox(-3.0F,
				-3.0F, -5.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -7.0F));

		PartDefinition bone7 = bone.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(8, 33).addBox(0.0F,
				-1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, -5.0F));

		PartDefinition bone8 = bone7.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(32, 36).addBox(-1.0F,
				0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, 0.0F));

		PartDefinition bone9 = bone.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(36, 13).addBox(-2.0F,
				-1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, -5.0F));

		PartDefinition bone10 = bone9.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(38, 21).addBox(-1.0F,
				0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 5.0F, 0.0F));

		PartDefinition bone3 = all.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(28, 13).addBox(-2.0F,
				-1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -2.0F, -1.0F));

		PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(16, 36).addBox(-1.0F,
				0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 5.0F, 0.0F));

		PartDefinition bone5 = all.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(0, 33).addBox(0.0F,
				-1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.0F, -1.0F));

		PartDefinition bone6 = bone5.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(24, 36).addBox(-1.0F,
				0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		all.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
	}
}