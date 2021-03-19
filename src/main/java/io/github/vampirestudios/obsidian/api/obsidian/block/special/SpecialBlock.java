package io.github.vampirestudios.obsidian.api.obsidian.block.special;

import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Functions;
import io.github.vampirestudios.obsidian.api.obsidian.block.OreInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;

public class SpecialBlock extends Block {

	public String type;
	public BlockInformation information;
	public DisplayInformation display;
	public Functions functions;
	public OreInformation ore_information;
	public FoodInformation food_information;

}
