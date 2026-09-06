package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.Identifier;

import java.util.Map;

public class ItemGroup {
	public NameInformation name;
	public transient Identifier id;
	public Identifier icon;
	public Map<String, Identifier> tags;
	public Identifier[] blocks;
	public Identifier[] items;
	public Identifier[] opBlocks;
	public Identifier[] opItems;
	public Map<String, Identifier> featureSetItems;
	public Map<String, Identifier> featureSetBlocks;
}
