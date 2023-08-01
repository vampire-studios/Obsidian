package io.github.vampirestudios.obsidian.api;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SubItemGroup {
    public NameInformation name;
    @SerializedName("target_group")
    @blue.endless.jankson.annotation.SerializedName("target_group")
    public ResourceLocation targetGroup;
    public boolean hasCustomTexture;
    public ResourceLocation customTexture = null;

    public Map<String, ResourceLocation> tags;
    public ResourceLocation[] blocks;
    public ResourceLocation[] items;
    public ResourceLocation[] opBlocks;
    public ResourceLocation[] opItems;
    public Map<String, ResourceLocation> featureSetItems;
    public Map<String, ResourceLocation> featureSetBlocks;
}
