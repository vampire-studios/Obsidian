package io.github.vampirestudios.obsidian.api;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class SubItemGroup {
    public NameInformation name;
    @SerializedName("target_group")
    @blue.endless.jankson.annotation.SerializedName("target_group")
    public Identifier targetGroup;
    public Styling styling;

    public Map<String, Identifier> tags;
    public Identifier[] blocks;
    public Identifier[] items;
    public Identifier[] opBlocks;
    public Identifier[] opItems;
    public Map<String, Identifier> featureSetItems;
    public Map<String, Identifier> featureSetBlocks;

    public class Styling {
        public boolean hasCustomBackground;
        public boolean hasCustomScrollBar;
        public boolean hasCustomSubTab;
        public boolean hasCustomTab;
        public Identifier customBackground = null;
        public Identifier[] customScrollBar = null;
        public Identifier[] customSubTab = null;
        public Identifier[] customTab = null;
    }
}
