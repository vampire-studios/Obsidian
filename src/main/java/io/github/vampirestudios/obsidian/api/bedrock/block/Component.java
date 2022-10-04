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

    @SerializedName("minecraft:display_name")
    public String display_name;

    @SerializedName("minecraft:entity_collision")
    public EntityCollision entity_collision;

    @SerializedName("minecraft:block_light_emission")
    public float block_light_emission;

    @SerializedName("minecraft:friction")
    public float friction;

    @SerializedName("minecraft:flammable")
    public Flammable flammable;

    @SerializedName("minecraft:breakonpush")
    public boolean breakonpush;

    public static class Flammable {
        public int flame_odds;
        public int burn_odds;
    }

    public static class EntityCollision {
        public float[] origin = new float[]{-8.0F, 0.0F, -8.0F};
        public float[] size = new float[]{16.0F, 16.0F, 16.0F};
    }
}