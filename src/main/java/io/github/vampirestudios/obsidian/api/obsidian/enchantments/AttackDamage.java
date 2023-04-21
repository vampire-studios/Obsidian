package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.world.entity.MobType;

public class AttackDamage {

    public int level = 1;
    public String entity_group;
    public float attack_damage;

    public MobType getEntityGroup() {
        return switch (entity_group) {
            default -> MobType.UNDEFINED;
            case "undead" -> MobType.UNDEAD;
            case "arthropod" -> MobType.ARTHROPOD;
            case "illager" -> MobType.ILLAGER;
            case "aquatic" -> MobType.WATER;
        };
    }
}
