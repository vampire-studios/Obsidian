package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class DisplayInformation {

    @SerializedName("block_model")
    @blue.endless.jankson.annotation.SerializedName("block_model")
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
    @SerializedName("item_model")
    @blue.endless.jankson.annotation.SerializedName("item_model")
    public TextureAndModelInformation itemModel;
    public TextureAndModelInformation stickyPiston;
    @SerializedName("block_state")
    @blue.endless.jankson.annotation.SerializedName("block_state")
    public BlockProperty blockState;

    public static class Property {
        public Identifier model;
        public int x;
        public int y;
        public int z;
    }

}
