package io.github.vampirestudios.obsidian.api.obsidian;

public class DisplayInformation {

    public TextureAndModelInformation blockModel;
    @Deprecated public TextureAndModelInformation model;
    public TextureAndModelInformation itemModel;
    public BlockstateInformation blockState;
    public TooltipInformation[] lore = new TooltipInformation[0];

}
