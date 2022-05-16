package io.github.vampirestudios.obsidian.api.obsidian.entity.models;

import com.google.gson.annotations.SerializedName;

public class Animation {

	@SerializedName("model_part")
	public String modelPart;
	@SerializedName("animation_channel")
	public AnimationChannel animationChannel;

}