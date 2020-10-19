package io.github.vampirestudios.obsidian.api.block;

import io.github.vampirestudios.obsidian.api.DisplayInformation;
import io.github.vampirestudios.obsidian.api.item.FoodInformation;

public class Block {

    public String template = null;
    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;
    public FoodInformation food_information;

}