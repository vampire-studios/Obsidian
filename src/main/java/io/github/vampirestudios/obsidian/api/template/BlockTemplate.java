package io.github.vampirestudios.obsidian.api.template;

import io.github.vampirestudios.obsidian.api.DisplayInformation;
import io.github.vampirestudios.obsidian.api.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.block.BlockInformation;
import io.github.vampirestudios.obsidian.api.block.Functions;
import io.github.vampirestudios.obsidian.api.block.OreInformation;
import io.github.vampirestudios.obsidian.api.item.FoodInformation;

public class BlockTemplate {

    public String name;
    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;

}