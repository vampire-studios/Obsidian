package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.entity.EntityGroup;

public class AttackDamage {

    public int level = 1;
    public String entity_group;
    public float attack_damage;

    public EntityGroup getEntityGroup() {
        return switch (entity_group) {
            default -> EntityGroup.DEFAULT;
            case "undead" -> EntityGroup.UNDEAD;
            case "arthropod" -> EntityGroup.ARTHROPOD;
            case "illager" -> EntityGroup.ILLAGER;
            case "aquatic" -> EntityGroup.AQUATIC;
        };
    }
}
