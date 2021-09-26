package io.github.vampirestudios.obsidian.api.obsidian.villager;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class VillagerProfession {

    public NameInformation name;
    public List<Identifier> harvestable_items;
    public PointOfInterest poi;
    public Identifier work_sound;

    public List<Item> getHarvestableItems() {
        List<Item> items = new ArrayList<>();
        harvestable_items.forEach(identifier -> items.add(Registry.ITEM.get(identifier)));
        return items;
    }

}
