package io.github.vampirestudios.obsidian.client.bbmodel;

import net.minecraft.resources.Identifier;

public class BBModel {
	public String name;
	public Identifier modelIdentifier;
	public Resolution resolution;

	public static class Resolution {
		public int width;
		public int height;
	}
}
