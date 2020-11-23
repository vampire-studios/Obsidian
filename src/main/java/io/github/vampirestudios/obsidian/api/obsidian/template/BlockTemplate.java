package io.github.vampirestudios.obsidian.api.obsidian.template;

import io.github.vampirestudios.obsidian.api.obsidian.DisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Functions;
import io.github.vampirestudios.obsidian.api.obsidian.block.OreInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodInformation;

public class BlockTemplate {

    public String name;
    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;

}