package io.github.vampirestudios.obsidian.api.obsidian.entity;

import net.minecraft.entity.SpawnGroup;

public class EntityComponents {

    public String entity_category;

    public SpawnGroup getCategory() {
        return switch (entity_category) {
            case "monster" -> SpawnGroup.MONSTER;
            case "creature" -> SpawnGroup.CREATURE;
            case "ambient" -> SpawnGroup.AMBIENT;
            case "axolotls" -> SpawnGroup.AXOLOTLS;
            case "underground_water_creature" -> SpawnGroup.UNDERGROUND_WATER_CREATURE;
            case "water_creature" -> SpawnGroup.WATER_CREATURE;
            case "water_ambient" -> SpawnGroup.WATER_AMBIENT;
            case "misc" -> SpawnGroup.MISC;
            default -> throw new IllegalArgumentException("Unknown entity category: " + entity_category);
        };
    }

}