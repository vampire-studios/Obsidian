package io.github.vampirestudios.obsidian.api.bedrock.block;

import com.google.gson.annotations.SerializedName;

public class Component {

    @SerializedName("minecraft:destroy_time")
    public float destroy_time;

    @SerializedName("minecraft:map_color")
    public String map_color;

    @SerializedName("minecraft:explosion_resistance")
    public float explosion_resistance;

    @SerializedName("minecraft:block_light_absorption")
    public int block_light_absorption;

    @SerializedName("minecraft:block_light_emission")
    public float block_light_emission;

    @SerializedName("minecraft:breakonpush")
    public boolean breakonpush;

}