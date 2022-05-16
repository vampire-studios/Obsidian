package io.github.vampirestudios.obsidian.api.obsidian.entity.models;

import io.github.vampirestudios.obsidian.KeyframeAnimations;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class Keyframe {

	public float timestamp;
	public Target target;
	public Identifier interpolation;

	public static class Target {
		public String type;
		public float[] vector;

		public Vec3f getVector() {
			return switch (type) {
				case "degreeVec" -> KeyframeAnimations.degreeVec(vector[0], vector[1], vector[2]);
				case "posVec" -> KeyframeAnimations.posVec(vector[0], vector[1], vector[2]);
				case "scaleVec" -> KeyframeAnimations.scaleVec(vector[0], vector[1], vector[2]);
				default -> throw new IllegalArgumentException();
			};
		}
	}

}
