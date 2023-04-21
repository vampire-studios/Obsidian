package io.github.vampirestudios.obsidian.api.obsidian;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ItemGroup {
    public NameInformation name;
    public ResourceLocation icon;
    public Map<String, ResourceLocation> tags;
    public ResourceLocation[] blocks;
    public ResourceLocation[] items;
    public ResourceLocation[] opBlocks;
    public ResourceLocation[] opItems;
    public Map<String, ResourceLocation> featureSetItems;
    public Map<String, ResourceLocation> featureSetBlocks;
}
