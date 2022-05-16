package io.github.vampirestudios.obsidian.api.obsidian.entity.models;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AnimationFile {

	@SerializedName("format_version")
	public String formatVersion;
	public Map<String, AnimationInformation> animations = new HashMap<>();

	public static class AnimationInformation {
		@SerializedName("animation_length")
		public float animationLength;
		public boolean loop;
		public Map<String, Bone> bones = new HashMap<>();

		public static class Bone {
			public Map<String, BoneTransformation> transformations = new HashMap<>();

			public static class BoneTransformation {
				public Map<Float, BoneInformation> animationFrame = new HashMap<>();

				public static class BoneInformation {
					public float[] post = new float[3];
					@SerializedName("lerp_mode")
					public Identifier lerpMode;
				}
			}
		}
	}

}
