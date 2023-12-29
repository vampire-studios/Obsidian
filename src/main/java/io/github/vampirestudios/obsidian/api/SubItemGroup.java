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
    public Styling styling;

    public Map<String, ResourceLocation> tags;
    public ResourceLocation[] blocks;
    public ResourceLocation[] items;
    public ResourceLocation[] opBlocks;
    public ResourceLocation[] opItems;
    public Map<String, ResourceLocation> featureSetItems;
    public Map<String, ResourceLocation> featureSetBlocks;

    public class Styling {
        public boolean hasCustomBackground;
        public boolean hasCustomScrollBar;
        public boolean hasCustomSubTab;
        public boolean hasCustomTab;
        public ResourceLocation customBackground = null;
        public ResourceLocation[] customScrollBar = null;
        public ResourceLocation[] customSubTab = null;
        public ResourceLocation[] customTab = null;
    }
}
