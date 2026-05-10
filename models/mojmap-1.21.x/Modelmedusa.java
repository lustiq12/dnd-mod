// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelmedusa<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "medusa"), "main");
	private final ModelPart all;
	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart bone8;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;
	private final ModelPart bone4;
	private final ModelPart bone5;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart Body;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public Modelmedusa(ModelPart root) {
		this.all = root.getChild("all");
		this.Waist = this.all.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.bone8 = this.Head.getChild("bone8");
		this.bone = this.bone8.getChild("bone");
		this.bone2 = this.bone8.getChild("bone2");
		this.bone3 = this.bone8.getChild("bone3");
		this.bone4 = this.bone8.getChild("bone4");
		this.bone5 = this.bone8.getChild("bone5");
		this.bone6 = this.bone8.getChild("bone6");
		this.bone7 = this.bone8.getChild("bone7");
		this.Body = this.Waist.getChild("Body");
		this.RightArm = this.Waist.getChild("RightArm");
		this.LeftArm = this.Waist.getChild("LeftArm");
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

		PartDefinition Head = Waist.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(0, 16)
						.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(96, 0)
						.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition bone8 = Head.addOrReplaceChild("bone8", CubeListBuilder.create(),
				PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition bone = bone8.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F,
				-5.0F, 0.0F, 16.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone2 = bone8.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition cube_r1 = bone2
				.addOrReplaceChild("cube_r1",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition bone3 = bone8.addOrReplaceChild("bone3", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition cube_r2 = bone3
				.addOrReplaceChild("cube_r2",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition bone4 = bone8.addOrReplaceChild("bone4", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, -3.0F));

		PartDefinition cube_r3 = bone4
				.addOrReplaceChild("cube_r3",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition bone5 = bone8.addOrReplaceChild("bone5", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, -3.0F));

		PartDefinition cube_r4 = bone5
				.addOrReplaceChild("cube_r4",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition bone6 = bone8.addOrReplaceChild("bone6", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r5 = bone6
				.addOrReplaceChild("cube_r5",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1781F, 0.0F));

		PartDefinition bone7 = bone8.addOrReplaceChild("bone7", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r6 = bone7
				.addOrReplaceChild("cube_r6",
						CubeListBuilder.create().texOffs(94, 51).addBox(-8.0F, -5.0F, 0.0F, 16.0F, 13.0F, 0.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.9635F, 0.0F));

		PartDefinition Body = Waist.addOrReplaceChild("Body",
				CubeListBuilder.create().texOffs(0, 32)
						.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(104, 80)
						.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition cube_r7 = Body.addOrReplaceChild("cube_r7",
				CubeListBuilder.create().texOffs(72, 119).addBox(-4.0F, -2.0F, -3.0F, 8.0F, 5.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 3.5F, -0.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition RightArm = Waist.addOrReplaceChild("RightArm",
				CubeListBuilder.create().texOffs(40, 32)
						.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 112)
						.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(-4.0F, -10.0F, 0.0F));

		PartDefinition LeftArm = Waist.addOrReplaceChild("LeftArm",
				CubeListBuilder.create().texOffs(0, 48)
						.addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(20, 112)
						.addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(4.0F, -10.0F, 0.0F));

		PartDefinition RightLeg = all.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(32, 16)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(69, 39)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(-1.9F, 0.0F, 0.0F));

		PartDefinition LeftLeg = all.addOrReplaceChild("LeftLeg",
				CubeListBuilder.create().texOffs(24, 32)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(22, 58)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
				PartPose.offset(1.9F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
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