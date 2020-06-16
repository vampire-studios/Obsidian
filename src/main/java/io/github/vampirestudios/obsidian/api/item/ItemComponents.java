package io.github.vampirestudios.obsidian.api.item;

import com.google.gson.annotations.SerializedName;

public class ItemComponents {

    public int use_duration = 5;
    @SerializedName("minecraft:food")
    public ItemFood food;

}