package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;

public class WeaponItem extends Item {

    public int attackDamage;
    public float attackSpeed;
    public Object material;
    public boolean damageable = true;

    public Tier getTier() {
        switch (material) {
            case ResourceLocation resourceLocation -> {
                return ContentRegistries.TIERS.get(resourceLocation);
            }
            case String resourceLocation -> {
                ResourceLocation location = ResourceLocation.tryParse(resourceLocation);
                return ContentRegistries.TIERS.get(location);
            }
            case Tier tier -> {
                return tier;
            }
            case null, default -> {
                return null;
            }
        }
    }

}