package io.github.vampirestudios.obsidian.api.obsidian.entity;

import net.minecraft.world.entity.MobCategory;

public class EntityComponents {

    public String entity_category;

    public MobCategory getCategory() {
        return switch (entity_category) {
            case "monster" -> MobCategory.MONSTER;
            case "creature" -> MobCategory.CREATURE;
            case "ambient" -> MobCategory.AMBIENT;
            case "axolotls" -> MobCategory.AXOLOTLS;
            case "underground_water_creature" -> MobCategory.UNDERGROUND_WATER_CREATURE;
            case "water_creature" -> MobCategory.WATER_CREATURE;
            case "water_ambient" -> MobCategory.WATER_AMBIENT;
            case "misc" -> MobCategory.MISC;
            default -> throw new IllegalArgumentException("Unknown entity category: " + entity_category);
        };
    }

}