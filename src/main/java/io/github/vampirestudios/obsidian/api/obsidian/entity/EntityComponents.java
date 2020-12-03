package io.github.vampirestudios.obsidian.api.obsidian.entity;

import net.minecraft.entity.SpawnGroup;

public class EntityComponents {

    public String entity_category;

    public SpawnGroup getCategory() {
        switch (entity_category) {
            case "monster":
                return SpawnGroup.MONSTER;
            case "creature":
                return SpawnGroup.CREATURE;
            case "ambient":
                return SpawnGroup.AMBIENT;
            case "water_creature":
                return SpawnGroup.WATER_CREATURE;
            case "misc":
            default:
                return SpawnGroup.MISC;
        }
    }

}