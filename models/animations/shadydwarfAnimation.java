// Save this class in your mod and generate all required imports

/**
 * Made with Blockbench 5.1.5 Exported for Minecraft version 1.19 or later with
 * Mojang mappings
 * 
 * @author Author
 */
public class shadydwarfAnimation {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(6.0F).looping()
			.addAnimation("Waist",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(3.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(4.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(5.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("Head",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 22.5F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, -22.5F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(3.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(4.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(5.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM),
							new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.CATMULLROM)))
			.build();
}