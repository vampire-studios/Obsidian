package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

import java.util.List;

public class CondensedEntry {
    public Identifier name;
    public Type type;
    @SerializedName("target_group") public Identifier targetGroup;
    @SerializedName("tabbed_group") public Identifier tabbedGroup;
    public boolean specificCreativeTab = false;
    public boolean specificTabbedGroup = false;
    public Identifier base;
    public Identifier tag;
    public List<Identifier> items;

    public enum Type {
        ITEM_TAG,
        BLOCK_TAG,
        ITEM_LIST
    }
}