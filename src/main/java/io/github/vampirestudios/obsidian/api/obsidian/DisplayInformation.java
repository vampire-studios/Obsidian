package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.ResourceLocation;

public class DisplayInformation {

    public TextureAndModelInformation blockModel;
    @Deprecated public TextureAndModelInformation model;
    public TextureAndModelInformation hangingModel;
    public TextureAndModelInformation trapdoorBottomModel;
    public TextureAndModelInformation trapdoorOpenModel;
    public TextureAndModelInformation trapdoorTopModel;
    public TextureAndModelInformation doorBottomModel;
    public TextureAndModelInformation doorBottomHingeModel;
    public TextureAndModelInformation doorTopModel;
    public TextureAndModelInformation doorTopHingeModel;
    public TextureAndModelInformation onModel;
    public TextureAndModelInformation offModel;
    public TextureAndModelInformation itemModel;
    public TextureAndModelInformation stickyPiston;
    public BlockProperty blockState;

    public static class Property {
        public ResourceLocation model;
        public int x;
        public int y;
        public int z;
    }

}
