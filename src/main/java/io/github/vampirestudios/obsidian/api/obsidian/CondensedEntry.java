package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class CondensedEntry {
    public ResourceLocation name;
    public Type type;
    @SerializedName("target_group") public ResourceLocation targetGroup;
    @SerializedName("tabbed_group") public ResourceLocation tabbedGroup;
    public boolean specificCreativeTab = false;
    public boolean specificTabbedGroup = false;
    public ResourceLocation base;
    public ResourceLocation tag;
    public List<ResourceLocation> items;

    public enum Type {
        ITEM_TAG,
        BLOCK_TAG,
        ITEM_LIST
    }
}