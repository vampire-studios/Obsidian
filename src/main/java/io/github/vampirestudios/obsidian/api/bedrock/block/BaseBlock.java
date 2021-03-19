package io.github.vampirestudios.obsidian.api.bedrock.block;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.bedrock.BaseInformation;
import io.github.vampirestudios.obsidian.api.bedrock.Description;

public class BaseBlock extends BaseInformation {

	@SerializedName("minecraft:block")
	public Block block;

	public static class Block {

		public Description description;
		public Component components;

	}

}