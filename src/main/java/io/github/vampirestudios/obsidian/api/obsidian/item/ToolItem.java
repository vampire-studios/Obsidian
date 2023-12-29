package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;

public class ToolItem extends Item {

    public Object material;
    public String tool_type;
    public int attackDamage;
    public float attackSpeed;
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
                System.out.printf("Material is null for %s%n", this.information.name.id);
                return null;
            }
        }
    }

}