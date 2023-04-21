package io.github.vampirestudios.obsidian;

import net.minecraft.resources.ResourceLocation;

public class Const {
	public static final String MOD_ID = "obsidian";
	public static final String MOD_NAME = "Obsidian";
	public static final String MOD_VERSION = "0.8.0-alpha";

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}