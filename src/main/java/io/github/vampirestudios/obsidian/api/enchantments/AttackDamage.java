package io.github.vampirestudios.obsidian.api.enchantments;

import net.minecraft.entity.EntityGroup;

public class AttackDamage {

    public int level = 1;
    public String entity_group;
    public float attack_damage;

    public EntityGroup getEntityGroup() {
        switch (entity_group) {
            case "default":
            default:
                return EntityGroup.DEFAULT;
            case "undead":
                return EntityGroup.UNDEAD;
            case "arthropod":
                return EntityGroup.ARTHROPOD;
            case "illager":
                return EntityGroup.ILLAGER;
            case "aquatic":
                return EntityGroup.AQUATIC;
        }
    }
}
