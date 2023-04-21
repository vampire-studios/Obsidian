package io.github.vampirestudios.obsidian.api.obsidian.villager;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class VillagerProfession {

    public NameInformation name;
    public List<ResourceLocation> harvestable_items;
    public PointOfInterest poi;
    public ResourceLocation work_sound;

    public List<Item> getHarvestableItems() {
        List<Item> items = new ArrayList<>();
        harvestable_items.forEach(identifier -> items.add(BuiltInRegistries.ITEM.get(identifier)));
        return items;
    }

}
