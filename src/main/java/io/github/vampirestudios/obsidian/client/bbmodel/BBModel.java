package io.github.vampirestudios.obsidian.client.bbmodel;

import net.minecraft.resources.ResourceLocation;

public class BBModel {
	public String name;
	public ResourceLocation modelIdentifier;
	public Resolution resolution;

	public static class Resolution {
		public int width;
		public int height;
	}
}
