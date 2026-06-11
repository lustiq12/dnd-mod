// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelgoristro<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "goristro"), "main");
	private final ModelPart all;
	private final ModelPart bone;
	private final ModelPart bone14;
	private final ModelPart bone15;
	private final ModelPart bone16;
	private final ModelPart bone2;
	private final ModelPart bone11;
	private final ModelPart bone12;
	private final ModelPart bone13;
	private final ModelPart bone3;
	private final ModelPart bone8;
	private final ModelPart bone4;
	private final ModelPart bone17;
	private final ModelPart bone18;
	private final ModelPart bone19;
	private final ModelPart bone20;
	private final ModelPart bone21;
	private final ModelPart bone5;
	private final ModelPart bone22;
	private final ModelPart bone23;
	private final ModelPart bone24;
	private final ModelPart bone25;
	private final ModelPart bone26;
	private final ModelPart bone7;
	private final ModelPart bone27;
	private final ModelPart bone6;
	private final ModelPart bone9;

	public Modelgoristro(ModelPart root) {
		this.all = root.getChild("all");
		this.bone = this.all.getChild("bone");
		this.bone14 = this.bone.getChild("bone14");
		this.bone15 = this.bone14.getChild("bone15");
		this.bone16 = this.bone15.getChild("bone16");
		this.bone2 = this.all.getChild("bone2");
		this.bone11 = this.bone2.getChild("bone11");
		this.bone12 = this.bone11.getChild("bone12");
		this.bone13 = this.bone12.getChild("bone13");
		this.bone3 = this.all.getChild("bone3");
		this.bone8 = this.bone3.getChild("bone8");
		this.bone4 = this.bone8.getChild("bone4");
		this.bone17 = this.bone4.getChild("bone17");
		this.bone18 = this.bone17.getChild("bone18");
		this.bone19 = this.bone17.getChild("bone19");
		this.bone20 = this.bone17.getChild("bone20");
		this.bone21 = this.bone17.getChild("bone21");
		this.bone5 = this.bone8.getChild("bone5");
		this.bone22 = this.bone5.getChild("bone22");
		this.bone23 = this.bone22.getChild("bone23");
		this.bone24 = this.bone22.getChild("bone24");
		this.bone25 = this.bone22.getChild("bone25");
		this.bone26 = this.bone22.getChild("bone26");
		this.bone7 = this.bone8.getChild("bone7");
		this.bone27 = this.bone7.getChild("bone27");
		this.bone6 = this.bone7.getChild("bone6");
		this.bone9 = this.bone7.getChild("bone9");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(),
				PartPose.offset(0.0F, 7.0F, 0.0F));

		PartDefinition bone = all.addOrReplaceChild("bone", CubeListBuilder.create(),
				PartPose.offset(-4.0F, 2.0F, 0.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(48, 0).addBox(-3.0F, -9.0F, -4.0F, 6.0F, 9.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 6.0F, -5.0F, -1.1781F, 0.0F, 0.0F));

		PartDefinition bone14 = bone.addOrReplaceChild("bone14", CubeListBuilder.create(),
				PartPose.offset(0.0F, 6.5F, -1.25F));

		PartDefinition cube_r2 = bone14
				.addOrReplaceChild("cube_r2",
						CubeListBuilder.create().texOffs(64, 25).addBox(-2.0F, -7.0F, -1.0F, 4.0F, 8.0F, 3.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone15 = bone14.addOrReplaceChild("bone15", CubeListBuilder.create(),
				PartPose.offset(0.0F, 2.25F, 3.25F));

		PartDefinition cube_r3 = bone15.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(0, 66).addBox(-1.0F, -7.0F, -1.0F, 3.0F, 7.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, 5.0F, -4.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition bone16 = bone15.addOrReplaceChild("bone16", CubeListBuilder.create().texOffs(60, 36).addBox(
				-2.0F, 0.0F, -5.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.25F, -3.0F));

		PartDefinition bone2 = all.addOrReplaceChild("bone2", CubeListBuilder.create(),
				PartPose.offset(4.0F, 2.0F, 0.0F));

		PartDefinition cube_r4 = bone2.addOrReplaceChild("cube_r4",
				CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, -9.0F, -4.0F, 6.0F, 9.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 6.0F, -5.0F, -1.1781F, 0.0F, 0.0F));

		PartDefinition bone11 = bone2.addOrReplaceChild("bone11", CubeListBuilder.create(),
				PartPose.offset(0.0F, 6.5F, -1.25F));

		PartDefinition cube_r5 = bone11
				.addOrReplaceChild("cube_r5",
						CubeListBuilder.create().texOffs(62, 65).addBox(-2.0F, -7.0F, -1.0F, 4.0F, 8.0F, 3.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone12 = bone11.addOrReplaceChild("bone12", CubeListBuilder.create(),
				PartPose.offset(0.0F, 2.25F, 3.25F));

		PartDefinition cube_r6 = bone12.addOrReplaceChild("cube_r6",
				CubeListBuilder.create().texOffs(70, 0).addBox(-2.0F, -7.0F, -1.0F, 3.0F, 7.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.5F, 5.0F, -4.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition bone13 = bone12.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(60, 45).addBox(
				-2.0F, 0.0F, -5.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.25F, -3.0F));

		PartDefinition bone3 = all.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 38).addBox(-6.0F,
				-6.0F, -2.0F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -1.0F));

		PartDefinition bone8 = bone3.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F,
				-11.0F, -3.0F, 16.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition bone4 = bone8.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(36, 38).addBox(0.0F,
				-2.0F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -9.0F, 1.0F));

		PartDefinition bone17 = bone4.addOrReplaceChild("bone17", CubeListBuilder.create().texOffs(22, 55).addBox(-2.0F,
				0.0F, -2.0F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 9.0F, -0.5F));

		PartDefinition bone18 = bone17.addOrReplaceChild("bone18", CubeListBuilder.create().texOffs(32, 70).addBox(
				-1.0F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 10.0F, 0.5F));

		PartDefinition bone19 = bone17.addOrReplaceChild("bone19", CubeListBuilder.create().texOffs(22, 70).addBox(
				-3.0F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 2.5F));

		PartDefinition bone20 = bone17.addOrReplaceChild("bone20", CubeListBuilder.create().texOffs(70, 10).addBox(
				-3.0F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 0.5F));

		PartDefinition bone21 = bone17.addOrReplaceChild("bone21", CubeListBuilder.create().texOffs(12, 66).addBox(
				-3.0F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, -1.5F));

		PartDefinition bone5 = bone8.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(40, 19).addBox(-6.0F,
				-2.0F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -9.0F, 1.0F));

		PartDefinition bone22 = bone5.addOrReplaceChild("bone22", CubeListBuilder.create().texOffs(42, 55).addBox(-3.0F,
				0.0F, -2.0F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 9.0F, -0.5F));

		PartDefinition bone23 = bone22.addOrReplaceChild("bone23", CubeListBuilder.create().texOffs(42, 70).addBox(0.0F,
				0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 10.0F, -1.5F));

		PartDefinition bone24 = bone22.addOrReplaceChild("bone24", CubeListBuilder.create().texOffs(52, 70).addBox(0.0F,
				0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 10.0F, 0.5F));

		PartDefinition bone25 = bone22.addOrReplaceChild("bone25", CubeListBuilder.create().texOffs(12, 74).addBox(0.0F,
				0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 10.0F, 2.5F));

		PartDefinition bone26 = bone22.addOrReplaceChild("bone26", CubeListBuilder.create().texOffs(0, 76).addBox(-2.0F,
				0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 10.0F, 0.5F));

		PartDefinition bone7 = bone8.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F,
				-4.0F, -7.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition bone27 = bone7.addOrReplaceChild("bone27", CubeListBuilder.create().texOffs(62, 76).addBox(-2.0F,
				1.0F, 1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -9.0F));

		PartDefinition bone6 = bone7.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(62, 59).addBox(0.0F,
				-2.0F, -2.0F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, -4.0F));

		PartDefinition cube_r7 = bone6.addOrReplaceChild("cube_r7",
				CubeListBuilder.create().texOffs(48, 14).addBox(-2.0F, -1.0F, -2.0F, 8.0F, 2.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(5.5F, -0.5F, -2.0F, 2.5294F, 0.5647F, -2.049F));

		PartDefinition bone9 = bone7.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(64, 19).addBox(-7.0F,
				-2.0F, -2.0F, 7.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -1.0F, -4.0F));

		PartDefinition cube_r8 = bone9.addOrReplaceChild("cube_r8",
				CubeListBuilder.create().texOffs(62, 54).addBox(-6.0F, -1.0F, -2.0F, 8.0F, 2.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-5.5F, -0.5F, -2.0F, 2.5294F, -0.5647F, 2.049F));

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