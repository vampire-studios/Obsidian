package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;

public class Item {
    public ItemInformation information;
    public ItemDisplayInformation rendering;
    public UseActions useActions;
    public TooltipInformation[] lore = new TooltipInformation[0];
}