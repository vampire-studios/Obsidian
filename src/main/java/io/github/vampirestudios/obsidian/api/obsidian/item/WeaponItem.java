package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;

public class WeaponItem extends Item {

    public int attackDamage;
    public float attackSpeed;
    public Object material;
    public boolean damageable = true;

    public Tier getTier() {
        if (material instanceof ResourceLocation resourceLocation) {
            return ContentRegistries.TIERS.get(resourceLocation);
        } else if (material instanceof  String s) {
            ResourceLocation location = ResourceLocation.tryParse(s);
            return ContentRegistries.TIERS.get(location);
        } else if (material instanceof Tier itemSettings1) {
            return itemSettings1;
        } else {
            System.out.printf("Tier is null for %s%n", this.information.name.id);
            return null;
        }
    }

}