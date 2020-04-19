package io.github.vampirestudios.obsidian.api.entity;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.EntityCategory;

public class EntityComponents {

    @SerializedName("minecraft:collision_box")
    public CollisionBox collision_box;
    public String entity_category;

    public EntityCategory getCategory() {
        switch (entity_category) {
            case "monster":
                return EntityCategory.MONSTER;
            case "creature":
                return EntityCategory.CREATURE;
            case "ambient":
                return EntityCategory.AMBIENT;
            case "water_creature":
                return EntityCategory.WATER_CREATURE;
            case "misc":
            default:
                return EntityCategory.MISC;
        }
    }

}