package io.github.vampirestudios.obsidian;

import net.minecraft.util.Identifier;

public class Const {
	public static final String MOD_ID = "obsidian";
	public static final String MOD_NAME = "Obsidian";
	public static final String MOD_VERSION = "0.8.0-alpha";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}